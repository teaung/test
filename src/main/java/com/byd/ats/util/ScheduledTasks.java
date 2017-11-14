package com.byd.ats.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.byd.ats.entity.ATSAlarmEvent;
import com.byd.ats.entity.AtsModeSwitch;
import com.byd.ats.entity.CLient2serJsonCommand;
import com.byd.ats.entity.CLient2serJsonCommandHistory;
import com.byd.ats.entity.Cli2CuCmd;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Ret2ClientResult;
import com.byd.ats.service.Client2serJsonCommandHistoryRepository;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 运行图定时自动备份、导入模块
 * @author wu.xianglan
 *
 */
@Component
@Configurable
@EnableScheduling
public class ScheduledTasks{

	@Autowired
	private Client2serJsonCommandRepository cmdRepository;
	@Autowired
	private Client2serJsonCommandHistoryRepository cmdHistoryRepository;
	@Autowired
	private RabbitTemplate template;
	
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	
	ObjectMapper mapper = new ObjectMapper(); // 转换器
	
	private String exchange = "topic.ats.trainrungraph";
	private String alarmKey = "ats.trainrungraph.alert";
	
	//1分钟
    @Scheduled(fixedRate = 1000 * 60)
    public void reportCurrentTime() throws JsonProcessingException{
    	long time = new Date().getTime() - 180 * 1000;//超时时间3分钟
    	Date date = new Date(time);
    	
    	//1、查找命令执行超时的记录
    	//2、发送命令执行告警信息
    	//3、记录命令执行超时状态至历史表
    	//4、将执行超时的命令信息反馈转发给客户端
    	//5、清除超时命令
    	List<CLient2serJsonCommand> cmdList = cmdRepository.findBySCuTimeLessThan(date);//判断时间是否为空？
    	
    	if(cmdList != null && cmdList.size() > 0){
    		for(CLient2serJsonCommand cmd:cmdList){
    			//发送命令执行告警信息
    			ATSAlarmEvent alarmEvent = new ATSAlarmEvent(CmdEnum.getByCode(cmd.getCmd()).getMsg()+"命令执行超时");
    			String alarmStr = mapper.writeValueAsString(alarmEvent);
    			template.convertAndSend(exchange, alarmKey, alarmStr);
    			logger.info("[x] AlarmEvent command timeout: "+alarmStr);
    			//logger.info("[x] command timeout: "+mapper.writeValueAsString(cmd));
    			
    			//记录命令执行超时状态至历史表
    			CLient2serJsonCommandHistory cmdHostory = cmdHistoryRepository.findByMagicAndCmdAndSCuTimeAndClientNum(cmd.getMagic(), cmd.getCmd(), cmd.getsCuTime(), cmd.getClientNum());
    			cmdHostory.setStatus(404);//命令执行超时状态编码404
    			cmdHistoryRepository.saveAndFlush(cmdHostory);
    			
    			//将执行超时的命令信息反馈转发给客户端
    			Cli2CuCmd Cli2CuCmd = null;
				try {
					Cli2CuCmd = mapper.readValue(cmd.getJson(), Cli2CuCmd.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			Ret2ClientResult ret = new Ret2ClientResult();
				ret.setClient_num(cmdHostory.getClientNum());
				ret.setUser_name(cmdHostory.getUsername());
				ret.setResoult(404);
				ret.setStationcontrol_cmd_type(cmdHostory.getCmd());
				ret.setCmd_parameter(Cli2CuCmd.getCuCmdParam().getDevId());//轨道ID
				ret.setCountdownTime(0);
				ret.setWorkstation(cmdHostory.getWorkstation());
				String obj = null;
				obj = mapper.writeValueAsString(ret);
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":"+obj+"}");
				logger.info("[CIfeed] feed timeout -> Client: " + obj);
				
				//删除超时的命令信息
    			cmdRepository.delete(cmd);
    		}
    		
    	}
    	
    	/*List<CLient2serJsonCommand> cmdList1 = cmdRepository.findByRClientTimeLessThan(date);
    	if(cmdList1 != null && cmdList1.size() > 0){
    		cmdRepository.delete(cmdList1);
    	}*/
    }

}

/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.byd.ats.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import com.byd.ats.entity.AmqpCiError;
import com.byd.ats.entity.AmqpCiFeed;
import com.byd.ats.entity.AodArriveInfo;
import com.byd.ats.entity.AodRet;
import com.byd.ats.entity.Ats2ciMsgComm;
import com.byd.ats.entity.Ats2serSkipStationStatus;
import com.byd.ats.entity.Ats2vobcAtoCommand;
import com.byd.ats.entity.Ats2vobcMsgComm;
import com.byd.ats.entity.Ats2zcElectrifyTsr;
import com.byd.ats.entity.Ats2zcExecuteTsr;
import com.byd.ats.entity.Ats2zcMsgElectrifyTsr;
import com.byd.ats.entity.Ats2zcMsgExecuteTsr;
import com.byd.ats.entity.Ats2zcMsgVerifyTsr;
import com.byd.ats.entity.Ats2zcVerifyTsr;
import com.byd.ats.entity.AtsAutoTrigger;
import com.byd.ats.entity.AtsModeSwitch;
import com.byd.ats.entity.AtsMsgCommand;
import com.byd.ats.entity.CLient2serJsonCommand;
import com.byd.ats.entity.CLient2serJsonCommandHistory;
import com.byd.ats.entity.CiMode;
import com.byd.ats.entity.Client2cuPasswordConfirm;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Client2serPwdCommand;
import com.byd.ats.entity.Client2serVobcCommand;
import com.byd.ats.entity.Client2serZcCommand;
import com.byd.ats.entity.Cu2AtsCiFeed;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.byd.ats.entity.PlatformDetainState;
import com.byd.ats.entity.PlatformState;
import com.byd.ats.entity.RecvPassword;
import com.byd.ats.entity.Ret2ClientResult;
import com.byd.ats.entity.SendPassword;
import com.byd.ats.entity.SkipStationState;
import com.byd.ats.entity.TraintraceInfo;
import com.byd.ats.service.CiModeService;
import com.byd.ats.service.Client2serJsonCommandHistoryRepository;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.byd.ats.service.PlatformDetainStateService;
import com.byd.ats.service.SkipStationStateService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author zhang.yuan7
 */
//@RabbitListener(queues = "#{autoDeleteQueue1.name}")
public class Tut5Receiver implements ReceiverInterface{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	private Client2serJsonCommandRepository cmdRepository;
	@Autowired
	private Client2serJsonCommandHistoryRepository cmdHistoryRepository;
	@Autowired
	private PlatformDetainStateService platformDetainStateService;
	@Autowired
	private CiModeService ciModeService;
	@Autowired
	private SkipStationStateService skipStationStateService;
	@Autowired
	private RestTemplate restTemplate;
/*	@Autowired
	RedisService redisService;*/
	
	private String ats2cicmdKey= "ats2cu.ci.command";
	private String ats2cistaKey = "ats2cu.ci.ats_status";
	//public ObjectMapper mapper = new ObjectMapper();
	private Ats2ciMsgComm cimsg=null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	private AtsMsgCommand msgcmd =null;
	//private CLient2serJsonCommand ser2clijson = null;
	//private CLient2serJsonCommand cli2serjson = null;
	private Client2serCommand cmd = null;
	private Ats2serSkipStationStatus ats2serSkipStationStatus = null;
	private Client2serPwdCommand pwdcmd = null;
	private AtsModeSwitch mode = null;
	//private AmqpCiFeed ciFeed = null;
	private Ret2ClientResult ret = null;
	private AtsAutoTrigger autocmd;
	private List<PlatformState> pstateArray = new ArrayList<PlatformState>();
	private List<PlatformState> skipStationArray = new ArrayList<PlatformState>();
	//private PlatformDetainState platformDetainState = null;
	private SkipStationState skipStationState = null;
	private int ci_mode = -1;
	private Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为成员变量
	private AmqpCiError amqpCiError = null;
	private CiMode ciMode = null;
	private AodArriveInfo aodArriveInfo;
	//需要定义用户信息状态
	//private List<Client2serCommand> ciStack = new CopyOnWriteArrayList<Client2serCommand>();
	//@RabbitHandler
	
	public int getCi_mode() {
		return ci_mode;
	}

	public void setCi_mode(int ci_mode) {
		this.ci_mode = ci_mode;
	}
	
	
	public Tut5Receiver()
	{
		for(int i=0;i<8;i++)
		{
			this.pstateArray.add(new PlatformState());//初始化扣车状态数组大小
			this.skipStationArray.add(new PlatformState()); //初始化站台跳停状态数组大小
		}
		
	}
	
	
	/**
	 * 监听客户端指令下发
	 * @param in
	 * @throws JsonProcessingException
	 */
	@RabbitListener(queues = "#{cli2ServTrainControlQueue.name}")
	public void receive(String in){
		logger.info("receive ....." + in);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0 && tempmap.containsKey("cmd_class") && !tempmap.get("cmd_class").toString().equals(""))
			{	
				if(tempmap.get("cmd_class").toString().equals("ci"))
				{
					cmd = mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{						
						send2CI(cmd);
					}
					//设置自动折返进路:22，或者取消自动折返进路：23
					//转发给进路办理-----------2017/9/14
					if(cmd.getStationcontrol_cmd_type() == 22 || cmd.getStationcontrol_cmd_type() == 23){
						template.convertAndSend("topic.ats.trainroute", "ats.trainroute.command_feedback", in);
						logger.info("send to trainroute auto_return "+cmd);
					}
				}

				if(tempmap.get("cmd_class").toString().equals("password"))
				{
					pwdcmd = mapper.readValue(in, Client2serPwdCommand.class);
					if(pwdcmd != null)
					{
						CLient2serJsonCommandHistory commandHistory = new CLient2serJsonCommandHistory();
						commandHistory.setrClientTime(new Date());
						commandHistory.setJson(in);
						sendPwdConfirm2CU(pwdcmd, commandHistory);
					}
				}
				
				if(tempmap.get("cmd_class").toString().equals("atsmode"))
				{
					mode = mapper.readValue(in, AtsModeSwitch.class);
					if(mode != null)
					{
						CLient2serJsonCommandHistory commandHistory = new CLient2serJsonCommandHistory();
						commandHistory.setrClientTime(new Date());
						commandHistory.setJson(in);
						sendMode2Client(in,mode,commandHistory);
					}
				}
				if(tempmap.get("cmd_class").toString().equals("ATSsev"))//转发给自动进路办理模块
				{
					
					template.convertAndSend("topic.ats.trainroute", "ats.trainroute.routeAttribute", in);
					logger.info("send to autoTrigger "+in);
					
				}
				if(tempmap.get("cmd_class").toString().equals("aod"))
				{
					cmd = mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
						CLient2serJsonCommandHistory commandHistory = new CLient2serJsonCommandHistory();
						commandHistory.setrClientTime(new Date());
						commandHistory.setJson(in);
						send2Aod(cmd,commandHistory);
					}
				}
				if(tempmap.get("cmd_class").toString().equals("abnormal_ATSsev"))
				{
					ats2serSkipStationStatus = mapper.readValue(in, Ats2serSkipStationStatus.class);
					if(ats2serSkipStationStatus != null)
					{
						updatePlatformDetainStatus(ats2serSkipStationStatus);//更新站台扣车状态
					}
				}
			}
			
			tempmap = null;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void updatePlatformDetainStatus(Ats2serSkipStationStatus ats2serSkipStationStatus)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		try{
			List<PlatformState> platformStateList = ats2serSkipStationStatus.getPlatformState();
			for(PlatformState platformState:platformStateList){
				SkipStationState skipStationState = skipStationStateService.findByPlatformId(platformState.getId());
				
				if(skipStationState == null){
					skipStationState = new SkipStationState();
				}
				
				skipStationState.setClientnum(platformState.getClientnum());
				skipStationState.setUsername(platformState.getUsername());
				skipStationState.setPlatformId(platformState.getId());
				skipStationState.setDetainStatus((short) platformState.getState());
				skipStationState.setWorkstation(platformState.getWorkstation());
				// 判断是否有扣车，有扣车则取消跳停
				if (platformState.getState() == 1) {
					skipStationState.setSkipState((short) 0);
				}
				skipStationStateService.save(skipStationState);
				logger.info("send to client updateSkipStationStatus exce ok .... ");
			}
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("send to client updateSkipStationStatus exce error .... ");
		}
	}
	/**
	 * 发送密码确认信息给CU
	 * @param pwdcmd
	 * @throws JsonProcessingException
	 */
	public void sendPwdConfirm2CU(Client2serPwdCommand pwdcmd, CLient2serJsonCommandHistory commandHistory) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		//-------------myAdd---------
		commandHistory.setClientNum(pwdcmd.getClient_num());
		commandHistory.setCmd(pwdcmd.getStationcontrol_cmd_type());
		commandHistory.setCmdClass(1);
		//commandHistory.setForCmd(pwdcmd.getFor_cmd());
		commandHistory.setUsername(pwdcmd.getUser_name());
		//commandHistory.setPassword(pwdcmd.getPassword());
		commandHistory.setsCuTime(new Date());
		cmdHistoryRepository.save(commandHistory);
		
		SendPassword password = new SendPassword();
		password.setClient_num(pwdcmd.getClient_num());
		password.setStationcontrol_cmd_type(pwdcmd.getStationcontrol_cmd_type());
		password.setUser_name(pwdcmd.getUser_name());
		password.setFor_cmd(pwdcmd.getFor_cmd());
		password.setPassword(pwdcmd.getPassword());
		//pwdconfirm.setRecv_password_t(password);
		
		String obj = mapper.writeValueAsString(password);
		
		template.convertAndSend("topic.ats2cu", "ats2cu.cli.password_confirm", obj);
		pwdcmd = null;
		
		logger.info("Sent Client2cuPasswordConfirm to [ats-cu] " + obj + " ");
	}
	
	/**
	 * 客户端切换站控模式
	 * @param contrcmd
	 * @throws JsonProcessingException
	 */
	public void sendMode2Client(String in,AtsModeSwitch mode, CLient2serJsonCommandHistory commandHistory) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		//-------myAdd-------
		commandHistory.setClientNum(mode.getClient_num());
		commandHistory.setCmd(mode.getStationcontrol_cmd_type());
		commandHistory.setCmdClass(0);
		commandHistory.setUsername(mode.getUser_name());
		//commandHistory.setMagic((int) (1000+Math.random()*(Short.MAX_VALUE*2-1000)));
		
		if(mode.getStationcontrol_cmd_type() == 171) //171为中心调度员请求转为中控
		{
			String obj = "{\"stationControl\":"+in+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
			logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
		
			commandHistory.setsClientTime(new Date());
			cmdHistoryRepository.save(commandHistory);
		}
		if(mode.getStationcontrol_cmd_type() == 172)
		{
			if(mode.getCurrent_mode() == mode.getModified_mode())
			{
				String obj = "{\"stationControl\":"+in+"}";
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
				logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
			
				commandHistory.setsClientTime(new Date());
				cmdHistoryRepository.save(commandHistory);
			}
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getModified_mode() ==1)
			{
				List<CiMode> listmode = ciModeService.findAll();
				if(getCi_mode() == 85)//ATS控模式
				{
					
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(1); //设置控制模式为站控
						ciModeService.save(ciMode);
						ciMode = null;
					}
					//控制模式转换成功后发送成功消息
					String obj = "{\"stationControl\":"+in+"}";
					template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
					logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");

					commandHistory.setsClientTime(new Date());
					cmdHistoryRepository.save(commandHistory);
				}
				if(getCi_mode() == 204)
				{
					mode.setModified_mode(mode.getCurrent_mode());
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(2);//设置控制模式为非常站控
						ciModeService.save(ciMode);
						ciMode = null;
					}
					String obj = "{\"stationControl\":"+mapper.writeValueAsString(mode)+"}";
					template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
					logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
				
					commandHistory.setsClientTime(new Date());
					cmdHistoryRepository.save(commandHistory);
				}
			}
		}
		if(mode.getStationcontrol_cmd_type() == 173)
		{
			if(mode.getWay() == 0)//way==0代表车站请求转站控
			{
				String obj = "{\"stationControl\":"+in+"}";
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
				logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
				
				commandHistory.setsClientTime(new Date());
				cmdHistoryRepository.save(commandHistory);
			}
			if(mode.getWay() == 1)//way==1代表车站抢权
			{
				List<CiMode> listmode = ciModeService.findAll();
				if(getCi_mode() == 85)//ATS控
				{
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(1); //设置控制模式为站控
						ciModeService.save(ciMode);
						ciMode = null;
					}
					//控制模式转换成功后发送成功消息
					String obj = "{\"stationControl\":"+in+"}";
					template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
					logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
				
					commandHistory.setsClientTime(new Date());
					cmdHistoryRepository.save(commandHistory);
				}
				if(getCi_mode() == 204)
				{
					mode.setModified_mode(mode.getCurrent_mode());
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(2);//设置控制模式为非常站控
						ciModeService.save(ciMode);
						ciMode = null;
					}
					String obj = "{\"stationControl\":"+mapper.writeValueAsString(mode)+"}";
					template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
					logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
				
					commandHistory.setsClientTime(new Date());
					cmdHistoryRepository.save(commandHistory);
				}
			}
		}
		if(mode.getStationcontrol_cmd_type() == 174)
		{
			if(mode.getCurrent_mode() == mode.getModified_mode())
			{
				String obj = "{\"stationControl\":"+in+"}";
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
				logger.info("Sent StationControl to [ats-client] " +  obj + " ");
			
				commandHistory.setsClientTime(new Date());
				cmdHistoryRepository.save(commandHistory);
			}
			//非常站控转中控
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getCurrent_mode() == 2 && mode.getModified_mode() == 0)
			{
				Client2serCommand modecmd = new Client2serCommand();
				modecmd.setClient_num(mode.getClient_num());
				modecmd.setCmd_class(mode.getCmd_class());
				List<Integer> listmode = new ArrayList<Integer>();
				listmode.add(mode.getCi_num());
				listmode.add(0xaa);//参数为中控模式
				modecmd.setCmd_parameter(listmode);
				modecmd.setStationcontrol_cmd_type(0x23);//控制模式指令
				modecmd.setUser_name(mode.getUser_name());
				send2CI(modecmd);
				modecmd = null;
				
				//commandHistory.setCmdClass(1);
				//commandHistory.setsCuTime(new Date());
				//cmdHistoryRepository.save(commandHistory);
			}
			//非常站控转站控
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getCurrent_mode() ==2 && mode.getModified_mode() == 1)
			{
				Client2serCommand modecmd = new Client2serCommand();
				modecmd.setClient_num(mode.getClient_num());
				modecmd.setCmd_class(mode.getCmd_class());
				List<Integer> listmode = new ArrayList<Integer>();
				listmode.add(mode.getCi_num());
				listmode.add(0x55);//参数为站控模式
				modecmd.setCmd_parameter(listmode);
				modecmd.setStationcontrol_cmd_type(0x23);//控制模式指令
				modecmd.setUser_name(mode.getUser_name());
				send2CI(modecmd);
				modecmd = null;
				
				//commandHistory.setCmdClass(1);
				//commandHistory.setsCuTime(new Date());
				//cmdHistoryRepository.save(commandHistory);
			}
			//从站控转为中控
			if(mode.getCurrent_mode() != mode.getModified_mode() && mode.getCurrent_mode() ==1 && mode.getModified_mode() == 0)
			{
				List<CiMode> listmode = ciModeService.findAll();
				if(getCi_mode() == 85)//ATS控
				{
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(0); //设置控制模式为中控
						ciModeService.save(ciMode);
						ciMode = null;
					}
					//控制模式转换成功后发送成功消息
					String obj = "{\"stationControl\":"+in+"}";
					template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
					logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
				
					commandHistory.setsClientTime(new Date());
					cmdHistoryRepository.save(commandHistory);
				}
				if(getCi_mode() == 204) //非常站控
				{
					mode.setModified_mode(mode.getCurrent_mode());//把当前模式的值赋值给更新，即表示失败
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(2);//设置控制模式为非常站控
						ciModeService.save(ciMode);
						ciMode = null;
					}
					String obj = "{\"stationControl\":"+mapper.writeValueAsString(mode)+"}";
					template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
					logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
				
					commandHistory.setsClientTime(new Date());
					cmdHistoryRepository.save(commandHistory);
				}
			}
		}
		mode = null;
	}

	/**
	 * CI控制命令下发
	 * @param cmd
	 * @param cli2serjson
	 * @throws JsonProcessingException
	 */
	public void send2CI(Client2serCommand cmd) throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
	    cimsg = new Ats2ciMsgComm();
		header_info = new HeaderInfo();
		msg_header = new MsgHeader();
		msg_header.setMsg_type((short)0x203);
	    msgcmd = new AtsMsgCommand();
		msgcmd.setCommand_num(1);
		msgcmd.setCommand_type(cmd.getStationcontrol_cmd_type());
		msgcmd.setObject_id(cmd.getCmd_parameter().get(0));
		
		if(cmd.getStationcontrol_cmd_type() == 37 || cmd.getStationcontrol_cmd_type() == 35) //0x25指令需要2个参数/0x23控制模式也需要2个参数
		{
			msgcmd.setObject_other(cmd.getCmd_parameter().get(1));
		}
		
		CLient2serJsonCommand cli2serjson = new CLient2serJsonCommand();
		cli2serjson.setJson(mapper.writeValueAsString(cmd));
		cli2serjson.setUsername(cmd.getUser_name());
		cli2serjson.setClientNum(cmd.getClient_num());
		cli2serjson.setCmd(cmd.getStationcontrol_cmd_type());
		cli2serjson.setMagic(getNewMagic());
		//cli2serjson.setMagic((int) (1000+Math.random()*(Short.MAX_VALUE*2-1000))); //65534(0xFFFE): Short.MAX_VALUE=32767, Short.MIN_VALUE=-32768

		cli2serjson.setrClientTime(new Date());
		msgcmd.setCom_serial_num(cli2serjson.getMagic());
		
		cimsg.setHeader_info(header_info);
		cimsg.setMsg_header(msg_header);
		cimsg.setAts_msg_command(msgcmd);
		
		String obj =  mapper.writeValueAsString(cimsg);
		template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
		
		cli2serjson.setCmdClass(0);
		cli2serjson.setsCuTime(new Date());
		
		cli2serjson.setWorkstation(cmd.getWorkstation());//sssssssssssssss
		
		CLient2serJsonCommandHistory commandHsitory = new CLient2serJsonCommandHistory();
		BeanUtils.copyProperties(cli2serjson, commandHsitory);
		cmdHistoryRepository.save(commandHsitory);
		//cmdRepository.save(cli2serjson);
		
		//只暂时保存需要CI反馈的命令信息
		if(cli2serjson.getCmd() <= 39){
			cmdRepository.save(cli2serjson);
		}
		
		header_info =null;
		msg_header = null;
		msgcmd = null;
		cimsg =null;
		cmd = null;
		cli2serjson = null;
		logger.info("Sent to [ci] " + obj + " ");
	}
	
	/**
	 * 获取新的流水号
	 * @return
	 */
	public int getNewMagic(){
		int magic = 1000;//初始化值1000
		CLient2serJsonCommand lastCommand = cmdRepository.findTop1ByOrderByIdDesc();
		if(lastCommand != null && lastCommand.getMagic() < 65534){//最后一条记录的流水号小于65534时，则新流水号为在其基础上自增1，否则为初始值
			magic = lastCommand.getMagic() + 1;
		}
		return magic;
	}
	
	/**
	 * AOD控制命令下发
	 * @param cmd
	 * @param cli2serjson
	 * @throws JsonProcessingException
	 */
	public void send2Aod(Client2serCommand cmd, CLient2serJsonCommandHistory commandHistory)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		if(cmd.getStationcontrol_cmd_type() ==102 || cmd.getStationcontrol_cmd_type() == 103)
		{
			SkipStationState skipStationState = skipStationStateService.findByPlatformId(cmd.getCmd_parameter().get(0));//获取扣车状态
			
			if(skipStationState != null) //判断是否有扣车状态
			{
				try {
					//pstateArray = mapper.readValue(platformDetainState.getValue1(), new TypeReference<List<PlatformState>>() {});
					if(skipStationState.getDetainStatus() == 0){//获取站台对应的扣车状态，状态为0才可以跳停
						skipStationState.setClientnum(cmd.getClient_num());
						skipStationState.setUsername(cmd.getUser_name());
						if(cmd.getStationcontrol_cmd_type() == 102)
						{
							skipStationState.setSkipState((short) 1);//站台跳停
						}
						if(cmd.getStationcontrol_cmd_type() == 103)
						{
							skipStationState.setSkipState((short) 0);//取消跳停
						}
						skipStationStateService.save(skipStationState); 
						
						logger.info("Sent to Aod .....cmdtype:"+cmd.getStationcontrol_cmd_type());
					}
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			skipStationState = null;
			
		}
		if(cmd.getStationcontrol_cmd_type() == 104) //立即发车
		{
			//-------------myAdd---------?需要保存吗
			commandHistory.setClientNum(cmd.getClient_num());
			commandHistory.setCmd(cmd.getStationcontrol_cmd_type());
			commandHistory.setCmdClass(0);
			commandHistory.setUsername(cmd.getUser_name());
			//commandHistory.setCmdParameter(cmd.getCmd_parameter().get(0).toString());
			
			String ret = restTemplate.getForObject("http://serv39-trainruntask/setSkipStationCommand?platformId={platformId}&carNum={carNum}", String.class,cmd.getCmd_parameter().get(0),cmd.getCmd_parameter().get(1));
			AodRet aodRet = new AodRet();
			aodRet.setCmdType(cmd.getStationcontrol_cmd_type());
			aodRet.setCode(ret);
			aodRet.setResoult(ret);
			aodRet.setPlatformId(cmd.getCmd_parameter().get(0));
			String json;
			try {
				json = mapper.writeValueAsString(aodRet);
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back","tgi_msg:{"+json+"}");
				logger.info("Sent to Aod .....cmdtype:104...."+json);
				
				commandHistory.setsClientTime(new Date());
				cmdHistoryRepository.save(commandHistory);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				aodRet = null;
				json = null;
			}

		}
		
	}
	
	/**
	 * CI命令下发信息反馈监听
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsCiFeedQueue.name}")
	public void receiveCu2AtsCiFeed(String in)
	{
		logger.info("receiveCu2AtsCiFeed(): "+in);
		
		Ret2ClientResult ret = null;
		ObjectMapper omap = new ObjectMapper();
		AmqpCiFeed ciFeed = null;
		CLient2serJsonCommand ser2clijson = null;
		
		omap.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			ciFeed = omap.readValue(in, AmqpCiFeed.class);
			if(ciFeed != null)
			{
				int feed_num = ciFeed.getFeed_num();
				if(feed_num>0)
				{
					for(int i=0;i<feed_num;i++)
					{
						Cu2AtsCiFeed ci_feed = ciFeed.getCi_feed_n().get(i);

						if(ci_feed.getFeed_type() == 37) //顾虑掉CI传过来的道岔交权
						{
							String obj = null;
							obj = omap.writeValueAsString(ci_feed);
							template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":"+obj+"}");
							logger.info("receiveCu2AtsCiFeed: send CI data to Client Ret"+obj);
							obj = null;
						}
						else //道岔交权的时候ser2clijson为NULL
						{
							//ser2clijson = cmdRepository.findOne(ci_feed.getCom_serial_num());//根据SN来查询用户名和客户端ID
							ser2clijson = cmdRepository.findByMagicAndCmd((int)ci_feed.getCom_serial_num(), ci_feed.getFeed_type());//根据魔数和命令号来查询用户名和客户端ID

							if (ser2clijson == null) {
								logger.info("[CIfeed] Can't find this feed's command ({}, {}), so discard!", ci_feed.getFeed_type(), ci_feed.getCom_serial_num());
								continue;
							}
							
							CLient2serJsonCommandHistory ser2clijsonHistory = cmdHistoryRepository.findByMagicAndCmdAndSCuTimeAndClientNum((int)ci_feed.getCom_serial_num(), ci_feed.getFeed_type(), ser2clijson.getsCuTime(), ser2clijson.getClientNum());//根据魔数和命令号来查询用户名和客户端ID
							
							ser2clijson.setrCuTime(new Date());
							ser2clijsonHistory.setrCuTime(new Date());
							
							//if(ci_feed != null && ser2clijson != null)
							//{
								//logger.info("ser2clijson ....." + ser2clijson.getJson());
								ret = new Ret2ClientResult();
								ret.setClient_num(ser2clijson.getClientNum());
								ret.setUser_name(ser2clijson.getUsername());
								ret.setResoult(ci_feed.getFeed_status());
								ret.setStationcontrol_cmd_type(ci_feed.getFeed_type());
								ret.setCmd_parameter(ci_feed.getFeed_id());//轨道ID
								ret.setCountdownTime(ci_feed.getFeed_time());
								
								ret.setWorkstation(ser2clijson.getWorkstation());
								
								if(ci_feed.getFeed_type() == 28 || ci_feed.getFeed_type() == 29)//更新扣车状态
								{
									Client2serCommand tempcmd = omap.readValue(ser2clijson.getJson(),Client2serCommand.class);
									if(ci_feed.getFeed_status() == 1 && tempcmd != null) //等于1表示扣车成功
									{
										SkipStationState skipStationState = skipStationStateService.findByPlatformId(tempcmd.getCmd_parameter().get(0));
										if(skipStationState == null)//初始化数据库数据
										{
											skipStationState = new SkipStationState();
											skipStationState.setPlatformId(tempcmd.getCmd_parameter().get(0));
											logger.info("receiveCu2AtsCiFeed: -----initialize key PlatformState and type is 28 to Db ----");
											
										}
										skipStationState.setClientnum(tempcmd.getClient_num());
										skipStationState.setUsername(tempcmd.getUser_name());
										skipStationState.setWorkstation(tempcmd.getWorkstation());
										if(ci_feed.getFeed_type() == 28)
										{
											skipStationState.setDetainStatus((short) 1);//设置扣车状态
											skipStationState.setSkipState((short) 0);//取消跳停
										}
										if(ci_feed.getFeed_type() == 29)
										{
											skipStationState.setDetainStatus((short) 0);//取消扣车状态
										}
										skipStationStateService.save(skipStationState);
										logger.info("receiveCu2AtsCiFeed: ----- save feed_status is 0x01 and type is {} to Db -----", skipStationState.getDetainStatus());
										
										//设置扣车、取消扣车成功，转发给进路办理
										template.convertAndSend("topic.ats.trainroute", "ats.trainroute.command_feedback", omap.writeValueAsString(ret));
										logger.info("send to trainroute platform detain status "+omap.writeValueAsString(ret));
									}
									tempcmd = null;
								}

								else if(ci_feed.getFeed_type() == 35)//设置控制模式
								{
									List<CiMode> listmod = ciModeService.findAll();
									CiMode cimode = null;
									if(listmod.size() > 0)
									{
										cimode = listmod.get(0);
										if(ci_feed.getFeed_status() == 0x55)
										{
											cimode.setCi_mode(1);
										}
										if(ci_feed.getFeed_status() == 0xaa)
										{
											cimode.setCi_mode(0);
										}
										ciModeService.save(cimode);
										
										logger.info("receiveCu2AtsCiFeed----- type: 35------getFeed_status: "+ci_feed.getFeed_status());
											
									}
								}
								//if(ci_feed.getFeed_type() == 37)
								//{
									//obj = mapper.writeValueAsString(ci_feed);
									//template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":"+obj+"}");
								//}else
								//{
									/*if (ser2clijson.getStatus() == 4) { // 该命令已经反馈给客户端
										continue;
									}*/
								
								// 将有效的CI命令反馈信息转发给客户端
								if (ser2clijson.getRet() == ci_feed.getFeed_status()) {
									logger.info("[CIfeed] feed_status {} is same as the last feed_status {}, so discard!", ci_feed.getFeed_status(), ser2clijson.getRet());
									continue;
								}
								// 命令反馈的状态值不同，则更新数据库表，并转发给客户端
								ser2clijson.setRet(ci_feed.getFeed_status());
								ser2clijsonHistory.setRet(ci_feed.getFeed_status());
								// 将有效的CI命令反馈信息转发给客户端
								String obj = null;
								obj = omap.writeValueAsString(ret);
								template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":"+obj+"}");
								logger.info("[CIfeed] feed -> Client: " + obj);
								
								//设置自动折返进路:22，或者取消自动折返进路：23，设置联锁自动通过进路:30,取消联锁自动通过进路:33
								//转发给进路办理-----------2017/9/14
								if(ci_feed.getFeed_status() == 1
										&& (ci_feed.getFeed_type() == 30 || ci_feed.getFeed_type() ==31)){
									template.convertAndSend("topic.ats.trainroute", "ats.trainroute.command_feedback", obj);
									logger.info("send to trainroute auto_return "+obj);
								}
								
									/**
									| 12 | 0x12 | 进路ID | 人解 |
									| 13 | 0x13 | 区段ID | 区段故障解锁 |
									| 15 | 0x15 | 进路ID | 引导进路办理 |
									*/
									/*if (ci_feed.getFeed_type() == 12 || ci_feed.getFeed_type() == 13 || ci_feed.getFeed_type() == 15) {
										if (ci_feed.getFeed_status() != 0xff) { // 如果命令反馈状态不为等待状态（0xff），即此命令执行流程结束
											ser2clijson.setStatus(4);// 客户端的命令执行流程完结！
										}
									}
									else {
										ser2clijson.setStatus(4);// 客户端的命令执行流程完结！
									}*/
								//}
								
								logger.info("send to Client Ret"+obj);
								ser2clijson.setsClientTime(new Date());
								ser2clijsonHistory.setsClientTime(new Date());
								
								cmdRepository.saveAndFlush(ser2clijson);//保存CI返回的执行状态
								cmdHistoryRepository.saveAndFlush(ser2clijsonHistory);
								obj = null;
								
								//0xff:延时等待
								if(ci_feed.getFeed_status() != 0xff){
									cmdRepository.delete(ser2clijson);//删除已执行完成的命令
								}
							/*}
							else
							{
								logger.error("receiveCu2AtsCiFeed: -------com_serial_num object is error-----------ci_feed.getCom_serial_num(): "+ci_feed.getCom_serial_num());
							}*/
						}
						
					}
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmqpException e) {
			e.printStackTrace();
		} finally {
			ciFeed = null;
			ser2clijson = null;
			ret = null;
			in = null;
		}
	}
	
	/**
	 * 密码确认反馈监听
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsPwdConfirmFeedQueue.name}")
	public void receiveCu2AtsPwdConfirmFeed(String in) throws JsonParseException, JsonMappingException, IOException
	{
		//String inst = new String(in, "UTF-8");byte[] in
		//RecvPassword recvpassword = mapper.readValue(in,RecvPassword.class);
		logger.info("receiveCu2AtsPwdConfirmFeed "+"{\"stationControl\":"+in+"}");
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.password_confirm", "{\"stationControl\":"+in+"}");
		logger.info("send to Client PwdConfirmFeed "+"{\"stationControl\":"+in+"}");
		in = null;
		
		/*ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		RecvPassword recvpassword = mapper.readValue(in,RecvPassword.class);
		CLient2serJsonCommandHistory ser2clijsonHistory = cmdHistoryRepository.findByMagicAndCmd((int)recvpassword.getCom_serial_num(), recvpassword.getFeed_type());//根据魔数和命令号来查询用户名和客户端ID
		
		if (ser2clijsonHistory == null) {
			logger.info("[CIfeed] Can't find this feed's command ({}, {}), so discard!", recvpassword.getFeed_type(), recvpassword.getCom_serial_num());
			continue;
		}
		
		ser2clijsonHistory.setrCuTime(new Date());*/
	}

	/**
	 * 接收CI发过来的控制模式
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsModeSwitchQueue.name}")
	public void receiveCiModeStatus(String in)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		//无法判断CI是否第一次上电.
		//System.out.println("in....."+in); //85/204---中心控制/非常站控
		try {
			amqpCiError = mapper.readValue(in, AmqpCiError.class);
			if(amqpCiError != null)
			{
				List<CiMode> listmode = ciModeService.findAll();
				if(listmode.size() > 0)
				{
					CiMode mode = listmode.get(0);
					//CI上电默认更新控制模式为ATS控
					if(mode.getCi_mode() == 3 && amqpCiError.getCi_msg_error1().getCiMode() == 85 )//0xaa 0x55CI刚上电，如果收到为ATS控，把状态改为中心控制
					{
						mode.setCi_mode(0);//中心控制
						ciModeService.save(mode);
						//logger.info("amqpCiError.getCi_msg_error1().getCiMode() == 85 and mode.getCi_mode() == 3");
					}
					//CI上电更新控制模式为非常站控
					if(amqpCiError.getCi_msg_error1().getCiMode() == 204)//CI刚上电，如果收到为非常站控，把状态改为非常站控 0xcc
					{
						mode.setCi_mode(2);//非常站控模式
						ciModeService.save(mode);
						logger.debug("amqpCiError.getCi_msg_error1().getCiMode() == 204");
					}
				}
				lock.lock();
				setCi_mode(amqpCiError.getCi_msg_error1().getCiMode()); //持续更新ci_mode状态
				listmode = null;
				mode = null;
			}
			//ci_mode
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (Exception e) {
			// TODO: handle exception\
			e.printStackTrace();
		}finally {
			// TODO: handle finally clause
			lock.unlock();
			amqpCiError = null;
			in = null;
		}

	}
	
	/**
	 * 监听CI的状态
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsCiInterruptWarningQueue.name}")
	public void receiveCiInterruptWarning(String in)
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		try {
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			if(tempmap.size()>0 && tempmap.containsKey("cu_ci_rupt"))
			{
				if(tempmap.get("cu_ci_rupt").toString().equals("85")) //通讯 0x55 中断； 0xaa 未中断
				{
					List<CiMode> listmode = ciModeService.findAll();
					if(listmode.size() > 0)
					{
						ciMode = listmode.get(0);
						ciMode.setCi_mode(3);//设置控制模式为未知模式
						ciModeService.save(ciMode);
					}
					List<SkipStationState> skipStationStateList = skipStationStateService.findAll();
					if(skipStationStateList.size()>0)
					for(SkipStationState skipStationState:skipStationStateList){
						//skipStationState.setClientnum(0);
						//skipStationState.setUsername(null);
						skipStationState.setDetainStatus((short) 0);
						skipStationStateService.save(skipStationState);
					}
					tempmap = null;
					listmode = null;
					ciMode = null;
					tempmap = null;
					listmode = null;
					ciMode = null;
					//logger.info("receiveCiInterruptWarning: "+in);
				}
			}
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 监听车子的状态信息
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	/*@RabbitListener(queues = "#{ser2serTraintraceQueue.name}")
	public void receiveTrainArriveStatus(String in)
	{
		logger.info("receiveTrainArriveStatus: "+in);
		try {
			aodArriveInfo = mapper.readValue(in, AodArriveInfo.class);
			if(aodArriveInfo != null)
			{
				if(skipStationStateService.findAll().size()>0)
				{
					skipStationState = skipStationStateService.findAll().get(0);
					skipStationArray = mapper.readValue(skipStationState.getValue1(), new TypeReference<List<PlatformState>>() {});
					if(skipStationArray.size() > 0)
					{
						if(skipStationArray.get(aodArriveInfo.getNextStationId()-1).getState()==1) //获取当前车次对应的下一车站是否有跳停指令
						{
							restTemplate.getForObject("http://serv39-trainruntask/setSkipStationCommand?skipStationId={skipStationId}&skipStationCommand={skipStationCommand}", String.class,aodArriveInfo.getNextStationId(),85);
							logger.info("receiveTrainArriveStatus send to Aod and nextStationId : "+aodArriveInfo.getNextStationId());
						}
						for(int i=0;i<skipStationArray.size();i++)//多个站台对同一趟车跳停
						{
							if(skipStationArray.get(i).getState() == 1 && (aodArriveInfo.getNextStationId() == (i+1))) //车子到达上一站给车发跳停
							{
								restTemplate.getForObject("http://serv39-trainruntask/setSkipStationCommand?skipStationId={skipStationId}&skipStationCommand={skipStationCommand}", String.class,aodArriveInfo.getNextStationId(),102);
								logger.info("receiveTrainArriveStatus send to Aod and nextStationId : "+aodArriveInfo.getNextStationId());
							}
						}
					}
					
				}		
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			in = null;
		}
		
	}*/

/*	@Scheduled(fixedDelay = 2000, initialDelay = 500)
	public void senderPlatformStateToClient() throws IOException {
		
		platformDetainState = platformDetainStateService.findByKey("PlatformState");
		if(platformDetainState != null)
		{
			String json = "{\"PlatformState\":"+platformDetainState.getValue1()+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.station_status",json);
			platformDetainState = null;
			logger.info("senderPlatformStateToClient:"+json);
		}
		platformDetainState = null;
	}
	
	@Scheduled(fixedDelay = 1000, initialDelay = 500)
	public void test()
	{
		List<CiMode> listmode = ciModeService.findAll();
		if(listmode.size()>0)
		{
			//listmode.get(0)
			String json =  "{\"control_mode_status\":"+listmode.get(0).getCi_mode()+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model",json);
			logger.debug("test:"+json);
		}
	}
	
	@Scheduled(fixedDelay = 1000, initialDelay = 500)
	public void test2()
	{
		List<SkipStationState> listState = skipStationStateService.findAll();
		if(listState.size() > 0)
		{
			String json ="{\"SkipStationStatus\":"+listState.get(0).getValue1()+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model",json);
			logger.debug("test2:"+json);
		}
	}*/
	
}

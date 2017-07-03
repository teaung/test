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
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import com.byd.ats.entity.AmqpCiFeed;
import com.byd.ats.entity.Ats2ciMsgComm;
import com.byd.ats.entity.Ats2vobcAtoCommand;
import com.byd.ats.entity.Ats2vobcMsgComm;
import com.byd.ats.entity.Ats2zcElectrifyTsr;
import com.byd.ats.entity.Ats2zcExecuteTsr;
import com.byd.ats.entity.Ats2zcMsgElectrifyTsr;
import com.byd.ats.entity.Ats2zcMsgExecuteTsr;
import com.byd.ats.entity.Ats2zcMsgVerifyTsr;
import com.byd.ats.entity.Ats2zcVerifyTsr;
import com.byd.ats.entity.AtsAutoTrigger;
import com.byd.ats.entity.AtsMsgCommand;
import com.byd.ats.entity.CLient2serJsonCommand;
import com.byd.ats.entity.Client2cuPasswordConfirm;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Client2serPwdCommand;
import com.byd.ats.entity.StationControl;
import com.byd.ats.entity.Client2serVobcCommand;
import com.byd.ats.entity.Client2serZcCommand;
import com.byd.ats.entity.Cu2AtsCiFeed;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.byd.ats.entity.PlatformState;
import com.byd.ats.entity.RecvPassword;
import com.byd.ats.entity.Ret2ClientResult;
import com.byd.ats.entity.SendPassword;
import com.byd.ats.entity.Ser2ClientModeCommand;
import com.byd.ats.entity.TraintraceInfo;
import com.byd.ats.entity.TsrRetrunCode;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.byd.ats.util.MyTimerTask;
import com.byd.ats.util.RedisService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Gary Russell
 * @author Scott Deeg
 */
//@RabbitListener(queues = "#{autoDeleteQueue1.name}")
public class Tut5Receiver implements ReceiverInterface{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RabbitTemplate template;
	
	@Autowired
	private Client2serJsonCommandRepository cmdRepository;
	
	@Autowired
	RedisService redisService;
	
	private String ats2cicmdKey= "ats2cu.ci.command";
	private String ats2cistaKey = "ats2cu.ci.ats_status";
	private ObjectMapper mapper = new ObjectMapper();
	private Ats2ciMsgComm cimsg=null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	private AtsMsgCommand msgcmd =null;
	private CLient2serJsonCommand ser2clijson = null;
	private CLient2serJsonCommand cli2serjson = null;
	private Client2serCommand cmd = null;
	private Client2serPwdCommand pwdcmd = null;
	private StationControl contrcmd = null;
	private AmqpCiFeed ciFeed = null;
	private Ret2ClientResult ret = null;
	private AtsAutoTrigger autocmd;
	private PlatformState[] pstateArray= {new PlatformState(),new PlatformState(),new PlatformState(),new PlatformState(),
			new PlatformState(),new PlatformState(),new PlatformState(),new PlatformState()};//初始化数组
	//需要定义用户信息状态
	//private List<Client2serCommand> ciStack = new CopyOnWriteArrayList<Client2serCommand>();
	//@RabbitHandler
	@RabbitListener(queues = "#{cli2ServTrainControlQueue.name}")
	public void receive(String in){
		logger.info("receive ....." + in);
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0 && tempmap.containsKey("CMD_CLASS") && !tempmap.get("CMD_CLASS").toString().equals("") 
					&& tempmap.containsKey("USER_NAME") && tempmap.containsKey("CLIENT_NUM"))
			{	
				if(tempmap.get("CMD_CLASS").toString().equals("ci"))
				{
					cmd = mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
						cli2serjson = new CLient2serJsonCommand();
						cli2serjson.setJson(in);
						cli2serjson.setUsername(tempmap.get("USER_NAME").toString());
						cli2serjson.setClient_num(Integer.parseInt(tempmap.get("CLIENT_NUM").toString()));
						cmdRepository.save(cli2serjson);
						
						send2CI(cmd,cli2serjson);
					}
				}

				if(tempmap.get("CMD_CLASS").toString().equals("password"))
				{
					pwdcmd = mapper.readValue(in, Client2serPwdCommand.class);
					if(cmd != null)
					{
						sendPwdConfirm2CU(pwdcmd);
					}
				}
				
				if(tempmap.get("CMD_CLASS").toString().equals("atsmode"))
				{
					contrcmd = mapper.readValue(in, StationControl.class);
					if(cmd != null)
					{
						sendMode2Client(contrcmd);
					}
				}
				if(tempmap.get("CMD_CLASS").toString().equals("ATSsev"))
				{
					//autocmd = mapper.readValue(in, AtsAutoTrigger.class);
					///if(autocmd != null)
					//{
						//atsAutoTrigger(in);
					//}
					template.convertAndSend("topic.ats.trainroute", "ats.trainroute.routeAttribute", in);
					logger.info("send to autoTrigger "+in);
					
				}
			}

			//logger.info("receive .end....");
						
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}
	public void sendPwdConfirm2CU(Client2serPwdCommand pwdcmd) throws JsonProcessingException
	{
		//logger.info("sendPwdConfirm2CU...."+cmd.getPASSWORD());
		Client2cuPasswordConfirm pwdconfirm = new Client2cuPasswordConfirm();
		SendPassword password = new SendPassword();
		password.setClient_num(pwdcmd.getCLIENT_NUM());
		password.setTraincontrol_cmd_type(pwdcmd.getCMD_TYPE());
		password.setUser_name(pwdcmd.getUSER_NAME());
		password.setFor_cmd(pwdcmd.getFOR_CMD());
		
		password.setPassword(pwdcmd.getPASSWORD());
		pwdconfirm.setRecv_password_t(password);
		
		String obj = mapper.writeValueAsString(pwdconfirm);
		
		template.convertAndSend("topic.ats2cu", "ats2cu.cli.password_confirm", obj);
		pwdcmd = null;
		
		logger.info("Sent Client2cuPasswordConfirm to [ats-cu] " + obj + " ");
	}
	public void sendMode2Client(StationControl contrcmd) throws JsonProcessingException
	{
		//logger.info("sendMode2Client...."+cmd.getCURRENT_MODE());
		Ser2ClientModeCommand modecmd = new Ser2ClientModeCommand();
		modecmd.setStationControl(contrcmd);
		
		String obj = mapper.writeValueAsString(modecmd);
		
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
		contrcmd = null;
		
		logger.info("Sent StationControl to [ats-client] " + obj + " ");
	}

	public void send2CI(Client2serCommand cmd,CLient2serJsonCommand cli2serjson) throws JsonProcessingException {

	    cimsg = new Ats2ciMsgComm();
	    
		header_info = new HeaderInfo();
		
		msg_header = new MsgHeader();
		msg_header.setMsg_type((short)0x203);
		
	    msgcmd = new AtsMsgCommand();
		msgcmd.setCommand_num(1);
		msgcmd.setCommand_type(cmd.getCMD_TYPE());
		msgcmd.setObject_id(cmd.getCMD_PARAMETER()[0]);
		msgcmd.setCom_serial_num(cli2serjson.getId());
		
		cimsg.setHeader_info(header_info);
		cimsg.setMsg_header(msg_header);
		cimsg.setAts_msg_command(msgcmd);
		
		String obj =  mapper.writeValueAsString(cimsg);
		
		try {
			//TopicExchange topic_ats2cu  =new TopicExchange("topic.ats2cu");
			template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
			if(cmd.getCMD_TYPE() == 28)
			{
				//redisService.set(key, value)
				if(redisService.exists("PlatformState"))
				{
					//String json2 = (String) redisService.get("PlatformState");
					//PlatformState[] pstateArray2 = mapper.readValue(json2, PlatformState[].class);
					//PlatformState pstate = pstateArray[cmd.getCMD_PARAMETER()[0]];//根据站台id获取对应下标的状态信息.
					pstateArray = (PlatformState[]) redisService.get("PlatformState");
					if(pstateArray != null)
					{
						//pstate.setState(1);
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setClientnum(cmd.getCLIENT_NUM());
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setId(cmd.getCMD_PARAMETER()[0]);
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setState(1);
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setUsername(cmd.getUSER_NAME());
						redisService.set("PlatformState", pstateArray);//根据进路Id保存进路由哪个控制单元下发的状态
					}

				}
				else
				{
					pstateArray[cmd.getCMD_PARAMETER()[0]-1].setId(cmd.getCMD_PARAMETER()[0]);
					pstateArray[cmd.getCMD_PARAMETER()[0]-1].setClientnum(cmd.getCLIENT_NUM());
					pstateArray[cmd.getCMD_PARAMETER()[0]-1].setState(1);
					pstateArray[cmd.getCMD_PARAMETER()[0]-1].setUsername(cmd.getCMD_CLASS());
					//pstateArray[cmd.getCMD_PARAMETER()[0]] = new PlatformState(cmd.getCMD_PARAMETER()[0],cmd.getUSER_NAME(),cmd.getCLIENT_NUM(),1);
					//String obj1 = mapper.writeValueAsString(pstateArray);
					//String json23 = mapper.writeValueAsString(pstateArray);
					redisService.set("PlatformState", pstateArray);//根据进路Id保存进路由哪个控制单元下发的状态
					//pstateArray = null;
				}
			}
			if(cmd.getCMD_TYPE()==29)
			{
				if(redisService.exists("PlatformState"))
				{
					pstateArray = (PlatformState[]) redisService.get("PlatformState");
					if(pstateArray != null)
					{
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setClientnum(cmd.getCLIENT_NUM());
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setId(cmd.getCMD_PARAMETER()[0]);
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setState(0);
						pstateArray[cmd.getCMD_PARAMETER()[0]-1].setUsername(cmd.getUSER_NAME());
						redisService.set("PlatformState", pstateArray);
					}
					
				}
			}
			header_info =null;
			msg_header = null;
			msgcmd = null;
			cimsg =null;
			cmd = null;
			cli2serjson = null;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		logger.info("Sent to [ci] " + obj + " ");
	}

	@RabbitListener(queues = "#{cu2atsCiFeedQueue.name}")
	public void receiveCu2AtsCiFeed(String in) throws JsonParseException, JsonMappingException, IOException
	{
		logger.info("receiveCu2AtsCiFeed "+in);
		ciFeed = mapper.readValue(in, AmqpCiFeed.class);
		if(ciFeed != null)
		{
			int feed_num = ciFeed.getFeed_num();
			if(feed_num>0)
			{
				for(int i=0;i<feed_num;i++)
				{
					Cu2AtsCiFeed ci_feed = ciFeed.getCi_feed_n()[i];
					ser2clijson = cmdRepository.findOne(ci_feed.getCom_serial_num());//根据SN来查询用户名和客户端ID
					logger.info("ser2clijson ....." + ser2clijson.getJson());
					if(ci_feed != null && ser2clijson != null)
					{
						ret = new Ret2ClientResult();
						ret.setClIENT_NUM(ser2clijson.getClient_num());
						ret.setUSER_NAME(ser2clijson.getUsername());
						if(ci_feed.getFeed_status()==0x01)
						{
							ret.setRESOULT("成功");
						}
						else
						{
							ret.setRESOULT("失败");
						}
						ret.setCMD_TYPE(ci_feed.getFeed_type());
						ret.setCODE(ci_feed.getFeed_status());//设置状态码
						ret.setDATA(Integer.toString(ci_feed.getFeed_id()));//进路ID/区段ID/道岔ID
						ret.setDATA(ci_feed.getFeed_time().toGMTString());//保留计时秒数
						String obj =  mapper.writeValueAsString(ret);
						template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", obj);
						logger.info("send to Client Ret"+in);
					}
					else
					{
						logger.error("receiveCu2AtsCiFeed-----ci_feed == null or ser2clijson == null..");
					}
				}
			}
		}
		ciFeed = null;
		ser2clijson = null;
		ret = null;
	}
	
	@RabbitListener(queues = "#{cu2atsPwdConfirmFeedQueue.name}")
	public void receiveCu2AtsPwdConfirmFeed(String in) throws JsonParseException, JsonMappingException, IOException
	{
		//RecvPassword recvpassword = mapper.readValue(in,RecvPassword.class);
		logger.info("receiveCu2AtsPwdConfirmFeed "+in);
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.password_confirm", in);
		logger.info("send to Client "+in);
	}

	@Scheduled(fixedDelay = 250, initialDelay = 500)
	public void senderPlatformStateToClient() throws IOException {
		if(redisService.exists("PlatformState"))
		{
			pstateArray = (PlatformState[])redisService.get("PlatformState");
			String ojson =  mapper.writeValueAsString(pstateArray);
			logger.debug("senderPlatformStateToClient:"+ojson);
		}
	}
}

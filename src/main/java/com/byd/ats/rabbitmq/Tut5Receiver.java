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
import com.byd.ats.entity.PlatformDetainState;
import com.byd.ats.entity.PlatformState;
import com.byd.ats.entity.RecvPassword;
import com.byd.ats.entity.Ret2ClientResult;
import com.byd.ats.entity.SendPassword;
import com.byd.ats.entity.Ser2ClientModeCommand;
import com.byd.ats.entity.TraintraceInfo;
import com.byd.ats.entity.TsrRetrunCode;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.byd.ats.service.PlatformDetainStateService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private PlatformDetainStateService platformDetainStateService;
/*	@Autowired
	RedisService redisService;*/
	
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
	private PlatformDetainState platformDetainState = null;
	//需要定义用户信息状态
	//private List<Client2serCommand> ciStack = new CopyOnWriteArrayList<Client2serCommand>();
	//@RabbitHandler
	
	/**
	 * 监听客户端指令下发
	 * @param in
	 * @throws JsonProcessingException
	 */
	@RabbitListener(queues = "#{cli2ServTrainControlQueue.name}")
	public void receive(String in){
		logger.info("receive ....." + in);
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			//mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0 && tempmap.containsKey("cmd_class") && !tempmap.get("cmd_class").toString().equals("") 
					&& tempmap.containsKey("user_name") && tempmap.containsKey("client_num"))
			{	
				if(tempmap.get("cmd_class").toString().equals("ci"))
				{
					cmd = mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
						cli2serjson = new CLient2serJsonCommand();
						cli2serjson.setJson(in);
						cli2serjson.setUsername(tempmap.get("user_name").toString());
						cli2serjson.setClient_num(Integer.parseInt(tempmap.get("client_num").toString()));
						cmdRepository.save(cli2serjson);
						
						send2CI(cmd,cli2serjson);
					}
				}

				if(tempmap.get("cmd_class").toString().equals("password"))
				{
					pwdcmd = mapper.readValue(in, Client2serPwdCommand.class);
					if(pwdcmd != null)
					{
						sendPwdConfirm2CU(pwdcmd);
					}
				}
				
				if(tempmap.get("cmd_class").toString().equals("atsmode"))
				{
					//contrcmd = mapper.readValue(in, StationControl.class);
					//if(contrcmd != null)
					//{
						sendMode2Client(in);
					//}
				}
				if(tempmap.get("cmd_class").toString().equals("ATSsev"))//转发给自动进路办理模块
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
	
	/**
	 * 发送密码确认信息给CU
	 * @param pwdcmd
	 * @throws JsonProcessingException
	 */
	public void sendPwdConfirm2CU(Client2serPwdCommand pwdcmd) throws JsonProcessingException
	{
		//logger.info("sendPwdConfirm2CU...."+cmd.getPASSWORD());
		//Client2cuPasswordConfirm pwdconfirm = new Client2cuPasswordConfirm();
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
	public void sendMode2Client(String in) throws JsonProcessingException
	{
		//logger.info("sendMode2Client...."+cmd.getCURRENT_MODE());
		//Ser2ClientModeCommand modecmd = new Ser2ClientModeCommand();
		//modecmd.setStationControl(contrcmd);
		
		String obj = "{\"stationControl\":"+in+"}";
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
		logger.info("Sent StationControl to [ats-client] " +  obj + " ");
	}

	/**
	 * CI控制命令下发
	 * @param cmd
	 * @param cli2serjson
	 * @throws JsonProcessingException
	 */
	public void send2CI(Client2serCommand cmd,CLient2serJsonCommand cli2serjson) throws JsonProcessingException {

	    cimsg = new Ats2ciMsgComm();
	    
		header_info = new HeaderInfo();
		
		msg_header = new MsgHeader();
		msg_header.setMsg_type((short)0x203);
		
	    msgcmd = new AtsMsgCommand();
		msgcmd.setCommand_num(1);
		msgcmd.setCommand_type(cmd.getStationcontrol_cmd_type());
		msgcmd.setObject_id(cmd.getCmd_parameter()[0]);
		msgcmd.setCom_serial_num(cli2serjson.getId());
		
		cimsg.setHeader_info(header_info);
		cimsg.setMsg_header(msg_header);
		cimsg.setAts_msg_command(msgcmd);
		
		String obj =  mapper.writeValueAsString(cimsg);
		
		try {
			//TopicExchange topic_ats2cu  =new TopicExchange("topic.ats2cu");
			template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
			if(cmd.getStationcontrol_cmd_type() == 28)
			{
				platformDetainState = platformDetainStateService.findByKey("PlatformState");
				String value = ""; 
				if(platformDetainState != null)
				{
					PlatformState[] tempArray = mapper.readValue(platformDetainState.getValue1(), PlatformState[].class);
					tempArray[cmd.getCmd_parameter()[0]-1].setClientnum(cmd.getClient_num());
					tempArray[cmd.getCmd_parameter()[0]-1].setId(cmd.getCmd_parameter()[0]);
					tempArray[cmd.getCmd_parameter()[0]-1].setState(1);
					tempArray[cmd.getCmd_parameter()[0]-1].setUsername(cmd.getUser_name());
					value = mapper.writeValueAsString(tempArray);
					platformDetainState.setValue1(value);
					platformDetainStateService.save(platformDetainState);
					value = "";
					platformDetainState = null;
					tempArray =null;
					
				}
				else
				{
					platformDetainState = new PlatformDetainState();
					pstateArray[cmd.getCmd_parameter()[0]-1].setId(cmd.getCmd_parameter()[0]);
					pstateArray[cmd.getCmd_parameter()[0]-1].setClientnum(cmd.getClient_num());
					pstateArray[cmd.getCmd_parameter()[0]-1].setState(1);
					pstateArray[cmd.getCmd_parameter()[0]-1].setUsername(cmd.getUser_name());
					value = mapper.writeValueAsString(pstateArray);
					platformDetainState.setKey1("PlatformState");
					platformDetainState.setValue1(value);
					platformDetainStateService.save(platformDetainState);
					platformDetainState = null;
					value = "";
					
				}
/*				//redisService.set(key, value)
				if(redisService.exists("PlatformState"))
				{
					pstateArray = (PlatformState[]) redisService.get("PlatformState");
					if(pstateArray != null)
					{
						//pstate.setState(1);
						pstateArray[cmd.getCmd_parameter()[0]-1].setClientnum(cmd.getClient_num());
						pstateArray[cmd.getCmd_parameter()[0]-1].setId(cmd.getCmd_parameter()[0]);
						pstateArray[cmd.getCmd_parameter()[0]-1].setState(1);
						pstateArray[cmd.getCmd_parameter()[0]-1].setUsername(cmd.getUser_name());
						redisService.set("PlatformState", pstateArray);//根据进路Id保存进路由哪个控制单元下发的状态
					}

				}
				else
				{
					pstateArray[cmd.getCmd_parameter()[0]-1].setId(cmd.getCmd_parameter()[0]);
					pstateArray[cmd.getCmd_parameter()[0]-1].setClientnum(cmd.getClient_num());
					pstateArray[cmd.getCmd_parameter()[0]-1].setState(1);
					pstateArray[cmd.getCmd_parameter()[0]-1].setUsername(cmd.getUser_name());
					//pstateArray[cmd.getCMD_PARAMETER()[0]] = new PlatformState(cmd.getCMD_PARAMETER()[0],cmd.getUSER_NAME(),cmd.getCLIENT_NUM(),1);
					//String obj1 = mapper.writeValueAsString(pstateArray);
					//String json23 = mapper.writeValueAsString(pstateArray);
					redisService.set("PlatformState", pstateArray);//根据进路Id保存进路由哪个控制单元下发的状态
					//pstateArray = null;
				}*/
			}
			if(cmd.getStationcontrol_cmd_type()==29)
			{
				
					platformDetainState = platformDetainStateService.findByKey("PlatformState");
					if(platformDetainState !=null)
					{
						PlatformState[] tempArray = mapper.readValue(platformDetainState.getValue1(), PlatformState[].class);
						tempArray[cmd.getCmd_parameter()[0]-1].setClientnum(cmd.getClient_num());
						tempArray[cmd.getCmd_parameter()[0]-1].setId(cmd.getCmd_parameter()[0]);
						tempArray[cmd.getCmd_parameter()[0]-1].setState(0);
						tempArray[cmd.getCmd_parameter()[0]-1].setUsername(cmd.getUser_name());
						String value = mapper.writeValueAsString(tempArray);
						platformDetainState.setValue1(value);
						platformDetainStateService.save(platformDetainState);
						value = "";
						platformDetainState = null;
						tempArray =null;
					}
/*					if(pstateArray != null)
					{
						pstateArray[cmd.getCmd_parameter()[0]-1].setClientnum(cmd.getClient_num());
						pstateArray[cmd.getCmd_parameter()[0]-1].setId(cmd.getCmd_parameter()[0]);
						pstateArray[cmd.getCmd_parameter()[0]-1].setState(0);
						pstateArray[cmd.getCmd_parameter()[0]-1].setUsername(cmd.getUser_name());
						redisService.set("PlatformState", pstateArray);
					}*/
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

	/**
	 * 命令下发信息反馈监听
	 * @param in
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RabbitListener(queues = "#{cu2atsCiFeedQueue.name}")
	//public void receiveCu2AtsCiFeed(String in) throws JsonParseException, JsonMappingException, IOException
	public void receiveCu2AtsCiFeed(String in)
	{
		logger.info("receiveCu2AtsCiFeed(): "+in);
		try {
			ciFeed = mapper.readValue(in, AmqpCiFeed.class);
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

		if(ciFeed != null)
		{
			int feed_num = ciFeed.getFeed_num();
			logger.info("receiveCu2AtsCiFeed(): feed_num=" + feed_num);
			if(feed_num>0)
			{
				for(int i=0;i<feed_num;i++)
				{
					Cu2AtsCiFeed ci_feed = ciFeed.getCi_feed_n().get(i);
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
						//ret.setDATA(ci_feed.getFeed_time().toGMTString());//保留计时秒数
						ret.setDATA(Integer.toString(ci_feed.getFeed_time()));//保留计时秒数
						String obj = null;
						try {
							obj = mapper.writeValueAsString(ret);
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
	}

	
	/**
	 * 周期给客户端发送车站扣车人状态
	 * @throws IOException
	 */
	@Scheduled(fixedDelay = 250, initialDelay = 500)
	public void senderPlatformStateToClient() throws IOException {
		
		platformDetainState = platformDetainStateService.findByKey("PlatformState");
		if(platformDetainState != null)
		{
			String ojson = "{\"PlatformState\":"+platformDetainState.getValue1()+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.station_status",ojson);
			platformDetainState = null;
			logger.debug("senderPlatformStateToClient:"+ojson);
		}
		
/*		if(redisService.exists("PlatformState"))
		{
			pstateArray = (PlatformState[])redisService.get("PlatformState");
			String ojson = "{\"PlatformState\":"+mapper.writeValueAsString(pstateArray)+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.station_status",ojson);
			logger.debug("senderPlatformStateToClient:"+ojson);
			
		}*/
	}
}

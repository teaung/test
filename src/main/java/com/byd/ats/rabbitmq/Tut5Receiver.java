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

import com.byd.ats.entity.AmqpCiError;
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
import com.byd.ats.entity.AtsModeSwitch;
import com.byd.ats.entity.AtsMsgCommand;
import com.byd.ats.entity.CLient2serJsonCommand;
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
import com.byd.ats.entity.TraintraceInfo;
import com.byd.ats.entity.TsrRetrunCode;
import com.byd.ats.service.CiModeService;
import com.byd.ats.service.Client2serJsonCommandRepository;
import com.byd.ats.service.PlatformDetainStateService;
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
	private PlatformDetainStateService platformDetainStateService;
	@Autowired
	private CiModeService ciModeService;
/*	@Autowired
	RedisService redisService;*/
	
	private String ats2cicmdKey= "ats2cu.ci.command";
	private String ats2cistaKey = "ats2cu.ci.ats_status";
	public ObjectMapper mapper = new ObjectMapper();
	private Ats2ciMsgComm cimsg=null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	private AtsMsgCommand msgcmd =null;
	private CLient2serJsonCommand ser2clijson = null;
	private CLient2serJsonCommand cli2serjson = null;
	private Client2serCommand cmd = null;
	private Client2serPwdCommand pwdcmd = null;
	private AtsModeSwitch mode = null;
	private AmqpCiFeed ciFeed = null;
	private Ret2ClientResult ret = null;
	private AtsAutoTrigger autocmd;
	private List<PlatformState> pstateArray = new ArrayList<PlatformState>();
	/*private PlatformState[] pstateArray= {new PlatformState(),new PlatformState(),new PlatformState(),new PlatformState(),
			new PlatformState(),new PlatformState(),new PlatformState(),new PlatformState()};//初始化数组
*/	private PlatformDetainState platformDetainState = null;
	private int ci_mode = -1;
	private Lock lock = new ReentrantLock();  // 注意这个地方:lock被声明为成员变量
	private AmqpCiError amqpCiError = null;
	private CiMode ciMode = null;
	//需要定义用户信息状态
	//private List<Client2serCommand> ciStack = new CopyOnWriteArrayList<Client2serCommand>();
	//@RabbitHandler
	
	public int getCi_mode() {
		return ci_mode;
	}

	public void setCi_mode(int ci_mode) {
		this.ci_mode = ci_mode;
	}

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
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0 && tempmap.containsKey("cmd_class") && !tempmap.get("cmd_class").toString().equals("") 
					&& tempmap.containsKey("user_name") && tempmap.containsKey("client_num"))
			{	
				if(tempmap.get("cmd_class").toString().equals("ci"))
				{
					cmd = mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
/*						cli2serjson = new CLient2serJsonCommand();
						cli2serjson.setJson(in);
						cli2serjson.setUsername(tempmap.get("user_name").toString());
						cli2serjson.setClient_num(Integer.parseInt(tempmap.get("client_num").toString()));
						cmdRepository.save(cli2serjson);*/
						
						for(int i=0;i<8;i++)
						{
							pstateArray.add(new PlatformState());//初始化数组大小
						}
						
						send2CI(cmd);
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
					mode = mapper.readValue(in, AtsModeSwitch.class);
					if(mode != null)
					{
						sendMode2Client(in,mode);
					}
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
			e.printStackTrace();
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
	public void sendMode2Client(String in,AtsModeSwitch mode) throws JsonProcessingException
	{
		//logger.info("sendMode2Client...."+cmd.getCURRENT_MODE());
		//Ser2ClientModeCommand modecmd = new Ser2ClientModeCommand();
		//modecmd.setStationControl(contrcmd);
		System.out.println("getCi_mode()...."+getCi_mode());
		if(mode.getStationcontrol_cmd_type() == 171) //171为中心调度员请求转为中控
		{
			String obj = "{\"stationControl\":"+in+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
			logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
		}
		if(mode.getStationcontrol_cmd_type() == 172)
		{
			if(mode.getCurrent_mode() == mode.getModified_mode())
			{
				String obj = "{\"stationControl\":"+in+"}";
				template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
				logger.info("Sent StationControl to [ats-client]: "+mode.getStationcontrol_cmd_type() +  obj + " ");
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
		
		cli2serjson = new CLient2serJsonCommand();
		cli2serjson.setJson(mapper.writeValueAsString(cmd));
		cli2serjson.setUsername(cmd.getUser_name());
		cli2serjson.setClient_num(cmd.getClient_num());
		cmdRepository.save(cli2serjson);
		msgcmd.setCom_serial_num(cli2serjson.getId());
		
		cimsg.setHeader_info(header_info);
		cimsg.setMsg_header(msg_header);
		cimsg.setAts_msg_command(msgcmd);
		
		String obj =  mapper.writeValueAsString(cimsg);
		template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
		
		header_info =null;
		msg_header = null;
		msgcmd = null;
		cimsg =null;
		cmd = null;
		cli2serjson = null;
		logger.info("Sent to [ci] " + obj + " ");
	}

	/**
	 * CI命令下发信息反馈监听
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
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			ciFeed = mapper.readValue(in, AmqpCiFeed.class);
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
							obj = mapper.writeValueAsString(ci_feed);
							template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":"+obj+"}");
							logger.info("receiveCu2AtsCiFeed: send CI data to Client Ret"+obj);
							obj = null;
						}
						else
						{
							ser2clijson = cmdRepository.findOne(ci_feed.getCom_serial_num());//根据SN来查询用户名和客户端ID
							if(ci_feed != null && ser2clijson != null)
							{
								//logger.info("ser2clijson ....." + ser2clijson.getJson());
								ret = new Ret2ClientResult();
								ret.setClient_num(ser2clijson.getClient_num());
								ret.setUser_name(ser2clijson.getUsername());
								ret.setResoult(ci_feed.getFeed_status());
								ret.setStationcontrol_cmd_type(ci_feed.getFeed_type());
								ret.setCmd_parameter(ci_feed.getFeed_id());//轨道ID
								ret.setCountdownTime(ci_feed.getFeed_time());
								if(ci_feed.getFeed_type() == 28)//更新扣车状态
								{
									Client2serCommand tempcmd = mapper.readValue(ser2clijson.getJson(),Client2serCommand.class);
									if(ci_feed.getFeed_status() == 1 && tempcmd != null) //等于3表示扣车成功
									{
										platformDetainState = platformDetainStateService.findByKey("PlatformState");
										String value = ""; 
										if(platformDetainState != null )
										{
											List<PlatformState> tempArray;
											tempArray = mapper.readValue(platformDetainState.getValue1(), new TypeReference<List<PlatformState>>() {});
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setClientnum(tempcmd.getClient_num());
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setId(tempcmd.getCmd_parameter().get(0));
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setState(1);
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setUsername(tempcmd.getUser_name());
											value = mapper.writeValueAsString(tempArray);
											platformDetainState.setValue1(value);
											platformDetainStateService.save(platformDetainState);
											logger.info("receiveCu2AtsCiFeed: ----- save feed_status is 0x01 and type is 28 to Db -----");
											value = null;
											platformDetainState = null;
											tempArray =null;
										}
										else//初始化数据库数据
										{
											platformDetainState = new PlatformDetainState();
											pstateArray.get(tempcmd.getCmd_parameter().get(0)-1).setId(tempcmd.getCmd_parameter().get(0));
											pstateArray.get(tempcmd.getCmd_parameter().get(0)-1).setClientnum(tempcmd.getClient_num());
											pstateArray.get(tempcmd.getCmd_parameter().get(0)-1).setState(1);
											pstateArray.get(tempcmd.getCmd_parameter().get(0)-1).setUsername(tempcmd.getUser_name());
											value = mapper.writeValueAsString(pstateArray);
											platformDetainState.setKey1("PlatformState");
											platformDetainState.setValue1(value);
											platformDetainStateService.save(platformDetainState);
											logger.info("receiveCu2AtsCiFeed: -----initialize key PlatformState and type is 28 to Db ----");
											platformDetainState = null;
											
										}
									}
									tempcmd = null;
								}
								if(ci_feed.getFeed_type() == 29)//更新取消扣车状态
								{
									Client2serCommand tempcmd = mapper.readValue(ser2clijson.getJson(),Client2serCommand.class);
									if(ci_feed.getFeed_status() == 1)
									{
										platformDetainState = platformDetainStateService.findByKey("PlatformState");
										if(platformDetainState !=null && tempcmd != null)
										{
											List<PlatformState> tempArray;
											tempArray = mapper.readValue(platformDetainState.getValue1(), new TypeReference<List<PlatformState>>() {});
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setClientnum(tempcmd.getClient_num());
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setId(tempcmd.getCmd_parameter().get(0));
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setState(0);
											tempArray.get(tempcmd.getCmd_parameter().get(0)-1).setUsername(tempcmd.getUser_name());
											String value = mapper.writeValueAsString(tempArray);
											platformDetainState.setValue1(value);
											platformDetainStateService.save(platformDetainState);
											logger.info("receiveCu2AtsCiFeed: -----save feed_status is 0x01 and type is 29 to Db ----");
											platformDetainState = null;
											tempArray =null;
											value = null;
											tempcmd = null;
										}
									}
								}
								if(ci_feed.getFeed_type() == 35)//设置控制模式 
								{
									List<CiMode> listmod = ciModeService.findAll();
									CiMode cimode = null;
									if(listmod.size() > 0)
									{
										if(ci_feed.getFeed_status() == 0x55)//站控
										{
											cimode = listmod.get(0);
											cimode.setCi_mode(1);
											ciModeService.save(cimode);
											logger.info("receiveCu2AtsCiFeed----- {type: 35,getFeed_status: 0x55,setCi_mode:1} ----");
										}
										if(ci_feed.getFeed_status() == 0xaa)//中心控
										{
											cimode = listmod.get(0);
											cimode.setCi_mode(0);
											ciModeService.save(cimode);
											logger.info("receiveCu2AtsCiFeed----- {type: 35,getFeed_status: 0x55,setCi_mode:0} ----");
										}
										
									}
								}
								String obj = null;
								obj = mapper.writeValueAsString(ret);
								template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", "{\"stationControl\":"+obj+"}");
								logger.info("send to Client Ret"+obj);
								obj = null;
							}
							else
							{
								logger.error("receiveCu2AtsCiFeed: -------com_serial_num object is error-----------ci_feed.getCom_serial_num(): "+ci_feed.getCom_serial_num());
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
					}
					//CI上电更新控制模式为非常站控
					if(amqpCiError.getCi_msg_error1().getCiMode() == 204)//CI刚上电，如果收到为非常站控，把状态改为非常站控 0xcc
					{
						mode.setCi_mode(2);//非常站控模式
						ciModeService.save(mode);
					}
				}
				lock.lock();
				setCi_mode(amqpCiError.getCi_msg_error1().getCiMode()); //持续更新ci_mode状态
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
		logger.info("receiveCiInterruptWarning: "+in);
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
						ciMode = null;
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
		}
	}
	
/*	*//**
	 * 周期给客户端发送车站扣车人状态
	 * @throws IOException
	 *//*
	@Scheduled(fixedDelay = 500, initialDelay = 500)
	public void senderPlatformStateToClient() throws IOException {
		
		platformDetainState = platformDetainStateService.findByKey("PlatformState");
		if(platformDetainState != null)
		{
			String json = "{\"PlatformState\":"+platformDetainState.getValue1()+"}";
			template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.station_status",json);
			platformDetainState = null;
			logger.debug("senderPlatformStateToClient:"+json);
		}
	}*/
/*	@Scheduled(fixedDelay = 500, initialDelay = 500)
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
	}*/
	
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StopWatch;

import com.byd.ats.entity.Ats2ciMsgComm;
import com.byd.ats.entity.Ats2vobcAtoCommand;
import com.byd.ats.entity.Ats2vobcMsgComm;
import com.byd.ats.entity.Ats2zcElectrifyTsr;
import com.byd.ats.entity.Ats2zcExecuteTsr;
import com.byd.ats.entity.Ats2zcMsgElectrifyTsr;
import com.byd.ats.entity.Ats2zcMsgExecuteTsr;
import com.byd.ats.entity.Ats2zcMsgVerifyTsr;
import com.byd.ats.entity.Ats2zcVerifyTsr;
import com.byd.ats.entity.AtsMsgCommand;
import com.byd.ats.entity.Client2cuPasswordConfirm;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Client2serPwdCommand;
import com.byd.ats.entity.StationControl;
import com.byd.ats.entity.Client2serVobcCommand;
import com.byd.ats.entity.Client2serZcCommand;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.byd.ats.entity.RecvPassword;
import com.byd.ats.entity.SendPassword;
import com.byd.ats.entity.Ser2ClientModeCommand;
import com.byd.ats.entity.TraintraceInfo;
import com.byd.ats.entity.TsrRetrunCode;
import com.byd.ats.util.MyTimerTask;
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
	private String ats2cicmdKey= "ats2cu.ci.command";
	private String ats2cistaKey = "ats2cu.ci.ats_status";
	private ObjectMapper mapper = new ObjectMapper();
	private Ats2ciMsgComm cimsg  =null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	private AtsMsgCommand msgcmd =null;
	//public static List<Client2serCommand> ciStack = new CopyOnWriteArrayList<Client2serCommand>();
	//@RabbitHandler
	@RabbitListener(queues = "#{autoDeleteQueue1.name}")
	public void receive(String in){
		logger.info("receive ....." + in);
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0&&tempmap.containsKey("CMD_CLASS")&&!tempmap.get("CMD_CLASS").toString().equals(""))
			{	

				if(tempmap.get("CMD_CLASS").toString().equals("ci"))
				{
					Client2serCommand cmd=mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
						send2CI(cmd);
					}
				}

				if(tempmap.get("CMD_CLASS").toString().equals("password"))
				{
					Client2serPwdCommand cmd =mapper.readValue(in, Client2serPwdCommand.class);
					if(cmd != null)
					{
						sendPwdConfirm2CU(cmd);
					}
				}
				if(tempmap.get("CMD_CLASS").toString().equals("atsmode"))
				{
					StationControl cmd =mapper.readValue(in, StationControl.class);
					if(cmd != null)
					{
						sendMode2Client(cmd);
					}
				}
			}

			//logger.info("receive .end....");
						
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}
	public void sendPwdConfirm2CU(Client2serPwdCommand cmd) throws JsonProcessingException
	{
		//logger.info("sendPwdConfirm2CU...."+cmd.getPASSWORD());
		Client2cuPasswordConfirm pwdconfirm = new Client2cuPasswordConfirm();
		SendPassword recvpassword = new SendPassword();
		recvpassword.setClient_num(cmd.getClIENT_NUM());
		recvpassword.setTraincontrol_cmd_type(cmd.getCMD_TYPE());
		recvpassword.setUser_name(cmd.getUSER_NAME());
		recvpassword.setFor_cmd(cmd.getFOR_CMD());
		recvpassword.setPassword(cmd.getPASSWORD());
		pwdconfirm.setRecv_password_t(recvpassword);
		String obj = mapper.writeValueAsString(pwdconfirm);
		template.convertAndSend("topic.ats2cu", "ats2cu.cli.password_confirm", obj);
		logger.info("Sent Client2cuPasswordConfirm to [ats-cu] " + obj + " ");
	}
	public void sendMode2Client(StationControl cmd) throws JsonProcessingException
	{
		//logger.info("sendMode2Client...."+cmd.getCURRENT_MODE());
		Ser2ClientModeCommand modecmd = new Ser2ClientModeCommand();
		modecmd.setStationControl(cmd);
		String obj = mapper.writeValueAsString(modecmd);
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.model", obj);
		logger.info("Sent StationControl to [ats-client] " + obj + " ");
	}

	public void send2CI(Client2serCommand cmd ) throws JsonProcessingException {

	    cimsg = new Ats2ciMsgComm();
		header_info = new HeaderInfo();
		msg_header = new MsgHeader();
		msg_header.setMsg_type((short)0x203);
	    msgcmd = new AtsMsgCommand();
		msgcmd.setCommand_num(1);
		msgcmd.setCommand_type(cmd.getCMD_TYPE());
		msgcmd.setObject_id(cmd.getCMD_PARAMETER()[0]);
		cimsg.setHeader_info(header_info);
		cimsg.setMsg_header(msg_header);
		cimsg.setAts_msg_command(msgcmd);
		String obj =  mapper.writeValueAsString(cimsg);
		try {
			//TopicExchange topic_ats2cu  =new TopicExchange("topic.ats2cu");
			template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
			//ciStack.add(cmd);//保存ci发送的状态
			header_info =null;
			msg_header = null;
			msgcmd = null;
			cimsg =null;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		logger.info("Sent to [ci] " + obj + " ");
	}
	
	
	@RabbitListener(queues = "#{autoDeleteQueue3.name}")
	public void receiveCuInfo(String in) throws JsonParseException, JsonMappingException, IOException
	{
		//RecvPassword recvpassword = mapper.readValue(in,RecvPassword.class);
		logger.info("receiveCuInfo "+in);
		template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.password_confirm", in);
		logger.info("send to Client "+in);
	}

}

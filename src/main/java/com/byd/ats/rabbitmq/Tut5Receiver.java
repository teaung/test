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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.amqp.core.TopicExchange;
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
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Client2serVobcCommand;
import com.byd.ats.entity.Client2serZcCommand;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.byd.ats.entity.TsrRetrunCode;
import com.byd.ats.util.MyTimerTask;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author Gary Russell
 * @author Scott Deeg
 */
public class Tut5Receiver {
	
	//@Autowired
	//@Qualifier("topicATS2CU")
	
	
	@Autowired
	private RabbitTemplate template;
/*	@Autowired
	private Client2serJsonCommandRepository client2serJsonCommandRepository;*/
	private String ats2cicmdKey= "ats2cu.ci.command";
	private String ats2cistaKey = "ats2cu.ci.ats_status";
	private String ats2vobccmdKey="ats2cu.vobc.command";
	private String ats2zcTsr1cmdKey = "ats2cu.zc.tsr1.command";
	private String ats2zcTsr2cmdKey = "ats2cu.zc.tsr2.command";
	private String ats2zcTsrackKey="ats2cu.zc.boot_tsr_ack";
	ObjectMapper mapper=null;
	Ats2ciMsgComm cimsg  =null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	AtsMsgCommand msgcmd =null;
	Ats2vobcMsgComm vobccmd =null;
	Ats2vobcAtoCommand ats2vobc_ato_command=null;
	Ats2zcMsgVerifyTsr ats2zc_verifytsr = null;
	 Ats2zcVerifyTsr verify_tsr =null;
	Ats2zcMsgExecuteTsr ats2zc_executetsr =null;
	Ats2zcExecuteTsr execute_tsr =null;
	Map<String,Timer> detainmap = new HashMap<String,Timer>();
	Map<String,Timer> crossmap = new HashMap<String,Timer>();
	@RabbitListener(queues = "#{autoDeleteQueue1.name}")
	public void receive1(String in) throws InterruptedException {
		System.out.println("receive1 ....." + in);
		//receive(in, 1);
		try {
			mapper = new ObjectMapper();
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0&&tempmap.containsKey("CMD_CLASS")&&!tempmap.get("CMD_CLASS").toString().equals(""))
			{
				if(tempmap.get("CMD_CLASS").toString().equals("vobc"))
				{
					//System.out.println(".......vobc..");
					Client2serCommand cmd=mapper.readValue(in, Client2serCommand.class);
					send2vobc(cmd,mapper);
				}
				if(tempmap.get("CMD_CLASS").toString().equals("ci"))
				{
					//System.out.println(".......ci..");
					Client2serCommand cmd=mapper.readValue(in, Client2serCommand.class);
					send2CI(cmd,mapper);
				}
				if(tempmap.get("CMD_CLASS").toString().equals("zc"))
				{
					Client2serZcCommand cmd = mapper.readValue(in, Client2serZcCommand.class);
					send2ZC(cmd,mapper);
				}
/*				CLient2serJsonCommand jsonCommand = new CLient2serJsonCommand();
				jsonCommand.setCreatetime(new Date());
				jsonCommand.setJson(in);
				jsonCommand.setUsername("zhang.yuan7");
				client2serJsonCommandRepository.save(jsonCommand);*/
			}

/*			for(String key : tempmap.keySet())
			{
				System.out.println("key = "+key+";value =" +tempmap.get(key));
			}*/
/*			Client2serCommand cmd=mapper.readValue(in, Client2serCommand.class);
			if(cmd.getCMD_CLASS().equals("zc"))
			{
				
			}
			if(cmd.getCMD_CLASS().equals("ci"))
			{
				send2CI(cmd,mapper);
				System.out.println(".......cmd...type:"+cmd.getCMD_TYPE());
				System.out.println(".......cmd...getCMD_PARAMETER:"+cmd.getCMD_PARAMETER());
			}
			if(cmd.getCMD_CLASS().equals("vobc"))
			{
				send2vobc(cmd,mapper);
			}*/


			System.out.println("receive1 .end....");
						
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}

	public void send2ZC(Client2serZcCommand cmd,ObjectMapper mapper) throws JsonProcessingException
	{
		if(cmd.getCMD_TYPE() == 100)
		{
			ats2zc_verifytsr = new Ats2zcMsgVerifyTsr();
			//if(cmd.getCMD_TYPE())
			header_info = new HeaderInfo();
			msg_header = new MsgHeader();
			msg_header.setMsg_type((short)0x0203);
			 verify_tsr = new Ats2zcVerifyTsr();
			 verify_tsr.setTemp_lim_v((short)cmd.getTSR_VALUE());
			 verify_tsr.setLogic_track_num((short)cmd.getTSR_NUM());
			 ats2zc_verifytsr.setHeader_info_ver(header_info);
			 ats2zc_verifytsr.setMsg_header_ver(msg_header);
			 ats2zc_verifytsr.setVerify_tsr(verify_tsr);
			// String s = Arrays.toString(cmd.getTSR_TRACKLIST());
			//int[] railid =  string2intArray(cmd.getTSR_TRACKLIST());
			 ats2zc_verifytsr.setLg_id(cmd.getTSR_TRACKLIST());
			 String obj =  mapper.writeValueAsString(ats2zc_verifytsr);
			 template.convertAndSend("topic.ats2cu", ats2zcTsr1cmdKey, obj);
			 ats2zc_verifytsr = null;
			 header_info = null;
			 msg_header = null;
			 verify_tsr = null;
			 System.out.println("Sent tsr verify  to [zc] '" + obj + "'");
			TsrRetrunCode trcode = new TsrRetrunCode();
			 trcode.setCMD_TYPE(101);
			 trcode.setCODE("success_code");
			 trcode.setRESOULT("0x55");
			 String obj1= mapper.writeValueAsString(trcode);
			 template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", obj1);
			 System.out.println("return code" + obj1 + "'");
		}
		if(cmd.getCMD_TYPE() == 101)
		{
			ats2zc_executetsr = new Ats2zcMsgExecuteTsr(); 
			header_info = new HeaderInfo();
			msg_header = new MsgHeader();
			msg_header.setMsg_type((short)0x205);
			execute_tsr = new Ats2zcExecuteTsr();
			execute_tsr.setTemp_lim_v((short)cmd.getTSR_VALUE());
			execute_tsr.setLogic_track_num((short)cmd.getTSR_NUM());
			ats2zc_executetsr.setHeader_info_exec(header_info);
			ats2zc_executetsr.setMsg_header_exec(msg_header);
			ats2zc_executetsr.setExecue_tsr(execute_tsr);
			//int[] railid =  string2intArray(cmd.getTSR_TRACKLIST());
			ats2zc_executetsr.setLg_id(cmd.getTSR_TRACKLIST());
			 String obj =  mapper.writeValueAsString(ats2zc_executetsr);
			 template.convertAndSend("topic.ats2cu", ats2zcTsr2cmdKey, obj);
			 ats2zc_executetsr = null;
			 header_info = null;
			 msg_header = null;
			 execute_tsr = null;
			 System.out.println("Sent tsr execute  to [zc] '" + obj + "'");
		}

	}
	public void send2CI(Client2serCommand cmd,ObjectMapper mapper ) throws JsonProcessingException {

	    cimsg = new Ats2ciMsgComm();
		header_info = new HeaderInfo();
		msg_header = new MsgHeader();
		msg_header.setMsg_type((short)0x203);
	    msgcmd = new AtsMsgCommand();
		msgcmd.setCommand_num(1);
		msgcmd.setCommand_type(cmd.getCMD_TYPE());
		msgcmd.setObject_id(cmd.getCMD_PARAMETER());
		cimsg.setHeader_info(header_info);
		cimsg.setMsg_header(msg_header);
		cimsg.setAts_msg_command(msgcmd);
		String obj =  mapper.writeValueAsString(cimsg);
		//System.out.println("json obj:"+obj);

		try {
			//TopicExchange topic_ats2cu  =new TopicExchange("topic.ats2cu");
			template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
			header_info =null;
			msg_header = null;
			msgcmd = null;
			cimsg =null;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		System.out.println("Sent to [ci] '" + obj + "'");
	}

	public void send2vobc(Client2serCommand  cmd,ObjectMapper mapper) throws IOException
	{
		String obj = "";
		vobccmd = new Ats2vobcMsgComm();
		header_info = new HeaderInfo();
		msg_header = new MsgHeader();
		msg_header.setMsg_type((short)0x203);
		vobccmd.setHeader_info(header_info);
		vobccmd.setMsg_header(msg_header);
		ats2vobc_ato_command = new Ats2vobcAtoCommand();
		ats2vobc_ato_command.setTrain_order_num((short)505);
		//发送扣车命令--现在没法区别中心扣车和站空扣车
		if(cmd.getCMD_TYPE()==31 ||cmd.getCMD_TYPE()==33)
		{
			ats2vobc_ato_command.setDetain_command((short)0x55);
			ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			obj =  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
			Timer timer = new Timer(); 
			MyTimerTask crossTimerTask = new MyTimerTask(template,obj,ats2vobccmdKey);
			timer.scheduleAtFixedRate(crossTimerTask,2000,2000);
			detainmap.put( Integer.toString(cmd.getCMD_PARAMETER()), timer);
		}
		//取消中心扣车和车站扣车
		if(cmd.getCMD_TYPE()==32 ||cmd.getCMD_TYPE()==34)
		{
			if(cmd.getCMD_PARAMETER()>0)
			{
				Timer detaintimer = (Timer) detainmap.get(Integer.toString(cmd.getCMD_PARAMETER()));
				if(detaintimer !=null)
				{
					detaintimer.cancel();
					detainmap.remove(Integer.toString(cmd.getCMD_PARAMETER()));
				}
			}
			ats2vobc_ato_command.setDetain_command((short)0xAA);
			ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			obj =  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
		}
		//跳停指令
		if(cmd.getCMD_TYPE() == 102)
		{
			//ats2vobc_ato_command.setDetain_command((short)0x55);
			ats2vobc_ato_command.setCross_station_command((short)0x55);
			ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			 obj =  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
			Timer crosstimer = new Timer(); 
			MyTimerTask crossTimerTask = new MyTimerTask(template,obj,ats2vobccmdKey);
			crosstimer.scheduleAtFixedRate(crossTimerTask,2000,2000);
			crossmap.put(Integer.toString(cmd.getCMD_PARAMETER()), crosstimer);
		}
		//取消跳停指令
		if(cmd.getCMD_TYPE() == 103)
		{
			if(cmd.getCMD_PARAMETER()>0)
			{
				Timer crosstimer = (Timer) crossmap.get(Integer.toString(cmd.getCMD_PARAMETER()));
				if(crosstimer !=null)
				{
					crosstimer.cancel();
					crossmap.remove(Integer.toString(cmd.getCMD_PARAMETER()));
				}
			}
			//ats2vobc_ato_command.setDetain_command((short)0x55);
			ats2vobc_ato_command.setCross_station_command((short)0x55);
			ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			 obj =  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
		}
		//提前发车
		if(cmd.getCMD_TYPE() == 104)
		{
			String response = (String) template.convertSendAndReceive("tut.rpc", "rpc",cmd.getCMD_PARAMETER() );
			//System.out.println("debug response ..."+response);
			Map<String,Object> resmap = mapper.readValue(response, Map.class);
			if(resmap.size()>0) //85是停稳状态，170是未停稳
			{
				ats2vobc_ato_command.setStop_station_time((short)0x0001);
				//ats2vobc_ato_command.setNext_station_id((short));
				ats2vobc_ato_command.setService_num(Short.parseShort(resmap.get("service_num").toString()));
				ats2vobc_ato_command.setLine_num(Short.parseShort(resmap.get("line_num").toString()));
				ats2vobc_ato_command.setTrain_line_num(Short.parseShort(resmap.get("train_line_num").toString()));
				ats2vobc_ato_command.setTrain_num(Short.parseShort(resmap.get("train_num").toString()));
				ats2vobc_ato_command.setOrigin_line_num(Short.parseShort(resmap.get("origin_line_num").toString()));
				ats2vobc_ato_command.setTrain_order_num(Short.parseShort(resmap.get("train_order_num").toString()));
				ats2vobc_ato_command.setDestin_line_num(Short.parseShort(resmap.get("destin_line_num").toString()));
				ats2vobc_ato_command.setDestin_num(Integer.parseInt(resmap.get("destin_num").toString()));
				ats2vobc_ato_command.setDirection_plan(Short.parseShort(resmap.get("direction_train").toString()));
				vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
				 obj =  mapper.writeValueAsString(vobccmd);
				template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
			}

		}
		//标记ATP命令---目前确认有列车识别跟踪来处理.
/*		if(cmd.getCMD_TYPE() == 109 || cmd.getCMD_TYPE() == 110)
		{
			
		}*/
		//取消全线扣车
		if(cmd.getCMD_TYPE() ==111)
		{
			if(detainmap.size()>0)
			{
				for(Map.Entry<String,Timer> m : detainmap.entrySet())
				{
					m.getValue().cancel();
					detainmap.remove(m.getKey());
				}
			}
/*			ats2vobc_ato_command.setDetain_command((byte)cmd.getCMD_TYPE());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			obj=  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);*/
		}
		System.out.println(" Sent to [vobc] '" + obj + "'");
		vobccmd = null;
		header_info = null;
		msg_header = null;
		ats2vobc_ato_command = null;
	}
	private int[] string2intArray(String railid)
	{
		String[] strarr = railid.split(",");
		int[] ids = new int[strarr.length];
		for(int i=0;i<strarr.length;i++)
		{
			ids[i]=Integer.parseInt(strarr[i]);
		}
		return ids;
	}

}

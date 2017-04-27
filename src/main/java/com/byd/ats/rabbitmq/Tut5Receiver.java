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
public class Tut5Receiver implements ReceiverInterface{
	
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
	private ObjectMapper mapper = new ObjectMapper();
	private Ats2ciMsgComm cimsg  =null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	private AtsMsgCommand msgcmd =null;
	private Ats2vobcMsgComm vobccmd =null;
	private Ats2vobcAtoCommand ats2vobc_ato_command=null;
	private Ats2zcMsgVerifyTsr ats2zc_verifytsr = null;
	private Ats2zcVerifyTsr verify_tsr =null;
	private Ats2zcMsgExecuteTsr ats2zc_executetsr =null;
	private Ats2zcExecuteTsr execute_tsr =null;
	public Map<String,Timer> detainmapTask = new HashMap<String,Timer>();
	private Map<String,Timer> crossmapTask = new HashMap<String,Timer>();
	private Map<Integer,Timer> trainCrossTask = new HashMap<Integer,Timer>();
	public List<TraintraceInfo> alldetainlist = new CopyOnWriteArrayList<TraintraceInfo>();
	private List<TraintraceInfo> allcrosslist = new CopyOnWriteArrayList<TraintraceInfo>();
	public static List<Client2serCommand> ciStack = new CopyOnWriteArrayList<Client2serCommand>();
	@RabbitListener(queues = "#{autoDeleteQueue1.name}")
	public void receive(String in){
		System.out.println("receive1 ....." + in);
		try {
			mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			
			if(tempmap.size()>0&&tempmap.containsKey("CMD_CLASS")&&!tempmap.get("CMD_CLASS").toString().equals(""))
			{
				if(tempmap.get("CMD_CLASS").toString().equals("vobc"))
				{
					//System.out.println(".......vobc..");
					Client2serCommand cmd=mapper.readValue(in, Client2serCommand.class);
					if(cmd !=null)
					{
						send2vobc(cmd);
					}
				}
				if(tempmap.get("CMD_CLASS").toString().equals("ci"))
				{
					//System.out.println(".......ci..");
					Client2serCommand cmd=mapper.readValue(in, Client2serCommand.class);
					if(cmd != null)
					{
						send2CI(cmd);
					}
				}
				if(tempmap.get("CMD_CLASS").toString().equals("zc"))
				{
					Client2serZcCommand cmd = mapper.readValue(in, Client2serZcCommand.class);
					if(cmd != null)
					{
						send2ZC(cmd);
					}
				}

			}

/*			for(String key : tempmap.keySet())
			{
				System.out.println("key = "+key+";value =" +tempmap.get(key));
			}*/

			System.out.println("receive1 .end....");
						
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}

	public void send2ZC(Client2serZcCommand cmd) throws JsonProcessingException
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
		//System.out.println("json obj:"+obj);
/*		if(cmd.getCMD_TYPE() == 1 ||cmd.getCMD_TYPE() == 3 || cmd.getCMD_TYPE() ==11 || cmd.getCMD_TYPE() == 18 || cmd.getCMD_TYPE() == 19
				|| cmd.getCMD_TYPE() == 23 || cmd.getCMD_TYPE()==24 || cmd.getCMD_TYPE()==25 || cmd.getCMD_TYPE()==26 || cmd.getCMD_TYPE()==36 || cmd.getCMD_TYPE()==37)
		{}*/
		try {
			//TopicExchange topic_ats2cu  =new TopicExchange("topic.ats2cu");
			template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
			ciStack.add(cmd);//保存ci发送的状态
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

	public  void send2vobc(Client2serCommand  cmd) throws IOException
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
		//发送扣车命令--现在没法区别中心扣车和站空扣车-------还要下发给CI
		if(cmd.getCMD_TYPE()==31 ||cmd.getCMD_TYPE()==33)
		{
/*			ats2vobc_ato_command.setDetain_command((short)0x55);
			ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER()[0]);
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			obj =  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);*/
			Timer timer = new Timer();  //timer是个单线程，可以在不同时间段调度多个任务
			MyTimerTask crossTimerTask = new MyTimerTask(template,cmd,alldetainlist);
			timer.scheduleAtFixedRate(crossTimerTask,1000,1000);
			detainmapTask.put( Integer.toString(cmd.getCMD_PARAMETER()[0]), timer);
			//template.convertAndSend("topic.ats2cu", ats2cicmdKey, obj);
		}
		//取消中心扣车和车站扣车
		if(cmd.getCMD_TYPE()==32 ||cmd.getCMD_TYPE()==34)
		{
			if(cmd.getCMD_PARAMETER()[0]>0)
			{
				Timer detaintimer = (Timer) detainmapTask.get(Integer.toString(cmd.getCMD_PARAMETER()[0]));
				detainmapTask.remove(Integer.toString(cmd.getCMD_PARAMETER()[0]));//取消当前任务
				if(detaintimer !=null)
				{
					detaintimer.cancel();
					ats2vobc_ato_command.setDetain_command((short)0xAA);
					ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER()[0]);
					vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
					obj =  mapper.writeValueAsString(vobccmd);
					template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
				}
				if(alldetainlist.size()>0) //释放站台对应的各车次
				{
						for(int i=0;i<alldetainlist.size();i++)
						{
							if( alldetainlist.get(i).getNext_station_id()==(short)cmd.getCMD_PARAMETER()[0])
							{
								//System.out.println("remove ...."+alldetainlist.get(i).getNext_station_id()+"   "+i);
								
								alldetainlist.remove(i--); //释放内存,通过站台关联车辆信息
								//可以在这里添加当前站台给哪些车发送取消扣车，记得给Ci
								
							}
					}		
				}
			}
		}
		//跳停指令
		if(cmd.getCMD_TYPE() == 102)
		{
			//ats2vobc_ato_command.setDetain_command((short)0x55);
			/*ats2vobc_ato_command.setCross_station_command((short)0x55);
			ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			 obj =  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);*/
			Timer crosstimer = new Timer(); 
			MyTimerTask crossTimerTask = new MyTimerTask(template,cmd,allcrosslist);
			crosstimer.scheduleAtFixedRate(crossTimerTask,1000,1000);
			crossmapTask.put(Integer.toString(cmd.getCMD_PARAMETER()[0]), crosstimer);
		}
		//取消跳停指令
		if(cmd.getCMD_TYPE() == 103)
		{
			if(cmd.getCMD_PARAMETER()[0]>0)
			{
				Timer crosstimer = (Timer) crossmapTask.get(Integer.toString(cmd.getCMD_PARAMETER()[0]));
				crossmapTask.remove(Integer.toString(cmd.getCMD_PARAMETER()[0]));
				if(crosstimer !=null)
				{
					crosstimer.cancel();
					//ats2vobc_ato_command.setDetain_command((short)0x55);
					ats2vobc_ato_command.setCross_station_command((short)0x55);
					ats2vobc_ato_command.setNext_station_id((short)cmd.getCMD_PARAMETER()[0]);
					vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
					obj =  mapper.writeValueAsString(vobccmd);
					template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
				}
			}
			if(allcrosslist.size()>0) //释放站台对应的各车次
			{
					for(int i=0;i<allcrosslist.size();i++)
					{
						if( allcrosslist.get(i).getNext_station_id()==(short)cmd.getCMD_PARAMETER()[0])
						{
							//System.out.println("remove ...."+allcrosslist.get(i).getNext_station_id()+"   "+i);
							allcrosslist.remove(i--); //释放内存,通过站台关联车辆信息
							//可以在这里添加当前站台给哪些车发送取消跳停，记得给Ci
						}
				}		
			}

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
		//取消全线扣车---针对中心扣车而言
		if(cmd.getCMD_TYPE() ==111)
		{
			if(detainmapTask.size()>0)
			{
				for(Map.Entry<String,Timer> m : detainmapTask.entrySet())
				{
					m.getValue().cancel();
					//detainmapTask.remove(m.getKey());
				}
				detainmapTask.clear();
				alldetainlist.clear();
			}
/*			ats2vobc_ato_command.setDetain_command((byte)cmd.getCMD_TYPE());
			vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
			obj=  mapper.writeValueAsString(vobccmd);
			template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);*/
		}
		if(cmd.getCMD_TYPE() == 156) //对车设置跳停
		{
			Timer timer = new Timer();  //timer是个单线程，可以在不同时间段调度多个任务
			MyTimerTask crossTimerTask = new MyTimerTask(template,cmd);
			timer.scheduleAtFixedRate(crossTimerTask,1000,1000);
			trainCrossTask.put(cmd.getCMD_PARAMETER()[1], timer);
		}
		if(cmd.getCMD_TYPE() == 157)//对车设置取消跳停跳停
		{
			Timer timer = trainCrossTask.get(cmd.getCMD_PARAMETER()[1]);
			timer.cancel();
			trainCrossTask.remove(cmd.getCMD_PARAMETER()[1]);
		}
		System.out.println(" Sent to [vobc] '" + obj + "'");
		vobccmd = null;
		header_info = null;
		msg_header = null;
		ats2vobc_ato_command = null;
	}
/*	private int[] string2intArray(String railid)
	{
		String[] strarr = railid.split(",");
		int[] ids = new int[strarr.length];
		for(int i=0;i<strarr.length;i++)
		{
			ids[i]=Integer.parseInt(strarr[i]);
		}
		return ids;
	}*/
	
	//监听列车在上一站台离开状态
	@RabbitListener(queues = "#{autoDeleteQueue2.name}")
	public  void receive2(String in) throws InterruptedException, IOException {
		System.out.println("receive2 in......."+in);
		TraintraceInfo traintraceinfo = mapper.readValue(in, TraintraceInfo.class);
		if(detainmapTask.size()>0) //表示有多个扣车指令线程已经启动
		{
			System.out.println("detainmapTask size"+detainmapTask.size());
			for(Map.Entry<String,Timer> m : detainmapTask.entrySet())
			{
				if(traintraceinfo.getNext_station_id() == Integer.parseInt(m.getKey()))
				{
					//记录上一个车站通过的车辆信息情况
					alldetainlist.add(traintraceinfo); //存储所有需要被扣车的车辆信息
				}
			}
		}
		if(crossmapTask.size()>0)
		{
			System.out.println("crossmapTask size"+crossmapTask.size());
			for(Map.Entry<String,Timer> m : crossmapTask.entrySet())
			{
				if(traintraceinfo.getNext_station_id() == Integer.parseInt(m.getKey()))
				{
					allcrosslist.add(traintraceinfo);//存储各个站台跳停所有需要跳停的车辆信息
				}
			}
		}
	}

}

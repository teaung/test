/*package com.byd.ats.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.ats.entity.Ats2vobcAtoCommand;
import com.byd.ats.entity.Ats2vobcMsgComm;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.byd.ats.entity.TraintraceInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyTimerTask  extends TimerTask{
	private RabbitTemplate template;
	private String ats2vobccmdKey="ats2cu.vobc.command";
	//private List<TraintraceInfo> alldetainlist =null;;//获取所有车站对应的所有车辆扣车信息
	//private List<TraintraceInfo> allcrosslist =null;
	private List<TraintraceInfo> listinfo = null;
	private Ats2vobcMsgComm vobccmd =null;
	private HeaderInfo header_info=null;
	private MsgHeader msg_header=null;
	private Ats2vobcAtoCommand ats2vobc_ato_command=null;
	private ObjectMapper mapper = new ObjectMapper();;
	private Client2serCommand  cmd = null;
	
	public MyTimerTask(RabbitTemplate template,Client2serCommand  cmd, List<TraintraceInfo> listinfo)
	{
		this.template = template;
		this.listinfo = listinfo;
		this.cmd = cmd;
	}
	public MyTimerTask(RabbitTemplate template,Client2serCommand  cmd)
	{
		this.template = template;
		this.cmd = cmd;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
				if(cmd.getCMD_TYPE() == 31 || cmd.getCMD_TYPE() == 33)//站台扣车指令
				{
					System.out.println(Thread.currentThread().getName()+"  "+"alldetainlist.size="+listinfo.size());
					if(listinfo.size()>0)
					{
						for(TraintraceInfo traintraceInfo:listinfo)
						{
							//扣车指令给多个车辆发送扣车信息,获取当前站台对应的上一车子，车辆信息
							if(traintraceInfo.getNext_station_id() == cmd.getCMD_PARAMETER()[0] ) //获取当前车站应该给哪些车发信息
							{
								String obj = "";
								vobccmd = new Ats2vobcMsgComm();
								header_info = new HeaderInfo();
								msg_header = new MsgHeader();
								msg_header.setMsg_type((short)0x203);
								vobccmd.setHeader_info(header_info);
								vobccmd.setMsg_header(msg_header);
								ats2vobc_ato_command = new Ats2vobcAtoCommand();
								ats2vobc_ato_command.setService_num(traintraceInfo.getService_num());
								ats2vobc_ato_command.setLine_num(traintraceInfo.getLine_num());
								ats2vobc_ato_command.setTrain_line_num(traintraceInfo.getTrain_line_num());
								ats2vobc_ato_command.setTrain_num(traintraceInfo.getTrain_num());
								ats2vobc_ato_command.setOrigin_line_num(traintraceInfo.getOrigin_line_num());
								ats2vobc_ato_command.setDestin_line_num(traintraceInfo.getDestin_line_num());
								ats2vobc_ato_command.setDestin_num(traintraceInfo.getDestin_num());
								ats2vobc_ato_command.setDirection_plan(traintraceInfo.getDirection_train());
								ats2vobc_ato_command.setTrain_order_num((short)505);
								ats2vobc_ato_command.setDetain_command((short)0x55);
								ats2vobc_ato_command.setNext_station_id(traintraceInfo.getNext_station_id());
								vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
								try {
									obj =  mapper.writeValueAsString(vobccmd);
									template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
									System.out.println("send to [vobc detain cmd]"+obj);
								} catch (JsonProcessingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								finally {
									vobccmd = null;
									header_info = null;
									msg_header = null;
									vobccmd = null;
									ats2vobc_ato_command = null;
								}
							}
						}
					
					}
				}
				if(cmd.getCMD_TYPE() == 102)//站台跳停指令
				{
					System.out.println(Thread.currentThread().getName()+"  "+"allcrosslist.size="+listinfo.size());
					if(listinfo.size()>0)
					{
						for(TraintraceInfo traintraceInfo:listinfo)
						{
							//扣车指令给多个车辆发送扣车信息,获取当前站台对应的上一车子，车辆信息
							if(traintraceInfo.getNext_station_id() == cmd.getCMD_PARAMETER()[0] ) //获取当前车站应该给哪些车发信息
							{
								String obj = "";
								vobccmd = new Ats2vobcMsgComm();
								header_info = new HeaderInfo();
								msg_header = new MsgHeader();
								msg_header.setMsg_type((short)0x203);
								vobccmd.setHeader_info(header_info);
								vobccmd.setMsg_header(msg_header);
								ats2vobc_ato_command = new Ats2vobcAtoCommand();
								ats2vobc_ato_command.setCross_station_command((short)0x55);
								ats2vobc_ato_command.setCross_station_id(traintraceInfo.getNext_station_id());
								ats2vobc_ato_command.setTrain_order_num((short)505);
								ats2vobc_ato_command.setService_num(traintraceInfo.getService_num());
								ats2vobc_ato_command.setLine_num(traintraceInfo.getLine_num());
								ats2vobc_ato_command.setTrain_line_num(traintraceInfo.getTrain_line_num());
								ats2vobc_ato_command.setTrain_num(traintraceInfo.getTrain_num());
								ats2vobc_ato_command.setOrigin_line_num(traintraceInfo.getOrigin_line_num());
								ats2vobc_ato_command.setDestin_line_num(traintraceInfo.getDestin_line_num());
								ats2vobc_ato_command.setDestin_num(traintraceInfo.getDestin_num());
								ats2vobc_ato_command.setDirection_plan(traintraceInfo.getDirection_train());
								vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
								 try {
									obj =  mapper.writeValueAsString(vobccmd);
									template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
									System.out.println("send to [vobc cross cmd]"+obj);
								} catch (JsonProcessingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								 finally {
									 vobccmd = null;
									header_info = null;
									msg_header = null;
									vobccmd = null;
									ats2vobc_ato_command = null;
								}
							}
						}
					}	
				}
				if(cmd.getCMD_TYPE() == 156) //对车设置跳停
				{
					String obj = "";
					vobccmd = new Ats2vobcMsgComm();
					header_info = new HeaderInfo();
					msg_header = new MsgHeader();
					msg_header.setMsg_type((short)0x203);
					vobccmd.setHeader_info(header_info);
					vobccmd.setMsg_header(msg_header);
					ats2vobc_ato_command = new Ats2vobcAtoCommand();
					ats2vobc_ato_command.setService_num((short)cmd.getCMD_PARAMETER()[0]);
					ats2vobc_ato_command.setTrain_num((short)cmd.getCMD_PARAMETER()[1]);
					ats2vobc_ato_command.setDestin_num(cmd.getCMD_PARAMETER()[3]);
					ats2vobc_ato_command.setDirection_plan((short)cmd.getCMD_PARAMETER()[4]);
					ats2vobc_ato_command.setTrain_order_num((short)cmd.getCMD_PARAMETER()[2]);
					ats2vobc_ato_command.setCross_station_command((short)0x55);
					vobccmd.setAts2vobc_ato_command(ats2vobc_ato_command);
					try {
						obj =  mapper.writeValueAsString(vobccmd);
						template.convertAndSend("topic.ats2cu", ats2vobccmdKey, obj);
						System.out.println("send to [vobc traincross cmd]"+obj);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finally {
						vobccmd = null;
						header_info = null;
						msg_header = null;
						vobccmd = null;
						ats2vobc_ato_command = null;
					}
				}
			
		}
	
}
*/
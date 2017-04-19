package com.byd.ats.util;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Client2serZcCommand;
/*import com.byd.entity.Ats2ciMsgComm;
import com.byd.entity.Ats2zcElectrifyTsr;
import com.byd.entity.Ats2zcLogic;
import com.byd.entity.Ats2zcMsgElectrifyTsr;
import com.byd.entity.AtsMsgCommand;
import com.byd.entity.AtsPub;
import com.byd.entity.CimsgStatus;
import com.byd.entity.Client2serCommand;
import com.byd.entity.HeaderInfo;
import com.byd.entity.MsgHeader;*/
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {

         public static void main(String[] args) throws IOException  {
        
/*        HeaderInfo hdinfo = new HeaderInfo();
        hdinfo.setInface_type((short)20);
        hdinfo.setMsg_cnum(888);
        MsgHeader msghd = new MsgHeader();
        msghd.setMsg_len((short)10);
        msghd.setMsg_type((short)20);
        AtsMsgCommand cmd = new AtsMsgCommand();
        cmd.setObject_id(900);
        Ats2ciMsgComm ats2ci = new Ats2ciMsgComm();
        ats2ci.setHeadinfo(hdinfo);
        ats2ci.setMsgheader(msghd);
        ats2ci.setAtsmsgcmd(cmd);
      //  JSONObject getObj = objectToJson(ats2ci);
        CimsgStatus cist = new CimsgStatus();
        ObjectMapper mapper = new ObjectMapper();
        String json;
		try {
			json = mapper.writeValueAsString(cist);
			  System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
/*        	 Client2serCommand cmd = new Client2serCommand();
        	 cmd.setCMD_TYPE(20);
        	 cmd.setCMD_PARAMETER(90);
        	 ObjectMapper objmapper = new ObjectMapper();
        	String obj =  objmapper.writeValueAsString(cmd);
        	System.out.println("obj output .."+obj);
        	Client2serCommand cmd2 = objmapper.readValue(obj,Client2serCommand.class);
        	System.out.println("cmd2....type"+cmd2.getCMD_TYPE());
        	System.out.println("cmd2....parameter"+cmd2.getCMD_PARAMETER());*/
/*        	 Client2serCICommand CMD = new Client2serCICommand();
        	 ObjectMapper mapper = new ObjectMapper();
        	 String  msg="{\"CMD_TYPE\":33,\"CMD_PARAMETER\":28673}";
        	String json =  mapper.writeValueAsString(CMD);
        	System.out.println("json..."+json);
        	//JsonParser js = ;
        	//mapper.readValue(p, valueType)
        	 Client2serCICommand cmd = mapper.readValue(msg, Client2serCICommand.class);
        	 System.out.println("cmd..."+cmd.getCMD_PARAMETER());*/
        	/*	Ats2ciMsgComm cimsg = new Ats2ciMsgComm();
        		HeaderInfo hinfo = new HeaderInfo();
        		MsgHeader mhd = new MsgHeader();
        		AtsMsgCommand msgcmd = new AtsMsgCommand();
        		msgcmd.setCommand_num(1);
        		msgcmd.setCommand_type(30);
        		msgcmd.setObject_id(30);
        		cimsg.setHeadinfo(hinfo);
        		cimsg.setMsgheader(mhd);
        		cimsg.setAtsmsgcmd(msgcmd);
        		String obj =  mapper.writeValueAsString(cimsg);*/
        		//System.out.println("json obj:"+obj);
/*        	 RabbitTemplate template = new RabbitTemplate();
        	 ObjectMapper mapper = new ObjectMapper();
     		Ats2ciMsgComm cimsg = new Ats2ciMsgComm();
    		HeaderInfo hinfo = new HeaderInfo();
    		MsgHeader mhd = new MsgHeader();
    		AtsMsgCommand msgcmd = new AtsMsgCommand();
    		msgcmd.setCommand_num(1);
    		msgcmd.setCommand_type(20);
    		msgcmd.setObject_id(22);*/
/*    		cimsg.setHeadinfo(hinfo);s
    		cimsg.setMsgheader(mhd);
    		cimsg.setAtsmsgcmd(msgcmd);*/
/*    		String obj =  mapper.writeValueAsString(cimsg);
    		System.out.println("json obj:"+obj);
    		template.convertAndSend(new TopicExchange("topic.ats2cu").getName(), "ats2cu.ci.command", "12321");*/
    		//System.out.println(" [x] Sent '" + obj + "'");
        	// String temp="{\"CMD_CLASS\":\"zc\",\"CMD_TYPE\":256,\"TSR_VALUE\":6,\"TSR_NUM\":67,\"TSR_TRACKLIST\":[40965,40968,40969,40970,40971,40972,40973,40975,40983,40991,40992,40993"
        	 //		+ ",40994,40995,40996,40998,41003,41004,41005,41006,41007,41008,41011,41013,41014,41015,41016,41017,41018,41029,41032,41033,41035,41036,41037,41038,41039,41040,41041,41042,41043,41044,41046,41048,41049,41050,41051,41053,41047,41045,41034,41030,41031,41019,41020,41021,41022,41023,41024,41025,41026,41027,41028,41012,41009,41010,40999]}";
        	 String temp =  "{\"CMD_CLASS\":\"vobc\",\"CMD_TYPE\":156,\"CMD_PARAMETER\":[01,003,505,06,85]}";
        	 ObjectMapper mapper = new ObjectMapper();
        	 mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        	 Client2serCommand cmd = mapper.readValue(temp, Client2serCommand.class);
        	 System.out.println("cmd..."+cmd.getCMD_TYPE());
        	// String str = "";
/*        	 for(int i :cmd.getTSR_TRACKLIST())
        	 {
        		 str = str+i+",";
        	 }
        	 System.out.println("cmd....:"+str);*/
        	 /*         	 Ats2zcMsgElectrifyTsr tsr  = new Ats2zcMsgElectrifyTsr();
        	 HeaderInfo header_info_elec = new HeaderInfo();
        	 MsgHeader msg_header_elec = new MsgHeader();
        	 Ats2zcElectrifyTsr elec_tsr = new Ats2zcElectrifyTsr();
        	 int[] lg_t_id = new int[3];
        	 lg_t_id[0]=1;
        	 lg_t_id[1]=2;
        	 lg_t_id[2]=3;
        	 tsr.setHeader_info_elec(header_info_elec);
        	 tsr.setMsg_header_elec(msg_header_elec);
        	 tsr.setElec_tsr(elec_tsr);
        	 tsr.setLg_t_id(lg_t_id);*/
        	 //Client2serZcCommand cmd =  mapper.readValue(temp, Client2serZcCommand.class);
        	//String obj =  mapper.writeValueAsString(tsr);
        	//System.out.println("obj .....:"+cmd.getTSR_TRACKLIST().toString());
/*        	for(int i :cmd.getTSR_TRACKLIST())
        	{
        		System.out.println("....getTSR_TRACKLIST:"+i);
        	}*/
/*        	 String  msg="{\"CMD_TYPE\":33,\"CMD_PARAMETER\":28673}";
 			Map<String,Object> tempmap = mapper.readValue(msg, Map.class);
        	 			for(String key : tempmap.keySet())
        	 			{
        	 				System.out.println("key = "+key+";value =" +tempmap.get(key));
        	 			}*/
         }
}

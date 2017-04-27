package com.byd.ats.rabbitmq;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.byd.ats.entity.AmqpZcStatusTsr;
import com.byd.ats.entity.Ats2zcElectrifyTsr;
import com.byd.ats.entity.Ats2zcMsgElectrifyTsr;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZcstatusReceiver implements ReceiverInterface{

	@Autowired
	private RabbitTemplate template;
	
	private ObjectMapper mapper = new ObjectMapper();
	private HeaderInfo header_info_ver = null;
	private MsgHeader msg_header_ver = null;
	private Ats2zcMsgElectrifyTsr msgElectrifyTsr = null;
	private Ats2zcElectrifyTsr elec_tsr = null;
	private boolean electrifyFlag = true;
	@Override
	@RabbitListener(queues = "#{autoDeleteQueue7.name}")
	public void receive(String in) {
		// TODO Auto-generated method stub
		//System.out.println("ZcstatusReceiver..in:"+in);
		try {
			AmqpZcStatusTsr amqpZcStatusTsr = mapper.readValue(in,AmqpZcStatusTsr.class);
			if(electrifyFlag)
			{
				if(amqpZcStatusTsr != null && amqpZcStatusTsr.getZc2ats_sta_tsr().getTsr_electrify_confirm() == 85)
				{
					msgElectrifyTsr = new Ats2zcMsgElectrifyTsr();
					header_info_ver = new HeaderInfo();
					msg_header_ver = new MsgHeader();
					msg_header_ver.setMsg_type((short)0x0207);
					msg_header_ver.setMsg_len((short)2);
					elec_tsr = new Ats2zcElectrifyTsr();
					msgElectrifyTsr.setHeader_info_elec(header_info_ver);
					msgElectrifyTsr.setMsg_header_elec(msg_header_ver);
					msgElectrifyTsr.setElec_tsr(elec_tsr);
					String obj =  mapper.writeValueAsString(msgElectrifyTsr);
					template.convertAndSend("topic.ats2cu", "ats2cu.zc.boot_tsr_ack", obj);
					System.out.println("Sent to [zc electrifyTsr cmd] '" + obj + "'");
					electrifyFlag = false;
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
		finally {
			header_info_ver = null;
			msg_header_ver = null;
			elec_tsr = null;
			msgElectrifyTsr = null;
		}
		
	}

}

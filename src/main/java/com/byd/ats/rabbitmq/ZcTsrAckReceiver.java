package com.byd.ats.rabbitmq;

import java.io.IOException;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.byd.ats.entity.AmqpZcExecuteTsr;
import com.byd.ats.entity.AmqpZcVerifyTsr;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.byd.ats.entity.TsrRetrunCode;
import com.byd.ats.entity.Zc2atsVerifyTsr;
import com.byd.ats.entity.Zs2atsExecuteTsr;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ZcTsrAckReceiver implements ReceiverInterface{

	@Autowired
	private RabbitTemplate template;
	private ObjectMapper mapper = new ObjectMapper();
	private HeaderInfo zc_header = null;
	private MsgHeader zc_msg_header = null;
	private AmqpZcVerifyTsr amqpZcVerifyTsr = null;
	private Zc2atsVerifyTsr zc2ats_ver_tsr = null;
	private AmqpZcExecuteTsr amqpZcExecuteTsr = null;
	private Zs2atsExecuteTsr zc2ats_execu_tsr = null;
	private TsrRetrunCode trcode = null;
	@Override
	@RabbitListener(queues = "#{autoDeleteQueue5.name}")
	public void receive(String in) {
		System.out.println("tsr......."+in);
		try {
			Map<String,Object> tempmap = mapper.readValue(in, Map.class);
			if(tempmap.size()>0)
			{
				if(tempmap.containsKey("zc2ats_ver_tsr"))
				{
					amqpZcVerifyTsr = mapper.readValue(in, AmqpZcVerifyTsr.class);
					if(amqpZcVerifyTsr != null)
					{
						if(amqpZcVerifyTsr.getZc2ats_ver_tsr().getConfirm_result() == 0x55)
						{
							 trcode = new TsrRetrunCode();
							 trcode.setCMD_TYPE(100);
							 trcode.setCODE("success_code");
							 trcode.setRESOULT((byte)0x55);
							 String obj1= mapper.writeValueAsString(trcode);
							 template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", obj1);
							 System.out.println("return ver_tsr success code" + obj1 + "'");
						}
						if(amqpZcVerifyTsr.getZc2ats_ver_tsr().getConfirm_result() == 0xAA)
						{
							 trcode = new TsrRetrunCode();
							 trcode.setCMD_TYPE(100);
							 trcode.setCODE("fail_code");
							 trcode.setRESOULT(amqpZcVerifyTsr.getZc2ats_ver_tsr().getFail_reason());
							 String obj1= mapper.writeValueAsString(trcode);
							 template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", obj1);
							 System.out.println("return ver_tsr fail code" + obj1 + "'");
						}
					}
				}
				if(tempmap.containsKey("zc2ats_execu_tsr"))
				{
					amqpZcExecuteTsr = mapper.readValue(in, AmqpZcExecuteTsr.class);
					if(amqpZcExecuteTsr != null)
					{
						if(amqpZcExecuteTsr.getZc2ats_execu_tsr().getConfirm_result() == 0x55)
						{
							 trcode = new TsrRetrunCode();
							 trcode.setCMD_TYPE(101);
							 trcode.setCODE("success_code");
							 trcode.setRESOULT((byte)0x55);
							 String obj1= mapper.writeValueAsString(trcode);
							 template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", obj1);
							 System.out.println("return execu_tsr success code" + obj1 + "'");
						}
						if(amqpZcExecuteTsr.getZc2ats_execu_tsr().getConfirm_result() == 0xAA)
						{
							 trcode = new TsrRetrunCode();
							 trcode.setCMD_TYPE(101);
							 trcode.setCODE("fail_code");
							 trcode.setRESOULT(amqpZcExecuteTsr.getZc2ats_execu_tsr().getWarning_msg());
							 String obj1= mapper.writeValueAsString(trcode);
							 template.convertAndSend("topic.serv2cli", "serv2cli.traincontrol.command_back", obj1);
							 System.out.println("return execu_tsr fail code" + obj1 + "'");
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
		}
		// TODO Auto-generated method stub
		
	}

	
}

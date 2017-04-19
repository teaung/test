package com.byd.ats.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import com.byd.ats.entity.AmqpCiStatus;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CistatusReceiver implements ReceiverInterface{

	private List<AmqpCiStatus> alldetainlist = new ArrayList<AmqpCiStatus>();
	private ObjectMapper mapper = new ObjectMapper();;
	private Tut5Receiver receiver = null;
	
	public CistatusReceiver(Tut5Receiver receiver)
	{
		this.receiver = receiver;
	}
	@RabbitListener(queues = "#{autoDeleteQueue4.name}")
	public void receive(String in){
/*		if(receiver != null)
		{
			System.out.println("receiver.ciStack.size().."+receiver.ciStack.size());
		}*/
		//System.out.println("out in:"+in);
		try {
			AmqpCiStatus cistatus = mapper.readValue(in, AmqpCiStatus.class);
			alldetainlist.add(cistatus);
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
		//System.out.println("CistatusReceiver..alldetainlist size:"+alldetainlist.size());
	}
}

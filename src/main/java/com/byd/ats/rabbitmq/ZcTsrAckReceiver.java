package com.byd.ats.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class ZcTsrAckReceiver implements ReceiverInterface{

	@Override
	@RabbitListener(queues = "#{autoDeleteQueue7.name}")
	public void receive(String in) {
		System.out.println("tsr......."+in);
		// TODO Auto-generated method stub
		
	}

	
}

package com.byd.ats.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class ZcstatusReceiver implements ReceiverInterface{

	@Override
	@RabbitListener(queues = "#{autoDeleteQueue3.name}")
	public void receive(String in) {
		// TODO Auto-generated method stub
		System.out.println("ZcstatusReceiver..in:"+in);
	}

}

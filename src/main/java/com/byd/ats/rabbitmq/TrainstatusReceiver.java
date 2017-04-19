package com.byd.ats.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class TrainstatusReceiver implements ReceiverInterface{

	@RabbitListener(queues = "#{autoDeleteQueue3.name}")
	public void receive(String in) {
		// TODO Auto-generated method stub
		System.out.println("TrainstatusReceiver..in:"+in);
	}

}

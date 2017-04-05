package com.byd.ats.util;

import java.util.TimerTask;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.byd.ats.entity.Ats2vobcAtoCommand;
import com.byd.ats.entity.Ats2vobcMsgComm;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.HeaderInfo;
import com.byd.ats.entity.MsgHeader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyTimerTask  extends TimerTask{
	private RabbitTemplate template;
	private String key="";
	private String json;
	
	public MyTimerTask(RabbitTemplate template,String  json,String key)
	{
		this.json = json;
		this.template = template;
		this.key = key;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
				template.convertAndSend("topic.ats2cu", key, json);
				System.out.println("send.....................................");
		}
	
}

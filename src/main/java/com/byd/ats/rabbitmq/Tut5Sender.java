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

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Gary Russell
 * @author Scott Deeg
 */
public class Tut5Sender {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	@Qualifier("topicTest")
	private TopicExchange topic_test;
	@Autowired
	@Qualifier("topicATS2CU")
	private TopicExchange topic_ats2cu;

	private int index;

	private int count;

/*	private final String[] keys = {"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox",
			"lazy.brown.fox", "lazy.pink.rabbit", "quick.brown.fox"};*/
	private final String[] keys = {"ats2cu.ci.command", "ats2cu.vobc.command", "ats2cu.zc.tsr1.command",
			"ats2cu.zc.tsr2.command","ats2cu.zc.boot_tsr_ack"};

	/*@Scheduled(fixedDelay = 1000, initialDelay = 500)
	public void send() {
		StringBuilder builder = new StringBuilder("Hello to ");
		if (++this.index == keys.length) {
			this.index = 0;
		}
		String key = keys[this.index];
		builder.append(key).append(' ');
		builder.append(Integer.toString(++this.count));
		String message = builder.toString();
		template.convertAndSend(topic.getName(), key, message);
		System.out.println(" [x] Sent '" + message + "'");
	}*/

	public void send(String msg) {
		//StringBuilder builder = new StringBuilder("Hello to ");
		StringBuilder builder = new StringBuilder("'" + msg + "' ");
		if (++this.index == keys.length) {
			this.index = 0;
		}
		String key = keys[this.index];
		builder.append(key).append(' ');
		builder.append(Integer.toString(++this.count));
		String message = builder.toString();
		template.convertAndSend(topic_test.getName(), key, message);
		System.out.println(" [x] Sent '" + message + "'");
	}
}

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

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Gary Russell
 * @author Scott Deeg
 */
//@Profile({"tut5","topics"})
@Configuration
public class Tut5Config {

	@Bean
	public TopicExchange topicCli2Serv() {
		return new TopicExchange("topic.cli2serv");
	}

	@Bean
	public TopicExchange topicATS2CU() {
		return new TopicExchange("topic.ats2cu");
	}

	@Bean
	public TopicExchange topicCU2ATS() {
		return new TopicExchange("topic.cu2ats");
	}

	@Bean
	public TopicExchange topicServ2Cli() {
		return new TopicExchange("topic.serv2cli");
	}
	
	@Bean
	public TopicExchange topicServ2Cont() {
		return new TopicExchange("topic.ats.traintrace");
		//return new TopicExchange("topic.cu2ats");
	}
	//@Profile("dev")
	private static class ReceiverConfig {

		@Bean
		public Tut5Receiver receiver() {
	 	 	return new Tut5Receiver();
		}

/*		@Bean
		public CistatusReceiver receiver2() {
	 	 	return new CistatusReceiver(receiver());
		}*/


		@Bean
		public Queue cli2ServTrainControlQueue() {
			//return new AnonymousQueue();
			return new Queue("q.traincontrol.traincontrol");
		}

		@Bean
		public Queue cu2atsCiFeedQueue() {
			//return new AnonymousQueue();
			return new Queue("q.traincontrol.cifeed");
		}
		
		@Bean
		public Queue cu2atsPwdConfirmFeedQueue() {
			//return new AnonymousQueue();
			return new Queue("q.traincontrol.pwdconfirmfeed");
		}
		
		@Bean
		public Queue routeAttributeQueue() {
			//return new AnonymousQueue();
			return new Queue("q.traincontrol.routeAttribute");
		}
		@Bean
		public Binding binding1a(@Qualifier("topicCli2Serv") TopicExchange topic, Queue cli2ServTrainControlQueue) {
			return BindingBuilder.bind(cli2ServTrainControlQueue).to(topic).with("cli2serv.traincontrol.command");
		}
		
		@Bean
		public Binding binding1c(@Qualifier("topicCU2ATS") TopicExchange topic, Queue cu2atsCiFeedQueue) {
			return BindingBuilder.bind(cu2atsCiFeedQueue).to(topic).with("cu2ats.ci.command_feedback");
		}
		
		
		@Bean
		public Binding binding1f(@Qualifier("topicCU2ATS") TopicExchange topic,Queue cu2atsPwdConfirmFeedQueue) {
			return BindingBuilder.bind(cu2atsPwdConfirmFeedQueue).to(topic).with("cu2ats.cli.password_confirm_back");
		}		

	}

	//@Profile("sender")
/*	@Bean
	public Tut5Sender sender() {
		return new Tut5Sender();
	}*/

}

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
	public TopicExchange topicTest() {
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
	
	
	/*
	@Bean
	public TopicExchange topicCli2Serv() {
		return new TopicExchange("topic.cli2serv");
	}*/

	//@Profile("receiver")
	private static class ReceiverConfig {

		@Bean
		public Tut5Receiver receiver() {
	 	 	return new Tut5Receiver();
		}

		@Bean
		public CistatusReceiver receiver2() {
	 	 	return new CistatusReceiver(receiver());
		}
		
		@Bean
		public ZcTsrAckReceiver receiver3()
		{
			return new ZcTsrAckReceiver();
		}
/*		@Bean
		public ZcstatusReceiver receiver3() {
	 	 	return new ZcstatusReceiver();
		}
		@Bean
		public TrainstatusReceiver receiver4() {
	 	 	return new TrainstatusReceiver();
		}*/
		
		@Bean
		public Queue autoDeleteQueue1() {
			return new AnonymousQueue();
		}

		@Bean
		public Queue autoDeleteQueue2() {
			return new AnonymousQueue();
		}
		
		@Bean
		public Queue autoDeleteQueue3() {
			return new AnonymousQueue();
		}
		
		@Bean
		public Queue autoDeleteQueue4() {
			return new AnonymousQueue();
		}
		
		@Bean
		public Queue autoDeleteQueue5() {
			return new AnonymousQueue();
		}

		@Bean
		public Queue autoDeleteQueue6() {
			return new AnonymousQueue();
		}
		
		@Bean
		public Queue autoDeleteQueue7() {
			return new AnonymousQueue();
		}
		@Bean
		public Binding binding1a(@Qualifier("topicTest") TopicExchange topic, Queue autoDeleteQueue1) {
			return BindingBuilder.bind(autoDeleteQueue1).to(topic).with("cli2serv.traincontrol.command");
		}

/*		@Bean
		public Binding binding1b(@Qualifier("topicCU2ATS") TopicExchange topic, Queue autoDeleteQueue3) {
			return BindingBuilder.bind(autoDeleteQueue3).to(topic).with("cu2ats.vobc.train.status");
		}*/
		
/*		@Bean
		public Binding binding1c(@Qualifier("topicCU2ATS") TopicExchange topic, Queue autoDeleteQueue4) {
			return BindingBuilder.bind(autoDeleteQueue4).to(topic).with("cu2ats.ci.status");
		}*/
		
/*		@Bean
		public Binding binding1d(@Qualifier("topicCU2ATS") TopicExchange topic,Queue autoDeleteQueue5) {
			return BindingBuilder.bind(autoDeleteQueue5).to(topic).with("cu2ats.zc.status");
		}*/

		@Bean
		public Binding binding2a(@Qualifier("topicServ2Cont") TopicExchange topic, Queue autoDeleteQueue6) {
			//return BindingBuilder.bind(autoDeleteQueue2).to(topic).with("cu2ats.vobc.ato.status");
				return BindingBuilder.bind(autoDeleteQueue6).to(topic).with("ats.traintrace.station.arrive");
		}
				
		@Bean
		public Binding binding2b(@Qualifier("topicCU2ATS") TopicExchange topic, Queue autoDeleteQueue7) {
			//return BindingBuilder.bind(autoDeleteQueue2).to(topic).with("cu2ats.vobc.ato.status");
				return BindingBuilder.bind(autoDeleteQueue7).to(topic).with("cu2ats.zc.tsr*.ack");
		}

	}

	//@Profile("sender")
/*	@Bean
	public Tut5Sender sender() {
		return new Tut5Sender();
	}*/

}

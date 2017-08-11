package com.byd.ats;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.byd.ats.entity.Client2serCommand;
/*import com.byd.ats.entity.Person;
import com.byd.ats.service.PersonRepository;*/
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ServTraincontrolApplicationTests {

	private MockMvc mvc;
/*	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate redisTemplate;*/
/*	@Autowired
	private RedisService redisService;*/
	//private Logger logger = Logger.getLogger(getClass());
	//public ObjectMapper mapper = new ObjectMapper();
	//@Autowired
	//private RabbitTemplate template;
	
/*	@Autowired
	PersonRepository personRepository;
	MockMvc mvc;
	@Autowired
	WebApplicationContext webApplicationContext;
	String expectedJson;
	
	@Before
	public void setUP() throws JsonProcessingException
	{
		Person p1 = new Person("wyf");
		Person p2 = new Person("wisely");
		personRepository.save(p1);
		personRepository.save(p2);
		expectedJson = Obj2Json(personRepository.findAll());
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	protected String Obj2Json(Object obj) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	@Test
	public void contextLoads() throws Exception {
		String uri = "/person";
		MvcResult result = (MvcResult) mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON));
		int status = result.getResponse().getStatus();
		String content = result.getResponse().getContentAsString();
		System.out.println("test......"+content);
		Assert.assertEquals("test.......erro", 200,status);
		Assert.assertEquals("test.......erro2", expectedJson,content);
		
		System.out.println("...............................................................");
	}*/

	@Test
	public void test2()
	{
/*		Client2serCommand client2serCommand = new Client2serCommand();
		client2serCommand.setCmd_class("aod");
		client2serCommand.setStationcontrol_cmd_type(104);
		client2serCommand.setClient_num(101);
		client2serCommand.setUser_name("213");
		List<Integer> cmd_parameter = new ArrayList<Integer>();
		cmd_parameter.add(1);
		cmd_parameter.add(2);
		client2serCommand.setCmd_parameter(cmd_parameter);
		try {
			String json = mapper.writeValueAsString(client2serCommand);
			template.convertAndSend("topic.cli2serv", "cli2serv.traincontrol.command", json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("0000000000000000000000000000000000000000000");*/
	}
	
/*		@Test
	public void contextLoads() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/hello").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(equalTo("Hello World")));
		//stringRedisTemplate.opsForValue().set("aaa", "111");
			AtsAutoTrigger auto = new AtsAutoTrigger();
			auto.setId(123);
			auto.setName("zhangsan");
		//redisService.set("bbb", auto);
		//Assert.assertEquals("222", redisService.get("bbb"));
		//redisService.set("bbb", "111");
		//Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
		AtsAutoTrigger dd = (AtsAutoTrigger) redisService.get("bbb");
		logger.info(".....debug.................."+dd.getId()+"  "+dd.getName());

	}*/

}

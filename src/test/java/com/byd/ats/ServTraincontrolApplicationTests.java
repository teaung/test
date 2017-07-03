package com.byd.ats;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.byd.ats.entity.AtsAutoTrigger;
import com.byd.ats.util.RedisService;
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
	@Autowired
	private RedisService redisService;
	private Logger logger = Logger.getLogger(getClass());
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
		//System.out.println("0000000000000000000000000000000000000000000");
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

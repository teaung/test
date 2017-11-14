package com.byd.ats.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.byd.ats.entity.ATSAlarmEvent;
import com.byd.ats.entity.CiMode;
import com.byd.ats.service.CiModeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/CiMode")
public class CiModeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CiModeService ciModeService;
	private CiMode cimode = null;
	@Autowired
	private RabbitTemplate template;
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public String getCiModeInfo()
	{
		List<CiMode> listmode = ciModeService.findAll();
		String json = null;
		if(listmode.size()>0)
		{
			cimode = listmode.get(0);
			json = "{\"control_mode_status\":"+cimode.getCi_mode()+"}";
			logger.debug("senderPlatformStateToClient:"+json);
		}
		return json;
		
	}
	
	@RequestMapping(value="/ciInterrupt", method=RequestMethod.GET)
	public void ciInterrupt() throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ATSAlarmEvent ATSAlarmEvent = new ATSAlarmEvent();
		ATSAlarmEvent.setAlarmCode(6200);
		ATSAlarmEvent.setDeviceId((long) 0x04);
		ATSAlarmEvent.setDeviceInfo("CI连接中断");
		ATSAlarmEvent.setLocation("控制单元服务器");
		String in = mapper.writeValueAsString(ATSAlarmEvent);
		logger.info("--in-"+in);
		template.convertAndSend("topic.cu2ats", "cu2ats.cu.alert", in);
	}
}

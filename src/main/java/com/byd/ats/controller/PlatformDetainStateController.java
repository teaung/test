package com.byd.ats.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.byd.ats.entity.PlatformDetainState;
import com.byd.ats.service.PlatformDetainStateService;

@RestController
@RequestMapping("/PlatformDetainState")
public class PlatformDetainStateController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PlatformDetainStateService platformDetainStateService;
	private PlatformDetainState platformDetainState = null;
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public String senderPlatformStateToClient()
	{
		//System.out.println("PlatformDetainStateController.....");
		platformDetainState = platformDetainStateService.findByKey("PlatformState");
		String ojson = null;
		if(platformDetainState != null)
		{
			ojson = "{\"PlatformState\":"+platformDetainState.getValue1()+"}";
			platformDetainState = null;
			logger.debug("senderPlatformStateToClient:"+ojson);
		}
		return ojson;
		
	}
}

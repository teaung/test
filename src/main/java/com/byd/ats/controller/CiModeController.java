package com.byd.ats.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.byd.ats.entity.CiMode;
import com.byd.ats.service.CiModeService;

@RestController
@RequestMapping("/CiMode")
public class CiModeController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CiModeService ciModeService;
	private CiMode cimode = null;
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public String getCiModeInfo()
	{
		List<CiMode> listmode = ciModeService.findAll();
		String json = null;
		if(listmode.size()>0)
		{
			cimode = listmode.get(0);
			json = "{\"control_mode_status\":"+cimode.getCi_mode()+"}";
			logger.info("senderPlatformStateToClient:"+json);
		}
		return json;
		
	}
}

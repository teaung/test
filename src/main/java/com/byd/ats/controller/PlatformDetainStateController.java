package com.byd.ats.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.byd.ats.entity.PlatformDetainState;
import com.byd.ats.entity.PlatformState;
import com.byd.ats.entity.SkipStationState;
import com.byd.ats.service.PlatformDetainStateService;
import com.byd.ats.service.SkipStationStateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/PlatformDetainState")
public class PlatformDetainStateController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SkipStationStateService skipStationStateService;
	private PlatformDetainState platformDetainState = null;
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public String senderPlatformStateToClient() throws JsonProcessingException
	{
		//System.out.println("PlatformDetainStateController.....");
		ObjectMapper mapper = new ObjectMapper();
		List<SkipStationState> skipStationStateList = skipStationStateService.findAll();
		List<PlatformState> platformStateList = new ArrayList<PlatformState>();
		String ojson = null;
		if(skipStationStateList != null && skipStationStateList.size()>0)
		{
			for(SkipStationState skipStationState:skipStationStateList){
				PlatformState platformState = new PlatformState();
				platformState.setId(skipStationState.getPlatformId());
				platformState.setClientnum(skipStationState.getClientnum());
				platformState.setUsername(skipStationState.getUsername());
				platformState.setState(skipStationState.getDetainStatus());
				platformState.setWorkstation(skipStationState.getWorkstation());
				platformStateList.add(platformState);
			}
			ojson = "{\"PlatformState\":"+mapper.writeValueAsString(platformStateList)+"}";
			logger.debug("senderPlatformStateToClient:"+ojson);
		}
		return ojson;
		
	}
}

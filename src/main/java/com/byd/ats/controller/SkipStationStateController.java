package com.byd.ats.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.ats.entity.PlatformState;
import com.byd.ats.entity.SkipStationState;
import com.byd.ats.entity.SkipStationState2Cli;
import com.byd.ats.service.SkipStationStateService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/SkipStationStatus")
public class SkipStationStateController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public ObjectMapper mapper = new ObjectMapper();
	private List<PlatformState> pstateArray = new ArrayList<PlatformState>();
	private PlatformState platformState = null;;
	
	@Autowired
	private SkipStationStateService skipStationStateService;
	
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public String getSkipStationStatus(@RequestParam(value="stationId") Integer stationId)
	{
		SkipStationState skipStationState = skipStationStateService.findByPlatformId(stationId);
		if(skipStationState != null)
		{
			String retrunStr = Short.toString(skipStationState.getSkipState());
			logger.info("getSkipStationStatus: " + retrunStr);
			return retrunStr; 
		}
		return null;
	}
	
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public String getAllSkipStationStatus() throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		List<SkipStationState> listState = skipStationStateService.findAll();
		List<SkipStationState2Cli> skipStationState2CliList = new ArrayList<SkipStationState2Cli>();
		if(listState.size() > 0)
		{
			for(SkipStationState skipStationState:listState){
				SkipStationState2Cli skipStationState2Cli = new SkipStationState2Cli();
				skipStationState2Cli.setId(skipStationState.getPlatformId());
				skipStationState2Cli.setClientnum(skipStationState.getClientnum());
				skipStationState2Cli.setUsername(skipStationState.getUsername());
				skipStationState2Cli.setState(skipStationState.getSkipState());
				skipStationState2CliList.add(skipStationState2Cli);
			}
			return "{\"SkipStationStatus\":"+mapper.writeValueAsString(skipStationState2CliList)+"}";
		}
		return null;
	}
}

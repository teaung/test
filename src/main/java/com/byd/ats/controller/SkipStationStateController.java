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
import com.byd.ats.service.SkipStationStateService;
import com.fasterxml.jackson.core.JsonParseException;
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
	public String getSkipStationStatus(@RequestParam(value="stationId") int stationId)
	{
		List<SkipStationState> listState = skipStationStateService.findAll();
		if(listState.size() > 0)
		{
			try {
				pstateArray = mapper.readValue(listState.get(0).getValue1(), new TypeReference<List<PlatformState>>() {});
				if(pstateArray.size() > 0)
				{
					platformState = pstateArray.get((stationId-1));
					return Integer.toString(platformState.getState());
					
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public String getAllSkipStationStatus()
	{
		List<SkipStationState> listState = skipStationStateService.findAll();
		if(listState.size() > 0)
		{
			return "{\"SkipStationStatus\":"+listState.get(0).getValue1()+"}";
		}
		return null;
	}
}

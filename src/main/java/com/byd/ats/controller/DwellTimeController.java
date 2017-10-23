/*package com.byd.ats.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.byd.ats.entity.AppDataDwellTimeCommand;
import com.byd.ats.entity.BackDwellTime2AppData;
import com.byd.ats.entity.DwellTimeData;
import com.byd.ats.entity.SkipStationState;
import com.byd.ats.service.SkipStationStateService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/DwellTime")
public class DwellTimeController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SkipStationStateService skipStationStateService;
	
	*//**
	 * 设置站台停站时间
	 * @param json 设置命令JSON
	 * @return
	 *//*
	@RequestMapping(value = "/setTime")
//	public @ResponseBody String setDwellTime(@RequestParam String json){
	public @ResponseBody String setDwellTime(@RequestParam Integer platformId){
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		BackDwellTime2AppData backDwellTime2AppData = null;
		AppDataDwellTimeCommand dwellTimeCommand = null;
		
		dwellTimeCommand = new AppDataDwellTimeCommand();
		dwellTimeCommand.setClientNum((short) 11);
		dwellTimeCommand.setUserName("11");
		dwellTimeCommand.setPlatformId(platformId);
		dwellTimeCommand.setTime(60);
		dwellTimeCommand.setSetWay(0);
		dwellTimeCommand.setRuntaskCmdType((short) 114);
		dwellTimeCommand.setSkipStationCommand((short) 0);
		String json = null;
		try {
			json = mapper.writeValueAsString(dwellTimeCommand);
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//反序列化
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		logger.info("--receive--" + json);
		try {
			dwellTimeCommand = mapper.readValue(json, AppDataDwellTimeCommand.class);
		} catch (Exception e) {
			// TODO: handle exception
			backDwellTime2AppData = new BackDwellTime2AppData(dwellTimeCommand.getRuntaskCmdType(), false, "设置失败，消息格式有误", 0, 0, 0);
			result = "{\"tgi_msg\":" + backDwellTime2AppData.toString() + "}";
			logger.info("[setDwellTime]--sender--" + result);
			return result;
		}
		// ----------------更新数据库停站时间，并更新------------------
		try {
			SkipStationState skipStationState = skipStationStateService.findByPlatformId(dwellTimeCommand.getPlatformId());
			if (skipStationState == null) {
				skipStationState = new SkipStationState();
				skipStationState.setClientnum(dwellTimeCommand.getClientNum());
				skipStationState.setUsername(dwellTimeCommand.getUserName());
				skipStationState.setPlatformId(dwellTimeCommand.getPlatformId());
			}
			skipStationState.setDwelltime(dwellTimeCommand.getTime());
			skipStationState.setSetWay(dwellTimeCommand.getSetWay());
			skipStationStateService.save(skipStationState);
			logger.info("update dwellTime exce ok .... ");
			backDwellTime2AppData = new BackDwellTime2AppData(dwellTimeCommand.getRuntaskCmdType(), true, "设置成功",
					dwellTimeCommand.getPlatformId(), dwellTimeCommand.getTime(), dwellTimeCommand.getSetWay());
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("[setDwellTime] save error.");
			backDwellTime2AppData.setResult(false);
			backDwellTime2AppData.setCode("设置失败");
		}
		// --------------------返回结果给客户端----------------------------
		result = "{\"tgi_msg\":" + backDwellTime2AppData.toString() + "}";
	
		logger.info("[setDwellTime]--sender--" + result);
		return result;
	}

	*//**
	 * 获取所有站台停站时间
	 * @return
	 *//*
	@RequestMapping(value = "/all")
	public @ResponseBody String allDwellTime() throws JsonParseException, JsonMappingException, IOException{
		//LOG.info("---[R]--getRuntaskAllCommand--");
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		List<DwellTimeData> dwellTimeDataList = new ArrayList<DwellTimeData>();
		List<SkipStationState> stateList = skipStationStateService.findAll();
		for(SkipStationState state:stateList){
			DwellTimeData dwellTimeData = new DwellTimeData();
			dwellTimeData.setPlatformId(state.getPlatformId());
			dwellTimeData.setTime(state.getDwelltime());
			dwellTimeData.setSetWay(state.getSetWay());
			dwellTimeDataList.add(dwellTimeData);
		}	
		result = "{\"ats_station_stop_time\":"+mapper.writeValueAsString(dwellTimeDataList)+"}";
		
		//LOG.info("---[S]--getRuntaskAllCommand--"+result);
		return result;
	}
	
	*//**
	 * 获取站台停站时间
	 * @param platformId 站台ID
	 * @return
	 *//*
	@RequestMapping(value="/info", method=RequestMethod.GET)
	public String getDwellTime(@RequestParam(value="platformId") Integer platformId)
	{
		SkipStationState state = skipStationStateService.findByPlatformId(platformId);
		if(state != null)
		{
			DwellTimeData dwellTimeData = new DwellTimeData();
			dwellTimeData.setPlatformId(state.getPlatformId());
			dwellTimeData.setTime(state.getDwelltime());
			dwellTimeData.setSetWay(state.getSetWay());
			logger.info("getDwellTime: " + dwellTimeData.toString());
			return dwellTimeData.toString(); 
		}
		return null;
	}
}
*/
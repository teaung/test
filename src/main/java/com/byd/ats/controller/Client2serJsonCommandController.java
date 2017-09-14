package com.byd.ats.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.ats.entity.AtsModeSwitch;
import com.byd.ats.entity.CLient2serJsonCommand2report;
import com.byd.ats.entity.CLient2serJsonCommandHistory;
import com.byd.ats.entity.Client2serCommand;
import com.byd.ats.entity.Client2serPwdCommand;
import com.byd.ats.service.Client2serJsonCommandHistoryRepository;
import com.byd.ats.util.CmdEnum;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/ClientCommand")
public class Client2serJsonCommandController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Client2serJsonCommandHistoryRepository clientCommandHistory;
	
	/**
	 * [报表] 获取客户端下发的命令信息
	 */
	@RequestMapping(value="/cmdInfo", method=RequestMethod.GET)
	public String getClientCommand(@RequestParam String startTime, @RequestParam String endTime) throws JsonParseException, JsonMappingException, IOException
	{
		logger.info("receive [getClientCommand] startTime:"+startTime+" endTime:"+endTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("parse string to date error!");
		}
		
		String returnStr = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		List<CLient2serJsonCommand2report> CLientcmd2report = new ArrayList<CLient2serJsonCommand2report>();
		List<CLient2serJsonCommandHistory> clientCmdHistory = clientCommandHistory.findByRClientTimeBetween(startDate, endDate);
		if(clientCmdHistory != null && clientCmdHistory.size() != 0){
			for(CLient2serJsonCommandHistory cmd:clientCmdHistory){
				String json = cmd.getJson();
				Map<String,Object> tempmap = mapper.readValue(json, Map.class);
				CLient2serJsonCommand2report cmd2report = new CLient2serJsonCommand2report();
				if(tempmap.get("cmd_class").toString().equals("ci")){
					Client2serCommand Client2serCommand = mapper.readValue(json, Client2serCommand.class);
					BeanUtils.copyProperties(Client2serCommand, cmd2report);
				}
				
				if(tempmap.get("cmd_class").toString().equals("password")){
					Client2serPwdCommand pwdcmd = mapper.readValue(json, Client2serPwdCommand.class);
					BeanUtils.copyProperties(pwdcmd, cmd2report);
				}
				
				if(tempmap.get("cmd_class").toString().equals("atsmode")){
					AtsModeSwitch mode = mapper.readValue(json, AtsModeSwitch.class);
					BeanUtils.copyProperties(mode, cmd2report);
				}
				
				if(tempmap.get("cmd_class").toString().equals("aod")){
					Client2serCommand aodcmd = mapper.readValue(json, Client2serCommand.class);
					BeanUtils.copyProperties(aodcmd, cmd2report);
				}
				cmd2report.setrClientTime(cmd.getrClientTime());
				cmd2report.setInstruction(CmdEnum.getByCode(cmd.getCmd()).getMsg());//命令说明
				CLientcmd2report.add(cmd2report);
			}
			returnStr = mapper.writeValueAsString(CLientcmd2report);
		}
		
		logger.info("send [getClientCommand]"+returnStr);
		return returnStr;
		
	}
}

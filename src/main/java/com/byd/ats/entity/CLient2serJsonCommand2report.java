package com.byd.ats.entity;

import java.util.Date;
import java.util.List;

public class CLient2serJsonCommand2report {

	private  String cmd_class;//命令处理者
	
	private int stationcontrol_cmd_type;//命令类型
	
	private String instruction;//说明
	
	private List<Integer> cmd_parameter;//命令参数
	
	private Date rClientTime;//发送时间
	
	private String user_name;//发送者
	
	private int client_num;//客户端编号

	public String getCmd_class() {
		return cmd_class;
	}

	public void setCmd_class(String cmd_class) {
		this.cmd_class = cmd_class;
	}

	public int getStationcontrol_cmd_type() {
		return stationcontrol_cmd_type;
	}

	public void setStationcontrol_cmd_type(int stationcontrol_cmd_type) {
		this.stationcontrol_cmd_type = stationcontrol_cmd_type;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public List<Integer> getCmd_parameter() {
		return cmd_parameter;
	}

	public void setCmd_parameter(List<Integer> cmd_parameter) {
		this.cmd_parameter = cmd_parameter;
	}

	public Date getrClientTime() {
		return rClientTime;
	}

	public void setrClientTime(Date rClientTime) {
		this.rClientTime = rClientTime;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getClient_num() {
		return client_num;
	}

	public void setClient_num(int client_num) {
		this.client_num = client_num;
	}
	
}

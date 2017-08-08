package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ret2ClientResult {

	@JsonProperty
	private int client_num;
	@JsonProperty
	private String user_name;
	@JsonProperty
	private int stationcontrol_cmd_type;
	@JsonProperty
	private int resoult;
	@JsonProperty
	private int cmd_parameter;//反馈参数，即相关ID
	@JsonProperty
	private int countdownTime;//倒计时秒数
	
	public int getClient_num() {
		return client_num;
	}
	public void setClient_num(int client_num) {
		this.client_num = client_num;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getStationcontrol_cmd_type() {
		return stationcontrol_cmd_type;
	}
	public void setStationcontrol_cmd_type(int stationcontrol_cmd_type) {
		this.stationcontrol_cmd_type = stationcontrol_cmd_type;
	}

	public int getResoult() {
		return resoult;
	}
	public void setResoult(int resoult) {
		this.resoult = resoult;
	}
	public int getCmd_parameter() {
		return cmd_parameter;
	}
	public void setCmd_parameter(int cmd_parameter) {
		this.cmd_parameter = cmd_parameter;
	}
	public int getCountdownTime() {
		return countdownTime;
	}
	public void setCountdownTime(int countdownTime) {
		this.countdownTime = countdownTime;
	}

	

		
}

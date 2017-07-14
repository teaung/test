package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client2serPwdCommand {

	@JsonProperty
	private int client_num;
	@JsonProperty
	private String user_name;
	@JsonProperty
	private String cmd_class;
	@JsonProperty
	private int stationcontrol_cmd_type;
	@JsonProperty
	private int for_cmd;
	@JsonProperty
	private String password;
	@JsonIgnore
	public int getClient_num() {
		return client_num;
	}
	@JsonIgnore
	public void setClient_num(int client_num) {
		this.client_num = client_num;
	}
	@JsonIgnore
	public String getUser_name() {
		return user_name;
	}
	@JsonIgnore
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	@JsonIgnore
	public String getCmd_class() {
		return cmd_class;
	}
	@JsonIgnore
	public void setCmd_class(String cmd_class) {
		this.cmd_class = cmd_class;
	}
	@JsonIgnore
	public int getStationcontrol_cmd_type() {
		return stationcontrol_cmd_type;
	}
	@JsonIgnore
	public void setStationcontrol_cmd_type(int stationcontrol_cmd_type) {
		this.stationcontrol_cmd_type = stationcontrol_cmd_type;
	}
	@JsonIgnore
	public int getFor_cmd() {
		return for_cmd;
	}
	@JsonIgnore
	public void setFor_cmd(int for_cmd) {
		this.for_cmd = for_cmd;
	}
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonIgnore
	public void setPassword(String password) {
		this.password = password;
	}

	
	
}

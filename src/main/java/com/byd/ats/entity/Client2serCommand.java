package com.byd.ats.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client2serCommand {

	@JsonProperty
	private String cmd_class;
	@JsonProperty
	private int stationcontrol_cmd_type;
	@JsonProperty
	//private int[] cmd_parameter;
	private List<Integer> cmd_parameter;
	@JsonProperty
	private int client_num;
	@JsonProperty
	private String user_name;
	
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
/*	@JsonIgnore
	public int[] getCmd_parameter() {
		return cmd_parameter;
	}
	@JsonIgnore
	public void setCmd_parameter(int[] cmd_parameter) {
		this.cmd_parameter = cmd_parameter;
	}*/
	
	@JsonIgnore
	public int getClient_num() {
		return client_num;
	}
	@JsonIgnore
	public List<Integer> getCmd_parameter() {
		return cmd_parameter;
	}
	@JsonIgnore
	public void setCmd_parameter(List<Integer> cmd_parameter) {
		this.cmd_parameter = cmd_parameter;
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
	
}

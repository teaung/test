package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StationControl {

	@JsonProperty
	private int client_num;
	@JsonProperty
	private String user_name;
	@JsonProperty
	private int stationcontrol_cmd_type;
	@JsonProperty
	private String cmd_class;
	@JsonProperty
	private int ci_num;
	@JsonProperty
	private int current_mode;
	@JsonProperty
	private int modified_mode;
	@JsonIgnore
	private int way;
	
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
	public int getStationcontrol_cmd_type() {
		return stationcontrol_cmd_type;
	}
	@JsonIgnore
	public void setStationcontrol_cmd_type(int stationcontrol_cmd_type) {
		this.stationcontrol_cmd_type = stationcontrol_cmd_type;
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
	public int getCi_num() {
		return ci_num;
	}
	@JsonIgnore
	public void setCi_num(int ci_num) {
		this.ci_num = ci_num;
	}
	@JsonIgnore
	public int getCurrent_mode() {
		return current_mode;
	}
	@JsonIgnore
	public void setCurrent_mode(int current_mode) {
		this.current_mode = current_mode;
	}
	@JsonIgnore
	public int getModified_mode() {
		return modified_mode;
	}
	@JsonIgnore
	public void setModified_mode(int modified_mode) {
		this.modified_mode = modified_mode;
	}
	@JsonIgnore
	public int getWay() {
		return way;
	}
	@JsonIgnore
	public void setWay(int way) {
		this.way = way;
	}

}

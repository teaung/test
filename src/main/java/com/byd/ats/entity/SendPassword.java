package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SendPassword {

	@JsonProperty
	private int client_num;
	@JsonProperty
	private String user_name;
	@JsonProperty
	private int traincontrol_cmd_type;
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
	public int getTraincontrol_cmd_type() {
		return traincontrol_cmd_type;
	}
	@JsonIgnore
	public void setTraincontrol_cmd_type(int traincontrol_cmd_type) {
		this.traincontrol_cmd_type = traincontrol_cmd_type;
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

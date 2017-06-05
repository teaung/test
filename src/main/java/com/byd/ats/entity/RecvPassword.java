package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RecvPassword {

	@JsonProperty
	private int client_num;
	@JsonProperty
	private String user_name;
	@JsonProperty
	private int traincontrol_cmd_type;
	@JsonProperty
	private int for_cmd;
	@JsonProperty
	private short resoult;
	@JsonProperty
	private short fail_code;
	
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
	public short getResoult() {
		return resoult;
	}
	@JsonIgnore
	public void setResoult(short resoult) {
		this.resoult = resoult;
	}
	@JsonIgnore
	public short getFail_code() {
		return fail_code;
	}
	@JsonIgnore
	public void setFail_code(short fail_code) {
		this.fail_code = fail_code;
	}
	
	
}

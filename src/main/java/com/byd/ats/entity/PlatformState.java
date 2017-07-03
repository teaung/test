package com.byd.ats.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlatformState implements Serializable{

	@JsonProperty
	private int id;
	@JsonProperty
	private String username;
	@JsonProperty
	private int clientnum;
	@JsonProperty
	private int state;
	
	@JsonIgnore
	public int getId() {
		return id;
	}
	@JsonIgnore
	public void setId(int id) {
		this.id = id;
	}
	@JsonIgnore
	public String getUsername() {
		return username;
	}
	@JsonIgnore
	public void setUsername(String username) {
		this.username = username;
	}
	@JsonIgnore
	public int getClientnum() {
		return clientnum;
	}
	@JsonIgnore
	public void setClientnum(int clientnum) {
		this.clientnum = clientnum;
	}
	public int getState() {
		return state;
	}
	@JsonIgnore
	public void setState(int state) {
		this.state = state;
	}
	
	
}

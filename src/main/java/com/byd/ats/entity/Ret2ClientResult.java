package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ret2ClientResult {

	@JsonProperty
	private int ClIENT_NUM;
	@JsonProperty
	private String USER_NAME;
	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private String RESOULT;
	@JsonProperty
	private int CODE;
	@JsonProperty
	private String DATA;
	@JsonIgnore
	public int getClIENT_NUM() {
		return ClIENT_NUM;
	}
	@JsonIgnore
	public void setClIENT_NUM(int clIENT_NUM) {
		ClIENT_NUM = clIENT_NUM;
	}
	@JsonIgnore
	public String getUSER_NAME() {
		return USER_NAME;
	}
	@JsonIgnore
	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}
	@JsonIgnore
	public int getCMD_TYPE() {
		return CMD_TYPE;
	}
	@JsonIgnore
	public void setCMD_TYPE(int cMD_TYPE) {
		CMD_TYPE = cMD_TYPE;
	}
	@JsonIgnore
	public String getRESOULT() {
		return RESOULT;
	}
	@JsonIgnore
	public void setRESOULT(String rESOULT) {
		RESOULT = rESOULT;
	}
	@JsonIgnore
	public int getCODE() {
		return CODE;
	}
	@JsonIgnore
	public void setCODE(int cODE) {
		CODE = cODE;
	}
	@JsonIgnore
	public String getDATA() {
		return DATA;
	}
	@JsonIgnore
	public void setDATA(String dATA) {
		DATA = dATA;
	}
		
}

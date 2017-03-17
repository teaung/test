package com.byd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TsrRetrunCode {

	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private String RESOULT;
	@JsonProperty
	private String CODE;
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
	public String getCODE() {
		return CODE;
	}
	@JsonIgnore
	public void setCODE(String cODE) {
		CODE = cODE;
	}
	
}

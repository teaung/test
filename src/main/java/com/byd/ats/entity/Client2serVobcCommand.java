package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client2serVobcCommand {

	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private int CMD_PARAMETER;
	@JsonProperty
	private String CMD_CLASS;
	@JsonIgnore
	public int getCMD_TYPE() {
		return CMD_TYPE;
	}
	@JsonIgnore
	public void setCMD_TYPE(int cMD_TYPE) {
		CMD_TYPE = cMD_TYPE;
	}
	@JsonIgnore
	public int getCMD_PARAMETER() {
		return CMD_PARAMETER;
	}
	@JsonIgnore
	public void setCMD_PARAMETER(int cMD_PARAMETER) {
		CMD_PARAMETER = cMD_PARAMETER;
	}
	@JsonIgnore
	public String getCMD_CLASS() {
		return CMD_CLASS;
	}
	@JsonIgnore
	public void setCMD_CLASS(String cMD_CLASS) {
		CMD_CLASS = cMD_CLASS;
	}
	
	
}

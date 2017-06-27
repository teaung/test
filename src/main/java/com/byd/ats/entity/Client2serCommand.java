package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client2serCommand {

	@JsonProperty
	private String CMD_CLASS;
	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private int[] CMD_PARAMETER;
	@JsonProperty
	private int CLIENT_NUM;
	@JsonProperty
	private String USER_NAME;
	
	@JsonIgnore
	public String getCMD_CLASS() {
		return CMD_CLASS;
	}
	@JsonIgnore
	public void setCMD_CLASS(String cMD_CLASS) {
		CMD_CLASS = cMD_CLASS;
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
	public int[] getCMD_PARAMETER() {
		return CMD_PARAMETER;
	}
	@JsonIgnore
	public void setCMD_PARAMETER(int[] cMD_PARAMETER) {
		CMD_PARAMETER = cMD_PARAMETER;
	}

	public int getCLIENT_NUM() {
		return CLIENT_NUM;
	}
	public void setCLIENT_NUM(int cLIENT_NUM) {
		CLIENT_NUM = cLIENT_NUM;
	}
	public String getUSER_NAME() {
		return USER_NAME;
	}
	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}


}

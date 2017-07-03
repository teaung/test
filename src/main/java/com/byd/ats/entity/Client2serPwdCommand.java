package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client2serPwdCommand {

	@JsonProperty
	private int CLIENT_NUM;
	@JsonProperty
	private String USER_NAME;
	@JsonProperty
	private String CMD_CLASS;
	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private int FOR_CMD;
	@JsonProperty
	private String PASSWORD;

	@JsonIgnore
	public int getCLIENT_NUM() {
		return CLIENT_NUM;
	}
	@JsonIgnore
	public void setCLIENT_NUM(int cLIENT_NUM) {
		CLIENT_NUM = cLIENT_NUM;
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
	public int getFOR_CMD() {
		return FOR_CMD;
	}
	@JsonIgnore
	public void setFOR_CMD(int fOR_CMD) {
		FOR_CMD = fOR_CMD;
	}
	@JsonIgnore
	public String getPASSWORD() {
		return PASSWORD;
	}
	@JsonIgnore
	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}
	
	
}

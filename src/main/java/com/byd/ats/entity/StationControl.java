package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StationControl {

	@JsonProperty
	private int ClIENT_NUM;
	@JsonProperty
	private String USER_NAME;
	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private String CMD_CLASS;
	@JsonProperty
	private int CI_NUM;
	@JsonProperty
	private String CURRENT_MODE;
	@JsonProperty
	private String MODIFIED_MODE;
	
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
	public String getCMD_CLASS() {
		return CMD_CLASS;
	}
	@JsonIgnore
	public void setCMD_CLASS(String cMD_CLASS) {
		CMD_CLASS = cMD_CLASS;
	}
	@JsonIgnore
	public int getCI_NUM() {
		return CI_NUM;
	}
	@JsonIgnore
	public void setCI_NUM(int cI_NUM) {
		CI_NUM = cI_NUM;
	}
	@JsonIgnore
	public String getCURRENT_MODE() {
		return CURRENT_MODE;
	}
	@JsonIgnore
	public void setCURRENT_MODE(String cURRENT_MODE) {
		CURRENT_MODE = cURRENT_MODE;
	}
	@JsonIgnore
	public String getMODIFIED_MODE() {
		return MODIFIED_MODE;
	}
	@JsonIgnore
	public void setMODIFIED_MODE(String mODIFIED_MODE) {
		MODIFIED_MODE = mODIFIED_MODE;
	}
	
	
}

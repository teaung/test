package com.byd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client2serZcCommand {

	@JsonProperty
	private String CMD_CLASS;
	@JsonProperty
	private int CMD_TYPE;
	@JsonProperty
	private int TSR_VALUE;
	@JsonProperty
	private int TSR_NUM;
/*	@JsonProperty
	private String TSR_TRACKLIST;*/
	@JsonProperty
	private int[] TSR_TRACKLIST;
	
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
	public int getTSR_VALUE() {
		return TSR_VALUE;
	}
	@JsonIgnore
	public void setTSR_VALUE(int tSR_VALUE) {
		TSR_VALUE = tSR_VALUE;
	}
	@JsonIgnore
	public int getTSR_NUM() {
		return TSR_NUM;
	}
	@JsonIgnore
	public void setTSR_NUM(int tSR_NUM) {
		TSR_NUM = tSR_NUM;
	}
	public int[] getTSR_TRACKLIST() {
		return TSR_TRACKLIST;
	}
	public void setTSR_TRACKLIST(int[] tSR_TRACKLIST) {
		TSR_TRACKLIST = tSR_TRACKLIST;
	}

	
}

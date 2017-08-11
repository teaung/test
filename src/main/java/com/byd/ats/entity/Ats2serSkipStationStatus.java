package com.byd.ats.entity;

import java.util.List;

public class Ats2serSkipStationStatus {

	private String cmd_class;
	private int stationcontrol_cmd_type;
	private String description;
	private List<PlatformState> platformState;
	
	public String getCmd_class() {
		return cmd_class;
	}
	public void setCmd_class(String cmd_class) {
		this.cmd_class = cmd_class;
	}
	public int getStationcontrol_cmd_type() {
		return stationcontrol_cmd_type;
	}
	public void setStationcontrol_cmd_type(int stationcontrol_cmd_type) {
		this.stationcontrol_cmd_type = stationcontrol_cmd_type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<PlatformState> getPlatformState() {
		return platformState;
	}
	public void setPlatformState(List<PlatformState> platformState) {
		this.platformState = platformState;
	}
	
	
}

package com.byd.ats.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "tb_atsmode_switch")
public class AtsModeSwitch {

	//@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	//private long id;
	private int client_num;
	private String user_name;
	private int stationcontrol_cmd_type;
	private String cmd_class;
	private int ci_num;
	private int current_mode;
	private int modified_mode;
	private int way;
	private int src_client_num;
	
/*	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}*/
	public int getClient_num() {
		return client_num;
	}
	public void setClient_num(int client_num) {
		this.client_num = client_num;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public int getStationcontrol_cmd_type() {
		return stationcontrol_cmd_type;
	}
	public void setStationcontrol_cmd_type(int stationcontrol_cmd_type) {
		this.stationcontrol_cmd_type = stationcontrol_cmd_type;
	}
	public String getCmd_class() {
		return cmd_class;
	}
	public void setCmd_class(String cmd_class) {
		this.cmd_class = cmd_class;
	}
	public int getCi_num() {
		return ci_num;
	}
	public void setCi_num(int ci_num) {
		this.ci_num = ci_num;
	}
	public int getCurrent_mode() {
		return current_mode;
	}
	public void setCurrent_mode(int current_mode) {
		this.current_mode = current_mode;
	}
	public int getModified_mode() {
		return modified_mode;
	}
	public void setModified_mode(int modified_mode) {
		this.modified_mode = modified_mode;
	}
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	public int getSrc_client_num() {
		return src_client_num;
	}
	public void setSrc_client_num(int src_client_num) {
		this.src_client_num = src_client_num;
	}
	
	
}

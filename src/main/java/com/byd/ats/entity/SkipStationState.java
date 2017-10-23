package com.byd.ats.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tb_skip_station_state")
public class SkipStationState {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String key1;
	@Column(name = "value1",length =2048)
	private String value1;
	
	
	public String getKey1() {
		return key1;
	}
	public void setKey1(String key1) {
		this.key1 = key1;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	
	
	private String username;
	
	private int clientnum;
	
	private int platformId;//站台ID
	
	private short skipState;//跳停状态
	
	private short detainStatus;//扣车状态
	
	private int workstation;
	
	
	/*private int dwelltime;		//停站时间
	
	private int setWay;			//设置方式（0，人工设置；1，自动设置）	//停站时间时  cmdParameter:[platform_id,time,setWay] //站台ID，停站时间，设置方式（0，人工设置；1，自动设置）
								//立即发车时  cmdParameter:[platform_id,group_mun]  //站台ID，当前站台停车的车组号

	public int getDwelltime() {
		return dwelltime;
	}
	public void setDwelltime(int dwelltime) {
		this.dwelltime = dwelltime;
	}
	public int getSetWay() {
		return setWay;
	}
	public void setSetWay(int setWay) {
		this.setWay = setWay;
	}*/
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getClientnum() {
		return clientnum;
	}
	public void setClientnum(int clientnum) {
		this.clientnum = clientnum;
	}
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	public short getSkipState() {
		return skipState;
	}
	public void setSkipState(short skipState) {
		this.skipState = skipState;
	}
	public short getDetainStatus() {
		return detainStatus;
	}
	public void setDetainStatus(short detainStatus) {
		this.detainStatus = detainStatus;
	}
	public int getWorkstation() {
		return workstation;
	}
	public void setWorkstation(int workstation) {
		this.workstation = workstation;
	}
	
}

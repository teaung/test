package com.byd.ats.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_skip_station_state")
public class SkipStationState {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;

	private String username;
	
	private int clientnum;
	
	private int platformId;//站台ID
	
	private short skipState;//跳停状态
	
	private short detainStatus;//扣车状态
	
	private int workstation;
	
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

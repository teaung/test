package com.byd.entity;

public class CimsgStatus {

	private byte[] s_status = new byte[AtsPub.SIGNAL_NUM];
	private byte[] sw_status = new byte[AtsPub.SWITCH_NUM];
	private byte[] t_status = new byte[AtsPub.PHY_TRACK_NUM];
	private byte[] lt_status = new byte[AtsPub.LOGIC_TRACK_NUM];
	private byte[] r_status = new byte[AtsPub.ROUTE_NUM];
	private byte[] autopass = new byte[AtsPub.AUTOPASS_NUM];
	private byte[] d_status = new byte[AtsPub.DOOR_NUM];
	private byte[] esp_status = new byte[AtsPub.ESP_NUM];
	private byte[] keep_train = new byte[AtsPub.KEEP_TRAIN_NUM];
	private byte[] autoback_status = new byte[AtsPub.AUTOBACK_NUM];
	private byte[] autoback_fully_status = new byte[AtsPub.AUTOBACK_FULLY_NUM];
	private byte[] spks_status = new byte[AtsPub.SPKS_NUM];
	private byte[] autotrig_status = new byte[AtsPub.AUTOTRIG_NUM];
	public byte[] getS_status() {
		return s_status;
	}
	public void setS_status(byte[] s_status) {
		this.s_status = s_status;
	}
	public byte[] getSw_status() {
		return sw_status;
	}
	public void setSw_status(byte[] sw_status) {
		this.sw_status = sw_status;
	}
	public byte[] getT_status() {
		return t_status;
	}
	public void setT_status(byte[] t_status) {
		this.t_status = t_status;
	}
	public byte[] getLt_status() {
		return lt_status;
	}
	public void setLt_status(byte[] lt_status) {
		this.lt_status = lt_status;
	}
	public byte[] getR_status() {
		return r_status;
	}
	public void setR_status(byte[] r_status) {
		this.r_status = r_status;
	}
	public byte[] getAutopass() {
		return autopass;
	}
	public void setAutopass(byte[] autopass) {
		this.autopass = autopass;
	}
	public byte[] getD_status() {
		return d_status;
	}
	public void setD_status(byte[] d_status) {
		this.d_status = d_status;
	}
	public byte[] getEsp_status() {
		return esp_status;
	}
	public void setEsp_status(byte[] esp_status) {
		this.esp_status = esp_status;
	}
	public byte[] getKeep_train() {
		return keep_train;
	}
	public void setKeep_train(byte[] keep_train) {
		this.keep_train = keep_train;
	}
	public byte[] getAutoback_status() {
		return autoback_status;
	}
	public void setAutoback_status(byte[] autoback_status) {
		this.autoback_status = autoback_status;
	}
	public byte[] getAutoback_fully_status() {
		return autoback_fully_status;
	}
	public void setAutoback_fully_status(byte[] autoback_fully_status) {
		this.autoback_fully_status = autoback_fully_status;
	}
	public byte[] getSpks_status() {
		return spks_status;
	}
	public void setSpks_status(byte[] spks_status) {
		this.spks_status = spks_status;
	}
	public byte[] getAutotrig_status() {
		return autotrig_status;
	}
	public void setAutotrig_status(byte[] autotrig_status) {
		this.autotrig_status = autotrig_status;
	}
	
}

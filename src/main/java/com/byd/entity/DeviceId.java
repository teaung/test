package com.byd.entity;
import com.byd.entity.AtsPub;
public class DeviceId {

	private short[] s_id = new short[AtsPub.SIGNAL_NUM];
	private short[] sw_id= new short[AtsPub.SWITCH_NUM];
	private short[] t_id= new short[AtsPub.PHY_TRACK_NUM];
	private short[] lt_id= new short[AtsPub.LOGIC_TRACK_NUM];
	private short[] r_id= new short[AtsPub.ROUTE_NUM];
	private short[] autopass_id= new short[AtsPub.AUTOPASS_NUM];
	private short[] d_id= new short[AtsPub.DOOR_NUM];
	private short[] esp_id= new short[AtsPub.ESP_NUM];
	private short[] keep_train_id= new short[AtsPub.KEEP_TRAIN_NUM];
	private short[] autoback_id= new short[AtsPub.AUTOBACK_NUM];
	private short[] autoback_fully_id= new short[AtsPub.AUTOBACK_FULLY_NUM];
	private short[] spks_id= new short[AtsPub.SPKS_NUM];
	private short[] autotrig_id= new short[AtsPub.AUTOTRIG_NUM];
	public short[] getS_id() {
		return s_id;
	}
	public void setS_id(short[] s_id) {
		this.s_id = s_id;
	}
	public short[] getSw_id() {
		return sw_id;
	}
	public void setSw_id(short[] sw_id) {
		this.sw_id = sw_id;
	}
	public short[] getT_id() {
		return t_id;
	}
	public void setT_id(short[] t_id) {
		this.t_id = t_id;
	}
	public short[] getLt_id() {
		return lt_id;
	}
	public void setLt_id(short[] lt_id) {
		this.lt_id = lt_id;
	}
	public short[] getR_id() {
		return r_id;
	}
	public void setR_id(short[] r_id) {
		this.r_id = r_id;
	}
	public short[] getAutopass_id() {
		return autopass_id;
	}
	public void setAutopass_id(short[] autopass_id) {
		this.autopass_id = autopass_id;
	}
	public short[] getD_id() {
		return d_id;
	}
	public void setD_id(short[] d_id) {
		this.d_id = d_id;
	}
	public short[] getEsp_id() {
		return esp_id;
	}
	public void setEsp_id(short[] esp_id) {
		this.esp_id = esp_id;
	}
	public short[] getKeep_train_id() {
		return keep_train_id;
	}
	public void setKeep_train_id(short[] keep_train_id) {
		this.keep_train_id = keep_train_id;
	}
	public short[] getAutoback_id() {
		return autoback_id;
	}
	public void setAutoback_id(short[] autoback_id) {
		this.autoback_id = autoback_id;
	}
	public short[] getAutoback_fully_id() {
		return autoback_fully_id;
	}
	public void setAutoback_fully_id(short[] autoback_fully_id) {
		this.autoback_fully_id = autoback_fully_id;
	}
	public short[] getSpks_id() {
		return spks_id;
	}
	public void setSpks_id(short[] spks_id) {
		this.spks_id = spks_id;
	}
	public short[] getAutotrig_id() {
		return autotrig_id;
	}
	public void setAutotrig_id(short[] autotrig_id) {
		this.autotrig_id = autotrig_id;
	}
	
	
}

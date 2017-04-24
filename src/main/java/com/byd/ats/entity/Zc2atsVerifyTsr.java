package com.byd.ats.entity;

public class Zc2atsVerifyTsr {

	private byte confirm_result;
	private byte fail_reason;
	private byte temp_lim_v;
	private short logic_track_num;
	public byte getConfirm_result() {
		return confirm_result;
	}
	public void setConfirm_result(byte confirm_result) {
		this.confirm_result = confirm_result;
	}
	public byte getFail_reason() {
		return fail_reason;
	}
	public void setFail_reason(byte fail_reason) {
		this.fail_reason = fail_reason;
	}
	public byte getTemp_lim_v() {
		return temp_lim_v;
	}
	public void setTemp_lim_v(byte temp_lim_v) {
		this.temp_lim_v = temp_lim_v;
	}
	public short getLogic_track_num() {
		return logic_track_num;
	}
	public void setLogic_track_num(short logic_track_num) {
		this.logic_track_num = logic_track_num;
	}
	
	
}

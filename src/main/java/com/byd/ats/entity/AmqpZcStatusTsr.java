package com.byd.ats.entity;

public class AmqpZcStatusTsr {

	private HeaderInfo zc_header_status;
	private MsgHeader zc_msg_header_sta;
	private Zc2atsStatusTsr zc2ats_sta_tsr;
	private short[] lgc_tsr_sta;
	private Timestamp t_stamp;
	
	public HeaderInfo getZc_header_status() {
		return zc_header_status;
	}
	public void setZc_header_status(HeaderInfo zc_header_status) {
		this.zc_header_status = zc_header_status;
	}
	public MsgHeader getZc_msg_header_sta() {
		return zc_msg_header_sta;
	}
	public void setZc_msg_header_sta(MsgHeader zc_msg_header_sta) {
		this.zc_msg_header_sta = zc_msg_header_sta;
	}
	public Zc2atsStatusTsr getZc2ats_sta_tsr() {
		return zc2ats_sta_tsr;
	}
	public void setZc2ats_sta_tsr(Zc2atsStatusTsr zc2ats_sta_tsr) {
		this.zc2ats_sta_tsr = zc2ats_sta_tsr;
	}

	public Timestamp getT_stamp() {
		return t_stamp;
	}
	public void setT_stamp(Timestamp t_stamp) {
		this.t_stamp = t_stamp;
	}
	public short[] getLgc_tsr_sta() {
		return lgc_tsr_sta;
	}
	public void setLgc_tsr_sta(short[] lgc_tsr_sta) {
		this.lgc_tsr_sta = lgc_tsr_sta;
	}

	
	
	
}

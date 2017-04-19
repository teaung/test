package com.byd.ats.entity;

public class AmqpCiStatus {

	HeaderInfo header_info;
	MsgHeader msg_header;
	CimsgStatus ci_status;
	DeviceId dev_id;
	Timestamp t_stamp;
	public HeaderInfo getHeader_info() {
		return header_info;
	}
	public void setHeader_info(HeaderInfo header_info) {
		this.header_info = header_info;
	}
	public MsgHeader getMsg_header() {
		return msg_header;
	}
	public void setMsg_header(MsgHeader msg_header) {
		this.msg_header = msg_header;
	}
	public CimsgStatus getCi_status() {
		return ci_status;
	}
	public void setCi_status(CimsgStatus ci_status) {
		this.ci_status = ci_status;
	}
	public DeviceId getDev_id() {
		return dev_id;
	}
	public void setDev_id(DeviceId dev_id) {
		this.dev_id = dev_id;
	}
	public Timestamp getT_stamp() {
		return t_stamp;
	}
	public void setT_stamp(Timestamp t_stamp) {
		this.t_stamp = t_stamp;
	}
	
	
}

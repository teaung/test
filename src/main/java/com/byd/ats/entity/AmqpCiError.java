package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmqpCiError {

	@JsonProperty("header_info")
	private HeaderInfo header_info;
	
	@JsonProperty("msg_header")
	private MsgHeader msg_header;
	
	@JsonProperty("ci_msg_error1")
	private CiMsgError1 ci_msg_error1;
	
	@JsonProperty("ci_msg_error2")
	private CiMsgError2 ci_msg_error2;
	
	@JsonProperty("t_stamp")
	private Timestamp t_stamp;
	
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
	public CiMsgError1 getCi_msg_error1() {
		return ci_msg_error1;
	}
	public void setCi_msg_error1(CiMsgError1 ci_msg_error1) {
		this.ci_msg_error1 = ci_msg_error1;
	}
	public CiMsgError2 getCi_msg_error2() {
		return ci_msg_error2;
	}
	public void setCi_msg_error2(CiMsgError2 ci_msg_error2) {
		this.ci_msg_error2 = ci_msg_error2;
	}
	public Timestamp getT_stamp() {
		return t_stamp;
	}
	public void setT_stamp(Timestamp t_stamp) {
		this.t_stamp = t_stamp;
	}
	
	
	
}

package com.byd.ats.entity;

public class MsgHeader {

	private short msg_len;
	private short msg_type;
	public short getMsg_len() {
		return msg_len;
	}
	public void setMsg_len(short msg_len) {
		this.msg_len = msg_len;
	}
	public short getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(short msg_type) {
		this.msg_type = msg_type;
	}
	
}

package com.byd.entity;

public class Ats2ciMsgComm {

	private HeaderInfo header_info;
	private MsgHeader msg_header;
	private AtsMsgCommand ats_msg_command;

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
	public AtsMsgCommand getAts_msg_command() {
		return ats_msg_command;
	}
	public void setAts_msg_command(AtsMsgCommand ats_msg_command) {
		this.ats_msg_command = ats_msg_command;
	}

	
}

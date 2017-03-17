package com.byd.entity;

public class Ats2vobcMsgComm {

	private HeaderInfo header_info;
	private MsgHeader msg_header;
	private Ats2vobcAtoCommand ats2vobc_ato_command;

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
	public Ats2vobcAtoCommand getAts2vobc_ato_command() {
		return ats2vobc_ato_command;
	}
	public void setAts2vobc_ato_command(Ats2vobcAtoCommand ats2vobc_ato_command) {
		this.ats2vobc_ato_command = ats2vobc_ato_command;
	}
	
}

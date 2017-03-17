package com.byd.entity;

public class Ats2ciMsgRunst {

	private HeaderInfo header_info;
	private MsgHeader msg_header;
	private AtsMsgRunStatus ats_msg_run_status;
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
	public AtsMsgRunStatus getAts_msg_run_status() {
		return ats_msg_run_status;
	}
	public void setAts_msg_run_status(AtsMsgRunStatus ats_msg_run_status) {
		this.ats_msg_run_status = ats_msg_run_status;
	}

}

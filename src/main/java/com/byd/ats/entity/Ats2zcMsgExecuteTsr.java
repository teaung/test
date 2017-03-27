package com.byd.ats.entity;

public class Ats2zcMsgExecuteTsr {

	private HeaderInfo header_info_exec;
	private MsgHeader msg_header_exec;
	private Ats2zcExecuteTsr execue_tsr;
	private int[]  lg_id;
	
	public HeaderInfo getHeader_info_exec() {
		return header_info_exec;
	}
	public void setHeader_info_exec(HeaderInfo header_info_exec) {
		this.header_info_exec = header_info_exec;
	}
	public MsgHeader getMsg_header_exec() {
		return msg_header_exec;
	}
	public void setMsg_header_exec(MsgHeader msg_header_exec) {
		this.msg_header_exec = msg_header_exec;
	}

	public Ats2zcExecuteTsr getExecue_tsr() {
		return execue_tsr;
	}
	public void setExecue_tsr(Ats2zcExecuteTsr execue_tsr) {
		this.execue_tsr = execue_tsr;
	}
	public int[] getLg_id() {
		return lg_id;
	}
	public void setLg_id(int[] lg_id) {
		this.lg_id = lg_id;
	}



}

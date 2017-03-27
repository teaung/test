package com.byd.ats.entity;

public class Ats2zcMsgVerifyTsr {

	private HeaderInfo header_info_ver;
	private MsgHeader msg_header_ver;
	private Ats2zcVerifyTsr verify_tsr;
	private int[]   lg_id;

	public HeaderInfo getHeader_info_ver() {
		return header_info_ver;
	}
	public void setHeader_info_ver(HeaderInfo header_info_ver) {
		this.header_info_ver = header_info_ver;
	}
	public MsgHeader getMsg_header_ver() {
		return msg_header_ver;
	}
	public void setMsg_header_ver(MsgHeader msg_header_ver) {
		this.msg_header_ver = msg_header_ver;
	}
	public Ats2zcVerifyTsr getVerify_tsr() {
		return verify_tsr;
	}
	public void setVerify_tsr(Ats2zcVerifyTsr verify_tsr) {
		this.verify_tsr = verify_tsr;
	}
	public int[] getLg_id() {
		return lg_id;
	}
	public void setLg_id(int[] lg_id) {
		this.lg_id = lg_id;
	}

	
}

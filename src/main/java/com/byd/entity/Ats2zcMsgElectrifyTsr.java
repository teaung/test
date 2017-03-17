package com.byd.entity;

public class Ats2zcMsgElectrifyTsr {

	private HeaderInfo header_info_elec;
	private MsgHeader msg_header_elec;
	private Ats2zcElectrifyTsr elec_tsr;;
	private int[] lg_t_id;

	public HeaderInfo getHeader_info_elec() {
		return header_info_elec;
	}
	public void setHeader_info_elec(HeaderInfo header_info_elec) {
		this.header_info_elec = header_info_elec;
	}
	public MsgHeader getMsg_header_elec() {
		return msg_header_elec;
	}
	public void setMsg_header_elec(MsgHeader msg_header_elec) {
		this.msg_header_elec = msg_header_elec;
	}
	public Ats2zcElectrifyTsr getElec_tsr() {
		return elec_tsr;
	}
	public void setElec_tsr(Ats2zcElectrifyTsr elec_tsr) {
		this.elec_tsr = elec_tsr;
	}
	public int[] getLg_t_id() {
		return lg_t_id;
	}
	public void setLg_t_id(int[] lg_t_id) {
		this.lg_t_id = lg_t_id;
	}




	
}

package com.byd.ats.entity;

public class AmqpZcStatusTsr {

	private HeaderInfo zc_header_status;
	private MsgHeader zc_msg_header_sta;
	private Zc2atsStatusTsr zc2ats_execu_tsr;
	private Zc2atsLogicTsrStatus []logic_tsr_sta = new Zc2atsLogicTsrStatus[AtsPub.LOGIC_TRACK_NUM];
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
	public Zc2atsStatusTsr getZc2ats_execu_tsr() {
		return zc2ats_execu_tsr;
	}
	public void setZc2ats_execu_tsr(Zc2atsStatusTsr zc2ats_execu_tsr) {
		this.zc2ats_execu_tsr = zc2ats_execu_tsr;
	}
	public Zc2atsLogicTsrStatus[] getLogic_tsr_sta() {
		return logic_tsr_sta;
	}
	public void setLogic_tsr_sta(Zc2atsLogicTsrStatus[] logic_tsr_sta) {
		this.logic_tsr_sta = logic_tsr_sta;
	}
	
	
}

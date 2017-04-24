package com.byd.ats.entity;

public class AmqpZcVerifyTsr {

	private HeaderInfo zc_header_ver;
	private MsgHeader zc_msg_header_ver;
	private Zc2atsVerifyTsr zc2ats_ver_tsr;
	private Zc2atsLogicId []logic_id_tsr = new Zc2atsLogicId[AtsPub.LOGIC_TRACK_NUM];
	public HeaderInfo getZc_header_ver() {
		return zc_header_ver;
	}
	public void setZc_header_ver(HeaderInfo zc_header_ver) {
		this.zc_header_ver = zc_header_ver;
	}
	public MsgHeader getZc_msg_header_ver() {
		return zc_msg_header_ver;
	}
	public void setZc_msg_header_ver(MsgHeader zc_msg_header_ver) {
		this.zc_msg_header_ver = zc_msg_header_ver;
	}
	public Zc2atsVerifyTsr getZc2ats_ver_tsr() {
		return zc2ats_ver_tsr;
	}
	public void setZc2ats_ver_tsr(Zc2atsVerifyTsr zc2ats_ver_tsr) {
		this.zc2ats_ver_tsr = zc2ats_ver_tsr;
	}
	public Zc2atsLogicId[] getLogic_id_tsr() {
		return logic_id_tsr;
	}
	public void setLogic_id_tsr(Zc2atsLogicId[] logic_id_tsr) {
		this.logic_id_tsr = logic_id_tsr;
	}
	
	
}

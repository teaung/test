package com.byd.ats.entity;

public class AmqpZcExecuteTsr {

	private HeaderInfo zc_header_execute;
	private MsgHeader zc_msg_header_execute;
	private Zs2atsExecuteTsr zc2ats_execu_tsr;
	private Zc2atsLogicId []logic_id_execu = new Zc2atsLogicId[AtsPub.LOGIC_TRACK_NUM];
	public HeaderInfo getZc_header_execute() {
		return zc_header_execute;
	}
	public void setZc_header_execute(HeaderInfo zc_header_execute) {
		this.zc_header_execute = zc_header_execute;
	}
	public MsgHeader getZc_msg_header_execute() {
		return zc_msg_header_execute;
	}
	public void setZc_msg_header_execute(MsgHeader zc_msg_header_execute) {
		this.zc_msg_header_execute = zc_msg_header_execute;
	}
	public Zs2atsExecuteTsr getZc2ats_execu_tsr() {
		return zc2ats_execu_tsr;
	}
	public void setZc2ats_execu_tsr(Zs2atsExecuteTsr zc2ats_execu_tsr) {
		this.zc2ats_execu_tsr = zc2ats_execu_tsr;
	}
	public Zc2atsLogicId[] getLogic_id_execu() {
		return logic_id_execu;
	}
	public void setLogic_id_execu(Zc2atsLogicId[] logic_id_execu) {
		this.logic_id_execu = logic_id_execu;
	}
	
	
}

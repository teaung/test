package com.byd.ats.entity;
public class HeaderInfo {
	private int inface_type;
	private int send_vender;
	private int receive_vender;
	private byte map_version;
	private int map_crc;
	private int msg_cnum;
	private byte comm_cycle;
	private int msg_snum_side;
	private int msg_cnum_previous_msg;
	private byte protocol_version;
	
	public int getInface_type() {
		return inface_type;
	}
	public void setInface_type(int inface_type) {
		this.inface_type = inface_type;
	}

	public int getSend_vender() {
		return send_vender;
	}
	public void setSend_vender(int send_vender) {
		this.send_vender = send_vender;
	}
	public int getReceive_vender() {
		return receive_vender;
	}
	public void setReceive_vender(int receive_vender) {
		this.receive_vender = receive_vender;
	}
	public byte getMap_version() {
		return map_version;
	}
	public void setMap_version(byte map_version) {
		this.map_version = map_version;
	}
	public int getMap_crc() {
		return map_crc;
	}
	public void setMap_crc(int map_crc) {
		this.map_crc = map_crc;
	}
	public int getMsg_cnum() {
		return msg_cnum;
	}
	public void setMsg_cnum(int msg_cnum) {
		this.msg_cnum = msg_cnum;
	}
	public byte getComm_cycle() {
		return comm_cycle;
	}
	public void setComm_cycle(byte comm_cycle) {
		this.comm_cycle = comm_cycle;
	}
	public int getMsg_snum_side() {
		return msg_snum_side;
	}
	public void setMsg_snum_side(int msg_snum_side) {
		this.msg_snum_side = msg_snum_side;
	}
	public int getMsg_cnum_previous_msg() {
		return msg_cnum_previous_msg;
	}
	public void setMsg_cnum_previous_msg(int msg_cnum_previous_msg) {
		this.msg_cnum_previous_msg = msg_cnum_previous_msg;
	}
	public byte getProtocol_version() {
		return protocol_version;
	}
	public void setProtocol_version(byte protocol_version) {
		this.protocol_version = protocol_version;
	}
	
}

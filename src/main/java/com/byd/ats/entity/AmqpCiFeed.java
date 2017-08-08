package com.byd.ats.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class AmqpCiFeed {

	private HeaderInfo header_info;
	private MsgHeader msg_header;
	private int feed_num;
	private Timestamp t_stamp;
	private List<Cu2AtsCiFeed> ci_feed_n;
	
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
	public int getFeed_num() {
		return feed_num;
	}
	public void setFeed_num(int feed_num) {
		this.feed_num = feed_num;
	}
	public List<Cu2AtsCiFeed> getCi_feed_n() {
		return ci_feed_n;
	}
	public void setCi_feed_n(List<Cu2AtsCiFeed> ci_feed_n) {
		this.ci_feed_n = ci_feed_n;
	}
	public Timestamp getT_stamp() {
		return t_stamp;
	}
	public void setT_stamp(Timestamp t_stamp) {
		this.t_stamp = t_stamp;
	}
	
	
}

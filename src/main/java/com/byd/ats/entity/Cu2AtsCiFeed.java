package com.byd.ats.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Cu2AtsCiFeed {
/*
"ci_feed_n":[{"feed_type":0,"feed_status":1,"feed_id":0,"feed_time":0,"com_serial_num":0},
{"feed_type":0,"feed_status":1,"feed_id":0,"feed_time":0,"com_serial_num":0},
{"feed_type":0,"feed_status":1,"feed_id":0,"feed_time":0,"com_serial_num":0}]
*/
	private int feed_type;//反馈状态类型
	private int feed_status;//执行状态
	private int feed_id;//进路ID/区段ID/道岔ID
	private int feed_time;//计时秒数
	private long com_serial_num; //序列号
	public int getFeed_type() {
		return feed_type;
	}
	public void setFeed_type(int feed_type) {
		this.feed_type = feed_type;
	}
	public int getFeed_status() {
		return feed_status;
	}
	public void setFeed_status(int feed_status) {
		this.feed_status = feed_status;
	}
	public int getFeed_id() {
		return feed_id;
	}
	public void setFeed_id(int feed_id) {
		this.feed_id = feed_id;
	}
	public int getFeed_time() {
		return feed_time;
	}
	public void setFeed_time(int feed_time) {
		this.feed_time = feed_time;
	}
	public long getCom_serial_num() {
		return com_serial_num;
	}
	public void setCom_serial_num(long com_serial_num) {
		this.com_serial_num = com_serial_num;
	}
	
	
}

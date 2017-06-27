package com.byd.ats.entity;

import java.util.Date;

public class Cu2AtsCiFeed {

	private int feed_type;//反馈状态类型
	private int feed_status;//执行状态
	private int feed_id;//进路ID/区段ID/道岔ID
	private Date feed_time;//计时秒数
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
	public Date getFeed_time() {
		return feed_time;
	}
	public void setFeed_time(Date feed_time) {
		this.feed_time = feed_time;
	}
	
	
}

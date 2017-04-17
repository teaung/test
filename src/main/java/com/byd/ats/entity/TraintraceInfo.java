package com.byd.ats.entity;

public class TraintraceInfo {

	private short service_num;
	private short line_num;
	private short train_line_num;
	private short train_num;
	private short origin_line_num;
	private short train_order_num;
	private short destin_line_num;
	private int destin_num;
	private short direction_train;
	private short park_stab_status;
	private short this_station_id;
	private short next_station_id;
	private long sec;
	private long usec;
	public short getService_num() {
		return service_num;
	}
	public void setService_num(short service_num) {
		this.service_num = service_num;
	}
	public short getLine_num() {
		return line_num;
	}
	public void setLine_num(short line_num) {
		this.line_num = line_num;
	}
	public short getTrain_line_num() {
		return train_line_num;
	}
	public void setTrain_line_num(short train_line_num) {
		this.train_line_num = train_line_num;
	}
	public short getTrain_num() {
		return train_num;
	}
	public void setTrain_num(short train_num) {
		this.train_num = train_num;
	}
	public short getOrigin_line_num() {
		return origin_line_num;
	}
	public void setOrigin_line_num(short origin_line_num) {
		this.origin_line_num = origin_line_num;
	}
	public short getTrain_order_num() {
		return train_order_num;
	}
	public void setTrain_order_num(short train_order_num) {
		this.train_order_num = train_order_num;
	}
	public short getDestin_line_num() {
		return destin_line_num;
	}
	public void setDestin_line_num(short destin_line_num) {
		this.destin_line_num = destin_line_num;
	}
	public int getDestin_num() {
		return destin_num;
	}
	public void setDestin_num(int destin_num) {
		this.destin_num = destin_num;
	}
	public short getDirection_train() {
		return direction_train;
	}
	public void setDirection_train(short direction_train) {
		this.direction_train = direction_train;
	}
	public short getPark_stab_status() {
		return park_stab_status;
	}
	public void setPark_stab_status(short park_stab_status) {
		this.park_stab_status = park_stab_status;
	}
	public long getSec() {
		return sec;
	}
	public void setSec(long sec) {
		this.sec = sec;
	}
	public long getUsec() {
		return usec;
	}
	public void setUsec(long usec) {
		this.usec = usec;
	}
	public short getThis_station_id() {
		return this_station_id;
	}
	public void setThis_station_id(short this_station_id) {
		this.this_station_id = this_station_id;
	}
	public short getNext_station_id() {
		return next_station_id;
	}
	public void setNext_station_id(short next_station_id) {
		this.next_station_id = next_station_id;
	}
	
	
}

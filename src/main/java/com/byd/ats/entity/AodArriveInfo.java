package com.byd.ats.entity;

public class AodArriveInfo {

	private String servTag;
	private short serviceNum;
	private short lineNum;
	private short carLineNum;
	private short carNum;
	private short srcLineNum;
	private short trainNum;
	private short dstLineNum;
	private String dstStationNum;
	private short directionPlan;
	private int station;
	private int nextStationId;
	private short trainPark;
	private String trainHeaderAtphycical;
	private int runningLevel;
	private long timestamp;
	public String getServTag() {
		return servTag;
	}
	public void setServTag(String servTag) {
		this.servTag = servTag;
	}
	public short getServiceNum() {
		return serviceNum;
	}
	public void setServiceNum(short serviceNum) {
		this.serviceNum = serviceNum;
	}
	public short getLineNum() {
		return lineNum;
	}
	public void setLineNum(short lineNum) {
		this.lineNum = lineNum;
	}
	public short getCarLineNum() {
		return carLineNum;
	}
	public void setCarLineNum(short carLineNum) {
		this.carLineNum = carLineNum;
	}
	public short getCarNum() {
		return carNum;
	}
	public void setCarNum(short carNum) {
		this.carNum = carNum;
	}
	public short getSrcLineNum() {
		return srcLineNum;
	}
	public void setSrcLineNum(short srcLineNum) {
		this.srcLineNum = srcLineNum;
	}
	public short getTrainNum() {
		return trainNum;
	}
	public void setTrainNum(short trainNum) {
		this.trainNum = trainNum;
	}
	public short getDstLineNum() {
		return dstLineNum;
	}
	public void setDstLineNum(short dstLineNum) {
		this.dstLineNum = dstLineNum;
	}
	public String getDstStationNum() {
		return dstStationNum;
	}
	public void setDstStationNum(String dstStationNum) {
		this.dstStationNum = dstStationNum;
	}
	public short getDirectionPlan() {
		return directionPlan;
	}
	public void setDirectionPlan(short directionPlan) {
		this.directionPlan = directionPlan;
	}
	public int getStation() {
		return station;
	}
	public void setStation(int station) {
		this.station = station;
	}
	public int getNextStationId() {
		return nextStationId;
	}
	public void setNextStationId(int nextStationId) {
		this.nextStationId = nextStationId;
	}
	public short getTrainPark() {
		return trainPark;
	}
	public void setTrainPark(short trainPark) {
		this.trainPark = trainPark;
	}
	public String getTrainHeaderAtphycical() {
		return trainHeaderAtphycical;
	}
	public void setTrainHeaderAtphycical(String trainHeaderAtphycical) {
		this.trainHeaderAtphycical = trainHeaderAtphycical;
	}
	public int getRunningLevel() {
		return runningLevel;
	}
	public void setRunningLevel(int runningLevel) {
		this.runningLevel = runningLevel;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}

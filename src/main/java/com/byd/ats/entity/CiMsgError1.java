package com.byd.ats.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 故障状态信息帧（CI->ATS）
 * 参考：《LRTSW-SYS-ATS与CI通信接口协议》
 * 400ms周期发送
 * 通信超时中断时间为6s
 */
/*
// cu_pub.h
typedef struct _ci_msg_error1
{
/* 1  uint32_t  ci_version;                CI应用软件版本号 
 2  uint8_t  ci_warning;                 联锁设备报警信息 
 3  uint8_t  ups_fail;                   UPS电源故障 
 4  uint8_t  power_screen;               电源屏故障信息 0x55:
 5  uint8_t  leu_status;                 LEU通信状态 
 6  uint8_t  turnback_auto;              无人折返状态 
 7  uint8_t  ci_mode;                    模式状态 
 8  uint8_t  ci_block;                   全站封锁状态 
 9  uint8_t  power_on_unlock;            上电解锁状态 
 10 uint8_t  fuse_warning;               熔丝报警 
 11 uint8_t  ci1_status;                 联锁1工作状态 
 12 uint8_t  ci2_status;                 联锁2工作状态 
 13 uint8_t  ci1_power;                  1路电源状态 
 14 uint8_t  ci2_power;                  2路电源状态 
 15 uint8_t  ci_zc_comm_main;            联锁ZC主设备通信 
 16 uint8_t  ci_zc_comm_spare;           联锁ZC主设备通信
}ci_msg_error1_t;
 */

public class CiMsgError1 {

	/*
	 * CI应用软件版本号 ：主版本(1字节).次版本(1字节).修订版本(2字节),如v0.3.0）不使用时填写默认值：0xFFFFFFFF
	 */
	@JsonProperty("ci_version")
	private long ciVersion;
	/*
	 * 联锁设备故障报警信息 0x55：未故障 0xaa：故障
	 */
	@JsonProperty("ci_warning")
	private short ciWarning;
	/*
	 * UPS电源故障 | 1 | 0x55：未故障<br>0xaa：故障 ||
	 */
	@JsonProperty("ups_fail")
	private short upsFail;
	/*
	 * 电源屏故障信息 0x55：未故障  0xaa：故障
	 */
	@JsonProperty("power_screen")
	private short powerScreen;
	/*
	 *LEU通信状态 0x01：当任意一个LEU设备与联锁通信正常，显示绿灯 0x03：当任意一个LEU设备的两个连接与联锁通信均中断，显示红灯
	 */
	@JsonProperty("leu_status")
	private short leuStatus;
	/*
	 *无人折返状态 0x01：常态 0x02：收到ZC的无人折返闪灯命令 0x03：收到ZC的无人折返稳灯命令
	 */
	@JsonProperty("turnback_auto")
	private short turnbackAuto;
	/*
	 *模式状态 0x55：站控 0xaa：中控 0xcc：非常站控
	 */
	@JsonProperty("ci_mode")
	private short ciMode;
	/*
	 *全站封锁状态  0x55：未封锁 0xaa：封锁
	 */
	@JsonProperty("ci_block")
	private short ciBlock;
	/*
	 * 上电解锁状态 0x55:未解锁 0xaa:解锁
	 */
	@JsonProperty("power_on_unlock")
	private short powerOnUnlock;
	/*
	 * 熔丝报警
	 * 0x55：有熔丝报警
	 * 0xaa：无熔丝报警
	 * 对应采集排架熔丝报警继电器
	 */
	@JsonProperty("fuse_warning")
	private short fuseWarning;
	/*
	 * 联锁1系工作状态（1字节）：
	 * 0x01=联锁1系主用；
	 * 0x02=联锁1系备用；
	 * 0x03=联锁1系停机或未与其建立通信；
	 */
	@JsonProperty("ci1_status")
	private short ci1Status;                // 联锁1工作状态
	/*
	 * 联锁2系工作状态（1字节）：
	 * 0x01=联锁2系主用；
	 * 0x02=联锁2系备用；
	 * 0x03=联锁2系停机或未与其建立通信；
	 */
	@JsonProperty("ci2_status")
	private short ci2Status;                // 联锁2工作状态

	/*
	 * 1路电源状态（1字节）：0x55=1路电源供电；0xAA=1路电源不供电；
	 */
	@JsonProperty("ci1_power")
	private short ci1Power;                 // 1路电源状态 
	/*
	 * 2路电源状态（1字节）：0x55=2路电源供电；0xAA=2路电源不供电；
	 */
	@JsonProperty("ci2_power")
	private short ci2Power;                 // 2路电源状态
	/*
	 *  联锁与ZC主用通信状态 0x01：联锁与ZC主用双网通信均正常 0x02：联锁与ZC主用单网通信中断 0x03：联锁与ZC主用双网通信都中断
	 */
	@JsonProperty("ci_zc_comm_main")
	private short ciZcCommMain;
	/*
	 *  联锁与ZC备用通信状态 0x01：联锁与ZC备用双网通信均正常 0x02：联锁与ZC备用单网通信中断 0x03：联锁与ZC备用双网通信都中断
	 */
	@JsonProperty("ci_zc_comm_spare")
	private short ci2ciCommSpare;

	public long getCiVersion() {
		return ciVersion;
	}

	public void setCiVersion(long ciVersion) {
		this.ciVersion = ciVersion;
	}

	public short getCiWarning() {
		return ciWarning;
	}

	public void setCiWarning(short ciWarning) {
		this.ciWarning = ciWarning;
	}

	public short getUpsFail() {
		return upsFail;
	}

	public void setUpsFail(short upsFail) {
		this.upsFail = upsFail;
	}

	public short getPowerScreen() {
		return powerScreen;
	}

	public void setPowerScreen(short powerScreen) {
		this.powerScreen = powerScreen;
	}

	public short getLeuStatus() {
		return leuStatus;
	}

	public void setLeuStatus(short leuStatus) {
		this.leuStatus = leuStatus;
	}

	public short getTurnbackAuto() {
		return turnbackAuto;
	}

	public void setTurnbackAuto(short turnbackAuto) {
		this.turnbackAuto = turnbackAuto;
	}

	public short getCiMode() {
		return ciMode;
	}

	public void setCiMode(short ciMode) {
		this.ciMode = ciMode;
	}

	public short getCiBlock() {
		return ciBlock;
	}

	public void setCiBlock(short ciBlock) {
		this.ciBlock = ciBlock;
	}

	public short getPowerOnUnlock() {
		return powerOnUnlock;
	}

	public void setPowerOnUnlock(short powerOnUnlock) {
		this.powerOnUnlock = powerOnUnlock;
	}

	public short getFuseWarning() {
		return fuseWarning;
	}

	public void setFuseWarning(short fuseWarning) {
		this.fuseWarning = fuseWarning;
	}

	public short getCi1Status() {
		return ci1Status;
	}

	public void setCi1Status(short ci1Status) {
		this.ci1Status = ci1Status;
	}

	public short getCi2Status() {
		return ci2Status;
	}

	public void setCi2Status(short ci2Status) {
		this.ci2Status = ci2Status;
	}

	public short getCi1Power() {
		return ci1Power;
	}

	public void setCi1Power(short ci1Power) {
		this.ci1Power = ci1Power;
	}

	public short getCi2Power() {
		return ci2Power;
	}

	public void setCi2Power(short ci2Power) {
		this.ci2Power = ci2Power;
	}

	public short getCiZcCommMain() {
		return ciZcCommMain;
	}

	public void setCiZcCommMain(short ciZcCommMain) {
		this.ciZcCommMain = ciZcCommMain;
	}

	public short getCi2ciCommSpare() {
		return ci2ciCommSpare;
	}

	public void setCi2ciCommSpare(short ci2ciCommSpare) {
		this.ci2ciCommSpare = ci2ciCommSpare;
	}
}

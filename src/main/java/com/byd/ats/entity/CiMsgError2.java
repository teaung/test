package com.byd.ats.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 站场状态信息帧（CI->ATS）
 * 参考：《LRTSW-SYS-ATS与CI通信接口协议》
 * 400ms周期发送
 * 通信超时中断时间为6s
 */
/*
// cu_pub.h
#define SIGNAL_NUM      14  // 信号机数量
#define SWITCH_NUM      1   //  道岔数量 
#define PHY_TRACK_NUM   32  // 物理区段数量 
#define LOGIC_TRACK_NUM 93  // 逻辑区段数量
#define ROUTE_NUM       13  // 进路数量 
#define AUTOPASS_NUM    1   // 自动通过进路数量 
#define DOOR_NUM        12  // 站台门数量 
#define ESP_NUM         1   // 紧急关闭数量 
#define KEEP_TRAIN_NUM  1   // 扣车数量 
#define AUTOBACK_NUM    1   // 自动折返数量 
#define AUTOBACK_FULLY_NUM  1 // 全自动折返数量 
#define SPKS_NUM        1   // SPKS数量 
#define AUTOTRIG_NUM    1   // 自动触发数量 
#define TRAIN_NUM       8   // 列车数量 
#define CI_NUM          3   // CI数量 
#define ZC_NUM          3   // ZC数量 
//
typedef struct _ci_msg_error2
{
 1  uint8_t ci_conn_ci_num;                    与CI通信的联锁数量
 2  uint8_t ci_conn_ci_main[CI_NUM];           与XX联锁主用通信状态 
 3  uint8_t ci_conn_ci_spare[CI_NUM];          与XX联锁备用通信状态 
 4  uint8_t ci_conn_train_num;                 与车通信数量 
 5  uint8_t ci_conn_train_main[TRAIN_NUM];     与XX车主用通讯状态 
 6  uint8_t ci_conn_train_spare[TRAIN_NUM];    与XX车备用通讯状态 
 7  uint8_t ci_conn_psd_num;                   PSD通信数量 
 8  uint8_t ci_conn_psd[DOOR_NUM];             与PSD X通信状态 
 9  uint8_t sig_check_num;                     照查条件检查数量 
 10 uint8_t sig_check[SIG_CHECK];              照查条件x 
 11 uint8_t light_broken_wire_num;             灯丝断丝数量 
 12 uint32_t light_broken_wire[SIGNAL_NUM];    信号机ID
 13 uint8_t sig_break_num;                     信号故障关闭数量
 14 uint32_t sig_break[SIGNAL_NUM];            信号机ID
 15 uint8_t track_break_num;                   轨道故障报警数量
 16 uint32_t track_break[PHY_TRACK_NUM];       区段ID
 17 uint8_t switch_break_num;                  道岔挤岔数量
 18 uint32_t switch_break[SWITCH_NUM];         道岔ID

		}ci_msg_error2_t;

 */

public class CiMsgError2 {

	/*
	*与CI通信的联锁数量
	*/
	@JsonProperty("ci_conn_ci_num")
	private short ciConnCiNum;
	
	/*
	 * 与XX联锁主用通信状态
	 * 0x01：与XX联锁双网通信均正常
	 * 0x02：与XX联锁单网通信中断
	 * 0x03：与XX联锁双网通信都中断
	 */
	@JsonProperty("ci_conn_ci_main")
	private List<Short> ciConnCiMain;
	
	/*
	 * 与XX联锁备用通信状态
	 * 0x01：与XX联锁双网通信均正常
	 * 0x02：与XX联锁单网通信中断
	 * 0x03：与XX联锁双网通信都中断
	 */
	@JsonProperty("ci_conn_ci_spare")
	private List<Short> ciConnCiSpare;
	
	/*
	 * 与车通信数量
	 */
	@JsonProperty("ci_conn_train_num")
	private short ciConnTrainNum;
	
	/*
	 * 与XX车主用通讯状态
	 *0x01：与XX车双网通信均正常
	 * 0x02：与XX车单网通信中断
	 * 0x03：与XX车双网通信都中断
	 */
	@JsonProperty("ci_conn_train_main")
	private List<Short> ciConnTrainMain;
	
	/*
	 * 与XX车备用通讯状态
	 * 0x01：与XX车双网通信均正常
	 * 0x02：与XX车单网通信中断
	 * 0x03：与XX车双网通信都中断
	 */
	@JsonProperty("ci_conn_train_spare")
	private List<Short> ciConnTrainSpare;
	
	/*
	 PSD通信数量
	 */
	@JsonProperty("ci_conn_psd_num")
	private short ciConnPsdNum;
	
	/*
	 * 与PSD X通信状态
	 * 0x55：通信正常
	 * 0xaa：通信中断
	 */
	@JsonProperty("ci_conn_psd")
	private List<Short> ciConnPsd;
	
	/*
	 * 照查条件检查数量
	 */
	@JsonProperty("sig_check_num")
	private short sigCheckNum;
	
	/*
	 *照查条件x
	 * 0x55：对应照查继电器状态吸起
	 * 0xaa：对应照查继电器落下
	 */
	@JsonProperty("sig_check")
	private List<Short> sigCheck;
	
	/*
	 * 灯丝断丝数量
	 */
	@JsonProperty("light_broken_wire_num")
	private short lightBrokenWireNum;
	
	/*
	 * 信号机ID
	 */
	@JsonProperty("light_broken_wire")
	private List<Long> lightBrokenWire;

	/*
     * 信号故障关闭数量
     */
	@JsonProperty("sig_break_num")
	private short sigBreakNum;
	/*
     * 信号机ID
     */
	@JsonProperty("sig_break")
	private List<Long> sigBreak;
	/*
		轨道故障报警数量
	 */
	@JsonProperty("track_break_num")
	private short trackBreakNum;
	/*
	* 区段ID
	*/
	@JsonProperty("track_break")
	private List<Long> trackBreak;
	/*
	道岔挤岔数量
	*/
	@JsonProperty("switch_break_num")
	private short switchBreakNum;
	/*
	* 道岔ID
	*/
	@JsonProperty("switch_break")
	private List<Long> switchBreak;

	public short getCiConnCiNum() {
		return ciConnCiNum;
	}

	public void setCiConnCiNum(short ciConnCiNum) {
		this.ciConnCiNum = ciConnCiNum;
	}

	public List<Short> getCiConnCiMain() {
		return ciConnCiMain;
	}

	public void setCiConnCiMain(List<Short> ciConnCiMain) {
		this.ciConnCiMain = ciConnCiMain;
	}

	public List<Short> getCiConnCiSpare() {
		return ciConnCiSpare;
	}

	public void setCiConnCiSpare(List<Short> ciConnCiSpare) {
		this.ciConnCiSpare = ciConnCiSpare;
	}

	public short getCiConnTrainNum() {
		return ciConnTrainNum;
	}

	public void setCiConnTrainNum(short ciConnTrainNum) {
		this.ciConnTrainNum = ciConnTrainNum;
	}

	public List<Short> getCiConnTrainMain() {
		return ciConnTrainMain;
	}

	public void setCiConnTrainMain(List<Short> ciConnTrainMain) {
		this.ciConnTrainMain = ciConnTrainMain;
	}

	public List<Short> getCiConnTrainSpare() {
		return ciConnTrainSpare;
	}

	public void setCiConnTrainSpare(List<Short> ciConnTrainSpare) {
		this.ciConnTrainSpare = ciConnTrainSpare;
	}

	public short getCiConnPsdNum() {
		return ciConnPsdNum;
	}

	public void setCiConnPsdNum(short ciConnPsdNum) {
		this.ciConnPsdNum = ciConnPsdNum;
	}

	public List<Short> getCiConnPsd() {
		return ciConnPsd;
	}

	public void setCiConnPsd(List<Short> ciConnPsd) {
		this.ciConnPsd = ciConnPsd;
	}

	public short getSigCheckNum() {
		return sigCheckNum;
	}

	public void setSigCheckNum(short sigCheckNum) {
		this.sigCheckNum = sigCheckNum;
	}

	public List<Short> getSigCheck() {
		return sigCheck;
	}

	public void setSigCheck(List<Short> sigCheck) {
		this.sigCheck = sigCheck;
	}

	public short getLightBrokenWireNum() {
		return lightBrokenWireNum;
	}

	public void setLightBrokenWireNum(short lightBrokenWireNum) {
		this.lightBrokenWireNum = lightBrokenWireNum;
	}

	public List<Long> getLightBrokenWire() {
		return lightBrokenWire;
	}

	public void setLightBrokenWire(List<Long> lightBrokenWire) {
		this.lightBrokenWire = lightBrokenWire;
	}

	public short getSigBreakNum() {
		return sigBreakNum;
	}

	public void setSigBreakNum(short sigBreakNum) {
		this.sigBreakNum = sigBreakNum;
	}

	public List<Long> getSigBreak() {
		return sigBreak;
	}

	public void setSigBreak(List<Long> sigBreak) {
		this.sigBreak = sigBreak;
	}

	public short getTrackBreakNum() {
		return trackBreakNum;
	}

	public void setTrackBreakNum(short trackBreakNum) {
		this.trackBreakNum = trackBreakNum;
	}

	public List<Long> getTrackBreak() {
		return trackBreak;
	}

	public void setTrackBreak(List<Long> trackBreak) {
		this.trackBreak = trackBreak;
	}

	public short getSwitchBreakNum() {
		return switchBreakNum;
	}

	public void setSwitchBreakNum(short switchBreakNum) {
		this.switchBreakNum = switchBreakNum;
	}

	public List<Long> getSwitchBreak() {
		return switchBreak;
	}

	public void setSwitchBreak(List<Long> switchBreak) {
		this.switchBreak = switchBreak;
	}
}

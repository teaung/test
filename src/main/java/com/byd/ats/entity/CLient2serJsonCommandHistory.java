package com.byd.ats.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_client2ser_json_command_history")
public class CLient2serJsonCommandHistory {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(name = "json",length =1024)
	private String json;
	private String username;
	private int clientNum;
	private int ret;
	private Date createtime;
	private int status = 0; // 0:无效; 1:客户端发送命令至运行控制模块；2：运行控制模块下发命令给CU；3：收到CU的命令反馈；4：将命令反馈发送给客户端
	private int magic; // 魔数，用于CI命令反馈信息的匹配
	private int cmd; // 命令号

	private int cmdClass;
	private String cmdParameter;
	private int workstation;
	private int forCmd;
	private String password;
	private Date rClientTime;
	private Date sClientTime;
	private Date rCuTime;
	private Date sCuTime;
	private int ciNum;
	private int currentMode;
	private int modifiedMode;
	private int way;
	private int srcClientNum;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getClientNum() {
		return clientNum;
	}
	public void setClientNum(int clientNum) {
		this.clientNum = clientNum;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getMagic() {
		return magic;
	}
	public void setMagic(int magic) {
		this.magic = magic;
	}
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public int getCmdClass() {
		return cmdClass;
	}
	public void setCmdClass(int cmdClass) {
		this.cmdClass = cmdClass;
	}
	public String getCmdParameter() {
		return cmdParameter;
	}
	public void setCmdParameter(String cmdParameter) {
		this.cmdParameter = cmdParameter;
	}
	public int getWorkstation() {
		return workstation;
	}
	public void setWorkstation(int workstation) {
		this.workstation = workstation;
	}
	public int getForCmd() {
		return forCmd;
	}
	public void setForCmd(int forCmd) {
		this.forCmd = forCmd;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getrClientTime() {
		return rClientTime;
	}
	public void setrClientTime(Date rClientTime) {
		this.rClientTime = rClientTime;
	}
	public Date getsClientTime() {
		return sClientTime;
	}
	public void setsClientTime(Date sClientTime) {
		this.sClientTime = sClientTime;
	}
	public Date getrCuTime() {
		return rCuTime;
	}
	public void setrCuTime(Date rCuTime) {
		this.rCuTime = rCuTime;
	}
	public Date getsCuTime() {
		return sCuTime;
	}
	public void setsCuTime(Date sCuTime) {
		this.sCuTime = sCuTime;
	}
	public int getCiNum() {
		return ciNum;
	}
	public void setCiNum(int ciNum) {
		this.ciNum = ciNum;
	}
	public int getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}
	public int getModifiedMode() {
		return modifiedMode;
	}
	public void setModifiedMode(int modifiedMode) {
		this.modifiedMode = modifiedMode;
	}
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	public int getSrcClientNum() {
		return srcClientNum;
	}
	public void setSrcClientNum(int srcClientNum) {
		this.srcClientNum = srcClientNum;
	}
	
	
	
}

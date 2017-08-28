package com.byd.ats.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_client2ser_json_command")
public class CLient2serJsonCommand {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(name = "json",length =1024)
	private String json;
	private String username;
	private int client_num;
	private int ret;
	private Date createtime;
	private int status = 0; // 0:无效; 1:客户端发送命令至运行控制模块；2：运行控制模块下发命令给CU；3：收到CU的命令反馈；4：将命令反馈发送给客户端
	
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
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public int getClient_num() {
		return client_num;
	}
	public void setClient_num(int client_num) {
		this.client_num = client_num;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}

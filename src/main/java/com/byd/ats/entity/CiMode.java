package com.byd.ats.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_ci_mode")
public class CiMode {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private int ci_mode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCi_mode() {
		return ci_mode;
	}
	public void setCi_mode(int ci_mode) {
		this.ci_mode = ci_mode;
	}
	
	
}

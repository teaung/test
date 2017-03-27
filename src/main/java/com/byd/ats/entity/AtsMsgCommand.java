package com.byd.ats.entity;

public class AtsMsgCommand {

	private int command_num;
	private int command_type;
	private int object_id;
	private int object_type;
	private int object_other;
	public int getCommand_num() {
		return command_num;
	}
	public void setCommand_num(int command_num) {
		this.command_num = command_num;
	}
	public int getCommand_type() {
		return command_type;
	}
	public void setCommand_type(int command_type) {
		this.command_type = command_type;
	}
	public int getObject_id() {
		return object_id;
	}
	public void setObject_id(int object_id) {
		this.object_id = object_id;
	}
	public int getObject_type() {
		return object_type;
	}
	public void setObject_type(int object_type) {
		this.object_type = object_type;
	}
	public int getObject_other() {
		return object_other;
	}
	public void setObject_other(int object_other) {
		this.object_other = object_other;
	}
	
}

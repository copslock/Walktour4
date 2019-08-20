package com.walktour.model;

public class ProcessModel {
	private String user;
	private String pid;
	private String ppid;
	private String name;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPpid() {
		return ppid;
	}
	public void setPpid(String ppid) {
		this.ppid = ppid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

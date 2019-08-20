package com.walktour.workorder.model;
/**
 * 服务器模型类
 * Author: ZhengLei
 *   Date: 2013-6-9 上午9:57:15
 */
public class ServerInfo {
	private String ipAddr;
	private int port;
	private String account;
	private String password;
	private int svrType;
	
	public ServerInfo() {
		super();
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSvrType() {
		return svrType;
	}

	public void setSvrType(int svrType) {
		this.svrType = svrType;
	}

	@Override
	public String toString() {
		return "ServerInfo [ipAddr=" + ipAddr + ", port=" + port + ", account="
				+ account + ", password=" + password + ", svrType=" + svrType
				+ "]";
	}

}

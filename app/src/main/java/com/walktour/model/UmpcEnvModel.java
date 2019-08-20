package com.walktour.model;

public class UmpcEnvModel {
	private boolean autoStart;		//自动启动连接服务器
	private String wifiName;		//wifi热点名称
	private String wifiPassword;	//wifi登陆密码
	private int wifiCiphermode;		//wifi加密方式
	
	private String serverIp;		//umpc服务器IP
	private int serverPort;			//umpc服务器端口
	private int connectTime;
	
	private String mobileName;		//手机模块登陆UMPC服务器名称
	private String mobilePassword;	//手机模块登陆UMPC服务器密码
	
	public UmpcEnvModel(){
		autoStart = true;
		
		wifiName = "";
		wifiPassword="";
		
		serverIp = "";
		serverPort= 0;
		connectTime = 10;
		
		mobileName = "";
		mobilePassword ="";
	}
	public synchronized boolean isAutoStart() {
		return autoStart;
	}
	public synchronized void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}
	public synchronized int getConnectTime() {
		return connectTime;
	}
	public synchronized void setConnectTime(int connectTime) {
		this.connectTime = connectTime;
	}
	public synchronized String getServerIp() {
		return serverIp;
	}
	public synchronized void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public synchronized int getServerPort() {
		return serverPort;
	}
	public synchronized void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public synchronized String getWifiName() {
		return wifiName;
	}
	public synchronized void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}
	public synchronized String getWifiPassword() {
		return wifiPassword;
	}
	public synchronized void setWifiPassword(String wifiPassword) {
		this.wifiPassword = wifiPassword;
	}
	public synchronized String getMobileName() {
		return mobileName;
	}
	public synchronized void setMobileName(String mobileName) {
		this.mobileName = mobileName;
	}
	public synchronized String getMobilePassword() {
		return mobilePassword;
	}
	public synchronized void setMobilePassword(String mobilePassword) {
		this.mobilePassword = mobilePassword;
	}
	
	public int getWifiCiphermode() {
		return wifiCiphermode;
	}
	public void setWifiCiphermode(int wifiCiphermode) {
		this.wifiCiphermode = wifiCiphermode;
	}
}

package com.walktour.model;

/**
 * Ftp服务器实体类
 * @author liqihang
 * */
public class FtpServerModel{
	
	/**
	 * 连接模式: 被动模式
	 */
	public final static int CONNECT_MODE_PASSIVE = 1;
	/**
	 * 连接模式：主动模式
	 */
	public final static int CONNECT_MODE_PORT = 2;
	
	private String name;
	private String ip;
	private String port ;
	private String user;
	private String pass;
	private int connect_mode = CONNECT_MODE_PASSIVE;
	
	/**
	 * 是否匿名
	 */
	private boolean anonymous = false;
	
	//Constructor
	public FtpServerModel(String name,String ip,String port ,String user,String pass){
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pass = pass;
	}//end Construct
	
	public FtpServerModel(){
		
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getIp(){
		return this.ip;
	}
	
	public String getPort(){
		return this.port;
	}
	
	public String getLoginUser(){
		return this.user;
	}
	
	public String getLoginPassword(){
		return this.pass;
	}
	
	public void setName(String value){
		this.name = value;
	}
	
	public void setIp(String value){
		this.ip = value;
	}
	
	public void setPort(String value){
		this.port = value;
	}
	
	public void setLoginUser(String value){
		this.user = value;
	}
	
	public void setLoginPassword(String value){
		this.pass = value;
	}

	public int getConnect_mode() {
		return connect_mode;
	}

	public void setConnect_mode(int connect_mode) {
		this.connect_mode = connect_mode;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	
	
}
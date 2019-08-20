package com.walktour.service;

public class FleetManager {
	private FleetManager(){
		
	}
	
	private static FleetManager instance ; 
	
	public static FleetManager getService (){
		if( instance== null ){
			instance = new FleetManager();
		}
		return instance;
	}
	
	private boolean isFleeterAlive = false;

	/**当前是否在和fleet服务器交互数据*/
	public synchronized boolean isFleeterAlive() {
		return isFleeterAlive;
	}

	/**设置当前是否正在和fleet服务器交互数据*/
	public synchronized void setFleeterAlive(boolean isFleeterAlive) {
		this.isFleeterAlive = isFleeterAlive;
	}
	
	
	
}

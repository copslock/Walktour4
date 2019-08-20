package com.walktour.control.config;


public class  ConfigAPN{
	
	private static ConfigAPN _configApn = null;
	private ConfigRoutine configRoutine = null;
	
	private ConfigAPN(){
		configRoutine = ConfigRoutine.getInstance();
	}
	
	public static ConfigAPN getInstance(){
		if(_configApn == null){
			_configApn = new ConfigAPN();
		}
		
		return _configApn;
	}
	
	/**
	 * 返回WAP测试任务的接入点名称,
	 * */
	public String getWapAPN(){
		return configRoutine.getWapAPN();
	}
	
	/**
	 * 返回数据业务的接入点名称
	 * */
	public String getDataAPN(){
		return configRoutine.getDataAPN();
	}
	
	public void setWapAPN(String value){
		configRoutine.setWapAPN(value);
	}
	
	public void setDataAPN(String value){
		configRoutine.setDataAPN(value);
	}	
	
}
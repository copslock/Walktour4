package com.walktour.model;

import java.io.Serializable;

public class HttpPageUrlModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private int enable;		//是否可选择   0为不可选 1为可选
	private String strUrl;  //url文本显示
	/**
	 * getter method
	 * @return the enable
	 */
	
	public int getEnable() {
		return enable;
	}
	/**
	 * setter method
	 * @param enable the enable to set
	 */
	
	public void setEnable(int enable) {
		this.enable = enable;
	}
	/**
	 * getter method
	 * @return the strUrl
	 */
	
	public String getStrUrl() {
		return strUrl;
	}
	/**
	 * setter method
	 * @param strUrl the strUrl to set
	 */
	
	public void setStrUrl(String strUrl) {
		this.strUrl = strUrl;
	}
	/**
	 * getter method
	 * @return the serialversionuid
	 */
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}

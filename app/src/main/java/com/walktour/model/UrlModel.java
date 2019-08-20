package com.walktour.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * 网址对象
 * 
 * @author lianzh
 * */
public class UrlModel implements Serializable ,Cloneable{
	private static final long serialVersionUID = 1L;
	/** 网址 */
	private String url;
	/** 是否可用 */
	private String enable;

	// Constructor
	public UrlModel(String url, String enable) {
		boolean flag = url.toLowerCase(Locale.getDefault()).startsWith("http://")
				|| url.toLowerCase(Locale.getDefault()).startsWith("https://");
		this.url = flag ? url : "http://" + url;
		this.enable = enable;
	}

	public UrlModel() {

	}

	public String getName() {
		return url;
	}

	public void setName(String name) {
		this.url = name;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "UrlModel [name=" + url + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
	
	@Override
	public UrlModel clone() {
		UrlModel urlModel = null;  
        try {  
            urlModel = (UrlModel) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return urlModel; 
	}

}
package com.walktour.gui.analysis.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/***
 * 异常信息Model
 * @author weirong.fan
 *
 */
public class AnalysisModel implements Serializable  {
	private static final long serialVersionUID = 1L;
	/** 主叫类型 **/
	public static final int TYPE_MO = 0;
	/** 被叫类型 **/
	public static final int TYPE_MT = 1;
	/** FTP download类型 **/
	public static final int TYPE_FTPDOWNLOAD = 2;
	/**发生异常时间**/
	private String exceptionTime;
	/**发生异常业务类型**/
	private int exceptionType=TYPE_MO;
	/**异常发生的原因码**/
	private float exceptionCode;
	/**异常发生的子原因码**/
	private float exceptionSubCode;
	/**发生异常原因**/
	private String exceptionInfo;
	/**精度**/
	private double lat;
	/**纬度**/
	private double lon; 
	/**ddib文件**/
	private String ddibFile;
	/**回放采样点，开始采样点*/
	private int startIndex;
	/**回放采样点，结束采样点*/
	private int endIndex;
	
	private Map<String,String> otherInfo=new HashMap<String,String>();
	public String getExceptionTime() {
		return exceptionTime;
	}
	public void setExceptionTime(String exceptionTime) {
		this.exceptionTime = exceptionTime;
	}
	public int getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(int exceptionType) {
		this.exceptionType = exceptionType;
	}
	public String getExceptionInfo() {
		return exceptionInfo;
	}
	public void setExceptionInfo(String exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public float getExceptionCode() {
		return exceptionCode;
	}
	public void setExceptionCode(float exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
	public String getDdibFile() {
		return ddibFile;
	}
	public void setDdibFile(String ddibFile) {
		this.ddibFile = ddibFile;
	}
	public float getExceptionSubCode() {
		return exceptionSubCode;
	}
	public void setExceptionSubCode(float exceptionSubCode) {
		this.exceptionSubCode = exceptionSubCode;
	}
	public Map<String, String> getOtherInfo() {
		return otherInfo;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	 
}

package com.walktour.service.phoneinfo.logcat;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日志信息对象
 * 
 * @author jianchao.wang
 *
 */
public class LogInfoBean {

	// private String date;
	public final static int EXCEPTION_TAG = 0;
	public final static int ANR_TAP = 1;
	public static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS",
			Locale.getDefault());
	private int tag;
	private long logTime;
	private long pid;
	private String pkgName = null;
	private String msg;
	private int id;
	private CellBean cellBean;

	public void setLogTime(long time) {
		this.logTime = time;
	}

	// public String getStrTime() {
	// return logDateFormat.format(time);
	// }
	public long getLogTime() {
		return logTime;
	}

	public void setPid(long process) {
		this.pid = process;
	}

	public long getPid() {
		return pid;
	}

	public void setMsg(String info) {
		this.msg = info;
	}

	public String getMsg() {
		return msg;
	}

	public void setPkgName(String appName) {
		this.pkgName = appName;
	}

	public String getPkgName() {
		return pkgName;
	}

	/*
	 * public void setDate(String date) { this.date = date; } public String
	 * getDate() { return date; }
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getTag() {
		return tag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCellBean(CellBean cellBean) {
		this.cellBean = cellBean;
	}

	public CellBean getCellBean() {
		return cellBean;
	}
}

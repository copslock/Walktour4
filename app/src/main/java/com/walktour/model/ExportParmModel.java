package com.walktour.model;

/**
 * 导出参数model
 * @author zhihui.lian
 */
public class ExportParmModel {
	
	private int enable = 0;
	private String showNmae;
	private String id;
	private int netType;

	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getShowNmae() {
		return showNmae;
	}

	public void setShowNmae(String showNmae) {
		this.showNmae = showNmae;
	}

	public int getNetType() {
		return netType;
	}

	public void setNetType(int netType) {
		this.netType = netType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}

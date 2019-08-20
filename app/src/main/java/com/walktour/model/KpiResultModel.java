package com.walktour.model;

public class KpiResultModel {
	/**
	 * 指标实际值
	 */
	String kpiRealValue 	= "";
	
	/**
	 * 是否达标
	 */
	boolean isScratch	= false;
	
	
	public String getShowKpiName() {
		return showKpiName;
	}
	public void setShowKpiName(String showKpiName) {
		this.showKpiName = showKpiName;
	}
	public String getActualValue() {
		return ActualValue;
	}
	public void setActualValue(String actualValue) {
		ActualValue = actualValue;
	}

	/**
	 * 业务指标
	 */
	String showKpiName = "";
	
	/**
	 * 阀值
	 */
	String ActualValue = "";
	
	public String getKpiRealValue() {
		return kpiRealValue;
	}
	public void setKpiRealValue(String kpiRealValue) {
		this.kpiRealValue = kpiRealValue;
	}
	public boolean isScratch() {
		return isScratch;
	}
	public void setScratch(boolean isScratch) {
		this.isScratch = isScratch;
	}
	
	public String getGoNoGo(){
		return isScratch ? "Go" : "No-Go";
	}
}

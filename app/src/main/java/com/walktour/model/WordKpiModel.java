package com.walktour.model;

/**
 * word kpi model
 * @author zhihui.lian
 */
public class WordKpiModel {
	
	private String 	kpiShowName = "";
	private String 	molecule 	= "";
	private String 	denominator = "";
	private String 	units 		= "";
	private float 	scale 		= 1f;
	private int	 	paramType 	= 1;
	private String 	markKey 	= "";
	
	public String getKpiShowName() {
		return kpiShowName;
	}
	public void setKpiShowName(String kpiShowName) {
		this.kpiShowName = kpiShowName;
	}
	/**
	 * 获得放比例
	 * @return
	 */
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	/**
	 * 获得指标分子,参数关键字，单一指标时分母无值
	 * 
	 * @return
	 */
	public String getMolecule() {
		return molecule;
	}
	public void setMolecule(String molecule) {
		this.molecule = molecule;
	}
	
	/**
	 * 获得指标分母，单一指标时该值为空
	 * @param molecule
	 */
	public String getDenominator() {
		return denominator;
	}
	public void setDenominator(String denominator) {
		this.denominator = denominator;
	}
	/**
	 * 获得单位
	 * @return
	 */
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	/**
	 * 参数类型 1: 业务类型的指标统计; 2 参数类型最大最小值平均值参数
	 * @return
	 */
	public int getParamType() {
		return paramType;
	}
	public void setParamType(int paramType) {
		this.paramType = paramType;
	}
	
	public String getMarkKey() {
		return markKey;
	}
	public void setMarkKey(String markKey) {
		this.markKey = markKey;
	}

}

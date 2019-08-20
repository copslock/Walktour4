package com.walktour.model;

/**
 * kpi设置model
 * @author zhihui.lian
 */
public class KpiSettingModel {
	
	private int enable = 0;
	private String kpiKey = "";
	private String kpiShowName = "";
	private String operator = "";
	private float value = 0;
	private float scale = 0;
	private String molecule ="";
	private String denominator = "";
	private String units = "";
	private String groupby = "";
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	public String getKpiKey() {
		return kpiKey;
	}
	public void setKpiKey(String kpiKey) {
		this.kpiKey = kpiKey;
	}
	public String getKpiShowName() {
		return kpiShowName;
	}
	public void setKpiShowName(String kpiShowName) {
		this.kpiShowName = kpiShowName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public String getMolecule() {
		return molecule;
	}
	public void setMolecule(String molecule) {
		this.molecule = molecule;
	}
	public String getDenominator() {
		return denominator;
	}
	public void setDenominator(String denominator) {
		this.denominator = denominator;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getGroupby() {
		return groupby;
	}
	public void setGroupby(String groupby) {
		this.groupby = groupby;
	}
	
	
	
	
	
}

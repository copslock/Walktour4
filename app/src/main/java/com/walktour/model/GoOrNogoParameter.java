package com.walktour.model;

/**
 * GoOrNogo相关实体
 * @author msi
 *
 */
public class GoOrNogoParameter{

	private String name = "";
	private String alias = "";
	private String condiction = "";
	
	private String business = "";    //业务名称
	private String showName = "";    //显示名字
	
	
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getCondiction() {
		return condiction;
	}
	public void setCondiction(String condiction) {
		this.condiction = condiction;
	}
	
}

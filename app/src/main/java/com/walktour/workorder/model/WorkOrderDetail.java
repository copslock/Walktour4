package com.walktour.workorder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单详细模型，工单列表中包含多个工单详细
 * Author: ZhengLei
 *   Date: 2013-6-7 上午10:50:56
 */
public class WorkOrderDetail implements Serializable{
	private static final long serialVersionUID = -7060210544600464485L; 
	private int workId;
	private String workName;
	private int workArea;
	private int workType;
	private int projectId;
	private String projectName;
	private String planEndTime = "";
	private String senderAccount;
	private int provinceId;
	private int cityId;
	public static final int INDOOR = 1;
	public static final int OUTDOOR = 2;
	private int areaId;
	private String testSite;
	private String testBuilding;
	private String address;
	private int siteSum;
	private int buildingSum;
	private int netType;
	private int isReceived;
	
	private List<WorkSubItem> workSubItems;

	public WorkOrderDetail() {
		super();
		workSubItems = new ArrayList<WorkSubItem>();
	}

	public int getWorkId() {
		return workId;
	}

	public void setWorkId(int workId) {
		this.workId = workId;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public int getWorkArea() {
		return workArea;
	}

	public void setWorkArea(int workArea) {
		this.workArea = workArea;
	}

	public int getWorkType() {
		return workType;
	}

	public void setWorkType(int workType) {
		this.workType = workType;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPlanEndTime() {
		return planEndTime;
	}

	public void setPlanEndTime(String planEndTime) {
		this.planEndTime = planEndTime;
	}

	public String getSenderAccount() {
		return senderAccount;
	}

	public void setSenderAccount(String senderAccount) {
		this.senderAccount = senderAccount;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getTestSite() {
		return testSite;
	}

	public void setTestSite(String testSite) {
		this.testSite = testSite;
	}

	public String getTestBuilding() {
		return testBuilding;
	}

	public void setTestBuilding(String testBuilding) {
		this.testBuilding = testBuilding;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getSiteSum() {
		return siteSum;
	}

	public void setSiteSum(int siteSum) {
		this.siteSum = siteSum;
	}

	public int getBuildingSum() {
		return buildingSum;
	}

	public void setBuildingSum(int buildingSum) {
		this.buildingSum = buildingSum;
	}

	public int getNetType() {
		return netType;
	}

	public void setNetType(int netType) {
		this.netType = netType;
	}

	public int getIsReceived() {
		return isReceived;
	}

	public void setIsReceived(int isReceived) {
		this.isReceived = isReceived;
	}

	public List<WorkSubItem> getWorkSubItems() {
		return workSubItems;
	}

	public void setWorkSubItems(List<WorkSubItem> workSubItems) {
		this.workSubItems = workSubItems;
	}

	@Override
	public String toString() {
		return "WorkOrderDetail [workId=" + workId + ", workName=" + workName
				+ ", workArea=" + workArea + ", workType=" + workType
				+ ", projectId=" + projectId + ", projectName=" + projectName
				+ ", planEndTime=" + planEndTime + ", senderAccount="
				+ senderAccount + ", provinceId=" + provinceId + ", cityId="
				+ cityId + ", areaId=" + areaId + ", testSite=" + testSite
				+ ", testBuilding=" + testBuilding + ", address=" + address
				+ ", siteSum=" + siteSum + ", buildingSum=" + buildingSum
				+ ", netType=" + netType + ", isReceived=" + isReceived
				+ ", workSubItems=" + workSubItems + "]";
	}

}

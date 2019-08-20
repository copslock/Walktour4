package com.walktour.gui.workorder.ah.model;

/**
 * 安徽电信工单信息点对象
 * 
 * @author jianchao.wang 2014年6月13日
 */
public class WorkOrderPoint {
	/** 地址 */
	private String address = "";
	/** 城市 */
	private String city = "";
	/** 名称 */
	private String name = "";
	/** 编号 */
	private String pointID = "";
	/** 网络编号 */
	private String region = "";
	/** 测试业务 */
	private String testTask = "";
	/** 信息点类型 */
	private String type = "";
	/** 所属工单 */
	private WorkOrder order;
	/** 经度 */
	private double longitude;
	/** 纬度 */
	private double latitude;
	/** 当前信息点是否为手动新增 */
	private boolean isCreate = false;
	/** 当前信息点是否已关闭 */
	private boolean isClosed = false;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPointID() {
		return pointID;
	}

	public void setPointID(String pointID) {
		this.pointID = pointID;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getTestTask() {
		return testTask;
	}

	public void setTestTask(String testTask) {
		this.testTask = testTask;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		else if (o instanceof WorkOrderPoint) {
			WorkOrderPoint point = (WorkOrderPoint) o;
			if (point.getPointID().equals(this.pointID))
				return true;
			return false;
		}
		return false;
	}

	public WorkOrder getOrder() {
		return order;
	}

	public void setOrder(WorkOrder order) {
		this.order = order;
	}

	public boolean isCreate() {
		return isCreate;
	}

	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
}

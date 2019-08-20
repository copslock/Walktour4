package com.walktour.gui.workorder.ah.model;

import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安徽电信工单对象
 * 
 * @author jianchao.wang 2014年6月13日
 */
public class WorkOrder {
	/** 工单编号 */
	private String workItemCode;
	/** 工单描述 */
	private String description;
	/** 工单名称 */
	private String name;
	/** 计划开始时间 */
	private String planToStart = "";
	/** 计划结束时间 */
	private String planToFinish = "";
	/** 工单ID，标识平台数据库中的工单记录ID */
	private String workItemID;
	/** 工单信息点列表 */
	private List<WorkOrderPoint> pointList = new ArrayList<WorkOrderPoint>();
	/** 工单关联任务列表 */
	private List<TaskModel> taskList = new ArrayList<TaskModel>();
	/** 工单关联任务xml字符串映射<任务名称，xml字符串> */
	private Map<String, String> taskXmlMap = new HashMap<String, String>();

	public String getWorkItemCode() {
		return workItemCode;
	}

	public void setWorkItemCode(String workItemCode) {
		this.workItemCode = workItemCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<WorkOrderPoint> getPointList() {
		return pointList;
	}

	public String getWorkItemID() {
		return workItemID;
	}

	public void setWorkItemID(String workItemID) {
		this.workItemID = workItemID;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		else if (o instanceof WorkOrder) {
			WorkOrder order = (WorkOrder) o;
			if (order.getWorkItemCode().equals(this.workItemCode))
				return true;
			return false;
		}
		return false;
	}

	public String getPlanToStart() {
		return planToStart;
	}

	public void setPlanToStart(String planToStart) {
		this.planToStart = planToStart;
	}

	public String getPlanToFinish() {
		return planToFinish;
	}

	public void setPlanToFinish(String planToFinish) {
		this.planToFinish = planToFinish;
	}

	public List<TaskModel> getTaskList() {
		return taskList;
	}

	/**
	 * 判断当前工单是否关闭，通过判断是否所有的下载信息点都已经关闭
	 * 
	 * @return
	 */
	public boolean isClosed() {
		int count = 0;
		int closes = 0;
		for (WorkOrderPoint point : this.pointList) {
			if (!point.isCreate()) {
				count++;
				if (point.isClosed())
					closes++;
			}
		}
		if (count > 0 && count == closes)
			return true;
		return false;
	}

	public Map<String, String> getTaskXmlMap() {
		return taskXmlMap;
	}

}

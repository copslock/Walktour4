package com.walktour.gui.workorder.hw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试子计划
 * 
 * @author jianchao.wang
 *
 */
public class TestSchema {
	/** 子计划ID */
	private long id;
	/** 子计划名称 */
	private String name;
	/** 父计划 */
	private TestPlan plan;
	/** 测试任务组列表 */
	private List<TestTaskGroup> taskGroupList = new ArrayList<TestTaskGroup>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TestTaskGroup> getTaskGroupList() {
		return taskGroupList;
	}

	public TestPlan getPlan() {
		return plan;
	}

	public void setPlan(TestPlan plan) {
		this.plan = plan;
	}
}

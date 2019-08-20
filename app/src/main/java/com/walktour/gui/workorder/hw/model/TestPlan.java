package com.walktour.gui.workorder.hw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试计划
 * 
 * @author jianchao.wang
 *
 */
public class TestPlan {
	/** 计划ID */
	private long id;
	/** 计划版本 */
	private String version;
	/** 时区 */
	private String timeZone;
	/** 测试子计划列表 */
	private List<TestSchema> schemaList = new ArrayList<TestSchema>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public List<TestSchema> getSchemaList() {
		return schemaList;
	}
}

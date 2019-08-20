package com.dinglicom.data.model;

public class RecordTaskType implements RecordBase{
	public int task_type_id;
	public String record_id;
	public int task_type;
	protected String task_type_str;
	public String task_info_id;
	public String test_plan;
	public long excute_time;
	
	public String getTask_type_str() {
		return task_type_str;
	}

	public void setTask_type_str(String task_type_str) {
		this.task_type_str = task_type_str;
	}
	
}

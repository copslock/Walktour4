package com.dinglicom.data.model;

public class RecordAbnormal implements RecordBase{
	public int abnormal_id;
	public String record_id;
	public int abnormal_type;
	protected String abnormal_type_str;
	public long abnormal_point;
	public long abnormal_time;
	
	public String getAbnormal_type_str() {
		return this.abnormal_type_str;
	}

	public void setAbnormal_type_str(String abnormal_type_str) {
		this.abnormal_type_str = abnormal_type_str;
	}
	
}

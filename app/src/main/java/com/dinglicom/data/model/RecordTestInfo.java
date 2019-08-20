package com.dinglicom.data.model;

public class RecordTestInfo implements RecordBase {
	protected int test_info_id;
	public String task_no;
	public String key_info;
	public String key_value;
	public RecordTestInfo(){
	}
	public RecordTestInfo(String key,String value){
		key_info 	= key;
		key_value	= value;
	}
	public RecordTestInfo(String no,String key,String value){
		task_no 	= no;
		key_info 	= key;
		key_value	= value;
	}
}

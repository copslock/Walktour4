package com.dinglicom.data.model;

import java.util.ArrayList;

/**
 * 数据管理测试文件对象
 * 存储每个文件的相关明细信息
 * 
 * 注意思,属性范围描棕信息
 * public是直接从数据库获取允许访问读写的
 * protected 是指从外表中直接获取值,但插入数据库时不需要插入的属性
 * private 是私有对象属性,该属性值不直接从数据库获取,而是另外往里赋值
 * 
 * @author Tangwq
 *
 */
public class TestRecord implements RecordBase{
	protected int 	totalid;			//自增主键,该值用于入库后与之前的统计表对应
	public String 	record_id;
	public int 		type_scene;
	protected String type_scene_str;
	public int 		test_type;
	protected String test_type_str;
	public String 	file_name;	
	public long	 	time_create;
	public long 	time_end;
	public int 		file_split_id;
	public String 	node_id;
	public String 	task_no;
	public String 	port_id;
	public int 		test_index;
	protected String node_id_str;
	protected int 	task_no_num;
	public String group_info;
	public int go_or_nogo;
	
	private ArrayList<RecordDetail>	recordDetails 	= new ArrayList<RecordDetail>();
	private ArrayList<RecordTaskType>	recordTaskTypes	= new ArrayList<RecordTaskType>();
	private ArrayList<RecordAbnormal>	recordAbnormals	= new ArrayList<RecordAbnormal>();
	private ArrayList<RecordNetType>	recordNetTypes	= new ArrayList<RecordNetType>();
	private ArrayList<RecordTestInfo> 	recordTestInfo	= new ArrayList<RecordTestInfo>();
	
	/**
	 * 清空对象明细内容
	 */
	public void cleanDetail(){
		recordDetails.clear();
		recordTaskTypes.clear();
		recordAbnormals.clear();
		recordNetTypes.clear();
		recordTestInfo.clear();
	}

	public int getTotalId(){
		return totalid;
	}
	public String getType_scene_str() {
		return type_scene_str;
	}
	public String getTest_type_str() {
		return test_type_str;
	}
	public String getNode_id_str() {
		return this.node_id_str;
	}
	public int getTaskNoNum(){
		return task_no_num;
	}
	public ArrayList<RecordDetail> getRecordDetails() {
		return recordDetails;
	}
	public void setRecordDetails(ArrayList<RecordDetail> recordDetails) {
		this.recordDetails = recordDetails;
	}
	
	public void addRecordDetail(RecordDetail detail){
		this.recordDetails.add(detail);
	}
	
	public ArrayList<RecordTaskType> getRecordTaskTypes() {
		return recordTaskTypes;
	}
	public void setRecordTaskTypes(ArrayList<RecordTaskType> recordTaskTypes) {
		this.recordTaskTypes = recordTaskTypes;
	}
	public void addRecordTaskType(RecordTaskType type){
		this.recordTaskTypes.add(type);
	}
	public ArrayList<RecordAbnormal> getRecordAbnormals() {
		return recordAbnormals;
	}
	public void setRecordAbnormals(ArrayList<RecordAbnormal> recordAbnormals) {
		this.recordAbnormals = recordAbnormals;
	}
	public void addRecordAbnormal(RecordAbnormal abnormal){
		this.recordAbnormals.add(abnormal);
	}
	public ArrayList<RecordNetType> getRecordNetTypes() {
		return recordNetTypes;
	}
	public void setRecordNetTypes(ArrayList<RecordNetType> recordNetTypes) {
		this.recordNetTypes = recordNetTypes;
	}
	public void addRecordNetType(RecordNetType netType){
		this.recordNetTypes.add(netType);
	}
	public ArrayList<RecordTestInfo> getRecordTestInfo() {
		return recordTestInfo;
	}
	public void setRecordTestInfo(ArrayList<RecordTestInfo> recordTestInfo) {
		this.recordTestInfo = recordTestInfo;
	}
	
	public void addRecordTestInfo(RecordTestInfo info){
		this.recordTestInfo.add(info);
	}
	public String getRecordTaskTypeName() {
		String result = "";
		for (int i = 0; i < this.recordTaskTypes.size(); i++) {
			result += recordTaskTypes.get(i).getTask_type_str() + ",";
		}
		if (result.contains(",")) {
			result = result.substring(0, result.lastIndexOf(","));
		}
		return result;
	}
}

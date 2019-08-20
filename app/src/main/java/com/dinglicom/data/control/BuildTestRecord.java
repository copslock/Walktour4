package com.dinglicom.data.control;

import com.dinglicom.data.model.RecordAbnormal;
import com.dinglicom.data.model.RecordBase;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordNetType;
import com.dinglicom.data.model.RecordTaskType;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileOperater;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * 构建测试信息类
 * @author Tangwq
 *
 */
public class BuildTestRecord {
	private final String TAG = "BuildTestRecord"; 
	
	private boolean isBuildRecord = false;	//是否构建过测试信息
	
	private TestRecord testRecord = new TestRecord();
	private HashMap<String,RecordDetail>	recordDetails 	= new HashMap<String, RecordDetail>();		//key file_type
	private HashMap<String,RecordTaskType>	recordTaskTypes	= new HashMap<String, RecordTaskType>();	//key task_info_id 测试模版中的任务名为主键,区分同个业务类型如FTP有两个测试计划的情况
	private HashMap<String,RecordAbnormal>	recordAbnormals	= new HashMap<String, RecordAbnormal>();	//key abnormal_time	以异常发生时间为主键
	private HashMap<String,RecordNetType>	recordNetTypes	= new HashMap<String, RecordNetType>();		//key net_type 网络类型为主键
	private HashMap<String,RecordTestInfo> recordTestInfo	= new HashMap<String, RecordTestInfo>();	//key key_info 关键键值对
	
	/**
	 * 设置测试记录主表信息
	 * 
	 * @param field	表属性名称
	 * @param value	属性值
	 */
	public void setTestRecordMsg(String field,Object value){
		setFieldValue(TestRecord.class,testRecord,field,value);
	}
	
	/**
	 * 设置生成明细文件相关信息
	 * 
	 * @param key	file_type 关键类型:当前文件类型值如RCU,DDIB等
	 * @param field	表属性名称
	 * @param value	属性值
	 */
	public void setRecordDetailMsg(String key,String field,Object value){
		RecordDetail model = null;
		if(recordDetails.containsKey(key)){
			model = recordDetails.get(key);
		}else{
			model = new RecordDetail();
			model.record_id = testRecord.record_id;
			recordDetails.put(key, model);
		}
		
		setFieldValue(RecordDetail.class,model,field,value);
	}
	
	/**
	 * 设置测试任务类型相关信息
	 * @param key	task_info_id 测试模版中的任务名为主键,区分同个业务类型如FTP有两个测试计划的情况
	 * @param field	表属性名称
	 * @param value	属性值
	 */
	public void setRecordTaskTypeMsg(String key,String field,Object value){
		RecordTaskType model = null;
		if(recordTaskTypes.containsKey(key)){
			model = recordTaskTypes.get(key);
		}else{
			model = new RecordTaskType();
			model.record_id = testRecord.record_id;
			recordTaskTypes.put(key, model);
		}
		
		setFieldValue(RecordTaskType.class,model,field,value);
	}
	
	/**
	 * 
	 * @param key	abnormal_time	以异常发生时间为主键
	 * @param field	表属性名称
	 * @param value	属性值
	 */
	public void setRecordAbnormalMsg(String key,String field,Object value){
		RecordAbnormal model = null;
		if(recordAbnormals.containsKey(key)){
			model = recordAbnormals.get(key);
		}else{
			model = new RecordAbnormal();
			model.record_id = testRecord.record_id;
			recordAbnormals.put(key, model);
		}
		
		setFieldValue(RecordAbnormal.class,model,field,value);
	}
	
	/**
	 *
	 * @param field	表属性名称
	 * @param value	属性值
	 */
	public void setRecordNetTypeMsg(String field,Object value){
		if(!recordNetTypes.containsKey(value.toString())){
			RecordNetType model = new RecordNetType();
			model.record_id = testRecord.record_id;
			model.network_time = System.currentTimeMillis();
			
			recordNetTypes.put(value.toString(), model);
			
			setFieldValue(RecordNetType.class,model,field,value);
		}
	}
	

	public void setRecordTestInfoMsg(HashMap<String, RecordTestInfo> extInfo){
		recordTestInfo = extInfo;
	}
	
	/**
	 * 给指定对象属性付值
	 * @param cls
	 * @param object
	 * @param fieldName
	 * @param value
	 */
	private void setFieldValue(Class<?> cls, RecordBase object,String fieldName,Object value){
		try{
			Field field = cls.getField(fieldName);
			field.setAccessible(true);
			field.set(object, value);
			
		}catch(Exception e){
			LogUtil.w(TAG,"setFieldValue" + fieldName + ":" + value,e);
		}
	}
	
	/**
	 * 获得测试记录当前文件列表明细信息
	 * @return
	 */
	public HashMap<String, RecordDetail> getRecordDetails(){
		return recordDetails;
	}
	
	/**
	 * 获取对象构建结果
	 * @return
	 */
	public TestRecord getTestRecord(){
		return getTestRecord(false);
	}
	
	/**
	 * 获取对象构建结果
	 * @param isReBuild 是否重新构建
	 * @return
	 */
	public TestRecord getTestRecord(boolean isReBuild){
		if(!isBuildRecord || isReBuild){
			isBuildRecord = true;
			
			//如果重新构建当前类,需要清空之前的明细信息
			if(isReBuild){
				testRecord.cleanDetail();
			}
			
			for(RecordDetail detail : recordDetails.values()){
				testRecord.addRecordDetail(detail);
			}
			for(RecordTaskType taskType : recordTaskTypes.values()){
				testRecord.addRecordTaskType(taskType);
			}
			for(RecordAbnormal abnormal : recordAbnormals.values()){
				testRecord.addRecordAbnormal(abnormal);
			}
			for(RecordNetType netType : recordNetTypes.values()){
				testRecord.addRecordNetType(netType);
			}
			for(RecordTestInfo testInfo : recordTestInfo.values()){
				testInfo.task_no = testRecord.task_no;
				testRecord.addRecordTestInfo(testInfo);
			}
		}
		
		return testRecord;
	}

	
	/**
	 * 重命名、挪动与rcu相关的文件
	 * @return 新的ddib路径
	 */
	public String moveRelativeFiles(boolean dontSaveFile,String filePath,String fileNewName){
		//如果当前为不保存文件,则删除,否则改为新名字,如果路径与伟入的路径不一致,调用文件操作类操作
		for(RecordDetail detail : recordDetails.values()){
			File file = new File(detail.file_path + detail.file_name);
			if(file.exists()){
				//如果当前为不保存文件
				if(dontSaveFile){
					recordDetails.remove(String.valueOf(detail.file_type));
					file.delete();
				}else{
					detail.file_size = file.length();
					FileType fileType = FileType.getFileType(detail.file_type);
					
					//如果当前为ddib文件则使用FileOperater执行move动作,因为file.rename()动作无效
					if(fileType == FileType.DDIB){
						FileOperater operater = new FileOperater();
						operater.move(detail.file_path + detail.file_name, filePath + fileNewName + fileType.getExtendName());
					}else{
						File newPath = new File(filePath + fileNewName + fileType.getExtendName());
						file.renameTo(newPath);
					}
					detail.file_path = filePath;
					detail.file_name = fileNewName + fileType.getExtendName();
				}
			}else{
				//如果真实文件不存在,删除当前记录
				LogUtil.w(TAG,"--del:" + (detail.file_path + detail.file_name));
				recordDetails.remove(String.valueOf(detail.file_type));
			}
		}
		
		return filePath + fileNewName + FileType.DDIB.getExtendName(); //udesFilePath;
	}
	
}

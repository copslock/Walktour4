package com.dinglicom.data.control;

import android.content.Context;
import android.os.Environment;

import com.dinglicom.data.control.DataTableStruct.RecordDetailEnum;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileOperater;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.instance.FileDB;
import com.walktour.gui.setting.SysBuildingManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * 同步目录,文件到数据库
 * 组织遍历所有的数据存储目录,包括楼层目录,将里头的文件与数据库进行同步
 * 
 * @author Tangwq
 *
 */
public class SyncFileToTable {	
	private final String tag = "SyncFileToTable";
	private Context mContext = null;
	
	public SyncFileToTable(Context context){
		mContext = context;
	}
	
	public void syncFilesToTable(){
		LogUtil.w(tag,"--syncFilesToTable start--");
		
		FileDB fileDB = FileDB.getInstance(mContext);
		
		/**
		 * 1 将应用内存目录中数据生在怕异常结束临时文件移动到正常工作文件夹中
		 */
		moveDataSetBackFile();
		
		/**
		 * 2 从数据库中获得全部数据对象列表
		 */
		ArrayList<TestRecord> recordList = fileDB.buildTestRecordList(null);
		
		//定义需要添加到数据库中的列表
		HashMap<String, BuildTestRecord> addRecord = new HashMap<String, BuildTestRecord>();
		
		//用于存储遍历列表是的文件名,后续目录文件同步时需要用到
		HashMap<String, TestRecord> recordListName = new HashMap<String, TestRecord>();
		
		/**
		 * 3 遍历对象列表,如果数据库中指定的文件不存在,当前为挂载模式且路径中包含/sdcard/的忽略 ;
		 * 遍历的过程中将文件名存到 recordListName 列表中,
		 */
		while(!recordList.isEmpty()){
			TestRecord record = recordList.remove(0);
			//@SuppressWarnings("unchecked")
			//ArrayList<RecordDetail> detailClone = (ArrayList<RecordDetail>)record.getRecordDetails().clone();
			int delCount = 0;
			
			for(RecordDetail detail : record.getRecordDetails()){
				File file = new File(detail.file_path + detail.file_name);
				//当文件不存在时,当前sdcard为加载成功,即可用状态 或者如果文件路径中不包含/sdcard/,则删除数据库中相关记录
				if(!file.exists() && 
						(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) 
								|| detail.file_path.indexOf(Environment.getExternalStorageDirectory().getPath()) < 0)){
					fileDB.deleteRecord(detail);
					//detailClone.remove(detail);
					
					delCount++;
				}
			}
			
			/**
			 * 如果所有文件都不存在,则整个记录删
			 * 
			 * 直接用删除记数值比较,如果计数值与明细个数相等,则删除整个记录
			 */
			//if(detailClone.size() == 0){
			if(delCount == record.getRecordDetails().size()){
				fileDB.deleteFormTable(record);
			}else {
				//record.setRecordDetails(detailClone);
				recordListName.put(record.file_name,record);
			}
		}
		
		/**
		 * 4 调用公共方法,给出指定路径,路径中的文件如果在对象列表中不存在,则创建一对象,存在于临时列表中,最后将该列表入库
		 */
		//@ TODO 浩华将楼层目录结构放到此处同步,添加目录到数据库的同时,调用文件同步接口
//		syncDictoryFileToDB("/sdcard/walktour/data/task", recordListName, addRecord);
		List<String> dirPath = SysBuildingManager.getInstance(mContext).getFolderList(mContext);
		for (String path : dirPath) {
			syncDictoryFileToDB(path, recordListName, addRecord);
		}
		
		
		/**
		 * 5 将不在数据库中的数据入库
		 */
		Iterator<?> iterator = addRecord.entrySet().iterator();
		while(iterator.hasNext()){
			@SuppressWarnings("unchecked")
			Entry<String,BuildTestRecord> build = (Entry<String,BuildTestRecord>)iterator.next();
			TestRecord record = build.getValue().getTestRecord();
			
			/*
			 * 用于保存当前记录是否为有效文件,如果为有效文件才执行入库动作,
			 * 当同步文件存在DDIB,RCU,DTLOG,DCF中的一种时,为有效记录
			 */
			boolean enableFile = false;
			for(RecordDetail detail : record.getRecordDetails()){
				if(detail.file_type == FileType.DDIB.getFileTypeId()
					|| detail.file_type == FileType.ORGRCU.getFileTypeId()
					|| detail.file_type == FileType.DTLOG.getFileTypeId()
					|| detail.file_type == FileType.DCF.getFileTypeId()){
					enableFile = true;
					break;
				}
			}
			
			if(enableFile){
				fileDB.syncTestRecord(record);
			}
		}
		
		//DTLOG生成现为实时生成,不再需要做异常特殊转换
		LogUtil.w(tag,"--syncFilesToTable end--");
	}
	
	/**
	 * 将测试过程中异常结束的数据集生文件移到正常工作目录中
	 * 如果文件名后面带着_Port2,直接去掉
	 */
	private void moveDataSetBackFile(){
		try{
			String ddibPath = ConfigRoutine.getInstance().getStorgePathDdib();
			String taskPath = ConfigRoutine.getInstance().getStorgePathTask();
			
			File[] tempDDIBFiles =new File(ddibPath).listFiles();
			if(tempDDIBFiles != null && tempDDIBFiles.length > 0){
				FileOperater fileOperate = new FileOperater();
				
				for(File file : tempDDIBFiles){
					//当不是以 tempfile 开头的文件时，表示当前需要移动
					if(!file.getName().startsWith("tempfile_")){
						String renamePath = String.format("%s%s", taskPath,file.getName().replaceAll("_Port2", ""));
						LogUtil.w(tag, "--move backup file:" + renamePath);
						
						fileOperate.move(file.getPath(), renamePath);
					}
				}
			}
		}catch(Exception e){
			LogUtil.w(tag, "moveDataSetBackFile",e);
		}
	}
	
	/**
	 * 指定目录下的文件同步到数据库中
	 * 
	 * @param filePath		指定目录
	 * @param recordList	数据库中存在的记录
	 * @param addRecord		需要添加到数据库中的对象
	 */
	private void syncDictoryFileToDB(String filePath,HashMap<String, TestRecord> recordList,HashMap<String, BuildTestRecord> addRecord){
		try{
			//用于存放当前目录下的task值,相同前缀下的taskno值一致
			HashMap<String, String> taskNoList = new HashMap<>();
			
			File[] dicFiles = new File(filePath).listFiles();
			int index=0;//防止taskNo重复
			for(File file : dicFiles){
				if(file.isFile()){
					if(file.getName().contains("_Port2.")){
						File newFile = new File(file.getAbsolutePath().replaceAll("_Port2.","."));
						file.renameTo(newFile);
						file = newFile;
					}
					String name = file.getName().substring(0, file.getName().indexOf("."));
					//文件名带(表示当前文件有分割
					String tnName = name.contains("(") ? name.substring(0, name.indexOf("(")) : name;
					String taskNo = UtilsMethod.sdfhmsss.format(System.currentTimeMillis()+(++index));
					if(taskNoList.containsKey(tnName)){
						taskNo = taskNoList.get(tnName);
					}else{
						taskNoList.put(tnName, taskNo);
					}
					
					//如果当前文件名,没有存在于数据库记录中,那么当前文件需要同步到数据库中
					if(!recordList.containsKey(name)){
						BuildTestRecord buildRecord = null;
						boolean isNew = false;
						if(addRecord.containsKey(name)){
							buildRecord = addRecord.get(name);
						}else{
							buildRecord = new BuildTestRecord();
							isNew = true;
						}
						
						buildTestRecord(file,buildRecord,isNew,taskNo);
						addRecord.put(name, buildRecord);
					}
				}
			}
		}catch(Exception e){
			LogUtil.w(tag,"syncDictoryFileToDB",e);
		}
	}
	
	/**
	 * 根据传入的文件,及构建文件对象类,构建一新的文件构建对象
	 * @param file
	 */
	private void buildTestRecord(File file,BuildTestRecord testRecord,boolean isNew,String taskNo){
		String name 	= file.getName().substring(0, file.getName().indexOf("."));
		String extName	= file.getName().substring(file.getName().indexOf(".") + 1);
		FileType fileType = FileType.getFileTypeByName(extName);
		
		if(isNew){
			testRecord.setTestRecordMsg(TestRecordEnum.record_id.name(), UtilsMethod.getUUID());
			testRecord.setTestRecordMsg(TestRecordEnum.type_scene.name(), SceneType.Manual.getSceneTypeId());
			testRecord.setTestRecordMsg(TestRecordEnum.test_type.name(), 
					(name.contains("DT")) ? TestType.DT.getTestTypeId() : TestType.CQT.getTestTypeId());
			testRecord.setTestRecordMsg(TestRecordEnum.file_name.name(), name);
			testRecord.setTestRecordMsg(TestRecordEnum.time_create.name(), file.lastModified());
			testRecord.setTestRecordMsg(TestRecordEnum.time_end.name(), file.lastModified());
			testRecord.setTestRecordMsg(TestRecordEnum.task_no.name(), taskNo);
			testRecord.setTestRecordMsg(TestRecordEnum.file_split_id.name(), getSplitId(name));
		}
		
		testRecord.setRecordDetailMsg(String.valueOf(fileType.getFileTypeId()), RecordDetailEnum.file_type.name(), fileType.getFileTypeId());
		testRecord.setRecordDetailMsg(String.valueOf(fileType.getFileTypeId()), RecordDetailEnum.file_path.name(), file.getParent() + "/");
		testRecord.setRecordDetailMsg(String.valueOf(fileType.getFileTypeId()), RecordDetailEnum.file_name.name(), file.getName());
		testRecord.setRecordDetailMsg(String.valueOf(fileType.getFileTypeId()), RecordDetailEnum.file_size.name(), file.length());
		testRecord.setRecordDetailMsg(String.valueOf(fileType.getFileTypeId()), RecordDetailEnum.file_guid.name(), UtilsMethod.getUUID());
	}
	
	/**
	 * 根据传入的文件名中是否带()并能转换成整形得到分割ID
	 * @param name
	 * @return
	 */
	private int getSplitId(String name){
		if(name.indexOf("(") > 0 && name.indexOf(")") > 0){
			try{
				return Integer.parseInt( name.substring(name.indexOf("(") + 1,name.indexOf(")")));
			}catch(Exception e){
				LogUtil.w(tag, "getSplitId",e);
				return 1;
			}
		}
		return 1;
	}
}

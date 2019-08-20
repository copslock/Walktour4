package com.walktour.control.instance;

import android.content.Context;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordDetailUpload;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据管理文件对象操作
 * 
 * @author haohua
 *
 */
public class DataManagerFileList {
	private static DataManagerFileList instance;
	private ArrayList<DataModel> fileList = new ArrayList<DataModel>();
	private ArrayList<DataModel> uploadList = new ArrayList<DataModel>();
	private static DBManager mDbManager;

	private TestRecord currentTestRecord;

	public static synchronized DataManagerFileList getInstance(Context context) {
		if (instance == null) {
			instance = new DataManagerFileList();
			mDbManager = DBManager.getInstance(context);
		}
		return instance;
	}

	private DataManagerFileList() {
	}

	public ArrayList<DataModel> initFileList(boolean isWorkorderDisplayType,
			HashMap<String, ArrayList<String>> map) {
		fileList = mDbManager.getFiles(isWorkorderDisplayType, map);
		return fileList;
	}

	public ArrayList<DataModel> getNewFileListInstance() {
		return fileList;
	}

	/***
	 * 按测试场景获取所有数据
	 * @param testSene 测试场景
	 * @return
	 */
	public ArrayList<DataModel> getAllFileList(SceneType testSene) {
		ArrayList<DataModel> result = new ArrayList<DataModel>();

		ArrayList<DataModel> tmpList = mDbManager.getAllFiles(testSene);
		for (DataModel d : tmpList) {
			for (DataModel dataLevel2 : d.getChild()) {
				if (dataLevel2.isFolder) {
					result.addAll(dataLevel2.getChild());
				} else {
					result.add(dataLevel2);
				}
			}
		}
		return result;
	}
	
	/***
	 *  按测试场景获取所有数据
	 * @param testSenes
	 * @return
	 */
	public ArrayList<DataModel> getAllFileList(List<SceneType> testSenes) {
		ArrayList<DataModel> result = new ArrayList<DataModel>();
		for(SceneType testSene:testSenes){
			ArrayList<DataModel> tmpList = mDbManager.getAllFiles(testSene);
			for (DataModel d : tmpList) {
				for (DataModel dataLevel2 : d.getChild()) {
					if (dataLevel2.isFolder) {
						result.addAll(dataLevel2.getChild());
					} else {
						result.add(dataLevel2);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 获取手动测试 所有数据
	 * 
	 * @param testType
	 *          测试类型
	 * @return
	 */
	public ArrayList<DataModel> getAllFileList(TestType testType) {
		ArrayList<DataModel> result = new ArrayList<DataModel>();

		ArrayList<DataModel> tmpList = mDbManager.getAllFiles(testType);
		for (DataModel d : tmpList) {
			for (DataModel dataLevel2 : d.getChild()) {
				if (dataLevel2.isFolder) {
					result.addAll(dataLevel2.getChild());
				} else {
					result.add(dataLevel2);
				}
			}
		}
		return result;
	}

	public ArrayList<DataModel> getUploadList() {
		return this.uploadList;
	}

	public void deleteFile(DataModel dataModel) {
		mDbManager.deleteFile(dataModel);
	}

	public void updateFileUploadState(DataModel dataModel) {
		for (RecordDetail recordDetail : dataModel.testRecord.getRecordDetails()) {
			try {
				FileType fType = FileType.getFileType(recordDetail.file_type);
				// 如果当前文件类型在于允许上传的列表中,才修改数据库,设置上传信息
				if (mDbManager.getAllowFileTypes().contains(fType.getFileTypeName())) {
					if (recordDetail.getDetailUploads() == null || recordDetail.getDetailUploads().size() == 0) {
						ArrayList<RecordDetailUpload> recordDetailUploads = new ArrayList<RecordDetailUpload>();
						RecordDetailUpload item = new RecordDetailUpload();
						item.detail_id = recordDetail.detail_id;
						item.server_info = mDbManager.getServerStr();
						item.upload_type = dataModel.getState();
						recordDetailUploads.add(item);
						recordDetail.setDetailUploads(recordDetailUploads);
						continue;
					}
					for (RecordDetailUpload recordDetailUpload : recordDetail.getDetailUploads()) {
						recordDetailUpload.upload_type = dataModel.getState();
					}
				}
			} catch (Exception e) {
				LogUtil.w("DataManagerFileList", "updateFileUploadState", e);
			}
		}
		mDbManager.updateFile(dataModel);

	}

	/**
	 * 设置当前正在操作的测试记录
	 * 
	 * @param testRecord
	 */
	public void setOperation(TestRecord testRecord) {
		currentTestRecord = testRecord;
	}

	public void refreshFilePath(String path) {
		for (RecordDetail recordDetail : currentTestRecord.getRecordDetails()) {
			recordDetail.file_path = path;
		}
		mDbManager.syncTestRecord(currentTestRecord);
	}

	public void refreshFileName(String name) {
		currentTestRecord.file_name = name;
		mDbManager.modifyName(currentTestRecord, name);
	}

	public long insertFile(TestRecord testRecord) {
		return mDbManager.syncTestRecord(testRecord);
	}

	public void refreshFiles() {
		// TODO Auto-generated method stub
	}

	public String getRefreshAction() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 删除旧数据66
	 * 
	 * @param day
	 */
	public void deleteOldFiles(int day) { 
		List<DataModel> filexs = new LinkedList<DataModel>();
		SceneType[] types = SceneType.values();
		for (int i = 0; i < types.length; i++) {
			filexs.addAll(this.getAllFileList(types[i]));
		}
		if (filexs.size() > 0) {
			Iterator<DataModel> iterator = filexs.iterator();
			while (iterator.hasNext()) {
				DataModel dataModel = iterator.next();
				// 当前文件的创建时间+保留时间如果小于或等于当前时间的话,那么就该删除此文件
				if (dataModel.getCreateTime() + day * 24 * 60 * 60 * 1000 <= System.currentTimeMillis()) {
					this.deleteFile(dataModel);
					iterator.remove();
				}
			}
		}
	}

	/**
	 * 文件上传状态改变
	 * 
	 * @param recordId
	 *          TestRecord主键ID
	 * @param fileType
	 *          当前上传文件类型
	 * @param state
	 *          上传状态:'-1表示未上传，100表示上传完成，-2表示上传失败；0，待上传；
	 * @param serverName
	 *          上传服务器信息,该字段以:“IP_Port”存储
	 */
	public void uploadStateChange(String recordId, FileType fileType, int state, String serverName) {
		mDbManager.uploadStateChange(recordId, fileType, state, serverName);
	}

	/**
	 * 保存记录的文件路径和文件名称
	 * 
	 * @param recordId
	 *          记录ID
	 * @param fileType
	 *          文件类型
	 * @param filePath
	 *          文件路径
	 * @param fileName
	 *          文件名称
	 */
	public void saveRecordFilePath(String recordId, FileType fileType, String filePath, String fileName) {
		mDbManager.saveRecordFilePath(recordId, fileType, filePath, fileName);
	}

}

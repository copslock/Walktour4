package com.walktour.service.app.datatrans.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.data.model.DBManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 上传的文件对象
 *
 * @author jianchao.wang
 *
 */
public class UploadFileModel implements Parcelable {
	/** 日志标识 */
	private static final String TAG = "UploadFileModel";
	/** 关联的测试记录ID */
	private final String mTestRecordId;
	/** 文件名称 */
	private String mName;
	/** 文件目录的绝对路径 */
	private String mParentPath;
	/** 是否正在压缩文件 */
	private transient boolean isZiping = false;
	/** 是否正在转换文件(如DTLog文件) */
	private transient boolean isConverting = false;
	/** 各文件类型的上传状态映射<文件类型，上传状态> */
	private transient Map<FileType, UploadState> mUploadStateMap = new HashMap<>();
	/** 各文件类型的上传进度映射<文件类型，上传进度> */
	private transient Map<FileType, Integer> mProgressMap = new HashMap<>();
	/** 额外追加的参数映射<参数名，参数值> */
	private Map<String, String> mExtraParams = new HashMap<>();
	/** 是否上传完保存到数据管理的数据库 */
	private boolean isUpdateDB;
	/**
	 * 当前上传的文件必须保证当前上传的类型成功才能执行下一个类型
	 */
	private boolean isLastSuccess;
	/** 测试类型Id */
	private int mTestTypeId;

	/** 当前上传状态 */
	public enum UploadState {
		WAIT(0, "wait"), DOING(1, "doing"), SUCCESS(2, "success"), FAILURE(3, "failure"), FILE_NO_FOUND(4,
				"file no found"), INTERRUPT(5, "interrupt");
		/** 状态Id */
		private int mId;
		/** 状态名称 */
		private String mName;

		UploadState(int id, String name) {
			this.mId = id;
			this.mName = name;
		}

		public int getId() {
			return this.mId;
		}

		public String getName() {
			return this.mName;
		}

		public static UploadState getState(int id) {
			for (UploadState state : values()) {
				if (state.mId == id)
					return state;
			}
			return null;
		}
	}

	public UploadFileModel(String testRecordId, int testTypeId) {
		this.mTestRecordId = testRecordId;
		this.isUpdateDB = true;
		this.mTestTypeId = testTypeId;
		this.isLastSuccess = false;
	}

	public UploadFileModel(Parcel in) {
		this.mTestRecordId = in.readString();
		this.mTestTypeId = in.readInt();
		this.mName = in.readString();
		this.mParentPath = in.readString();
		this.isUpdateDB = Boolean.valueOf(in.readString());
		this.isLastSuccess = Boolean.valueOf(in.readString());
		int[] fileTypes = in.createIntArray();
		int[] uploadStates = in.createIntArray();
		int[] progresses = in.createIntArray();
		String[] extraParamKeys = in.createStringArray();
		String[] extraParamValues = in.createStringArray();
		if (fileTypes != null) {
			for (int i = 0; i < fileTypes.length; i++) {
				FileType fileType = FileType.getFileType(fileTypes[i]);
				this.mUploadStateMap.put(fileType, UploadState.getState(uploadStates[i]));
				this.mProgressMap.put(fileType, progresses[i]);
			}
		}
		if (extraParamKeys != null) {
			for (int i = 0; i < extraParamKeys.length; i++) {
				this.mExtraParams.put(extraParamKeys[i], extraParamValues[i]);
			}
		}
	}

	/**
	 * 根据文件类型返回不同文件的上传状态
	 *
	 * @param fileType
	 *          文件类型
	 * @return
	 */
	public UploadState getUploadState(FileType fileType) {
		if (this.mUploadStateMap.containsKey(fileType))
			return this.mUploadStateMap.get(fileType);
		return null;
	}

	/**
	 * 设置不同文件类型的上传状态
	 *
	 * @param fileType
	 *          文件类型
	 * @param uploadState
	 *          上传状态
	 */
	public void setUploadState(FileType fileType, UploadState uploadState) {
		if (this.mUploadStateMap.containsKey(fileType))
			this.mUploadStateMap.put(fileType, uploadState);
	}

	/**
	 * 根据文件类型返回不同文件的上传进度
	 *
	 * @param fileType
	 *          文件类型
	 * @return
	 */
	public int getProgress(FileType fileType) {
		if (this.mProgressMap.containsKey(fileType))
			return mProgressMap.get(fileType);
		return 0;
	}

	/**
	 * 获得进度表
	 *
	 * @return
	 */
	public Map<FileType, Integer> getProgressMap() {
		return mProgressMap;
	}

	/**
	 * 获得整个文件的上传进度
	 *
	 * @return
	 */
	public int getProgress() {
		int sum = 0;
		int count = 0;
		for (int progress : this.mProgressMap.values()) {
			sum += progress;
			count++;
		}
		return sum / count;
	}

	/**
	 * 设置不同文件类型的上传进度
	 *
	 * @param fileType
	 *          文件类型
	 * @param progress
	 *          当前进度
	 * @return
	 */
	public void setUploadProgress(FileType fileType, int progress) {
		if (!this.mProgressMap.containsKey(fileType))
			return;
		this.mProgressMap.put(fileType, progress);
	}

	/**
	 * 获取不带后缀的文件名
	 *
	 * @return
	 */
	public String getName() {
		return mName;
	}

	public String getName(FileType fileType) {
		if (!this.mProgressMap.containsKey(fileType))
			return null;
		return this.mName + fileType.getExtendName();
	}

	public void setName(String name) {
		mName = name;
	}

	public boolean isZiping() {
		return isZiping;
	}

	public void setZiping(boolean isZiping) {
		this.isZiping = isZiping;
	}

	public String getTestRecordId() {
		return this.mTestRecordId;
	}

	/** 根据文件类型获取文件 */
	public File getFile(FileType fileType) {
		if (!this.mProgressMap.containsKey(fileType)) {
			LogUtil.d(TAG, "----getFile:fileType not exist----");
			return null;
		}
		File file = new File(this.mParentPath + File.separator + this.getName(fileType));
		if (file.exists()) {
			LogUtil.d(TAG, "----getFile:" + file.getAbsolutePath() + "----");
			return file;
		} else if (fileType == FileType.MOSZIP && this.hasExtraParam("MOSFilesPath")) {
			file = new File(this.getStringExtraParam("MOSFilesPath"));
			if (file.exists()) {
				LogUtil.d(TAG, "----getFile:" + file.getAbsolutePath() + "----");
				return file;
			}
		} else if (fileType == FileType.MIXZIP) {//标注文件
			String pax=AppFilePathUtil.getInstance().getSDCardBaseDirectory("tag")+this.getName()+".mixzip";
			file = new File(pax);
			if (file.exists()) {//标注文件存在,直接返回
				LogUtil.d(TAG, "----getFile:" + file.getAbsolutePath() + "----");
				return file;
			}else{//文件为何不存在
				LogUtil.w(TAG,"pax="+pax);
			}
		} else if (fileType == FileType.FloorPlan && this.hasExtraParam("BgPicID")) {
			file = new File(this.mParentPath + File.separator + this.getStringExtraParam("BgPicID"));
			if (file.exists()) {
				LogUtil.d(TAG, "----getFile:" + file.getAbsolutePath() + "----");
				return file;
			}
		}
		if(!file.exists()&&fileType==FileType.ECTI){//文件不存在，且文件格式为ecti，做特殊处理，ecti文件会改名.
			TestRecord testRecord = DBManager.getInstance(WalktourApplication.getAppContext()).getTestRecord(mTestRecordId);
			if(null!=testRecord){
				for(RecordDetail detail:testRecord.getRecordDetails()){
					if(detail.file_type==FileType.ECTI.getFileTypeId()){
						file = new File(this.mParentPath + File.separator + detail.file_name);
						if(file.exists()){
							return file;
						}
					}
				}
			}
		}
		return null;
	}

	public boolean isConverting() {
		return isConverting;
	}

	public void setConverting(boolean isConverting) {
		this.isConverting = isConverting;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mTestRecordId);
		dest.writeInt(this.mTestTypeId);
		dest.writeString(this.mName);
		dest.writeString(this.mParentPath);
		dest.writeString(Boolean.toString(this.isUpdateDB));
		dest.writeString(Boolean.toString(this.isLastSuccess));
		if (!this.mUploadStateMap.isEmpty()) {
			int[] fileTypes = new int[this.mUploadStateMap.size()];
			int[] uploadStates = new int[fileTypes.length];
			int[] progresses = new int[fileTypes.length];
			int pos = 0;
			for (FileType fileType : this.mUploadStateMap.keySet()) {
				fileTypes[pos] = fileType.getFileTypeId();
				uploadStates[pos] = this.mUploadStateMap.get(fileType).getId();
				progresses[pos] = this.mProgressMap.get(fileType);
				pos++;
			}
			dest.writeIntArray(fileTypes);
			dest.writeIntArray(uploadStates);
			dest.writeIntArray(progresses);
		}
		String[] extraParamKeys = new String[this.mExtraParams.size()];
		String[] extraParamValues = new String[this.mExtraParams.size()];
		int pos = 0;
		for (String key : this.mExtraParams.keySet()) {
			extraParamKeys[pos] = key;
			extraParamValues[pos] = this.mExtraParams.get(key);
			pos++;
		}
		dest.writeStringArray(extraParamKeys);
		dest.writeStringArray(extraParamValues);
	}

	/** 创建类 */
	public static final Parcelable.Creator<UploadFileModel> CREATOR = new Creator<UploadFileModel>() {
		@Override
		public UploadFileModel[] newArray(int size) {
			return new UploadFileModel[size];
		}

		@Override
		public UploadFileModel createFromParcel(Parcel in) {
			return new UploadFileModel(in);
		}
	};

	public String getParentPath() {
		return mParentPath;
	}

	public void setParentPath(String parentPath) {
		mParentPath = parentPath;
	}

	public void setFileTypes(Set<FileType> fileTypes) {
		this.mUploadStateMap.clear();
		this.mProgressMap.clear();
		if (fileTypes != null && !fileTypes.isEmpty()) {
			for (FileType fileType : fileTypes) {
				this.mUploadStateMap.put(fileType, UploadState.WAIT);
				this.mProgressMap.put(fileType, 0);
			}
		}
	}

	/**
	 * 获取当前要上传的文件类型集合
	 *
	 * @return
	 */
	public FileType[] getFileTypes() {
		if (this.mUploadStateMap.isEmpty())
			return new FileType[0];
		Set<FileType> fileTypeSet = this.mUploadStateMap.keySet();
		FileType[] fileTypes = new FileType[fileTypeSet.size()];
		int pos = 0;
		// 对于包含MOS文件类型的上传，要做提前处理
		if (fileTypeSet.contains(FileType.MOSZIP)) {
			fileTypes[pos++] = FileType.MOSZIP;
		}
		if (fileTypeSet.contains(FileType.MIXZIP)) {
			fileTypes[pos++] = FileType.MIXZIP;
		}
		for (FileType fileType : fileTypeSet) {
			if (fileType == FileType.MOSZIP||fileType == FileType.MIXZIP)
				continue;
			fileTypes[pos++] = fileType;
		}
		return fileTypes;
	}

	/**
	 * 是否有设置文件类型
	 *
	 * @return
	 */
	public boolean hasFileTypes(FileType fileType) {
		if (this.mUploadStateMap.containsKey(fileType))
			return true;
		return false;
	}

	/**
	 * 是否有设置文件类型
	 *
	 * @return
	 */
	public boolean hasFileTypes() {
		if (this.mProgressMap.isEmpty())
			return false;
		return true;
	}

	/**
	 * 获取要上传的文件列表
	 *
	 * @return
	 */
	public List<File> getUploadFiles() {
		List<File> files = new ArrayList<File>();
		for (FileType type : this.mProgressMap.keySet()) {
			File file = this.getFile(type);
			if (file != null)
				files.add(file);
		}
		return files;
	}

	/**
	 * 新增外部参数值
	 *
	 * @param name
	 *          参数名
	 * @param value
	 *          参数值
	 */
	public void addExtraParam(String name, String value) {
		this.mExtraParams.put(name, value);
	}

	/**
	 * 获取布尔值的外部参数
	 *
	 * @param name
	 * @return
	 */
	public boolean getBooleanExtraParam(String name) {
		if (!this.mExtraParams.containsKey(name))
			return false;
		String value = this.mExtraParams.get(name);
		return Boolean.getBoolean(value);
	}

	/**
	 * 获取字符串值的外部参数
	 *
	 * @param name
	 * @return
	 */
	public String getStringExtraParam(String name) {
		if (!this.mExtraParams.containsKey(name))
			return null;
		return this.mExtraParams.get(name);
	}

	/**
	 * 获取整型值的外部参数
	 *
	 * @param name
	 * @return
	 */
	public int getIntExtraParam(String name) {
		if (!this.mExtraParams.containsKey(name))
			return -99;
		String obj = this.mExtraParams.get(name);
		return Integer.parseInt(obj);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof UploadFileModel))
			return false;
		UploadFileModel file = (UploadFileModel) o;
		return file.mTestRecordId.equals(this.mTestRecordId);
	}

	/**
	 * 当前文件是否上传成功
	 *
	 * @return
	 */
	public boolean isUploadSuccess() {
		for (UploadState uploadState : this.mUploadStateMap.values()) {
			if (uploadState != UploadState.SUCCESS && uploadState != UploadState.FILE_NO_FOUND)
				return false;
		}
		return true;
	}

	/**
	 * 当前文件是否上传失败
	 *
	 * @return
	 */
	public boolean isUploadFailure() {
		for (UploadState uploadState : this.mUploadStateMap.values()) {
			if (uploadState == UploadState.FAILURE)
				return true;
		}
		return false;
	}

	public boolean isUpdateDB() {
		return isUpdateDB;
	}

	public boolean isLastSuccess() {
		return isLastSuccess;
	}

	public void setLastSuccess(boolean lastSuccess) {
		isLastSuccess = lastSuccess;
	}

	public int getTestTypeId() {
		return mTestTypeId;
	}

	/**
	 * 是否有指定名称的外部参数
	 *
	 * @param name
	 *          参数名称
	 * @return
	 */
	public boolean hasExtraParam(String name) {
		return this.mExtraParams.containsKey(name);
	}

}

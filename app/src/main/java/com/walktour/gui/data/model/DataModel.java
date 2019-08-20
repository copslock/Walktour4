package com.walktour.gui.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.dinglicom.data.model.RecordAbnormal;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.WalkStruct.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataModel implements Cloneable,Parcelable{
	/** 上传状态：生成文件时的默认状态 */
	public static final int STATUS_UPLOAD_INIT = -1;
	/**上传状态：上传失败*/
	public static final int STATUS_UPLOAD_FAIL = -2;
	/** 上传状态：已经上传 */
	public static final int STATUS_UPLOAD_FINISH = 2;
	/** 上传状态：等待上传 */
	public static final int STATUS_UPLOAD_WAITING = 0;
	/** 上传状态：正在上传 */
	public static final int STATUS_UPLOAD_DOING = 1;
	
	/** 所有接受数据管理文件 */
	public static final String[] FILE_INCONTROL = new String[] { 
													FileType.ORGRCU.getFileTypeName(), 
//													FileType.RCU.getFileTypeName(),
													FileType.DCF.getFileTypeName(),
													FileType.DTLOG.getFileTypeName(),
													FileType.DDIB.getFileTypeName(), 
													FileType.PCAP.getFileTypeName(),
													FileType.ECTI.getFileTypeName(),
			FileType.OTSPARAM.getFileTypeName()};
	/** 文件预览轨迹时的保存图片 */
	public static final String FILE_LOCUS_JPEG 		= "locus.jpeg";

	public String firstLevelTitle = "";//第一层显示名称(按时间格式显示时是时间，按工单格式显示时是工单号)
	public String date = "";//用于按时间格式显示时分类判断
	public String node_id = "";//用于按工单格式显示时分类判断
	public boolean isChecked = false;
	public boolean isDeleteChecked = false;
	public boolean isTotalChecked = false;
	public boolean isReplayChecked = false;
	public boolean isMark = false;//是否高亮显示
	
	private String taskName;//测试业务名，多个业务用","隔开
	private String startTime;//开始时间 yyyy-MM-dd 用于分组
	private long createTime;
	private long endTime;
	private String duration;//测试时长
	private int state;//上传状态 -1:未上传过的文件  -2:上传失败 100:上传完成 0:上传中
	private int exceptionCount;//异常数量
	private boolean go;
	private List<DataModel> child = new ArrayList<DataModel>();//分文件
	public boolean isFolder = false;
	public boolean isFirstLevel = false;
	public String task_no = "";//文件归类，task_no相同的放在同以文件夹中
	public TestRecord testRecord;
//	protected Map<String, Boolean> uploadMap = new HashMap<String, Boolean>();
	/** 保存文件上传情况，Key为文件后缀,Value为上传进度 要保存到数据库 */
//	protected Map<String, Integer> progressMap = new HashMap<String, Integer>();
	/** 保存文件的大小，Key为文件后缀，Value为大小,这个值在读文件时存入 */
//	protected Map<String, Long> sizeMap = new HashMap<String, Long>();
	
	/** 是否正在上传 */
	private boolean isUploading = false;
//	/** 整体上传进度 */
//	private int progress;
	
	@Override
	public DataModel clone() {
		DataModel dataModel = new DataModel();
		try {
			DataModel d = (DataModel)super.clone();
			dataModel.setTaskName(d.getTaskName());
			dataModel.setStartTime(d.getStartTime());
			dataModel.setCreateTime(d.getCreateTime());
			dataModel.setEndTime(d.getEndTime());
			dataModel.setDuration(d.getDuration());
			dataModel.setState(d.getState());
			dataModel.setExceptionCount(d.getExceptionCount());
			dataModel.setGo(d.isGo());
			return dataModel;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	public DataModel() {
		super(); 
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getExceptionCount() {
		return exceptionCount;
	}
	public void setExceptionCount(int exceptionCount) {
		this.exceptionCount = exceptionCount;
	}
	public List<DataModel> getChild() {
		return child;
	}
	public void setChild(List<DataModel> child) {
		this.child = child;
	}
	public boolean isGo() {
		return go;
	}
	public void setGo(boolean go) {
		this.go = go;
	}
	
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	/**
	 * 判断和标记是否有标记的异常
	 * @param abnormalTypes
	 */
	public void isMark(String abnormalTypes) {
		if (isFolder || isFirstLevel || testRecord == null) return;
		for (RecordAbnormal ra : testRecord.getRecordAbnormals()) {
			if (abnormalTypes.contains(ra.abnormal_type + "")) {
				isMark = true;
				return;
			}
		}
	}


	/**
	 * 获取所有文件的路径 如 rcu ddib文件等
	 * @return
	 */
	public ArrayList<String> getAllFilePath() {
		ArrayList<String> paths = new ArrayList<String>();
		for (RecordDetail recordDetail : testRecord.getRecordDetails()) {
			String fileAbsolutePath = (recordDetail.file_path.endsWith("/") == true ?   recordDetail.file_path : recordDetail.file_path + File.separator) + recordDetail.file_name;//正常
			System.out.println("All FilePath:" + fileAbsolutePath);
			paths.add(fileAbsolutePath);
		}
		return paths;
	}
	
	
	/**
	 * 函数功能：设置要上传的文件格式
	 * 
	 * @param hashMap
	 */
//	@Override
//	public void setUploadMap(Map<String, Boolean> hashMap) {
//		this.uploadMap = hashMap;
//		for (String type : uploadMap.keySet()) {
//			if (uploadMap.get(type)) {
//				File file = new File(getFilePath(type));
//				if (file.exists() && file.isFile()) {
//					progressMap.put(type, STATUS_UPLOAD_WAITING);
//					sizeMap.put(type, file.length());
//				} else {
//					progressMap.remove(type);
//					sizeMap.remove(type);
//				}
//			}
//		}
//	}
	
	/**
	 * 函数功能：按指定类型返回文件路径
	 * 
	 * @param fileType 指定类型文件
	 * @return
	 */
//	@Override
	public String getFilePath(String fileType) {
		String result = "";
		for (RecordDetail recordDetail : testRecord.getRecordDetails()) {
			if (recordDetail.getFile_type_str().equalsIgnoreCase(fileType)) {
				result = (recordDetail.file_path.endsWith("/") == true ?   recordDetail.file_path : recordDetail.file_path + File.separator)
						+ recordDetail.file_name;
				System.out.println("FilePath:" + result);
			}
		}
		return result;
	}
//	@Override
	public boolean rename(String newName) {
		if (newName != null && newName.length() > 0 && newName.matches("(\\w|\\(|\\)|\\-|\\_)+")) {
			ArrayList<String> filePaths = getAllFilePath();
			for (String filePath : filePaths) {
				File file = new File(filePath);
				String oldName = file.getName().replaceAll("(\\.(\\w+))+", "");
				File newPath = new File(file.getParent() + File.separator + file.getName().replace(oldName, newName));
				System.out.println("new File path:" + newPath.getAbsolutePath());
				file.renameTo(newPath);
			}
			return true;
		}

		return false;
	}
	

	public boolean isUPloading() {
		return isUploading;
	}

	public void setUPloading(boolean isUploading) {
		this.isUploading = isUploading;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.taskName);
		dest.writeString(this.startTime);
		dest.writeLong(this.createTime);
		dest.writeLong(this.endTime);
		dest.writeString(this.duration);
		dest.writeInt(this.state);
		dest.writeInt(this.exceptionCount);
		if(this.isGo()){
			dest.writeInt(1);	
		}else{
			dest.writeInt(0);
		}     
		dest.writeInt(this.testRecord.test_type); 
		for(RecordDetail detail:this.testRecord.getRecordDetails()){
			if(detail.file_name.endsWith(".ddib")){
				dest.writeString(detail.file_path+detail.file_name);
				break;
			}
		}
	
	}
	  public static final Parcelable.Creator<DataModel> CREATOR = new Parcelable.Creator<DataModel>() {

		@Override
		public DataModel createFromParcel(Parcel source) { 
			return new DataModel(source);
		}

		@Override
		public DataModel[] newArray(int size) { 
			return new DataModel[size];
		}
		  
	  };
	  
	private DataModel(Parcel source) {
		super(); 
		this.taskName=source.readString();
		this.startTime=source.readString();
		this.createTime=source.readLong();
		this.endTime=source.readLong();
		this.duration=source.readString();
		this.state=source.readInt();
		this.exceptionCount=source.readInt();
		this.go=(source.readInt()==1?true:false);  
		this.testRecord=new TestRecord();
		this.testRecord.test_type=source.readInt(); 
		this.testRecord.file_name=source.readString();	 
	}
	/**
	 * 获取运营商
	 * @return
	 */
	public String getOperator() {
		if (testRecord != null && testRecord.getRecordTestInfo() != null) {
			for (RecordTestInfo recordTestInfo : testRecord.getRecordTestInfo()) {
				if (recordTestInfo.key_info.equalsIgnoreCase("operator")) {
					return recordTestInfo.key_value;
				}
			}
		}
		return "";
	}

}

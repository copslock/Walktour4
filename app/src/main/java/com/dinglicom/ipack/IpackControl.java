package com.dinglicom.ipack;

import android.content.Context;

import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordDetailUpload;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyFileWriter;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.data.model.DBManager;
import com.walktour.model.UmpcTestInfo;

import java.util.ArrayList;

/**
 * 连接小背包相关的方法控制类
 * 
 * @author weirong.fan
 *
 */
public class IpackControl {
	/** Tag标识 **/
	private static String TAG = IpackControl.class.getSimpleName();
	/** 单例类 **/
	private final static IpackControl instance = new IpackControl();

	/**
	 * 初始化小包背时结构串
	 * DevID=%s\r\nDevModel=%s\r\nCmdPort=%s\r\nDataPort=%s\r\nCtrlPort=%s\r\nTelComName=%s\r\ndevNetType=%s\r\nifrName=%s\r\nbcport=%s\r\nTermType=%d\r\n
	 */
	private String beginInitFormat = "DevID=%s\r\nDevModel=%s\r\nCmdPort=%s\r\nDataPort=%s\r\nCtrlPort=%s\r\nTelComName=%s\r\ndevNetType=%s\r\nifrName=%s\r\nbcport=%s\r\nTermType=%d\r\n";
	/**
	 * iPack要求上传上传指定文件时填充当前串，调晓军库上传
	 * GUID=%s\r\nDevID=%s\r\nFilePath=%s\r\nFileName=%s\r\nFileType=%s\r\nTransMode=%s\r\nGroupInfo=%s\r\nTestMode=%s\r\n
	 */
	private String sendFileFormat = "GUID=%s\r\nDevID=%s\r\nFilePath=%s\r\nFileName=%s\r\nFileType=%s\r\nTransMode=%s\r\nGroupInfo=%s\r\nTestMode=%s\r\nTaskNo=%s\r\nATUPort=%s\r\n";
	/**
	 * 当连小背包测试开始时，如果为实时上传指定文件，按要求填充当前串并调JNI接口进行上传
	 * GUID=%s\r\nFilePath=%s\r\nFileName=%s\r\nFileType=%s\r\nTransMode=%d\r\nGroupInfo=%s\r\nTestMode=%s\r\n
	 */
	private String sendRTFileFormat = "GUID=%s\r\nFilePath=%s\r\nFileName=%s\r\nFileType=%s\r\nTransMode=%d\r\nGroupInfo=%s\r\nTestMode=%s\r\nTaskNo=%s\r\nATUPort=%s\r\n";
	/**
	 * 实时上传过程中实时更新当前文件的大小,填充当前串 GUID=%s\r\nWriteEof=%d\r\nCurrSize=%d\r\n
	 */
	private String setRTFileInfoFormat = "GUID=%s\r\nWriteEof=%d\r\nCurrSize=%d\r\n";
	/**
	 * 测试结束后，如果生成文件的名字发生变化时，调用当方法更新 GUID=%s\r\nDevID=%s\r\nFileName=%s\r\n
	 */
	private String sendRTFileUpdateFormat = "GUID=%s\r\nDevID=%s\r\nFileName=%s\r\n";
	/**
	 * 查询文件列表时生成每行数据格式 参数 IMEI TaskNo TestMode GroupInfo Operators FileName
	 * FileSize FileNum FileTypeID CreateTime TestDuration FileID
	 */
	private String fileListFormat = "<FILE IMEI=\"%s\" TaskNo=\"%s\" TestMode=\"%s\" GroupInfo=\"%s\" Operators=\"%s\" FileName=\"%s\" FileSize=\"%s\" FileNum=\"%s\" FileTypeID=\"%s\" CreateTime=\"%s\" TestDuration=\"%s\" FileID=\"%s\" ATUPort=\"%s\" />";

	/**
	 * 电信巡检
	 */
	private String fileListFormatDXXJ = "<FILE IMEI=\"%s\" TaskNo=\"%s\" TestMode=\"%s\" GroupInfo=\"%s\" Operators=\"%s\" FileName=\"%s\" FileSize=\"%s\" FileNum=\"%s\" FileTypeID=\"%s\" CreateTime=\"%s\" TestDuration=\"%s\" FileID=\"%s\" UploadProgress=\"%s\" ATUPort=\"%s\" />";

	/**
	 * 防止外部构造
	 */
	private IpackControl() {
		super();
	}

	/**
	 * 单例模式
	 * 
	 * @return
	 */

	public static synchronized IpackControl getInstance() {
		return instance;
	}
 

	public synchronized final String getBeginInitFormat() {
		return beginInitFormat;
	}

	public synchronized final String getSendFileFormat() {
		return sendFileFormat;
	}

	public synchronized final String getSendRTFileFormat() {
		return sendRTFileFormat;
	}

	public synchronized final String getSetRTFileInfoFormat() {
		return setRTFileInfoFormat;
	}

	public synchronized final String getSendRTFileUpdateFormat() {
		return sendRTFileUpdateFormat;
	}


	public static enum IpackFileType {
		RCU("1", FileType.RCU.getFileTypeName()),
		DTLOG("2", FileType.DTLOG.getFileTypeName()),
        DDIB("3", FileType.DDIB.getFileTypeName()),
		PCAP("4", FileType.PCAP.getFileTypeName()),
		DCF("5",FileType.DCF.getFileTypeName()),
		ORGRCU("6",FileType.ORGRCU.getFileTypeName()),
		CU("7", FileType.CU.getFileTypeName()),
		ECTI("8", FileType.ECTI.getFileTypeName()),
		FILELIST("11", "filelist"),
		STAT("12", "stat"),
		FLEETFILELIST("13", "fleetfilelist"),
		TESTPLAN("21", "testplan"),
		Unknown("99", "unknown");

		private String TypeID;
		private String TypeName;

		private IpackFileType(String id, String name) {
			TypeID = id;
			TypeName = name;
		}

		/**
		 * 获得当前文件类型的类型ID
		 * 
		 * @return
		 */
		public String getTypeID() {
			return TypeID;
		}

		public String getTypeName() {
			return TypeName;
		}

		/**
		 * 通过传入的类型ID，获得文件类型枚举
		 * 
		 * @param id
		 * @return
		 */
		public static IpackFileType getFileTypeByID(String id) {
			for (IpackFileType type : values()) {
				if (type.getTypeID().equals(id)) {
					return type;
				}
			}
			return Unknown;
		}

		/**
		 * 通过传入的类型名称，获得文件类型枚举
		 * 
		 * @param id
		 * @return
		 */
		public static IpackFileType getFileTypeByName(String name) {
			for (IpackFileType type : values()) {
				if (type.getTypeName().equals(name)) {
					return type;
				}
			}
			return Unknown;
		}
	}

	/**
	 * 终端类型定义
	 * 
	 * @author Tangwq
	 *
	 */
	public static enum TermType {
		Unknow(0), IPhone(1), Android(2), Scanner(3), PlanATU(4);

		private int termType = 0;

		private TermType(int type) {
			termType = type;
		}

		public int getTermTypeId() {
			return termType;
		}

		/** 根据终端类型ID获得终端枚举类型 */
		public static TermType getTermTypeById(int id) {
			TermType[] termTypes = TermType.values();
			for (TermType type : termTypes) {
				if (type.getTermTypeId() == id) {
					return type;
				}
			}

			return Unknow;
		}
	}

	/**
	 * 从传入的字符串中获得指定关键字的值
	 * 
	 * @param params
	 *            FileType=%d,FileName=%s
	 * @param key
	 *            关键字
	 * @return
	 */
	public String getValueByKey(String param, String key) {
		return getValueByKey(param, key, "");
	}

	/**
	 * 从传入的字符串中获得指定关键字的值
	 * 
	 * @param params
	 *            FileType=%d,FileName=%s
	 * @param key
	 *            关键字
	 * @param def
	 *            默认值
	 * @return
	 */
	public String getValueByKey(String param, String key, String def) {
		String[] params = param.split(",");

		return getValueByKey(params, key, def);
	}

	/**
	 * 从传入的字符串数组获得对应
	 * 
	 * @param params
	 *            key=value键值数组
	 * @param key
	 *            要获得的键值
	 * @param defaultStr
	 *            当获不到键值时的默认值
	 * @return
	 */
	public String getValueByKey(String[] params, String key, String defaultStr) {
		if (params != null) {
			for (String str : params) {
				if (str.startsWith(key + "=")) {
					return str.substring(key.length() + 1);
				}
			}
		}
		return defaultStr;
	}

	/**
	 * 处理文件列表查询，返回生成的文件路径
	 * 
	 * @param context
	 * @param taskFileList
	 * @param fileName
	 * @return
	 */
	public String getFileListPath(Context context, ArrayList<TestRecord> testRecords, String fileName) {
		StringBuffer files = new StringBuffer();

		files.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\r");
		files.append("<FILELIST>\n\r");
		files.append("<DIR name=\"\">\n\r");
		// int i=0;
		for (TestRecord model : testRecords) {
			for (RecordDetail detail : model.getRecordDetails()) {
				IpackFileType iFileType = IpackFileType
						.getFileTypeByName(FileType.getFileType(detail.file_type).getFileTypeName());
				// 当前的文件类型在IPAD上端有定义的文件类型时,才需要往IPAD上传,否则过滤掉,像图片文件
				if (iFileType != IpackFileType.Unknown) {
					files.append(String.format(fileListFormat, MyPhoneState.getInstance().getMyDeviceId(context),
							model.task_no, model.getTest_type_str(), model.group_info, "", // Operate
							detail.file_name, detail.file_size, model.file_split_id, iFileType.getTypeID(),
							model.time_create, model.time_end - model.time_create, detail.file_guid, model.port_id));
					files.append("\n\r");
				}
			}
		}
		files.append("</DIR>\n\r");
		files.append("</FILELIST>\n\r");

		String listInfoPath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
		MyFileWriter.write(listInfoPath, files.toString());
		UtilsMethod.runRootCommand("chmod 777 " + listInfoPath);
		// m_strResult = listInfoPath ;

		String fileListPath = String.format("FilePath=%s\r\nFileName=%s\r\nFileType=%s\r\n",
				context.getFilesDir().getAbsolutePath(), fileName, IpackFileType.FILELIST.getTypeID());

		LogUtil.w(TAG, "--fileList:" + fileListPath);
		return fileListPath;
	}

	public String getFileListPathDXXJ(Context context, ArrayList<TestRecord> testRecords, String fileName,String groupInfo) {
		StringBuffer files = new StringBuffer();

		files.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\r");
		files.append("<FILELIST>\n\r");
		files.append("<DIR name=\"\">\n\r");
		LogUtil.w(TAG, "--fileList:testRecords size="+testRecords.size());
		for (TestRecord model : testRecords) {
			for (RecordDetail detail : model.getRecordDetails()) {
				if(null!=groupInfo&&model.group_info.equals(groupInfo)) {
					IpackFileType iFileType = IpackFileType
							.getFileTypeByName(FileType.getFileType(detail.file_type).getFileTypeName());
					// 当前的文件类型在IPAD上端有定义的文件类型时,才需要往IPAD上传,否则过滤掉,像图片文件
					if (iFileType != IpackFileType.Unknown) {
						if(detail.getDetailUploads().size()>0) {
							for (RecordDetailUpload upload : detail.getDetailUploads()) {
								DBManager mDbManager = DBManager.getInstance(context);
								LogUtil.w(TAG,"FFFWWW=1"+upload.detail_id+","+detail.detail_id+","+mDbManager.getServerStr()+","+upload.server_info);
								if (upload.detail_id == detail.detail_id && mDbManager.getServerStr().equals(upload.server_info)) {
									files.append(String.format(fileListFormatDXXJ, MyPhoneState.getInstance().getMyDeviceId(context),
											model.task_no, model.getTest_type_str(), model.group_info, "", // Operate
											detail.file_name, detail.file_size, model.file_split_id, iFileType.getTypeID(),
											model.time_create, model.time_end - model.time_create, detail.file_guid, upload.upload_type+"", model.port_id));
								}else{
									LogUtil.w(TAG,upload.detail_id+","+detail.detail_id+mDbManager.getServerStr()+","+upload.server_info);
									files.append(String.format(fileListFormatDXXJ, MyPhoneState.getInstance().getMyDeviceId(context),
											model.task_no, model.getTest_type_str(), model.group_info, "", // Operate
											detail.file_name, detail.file_size, model.file_split_id, iFileType.getTypeID(),
											model.time_create, model.time_end - model.time_create, detail.file_guid,"0", model.port_id));
								}
							}
						}else{
							LogUtil.w(TAG,"FFFWWW=2 detail.getDetailUploads().size()=0");
							files.append(String.format(fileListFormatDXXJ, MyPhoneState.getInstance().getMyDeviceId(context),
									model.task_no, model.getTest_type_str(), model.group_info, "", // Operate
									detail.file_name, detail.file_size, model.file_split_id, iFileType.getTypeID(),
									model.time_create, model.time_end - model.time_create, detail.file_guid,"0", model.port_id));
						}
						files.append("\n\r");
					}
				}
			}
		}
		files.append("</DIR>\n\r");
		files.append("</FILELIST>\n\r");

		String listInfoPath = context.getFilesDir().getAbsolutePath() + "/" + fileName;
		FileUtil.deleteFile(listInfoPath);
		MyFileWriter.write(listInfoPath, files.toString());
		UtilsMethod.runRootCommand("chmod 777 " + listInfoPath);
		// m_strResult = listInfoPath ;

		String fileListPath = String.format("FilePath=%s\r\nFileName=%s\r\nFileType=%s\r\n",
				context.getFilesDir().getAbsolutePath(), fileName, IpackFileType.FLEETFILELIST.getTypeID());

		LogUtil.w(TAG, "--fileList:" + fileListPath);
		return fileListPath;
	}

	/**
	 * 获得指定类型可上传文件路径
	 * 
	 * @param context
	 * @param taskFileList
	 * @param fileName
	 * @param type
	 * @return
	 */
	public String getUploadFilesPath(Context context, ArrayList<TestRecord> testRecords, String fileName,
			IpackFileType type) {
		for (TestRecord model : testRecords) {
			LogUtil.w(TAG, "--type:" + type.name() + "--modelName:" + model.file_name + "--eq:"
					+ fileName.startsWith(model.file_name));
			if (fileName.startsWith(model.file_name)) {
				for (RecordDetail detail : model.getRecordDetails()) {
					if (FileType.getFileType(detail.file_type).getFileTypeName().equals(type.getTypeName())) {

						return String.format(sendFileFormat, detail.file_guid,
								MyPhoneState.getInstance().getDeviceId(context), detail.file_path, detail.file_name,
								type.getTypeID(), UmpcTestInfo.eTransMode_ReTrans, model.group_info,
								model.getTest_type_str(), model.task_no, model.port_id);
					}
				}
				break;
			}
		}

		return null;
	}
}

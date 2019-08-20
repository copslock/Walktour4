package com.walktour.gui.setting.sysroutine.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus.UploadStatus;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.ZipUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.datepicker.WheelMain;
import com.walktour.gui.R;
import com.walktour.gui.data.model.DataModel;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 日志上传对话框
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("SdCardPath")
public class SysRoutineUploadLogDialog extends BasicDialog implements OnClickListener {
	/** 系统日志所在目录路径 */
	private final static String SYS_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour/log/";
	/** 库日志所在目录路径 */
	private final static String LIB_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour/liblog/";
	/** 系统所在目录路径 */
	private final static String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour/";
	/** 连接超时时长 */
	private int timeout = 5 * 1000;
	/** 上传失败标识 */
	private static final int UPLOAD_FAIL = 11;
	/** 上传成功标识 */
	private static final int UPLOAD_END = 12;
	/** 上传的服务器IP列表 */
	private List<String> ftpIPList = new ArrayList<String>();
	/** 上传的服务器Port */
	private int ftpPort;
	/** 上传的服务器用户 */
	private String ftpUser;
	/** 上传的服务器密码 */
	private String ftpPass;
	/** 上传的服务器目录 */
	private String ftpCatalog = "";
	/** 建设类 */
	private Builder builder;
	/** 上下文 */
	private Activity activity;
	/** 起始时间 */
	private RelativeLayout startTime;
	/** 结束时间 */
	private RelativeLayout endTime;
	/** 起始时间 */
	private long startTimeValue = System.currentTimeMillis();
	/** 结束时间 */
	private long endTimeValue = System.currentTimeMillis();
	/** 起始时间显示 */
	private TextView startTimeTxt;
	/** 结束时间显示 */
	private TextView endTimeTxt;
	/** 上传文件类型显示 */
	private TextView fileTypeTxt;
	/** 是否同步上传数据 */
	private CheckBox uploadData;
	/** 上传进度条 */
	private ProgressDialog progress;
	/** 处理删除过程 */
	private Handler handler = new MyHandler(this);
	/** 上传工具类 */
	private FtpOperate ftp = new FtpOperate(activity);
	/** 日期格式 */
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
	/** 文件类型映射 */
	private Map<String, Boolean> fileTypeMap = new HashMap<String, Boolean>();
	/** 文件名称 */
	private String fileName = "";
	private ApplicationModel appModel = ApplicationModel.getInstance();

	public SysRoutineUploadLogDialog(Activity activity, Builder builder) {
		super(activity);
		this.activity = activity;
		this.builder = builder;
		try {
			fileName = activity.getFilesDir() + "/config/config_ftp_setting.xml";
			parseFtpSetting(new FileInputStream(new File(fileName)), "log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.initFileType();
		this.init();
	}

	/**
	 * 初始化文件类型选项
	 */
	private void initFileType() {
//		fileTypeMap.put(FileType.RCU.getFileTypeName(), true);
		fileTypeMap.put(FileType.DTLOG.getFileTypeName(), false);
		fileTypeMap.put(FileType.DCF.getFileTypeName(), false);
		fileTypeMap.put(FileType.PCAP.getFileTypeName(), false);
		fileTypeMap.put(FileType.DDIB.getFileTypeName(), false);
		fileTypeMap.put(FileType.ORGRCU.getFileTypeName(), false);
		fileTypeMap.put(FileType.ECTI.getFileTypeName(), false);
	}

	/**
	 * 初始化
	 */
	@SuppressLint("InflateParams")
	private void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sys_routine_setting_upload_log, null);
		builder.setTitle(R.string.sys_setting_data_upload_log_title);
		builder.setView(layout);
		layout.findViewById(R.id.setting_upload_file_type_layout).setOnClickListener(this);
		this.startTime = (RelativeLayout) layout.findViewById(R.id.start_time);
		this.endTime = (RelativeLayout) layout.findViewById(R.id.end_time);
		this.uploadData = (CheckBox) layout.findViewById(R.id.upload_data);
		this.startTimeTxt = (TextView) layout.findViewById(R.id.start_time_txt);
		this.endTimeTxt = (TextView) layout.findViewById(R.id.end_time_txt);
		this.startTimeTxt.setText(this.getShowDateTime(this.startTimeValue));
		this.endTimeTxt.setText(this.getShowDateTime(this.endTimeValue));
		this.fileTypeTxt = (TextView) layout.findViewById(R.id.setting_upload_file_type_text);
		builder.setNeutralButton(R.string.upload, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				uploadLog();
			}
		}).setNegativeButton(R.string.str_cancle);
		this.startTime.setOnClickListener(this);
		this.endTime.setOnClickListener(this);
		this.showFileTypes();
	}

	/**
	 * 获得显示的时间
	 * 
	 * @param time
	 *          时间
	 * @return
	 */
	private String getShowDateTime(long time) {
		return UtilsMethod.sdFormatss.format(time).substring(0, 16);
	}

	/**
	 * 消息处理句柄
	 * 
	 * @author jianchao.wang
	 *
	 */
	private static class MyHandler extends Handler {

		WeakReference<SysRoutineUploadLogDialog> reference;

		public MyHandler(SysRoutineUploadLogDialog dialog) {
			reference = new WeakReference<SysRoutineUploadLogDialog>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			SysRoutineUploadLogDialog dialog = reference.get();
			switch (msg.what) {
			case UPLOAD_FAIL:
				dialog.progress.setMessage(dialog.activity.getString(R.string.str_unuploaderror));
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dialog.progress.dismiss();
				dialog.dismiss();
			case UPLOAD_END:
				dialog.progress.setMessage(dialog.activity.getString(R.string.str_uploadSuccess));
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dialog.progress.dismiss();
				dialog.dismiss();
			}
		}

	}

	/**
	 * 上传日志文件
	 */
	private void uploadLog() {
		this.progress = ProgressDialog.show(activity, this.activity.getString(R.string.upload),
				this.activity.getString(R.string.str_uploading));
		new Thread() {

			@Override
			public void run() {
				try {
					File sysLog = createSysLogZipFile();
					File liblog = createLibLogZipFile();
					File data = null;
					if (uploadData.isChecked()) {
						data = createDataZipFile();
					}
					boolean connect = false;
					for (String ftpIP : ftpIPList) {
						try {
							connect = ftp.connect(ftpIP, ftpPort, ftpUser, ftpPass, timeout);
						} catch (Exception e) {
							connect = false;
						}
						if (connect)
							break;
					}
					if (connect) {
						uploadFile(sysLog);
						uploadFile(liblog);
						uploadFile(data);
					}
					Message msg = handler.obtainMessage(UPLOAD_END);
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = handler.obtainMessage(UPLOAD_FAIL);
					handler.sendMessage(msg);
				} finally {
					try {
						ftp.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 上传文件到ftp服务器上
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void uploadFile(File file) throws Exception {
		if (file == null)
			return;
		String imei = MyPhoneState.getInstance().getDeviceId(this.activity);
		StringBuilder remoteName = new StringBuilder();
		remoteName.append("/").append(this.ftpCatalog).append("/").append(imei).append("/");
		remoteName.append(df.format(new Date(this.startTimeValue))).append("_");
		remoteName.append(df.format(new Date(this.endTimeValue))).append("_");
		remoteName.append(file.getName());
		UploadStatus uploadStatus = ftp.uploadFile(file.getAbsolutePath(), remoteName.toString());
		switch (uploadStatus) {
		case Upload_New_File_Success:
		case Upload_From_Break_Success:
		case File_Exits:
			file.delete();
			break;
		case Upload_Interrupted:
			file.delete();
			throw new Exception("upload fail");
		default:
			break;
		}
	}

	/**
	 * 创建系统日志压缩文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private File createSysLogZipFile() throws Exception {
		File zipFile = new File(SYS_LOG_PATH + "syslog.zip");
		if (zipFile.exists())
			zipFile.delete();
		zipFile.createNewFile();
		Set<File> files = getUploadSysLogFiles();
		if (!files.isEmpty()) {
			ZipUtil.zip(files, zipFile);
		} else {
			throw new Exception("is Null");
		}
		return zipFile;
	}

	/**
	 * 创建数据压缩文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private File createDataZipFile() throws Exception {
		File zipFile = new File(BASE_PATH + "data.zip");
		if (zipFile.exists())
			zipFile.delete();
		zipFile.createNewFile();
		Set<File> files = this.getUploadDateFiles();
		if (!files.isEmpty()) {
			ZipUtil.zip(files, zipFile);
		} else {
			return null;
		}
		return zipFile;
	}

	/**
	 * 创建库日志压缩文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private File createLibLogZipFile() throws Exception {
		File zipFile = new File(LIB_LOG_PATH + "liblog.zip");
		if (zipFile.exists())
			zipFile.delete();
		zipFile.createNewFile();
		Set<File> files = getUploadLibLogFiles();
		if (!files.isEmpty()) {
			ZipUtil.zip(files, zipFile);
		} else {
			throw new Exception("is Null");
		}
		return zipFile;
	}

	/**
	 * 获取要上传的系统日志文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private Set<File> getUploadSysLogFiles() throws Exception {
		Set<File> files = new HashSet<File>();
		File path = new File(SYS_LOG_PATH);
		Date startDate = new Date(this.startTimeValue);
		Date endDate = new Date(this.endTimeValue);
		for (File file : path.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".log")) {
				Date createDate = df.parse(file.getName().substring(0, file.getName().length() - 4));
				Date modifDate = new Date(file.lastModified());
				if (modifDate.after(startDate) && modifDate.before(endDate)) {
					files.add(file);
				} else if (createDate.before(startDate) && modifDate.after(endDate)) {
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * 获取要上传的库日志文件
	 * 
	 * @return
	 */
	private Set<File> getUploadLibLogFiles() {
		Set<File> files = new HashSet<File>();
		File path = new File(LIB_LOG_PATH);
		for (File file : path.listFiles()) {
			if (file.isFile() && !file.getName().endsWith("liblog.zip")) {
				files.add(file);
			}
		}
		return files;
	}

	/**
	 * 获取要上传的数据文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private Set<File> getUploadDateFiles() throws Exception {
		Set<File> files = new HashSet<File>();
		List<DataModel> fileList = new ArrayList<DataModel>();
		DataManagerFileList dataManage = DataManagerFileList.getInstance(activity);

		if (appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro) {// 高铁和地铁按场景上传
			fileList.addAll(dataManage.getAllFileList(appModel.getSelectScene()));
		} else {
			fileList.addAll(DataManagerFileList.getInstance(activity).getAllFileList(appModel.getBusinessTestScenes()));
			// fileList.addAll(dataManage.getAllFileList(TestType.DT));
			// fileList.addAll(dataManage.getAllFileList(TestType.CQT));
		}
		Date startDate = new Date(this.startTimeValue);
		Date endDate = new Date(this.endTimeValue);
		for (DataModel file : fileList) {
			for (String type : this.fileTypeMap.keySet()) {
				if (this.fileTypeMap.get(type)) {
					String path = file.getFilePath(type);
					File dataFile = new File(path);
					if (dataFile.exists()) {
						Date modifDate = new Date(dataFile.lastModified());
						Date createDate = this.getDateFileCreateDate(dataFile.getName());
						if (modifDate.after(startDate) && modifDate.before(endDate)) {
							files.add(dataFile);
						} else if (createDate != null && createDate.before(startDate) && modifDate.after(endDate)) {
							files.add(dataFile);
						}
					}
				}
			}
		}
		return files;
	}

	/**
	 * 获取数据文件的生成时间
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private Date getDateFileCreateDate(String fileName) throws Exception {
		if (fileName.indexOf("IN") >= 0 || fileName.indexOf("OUT") >= 0) {
			int pos = fileName.indexOf("IN");
			if (pos < 0) {
				pos = fileName.indexOf("OUT");
				pos += 3;
			} else {
				pos += 2;
			}
			String date = fileName.substring(pos, pos + 15);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
			return df.parse(date);
		}
		int pos = fileName.lastIndexOf("_");
		if (pos > 0) {
			pos++;
			String date = fileName.substring(pos, pos + 19);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
			return df.parse(date);
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_time:
			showDateTimePicker(v, this.startTimeValue,
					Html.fromHtml("<font color=white>Please set a date </font>" + "<font color=#28aae2>"
							+ this.getShowDateTime(this.startTimeValue) + "</font>" + " to " + "<font color=#28aae2>"
							+ this.getShowDateTime(endTimeValue) + "</font>"));
			break;
		case R.id.end_time:
			showDateTimePicker(v, this.endTimeValue,
					Html.fromHtml("<font color=white>Please set a date </font>" + "<font color=#28aae2>"
							+ this.getShowDateTime(startTimeValue) + "</font>" + " to " + "<font color=#28aae2>"
							+ this.getShowDateTime(endTimeValue) + "</font>"));
			break;
		case R.id.setting_upload_file_type_layout:
			showFileTypeDialog();
			break;
		}
	}

	/**
	 * 创建上传文件类型对话框
	 * 
	 * @author qihang.li
	 */
	private void showFileTypeDialog() {

		final String[] strArray = new String[this.fileTypeMap.size()];
		final boolean[] checkedItems = new boolean[strArray.length];

		int i = 0;
		for (String type : this.fileTypeMap.keySet()) {
			/*
			 * if (type.equals(DataModel.FILE_LTE_DGZ)) strArray[i] =
			 * FileType.DTLOG.getFileTypeName(); else
			 */
			strArray[i] = type;
			checkedItems[i] = this.fileTypeMap.get(type);
			i++;
		}

		new BasicDialog.Builder(this.activity).setTitle(R.string.sys_setting_data_upload_file_type)
				.setMultiChoiceItems(strArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						String key = strArray[which];
						/*
						 * if (key.equals(DataModel.FILE_DTLOG)) key =
						 * FileType.DTLOG.getFileTypeName();
						 */
						fileTypeMap.put(key, isChecked);
						showFileTypes();
					}
				}).show();
	}

	/**
	 * 显示文件类型
	 */
	private void showFileTypes() {

		StringBuilder fileTypes = new StringBuilder();

		for (String type : this.fileTypeMap.keySet()) {
			if (this.fileTypeMap.get(type)) {
				/*
				 * if (type.equals(DataModel.FILE_LTE_DGZ)) type =
				 * FileType.DTLOG.getFileTypeName();
				 */
				fileTypes.append(type).append(",");
			}
		}
		if (fileTypes.length() > 0)
			fileTypes.deleteCharAt(fileTypes.length() - 1);
		if (fileTypes.length() > 0)
			this.fileTypeTxt.setText(fileTypes);
		else
			this.fileTypeTxt.setText("");
	}

	/**
	 * 弹出自定义时间选择器
	 * 
	 */

	@SuppressLint("InflateParams")
	private void showDateTimePicker(final View v, long time, Spanned tipStr) {
		DisplayMetrics metric = new DisplayMetrics();
		this.activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_time_layout, null);
		TextView showDateTip = (TextView) view.findViewById(R.id.show_tip_massage);
		showDateTip.setText(tipStr);
		final WheelMain main = new WheelMain(view);
		main.setTime(time);
		main.setShowSs(false);
		new BasicDialog.Builder(this.activity).setTitle("Select DateTime").setIcon(R.drawable.pointer)
				.setView(main.showDateTimePicker()).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String choutDate = main.getTime();
						long time = UtilsMethod.getSeconds(choutDate) / 1000;
						switch (v.getId()) {
						case R.id.start_time:
							startTimeValue = time;
							startTimeTxt.setText(getShowDateTime(startTimeValue));
							break;
						case R.id.end_time:
							endTimeValue = time;
							endTimeTxt.setText(getShowDateTime(endTimeValue));
							break;

						default:
							break;
						}
						dialog.dismiss();
					}
				}).setNegativeButton(R.string.str_cancle).show();

	}

	/***
	 * 解析高铁ftp配置文件
	 * 
	 * @param inputStream
	 *          文件输入
	 * @param tagName
	 *          要解析的标签，其他的忽略
	 * @throws Exception
	 *           异常
	 */
	private void parseFtpSetting(InputStream inputStream, String tagName) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int event = parser.getEventType();
		boolean isFlag = true;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				String name = parser.getName();
				if (tagName.equals(name)) {
					isFlag = true;
				}
				if (isFlag) {// 只解析高铁配置
					if ("serverIp".equals(name)) {
						ftpIPList.add(UtilsMethod.jem(parser.getAttributeValue(0)));
						// com.walktour.Utils.LogUtil.w(TAG,"Server:IP"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverPort".equals(name)) {
						ftpPort = Integer.parseInt(UtilsMethod.jem(parser.getAttributeValue(0)));
						// com.walktour.Utils.LogUtil.w(TAG,"Server:Port"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverUser".equals(name)) {
						ftpUser = UtilsMethod.jem(parser.getAttributeValue(0));
						// com.walktour.Utils.LogUtil.w(TAG,"Server:User"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverPassword".equals(name)) {
						ftpPass = UtilsMethod.jem(parser.getAttributeValue(0));
						// com.walktour.Utils.LogUtil.w(TAG,"Server:Pass"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverCatalog".equals(name)) {
						ftpCatalog = UtilsMethod.jem(parser.getAttributeValue(0));
						// com.walktour.Utils.LogUtil.w(TAG,"Server:Path"+UtilsMethod.jem(parser.getAttributeValue(0)));
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if (tagName.equals(parser.getName())) {
					isFlag = false;
				}
				break;
			}
			event = parser.next();
		}
	}
}

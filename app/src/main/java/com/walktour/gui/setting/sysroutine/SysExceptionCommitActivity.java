package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtils;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus.UploadStatus;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.data.model.DataModel;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.service.phoneinfo.utils.MobileUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/***
 * 异常问题在线反馈
 * 
 * @author weirong.fan
 *
 */
@SuppressLint({ "InflateParams", "SdCardPath" })
public class SysExceptionCommitActivity extends BasicActivity{
	private static final String TAG = "SysExceptionCommitActivity";
	/** 上下文 **/
	private Context context = SysExceptionCommitActivity.this;
	/** 系统日志所在目录路径 */
	private final static String SYS_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour/log/";
	/** 库日志所在目录路径 */
	private final static String LIB_LOG_PATH = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/liblog/";
	/** 系统所在目录路径 */
	private final static String BASE_PATH = Environment.getExternalStorageDirectory().getPath() + "/Walktour/";
	/** 连接超时时长 */
	private int timeout = 5 * 1000;
	/** 上传正在进行标识 */
	private static final int UPLOAD_START = 0x3223;
	/** 上传正在进行标识 */
	private static final int UPLOAD_START_SUCCESS = 0x32231;
	/** 上传正在进行标识 */
	private static final int UPLOAD_START_FAILURE = 0x32232;
	/** 上传失败标识 */
	private static final int UPLOAD_FAIL = 0x12211;
	/** 上传成功标识 */
	private static final int UPLOAD_END = 0x1312;
	/** 上传正在进行标识 */
	private static final int UPLOAD_DOING = 0x3213;
	/** 文件类型映射 */
	private Map<String, Boolean> fileTypeMap = new HashMap<String, Boolean>();
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
	private TextView timeTV;
	private TextView typeTV;
	private EditText exceptioninfoET;
	/** 起始时间 */
	private long startTimeValue = System.currentTimeMillis();
	private final int RESULT = 0X83240;
	/** 选择的图片信息 **/
	private Bitmap bmp;
	/** 最多选择的图片数目 **/
	private int maxSize = 3;
	private LinearLayout imagesLayout;
	private ImageButton addImageBtn;
	/** 是否同步上传数据 */
	private CheckBox uploadData;
	/** 是否同步上传测试计划 */
	private CheckBox uploadTestPlan;
	/** 是否选择pccap文件 **/
	private CheckBox pccapData;
	/** 日期格式 */
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.getDefault());
	/** 上传工具类 */
	private FtpOperate ftp = new FtpOperate(SysExceptionCommitActivity.this);
	/** 处理删除过程 */
	private Handler handler = new MyHandler();
	/** 上传进度条 */
	private ProgressDialog progress;
	/** 全局对象 ***/
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private Date startDate = null;
	private Date endDate = null;
	private List<String> bitMapPaths = new LinkedList<String>();
	private String message = "";
	/** 文件名称 */
	private String fileName = "";
	private String KEY_EXCEPTION_UPLOADDATA = "com.walktour.gui.setting.sysroutine.SysExceptionCommitActivity.uploaddata";
	private String KEY_EXCEPTION_UPLOADTESTPLAN = "com.walktour.gui.setting.sysroutine.SysExceptionCommitActivity.uploadtestplan";
	private String KEY_EXCEPTION_UPLOAPCCAP = "com.walktour.gui.setting.sysroutine.SysExceptionCommitActivity.uploadpccap";
	private SharePreferencesUtil preferences = null;
	/**是否取消上传***/
	private boolean isCanceled=false;
	/**以上传大小**/
//	private long uploadSize=0;
//	private long totalSize=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		endDate = new Date();
		startDate = new Date(endDate.getTime() - 180000);
		preferences = SharePreferencesUtil.getInstance(context);
		printTime();
		findView();
	}

	/**
	 * 初始化视图
	 */
	private void findView() {
		setContentView(R.layout.sys_exception_commit);
		initTextView(R.id.title_txt).setText(R.string.exception_project_test);
		initImageButton(R.id.pointer).setOnClickListener(this);
		initButton(R.id.uploadDlog).setOnClickListener(this);
		this.uploadData = initCheckBox(R.id.txt_exception_2);
		this.uploadData.setChecked(preferences.getBoolean(KEY_EXCEPTION_UPLOADDATA, true));
		this.uploadTestPlan = initCheckBox(R.id.txt_exception_4);
		this.uploadTestPlan.setChecked(preferences.getBoolean(KEY_EXCEPTION_UPLOADTESTPLAN, true));
		this.pccapData = initCheckBox(R.id.txt_exception_5);
		this.pccapData.setChecked(preferences.getBoolean(KEY_EXCEPTION_UPLOAPCCAP, false));
		timeTV = initTextView(R.id.txt_exception_1);
		timeTV.setText(this.getShowDateTime(this.startTimeValue));
		typeTV = initTextView(R.id.txt_exception_3);
		exceptioninfoET = initEditText(R.id.exceptioninfo);
		imagesLayout = initLinearLayout(R.id.imageslayout);
		initRelativeLayout(R.id.txt_exception_1_layout).setOnClickListener(this);
		initRelativeLayout(R.id.txt_exception_3_layout).setOnClickListener(this);
		addImageBtn = initImageButton(R.id.click_exception_1);
		addImageBtn.setOnClickListener(this);
		this.uploadData.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.saveBoolean(KEY_EXCEPTION_UPLOADDATA, isChecked);
			}

		});
		this.uploadTestPlan.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.saveBoolean(KEY_EXCEPTION_UPLOADTESTPLAN, isChecked);
			}

		});

		this.pccapData.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.saveBoolean(KEY_EXCEPTION_UPLOAPCCAP, isChecked);
				fileTypeMap.put(FileType.PCAP.getFileTypeName(), isChecked);
				showFileTypes();
			}

		});

		try {
			fileName = getFilesDir() + "/config/config_ftp_setting.xml";
			parseFtpSetting(new FileInputStream(new File(fileName)), "log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.initFileType();
		this.showFileTypes();
	}

	/**
	 * 获得显示的时间
	 * 
	 * @param time
	 *            时间
	 * @return
	 */
	private String getShowDateTime(long time) {
		return UtilsMethod.sdFormatss.format(time).substring(0, 16);
	}

	/**
	 * 初始化文件类型选项
	 */
	private void initFileType() {
//		fileTypeMap.put(FileType.RCU.getFileTypeName(), true);
		fileTypeMap.put(FileType.DTLOG.getFileTypeName(), false);
		fileTypeMap.put(FileType.DCF.getFileTypeName(), false);
		fileTypeMap.put(FileType.PCAP.getFileTypeName(), preferences.getBoolean(KEY_EXCEPTION_UPLOAPCCAP, false));
		fileTypeMap.put(FileType.DDIB.getFileTypeName(), true);
		fileTypeMap.put(FileType.ORGRCU.getFileTypeName(), false);
		fileTypeMap.put(FileType.ECTI.getFileTypeName(), false);
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
			strArray[i] = type;
			checkedItems[i] = this.fileTypeMap.get(type);
			i++;
		}
		new BasicDialog.Builder(this).setTitle(R.string.sys_setting_data_upload_file_type)
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

						if (key.equals(FileType.PCAP.getFileTypeName())) {
							pccapData.setChecked(isChecked);
						}
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
			typeTV.setText(fileTypes);
		else
			typeTV.setText("");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.txt_exception_1_layout:
			showDate();
			break;
		case R.id.txt_exception_3_layout:
			showFileTypeDialog();
			break;
		case R.id.pointer:
			this.finish();
			break;
		case R.id.click_exception_1:
			Intent intent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, RESULT);
			break;
		case R.id.uploadDlog:
			this.uploadLog();
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RESULT) {
			if (resultCode == RESULT_OK) {
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				final View view = layoutInflater.inflate(R.layout.sys_exception_photo, null);
				// 选择图片,新增图片
				Uri uri = data.getData();
				ContentResolver cr = this.getContentResolver();
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					bmp = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
					final ImageView imageView = (ImageView) view.findViewById(R.id.click_exception_image);
					final Button btn = (Button) view.findViewById(R.id.click_exception_imagedel);
					imageView.setImageBitmap(bmp);
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, proj, null, null, null);
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					final String path = cursor.getString(column_index);
					bitMapPaths.add(path);
					btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							bitMapPaths.remove(path);
							imagesLayout.removeView(view);
							maxSize += 1;
							if (maxSize >= 1 && maxSize <= 3)
								addImageBtn.setVisibility(View.VISIBLE);
							else
								addImageBtn.setVisibility(View.GONE);
						}
					});
					imagesLayout.addView(view);
					maxSize -= 1;
					if (maxSize <= 0)
						addImageBtn.setVisibility(View.GONE);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void showDate() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_time_layout_new, null);
		final DatePicker dateP = (DatePicker) view.findViewById(R.id.date_picker);
		final TimePicker timeP = (TimePicker) view.findViewById(R.id.time_picker);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		dateP.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
		timeP.setIs24HourView(true);
		timeP.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timeP.setCurrentMinute(Calendar.MINUTE);
		new BasicDialog.Builder(this).setTitle(R.string.data_choose_data_time).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", dateP.getYear(), dateP.getMonth() + 1,
								dateP.getDayOfMonth()));
						sb.append("  ");
						sb.append(timeP.getCurrentHour()).append(":").append(timeP.getCurrentMinute());
						timeTV.setText(sb.toString() + "");
						endDate = new Date(dateP.getYear() - 1900, dateP.getMonth(), dateP.getDayOfMonth(),
								timeP.getCurrentHour(), timeP.getCurrentMinute());
						startDate = new Date(endDate.getTime() - 180000);

						printTime();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	private void printTime() {
		LogUtil.w(TAG, "startDate:" + DateUtils.formatDate(startDate, DateUtils.DATE_TIME_FORMAT));
		LogUtil.w(TAG, "endDate:" + DateUtils.formatDate(endDate, DateUtils.DATE_TIME_FORMAT));
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
			return null;
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
		Set<File> files = this.getUploadDataFiles();
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
			return null;
		}
		return zipFile;
	}

	private File createExceptionZipFile() throws Exception {
		File zipFile = new File(BASE_PATH + "exception.zip");
		if (zipFile.exists())
			zipFile.delete();
		zipFile.createNewFile();
		Set<File> files = this.getUploadExceitpinFiles();
		if (!files.isEmpty()) {
			ZipUtil.zip(files, zipFile);
		} else {
			return null;
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
	 * 获取要上传的库日志文件
	 * 
	 * @return
	 */
	private Set<File> getUploadExceitpinFiles() {
		Set<File> exceptionFiles = new HashSet<File>();
		try {
			// 1.写异常描述信息
			if (exceptioninfoET.getText().length() > 0) {
				File file = new File(BASE_PATH, "exceptioninfo.txt");
				com.walktour.Utils.FileUtil.writeToFile(file, exceptioninfoET.getText().toString() + "");
				exceptionFiles.add(file);
			}
			// 异常图片
			if (bitMapPaths.size() > 0) {
				for (String path : bitMapPaths) {
					File fl = new File(path);
					exceptionFiles.add(fl);
				}

			}

			if (preferences.getBoolean(KEY_EXCEPTION_UPLOADTESTPLAN, true)) {
				File fl = new File(TaskListDispose.getInstance().getFileName());
				exceptionFiles.add(fl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return exceptionFiles;
	}

	/**
	 * 获取要上传的数据文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private Set<File> getUploadDataFiles() throws Exception {
		Set<File> files = new HashSet<File>();
		List<DataModel> fileList = new ArrayList<DataModel>();
		DataManagerFileList dataManage = DataManagerFileList.getInstance(this);
		if (appModel.getSelectScene() == SceneType.HighSpeedRail || appModel.getSelectScene() == SceneType.Metro) {// 高铁和地铁按场景上传
			fileList.addAll(dataManage.getAllFileList(appModel.getSelectScene()));
		} else {
			fileList.addAll(DataManagerFileList.getInstance(this).getAllFileList(appModel.getBusinessTestScenes()));

			// fileList.addAll(dataManage.getAllFileList(TestType.DT));
			// fileList.addAll(dataManage.getAllFileList(TestType.CQT));
		}

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

	/**
	 * 上传日志文件
	 */
	private void uploadLog() {
		isCanceled=false;
		if(!MobileUtil.isWifiConnected(this)&&!MobileUtil.isMobileConnected(this)){
			ToastUtil.showToastShort(context, R.string.sys_alarm_speech_neterr);
			return;
		}
		this.progress=new ProgressDialog(this); 
		this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		this.progress.setTitle(this.getString(R.string.exception_project_test));
		this.progress.setMessage(getString(R.string.str_uploading)); 
		//设置可点击的按钮，最多有三个(默认情况下)
		this.progress.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.str_cancle),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) { 
                    	try {
                    		isCanceled=true;
							ftp.disconnect();
						} catch (IOException e) { 
							e.printStackTrace();
						}
                    	progress.dismiss();
                    }
                });
		this.progress.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.hide),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	progress.dismiss();
                    }
                });
		this.progress.show();
		new Thread() {
			@Override
			public void run() {
				try {
					handler.sendEmptyMessage(UPLOAD_START);
					boolean connect = false;
					for (String ftpIP : ftpIPList) {
						try {
							connect = ftp.connect(ftpIP, ftpPort, ftpUser, ftpPass, timeout);
						} catch (Exception e) {
							connect = false;
						}
						if (connect) {
							break;
						}
					} 
					if (connect) {
						File sysLog = createSysLogZipFile();
						File liblog = createLibLogZipFile();
						File data = null;
						if (uploadData.isChecked()) {
							data = createDataZipFile();
						}
						File exception = createExceptionZipFile();
						uploadFile(sysLog, getString(R.string.monitor_exception));
						uploadFile(liblog, getString(R.string.sys_setting_open_datasetlog));
						uploadFile(data, getString(R.string.info_data));
						uploadFile(exception, getString(R.string.monitor_exception));
						handler.sendEmptyMessage(UPLOAD_END); 
						closdDialog();
					} else {
						handler.sendEmptyMessage(UPLOAD_FAIL);
						closdDialog();
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(UPLOAD_FAIL);
					closdDialog();
				} finally {
					try {
						ftp.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
	}
 
	/**
	 * 关闭对话框
	 */
	private void closdDialog(){
		try { 
			Thread.sleep(5*1000);
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		progress.dismiss();
	}
	/**
	 * 上传文件到ftp服务器上
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void uploadFile(File file, String message) throws Exception {
		if (file == null||isCanceled)
			return;

		this.message = message;
		handler.sendEmptyMessage(UPLOAD_DOING);
		String imei = MyPhoneState.getInstance().getDeviceId(this);
		StringBuilder remoteName = new StringBuilder();
		remoteName.append("/").append(this.ftpCatalog).append("/").append(imei).append("/");
		remoteName.append(df.format(startDate)).append("_");
		remoteName.append(df.format(endDate)).append("_");
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
 
    
	/***
	 * 消息处理句柄
	 * 
	 * @author weirong.fan
	 *
	 */
	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {
		public MyHandler() {

		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPLOAD_START:
				progress.setMessage(getString(R.string.fleet_connecting));
			case UPLOAD_START_SUCCESS:
				progress.setMessage(getString(R.string.fleet_longin));
				break;
			case UPLOAD_START_FAILURE:
				progress.setMessage(getString(R.string.fleet_error_login));
				break;
			case UPLOAD_DOING:
				progress.setMessage(getString(R.string.str_uploading) + message); 
				break;
			case UPLOAD_FAIL:
				progress.setMessage(getString(R.string.str_unuploaderror)); 
				break;
			case UPLOAD_END:
				progress.setMessage(getString(R.string.exception_project_test_12) + "");
				break;
			}
			super.handleMessage(msg);  
		}
	}

	/***
	 * 解析高铁ftp配置文件
	 * 
	 * @param inputStream
	 *            文件输入
	 * @param tagName
	 *            要解析的标签，其他的忽略
	 * @throws Exception
	 *             异常
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
					} else if ("serverPort".equals(name)) {
						ftpPort = Integer.parseInt(UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverUser".equals(name)) {
						ftpUser = UtilsMethod.jem(parser.getAttributeValue(0));
					} else if ("serverPassword".equals(name)) {
						ftpPass = UtilsMethod.jem(parser.getAttributeValue(0));
					} else if ("serverCatalog".equals(name)) {
						ftpCatalog = "log";
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

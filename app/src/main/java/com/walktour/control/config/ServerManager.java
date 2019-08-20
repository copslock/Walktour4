package com.walktour.control.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.text.TextUtils;

import com.dinglicom.UnicomInterface;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus.DownloadStatus;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.control.bean.Verify;
import com.walktour.gui.R;
import com.walktour.gui.listener.ServerStatus;
import com.walktour.gui.listener.ServerStatusListener;
import com.walktour.gui.task.parsedata.xml.btu.model.BtuEvent;
import com.walktour.model.FtpJob;
import com.walktour.model.FtpServerModel;
import com.walktour.model.WalktourEvent;
import com.walktour.service.app.DataTransService;
import com.walktour.service.app.datatrans.model.UploadFileModel;
import com.walktour.workorder.model.ServerInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 服务器管理
 */
@SuppressLint("SdCardPath")
public class ServerManager {

	/** 保存BTU平台密码，测试计划版本号的文件 */
	private final static String FILE_BTU_BOXID = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/btuboxid.txt";
	private final static String FILE_ATU_BOXID = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/atuboxid.txt";
	/** 最新密码的文件 */
	public final static String FILE_BTU_PWD = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/btu_%s.txt";
	public final static String FILE_ATU_PWD = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/atu_%s.txt";
	/** BTU平台下载的测试计划 */
	public final static String FILE_BTU_TASK = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/task_btu.xml";
	public final static String FILE_ATU_TASK = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/task_atu.xml";
	/** BTU平台事件 */
	private final static String FILE_BTU_EVENT = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/event_btu.txt";
	private final static String FILE_ATU_EVENT = Environment.getExternalStorageDirectory().getPath()
			+ "/Walktour/config/event_atu.txt";

	private final SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS", Locale.getDefault());

	public static final int SERVER_FLEET = 0;
	public static final int SERVER_HTTPS = 1;
	public static final int SERVER_FTP = 2;
	public static final int SERVER_BTU = 3;// 移动的DTLog平台
	public static final int SERVER_ATU = 4;// 移动的DTLog平台
	/** 联通平台 */
	public static final int SERVER_UNICOM = 5;
	/**
	 * 寅时服务器
	 */
	public static final int SERVER_INNS = 6;

	public static final int UPLOAD_NETWORK_ALL = 0;
	public static final int UPLOAD_NETWORK_ONLY_WIFI = 1;
	public static final int UPLOAD_NETWORK_ONLY_MOBILE = 2;

	private static final String KEY_SERVER = "setting_server";
	private static final String KEY_FLEET_SERVER_TYPE = "setting_fleet_server_type";
	private static final String KEY_UPLOAD_FLEET_IP = "setting_upload_fleet_ip";
	private static final String KEY_UPLOAD_FLEET_PORT = "setting_upload_fleet_port";
	private static final String KEY_DOWNLOAD_FLEET_IP = "setting_download_fleet_ip";
	private static final String KEY_DOWNLOAD_FLEET_PORT = "setting_download_fleet_port";
	private static final String KEY_FLEET_ACCOUNT = "setting_fleet_account";
	private static final String KEY_FLEET_PASSWORD = "setting_fleet_password";
	private static final String KEY_DTLOG_IP = "setting_dtlog_ip";
	private static final String KEY_DTLOG_PORT = "setting_dtlog_port";
	private static final String KEY_DTLOG_MOUDLE_NUM = "setting_dtlog_moudle_num";
	private static final String KEY_DTLOG_SVER = "SVersion";
	private static final String KEY_DTLOG_CVER = "CVersion";
	private static final String KEY_DTLOG_SCHEME = "Scheme";
	private static final String KEY_HTTPS_URL = "setting_https_url";
	private static final String KEY_HTTPS_USER = "setting_https_user";
	private static final String KEY_HTTPS_PWD = "setting_https_pass";
	private static final String KEY_HTTPS_DRIVE = "setting_https_drive";
	private static final String KEY_HTTPS_MARKET = "setting_https_market";
	private static final String KEY_HTTPS_SCOPE = "setting_https_scope";
	private static final String KEY_HTTPS_DESCRITION = "setting_https_descrition";
	private static final String KEY_HTTPS_EVENT = "setting_https_event";
	private static final String KEY_FTP_NAME = "setting_ftp_name";// 此名字引用FTP设置的中的名字
	private static final String KEY_FTP_PATH = "setting_ftp_path";
	private static final String KEY_NETWORK_NAME = "setting_network";
	private static final String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address";
	private static final String KEY_EMAIL_SEND_PASSWORD = "key_email_send_password";
	private static final String KEY_EMAIL_SEND_PORT = "key_email_send_port";
	private static final String KEY_EMAIL_SEND_SERVER = "key_email_send_server";
	private static final String KEY_EMAIL_RECIVER_EMAIL = "key_email_reciver_email";
	private static final String KEY_EMAIL_NOTIFY_TOGGLE = "key_email_notify_toggle";
	private static final String KEY_FLEET_EVENT = "fleet_event";
	private static final String KEY_AUTO_UPGRADE = "auto_upgrade";
	private static final String KEY_COMPREHENSIVE_TEST_ENVIRONMENT="comprehensive_test_enviroment";
	private static final String KEY_SHOW_L1_L2_COMMAND="is_show_l1_l2_command";
	private static final String KEY_SHOW_L1_L2_COMMAND_BUFFER="is_show_l1_l2_command_buffer";
	private static final String KEY_AUTO_TTPS = "auto_tip";
	private static final String KEY_LOCK_LTECA = "locklteca";
	private static final String KEY_IS_AUTO_UPLOAD = "key_is_auto_upload";
	private static final String KEY_CSFB_ANALYSIS = "csfb_analysys";
	private static final String KEY_IS_UPLOAD_MOS_FILE = "key_is_upload_mos_file";
	private static final String KEY_IS_UPLOAD_TAGGING_FILE = "key_is_upload_tagging_file";
	/** 选择的历史的服务器还原服务器使用 **/
	private static final String KEY_HISTORY_SERVER_SELECT = "key_is_history_server_select";
	//寅时服务器地址和UserId
	private static final String KEY_INNS_SERVER_IP = "setting_inns_server_ip";
	private static final String KEY_INNS_SERVER_USER_ID = "setting_inns_server_user_id";
	// 联通统一平台相关服务器信息
	// 以下两个是公开的接入ip和端口，可以获取其它（业务，回传）的ip和端口、账号等
	private static final String KEY_UNICOM_ACCESS_IP = "setting_unicom_access_ip";
	private static final String KEY_UNICOM_ACCESS_PORT = "setting_unicom_access_port";
	private static final String KEY_UNICOM_ACCESS_ACCOUNT = "setting_unicom_access_account";
	private static final String KEY_UNICOM_ACCESS_PASSWORD = "setting_unicom_access_password";
	// 以下两个是业务服务器地址和端口
	private static final String KEY_UNICOM_TASK_IP = "setting_unicom_task_ip";
	private static final String KEY_UNICOM_TASK_PORT = "setting_unicom_task_port";
	private static final String KEY_UNICOM_TASK_ACCOUNT = "setting_unicom_task_account";
	private static final String KEY_UNICOM_TASK_PASSWORD = "setting_unicom_task_password";
	// 以下两个是Ftp上传服务器地址和端口
	private static final String KEY_UNICOM_FTP_UPLOAD_IP = "setting_unicom_ftp_upload_ip";
	private static final String KEY_UNICOM_FTP_UPLOAD_PORT = "setting_unicom_ftp_upload_port";
	private static final String KEY_UNICOM_FTP_UPLOAD_ACCOUNT = "setting_unicom_ftp_upload_account";
	private static final String KEY_UNICOM_FTP_UPLOAD_PASSWORD = "setting_unicom_ftp_upload_password";
	// 以下两个是Ftp下载服务器地址和端口
	private static final String KEY_UNICOM_FTP_DOWNLOAD_IP = "setting_unicom_ftp_download_ip";
	private static final String KEY_UNICOM_FTP_DOWNLOAD_PORT = "setting_unicom_ftp_download_port";
	private static final String KEY_UNICOM_FTP_DOWNLOAD_ACCOUNT = "setting_unicom_ftp_download_account";
	private static final String KEY_UNICOM_FTP_DOWNLOAD_PASSWORD = "setting_unicom_ftp_download_password";
	// 以下两个是Http上传服务器地址和端口
	private static final String KEY_UNICOM_HTTP_UPLOAD_IP = "setting_unicom_http_upload_ip";
	private static final String KEY_UNICOM_HTTP_UPLOAD_PORT = "setting_unicom_http_upload_port";
	private static final String KEY_UNICOM_HTTP_UPLOAD_ACCOUNT = "setting_unicom_http_upload_account";
	private static final String KEY_UNICOM_HTTP_UPLOAD_PASSWORD = "setting_unicom_http_upload_password";
	// 以下两个是Http下载服务器地址和端口
	private static final String KEY_UNICOM_HTTP_DOWNLOAD_IP = "setting_unicom_http_download_ip";
	private static final String KEY_UNICOM_HTTP_DOWNLOAD_PORT = "setting_unicom_http_download_port";
	private static final String KEY_UNICOM_HTTP_DOWNLOAD_ACCOUNT = "setting_unicom_http_download_account";
	private static final String KEY_UNICOM_HTTP_DOWNLOAD_PASSWORD = "setting_unicom_http_download_password";
	// 所有服务器类型
	private int[] servers = new int[] { UnicomInterface.TASK_SERVER, UnicomInterface.FTP_UPLOAD_SERVER,
			UnicomInterface.FTP_DOWNLOAD_SERVER, UnicomInterface.HTTP_UPLOAD_SERVER, UnicomInterface.HTTP_DOWNLOAD_SERVER };

	/**
	 * AT&T的market区域(联邦地名)
	 */
	public static final String[] ATT_MARKET = new String[] { "AL/MS", "AR/OK", "AZ/NM", "Carolinas", "EPA/SNJ/DE",
			"Georgia", "HQ", "IL/WI", "Los Angeles", "Louisiana", "MI/IN", "Minn/N Plains", "MO/KS", "New England", "NFL",
			"Northern CA", "NTX", "NYC/NNJ", "OH/WPA", "Pacific NW", "PR/VI", "Rocky Mnt", "SD/LV/HI", "SFL", "STX", "TN/KY",
			"Upstate NY", "VA/WV", "WA/BA" };

	/**
	 * AT&T的测试类型
	 */
	public static final String[] ATT_SCOPE = new String[] { "ADHOC", "GNG", "Realtime" };

	private Context mContext;
	private SharedPreferences share;
	private ServerStatusListener serverListener = null;
	private ApplicationModel appModel;
	/**
	 * 记录要上传的文件类型，Key为文件后缀,Value为是否要上传
	 */
	private HashMap<String, Boolean> uploadHM = new HashMap<>();
	private static boolean genBtuEvent = false;
	private static ServerManager instance;

	private ServerManager(Context context) {
		this.mContext = context;
		appModel = ApplicationModel.getInstance();
		initPreference();
	}

	public static ServerManager getInstance(Context context) {
		if (instance == null) {
			instance = new ServerManager(context.getApplicationContext());
			genBtuEvent = (instance.getUploadServer() == SERVER_BTU || instance.getUploadServer() == SERVER_ATU);
		}
		return instance;
	}

	/**
	 * 按默认初始化SharedPreferences
	 */
	private void initPreference() {
		share = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
		if (!share.contains(ServerManager.KEY_SERVER)) {
			Editor editor = share.edit();
			editor.putInt(KEY_SERVER, SERVER_FLEET);
			editor.apply();
		}
		if (!share.contains(ServerManager.KEY_NETWORK_NAME)) {
			Editor editor = share.edit();
			editor.putInt(KEY_NETWORK_NAME, 0);
			editor.apply();
		}
		if (!share.contains(ServerManager.KEY_AUTO_UPGRADE)) {
			Editor editor = share.edit();
			editor.putBoolean(KEY_AUTO_UPGRADE, false);
			editor.apply();
		}
	}

	static void resetInstance(Context context) {
		instance = null;
		getInstance(context);
	}

	public String[] getUploadServers() {
		String[] serverNames = new String[7];
		serverNames[0] = mContext.getResources().getString(R.string.setting_server_fleet);
		serverNames[1] = mContext.getResources().getString(R.string.setting_server_https);
		serverNames[2] = mContext.getResources().getString(R.string.setting_server_ftp);
		serverNames[3] = mContext.getResources().getString(R.string.setting_server_btu);
		serverNames[4] = mContext.getResources().getString(R.string.setting_server_atu);
		serverNames[5] = mContext.getResources().getString(R.string.setting_server_union);
		serverNames[6] = mContext.getResources().getString(R.string.setting_server_inns);
		return serverNames;
	}

	public String[] getUploadNetWorks() {
		return mContext.getResources().getStringArray(R.array.setting_server_network);
	}

	/** 上传的服务器 */
	public int getUploadServer() {
		return share.getInt(KEY_SERVER, SERVER_FLEET);
	}

	public void setUploadServer(int server) {
		if(this.getUploadServer() == server)
			return;
		String[] servers = this.getUploadServers();
		if (server >= 0 && server < servers.length) {
			share.edit().putInt(KEY_SERVER, server).apply();
		}

		// 选其BTU以外的平台时，把BTU平台计划版本号留空
		genBtuEvent = (server == SERVER_BTU || server == SERVER_ATU);
		setDTLogCVersion(0);
	}

	/** 上传的服务器 */
	public String getUploadServerName() {
		int index = getUploadServer();
		String[] servers = this.getUploadServers();
		if (index >= 0 && index < servers.length && servers.length > 0) {
			return servers[index];
		}
		return "";
	}

	/** 上传的服务器 */
	public int getUploadNetWork() {
		return share.getInt(KEY_NETWORK_NAME, 0);
	}

	public boolean setUploadNetWork(int type) {
		return share.edit().putInt(KEY_NETWORK_NAME, type).commit();

	}

	/***
	 * 设置历史服务器
	 *
	 * @param selectServer
	 */
	public void setHistoryServer(int selectServer) {
		share.edit().putInt(KEY_HISTORY_SERVER_SELECT, selectServer).apply();
	}

	/***
	 * 获取历史选择的服务器
	 *
	 * @return
	 */
	public int getHistoryServer() {
		return share.getInt(KEY_HISTORY_SERVER_SELECT, SERVER_FLEET);
	}

	/** 上传的服务器 */
	public String getUploadNetWorkName() {
		int index = getUploadNetWork();
		String[] servers = mContext.getResources().getStringArray(R.array.setting_server_network);
		if (index >= 0 && index < servers.length && servers.length > 0) {
			return servers[index];
		}
		return "";
	}

	/**
	 * 根据服务器类型，初始化上传文件类型
	 *
	 * @param context
	 * @param server
	 *          服务器类型
	 */
	public void initUploadFileType(Context context, int server) {
		uploadHM.clear();
		switch (server) {
			case ServerManager.SERVER_FLEET:
				if (!share.contains(FileType.ORGRCU.getFileTypeName()))
					share.edit().putBoolean(FileType.ORGRCU.getFileTypeName(), true).apply();
				if (!share.contains(FileType.DCF.getFileTypeName()))
					share.edit().putBoolean(FileType.DCF.getFileTypeName(), false).apply();
				if (!share.contains(FileType.CU.getFileTypeName()))
					share.edit().putBoolean(FileType.CU.getFileTypeName(), false).apply();
				if (!share.contains(FileType.DTLOG.getFileTypeName()))
					share.edit().putBoolean(FileType.DTLOG.getFileTypeName(), false).apply();

				// 华为工单专业接口
				boolean isHuawei = appModel.isHuaWeiTest();
				if (isHuawei) {
					if (!share.contains(FileType.PCAP.getFileTypeName()))
						share.edit().putBoolean(FileType.PCAP.getFileTypeName(), true).apply();
					uploadHM.put(FileType.PCAP.getFileTypeName(), share.getBoolean(FileType.PCAP.getFileTypeName(), true));
				}

				if(appModel.hasOrgRcu()) {
					uploadHM.put(FileType.ORGRCU.getFileTypeName(), share.getBoolean(FileType.ORGRCU.getFileTypeName(), true));
				}
				if(appModel.hasDcf()){
					uploadHM.put(FileType.DCF.getFileTypeName(), share.getBoolean(FileType.DCF.getFileTypeName(), false));
				}
				if(appModel.showInfoTypeCu()){
					uploadHM.put(FileType.CU.getFileTypeName(), share.getBoolean(FileType.CU.getFileTypeName(), false));
				}
				if (!isHuawei && (appModel.isAtu() || appModel.isBtu())) {
					uploadHM.put(FileType.DTLOG.getFileTypeName(), share.getBoolean(FileType.DTLOG.getFileTypeName(), true));
				}

				if(appModel.showInfoTypeEcti()) {
					if (!share.contains(FileType.ECTI.getFileTypeName()))
						share.edit().putBoolean(FileType.ECTI.getFileTypeName(), false).apply();
					uploadHM.put(FileType.ECTI.getFileTypeName(), share.getBoolean(FileType.ECTI.getFileTypeName(), false));
				}
				break;
			case ServerManager.SERVER_ATU:
			case ServerManager.SERVER_BTU:
				if (!share.contains(FileType.DTLOG.getFileTypeName()))
					share.edit().putBoolean(FileType.DTLOG.getFileTypeName(), true).apply();
				if(appModel.hasDTLog()){
					uploadHM.put(FileType.DTLOG.getFileTypeName(), share.getBoolean(FileType.DTLOG.getFileTypeName(), true));
				}
				break;
			case ServerManager.SERVER_HTTPS:
				if (!share.contains(FileType.ORGRCU.getFileTypeName()))
					share.edit().putBoolean(FileType.ORGRCU.getFileTypeName(), true).apply();
				uploadHM.put(FileType.ORGRCU.getFileTypeName(), share.getBoolean(FileType.ORGRCU.getFileTypeName(), true));
				break;
			case ServerManager.SERVER_FTP:
				if (!share.contains(FileType.ORGRCU.getFileTypeName()))
					share.edit().putBoolean(FileType.ORGRCU.getFileTypeName(), true).apply();
				if (!share.contains(FileType.DTLOG.getFileTypeName()))
					share.edit().putBoolean(FileType.DTLOG.getFileTypeName(), false).apply();
				if (!share.contains(FileType.DCF.getFileTypeName()))
					share.edit().putBoolean(FileType.DCF.getFileTypeName(), false).apply();
				if (!share.contains(FileType.PCAP.getFileTypeName()))
					share.edit().putBoolean(FileType.PCAP.getFileTypeName(), false).apply();
				if (!share.contains(FileType.DDIB.getFileTypeName()))
					share.edit().putBoolean(FileType.DDIB.getFileTypeName(), false).apply();
				if (!share.contains(FileType.ORGRCU.getFileTypeName()))
					share.edit().putBoolean(FileType.ORGRCU.getFileTypeName(), false).apply();

//				uploadHM.put(FileType.ORGRCU.getFileTypeName(), share.getBoolean(FileType.ORGRCU.getFileTypeName(), true));
				if(appModel.hasDTLog()){
					uploadHM.put(FileType.DTLOG.getFileTypeName(), share.getBoolean(FileType.DTLOG.getFileTypeName(), false));
				}
				if(appModel.hasDcf()){
					uploadHM.put(FileType.DCF.getFileTypeName(), share.getBoolean(FileType.DCF.getFileTypeName(), false));
				}
				if(appModel.hasPCap()){
					uploadHM.put(FileType.PCAP.getFileTypeName(), share.getBoolean(FileType.PCAP.getFileTypeName(), false));
				}
				uploadHM.put(FileType.DDIB.getFileTypeName(), share.getBoolean(FileType.DDIB.getFileTypeName(), false));
				if(appModel.hasOrgRcu()){
					uploadHM.put(FileType.ORGRCU.getFileTypeName(), share.getBoolean(FileType.ORGRCU.getFileTypeName(), false));
				}
				break;
			case ServerManager.SERVER_UNICOM:
				if (!share.contains(FileType.DCF.getFileTypeName()))
					share.edit().putBoolean(FileType.DCF.getFileTypeName(), true).apply();
				if(appModel.hasDcf()){
					uploadHM.put(FileType.DCF.getFileTypeName(), share.getBoolean(FileType.DCF.getFileTypeName(), true));
				}
				break;


			case ServerManager.SERVER_INNS:
				if (!share.contains(FileType.TXT.getFileTypeName()))
					share.edit().putBoolean(FileType.TXT.getFileTypeName(), true).apply();
				uploadHM.put(FileType.TXT.getFileTypeName(), share.getBoolean(FileType.TXT.getFileTypeName(), true));
				break;
		}

		saveUploadFileTypes();
	}

	/**
	 * 获得要上传的文件类型
	 *
	 * @return
	 */
	public HashMap<String, Boolean> getUploadFileTypes(Context context) {
		if (uploadHM.isEmpty()) {
			initUploadFileType(context, getUploadServer());
		}
		return uploadHM;
	}

	/**
	 * 函数功能：保存要上传的文件类型
	 */
	public void saveUploadFileTypes() {
		for (String fileType:uploadHM.keySet()) {
			share.edit().putBoolean(fileType, uploadHM.get(fileType)).apply();
		}
	}

	/**
	 * 函数功能：上传文件
	 *
	 * @param context
	 *          上下文
	 * @param operateType
	 *          文件的类型
	 */
	public void uploadFile(Context context, ServerOperateType operateType, List<UploadFileModel> uploadFiles) {
		Intent serviceIntent = new Intent(context, DataTransService.class);
		serviceIntent.putExtra(DataTransService.EXTRA_KEY_OPERATE_TYPE_NAME, operateType);
		if (uploadFiles != null && !uploadFiles.isEmpty()) {
			UploadFileModel[] files = uploadFiles.toArray(new UploadFileModel[uploadFiles.size()]);
			if (this.isUploadMOSFile()) {
				for (UploadFileModel fileModel : files) {
					Set<FileType> fileTypes = new HashSet<>();
					Collections.addAll(fileTypes,fileModel.getFileTypes());
					fileTypes.add(FileType.MOSZIP);
					fileModel.setFileTypes(fileTypes);
				}
			}

			if(this.isUploadTaggingFile()){//上传标注文件
				for (UploadFileModel fileModel : files) {
					Set<FileType> fileTypes = new HashSet<>();
					Collections.addAll(fileTypes,fileModel.getFileTypes());
					fileTypes.add(FileType.MIXZIP);
					fileModel.setFileTypes(fileTypes);
				}
			}
			//默认上传标注标注的内容
			serviceIntent.putExtra(DataTransService.EXTRA_KEY_UPLOAD_FILES, files);
		}
		context.startService(serviceIntent);
	}

	/**
	 * 是否有配置fleet服务器
	 *
	 * @return
	 */
	public boolean hasSetFleetServer() {
		String ip = getUploadFleetIp();
		int port = getUploadFleetPort();

		// ip或者port设置不正确时通知用户
		if (Verify.isIp(ip) && Verify.isPort(port + "")) {
			return true;
		}

		return false;
	}

	public String getUploadFleetIp() {
		return share.getString(KEY_UPLOAD_FLEET_IP, "");
	}

	public boolean getFleetServerType() {
		return share.getBoolean(KEY_FLEET_SERVER_TYPE, false);
	}

	public boolean setFleetServerType(boolean isChecked) {
		return share.edit().putBoolean(KEY_FLEET_SERVER_TYPE, isChecked).commit();
	}

	public boolean setUploadFleetIp(String ip) {
		return share.edit().putString(KEY_UPLOAD_FLEET_IP, ip).commit();
	}

	public int getUploadFleetPort() {
		return share.getInt(KEY_UPLOAD_FLEET_PORT, 0);
	}

	public boolean setUploadFleetPort(int port) {
		return share.edit().putInt(KEY_UPLOAD_FLEET_PORT, port).commit();
	}

	public String getDownloadFleetIp() {
		return share.getString(KEY_DOWNLOAD_FLEET_IP, "");
	}

	public boolean setDownloadFleetIp(String ip) {
		return share.edit().putString(KEY_DOWNLOAD_FLEET_IP, ip).commit();
	}

	public int getDownloadFleetPort() {
		return share.getInt(KEY_DOWNLOAD_FLEET_PORT, 0);
	}

	public boolean setDownloadFleetPort(int port) {
		return share.edit().putInt(KEY_DOWNLOAD_FLEET_PORT, port).commit();
	}

	public String getFleetAccount() {
		return share.getString(KEY_FLEET_ACCOUNT, "");
	}

	public boolean setFleetAccount(String account) {
		return share.edit().putString(KEY_FLEET_ACCOUNT, account).commit();
	}

	public String getFleetPassword() {
		return share.getString(KEY_FLEET_PASSWORD, "");
	}

	public boolean setFleetPassword(String password) {
		return share.edit().putString(KEY_FLEET_PASSWORD, password).commit();
	}

	public String getDTLogIp() {
		return share.getString(KEY_DTLOG_IP, "221.176.65.13");
	}

	public boolean setDTLogIp(String ip) {
		return share.edit().putString(KEY_DTLOG_IP, ip).commit();
	}

	public int getDTLogPort() {
		return share.getInt(KEY_DTLOG_PORT, 8082);
	}

	public boolean setDTLogPort(int port) {
		return share.edit().putInt(KEY_DTLOG_PORT, port).commit();
	}

	public String getDTLogBoxId() {
		if (this.getUploadServer() == SERVER_BTU) {
			StringBuffer sb = getTextFromFile(new File(FILE_BTU_BOXID));
			return sb.toString().trim();
		}
		StringBuffer sb = getTextFromFile(new File(FILE_ATU_BOXID));
		return sb.toString().trim();
	}

	public void setDTLogBoxId(String boxId) {
		if (this.getUploadServer() == SERVER_BTU) {
			writeTex2File(FILE_BTU_BOXID, boxId);
		} else {
			writeTex2File(FILE_ATU_BOXID, boxId);
		}
	}

	public int getDTLogMoudleNum() {
		return share.getInt(KEY_DTLOG_MOUDLE_NUM, 1);
	}

	public boolean setDTLogMoudleNum(int moudleNum) {
		return share.edit().putInt(KEY_DTLOG_MOUDLE_NUM, moudleNum).commit();
	}

	public String getDTLogNewPwd() {
		if (this.getUploadServer() == SERVER_BTU) {
			StringBuffer sb = getTextFromFile(new File(String.format(FILE_BTU_PWD, getDTLogBoxId())));
			return sb.toString().trim();
		}
		StringBuffer sb = getTextFromFile(new File(String.format(FILE_ATU_PWD, getDTLogBoxId())));
		return sb.toString().trim();
	}

	/**
	 * 函数功能：设置新密码
	 *
	 * @param newPassword
	 * @return
	 */
	public void setDTLogNewPwd(String newPassword) {
		if (this.getUploadServer() == SERVER_BTU) {
			writeTex2File(String.format(FILE_BTU_PWD, getDTLogBoxId()), newPassword);
		} else {
			writeTex2File(String.format(FILE_ATU_PWD, getDTLogBoxId()), newPassword);
		}
	}

	public String getDTLogSVersion() {
		return share.getString(KEY_DTLOG_SVER, "1");
	}

	/**
	 * 函数功能：设置软件版本号
	 *
	 * @param sVersion
	 * @return
	 */
	public void setDTLogSVersion(String sVersion) {
		share.edit().putString(KEY_DTLOG_SVER, sVersion).commit();
	}

	public int getDTLogCVersion() {
		int version = 0;
		try {
			version = share.getInt(KEY_DTLOG_CVER, 0);
		} catch (Exception e) {

		}
		return version;
	}

	public void setDTLogCVersion(int cVersion) {
		share.edit().putInt(KEY_DTLOG_CVER, cVersion).commit();
	}

	/**
	 * 函数功能：测试方案号
	 *
	 * @return
	 */
	public int getDTLogScheme() {
		int result = share.getInt(KEY_DTLOG_SCHEME, 0);
		return result;
	}

	public void setDTLogScheme(int scheme) {
		share.edit().putInt(KEY_DTLOG_SCHEME, scheme).commit();
	}

	public boolean genDTLogEvent() {
		return genBtuEvent;
	}

	/**
	 * 函数功能：从文本文件获取事件
	 *
	 * @return
	 */
	public ArrayList<BtuEvent> popDTLogEventList() {
		ArrayList<BtuEvent> result = new ArrayList<BtuEvent>();
		// File file;
		// if (this.getUploadServer() == SERVER_BTU) {
		// file=new File(FILE_BTU_EVENT);
		// }else{
		// file=new File(FILE_ATU_EVENT);
		// }
		StringBuffer sb = getTextFromFile(new File(FILE_BTU_EVENT));
		String[] eventLines = sb.toString().split("\n");
		for (String line : eventLines) {
			String[] params = line.split("\t");
			try {
				long time = sdFormat.parse(params[0]).getTime();
				int code = Integer.parseInt(params[1], 16);
				int moudle = Integer.parseInt(params[2].trim());
				BtuEvent event = new BtuEvent(time, code, moudle);
				result.add(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 清空事件
		if (this.getUploadServer() == SERVER_BTU) {
			writeTex2File(FILE_BTU_EVENT, "");
		} else {
			writeTex2File(FILE_ATU_EVENT, "");
		}
		return result;
	}

	/**
	 * 函数功能： 把事件写入文本中，注意此函数是I/O操作，不要在UI线程里执行
	 *
	 * @param btuEvent
	 */
	public synchronized void pushDTLogEvent(BtuEvent btuEvent) {
		if (btuEvent != null) {
			String time = sdFormat.format(btuEvent.getTime());
			int code = btuEvent.getCode();
			String line = String.format(Locale.getDefault(), "%s\t%x\t%d\r\n", time, code, btuEvent.getMoudle());
			if (this.getUploadServer() == SERVER_BTU) {
				appendText2File(FILE_BTU_EVENT, line);
			} else {
				appendText2File(FILE_ATU_EVENT, line);
			}
		}
	}

	public String getHttpsUrl() {
		return share.getString(KEY_HTTPS_URL, "");
	}

	public boolean setHttpsUrl(String url) {
		return share.edit().putString(KEY_HTTPS_URL, url).commit();
	}

	public String getHttpsUsername() {
		return share.getString(KEY_HTTPS_USER, "");
	}

	public boolean setHttpsUsername(String userName) {
		return share.edit().putString(KEY_HTTPS_USER, userName).commit();
	}

	public String getHttpsPass() {
		return share.getString(KEY_HTTPS_PWD, "");
	}

	public boolean setHttpsPass(String pwd) {
		return share.edit().putString(KEY_HTTPS_PWD, pwd).commit();
	}

	public String getHttpsDrive() {
		return share.getString(KEY_HTTPS_DRIVE, "");
	}

	public boolean setHttpsDrive(String drive) {
		return share.edit().putString(KEY_HTTPS_DRIVE, drive).commit();
	}

	public String getHttpsMarket() {
		return share.getString(KEY_HTTPS_MARKET, "");
	}

	public boolean setHttpsMarket(String market) {
		return share.edit().putString(KEY_HTTPS_MARKET, market).commit();
	}

	public int getHttpsMarketPosition() {
		String market = getHttpsMarket();
		int result = 0;
		for (int i = 0; i < ATT_MARKET.length; i++) {
			if (market.equals(ATT_MARKET[i])) {
				result = i;
				break;
			}
		}
		return result;
	}

	public String getHttpsScope() {
		return share.getString(KEY_HTTPS_SCOPE, "");
	}

	public boolean setHttpsScope(String scope) {
		return share.edit().putString(KEY_HTTPS_SCOPE, scope).commit();
	}

	public int getHttpsScopePosition() {
		String scope = getHttpsScope();
		int result = 0;
		for (int i = 0; i < ATT_SCOPE.length; i++) {
			if (scope.equals(ATT_SCOPE[i])) {
				result = i;
				break;
			}
		}
		return result;
	}

	public String getHttpsDescription() {
		return share.getString(KEY_HTTPS_DESCRITION, "");
	}

	public boolean setHttpsDescription(String description) {
		return share.edit().putString(KEY_HTTPS_DESCRITION, description).commit();
	}

	public String getHttpsEvent() {
		return share.getString(KEY_HTTPS_EVENT, "");
	}

	public boolean setHttpsEvent(String event) {
		return share.edit().putString(KEY_HTTPS_EVENT, event).commit();
	}

	/**
	 * @return 要 上传的FTP服务器名
	 */
	public String getFtpName() {
		return share.getString(KEY_FTP_NAME, "");
	}

	public boolean setFtpName(String ftpName) {
		return share.edit().putString(KEY_FTP_NAME, ftpName).commit();
	}

	public String getFtpPath() {
		return share.getString(KEY_FTP_PATH, "/");
	}

	public boolean setFtpPath(String ftpPath) {
		if (StringUtil.isNullOrEmpty(ftpPath)) {
			ftpPath = "/";
		}

		if (!ftpPath.endsWith("/")) {
			ftpPath += "/";
		}
		return share.edit().putString(KEY_FTP_PATH, ftpPath).commit();
	}

	public boolean setEmailSendAddress(String address) {
		return share.edit().putString(KEY_EMAIL_SEND_ADDRESS, address).commit();
	}

	public boolean setEmailSendPassoword(String password) {
		return share.edit().putString(KEY_EMAIL_SEND_PASSWORD, password).commit();
	}

	public boolean setEmailSendPort(String port) {
		return share.edit().putString(KEY_EMAIL_SEND_PORT, port).commit();
	}

	public boolean setEmailSendServer(String server) {
		return share.edit().putString(KEY_EMAIL_SEND_SERVER, server).commit();
	}

	public boolean setEmailReciverAddress(String address) {
		return share.edit().putString(KEY_EMAIL_RECIVER_EMAIL, address).commit();
	}

	public boolean setEmailNotifyToggle(boolean isCheck) {
		return share.edit().putBoolean(KEY_EMAIL_NOTIFY_TOGGLE, isCheck).commit();
	}

	public boolean setAutoTip(boolean isCheck) {
		return share.edit().putBoolean(KEY_AUTO_TTPS, isCheck).commit();
	}

	public boolean setLockLTECA(int caState) {
		return share.edit().putInt(KEY_LOCK_LTECA, caState).commit();
	}

	public boolean setAutoUpload(boolean isCheck) {
		return share.edit().putBoolean(KEY_IS_AUTO_UPLOAD, isCheck).commit();
	}

	public String getEmailSendAddress() {
		return share.getString(KEY_EMAIL_SEND_ADDRESS, "dinglicom.email@gmail.com");
	}

	public String getEmailSendPassoword() {
		return share.getString(KEY_EMAIL_SEND_PASSWORD, "dinglicom");
	}

	public String getEmailSendPort() {
		return share.getString(KEY_EMAIL_SEND_PORT, "465");
	}

	public String getEmailSendServer() {
		return share.getString(KEY_EMAIL_SEND_SERVER, "smtp.gmail.com");
	}

	public String getEmailReciverAddress() {
		return share.getString(KEY_EMAIL_RECIVER_EMAIL, "");
	}

	public boolean isEmailNotifyToggle() {
		return share.getBoolean(KEY_EMAIL_NOTIFY_TOGGLE, false);
	}

	public boolean getAutoTip() {
		return share.getBoolean(KEY_AUTO_TTPS, false);
	}

	public int getLockLTECA() {
		return share.getInt(KEY_LOCK_LTECA, 0);
	}

	public boolean isAutoUpload() {
		return share.getBoolean(KEY_IS_AUTO_UPLOAD, false);
	}

	/**
	 * 获得当前CSFB异常分析开关状态
	 *
	 * @return
	 */
	public boolean getCsfbAnalysis() {
		return share.getBoolean(KEY_CSFB_ANALYSIS, false);
	}

	/**
	 * 设置CSFB异常分析开关状态
	 *
	 * @param isCheck
	 * @return
	 */
	public boolean setCsfbAnalysis(boolean isCheck) {
		return share.edit().putBoolean(KEY_CSFB_ANALYSIS, isCheck).commit();
	}

	/**
	 * @return 当前的上传服务器是否已经配置
	 */
	public boolean hasUploadServerSet() {
		if (getUploadServer() == ServerManager.SERVER_HTTPS) {// 如果 是选择了上传到https
			String url = getHttpsUrl();
			// ip或者port设置不正确时通知用户
			if (Verify.isUrl(url)) {
				return true;
			}
		} else if (getUploadServer() == ServerManager.SERVER_FTP) {// 上传到FTP
			String ftpName = getFtpName();
			ConfigFtp configFtp = new ConfigFtp();
			if (configFtp.contains(ftpName)) {
				String ip = configFtp.getFtpIp(ftpName);
				String port = configFtp.getFtpPort(ftpName);
				if (Verify.isIp(ip) && Verify.isPort(String.valueOf(port))) {
					return true;
				}
			}
		} else if (getUploadServer() == SERVER_BTU || getUploadServer() == SERVER_ATU) {
			String ip = getDTLogIp();
			int port = getDTLogPort();
			String boxId = getDTLogBoxId();
			if (Verify.isIp(ip) && Verify.isPort(String.valueOf(port)) && boxId.length() > 0) {
				return true;
			}
		} else if (getUploadServer() == SERVER_FLEET) {
			String ip = getUploadFleetIp();
			int port = getUploadFleetPort();
			if (Verify.isIp(ip) && Verify.isPort(String.valueOf(port))) {
				return true;
			}
		} else if (getUploadServer() == SERVER_UNICOM) { // 设置为上传到联通统一平台
			String ip = getUnicomIp();
			int port = getUnicomPort();
			String account = getUnicomAccount();
			String password = getUnicomPassword();

			if (Verify.isIp(ip) && Verify.isPort(String.valueOf(port)) && account != null && !"".equals(account)
					&& password != null && !"".equals(password)) {
				return true;
			}
		} else if(getUploadServer() == SERVER_INNS){
			String ip = getInnsServerIp();
			String userId = getInnsServerUserId();
			return !TextUtils.isEmpty(ip) && !TextUtils.isEmpty(userId);
		}
		return false;
	}

	public boolean hasDownloadServerSet() {
		boolean hasSet = false;
		if (getUploadServer() == SERVER_FLEET) {
			if (Verify.isIp(getUploadFleetIp()) && getUploadFleetPort() != 0) {
				hasSet = true;
			}
		} else if (getUploadServer() == SERVER_BTU || getUploadServer() == SERVER_ATU) {
			if (Verify.isIp(getDTLogIp()) && getDTLogPort() != 0 && getDTLogBoxId().length() > 0) {
				hasSet = true;
			}
		}
		return hasSet;
	}

	/**
	 * 设置是否上报fleet事件
	 */
	public void setFleetEvent(Context context, boolean send) {
		SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences",
				Context.MODE_PRIVATE);
		if (!share.contains(KEY_FLEET_EVENT)) {
			Editor editor = share.edit();
			editor.putBoolean(KEY_FLEET_EVENT, send);
			editor.commit();
		}
		Editor editor = share.edit();
		editor.putBoolean(KEY_FLEET_EVENT, send);
		editor.commit();
	}

	/**
	 * 是否上报fleet事件
	 */
	public boolean hasFleetEvent(Context context) {
		SharedPreferences share = context.getSharedPreferences(context.getPackageName() + "_preferences",
				Context.MODE_PRIVATE);
		if (!share.contains(KEY_FLEET_EVENT)) {
			Editor editor = share.edit();
			// GMCC版本默认上报事件
			editor.putBoolean(KEY_FLEET_EVENT, ConfigRoutine.getInstance().isGmccVersion());
			editor.commit();
		}
		return share.getBoolean(KEY_FLEET_EVENT, true);
	}

	// /**
	// * 是否上传RCU文件
	// *
	// * @param context
	// * @return
	// */
	// public boolean isUploadRCUFile(Context context) {
	// return this.getBoolean(context, KEY_FILE_TYPE_RCU);
	// }
	//
	// /**
	// * 设置是否上传RCU文件
	// *
	// * @param context
	// * @param value
	// */
	// public void setUploadRCUFile(Context context, boolean value) {
	// this.setBoolean(context, KEY_FILE_TYPE_RCU, value);
	// }
	//
	// /**
	// * 是否上传DTLog文件
	// *
	// * @param context
	// * @return
	// */
	// public boolean isUploadDTLogFile(Context context) {
	// return this.getBoolean(context, KEY_FILE_TYPE_DTLOG);
	// }
	//
	// /**
	// * 设置是否上传RCU文件
	// *
	// * @param context
	// * @param value
	// */
	// public void setUploadDTLogFile(Context context, boolean value) {
	// this.setBoolean(context, KEY_FILE_TYPE_DTLOG, value);
	// }
	//
	// /**
	// * 是否上传DCF文件
	// *
	// * @param context
	// * @return
	// */
	// public boolean isUploadDCFFile(Context context) {
	// return this.getBoolean(context, KEY_FILE_TYPE_DCF);
	// }
	//
	// /**
	// * 设置是否上传DCF文件
	// *
	// * @param context
	// * @param value
	// */
	// public void setUploadDCFFile(Context context, boolean value) {
	// this.setBoolean(context, KEY_FILE_TYPE_DCF, value);
	// }
	//
	// /**
	// * 是否上传ddib文件
	// *
	// * @param context
	// * @return
	// */
	// public boolean isUploadDdibFile(Context context) {
	// return this.getBoolean(context, KEY_FILE_TYPE_DDIB);
	// }
	//
	// /**
	// * 设置是否上传ddib文件
	// *
	// * @param context
	// * @param value
	// */
	// public void setUploadDdibFile(Context context, boolean value) {
	// this.setBoolean(context, KEY_FILE_TYPE_DDIB, value);
	// }
	/**
	 * @return 综合测试仪环境 0：归一化到NAS，1：归一化到RLC，2：归一化到MAC，3：归一化到PHY，4：不归一化 默认未0
	 */
	public int getComprehensiveTestEnvironment() {
		return share.getInt(KEY_COMPREHENSIVE_TEST_ENVIRONMENT, 0);
	}

	/**
	 * 综合测试仪环境
	 *
	 * @param index
	 */
	public void setComprehensiveTestEnvironment(int index) {
		share.edit().putInt(KEY_COMPREHENSIVE_TEST_ENVIRONMENT, index).commit();
	}

	/**
	 * @return 是否显示层1,层2信令
	 */
	public boolean hasShowL1L2Command() {
		return share.getBoolean(KEY_SHOW_L1_L2_COMMAND, false);
	}

	/**
	 * 设置是否显示层1,层2信令
	 *
	 * @param autoUpgrade
	 */
	public void setShowL1L2Command(boolean autoUpgrade) {
		share.edit().putBoolean(KEY_SHOW_L1_L2_COMMAND, autoUpgrade).commit();
	}

	/**
	 * @return 是否显示层1,层2信令
	 */
	public boolean hasShowL1L2CommandBuffer() {
		return share.getBoolean(KEY_SHOW_L1_L2_COMMAND_BUFFER, false);
	}

	/**
	 * 设置是否显示层1,层2信令
	 *
	 * @param autoUpgrade
	 */
	public void setShowL1L2CommandBuffer(boolean autoUpgrade) {
		share.edit().putBoolean(KEY_SHOW_L1_L2_COMMAND_BUFFER, autoUpgrade).commit();
	}

	/**
	 * @return 是否需要提示版本更新
	 */
	public boolean hasAutoUpgrade() {
		return share.getBoolean(KEY_AUTO_UPGRADE, false);
	}

	/**
	 * 自动升级版本
	 *
	 * @param autoUpgrade
	 */
	public void setAutoUpgrade(boolean autoUpgrade) {
		share.edit().putBoolean(KEY_AUTO_UPGRADE, autoUpgrade).commit();
	}



	private ServerOperateType fleeterType = ServerOperateType.idle;
	private ArrayList<WalktourEvent> fleetEventList = new ArrayList<WalktourEvent>();

	public ArrayList<WalktourEvent> getEventList() {
		return fleetEventList;
	}

	/**
	 * @return 当前Fleet的工作状态（类型）
	 */
	public ServerOperateType getServerType() {
		return this.fleeterType;
	}

	public void setFleetType(ServerOperateType type) {
		this.fleeterType = type;
	}

	/**
	 * 向平台发送消息,这里的消息是无需获取响应状态
	 */
	public void sendEvent(Context context) {
		if (getUploadServer() == SERVER_FLEET) {
			Intent intent = new Intent(ServerMessage.ACTION_FLEET_SEND_MESSAGE);
			context.sendBroadcast(intent);
		} else {
			Intent service = new Intent(mContext, DataTransService.class);
			service.putExtra("type", ServerOperateType.sendEvent);
			mContext.startService(service);
		}
	}

	/***
	 * 网络操作，必须在非主线程里进行操作
	 *
	 * 发送 开始测试信息 参考<Fleet RCU命令交互协议>和<RCU自定义事件存储结构>两个文档
	 *
	 */
	public void addStartTestEvent(Context context) {
		WalktourEvent event = new WalktourEvent(context, "start test", System.currentTimeMillis(),
				RcuEventCommand.EVENT_START_TEST, RcuEventCommand.EVENT_REASON_NONE);
		this.fleetEventList.add(event);
		sendEvent(context);

		waitForMinute();
	}

	/***
	 * 网络操作，必须在非主线程里进行操作
	 *
	 * 发送 停止测试信息
	 */
	public void addStopTestEvent(Context context) {

		WalktourEvent event = new WalktourEvent(context, "stop test", System.currentTimeMillis(),
				RcuEventCommand.EVENT_STOP_TEST, RcuEventCommand.EVENT_REASON_NONE);
		this.fleetEventList.add(event);
		sendEvent(context);
	}

	private void waitForMinute() {
		/**
		 * 等待1分钟超时
		 */
		for (int i = 0; i < 60; i++) {
			if (fleetEventList.size() > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
	}

	/**
	 * FTP下载任务 ,请在单独线程里执行
	 *
	 * @param ip
	 *          IP地址
	 * @param port
	 *          FTP服务器端口
	 * @param username
	 *          用户名
	 * @param password
	 *          密码
	 * @param ftpJob
	 *          任务
	 * @return 是否下载 成功
	 */
	public boolean downLoadFile(String ip, int port, String username, String password, FtpJob ftpJob) {

		FtpOperate ftp = new FtpOperate(mContext);
		try {
			boolean connected = ftp.connect(ip, port, username, password);
			if (connected) {
				DownloadStatus downStatus = ftp.download(ftpJob.getRemoteFile(), ftpJob.getLocalFile());
				if (DownloadStatus.Download_From_Break_Success == downStatus
						|| DownloadStatus.Download_New_Success == downStatus) {
					ftpJob.setJobDone(true);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ftpJob.isJobDone();
	}

	/**
	 * FTP下载任务 ,请在单独线程里执行
	 *
	 * @param ip
	 *          IP地址
	 * @param port
	 *          FTP服务器端口
	 * @param username
	 *          用户名
	 * @param password
	 *          密码
	 * @param ftpJobs
	 *          任务
	 * @return 下载状态改变之后的任务列表
	 */
	public ArrayList<FtpJob> downLoadFiles(String ip, int port, String username, String password,
										   ArrayList<FtpJob> ftpJobs) {
		FtpOperate ftp = new FtpOperate(mContext);
		try {
			boolean connected = ftp.connect(ip, port, username, password);
			if (connected) {
				for (FtpJob j : ftpJobs) {
					DownloadStatus downStatus = ftp.download(j.getRemoteFile(), j.getLocalFile());
					if (DownloadStatus.Download_From_Break_Success == downStatus
							|| DownloadStatus.Download_New_Success == downStatus) {
						j.setJobDone(true);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ftpJobs;
	}

	/**
	 * 发送当前进度的广播
	 **/
	public void sendProgressBroadcast(String localFileName, String remoteFileName, int progress, String speedInfo) {
		Intent intentProgress = new Intent();
		String text = mContext.getString(R.string.str_uploading) + "\t" + progress + "%\r" + speedInfo + "\n"
				+ localFileName;
		if (remoteFileName != null) {
			if (remoteFileName.length() > 0) {
				text += "\n" + mContext.getString(R.string.str_toRemote) + "\n" + remoteFileName;
			}
		}
		intentProgress.putExtra(ServerMessage.KEY_MSG_FILENAME, localFileName);
		intentProgress.putExtra(ServerMessage.KEY_MSG_PROGRESS, progress);
		intentProgress.putExtra(ServerMessage.KEY_MSG, text);
		intentProgress.setAction(ServerMessage.ACTION_FLEET_PROGRESS);
		mContext.sendBroadcast(intentProgress);
	}

	/**
	 * 发送当前进度的广播
	 **/
	public void sendProgressBroadcast(String fileName, int progress) {
		Intent intentProgress = new Intent();
		String text = mContext.getString(R.string.str_uploading) + fileName + "\n" + progress + "%";
		intentProgress.putExtra(ServerMessage.KEY_MSG_FILENAME, fileName);
		intentProgress.putExtra(ServerMessage.KEY_MSG_PROGRESS, progress);
		intentProgress.putExtra(ServerMessage.KEY_MSG, text);
		intentProgress.setAction(ServerMessage.ACTION_FLEET_PROGRESS);
		mContext.sendBroadcast(intentProgress);
	}

	/**
	 * 发送当前文件操作的tip
	 **/
	public void sendTipBroadcast(String text) {
		Intent intentProgress = new Intent();
		intentProgress.putExtra(ServerMessage.KEY_MSG, text);
		intentProgress.setAction(ServerMessage.ACTION_FLEET_TIP);
		mContext.sendBroadcast(intentProgress);
	}

	/**
	 * 函数功能：下载测试计划
	 *
	 * @param force
	 *          强制更新
	 */
	public void downloadTestTask(boolean force) {
		if (getUploadServer() == ServerManager.SERVER_BTU || getUploadServer() == ServerManager.SERVER_ATU) {
			Intent service = new Intent(mContext, DataTransService.class);
			service.putExtra(DataTransService.EXTRA_KEY_OPERATE_TYPE_NAME,
					force ? ServerOperateType.downManualForce : ServerOperateType.downManual);
			mContext.startService(service);
		} else if (getUploadServer() == ServerManager.SERVER_FLEET) {
			mContext.sendBroadcast(new Intent(ServerMessage.ACTION_FLEET_DOWNLOAD_MANUAL));
		}
	}

	public void OnServerStatusChange(ServerStatus status, String info) {
		if (serverListener != null) {
			serverListener.onStatusChange(status, info);
		}
	}

	public void setServerStatusChangeListener(ServerStatusListener listener) {
		serverListener = listener;
	}

	/**
	 * 从一个文本文件读取
	 *
	 * @param file
	 * @return
	 */
	private StringBuffer getTextFromFile(File file) {
		StringBuffer result = new StringBuffer();

		if (file.exists() && file.isFile()) {

			FileInputStream inStream = null;
			try {
				inStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
				String line;
				while ((line = br.readLine()) != null) {
					result.append(line + "\r\n");
				}

				br.close();
				if(inStream != null)
					inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 把文件写入到指定路径的文件，全部覆盖
	 *
	 * @param filePath
	 *          目标文件的绝对路径
	 * @param data
	 *          要写入文件的文本
	 */
	private File writeTex2File(String filePath, String data) {
		File f = new File(filePath);

		// 文件不存在时创建文件
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			OutputStream stream = new FileOutputStream(f);
			stream.write(data.getBytes());
			stream.flush();
			stream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} // end try&catch
		return f;
	}// end method write

	/********************************************* 以下是联通一级平台相关的服务器信息保存和读取 *********************************************/
	/**
	 * 保存服务器信息实体类到系统是Preference中
	 *
	 * @param info
	 *          服务器信息实体类
	 */
	public void saveUnicomServer(ServerInfo info) {
		switch (info.getSvrType()) {
			case UnicomInterface.TASK_SERVER:
				share.edit().putString(KEY_UNICOM_TASK_IP, info.getIpAddr()).commit();
				share.edit().putInt(KEY_UNICOM_TASK_PORT, info.getPort()).commit();
				share.edit().putString(KEY_UNICOM_TASK_ACCOUNT, info.getAccount()).commit();
				share.edit().putString(KEY_UNICOM_TASK_PASSWORD, info.getPassword()).commit();
				break;
			case UnicomInterface.FTP_UPLOAD_SERVER:
				share.edit().putString(KEY_UNICOM_FTP_UPLOAD_IP, info.getIpAddr()).commit();
				share.edit().putInt(KEY_UNICOM_FTP_UPLOAD_PORT, info.getPort()).commit();
				share.edit().putString(KEY_UNICOM_FTP_UPLOAD_ACCOUNT, info.getAccount()).commit();
				share.edit().putString(KEY_UNICOM_FTP_UPLOAD_PASSWORD, info.getPassword()).commit();
				this.setUploadFtp(info.getIpAddr(), info.getPort(), info.getAccount(), info.getPassword());
				break;
			case UnicomInterface.FTP_DOWNLOAD_SERVER:
				share.edit().putString(KEY_UNICOM_FTP_DOWNLOAD_IP, info.getIpAddr()).commit();
				share.edit().putInt(KEY_UNICOM_FTP_DOWNLOAD_PORT, info.getPort()).commit();
				share.edit().putString(KEY_UNICOM_FTP_DOWNLOAD_ACCOUNT, info.getAccount()).commit();
				share.edit().putString(KEY_UNICOM_FTP_DOWNLOAD_PASSWORD, info.getPassword()).commit();
				break;
			case UnicomInterface.HTTP_UPLOAD_SERVER:
				share.edit().putString(KEY_UNICOM_HTTP_UPLOAD_IP, info.getIpAddr()).commit();
				share.edit().putInt(KEY_UNICOM_HTTP_UPLOAD_PORT, info.getPort()).commit();
				share.edit().putString(KEY_UNICOM_HTTP_UPLOAD_ACCOUNT, info.getAccount()).commit();
				share.edit().putString(KEY_UNICOM_HTTP_UPLOAD_PASSWORD, info.getPassword()).commit();
				break;
			case UnicomInterface.HTTP_DOWNLOAD_SERVER:
				share.edit().putString(KEY_UNICOM_HTTP_DOWNLOAD_IP, info.getIpAddr()).commit();
				share.edit().putInt(KEY_UNICOM_HTTP_DOWNLOAD_PORT, info.getPort()).commit();
				share.edit().putString(KEY_UNICOM_HTTP_DOWNLOAD_ACCOUNT, info.getAccount()).commit();
				share.edit().putString(KEY_UNICOM_HTTP_DOWNLOAD_PASSWORD, info.getPassword()).commit();
				break;

			default:
				break;
		}
	}

	private void appendText2File(String filePath, String text) {
		File f = new File(filePath);
		// 文件不存在时创建文件
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			FileWriter writer = new FileWriter(f, true);
			writer.append(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 从Preference中读取服务器信息
	 *
	 * @param serverType
	 *          服务器类型
	 * @return 服务器信息实体类
	 */
	public ServerInfo readUnicomServer(int serverType) {
		ServerInfo info = new ServerInfo();
		switch (serverType) {
			case UnicomInterface.TASK_SERVER:
				info.setIpAddr(share.getString(KEY_UNICOM_TASK_IP, ""));
				info.setPort(share.getInt(KEY_UNICOM_TASK_PORT, 0));
				// 这块是access的账号和密码，不是Task的账号和密码，是因为调用的时候返回的task的服务器资源里用户名和密码是空
				info.setAccount(share.getString(KEY_UNICOM_ACCESS_ACCOUNT, ""));
				info.setPassword(share.getString(KEY_UNICOM_ACCESS_PASSWORD, ""));
				// info.setAccount("sq_wjtest");
				// info.setPassword("888888");
				break;
			case UnicomInterface.FTP_UPLOAD_SERVER:
				info.setIpAddr(share.getString(KEY_UNICOM_FTP_UPLOAD_IP, ""));
				info.setPort(share.getInt(KEY_UNICOM_FTP_UPLOAD_PORT, 0));
				info.setAccount(share.getString(KEY_UNICOM_FTP_UPLOAD_ACCOUNT, ""));
				info.setPassword(share.getString(KEY_UNICOM_FTP_UPLOAD_PASSWORD, ""));
				break;
			case UnicomInterface.FTP_DOWNLOAD_SERVER:
				info.setIpAddr(share.getString(KEY_UNICOM_FTP_DOWNLOAD_IP, ""));
				info.setPort(share.getInt(KEY_UNICOM_FTP_DOWNLOAD_PORT, 0));
				info.setAccount(share.getString(KEY_UNICOM_FTP_DOWNLOAD_ACCOUNT, ""));
				info.setPassword(share.getString(KEY_UNICOM_FTP_DOWNLOAD_PASSWORD, ""));
				break;
			case UnicomInterface.HTTP_UPLOAD_SERVER:
				info.setIpAddr(share.getString(KEY_UNICOM_HTTP_UPLOAD_IP, ""));
				info.setPort(share.getInt(KEY_UNICOM_HTTP_UPLOAD_PORT, 0));
				info.setAccount(share.getString(KEY_UNICOM_HTTP_UPLOAD_ACCOUNT, ""));
				info.setPassword(share.getString(KEY_UNICOM_HTTP_UPLOAD_PASSWORD, ""));
				break;
			case UnicomInterface.HTTP_DOWNLOAD_SERVER:
				info.setIpAddr(share.getString(KEY_UNICOM_HTTP_DOWNLOAD_IP, ""));
				info.setPort(share.getInt(KEY_UNICOM_HTTP_DOWNLOAD_PORT, 0));
				info.setAccount(share.getString(KEY_UNICOM_HTTP_DOWNLOAD_ACCOUNT, ""));
				info.setPassword(share.getString(KEY_UNICOM_HTTP_DOWNLOAD_PASSWORD, ""));
				break;

			default:
				break;
		}
		return info;
	}

	/**
	 * 保存服务器信息List到系统是Preference中
	 *
	 * @param infos
	 *          服务器信息List
	 */
	public void saveUnicomServer(List<ServerInfo> infos) {
		for (ServerInfo info : infos) {
			saveUnicomServer(info);
		}
	}

	/**
	 * 读取所有的服务器信息
	 *
	 * @return 服务器信息的List
	 */
	public List<ServerInfo> readAllUnicomServer() {
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		for (int i = 0; i < servers.length; i++) {
			serverInfos.add(readUnicomServer(servers[i]));
		}
		return serverInfos;
	}

	/**
	 * 联通平台进行数据上传之前设置的FTP服务器
	 */
	public void setUploadFtp(String ip, int port, String user, String pass) {
		final String name = "DataUpload";
		ConfigFtp configFtp = new ConfigFtp();
		if (configFtp.contains(name)) {
			configFtp.setFtpIp(name, ip);
			configFtp.setFtpPort(name, port + "");
			configFtp.setFtpUser(name, user);
			configFtp.setFtpPass(name, pass);
		} else {
			FtpServerModel ftpServerModel = new FtpServerModel();
			ftpServerModel.setName(name);
			ftpServerModel.setIp(ip);
			ftpServerModel.setPort(String.valueOf(port));
			ftpServerModel.setLoginUser(user);
			ftpServerModel.setLoginPassword(pass);
			configFtp.addFtp(ftpServerModel);
		}
		setFtpName(name);
	}

	/**
	 * FTP下载任务 ,请在单独线程里执行
	 *
	 * @param ip
	 *          IP地址
	 * @param port
	 *          FTP服务器端口
	 * @param username
	 *          用户名
	 * @param password
	 *          密码
	 * @param ftpJob
	 *          任务
	 * @return 是否下载 成功
	 */
	public boolean singerDownLoadFile(String ip, int port, String username, String password, FtpJob ftpJob) {
		FtpOperate ftp = new FtpOperate(mContext);
		try {
			boolean connected = ftp.connect(ip, port, username, password);
			if (connected) {
				DownloadStatus downStatus = ftp.singerDownload(ftpJob.getRemoteFile(), ftpJob.getLocalFile());
				if (DownloadStatus.Download_From_Break_Success == downStatus
						|| DownloadStatus.Download_New_Success == downStatus) {
					ftpJob.setJobDone(true);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ftpJob.isJobDone();
	}

	public String getUnicomIp() {
		return share.getString(KEY_UNICOM_ACCESS_IP, "202.99.45.126");
	}

	public boolean setUnicomIp(String ip) {
		return share.edit().putString(KEY_UNICOM_ACCESS_IP, ip).commit();
	}

	public int getUnicomPort() {
		return share.getInt(KEY_UNICOM_ACCESS_PORT, 9981);
	}

	public boolean setUnicomPort(int port) {
		return share.edit().putInt(KEY_UNICOM_ACCESS_PORT, port).commit();
	}

	public String getUnicomAccount() {
		return share.getString(KEY_UNICOM_ACCESS_ACCOUNT, "");
	}

	public boolean setUnicomAccount(String account) {
		return share.edit().putString(KEY_UNICOM_ACCESS_ACCOUNT, account).commit();
	}

	public String getUnicomPassword() {
		return share.getString(KEY_UNICOM_ACCESS_PASSWORD, "");
	}

	public boolean setUnicomPassword(String password) {
		return share.edit().putString(KEY_UNICOM_ACCESS_PASSWORD, password).commit();
	}
	public String getInnsServerIp() {
		return share.getString(KEY_INNS_SERVER_IP, "");
	}

	public boolean setInnsServerIp(String ip) {
		return share.edit().putString(KEY_INNS_SERVER_IP, ip).commit();
	}
	public String getInnsServerUserId() {
		return share.getString(KEY_INNS_SERVER_USER_ID, "");
	}

	public boolean setInnsServerUserId(String userId) {
		return share.edit().putString(KEY_INNS_SERVER_USER_ID, userId).commit();
	}

	/**
	 * 获得文件类型数组
	 *
	 * @return
	 */
	public String[] getFileTypes() {
		switch (this.getUploadServer()) {
			case ServerManager.SERVER_FLEET:
				return new String[] { "RCU", "DTLog","DCF","CU" };
			case ServerManager.SERVER_BTU:
			case ServerManager.SERVER_ATU:
				return new String[] { "DTLog" };
			case ServerManager.SERVER_HTTPS:
				return new String[] { "RCU" };
			case ServerManager.SERVER_INNS:
				return new String[]{"TXT"};
			default:
				return new String[] { "RCU", "DTLog", "DCF", "DDIB" };
		}
	}


	/**
	 * 设置是否同步上传MOS文件
	 *
	 * @param isCheck
	 *          是否上传
	 * @return
	 */
	public boolean setUploadMOSFile(boolean isCheck) {
		return share.edit().putBoolean(KEY_IS_UPLOAD_MOS_FILE, isCheck).commit();
	}

	/**
	 * 是否同步上传MOS文件
	 *
	 * @return
	 */
	public boolean isUploadMOSFile() {
		return share.getBoolean(KEY_IS_UPLOAD_MOS_FILE, false);
	}

	/**
	 * 设置是否同步上传标注文件
	 *
	 * @param isCheck
	 *          是否上传
	 * @return
	 */
	public boolean setUploadTaggingFile(boolean isCheck) {
		return share.edit().putBoolean(KEY_IS_UPLOAD_TAGGING_FILE, isCheck).commit();
	}

	/**
	 * 是否同步上传标注文件
	 *
	 * @return
	 */
	public boolean isUploadTaggingFile() {
		return share.getBoolean(KEY_IS_UPLOAD_TAGGING_FILE, false);
	}
}
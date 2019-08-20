package com.walktour.gui.upgrade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus.DownloadStatus;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.excel.ExcelUtil;
import com.walktour.Utils.excel.model.NoUpgradeExcelBean;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.model.FtpJob;
import com.walktour.model.FtpServerModel;

import org.apache.commons.net.ftp.FTPFile;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 升级服务，通过JNI库获取最新的版本信息，再调用FTP的jar包下载版本安装
 * 
 * @author qihang.li
 */
public class UpgradeService extends Service implements FtpOperate.OnProgressChangeListener {
	/** 日志标识 */
	private final String TAG = "UpgradeService";
	/** 是否检查标识 */
	public static final String KEY_CHECK_NOW = "check";
	/** 临时文件后缀 */
	private String TEMP_NAME = ".tmp";
	/** 当前状态 */
	private Status mStatus = Status.IDLE;
	/** 状态监听类 */
	private StatusChangeListener mStatusListener = null;
	/** 上下文 */
	private Context mContext;
	/** 服务器上的Apk名字 */
	private String serverApkName = "";
	/** 更新信息文件名 */
	private String whatNewName = "What's New.txt";
	/** 更新信息文件名英文版 */
	private String whatNewNameEN = "What's New_en.txt";
	/** 服务器的配置信息 */
	private FtpServerModel ftpServerModel = null;
	/** 服务器目录 */
	private String serverCatalog = "";
	/** 本地APK路径 */
	private String localApkPath = "";
	/** 本地更新信息路径 */
	private String localWhatNewPath = "";
	/** FTP操作类 */
	private FtpOperate ftp;
	/** 是否后台运行 */
	private boolean runInBackground = false;
	/** 是否正在执行任务 */
	private boolean isDoingJob = false;
	/** 更新信息 */
	private String upgrageMesage = "";
	/** IP列表 */
	private List<String> ipList = new ArrayList<>();
	/** 端口 */
	private String port = "";
	/** 登录用户 */
	private String user = "";
	/** 登录密码 */
	private String password = "";
	/** 服务类绑定 */
	private ServiceBinder binder = new ServiceBinder();
	/** 远程版本号 */
	protected String remoteVersion;
	/** 连接超时时长 */
	private int timeout = 10 * 1000;
	/** 文件名称 */
	private String fileName = "";
	public void onCreate() {
		Log.i(TAG, "---onCreate");
		super.onCreate();
		
		try {
			mContext = this;
			fileName=getFilesDir()+"/config/config_ftp_setting.xml";
			if(checkNoUpgrade()){
				parseFtpSetting(new FileInputStream(new File(fileName)),"versionupgrade");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/*
* 检查是否不需要升级的，*/
	private boolean checkNoUpgrade() {
		List<NoUpgradeExcelBean> imeis = ExcelUtil.getInstance(this).onImportNoUpgrade();
		if (imeis==null){
			return true;
		}
		LogUtil.d(TAG,""+MyPhoneState.getInstance().getMyDeviceId(getApplicationContext()));
		for(int i=0;i<imeis.size();i++){
			String imei=imeis.get(i).getImei();
			LogUtil.d(TAG,""+imei);
			LogUtil.d(TAG,""+imei.contains(MyPhoneState.getInstance().getMyDeviceId(getApplicationContext())));
			if (imei.contains(MyPhoneState.getInstance().getMyDeviceId(getApplicationContext()))){
				setStatus(Status.VERSION_CURRENT_LATEST);
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查连接是否可用
	 * 
	 * @param serverIp
	 *          服务端Ip
	 * @param serverPort
	 *          服务端Port
	 * @param serverUser
	 *          登录帐号
	 * @param serverPassword
	 *          登录密码
	 */
	private boolean checkConnect(String serverIp, int serverPort, String serverUser, String serverPassword) {
		boolean connected = false;
		try {
			ftp = new FtpOperate();
			connected = ftp.connect(serverIp, serverPort, serverUser, serverPassword, this.timeout);
		} catch (Exception e) {
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connected;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "---onStartCommand");
		try {
			runInBackground = intent.getBooleanExtra(KEY_CHECK_NOW, false);
			this.timeout = intent.getIntExtra("timeout", 3 * 1000);
			if (runInBackground) {
				checkVersion();
			}
		} catch (Exception e) {
			runInBackground = false;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "---onBind");
		return binder;
	}

	public void onDestroy() {
		Log.e(TAG, "---onDestroy");
		super.onDestroy();
	}

	private class ServiceBinder extends Binder implements UpgradeBinder {

		@Override
		public void setStatusChangeListener(StatusChangeListener statusChangeListener) {
			mStatusListener = statusChangeListener;
		}

		@Override
		public void stopTest() {
			new Thread() {
				@Override
				public void run() {
					sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));
					setStatus(Status.TEST_STOPPING);
					while (ApplicationModel.getInstance().isTestJobIsRun() || ApplicationModel.getInstance().isTestStoping()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// 等待网络可用
					for (int i = 0; i < 5; i++) {
						if (!MyPhoneState.getInstance().isNetworkAvirable(mContext)) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							break;
						}
					}
					// 停止测试后，已经获取过版本信息则直接下载，未获取则去获取
					if (serverApkName == null || serverApkName.trim().length() == 0) {
						checkVersion();
					} else {
						downloadFile();
					}
				}
			}.start();
		}

		@Override
		public void checkNewVersion() {
			checkVersion();
		}

		@Override
		public void download() {

			new JobPreparer() {
				@Override
				public void doTheJob() {
					Status status = downloadFile();
					setStatus(status);
				}

				@Override
				public void doFinally() {

				}
			}.start();
		}

		@Override
		public void stopDownload() {
			new Thread() {
				public void run() {
					if (ftp != null) {
						ftp.interrupt();
					}
				}
			}.start();
		}

		@Override
		public boolean install() {

			File localApk = new File(localApkPath);

			if (localApk.exists() && localApk.isFile() && localApk.length() > 0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setDataAndType(Uri.parse("file://" + localApk.getAbsolutePath()), "application/vnd.android.package-archive");
				startActivity(i);
				return true;
			}

			return false;
		}
	}

	/**
	 * 检查是否有新的版本,通过获得FTP服务器上的apk安装文件名称来判断
	 */
	private void checkVersion() {

		new JobPreparer() {

			@Override
			public void doTheJob() {

				checkVersionFromFtp();

				if (serverApkName == null || serverApkName.trim().length() == 0) {
					setStatus(Status.VERSION_NO_INFO);
				} else {
					String currentVersion = UtilsMethod.getCurrentVersionName(mContext);
					LogUtil.e(TAG,"apk名字："+serverApkName);
					remoteVersion = serverApkName.substring(serverApkName.indexOf("-") + 1, serverApkName.lastIndexOf("-"));
					if (mStatusListener != null) {
						mStatusListener.setRemoteVersion(remoteVersion);
					}
					if (!checkVersionIsNew(currentVersion, remoteVersion)) {
						setStatus(Status.VERSION_CURRENT_LATEST);
					} else {
						File temp = new File(localApkPath + TEMP_NAME);
						File apk = new File(localApkPath);
						if (temp.exists() && temp.isFile()) {
							setStatus(Status.DOWNLOAD_STOP);
							getUpdateMessage(false);
						} else if (apk.exists() && apk.isFile()) {
							setStatus(Status.VERSION_LOCAL_INCLUDE);
							getUpdateMessage(false);
						} else {
							setStatus(Status.VERSION_NEED_UPGRADE);
							getUpdateMessage(true);
						}
					}
				}
			}

			@Override
			public void doFinally() {
				if (runInBackground) {
					if (mStatus == Status.VERSION_NEED_UPGRADE || mStatus == Status.VERSION_LOCAL_INCLUDE
							|| mStatus == Status.DOWNLOAD_STOP) {

						Intent intent = new Intent(mContext, UpgradeActivity.class);
						intent.putExtra(UpgradeActivity.KEY_STATUS, mStatus);
						intent.putExtra(UpgradeActivity.KEY_MESSAGE, upgrageMesage);
						intent.putExtra(UpgradeActivity.KEY_VERSION, remoteVersion);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else {
						stopSelf();
					}
				}
			}
		}.start();

	}

	/**
	 * 判断版本号是新的还是旧的
	 * 
	 * @param currentVersion
	 *          当前版本
	 * @param remoteVersion
	 *          远程版本
	 * @return
	 *
	 * //3.9.0.0921 Build 1620
	 */
	private boolean checkVersionIsNew(String currentVersion, String remoteVersion) {
		try{
			if (remoteVersion == null || remoteVersion.trim().length() == 0)
				return false;
			currentVersion=currentVersion.split(" Build")[0];//去掉 BUild XXXX，
			remoteVersion=remoteVersion.split(" Build")[0];//去掉 BUild XXXX，
			String[] currVers = this.split(currentVersion);
			String[] remoteVers = this.split(remoteVersion);
			for (int i = 0; i < currVers.length; i++) {
				if (Integer.parseInt(remoteVers[i]) > Integer.parseInt(currVers[i])) {
					return true;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 按.号分割字符串
	 * 
	 * @param str
	 *          字符串
	 * @return
	 */
	private String[] split(String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '.') {
				count++;
			}
		}
		String[] split = new String[count + 1];
		int pos = -1;
		int index = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '.') {
				split[index++] = str.substring(pos + 1, i);
				pos = i;
			}
		}
		split[index++] = str.substring(pos + 1);
		return split;
	}

	/**
	 * 从版本服务器上获取更新信息
	 * 
	 * @param isDownload
	 *          是否下载
	 */
	private void getUpdateMessage(boolean isDownload) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(this.localWhatNewPath); // 要读取以上路径的input。txt文件
			if (isDownload || !file.exists()) {
				String remoteWhatNewPath = File.separator + this.serverCatalog + File.separator;
				if (getResources().getConfiguration().locale.getCountry().equals("CN"))
					remoteWhatNewPath += this.whatNewName;
				else
					remoteWhatNewPath += this.whatNewNameEN;
				FtpJob whatNewJob = new FtpJob(remoteWhatNewPath, localWhatNewPath);
				downLoadFromFtp(mContext, whatNewJob, false);
			}
			LogUtil.d(TAG, "----getUpdateMessage----");
			if (!file.exists())
				return;
			InputStreamReader reader = null;
			BufferedReader br = null;
			String currentVersion = UtilsMethod.getCurrentVersionName(mContext);
			try {
				reader = new InputStreamReader(new FileInputStream(file), "GBK");
				br = new BufferedReader(reader);
				StringBuilder message = new StringBuilder();
				String line = "";
				while (line != null) {
					line = br.readLine();
					if (line != null && line.trim().length() > 0) {
						if (line.indexOf(currentVersion) > 0)
							break;
						message.append(line);
					}
					message.append("\n");
				}
				this.upgrageMesage = message.toString();
				if (mStatusListener != null) {
					mStatusListener.setUpgradeMessage(message.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	/**
	 * 从版本服务器上获取版本信息
	 * 
	 * @return
	 */
	private void checkVersionFromFtp() {
		try {
			ftp = new FtpOperate(mContext, UpgradeService.this);
			setStatus(Status.CONNECTING_FTP);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean connected = false;
			if (this.ftpServerModel != null)
				connected = ftp.connect(this.ftpServerModel.getIp(), Integer.parseInt(this.ftpServerModel.getPort()),
						this.ftpServerModel.getLoginUser(), this.ftpServerModel.getLoginPassword());
//			LogUtil.d("max","是否登录成功:"+connected);
			if (connected) {
				FTPFile[] files = ftp.getFTPLists(File.separator + this.serverCatalog, ftpServerModel);
				if (files != null) {
					List<String> remoteFiles = new ArrayList<String>();
					for (FTPFile file : files) {

						if (file.getName().endsWith(".apk")) {
							if (remoteFiles.isEmpty()) {
								remoteFiles.add(file.getName());
							} else {
								boolean isfind = false;
								for (int i = 0; i < remoteFiles.size(); i++) {
									String remoteVer = file.getName().substring(file.getName().indexOf("-") + 1,
											file.getName().lastIndexOf("-"));
									String standVer = remoteFiles.get(i).substring(remoteFiles.get(i).indexOf("-") + 1,
											remoteFiles.get(i).lastIndexOf("-"));
									if (!this.checkVersionIsNew(standVer, remoteVer)) {
										remoteFiles.add(i, file.getName());
										isfind = true;
									}
								}
								if (!isfind) {
									remoteFiles.add(file.getName());
								}
							}
						}
					}
					if (remoteFiles.size() > 0) {
						this.serverApkName = remoteFiles.get(remoteFiles.size() - 1);
						File parentFile = AppFilePathUtil.getInstance().createSDCardBaseFile("Upgrade");
						if (!parentFile.exists())
							parentFile.mkdir();
						this.localApkPath = parentFile.getAbsolutePath() + File.separator + remoteFiles.get(remoteFiles.size() - 1);
						this.localWhatNewPath = parentFile.getAbsolutePath() + File.separator + this.whatNewName;
					}
				}
			} else {
				setStatus(Status.CONNECT_FTP_FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Log.d(TAG, "---disconnect from ftp");
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载文件
	 * 
	 * @return
	 */
	private Status downloadFile() {

		Status status = Status.DOWNLOAD_FAIL;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			FtpJob apkJob = new FtpJob(File.separator + this.serverCatalog + File.separator + this.serverApkName,
					localApkPath + TEMP_NAME);

			if (mStatus != Status.DOWNLOAD_STARTED) {
				setStatus(Status.DOWNLOAD_STARTED);
				status = downLoadFromFtp(mContext, apkJob, true);
			}

			// 下载完成的话就改名
			if (status == Status.DOWNLOAD_SUCCESS) {
				File tempFile = new File(localApkPath + TEMP_NAME);
				File apkFile = new File(localApkPath);
				tempFile.renameTo(apkFile);
			}
		} else {
			status = Status.DOWNLOAD_FAIL_NOSDCARD;
		}

		return status;
	}

	/**
	 * FTP下载任务 ,请在单独线程里执行
	 * 
	 * @param ftpJob
	 *          任务
	 * @param isReport
	 *          是否报告百分比
	 * @return 是否下载 成功
	 */
	private Status downLoadFromFtp(Context context, FtpJob ftpJob, boolean isReport) {
		try {
			ftp = new FtpOperate(context, UpgradeService.this);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean connected = ftp.connect(this.ftpServerModel.getIp(), Integer.parseInt(this.ftpServerModel.getPort()),
					this.ftpServerModel.getLoginUser(), this.ftpServerModel.getLoginPassword());
			if (connected) {
				DownloadStatus downStatus = ftp.download(ftpJob.getRemoteFile(), ftpJob.getLocalFile(), isReport, false);
				if (DownloadStatus.Local_Bigger_Remote == downStatus) {
					File file = new File(ftpJob.getLocalFile());
					file.delete();
					return this.downLoadFromFtp(context, ftpJob, isReport);
				}
				if (DownloadStatus.Download_From_Break_Success == downStatus
						|| DownloadStatus.Download_New_Success == downStatus) {
					ftpJob.setJobDone(true);
					return Status.DOWNLOAD_SUCCESS;
				} else if (DownloadStatus.Remote_File_Noexist == downStatus) {
					return Status.REMOTE_NOT_EXSIT;
				} else if (DownloadStatus.Download_Stopped == downStatus) {
					return Status.DOWNLOAD_STOP;
				}
			} else {
				return Status.CONNECT_FTP_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Log.d(TAG, "---disconnect from ftp");
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return Status.DOWNLOAD_FAIL;
	}

	/**
	 * 设置当前状态
	 * 
	 * @param newStatus
	 */
	private void setStatus(Status newStatus) {
		mStatus = newStatus;
		if (mStatusListener != null) {
			mStatusListener.onStatusChange(mStatus);
		}
	}

	@Override
	public void onProgressChange(long localSize, long remoteSize) {
		if (mStatusListener != null) {
			mStatusListener.onProgressChange(localSize, remoteSize);
		}
	}

	public abstract class JobPreparer extends Thread {
		/**
		 * 具体要做的工作
		 */
		public abstract void doTheJob();

		/**
		 * 最后要完成的
		 */
		public abstract void doFinally();

		@Override
		public void run() {
			if (!isDoingJob) {
				isDoingJob = true;

				if (ApplicationModel.getInstance().isTestJobIsRun()) {
					setStatus(Status.TEST_RUNNING);
				} else {
					if (!MyPhoneState.getInstance().isNetworkAvirable(mContext)) {
						setStatus(Status.NETWORK_UNAVIRABLE);
					} else {
						setStatus(Status.NETWORK_CHECK);
						Log.d(TAG,"更新服务器列表："+ipList);
						for (String ip : ipList) {
							ip="61.143.60.83";
							if (checkConnect(ip, Integer.parseInt(port), user, password)) {
								ftpServerModel = new FtpServerModel();
								ftpServerModel.setName("VersionUpgrade");
								ftpServerModel.setIp(ip);
								ftpServerModel.setPort(port);
								ftpServerModel.setLoginUser(user);
								ftpServerModel.setLoginPassword(password);
								break;
							}
						}
						doTheJob();
					}
				}

				doFinally();

				isDoingJob = false;
			}

		}

	}

	/***
	 * 解析ftp配置文件
	 * 
	 * @param inputStream 文件输入
	 * @param tagName 要解析的标签，其他的忽略
	 * @throws Exception 异常
	 */
	private void parseFtpSetting(InputStream inputStream,String tagName) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int event = parser.getEventType();
		boolean isFlag = true;
		ipList.clear();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				String name = parser.getName();
				if (tagName.equals(name)) {
					isFlag = true;
				}
				if (isFlag) {//只解析高铁配置
					if ("serverIp".equals(name)) {
						ipList.add(UtilsMethod.jem(parser.getAttributeValue(0))); 
//						LogUtil.w(TAG,"Server:IP"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverPort".equals(name)) { 
						port=UtilsMethod.jem(parser.getAttributeValue(0));
//						LogUtil.w(TAG,"Server:Port"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverUser".equals(name)) { 
						user=UtilsMethod.jem(parser.getAttributeValue(0));
//						LogUtil.w(TAG,"Server:User"+UtilsMethod.jem(parser.getAttributeValue(0)));
					} else if ("serverPassword".equals(name)) { 
						password=UtilsMethod.jem(parser.getAttributeValue(0));
//						LogUtil.w(TAG,"Server:Pass"+UtilsMethod.jem(parser.getAttributeValue(0)));
					}else if ("serverCatalog".equals(name)) {
						serverCatalog=parser.getAttributeValue(0);//UtilsMethod.jem();
						LogUtil.w(TAG,"Server:Path"+serverCatalog);
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

package com.walktour.service.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.Parcelable;

import com.walktour.Utils.ServerMessage;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.ServerOperateType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.gui.R;
import com.walktour.service.app.datatrans.AtuDataTransfer;
import com.walktour.service.app.datatrans.BaseDataTransfer;
import com.walktour.service.app.datatrans.BtuDataTransfer;
import com.walktour.service.app.datatrans.FtpDataTransfer;
import com.walktour.service.app.datatrans.HttpsDataTransfer;
import com.walktour.service.app.datatrans.UnicomDataTransfer;
import com.walktour.service.app.datatrans.fleet.FleetDataTransferFactory;
import com.walktour.service.app.datatrans.inns.InnsDataTransfer;
import com.walktour.service.app.datatrans.model.DataTransferModel;
import com.walktour.service.app.datatrans.model.UploadFileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * DataTransService 数据交互服务<BR>
 * 负责Https上传，DTLog上传到BTU平台，RCU上传到ATU平台
 * 
 * @author jianchao.wang
 *
 */
public class DataTransService extends Service {
	/** 日志标识 */
	private static final String TAG = "DataTransService";
	/** 反馈消息：当前正要上传的文件对象 */
	public static final String EXTRA_DATA_TRANS_FILE_MODEL = "DataTransService.data_trans_file_model";
	/** 反馈消息：当前正要上传的文件对象 */
	public static final String EXTRA_DATA_TRANS_FILE_START = "DataTransService.data_trans_file_start";
	/** 反馈消息：当前上传的文件进度 */
	public static final String EXTRA_DATA_TRANS_FILE_PROGRESS = "DataTransService.data_trans_file_progress";
	/** 反馈消息：当前上传的文件结束 */
	public static final String EXTRA_DATA_TRANS_FILE_END = "DataTransService.data_trans_file_end";
	/** 反馈消息：当前上传的一批文件结束 */
	public static final String EXTRA_DATA_TRANS_END = "DataTransService.data_trans_end";
	/** 反馈消息：交互反馈的消息 */
	public static final String EXTRA_DATA_TRANS_MESSAGE = "DataTransService.data_trans_message";
	/** 调用的参数名：反馈的消息内容 */
	public static final String EXTRA_KEY_MESSAGE = "callback_message";
	/** 调用的参数名：操作类型 */
	public static final String EXTRA_KEY_OPERATE_TYPE_NAME = "operate_type";
	/** 调用的参数名：要上传的文件列表 */
	public static final String EXTRA_KEY_UPLOAD_FILES = "upload_files";
	/** 调用的参数名：要上传的消息内容 */
	public static final String EXTRA_KEY_UPLOAD_MESSAGE = "upload_message";

	/** 上下文 */
	private DataTransService mContext;
	/** 服务器管理类 */
	private ServerManager mServerManager;
	/** 设置的上传网络是否可用(Wifi或移动数据) */
	private boolean isNetworkOn = false;
	/** 最后断开连接时间 */
	private long mLastDisconnectTime = 0;
	/** 当前的交互对象 */
	private BaseDataTransfer mCurrTransfer = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		mServerManager = ServerManager.getInstance(mContext);
		LogUtil.d(TAG, "-------onCreate-------------");
		regeditMyReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(TAG, "--------onStartCommand-------------");
		// 检查URL地址
		boolean isServerOK = mServerManager.hasUploadServerSet();
		if (!isServerOK) {
			// 界面通知
			showNotification(getString(R.string.fleet_set_notset_notify), ServerMessage.ACTION_FLEET_SERVER_NOTSET);
			this.handlerMessage(R.string.fleet_set_notset_notify);
			stopSelf();
		} else {
			if (intent == null) {
				stopSelf();
			} else {
                final ServerOperateType operateType = (ServerOperateType) intent
						.getSerializableExtra(EXTRA_KEY_OPERATE_TYPE_NAME);
				if (operateType != null) {
					Parcelable[] uploadFiles = intent.getParcelableArrayExtra(EXTRA_KEY_UPLOAD_FILES);
					String message = intent.getStringExtra(EXTRA_KEY_UPLOAD_MESSAGE);
					List<UploadFileModel> uploadFileList = new ArrayList<UploadFileModel>();
					if (uploadFiles != null && uploadFiles.length > 0) {
						for (int i = 0; i < uploadFiles.length; i++) {
							uploadFileList.add((UploadFileModel) uploadFiles[i]);
						}
					}
					LogUtil.d(TAG, "type:" + operateType.name());
					DataTransferModel model = new DataTransferModel(operateType);
					model.setUploadFiles(uploadFileList);
					model.setMessage(message);
					if (operateType == ServerOperateType.stopUpload) {
						if (this.mCurrTransfer != null)
							this.mCurrTransfer.stopOperate(model);
					} else
						this.addTask(model);
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 函数功能：增加任务到列表
	 * 
	 * @param model
	 *          操作对象
	 * 
	 */
	public void addTask(DataTransferModel model) {
		LogUtil.d(TAG, "-------addTask--------");
		if (this.mCurrTransfer != null) {
			if (this.mCurrTransfer.getTransferState() == BaseDataTransfer.STATE_FINISH) {
				this.mCurrTransfer = null;
			} else if (this.mCurrTransfer.getUploadServer() == mServerManager.getUploadServer()) {
				if (mServerManager.getUploadServer() == ServerManager.SERVER_BTU
						|| mServerManager.getUploadServer() == ServerManager.SERVER_ATU) {
					if (model.getOperateType() == ServerOperateType.uploadTestFile
							|| model.getOperateType() == ServerOperateType.uploadIndoorFile
							|| model.getOperateType() == ServerOperateType.uploadAutoTestFile) {
						this.mCurrTransfer.finishTransfer();
						this.mCurrTransfer = null;
					} else if ((this.mCurrTransfer instanceof BtuDataTransfer)) {
						this.mCurrTransfer.finishTransfer();
						this.mCurrTransfer = null;
					} else if ((this.mCurrTransfer instanceof AtuDataTransfer)) {
						this.mCurrTransfer.finishTransfer();
						this.mCurrTransfer = null;
					} else {
						this.mCurrTransfer.finishTransfer();
						this.mCurrTransfer = null;
					}

				}
			} else {
				this.mCurrTransfer.finishTransfer();
				this.mCurrTransfer = null;
			}
		}
		if (this.mCurrTransfer != null) {
			this.mCurrTransfer.addTask(model);
			return;
		}
		switch (mServerManager.getUploadServer()) {
		case ServerManager.SERVER_ATU: // ATU服务器
			this.mCurrTransfer = new AtuDataTransfer(mContext);
			break;
		case ServerManager.SERVER_BTU: // BTU服务器
			this.mCurrTransfer = new BtuDataTransfer(mContext);
			break;
		case ServerManager.SERVER_HTTPS: // HTTPS服务器
			this.mCurrTransfer = new HttpsDataTransfer(mContext);
			break;
		case ServerManager.SERVER_FLEET:// Fleet服务器
			this.mCurrTransfer = FleetDataTransferFactory.getInstance().getDataTransfer(mContext);
			break;
		case ServerManager.SERVER_FTP:// FTP服务器
			this.mCurrTransfer = new FtpDataTransfer(mContext);
			break;
		case ServerManager.SERVER_UNICOM: // 联通平台(FTP)
			this.mCurrTransfer = new UnicomDataTransfer(mContext);
			break;
			case ServerManager.SERVER_INNS://寅时服务器
				this.mCurrTransfer = new InnsDataTransfer(mContext);
			break;
		default:
			return;
		}
		if (this.mCurrTransfer != null) {
			this.mCurrTransfer.addTask(model);
			this.mCurrTransfer.start();
		}
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "------onDestroy----------");
		super.onDestroy();
		this.unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/***
	 * 显示通知
	 * 
	 * @param tickerText
	 *          通知显示的内容
	 * @param strBroadcast
	 *          点通知后要发的广播
	 */
	@SuppressWarnings("deprecation")
	private void showNotification(String tickerText, String strBroadcast) {
		// 生成通知管理器
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
		Notification.Builder notification = new Notification.Builder(this);
		notification.setTicker(tickerText);
		notification.setSmallIcon(R.mipmap.walktour);
		notification.setWhen(System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getBroadcast(DataTransService.this, 0, new Intent(strBroadcast), 0);
		// must set this for content view, or will throw a exception
		// 如果想要更新一个通知，只需要在设置好notification之后，再次调用
		// setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
		notification.setAutoCancel(true);
		notification.setContentIntent(contentIntent);
		notification.setContentTitle(getString(R.string.sys_alarm));
		notification.setContentText(tickerText);
		mNotificationManager.notify(R.string.service_started, notification.build());
	}

	/**
	 * 注册广播接收器
	 */
	private void regeditMyReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ServerMessage.ACTION_FLEET_STOPUPLD);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(mBroadcastReceiver, filter);
	}

	/**
	 * 数据交换结束执行
	 */
	public void handlerTransferEnd() {
		LogUtil.d(TAG, "----------handlerTransferEnd-----------");
		Intent intent = new Intent(EXTRA_DATA_TRANS_END);
		this.sendBroadcast(intent);
	}

	/**
	 * 数据交换反馈的消息
	 * 
	 * @param messageId
	 *          消息内容资源ID
	 */
	public void handlerMessage(int messageId) {
		LogUtil.d(TAG, "----------handlerMessage:" + this.mContext.getString(messageId) + "-----------");
		Intent intent = new Intent(EXTRA_DATA_TRANS_MESSAGE);
		intent.putExtra(EXTRA_KEY_MESSAGE, this.mContext.getString(messageId));
		this.sendBroadcast(intent);
	}

	/**
	 * 当前文件上传结束
	 * 
	 * @param fileModel
	 *          文件对象
	 */
	public void handleTransferFileEnd(UploadFileModel fileModel) {
		LogUtil.d(TAG, "----------handleTransferFileEnd-----------");
		Intent intent = new Intent(EXTRA_DATA_TRANS_FILE_END);
		intent.putExtra(EXTRA_DATA_TRANS_FILE_MODEL, fileModel);
		this.sendBroadcast(intent);
	}

	/**
	 * 当前文件上传开始
	 * 
	 * @param fileModel
	 *          文件对象
	 */
	public void handleTransferFileStart(UploadFileModel fileModel) {
		LogUtil.d(TAG, "----------handleTransferFileStart-----------");
		Intent intent = new Intent(EXTRA_DATA_TRANS_FILE_START);
		intent.putExtra(EXTRA_DATA_TRANS_FILE_MODEL, fileModel);
		this.sendBroadcast(intent);
	}

	/**
	 * 当前文件上传进度
	 * 
	 * @param fileModel
	 *          文件对象
	 */
	public void handleTransferFileProgress(UploadFileModel fileModel) {
		Intent intent = new Intent(EXTRA_DATA_TRANS_FILE_PROGRESS);
		intent.putExtra(EXTRA_DATA_TRANS_FILE_MODEL, fileModel);
		this.sendBroadcast(intent);
	}

	/**
	 * 广播接收器:接收来广播更新界面
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 如果是中断上传
			if (intent.getAction().equals(ServerMessage.ACTION_FLEET_STOPUPLD)) {
				// 获取停止类型
				String stopType = intent.getExtras().getString(ServerMessage.KEY_STOPTYPE);
				ServerOperateType operateType = ServerOperateType.valueOf(stopType);
				if (mCurrTransfer != null) {
					mCurrTransfer.stopOperate(new DataTransferModel(operateType));
				}
			} else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				int state = MyPhoneState.getInstance().getDataConnectState(getApplicationContext());
				int uploadNetwork = mServerManager.getUploadNetWork();

				LogUtil.d(TAG, String.format("upload network:%s,network state:%s",
						uploadNetwork == 0 ? "ALL" : (uploadNetwork == 1 ? "WIFI ONLY" : "MOBILE ONLY"), state));

				if (!MyPhoneState.getInstance().isNetworkAvirable(mContext)) {
					mLastDisconnectTime = System.currentTimeMillis();
				}

				switch (uploadNetwork) {
				case ServerManager.UPLOAD_NETWORK_ALL:
					isNetworkOn = state == MyPhoneState.DATA_STATE_WIFI || state == MyPhoneState.DATA_STATE_MOBILE;
					break;
				case ServerManager.UPLOAD_NETWORK_ONLY_WIFI:// Wifi模式
					isNetworkOn = state == MyPhoneState.DATA_STATE_WIFI;
					if (!isNetworkOn) {
						// 提示网络不可用
						handlerMessage(R.string.fleet_network_wifi);
					}
					break;
				case ServerManager.UPLOAD_NETWORK_ONLY_MOBILE:// data模式
					isNetworkOn = state == MyPhoneState.DATA_STATE_MOBILE;
					if (!isNetworkOn) {
						// 提示网络不可用
						handlerMessage(R.string.fleet_network_data);
					}
					break;
				}

			}
		}
	};

	public long getLastDisconnectTime() {
		return mLastDisconnectTime;
	}

	/**
	 * 更新数据库的上传状态
	 * 
	 * @param file
	 *          上传文件对象
	 * @param fileType
	 *          上传文件类型
	 * @param serverName
	 *          服务端描述
	 */
	public void updateDBUploadState(UploadFileModel file, FileType fileType, String serverName) {
		if (!file.isUpdateDB())
			return;
		int state = -1;
		switch (file.getUploadState(fileType)) {
		case SUCCESS:
			state = 100;
			break;
		case FAILURE:
		case FILE_NO_FOUND:
			state = -2;
			break;
		case INTERRUPT:
			state = -1;
		default:
			break;
		}
		DataManagerFileList.getInstance(mContext).uploadStateChange(file.getTestRecordId(), fileType, state, serverName);
	}

	/**
	 * 更新数据库的上传状态
	 * 
	 * @param file
	 *          上传文件对象
	 * @param fileType
	 *          上传文件类型
	 * @param filePath
	 *          文件路径
	 * @param fileName
	 *          文件名
	 */
	public void saveDBFilePath(UploadFileModel file, FileType fileType, String filePath, String fileName) {
		if (!file.isUpdateDB())
			return;
		DataManagerFileList.getInstance(mContext).saveRecordFilePath(file.getTestRecordId(), fileType, filePath, fileName);
	}

	public ServerManager getServerManager() {
		return mServerManager;
	}

}
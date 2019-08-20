package com.walktour.gui.upgrade;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.gui.R;

/**
 * 升级界面,绑定{@link UpgradeService}显示升级状态和进度
 *
 * @author qihang.li
 */
public class UpgradeActivity extends BasicActivity implements StatusChangeListener {

	public static final String TAG = "UpgradeActivity";
	/** 状态标识 */
	public static final String KEY_STATUS = "status";
	/** 更新内容标识 */
	public static final String KEY_MESSAGE = "message";
	/** 版本号标识 */
	public static final String KEY_VERSION = "version";
	/** 状态更新标识 */
	private static final int MSG_DIAG_STATUS = 0;
	/** 进度更新标识 */
	private static final int MSG_DIAG_PROGRESS = 1;
	/** 更新内容标识 */
	private static final int MSG_DIAG_UPGRADE_MESSAGE = 2;
	/** 版本号标识 */
	private static final int MSG_DIAG_VERSION = 3;
	/** 文本进度更新 */
	private static final int MSG_DOWN_PROCESS = 4;
	/** 绑定类 */
	private UpgradeBinder iBinder;
	/** 当前状态 */
	private Status mStatus = Status.IDLE;
	/** 进度条控件 */
	private ProgressBar progressBar;
	/** 更新内容显示 */
	private EditText upgradeMessage;
	/** 更新进度 */
	private TextView downloadProgress;
	/** 更新内容 */
	private String messageContent = "";
	/** 当前显示的对话框 */
	private BasicDialog dialog;
	/** 服务器端版本号 */
	private String remoteVersion = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "----onCreate----");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				this.mStatus = (Status) bundle.getSerializable(KEY_STATUS);
				this.messageContent = (String) bundle.getString(KEY_MESSAGE);
				this.remoteVersion = (String) bundle.getString(KEY_VERSION);
				if (remoteVersion.endsWith("."))
					remoteVersion = remoteVersion.substring(0, remoteVersion.length() - 1);
				this.showStatus(this.mStatus);
				this.upgradeMessage.setText(this.messageContent);
			}
			Intent service = new Intent(this, UpgradeService.class);
			service.putExtra("timeout", 10 * 1000);
			startService(service);
			bindService(service, serviceConnection, BIND_AUTO_CREATE);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage());
		}
	}

	/**
	 * 初始化控件
	 */
	private View findView() {
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.upgrade, null);
		this.upgradeMessage = (EditText) view.findViewById(R.id.upgrade_message);
		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		this.downloadProgress=(TextView)view.findViewById(R.id.text_download_progressrogress);
		return view;
	}

	@Override
	protected void onStart() {
		LogUtil.d(TAG, "----onStart----");
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		LogUtil.d(TAG, "----onDestroy----");
		unbindService(serviceConnection);
		if (this.dialog != null && this.dialog.isShowing())
			this.dialog.dismiss();
		this.iBinder.stopDownload();
        this.iBinder.setStatusChangeListener(null);
		Intent service = new Intent(this, UpgradeService.class);
		stopService(service);
		super.onDestroy();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBinder = (UpgradeBinder) service;
			iBinder.setStatusChangeListener(UpgradeActivity.this);
			if (mStatus == Status.IDLE) {
				iBinder.checkNewVersion();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
            if (iBinder != null) {
                iBinder.setStatusChangeListener(null);
            }
        }
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DIAG_STATUS:
				Status status = (Status) msg.obj;
				showStatus(status);
				break;
			case MSG_DIAG_PROGRESS:
				int progress = (Integer) msg.obj;
				showProgress(progress);
				break;
			case MSG_DOWN_PROCESS:
				String processText = (String) msg.obj;
				if (processText!=null&&downloadProgress!=null){
					downloadProgress.setText(processText);
				}
				break;
			case MSG_DIAG_UPGRADE_MESSAGE:
				String message = (String) msg.obj;
				showUpgradeMessage(message);
				break;
			case MSG_DIAG_VERSION:
				remoteVersion = (String) msg.obj;
				if (remoteVersion.endsWith("."))
					remoteVersion = remoteVersion.substring(0, remoteVersion.length() - 1);
				break;
			}
		}
	};

	/**
	 * 显示当前状态
	 *
	 * @param status
	 */
	private void showStatus(Status status) {
		Builder builder = new Builder(UpgradeActivity.this);
		builder.setMessageShowCenter(true);
		if (this.dialog != null && this.dialog.isShowing())
			this.dialog.dismiss();
		switch (status) {
		case NETWORK_UNAVIRABLE:// 网络不可用
			builder.setMessage(R.string.update_net_error);
			break;
		case TEST_RUNNING:// 正在测试
			builder.setMessage(R.string.update_testing);
			builder.setPositiveButton(R.string.main_stop, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iBinder.stopTest();
				}
			}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			break;
		case TEST_STOPPING:// 正在停止测试
			builder.setMessage(R.string.stop_testing);
			break;
		case CHECKING_VERSION:// 正在检查版本
			break;
		case VERSION_LOCAL_INCLUDE:// 本地已经有最新版本安装包
			builder.setTitle(getVersionTitle());
			builder.setView(this.findView());
			builder.setPositiveButton(R.string.download_finish_install, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iBinder.install();
					finish();
				}
			}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			break;
		case VERSION_NO_INFO:// 无版本信息
			builder.setTitle(R.string.about_check_updates);
			builder.setMessage(R.string.no_info);
			break;
		case VERSION_NEED_UPGRADE:// 有升级版本
			builder.setTitle(getVersionTitle());
			builder.setView(this.findView());
			builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iBinder.download();
				}
			}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			break;

		case VERSION_CURRENT_LATEST:// 当前是最新版本
			builder.setTitle(R.string.about_check_updates);
			builder.setMessage(R.string.no_update);
			break;

		case DOWNLOAD_FAIL_NOSDCARD:// 无可以用SD卡
			builder.setTitle(R.string.about_check_updates);
			builder.setMessage(R.string.update_nosdcard);
			break;

		case CONNECTING_FTP:// 正在连接FTP
			builder.setTitle(R.string.about_check_updates);
			builder.setMessage(R.string.connete_server);
			break;

		case CONNECT_FTP_FAIL:// 连接FTP失败
			builder.setTitle(R.string.about_check_updates);
			builder.setMessage(R.string.conn_faild);
			break;

		case DOWNLOAD_STARTED:// 正在下载
			builder.setTitle(getVersionTitle());
			builder.setView(this.findView());
			this.upgradeMessage.setText(this.messageContent);
			builder.setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iBinder.stopDownload();
				}
			});
			break;

		case REMOTE_NOT_EXSIT:// 目标文件不存在
			builder.setMessage(R.string.update_remote_not_exist);
			break;

		case DOWNLOAD_SUCCESS:// 下载成功
			builder.setTitle(getVersionTitle());
			builder.setView(this.findView());
			this.upgradeMessage.setText(this.messageContent);
			builder.setPositiveButton(R.string.download_finish_install, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iBinder.install();
					finish();
				}
			}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			this.iBinder.install();
			finish();
			break;

		case DOWNLOAD_FAIL:// 下载失败
			builder.setMessage(R.string.update_download_error);
			break;
		case DOWNLOAD_STOP:// 下载停止
			builder.setTitle(getVersionTitle());
			builder.setView(this.findView());
			this.upgradeMessage.setText(this.messageContent);
			builder.setPositiveButton(R.string.upgrade_continue, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					iBinder.download();
				}
			}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			break;
		case IDLE:
			break;
		case DOWNLOAD_DOING:
			break;
		case NETWORK_CHECK:// 检查服务器是否可以登录
			builder.setMessage(R.string.fetch_version_info);
			break;
		default:
			break;
		}
		builder.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mStatus == Status.DOWNLOAD_DOING)
						return true;
					finish();
				}
				return false;
			}

		});
		//修复在弹出框外部点击消失后无法点击页面item问题(透明界面未关闭)
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(!UpgradeActivity.this.isDestroyed()){
					finish();
				}
			}
		});
		if (!isDestroyed()){
			this.dialog = builder.show();
		}
	}

	/**
	 * 获取带版本号的标题
	 *
	 * @return
	 */
	private String getVersionTitle() {
		String title = getString(R.string.check_new_version);
		return title.replaceAll("vs", remoteVersion);
	}

	/**
	 * 显示更新内容
	 *
	 * @param message
	 */
	protected void showUpgradeMessage(String message) {
		this.showStatus(Status.VERSION_NEED_UPGRADE);
		this.upgradeMessage.setText(message);
	}

	/**
	 * 显示当前进度
	 *
	 * @param progress
	 */
	private void showProgress(int progress) {
		this.mStatus = Status.DOWNLOAD_DOING;
		this.progressBar.setVisibility(View.VISIBLE);
		this.progressBar.setMax(100);
		this.progressBar.setProgress(progress);
	}

	@Override
	public void onStatusChange(Status status) {
		mStatus = status;
		if (status != Status.VERSION_NEED_UPGRADE)
			mHandler.obtainMessage(MSG_DIAG_STATUS, status).sendToTarget();
	}

	@Override
	public void onProgressChange(long localSize, long remoteSize) {
		int progress = (int) ((double) localSize / (double) remoteSize * 100);
		mHandler.obtainMessage(MSG_DIAG_PROGRESS, progress).sendToTarget();
		String downLoadProcess=localSize+" KB /"+remoteSize+" KB";
		mHandler.obtainMessage(MSG_DOWN_PROCESS,downLoadProcess).sendToTarget();
	}

	@Override
	public void setUpgradeMessage(String message) {
		this.messageContent = message;
		mHandler.obtainMessage(MSG_DIAG_UPGRADE_MESSAGE, message).sendToTarget();
	}

	@Override
	protected void onResume() {
		LogUtil.d(TAG, "----onResume----");
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (this.mStatus == Status.DOWNLOAD_DOING) {
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setRemoteVersion(String version) {
		mHandler.obtainMessage(MSG_DIAG_VERSION, version).sendToTarget();
	}
}

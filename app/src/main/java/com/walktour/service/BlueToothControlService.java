package com.walktour.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.ServiceDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * 手机间蓝牙通信控制服务类
 * 
 * @author jianchao.wang
 *
 */
public class BlueToothControlService extends Service {
	private static final String tag = "BlutToothControlService";
	public static final String BlueToothStartType = "BlueToothStartType";
	public static final int BlueToothStart_Server = 1;
	public static final int BlueToothStart_Client = 2;

	private static final int HANDLE_MESSAGE_READ = 1;
	private static final int HANDLE_MESSAGE_WRITE = 2;
	private static final int HANDLE_DISCONNECTED = 3;

	private static final int SYNC_MSG_SEND = 1;
	private static final int SYNC_MSG_RECV = 2;
	private static final String TAG_RESPONSE = "RESPONSE::";
	private static final String TAG_NORMAL = "NORMAL::";
	private static final String TAG_SYNCMSG = "SYNCMSG::";
	private static final String TAG_RESEND = "::RESEND";
	private static final String HARDMSGTAG = "HARTMSGTAG";
	private static final String SYNC_TIMES = "SYNCTIMES::";

	/** 发送到另一端的同步消息,收到接收端的响应后修改该值为发送值,防止发出去后,接受端没有收到同步消息 */
	private String sendSyncMessage = "";
	/** 从另一端接收过来的同步消息串 */
	private String receSyncMessage = "";
	/** 发送同步消息成功状态,发送时置为false;受到同步消息或Response消息后,如果条件成立即状态为False,则发送同步消息并置为True */
	private boolean sendSyncFlag = false;
	/** 需要响应的同步消息发送成功 */
	private boolean waitResponse = false;
	/** 定义多长时间间隔发送心跳消息 */
	private static final int hardSendInt = 2000;
	/** 定义多长时间未收到心跳超时消息 */
	private static final int hardTimeOut = 20000;

	private UUID uuid = UUID.fromString("40b0a6ef-6111-451a-aa78-27d13b358474");
	private BluetoothAdapter adapter;
	private BluetoothDevice bandDev;

	private BlueServerThread serverThread = null;
	private BlueClientThread clientThread = null;
	private ConnectedThread connectedThread = null;
	private HardMsgSendThread sendMsgThread = null;
	private HardMsgReceThread receMsgThread = null;
	/** 消息处理类 */
	private MyHandler mHandler = new MyHandler(this);
	private long receMsgTime = 0;
	// 持续失败次数
	// private int keepDisconnectTime = 0;

	private ApplicationModel appModel = null;

	private static int connectType = BlueToothStart_Server;

	// private Queue<String> sendMsgList = new LinkedBlockingQueue<String>();
	// //发送消息队列
	private ArrayList<SendMsgModel> sendMsgList = new ArrayList<SendMsgModel>(); // 用队列的话,无法在开始位置插入需要的信息
	private String synResponseStr = ""; // 同步消息响应串
	private String norResponseStr = ""; // 普通消息响应串
	private int[] lockObj = new int[0]; // 队列空时通知对象锁
	/** 保证消息发送成功线程 */
	private KeepSendMsgSuccThread keepSendSuccThread = null;
	private boolean serviceRunnig = true; // 服务运行中.
	private Context mContext = null;

	public BlueToothControlService() {
	}

	private class SendMsgModel {

		public String sendMsg = "";
		public boolean needResponse = false;

		/**
		 * 新建发送消息对象
		 * 
		 * @param msg
		 *          发送内容,不需要等待响应
		 */
		public SendMsgModel(String msg) {
			this.sendMsg = msg;
		}

		/**
		 * 新建发送消息对象
		 * 
		 * @param msg
		 *          发送内容
		 * @param needResp
		 *          需要等待响应重发
		 */
		public SendMsgModel(String msg, boolean needResp) {
			this.sendMsg = msg;
			this.needResponse = needResp;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		registRecevier();
		appModel = ApplicationModel.getInstance();
		initBlueToothEnv();
	}

	/** 注册消息接收 */
	private void registRecevier() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_UNIT_NORMAL_SEND);
		filter.addAction(WalkMessage.ACTION_UNIT_SYNC_START);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.w(tag, "--onDestroy--" + connectType);
		unregisterReceiver(mReceiver);

		stopConnect();
	}

	/** 开启连接线程 */
	private void startConnect() {
		if (connectType == BlueToothStart_Server) {
			Log.w(tag, "--start bluetooth server--");
			serverThread = new BlueServerThread();
			serverThread.start();
		} else {
			Log.w(tag, "--start bluetooth client--");
			clientThread = new BlueClientThread(bandDev);
			clientThread.start();
		}
	}

	/** 关闭Socket连接线程 */
	private void stopConnect() {
		serviceRunnig = false;

		appModel.setBluetoothConnected(false);
		sendConnectStateChange();

		if (connectedThread != null) {
			connectedThread.cancel();
		}
		if (clientThread != null) {
			clientThread.cancel();
		}
		if (serverThread != null) {
			serverThread.cancel();
		}
	}

	/** 重新启动服务连接线程 */
	// private class ReStartConnectThr extends Thread {
	// @Override
	// public void run() {
	// LogUtil.w(tag, "--ReStartConnectThr--");
	// stopConnect();
	// UtilsMethod.ThreadSleep(2000);
	// startConnect();
	// }
	// }

	private void initBlueToothEnv() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (!adapter.isEnabled()) {
			adapter.enable();
			Intent blueTooth = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
			blueTooth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(blueTooth);
			// Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// startActivity(enabler);
			// startActivityForResult(enabler,reCode);
		}
		Log.w(tag, "--add:" + adapter.getAddress() + "--name:" + adapter.getName());
		Set<BluetoothDevice> band = adapter.getBondedDevices();
		Log.w(tag, "--band:" + band.hashCode());

		Iterator<BluetoothDevice> it = band.iterator();
		/*
		 * while(it.hasNext()){ BluetoothDevice bd = it.next(); Log.w(tag,"--bd:" +
		 * bd.getAddress() + "--name:" + bd.getName() + "--state:" +
		 * bd.getUuids().hashCode() + "--" +bd.getUuids().toString() + "--" +
		 * bd.getUuids().length); }
		 */
		if (it.hasNext()) {
			bandDev = it.next();
			Log.w(tag, "--bandDev:" + bandDev.getName() + "---add:" + bandDev.getAddress());
		} else {
			Log.w(tag, "--no band device--");
		}
	}

	private class BlueServerThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;
		private BluetoothSocket socket = null;

		public BlueServerThread() {
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client code
				tmp = adapter.listenUsingRfcommWithServiceRecord(adapter.getName(), uuid);
			} catch (IOException e) {
				LogUtil.w(tag, "BlueServerThread init", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				try {
					if (mmServerSocket != null) {
						socket = mmServerSocket.accept();
						Log.w(tag, "--serversocket accepted:" + (socket == null));

						appModel.setBluetoothConnected(socket != null);
						sendConnectStateChange();

						receMsgTime = System.currentTimeMillis();
						receMsgThread = new HardMsgReceThread();
						receMsgThread.start();

						keepSendSuccThread = new KeepSendMsgSuccThread(lockObj);
						keepSendSuccThread.start();

						// If a connection was accepted
						if (socket != null) {
							// Do work to manage the connection (in a separate thread)
							manageConnectedSocket(socket);
							// mmServerSocket.close();
							break;
						}
					}
				} catch (IOException e) {
					LogUtil.w(tag, "BlueServerThread Run", e);
					break;
				}
			}
		}

		public void cancel() {
			try {
				if (socket != null) {
					socket.close();
				}
				if (mmServerSocket != null) {
					mmServerSocket.close();
				}
			} catch (Exception e) {
				LogUtil.w(tag, "BlueServerThread cancel", e);
			}
		}
	}

	private class BlueClientThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public BlueClientThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			// mmDevice = adapter.getRemoteDevice("1C:66:AA:F2:9B:3C");
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				Log.w(tag, "--bandDevices:" + mmDevice.getName() + "--" + mmDevice.getAddress());
				tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
				// Method m = mmDevice.getClass().getMethod("createRfcommSocket", new
				// Class[] { int.class });
				// tmp = (BluetoothSocket) m.invoke(mmDevice, 3);
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.w(tag, "BlueClientThread init", e);
			}

			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			adapter.cancelDiscovery();
			Log.w(tag, "--BlueClientThread run--");
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				if (mmSocket != null) {
					mmSocket.connect();
					Log.w(tag, "--mmSocket.connect()--");
					manageConnectedSocket(mmSocket);
					appModel.setBluetoothConnected(true);
					sendConnectStateChange();

					receMsgTime = System.currentTimeMillis();

					addToSendMsgList(true, new SendMsgModel(SYNC_TIMES + receMsgTime, true));

					sendMsgThread = new HardMsgSendThread();
					sendMsgThread.start();

					keepSendSuccThread = new KeepSendMsgSuccThread(lockObj);
					keepSendSuccThread.start();
				}
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				LogUtil.w(tag, "BlueClientThread e1", connectException);
				sendConnectStateChange();
				try {
					if (mmSocket != null) {
						mmSocket.close();
					}
				} catch (IOException closeException) {
					LogUtil.w(tag, "BlueClientThread e2", closeException);
				}
				return;
			}

		}

		public void cancel() {
			try {
				if (mmSocket != null) {
					mmSocket.close();
				}
			} catch (IOException closeException) {
				LogUtil.w(tag, "BlueClientThread run", closeException);
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		private boolean connectedRun = true;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.w(tag, "--ConnectedThread run--");

			byte[] buffer = new byte[1024]; // buffer store for the stream
			int bytes; // bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while (connectedRun) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(HANDLE_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				} catch (IOException e) {
					LogUtil.w(tag, "ConnectedThread run", e);
					break;
				}
			}
		}

		/* Call this from the main Activity to send data to the remote device */
		public synchronized void write(byte[] bytes) {
			try {
				if (mmOutStream != null) {
					mmOutStream.write(bytes);
				}
			} catch (IOException e) {
				LogUtil.w(tag, "ConnectedThread write", e);
			}
		}

		/* Call this from the main Activity to shutdown the connection */
		public void cancel() {
			connectedRun = false;
			try {
				if (mmOutStream != null) {
					mmOutStream.close();
				}
				if (mmSocket != null) {
					mmSocket.close();
				}
			} catch (IOException e) {
				LogUtil.w(tag, "ConnectedThread cancel", e);
			}
		}
	}

	/**
	 * 心跳包发送线程
	 * 
	 * @author tangwq
	 *
	 */
	private class HardMsgSendThread extends Thread {

		public void run() {
			while (serviceRunnig) {
				try {
					Thread.sleep(hardSendInt);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 当前没有需要发送的消息且距上次收到消息超过心跳间隔才发送心跳包
				if (sendMsgList.size() == 0 && (System.currentTimeMillis() - receMsgTime > hardSendInt)) {
					addToSendMsgList(new SendMsgModel(HARDMSGTAG));
				}
				if (System.currentTimeMillis() - receMsgTime > hardTimeOut) {
					LogUtil.w(tag, "--lastReceC:" + receMsgTime + "--currTime:" + System.currentTimeMillis() + "--Testting:"
							+ appModel.isTesting());

					receMsgTime = System.currentTimeMillis();
					appModel.setBluetoothConnected(false);
					sendConnectStateChange();
					// 如果当前正在测试中才弹出提示窗口
					if (appModel.isTesting()) {
						mHandler.obtainMessage(HANDLE_DISCONNECTED).sendToTarget();
					}
				}
			}
		}
	}

	/**
	 * 心跳包接收线程
	 * 
	 * @author tangwq
	 *
	 */
	private class HardMsgReceThread extends Thread {

		public void run() {
			while (serviceRunnig) {
				try {
					Thread.sleep(hardSendInt);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 如果接受时间与当前超过超时时间,设置连接状态为未连接且弹出Toast提示
				if (System.currentTimeMillis() - receMsgTime > hardTimeOut) {
					LogUtil.w(tag, "--lastReceS:" + receMsgTime + "--currTime:" + System.currentTimeMillis() + "--Testting:"
							+ appModel.isTesting());
					// keepDisconnectTime++;
					receMsgTime = System.currentTimeMillis();
					appModel.setBluetoothConnected(false);
					sendConnectStateChange();

					if (appModel.isTesting()) {
						mHandler.obtainMessage(HANDLE_DISCONNECTED).sendToTarget();
					}
					// 如果服服务连续3次,客户端连续4次报断开连接,则各自重新启动服务端,客户端,此处为了保证服务端比客户端先启
					// 不靠谱,还是手工开关一下吧.
					/*
					 * if(keepDisconnectTime % (connectType == BlueToothStart_Server ? 3 :
					 * 4) == 0){ new Thread(new ReStartConnectThr()).start(); }
					 */
				}
			}
		}
	}

	private void manageConnectedSocket(BluetoothSocket socket) {
		connectedThread = new ConnectedThread(socket);
		connectedThread.start();
	}

	/**
	 * 获得输入流
	 * 
	 * @param inStream
	 * @return
	 */
	public static byte[] readStream(InputStream inStream) {
		// ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		// byte[] buffer = new byte[1024];
		// int len = -1;
		// while ((len = inStream.read(buffer)) != -1) {
		// outSteam.write(buffer, 0, len);
		// }
		// outSteam.close();
		// inStream.close();
		// return outSteam.toByteArray();
		try {
			int count = 0;
			while (count == 0) {
				count = inStream.available();
			}
			byte[] b = new byte[count];
			inStream.read(b);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new byte[0];
	}

	private static class MyHandler extends Handler {
		private WeakReference<BlueToothControlService> refrence;
		/** 上一条同步消息收到的时间,如果当前收到的同步时间小于3秒时表示当前同步消息重复了,不需要继续处理 */
		private long lastNormalTime = 0;

		private MyHandler(BlueToothControlService service) {
			this.refrence = new WeakReference<BlueToothControlService>(service);
		}

		public void handleMessage(android.os.Message msg) {
			BlueToothControlService service = this.refrence.get();
			switch (msg.what) {
			case HANDLE_MESSAGE_READ:
				service.receMsgTime = System.currentTimeMillis();
				if (!service.appModel.isBluetoothConnected()) {
					service.appModel.setBluetoothConnected(true);
					service.sendConnectStateChange();
				}

				int bufLen = msg.arg1;
				byte[] buffer = (byte[]) msg.obj;

				String msgs = new String(Arrays.copyOf(buffer, bufLen));
				if (msgs.equals(HARDMSGTAG)) {
					service.addToSendMsgList(service.new SendMsgModel(TAG_RESPONSE + msgs));
					// 收到心跳消息记收到时间
				} else if (msgs.equals(TAG_RESPONSE + HARDMSGTAG)) {
					// 收到响应心跳消息计发送时间
					// sendHardMsgTime = System.currentTimeMillis();
				} else if (msgs.startsWith(TAG_NORMAL)) {
					boolean isReSendMsg = msgs.endsWith(TAG_RESEND);
					if (isReSendMsg) {
						msgs = msgs.substring(0, msgs.length() - TAG_RESEND.length());
					}
					service.addToSendMsgList(true, service.new SendMsgModel(TAG_RESPONSE + msgs));

					if (!isReSendMsg || (System.currentTimeMillis() - lastNormalTime > 5000)) {
						service.sendNormalMsg(msgs.substring(TAG_NORMAL.length()));
					}

					lastNormalTime = System.currentTimeMillis();
				} else if (msgs.startsWith(TAG_SYNCMSG)) {
					if (msgs.endsWith(TAG_RESEND)) {
						msgs = msgs.substring(0, msgs.length() - TAG_RESEND.length());
					}
					service.addToSendMsgList(true, service.new SendMsgModel(TAG_RESPONSE + msgs));

					service.sendSycnMsg(SYNC_MSG_RECV, msgs);
					Log.w(tag, "--readSyn:" + msgs);
					// }else if(msgs.startsWith(TAG_RESPONSE + TAG_SYNCMSG)){
				} else if (msgs.startsWith(TAG_RESPONSE)) {
					if (msgs.startsWith(TAG_RESPONSE + TAG_SYNCMSG)) {
						service.synResponseStr = msgs.substring(TAG_RESPONSE.length());
						service.sendSycnMsg(SYNC_MSG_SEND, service.synResponseStr);
						Log.w(tag, "--readSynR:" + msgs);
					} else {
						service.norResponseStr = msgs.substring(TAG_RESPONSE.length());
						Log.w(tag, "--readNorR:" + msgs);
					}
				} /*
					 * //反响应的动作放上面去统一处理,如果不是同步消息响应,都标置为普通响应消息 else
					 * if(msgs.startsWith(TAG_RESPONSE + TAG_NORMAL)){ norResponseStr =
					 * msgs.substring(TAG_RESPONSE.length()); Log.w(tag,"--readNorR:" +
					 * msgs); }
					 */else if (msgs.startsWith(SYNC_TIMES)) {
					// 执行时间同步之前, 关闭系统设置里的自动同步
					UtilsMethod.setTimeAuto(service.mContext, false);

					service.addToSendMsgList(true, service.new SendMsgModel(TAG_RESPONSE + msgs));
					long syncTime = 0;
					if (msgs.endsWith(TAG_RESEND)) {
						syncTime = Long.parseLong(msgs.substring(SYNC_TIMES.length(), msgs.length() - TAG_RESEND.length()));
					} else {
						syncTime = Long.parseLong(msgs.substring(SYNC_TIMES.length()));
					}
					UtilsMethod.setTime(syncTime, service.getApplicationContext());
				} else {
					LogUtil.w(tag, "--readOther:" + msgs);
				}
				break;
			case HANDLE_MESSAGE_WRITE:
				service.connectedThread.write(msg.obj.toString().getBytes());
				break;
			case HANDLE_DISCONNECTED:
				service.diagBlueDisconnct();
				break;
			}
		}

	};

	private void diagBlueDisconnct() {
		Intent dialIntent = new Intent(getApplicationContext(), ServiceDialog.class);
		dialIntent.putExtra(ServiceDialog.DIALOG_ID, ServiceDialog.BLUETOOTH_INTERRUPT);
		dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(dialIntent);
	}

	/**
	 * 从指定的byte数组中截取指定长度的数组长度返回
	 * 
	 * @param bytes
	 * @param bytesLen
	 * @return
	 */
	public static byte[] getBytesByLen(byte[] bytes, int bytesLen) {
		if (bytes.length <= bytesLen) {
			return bytes;
		}
		byte[] result = new byte[bytesLen];
		for (int i = 0; i < bytesLen; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	/**
	 * 蓝牙socket连接成功后发送消息,用于设置界面的状态刷新
	 */
	private void sendConnectStateChange() {
		sendBroadcast(new Intent(WalkMessage.ACTION_BLUETOOTH_SOCKET_CHANGE));
	}

	/**
	 * 发送普通消息
	 * 
	 * @param msg
	 */
	private void sendNormalMsg(String msg) {
		Intent normal = new Intent(WalkMessage.ACTION_UNIT_NORMAL_RECEIVE);
		normal.putExtra(WalkMessage.KEY_UNIT_MSG, msg);
		sendBroadcast(normal);
	}

	/**
	 * 发送同步消息
	 * 
	 * @param type
	 *          1:发送出去收到响应的同步串,2:接受到的同步串
	 * @param syncMsg
	 *          同步消息内容
	 */
	private void sendSycnMsg(int type, String syncMsg) {
		if (type == SYNC_MSG_SEND) {
			sendSyncMessage = syncMsg;
		} else if (type == SYNC_MSG_RECV) {
			receSyncMessage = syncMsg;
		}

		if (sendSyncMessage.equals(receSyncMessage) && !sendSyncFlag) {
			sendSyncFlag = true;

			Intent intent = new Intent(WalkMessage.ACTION_UNIT_SYNC_DONE);
			intent.putExtra(WalkMessage.KEY_UNIT_MSG, syncMsg.substring(TAG_SYNCMSG.length()));
			sendBroadcast(intent);
		}
	}

	/**
	 * 保证队列中的消息发送成功线程
	 * 
	 * @author tangwq
	 *
	 */
	private class KeepSendMsgSuccThread extends Thread {
		Object lock;

		public KeepSendMsgSuccThread(Object obj) {
			this.lock = obj;
		}

		public void run() {
			SendMsgModel sendModel = null;
			while (serviceRunnig) {
				if (sendMsgList.size() <= 0) {
					try {
						synchronized (lock) {
							lock.wait();
						}
					} catch (InterruptedException ie) {
						LogUtil.w(tag, "WaitSynchronized", ie);
					}
				}

				sendSyncFlag = false;

				/*
				 * 如果当前不在等待响应成功的状态,或者当前要发送的消息不需要等待响应则执行消息发送 主要是处理同一时间只有一个消息在等待响应
				 */
				if (!waitResponse || !sendMsgList.get(0).needResponse) {
					sendModel = sendMsgList.remove(0);

					if (sendModel.needResponse) {
						LogUtil.w(tag, "--SendMsg:" + sendModel.sendMsg);
						waitResponse = true;
						if (sendModel.sendMsg.startsWith(TAG_SYNCMSG)) {
							synResponseStr = "";
						} else {
							norResponseStr = "";
						}

						new WaitResponseThread(sendModel.sendMsg).start();
					}

					mHandler.obtainMessage(HANDLE_MESSAGE_WRITE, sendModel.sendMsg).sendToTarget();
				}

				// 消息发送后休眠50毫秒,错开连续两条同时发送的问题
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 需要等待响应的消息单独起线程等待,如果超时时间内等不到则重发该消息
	 * 
	 * @author tangwq
	 *
	 */
	private class WaitResponseThread extends Thread {
		private String sendMsg = "";

		public WaitResponseThread(String msg) {
			this.sendMsg = msg;
		}

		public void run() {
			int rTime = 0;
			while (serviceRunnig && !synResponseStr.equals(sendMsg) && !norResponseStr.equals(sendMsg)) {
				rTime++;
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					LogUtil.w(tag, "WaitResponse", e);
				}
				// 一点五秒钟未收到响应消息,重发
				if (rTime % 50 == 0) {
					// mHandler.obtainMessage(HANDLE_MESSAGE_WRITE, sendMsg +
					// TAG_RESEND).sendToTarget();
					addToSendMsgList(true, new SendMsgModel(sendMsg + TAG_RESEND));
					LogUtil.w(tag, "--reSendMsg:" + sendMsg);
				}
			}
			waitResponse = false;
			LogUtil.w(tag, "--SendMsgSucces:" + sendMsg);
		}
	}

	/**
	 * 将要发送的消息添加到发送消息列表
	 * 
	 * @param str
	 *          发送消息内容
	 */
	private void addToSendMsgList(SendMsgModel model) {
		addToSendMsgList(false, model);
	}

	/**
	 * 将要发送的消息添加到发送消息列表
	 * 
	 * @param isPriority
	 *          是否优选发送(响应消息优先发送)
	 * @param str
	 *          发送内容
	 */
	private void addToSendMsgList(boolean isPriority, SendMsgModel model) {
		if (isPriority) {
			sendMsgList.add(0, model);
		} else {
			sendMsgList.add(model);
		}

		synchronized (lockObj) {
			lockObj.notifyAll();
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String receMsg = intent.getExtras().getString(WalkMessage.KEY_UNIT_MSG);
			if (intent.getAction().equals(WalkMessage.ACTION_UNIT_NORMAL_SEND)) {
				addToSendMsgList(new SendMsgModel(TAG_NORMAL + receMsg, true));
			} else if (intent.getAction().equals(WalkMessage.ACTION_UNIT_SYNC_START)) {
				addToSendMsgList(new SendMsgModel(TAG_SYNCMSG + receMsg, true));
			}
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w(tag, "---onStart---" + (intent == null));
		if ((intent != null)) {
			connectType = intent.getIntExtra(BlueToothStartType, BlueToothStart_Server);
			startConnect();
		} else {
			this.onDestroy();
		}
		return super.onStartCommand(intent, flags, startId);
	}
}

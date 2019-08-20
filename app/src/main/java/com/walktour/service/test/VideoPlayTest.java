package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.dingli.dmplayer.sdktest.DMPlayerAPI.FailedInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.KpiInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.MsgInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.QosInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvDropInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvFinishInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartFailedInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsfail_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsstart_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnssucc_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.send_get_info;
import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.RcuEventCommand.FailReason;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.DataTaskEvent;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import cn.dolit.siteparser.Module;

/**
 * @author Xie Jihong 视频播放服务
 */
@SuppressLint({ "SdCardPath", "HandlerLeak" })
public class VideoPlayTest extends VideoPlayTestBase {
	private static final String TAG = "VideoPlayTest";
	private static final int HTTPVS_TEST = 1024;

	private ipc2jni aIpc2Jni;


	static {
		System.loadLibrary("crystax_shared");
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("miniSDL");
		System.loadLibrary("ipc2");
		System.loadLibrary("iconv");
		System.loadLibrary("myiconv");
		System.loadLibrary("ipc2jni");
		System.loadLibrary("mysock");
		System.loadLibrary("crypto_v11");
		System.loadLibrary("ssl_v11");
		System.loadLibrary("mydns");
		System.loadLibrary("curl");
		System.loadLibrary("avutil");
		System.loadLibrary("avcodec");
		System.loadLibrary("swscale");
		System.loadLibrary("flvStream");
		System.loadLibrary("jpeg8d");
		System.loadLibrary("vMOS_V301");
		System.loadLibrary("DES");
	}

	@Override
	protected void startTest() {
		try {
			LogUtil.w(TAG, "start to prep args");
			JitterAvg=0;
			CurJitter=0;
			StringBuffer args = new StringBuffer();
			if (aIpc2Jni == null) {
				aIpc2Jni = new ipc2jni(mEventHandler);
			}
			aIpc2Jni.initServer(this.getLibLogPath());
			args.append("-m httpvs -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory());
			LogUtil.i(TAG, args.toString());
			Module.release();
			File ff=new File("/data/data/com.walktour.gui/files/script.spp");
			if(ff.exists()){
			    LogUtil.w(TAG,"ff is exists.");
            }else{
                LogUtil.w(TAG,"ff is not exists.");
            }
			int bvalue=Module.instance().init("/data/data/com.walktour.gui/files/script.spp",this.getApplicationContext());
			LogUtil.w(TAG,"bvalue="+bvalue);
			useRoot=false;//为了适配电信VolteRom，统一使用SO库
			String client_path =AppFilePathUtil.getInstance().getAppLibFile(useRoot ? "DMPlayerAndroid" : "libdmplayer_so.so").getAbsolutePath();
			LogUtil.i(TAG, "client_path:" + client_path);
			if (useRoot) {
				String get_root = "chmod 777 " + client_path;
				ipc2jni.runCommand(get_root);
				aIpc2Jni.set_su_file_path("su -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance().getAppLibDirectory());
			}
			aIpc2Jni.run_client(client_path, args.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w(TAG, e.getMessage());
			Message msg = mHandler.obtainMessage(TEST_STOP, "Error");
			msg.sendToTarget();
			aIpc2Jni.uninit_server();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(tag, "---onStart");
		int startFlag = super.onStartCommand(intent, flags, startId);

		Thread thread = new Thread(new RunTest());
		thread.start();

		return startFlag;
	}

	private Handler mEventHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;

			// 如果已经脱网或手动结束，则不再接收任何回调
			if (isTestFinish)
				return;

			currentStep = aMsg.event_id;

			LogUtil.w(TAG, "--event_id:" + aMsg.event_id + "--" + aMsg.data);

			switch (aMsg.event_id) {
			case HTTPVS_QOS_ARRIVED:
				QosInfo qosInfo = new QosInfo();
				this.datapack(qosInfo, aMsg.data);
				onQosArrived(qosInfo, aMsg.getRealTime());
				break;
			case HTTPVS_PLAY_QOS_ARRIVED:
				onPlayQosArrived(null, aMsg.getRealTime());
				break;
			case HTTPVS_INITED:
				onInited(aMsg.getRealTime());
				break;
			case HTTPVS_URLPARSE_SUCCESS:
				onUrlParseSuccess(aMsg.getRealTime());
				break;
			case HTTPVS_URLPARSE_FAILED:
				FailedInfo failInfo = new FailedInfo();
				this.datapack(failInfo, aMsg.data);
				onUrlParseFailed(failInfo, aMsg.getRealTime());
				break;
			case HTTPVS_KPIS_REPORT:
				KpiInfo kpiInfo = new KpiInfo();
				this.datapack(kpiInfo, aMsg.data);
				onKPIsReport(kpiInfo, aMsg.getRealTime());
				break;
			case HTTPVS_DNSRESOLVE_START:
				dnsstart_info dnsStartInfo = new dnsstart_info();
				this.datapack(dnsStartInfo, aMsg.data);
				onDNSResolveStart(dnsStartInfo, aMsg.getRealTime());
				break;
			case HTTPVS_DNSRESOLVE_SUCCESS:
				dnssucc_info dnsSuccInfo = new dnssucc_info();
				this.datapack(dnsSuccInfo, aMsg.data);
				onDNSResolveSuccess(dnsSuccInfo, aMsg.getRealTime());
				break;
			case HTTPVS_DNSRESOLVE_FAILED:
				dnsfail_info dnsFailInfo = new dnsfail_info();
				this.datapack(dnsFailInfo, aMsg.data);
				onDNSResolveFailed(dnsFailInfo, aMsg.getRealTime());
				break;
			case HTTPVS_CONNECT_START:
				onConnectStart(aMsg.getRealTime());
				break;
			case HTTPVS_CONNECT_SUCCESS:
				onConnectSuccess(aMsg.getRealTime());
				break;
			case HTTPVS_CONNECT_FAILED:
				onConnectFailed(aMsg.getRealTime());
				break;
			case HTTPVS_SEND_GET:
				send_get_info sendGetInfo = new send_get_info();
				this.datapack(sendGetInfo, aMsg.data);
				onSendGet(sendGetInfo, aMsg.getRealTime());
				break;
			case HTTPVS_FIRSTDATA_ARRIVED:
				onFirstDataArrived(aMsg.getRealTime());
				break;
			case HTTPVS_REQUEST_FAILED:
				FailedInfo failedInfo = new FailedInfo();
				this.datapack(failedInfo, aMsg.data);
				onRequestFailed(failedInfo, aMsg.getRealTime());
				break;
			case HTTPVS_REPRODUCTION_START:
				ReproductionStartInfo startInfo = new ReproductionStartInfo();
				this.datapack(startInfo, aMsg.data);
				onReproductionStart(startInfo, aMsg.getRealTime());
				break;
			case HTTPVS_REPRODUCTION_START_FAILED:
				ReproductionStartFailedInfo startFailInfo = new ReproductionStartFailedInfo();
				this.datapack(startFailInfo, aMsg.data);
				onReproductionStartFailed(startFailInfo, aMsg.getRealTime());
				break;
			case HTTPVS_MSG:
				MsgInfo msgInfo = new MsgInfo();
				this.datapack(msgInfo, aMsg.data);
				onMsg(msgInfo, aMsg.getRealTime());
				break;
			case HTTPVS_RECV_FINISH:
				RecvFinishInfo recvFinishInfo = new RecvFinishInfo();
				this.datapack(recvFinishInfo, aMsg.data);
				onRecvFinish(recvFinishInfo, aMsg.getRealTime());
				break;
			case HTTPVS_RECV_DROP:
				RecvDropInfo recvDropInfo = new RecvDropInfo();
				this.datapack(recvDropInfo, aMsg.data);
				onRecvDrop(recvDropInfo, aMsg.getRealTime());
				break;
			case HTTPVS_REBUFFERING_START:
				onReBufferingStart(aMsg.getRealTime());
				break;
			case HTTPVS_REBUFFERING_END:
				onReBufferingEnd(aMsg.getRealTime());
				break;
			case HTTPVS_PLAY_FINISH:
				onPlayFinish(aMsg.getRealTime());
				break;
			case HTTPVS_QUIT:
				onQuit();
				break;
			}
		}

		/**
		 * 把字符串中的数据映射到对象属性中去
		 * 
		 * @param obj
		 *          对象
		 * @param data
		 *          数据
		 */
		private void datapack(Object obj, String data) {
			LogUtil.d(TAG, "-----datapack-----data:" + data);
			HashMap<String, String> hashMap = splitResultByLit(data);

			if(null!=hashMap&&hashMap.containsKey("JitterAvg")){//平均抖动
				try {
					JitterAvg = Integer.parseInt(hashMap.get("JitterAvg"));
				}catch(Exception ex){
					JitterAvg=0;
					LogUtil.w(TAG,ex.getMessage());
				}
			}
			if(null!=hashMap&&hashMap.containsKey("CurJitter")){//平均抖动
				try {
					CurJitter = Integer.parseInt(hashMap.get("CurJitter"));
				}catch(Exception ex){
					CurJitter=0;
					LogUtil.w(TAG,ex.getMessage());
				}
			}
			for (Field field : obj.getClass().getDeclaredFields()) {
				String type = field.getType().getSimpleName();
				try {
					if (type.equalsIgnoreCase("String")) {
						field.set(obj, this.getStringValue(hashMap, field.getName(), ""));
					} else if (type.equalsIgnoreCase("int")) {
						field.setInt(obj, this.getIntValue(hashMap, field.getName(), 0));
					} else if (type.equalsIgnoreCase("Float")) {
						field.setFloat(obj, this.getFloatValue(hashMap, field.getName(), 0));
					} else if (type.equalsIgnoreCase("Double")) {
						field.setDouble(obj, this.getDoubleValue(hashMap, field.getName(), 0));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 解析业务库返回的节点信息,组织成HashMap<String,String>的格式返回
		 * 
		 * 业务库返回的节点信息格式如下: key1::value1\nkey2::value2\nkey3::value3...
		 * 
		 * @param aData
		 * @return 注意获得解析结果使用后清除
		 */
		private HashMap<String, String> splitResultByLit(String aData) {
			HashMap<String, String> keyValues = new HashMap<String, String>();
			String[] keyList = aData.split("\n");
			for (String key : keyList) {
				String[] keyValue = key.split("::");
				if (keyValue.length == 2)
					keyValues.put(keyValue[0], keyValue[1]);
				else
					keyValues.put(keyValue[0], "");
			}
			return keyValues;
		}

		/**
		 * 获得传入HashMap表的键值,如果该值不存在返回默认值
		 * 
		 * @param hashMap
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private int getIntValue(HashMap<String, String> hashMap, String key, int defaultValue) {
			if (hashMap.containsKey(key)) {
				return Integer.parseInt(hashMap.get(key));
			}
			return defaultValue;
		}

		/**
		 * 获得传入HashMap表的键值,如果该值不存在返回默认值
		 * 
		 * @param hashMap
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private String getStringValue(HashMap<String, String> hashMap, String key, String defaultValue) {
			if (hashMap.containsKey(key)) {
				return hashMap.get(key);
			}
			return defaultValue;
		}

		/**
		 * 获得传入HashMap表的键值,如果该值不存在返回默认值
		 * 
		 * @param hashMap
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private float getFloatValue(HashMap<String, String> hashMap, String key, float defaultValue) {
			if (hashMap.containsKey(key)) {
				return Float.parseFloat(hashMap.get(key));
			}
			return defaultValue;
		}

		/**
		 * 获得传入HashMap表的键值,如果该值不存在返回默认值
		 * 
		 * @param hashMap
		 * @param key
		 * @param defaultValue
		 * @return
		 */
		private double getDoubleValue(HashMap<String, String> hashMap, String key, double defaultValue) {
			if (hashMap.containsKey(key)) {
				return Double.parseDouble(hashMap.get(key));
			}
			return defaultValue;
		}

	};

	/**
	 * 请求失败
	 * 
	 * @param data
	 *          数据
	 * @param realTime
	 *          事件时间
	 */
	private void onRequestFailed(FailedInfo data, long realTime) {
		LogUtil.w(TAG, "HTTPVS_REQUEST_FAILED");
		// LogUtil.w(TAG, aMsg.data + "-->");
		int delay = (int) (realTime - playStartTime) / 1000;
		analyseData(data, HTTPVS_REQUEST_FAILED);
		String strQos = String.format(DataTaskEvent.VIDEO_PLAY_REQUEST_FAILURE.toString(), delay,
				FailReason.getFailReason(ItemValues.reason).getResonStr());
		displayEvent(strQos);
		EventBytes.Builder(getBaseContext(), RcuEventCommand.VIDEOPLAY_REQUEST_FAILURE).addInteger(ItemValues.reason)
				.writeToRcu(realTime);
		sendMsgToPioneer(",bSuccess=0");
	}

	public void onDestroy() {
		LogUtil.v(TAG, "--onDestroy--");
		super.onDestroy();
		// com.walktour.service.test.VideoPlay
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.test.VideoPlay", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("DMPlayerAndroid").getAbsolutePath(), false);

	};

	@Override
	protected void stopTest() {
		try {
			aIpc2Jni.send_command(HTTPVS_TEST, HTTPVS_STOP_TEST, "", "".length());
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String onInited(long realTime) {
		String eventData = super.onInited(realTime);
		aIpc2Jni.send_command(HTTPVS_TEST, HTTPVS_START_TEST, eventData, eventData.length());
		return eventData;
	}
}

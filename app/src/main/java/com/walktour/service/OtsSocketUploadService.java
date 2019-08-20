package com.walktour.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.dinglicom.DataSetLib;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.PageQueryParas;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.HttpServer;
import com.walktour.Utils.HttpServer.ConfigInfo;
import com.walktour.Utils.HttpServer.UpLoadStrI;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.model.NetStateModel;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OTS实时上报参数
 * @author zhihui.lian
 */
public class OtsSocketUploadService extends Service implements UpLoadStrI {
	private final String TAG = "OtsHttpUpload";
	private MyBroadcastReceiver mEventReceiver;
	private MyPhoneState myPhoneState;
	GpsInfo gpsInfo = null;
	private APNOperate apnOperate;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//	private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
	private HttpServer httpInstance;
	private ConfigInfo configInfo;
	private boolean stopSendParm = false;
	
	private SendL3msgThread l3Thread;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.w(TAG, "--onCreate--");
		httpInstance = HttpServer.getInstance(getApplicationContext());
		httpInstance.setUpLoadStrI(this);
		configInfo = httpInstance.getConfigInfo();
		if (configInfo == null) {
			configInfo = httpInstance.getConfigInfoIn();
		}
		createClientSocket();
		createL3Socket();
		createCellSocket();
		gpsInfo = gpsInfo.getInstance();
		regedit();
		myPhoneState = MyPhoneState.getInstance();
		apnOperate = APNOperate.getInstance(getApplicationContext());
		scheduler.scheduleAtFixedRate(new TodoOtsSocketUpload(), 0,configInfo.getInerval(), TimeUnit.MILLISECONDS);
		l3Thread = new SendL3msgThread();
		l3Thread.start();
	}

	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.traceL3MsgChanged);
		filter.addAction(GpsInfo.gpsLocationChanged);
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.w(TAG, "--onDestroy--");
		if (mEventReceiver != null) {
			unregisterReceiver(mEventReceiver);
		}
	}

	private int intervalDelay = 1000;
	private Socket senderSocket;
	private Socket l3Socket;
	private Socket cellSocket;
	private int currentNetType;
	private OutputStream ops;
	private OutputStream l3ops;
	private OutputStream cellOps;

	/**
	 * @author zhihui.lian
	 */

	// 为空的时候重新建立Socket
	private void createClientSocket() {
		try {
			senderSocket = new Socket("localhost", configInfo.getPARM_PORT());
			senderSocket.setSoTimeout(30 * 1000);
			Log.e("--parm--", "socket create" + senderSocket.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 创建信令Socket
	private void createL3Socket() {
		try {
			l3Socket = new Socket("localhost", configInfo.getL3_PORT());
			l3Socket.setSoTimeout(30 * 1000);
			Log.e("--L3---", "socket create" + l3Socket.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 创建参数核查Socket
	private void createCellSocket() {
		try {
			cellSocket = new Socket("localhost", configInfo.getCELL_PORT());
			cellSocket.setSoTimeout(30 * 1000);
			Log.e("--Cell---", "cell socket create" + cellSocket.isConnected());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final int NO_DATA_TIMEOUT = 300 * 1000;

	/***
	 * 发送命令,msg为要发送的命令字符串
	 * **/
	public synchronized void SendL3CommandMsg(String msg) {

		try {
			if (l3Socket == null) {
				createL3Socket();
			}
//			Log.i("-----", "sendL3" + msg);
			l3ops = l3Socket.getOutputStream();

			if (l3ops != null) {
				l3ops.write(intToByteArray1(msg.length()));
				l3ops.write(msg.getBytes());
				l3ops.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 发送命令,msg为要发送的命令字符串
	 * **/
	private void sendCommandMsg(Socket parmSocket, String msg) {
		try {
			if (parmSocket == null) {
				createClientSocket();
			}
			Log.i("-----", "sendParm" + msg);
			ops = parmSocket.getOutputStream();

			if (ops != null) {
				ops.write(intToByteArray1(msg.length()));
				ops.write(msg.getBytes());
				ops.flush();
			}
			msg=null;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * 发送参数核查数据给OTS
	 * **/
	private void sendCellCommandMsg(Socket cellSocket, String msg) {
		try {
			if (cellSocket == null) {
				createCellSocket();
			}
			Log.i("-----", "===sendCell" + msg);
			cellOps = cellSocket.getOutputStream();
			Log.i("-----", "===sendCell" + " "  + cellSocket.isConnected());
			if (cellOps != null) {
				cellOps.write(intToByteArray1(msg.length()));
				cellOps.write(msg.getBytes());
				cellOps.flush();
			}
			msg=null;
			Log.i("-----", "===sendCell" + "  finished");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * int类型转byte数组
	 * 
	 * @param i
	 * @return
	 */
	public byte[] intToByteArray1(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	/**
	 * 传输socket线程
	 * 
	 * @author Administrator
	 * 
	 */

	class TodoOtsSocketUpload implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LogUtil.w(TAG, "--TodoOtsHttpUpload Start--" + intervalDelay);
			while(!stopSendParm){
				try {
					getCurrentNetType();
					sendCommandMsg(senderSocket, getNetStr());
					sendCommandMsg(senderSocket, getDevInfoStr());
					sendCommandMsg(senderSocket, sendCommandMsgStr());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	 public Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:
				
				l3Thread.sendL3str(msg.obj.toString());
//				if(!mExecutorService.isShutdown() && !mExecutorService.isTerminated()){
//					String msgStr = "";
//					msgStr = msg.obj.toString();
//					mExecutorService.execute(new SendL3Thead(msgStr));
//				}
				break;

			default:
				break;
			}
		}
	 };
	
	

	/**
	 * 拼网络字符串
	 * 
	 * @return
	 */
	private String getNetStr() {
		String netStr = "";
		switch (currentNetType) {
		case UnifyParaID.NET_TDSCDMA:
			netStr = "NetWorkMode\t0\t1\t0";
			break;
		case UnifyParaID.NET_GSM:
			netStr = "NetWorkMode\t1\t0\t0";
			break;
		case UnifyParaID.NET_LTE:
			netStr = "NetWorkMode\t0\t0\t1";
			break;
		default:
			netStr = "NetWorkMode\t0\t0\t0";
			break;
		}
		return netStr;
	}

	/**
	 * 获取网络类型
	 */
	private void getCurrentNetType() {
		try {
			CurrentNetState currentNet = NetStateModel.getInstance().getCurrentNetType();
			currentNetType = currentNet.getCurrentNetId();
			switch (currentNetType) {
			case UnifyParaID.NET_TDSCDMA:
				currentNetType = UnifyParaID.NET_TDSCDMA;
				break;
			case UnifyParaID.NET_GSM:
				currentNetType = UnifyParaID.NET_GSM;
				break;
			case UnifyParaID.NET_LTE:
				currentNetType = UnifyParaID.NET_LTE;
				break;
			default:
				currentNetType = UnifyParaID.NET_UNKONWN;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			currentNetType = UnifyParaID.NET_UNKONWN;
		}

	}

	/**
	 * 根据网络类型，上报参数 只上报TD、Lte、Gsm
	 */
	private String sendCommandMsgStr() {

		String msgStr = "";
		try {
			switch (currentNetType) {
			case UnifyParaID.NET_TDSCDMA:
				/*
				 * 1)下行测量参数列表(Downlink Measurements) PCCPCH_RSCP PCCPCH_ISCP
				 * UE_Txpower UTRAN_CarrierRSSI TS_ISCP TS_DPCH_RSCP
				 * 
				 * 2)邻区信息列表(Neighbours Information) NCell_PCCPCH_RSCP
				 * NCell_Timeslot_ISCP NCELL_UARFCN NCELL_CPI 4)服务小区参数列表(Serving
				 * Cell Parameters) ServerCellName ServLAC CPI ServUARFCN
				 * 时隙上下行指示（Uplink，Downlink）
				 */
				DatasetManager.getInstance(getApplicationContext()).queryParamsSync(PageQueryParas.OtsParamTdScdma);
				msgStr = "TDSParam\t"
						+ getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP) + "\t"
						+ getParaValue(UnifyParaID.TD_Ser_PCCPCHISCP) + "\t"
						+ getParaValue(UnifyParaID.TD_Ser_UETxPower) + "\t"
						+ getParaValue(UnifyParaID.TD_Ser_CarrierRSSI) + "\t"
						+ getParaValue(UnifyParaID.TD_Ser_DPCHISCP) + "\t"
						+ getParaValue(UnifyParaID.TD_Ser_DPCHRSCP) + "\t"
						+ getParaValue(UnifyParaID.T_NCell_N1_RSCP) + " "
						+ getParaValue(UnifyParaID.T_NCell_N2_RSCP) + " "
						+ getParaValue(UnifyParaID.T_NCell_N3_RSCP) + " "
						+ getParaValue(UnifyParaID.T_NCell_N4_RSCP) + " "
						+ getParaValue(UnifyParaID.T_NCell_N5_RSCP) + " "
						+ getParaValue(UnifyParaID.T_NCell_N6_RSCP) + "\t"
						+ "NA" + " " + "NA" + " " + "NA" + " " + "NA" + " "
						+ "NA" + " " + "NA" + "\t"
						+ getParaValue(UnifyParaID.T_NCell_N1_UARFCN) + " "
						+ getParaValue(UnifyParaID.T_NCell_N2_UARFCN) + " "
						+ getParaValue(UnifyParaID.T_NCell_N3_UARFCN) + " "
						+ getParaValue(UnifyParaID.T_NCell_N4_UARFCN) + " "
						+ getParaValue(UnifyParaID.T_NCell_N5_UARFCN) + " "
						+ getParaValue(UnifyParaID.T_NCell_N6_UARFCN) + "\t"
						+ getParaValue(UnifyParaID.T_NCell_N1_CPI) + " "
						+ getParaValue(UnifyParaID.T_NCell_N2_CPI) + " "
						+ getParaValue(UnifyParaID.T_NCell_N3_CPI) + " "
						+ getParaValue(UnifyParaID.T_NCell_N4_CPI) + " "
						+ getParaValue(UnifyParaID.T_NCell_N5_CPI) + " "
						+ getParaValue(UnifyParaID.T_NCell_N6_CPI) + "\t"
						+ "NA" + "\t" + getParaValue(UnifyParaID.TD_Ser_LAC)
						+ "\t" + getParaValue(UnifyParaID.TD_Ser_CPI) + "\t"
						+ getParaValue(UnifyParaID.TD_Ser_UARFCN) + "\t" + "NA";

				break;

			case UnifyParaID.NET_LTE:
				/**
				 * RSRP(dBm) CRS SINR(dB) XX 临时增加两个参数，先用字符串代替 XX PUSCH
				 * Power(dBm) PHY Code0 Throughput PHY Code1 Throughput PHY UL
				 * Throughput(kbit/s) PDCP DL Throughput(kbit/s) PDCP UL
				 * Throughput(kbit/s） MIMO MIMO mode RANK1 SINR(dB) RANK2
				 * SINR1(dB) RANK2 SINR2(dB) 服务小区信息 FreqInfo BandWidth(M) PCI
				 * SubFrameAssignmentType SpecialSubFramePatterns 邻区信息 LTE制式邻区
				 * Neighbor Cell PCI Neighbor Cell EARFCN DL Neighbor Cell RSRP
				 * RB信息 UL RB Num DL RB Num TAC eNodeB ID
				 */
				DatasetManager.getInstance(getApplicationContext()).queryParamsSync(PageQueryParas.OtsParamLte);
				DatasetManager.getInstance(getApplicationContext()).queryCellSet(DataSetLib.EnumNetType.LTE);
				msgStr = "TDDLTEParam\t" + getParaValue(UnifyParaID.L_SRV_RSRP)
						+ "\t" + getParaValue(UnifyParaID.L_SYS_Rx0RSRP) + "\t"
						+ getParaValue(UnifyParaID.L_SYS_Rx1RSRP) + "\t"
						+ getParaValue(UnifyParaID.L_SRV_CRS_SINR) + "\t"
						+ getParaValue(UnifyParaID.L_CH1_PUSCH_TxPower) + "\t"
						+ getParaValue(UnifyParaID.L_Thr_DL_PhyThrCode0) + "\t"
						+ getParaValue(UnifyParaID.L_Thr_DL_PhyThrCode1) + "\t"
						+ getParaValue(UnifyParaID.L_Thr_UL_Phy_Thr) + "\t"
						+ getParaValue(UnifyParaID.L_Thr_DL_PDCP_Thr) + "\t"
						+ getParaValue(UnifyParaID.L_Thr_UL_PDCP_Thr) + "\t"
						+ getParaValue(UnifyParaID.L_MIMO_Mode) + "\t"
						+ getParaValue(UnifyParaID.L_RANK1_SINR) + "\t"
						+ getParaValue(UnifyParaID.L_RANK2_SINR1) + "\t"
						+ getParaValue(UnifyParaID.L_RANK2_SINR2) + "\t"
						+ getParaValue(UnifyParaID.L_FreqInfo) + "\t"
						+ getParaValue(UnifyParaID.LTECA_UL_BandWidth) + "\t"
						+ getParaValue(UnifyParaID.L_SRV_PCI) + "\t"
						+ getParaValue(UnifyParaID.L_SubFrame_AssignmentType)
						+ "\t"
						+ getParaValue(UnifyParaID.L_Special_SubFramePatterns)
						+ "\t" + getLteCellSetStr(UnifyParaID.LTE_CELL_LIST)
						+ "\t" + getParaValue(UnifyParaID.L_UL_RB_Num) + "\t"
						+ getParaValue(UnifyParaID.L_DL_RB_Num) + "\t"
						+ getParaValue(UnifyParaID.L_SRV_TAC) + "\t"
						+ getParaValue(UnifyParaID.L_eNodeB_ID);

				break;

			case UnifyParaID.NET_GSM:

				/*
				 * 按照此顺序拼字符串 BCCH Level BSIC RxLev Full TA MS TxPower LAC CELL
				 * ID 3）邻小区测量信息 Neighbor BCCH Neighbor BSIC Neighbor RxLev CELL
				 * ID
				 */
				DatasetManager.getInstance(getApplicationContext()).queryParamsSync(PageQueryParas.OtsParamGsm);
				msgStr = "GSMParam\t" + getParaValue(UnifyParaID.G_Ser_BCCH)
						+ "\t" + getParaValue(UnifyParaID.G_Ser_BSIC) + "\t"
						+ getParaValue(UnifyParaID.G_Ser_RxLevFull) + "\t"
//						+ getParaValue(UnifyParaID.G_Ser_TCH_C2I) + "\t"
						+ getParaValue(UnifyParaID.G_Ser_TA) + "\t"
						+ getParaValue(UnifyParaID.G_Ser_TxPower) + "\t"
						+ getParaValue(UnifyParaID.G_Ser_LAC) + "\t"
						+ getParaValue(UnifyParaID.G_Ser_Cell_ID) + "\t"
						+ getParaValue(UnifyParaID.G_NCell_N1_BCCH) + " "
						+ getParaValue(UnifyParaID.G_NCell_N2_BCCH) + " "
						+ getParaValue(UnifyParaID.G_NCell_N3_BCCH) + " "
						+ getParaValue(UnifyParaID.G_NCell_N4_BCCH) + " "
						+ getParaValue(UnifyParaID.G_NCell_N5_BCCH) + " "
						+ getParaValue(UnifyParaID.G_NCell_N6_BCCH) + "\t"
						+ getParaValue(UnifyParaID.G_NCell_N1_BSIC) + " "
						+ getParaValue(UnifyParaID.G_NCell_N2_BSIC) + " "
						+ getParaValue(UnifyParaID.G_NCell_N3_BSIC) + " "
						+ getParaValue(UnifyParaID.G_NCell_N4_BSIC) + " "
						+ getParaValue(UnifyParaID.G_NCell_N5_BSIC) + " "
						+ getParaValue(UnifyParaID.G_NCell_N6_BSIC) + "\t"
						+ getParaValue(UnifyParaID.G_NCell_N1_RxLevel) + " "
						+ getParaValue(UnifyParaID.G_NCell_N2_RxLevel) + " "
						+ getParaValue(UnifyParaID.G_NCell_N3_RxLevel) + " "
						+ getParaValue(UnifyParaID.G_NCell_N4_RxLevel) + " "
						+ getParaValue(UnifyParaID.G_NCell_N5_RxLevel) + " "
						+ getParaValue(UnifyParaID.G_NCell_N6_RxLevel) + "\t"
						+ "NA" + " " + "NA" + " " + "NA" + " " + "NA" + " "
						+ "NA" + " " + "NA";

				break;

			case UnifyParaID.NET_UNKONWN:

				break;
			default:

				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return msgStr;
	}

	/**
	 * 获得lte邻区Str,组装成服务端需要的规则
	 * 
	 * @param paraId
	 * @return
	 */
	private String getLteCellSetStr(int paraId) {
		String nullValue = "NA NA NA NA NA NA" + "\t" + "NA NA NA NA NA NA"
				+ "\t" + "NA NA NA NA NA NA";
		String paraValue = TraceInfoInterface.getParaValue(paraId);
		String[] paraValueSpit = paraValue.split(";");
		if (paraValueSpit.length > 6) {
			paraValueSpit = paraValue.substring(0,
					getCharacterPosition(paraValue)).split(";"); // 限制传输的邻区个数最大为6个
		}
		String pciStr = "";
		String EarfcnStr = "";
		String RsrqStr = "";
		try {
			int delStrNum = 0;
			for (int i = 0; i < paraValueSpit.length; i++) {
				String testS = paraValueSpit[i];
				if (!testS.equals("")) {
					String[] testDetail = testS.split(",");
					if (testDetail.length < 3 || testDetail[0]
							.equals(getParaValue(UnifyParaID.L_SRV_EARFCN)) // 屏蔽主服务区
							&& testDetail[1]
									.equals(getParaValue(UnifyParaID.L_SRV_PCI))) {
						delStrNum++;
						continue;
					}
					pciStr += testDetail[1]
							+ (paraValueSpit.length - 1 != i ? " " : "");
					EarfcnStr += testDetail[0]
							+ (paraValueSpit.length - 1 != i ? " " : "");
					RsrqStr += testDetail[2]
							+ (paraValueSpit.length - 1 != i ? " " : "");
				}
			}
			if (paraValue.length() != 0) {
				if (paraValueSpit.length != 6) {
					for (int i = 0; i < 7 - (paraValueSpit.length - delStrNum); i++) {
						pciStr = pciStr + " NA";
						EarfcnStr = EarfcnStr + " NA";
						RsrqStr = RsrqStr + " NA";
					}
				}
			} else {
				return nullValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return nullValue;
		}
//
//		System.out.println("LTE-----" + pciStr + "\\t" + EarfcnStr + "\\t"
//				+ RsrqStr);
		return pciStr + "\t" + EarfcnStr + "\t" + RsrqStr;
	}

	/**
	 * 获取指定符号出现的位置
	 * 
	 * @param string
	 * @return
	 */
	public int getCharacterPosition(String string) {
		Matcher slashMatcher = Pattern.compile(";").matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			if (mIdx == 6) {
				break;
			}
		}
		return slashMatcher.start();
	}

	/** 获得得参数队列中指定ID的值 */
	private String getParaValue(int paraId) {
		String paraValue = TraceInfoInterface.getParaValue(paraId);
		if (paraValue.trim().toString().equals("")) {
			return "NA";
		}
		return paraValue;
	}

	/** 拼设备信息串，用\t分隔 **/

	private String getDevInfoStr() {
		StringBuffer sb = new StringBuffer();
		sb.append("DEVInfo" + "\t");
		try {
			sb.append(android.os.Build.MODEL + "\t");
			sb.append(myPhoneState.getIMSI(getApplicationContext()) + "\t");
			sb.append(myPhoneState.getIMEI(getApplicationContext()) + "\t");
			sb.append(myPhoneState.getLocalMacAddress(getApplicationContext())
					+ "\t");
			sb.append("COM2" + "\t");
			sb.append(myPhoneState.getAndroidVersion() + "\t");
			sb.append("0" + "\t");
			sb.append("NA" + "\t");
			sb.append(myPhoneState.getLocalIpv4Address() + "\t");
			sb.append(myPhoneState.getLocalIpv4Address() + "\t");
			sb.append(myPhoneState.getBaseBandVersion() + "\t");
			sb.append("0x01" + "\t");
			sb.append(UtilsMethod.getCurrentVersionName(getApplicationContext()));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return sb.toString();

	}

	private String getL3HeadStr() {
		switch (currentNetType) {
		case UnifyParaID.NET_TDSCDMA:
			return "TDSL3";
		case UnifyParaID.NET_GSM:
			return "GSML3";
		case UnifyParaID.NET_LTE:
			return "TDDLTEL3";
		default:
			break;
		}
		return "GSML3";
	}

	
	
	public class SendL3msgThread extends Thread {
		private ArrayList<String> l3strQueue = new ArrayList<String>();
		private boolean stopThread = false;

		public SendL3msgThread() {
		}

		public void sendL3str(String l3sStr) {
			if(l3strQueue == null){
				return;
			}
			synchronized (l3strQueue) {
				l3strQueue.add(l3sStr);
				l3strQueue.notify();
			}
		}
		
		@Override
		public void run() {
			while (!stopThread) {
				try {
					synchronized (l3strQueue) {
						if(l3strQueue.size() <= 0){
							l3strQueue.wait();
						}else{
							String l3Msg = l3strQueue.get(0);
							l3strQueue.remove(0);
							SendL3CommandMsg(l3Msg);
						}
					}
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
			
		public void stopThead() {
			stopThread = true;
			if(l3strQueue!=null){
				l3strQueue.clear();
			}
			l3strQueue = null;
			try {
				this.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	class SendL3Thead implements Runnable{
		
		private String l3MsgStr = "";
	
		public SendL3Thead(String l3MsgStr){
			this.l3MsgStr = l3MsgStr;
		}

		@Override
		public void run() {
			SendL3CommandMsg(l3MsgStr);
		}
		
	}
	
	

	private boolean sendGpsFlag = false;
	
	/**
	 * 广播接收器:接收来自广播更新,实时传输层三信令
	 * */
	private long endTime = 0;
	
	private class MyBroadcastReceiver extends BroadcastReceiver {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			// LogUtil.w(tag,"--action:"+intent.getAction());
			if (intent.getAction().equals(WalkMessage.traceL3MsgChanged)) {
				if (intent.getExtras() != null) {
						long startTime = System.currentTimeMillis();
						if( ((startTime - endTime) > 50)){
							String lsMsgStr = "";
							lsMsgStr = intent.getExtras().getString("L3Str", "");
							//Log.i(TAG, "-------l3" + lsMsgStr);
							String[] l3ModelStr = lsMsgStr.split(",");
							// 时间戳\tMessage Name\tDir\tChannelType\tMessage Detail
							l3MsgSend(l3ModelStr);
							endTime = System.currentTimeMillis();
						}
				}
			}
			if (intent.getAction().equals(GpsInfo.gpsLocationChanged)
					&& gpsInfo.getLocation() != null) {
				Location location = gpsInfo.getLocation();
				String gpsStr = "GPSParam"
						+ "\t"
						+ String.valueOf(location.getLongitude())
						+ "\t"
						+ String.valueOf(location.getLatitude())
						+ "\t"
						+ UtilsMethod.decFormat
								.format(location.getSpeed() * 3.6)
						+ "\t"
						+ UtilsMethod.decFormat.format(location.getAltitude())
						+ "\t"
						+ UtilsMethod.decFormat
								.format(location.getSpeed() * 3.6);
				Log.i(TAG, "------gps speed" + UtilsMethod.decFormat
								.format(location.getSpeed() * 3.6));
				if( !sendGpsFlag){
					sendCommandMsg(senderSocket, gpsStr);
				}
			}
		}

	}
	
	
	private synchronized  void l3MsgSend(String[] l3ModelStr) {
		try {
			String l3Str = getL3HeadStr()
					+ "\t"
					+ System.currentTimeMillis()
					+ "\t"
					+ l3ModelStr[1].substring(l3ModelStr[1].indexOf(" ") + 1)
					+ "\t"
					+ (l3ModelStr[2].equals("0") ? 1 : 0)
					+ "\t"
					+ getChannelType(Integer.parseInt(l3ModelStr[4])) + "\t"
					+ getL3Detail(Integer.valueOf(l3ModelStr[3]));
			Message msg = mHandler.obtainMessage();
			msg.what = 0x01;
			msg.obj = l3Str;
			mHandler.sendMessage(msg);
			msg=null;
			l3Str=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据code判断ChannelType
	 */
	private String getChannelType(int code) {
		if (code2Int("413F0001") <= code && code2Int("413F0003") >= code) {
			return "RRC Signalling(UL_CCCH)";
		} else if (code2Int("413F0101") <= code && code2Int("413F011F") >= code) {
			return "RRC Signaling(UL_DCCH)";
		} else if (code2Int("413F0201") <= code && code2Int("413F0206") >= code) {
			return "RRC Signaling(DL_CCCH)";
		} else if (code2Int("413F0301") <= code && code2Int("413F031D") >= code) {
			return "RRC Signaling(DL_DCCH)";
		} else if (code2Int("413F0301") <= code && code2Int("413F031D") >= code) {
			return "RRC Signaling(DL_DCCH)";
		} else if (code2Int("413F0401") <= code && code2Int("413F0421") >= code) {
			return "RRC Signaling(BCCH_BCH)";
		} else if (code2Int("413F0501") <= code && code2Int("413F0502") >= code) {
			return "RRC Signaling(BCCH_FACH)";
		} else if (code2Int("413F0601") == code) {
			return "RRC Signaling(DL_PCCH)";
		} else if (code2Int("413F0701") == code) {
			return "RRC Signaling(UL_SHCCH)";
		} else if (code2Int("413F0801") == code) {
			return "RRC Signaling(DL_SHCCH)";
		} else if (code2Int("30000100") == code) {
			return "BCCH BCH Messages(BCCH-BCH)";
		} else if (code2Int("30000200") <= code && code2Int("3000020f") >= code) {
			return "UL DCCH Messages(DCCH)";
		} else if (code2Int("30000500") <= code && code2Int("3000050c") >= code) {
			return "BCCH DL SCH Messages(BCCH-SCH)";
		} else if (code2Int("30000400") == code) {
			return "PCCH Messages(PCCH)";
		} else if (code2Int("30000300") <= code && code2Int("30000303") >= code) {
			return "DL CCCH Messages(CCCH)";
		} else if (code2Int("30000600") <= code && code2Int("3000060f") >= code) {
			return "DL DCCH Messages(DCCH)";
		} else if (code2Int("30000700") <= code && code2Int("30000701") >= code) {
			return "UL CCCH Messages(CCCH)";
		} else if (code2Int("40000741") <= code && code2Int("400002e8") >= code) {
			return "EMMMessages(LTE NAS)";
		} else if (code2Int("713A0501") <= code && code2Int("713A0A55") >= code) {
			return "MMMsgs(3G NAS)";
		}
		return "NA";

	}

	/**
	 * code字符串类型转为int类型
	 * 
	 * @param codeStr
	 * @return
	 */
	private int code2Int(String codeStr) {
		long code = 0;
		try {
			code = Long.valueOf(codeStr, 16);
		} catch (Exception e) {
			return 0;
		}
		return (int) code;
	}

	/**
	 * 查询层三详细解码
	 * 
	 * @param pointIndex
	 * @return
	 */
	private  String getL3Detail(int pointIndex) {
		try {
			if (pointIndex != -1) {
				String l3DetailStr = DatasetManager.getInstance(
						getApplicationContext()).queryL3Detail(pointIndex);
				Log.i(TAG, "pointIndex" + pointIndex);
				if (l3DetailStr != null && !l3DetailStr.trim().equals("")) {
					return l3DetailStr;
				}
				return "The message can't  be decoded";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "NA";
	}

	@Override
	public void upLoad(String cellStr) {
		if (cellStr.length() != 0) {
			sendCellCommandMsg(cellSocket, cellStr);
		}
	}

	@Override
	public void stopUpload() {
		try {
			if (senderSocket != null) {
				senderSocket.close();
				senderSocket = null;
			}
			if (cellSocket != null) {
				cellSocket.close();
				cellSocket = null;
			}
			if (l3Socket != null) {
				l3Socket.close();
				l3Socket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopRealtimeParm() {
		stopSendParm = true;
		sendGpsFlag = true;
		scheduler.shutdown();
//		mExecutorService.shutdown();
		l3Thread.stopThead();
		
	}

}

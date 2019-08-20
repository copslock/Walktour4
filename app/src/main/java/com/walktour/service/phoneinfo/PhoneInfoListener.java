package com.walktour.service.phoneinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.CellIdentityLte;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.model.NetStateModel;
import com.walktour.service.phoneinfo.logcat.CallFailCause;
import com.walktour.service.phoneinfo.logcat.EventCode;
import com.walktour.service.phoneinfo.logcat.EventListener;
import com.walktour.service.phoneinfo.logcat.RadioLogMonitor;
import com.walktour.service.phoneinfo.model.LTESignalStrength;
import com.walktour.service.phoneinfo.utils.MobileUtil;
import com.walktour.service.phoneinfo.utils.ReflectionUtils;

/**
 * 采集收集信息监听器
 *
 * @author jianchao.wang
 *
 */
public class PhoneInfoListener extends PhoneStateListener {
	/** 日志标识 */
	private static final String TAG = "PhoneInfoListener";
	/** 上下文 */
	private Context mContext;
	/** 电话管理器 */
	private TelephonyManager mTeleManager;
	/** 设备信息 */
	private MobileInfo mInfo;
	/** 上一次的呼叫状态 */
	private CallState mLastCallState = CallState.Idle;
	/** 当前的呼叫状态 */
	private CallState mCallState = CallState.Idle;
	/** 呼叫结束执行句柄时延 */
	private static final int CALL_END_HANDLER_DELAY = 5000;
	/** 当前呼叫顺序 */
	private CallDirection mCallDirection = CallDirection.None;
	/** 日志监听类 */
	private RadioLogMonitor radioLogMonitor;
	/** 呼叫事件编码 */
	private volatile int callEventCode;
	/** 呼叫结束原因 */
	private volatile String callEndCause;

	/** 呼叫状态 */
	private enum CallState {
		Idle, Ringing, InProgress
	}

	/** 呼叫方向 */
	public enum CallDirection {
		None, MT, MO
	}

	public PhoneInfoListener(Context context, TelephonyManager telephonyManager) {
		this.mContext = context;
		this.mTeleManager = telephonyManager;
		this.mInfo = new MobileInfo();
		this.callEventCode = -1;
		this.callEndCause = "";
		this.radioLogMonitor = new RadioLogMonitor(context, logcatEventListener);
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		LogUtil.d(TAG, "----onCallStateChanged----state:" + state + "---incomingNumber:" + incomingNumber);
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			this.mCallState = CallState.Idle;
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			this.mCallState = CallState.InProgress;
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			this.mCallState = CallState.Ringing;
			break;
		}
		if (incomingNumber == null) {
			incomingNumber = "";
		}
		this.createCallEvents(this.mCallState, this.mLastCallState, incomingNumber);
		this.mLastCallState = this.mCallState;
	}

	/**
	 * 生成语音事件
	 *
	 * @param currState
	 *          当前呼叫状态
	 * @param prevState
	 *          上一次呼叫状态
	 * @param incomingNumber
	 *          呼叫号码
	 */
	private void createCallEvents(CallState currState, CallState prevState, String incomingNumber) {

		// when on call context monitor the log
		if (currState != CallState.Idle) {
			callEventCode = -1;
			// clear any previous state
			callEndCause = "";
			// stop will be called on the call connected/end
			radioLogMonitor.start();
		}
		// idle -> ringing --- MT call start
		if (prevState == CallState.Idle && currState == CallState.Ringing) {
			this.mCallDirection = CallDirection.MT;
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MT_Attempt);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MT_Attempt");
			this.mContext.sendBroadcast(intent);
			// ringing -> in progress --- MT call connected
		} else if (prevState == CallState.Ringing && currState == CallState.InProgress) {
			this.mCallDirection = CallDirection.MT;
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MT_Connect);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MT_Connect");
			this.mContext.sendBroadcast(intent);
			// in progress -> idle --- MT call end
		} else if (prevState == CallState.InProgress && currState == CallState.Idle && mCallDirection == CallDirection.MT) {
			this.mCallDirection = CallDirection.None;
			new Handler().postDelayed(rGetMTCallEndCause, CALL_END_HANDLER_DELAY);
			// ringing -> idle --- Missed call
		} else if (prevState == CallState.Ringing && currState == CallState.Idle) {
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			if (this.mCallDirection == CallDirection.MT) {
				intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MT_Block);
				intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MT_Block");
			} else {
				intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MO_Block);
				intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MO_Block");
			}
			this.mCallDirection = CallDirection.None;
			this.mContext.sendBroadcast(intent);
			// idle -> in progress --- MO call start
		} else if (prevState == CallState.Idle && currState == CallState.InProgress) {
			this.mCallDirection = CallDirection.MO;
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MO_Attempt);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MO_Attempt");
			this.mContext.sendBroadcast(intent);
			intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MO_Connect);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MO_Connect");
			this.mContext.sendBroadcast(intent);
			// in progress -> idle --- MO call end
		} else if (prevState == CallState.InProgress && currState == CallState.Idle && mCallDirection == CallDirection.MO) {
			this.mCallDirection = CallDirection.None;
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MO_End);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MO_End");
			this.mContext.sendBroadcast(intent);
			new Handler().postDelayed(rGetMOCallEndCause, CALL_END_HANDLER_DELAY);
		}
	}

	/**
	 * 判断主叫是否正常断开的线程
	 */
	private Runnable rGetMOCallEndCause = new Runnable() {
		@Override
		public synchronized void run() {
			radioLogMonitor.getCallLog().setCallAlerting(false);
			// stop listening to the log as the call has ended
			radioLogMonitor.stop();

			// MO call end
			int signal = 0;
			int cellId = 0;
			if (mInfo != null) {
				signal = mInfo.rxLev;
				cellId = mInfo.cellID;
			}
			int eventID = 0;
			String eventName = "";
			if (signal == -1 && cellId == -1) {
				eventID = DataSetEvent.ET_MO_Drop;
				eventName = "ET_MO_Drop";
			} else if (callEventCode == EventCode.FLAG_CALL_SETUP_FAILURE) {
				eventID = DataSetEvent.ET_MO_Drop;
				eventName = "ET_MO_Drop";
			} else if (callEventCode == EventCode.FLAG_CALL_DIS_DROP) {
				eventID = DataSetEvent.ET_MO_Drop;
				eventName = "ET_MO_Drop";
			} else {
				eventID = DataSetEvent.ET_MO_End;
				eventName = "ET_MO_End";
			}
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, eventID);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, eventName);
			mContext.sendBroadcast(intent);
		}
	};
	/*
	 * to try and the the the end cause from the log tool
	 */
	private Runnable rGetMTCallEndCause = new Runnable() {

		@Override
		public void run() {

			radioLogMonitor.stop();
			// MT call end
			int signal = 0;
			int cellId = 0;
			if (mInfo != null) {
				signal = mInfo.rxLev;
				cellId = mInfo.cellID;
			}
			int eventID = 0;
			String eventName = "";
			if (signal == -1 && cellId == -1) {
				eventID = DataSetEvent.ET_MT_Drop;
				eventName = "ET_MT_Drop";
			} else if (callEventCode == EventCode.FLAG_CALL_SETUP_FAILURE) {
				eventID = DataSetEvent.ET_MT_Drop;
				eventName = "ET_MT_Drop";
			} else if (callEventCode == EventCode.FLAG_CALL_DIS_DROP) {
				eventID = DataSetEvent.ET_MT_Drop;
				eventName = "ET_MT_Drop";
			} else {
				eventID = DataSetEvent.ET_MT_End;
				eventName = "ET_MT_End";
			}
			Intent intent = new Intent(WalkMessage.ACTION_EVENT);
			intent.putExtra(WalkMessage.KEY_EVENT_RCUID, eventID);
			intent.putExtra(WalkMessage.KEY_EVENT_STRING, eventName);
			mContext.sendBroadcast(intent);
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCellLocationChanged(CellLocation location) {
		// LogUtil.d(TAG, "----onCellLocationChanged-----");
		super.onCellLocationChanged(location);
		// LogUtil.d(TAG, location.toString());
		if (this.mTeleManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
			return;
		}
		if (location instanceof GsmCellLocation) { // 判断网络是否属于GSM类型。
			GsmCellLocation cellLoc = (GsmCellLocation) location;
			this.mInfo.mcc = MobileUtil.getSIM_MCC(mContext);
			this.mInfo.mnc = MobileUtil.getSIM_MNC(mContext);
			if (cellLoc.getCid() > 0)
				this.mInfo.cellID = cellLoc.getCid();
			this.mInfo.lac = cellLoc.getLac();
			this.mInfo.psc = cellLoc.getPsc() > 0 ? cellLoc.getPsc() : -1;
		} else if (location instanceof CdmaCellLocation) {// 判断网络是否属于类型。CDMA
			CdmaCellLocation cellLoc = (CdmaCellLocation) location;
			if (cellLoc.getSystemId() > 0)
				this.mInfo.cellID = cellLoc.getSystemId();
			this.mInfo.mcc = MobileUtil.getSIM_MCC(mContext);
			this.mInfo.mnc = MobileUtil.getSIM_MNC(mContext);
		}

		CellIdentityLte lte = MobileUtil.getCellIdentityLte(mContext);
		if (null != lte) {
			this.mInfo.tac = (lte.getTac() == Integer.MAX_VALUE ? -1 : lte.getTac());
			this.mInfo.pci = (lte.getPci() == Integer.MAX_VALUE ? -1 : lte.getPci());
			if (lte.getCi() > 0)
				this.mInfo.cellID = lte.getCi();
			this.mInfo.mcc = lte.getMcc();
			this.mInfo.mnc = lte.getMnc();
		} else {
			this.mInfo.tac = -1;
			this.mInfo.pci = -1;
		}
		this.mInfo.netType = MobileUtil.getNetworkType(mTeleManager);
		if(ApplicationModel.getInstance().isNBTest()&&Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_SUCCESS&&!DatasetManager.isPlayback) {
			NetStateModel state = NetStateModel.getInstance();
			WalkStruct.CurrentNetState status = state.getCurrentNetTypeSync();
			this.mInfo.netType=state.getCurrentNetType();
			TraceInfoInterface.currentNetType = this.mInfo.netType;
            MyPhoneState.saveNetTypeToSpf(this.mInfo.netType.getNetTypeName());
		}
		TraceInfoInterface.decodeResultUpdate(UnifyParaID.CURRENT_NETWORKTYPE,
				String.valueOf(this.mInfo.netType.getCurrentNetId()));
		TraceInfoInterface.currentNetType = this.mInfo.netType;
        MyPhoneState.saveNetTypeToSpf(this.mInfo.netType.getNetTypeName());
		switch (this.mInfo.netType) {
		case GSM:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.G_Ser_MCC,
					this.mInfo.mcc > 0 ? String.valueOf(this.mInfo.mcc) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.G_Ser_MNC, this.mInfo.mnc >= 0 ? "0" + this.mInfo.mnc : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.G_Ser_Cell_ID,
					this.mInfo.cellID > 0 ? String.valueOf(this.mInfo.cellID) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.G_Ser_LAC,
					this.mInfo.lac > 0 ? String.valueOf(this.mInfo.lac) : "");
			break;
		case CDMA:
			break;
		case LTE:
		case NBIoT:
		case CatM:
			case ENDC:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_MCC,
					this.mInfo.mcc > 0 ? String.valueOf(this.mInfo.mcc) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_MNC, this.mInfo.mnc >= 0 ? "0" + this.mInfo.mnc : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_ECGI,
					this.mInfo.cellID > 0 ? String.valueOf(this.mInfo.cellID) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_TAC,
					this.mInfo.tac > 0 ? String.valueOf(this.mInfo.tac) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_PCI,
					this.mInfo.pci > 0 ? String.valueOf(this.mInfo.pci) : "");
			break;
		case WCDMA:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_MCC,
					this.mInfo.mcc > 0 ? String.valueOf(this.mInfo.mcc) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_MNC, this.mInfo.mnc >= 0 ? "0" + this.mInfo.mnc : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_Cell_ID,
					this.mInfo.cellID > 0 ? String.valueOf(this.mInfo.cellID) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_LAC,
					this.mInfo.lac > 0 ? String.valueOf(this.mInfo.lac) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_Max_PSC,
					this.mInfo.psc > 0 ? String.valueOf(this.mInfo.psc) : "");
			break;
		case TDSCDMA:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.TD_Ser_MCC,
					this.mInfo.mcc > 0 ? String.valueOf(this.mInfo.mcc) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.TD_Ser_MNC, this.mInfo.mnc >= 0 ? "0" + this.mInfo.mnc : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.TD_Ser_CellID,
					this.mInfo.cellID > 0 ? String.valueOf(this.mInfo.cellID) : "");
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.TD_Ser_LAC,
					this.mInfo.lac > 0 ? String.valueOf(this.mInfo.lac) : "");
			break;
		default:
			break;
		}
		// LogUtil.d(TAG, this.mInfo.toString());
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		// LogUtil.d(TAG, "-----onSignalStrengthsChanged-----");
		super.onSignalStrengthsChanged(signalStrength);
		// LogUtil.d(TAG, signalStrength.toString());
		int signalStrenghInt = 1;
		LTESignalStrength lteSignalStrengh = null;
		this.mInfo.netType = MobileUtil.getNetworkType(mTeleManager);
		if (MobileUtil.isLteNetWork(mTeleManager)) {
			lteSignalStrengh = ReflectionUtils.dumpClassToLteSignalStrength(SignalStrength.class, signalStrength);
			signalStrenghInt = lteSignalStrengh.rsrp;
			this.mInfo.rxLev = (signalStrenghInt == Integer.MAX_VALUE ? 1 : signalStrenghInt);
			this.mInfo.rsrq = (lteSignalStrengh.rsrq == Integer.MAX_VALUE ? 1 : lteSignalStrengh.rsrq);
			this.mInfo.sinr = (lteSignalStrengh.sinr == Integer.MAX_VALUE ? -1 : (int) (lteSignalStrengh.sinr / 10));
			this.mInfo.rsrp = (lteSignalStrengh.rsrp == Integer.MAX_VALUE ? 1 : lteSignalStrengh.rsrp);
		} else {
			signalStrenghInt = MobileUtil.getSignalStrengthDbm(this.mContext, signalStrength);
			this.mInfo.rxLev = (signalStrenghInt == Integer.MAX_VALUE ? 1 : signalStrenghInt);
			this.mInfo.rsrq = -1;
			this.mInfo.sinr = -1;
			if (this.mInfo.netType == CurrentNetState.CDMA) {
				this.mInfo.ecio = signalStrength.getCdmaEcio() < -1 ? signalStrength.getCdmaEcio() : -1;
				if (this.mInfo.ecio >= -1) {
					this.mInfo.ecio = signalStrength.getEvdoEcio() < -1 ? signalStrength.getEvdoEcio() : -1;
				}
			} else
				this.mInfo.ecio = -1;
		}
		switch (this.mInfo.netType) {
		case GSM:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.G_Ser_RxLevFull,
					this.mInfo.rxLev < -1 ? String.valueOf(this.mInfo.rxLev) : "");
			break;
		case WCDMA:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_Total_RSCP,
					this.mInfo.rxLev < -1 ? String.valueOf(this.mInfo.rxLev) : "");
			break;
		case TDSCDMA:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.TD_DPA1_A_DPCH_RSCP,
					this.mInfo.rxLev < -1 ? String.valueOf(this.mInfo.rxLev) : "");
			break;
		case CDMA:
			TraceInfoInterface.decodeResultUpdate(UnifyParaID.C_RxAGC,
					this.mInfo.rxLev < -1 ? String.valueOf(this.mInfo.rxLev) : "");
			break;
		default:
			break;
		}
		TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_RSRQ,
				this.mInfo.rsrq < -1 ? String.valueOf(this.mInfo.rsrq) : "");
		TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_SINR,
				this.mInfo.sinr > 0 ? String.valueOf(this.mInfo.sinr) : "");
		TraceInfoInterface.decodeResultUpdate(UnifyParaID.L_SRV_RSRP,
				this.mInfo.rsrp < -1 ? String.valueOf(this.mInfo.rsrp) : "");
		TraceInfoInterface.decodeResultUpdate(UnifyParaID.W_Ser_Max_EcIo,
				this.mInfo.ecio < -1 ? String.valueOf(this.mInfo.ecio) : "");
		// LogUtil.d(TAG, this.mInfo.toString());

	}

	/**
	 * 日志事件监听类
	 */
	private EventListener logcatEventListener = new EventListener() {
		@Override
		public void comeEvent(int eventCode, int causeCode, long timestamp) {
			switch (eventCode) {
			case EventCode.FLAG_CALL_NORMAL:
				// cause is empty
				break;
			case EventCode.FLAG_CALL_DIS_UNKOWN:
				// cause is empty
				break;
			case EventCode.FLAG_CALL_ALERTING:
				Intent intent = new Intent(WalkMessage.ACTION_EVENT);
				intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MO_Alerting);
				intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MO_Alerting");
				mContext.sendBroadcast(intent);
				break;
			case EventCode.FLAG_CALL_RINGING:
				intent = new Intent(WalkMessage.ACTION_EVENT);
				intent.putExtra(WalkMessage.KEY_EVENT_RCUID, DataSetEvent.ET_MT_Alerting);
				intent.putExtra(WalkMessage.KEY_EVENT_STRING, "ET_MT_Alerting");
				mContext.sendBroadcast(intent);
				break;
			case EventCode.FLAG_CALL_DIS_BUSY:
				callEventCode = EventCode.FLAG_CALL_DIS_BUSY;
				callEndCause = "user busy (17)";
				break;
			case EventCode.FLAG_CALL_DIS_LOCAL:
				callEventCode = EventCode.FLAG_CALL_DIS_LOCAL;
				callEndCause = "local";
				break;
			case EventCode.FLAG_CALL_DIS_REMOTE:
				callEventCode = EventCode.FLAG_CALL_DIS_REMOTE;
				callEndCause = "normal call clearing (16)";
				break;
			case EventCode.FLAG_CALL_MISSED:
				callEventCode = EventCode.FLAG_CALL_MISSED;
				callEndCause = "user alerting/no answer (19)";
				break;
			case EventCode.FLAG_CALL_REJECTED:
				callEventCode = EventCode.FLAG_CALL_REJECTED;
				callEndCause = "call rejected (21)";
			case EventCode.FLAG_CALL_ACTIVE:
				// cause is empty
				break;
			case EventCode.FLAG_CALL_DIS_DROP:
				callEventCode = EventCode.FLAG_CALL_DIS_DROP;
				callEndCause = CallFailCause.getCauseByCode(causeCode);
				break;
			case EventCode.FLAG_CALL_NORESPONSE:
				callEventCode = EventCode.FLAG_CALL_NORESPONSE;
				callEndCause = CallFailCause.getCauseByCode(causeCode);
				break;
			case EventCode.FLAG_CALL_SETUP_FAILURE:
				callEventCode = EventCode.FLAG_CALL_SETUP_FAILURE;
				callEndCause = CallFailCause.getCauseByCode(causeCode);
				break;
			default:
				return;
			}

			if (callEndCause == null) {
				callEndCause = "";
			}
		}
	};

	/**
	 * 手机基本信息,实时存储手机当前信息
	 *
	 * @author jianchao.wang
	 *
	 */
	protected class MobileInfo {
		/** 手机网络制式 */
		protected CurrentNetState netType;
		/** 手机TAC */
		protected int tac;
		/** 手机PCI */
		protected int pci;
		/** 手机信号强度Rxlev */
		protected int rxLev;
		/** 手机RSRQ */
		protected int rsrq;
		/** 手机RSRP */
		protected int rsrp;
		/** 手机SINR */
		protected int sinr;
		/** 小区ID */
		protected int cellID;
		/** 手机LAC */
		protected int lac;
		/** 手机PSC */
		protected int psc;
		/** 手机MNC */
		protected int mnc = -1;
		/** 手机MCC */
		protected int mcc;
		/** 手机ECIO */
		protected int ecio;

		@Override
		public String toString() {
			return "MobileInfo [netType=" + netType + ",tac=" + tac + ",pci=" + pci + ",rxLev=" + rxLev + ",rsrq=" + rsrq
					+ ",sinr=" + sinr + ",cellID=" + cellID + ",lac=" + lac + ",psc=" + psc + ",mnc=" + mnc + ",mcc=" + mcc + "]";
		}
	}

	public MobileInfo getInfo() {
		return mInfo;
	}

}

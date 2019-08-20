package com.walktour.service.phoneinfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.walktour.Utils.UnifyParaID;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.service.DatasetRecordService;
import com.walktour.service.phoneinfo.PhoneInfoListener.MobileInfo;
import com.walktour.service.phoneinfo.pointinfo.PointInfo;
import com.walktour.service.phoneinfo.pointinfo.model.PointInfoHeader;
import com.walktour.service.phoneinfo.pointinfo.model.PointInfoParam;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 移动网络监听服务类
 * 
 * @author lucien
 *
 */
public class TelephonyManagerService extends Service {
	private final static String TAG = "TelephonyManagerService";
	/** 移动网络控制类 */
	private TelephonyManager mTeleManager;
	/** 网络参数监听类 */
	private PhoneInfoListener mListener;
	/** 新增采样点间隔时间（毫秒） */
	private static final int intervalAddPointTime = 500;
	/** 刷新参数界面间隔时间（毫秒） */
	private static final int intervalFreshViewTime = 1000;
	/** 上一个采样点的时间(单位：微秒) */
	private long mLastPointTime = 0;
	/** 上下文 */
	private Context mContext;
	/** 新增采样点定时器任务 */
	private Timer mAddPointTimer = new Timer();
	/** 刷新参数界面定时器任务 */
	private Timer mFreshViewTimer = new Timer();
	/** 句柄 */
	private Handler mHandler = new MyHandler(this);

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d(TAG, "---------onStartCommand----------");
		this.mContext = this;
		this.mTeleManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		this.mListener = new PhoneInfoListener(this, this.mTeleManager);
		this.mTeleManager.listen(this.mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_CALL_STATE);
		Intent datasetIntent = new Intent(this, DatasetRecordService.class);
		startService(datasetIntent);
		this.mAddPointTimer.schedule(this.mAddPointTask, intervalAddPointTime, intervalAddPointTime);
		this.mFreshViewTimer.schedule(this.mFreshViewTask, intervalFreshViewTime, intervalFreshViewTime);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.d(TAG, "---------onDestroy----------");
		Intent datasetIntent = new Intent(this, DatasetRecordService.class);
		stopService(datasetIntent);
		if (this.mAddPointTask != null) {
			this.mAddPointTask.cancel();
		}
		if (this.mAddPointTimer != null) {
			this.mAddPointTimer.cancel();
		}
		if (this.mFreshViewTask != null) {
			this.mFreshViewTask.cancel();
		}
		if (this.mFreshViewTimer != null) {
			this.mFreshViewTimer.cancel();
		}
		super.onDestroy();
	}

	private static class MyHandler extends Handler {
		private WeakReference<TelephonyManagerService> reference;

		public MyHandler(TelephonyManagerService service) {
			this.reference = new WeakReference<TelephonyManagerService>(service);
		}

		public void handleMessage(Message msg) {
			TelephonyManagerService service = this.reference.get();
			switch (msg.what) {
			case 1:
				service.addPoint();
				break;
			case 2:
				service.freshView();
				break;
			}
		};
	};

	/**
	 * 刷新参数界面
	 */
	private void freshView() {
		RefreshEventManager.notifyRefreshEvent(RefreshType.ACTION_WALKTOUR_TIMER_CHANGED, null);
	}

	/**
	 * 添加采样点
	 */
	private void addPoint() {
		MobileInfo info = mListener.getInfo();
		if (info.netType == null)
			return;
		switch (info.netType) {
		case GSM:
			this.addGSMPoint(info);
			break;
		case CDMA:
			this.addCDMAPoint(info);
			break;
		case LTE:
			this.addLTEPoint(info);
			break;
		case WCDMA:
			this.addWCDMAPoint(info);
			break;
		case TDSCDMA:
			this.addTDSCDMAPoint(info);
			break;
		default:
			break;
		}
	}

	/**
	 * 采样点任务
	 */
	private TimerTask mAddPointTask = new TimerTask() {

		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			mHandler.sendMessage(message);
		}
	};

	/**
	 * 刷新参数界面任务
	 */
	private TimerTask mFreshViewTask = new TimerTask() {

		@Override
		public void run() {
			Message message = new Message();
			message.what = 2;
			mHandler.sendMessage(message);
		}
	};

	/**
	 * 添加CDMA采样点
	 * 
	 * @param info
	 *          设备信息
	 * 
	 */
	private void addCDMAPoint(MobileInfo info) {
		List<PointInfoParam> paramList = new ArrayList<PointInfoParam>();
		paramList.add(new PointInfoParam(UnifyParaID.CURRENT_NETWORKTYPE, UnifyParaID.NET_CDMA_EVDO));
		if (info.ecio < -1)
			paramList.add(new PointInfoParam(UnifyParaID.C_MaxEcIo, info.ecio * 1000));
		if (paramList.size() == 1)
			return;
		PointInfoHeader header = new PointInfoHeader(PointInfoHeader.MSG_CODE_TDSCDMA);
		this.setPointHandSetInterval(header);
		new PointInfo(mContext, header, paramList, null).writeToRcu();
	}

	/**
	 * 添加TDSCDMA采样点
	 * 
	 * @param info
	 *          设备信息
	 * 
	 */
	private void addTDSCDMAPoint(MobileInfo info) {
		List<PointInfoParam> paramList = new ArrayList<PointInfoParam>();
		paramList.add(new PointInfoParam(UnifyParaID.CURRENT_NETWORKTYPE, UnifyParaID.NET_TDSCDMA));
		if (info.mcc > 0)
			paramList.add(new PointInfoParam(UnifyParaID.TD_Ser_MCC, info.mcc));
		if (info.mnc >= 0)
			paramList.add(new PointInfoParam(UnifyParaID.TD_Ser_MNC, info.mnc));
		if (info.cellID > 0)
			paramList.add(new PointInfoParam(UnifyParaID.TD_Ser_CellID, info.cellID));
		if (info.lac > 0)
			paramList.add(new PointInfoParam(UnifyParaID.TD_Ser_LAC, info.lac));
		if (info.rxLev < -1)
			paramList.add(new PointInfoParam(UnifyParaID.TD_Ser_CarrierRSSI, info.rxLev * 1000));
		if (paramList.size() == 1)
			return;
		PointInfoHeader header = new PointInfoHeader(PointInfoHeader.MSG_CODE_TDSCDMA);
		this.setPointHandSetInterval(header);
		new PointInfo(mContext, header, paramList, null).writeToRcu();
	}

	/**
	 * 添加WCDMA采样点
	 * 
	 * @param info
	 *          设备信息
	 * 
	 */
	private void addWCDMAPoint(MobileInfo info) {
		List<PointInfoParam> paramList = new ArrayList<PointInfoParam>();
		paramList.add(new PointInfoParam(UnifyParaID.CURRENT_NETWORKTYPE, UnifyParaID.NET_WCDMA));
		if (info.mcc > 0)
			paramList.add(new PointInfoParam(UnifyParaID.W_Ser_MCC, info.mcc));
		if (info.mnc >= 0)
			paramList.add(new PointInfoParam(UnifyParaID.W_Ser_MNC, info.mnc));
		if (info.cellID > 0)
			paramList.add(new PointInfoParam(UnifyParaID.W_Ser_Cell_ID, info.cellID));
		if (info.lac > 0)
			paramList.add(new PointInfoParam(UnifyParaID.W_Ser_LAC, info.lac));
		if (info.psc > 0)
			paramList.add(new PointInfoParam(UnifyParaID.W_Ser_Max_PSC, info.psc));
		if (info.rxLev < -1)
			paramList.add(new PointInfoParam(UnifyParaID.W_Ser_Total_RSCP, info.rxLev));
		if (paramList.size() == 1)
			return;
		PointInfoHeader header = new PointInfoHeader(PointInfoHeader.MSG_CODE_WCDMA);
		this.setPointHandSetInterval(header);
		new PointInfo(mContext, header, paramList, null).writeToRcu();
	}

	/**
	 * 添加GSM采样点
	 * 
	 * @param info
	 *          设备信息
	 * 
	 */
	private void addGSMPoint(MobileInfo info) {
		List<PointInfoParam> paramList = new ArrayList<PointInfoParam>();
		paramList.add(new PointInfoParam(UnifyParaID.CURRENT_NETWORKTYPE, UnifyParaID.NET_GSM));
		if (info.mcc > 0)
			paramList.add(new PointInfoParam(UnifyParaID.G_Ser_MCC, info.mcc));
		if (info.mnc >= 0)
			paramList.add(new PointInfoParam(UnifyParaID.G_Ser_MNC, info.mnc));
		if (info.cellID > 0)
			paramList.add(new PointInfoParam(UnifyParaID.G_Ser_Cell_ID, info.cellID));
		if (info.lac > 0)
			paramList.add(new PointInfoParam(UnifyParaID.G_Ser_LAC, info.lac));
		if (paramList.size() == 1)
			return;
		PointInfoHeader header = new PointInfoHeader(PointInfoHeader.MSG_CODE_GSM);
		this.setPointHandSetInterval(header);
		new PointInfo(mContext, header, paramList, null).writeToRcu();
	}

	/**
	 * 添加LTE采样点
	 * 
	 * @param info
	 *          设备信息
	 */
	private void addLTEPoint(MobileInfo info) {
		List<PointInfoParam> paramList = new ArrayList<PointInfoParam>();
		paramList.add(new PointInfoParam(UnifyParaID.CURRENT_NETWORKTYPE, UnifyParaID.NET_LTE));
		if (info.mcc > 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_MCC, info.mcc));
		if (info.mnc >= 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_MNC, info.mnc));
		if (info.cellID > 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_ECGI, info.cellID));
		if (info.tac > 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_TAC, info.tac));
		if (info.pci > 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_PCI, info.pci));
		if (info.rsrq < 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_RSRQ, info.rsrq * 100));
		if (info.sinr > 0)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_SINR, info.sinr * 1000));
		if (info.rsrp < -1)
			paramList.add(new PointInfoParam(UnifyParaID.L_SRV_RSRP, info.rsrp * 100));
		if (paramList.size() == 1)
			return;
		PointInfoHeader header = new PointInfoHeader(PointInfoHeader.MSG_CODE_LTE);
		this.setPointHandSetInterval(header);
		new PointInfo(mContext, header, paramList, null).writeToRcu();
	}

	/**
	 * 设置采样点的间隔时间
	 * 
	 * @param header
	 *          采样点头
	 */
	private void setPointHandSetInterval(PointInfoHeader header) {
		if (this.mLastPointTime > 0)
			header.setHandSetInterval((int) (header.getHandSetTime() - this.mLastPointTime));
		this.mLastPointTime = header.getHandSetTime();
	}

}

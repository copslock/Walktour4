package com.walktour.gui.locknet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.PageQueryParas;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.dialog.LockBandDialog;
import com.walktour.gui.locknet.dialog.LockCellDialog;
import com.walktour.gui.locknet.dialog.LockFrequencyDialog;
import com.walktour.gui.locknet.dialog.LockNetworkDialog;
import com.walktour.gui.locknet.dialog.OnDialogChangeListener;
import com.walktour.gui.locknet.dialog.UnlockAllDialog;
import com.walktour.model.NetStateModel;

import java.lang.ref.WeakReference;

/**
 * LockMainPage
 *
 * 2014-6-23 上午11:50:39
 *
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class LockMainPage extends BasicActivity {

	private final static int MSG_REFRESH = 0;
	private final static int VIEW_REFRESH = 10010;
	/** 显示信息 */
	private final static int MSG_SHOW = 1;
	/** 隐藏信息 */
	private final static int MSG_DIMISS = -1;

	private Activity mContext;
	private ForceManager mForceMgr;

	// View
	private RelativeLayout network;
	private RelativeLayout band;
	private RelativeLayout frequency;
	private RelativeLayout cell;
	private RelativeLayout camp;
	private RelativeLayout release;
	private RelativeLayout releaseCell;
	private TextView txtStatus;
	private TextView txtParaName1;
	private TextView txtParaValue1;
	private TextView txtParaName2;
	private TextView txtParaValue2;
	private TextView txtParaName3;
	private TextView txtParaValue3;
	private TextView txtParaName4;
	private TextView txtParaValue4;
	private TextView txtParaName5;
	private TextView txtParaValue5;
	private TextView txtParaName6;
	private TextView txtParaValue6;

	private TextView txtLockNet;
	private TextView txtLockBand;
	private TextView txtLockFrequency;
	private TextView txtLockCell;

	private boolean finish = false;
	private DatasetManager dataSetManager = null;
	private Handler mHandler = new MyHandler(new WeakReference<>(this));
	/** 进度对话框 */
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lock);
		(initTextView(R.id.title_txt)).setText(R.string.setting);
		findViewById(R.id.pointer).setOnClickListener(this);

		mForceMgr = ForceManager.getInstance();
		mForceMgr.init();

		dataSetManager = DatasetManager.getInstance(mContext);

		findView();

		finish = false;
		threadRefresh.start();
	}

	private void findView() {
		network = initRelativeLayout(R.id.setting_network_layout);
		band = initRelativeLayout(R.id.setting_band_layout);
		frequency = initRelativeLayout(R.id.setting_frequency_layout);
		cell = initRelativeLayout(R.id.setting_cell_layout);
		camp = initRelativeLayout(R.id.setting_camp_layout);
		release = initRelativeLayout(R.id.setting_release_layout);
		releaseCell = initRelativeLayout(R.id.setting_release_cell_layout);

		txtLockNet = initTextView(R.id.TextNet);
		txtLockBand = initTextView(R.id.TextBand);
		txtLockFrequency = initTextView(R.id.TextFrequency);
		txtLockCell = initTextView(R.id.TextCell);

		String lockNet = mForceMgr.getLockNet(mContext).descrition;
		txtLockNet.setText(lockNet);
		txtLockBand.setText(mForceMgr.getLockBands(mContext));
		txtLockFrequency.setText(mForceMgr.getLockFrequency(mContext));
		txtLockCell.setText(mForceMgr.getLockCell(mContext));

		Deviceinfo device = Deviceinfo.getInstance();
		Deviceinfo.LockInfo lockInfo = device.getLockInfo();
		network.setVisibility(lockInfo.hasLockNet() ? View.VISIBLE : View.GONE);
		band.setVisibility(lockInfo.hasLockBand() ? View.VISIBLE : View.GONE);
		frequency.setVisibility(lockInfo.hasLockFreq() ? View.VISIBLE : View.GONE);
		cell.setVisibility(lockInfo.hasLockCell() ? View.VISIBLE : View.GONE);
		release.setVisibility(device.getDevicemodel().equals("ZTENX569J") ? View.GONE : View.VISIBLE);
		Deviceinfo deviceinfo = Deviceinfo.getInstance();

		releaseCell.setVisibility(lockInfo.hasLockCell() ? View.VISIBLE : View.GONE);
		camp.setVisibility(lockInfo.hasCampCell() ? View.VISIBLE : View.GONE);

		network.setOnClickListener(this);
		band.setOnClickListener(this);
		frequency.setOnClickListener(this);
		cell.setOnClickListener(this);
		camp.setOnClickListener(this);
		release.setOnClickListener(this);
		releaseCell.setOnClickListener(this);

		txtStatus = initTextView(R.id.TextViewStatus);
		txtParaName1 = initTextView(R.id.TextViewPara1);
		txtParaValue1 = initTextView(R.id.TextViewValue1);
		txtParaName2 = initTextView(R.id.TextViewPara2);
		txtParaValue2 = initTextView(R.id.TextViewValue2);
		txtParaName3 = initTextView(R.id.TextViewPara3);
		txtParaValue3 = initTextView(R.id.TextViewValue3);
		txtParaName4 = initTextView(R.id.TextViewPara4);
		txtParaValue4 = initTextView(R.id.TextViewValue4);
		txtParaName5 = initTextView(R.id.TextViewPara5);
		txtParaValue5 = initTextView(R.id.TextViewValue5);
		txtParaName6 = initTextView(R.id.TextViewPara6);
		txtParaValue6 = initTextView(R.id.TextViewValue6);
		setEmpty();
	}

	private Thread threadRefresh = new Thread() {
		@Override
		public void run() {

			while (!finish) {
				NetStateModel state = NetStateModel.getInstance();
				CurrentNetState status = state.getCurrentNetTypeSync();
//				LogUtil.w("ffppooqq","status="+status.getNetTypeName()+",type="+status.getNetType());
				int[] queryParas = new int[0];
				switch (status) {
					case GSM:
						queryParas = PageQueryParas.PageQueryGsm;
						break;
					case TDSCDMA:
						queryParas = PageQueryParas.PageQueryTDScdma;
						break;
					case WCDMA:
						queryParas = PageQueryParas.PageQueryWcdma;
						break;
					case CDMA:
						queryParas = PageQueryParas.PageQueryCdma;
						break;
					case LTE:
						queryParas = PageQueryParas.PageQueryLte;
						break;
					case NBIoT:
					case CatM:
						queryParas = PageQueryParas.PageQueryLte;
						break;
					default:
						break;
				}

				if (ApplicationModel.getInstance().isTraceInitSucc() && queryParas.length > 0) {
					dataSetManager.queryParamsSync(queryParas);
					mHandler.obtainMessage(MSG_REFRESH, status).sendToTarget();
				}

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private static class MyHandler extends Handler {
		WeakReference<LockMainPage> reference;

		public MyHandler(WeakReference<LockMainPage> reference) {
			this.reference = reference;
		}

		@Override
		public void handleMessage(Message msg) {
			LockMainPage ref = this.reference.get();
			switch (msg.what) {
				case MSG_REFRESH:
					CurrentNetState status = (CurrentNetState) msg.obj;
					ref.setStatus(status);
					break;
				case VIEW_REFRESH:
					ref.findView();
					break;
				case MSG_SHOW:
					ref.showProgressDialog("", true);
					break;

				case MSG_DIMISS:
					if (ref.progressDialog != null) {
						if (ref.progressDialog.isShowing()) {
							ref.progressDialog.dismiss();
						}
					}
					break;
			}
		}

	}

	/**
	 * 显示进度对话框
	 *
	 * @param message
	 *          信息
	 * @param cancleable
	 *          是否可关闭
	 */
	private void showProgressDialog(String message, boolean cancleable) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancleable);
		progressDialog.show();
	}

	private void setStatus(CurrentNetState status) {
		txtStatus.setText(status.name());
		switch (status) {
			case GSM:
				setGSM();
				break;
			case TDSCDMA:
				setTDSCDMA();
				break;
			case WCDMA:
				setWCDMA();
				break;
			case CDMA:
				setCDMA();
				break;
			case LTE:
				setLTE();
				break;
			case NBIoT:
			case CatM:
				setLTE();
				break;
			default:
				setEmpty();
				break;
		}
	}

	private void setGSM() {
		String[] paras = new String[] { getString(R.string.gsm_rxLevFull), getString(R.string.gsm_mcc_mnc), "",
				getString(R.string.gsm_servingBCCHARFCN), getString(R.string.gsm_bsic), getString(R.string.gsm_ta) };

		String[] values = new String[] { getParaValue(UnifyParaID.G_Ser_RxLevFull),
				getParaValue(UnifyParaID.G_Ser_MCC).equals("") && getParaValue(UnifyParaID.G_Ser_MNC).equals("") ? ""
						: getParaValue(UnifyParaID.G_Ser_MCC) + "/" + getParaValue(UnifyParaID.G_Ser_MNC),
				"", getParaValue(UnifyParaID.G_Ser_BCCH), getParaValue(UnifyParaID.G_Ser_BSIC),
				getParaValue(UnifyParaID.G_Ser_TA)

		};
		setParas(paras, values);
	}

	private void setWCDMA() {
		String[] paras = new String[] { getString(R.string.wcdma_total_rscp), getString(R.string.wcdma_mcc_mnc), "",
				"DL UARFCN", "UL UARFCN", getString(R.string.wcdma_psc) };
		String mcc_mnc = getParaValue(UnifyParaID.W_Ser_MCC).equals("") && getParaValue(UnifyParaID.W_Ser_MNC).equals("")
				? "" : getParaValue(UnifyParaID.W_Ser_MCC) + "/" + getParaValue(UnifyParaID.W_Ser_MNC);
		String[] values = new String[] { getParaValue(UnifyParaID.W_Ser_Total_RSCP), mcc_mnc, "",
				getParaValue(UnifyParaID.W_Ser_DL_UARFCN), getParaValue(UnifyParaID.W_Ser_UL_UARFCN),
				getParaValue(UnifyParaID.W_Ser_Max_PSC)

		};
		setParas(paras, values);
	}

	private void setTDSCDMA() {
		String[] paras = new String[] { getString(R.string.tdscdma_pccpchrscp), getString(R.string.tdscdma_mcc_mnc), "",
				getString(R.string.tdscdma_dchuarfcn), getString(R.string.tdscdma_uarfcn), getString(R.string.tdscdma_cpi) };

		String mnc = getParaValue(UnifyParaID.TD_Ser_MCC).equals("") && getParaValue(UnifyParaID.TD_Ser_MNC).equals("") ? ""
				: getParaValue(UnifyParaID.TD_Ser_MCC) + "/" + getParaValue(UnifyParaID.TD_Ser_MNC);
		String[] values = new String[] { getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP), mnc, "",
				getParaValue(UnifyParaID.TD_Ser_DCHURAFCN), getParaValue(UnifyParaID.TD_Ser_URAID),
				getParaValue(UnifyParaID.TD_Ser_CPI) };
		setParas(paras, values);
	}

	private void setLTE() {
		String[] paras = new String[] { getString(R.string.lte_rsrp), getString(R.string.lte_mcc_mnc),
				getString(R.string.lte_band), getString(R.string.lte_workmode), "UL Freq", "DL Freq" };

		String mnc = getParaValue(UnifyParaID.L_SRV_MCC).equals("") && getParaValue(UnifyParaID.L_SRV_MNC).equals("") ? ""
				: getParaValue(UnifyParaID.L_SRV_MCC) + "/" + getParaValue(UnifyParaID.L_SRV_MNC);

		String[] values = new String[] { getParaValue(UnifyParaID.L_SRV_RSRP), mnc, getParaValue(UnifyParaID.L_SRV_Band),
				UtilsMethodPara.getLteWorkModel(getParaValue(UnifyParaID.L_SRV_Work_Mode)),
				getParaValue(UnifyParaID.L_SRV_UL_Freq), getParaValue(UnifyParaID.L_SRV_DL_Freq), };
		setParas(paras, values);
	}

	private void setCDMA() {

	}

	private void setEmpty() {
		txtStatus.setText("");
		txtParaName1.setText("");
		txtParaName2.setText("");
		txtParaName3.setText("");
		txtParaName4.setText("");
		txtParaName5.setText("");
		txtParaName6.setText("");
		txtParaValue1.setText("");
		txtParaValue2.setText("");
		txtParaValue3.setText("");
		txtParaValue4.setText("");
		txtParaValue5.setText("");
		txtParaValue6.setText("");
	}

	private void setParas(String[] paraNames, String[] paraValues) {
		txtParaName1.setText(paraNames[0]);
		txtParaName2.setText(paraNames[1]);
		txtParaName3.setText(paraNames[2]);
		txtParaName4.setText(paraNames[3]);
		txtParaName5.setText(paraNames[4]);
		txtParaName6.setText(paraNames[5]);
		txtParaValue1.setText(paraValues[0]);
		txtParaValue2.setText(paraValues[1]);
		txtParaValue3.setText(paraValues[2]);
		txtParaValue4.setText(paraValues[3]);
		txtParaValue5.setText(paraValues[4]);
		txtParaValue6.setText(paraValues[5]);
	}

	/** 获得得参数队列中指定ID的值 */
	private String getParaValue(int paraId) {
		return TraceInfoInterface.getParaValue(paraId);
	}

	@Override
	public void onClick(View v) {
		Deviceinfo info = Deviceinfo.getInstance();
		String deviceModel = info.getDevicemodel();
		switch (v.getId()) {
			case R.id.setting_network_layout:
				switch (deviceModel) {
					case "SM-G9300":
					case "SM-G9308": {
						new Thread(new ThreadCommand("2263")).start();
						break;
					}
					case "SM-G9500":
					case "SM-G9550": {
						//如果是S8定制机root版或非root版;
						if (info.isSamsungCustomRom() || info.isCustomS8RomRoot()) {
							new LockNetworkDialog(mContext, listener).show();
						} else if (7 == info.getMainReleaseVersion()) {
							new Thread(new ThreadCommand("2263")).start();
						} else {
							new LockNetworkDialog(mContext, listener).show();
						}
						break;
					}
					default: {
						new LockNetworkDialog(mContext, listener).show();
						break;
					}
				}
				break;
			case R.id.setting_band_layout:
				switch (deviceModel) {
					case "SM-G9300":
					case "SM-G9308": {
						new Thread(new ThreadCommand("2263")).start();
						break;
					}
					case "SM-G9500":
					case "SM-G9550": {
						//如果是S8定制机root版或非root版;
						if (info.isSamsungCustomRom() || info.isCustomS8RomRoot()) {
							new LockBandDialog(mContext, listener).show();
						} else if (7 == info.getMainReleaseVersion()) {
							new Thread(new ThreadCommand("2263")).start();
						} else {
							new LockBandDialog(mContext, listener).show();
						}
						break;
					}
					default: {
						new LockBandDialog(mContext, listener).show();
						break;
					}
				}
				break;
			case R.id.setting_frequency_layout:
				new LockFrequencyDialog(mContext, listener).show();
				break;
			case R.id.setting_cell_layout:
				new LockCellDialog(mContext, listener).show();
				break;
			case R.id.setting_release_layout:
				new UnlockAllDialog(mContext, false, listener).show();
				break;
			case R.id.setting_release_cell_layout:
				new UnlockAllDialog(mContext, true, listener).show();
				break;
			case R.id.pointer:
				this.finish();
		}
	}

	// 2012.11.22添加SUMSUNG Galax 3手机的锁网页面跳转
	private boolean setting = false;

	private class ThreadCommand implements Runnable {
		String command = "";

		public ThreadCommand(String cmd) {
			this.command = cmd;
		}

		@Override
		public void run() {
			if (setting) {
				return;
			}
			setting = true;
			mHandler.obtainMessage(MSG_SHOW).sendToTarget();

			UtilsMethod
					.runRootCommand("am broadcast -a android.provider.Telephony.SECRET_CODE -d android_secret_code://" + command);

			mHandler.obtainMessage(MSG_DIMISS).sendToTarget();
			setting = false;
		}

	}

	private OnDialogChangeListener listener = new OnDialogChangeListener() {
		@Override
		public void onPositive() {
			mHandler.obtainMessage(VIEW_REFRESH).sendToTarget();
		}

		@Override
		public void onLockPositive(String lockType) {
			mHandler.obtainMessage(VIEW_REFRESH).sendToTarget();

			if (Deviceinfo.getInstance().isVivo())
			{
				if (lockType.equals(ForceManager.KEY_LOCK_CELL) || lockType.equals(ForceManager.KEY_LOCK_FREQUENCY)
					|| lockType.equals(ForceManager.KEY_UNLOCK_FREQUENCY_CELL))
				showRebortDialog();

				if (lockType.equals(ForceManager.KEY_LOCK_NET))
					showRebortDialog();
			}
		}

	};

	/**
	 * 显示重启提示对话框
	 */
	private void showRebortDialog() {
		new BasicDialog.Builder(this.mContext).setMessage(R.string.lock_device_rebort)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						UtilsMethod.rebootMachine();
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mForceMgr.release();
		finish = true;
	}

}

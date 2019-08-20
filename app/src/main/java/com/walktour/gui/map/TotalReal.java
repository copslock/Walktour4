package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalEvent;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.Utils.excel.manger.TotalManger;
import com.walktour.control.adapter.CustomPagerAdapter;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.framework.view.TotalScrollTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.gui.total.Total3GParamView;
import com.walktour.gui.total.TotalAttachView;
import com.walktour.gui.total.TotalCdma1ParamView;
import com.walktour.gui.total.TotalCdmaParamView;
import com.walktour.gui.total.TotalDNSView;
import com.walktour.gui.total.TotalDialView;
import com.walktour.gui.total.TotalDialVoLteView;
import com.walktour.gui.total.TotalEmailView;
import com.walktour.gui.total.TotalEvdoParamView;
import com.walktour.gui.total.TotalEventCustomView;
import com.walktour.gui.total.TotalFaceBookView;
import com.walktour.gui.total.TotalFtpView;
import com.walktour.gui.total.TotalGprsEParamView;
import com.walktour.gui.total.TotalHttpView;
import com.walktour.gui.total.TotalLAlarmParamView;
import com.walktour.gui.total.TotalLTEParam2View;
import com.walktour.gui.total.TotalLTEParamView;
import com.walktour.gui.total.TotalMmsView;
import com.walktour.gui.total.TotalMultiHttpDownloadView;
import com.walktour.gui.total.TotalOneGeEventView;
import com.walktour.gui.total.TotalOpenSignalTestView;
import com.walktour.gui.total.TotalOttView;
import com.walktour.gui.total.TotalPBMView;
import com.walktour.gui.total.TotalPPPView;
import com.walktour.gui.total.TotalPara;
import com.walktour.gui.total.TotalPdpView;
import com.walktour.gui.total.TotalPingView;
import com.walktour.gui.total.TotalQualityGsmView;
import com.walktour.gui.total.TotalQualityLteView;
import com.walktour.gui.total.TotalQualityTdView;
import com.walktour.gui.total.TotalSecondGeEventView;
import com.walktour.gui.total.TotalSmsView;
import com.walktour.gui.total.TotalSpeedTestView;
import com.walktour.gui.total.TotalTDSCDMAParamView;
import com.walktour.gui.total.TotalTraceRouteView;
import com.walktour.gui.total.TotalUDPView;
import com.walktour.gui.total.TotalVSParaView;
import com.walktour.gui.total.TotalVSView;
import com.walktour.gui.total.TotalVideoKpiView;
import com.walktour.gui.total.TotalVideoKriView;
import com.walktour.gui.total.TotalWapView;
import com.walktour.gui.total.TotalWcdmaParamView;
import com.walktour.gui.total.TotalWeChatView;
import com.walktour.gui.total.TotalWeiBoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 实时统计信息,至尊版
 * 
 * @author zhihui.lian
 */
@SuppressLint("UseSparseArrays")
public class TotalReal extends TotalScrollTabActivity implements RefreshEventListener {

	private Context mContext;
	private ViewPager mPager;
	private CustomPagerAdapter adapter;
	private List<View> views = new ArrayList<View>();
	private LinearLayout switchLayout;
	private OnCustomPageChangeListener onCustomPageChangeListener;
	private ApplicationModel applicationModel; // 取当前测试类型
	private Set<Integer> mapExist = new HashSet<Integer>();
	private BasicDialog.Builder dialog;
	private ImageButton exportExcel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.total_info_activity);
		mContext = this;
		registFilter();
		applicationModel = ApplicationModel.getInstance();
		RefreshEventManager.addRefreshListener(this);
		mPager = (ViewPager) findViewById(R.id.viewPager);
		switchLayout = (LinearLayout) findViewById(R.id.switch_layout);
		adapter = new CustomPagerAdapter(views);
		mPager.setAdapter(adapter);

		initView();

	}

	private void registFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WalkMessage.ACTION_WALKTOUR_START_TEST);
		registerReceiver(broadcastReceiver, filter);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		HashMap<Integer, Integer> testNetHashMap = applicationModel.getCurrentTestNet(); // 网络

		HashMap<String, String> taskListHashMap = applicationModel.getCurrentTestTaskList(); // 任务
		if (testNetHashMap != null) {
			for (HashMap.Entry<Integer, Integer> entry : testNetHashMap.entrySet()) {
//				LogUtil.d("-----", "----" + CurrentNetState.getNetTypeId(entry.getKey()) + "---" + entry.getKey());
				switch (CurrentNetState.getNetTypeId(entry.getKey())) {
				case GSM:
					if (!mapExist.contains(1001)) {
						views.add(getLocalActivityManager()
								.startActivity("Module1", new Intent(getApplicationContext(), TotalPara.class)).getDecorView());
						views.add(new TotalGprsEParamView(this));
						mapExist.add(1001);
					}
					break;
				case WCDMA:
					if (!mapExist.contains(1002)) {
						views.add(new TotalWcdmaParamView(this));
						views.add(new Total3GParamView(this));
						mapExist.add(1002);
					}
					break;
				case TDSCDMA:
					if (!mapExist.contains(1003)) {
						views.add(new TotalTDSCDMAParamView(this));
						mapExist.add(1003);
					}
					break;
				case LTE:
					if (!mapExist.contains(1004)) {
						views.add(new TotalLTEParamView(this));
						views.add(new TotalLTEParam2View(this));
						views.add(new TotalQualityLteView(this));
						mapExist.add(1004);
					}
					break;
				case CDMA:
					if (!mapExist.contains(1005)) {
						views.add(new TotalCdmaParamView(this));
						views.add(new TotalCdma1ParamView(this));
						views.add(new TotalEvdoParamView(this));
						mapExist.add(1005);
					}
					break;
				default:
					break;
				}

			}
		}

		if (!mapExist.contains(100003) && StringUtil.isShowView(
				TotalDataByGSM.getHashMapValue(TotalDataByGSM.getInstance().getEvent(), TotalEvent._lacTry.name()))) {
			views.add(new TotalSecondGeEventView(this));
			mapExist.add(100003);
		}

		if (!mapExist.contains(100004) && StringUtil.isShowView(
				TotalDataByGSM.getHashMapValue(TotalDataByGSM.getInstance().getEvent(), TotalEvent._lteHandOverReq.name()))) {
			if (!mapExist.contains(100003)) {
				views.add(new TotalSecondGeEventView(this));
				mapExist.add(100003);
			}
			ScrollView scrollView = new ScrollView(this);
			scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			scrollView.setFillViewport(true);
			scrollView.addView(new TotalOneGeEventView(this));
			views.add(scrollView);
			mapExist.add(100004);
		}

		if (!mapExist.contains(100005) && CustomEventFactory.getInstance().getTotalEventList().size() > 0) {
			views.add(new TotalEventCustomView(this));
			mapExist.add(100005);
		}
		if (!mapExist.contains(100017) && NewMapFactory.getInstance().getAlarmList().size()>0){
            views.add(new TotalLAlarmParamView(this));
            mapExist.add(100017);
        }
		if (taskListHashMap != null) {
			for (HashMap.Entry<String, String> entry : taskListHashMap.entrySet()) {
				if (!this.applicationModel.isGeneralMode() && !mapExist.contains(10016)&& !ApplicationModel.getInstance().isNBTest()) {
					views.add(new TotalPPPView(this));
					mapExist.add(10016);
				}
				switch (TaskType.valueOf(entry.getValue())) {
				case PassivityCall:
				case InitiativeCall:
					if (testNetHashMap != null) {
						if (!mapExist.contains(100001) && testNetHashMap.containsKey(CurrentNetState.TDSCDMA.getNetTypeId())) {
							views.add(new TotalQualityTdView(this));
							mapExist.add(100001);
						}
						if (!mapExist.contains(100002) && testNetHashMap.containsKey(CurrentNetState.GSM.getNetTypeId())) {
							views.add(new TotalQualityGsmView(this));
							mapExist.add(100002);
						}
					}

					if (!mapExist.contains(10001)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalDialView(this));
						mapExist.add(10001);
						views.add(scrollView);
						views.add(new TotalDialVoLteView(this));
					}
					break;
				case Ping:
					if (!mapExist.contains(10002)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalPingView(this));
						mapExist.add(10002);
						views.add(scrollView);
					}
					break;
				case FTPDownload:
				case FTPUpload:
					if (!mapExist.contains(10003)) {
						views.add(new TotalFtpView(this));
						mapExist.add(10003);
					}
					break;
				case Http:
				case HttpDownload:
				case HttpRefurbish:
				case HttpUpload:
					if (!mapExist.contains(10004)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalHttpView(this));
						views.add(scrollView);
						mapExist.add(10004);
					}
					break;

				case EmailPop3:
				case EmailSmtp:
				case EmailSmtpAndPOP:
					if (!mapExist.contains(10005)) {
						views.add(new TotalEmailView(this));
						mapExist.add(10005);
					}
					break;
				case DNSLookUp:
					if (!mapExist.contains(10006)) {
						views.add(new TotalDNSView(this));
						mapExist.add(10006);
					}
					break;
				case SpeedTest:
					if (!mapExist.contains(10007)) {
						views.add(new TotalSpeedTestView(this));
						mapExist.add(10007);
					}
					break;
				case Facebook:
					if (!mapExist.contains(10008)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalFaceBookView(this));
						views.add(scrollView);
						mapExist.add(10008);
					}
					break;
				case TraceRoute:
					if (!mapExist.contains(10009)) {
						views.add(new TotalTraceRouteView(this));
						mapExist.add(10009);
					}
					break;
				case Attach:
					if (!mapExist.contains(10010)) {
						views.add(new TotalAttachView(this));
						mapExist.add(10010);
					}
					break;
				case PDP:
					if (!mapExist.contains(10011)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalPdpView(this));
						views.add(scrollView);
						mapExist.add(10011);
					}
					break;
				case PBM:
					if (!mapExist.contains(10012)) {
						views.add(new TotalPBMView(this));
						mapExist.add(10012);
					}
					break;
				case UDP:
					if (!mapExist.contains(10022)) {
						views.add(new TotalUDPView(this));
						mapExist.add(10022);
					}
						break;
					case OpenSignal:
						if (!mapExist.contains(10023)) {
							views.add(new TotalOpenSignalTestView(this));
							mapExist.add(10023);
						}
						break;
					case MultiHttpDownload:
						if (!mapExist.contains(10024)) {
							views.add(new TotalMultiHttpDownloadView(this));
							mapExist.add(10024);
						}
						break;
				case WeiBo:
					if (!mapExist.contains(10013)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalWeiBoView(this));
						views.add(scrollView);
						mapExist.add(10013);
					}
					break;
				case Stream:
					if (!mapExist.contains(10014)) {
						View page1 = new TotalVSView(this);
						View page2 = new TotalVSParaView(this);
						views.add(page1);
						views.add(page2);
						mapExist.add(10014);
					}
					break;
				case HTTPVS:
					if (!mapExist.contains(10015)) {
						View page1 = new TotalVideoKpiView(this);
						View page2 = new TotalVideoKriView(this);
						views.add(page1);
						views.add(page2);
						mapExist.add(10015);
					}
					break;
				case WapDownload:
				case WapLogin:
				case WapRefurbish:
					if (!mapExist.contains(10017)) {
						views.add(new TotalWapView(this));
						mapExist.add(10017);
					}
					break;
				case MMSIncept:
				case MMSSend:
				case MMSSendReceive:
					if (!mapExist.contains(10018)) {
						views.add(new TotalMmsView(this));
						mapExist.add(10018);
					}
					break;
				case SMSIncept:
				case SMSSend:
				case SMSSendReceive:
					if (!mapExist.contains(10019)) {
						views.add(new TotalSmsView(this));
						mapExist.add(10019);
					}
					break;
				case WeChat:
					if (!mapExist.contains(10020)) {
						ScrollView scrollView = new ScrollView(this);
						scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
						scrollView.setFillViewport(true);
						scrollView.addView(new TotalWeChatView(this));
						views.add(scrollView);
						mapExist.add(10020);
					}
					break;
					case WeCallMoc:
					case WeCallMtc:
					case SkypeChat:
					case SinaWeibo:
                    case WhatsAppChat:
                    case WhatsAppMoc:
                    case WhatsAppMtc:
                    case QQ:
                        if (!mapExist.contains(10021)){
                            views.add(new TotalOttView(this));
                            mapExist.add(10021);
                        }
						break;
				default:
					break;
				}
			}
		}

		adapter.notifyDataSetChanged(views);
		createSwitchImages();
		changeOtherSwitchImage(current);
		onCustomPageChangeListener = new OnCustomPageChangeListener();
		mPager.setOnPageChangeListener(onCustomPageChangeListener);
		exportExcel = (ImageButton) findViewById(R.id.export_excel);
		if (adapter.getCount()>0){
			exportExcel.setVisibility(View.VISIBLE);
		}else {
			exportExcel.setVisibility(View.GONE);
		}
		exportExcel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
						showExcelDialog();
			}
		});
	}

	private void showExcelDialog() {
		if (dialog==null){
			dialog=new BasicDialog.Builder(getParent()).setMessage(getString(R.string.dialog_message_total_into_excel))
					.setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						    new  Thread(new Runnable() {
                                @Override
                                public void run() {
                                    TotalManger.getInstance(mContext).totalIntoExcel();
                                }
                            }).start();
						}
					});
		}
		dialog.show();
	}

	private List<ImageView> switchImages = new ArrayList<ImageView>();

	public void createSwitchImages() {
		switchImages.clear();
		switchLayout.removeAllViews();
		for (int i = 0; i < views.size(); i++) {
			ImageView img = new ImageView(mContext);
			img.setImageResource(R.drawable.darkdot);
			switchImages.add(img);
			switchLayout.addView(img);
			if (i < views.size() - 1) {
				ImageView imgDivider = new ImageView(mContext);
				imgDivider.setImageResource(R.drawable.img_switch_divider);
				switchLayout.addView(imgDivider);
			}
		}
	}

	/**
	 * 改变除当前页外的其他的图片的状态
	 * 
	 * @param current
	 */
	private void changeOtherSwitchImage(int current) {
		for (int i = 0; i < views.size(); i++) {
			if (i != current) {
				switchImages.get(i).setImageResource(R.drawable.darkdot);
			} else {
				switchImages.get(i).setImageResource(R.drawable.lightdot);
			}
		}
	}

	private int current;

	public class OnCustomPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			current = position;
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case ACTION_WALKTOUR_TIMER_CHANGED:
			initView();
			break;

		default:
			break;
		}

	}

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(WalkMessage.ACTION_WALKTOUR_START_TEST)) {
				if (applicationModel.getCurrentTestTaskList() != null) { // 清空测试任务状态,用于统计
					applicationModel.getCurrentTestTaskList().clear();
				}
				if (applicationModel.getCurrentTestNet() != null) { // 清空网络集合状态,用于统计
					applicationModel.getCurrentTestNet().clear();
				}
			}
		};
	};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefreshEventManager.removeRefreshListener(this);
    }
}

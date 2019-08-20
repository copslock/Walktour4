package com.walktour.gui.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.dingli.wlan.apscan.WifiScanner;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivityGroup;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;

import java.util.ArrayList;
import java.util.List;

public class ParamActivity2  extends BasicActivityGroup implements
LayoutChangeListener, OnClickListener, OnTabChangeListener, RefreshEventListener {

	public int mCurScreen = 0;

	public TabHost mTabHost;
	
	private List<Button> tabs = new ArrayList<Button>();
	private List<ImageView> tabImgs = new ArrayList<ImageView>();

	/** 参数界面实际展示列表项	 */
	private List<ParamTab> paramTabList = new ArrayList<ParamTab>();
	/**要显示的tab*/
	private List<ParamTab> paramTabListToShow = new ArrayList<ParamTab>();

	private SharedPreferences mPreferences;

	/** 滑动位置标识图片	 */
	protected ImageView scrollTag;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE );
		WifiScanner.instance(this);
        RefreshEventManager.addRefreshListener(this);
        byNetWorkTocurrentTab();
        initView();
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		
	}
	
	@SuppressWarnings("deprecation")
	private void initView() {
		setContentView(R.layout.info_param);
		mTabHost = (TabHost) findViewById(R.id.child_tabhost);
		mTabHost.setup(this.getLocalActivityManager());
		scrollTag = initImageView(R.id.img1);
		tabs.clear();
		tabImgs.clear();
		tabs.add(initButton(R.id.tab1));
		tabs.add(initButton(R.id.tab2));
		tabs.add(initButton(R.id.tab3));
		tabs.add(initButton(R.id.tab4));
		tabs.add(initButton(R.id.tab5));
		tabImgs.add(initImageView(R.id.img1));
		tabImgs.add(initImageView(R.id.img2));
		tabImgs.add(initImageView(R.id.img3));
		tabImgs.add(initImageView(R.id.img4));
		tabImgs.add(initImageView(R.id.img5));
		for (int i = 0; i < tabs.size(); i++) {
			tabs.get(i).setVisibility(View.GONE);
			tabImgs.get(i).setVisibility(View.GONE);
			((LinearLayout)tabs.get(i).getParent()).setVisibility(View.GONE);
			tabs.get(i).setOnClickListener(this);
		}

		ParamTab paramTab = new ParamTab(getString(R.string.info_parm),
				new Intent(ParamActivity2.this, DynamicParamInfo.class));
		paramTabList.add(paramTab);
		ParamTab evenParm = new ParamTab(getString(R.string.info_event), 
				new Intent(ParamActivity2.this, Event.class));
		paramTabList.add(evenParm);
		// ParamTab l3msgParm = new ParamTab(getString(R.string.info_signal), new
		// Intent(ParamActivity2.this, L3Msg.class));
		ParamTab l3msgParm = new ParamTab(getString(R.string.info_signal),
				new Intent(ParamActivity2.this, SignalActivity.class));
		paramTabList.add(l3msgParm);
		ParamTab vsParm = new ParamTab(getString(R.string.info_application),
				new Intent(ParamActivity2.this, VideoRealPara.class));
		paramTabList.add(vsParm);
		ParamTab analysisParm = new ParamTab(getString(R.string.info_analysis),
				new Intent(ParamActivity2.this, AnalysisActivity.class));
		paramTabList.add(analysisParm);
		// setTabBtnText();
		initTab();
		updateBtnColor(mCurScreen);
	}
	
	/**
	 * 初始化要显示的窗口
	 */
	private void initTab() {
		paramTabListToShow.clear();
		List<String> win = this.getAllWindows();
		List<String> paramText = new ArrayList<String>();
		String[] paramTextArray = new String[win.size()];
		for (int i = 0; i < win.size(); i++) {
			String name = win.get(i).split(":")[0];
			int position = mPreferences.getInt("SORT_" + getTab(name), i);
			paramTextArray[position] = name;
		}
		for (int i = 0; i < paramTextArray.length; i++) {
			paramText.add(paramTextArray[i]);
		}
		for (int i = 0; i < paramText.size(); i++) {
			String tag="display_" + getTab(paramText.get(i));
			boolean ischecked = mPreferences.getBoolean(tag, true);
			if (ischecked) {
				//特殊处理,如果是Wifi测试,默认关闭信令,视频，分析这三界面
				if(TaskListDispose.getInstance().isWlanTest() && ApplicationModel.getInstance().isTestJobIsRun()){
					if(tag.equals("display_tab2")||tag.equals("display_tab3")||tag.equals("display_tab4")){
						continue;
					}
				}
				//如果是通用版本，默认关闭信令、分析这两个界面
				if(ApplicationModel.getInstance().isGeneralMode()){
					if(tag.equals("display_tab2")||tag.equals("display_tab4")){
						continue;
					}
				}
				//如果是北京测试项目，默认关闭视频、分析这两个界面
				if(ApplicationModel.getInstance().isBeiJingTest()){
					if(tag.equals("display_tab3")||tag.equals("display_tab4")){
						continue;
					}
				}
				paramTabListToShow.add(getParamTab(paramText.get(i)));
			}
		}
		setTabBtnText(paramTabListToShow);
	}
	
	/**
	 * 根据名字获取标签页
	 * @param name
	 * @return
	 */
	private ParamTab getParamTab(String name) {
		ParamTab paramTab = new ParamTab("", null);
		for (int i = 0; i < paramTabList.size(); i++) {
			if (paramTabList.get(i).paramBtnText.equals(name)) {
				paramTab =  paramTabList.get(i);
				break;
			}
		}
		return paramTab;
	}
	
	/**
	 * 获取所有窗口
	 * @return
	 * @author MSI
	 */
	private List<String> getAllWindows() {
		List<String> windows = new ArrayList<String>();
		String[] tmps = getResources().getStringArray(R.array.array_custom_window);
		for (int i = 0; i < tmps.length; i++) {
			String str = tmps[i] + ":" + "tab" + i;
			windows.add(str);
		}
		return windows;
	}
	
	/**
	 * 获取对应的标签
	 * @param name
	 * @return
	 */
	private String getTab(String name) {
		String tab = "";
		List<String> list = this.getAllWindows();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains(name)) {
				return list.get(i).split(":")[1];
			}
		}
		return tab;
	}
	 
	
	@SuppressWarnings("deprecation")
	private void setTabBtnText(List<ParamTab> paramTabList) {
		for (int i = 0; i < paramTabList.size(); i++) {
			ParamTab paramTab = paramTabList.get(i);
			if(paramTab.intent != null){
			tabs.get(i).setVisibility(View.VISIBLE);
			((LinearLayout)tabs.get(i).getParent()).setVisibility(View.VISIBLE);
			tabImgs.get(i).setVisibility(View.VISIBLE);
			tabs.get(i).setText(paramTab.paramBtnText);
			Button btn = new Button(this);
			btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.background_mybutton));
			btn.setPadding(3, 5, 3, 5);
			btn.setText(paramTab.paramBtnText);
			btn.setTextColor(getResources().getColor(R.color.white));
				if (paramTab.intent instanceof Intent) {
					mTabHost.addTab(mTabHost.newTabSpec(paramTab.paramBtnText)
							.setIndicator(btn).setContent((Intent) paramTab.intent));
				} else {
					mTabHost.addTab(mTabHost.newTabSpec(paramTab.paramBtnText)
							.setIndicator(btn)
							.setContent((Integer) paramTab.intent));
				}
			}
			

		}
		mTabHost.setOnTabChangedListener(this);
	}
	


	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equals(getString(R.string.info_event))) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Event;
		} else if (tabId.equals(getString(R.string.info_signal))) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.L3Msg;
		}
		InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(ParamActivity2.this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < tabs.size(); i++) {
			if (v == tabs.get(i)) {
				snapToScreen(i);
				mTabHost.setCurrentTab(i);
				break;
			}
		}
	}

	@Override
	public void doChange(int lastIndex, int currentIndex) {

	}
	
	/**
	 * 动态更新按钮颜色
	 */

	private void updateBtnColor(int mCurScreen) {
        TabHostUtil.updateBtnColor(this,tabs,mTabHost,mCurScreen);
	}


	public void snapToScreen(int curScreen) {
//		if (mCurScreen != curScreen) {
//			if (tabs.get(curScreen) != null) {
//				tabs.get(curScreen).setTextColor(
//						getResources().getColor(R.color.csfb_delay_color));
//			}
//		}
		mCurScreen = curScreen;
//		doChange(mCurScreen, curScreen);
		updateBtnColor(mCurScreen);
	}
	
    /**
     * 当前显示页面所属网络组是否改变
     * @param showType
     * @return
     */
    private boolean currentGroupChange(ShowInfoType showType){
        return TraceInfoInterface.currentShowChildTab.getNetGroup() != showType.getNetGroup();
    }
	
	 /**
     * 根据当前网络，判断Tab标签应该默认为哪一个<BR>
     * [功能详细描述]
     */
    private void byNetWorkTocurrentTab(){
    	//twq20130724 当前显示页不在WLAN下才执行网络切换时的
		if (TraceInfoInterface.currentShowChildTab != WalkStruct.ShowInfoType.WLAN) {
			int networkType = MyPhoneState.getInstance().getNetWorkType(
					ParamActivity2.this);
			switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
			case TelephonyManager.NETWORK_TYPE_EDGE:
				if (currentGroupChange(ShowInfoType.Gsm)) {
					TraceInfoInterface.currentShowChildTab = ShowInfoType.Gsm;
				}
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				if (currentGroupChange(ShowInfoType.Umts)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
				}
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case 15: // HSPA+
				if (Deviceinfo.getInstance().getNettype() == NetType.TDSCDMA
						.getNetType()
						|| Deviceinfo.getInstance().getNettype() == NetType.LTETDD
								.getNetType()) {
					if (currentGroupChange(ShowInfoType.TDSCDMA)) {
						TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
					}
				} else {
					if (currentGroupChange(ShowInfoType.Umts)) {
						TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Umts;
					}
				}
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				if (currentGroupChange(ShowInfoType.Cdma)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Cdma;
				}
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
			case 12: // TelephonyManager.NETWORK_TYPE_EVDO_B:
				if (currentGroupChange(ShowInfoType.EvDo)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.EvDo;
				}
				break;
			case 13:
				if (currentGroupChange(ShowInfoType.LTE)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.LTE;
				}
				break;
			case 14: // EHRPD
				break;
			case 16:
			case 17:
				if (currentGroupChange(ShowInfoType.TDSCDMA)) {
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TDSCDMA;
				}
				break;
			}
		}
    }

	private class ParamTab {
		/**
		 * [构造简要说明]
		 * 
		 * @param paramBtnText
		 * @param intent
		 */
		public ParamTab(String paramBtnText, Object intent) {
			this.paramBtnText = paramBtnText;
			this.intent = intent;
		}

		public String paramBtnText;

		public Object intent;

	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefreshEventManager.removeRefreshListener(this);
    }
}

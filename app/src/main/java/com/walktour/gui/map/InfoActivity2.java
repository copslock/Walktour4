package com.walktour.gui.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.ui.BasicActivityGroup;
import com.walktour.framework.view.LayoutChangeListener;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity2 extends BasicActivityGroup implements
		LayoutChangeListener, OnClickListener, OnTabChangeListener {

	public int mCurScreen = 0;

	public TabHost mTabHost;

	public Button tab1;

	public Button tab2;

	List<Button> tabs = new ArrayList<Button>();

	/**
	 * 参数界面实际展示列表项
	 */
	private List<ParamTab> paramTabList = new ArrayList<ParamTab>();

	private SharedPreferences mPreferences;

	// 滑动动画执行时间
	private final int DELAY_TIME = 500;

	/**
	 * 滑动位置标识图片
	 */
	protected ImageView scrollTag;

	private Button tab3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();

	}

	private void initView() {
		setContentView(R.layout.info_info2);
		ApplicationModel appModel = ApplicationModel.getInstance();
		mTabHost = (TabHost) findViewById(R.id.child_tabhost);
		mTabHost.setup(this.getLocalActivityManager());
		scrollTag = initImageView(R.id.img1);
		tab1 = initButton(R.id.tab1);
		tab2 = initButton(R.id.tab2);
		tab3 = initButton(R.id.tab3);
		tabs.add(tab1);
		tabs.add(tab2);
		tabs.add(tab3);
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);

		ParamTab dataParm = new ParamTab(getString(R.string.info_data),
				new Intent(InfoActivity2.this, DataDashboardActivity.class));
		paramTabList.add(dataParm);
		ParamTab chartParm = new ParamTab(getString(R.string.info_chart),
				new Intent(InfoActivity2.this, Chart.class));
		paramTabList.add(chartParm);
		ParamTab total = new ParamTab(getString(R.string.total_total),
				new Intent(InfoActivity2.this, TotalReal.class));
		paramTabList.add(total);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		setTabBtnText();
		updateBtnColor(mCurScreen);
	}

	public void setTabBtnText() {
		for (int i = 0; i < paramTabList.size(); i++) {
			ParamTab paramTab = paramTabList.get(i);
			tabs.get(i).setText(paramTab.paramBtnText);
			Button btn = new Button(this);
			btn.setBackgroundResource(R.drawable.background_mybutton);
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
		mTabHost.setOnTabChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == tab1) {
			snapToScreen(0);
			mTabHost.setCurrentTab(0);
		} else if (v == tab2) {
			snapToScreen(1);
			mTabHost.setCurrentTab(1);
		} else if(v == tab3){
			snapToScreen(2);
			mTabHost.setCurrentTab(2);
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


	@Override
	protected void onResume() {
		super.onResume();
		Log.i("--", "info");
		Message msg = new Message();
		msg.what = -1;
		initSelectTabHandle.sendMessageDelayed(msg, DELAY_TIME);

	}

	/**
	 * 初始化选中Tab覆盖图片的Handler
	 */
	private Handler initSelectTabHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (TraceInfoInterface.currentShowChildTab) {
			case Chart:
				mTabHost.setCurrentTabByTag(getResources().getString(
						R.string.info_chart));
				snapToScreen(mTabHost.getCurrentTab());
				break;
			case Data:
				mTabHost.setCurrentTabByTag(getResources().getString(
						R.string.info_data));
				snapToScreen(mTabHost.getCurrentTab());
				break;
			case Total:
				mTabHost.setCurrentTabByTag(getResources().getString(
						R.string.total_total));
				snapToScreen(mTabHost.getCurrentTab());
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void snapToScreen(int curScreen) {
//		doChange(mCurScreen, curScreen);
//
//		if (mCurScreen != curScreen) {
//			if (tabs.get(curScreen) != null) {
//				tabs.get(curScreen).setTextColor(
//						getResources().getColor(R.color.csfb_delay_color));
//			}
//		}
		mCurScreen = curScreen;
		updateBtnColor(mCurScreen);
	}

	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equals(getString(R.string.info_chart))) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Chart;
		} else if (tabId.equals(getString(R.string.info_data))) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Data;
		} else if (tabId.equals(getString(R.string.total_total))) {
			TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Total;
		}
		InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(InfoActivity2.this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		getLocalActivityManager().getCurrentActivity().onKeyUp(keyCode, event);
		return super.onKeyUp(keyCode, event);
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
}

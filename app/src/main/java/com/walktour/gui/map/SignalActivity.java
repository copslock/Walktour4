package com.walktour.gui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.CustomPagerAdapter;
import com.walktour.framework.view.TotalScrollTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.eventbus.OnL3MsgMenuSelectedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SignalActivity  extends TotalScrollTabActivity {

	private Context mContext;
	private ViewPager mPager;
	private CustomPagerAdapter adapter;
	private List<View> views = new ArrayList<View>();
	private LinearLayout switchLayout;
	private OnCustomPageChangeListener onCustomPageChangeListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.analysis_activity);
		mContext = this;
		initView();
	}

	private void initView() {
		mPager = (ViewPager)findViewById(R.id.viewPager);
		switchLayout = (LinearLayout)findViewById(R.id.switch_layout);
		
		//pdfView
		Intent l3MsgIntent = new Intent(this, L3Msg.class);
		View pdfView = getLocalActivityManager().startActivity("L3Msg", l3MsgIntent).getDecorView();
		views.add(pdfView);
		//TCP/IP
		Intent tcpIpIntent = new Intent(this, TcpIpListActivity.class);
		View tcpIpView = getLocalActivityManager().startActivity("TcpIp", tcpIpIntent).getDecorView();
		views.add(tcpIpView);
		//TODO 增加其他界面
		
		adapter = new CustomPagerAdapter(views);
		mPager.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		onCustomPageChangeListener = new OnCustomPageChangeListener();
		mPager.setOnPageChangeListener(onCustomPageChangeListener);
		mPager.setCurrentItem(0);
		onCustomPageChangeListener.changeOtherSwitchImage(0);
	}
	
	
public class OnCustomPageChangeListener implements OnPageChangeListener {
		
		private List<ImageView> switchImages = new ArrayList<ImageView>();
		
		public OnCustomPageChangeListener() {

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
		
		

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			changeOtherSwitchImage(position);
			switchImages.get(position).setImageResource(R.drawable.lightdot);
			if (position == 0) {
				TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.L3Msg;
			} else {
				TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.TcpIpPcap;
			}
		}
		
		/**
		 * 改变除当前页外的其他的图片的状态
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
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			this.getLocalActivityManager().getActivity("L3Msg").openOptionsMenu();
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 接收查看信息-》更多下拉的menu点击事件
	 * @param event
	 */
	@Subscribe
	public void onReceiveL2MsgMenuSelectedEvent(OnL3MsgMenuSelectedEvent event) {
		if(null != event){
			try{
				L3Msg l3MsgActivity = (L3Msg) this.getLocalActivityManager().getActivity("L3Msg");
				l3MsgActivity.onReceiveL2MsgMenuSelectedEvent(event);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		LogUtil.d("SignalActivity","----onResume----");
		super.onResume();
		TraceInfoInterface.sIsOnL3Msg = true;
		EventBus.getDefault().register(this);
		try{
			L3Msg l3MsgActivity = (L3Msg) this.getLocalActivityManager().getActivity("L3Msg");
			if(null != l3MsgActivity && !l3MsgActivity.isFinishing()){
				l3MsgActivity.enableKeywordChanged();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.d("SignalActivity","----onPause----");
		TraceInfoInterface.sIsOnL3Msg = false;
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
		try{
			L3Msg l3MsgActivity = (L3Msg) this.getLocalActivityManager().getActivity("L3Msg");
			if(null != l3MsgActivity && !l3MsgActivity.isFinishing()){
				l3MsgActivity.closePopWindow();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}

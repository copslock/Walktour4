package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.control.adapter.CustomFragmentPagerAdapter;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 动态参数显示界面
 * 
 * @author zhihui.lian
 */
@SuppressLint("UseSparseArrays")
public class DynamicParamInfo extends FragmentActivity implements RefreshEventListener {

	private ViewPager mPager;
	private LinearLayout switchLayout;
	private OnCustomPageChangeListener onCustomPageChangeListener;
	private Context mContext;
	private ParameterSetting mParameterSet;
	private Parameter[] parameterArray; // 根据网络获取参数
	private String netTypeStr;
	/** 实时参数 */
	private Fragment mPageParams;
	/** 小区列表 */
	private Fragment mPageCellList;
	private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();
	private List<Fragment> otherPage = new ArrayList<Fragment>();
	private CustomFragmentPagerAdapter adapter;
	private List<ImageView> switchImages = new ArrayList<ImageView>();
	private List<ImageView> switchImgDividers = new ArrayList<ImageView>();
	private Map<Integer, String> map = new HashMap<Integer, String>(); // 区分唯一

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic_param_info);

		// 监听测试情况
		// IntentFilter filter3 = new IntentFilter();
		// filter3.addAction(WalkMessage.ACTION_WALKTOUR_START_TEST);
		// filter3.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		// filter3.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
		// registerReceiver(testJobDoneReceiver, filter3);

		mContext = this;
		mParameterSet = ParameterSetting.getInstance();
		mPager = (ViewPager) findViewById(R.id.viewPager);
		mPager.setOffscreenPageLimit(7);
		switchLayout = (LinearLayout) findViewById(R.id.switch_layout);
		initView();
		RefreshEventManager.addRefreshListener(this);
	}

	private void initView() {
		mPager.removeAllViews();
		switchImages.add(new ImageView(mContext));
		switchImages.add(new ImageView(mContext));
		ImageView imgDivider = new ImageView(mContext);
		imgDivider.setImageResource(R.drawable.img_switch_divider);
		switchImgDividers.add(imgDivider);
		if (ApplicationModel.getInstance().isGeneralMode())
			mPageParams = new DynamicGeneralParamterFragment(mContext);
		else
			mPageParams = new DynamicParamterFragment(mContext);
		fragmentsList.add(mPageParams);
		if (!ApplicationModel.getInstance().isGeneralMode()) {
			mPageCellList = new DynamicCellInfoFragment(mContext);
			fragmentsList.add(mPageCellList);
		}
		adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList);
		mPager.setAdapter(adapter);
		mPager.setCurrentItem(0);
		onCustomPageChangeListener = new OnCustomPageChangeListener();
		onCustomPageChangeListener.getSwitchImages().clear();
		onCustomPageChangeListener.getSwitchImages().addAll(switchImages);
		mPager.setOnPageChangeListener(onCustomPageChangeListener);
		mPager.setCurrentItem(0);
		onCustomPageChangeListener.changeOtherSwitchImage(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case ACTION_WALKTOUR_TIMER_CHANGED:

			getParamData();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取参数数据信息
	 */
	private synchronized void getParamData() {
		if (!TraceInfoInterface.currentNetType.name().equals(netTypeStr)) {
			map.clear();
			otherPage.clear();
			synchronized (map) {
				netTypeStr = TraceInfoInterface.currentNetType.name();
				parameterArray = mParameterSet.getTableParametersByNetworkType(netTypeStr);
				for (int i = 0; i < parameterArray.length; i++) {
					int page = parameterArray[i].getTabIndex();
					if ((page > 2)) {
						if (!map.containsKey(page)) {
							Log.i("-----", "----" + page);
							otherPage.add(new DynamicParamterOtherFragment(mContext, page));
							switchImages.add(new ImageView(mContext));
							ImageView imgDivider = new ImageView(mContext);
							imgDivider.setImageResource(R.drawable.img_switch_divider);
							switchImgDividers.add(imgDivider);
						}
						map.put(page, "");
					}
				}
				refreshView();
			}
		}

	}

	private void refreshView() {
		if (adapter == null)
			return;
		switchLayout.removeAllViews();
		adapter.getFragments().clear();
		fragmentsList.add(mPageParams);
		if (!ApplicationModel.getInstance().isGeneralMode()) {
			fragmentsList.add(mPageCellList);
			Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();
			int count = 0;
			while (it.hasNext()) {
				Entry<Integer, String> entry = it.next();
				System.out.println("-----key= " + entry.getKey());
				fragmentsList.add(otherPage.get(count));
				count++;
			}
		}
		onCustomPageChangeListener.getSwitchImages().clear();
		for (int i = 0; i < fragmentsList.size(); i++) {
			ImageView img = switchImages.get(i);
			onCustomPageChangeListener.getSwitchImages().add(img);
			switchLayout.addView(img);
			if (i < fragmentsList.size() - 1) {
				ImageView imgDivider = switchImgDividers.get(i);
				switchLayout.addView(imgDivider);
			}
		}
		adapter.notifyDataSetChanged();
		if (adapter.getCount() == 2) {
			mPager.setCurrentItem(0);
		}
	}

	// ===============================================================================================================================
	public class OnCustomPageChangeListener implements OnPageChangeListener {

		private List<ImageView> switchImages = new ArrayList<ImageView>();

		public OnCustomPageChangeListener() {
		}

		public List<ImageView> getSwitchImages() {
			return this.switchImages;
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

		}

		/**
		 * 改变除当前页外的其他的图片的状态
		 * 
		 * @param current
		 */
		private void changeOtherSwitchImage(int current) {
			for (int i = 0; i < fragmentsList.size(); i++) {
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

}

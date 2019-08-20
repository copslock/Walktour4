package com.walktour.gui.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.OnTabActivityResultListener;
import com.walktour.framework.view.ScroollTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.GoogleMapMainActivity;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.offline.baidu.BaiduMapOfflineActivity;
import com.walktour.gui.newmap2.IMapTab;
import com.walktour.gui.newmap2.NewMapActivity;
import com.walktour.service.automark.LiftAutoMarkActivity;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;

/**
 * [查看信息的地图展示页]
 * 
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-8]
 */
@SuppressWarnings("deprecation")
public class MapTabActivity extends ScroollTabActivity implements OnTabChangeListener,IMapTab {
	private ApplicationModel appModel;
	private static final String TAG = "MapTabActivity";
	/**
	 * [初始化控件]
	 * @see com.walktour.framework.view.ScroollTabActivity#initView()
	 */
	@Override
	public void initView() {
		setContentView(R.layout.maptab_activity);
		mTabHost = (TabHost) findViewById(R.id.child_tabhost);
		mTabHost.setup(this.getLocalActivityManager());
		appModel = ApplicationModel.getInstance();
		tab1 = initButton(R.id.tab1);
		tab2 = initButton(R.id.tab2);

		scrollTag = initImageView(R.id.top_bar_select);
		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.OtherMap.toString())
				.setIndicator(WalkStruct.ShowInfoType.OtherMap.toString())
				.setContent(new Intent(MapTabActivity.this, MapActivity.class).putExtra("showInnsMap", false)));
		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.GoogleMap.toString())
				.setIndicator(WalkStruct.ShowInfoType.GoogleMap.toString())
				.setContent(new Intent(MapTabActivity.this, GoogleMapMainActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.AutoMark.toString())
				.setIndicator(WalkStruct.ShowInfoType.AutoMark.toString())
				.setContent(new Intent(MapTabActivity.this, LiftAutoMarkActivity.class)));
/*		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.BaiduMap.toString())
				.setIndicator(WalkStruct.ShowInfoType.BaiduMap.toString())
				.setContent(new Intent(MapTabActivity.this, BaiduMapMainActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.AMap.toString())
                .setIndicator(WalkStruct.ShowInfoType.AMap.toString())
                .setContent(new Intent(MapTabActivity.this, BaiduMapMainActivity.class)));*/
//              2018/7/12 切换新地图
		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.BaiduMap.toString())
				.setIndicator(WalkStruct.ShowInfoType.BaiduMap.toString())
				.setContent(new Intent(MapTabActivity.this, NewMapActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.AMap.toString())
				.setIndicator(WalkStruct.ShowInfoType.AMap.toString())
				.setContent(new Intent(MapTabActivity.this, NewMapActivity.class)));

		mTabHost.addTab(mTabHost.newTabSpec(WalkStruct.ShowInfoType.InnsMap.toString())
				.setIndicator(WalkStruct.ShowInfoType.InnsMap.toString())
				.setContent(new Intent(MapTabActivity.this, MapActivity.class).putExtra("showInnsMap", true)));
		LogUtil.i("地图默认值", "isGerenalTest()>>" + this.appModel.isGerenalTest() + "  isIndoorTest() "
				+ this.appModel.isIndoorTest() + " isTestJobIsRun() " + this.appModel.isTestJobIsRun());
		mTabHost.setOnTabChangedListener(this);


		if (!this.appModel.isGerenalTest() && !this.appModel.isIndoorTest() && this.appModel.isTestJobIsRun()) {
			String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
			LogUtil.d(TAG,"默认地图:"+ParameterSetting.getInstance().getDtDefaultMap());
			if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[0])) {//MIF也放在百度图层上
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
			} else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[1])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.GoogleMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
			} else if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
				//TODO 高德地图暂时使用百度地图呈现
			} else if (dtmap.length > 3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.AMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
			}else if (dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
			}
		} else if (this.appModel.getSelectScene() == SceneType.Metro) {
			String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
			if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[0])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
			} else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[1])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.GoogleMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
			} else if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
				//TODO 高德地图暂时使用百度地图呈现
			} else if (dtmap.length >3  && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.AMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
			}else if (dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
			}
		} else if ((this.appModel.isGerenalTest() || this.appModel.isIndoorTest()) && this.appModel.isTestJobIsRun()) {
			if (!this.appModel.isInnsmapTest()) {
				//如果是室内并且是外置陀螺仪打点的时候
				if (AutoMarkConstant.markScene!= MarkScene.COMMON){
					mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.AutoMark.toString());
					return;
				}
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.OtherMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
			} else {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.InnsMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.InnsMap;
			}
		} else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.GoogleMap)) {
			LogUtil.i("谷歌地图默认值", "**************************");
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.GoogleMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
		} else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.BaiduMap)) {
			LogUtil.i("百度地图默认值", "**************************");
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
		} else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.AMap)) {
			LogUtil.i("高德地图默认值", "**************************");
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.AMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
		} else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.OtherMap)) {
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.OtherMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
		} else if (TraceInfoInterface.currentMapChildTab.equals(WalkStruct.ShowInfoType.InnsMap)) {
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.InnsMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.InnsMap;
		}else {//默认百度地图加载
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
		}



	}

	/**
	 * 切换地图<BR>
	 * @param showInfoType
	 *          地图类型 1:离线 2：在线 3:其他地图
	 */
	@Override
	public void swicthMap(WalkStruct.ShowInfoType showInfoType) {
		if (showInfoType.equals(WalkStruct.ShowInfoType.OfflineMap)) {
			String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
			if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[1])) {
				Intent intent = new Intent(MapTabActivity.this, OfflineMapActivity.class);
				startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
			} else if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
				Intent intent = new Intent(MapTabActivity.this, BaiduMapOfflineActivity.class);
				startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
				//TODO 高德地图暂时使用百度地图呈现
			} else if (dtmap.length > 3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
				Intent intent = new Intent(MapTabActivity.this, BaiduMapOfflineActivity.class);
				startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
			}else {
				Intent intent = new Intent(MapTabActivity.this, BaiduMapOfflineActivity.class);
				startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
				//TODO 高德地图暂时使用百度地图呈现
			}
		} else if (showInfoType.equals(WalkStruct.ShowInfoType.OnlineMap)) {
			appModel.in2out(this);
			onActivityResult(BaseMapActivity.ONLINE_MAP_RESULT_CODE, BaseMapActivity.ONLINE_MAP_RESULT_CODE, new Intent());
		} else if (showInfoType.equals(WalkStruct.ShowInfoType.GoogleMap)) {
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.GoogleMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
		} else if (showInfoType.equals(WalkStruct.ShowInfoType.BaiduMap)) {
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
		} else if (showInfoType.equals(WalkStruct.ShowInfoType.AMap)) {
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.AMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
		}  else if (showInfoType.equals(WalkStruct.ShowInfoType.InnsMap)) {
			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.InnsMap.toString());
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.InnsMap;
			/**
			@anthor  jinfeng.xie
			室外转室内地图的时候就是Map ,这个功能就是联通招标，禅道任务233：
			（6）	室内测试支持手动打点。
			支持打点图放大、缩小、平移，参数轨迹图例设置、图例显示和实际测量值随轨迹显示等功能。支持手动打点与GPS打点的切换功能。
			*/
		}  else if (showInfoType.equals(WalkStruct.ShowInfoType.Map)&&appModel.isInOutSwitchMode()) {
			appModel.out2in(this);
//			mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.InnsMap.toString());
//			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.InnsMap;
		} else {
			if (!mTabHost.getCurrentTabTag().equals(WalkStruct.ShowInfoType.OtherMap.toString())) {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.OtherMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			if (requestCode == BaseMapActivity.OFFLINE_MAP_RESULT_CODE
					|| requestCode == BaseMapActivity.ONLINE_MAP_RESULT_CODE) {
				String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
				if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[1])) {
					mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.GoogleMap.toString());
					TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
				} else if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
					mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
					TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
					//TODO 高德地图暂时使用百度地图呈现
				} else if (dtmap.length >3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
					mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.AMap.toString());
					TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
				}else if (dtmap.length >0 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[0])) {//MIF
					mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
					TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
				} else if (dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
					mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.BaiduMap.toString());
					TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
				}
			} else {
				mTabHost.setCurrentTabByTag(WalkStruct.ShowInfoType.OtherMap.toString());
				TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
			}
			// 获取当前活动的Activity实例
			Activity subActivity = getLocalActivityManager().getCurrentActivity();
			// 判断是否实现返回值接口
			if (subActivity instanceof OnTabActivityResultListener) {
				// 获取返回值接口实例
				OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
				// 转发请求到子Activity
				listener.onTabActivityResult(requestCode, resultCode, data);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equals(WalkStruct.ShowInfoType.OtherMap.toString())) {
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.OtherMap;
		} else if (tabId.equals("GoogleMap")) {
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.GoogleMap;
		} else if (tabId.equals("BaiduMap")) {
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.BaiduMap;
		} else if (tabId.equals("AMap")) {
			TraceInfoInterface.currentMapChildTab = WalkStruct.ShowInfoType.AMap;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		LogUtil.i("MapTabActivity", "onSaveInstanceState");
		// No call for super(). Bug on API Level > 11.
	}

    @Override
    protected void onDestroy() {
        getLocalActivityManager().destroyActivity(WalkStruct.ShowInfoType.BaiduMap.name(),true);
        getLocalActivityManager().destroyActivity(WalkStruct.ShowInfoType.GoogleMap.name(),true);
        getLocalActivityManager().destroyActivity(WalkStruct.ShowInfoType.OtherMap.name(),true);
        getLocalActivityManager().destroyActivity(WalkStruct.ShowInfoType.InnsMap.name(),true);
	    super.onDestroy();
    }
}

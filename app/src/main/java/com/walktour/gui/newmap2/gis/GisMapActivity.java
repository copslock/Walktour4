package com.walktour.gui.newmap2.gis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.framework.view.OnTabActivityResultListener;
import com.walktour.gui.R;
import com.walktour.gui.map.MapActivity;
import com.walktour.gui.map.OfflineMapActivity;
import com.walktour.gui.map.googlemap.GoogleMapMainActivity;
import com.walktour.gui.newmap.BaseMapActivity;
import com.walktour.gui.newmap.offline.baidu.BaiduMapOfflineActivity;
import com.walktour.gui.newmap2.IMapTab;
import com.walktour.gui.newmap2.NewMapActivity;
import com.walktour.service.automark.LiftAutoMarkActivity;
import com.walktour.service.automark.constant.AutoMarkConstant;
import com.walktour.service.automark.constant.MarkScene;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author zhicheng.chen
 * @date 2018/11/18
 * gis 分析
 */
public class GisMapActivity extends BasicTabActivity implements IMapTab, TabHost.OnTabChangeListener {

    private final WalkStruct.ShowInfoType aMap = WalkStruct.ShowInfoType.AMap;
    private final WalkStruct.ShowInfoType otherMap = WalkStruct.ShowInfoType.OtherMap;
    private final WalkStruct.ShowInfoType innsMap = WalkStruct.ShowInfoType.InnsMap;
    private final WalkStruct.ShowInfoType baiduMap = WalkStruct.ShowInfoType.BaiduMap;
    private final WalkStruct.ShowInfoType googleMap = WalkStruct.ShowInfoType.GoogleMap;
    private final WalkStruct.ShowInfoType autoMark = WalkStruct.ShowInfoType.AutoMark;

    private final String aMapTag = aMap.toString();
    private final String baiduMapTag = baiduMap.toString();
    private final String googleMapTag = googleMap.toString();
    private final String otherMapTag = otherMap.toString();
    private final String innsMapTag = innsMap.toString();
    private final String autoMarkTag = autoMark.toString();


    private ApplicationModel appModel;
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_map);
        ButterKnife.bind(this);
        appModel = ApplicationModel.getInstance();
        initView();
    }

    @OnClick(R.id.ib_back)
    void back() {
        GisMapDataHolder.get().clearAllData();
        finish();
    }

    public void initView() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(getLocalActivityManager());




        Intent intent = new Intent(this, NewMapActivity.class);
        intent.putExtra(NewMapActivity.EXTRA_FROM, NewMapActivity.FROM_GIS_ANALYSIS);

        // new map
        mTabHost.addTab(mTabHost.newTabSpec(baiduMapTag)
                .setIndicator(baiduMapTag)
                .setContent(intent));
        mTabHost.addTab(mTabHost.newTabSpec(aMapTag)
                .setIndicator(aMapTag)
                .setContent(intent));


        Intent showInnsMap = new Intent(this, MapActivity.class);
        showInnsMap.putExtra("showInnsMap", false);

        mTabHost.addTab(mTabHost.newTabSpec(otherMapTag)
                .setIndicator(otherMapTag)
                .setContent(showInnsMap));

        showInnsMap.putExtra("showInnsMap", true);
        mTabHost.addTab(mTabHost.newTabSpec(innsMapTag)
                .setIndicator(innsMapTag)
                .setContent(showInnsMap));


        mTabHost.addTab(mTabHost.newTabSpec(googleMapTag)
                .setIndicator(googleMapTag)
                .setContent(new Intent(this, GoogleMapMainActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(autoMarkTag)
                .setIndicator(autoMarkTag)
                .setContent(new Intent(this, LiftAutoMarkActivity.class)));




        //        LogUtil.i("地图默认值", "isGerenalTest()>>" + this.appModel.isGerenalTest() + "  isIndoorTest() "
        //                + this.appModel.isIndoorTest() + " isTestJobIsRun() " + this.appModel.isTestJobIsRun());

        mTabHost.setOnTabChangedListener(this);


        if (!this.appModel.isGerenalTest() && !this.appModel.isIndoorTest() && this.appModel.isTestJobIsRun()) {
            String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
            if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[0])) {//MIF也放在百度图层上
                mTabHost.setCurrentTabByTag(baiduMapTag);
                TraceInfoInterface.currentMapChildTab = baiduMap;
            } else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[1])) {
                mTabHost.setCurrentTabByTag(googleMapTag);
                TraceInfoInterface.currentMapChildTab = googleMap;
            } else if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
                mTabHost.setCurrentTabByTag(baiduMapTag);
                TraceInfoInterface.currentMapChildTab = baiduMap;
                //TODO 高德地图暂时使用百度地图呈现
            } else if (dtmap.length > 3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
                mTabHost.setCurrentTabByTag(aMapTag);
                TraceInfoInterface.currentMapChildTab = aMap;
            } else if (dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
                mTabHost.setCurrentTabByTag(baiduMapTag);
                TraceInfoInterface.currentMapChildTab = baiduMap;
            }
        } else if (this.appModel.getSelectScene() == WalkStruct.SceneType.Metro) {
            String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
            if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[0])) {
                mTabHost.setCurrentTabByTag(baiduMapTag);
                TraceInfoInterface.currentMapChildTab = baiduMap;
            } else if (ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[1])) {
                mTabHost.setCurrentTabByTag(googleMapTag);
                TraceInfoInterface.currentMapChildTab = googleMap;
            } else if (dtmap.length > 2 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[2])) {
                mTabHost.setCurrentTabByTag(baiduMapTag);
                TraceInfoInterface.currentMapChildTab = baiduMap;
                //TODO 高德地图暂时使用百度地图呈现
            } else if (dtmap.length > 3 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[3])) {
                mTabHost.setCurrentTabByTag(aMapTag);
                TraceInfoInterface.currentMapChildTab = aMap;
            } else if (dtmap.length > 4 && ParameterSetting.getInstance().getDtDefaultMap().equals(dtmap[4])) {
                mTabHost.setCurrentTabByTag(baiduMapTag);
                TraceInfoInterface.currentMapChildTab = baiduMap;
            }
        } else if ((this.appModel.isGerenalTest() || this.appModel.isIndoorTest()) && this.appModel.isTestJobIsRun()) {
            if (!this.appModel.isInnsmapTest()) {
                //如果是室内并且是外置陀螺仪打点的时候
                if (AutoMarkConstant.markScene != MarkScene.COMMON) {
                    mTabHost.setCurrentTabByTag(autoMarkTag);
                    return;
                }
                mTabHost.setCurrentTabByTag(otherMapTag);
                TraceInfoInterface.currentMapChildTab = otherMap;
            } else {
                mTabHost.setCurrentTabByTag(innsMapTag);
                TraceInfoInterface.currentMapChildTab = innsMap;
            }
        } else if (TraceInfoInterface.currentMapChildTab.equals(googleMap)) {
            mTabHost.setCurrentTabByTag(googleMapTag);
            TraceInfoInterface.currentMapChildTab = googleMap;
        } else if (TraceInfoInterface.currentMapChildTab.equals(baiduMap)) {
            mTabHost.setCurrentTabByTag(baiduMapTag);
            TraceInfoInterface.currentMapChildTab = baiduMap;
        } else if (TraceInfoInterface.currentMapChildTab.equals(aMap)) {
            mTabHost.setCurrentTabByTag(aMapTag);
            TraceInfoInterface.currentMapChildTab = aMap;
        } else if (TraceInfoInterface.currentMapChildTab.equals(otherMap)) {
            mTabHost.setCurrentTabByTag(otherMapTag);
            TraceInfoInterface.currentMapChildTab = otherMap;
        } else if (TraceInfoInterface.currentMapChildTab.equals(innsMap)) {
            mTabHost.setCurrentTabByTag(innsMapTag);
            TraceInfoInterface.currentMapChildTab = innsMap;
        } else {//默认百度地图加载
            mTabHost.setCurrentTabByTag(baiduMapTag);
        }


    }

    /**
     * 切换地图
     *
     * @param type 地图类型 1:离线 2：在线 3:其他地图
     */
    @Override
    public void swicthMap(WalkStruct.ShowInfoType type) {
        if (type.equals(WalkStruct.ShowInfoType.OfflineMap)) {
            String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
            String dtDefaultMap = ParameterSetting.getInstance().getDtDefaultMap();
            if (dtDefaultMap.equals(dtmap[1])) {
                Intent intent = new Intent(this, OfflineMapActivity.class);
                startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
            } else if (dtmap.length > 2 && dtDefaultMap.equals(dtmap[2])) {
                Intent intent = new Intent(this, BaiduMapOfflineActivity.class);
                startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
                //TODO 高德地图暂时使用百度地图呈现
            } else if (dtmap.length > 3 && dtDefaultMap.equals(dtmap[3])) {
                Intent intent = new Intent(this, BaiduMapOfflineActivity.class);
                startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
            } else {
                Intent intent = new Intent(this, BaiduMapOfflineActivity.class);
                startActivityForResult(intent, BaseMapActivity.OFFLINE_MAP_RESULT_CODE);
            }
        } else if (type.equals(WalkStruct.ShowInfoType.OnlineMap)) {
            onActivityResult(BaseMapActivity.ONLINE_MAP_RESULT_CODE, BaseMapActivity.ONLINE_MAP_RESULT_CODE, new Intent());
        } else if (type.equals(googleMap)) {
            mTabHost.setCurrentTabByTag(googleMapTag);
            TraceInfoInterface.currentMapChildTab = googleMap;
        } else if (type.equals(baiduMap)) {
            mTabHost.setCurrentTabByTag(baiduMapTag);
            TraceInfoInterface.currentMapChildTab = baiduMap;
        } else if (type.equals(aMap)) {
            mTabHost.setCurrentTabByTag(aMapTag);
            TraceInfoInterface.currentMapChildTab = aMap;
        } else if (type.equals(innsMap)) {
            mTabHost.setCurrentTabByTag(innsMapTag);
            TraceInfoInterface.currentMapChildTab = innsMap;
        } else {
            if (!mTabHost.getCurrentTabTag().equals(otherMapTag)) {
                mTabHost.setCurrentTabByTag(otherMapTag);
                TraceInfoInterface.currentMapChildTab = otherMap;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == BaseMapActivity.OFFLINE_MAP_RESULT_CODE
                    || requestCode == BaseMapActivity.ONLINE_MAP_RESULT_CODE) {
                String[] dtmap = getResources().getStringArray(R.array.sys_dtmap_default);
                String dtDefaultMap = ParameterSetting.getInstance().getDtDefaultMap();
                if (dtDefaultMap.equals(dtmap[1])) {
                    mTabHost.setCurrentTabByTag(googleMapTag);
                    TraceInfoInterface.currentMapChildTab = googleMap;
                } else if (dtmap.length > 2 && dtDefaultMap.equals(dtmap[2])) {
                    mTabHost.setCurrentTabByTag(baiduMapTag);
                    TraceInfoInterface.currentMapChildTab = baiduMap;
                    //TODO 高德地图暂时使用百度地图呈现
                } else if (dtmap.length > 3 && dtDefaultMap.equals(dtmap[3])) {
                    mTabHost.setCurrentTabByTag(aMapTag);
                    TraceInfoInterface.currentMapChildTab = aMap;
                } else if (dtmap.length > 0 && dtDefaultMap.equals(dtmap[0])) {//MIF
                    mTabHost.setCurrentTabByTag(baiduMapTag);
                    TraceInfoInterface.currentMapChildTab = baiduMap;
                } else if (dtmap.length > 4 && dtDefaultMap.equals(dtmap[4])) {
                    mTabHost.setCurrentTabByTag(baiduMapTag);
                    TraceInfoInterface.currentMapChildTab = baiduMap;
                }
            } else {
                mTabHost.setCurrentTabByTag(otherMapTag);
                TraceInfoInterface.currentMapChildTab = otherMap;
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
        if (tabId.equals(otherMapTag)) {
            TraceInfoInterface.currentMapChildTab = otherMap;
        } else if (tabId.equals(googleMapTag)) {
            TraceInfoInterface.currentMapChildTab = googleMap;
        } else if (tabId.equals(baiduMapTag)) {
            TraceInfoInterface.currentMapChildTab = baiduMap;
        } else if (tabId.equals(aMapTag)) {
            TraceInfoInterface.currentMapChildTab = aMap;
        }
    }
}

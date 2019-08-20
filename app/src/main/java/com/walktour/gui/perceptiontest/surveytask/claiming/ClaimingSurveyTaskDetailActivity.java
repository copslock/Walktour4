package com.walktour.gui.perceptiontest.surveytask.claiming;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.perceptiontest.surveytask.claiming.event.RefreshDataEvent;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;
import com.walktour.gui.perceptiontest.surveytask.data.dao.DBManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/11/19.
 * <p>
 * 勘测任务详情界面（带地图）
 */

public class ClaimingSurveyTaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SURVEY_TASK = "extra_survey_task";
    private static final String TAG = "ClaimingSurveyTaskDetailActivity";
    private static final int MSG_STOP_PD = 0x01;

    @BindView(R.id.map_view_container)
    FrameLayout mMapViewContainer;
    @BindView(R.id.tv_survey_task_no)
    TextView mTvSurveyTaskNo;
    @BindView(R.id.tv_survey_task_location_desc)
    TextView mTvSurveyTaskLocationDesc;
    @BindView(R.id.tv_survey_task_lat_long)
    TextView mTvSurveyTaskLatLong;

//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mTvTitle;

    @BindView(R.id.container_claim)
    LinearLayout mClaimButtonContainer;

    private MapView mMapView;
    private ProgressDialog mProgressDialog;
    private SurveyTask mSurveyTask;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_task_detail);

        mSurveyTask = (SurveyTask) getIntent().getSerializableExtra(EXTRA_SURVEY_TASK);

        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在领取，请稍等...");

        //任务编号
        mTvSurveyTaskNo.setText(mSurveyTask.getTaskNo());
        //任务地点描述
        mTvSurveyTaskLocationDesc.setText(mSurveyTask.getAddress());
        //经纬度
        mTvSurveyTaskLatLong.setText(String.valueOf(mSurveyTask.getLatitude() + "," + mSurveyTask.getLongitude()));
        mClaimButtonContainer.setVisibility(mSurveyTask.getState() == SurveyTask.STATE_CLAIMED ? View.GONE : View.VISIBLE);

//        setSupportActionBar(this.mToolbar);
        setToolbarTitle(mSurveyTask.getStationName());
        SDKInitializer.initialize(WalktourApplication.getAppContext());

        BaiduMapOptions option = new BaiduMapOptions();
        option.zoomControlsEnabled(false);
        mMapView = new MapView(this, option);
        BaiduMap map = mMapView.getMap();
        mMapView.setClickable(true);
        //显示卫星图层
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapViewContainer.addView(mMapView);
        //定义Maker坐标点
        LatLng point = new LatLng(mSurveyTask.getLatitude(), mSurveyTask.getLongitude());
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.btn_control_station_pressed);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions options = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        map.addOverlay(options);

        map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(mSurveyTask.getLatitude(), mSurveyTask.getLongitude()), 15));
    }

    /**
     * 设置标题栏
     *
     * @param title 标题
     */
    protected void setToolbarTitle(String title) {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar == null)
//            return;
        //为标题栏设置标题，即给ActionBar设置标题。
        this.mTvTitle.setText(title);
//        actionBar.setTitle("");
//        //ActionBar加一个返回图标
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        //不显示当前程序的图标。
//        actionBar.setDisplayShowHomeEnabled(false);
    }

    @OnClick(R.id.ib_back)
    void back() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btn_cancel)
    public void onClickCancel() {
        finish();
    }


    @OnClick(R.id.btn_claim)
    public void onClickClaim() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mHandler.sendEmptyMessageDelayed(MSG_STOP_PD, 2000);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                ToastUtil.showShort(ClaimingSurveyTaskDetailActivity.this, "勘测任务领取成功!");
                EventBus.getDefault().post(new RefreshDataEvent());
                mSurveyTask.setState(SurveyTask.STATE_CLAIMED);
                DBManager.getInstance(ClaimingSurveyTaskDetailActivity.this).update(mSurveyTask);
                finish();
            }
        }
    };


}

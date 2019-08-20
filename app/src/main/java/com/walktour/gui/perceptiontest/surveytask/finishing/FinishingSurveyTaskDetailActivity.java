package com.walktour.gui.perceptiontest.surveytask.finishing;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.perceptiontest.surveytask.claiming.event.RefreshDataEvent;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;
import com.walktour.gui.perceptiontest.surveytask.data.dao.DBManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/11/19.
 * <p>
 * 勘测任务详情界面（带地图）
 */

public class FinishingSurveyTaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SURVEY_TASK = "extra_survey_task";
    private static final String TAG = "ClaimingSurveyTaskDetailActivity";
    private static final int MSG_STOP_PD = 0x01;

    private static final int REQUEST_CODE_TAKE_PHONE = 0x101;
    private final int CLOSE = 0;
    private final int FAR = 1;
    private final int ANTENNA = 2;
    private final int COVER = 3;

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

    @BindView(R.id.container_finishing)
    LinearLayout mFinishingButtonContainer;

    @BindView(R.id.survey_content_container)
    LinearLayout mContainerSurveyContent;
    @BindView(R.id.container_cell_info)
    LinearLayout mContainerCellInfo;

    @BindView(R.id.tv_capture_close_shot_path)
    TextView mTvCaptureCloseShotPath;
    @BindView(R.id.tv_capture_far_shot_path)
    TextView mTvCaptureFarShotPath;
    @BindView(R.id.tv_capture_antenna_back_path)
    TextView mTvCaptureAntennaBackPath;
    @BindView(R.id.tv_capture_cover_direction_path)
    TextView mTvCaptureCoverDirectionPath;

    @BindView(R.id.iv_capture_close_shot)
    ImageView mIvCaptureCloseShot;
    @BindView(R.id.iv_capture_far_shot)
    ImageView mIvCaptureFarShot;
    @BindView(R.id.iv_capture_antenna_back)
    ImageView mIvCaptureAntennaBack;
    @BindView(R.id.iv_capture_cover_direction)
    ImageView mIvCaptureCoverDirection;

    @BindView(R.id.tv_cell_lon_lat)
    TextView mTvCellLonLat;
    @BindView(R.id.tv_azimuth)
    TextView mTvAzimuth;
    @BindView(R.id.tv_downtilt)
    TextView mTvDowntilt;

    /**
     * 启动测试
     */
    @BindView(R.id.btn_start)
    Button mBtnStart;
    /**
     * 获取数据
     */
    @BindView(R.id.btn_analyze)
    Button mBtnAnalyze;

    @BindView(R.id.btn_finish)
    Button mBtnFinish;

    private int mCurrentType; //当前拍照类型
    private String mCurrentPhotoPath;

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

        //任务编号
        mTvSurveyTaskNo.setText(mSurveyTask.getTaskNo());
        //任务地点描述
        mTvSurveyTaskLocationDesc.setText(mSurveyTask.getAddress());
        //经纬度
        mTvSurveyTaskLatLong.setText(String.valueOf(mSurveyTask.getLatitude() + "," + mSurveyTask.getLongitude()));
        mFinishingButtonContainer.setVisibility(mSurveyTask.getState() >= SurveyTask.STATE_FINISHED ? View.GONE : View.VISIBLE);
        if (mSurveyTask.getState() >= SurveyTask.STATE_FINISHED) {
            mMapViewContainer.setVisibility(View.GONE);
            mContainerSurveyContent.setVisibility(View.VISIBLE);
            mTvCaptureCloseShotPath.setText(mSurveyTask.getCloseShotImgPath());
            mTvCaptureFarShotPath.setText(mSurveyTask.getFarShotImgPath());
            mTvCaptureAntennaBackPath.setText(mSurveyTask.getAntennaBackImgPath());
            mTvCaptureCoverDirectionPath.setText(mSurveyTask.getCoverDirectionImgPath());

            mIvCaptureCloseShot.setVisibility(View.GONE);
            mIvCaptureFarShot.setVisibility(View.GONE);
            mIvCaptureAntennaBack.setVisibility(View.GONE);
            mIvCaptureCoverDirection.setVisibility(View.GONE);

            mContainerCellInfo.setVisibility(View.VISIBLE);
            mTvDowntilt.setText(String.valueOf(mSurveyTask.getDowntilt()));
            mTvAzimuth.setText(String.valueOf(mSurveyTask.getAzimuth()));
            mTvCellLonLat.setText(String.valueOf(mSurveyTask.getLatitude() + "," + mSurveyTask.getLongitude()));
        }


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
//        //为标题栏设置标题，即给ActionBar设置标题。
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                ToastUtil.showShort(FinishingSurveyTaskDetailActivity.this, "已完成该勘测任务!");
                EventBus.getDefault().post(new RefreshDataEvent());
                mSurveyTask.setState(SurveyTask.STATE_FINISHED);
                DBManager.getInstance(FinishingSurveyTaskDetailActivity.this).update(mSurveyTask);
                finish();
            }
        }
    };

    @OnClick(R.id.btn_start)
    public void onClickStart() {
        mMapViewContainer.setVisibility(View.GONE);
        mContainerSurveyContent.setVisibility(View.VISIBLE);
        mBtnAnalyze.setEnabled(true);
    }

    @OnClick(R.id.btn_analyze)
    public void onClickAnalyze() {
        if (!TextUtils.isEmpty(mTvCaptureCloseShotPath.getText().toString().trim()) &&
                !TextUtils.isEmpty(mTvCaptureFarShotPath.getText().toString().trim()) &&
                !TextUtils.isEmpty(mTvCaptureAntennaBackPath.getText().toString().trim()) &&
                !TextUtils.isEmpty(mTvCaptureCoverDirectionPath.getText().toString().trim())) {
            mProgressDialog.setMessage("正在分析，请稍等...");
            mProgressDialog.show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTvCellLonLat.setText(String.valueOf(mSurveyTask.getLatitude() + "," + mSurveyTask.getLongitude()));
                    mTvAzimuth.setText(String.valueOf(mSurveyTask.getAzimuth()));
                    mTvDowntilt.setText(String.valueOf(mSurveyTask.getDowntilt()));
                    mContainerCellInfo.setVisibility(View.VISIBLE);
                    mBtnFinish.setEnabled(true);
                    mProgressDialog.dismiss();
                }
            }, 1500);
        } else {
            ToastUtil.showShort(FinishingSurveyTaskDetailActivity.this, "请完成所需照片拍摄！");
        }

    }

    @OnClick(R.id.btn_finish)
    public void onClickFinish() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.setMessage("处理中...");
            mProgressDialog.show();
            mHandler.sendEmptyMessageDelayed(MSG_STOP_PD, 1000);
            updateSurveyTask();
        }
    }

    /**
     * 更新数据库数据
     */
    private void updateSurveyTask() {
        //        SurveyCell surveyCell = mSurveyTask.getSurveyCells().get(0);
        mSurveyTask.setCloseShotImgPath(mTvCaptureCloseShotPath.getText().toString().trim());
        mSurveyTask.setFarShotImgPath(mTvCaptureFarShotPath.getText().toString().trim());
        mSurveyTask.setAntennaBackImgPath(mTvCaptureAntennaBackPath.getText().toString().trim());
        mSurveyTask.setCoverDirectionImgPath(mTvCaptureCoverDirectionPath.getText().toString().trim());
        LogUtil.e(TAG, "------updateSurveyTask:" + mSurveyTask.toString());
        DBManager.getInstance(this).update(mSurveyTask);
    }

    @OnClick(R.id.iv_capture_close_shot)
    public void onClickCaptureCloseShot() {
        ToastUtil.showShort(this, "近景拍摄");
        takePhoto(CLOSE);
    }

    @OnClick(R.id.iv_capture_far_shot)
    public void onClickCaptureFarShot() {
        ToastUtil.showShort(this, "远景拍摄");
        takePhoto(FAR);
    }

    @OnClick(R.id.iv_capture_antenna_back)
    public void onClickCaptureAntennaBack() {
        ToastUtil.showShort(this, "天线背板拍摄");
        takePhoto(ANTENNA);
    }

    @OnClick(R.id.iv_capture_cover_direction)
    public void onClickCaptureCoverDirection() {
        ToastUtil.showShort(this, "覆盖方向拍摄");
        takePhoto(COVER);
    }


    private void takePhoto(int type) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);


        String dir = AppFilePathUtil.getInstance().getSDCardBaseDirectory() + "/DCIM/Survey/";

        File dirF = new File(dir);
        if (!dirF.exists()){
            dirF.mkdirs();
        }

        String path = dir + System.currentTimeMillis() + ".jpg";
        mCurrentType = type;
        mCurrentPhotoPath = path;

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHONE) {
            File file = new File(mCurrentPhotoPath);
            if (file.exists()) {
                String absPath = file.getAbsolutePath();
                switch (mCurrentType) {
                    case CLOSE:
                        mTvCaptureCloseShotPath.setText(absPath);
                        break;
                    case FAR:
                        mTvCaptureFarShotPath.setText(absPath);
                        break;
                    case ANTENNA:
                        mTvCaptureAntennaBackPath.setText(absPath);
                        break;
                    case COVER:
                        mTvCaptureCoverDirectionPath.setText(absPath);
                        break;
                }
            }
        }
    }
}

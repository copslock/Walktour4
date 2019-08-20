package com.walktour.gui.perceptiontest.surveytask;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.walktour.gui.R;

import butterknife.BindView;

/**
 * Created by Yi.Lin on 2018/11/19.
 * <p>
 * 勘测任务上传详情界面（4张图片）
 */

public class SurveyTaskUploadDetailActivity extends AppCompatActivity {

    @BindView(R.id.map_view_container)
    FrameLayout mMapViewContainer;
    @BindView(R.id.tv_survey_task_no)
    TextView mTvSurveyTaskNo;
    @BindView(R.id.tv_survey_task_location_desc)
    TextView mTvSurveyTaskLocationDesc;
    @BindView(R.id.tv_survey_task_lat_long)
    TextView mTvSurveyTaskLatLong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_task_detail);

    }

}
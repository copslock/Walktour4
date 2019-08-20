package com.walktour.gui.perceptiontest.surveytask.claiming.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.walktour.Utils.StringUtil;
import com.walktour.base.gui.adapter.recyclerview.BaseViewHolder;
import com.walktour.base.gui.adapter.recyclerview.CommonRecyclerAdapter;
import com.walktour.gui.R;
import com.walktour.gui.newmap2.util.BaiduMapUtil;
import com.walktour.gui.perceptiontest.surveytask.claiming.UnclaimedSurveyTaskFragment;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;

/**
 * Created by Yi.Lin on 2018/11/18.
 * 待领取勘测任务列表数据适配器
 */

public class UnclaimedSurveyTaskRecyclerAdapter extends CommonRecyclerAdapter<SurveyTask> {

    private Context mContext;

    public UnclaimedSurveyTaskRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.item_unclaimed_survey_task;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        SharedPreferences spf = mContext.getSharedPreferences(UnclaimedSurveyTaskFragment.SPF_NAME, Context.MODE_PRIVATE);
        String survey_lat = spf.getString("survey_lat", "");
        String survey_lng = spf.getString("survey_lng", "");


        final SurveyTask surveyTask = mList.get(position);
        TextView tvName = holder.get(R.id.tv_name);
        tvName.setText(surveyTask.getStationName());

        TextView tvTaskType = holder.get(R.id.tv_survey_task_type);
        tvTaskType.setText(surveyTask.getTaskType());
        TextView tvTaskName = holder.get(R.id.tv_survey_task_name);
        tvTaskName.setText(surveyTask.getTaskName());
        TextView tvTaskTimeLimit = holder.get(R.id.tv_survey_task_time_limit);
        tvTaskTimeLimit.setText(surveyTask.getTimeLimit());
        TextView tvTaskDistance = holder.get(R.id.tv_survey_task_distance);
        tvTaskDistance.setText(surveyTask.getDistance());

        try {
            if (!StringUtil.isNullOrEmpty(survey_lat)
                    && !StringUtil.isNullOrEmpty(survey_lng)) {

                double lat = Double.parseDouble(survey_lat);
                double lng = Double.parseDouble(survey_lng);
                LatLng start = new LatLng(lat, lng);

                double latitude = surveyTask.getLatitude();
                double longitude = surveyTask.getLongitude();
                LatLng end = BaiduMapUtil.convert(latitude, longitude);

                String distance = BaiduMapUtil.getDistanceStr(start, end);
                tvTaskDistance.setText(distance);
            }
        } catch (Exception e) {
            // ignore
        }


        final View ivSeeDetail = holder.get(R.id.iv_see_detail);
        final View detailContainer = holder.get(R.id.container_detail);
        detailContainer.setVisibility(surveyTask.isExpanded() ? View.VISIBLE : View.GONE);

        if (!surveyTask.isExpanded()) {
            ivSeeDetail.setRotation(0);
        } else {
            ivSeeDetail.setRotation(90);
        }

        ivSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = detailContainer.getVisibility() == View.VISIBLE;
                detailContainer.setVisibility(expanded ? View.GONE : View.VISIBLE);
                surveyTask.setExpanded(!expanded);
                if (!expanded) {
                    ivSeeDetail.setRotation(90);
                } else {
                    ivSeeDetail.setRotation(0);
                }
            }
        });
    }
}

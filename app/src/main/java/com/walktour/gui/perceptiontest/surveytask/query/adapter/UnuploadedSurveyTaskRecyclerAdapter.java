package com.walktour.gui.perceptiontest.surveytask.query.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.walktour.base.gui.adapter.recyclerview.BaseViewHolder;
import com.walktour.base.gui.adapter.recyclerview.CommonRecyclerAdapter;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;

/**
 * Created by Yi.Lin on 2018/11/18.
 * 待领取勘测任务列表数据适配器
 */

public class UnuploadedSurveyTaskRecyclerAdapter extends CommonRecyclerAdapter<SurveyTask> {

    public UnuploadedSurveyTaskRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public int onCreateViewLayoutID(int viewType) {
        return R.layout.item_unuploaded_survey_task;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final SurveyTask surveyTask = mList.get(position);
        CheckBox cb = holder.get(R.id.checkbox);
        TextView tvName = holder.get(R.id.tv_survey_task_name);
        tvName.setText(surveyTask.getTaskName());
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                surveyTask.setChecked(isChecked);
            }
        });
    }
}

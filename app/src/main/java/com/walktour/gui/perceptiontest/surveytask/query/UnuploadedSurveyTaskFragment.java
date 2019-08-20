package com.walktour.gui.perceptiontest.surveytask.query;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.surveytask.claiming.event.RefreshDataEvent;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;
import com.walktour.gui.perceptiontest.surveytask.data.DataProvider;
import com.walktour.gui.perceptiontest.surveytask.data.dao.DBManager;
import com.walktour.gui.perceptiontest.surveytask.finishing.FinishingSurveyTaskDetailActivity;
import com.walktour.gui.perceptiontest.surveytask.query.adapter.UnuploadedSurveyTaskRecyclerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/11/18.
 * 已领取勘察任务列表
 */

public class UnuploadedSurveyTaskFragment extends Fragment {

    private static final String TAG = "UnuploadedSurveyTaskFragment";
    private static final int MSG_STOP_PD = 0x02;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private UnuploadedSurveyTaskRecyclerAdapter mRecyclerAdapter;
    private ArrayList<SurveyTask> mSurveyTaskArrayList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private EventBus mEventBus = EventBus.getDefault();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unuploaded_survey_task, container, false);
        ButterKnife.bind(this, rootView);
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("正在加载数据，请稍等...");
        mRecyclerAdapter = new UnuploadedSurveyTaskRecyclerAdapter(getActivity());
        mRecyclerAdapter.replaceList(DBManager.getInstance(getActivity()).queryList(SurveyTask.STATE_UNUPLOADED));
        mRecyclerAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FinishingSurveyTaskDetailActivity.class);
                SurveyTask surveyTask = mRecyclerAdapter.getList().get(position);
                intent.putExtra(FinishingSurveyTaskDetailActivity.EXTRA_SURVEY_TASK, surveyTask);
                startActivity(intent);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        return rootView;
    }


    @OnClick(R.id.btn_cancel_upload)
    public void onClickCancelUpload(View view) {
        //todo 暂时隐藏
    }


    @OnClick(R.id.btn_upload)
    public void onClickUpload(View view) {
        final List<SurveyTask> tasks = mRecyclerAdapter.getList();
        if (null != tasks && !tasks.isEmpty()) {
            //已勾选勘测任务
            List<SurveyTask> checkedTasks = new ArrayList<>();
            for (SurveyTask task : tasks) {
                if (task.isChecked()) {
                    checkedTasks.add(task);
                }
            }

            if (!checkedTasks.isEmpty()) {
                String toJson = new Gson().toJson(tasks);
                mProgressDialog.setMessage("正在上传数据...");
                mProgressDialog.show();
                for (SurveyTask task : checkedTasks) {
                    task.setState(SurveyTask.STATE_UPLOADED);
                    DBManager.getInstance(getActivity()).update(task);

                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        EventBus.getDefault().post(new RefreshDataEvent());
                    }
                }, 2000 * checkedTasks.size());
            } else {
                ToastUtil.showShort(getActivity(), "请勾选需要上传的数据!");
            }
        } else {
            ToastUtil.showShort(getActivity(), "无可上传数据!");
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                ToastUtil.showShort(getActivity(), "数据获取成功!");
                mRecyclerAdapter.replaceList(DataProvider.getInstance().getSurveyTaskList(getActivity(), SurveyTask.STATE_UNUPLOADED));
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveRefreshDataEvent(RefreshDataEvent event) {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
            mHandler.sendEmptyMessageDelayed(MSG_STOP_PD, 1000);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }
}

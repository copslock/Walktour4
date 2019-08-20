package com.walktour.gui.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.walktour.Utils.StringUtil;
import com.walktour.control.adapter.TaskSelectSampleAdapter;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.dragsortlistview.DragSortListView;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.command.BaseCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskSelSampleActivity extends BasicActivity {


    public static final String SELECT_SAMPLE_DATA = "select_sample_data";

    @BindView(R.id.param_listview)
    DragSortListView mLv;

    @BindView(R.id.title_txt)
    TextView mTitle;

    private List<SampleBean> mDatas;
    private TaskSelectSampleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_sel_sample);

        ButterKnife.bind(this);

        mTitle.setText("选择语料");

        String selectSampleData = getIntent().getStringExtra(SELECT_SAMPLE_DATA);
        String[] split = null;
        if (!StringUtil.isNullOrEmpty(selectSampleData)) {
            split = selectSampleData.split(",");
        }


        mDatas = new ArrayList<>();
        for (BaseCommand.FileType type : BaseCommand.FileType.values()) {
            if (BaseCommand.FileType.check_8k == type || BaseCommand.FileType.pesq_8k == type) {
                continue;
            }
            SampleBean sampleBean = new SampleBean();
            sampleBean.name = type.getName();
            if (split != null) {
                for (int i = 0; i < split.length; i++) {
                    String selName = split[i];
                    if (selName.equals(type.getName())) {
                        sampleBean.select = true;
                        sampleBean.index = i;
                    }
                }
            }
            mDatas.add(sampleBean);
        }

        Collections.sort(mDatas, new Comparator<SampleBean>() {
            @Override
            public int compare(SampleBean o1, SampleBean o2) {
                if (o1.index > o2.index)
                    return 1;//由底到高排序
                else if (o1.index < o2.index)
                    return -1;
                else
                    return 0;
            }
        });


        mAdapter = new TaskSelectSampleAdapter(mDatas, this);
        mLv.setAdapter(mAdapter);
        mLv.setDragEnabled(true);
        mLv.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                SampleBean bean = mAdapter.getItem(from);
                mDatas.remove(bean);
                mDatas.add(to, bean);
                mLv.moveCheckState(from, to);
                mAdapter.notifyDataSetChanged();
            }
        });

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SampleBean bean = mDatas.get(position);
                bean.select = !bean.select;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.pointer)
    void clickBack() {
        setSelectResult();
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setSelectResult();
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setSelectResult() {
        List<String> sampleNameList = new ArrayList<>();
        for (SampleBean bean : mDatas) {
            if (bean.select) {
                sampleNameList.add(bean.name);
            }
        }
        Intent intent = new Intent();
        intent.putExtra(SELECT_SAMPLE_DATA, new Gson().toJson(sampleNameList));
        setResult(RESULT_OK, intent);
    }

    public static class SampleBean {
        public String name;
        public boolean select;
        public int index = 10;
    }
}

package com.walktour.gui.perceptiontest.surveytask.query;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.surveytask.claiming.adapter.SurveyTaskClaimingPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yi.Lin on 2018/11/18.
 * 任务查询完成界面，包括带上传、已上传
 */

public class SurveyTaskQueryActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

//    @BindView(R.id.toolbar)
//    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mTvTitle;

    private SurveyTaskClaimingPagerAdapter mPagerAdapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_task_finishing);
        ButterKnife.bind(this);
        initViews();
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

    private void initViews() {
//        setSupportActionBar(this.mToolbar);
        setToolbarTitle("勘测查询");

        if (mFragments.isEmpty()) {
            mFragments.add(new UnuploadedSurveyTaskFragment());
            mFragments.add(new UploadedSurveyTaskFragment());
        }
        if (mTitles.isEmpty()) {
            mTitles.add("未上传");
            mTitles.add("已上传");
        }

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitles.get(1)));

        mPagerAdapter = new SurveyTaskClaimingPagerAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            this.finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @OnClick(R.id.ib_back)
    void back() {
        finish();
    }
}

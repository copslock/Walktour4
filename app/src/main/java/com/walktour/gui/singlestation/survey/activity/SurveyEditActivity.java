package com.walktour.gui.singlestation.survey.activity;

import android.support.design.widget.TabLayout;
import android.view.Menu;

import com.walktour.base.gui.activity.BaseTabHostActivity;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.SurveyStationInfo;
import com.walktour.gui.singlestation.survey.component.DaggerSurveyEditActivityComponent;
import com.walktour.gui.singlestation.survey.fragment.SurveyEditBaseFragment;
import com.walktour.gui.singlestation.survey.module.SurveyEditActivityModule;
import com.walktour.gui.singlestation.survey.presenter.SurveyEditActivityPresenter;

import java.util.List;

import javax.inject.Inject;

/**
 * 基站勘查编辑界面
 * Created by wangk on 2017/7/14.
 */

public class SurveyEditActivity extends BaseTabHostActivity {
    /**
     * 日志标识
     */
    private static final String TAG = "SurveyEditActivity";
    /**
     * 关联交互类
     */
    @Inject
    SurveyEditActivityPresenter mPresenter;

    @Override
    protected void onCreate() {
        super.setToolbarTitle(R.string.single_station_survey);
        mPresenter.setSurveyStationEditing();
    }

    @Override
    protected void initFragments() {
        List<SurveyEditBaseFragment> fragmentList = this.mPresenter.getInitFragments();
        for (BaseFragment fragment : fragmentList) {
            super.addFragment(fragment);
        }
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mPresenter.saveEditingData();
    }

    @Override
    public BaseActivityPresenter getPresenter() {
        return this.mPresenter;
    }

    /**
     * 生成顶部菜单栏
     *
     * @param menu 菜单对象
     * @return 是否有生成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SurveyStationInfo surveyStationInfo = this.getIntent().getParcelableExtra("survey_station_info");
        int fromType = surveyStationInfo.getStationInfo().getFromType();
        if (fromType == StationInfo.FROM_TYPE_PLATFORM) {
            return super.onCreateOptionsMenu(menu, R.menu.singlestation_survey_edit_menu);
        } else {
            return super.onCreateOptionsMenu(menu);
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        mPresenter.saveEditingData((int) tab.getTag());
    }

    @Override
    protected void setupActivityComponent() {
        DaggerSurveyEditActivityComponent.builder().surveyEditActivityModule(new SurveyEditActivityModule(this)).build().inject(this);
    }

}

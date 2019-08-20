package com.walktour.gui.singlestation.test.fragment;

import android.text.Editable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.net.model.StationSearch;
import com.walktour.gui.singlestation.test.component.DaggerStationSearchFragmentComponent;
import com.walktour.gui.singlestation.test.module.StationSearchFragmentModule;
import com.walktour.gui.singlestation.test.presenter.StationSearchFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 基站测试平台基站查询列表
 * Created by wangk on 2017/6/19.
 */

public class StationSearchFragment extends BaseListFragment<StationSearch> {
    /**
     * 日志标识
     */
    private static final String TAG = "StationSearchFragment";
    /**
     * 界面交互类
     */
    @Inject
    StationSearchFragmentPresenter mPresenter;
    /**
     * 任务组列表
     */
    @BindView(R2.id.list_view)
    ListView mStationList;
    /**
     * 检索文本
     */
    @BindView(R2.id.search_content_edit)
    EditText mSearchText;
    /**
     * 按距离查询条件行
     */
    @BindView(R2.id.search_condition_distance)
    RadioGroup mSearchConditionDistance;
    /**
     * 是否显示距离查询行
     */
    private boolean isShowDistanceCondition = false;
    /**
     * 查询基站的范围（米）
     */
    private int mDistance = 1000;

    public StationSearchFragment() {
        super(R.string.single_station_station_search, R.layout.fragment_single_station_search_list, R.layout.fragment_single_station_search_list_row);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerStationSearchFragmentComponent.builder().stationSearchFragmentModule(new StationSearchFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

    }

    @Override
    public int[] showActivityMenuItemIds() {
        return new int[]{R.id.menu_singlestation_login/*, R.id.menu_singlestation_import_station*/};
    }

    @Override
    protected AbsListView getListView() {
        return this.mStationList;
    }

    @Override
    protected BaseHolder createViewHolder() {
        return new ViewHolder();
    }

    /**
     * 根据输入的值查询指定的基站
     *
     * @param s 输入的值
     */
    @OnTextChanged(value = R.id.search_content_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void searchStation(Editable s) {
        this.mPresenter.searchStationFromPlatform(s.toString());
    }

    /**
     * 根据选择的距离查询指定的基站
     *
     * @param radio   距离勾选框
     * @param checked 是否选中
     */
    @OnCheckedChanged(value = {R.id.search_condition_1000, R.id.search_condition_2000, R.id.search_condition_3000, R.id.search_condition_5000})
    public void searchStation(CompoundButton radio, boolean checked) {
        if (radio.getId() == R.id.search_condition_1000 && checked) {
            this.mDistance = 1000;
        } else if (radio.getId() == R.id.search_condition_2000 && checked) {
            this.mDistance = 2000;
        } else if (radio.getId() == R.id.search_condition_3000 && checked) {
            this.mDistance = 3000;
        } else if (radio.getId() == R.id.search_condition_5000 && checked) {
            this.mDistance = 5000;
        }
        if (checked)
            this.mPresenter.searchStationFromPlatform(this.mDistance);
    }

    /**
     * 切换查询条件显示
     */
    @OnClick(value = R.id.search_condition)
    public void changeConditionShow() {
        this.isShowDistanceCondition = !this.isShowDistanceCondition;
        if (this.isShowDistanceCondition)
            this.mSearchConditionDistance.setVisibility(View.VISIBLE);
        else
            this.mSearchConditionDistance.setVisibility(View.GONE);
    }

    /**
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<StationSearch> {
        /**
         * 基站图片
         */
        @BindView(R2.id.station_img)
        ImageView mImage;
        /**
         * 基站号
         */
        @BindView(R2.id.station_code)
        TextView mCode;
        /**
         * 基站名称
         */
        @BindView(R2.id.station_name)
        TextView mName;
        /**
         * 基站地址
         */
        @BindView(R2.id.station_address)
        TextView mAddress;
        /**
         * 平台基站ID
         */
        private int mStationId;

        @Override
        public void setData(int position, StationSearch data) {
            this.showStationImage(data);
            this.mStationId = data.getSiteId();
            this.mCode.setText(String.valueOf(data.getENodeBID()));
            this.mName.setText(data.getSiteName());
            this.mAddress.setText(data.getSiteAddress());
        }

        /**
         * 显示基站图片
         *
         * @param data 基站对象
         */
        private void showStationImage(StationSearch data) {
            if (data.getSiteType() == StationSearch.SITE_TYPE_INDOOR) {
                this.mImage.setBackgroundResource(R.drawable.singlestation_indoor_untest);
            } else {
                this.mImage.setBackgroundResource(R.drawable.singlestation_outdoor_untest);
            }
        }

        /**
         * 新增基站
         */
        @OnClick(value = R.id.station_add)
        public void addStation() {
            mPresenter.addStation(this.mStationId);
        }

    }

}

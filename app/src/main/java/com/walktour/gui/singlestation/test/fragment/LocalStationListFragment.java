package com.walktour.gui.singlestation.test.fragment;

import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.component.DaggerLocalStationListFragmentComponent;
import com.walktour.gui.singlestation.test.module.LocalStationListFragmentModule;
import com.walktour.gui.singlestation.test.presenter.LocalStationListFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 基站测试本地基站列表界面
 * Created by wangk on 2017/6/13.
 */

public class LocalStationListFragment extends BaseListFragment<StationInfo> {
    /**
     * 日志标识
     */
    private static final String TAG = "LocalStationListFragment";
    /**
     * 界面交互类
     */
    @Inject
    LocalStationListFragmentPresenter mPresenter;
    /**
     * 基站列表
     */
    @BindView(R2.id.list_view)
    ListView mStationListView;

    public LocalStationListFragment() {
        super(R.string.single_station_local_station, R.layout.fragment_list_base, R.layout.fragment_single_station_test_local_list_row);
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
        DaggerLocalStationListFragmentComponent.builder().localStationListFragmentModule(new LocalStationListFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

    }

    @Override
    public int[] showActivityMenuItemIds() {
        return new int[]{R.id.menu_singlestation_show_map};
    }

    @Override
    protected AbsListView getListView() {
        return this.mStationListView;
    }

    @Override
    protected BaseHolder createViewHolder() {
        return new ViewHolder();
    }

    @OnItemClick(R.id.list_view)
    void jumpToNextActivity(int position) {
        this.mPresenter.jumpToNextActivity(this.getItem(position));
    }

    /**
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<StationInfo> {
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
         * 基站距离
         */
        @BindView(R2.id.station_distance)
        TextView mDistance;
        /**
         * 测试状态
         */
        @BindView(R2.id.station_test_status)
        TextView mTestStatus;
        /**
         * 当前的编辑的基站
         */
        private StationInfo mStationInfo;

        @Override
        public void setData(int position, StationInfo data) {
            this.mStationInfo = data;
            this.showStationImage(data);
            this.mCode.setText(data.getCode());
            this.mName.setText(data.getName());
            this.mAddress.setText(data.getAddress());
            this.mDistance.setText(data.getDistance());
            this.showTestStatus(data);
        }

        /**
         * 显示基站图片
         *
         * @param data 基站对象
         */
        private void showStationImage(StationInfo data) {
            if (data.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
                if (data.getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                    this.mImage.setBackgroundResource(R.drawable.singlestation_indoor_untest);
                } else {
                    this.mImage.setBackgroundResource(R.drawable.singlestation_indoor_tested);
                }
            } else {
                if (data.getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                    this.mImage.setBackgroundResource(R.drawable.singlestation_outdoor_untest);
                } else {
                    this.mImage.setBackgroundResource(R.drawable.singlestation_outdoor_tested);
                }
            }
        }

        @OnClick(R2.id.station_test_status)
        void jumpToResultActivity() {
            mPresenter.jumpToResultActivity(this.mStationInfo);
        }

        /**
         * 显示测试状态
         *
         * @param data 基站对象
         */
        private void showTestStatus(StationInfo data) {
            int textId = R.string.single_station_test_status_init;
            int colorId = R.color.app_main_text_color;
            switch (data.getTestStatus()) {
                case StationInfo.TEST_STATUS_INIT:
                    textId = R.string.single_station_test_status_init;
                    break;
                case StationInfo.TEST_STATUS_TESTING:
                    textId = R.string.single_station_test_status_testing;
                    break;
                case StationInfo.TEST_STATUS_DEAL:
                    textId = R.string.single_station_test_status_dealing;
                    break;
                case StationInfo.TEST_STATUS_FAULT:
                    textId = R.string.single_station_test_status_fault;
                    colorId = R.color.red;
                    break;
                case StationInfo.TEST_STATUS_SUCCESS:
                    textId = R.string.single_station_test_status_success;
                    break;
            }
            this.mTestStatus.setText(textId);
            this.mTestStatus.setTextColor(getResources().getColor(colorId));
        }
    }
}

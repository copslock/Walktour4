package com.walktour.gui.singlestation.report.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.base.R2;
import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.report.component.DaggerTestedStationFragmentComponent;
import com.walktour.gui.singlestation.report.module.TestedStationFragmentModule;
import com.walktour.gui.singlestation.report.presenter.TestedStationFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;

import static com.walktour.gui.singlestation.dao.model.StationInfo.EXPORTED_REPORT_NO;


/**
 * 已测试基站界面
 */
public class TestedStationFragment extends BaseListFragment<StationInfo> {
    /**
     * 日志标识
     */
    private static final String TAG = "TestedStationFragment";
    /***
     * 远程报告
     */
    public final int SWITCH_FETCH_REMOTE_REPORT = 0;
    /**
     * 本地报告
     */
    public final int SWITCH_FETCH_LOCAL_REPORT = 1;
    /**
     * 界面交互类
     */
    @Inject
    TestedStationFragmentPresenter mPresenter;
    /**
     * 任务组列表
     */
    @BindView(R2.id.list_view)
    ListView mStationList;

    /***
     * 获取远程报告
     */
    @BindView(R.id.fetchremotereportbtn)
    Button fetchremotereportBtn;

    /***
     * 生成本地报告
     */
    @BindView(R.id.localreportbtn)
    Button localreportBtn;

    /***
     * 确认按钮
     */
    @BindView(R.id.confirmbtn)
    Button confirmBtn;

    /***
     * 取消按钮
     */
    @BindView(R.id.cancelbtn)
    Button cancelBtn;

    /***
     * 获取历史报告布局
     */
    @BindView(R.id.bottomFetchReportlayout)
    LinearLayout fetchReportLayout;
    /***
     * 确认或取消布局
     */
    @BindView(R.id.bottomConfirmOrCancellayout)
    LinearLayout corfirmOrCancelLayout;
    /**
     * 选择信息
     */
    @BindString(R.string.work_order_fj_select_action)
    public String msg_select;
    /**
     * 选择信息
     */
    @BindString(R.string.total_export_success_str)
    public String msg_export_statue;
    /**
     * 是否显示了确认和删除按钮
     */
    private Boolean isShowConfirmOrCancel = false;

    /***
     * 默认选择获取远程报告
     */
    private int switchBtn = SWITCH_FETCH_REMOTE_REPORT;

    public TestedStationFragment() {
        super(R.string.single_station_testedstation, R.layout.fragment_list_testedstation, R.layout.fragment_single_station_testedstation_row);
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
        DaggerTestedStationFragmentComponent.builder().testedStationFragmentModule(new TestedStationFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

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
     * 获取远程报告
     */
    @OnClick(R.id.fetchremotereportbtn)
    void fetchRemoteReport() {
        switchBtn = SWITCH_FETCH_REMOTE_REPORT;
        isShowConfirmOrCancel = !isShowConfirmOrCancel;
        if (isShowConfirmOrCancel) {
            fetchReportLayout.setVisibility(View.GONE);
            corfirmOrCancelLayout.setVisibility(View.VISIBLE);
        } else {
            fetchReportLayout.setVisibility(View.VISIBLE);
            corfirmOrCancelLayout.setVisibility(View.GONE);

        }
        notifyDataSetChanged();
    }

    /**
     * 生成本地报告
     */
    @OnClick(R.id.localreportbtn)
    void localReport() {
        switchBtn = SWITCH_FETCH_LOCAL_REPORT;
        isShowConfirmOrCancel = !isShowConfirmOrCancel;
        if (isShowConfirmOrCancel) {
            fetchReportLayout.setVisibility(View.GONE);
            corfirmOrCancelLayout.setVisibility(View.VISIBLE);
        } else {
            fetchReportLayout.setVisibility(View.VISIBLE);
            corfirmOrCancelLayout.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    /**
     * 确认按钮
     */
    @OnClick(R.id.confirmbtn)
    void confirm() {
        if (mPresenter.getSelectedStationInfo().size() <= 0) {
            ToastUtil.showToastShort(this.getContext(), msg_select);
            return;
        }
        switch (switchBtn) {
            case SWITCH_FETCH_REMOTE_REPORT:
                mPresenter.fetchRemoteReport();
                break;
            case SWITCH_FETCH_LOCAL_REPORT:
                mPresenter.fetchLocalReport();
                break;
        }
        mPresenter.loadData();
        isShowConfirmOrCancel = false;
        fetchReportLayout.setVisibility(View.VISIBLE);
        corfirmOrCancelLayout.setVisibility(View.GONE);

        notifyDataSetChanged();
    }

    /**
     * 取消按钮
     */
    @OnClick(R.id.cancelbtn)
    void cancel() {
        switch (switchBtn) {
            case SWITCH_FETCH_REMOTE_REPORT:
//                ToastUtil.showToastShort(this.getContext(), "3");
                break;
            case SWITCH_FETCH_LOCAL_REPORT:
//                ToastUtil.showToastShort(this.getContext(), "4");
                break;
        }
        isShowConfirmOrCancel = false;
        fetchReportLayout.setVisibility(View.VISIBLE);
        corfirmOrCancelLayout.setVisibility(View.GONE);
        notifyDataSetChanged();
    }

    /**
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<StationInfo> {

        /**
         * 基站选择
         */
        @BindView(R.id.station_check)
        ImageButton stationCheck;
        /**
         * 基站图片,室内还是室外
         */
        @BindView(R.id.station_img)
        ImageView stationImg;
        /**
         * 基站名称
         */
        @BindView(R.id.station_name)
        TextView stationName;
        /**
         * 基站地址
         */
        @BindView(R.id.station_address)
        TextView stationAddress;

        /***
         * 导出状态
         */
        @BindView(R.id.export_status)
        TextView exportStatus;
        /**
         * 当前的基站信息
         */
        private StationInfo stationInfo;

        /***
         * 点击选择图标是否选择
         */
        @OnClick(R.id.station_check)
        void checkSelect() {
            if (mPresenter.existStationInfo(stationInfo)) {//选中状态
                stationCheck.setImageResource(R.drawable.btn_check_off);
                mPresenter.removetationInfo(stationInfo);
            } else {
                stationCheck.setImageResource(R.drawable.btn_check_on);
                mPresenter.addStationInfo(stationInfo);
            }
        }

        @Override
        public void setData(int position, StationInfo data) {
            this.stationInfo = data;
            this.showStationImage(data);
            if (isShowConfirmOrCancel) {
                stationCheck.setVisibility(View.VISIBLE);
            } else {
                stationCheck.setVisibility(View.GONE);
            }
            this.stationName.setText(data.getName());
            this.stationAddress.setText(data.getAddress());
            this.exportStatus.setText(data.getIsExportedReport() == EXPORTED_REPORT_NO ? "" : msg_export_statue);
        }

        /**
         * 显示基站图片
         *
         * @param data 基站对象
         */
        private void showStationImage(StationInfo data) {
            if (data.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
                if (data.getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_indoor_untest);
                } else {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_indoor_tested);
                }
            } else {
                if (data.getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_outdoor_untest);
                } else {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_outdoor_tested);
                }
            }
        }
    }

}

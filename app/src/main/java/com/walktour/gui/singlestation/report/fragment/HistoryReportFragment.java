package com.walktour.gui.singlestation.report.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.gui.R;
import com.walktour.gui.report.ReportFactory;
import com.walktour.gui.report.ReportPreviewActivity;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.StationInfoReport;
import com.walktour.gui.singlestation.report.component.DaggerHistoryReportFragmentComponent;
import com.walktour.gui.singlestation.report.model.StationInfoAndReport;
import com.walktour.gui.singlestation.report.module.HistoryReportFragmentModule;
import com.walktour.gui.singlestation.report.presenter.HistoryReportFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;


/**
 * 历史报告界面
 */
public class HistoryReportFragment extends BaseListFragment<StationInfoAndReport> {
    /**
     * 日志标识
     */
    private static final String TAG = "HistoryReportFragment";
    /***
     * 批量删除
     */
    private final int SWITCH_BATCH_DELETE = 0;
    /**
     * 重新生成报告
     */
    private final int SWITCH_GENERAL_REPORT = 1;
    /**
     * 界面交互类
     */
    @Inject
    HistoryReportFragmentPresenter mPresenter;
    /**
     * 基站列表
     */
    @BindView(R.id.list_view)
    ListView mStationList;

    /***
     * 批量删除
     */
    @BindView(R.id.batchdeletebtn)
    Button batchDeleteBtn;

    /**
     * 重新生成报告
     */
    @BindView(R.id.rereportbtn)
    Button regenealReportBtn;

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
     * 获取批量删除和重新生成报告布局
     */
    @BindView(R.id.battchdeletelayout)
    LinearLayout battchDeleteLayout;
    /***
     * 确认或取消布局
     */
    @BindView(R.id.bottomConfirmOrCancellayout)
    LinearLayout corfirmOrCancelLayout;

    /**
     * 本地报告
     */
    @BindString(R.string.local_station_report)
    public String msg_local;
    /**
     * 服务器报告
     */
    @BindString(R.string.monitor_normal_server)
    public String msg_server;
    /**
     * 测试成功
     */
    @BindString(R.string.single_station_test_status_success)
    public String msg_success;
    /***
     * 测试失败
     */
    @BindString(R.string.single_station_test_status_fault)
    public String msg_failure;
    /**
     * 是否显示了确认和删除按钮
     */
    private Boolean isShowConfirmOrCancel = false;

    /***
     * 默认选择批量删除
     */
    private int switchBtn = SWITCH_BATCH_DELETE;


    public HistoryReportFragment() {
        super(R.string.single_station_historyreport, R.layout.fragment_list_historyreport, R.layout.fragment_single_station_stationreport_row);
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
        DaggerHistoryReportFragmentComponent.builder().historyReportFragmentModule(new HistoryReportFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {

    }

    /**
     * 批量删除报告
     */
    @OnClick(R.id.batchdeletebtn)
    void deleteReport() {
        switchBtn = SWITCH_BATCH_DELETE;
        isShowConfirmOrCancel = !isShowConfirmOrCancel;
        if (isShowConfirmOrCancel) {
            battchDeleteLayout.setVisibility(View.GONE);
            corfirmOrCancelLayout.setVisibility(View.VISIBLE);
        } else {
            battchDeleteLayout.setVisibility(View.VISIBLE);
            corfirmOrCancelLayout.setVisibility(View.GONE);

        }
        notifyDataSetChanged();
    }

    /**
     * 跳转到报告列表界面
     *
     * @param position
     */
    @OnItemClick(R.id.list_view)
    void jumpToNextActivity(int position) {
        ReportFactory.getInstance(this.getContext()).createHtmlFile(this.getItem(position).getStationInfoReport().getReportPath());
        Intent intent = new Intent(this.getContext(), ReportPreviewActivity.class);
        intent.putExtra("file_path", this.getItem(position).getStationInfoReport().getReportPath());
        this.startActivity(intent);
    }

    /**
     * 重新生成本地报告
     */
    @OnClick(R.id.rereportbtn)
    void localReport() {
        switchBtn = SWITCH_GENERAL_REPORT;
        isShowConfirmOrCancel = !isShowConfirmOrCancel;
        if (isShowConfirmOrCancel) {
            battchDeleteLayout.setVisibility(View.GONE);
            corfirmOrCancelLayout.setVisibility(View.VISIBLE);
        } else {
            battchDeleteLayout.setVisibility(View.VISIBLE);
            corfirmOrCancelLayout.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }

    /**
     * 确认按钮
     */
    @OnClick(R.id.confirmbtn)
    void confirm() {
        switch (switchBtn) {
            case SWITCH_BATCH_DELETE:
                mPresenter.deleteReport();
                mPresenter.loadData();
                break;
            case SWITCH_GENERAL_REPORT:
//                ToastUtil.showToastShort(this.getContext(), "2");
                break;
        }
        isShowConfirmOrCancel = false;
        battchDeleteLayout.setVisibility(View.VISIBLE);
        corfirmOrCancelLayout.setVisibility(View.GONE);
        notifyDataSetChanged();
    }

    /**
     * 取消按钮
     */
    @OnClick(R.id.cancelbtn)
    void cancel() {
        switch (switchBtn) {
            case SWITCH_BATCH_DELETE:
//                ToastUtil.showToastShort(this.getContext(), "3");
                break;
            case SWITCH_GENERAL_REPORT:
//                ToastUtil.showToastShort(this.getContext(), "4");
                break;
        }
        isShowConfirmOrCancel = false;
        battchDeleteLayout.setVisibility(View.VISIBLE);
        corfirmOrCancelLayout.setVisibility(View.GONE);
        notifyDataSetChanged();
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
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<StationInfoAndReport> {

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
         * 报表类型
         */
        @BindView(R.id.report_btn)
        TextView reportTypeBtn;

        /**
         * 报表状态
         */
        @BindView(R.id.report_msg_tv)
        TextView reportStatus;
        /**
         * 当前的基站信息
         */
        private StationInfoAndReport stationInfo;

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
        public void setData(int position, StationInfoAndReport data) {
            this.stationInfo = data;
            this.showStationImage(data);
            if (isShowConfirmOrCancel) {
                stationCheck.setVisibility(View.VISIBLE);
            } else {
                stationCheck.setVisibility(View.GONE);
            }
            this.stationName.setText(data.getStationInfo().getName());
            this.stationAddress.setText(data.getStationInfo().getAddress());
            this.reportTypeBtn.setText(data.getStationInfoReport().getType() == StationInfoReport.TYPE_LOCAL ? msg_local : msg_server);
            this.reportStatus.setText(data.getStationInfo().getTestStatus() == StationInfo.TEST_STATUS_SUCCESS ? msg_success : msg_failure);
        }

        /**
         * 显示基站图片
         *
         * @param data 基站对象
         */
        private void showStationImage(StationInfoAndReport data) {
            if (data.getStationInfo().getType() == SingleStationDaoManager.STATION_TYPE_INDOOR) {
                if (data.getStationInfo().getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_indoor_untest);
                } else {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_indoor_tested);
                }
            } else {
                if (data.getStationInfo().getTestStatus() == StationInfo.TEST_STATUS_INIT) {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_outdoor_untest);
                } else {
                    this.stationImg.setBackgroundResource(R.drawable.singlestation_outdoor_tested);
                }
            }
        }

    }

}

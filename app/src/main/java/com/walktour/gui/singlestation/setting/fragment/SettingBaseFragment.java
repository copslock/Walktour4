package com.walktour.gui.singlestation.setting.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.TotalStruct;
import com.walktour.base.gui.activity.BaseBottomControlBar;
import com.walktour.base.gui.fragment.BaseHolder;
import com.walktour.base.gui.fragment.BaseListFragment;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.R2;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;

import butterknife.BindView;

/**
 * 阈值设置页面
 * Created by jun.luo on 2017/7/23.
 */

public abstract class SettingBaseFragment extends BaseListFragment<TreeNode> {

    /**
     * 日志标识
     */
    private static final String TAG = "SettingFragment";

    /**
     * 测试指标列表
     */
    @BindView(R2.id.list_view)
    ListView mSettingListView;
    /**
     * 底部工具栏
     */
    @BindView(R.id.bottom_bar)
    BaseBottomControlBar mBaseBottomControlBar;

    /**
     * @param titleId          标题ID
     * @param fragmentLayoutId 视图关联的布局资源ID
     * @param rowLayoutId      行布局资源ID
     */
    public SettingBaseFragment(int titleId, int fragmentLayoutId, int rowLayoutId) {
        super(titleId, fragmentLayoutId, rowLayoutId);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    protected AbsListView getListView() {
        return this.mSettingListView;
    }


    @Override
    protected void onCreateView() {
        //设置还原默认设置,
        mBaseBottomControlBar.setButtonText(0, R.string.main_menu_resetdefault, 16);
        mBaseBottomControlBar.setButtonListener(0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDefaultSetting();
            }
        });

    }

    /**
     * 实现加载默认设置功能
     */
    protected abstract void loadDefaultSetting();

    @Override
    protected BaseHolder createViewHolder() {
        LogUtil.e(TAG, "--createViewHolder()--");
        return new ViewHolder();
    }

    public class ViewHolder extends BaseHolder<TreeNode> {

        /**
         * 测试场景
         */
        @BindView(R.id.setting_threshold_project)
        TextView mSettingThresholdProject;

        /**
         * 测试任务
         */
        @BindView(R.id.setting_threshold_task)
        TextView mSettingTask;

        /**
         * 阈值指标
         */
        @BindView(R.id.setting_threshold_index)
        TextView mSettingThresholdIndex;

        /**
         * 判断方法
         */
        @BindView(R.id.setting_threshold_opera)
        TextView mSettingThresholdOpera;

        /**
         * 判断值
         */
        @BindView(R.id.setting_threhold_value)
        TextView mSettingThresholdValue;

        /**
         * 单位值
         */
        @BindView(R.id.setting_threshold_unit)
        TextView mSettingThresholdUint;

        /**
         * 场景行
         */
        @BindView(R.id.setting_threshold_project_layout)
        LinearLayout mSettingSceneLayout;

        /**
         * 阈值设置行
         */
        @BindView(R.id.setting_threshold_task_layout)
        LinearLayout mSettingThresholdLayout;

        @Override
        public void setData(int position, TreeNode data) {
            int iLevel = data.getLevel();
            LogUtil.e(TAG, TAG + "-- setData -- Level: " + Integer.toString(iLevel));
            if (0 == iLevel) {
                showSettingThresholdSceneRow(data);
            } else {
                showSettingThresholdTaskRow(data);
            }
        }

        /**
         * 显示阈值所属场景
         *
         * @param data 节点对象
         */
        private void showSettingThresholdSceneRow(TreeNode data) {
            LogUtil.e(TAG, "-- " + TAG + ": showSettingThresholdSceneRow" + "--");

            this.mSettingSceneLayout.setVisibility(View.VISIBLE);
            this.mSettingThresholdLayout.setVisibility(View.GONE);

            int sceneType = (Integer) data.getObject();
            mSettingThresholdProject.setText(this.getTestSceneResId(sceneType));
        }

        /**
         * 获得测试场景资源ID
         *
         * @param sceneType 测试场景
         * @return 测试场景资源ID
         */
        private int getTestSceneResId(int sceneType) {
            switch (sceneType) {
                case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                    return R.string.single_station_scene_coverage_test;
                case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                    return R.string.single_station_scene_handover_test;
                case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                    return R.string.single_station_scene_signal_leakage_test;
                case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                    return R.string.single_station_scene_performance_test;
                /*case SingleStationDaoManager.SCENE_TYPE_PARK:
                    return R.string.single_station_scene_park_test;*/
            }
            return R.string.single_station_validation;
        }

        /**
         * 显示阈值设置行
         *
         * @param data 数据
         */
        private void showSettingThresholdTaskRow(TreeNode data) {
            LogUtil.e(TAG, "-- " + TAG + ": showSettingThresholdTaskRow" + "--");
            this.mSettingSceneLayout.setVisibility(View.GONE);
            this.mSettingThresholdLayout.setVisibility(View.VISIBLE);

            ThresholdSetting thresholdSetting = (ThresholdSetting) data.getObject();
            this.mSettingTask.setText(getTestTaskResId(thresholdSetting.getTestTask()));
            this.mSettingThresholdIndex.setText(getThresholdKeyResId(thresholdSetting.getThresholdKey()));
            this.mSettingThresholdOpera.setText(thresholdSetting.getOperator());

            String strValue = Float.toString(thresholdSetting.getThresholdValue());
            if (strValue.indexOf(".") > 0) {
                strValue = strValue.replaceAll("0+?$", "");
                strValue = strValue.replaceAll("[.]$", "");
            }
            this.mSettingThresholdValue.setText(strValue);
            this.mSettingThresholdUint.setText(thresholdSetting.getThresholdUnit());
        }
    }

    /**
     * 获取测试任务的名称资源ID
     *
     * @param testTask 测试任务
     * @return 测试任务的名称资源ID
     */
    private int getTestTaskResId(String testTask) {
        switch (testTask) {
            case "FTP_Download":
                return R.string.act_task_ftpdownload;
            case "Idle":
                return R.string.act_task_empty;
            case "Attach":
                return R.string.act_task_attach;
            case "FTP_Upload":
                return R.string.act_task_ftpupload;
            case "MOC_CSFB":
            case "MOC_VOLTE":
                return R.string.act_task_initiativecall;
        }

        return R.string.single_station_validation;
    }

    /**
     * 获得要显示的阈值名称资源ID
     *
     * @param thresholdKey 阈值Key
     * @return 显示的阈值名称资源ID
     */
    private int getThresholdKeyResId(String thresholdKey) {
        if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSCoverMileage.name())) {
            return R.string.single_station_threshold_rs_coverage_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPAttemptHandoverTimes.name())) {
            return R.string.single_station_threshold_attempt_handover_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._FTPHandoverSuccessRate.name())) {
            return R.string.single_station_threshold_handover_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._PCISample.name())) {
            return R.string.single_station_threshold_pci_sample;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._RSRPAverage.name())) {
            return R.string.single_station_threshold_rsrp;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._tryTimes.name())) {
            return R.string.single_station_threshold_attempt_times;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._successRate.name())) {
            return R.string.single_station_threshold_succ_ratio;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._SpeedAverage.name())) {
            return R.string.single_station_threshold_data_average_rate;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_tryTimes.name())) {
            return R.string.single_station_threshold_attampt_call_times_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_establishedRate.name())) {
            return R.string.single_station_threshold_establised_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_csfb;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_successRate.name())) {
            return R.string.single_station_threshold_call_succ_ratio_volte;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._volte_eSRVCC.name())) {
            return R.string.single_station_threshold_handover_succ_times_esrvcc;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnDelay.name())) {
            return R.string.single_station_threshold_csfb_return_delay;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_connectDelay.name())) {
            return R.string.single_station_threshold_csfb_connect_delay;
        } else if (thresholdKey.equals(TotalStruct.TotalSingleStation._csfb_returnSuccessRate.name())) {
            return R.string.single_station_threshold_csfb_return_success_rate;
        }

        return R.string.single_station_validation;
    }

}

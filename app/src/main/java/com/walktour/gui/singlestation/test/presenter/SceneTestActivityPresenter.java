package com.walktour.gui.singlestation.test.presenter;

import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.base.gui.presenter.BaseActivityPresenter;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.CellInfo;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.test.activity.SceneTestActivity;
import com.walktour.gui.singlestation.test.fragment.SceneTestBaseFragment;
import com.walktour.gui.singlestation.test.fragment.SceneTestCellFragment;
import com.walktour.gui.singlestation.test.fragment.SceneTestStationFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 场景测试界面交互类
 */
public class SceneTestActivityPresenter extends BaseActivityPresenter {
    /**
     * 日志标识
     */
    private static final String TAG = "SceneTestActivityPresenter";
    /**
     * 关联界面
     */
    private SceneTestActivity mActivity;

    public SceneTestActivityPresenter(SceneTestActivity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    protected String getLogTAG() {
        return TAG;
    }

    /**
     * 获取初始化的视图列表
     *
     * @return 视图列表
     */
    public List<BaseFragment> getInitFragments() {
        List<BaseFragment> fragmentList = new ArrayList<>();
        Set<String> flagSet = new HashSet<>();//用于标志是否添加页面
        StationInfo stationInfo = this.getIntent().getParcelableExtra("station_info");
        int cellCount = 0;
        List<SceneInfo> sceneInfoList = stationInfo.getSceneInfoList();
        Collections.sort(sceneInfoList);
        for (SceneInfo sceneInfo : sceneInfoList) {
            int titleNameId;
            switch (sceneInfo.getSceneType()) {
                case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                    titleNameId = R.string.single_station_scene_coverage;
                    break;
                case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                    titleNameId = R.string.single_station_scene_handover;
                    break;
                case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                    titleNameId = R.string.single_station_scene_signal_leakage;
                    break;
                case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                    if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_INDOOR)
                        titleNameId = R.string.single_station_scene_performance;
                    else {
                        titleNameId = R.string.single_station_scene_performance_cell;
                    }
                    break;
                default:
                    titleNameId = R.string.single_station_scene_park;
                    break;
            }
            String titleName = this.mActivity.getString(titleNameId);
            if (stationInfo.getType() == SingleStationDaoManager.STATION_TYPE_OUTDOOR && sceneInfo.getSceneType() == SingleStationDaoManager.SCENE_TYPE_PERFORMANCE)
                titleName += String.valueOf(++cellCount);
            String flag = String.valueOf(sceneInfo.getCellId()) + "_" +String.valueOf(sceneInfo.getSceneType());//将cellId_sceneType放入set内，用于判断是否需要添加新界面
            if(!flagSet.contains(flag)){
                flagSet.add(flag);
                fragmentList.add(this.createSceneTestFragment(sceneInfo, titleName));
            }
        }
        return fragmentList;
    }

    /**
     * 生成场景测试视图
     *
     * @param sceneInfo 场景对象
     * @param titleName 页签标题
     * @return 场景测试视图
     */
    private SceneTestBaseFragment createSceneTestFragment(SceneInfo sceneInfo, String titleName) {
        SceneTestBaseFragment fragment;
        if (sceneInfo.getCellId() > 0) {
            fragment = new SceneTestCellFragment();
            ((SceneTestCellFragment) fragment).setCellInfo(this.getCellInfo(sceneInfo.getCellId()));
        } else {
            fragment = new SceneTestStationFragment();
        }
        fragment.setSceneInfo(sceneInfo);
        fragment.setTitleName(titleName);
        return fragment;
    }

    /**
     * 获取小区信息
     *
     * @param cellId 小区ID
     * @return 小区信息
     */
    private CellInfo getCellInfo(int cellId) {
        StationInfo stationInfo = this.getIntent().getParcelableExtra("station_info");
        for (CellInfo cellInfo : stationInfo.getCellInfoList()) {
            if (cellInfo.getCellId() == cellId) {
                return cellInfo;
            }
        }
        return null;
    }
}

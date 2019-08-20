package com.walktour.gui.singlestation.test.service;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.SceneInfo;
import com.walktour.gui.singlestation.dao.model.StationInfo;
import com.walktour.gui.singlestation.dao.model.TaskTestResult;
import com.walktour.gui.singlestation.dao.model.ThresholdTestResult;
import com.walktour.gui.singlestation.test.model.LocalStationCallBack;
import com.walktour.gui.singlestation.test.model.StationTestResultCallBack;
import com.walktour.gui.singlestation.test.model.SurveySiteCallBack;
import com.walktour.gui.singlestation.test.model.TaskTestResultCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地基站服务服务类
 */
public class LocalStationService {
    /**
     * 日志标识
     */
    private static final String TAG = "LocalStationService";
    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;

    public LocalStationService(Context context) {
        this.mDaoManager = SingleStationDaoManager.getInstance(context);
    }

    /**
     * 查询基站信息关联的基站勘查信息
     *
     * @param stationId 基站ID
     * @param callBack  回调类
     */
    public void getSurveyStation(long stationId, SurveySiteCallBack callBack) {
        callBack.onSuccess(this.mDaoManager.getSurveyStationInfo(stationId));
    }

    /**
     * 本地基站列表查询
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param callBack  回调类
     */
    public void getLocalStationList(double latitude, double longitude, LocalStationCallBack callBack) {
        List<StationInfo> list = this.mDaoManager.getStationInfoList();
        for (StationInfo station : list) {
            if (latitude != -9999) {
                long distance = (long) DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(station.getLatitude(), station.getLongitude()));
                station.setDistance(distance + "m");
            } else {
                station.setDistance("0m");
            }
        }
        callBack.onSuccess(list);
    }

    /**
     * 获取指定基站的场景测试结果
     *
     * @param stationId 基站ID
     * @param callBack  回调类
     */
    public void getTaskTestResultList(long stationId, TaskTestResultCallBack callBack) {
        callBack.onSuccess((ArrayList<TaskTestResult>) this.mDaoManager.getTaskTestResultList(stationId));
    }

    /**
     * 获取指定基站的测试结果
     *
     * @param context   上下文
     * @param stationId 基站ID
     * @param callBack  回调类
     */
    public void getStationTestResultList(Context context, long stationId, StationTestResultCallBack callBack) {
        LogUtil.d(TAG, "---getStationTestResultList---stationId:" + stationId);
        List<TreeNode> list = new ArrayList<>();
        List<SceneInfo> sceneInfoList = this.mDaoManager.getSceneInfoList(stationId);
        for (SceneInfo sceneInfo : sceneInfoList) {
            String sceneType = "";
            switch (sceneInfo.getSceneType()) {
                case SingleStationDaoManager.SCENE_TYPE_COVERAGE:
                    sceneType = context.getString(R.string.single_station_scene_coverage_test);
                    break;
                case SingleStationDaoManager.SCENE_TYPE_HANDOVER:
                    sceneType = context.getString(R.string.single_station_scene_handover_test);
                    break;
                case SingleStationDaoManager.SCENE_TYPE_SIGNAL_LEAKAGE:
                    sceneType = context.getString(R.string.single_station_scene_signal_leakage_test);
                    break;
                case SingleStationDaoManager.SCENE_TYPE_PERFORMANCE:
                    sceneType = context.getString(R.string.single_station_scene_performance_test);
                    break;
                /*case SingleStationDaoManager.SCENE_TYPE_PARK:
                    sceneType = context.getString(R.string.single_station_scene_park_test);
                    break;*/
            }
            TreeNode treeNode = new TreeNode();
            treeNode.setLevel(0);
            treeNode.setObject(sceneType);
            list.add(treeNode);
            for (TaskTestResult taskTestResult : sceneInfo.getTaskTestResultList()) {
                for (ThresholdTestResult thresholdTestResult : taskTestResult.getThresholdTestResultList()) {
                    TreeNode node = new TreeNode();
                    node.setLevel(1);
                    node.setObject(thresholdTestResult);
                    list.add(node);
                }
            }
        }
        callBack.onSuccess(list);
    }

}

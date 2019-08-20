package com.walktour.gui.singlestation.setting.service;

import android.content.Context;

import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.gui.model.TreeNode;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.singlestation.dao.SingleStationDaoManager;
import com.walktour.gui.singlestation.dao.model.ThresholdSetting;
import com.walktour.gui.singlestation.setting.model.SettingModel;
import com.walktour.gui.singlestation.setting.model.SettingModelCallBack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 阈值设置服务类
 * Created by luojun on 2017/7/5.
 */

public class SettingService {
    /**
     * 日志标识
     */
    private static final String TAG = "SettingService";

    /**
     * 数据库操作类
     */
    private SingleStationDaoManager mDaoManager;
    /**
     * XML操作类, 可返回读取到的列表信息
     */
    private ConfigSettingManager mConfigSetting = null;

    public SettingService(Context context) {
        this.mConfigSetting = ConfigSettingManager.getInstance(context);
        this.mDaoManager = SingleStationDaoManager.getInstance(context);
    }

    /**
     * 保存编辑的阈值设置
     *
     * @param setting  阈值设置对象
     * @param callBack 回调对象
     */
    public void editThresholdSetting(ThresholdSetting setting, SimpleCallBack callBack) {
        this.mDaoManager.save(setting);
        callBack.onSuccess();
    }

    /**
     * 设置基站阈值为默认值
     *
     * @param stationType 基站类型
     */
    public void setThresholdDefaultSetting(int stationType, SettingModelCallBack callBack) {
        List<SettingModel> settingModelList = this.mConfigSetting.getSettingModelList();
        List<ThresholdSetting> list = this.mDaoManager.getThresholdSettingList(stationType);
        for (SettingModel settingModel : settingModelList) {
            if (settingModel.getStationType() != stationType)
                continue;
            boolean isFind = false;
            for (ThresholdSetting thresholdSetting : list) {
                if (thresholdSetting.getSceneType() == settingModel.getSceneType()
                        && thresholdSetting.getTestTask().equals(settingModel.getTestTask())
                        && thresholdSetting.getThresholdKey().equals(settingModel.getThresholdKey())) {
                    isFind = true;
                    thresholdSetting.setOperator(settingModel.getOperator());
                    thresholdSetting.setThresholdValue(settingModel.getThresholdValue());
                    this.mDaoManager.save(thresholdSetting);
                    break;
                }
            }
            if (!isFind) {
                ThresholdSetting thresholdSetting = new ThresholdSetting();
                thresholdSetting.setThresholdValue(settingModel.getThresholdValue());
                thresholdSetting.setOperator(settingModel.getOperator());
                thresholdSetting.setSceneType(settingModel.getSceneType());
                thresholdSetting.setStationType(settingModel.getStationType());
                thresholdSetting.setTestTask(settingModel.getTestTask());
                thresholdSetting.setThresholdKey(settingModel.getThresholdKey());
                thresholdSetting.setThresholdUnit(settingModel.getThresholdUnit());
                this.mDaoManager.save(thresholdSetting);
            }
        }
        if (!settingModelList.isEmpty() || !list.isEmpty())
            this.getThresholdSettingModeList(stationType, callBack);
    }

    /**
     * 获得基站当前的阈值
     *
     * @param stationType 基站类型
     * @param callBack    回调类
     */
    public void getThresholdSettingModeList(int stationType, SettingModelCallBack callBack) {
        LogUtil.d(TAG, "----getThresholdSettingModeList----");
        List<ThresholdSetting> list = this.mDaoManager.getThresholdSettingList(stationType);
        if (list == null || list.isEmpty()) {
            this.setThresholdDefaultSetting(stationType, callBack);
            return;
        }
        List<TreeNode> treeNodeList = new ArrayList<>();
        Set<Integer> sceneTypeSet = new HashSet<>();
        for (ThresholdSetting thresholdSetting : list) {
            if (!sceneTypeSet.contains(thresholdSetting.getSceneType())) {
                TreeNode treeNode = new TreeNode();
                treeNode.setLevel(0);
                treeNode.setObject(thresholdSetting.getSceneType());
                treeNodeList.add(treeNode);
                sceneTypeSet.add(thresholdSetting.getSceneType());
            }
            TreeNode treeNode = new TreeNode();
            treeNode.setLevel(1);
            treeNode.setObject(thresholdSetting);
            treeNodeList.add(treeNode);
        }
        callBack.onSuccess(treeNodeList);
    }

}

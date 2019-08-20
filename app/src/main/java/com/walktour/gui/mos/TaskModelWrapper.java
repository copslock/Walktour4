package com.walktour.gui.mos;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.MTCTestConfig;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.service.bluetoothmos.command.BaseCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * 解决主被叫不同model兼容问题
 *
 * @author zhicheng.chen
 * @date 2019/4/12
 */
public class TaskModelWrapper {
    public boolean isAlternaterTest;
    public int callMosCount;
    public int polqaSample;
    public int keepTime;
    public String mosAlgorithm;
    public String sampleType;
    public String calcMode;
    public boolean isSwb;
    public boolean isMocTest;
    public boolean isRealTimeCalculation;
    public boolean isMultiTest;
    public int cycleTimes;
    public long cycleInterval;
    public List<BaseCommand.FileType> cycleDatas;
    public boolean isWecall;

    public TaskModelWrapper(TaskInitiativeCallModel model) {
        isAlternaterTest = model.isAlternateTest();
        callMosCount = model.getCallMOSCount();
        polqaSample = model.getPolqaSample();
        keepTime = model.getKeepTime();
        isSwb = model.getPolqaCalc() == 1;
        mosAlgorithm = model.getTestConfig().getMosAlgorithm();
        sampleType = model.getTestConfig().getSampleType();
        calcMode = model.getTestConfig().getCalcMode();
        isRealTimeCalculation = model.isRealtimeCalculation();

        isMultiTest = model.isMultiTest();
        if (isMultiTest) {
            cycleTimes = model.getCycleTimes();
            cycleInterval = model.getCycleInterval();
            String multiCycleDataName = model.getMultiCycleDataName();
            setCycleDatas(multiCycleDataName);
        } else {
            cycleTimes = 999;
            cycleInterval = 0;
            setCycleDatas(getFileType().getName());
        }

        isMocTest = true;
    }

    public TaskModelWrapper(TaskWeCallModel model) {
        isAlternaterTest = false;
        callMosCount = model.getCallMOSCount();
        polqaSample = model.getPolqaSample();
        keepTime = model.getKeepTime();
        isSwb = model.getPolqaCalc() == 1;
        mosAlgorithm = model.getTestConfig().getMosAlgorithm();
        sampleType = model.getTestConfig().getSampleType();
        calcMode = model.getTestConfig().getCalcMode();
        isRealTimeCalculation = model.isRealtimeCalculation();
        cycleTimes = 999;
        cycleInterval = 0;
        setCycleDatas(getFileType().getName());
        isMocTest = model.getTaskName().equals(WalkStruct.TaskType.WeCallMoc.getTypeName());
        isWecall = true;
    }

    public TaskModelWrapper(TaskPassivityCallModel model) {
        isAlternaterTest = model.isAlternateTest();
        callMosCount = model.getCallMOSCount();
        polqaSample = model.getPolqaSample();
        isSwb = model.getPolqaCalc() == 1;
        keepTime = 5400;
        mosAlgorithm = model.getTestConfig().getMosAlgorithm();
        sampleType = model.getTestConfig().getSampleType();
        calcMode = model.getTestConfig().getCalcMode();
        isRealTimeCalculation = model.isRealtimeCalculation();

        isMultiTest = model.isMultiTest();
        if (isMultiTest) {
            cycleTimes = model.getCycleTimes();
            cycleInterval = model.getCycleInterval();
            String multiCycleDataName = model.getMultiCycleDataName();
            setCycleDatas(multiCycleDataName);
        } else {
            cycleTimes = 999;
            cycleInterval = 0;
            setCycleDatas(getFileType().getName());
        }

        isMocTest = false;
    }


    private void setCycleDatas(String multiCycleDataName) {
        String[] split = null;
        if (!StringUtil.isNullOrEmpty(multiCycleDataName)) {
            split = multiCycleDataName.split(",");
        }
        if (split != null) {
            for (BaseCommand.FileType type : BaseCommand.FileType.values()) {
                for (String name : split) {
                    if (name.equals(type.getName())) {
                        if (cycleDatas == null) {
                            cycleDatas = new ArrayList<>();
                        }
                        cycleDatas.add(type);
                    }
                }

            }
        }
    }

    public BaseCommand.FileType getFileType() {
        BaseCommand.FileType fileType = null;
        if (callMosCount == TaskModel.MOS_PESQ) {
            fileType = BaseCommand.FileType.pesq_8k;
        } else if (callMosCount == TaskModel.MOS_POLQA) {
            if (polqaSample == TaskModel.POLQA_16K) {
                fileType = BaseCommand.FileType.polqa_16k;
            } else if (polqaSample == TaskModel.POLQA_48K) {
                fileType = BaseCommand.FileType.polqa_48k;
            } else {
                fileType = BaseCommand.FileType.polqa_8k;
            }
        }
        return fileType;
    }

    public String getFileExtend() {
        String _Ext = mosAlgorithm + "_Default";
        if (MOCTestConfig.MOSAlgorithm_POLQA.equals(mosAlgorithm)
                || MTCTestConfig.MOSALGORITHM_POLQA.equals(mosAlgorithm)) {
            String SEP = "_";
            int index = sampleType.indexOf(" ");
            String sampleName = index == -1 ? sampleType : sampleType.substring(index + 1);
            _Ext = _Ext + SEP + sampleName + SEP + calcMode;
        }

        return _Ext;
    }


}

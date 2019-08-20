package com.walktour.service.test.MultipleAppEvent;

import com.dingli.ott.weixin.WeiXinEvent;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttQQAppTest;
import com.walktour.service.test.OttWeChatTest;

import java.util.Map;

/**
 * WeChat 语音测试事件
 *
 * @author zhicheng.chen
 * @date 2018/8/21
 */
public class WeChatEventor extends BaseEventor {

    private boolean mSuccess = true;
    private int mFailedCode;
    private String mFailedDesc;
    protected OttWeChatTest.MultipleAppTestHandler mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

    public WeChatEventor(OttWeChatTest.MultipleAppTestHandler handler) {
        this.mHandler = handler;
        mTaskModel = handler.getTaskModel();
    }

    @Override
    public void handleStartEvent() {
        int pictureQuality = 0;
        switch (this.mTaskModel.getSendPictureType()) {
            case 0:
                pictureQuality = 1048576;
                break;
            case 1:
                pictureQuality = 3145728;
                break;
        }
        Event(RcuEventCommand.WeChat_Action_Start)
                .addStringBuffer(mTaskModel.getSendText())
                .addInteger(pictureQuality)
                .addInteger(mTaskModel.getVoiceDuration())
                .addStringBuffer(mHandler.mIp)
                .writeToRcu(System.currentTimeMillis());
    }

    @Override
    public void handleProcessEvent(Map<String, String> datasMap) {
        String eventType = datasMap.get(SUB_EVENT);
        switch (eventType) {
            case SUB_EVENT_MPACTION_START:
                handleStartEvent(datasMap);
                break;
            case SUB_EVENT_MPACTION_SUCCESS:
                Event(RcuEventCommand.WeChat_Action_Success)
                        .addInteger(getInt(datasMap, MULTIPLE_ACTION_TYPE))
                        .addInteger(getInt(datasMap, INNER_DELAY))
                        .addInteger(getInt(datasMap, UP_BYTES))
                        .addInteger(getInt(datasMap, DOWN_BYTES))
                        .writeToRcu(System.currentTimeMillis());
                break;
            case SUB_EVENT_MPACTION_FAILED:
                mSuccess = false;
                mFailedCode = getInt(datasMap, CODE);
                mFailedDesc = datasMap.get(DESC) == null ? "" : datasMap.get(DESC);
                Event(RcuEventCommand.WeChat_Action_Failure)
                        .addInteger(getInt(datasMap, MULTIPLE_ACTION_TYPE))
                        .addInteger(getInt(datasMap, INNER_DELAY))
                        .addInteger(mFailedCode)
                        .addStringBuffer(mFailedDesc)
                        .writeToRcu(System.currentTimeMillis());
                break;
            default:
                break;
        }
    }

    private void handleStartEvent(Map<String, String> datasMap) {
        int actionType = getInt(datasMap, MULTIPLE_ACTION_TYPE);
        EventBytes eventBytes = Event(RcuEventCommand.WeChat_Action_Start)
                .addInteger(actionType);
        if (WeiXinEvent.WEIXIN_ACTION_TYPE_SEND_MSG == actionType) {
            eventBytes.addStringBuffer(mTaskModel.getSendText())
                    .addInteger(mTaskModel.getSendText().getBytes().length)
                    .writeToRcu(System.currentTimeMillis());
        } else if (WeiXinEvent.WEIXIN_ACTION_TYPE_SEND_IMG == actionType) {
            int pictureQuality = 0;
            String imgType = "";
            switch (this.mTaskModel.getSendPictureType()) {
                case 0:
                    pictureQuality = 1048576;
                    imgType = "1M";
                    break;
                case 1:
                    pictureQuality = 3145728;
                    imgType = "3M";
                    break;
            }
            eventBytes.addStringBuffer(imgType)
                    .addInteger(pictureQuality)
                    .writeToRcu(System.currentTimeMillis());
        } else if (WeiXinEvent.WEIXIN_ACTION_TYPE_SEND_VOICE == actionType) {
            eventBytes.addStringBuffer("WeChat voice msg")
                    .addInteger(mTaskModel.getVoiceDuration())
                    .writeToRcu(System.currentTimeMillis());
        }
    }

    @Override
    public void handleEndEvent() {
        if (mSuccess) {
            Event(RcuEventCommand.WeChat_Test_Success)
                    .writeToRcu(System.currentTimeMillis());
        } else {
            Event(RcuEventCommand.WeChat_Test_Failure)
                    .addInteger(mFailedCode)
                    .addStringBuffer(mFailedDesc)
                    .writeToRcu(System.currentTimeMillis());
        }
    }
}

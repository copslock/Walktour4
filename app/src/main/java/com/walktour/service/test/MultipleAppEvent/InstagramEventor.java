package com.walktour.service.test.MultipleAppEvent;

import com.dingli.ott.facebook.FacebookEvent;
import com.dingli.ott.instagram.InstagramEvent;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttInstagramTest;
import com.walktour.service.test.OttSinaWeiboTest;

import java.util.Map;

/**
 * Instagram 测试事件
 *
 * @author zhicheng.chen
 * @date 2018/8/21
 */
public class InstagramEventor extends BaseEventor {

    private boolean mSuccess = true;
    private int mFailedCode;
    private String mFailedDesc;
    protected OttInstagramTest.MultipleAppTestHandler mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

    public InstagramEventor(OttInstagramTest.MultipleAppTestHandler handler) {
        this.mHandler = handler;
        mTaskModel = handler.getTaskModel();
    }

    @Override
    public void handleStartEvent() {
        Event(RcuEventCommand.Multiple_App_Instagram_Test_Start)
                .addStringBuffer(mTaskModel.getSendText())
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
                Event(RcuEventCommand.Multiple_App_Instagram_Action_Success)
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
                Event(RcuEventCommand.Multiple_App_Instagram_Action_Failed)
                        .addInteger(getInt(datasMap, MULTIPLE_ACTION_TYPE))
                        .addInteger(getInt(datasMap, INNER_DELAY))
                        .addInteger(mFailedCode)
                        .addStringBuffer(mFailedDesc)
                        .writeToRcu(System.currentTimeMillis());
                break;
        }
    }
    private void handleStartEvent(Map<String, String> datasMap) {
        int actionType = getInt(datasMap, MULTIPLE_ACTION_TYPE);
        EventBytes eventBytes = Event(RcuEventCommand.WhatsApp_Action_Start)
                .addInteger(actionType);
        String text = "";
        int size = 0;
        switch (actionType) {
            case InstagramEvent.INSTAGRAM_ACTION_TYPE_LOAD_PICTURE:
                break;
            case InstagramEvent.INSTAGRAM_ACTION_TYPE_SEARCH_HASHTAGS:
                text = this.mTaskModel.getSendText();
                size = text.getBytes().length;
                break;
        }

        eventBytes.addInteger(size)
                .addStringBuffer(text)
                .writeToRcu(System.currentTimeMillis());

    }


    @Override
    public void handleEndEvent() {
        if (mSuccess) {
            Event(RcuEventCommand.Multiple_App_Instagram_Test_END)
                    .addInteger(0)
//                    .addInteger(0)
//                    .addStringBuffer("success")
                    .writeToRcu(System.currentTimeMillis());
        } else {
            Event(RcuEventCommand.Multiple_App_Instagram_Test_END)
                    .addInteger(1)
                    .addInteger(mFailedCode)
                    .addStringBuffer(mFailedDesc)
                    .writeToRcu(System.currentTimeMillis());
        }
    }
}

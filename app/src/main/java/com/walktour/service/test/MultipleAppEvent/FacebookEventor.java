package com.walktour.service.test.MultipleAppEvent;

import com.dingli.ott.facebook.FacebookEvent;
import com.dingli.ott.skype.SkypeEvent;
import com.dingli.ott.whatsapp.WhatsAppEvent;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttFacebookTest;
import com.walktour.service.test.OttSinaWeiboTest;

import java.util.Map;

/**
 * Facebook 测试事件
 *
 * @author zhicheng.chen
 * @date 2018/8/21
 */
public class FacebookEventor extends BaseEventor {

    private boolean mSuccess = true;
    private int mFailedCode;
    private String mFailedDesc;
    protected OttFacebookTest.MultipleAppTestHandler mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

    public FacebookEventor(OttFacebookTest.MultipleAppTestHandler handler) {
        this.mHandler = handler;
        mTaskModel = handler.getTaskModel();
    }

    @Override
    public void handleStartEvent() {
        Event(RcuEventCommand.Multiple_App_Facebook_Test_Start)
                .addStringBuffer(mTaskModel.getSendText())
                .addInteger(mTaskModel.getSendPictureType() + 1)
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
                Event(RcuEventCommand.Multiple_App_Facebook_Action_Success)
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
                Event(RcuEventCommand.Multiple_App_Facebook_Action_Failed)
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
        EventBytes eventBytes = Event(RcuEventCommand.Multiple_App_Facebook_Action_Start)
                .addInteger(actionType);
        String text = "";
        int size = 0;
        switch (actionType) {
            case FacebookEvent.FACEBOOK_ACTION_TYPE_LOAD_FEEDS:
                text = this.mTaskModel.getSendText();
                size = text.getBytes().length;
                break;
            case FacebookEvent.FACEBOOK_ACTION_TYPE_UPDATE_STATUS:
                break;
            case FacebookEvent.FACEBOOK_ACTION_TYPE_POST_PHOTO:
                text = this.mTaskModel.getSendPictureType() == 0 ? "1M" : "3M";
                size = this.mTaskModel.getSendPictureType() == 0 ? 1048576 : 3145728;
                break;
            case FacebookEvent.FACEBOOK_ACTION_TYPE_LOAD_FRIEND_LISTS:
                break;
        }

        eventBytes.addStringBuffer(text)
                .addInteger(size)
                .writeToRcu(System.currentTimeMillis());

    }

    @Override
    public void handleEndEvent() {
        if (mSuccess) {
            Event(RcuEventCommand.Multiple_App_Facebook_Test_END)
                    .addInteger(0)
//                    .addInteger(0)
//                    .addStringBuffer("success")
                    .writeToRcu(System.currentTimeMillis());
        } else {
            Event(RcuEventCommand.Multiple_App_Facebook_Test_END)
                    .addInteger(1)
                    .addInteger(mFailedCode)
                    .addStringBuffer(mFailedDesc)
                    .writeToRcu(System.currentTimeMillis());
        }
    }
}

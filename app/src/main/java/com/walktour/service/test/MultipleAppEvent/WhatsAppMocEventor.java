package com.walktour.service.test.MultipleAppEvent;

import com.dingli.ott.whatsappvf.WhatsAppVFEvent;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttWhatsAppMocTest;

import java.util.Map;

/**
 * WhatsApp语音主叫
 *
 * @author zhicheng.chen
 * @date 2018/8/16
 */
public class WhatsAppMocEventor extends BaseEventor {
    private static final String TAG = WhatsAppMocEventor.class.getSimpleName();
    private long mAttemptMillions; // attempt 事件的时间
    private long mEstablishMillions;// establish 事件的时间
    private boolean mRing;
    private int DEFAULT = -1;
    private int SUCCESS = 1;
    private int FAIL = 0;
    private int mStatus = DEFAULT;
    protected OttWhatsAppMocTest.MultipleAppTestHandler mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

    public WhatsAppMocEventor(OttWhatsAppMocTest.MultipleAppTestHandler handler, TaskMultipleAppTestModel taskModel) {
        this.mHandler = handler;
        this.mTaskModel = taskModel;
    }

    @Override
    public void handleStartEvent() {
        Event(RcuEventCommand.WhatsApp_MO_Dial)
                .addInteger(0)
                .addStringBuffer(mTaskModel.getContactName())
                .addInteger(2)
                .addInteger(mTaskModel.getTaskTimeout() * 1000)
                .addInteger(mTaskModel.getVoiceDuration())
                .addStringBuffer(mHandler.mIp)
                .writeToRcu(System.currentTimeMillis());

    }

    @Override
    public void handleProcessEvent(Map<String, String> datasMap) {
        String eventType = datasMap.get(SUB_EVENT);
        String actionTypeStr = datasMap.get(MULTIPLE_ACTION_TYPE);
        int actionType = -1;
        if (!StringUtil.isEmpty(actionTypeStr)) {
            actionType = Integer.parseInt(actionTypeStr);
        }
        switch (eventType) {
            case SUB_EVENT_MPACTION_START:

                break;
            case SUB_EVENT_MPACTION_SUCCESS:
                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLER_DIAL_AUDIO == actionType
                        && mAttemptMillions == 0) {
                    mAttemptMillions = System.currentTimeMillis();
                    Event(RcuEventCommand.WhatsApp_MO_Attempt)
                            .writeToRcu(mAttemptMillions);
                }

                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLER_SESSION_AUDIO == actionType) {
                    //                    Log.w(TAG, "WECALL_MO_ESTABLISH");
                    mEstablishMillions = System.currentTimeMillis();
                    Event(RcuEventCommand.WhatsApp_MO_Establish)
                            .addInteger(getInt(datasMap, INNER_DELAY))
                            .writeToRcu(mEstablishMillions);
                }

                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLER_SESSION_AUDIO == actionType
                        && mStatus == DEFAULT) {
                    mStatus = SUCCESS;
                    //发送一次成功事件
                    Event(RcuEventCommand.WhatsApp_MO_Hangup)
                            .addInteger(0)
                            .addInteger(-9999)
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
            case SUB_EVENT_MPACTION_FAILED:
                if (/*WeiXinVFEvent.MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_SESSION_AUDIO == actionType
						&& */mStatus == DEFAULT) {
                    mStatus = FAIL;
                    if ("time out".equals(datasMap.get(DESC))) {
                        Event(RcuEventCommand.WhatsApp_MO_Block)
                                .addInteger(getInt(datasMap, CODE))
                                .addStringBuffer(datasMap.get(DESC))
                                .writeToRcu(System.currentTimeMillis());
                    }
                    if ("drop".equals(datasMap.get(DESC))) {
                        Event(RcuEventCommand.WhatsApp_MO_Drop)
                                .addInteger(getInt(datasMap, CODE))
                                .addStringBuffer(datasMap.get(DESC))
                                .writeToRcu(System.currentTimeMillis());
                    }

                    //没出现成功标识，就是失败
                    Event(RcuEventCommand.WhatsApp_MO_Hangup)
                            .addInteger(1)
                            .addInteger(getInt(datasMap, CODE))
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
            case SUB_EVENT_MPACTION_QOS:
                if (actionType == 18009) {
                    writeQos(datasMap);
                }
                break;
            case SUB_EVENT_MPACTION_STATUS:
                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLER_DIAL_AUDIO_RING == actionType && getInt(datasMap, INNER_DELAY) != 0) {
                    mRing = true;
                    Event(RcuEventCommand.WhatsApp_MO_Setup)
                            .addInteger(getInt(datasMap, INNER_DELAY))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void handleEndEvent() {
        //如果没有接通，则该事件的发生时间为0
        if (mEstablishMillions != 0) {
            int delay = (int) (System.currentTimeMillis() - mEstablishMillions);
            Event(RcuEventCommand.WhatsApp_MO_End)
                    .addInteger(delay)
                    .writeToRcu(System.currentTimeMillis());
        } else {
            Event(RcuEventCommand.WhatsApp_MO_End)
                    .writeToRcu(System.currentTimeMillis());
        }
    }

    public boolean isSuccess() {
        return mStatus == SUCCESS;
    }

    private void writeQos(Map<String, String> datasMap) {
        EventBytes.Builder(WalktourApplication.getAppContext())
                .addInteger(WalkCommonPara.WhatsApp)
                .addInteger(0x000013C8)
                .addInteger(getInt(datasMap, "send_time"))
                .addInteger(getInt(datasMap, "send_time"))
                .addInteger(getInt(datasMap, "recv_time"))
                .addInteger(getInt(datasMap, "send_bytes"))
                .addInteger(getInt(datasMap, "send_pkgs"))
                .addInteger(getInt(datasMap, "recv_bytes"))
                .addInteger(getInt(datasMap, "recv_pkgs"))
                .addInteger(getInt(datasMap, "lost_pkgs"))
                .addDouble(getDouble(datasMap, "lost_fraction"))
                .addInteger(getInt(datasMap, "send_bytes_cur"))
                .addInteger(getInt(datasMap, "send_pkgs_cur"))
                .addInteger(getInt(datasMap, "recv_bytes_cur"))
                .addInteger(getInt(datasMap, "recv_pkgs_cur"))
                .addInteger(getInt(datasMap, "lost_pkgs_cur"))
                .addDouble(getDouble(datasMap, "lost_fraction_cur"))
                .writeToRcu(WalkCommonPara.MsgDataFlag_A);
    }
}

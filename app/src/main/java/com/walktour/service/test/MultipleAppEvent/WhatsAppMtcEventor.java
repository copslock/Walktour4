package com.walktour.service.test.MultipleAppEvent;

import com.dingli.ott.whatsappvf.WhatsAppVFEvent;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.service.test.OttWhatsAppMtcTest;

import java.util.Map;

/**
 * WhatsApp语音被叫
 *
 * @author zhicheng.chen
 * @date 2018/8/16
 */
public class WhatsAppMtcEventor extends BaseEventor {

    private boolean mRing;
    private long mAttemptMillions; // attempt 事件的时间
    private long mEstablishMillions;// establish 事件的时间
    private int DEFAULT = -1;
    private int SUCCESS = 1;
    private int FAIL = 0;
    private int mStatus = DEFAULT;
    protected OttWhatsAppMtcTest.MultipleAppTestHandler mHandler;
    protected TaskMultipleAppTestModel mTaskModel;

    public WhatsAppMtcEventor(OttWhatsAppMtcTest.MultipleAppTestHandler handler, TaskMultipleAppTestModel taskModel) {
        this.mHandler = handler;
        this.mTaskModel = taskModel;
    }

    @Override
    public void handleStartEvent() {
        Event(RcuEventCommand.WhatsApp_MT_Dial)
                .addStringBuffer(mTaskModel.getContactName())
                .addInteger(mTaskModel.getTaskTimeout() * 1000)
                .addStringBuffer(mHandler.mIp)
                .writeToRcu(System.currentTimeMillis());
        //        		Log.i("czc", "getTaskTimeout=" + mTaskModel.getTaskTimeout() * 1000 + ",getVoiceDuration=" + mTaskModel.getVoiceDuration() + ",ip=" + mHandler.mIp);
    }

    @Override
    public void handleProcessEvent(Map<String, String> datasMap) {
        String eventType = datasMap.get(SUB_EVENT);
        String actionTypeStr = datasMap.get(MULTIPLE_ACTION_TYPE);
        int actionType = -1;
        if (!StringUtil.isEmpty(actionTypeStr)) {
            actionType = Integer.parseInt(actionTypeStr);
        }
        //        		Log.i("czc", "eventType=" + eventType + ",actiontype=" + actionType + ",desc=" + datasMap.get(DESC) + ",code=" + datasMap.get(CODE));
        switch (eventType) {
            case SUB_EVENT_MPACTION_START:

                break;
            case SUB_EVENT_MPACTION_SUCCESS:

                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLEE_DIAL_AUDIO == actionType) {
                    mAttemptMillions = System.currentTimeMillis();
                    Event(RcuEventCommand.WhatsApp_MT_Attempt)
                            .writeToRcu(mAttemptMillions);
                }

                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLEE_SESSION_AUDIO == actionType) {
                    mEstablishMillions = System.currentTimeMillis();
                    Event(RcuEventCommand.WhatsApp_MT_Establish)
                            .addInteger(getInt(datasMap, INNER_DELAY))
                            .writeToRcu(mEstablishMillions);
                }

                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLEE_SESSION_AUDIO == actionType
                        && mStatus == DEFAULT) {
                    mStatus = SUCCESS;
                    //发送一次成功事件
                    Event(RcuEventCommand.WhatsApp_MT_Hangup)
                            .addInteger(0)
                            .addInteger(-9999)
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
            case SUB_EVENT_MPACTION_FAILED:
                if (/*WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLED_SESSION_AUDIO == actionType
						&&*/ mStatus == DEFAULT) {
                    mStatus = FAIL;
                    //					Log.i("czc", "failed=" + datasMap.get(DESC) + "," + datasMap.get(CODE));

                    Event(RcuEventCommand.WhatsApp_MT_Drop)
                            .addInteger(getInt(datasMap, CODE))
                            .addStringBuffer(datasMap.get(DESC))
                            .addStringBuffer(datasMap.get(INNER_DELAY))
                            .writeToRcu(System.currentTimeMillis());

                    //没出现成功标识，就是失败
                    Event(RcuEventCommand.WhatsApp_MT_Hangup)
                            .addInteger(1)
                            .addInteger(getInt(datasMap, CODE))
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
            case SUB_EVENT_MPACTION_QOS:
                if (!mRing) {
                    Event(RcuEventCommand.WhatsApp_MT_Block)
                            .addInteger(getInt(datasMap, CODE))
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());
                }

                if (actionType == 19009) {
                    writeQos(datasMap);
                }

                break;
            case SUB_EVENT_MPACTION_STATUS:
                if (WhatsAppVFEvent.MULTIPLE_APP_WHATSAPP_ACTION_TYPE_CALLEE_DIAL_AUDIO_RING == actionType) {
                    Event(RcuEventCommand.WhatsApp_MT_Setup)
                            .addInteger(getInt(datasMap, INNER_DELAY))
                            .writeToRcu(System.currentTimeMillis());
                    mRing = true;
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
            Event(RcuEventCommand.WhatsApp_MT_End)
                    .addInteger(delay)
                    .writeToRcu(System.currentTimeMillis());
        } else {
            Event(RcuEventCommand.WhatsApp_MT_End)
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

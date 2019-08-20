package com.walktour.service.test.MultipleAppEvent;

import android.util.Log;

import com.dingli.ott.weixinvf.WeiXinVFEvent;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.StringUtil;
import com.walktour.gui.mos.CaculateModeFacade;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.service.test.OttWeCallMocTest;

import java.util.Map;

/**
 * 微信语音主叫
 *
 * @author zhicheng.chen
 * @date 2018/8/16
 */
public class WecallMocEventor extends BaseEventor {
    private static final String TAG = WecallMocEventor.class.getSimpleName();
    private long mAttemptMillions; // attempt 事件的时间
    private long mEstablishMillions;// establish 事件的时间
    private boolean mRing;
    private int DEFAULT = -1;
    private int SUCCESS = 1;
    private int FAIL = 0;
    private int mStatus = DEFAULT;
    protected OttWeCallMocTest.MultipleAppTestHandler mHandler;
    protected TaskWeCallModel mTaskModel;
    private CaculateModeFacade mCaculateModeFacade;

    public WecallMocEventor(OttWeCallMocTest.MultipleAppTestHandler handler, TaskWeCallModel taskModel) {
        this.mHandler = handler;
        this.mTaskModel = taskModel;
    }

    @Override
    public void handleStartEvent() {
        Event(RcuEventCommand.WECALL_MO_DIAL)
                .addStringBuffer(mTaskModel.getContactName())
                .addInteger(mTaskModel.getConnectTime() * 1000)
                .addInteger(mTaskModel.getKeepTime())
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

                if (WeiXinVFEvent.MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_DIAL_AUDIO == actionType
                        && mAttemptMillions == 0) {
                    mAttemptMillions = System.currentTimeMillis();
                    Event(RcuEventCommand.WECALL_MO_ATTEMPT)
                            .writeToRcu(mAttemptMillions);
                }

                if (WeiXinVFEvent.MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_SESSION_AUDIO == actionType) {
                    //                    Log.w(TAG, "WECALL_MO_ESTABLISH");
                    mEstablishMillions = System.currentTimeMillis();
                    Event(RcuEventCommand.WECALL_MO_ESTABLISH)
                            .addInteger(/*getInt(datasMap, INNER_DELAY)*1000*/(int) (System.currentTimeMillis() - mAttemptMillions))
                            .writeToRcu(mEstablishMillions);

                    Log.w("czc", "接听了");
                    if (mCaculateModeFacade != null) {
                        mCaculateModeFacade.start();
                    }
                }

                break;
            case SUB_EVENT_MPACTION_SUCCESS:
                if (WeiXinVFEvent.MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_SESSION_AUDIO == actionType
                        && mStatus == DEFAULT) {
                    mStatus = SUCCESS;
                    //发送一次成功事件
                    Event(RcuEventCommand.WECALL_MO_HANGUP)
                            .addInteger(0)
                            .addInteger(-9999)
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());

                    Log.w("czc", "挂断了");
                    if (mCaculateModeFacade != null) {
                        mCaculateModeFacade.stop();
                    }
                }
                break;
            case SUB_EVENT_MPACTION_FAILED:
                if (/*WeiXinVFEvent.MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_SESSION_AUDIO == actionType
						&& */mStatus == DEFAULT) {
                    mStatus = FAIL;
                    if ("time out".equals(datasMap.get(DESC))) {
                        Event(RcuEventCommand.WECALL_MO_BLOCK)
                                .addInteger(getInt(datasMap, CODE))
                                .addStringBuffer(datasMap.get(DESC))
                                .writeToRcu(System.currentTimeMillis());
                    }
                    if ("drop".equals(datasMap.get(DESC))) {
                        Event(RcuEventCommand.WECALL_MO_DROP)
                                .addInteger(getInt(datasMap, CODE))
                                .addStringBuffer(datasMap.get(DESC))
                                .writeToRcu(System.currentTimeMillis());
                    }

                    //没出现成功标识，就是失败
                    Event(RcuEventCommand.WECALL_MO_HANGUP)
                            .addInteger(1)
                            .addInteger(getInt(datasMap, CODE))
                            .addStringBuffer(datasMap.get(DESC))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
            case SUB_EVENT_MPACTION_QOS:


                break;
            case SUB_EVENT_MPACTION_STATUS:
                if (WeiXinVFEvent.MULTIPLE_APP_WEIXIN_ACTION_TYPE_CALLER_DIAL_AUDIO_RING == actionType) {
                    mRing = true;
                    Event(RcuEventCommand.WECALL_MO_SETUP)
                            .addInteger(/*getInt(datasMap, INNER_DELAY)*1000*/(int) (System.currentTimeMillis() - mAttemptMillions))
                            .writeToRcu(System.currentTimeMillis());
                }
                break;
        }
    }

    @Override
    public void handleEndEvent() {
        //如果没有接通，则该事件的发生时间为0
        if (mEstablishMillions != 0) {
            int delay = (int) (System.currentTimeMillis() - mEstablishMillions);
            Event(RcuEventCommand.WECALL_MO_END)
                    .addInteger(delay)
                    .writeToRcu(System.currentTimeMillis());
        } else {
            Event(RcuEventCommand.WECALL_MO_END)
                    .writeToRcu(System.currentTimeMillis());
        }
    }

    public boolean isSuccess() {
        return mStatus == SUCCESS;
    }

    public long getDelayTime() {
        if (isSuccess()) {
            return mEstablishMillions - mAttemptMillions;
        }
        return 0;
    }

    public void setMosManager(CaculateModeFacade caculateModeFacade) {
        mCaculateModeFacade = caculateModeFacade;
    }
}

package com.walktour.base.gui.model;

/**
 * service类返回给界面的消息对象
 * Created by wangk on 2017/9/6.
 */

public class ServiceMessage {
    /**
     * 消息活动类型
     */
    private String mAction;
    /**
     * 消息包含对象
     */
    private Object mObject;

    public ServiceMessage(String action) {
        this.mAction = action;
    }

    public String getAction() {
        return mAction;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }
}

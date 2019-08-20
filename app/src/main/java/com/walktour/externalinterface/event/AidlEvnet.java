package com.walktour.externalinterface.event;

import android.os.Bundle;

/**
 * 外部App调用Walktour的服务，EventBus post的事件
 *
 * @author zhicheng.chen
 * @date 2018/12/7
 */
public class AidlEvnet {
    public static final int NOTIFY_START_TEST = 0;
    public static final int NOTIFY_STOP_TEST = 1;
    public static final int NOTIFY_FILE_CREATE = 2;
    public int eventType;
    public Bundle bundle;

    public AidlEvnet(int eventType) {
        this.eventType = eventType;
    }

    public AidlEvnet(int eventType,Bundle bundle) {
        this.eventType = eventType;
        this.bundle = bundle;
    }
}

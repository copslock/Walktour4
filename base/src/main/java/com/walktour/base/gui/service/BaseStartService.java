package com.walktour.base.gui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.walktour.base.gui.model.ServiceMessage;
import com.walktour.base.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 基础启动服务类，不可绑定
 * Created by wangk on 2017/6/29.
 */

public abstract class BaseStartService extends Service {
    /**
     * 获取日志标识
     *
     * @return 日志标识
     */
    protected abstract String getLogTAG();

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(this.getLogTAG(), "----onCreate----");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(this.getLogTAG(), "----onStartCommand----");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发送消息给界面类
     *
     * @param message 消息对象
     */
    protected void sendMessageToActivity(ServiceMessage message) {
        LogUtil.d(this.getLogTAG(), "----sendMessageToActivity----action:" + message.getAction());
        EventBus.getDefault().post(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getLogTAG(), "----onDestroy----");
    }

    @Nullable
    @Override
    final public IBinder onBind(Intent intent) {
        return null;
    }
}

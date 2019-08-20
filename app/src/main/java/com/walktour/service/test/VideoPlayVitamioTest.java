package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;

import com.dingli.dmplayer.sdktest.DMPlayerAPI;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.FailedInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.KpiInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.MsgInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.PlayQosInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.QosInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvDropInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.RecvFinishInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartFailedInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.ReproductionStartInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.SegInfo;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsfail_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnsstart_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.dnssucc_info;
import com.dingli.dmplayer.sdktest.DMPlayerAPI.send_get_info;
import com.dingli.dmplayer.sdktest.SurfacePlayer;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.service.dmplayer.VideoPlayVitamioActivity;

/**
 * Vitamio版实现视频播放测试业务
 *
 * @author jianchao.wang
 *
 */

@SuppressLint("SdCardPath")
public class VideoPlayVitamioTest extends VideoPlayTestBase implements DMPlayerAPI.EventListener {
    /** 日志标识 */
    private static final String TAG = "VideoPlayVitamioTest";

    /** 视频播放广播监听类 */
    private MyReceiver myReceiver = new MyReceiver();
    /** 播放器 */
    private SurfacePlayer mSurfacePlayer;

    @Override
    public void stopTest() {
        Message msg = mHandler.obtainMessage(TEST_STOP, "1");
        mHandler.sendMessage(msg);
        sendMsgToPioneer(",bSuccess=1");
        if (taskModel.isVideoShow()) {
            Intent intent = new Intent(BROADCAST_STOP);
            mContext.sendBroadcast(intent);
        } else {
            mSurfacePlayer.StopTest();
        }
    }

    /**
     * 显示视频播放界面
     */
    private void showVideoPlayActivity() {
        try {
            LogUtil.d(TAG, "start to prep args");
            Intent intent = new Intent(getBaseContext(), VideoPlayVitamioActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putSerializable("taskModel", this.taskModel);
            intent.putExtras(bundle);
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
            Message msg = mHandler.obtainMessage(TEST_STOP, "Error");
            msg.sendToTarget();
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void startTest() {
        if (this.taskModel.isVideoShow()) {
            Intent intent = new Intent(BROADCAST_START);
            mContext.sendBroadcast(intent);
        } else {
            LogUtil.d(TAG, "-----startTest-----");
            DMPlayerAPI.StartTestInfo info = new DMPlayerAPI.StartTestInfo();
            info.ps_call = this.taskModel.getPlayType() == 1;
            info.url = this.taskModel.getUrl();
            info.media_quality = super.getMediaQuality();
            info.playtime_ms = this.getPlayTimeout() * 1000;
            info.media_play_time_ms = this.getPlayTimeout() * 1000;
            info.media_play_percent = (taskModel.getPlayType() == 0 ? 0
                    : taskModel.getPlayTimerMode() == 0 ? 0 : taskModel.getPlayPercentage());
            info.use_media_play_percent = (taskModel.getPlayType() == 1 && taskModel.getPlayTimerMode() == 1);
            info.buffering_time_ms = (taskModel.getPlayType() == 0 ? 0
                    : taskModel.getBufTimerMode() == 0 ? taskModel.getMaxBufferTimeout() * 1000 : 0);
            info.buffering_percent = (taskModel.getPlayType() == 0 ? 0
                    : taskModel.getBufTimerMode() == 0 ? 0 : taskModel.getMaxBufferPercentage());
            info.use_buffering_percent = (taskModel.getPlayType() == 1 && taskModel.getBufTimerMode() == 1);
            info.buffering_counts = (taskModel.getPlayType() == 0 ? 0 : taskModel.getMaxBufCounts());
            info.prebuffer_time_ms = taskModel.getBufThred() * 1000;
            info.rebuffer_time_ms = taskModel.getBufThred() * 1000;
            mSurfacePlayer.StartTest(info);
        }
    }

    /**
     * 获取播放超时设置
     * @return 播放超时设置
     */
    private int getPlayTimeout() {
        int playTime = 0;
        switch (this.taskModel.getPlayType()) {
            case 0:
                return this.taskModel.getPlayTimeout();
            case 1:
                switch (this.taskModel.getPlayTimerMode()) {
                    case 0:
                        return this.taskModel.getPlayDuration();
                    case 1:
                        return this.taskModel.getPlayPercentage();
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return playTime;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(tag, "-----onStartCommand-----");
        int startFlag = super.onStartCommand(intent, flags, startId);

        if (taskModel.isVideoShow()) {
            this.regeditBroadcast();
            this.showVideoPlayActivity();
        } else {
            mSurfacePlayer = new SurfacePlayer(null, false, false, this, getApplicationInfo().uid, getApplicationContext());
        }
        new RunTest().start();

        return startFlag;
    }

    /**
     * 注册广播监听
     */
    private void regeditBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_FIRST_DATA_ARRIVED);
        intentFilter.addAction(BROADCAST_INITED);
        intentFilter.addAction(BROADCAST_INIT_FAILED);
        intentFilter.addAction(BROADCAST_MSG);
        intentFilter.addAction(BROADCAST_PLAY_QOS_ARRIVED);
        intentFilter.addAction(BROADCAST_PLAY_FINISH);
        intentFilter.addAction(BROADCAST_QOS_ARRIVED);
        intentFilter.addAction(BROADCAST_QUIT);
        intentFilter.addAction(BROADCAST_REBUFFERING_END);
        intentFilter.addAction(BROADCAST_REBUFFERING_START);
        intentFilter.addAction(BROADCAST_RECV_DROP);
        intentFilter.addAction(BROADCAST_RECV_FINISH);
        intentFilter.addAction(BROADCAST_REPRODUCTION_START);
        intentFilter.addAction(BROADCAST_REPRODUCTION_START_FAILED);
        intentFilter.addAction(BROADCAST_CONNECT_FAILED);
        intentFilter.addAction(BROADCAST_CONNECT_START);
        intentFilter.addAction(BROADCAST_CONNECT_SUCCESS);
        intentFilter.addAction(BROADCAST_DNS_FAILED);
        intentFilter.addAction(BROADCAST_DNS_START);
        intentFilter.addAction(BROADCAST_DNS_SUCCESS);
        intentFilter.addAction(BROADCAST_KPIS_REPORT);
        intentFilter.addAction(BROADCAST_SENT_GET);
        intentFilter.addAction(BROADCAST_SENT_GET_FAILED);
        intentFilter.addAction(BROADCAST_URL_PARSE_FAILED);
        intentFilter.addAction(BROADCAST_URL_PARSE_START);
        intentFilter.addAction(BROADCAST_URL_PARSE_SUCCESS);
        intentFilter.addAction(BROADCAST_SEGMENT_REPORT);
        registerReceiver(myReceiver, intentFilter);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long realTime = intent.getLongExtra("RealTime", 0);
            if (realTime == 0)
                realTime = System.currentTimeMillis() * 1000;
            if (intent.getAction().equals(BROADCAST_INITED)) {
                onInited(realTime);
            } else if (intent.getAction().equals(BROADCAST_INIT_FAILED)) {
                FailedInfo info = (FailedInfo) intent.getSerializableExtra("Info");
                onInitFailed(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_FIRST_DATA_ARRIVED)) {
                onFirstDataArrived(realTime);
            } else if (intent.getAction().equals(BROADCAST_REPRODUCTION_START)) {
                ReproductionStartInfo info = (ReproductionStartInfo) intent.getSerializableExtra("Info");
                onReproductionStart(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_REPRODUCTION_START_FAILED)) {
                ReproductionStartFailedInfo info = (ReproductionStartFailedInfo) intent.getSerializableExtra("Info");
                onReproductionStartFailed(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_QOS_ARRIVED)) {
                QosInfo info = (QosInfo) intent.getSerializableExtra("Info");
                onQosArrived(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_RECV_FINISH)) {
                RecvFinishInfo info = (RecvFinishInfo) intent.getSerializableExtra("Info");
                onRecvFinish(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_RECV_DROP)) {
                RecvDropInfo info = (RecvDropInfo) intent.getSerializableExtra("Info");
                onRecvDrop(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_REBUFFERING_START)) {
                onReBufferingStart(realTime);
            } else if (intent.getAction().equals(BROADCAST_REBUFFERING_END)) {
                onReBufferingEnd(realTime);
            } else if (intent.getAction().equals(BROADCAST_PLAY_QOS_ARRIVED)) {
                PlayQosInfo info = (PlayQosInfo) intent.getSerializableExtra("Info");
                onPlayQosArrived(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_PLAY_FINISH)) {
                onPlayFinish(realTime);
            } else if (intent.getAction().equals(BROADCAST_QUIT)) {
                onQuit();
            } else if (intent.getAction().equals(BROADCAST_MSG)) {
                MsgInfo info = (MsgInfo) intent.getSerializableExtra("Info");
                onMsg(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_CONNECT_FAILED)) {
                onConnectFailed(realTime);
            } else if (intent.getAction().equals(BROADCAST_CONNECT_START)) {
                onConnectStart(realTime);
            } else if (intent.getAction().equals(BROADCAST_CONNECT_SUCCESS)) {
                onConnectSuccess(realTime);
            } else if (intent.getAction().equals(BROADCAST_DNS_FAILED)) {
                dnsfail_info info = (dnsfail_info) intent.getSerializableExtra("Info");
                onDNSResolveFailed(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_DNS_START)) {
                dnsstart_info info = (dnsstart_info) intent.getSerializableExtra("Info");
                onDNSResolveStart(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_DNS_SUCCESS)) {
                dnssucc_info info = (dnssucc_info) intent.getSerializableExtra("Info");
                onDNSResolveSuccess(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_KPIS_REPORT)) {
                KpiInfo info = (KpiInfo) intent.getSerializableExtra("Info");
                onKPIsReport(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_SENT_GET)) {
                send_get_info info = (send_get_info) intent.getSerializableExtra("Info");
                onSendGet(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_SENT_GET_FAILED)) {
                FailedInfo info = (FailedInfo) intent.getSerializableExtra("Info");
                onSendGetFailed(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_URL_PARSE_FAILED)) {
                FailedInfo info = (FailedInfo) intent.getSerializableExtra("Info");
                onUrlParseFailed(info, realTime);
            } else if (intent.getAction().equals(BROADCAST_URL_PARSE_START)) {
                onUrlParseStart(realTime);
            } else if (intent.getAction().equals(BROADCAST_URL_PARSE_SUCCESS)) {
                onUrlParseSuccess(realTime);
            } else if (intent.getAction().equals(BROADCAST_SEGMENT_REPORT)) {
                SegInfo info = (SegInfo) intent.getSerializableExtra("Info");
                onSegmentReport(info, realTime);
            }
        }

    }

    public void onDestroy() {
        LogUtil.v(TAG, "--onDestroy--");
        super.onDestroy();
        if (this.taskModel.isVideoShow())
            this.unregisterReceiver(myReceiver);
        // com.walktour.service.test.VideoPlay
        // 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
        UtilsMethod.killProcessByPname("com.walktour.service.test.VideoPlayVitamioTest", false);

    }

    /**
     * 获取实际时间
     *
     * @return
     */
    private long getRealTime() {
        return System.currentTimeMillis() * 1000;
    }

    @Override
    public void onConnectFailed(FailedInfo info) {
        super.onConnectFailed(this.getRealTime());

    }

    @Override
    public void onConnectStart() {
        super.onConnectStart(this.getRealTime());

    }

    @Override
    public void onConnectSuccess() {
        super.onConnectSuccess(this.getRealTime());

    }

    @Override
    public void onDNSFailed(dnsfail_info info) {
        super.onDNSResolveFailed(info, this.getRealTime());

    }

    @Override
    public void onDNSStart(dnsstart_info info) {
        super.onDNSResolveStart(info, this.getRealTime());

    }

    @Override
    public void onDNSSuccess(dnssucc_info info) {
        super.onDNSResolveSuccess(info, this.getRealTime());

    }

    @Override
    public void onFirstDataArrived() {
        super.onFirstDataArrived(this.getRealTime());

    }

    @Override
    public void onInitFailed(FailedInfo info) {
        super.onInitFailed(info, this.getRealTime());

    }

    @Override
    public void onInitFailed(DMPlayerAPI.YoutubeInitFailedInfo info) {
        super.onInitFailed(info, this.getRealTime());
    }

    @Override
    public void onInited() {
        super.onInited(this.getRealTime());

    }

    @Override
    public void onKPISReport(KpiInfo info) {
        super.onKPIsReport(info, this.getRealTime());

    }

    @Override
    public void onMsg(MsgInfo info) {
        super.onMsg(info, this.getRealTime());

    }

    @Override
    public void onPlayFinish() {
        super.onPlayFinish(this.getRealTime());

    }

    @Override
    public void onPlayQosArrived(PlayQosInfo info) {
        super.onPlayQosArrived(info, this.getRealTime());

    }

    @Override
    public void onQosArrived(QosInfo info) {
        super.onQosArrived(info, this.getRealTime());

    }

    @Override
    public void onReBufferingEnd() {
        super.onReBufferingEnd(this.getRealTime());

    }

    @Override
    public void onReBufferingStart() {
        super.onReBufferingStart(this.getRealTime());

    }

    @Override
    public void onRecvDrop(RecvDropInfo info) {
        super.onRecvDrop(info, this.getRealTime());

    }

    @Override
    public void onRecvFinish(RecvFinishInfo info) {
        super.onRecvFinish(info, this.getRealTime());

    }

    @Override
    public void onReproductionStart(ReproductionStartInfo info) {
        super.onReproductionStart(info, this.getRealTime());

    }

    @Override
    public void onReproductionStartFailed(ReproductionStartFailedInfo info) {
        super.onReproductionStartFailed(info, this.getRealTime());

    }

    @Override
    public void onSendGet(send_get_info info) {
        super.onSendGet(info, this.getRealTime());

    }

    @Override
    public void onSendGetFailed(FailedInfo info) {
        super.onSendGetFailed(info, this.getRealTime());

    }

    @Override
    public void onUrlParseFailed(FailedInfo info) {
        super.onUrlParseFailed(info, this.getRealTime());

    }

    @Override
    public void onUrlParseStart() {
        super.onUrlParseStart(this.getRealTime());

    }

    @Override
    public void onUrlParseSuccess() {
        super.onUrlParseSuccess(this.getRealTime());

    }

    @Override
    public void onSegReport(SegInfo info) {
        super.onSegmentReport(info, this.getRealTime());

    };

}

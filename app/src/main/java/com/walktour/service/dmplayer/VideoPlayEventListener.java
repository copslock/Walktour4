package com.walktour.service.dmplayer;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

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
import com.walktour.base.util.LogUtil;
import com.walktour.service.test.VideoPlayTestBase;

/**
 * 视频播放事件监听类
 * 
 * @author jianchao.wang
 *
 */
public class VideoPlayEventListener implements DMPlayerAPI.EventListener {
	/** 日志标识 */
	private final static String TAG = "VideoPlayEventListener";
	/** 上下文 */
	private Context mContext;
	/** 是否设置了实际时间 */
	private boolean isSetRealTime = false;
	/** 事件实际时间 */
	private long mRealTime = 0;

	public VideoPlayEventListener(Activity activity) {
		this.mContext = activity;
	}

	public VideoPlayEventListener(Service service) {
		this.mContext = service;
	}

	public VideoPlayEventListener(Context context) {
		this.mContext = context;
	}

	/**
	 * 发送系统广播
	 * 
	 * @param intent
	 *          广播内容
	 */
	private void sendBroadcast(Intent intent) {
		this.mContext.sendBroadcast(intent);
	}

	/**
	 * 获取事件实际时间
	 * 
	 * @return
	 */
	private long getRealTime() {
		if (this.isSetRealTime) {
			this.isSetRealTime = false;
			return this.mRealTime;
		}
		return System.currentTimeMillis() * 1000;
	}

	/**
	 * 设置事件实际时间
	 * 
	 * @param time
	 *          实际时间
	 */
	public void setRealTime(long time) {
		this.mRealTime = time;
		this.isSetRealTime = true;
	}

	@Override
	public void onInited() {
		LogUtil.d(TAG, "-----onInited-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_INITED);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onFirstDataArrived() {
		LogUtil.d(TAG, "-----onFirstDataArrived-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_FIRST_DATA_ARRIVED);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onReproductionStart(ReproductionStartInfo info) {
		LogUtil.d(TAG, "-----onReproductionStart-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_REPRODUCTION_START);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onReproductionStartFailed(ReproductionStartFailedInfo info) {
		LogUtil.d(TAG, "-----onReproductionStartFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_REPRODUCTION_START_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onQosArrived(QosInfo info) {
		LogUtil.d(TAG, "-----onQosArrived-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_QOS_ARRIVED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onRecvFinish(RecvFinishInfo info) {
		LogUtil.d(TAG, "-----onRecvFinish-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_RECV_FINISH);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onRecvDrop(RecvDropInfo info) {
		LogUtil.d(TAG, "-----onRecvDrop-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_RECV_DROP);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onReBufferingStart() {
		LogUtil.d(TAG, "-----onReBufferingStart-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_REBUFFERING_START);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onReBufferingEnd() {
		LogUtil.d(TAG, "-----onReBufferingEnd-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_REBUFFERING_END);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onPlayFinish() {
		LogUtil.d(TAG, "-----onPlayFinish-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_PLAY_FINISH);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onQuit() {
		LogUtil.d(TAG, "-----onQuit-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_QUIT);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onMsg(MsgInfo info) {
		LogUtil.d(TAG, "-----onMsg-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_MSG);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onConnectFailed(FailedInfo info) {
		LogUtil.d(TAG, "-----onConnectFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_CONNECT_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onConnectStart() {
		LogUtil.d(TAG, "-----onConnectStart-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_CONNECT_START);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onConnectSuccess() {
		LogUtil.d(TAG, "-----onConnectSuccess-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_CONNECT_SUCCESS);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onDNSFailed(dnsfail_info info) {
		LogUtil.d(TAG, "-----onDNSFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_DNS_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onDNSStart(dnsstart_info info) {
		LogUtil.d(TAG, "-----onDNSStart-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_DNS_START);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onDNSSuccess(dnssucc_info info) {
		LogUtil.d(TAG, "-----onDNSSuccess-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_DNS_SUCCESS);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onKPISReport(KpiInfo info) {
		LogUtil.d(TAG, "-----onKPISReport-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_KPIS_REPORT);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onSendGet(send_get_info info) {
		LogUtil.d(TAG, "-----onSendGet-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_SENT_GET);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onSendGetFailed(FailedInfo info) {
		LogUtil.d(TAG, "-----onSendGetFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_SENT_GET_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onUrlParseFailed(FailedInfo info) {
		LogUtil.d(TAG, "-----onUrlParseFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_URL_PARSE_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onUrlParseStart() {
		LogUtil.d(TAG, "-----onUrlParseStart-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_URL_PARSE_START);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onUrlParseSuccess() {
		LogUtil.d(TAG, "-----onUrlParseSuccess-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_URL_PARSE_SUCCESS);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onInitFailed(FailedInfo info) {
		LogUtil.d(TAG, "-----onInitFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_INIT_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onInitFailed(DMPlayerAPI.YoutubeInitFailedInfo info) {
		LogUtil.d(TAG, "-----onYoutubeInitFailed-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_YOUTUBE_INIT_FAILED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onPlayQosArrived(PlayQosInfo info) {
		LogUtil.d(TAG, "-----onPlayQosArrived-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_PLAY_QOS_ARRIVED);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

	@Override
	public void onSegReport(SegInfo info) {
		LogUtil.d(TAG, "-----onSegReport-----");
		Intent intent = new Intent();
		intent.setAction(VideoPlayTestBase.BROADCAST_SEGMENT_REPORT);
		intent.putExtra("Info", info);
		intent.putExtra("RealTime", this.getRealTime());
		this.sendBroadcast(intent);
	}

}

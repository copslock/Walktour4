package com.walktour.service.dmplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dingli.dmplayer.sdktest.DMPlayerAPI;
import com.dingli.dmplayer.sdktest.SurfacePlayer;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.service.test.VideoPlayVitamioTest;

import io.vov.vitamio.widget.VideoView;

/**
 * Vitamio版视频播放显示界面
 * 
 * @author jianchao.wang
 *
 */
public class VideoPlayVitamioActivity extends Activity implements OnClickListener {
	/** 日志标识 */
	private static final String TAG = "VideoPlayVitamioActivity";
	/** 视图界面 */
	private VideoView mVideoView;
	/** 播放器 */
	private SurfacePlayer mSurfacePlayer;
	/** 参数对象 */
	private TaskVideoPlayModel taskModel;
	/** 视频播放广播监听类 */
	private MyReceiver myReceiver = new MyReceiver();
	/** 停止测试按钮 */
	private Button mStopBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogUtil.d(TAG, "-----onCreate-----");
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			taskModel = (TaskVideoPlayModel) bundle.getSerializable("taskModel");
		}
		setContentView(R.layout.activity_dmplayer_vitamio);
		this.findView();
		mSurfacePlayer = new SurfacePlayer(mVideoView, true, true, new VideoPlayEventListener(this),
				getApplicationInfo().uid, getApplicationContext());
		this.regeditBroadcast();
	}

	/**
	 * 注册广播监听
	 */
	private void regeditBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(VideoPlayVitamioTest.BROADCAST_START);
		intentFilter.addAction(VideoPlayVitamioTest.BROADCAST_STOP);
		intentFilter.addAction(VideoPlayVitamioTest.BROADCAST_QUIT);
		intentFilter.addAction(VideoPlayVitamioTest.BROADCAST_FIRST_DATA_ARRIVED);
		registerReceiver(myReceiver, intentFilter);
	}

	/**
	 * 视图查找
	 */
	private void findView() {
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mStopBtn = (Button) findViewById(R.id.stop_test);
		mStopBtn.setVisibility(View.INVISIBLE);
		mStopBtn.setOnClickListener(this);
	}

	/**
	 * 获取视频质量
	 * 
	 * @return 视频质量
	 */
	private int getMediaQuality() {
		int mediaQuality = taskModel.getVideoQuality() + 1;
		if (mediaQuality == 8)
			return 19;
		return mediaQuality;
	}

	/**
	 * 开始测试
	 */
	private void startTest() {
		LogUtil.d(TAG, "-----startTest-----");
		DMPlayerAPI.StartTestInfo info = new DMPlayerAPI.StartTestInfo();
		info.ps_call = this.taskModel.getPlayType() == 1;
		info.url = this.taskModel.getUrl();
		info.media_quality = this.getMediaQuality();
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
		info.stream_type = taskModel.getStreamType();
		mSurfacePlayer.StartTest(info);
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
	public void onDestroy() {
		LogUtil.d(TAG, "-----onDestroy-----");
		this.unregisterReceiver(myReceiver);
		super.onDestroy();
	}

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(VideoPlayVitamioTest.BROADCAST_STOP)
					|| intent.getAction().equals(VideoPlayVitamioTest.BROADCAST_QUIT)) {
				finish();
			} else if (intent.getAction().equals(VideoPlayVitamioTest.BROADCAST_START)) {
				startTest();
			} else if (intent.getAction().equals(VideoPlayVitamioTest.BROADCAST_FIRST_DATA_ARRIVED)) {
				mVideoView.postInvalidate();
				mStopBtn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onBackPressed() {
		// 禁止返回键
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.stop_test) {
			mSurfacePlayer.StopTest();
			Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
			sendBroadcast(interruptIntent);
		}
	}

}

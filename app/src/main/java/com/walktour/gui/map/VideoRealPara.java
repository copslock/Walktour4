package com.walktour.gui.map;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import com.dingli.player.VideoStreamPlayer;
import com.walktour.Utils.ConstItems;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;

public class VideoRealPara extends BasicActivity implements RefreshEventListener {
	private static final String TAG = "VideoRealPara";
	// private Bitmap bitmap;
	// private ImageView ivVideo;
	private LinearLayout control_show; // 该层用于控制参数显示格式占位，如果显示显示，则此隐藏，否则此处隐藏占位
	private LinearLayout control_play_show; // 显示视频窗口

	boolean isShow = false;

	private VideoStreamPlayer mPlayer = null;
	private SurfaceView mSurfaceView;

//	static {
//		System.loadLibrary("crystax_shared");
//		System.loadLibrary("gnustl_shared");
//		System.loadLibrary("oswsharemjni");
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_videoview);

		// ivVideo = initImageView(R.id.play);
		control_show = (LinearLayout) findViewById(R.id.control_show);
		control_play_show = (LinearLayout) findViewById(R.id.control_play_show);
		mSurfaceView = (SurfaceView) findViewById(R.id.sv_surface_view);
		RefreshEventManager.addRefreshListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
	}

	// private Handler mVideoHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// byte[] video_frame = (byte[]) msg.obj;
	// bitmap = BitmapFactory.decodeByteArray(video_frame, 0,
	// video_frame.length);
	// ivVideo.setImageBitmap(bitmap);
	// bitmap = null;
	// }
	// };

	public void startPlay() {
		Log.v(TAG, "--startPlay--" + isShow);
		if (!isShow) {
			mPlayer = new VideoStreamPlayer(
					Environment.getExternalStorageDirectory().getPath() + "/Walktour/liblog/vs_VideoStreamPlayer.log");
			mPlayer.SetDisplay(mSurfaceView);

			mPlayer.Start(Environment.getExternalStorageDirectory().getPath() + "/Walktour/liblog/");
		}
		isShow = true;
	}

	public void stopPlay() {
		Log.v(TAG, "--stopPlay--" + isShow);
		if (isShow && mPlayer != null) {
			mPlayer.Stop();
			mPlayer.Destory();
			mPlayer = null;
		}
		isShow = false;
	}

	java.util.Map<String, Object> values = TraceInfoInterface.traceData.getVideoRealPrar();

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {

		case ACTION_WALKTOUR_TIMER_CHANGED:
			if (values != null) {
				if (values.containsKey(ConstItems.IS_SHOW_VIDEO)) { // &&
																														// values.containsKey(ConstItems.VIDEO_TYPE)
					Boolean isShowVideo = Boolean.parseBoolean(values.get(ConstItems.IS_SHOW_VIDEO).toString());
					// int type = (Integer)values.get(ConstItems.VIDEO_TYPE);
					// if( type == WalkCommonPara.CALL_BACK_VIDEO_STREAM_REAL_PARA ){
					if (isShowVideo) {
						if (!isShow
								&& (values.containsKey(ConstItems.IS_SHOW_VIDEO) && (Boolean) values.get(ConstItems.IS_SHOW_VIDEO))) {
							control_play_show.setVisibility(View.VISIBLE);
							control_show.setVisibility(View.GONE);

							startPlay();
						} else if (isShow
								&& (!values.containsKey(ConstItems.IS_SHOW_VIDEO) || !(Boolean) values.get(ConstItems.IS_SHOW_VIDEO))) {
							control_play_show.setVisibility(View.GONE);
							control_show.setVisibility(View.INVISIBLE);

							stopPlay();
						}

					} else {
						if (isShow) {
							control_play_show.setVisibility(View.GONE);
							control_show.setVisibility(View.INVISIBLE);

							stopPlay();
						}
					}
					/*
					 * }else{ control_play_show.setVisibility(View.GONE);
					 * control_show.setVisibility(View.INVISIBLE); }
					 */

				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		LogUtil.w(TAG, "--onDetachedFromWindow L--");

		stopPlay();
	}

}

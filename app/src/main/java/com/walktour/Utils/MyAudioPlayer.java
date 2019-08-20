package com.walktour.Utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.walktour.base.util.LogUtil;

/**
 * 音频播放工具
 * 
 * @author jianchao.wang
 *
 */
public class MyAudioPlayer {
	private final String tag = "MyAudioPlayer";
	/** 上下文 */
	private Context mContext;
	/** 播放器 */
	private MediaPlayer player = null;

	/**
	 * 媒体播放器
	 * 
	 * @param context
	 *          上下文
	 * @param rawId
	 *          要播放的raw文件id
	 */
	public MyAudioPlayer(Context context, int rawId) {
		mContext = context;
		// 读取音频文件资源，一个MyAudioTrace对象只读取一次,减少MOS测试的时间误差
		player = MediaPlayer.create(mContext, rawId);
		player.setLooping(false);
	}

	/** 开始播放 */
	public void startPlayer() {
		try {
			LogUtil.w(tag, "---start()");
			player.start();
		} catch (Exception e) {
			LogUtil.w(tag, e.toString());
		}
	}

	/**
	 * 停止播放、释放资源
	 */
	public void stopPlayer() {
		if (this.player != null) {
			try {
				LogUtil.d(tag, "---stop and release()");
				player.stop();// 最后在释放资源前才停止
				player.release();
			} catch (Exception e) {
				LogUtil.e(tag, e.toString(), e.fillInStackTrace());
			}
		}
	}
}

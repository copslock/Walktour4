package com.walktour.gui.task.parsedata.model.task.videoplay;

import android.content.Context;

import com.walktour.gui.R;

/**
 * 特殊获取字符串方法，解决传Model类传中文问题
 */
public class StringSpecialInit {

	private Context mContext;
	private static StringSpecialInit instance = null;

	public synchronized static StringSpecialInit getInstance() {
		if (instance == null)
			instance = new StringSpecialInit();
		return instance;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public String[] getVideoQualityArray() {
		return mContext.getResources().getStringArray(R.array.array_video_quality);
	}
}

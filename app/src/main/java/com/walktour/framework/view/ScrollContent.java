/*
 * 文件名: ScrollContent.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2012-6-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

public abstract class ScrollContent {

	protected Activity activity;
	protected Context context;
	protected int resourceID;
	protected LayoutInflater inflater;
	protected View view;
	protected Resources mRes;

	public ScrollContent(Activity activity, int resourceID) {
		this.activity = activity;
		this.context = activity;
		this.resourceID = resourceID;
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(resourceID, null);
		mRes = context.getResources();
	}

	protected View findViewById(int id) {
		return view.findViewById(id);
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public void overridePendingTransition(int enterAnim, int exitAnim) {
		activity.overridePendingTransition(enterAnim, exitAnim);
	}

	public void startActivity(Intent intent) {
		activity.startActivity(intent);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
	}
}

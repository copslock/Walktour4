package com.walktour.framework.ui;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.walktour.gui.R;
public class BasicExpandableListActivity extends ExpandableListActivity implements OnClickListener {
	/**
	 * Activity跳转，Activity保留,请传递数据值
	 * 
	 * @param cls
	 *            跳转的类
	 * @param bundle
	 *            数据值
	 */
	protected void jumpActivity(Class<?> cls, Bundle bundle) {
		Intent intentx = new Intent(this, cls);
		intentx.putExtras(bundle);
		startActivity(intentx);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		intentx = null;
	}
	/**
	 * Activity跳转，Activity保留,返回值
	 * 
	 * @param cls
	 *            跳转的类
	 * 
	 */
	protected void jumpActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
		Intent intentx = new Intent(this, cls);
		intentx.putExtras(bundle);
		this.startActivityForResult(intentx, requestCode);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);
		intentx = null;
	}
	@Override
	public void onClick(View arg0) { 
	}
}

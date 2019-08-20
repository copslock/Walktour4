package com.walktour.gui.metro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.walktour.gui.R;
import com.walktour.gui.metro.fragment.MetroSettingCityFragment;
import com.walktour.gui.setting.bluetoothmos.fragment.ProgressFragment;

/**
 * 地铁线路城市列表界面
 * 
 * @author jianchao.wang
 *
 */
public class MetroSettingCityActivity extends FragmentActivity {
	/** 是否是选择城市的模式 */
	private boolean isSelect = false;
	/** 进度条对话框 */
	private ProgressFragment mProgressDialog = null;
	/** 对话框类型：显示进度条 */
	private static final String DIALOG_SHOW_PROGRESS = "show_progress";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_metro_setting_main);
		Intent intent = this.getIntent();
		this.isSelect = intent.getBooleanExtra("is_select", false);
		this.setCityFragment();
	}

	/**
	 * 设置城市列表视图
	 */
	public void setCityFragment() {
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		MetroSettingCityFragment cityFragment = new MetroSettingCityFragment();
		Bundle args = new Bundle();
		args.putBoolean("is_select", this.isSelect);
		cityFragment.setArguments(args);
		transaction.replace(R.id.id_content, cityFragment);
		transaction.commit();
	}

	/**
	 * 创建进度条对话框
	 * 
	 * @param message
	 *          进度条内容
	 */
	public void showProgressDialog(String message) {
		if (this.mProgressDialog != null)
			return;
		FragmentManager fm = this.getSupportFragmentManager();
		this.mProgressDialog = new ProgressFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ProgressFragment.PROGRESS_MESSAGE, message);
		this.mProgressDialog.setArguments(bundle);
		this.mProgressDialog.show(fm, DIALOG_SHOW_PROGRESS);
		this.mProgressDialog.setCancelable(false);
	}

	/**
	 * 中断进度条显示
	 */
	public void dismissProgress() {
		if (this.mProgressDialog != null && !this.mProgressDialog.isHidden()) {
			this.mProgressDialog.dismiss();
			this.mProgressDialog = null;
		}
	}

}

package com.walktour.gui.newmap.innsmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.walktour.gui.R;
import com.walktour.gui.newmap.innsmap.fragment.InnsmapSelectFragment;
import com.walktour.gui.setting.bluetoothmos.fragment.ProgressFragment;

/**
 * 寅时室内测试选择城市、建筑物、楼层界面
 * 
 * @author jianchao.wang
 *
 */
public class InnsmapSelectActivity extends FragmentActivity {
	/** 对话框类型：显示进度条 */
	private static final String DIALOG_SHOW_PROGRESS = "show_progress";
	/** 检索的对象类型ID */
	private int mTypeId;
	/** 进度条对话框 */
	private ProgressFragment mProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_innsmap_select_main);
		Intent intent = this.getIntent();
		this.mTypeId = intent.getIntExtra("typeId", 0);
		this.setSelectFragment();
	}

	/**
	 * 设置选择列表视图
	 */
	public void setSelectFragment() {
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		InnsmapSelectFragment selectFragment = new InnsmapSelectFragment();
		Bundle args = new Bundle();
		args.putInt("typeId", this.mTypeId);
		selectFragment.setArguments(args);
		transaction.replace(R.id.id_content, selectFragment);
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

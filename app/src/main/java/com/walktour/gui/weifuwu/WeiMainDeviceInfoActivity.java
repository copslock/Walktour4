package com.walktour.gui.weifuwu;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
/***
 * 群组的详细信息
 * 
 * @author weirong.fan
 *
 */
public class WeiMainDeviceInfoActivity extends BasicActivity implements OnClickListener {
	private Context context = WeiMainDeviceInfoActivity.this;
	/** 当前的群组code **/
	private String currentDeviceCode = "";
	private TextView deviceNameTxt;
	/** 进度提示 */
	private ProgressDialog progressDialog;
	/** 当前组信息 **/
	private ShareDeviceModel deviceModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.weifuwumaindeviceinfolayout);
		currentDeviceCode = this.getIntent().getStringExtra("deviceCode");
		try {
			deviceModel = ShareDataBase.getInstance(context).fetchDeviceByDeviceCode(currentDeviceCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null == deviceModel)
			this.finish();
		initViews();
	}
	/***
	 * 初始化控件
	 */
	private void initViews() {
		deviceNameTxt = initTextView(R.id.groupname);
		initTextView(R.id.title_txt).setText(R.string.act_info);
		findViewById(R.id.pointer).setOnClickListener(this);
		initLinearLayout(R.id.clearhistory).setOnClickListener(this);
		initButton(R.id.exitgroup).setOnClickListener(this);
		deviceNameTxt.setText(deviceModel.getDeviceName());
		initTextView(R.id.weifuwuhaoxx).setText(deviceModel.getDeviceCode() + "");
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.clearhistory:
			dialog(R.string.str_delete_makesure);
			break;
		case R.id.pointer:
			this.finish();
			break;
		case R.id.exitgroup:
			initdialog();
			break;
		}
	}
	private void initdialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(R.string.share_project_exit_device_confirm);
		builder.setTitle(R.string.str_tip);
		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new DeleteDeviceRelation().execute();
			}
		});
		builder.setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	/**
	 * 解除关系
	 * 
	 * @author weirong.fan
	 *
	 */
	private class DeleteDeviceRelation extends AsyncTask<Void, Void, BaseResultInfoModel> {
		public DeleteDeviceRelation() {
			super();
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {
				if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 更新设备名称失败
					ToastUtil.showToastShort(context, getString(R.string.total_faild));
				} else {// 更新成功 
					try {
						ShareDeviceModel sm = ShareDataBase.getInstance(context).fetchDeviceByDeviceCode(currentDeviceCode);
						if (null != sm) {
							sm.setDeviceStatus(ShareDeviceModel.STATUS_DELETED);
							ShareDataBase.getInstance(context).updateDevice(sm);
							Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
							sendBroadcast(intent);
						}
					} catch (Exception e) { 
						e.printStackTrace();
					}
					finish();
				}
			} else {
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			}
			closeDialog();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			openDialog(getString(R.string.share_project_server_doing));
		}
		@Override
		protected BaseResultInfoModel doInBackground(Void... params) {
			BaseResultInfoModel model=ShareHttpRequestUtil.getInstance().deleteDeviceRelation(currentDeviceCode, ShareCommons.session_id);;
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model=ShareHttpRequestUtil.getInstance().deleteDeviceRelation(currentDeviceCode, ShareCommons.session_id);;
			}
			return model;
		}
	}
	protected void dialog(int message) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(message);
		builder.setTitle(getString(R.string.str_tip));
		builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除指定群组的历史资源
				ShareDataBase.getInstance(context).updateAllFileByDeviceCode(currentDeviceCode);
				Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_1);
				sendBroadcast(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(getString(R.string.str_cancle), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	/**
	 * 打开进度条
	 * 
	 * @param txt
	 */
	protected void openDialog(String txt) {
		progressDialog = new ProgressDialog(this.context);
		progressDialog.setMessage(txt);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	/**
	 * 关闭进度条
	 */
	protected void closeDialog() {
		progressDialog.dismiss();
	}
}

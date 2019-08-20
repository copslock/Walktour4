package com.walktour.gui.weifuwu;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
public class WeiMain3Activity extends BasicActivity implements OnClickListener {
	private Context context = WeiMain3Activity.this;
	private String tokenID = "";
	private TextView deviceNameTxt;
	private String deviceName;
	/** 进度提示 */
	private ProgressDialog progressDialog;
	// 当前的设备信息
	private ShareDeviceModel mm = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.weifuwumain3layout);
		if (ShareCommons.device_code.equals("")) {
			RegisterDeviceLogic.getInstance(context).shareRegister();
		}
		tokenID = SharePreferencesUtil.getInstance(context).getString(RegisterDeviceLogic.TOKEN_ID, "");
		deviceName = SharePreferencesUtil.getInstance(context).getString(RegisterDeviceLogic.SHARE_DEVICE_NAME, "");
		if (!ShareCommons.device_code.equals("")) {
			try {
				mm = ShareDataBase.getInstance(context).fetchDeviceByDeviceCode(ShareCommons.device_code);
				deviceName = mm.getDeviceName() + "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		initViews();
	}
	/***
	 * 初始化控件
	 */
	private void initViews() {
		deviceNameTxt = initTextView(R.id.groupname);
		initLinearLayout(R.id.comname).setOnClickListener(this);
		initLinearLayout(R.id.localhistory).setOnClickListener(this);
		initLinearLayout(R.id.devicelist).setOnClickListener(this);
		initLinearLayout(R.id.clearhistory).setOnClickListener(this);
		deviceNameTxt.setText(deviceName);
		initTextView(R.id.weifuwuhao).setText(ShareCommons.device_code + "");
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.comname:
			createGroup();
			break;
		case R.id.localhistory:
			Bundle bundle = new Bundle();
			bundle.putString("isFrom", "relation-me");
			jumpActivity(WeiMainHistoryActivity.class, bundle);
			break;
		case R.id.devicelist:
			this.jumpActivity(WeiMainListMemberActivity.class);
			break;
		case R.id.clearhistory:
			dialog(2, R.string.str_delete_makesure);
			break;
		}
	}
	/***
	 * 查询设备名称是否相同
	 * 
	 * @author weirong.fan
	 *
	 */
	private class FindDeviceName extends AsyncTask<Void, Void, Integer> {
		public FindDeviceName() {
			super();
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == -1) {
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			} else if (result == -2) {
				ToastUtil.showToastShort(context, getString(R.string.share_project_update_samedevicename));
			} else if (result == 0) {
				deviceNameTxt.setText(deviceName + "");
			} else if (result == 1) {
				dialog(1, R.string.share_project_exist_samedevicename);
			}
			closeDialog();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			openDialog(getString(R.string.share_project_server_doing));
		}
		@Override
		protected Integer doInBackground(Void... params) {
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().queryDeviceName(ShareCommons.device_code,
					deviceName, ShareCommons.session_id);
			if (model.getReasonCode() == 1 && model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().queryDeviceName(ShareCommons.device_code, deviceName,
						ShareCommons.session_id);
			}
			if (model.getReasonCode() == 1) {
				if (!model.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 不存在相同的设备备注名,直接更新备注名
					model = ShareHttpRequestUtil.getInstance().editDevice(deviceName, tokenID, ShareCommons.session_id);
					if (model.getReasonCode() == 1) {
						if (model.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 更新设备备注名成功
							SharePreferencesUtil.getInstance(context).saveString(RegisterDeviceLogic.SHARE_DEVICE_NAME,
									deviceName);
							try {
								mm.setDeviceName(deviceName);
								ShareDataBase.getInstance(context).saveOrUpdateDevice(mm);
							} catch (Exception e) {
								e.printStackTrace();
								return -1;
							}
							return 0;
						} else {// 更新设备备注名失败
							return -2;
						}
					} else {
						return -1;
					}
				} else {// 存在相同的备注名，弹出对话框，给出提示
					return 1;
				}
			} else {// 网络错误
				return -1;
			}
		}
	}
	/***
	 * 更新设备备注名
	 * 
	 * @author weirong.fan
	 *
	 */
	private class UpdateDeviceName extends AsyncTask<Void, Void, BaseResultInfoModel> {
		public UpdateDeviceName() {
			super();
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {
				if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 更新设备名称失败
					ToastUtil.showToastShort(context, getString(R.string.share_project_update_samedevicename));
				} else {// 更新成功
					mm.setDeviceName(deviceName);
					try {
						ShareDataBase.getInstance(context).saveOrUpdateDevice(mm);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().editDevice(deviceName, tokenID,
					ShareCommons.session_id);
			if (model.getReasonCode() == 1 && model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().editDevice(deviceName, tokenID, ShareCommons.session_id);
			}
			return model;
		}
	}
	private void createGroup() {
		final View textEntryView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_edittext1, null);
		final EditText et = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
		et.setText(deviceName);
		new BasicDialog.Builder(this).setView(textEntryView).setTitle(R.string.share_project_input_device_name)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deviceName = et.getText().toString();
						if (null != deviceName && deviceName.length() > 0) {
							new FindDeviceName().execute();
						} else {
							ToastUtil.showToastShort(context, getString(R.string.share_project_input_device_notnull));
						}
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}
	protected void dialog(final int select, int message) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(message);
		builder.setTitle(getString(R.string.str_tip));
		builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (select) {
				case 1:
					new UpdateDeviceName().execute();
					break;
				case 2:
					ShareDataBase.getInstance(context).deleteAllFile();
					Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_1);
					sendBroadcast(intent);
					break;
				}
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

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

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
/***
 * 群组的详细信息
 * 
 * @author weirong.fan
 *
 */
public class WeiMainGroupInfoActivity extends BasicActivity implements OnClickListener {
	private Context context = WeiMainGroupInfoActivity.this;
	/** 当前的群组code **/
	private String currentGroupCode = "";
	private TextView deviceNameTxt;
	/** 进度提示 */
	private ProgressDialog progressDialog;
	/** 当前组信息 **/
	private ShareGroupModel groupModel;
	private String groupName; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.weifuwumaingroupinfolayout);
		
		currentGroupCode = this.getIntent().getStringExtra("groupCode");
		groupModel = ShareDataBase.getInstance(context).fetchGroup(currentGroupCode);
		if (null == groupModel)
			this.finish();
		groupName = groupModel.getGroupName();
		initViews();
	}
	/***
	 * 初始化控件
	 */
	private void initViews() {
		deviceNameTxt = initTextView(R.id.groupname);
		initTextView(R.id.grouplist).setText(R.string.share_project_device_list);
		initTextView(R.id.title_txt).setText(R.string.share_project_devices_release_relation_16);
		initLinearLayout(R.id.comname).setOnClickListener(this);
		findViewById(R.id.pointer).setOnClickListener(this);
		initLinearLayout(R.id.localhistory).setOnClickListener(this);
		initLinearLayout(R.id.clearhistory).setOnClickListener(this);
		initButton(R.id.exitgroup).setOnClickListener(this);
		deviceNameTxt.setText(groupName);
		initTextView(R.id.weifuwuhaoxx).setText(groupModel.getGroupCode() + "");
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.comname:
			if (!ShareCommons.device_code.equals(groupModel.getCreateDeviceCode())) {
				ToastUtil.showToastShort(context, R.string.share_project_group_edit_cor);
				return;
			}
			createGroup();
			break;
		case R.id.localhistory:
			Bundle bundle = new Bundle();
			bundle.putString("groupCode", currentGroupCode);
			jumpActivity(WeiMainGroupRelationActivity.class, bundle);
			break;
		case R.id.clearhistory:
			dialog(2, R.string.str_delete_makesure);
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
		builder.setMessage(R.string.share_project_exit_group_confirm);
		builder.setTitle(R.string.str_tip);
		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new ExitGroup().execute();
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
	/***
	 * 查询设备名称是否相同
	 * 
	 * @author weirong.fan
	 *
	 */
	private class FindGroupName extends AsyncTask<Void, Void, Integer> {
		public FindGroupName() {
			super();
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == -1) {
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			} else if (result == -2) {
				ToastUtil.showToastShort(context, getString(R.string.share_project_update_samegroupname));
			} else if (result == 0) {
				deviceNameTxt.setText(groupName + "");
			} else if (result == 1) {
				dialog(1, R.string.share_project_exist_samegroupname);
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
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().queryGroupName(groupName, ShareCommons.device_code,ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().queryGroupName(groupName, ShareCommons.device_code,ShareCommons.session_id);
			}
			if (model.getReasonCode() == 1) {
				if (!model.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 不存在相同的设备备注名,直接更新备注名
					model = ShareHttpRequestUtil.getInstance().editGroup(groupModel.getGroupCode(), groupName,
							ShareCommons.device_code,ShareCommons.session_id);
					if (model.getReasonCode() == 1) {
						if (model.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 更新设备备注名成功
							groupModel.setGroupName(groupName);
							try {
								ShareDataBase.getInstance(context).saveOrUpdateGroup(groupModel);
								return 0;
							} catch (Exception e) {
								e.printStackTrace();
							}
							return -1;
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
	private class UpdateGroupName extends AsyncTask<Void, Void, BaseResultInfoModel> {
		public UpdateGroupName() {
			super();
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {
				if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 更新设备名称失败
					ToastUtil.showToastShort(context, getString(R.string.share_project_update_samegroupname));
				} else {// 更新成功
					groupModel.setGroupName(groupName+"");
					deviceNameTxt.setText(groupName+"");
					try {
						ShareDataBase.getInstance(context).saveOrUpdateGroup(groupModel);
						
						Intent intentx = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
						context.sendBroadcast(intentx);
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
			BaseResultInfoModel model=ShareHttpRequestUtil.getInstance().editGroup(groupModel.getGroupCode(), groupName, ShareCommons.device_code,ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model=ShareHttpRequestUtil.getInstance().editGroup(groupModel.getGroupCode(), groupName, ShareCommons.device_code,ShareCommons.session_id);
			}
			return model;
		}
	}
	/**
	 * 退出指定的群组
	 * 
	 * @author weirong.fan
	 *
	 */
	private class ExitGroup extends AsyncTask<Void, Void, BaseResultInfoModel> {
		public ExitGroup() {
			super();
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {
				if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 更新设备名称失败
					ToastUtil.showToastShort(context, getString(R.string.share_project_update_samegroupname));
				} else {// 更新成功
					ShareDataBase.getInstance(context).deleteAllFileByFromGroupCode(groupModel.getGroupCode());
					ShareDataBase.getInstance(context).deleteGroup(groupModel.getGroupCode());
					Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
					sendBroadcast(intent);
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
			BaseResultInfoModel model=ShareHttpRequestUtil.getInstance().exitGroup(groupModel.getGroupCode(),ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model=ShareHttpRequestUtil.getInstance().exitGroup(groupModel.getGroupCode(),ShareCommons.session_id);
			}
			return model;
		}
	}
	private void createGroup() {
		final View textEntryView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_edittext1, null);
		final EditText et = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
		et.setText(groupName);
		new BasicDialog.Builder(this).setView(textEntryView).setTitle(R.string.share_project_input_group_name)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						groupName = et.getText().toString();
						if (null != groupName && groupName.length() > 0) {
							new FindGroupName().execute();
						} else {
							ToastUtil.showToastShort(context, getString(R.string.share_project_input_group_notnull));
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
					new UpdateGroupName().execute();
					break;
				case 2:
					//删除指定群组的历史资源
					ShareDataBase.getInstance(context).updateAllFileByGroupCode(groupModel.getGroupCode());
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

package com.walktour.gui.weifuwu;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.upload.UploadCallback;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.util.LinkedList;
import java.util.List;
/***
 * 成员列表信息
 * 
 * @author weirong.fan
 *
 */
public class WeiMainListMemberActivity extends BasicActivity implements OnClickListener {
	private Context context = WeiMainListMemberActivity.this;
	private ListView listView = null;
	private List<ShareDeviceModel> list = new LinkedList<ShareDeviceModel>();
	private MyAdapter adapter = new MyAdapter();
	/** 进度提示 */
	private ProgressDialog progressDialog;
	private MyReceiver receiver = new MyReceiver();
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.weifuwumainglistmemberlayout);
		initView();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(UploadCallback.ACTION);
		filter.addAction(ShareCommons.SHARE_ACTION_MAIN_2);
		this.registerReceiver(receiver, filter);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	/***
	 * 初始化
	 */
	private void initView() {
		findViewById(R.id.pointer).setOnClickListener(this);
		initData();
		listView = this.initListView(R.id.id_listview);
		listView.setAdapter(adapter);
	}
	private void initData() {
		List<Integer> status = new LinkedList<Integer>();
		status.add(ShareDeviceModel.STATUS_NEW);
		status.add(ShareDeviceModel.STATUS_CONFIRM);
		status.add(ShareDeviceModel.STATUS_ADDED);
		status.add(ShareDeviceModel.STATUS_REFUSED);
		status.add(ShareDeviceModel.STATUS_DELETED); 
		list.clear();
		list = ShareDataBase.getInstance(context).fetchAllDeviceByStatus(status);
	}
	private class MyAdapter extends BaseAdapter { 
		private MyAdapter() {
			super(); 
		}
		@Override
		public int getCount() {
			return null == list ? 0 : list.size();
		}
		@Override
		public Object getItem(int position) {
			return null == list ? null : list.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == convertView){
				LayoutInflater inflate = (LayoutInflater) WeiMainListMemberActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflate.inflate(R.layout.weifuwumain3layout_childs, null);
			}
			ImageView imageView = (ImageView) convertView.findViewById(R.id.objimg);
			TextView textView1 = (TextView) convertView.findViewById(R.id.name1);
			TextView textView2 = (TextView) convertView.findViewById(R.id.name2);
			TextView textView3 = (TextView) convertView.findViewById(R.id.joinnumbers);
			Button add = (Button) convertView.findViewById(R.id.addmem);
			Button refuse = (Button) convertView.findViewById(R.id.refusemem);
			final ShareDeviceModel m = list.get(position);
			if (m.getDeviceOS() == ShareDeviceModel.OS_ANDROID) {
				imageView.setBackgroundResource(R.drawable.obj_android);
			} else if (m.getDeviceOS() == ShareDeviceModel.OS_IOS) {
				imageView.setBackgroundResource(R.drawable.obj_iphone);
			} else {
				imageView.setBackgroundResource(R.drawable.obj_group);
			}
			textView1.setText(m.getDeviceName() + "("+getString(R.string.share_project_device_code)+":" + m.getDeviceCode()+")");
			textView2.setText((m.getDeviceMessage()==null||m.getDeviceMessage().equals("null"))?"":m.getDeviceMessage()+"");
			textView3.setText("");
			add.setVisibility(View.GONE);
			refuse.setVisibility(View.GONE);
			if (m.getDeviceStatus() == ShareDeviceModel.STATUS_CONFIRM) {
				add.setVisibility(View.VISIBLE);
				refuse.setVisibility(View.VISIBLE);
			} else {
				if (m.getDeviceStatus() == ShareDeviceModel.STATUS_NEW) {
					textView3.setText(R.string.share_project_devices_requiring);
				}else if(m.getDeviceStatus() == ShareDeviceModel.STATUS_DELETED){
					textView3.setText(R.string.share_project_devices_release_relation_4);
				}else if(m.getDeviceStatus() == ShareDeviceModel.STATUS_ADDED){
					textView3.setText(R.string.share_project_devices_release_relation_5);
				}else if(m.getDeviceStatus() == ShareDeviceModel.STATUS_REFUSED){
					textView3.setText(R.string.share_project_devices_release_relation_6);
				}
			}
			add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CheckDevice(m.getDeviceCode(), "1").execute();
				}
			});
			refuse.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CheckDevice(m.getDeviceCode(), "2").execute();
				}
			});
			return convertView;
		}
	}
	/***
	 * 检查设备确认添加还是拒绝添加
	 * 
	 * @author weirong.fan
	 *
	 */
	private class CheckDevice extends AsyncTask<Void, Void, BaseResultInfoModel> {
		/** 关联设备code **/
		private String relation_code;
		/** 确认还是拒绝加为好友 */
		private String check_flag = "1";
		private CheckDevice(String relation_code, String check_flag) {
			super();
			this.relation_code = relation_code;
			this.check_flag = check_flag;
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			closeDialog();
			
			if (result.getReasonCode() == 1) {
				if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)
						&&!result.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_ADDED)) {// 处理失败
					ToastUtil.showToastShort(context, getString(R.string.total_faild));
				} else {// 更新成功
					try {
						if (check_flag.equals("1")) {
							// 同意更新状态
							ShareDeviceModel sm = ShareDataBase.getInstance(context)
									.fetchDeviceByDeviceCode(relation_code);
							sm.setDeviceStatus(ShareDeviceModel.STATUS_ADDED);
							ShareDataBase.getInstance(context).updateDevice(sm);
						} else {
							// 拒绝更新状态
							ShareDeviceModel sm = ShareDataBase.getInstance(context)
									.fetchDeviceByDeviceCode(relation_code);
							sm.setDeviceStatus(ShareDeviceModel.STATUS_DELETED);
							ShareDataBase.getInstance(context).updateDevice(sm);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
					sendBroadcast(intent);
				}
				initData();
				adapter.notifyDataSetChanged();
				listView.invalidate();
			} else {
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			}
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			openDialog(getString(R.string.exe_info));
		}
		@Override
		protected BaseResultInfoModel doInBackground(Void... params) {
			BaseResultInfoModel model = ShareHttpRequestUtil.getInstance().checkDevicerelations(relation_code,
					check_flag, ShareCommons.session_id);
			if(model.getReasonCode()==1&&model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				model = ShareHttpRequestUtil.getInstance().checkDevicerelations(relation_code,
						check_flag, ShareCommons.session_id);
			}
			return model;
		}
	}
	/**
	 * 广播接收器
	 * 
	 * @author weirong.fan
	 *
	 */
	private class MyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (action.equals(ShareCommons.SHARE_ACTION_MAIN_2)) {
					initData();
					adapter.notifyDataSetChanged();
					listView.invalidate();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	/**
	 * 打开进度条
	 * 
	 * @param txt
	 */
	private void openDialog(String txt) {
		progressDialog = new ProgressDialog(this.context);
		progressDialog.setMessage(txt);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}
	/**
	 * 关闭进度条
	 */
	private void closeDialog() {
		progressDialog.dismiss();
	}
}

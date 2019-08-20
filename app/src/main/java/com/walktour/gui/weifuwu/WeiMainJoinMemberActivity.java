package com.walktour.gui.weifuwu;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupRelationModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.util.LinkedList;
import java.util.List;
/***
 * 加入群组成员
 * 
 * @author weirong.fan
 *
 */
public class WeiMainJoinMemberActivity extends BasicActivity implements OnClickListener {
	private Context context = WeiMainJoinMemberActivity.this;
	/** 接收对象列表 **/
	private MyAdapter adapter;
	private String groupCode;
	private ListView mListView;
	private String inputText = "";
	private EditText inputTxt;
	/** 实际搜索到的设备数 **/
	private List<ShareDeviceModel> allDevices = new LinkedList<ShareDeviceModel>();
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.weifuwumaingjoinmemeberlayout);
		groupCode = this.getIntent().getStringExtra("groupCode");
		initView();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		}
	}
	/***
	 * 初始化
	 */
	private void initView() {
		findViewById(R.id.pointer).setOnClickListener(this);
		inputTxt = initEditText(R.id.inputxt);
		inputTxt.addTextChangedListener(textWatcher);
		adapter = new MyAdapter(allDevices);
		mListView = (ListView) findViewById(R.id.id_listview);
		mListView.setAdapter(adapter);
		refreshData();
	}
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private List<ShareDeviceModel> list;
		private MyAdapter(List<ShareDeviceModel> list) {
			super();
			layoutInflater = LayoutInflater.from(context);
			this.list = list;
		}
		@Override
		public int getCount() {
			return list != null ? list.size() : 0;
		}
		@Override
		public Object getItem(int arg0) {
			return list != null ? list.get(arg0) : null;
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.weifuwumain2layout_item, parent, false);
			}
			final ShareDeviceModel model = list.get(position);
			ImageView objImg = (ImageView) convertView.findViewById(R.id.objimg);
			TextView name1 = (TextView) convertView.findViewById(R.id.name1);
			TextView name2 = (TextView) convertView.findViewById(R.id.name2);
			name2.setVisibility(View.VISIBLE);
			TextView t1 = (TextView) convertView.findViewById(R.id.joingpointer);
			t1.setVisibility(View.GONE);
			Button btn = (Button) convertView.findViewById(R.id.joingroup);
			btn.setVisibility(View.VISIBLE);
			if (model.getDeviceOS() == ShareDeviceModel.OS_ANDROID) {
				objImg.setBackgroundResource(R.drawable.obj_android);
			} else {
				objImg.setBackgroundResource(R.drawable.obj_iphone);
			}
			try {
				btn.setText(getString(R.string.share_project_add_member));
				boolean exist = ShareDataBase.getInstance(context).exitGroupRelation(groupCode, model.getDeviceCode());
				btn.setText(exist ? getString(R.string.share_project_add_member_too)
						: getString(R.string.share_project_add_member));
				if (exist) {
					btn.setEnabled(false);
					btn.setVisibility(View.GONE);
				} else {
					btn.setEnabled(true);
					btn.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			name1.setText(model.getDeviceName() + "");
			name2.setText(getString(R.string.share_project_device_code) +":"+ model.getDeviceCode() + "");
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new JoinMember(model.getDeviceCode()).execute();
				}
			});
			return convertView;
		}
	}
	private void refreshData() {
		try {
			allDevices.clear();
			List<Integer> status = new LinkedList<Integer>();
			status.add(ShareDeviceModel.STATUS_ADDED);
			List<ShareDeviceModel> allD = ShareDataBase.getInstance(context).fetAllDevice(inputText, status);
			allDevices.addAll(allD);
			adapter.notifyDataSetChanged();
			mListView.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * 加入群组成员
	 * 
	 * @author weirong.fan
	 *
	 */
	private class JoinMember extends AsyncTask<Void, Void, BaseResultInfoModel> {
		private String deviceCode;
		private JoinMember(String deviceCode) {
			super();
			this.deviceCode = deviceCode;
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {// 网络正常
				if (result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 加入正常
					try {
						ShareGroupRelationModel model = new ShareGroupRelationModel();
						model.setGroupCode(groupCode);
						model.setDeviceCode(deviceCode);
						ShareDataBase.getInstance(context).insertGroupRelation(model);
						model.setDeviceCode(ShareCommons.device_code);
						ShareDataBase.getInstance(context).insertGroupRelation(model);
						adapter.notifyDataSetChanged();
						mListView.invalidate();
						Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
						sendBroadcast(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
				}
			} else {// 网络失败
				ToastUtil.showToastShort(context, getString(R.string.sys_alarm_neterr));
			}
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected BaseResultInfoModel doInBackground(Void... arg0) {
			BaseResultInfoModel bi = ShareHttpRequestUtil.getInstance().add_members(groupCode, deviceCode,
					ShareCommons.session_id);
			if(bi.getReasonCode() == 1&&bi.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)){
				RegisterDeviceLogic.getInstance(context).shareRegister();
				bi = ShareHttpRequestUtil.getInstance().add_members(groupCode, deviceCode,
						ShareCommons.session_id);
			}
			if (bi.getReasonCode() == 1) {
				if (bi.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
					// 从服务器查询相应设备的信息，插入设备表
					DeviceInfoModel deviceModel = ShareHttpRequestUtil.getInstance().query_members(groupCode,
							ShareCommons.session_id);
					if (deviceModel.getReasonCode() == 1) {
						if (deviceModel.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
							List<DeviceInfoModel.Device> listDevices = deviceModel.getDevices();
							for (DeviceInfoModel.Device d : listDevices) {
								ShareDeviceModel m = new ShareDeviceModel();
								m.setDeviceCode(d.getDevice_code());
								m.setDeviceName(d.getDevice_name());
								m.setDeviceOS(d.getDevice_type().equalsIgnoreCase(ShareDeviceModel.OS_ANDROID + "")
										? ShareDeviceModel.OS_ANDROID : ShareDeviceModel.OS_IOS);
								try {
									// 检测本地是否有此设备，没有则添加，状态为default
									ShareDeviceModel km = ShareDataBase.getInstance(context)
											.fetchDeviceByDeviceCode(d.getDevice_code());
									if (null == km) { 
										ShareDataBase.getInstance(context).insertDevice(m);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			return bi;
		}
	}
	/**
	 * 文本更新监听
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			inputText = inputTxt.getText().toString();
			refreshData();
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
}

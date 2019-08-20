package com.walktour.gui.weifuwu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel.Device;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.util.LinkedList;
import java.util.List;
/***
 * 添加新成员
 * 
 * @author weirong.fan
 *
 */
public class WeiMainJoinGroupActivity extends BasicActivity
		implements SwipeRefreshLayout.OnRefreshListener, OnClickListener {
	private Context context = WeiMainJoinGroupActivity.this;
	private List<Device> listDevicesModel = new LinkedList<Device>();
	/** 接收对象列表 **/
	private MyAdapter adapter;
	private static final int REFRESH_COMPLETE = 0X120;
	private SwipeRefreshLayout mSwipeLayout;
	private ListView mListView;
	private int start_row = 0;
	private String inputText = "";
	private EditText inputTxt;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.weifuwumaingjoinrouplayout);
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
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				new FetchNewDevice().execute();
				break;
			}
		};
	};
	public void onRefresh() {
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
	}
	/***
	 * 初始化
	 */
	private void initView() {
		findViewById(R.id.pointer).setOnClickListener(this);
		inputTxt = initEditText(R.id.inputxt);
		inputTxt.addTextChangedListener(textWatcher);
		inputTxt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		adapter = new MyAdapter(listDevicesModel);
		mListView = (ListView) findViewById(R.id.id_listview);
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mListView.setAdapter(adapter);
		// 刷新数据
		// new FetchNewDevice().execute();
	}
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private List<Device> list;
		private MyAdapter(List<Device> list) {
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
			ImageView img = (ImageView) convertView.findViewById(R.id.objimg);
			TextView name1 = (TextView) convertView.findViewById(R.id.name1);
			TextView name2 = (TextView) convertView.findViewById(R.id.name2);
			name2.setVisibility(View.VISIBLE);
			TextView t1 = (TextView) convertView.findViewById(R.id.joingpointer);
			t1.setVisibility(View.GONE);
			final Button btn = (Button) convertView.findViewById(R.id.joingroup);
			btn.setVisibility(View.VISIBLE);
			final Device model = list.get(position);
			try {
				// 存在群组关系，隐藏加入群组按钮
				List<Integer> deviceStatus = new LinkedList<Integer>();
				deviceStatus.add(ShareDeviceModel.STATUS_NEW);
				deviceStatus.add(ShareDeviceModel.STATUS_ADDED);
				deviceStatus.add(ShareDeviceModel.STATUS_CONFIRM);
				boolean flag = ShareDataBase.getInstance(context).existDevice(model.getDevice_code(), deviceStatus);
				if (flag) {
					btn.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (model.getDevice_type().equalsIgnoreCase(ShareDeviceModel.OS_ANDROID + "")) {
				img.setBackgroundResource(R.drawable.obj_android);
			} else {
				img.setBackgroundResource(R.drawable.obj_iphone);
			}
			name1.setText(model.getDevice_name() + "");
			name2.setText(getString(R.string.share_project_device_code)+":" + model.getDevice_code() + "");
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						final View textEntryView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_edittext1,
								null);
						final EditText et = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
						ShareDeviceModel dm;
						dm = ShareDataBase.getInstance(context).fetchDeviceByDeviceCode(ShareCommons.device_code);
						String s = String.format(context.getString(R.string.share_project_devices_release_relation_8),
								dm.getDeviceName() + "");
						et.setText(s);
						new BasicDialog.Builder(context).setView(textEntryView)
								.setTitle(R.string.share_project_devices_requiring_message)
								.setPositiveButton(R.string.share_project_devices_release_relation_10, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new AddNewDevice(et.getText().toString() + "", model, btn).execute();
							}
						}).setNegativeButton(R.string.str_cancle).show();
					} catch (Exception e) { 
						e.printStackTrace();
					}
				}
			});
			return convertView;
		}
	}
	/***
	 * 获取设备信息
	 * 
	 * @author weirong.fan
	 *
	 */
	private class FetchNewDevice extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			mSwipeLayout.setRefreshing(false);
			adapter.notifyDataSetChanged();
			mListView.invalidate();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(Void... arg0) {
			try {
				DeviceInfoModel model = ShareHttpRequestUtil.getInstance().queryNewDevice(inputText, start_row,
						ShareCommons.SEARCH_PAGE_SIZE, ShareCommons.session_id);
				if (model.getReasonCode() == 1
						&& model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
					RegisterDeviceLogic.getInstance(context).shareRegister();
					model = ShareHttpRequestUtil.getInstance().queryNewDevice(inputText, start_row,
							ShareCommons.SEARCH_PAGE_SIZE, ShareCommons.session_id);
				}
				start_row += model.getDevices().size();
				List<Device> ld = new LinkedList<Device>();
				for (Device d1 : model.getDevices()) {
					boolean flag = false;
					for (Device d2 : listDevicesModel) {
						if (d1.getDevice_code().equals(d2.getDevice_code())) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						ld.add(d1);
					}
				}
				if (ld.size() > 0) {
					listDevicesModel.addAll(ld);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}
	/***
	 * 添加设备
	 * 
	 * @author weirong.fan
	 *
	 */
	private class AddNewDevice extends AsyncTask<Void, Void, BaseResultInfoModel> {
		private String message;
		private Device dm;
		private Button btn;
		private AddNewDevice(String message, Device dm, Button btn) {
			super();
			this.message = message;
			this.dm = dm;
			this.btn = btn;
		}
		@Override
		protected void onPostExecute(BaseResultInfoModel result) {
			super.onPostExecute(result);
			if (result.getReasonCode() == 1) {// 网络正常
				if (result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {// 加入正常
					this.btn.setVisibility(View.GONE);
					try {
						ShareDeviceModel m = ShareDataBase.getInstance(context)
								.fetchDeviceByDeviceCode(dm.getDevice_code());
						if (null == m) {
							m = new ShareDeviceModel();
							m.setDeviceCode(dm.getDevice_code());
							m.setDeviceName(dm.getDevice_name());
							m.setDeviceType(dm.getDevice_type());
							m.setDeviceMessage(message);
							m.setDeviceStatus(ShareDeviceModel.STATUS_NEW);
							ShareDataBase.getInstance(context).insertDevice(m);
						} else {
							if (m.getDeviceStatus() != ShareDeviceModel.STATUS_ADDED) {
								m.setDeviceStatus(ShareDeviceModel.STATUS_NEW);
								m.setDeviceMessage(message);
								ShareDataBase.getInstance(context).updateDevice(m);
							}
						}
						adapter.notifyDataSetChanged();
						mListView.invalidate();
						Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
						sendBroadcast(intent);
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.showToastShort(context, getString(R.string.share_project_exception));
					}
				} else {
					ToastUtil.showToastShort(context, getString(R.string.share_project_exception));
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
			BaseResultInfoModel mode = ShareHttpRequestUtil.getInstance().requestAddNewDevice(dm.getDevice_code(),
					message, ShareCommons.session_id);
			if (mode.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
				RegisterDeviceLogic.getInstance(context).shareRegister();
				mode = ShareHttpRequestUtil.getInstance().requestAddNewDevice(dm.getDevice_code(), message,
						ShareCommons.session_id);
			}
			return mode;
		}
	}
	/**
	 * 文本更新监听
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			boolean isNum = false;
			try {
				Integer.parseInt(s.toString());
				isNum = true;
			} catch (Exception ex) {
				ex.printStackTrace();
				isNum = false;
			}
			start_row = 0;
			listDevicesModel.clear();
			inputText = inputTxt.getText().toString();
			if (isNum && null != inputText && inputText.length() >= 3) {
				new FetchNewDevice().execute();
			} else {
				if (!isNum && null != inputText && inputText.length() > 0) {
					new FetchNewDevice().execute();
				}
			}
			adapter.notifyDataSetChanged();
			mListView.invalidate();
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
}

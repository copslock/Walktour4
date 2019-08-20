package com.walktour.gui.weifuwu;

import android.annotation.SuppressLint;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicExpandableListActivity;
import com.walktour.gui.R;
import com.walktour.gui.share.logic.RegisterDeviceLogic;
import com.walktour.gui.share.logic.ShareHttpRequestUtil;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.upload.UploadCallback;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupRelationModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
@SuppressLint("NewApi")
public class WeiMain2Activity extends BasicExpandableListActivity implements OnClickListener {
	private Context context = WeiMain2Activity.this;
	private int requestCode = 0x1199;
	private MyExpandableListAdapter adapter;
	/** 进度提示 */
	private ProgressDialog progressDialog;
	/**
	 * 创建一级条目容器
	 */
	List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
	/**
	 * 存放内容, 以便显示在列表中
	 */
	List<List<Map<String, String>>> childs = new ArrayList<List<Map<String, String>>>();
	private MyReceiver receiver = new MyReceiver();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weifuwumain2layout);
		setListData();
		adapter = new MyExpandableListAdapter();
		this.setListAdapter(adapter); 
		// 加入列表
		this.getExpandableListView().expandGroup(1, true);
		this.getExpandableListView().expandGroup(0, true);
		IntentFilter filter = new IntentFilter();
		filter.addAction(UploadCallback.ACTION);
		filter.addAction(ShareCommons.SHARE_ACTION_MAIN_2);
		this.registerReceiver(receiver, filter);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	/**
	 * 设置列表内容
	 */
	public void setListData() {
		try {
			groups.clear();
			childs.clear();
			// 创建二个一级条目标题
			Map<String, String> title_1 = new HashMap<String, String>();
			Map<String, String> title_2 = new HashMap<String, String>();
			title_1.put("group", this.getString(R.string.share_project_devices_group));
			title_2.put("group", this.getString(R.string.share_project_devices_device));
			groups.add(title_1);
			groups.add(title_2);
			List<ShareGroupModel> listGroup = ShareDataBase.getInstance(context).fetchAllGroup();
			List<Map<String, String>> childs_1 = new ArrayList<Map<String, String>>();
			for (ShareGroupModel g : listGroup) {
				Map<String, String> title_1_content_1 = new HashMap<String, String>();
				title_1_content_1.put("child1", "-1");
				title_1_content_1.put("child2", g.getGroupName());
				title_1_content_1.put("child3", getString(R.string.share_project_device_code)+":" + g.getGroupCode());
				List<ShareGroupRelationModel> list = ShareDataBase.getInstance(context)
						.fetchGroupRelation(g.getGroupCode());
				title_1_content_1.put("child4",
						String.format(getString(R.string.share_project_devices), list.size() + ""));
				title_1_content_1.put("child5", g.getGroupCode() + "");
				title_1_content_1.put("child6", "");
				title_1_content_1.put("child7", "");
				childs_1.add(title_1_content_1);
			}
			childs.add(childs_1);
			List<ShareDeviceModel> listDevices = new LinkedList<ShareDeviceModel>();
			List<Integer> status = new LinkedList<Integer>();
//			status.add(ShareDeviceModel.STATUS_NEW);
			status.add(ShareDeviceModel.STATUS_CONFIRM);
			listDevices.addAll(ShareDataBase.getInstance(context).fetchAllDeviceByStatus(status));
			status.clear();
			status.add(ShareDeviceModel.STATUS_ADDED);
			listDevices.addAll(ShareDataBase.getInstance(context).fetchAllDeviceByStatus(status));
			List<Map<String, String>> childs_2 = new ArrayList<Map<String, String>>();
			for (ShareDeviceModel d : listDevices) {
				Map<String, String> title_1_content_1 = new HashMap<String, String>();
				title_1_content_1.put("child1", d.getDeviceOS() + "");
				title_1_content_1.put("child2", d.getDeviceName());
				title_1_content_1.put("child3", getString(R.string.share_project_device_code) +":"+ d.getDeviceCode());
				title_1_content_1.put("child4", "");
				title_1_content_1.put("child5", d.getDeviceCode() + "");
				title_1_content_1.put("child6", d.getDeviceStatus() + "");
				title_1_content_1.put("child7", d.getDeviceMessage() + "");
				childs_2.add(title_1_content_1);
			}
			childs.add(childs_2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 列表内容按下
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		Bundle bundle = new Bundle();
		if (groups.get(groupPosition).get("group").equals(this.getString(R.string.share_project_devices_group))) {
			// 组
			bundle.putString("isFrom", "group");
			bundle.putString("groupCode", childs.get(groupPosition).get(childPosition).get("child5"));
			jumpActivityForResult(WeiMainHistoryActivity.class, bundle, requestCode);
		} else {
			String status=childs.get(groupPosition).get(childPosition).get("child6");
			if(null!=status&&!status.equals("")){
				if(status.equals(ShareDeviceModel.STATUS_ADDED+"")){
					// 终端
					bundle.putString("isFrom", "relation-device");
					bundle.putString("deviceCode", childs.get(groupPosition).get(childPosition).get("child5"));
					jumpActivity(WeiMainHistoryActivity.class, bundle);
				}
			}
		}
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}
	/**
	 * 二级标题按下
	 */
	@Override
	public boolean setSelectedChild(int groupPosition, int childPosition, boolean shouldExpandGroup) {
		return super.setSelectedChild(groupPosition, childPosition, shouldExpandGroup);
	}
	/**
	 * 一级标题按下
	 */
	@Override
	public void setSelectedGroup(int groupPosition) {
		super.setSelectedGroup(groupPosition);
	}
	@Override
	public void onClick(View v) {
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (this.requestCode == requestCode) {
			setListData();
			adapter.notifyDataSetChanged();
			this.getExpandableListView().invalidate();
		}
	}
	private class MyExpandableListAdapter extends BaseExpandableListAdapter {
		LayoutInflater inflate = (LayoutInflater) WeiMain2Activity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
		@Override
		public Object getChild(int arg0, int arg1) {
			return childs.get(arg0).get(arg1);
		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View view = inflate.inflate(R.layout.weifuwumain2layout_childs, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.objimg);
			TextView textView1 = (TextView) view.findViewById(R.id.name1);
			TextView textView2 = (TextView) view.findViewById(R.id.name2);
			TextView textView3 = (TextView) view.findViewById(R.id.joinnumbers);
			Button add = (Button) view.findViewById(R.id.addmem);
			Button refuse = (Button) view.findViewById(R.id.refusemem);
			List<Map<String, String>> objs = childs.get(groupPosition);
			final Map<String, String> map = objs.get(childPosition);
			if (map.get("child1").equalsIgnoreCase(ShareDeviceModel.OS_ANDROID + "")) {
				imageView.setBackgroundResource(R.drawable.obj_android);
			} else if (map.get("child1").equalsIgnoreCase(ShareDeviceModel.OS_IOS + "")) {
				imageView.setBackgroundResource(R.drawable.obj_iphone);
			} else {
				imageView.setBackgroundResource(R.drawable.obj_group);
			}
			textView1.setText(map.get("child2") + "");
			textView2.setText(map.get("child3") + "");
			textView3.setText(map.get("child4") + "");
			if (map.get("child6").equals("")) {
				add.setVisibility(View.GONE);
				refuse.setVisibility(View.GONE);
			} else {
				if (map.get("child6").equals(ShareDeviceModel.STATUS_CONFIRM + "")) {
					add.setVisibility(View.VISIBLE);
					refuse.setVisibility(View.VISIBLE);
					textView1.setText(map.get("child2") + "("+map.get("child3")+")");
					textView2.setText(map.get("child7"));
				} else {
					add.setVisibility(View.GONE);
					refuse.setVisibility(View.GONE);
					if (map.get("child6").equals(ShareDeviceModel.STATUS_NEW + "")) {
						textView3.setText(R.string.share_project_devices_requiring);
					}
				}
			}
			add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CheckDevice(map.get("child5"), "1").execute();
				}
			});
			refuse.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CheckDevice(map.get("child5"), "2").execute();
				}
			});
			return view;
		}
		@Override
		public int getChildrenCount(int groupPosition) {
			return childs.get(groupPosition).size();
		}
		@Override
		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}
		@Override
		public int getGroupCount() {
			return groups.size();
		}
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			View view = inflate.inflate(R.layout.weifuwumain2layout_groups, null);
			TextView textView = (TextView) view.findViewById(R.id.textGroup);
			textView.setText(groups.get(groupPosition).get("group") + "");
			return view;
		}
		@Override
		public boolean hasStableIds() {
			return true;
		}
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
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
				if (!result.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)&&!result.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_ADDED)) {// 处理失败
					ToastUtil.showToastShort(context, getString(R.string.total_faild));
				} else {// 更新成功
					try {
						ShareDeviceModel sm = ShareDataBase.getInstance(context).fetchDeviceByDeviceCode(relation_code);
						if (check_flag.equals("1")) {
							// 同意
							sm.setDeviceStatus(ShareDeviceModel.STATUS_ADDED);
						} else {
							// 拒绝
							sm.setDeviceStatus(ShareDeviceModel.STATUS_REFUSED);
						}
						ShareDataBase.getInstance(context).updateDevice(sm);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(ShareCommons.SHARE_ACTION_MAIN_2);
					sendBroadcast(intent);
				}
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
					setListData();
					adapter.notifyDataSetChanged();
					getExpandableListView().invalidate();
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

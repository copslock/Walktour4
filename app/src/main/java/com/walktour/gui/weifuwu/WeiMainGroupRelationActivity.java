package com.walktour.gui.weifuwu;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;

import java.util.LinkedList;
import java.util.List;
/***
 * 群内成员信息界面
 * 
 * @author weirong.fan
 *
 */
public class WeiMainGroupRelationActivity extends BasicActivity implements OnClickListener {
	private Context context = WeiMainGroupRelationActivity.this;
	private List<ShareDeviceModel> listDevicesModel = new LinkedList<ShareDeviceModel>();
	/**创建群组的deviceCODE**/
	private String createDeviceCode = "";
	private String groupCode = "";
	/** 接收对象列表 **/
	private ListView listView;
	private MyAdapter adapter;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		groupCode = this.getIntent().getStringExtra("groupCode");
		setContentView(R.layout.weifuwumaingrouplayout);
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
		initTextView(R.id.title_txt).setText(getString(R.string.share_project_device_list));
		listView = this.initListView(R.id.listview);
		try {
			ShareGroupModel groupM=ShareDataBase.getInstance(context).fetchGroup(groupCode);
			createDeviceCode=groupM.getCreateDeviceCode();
			listDevicesModel.addAll(ShareDataBase.getInstance(context).fetchDeviceByGroupCode(groupCode));
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter = new MyAdapter(listDevicesModel);
		listView.setAdapter(adapter); 
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
			ImageView objImg = (ImageView) convertView.findViewById(R.id.objimg);
			TextView name1 = (TextView) convertView.findViewById(R.id.name1);
			TextView name2 = (TextView) convertView.findViewById(R.id.name2);
			TextView qunzhu = (TextView) convertView.findViewById(R.id.joingpointer);
			final ShareDeviceModel model = list.get(position);
			if (model.getDeviceOS() == ShareDeviceModel.OS_ANDROID) {
				objImg.setBackgroundResource(R.drawable.obj_android);
			} else {
				objImg.setBackgroundResource(R.drawable.obj_iphone);
			}
			name1.setText(model.getDeviceName());
			name2.setText(getString(R.string.share_project_device_code) +":"+ model.getDeviceCode());
			name2.setVisibility(View.VISIBLE);
			if (createDeviceCode.equals(model.getDeviceCode())) {
				qunzhu.setVisibility(View.VISIBLE);
				qunzhu.setText("");
				qunzhu.setBackgroundResource(R.drawable.obj_manager);
			} else {
				qunzhu.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});
			return convertView;
		}
	}
 
}

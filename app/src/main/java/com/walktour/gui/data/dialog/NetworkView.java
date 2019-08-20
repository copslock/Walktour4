package com.walktour.gui.data.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;
import com.walktour.gui.data.model.NetworkType;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class NetworkView extends BaseView{
	
	private View mView;
	private ListView mListView;
	public List<NetworkType> selectedList = new ArrayList<NetworkType>();
	public ListAdapter adapter;
	private String selectedKeys = "";

	public NetworkView(Context context, String type) {
		super(context, type);
		init();
	}

	public View getView() {
		return this.mView;
	}
	
	private void init() {
		mView = inflater.inflate(R.layout.business_fragment, null);
		mListView = (ListView)mView.findViewById(R.id.ListView02);
		selectedList.clear();
		initData();
	}
	private void initData() {
		adapter = new ListAdapter(getNetworkTypes());
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unused")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ViewHolder holder = (ViewHolder)arg1.getTag();
				if (adapter.getDatas().get(position).isChecked) {
					adapter.getDatas().get(position).isChecked = false;
					selectedList.remove(adapter.getDatas().get(position));
				} else {
					adapter.getDatas().get(position).isChecked = true;
					selectedList.add(adapter.getDatas().get(position));
				}
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 获取实际发生采样的测试任务
	 * @return
	 */
	private ArrayList<NetworkType> getNetworkTypes() {

		ArrayList<NetworkType> result  = new ArrayList<NetworkType>();
		selectedKeys = mPreferences.getString(FilterKey.KEY_NETWORK_TYPE_SELECTED + type, "");
		ArrayList<ShowInfoType> netList = ApplicationModel.getInstance().getNetList();//有权限的网络类型
		for (int i = 0; i < netList.size(); i++) {
			NetType nt = netList.get(i).getNetGroup();
			if (!nt.name().equalsIgnoreCase(NetType.Normal.name()) && !contain(result, nt.name())) {
				NetworkType networkType = new NetworkType();
				networkType.setNetName(nt.name());
				networkType.setNetType(6000 + nt.getNetType());
				networkType.isChecked = selectedKeys.contains(nt.getNetType() + "");
				if (networkType.isChecked) {
					selectedList.add(networkType);
				}
				result.add(networkType);
			}
		}
		return result;

	}
	

	private boolean contain(ArrayList<NetworkType> list, String name) {
		for (NetworkType networkType : list) {
			if (networkType.getNetName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressLint("InflateParams")
	public class ListAdapter extends BaseAdapter{
		private ArrayList<NetworkType> networkTypes;
		public ListAdapter(ArrayList<NetworkType> networkTypes) {
			this.networkTypes = networkTypes;
		}

		public ArrayList<NetworkType> getDatas() {
			return this.networkTypes;
		}
		@Override
		public int getCount() {
			return networkTypes.size();
		}

		@Override
		public Object getItem(int arg0) {
			return networkTypes.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.business_fragment_listitem, null);
				holder.tv_name = (TextView)view.findViewById(R.id.txt_name);
				holder.img_choose = (ImageView)view.findViewById(R.id.checkBox1);
				view.setTag(holder);
			} else {
				holder = (ViewHolder)view.getTag();
			}
			holder.tv_name.setText(networkTypes.get(position).getNetName());
			boolean checked = networkTypes.get(position).isChecked;
			holder.img_choose.setImageResource(checked ? R.drawable.btn_check_on : R.drawable.btn_check_off);
			return view;
		}
		
	}
	
	private static class ViewHolder{
		private TextView tv_name;
		private ImageView img_choose;
	}
	
}

package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dinglicom.data.model.RecordTestInfo;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;

import java.util.LinkedList;
import java.util.List;

/**
 * 地铁view
 * 
 * @author weirong.fan
 *
 */
public class MetroView extends BaseView {

	private View mView;
	private List<RecordTestInfo> allCitys = new LinkedList<RecordTestInfo>();// 包含重复的城市
	private List<RecordTestInfo> cityLists = new LinkedList<RecordTestInfo>();// 不包含重复的城市
	private List<RecordTestInfo> currentCityLineLists = new LinkedList<RecordTestInfo>();
	private ListView leftListView;
	private LeftListViewAdapter leftAdapter;
	private ListView rightListView;
	private RightListViewAdapter rightAdapter;
	private String currentCity = "";// 当前选择的城市
	private List<String> routes = new LinkedList<String>();// 当前选择的线路
	private Context context;
	private boolean isSelect = false;

	public MetroView(Context context, String type) {
		super(context, type);
		this.context = context;
		isSelect = mPreferences.getBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, false);
		String rr = mPreferences.getString(FilterKey.KEY_ROUTE_SELECTED + type, "");
		if(isSelect){
		 String[] arr=rr.split(",");
		 for(int i=0;i<arr.length;i++){
			 routes.add(arr[i]);
		 } 
		}
		init();

	}

	public View getView() {
		return mView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_reset:
			clear(); 
			isSelect=false;
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;
		case R.id.btn_summit:
			if(routes.size()<=0){ 
				ToastUtil.showToastShort(context, R.string.metro_select_route);
				return;
			}
			save();
			if (mCallBack != null) {
				mCallBack.onSummit();
			}
			break;

		default:
			break;
		}
	}

	private void init() {
		mView = inflater.inflate(R.layout.metro_view_root, null);
		mView.findViewById(R.id.btn_reset).setOnClickListener(this);
		mView.findViewById(R.id.btn_summit).setOnClickListener(this);
		leftListView = (ListView) mView.findViewById(R.id.left_listview);
		rightListView = (ListView) mView.findViewById(R.id.right_listview);
		cityLists = getCitys();
		if (null != allCitys && allCitys.size() > 0) {
			int selectIndex = 0;
			if (isSelect) {
				String city = mPreferences.getString(FilterKey.KEY_CITY_SELECTED + type, "");
				for (int index = 0; index < cityLists.size(); index++) {
					if (city.equals(cityLists.get(index).key_value)) {
						selectIndex=index;
						break;
					}
				}
			}
			currentCity = cityLists.get(selectIndex).key_value; 
			List<String> taskNos = new LinkedList<String>();
			for (RecordTestInfo rt : allCitys) {
				if (rt.key_value.equals(currentCity)) {
					taskNos.add(rt.task_no);
				}
			}
			currentCityLineLists = this.getCurrentCityLineLists(taskNos);
		}

		leftAdapter = new LeftListViewAdapter();
		rightAdapter = new RightListViewAdapter();
		leftListView.setAdapter(leftAdapter);
		rightListView.setAdapter(rightAdapter);
		if (!enable()) {
//			mView.findViewById(R.id.nocityway).setVisibility(View.VISIBLE);
			return;
		}
	}

	private void refreshRightData(String cityName) {
		currentCityLineLists.clear();
		List<String> taskNos = new LinkedList<String>();
		for (RecordTestInfo rt : allCitys) {
			if (rt.key_value.equals(cityName)) {
				taskNos.add(rt.task_no);
			}
		}
		currentCityLineLists = this.getCurrentCityLineLists(taskNos);
		rightAdapter.notifyDataSetChanged();
		rightListView.invalidate();
	}

	/**
	 * 城市item
	 * 
	 * @author andy
	 *
	 */
	private class ListCityItem {
		private TextView cityName;
	}

	/***
	 * 城市列表
	 * 
	 * @author andy
	 *
	 */
	private class LeftListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cityLists.size();
		}

		@Override
		public RecordTestInfo getItem(int position) {
			return cityLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RecordTestInfo testInfo = cityLists.get(position);
			ListCityItem itemView = null;
			if (convertView == null) {
				itemView = new ListCityItem();
				convertView = inflater.inflate(R.layout.highspeedrail_view_root_item, parent, false);
				convertView.findViewById(R.id.projectid).setVisibility(View.GONE);
				itemView.cityName = (TextView) convertView.findViewById(R.id.projectname);
				convertView.setTag(itemView);
			} else {
				itemView = (ListCityItem) convertView.getTag();
			}
			itemView.cityName.setText(testInfo.key_value + "");

			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					currentCity = testInfo.key_value;
					mPreferences.edit().putBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, false).commit();
					isSelect=false;
					routes.clear();
					refreshRightData(testInfo.key_value);
				}
			});
			return convertView;
		}

	}

	/**
	 * 城市列表对应的线路列表
	 * 
	 * @author andy
	 *
	 */
	private class RightListViewAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return currentCityLineLists.size();
		}

		@Override
		public RecordTestInfo getItem(int position) {
			return currentCityLineLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RecordTestInfo testInfo = currentCityLineLists.get(position);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.highspeedrail_view_root_item, parent, false);
			}
			final TextView cityName = (TextView) convertView.findViewById(R.id.projectname);
			final CheckBox check = (CheckBox) convertView.findViewById(R.id.projectid);
			cityName.setText(testInfo.key_value + "");
			if (isSelect) { 
				if (routes.contains(testInfo.key_value)) {
					check.setChecked(true); 
				}
			} else {
				check.setChecked(false); 
			}

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (check.isChecked()) {
						check.setChecked(false); 
					} else {
						check.setChecked(true); 
					}
				}
			});
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {// 选择
						if (!routes.contains(testInfo.key_value))
							routes.add(testInfo.key_value);
						
					} else {// 取消
						routes.remove(testInfo.key_value);
						
					}
					
					if(routes.size()>0){
						isSelect=true;
						mPreferences.edit().putBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, true).commit();
					}else{
						isSelect=false;
						mPreferences.edit().putBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, false).commit();
					}
				}
			});
			return convertView;
		}
	}

	public boolean enable() {
		return this.cityLists == null || this.cityLists.size() == 0 ? false : true;
	}

	/***
	 * 去除相同的城市
	 * 
	 * @return
	 */
	private List<RecordTestInfo> getCitys() {
		cityLists.clear();
		allCitys = mDBManager.getRecordTestInfoCitys(ApplicationModel.getInstance().getSelectScene() + "");
		for (RecordTestInfo lr : allCitys) {
			boolean flag = false;
			for (RecordTestInfo rt : cityLists) {
				if (lr.key_value.equals(rt.key_value)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				cityLists.add(lr);
			}
		}
		return cityLists;
	}

	private List<RecordTestInfo> getCurrentCityLineLists(List<String> taskNos) {
		if (null == taskNos || taskNos.size() <= 0)
			return new LinkedList<RecordTestInfo>();
		return mDBManager.getRecordTestInfoLines(taskNos, "metro_line");
	}

	private void clear() {
		mPreferences.edit().putString(FilterKey.KEY_ROUTE_SELECTED + type, "").commit();
		mPreferences.edit().putString(FilterKey.KEY_CITY_SELECTED + type, "").commit();
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, false).commit();
	}

	private void save() {
		String floor_node_ids = "";
		for (int i = 0; i < routes.size(); i++) {
			floor_node_ids += routes.get(i) + ",";
		}
		if (floor_node_ids.contains(",")) {
			floor_node_ids = floor_node_ids.substring(0, floor_node_ids.lastIndexOf(","));
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_ROUTE_SETTING + type, true).commit();
		mPreferences.edit().putString(FilterKey.KEY_CITY_SELECTED + type, currentCity).commit();
		mPreferences.edit().putString(FilterKey.KEY_ROUTE_SELECTED + type, floor_node_ids).commit();
	}
 
	private ClickListenerCallBack mCallBack;

	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
}

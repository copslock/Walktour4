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
 * 单站验证view
 *
 * @author weirong.fan
 *
 */
public class SingleStationView extends BaseView {

	private View mView;
	private ListView listView;
	private RightListViewAdapter adapter;
	private List<String> routes = new LinkedList<String>();// 当前选择的基站
	private Context context;
	private boolean isSelect = false;
	private List<RecordTestInfo> currentLineLists = new LinkedList<RecordTestInfo>();

	public SingleStationView(Context context, String type) {
		super(context, type);
		this.context = context;
		isSelect = mPreferences.getBoolean(FilterKey.KEY_IS_STATION_SETTING + type, false);
		String rr = mPreferences.getString(FilterKey.KEY_STATION_SELECTED + type, "");
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
		mView = inflater.inflate(R.layout.highspeedrail_view_root, null);
		mView.findViewById(R.id.btn_reset).setOnClickListener(this);
		mView.findViewById(R.id.btn_summit).setOnClickListener(this);
		listView = (ListView) mView.findViewById(R.id.left_listview);
		currentLineLists = mDBManager
				.getRecordTestInfoStations(ApplicationModel.getInstance().getSelectScene().getSceneTypeId());
		adapter = new RightListViewAdapter();
		listView.setAdapter(adapter);
		if (!enable()) {
			//			mView.findViewById(R.id.nocityway).setVisibility(View.VISIBLE);
			return;
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
			return currentLineLists.size();
		}

		@Override
		public RecordTestInfo getItem(int position) {
			return currentLineLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RecordTestInfo testInfo = currentLineLists.get(position);

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
						mPreferences.edit().putBoolean(FilterKey.KEY_IS_STATION_SETTING + type, true).commit();
					}else{
						isSelect=false;
						mPreferences.edit().putBoolean(FilterKey.KEY_IS_STATION_SETTING + type, false).commit();
					}
				}
			});
			return convertView;
		}
	}

	public boolean enable() {
		return this.currentLineLists == null || this.currentLineLists.size() == 0 ? false : true;
	}

	private void clear() {
		mPreferences.edit().putString(FilterKey.KEY_STATION_SELECTED + type, "").commit();
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_STATION_SETTING + type, false).commit();
	}

	private void save() {
		String floor_node_ids = "";
		for (int i = 0; i < routes.size(); i++) {
			floor_node_ids += routes.get(i) + ",";
		}
		if (floor_node_ids.contains(",")) {
			floor_node_ids = floor_node_ids.substring(0, floor_node_ids.lastIndexOf(","));
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_STATION_SETTING + type, true).commit();
		mPreferences.edit().putString(FilterKey.KEY_STATION_SELECTED + type, floor_node_ids).commit();
	}

	private ClickListenerCallBack mCallBack;

	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
}

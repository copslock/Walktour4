package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinglicom.data.model.RecordBuild;
import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;
import com.walktour.gui.data.model.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * 楼层view
 * @author haohua
 *
 */
public class FloorView extends BaseView{

	private View mView;
	private ArrayList<Building> buildingList;
	private LinearLayout contentLayout;
	private List<Building> selectedList = new ArrayList<Building>();
	private List<CheckBox> allFloorCheckBoxes = new ArrayList<CheckBox>();
	
	public FloorView(Context context, String type) {
		super(context, type);
		init();
		initData();
	}
	
	public View getView() {
		return this.mView;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_reset:
			clear();
			if (mCallBack != null) {
				mCallBack.onClear();
			}
			break;
		case R.id.btn_summit:
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
		mView = inflater.inflate(R.layout.floor_view_root, null);
		this.mView.findViewById(R.id.btn_reset).setOnClickListener(this);
		this.mView.findViewById(R.id.btn_summit).setOnClickListener(this);
		this.contentLayout = (LinearLayout)this.mView.findViewById(R.id.content_layout);
		
	}
	
	public boolean enable() {
		return this.buildingList == null || this.buildingList.size() == 0 ? false : true;
	}
	
	private void initData() {
		allFloorCheckBoxes.clear();
		selectedList.clear();
		String selectedFloorNodeIds = mPreferences.getString(FilterKey.KEY_FLOOR_SELECTED + type, "");
        buildingList = getBuildings();
        if (!enable()) {
//        	clear();
        	View noteView = inflater.inflate(R.layout.note_text, contentLayout);
        	((TextView)noteView.findViewById(R.id.txt_note)).setText(mContext.getResources().getString(R.string.str_note_text_no_floor_info));
        	return;
        }
        for (int i = 0; i < buildingList.size(); i++) {
			Building item = buildingList.get(i);
			 if (item.floors != null && item.floors.size() > 0) {
				 View buildingView = inflater.inflate(R.layout.floor_building_item, null);
				 contentLayout.addView(buildingView);
				 if (item.floors.size() == 1) {
					 buildingView.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
				 }
				 TextView tvBuilding = (TextView)buildingView.findViewById(R.id.building_name);
				 tvBuilding.setText(item.node_name);
				 LinearLayout floorLayout = (LinearLayout) buildingView.findViewById(R.id.floor_layout);
				 for (int j = 0; j < item.floors.size(); j++) {
					Building floorModel = item.floors.get(j);
					View floorView = inflater.inflate(R.layout.floor_floor_item, null);
					floorLayout.addView(floorView);
					if (j == item.floors.size() - 1) {
						floorView.findViewById(R.id.floor_divider).setVisibility(View.INVISIBLE);
					}
					CheckBox cbFloor = (CheckBox)floorView.findViewById(R.id.floor_check);
					cbFloor.setTag(floorModel);
					allFloorCheckBoxes.add(cbFloor);
					if (selectedFloorNodeIds.contains(floorModel.node_id)) {
						cbFloor.setChecked(true);
					}
					cbFloor.setOnCheckedChangeListener(onCheckedChangeListener);
					cbFloor.setText(floorModel.node_name);
				}
			 }
		}
	}
	
	private ArrayList<Building> getBuildings() {
		ArrayList<Building> result = new ArrayList<Building>();
		ArrayList<RecordBuild> list = mDBManager.getRecordBuilds(type);
		for (RecordBuild recordBuild : list) {
			if (recordBuild.parent_id.equals("0")) {
				Building building = new Building();
				building.node_id = recordBuild.node_id;
				building.parent_id = recordBuild.parent_id;
				building.node_name = recordBuild.node_name;
				building.node_info = recordBuild.node_info;
				result.add(building);
			} else {
				Building floor = new Building();
				floor.node_id = recordBuild.node_id;
				floor.parent_id = recordBuild.parent_id;
				floor.node_name = recordBuild.node_name;
				floor.node_info = recordBuild.node_info;
				addFloor(result, floor);
			}
		}
		return result;
 	}
	
	private void addFloor(ArrayList<Building> buildings, Building floor) {
		for (Building building : buildings) {
			if (building.node_id.equals(floor.parent_id)) {
				building.floors.add(floor);
				return;
			}
		}
	}
	
	private void clear() {
		selectedList.clear();
		mPreferences.edit().putString(FilterKey.KEY_FLOOR_SELECTED + type, "").commit();
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_FLOOR_SETTING + type, false).commit();
		for (int i = 0; i < allFloorCheckBoxes.size(); i++) {
			allFloorCheckBoxes.get(i).setChecked(false);
		}
	}
	
	private void save() {
		String floor_node_ids = "";
		for (int i = 0; i < selectedList.size(); i++) {
			floor_node_ids += selectedList.get(i).node_id + ",";
		}
		if (floor_node_ids.contains(",")) {
			floor_node_ids = floor_node_ids.substring(0, floor_node_ids.lastIndexOf(","));
		}
		mPreferences.edit().putBoolean(FilterKey.KEY_IS_FLOOR_SETTING + type, true).commit();
		mPreferences.edit().putString(FilterKey.KEY_FLOOR_SELECTED + type, floor_node_ids).commit();
	}
	
	private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			Building floorModel = (Building)button.getTag();
			if (isChecked) {
				selectedList.add(floorModel);
			} else {
				selectedList.remove(floorModel);
			}
		}
	};
	
	private ClickListenerCallBack mCallBack;
	public void setClickListenerCallBack(ClickListenerCallBack callback) {
		this.mCallBack = callback;
	}
}

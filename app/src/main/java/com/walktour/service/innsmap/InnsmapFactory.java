package com.walktour.service.innsmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.walktour.Utils.StringUtil;
import com.walktour.service.innsmap.model.InnsmapModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 寅时室内自动打点测试工厂类
 * 
 * @author jianchao.wang
 *
 */
public class InnsmapFactory {
	/** 选择的城市ID */
	private static final String SELECT_CITY_ID = "select_city_id";
	/** 选择的建筑物ID */
	private static final String SELECT_BUILDING_ID = "select_building_id";
	/** 选择的建筑物名称 */
	private static final String SELECT_BUILDING_NAME = "select_building_name";
	/** 选择的楼层ID */
	private static final String SELECT_FLOOR_ID = "select_floor_id";
	/** 选择的楼层名称 */
	private static final String SELECT_FLOOR_NAME = "select_floor_name";
	/** 唯一实例 */
	private static InnsmapFactory sInstance;
	/** 可选城市列表 */
	private List<InnsmapModel> mCities = new ArrayList<InnsmapModel>();
	/** 选择的测试国家 */
	private InnsmapModel mCurrentCountry;
	/** 选择的测试城市 */
	private InnsmapModel mCurrentCity;
	/** 选择的测试建筑物 */
	private InnsmapModel mCurrentBuilding;
	/** 选择的测试楼层 */
	private InnsmapModel mCurrentFloor;
	/** 回放选择的楼层ID */
	private String mReplayFloorId;
	/** 属性存放关键字 */
	private String mSharedPrefsName;
	/**
	 * 上下文
	 */
	private Context mContext;

	private InnsmapFactory(Context context) {
		this.mSharedPrefsName = context.getPackageName() + "_innsmap";
		this.init(context);
		this.mContext = context;
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
		String selectCityId = preference.getString(SELECT_CITY_ID, "");
		if (!StringUtil.isNullOrEmpty(selectCityId)) {
			for (InnsmapModel city : this.mCities) {
				if (city.getId().equals(selectCityId)) {
					this.mCurrentCity = city;
					break;
				}
			}
		}
		String selectBuildingId = preference.getString(SELECT_BUILDING_ID, "");
		if (!StringUtil.isNullOrEmpty(selectBuildingId)) {
			InnsmapModel building = new InnsmapModel();
			building.setId(selectBuildingId);
			building.setName(preference.getString(SELECT_BUILDING_NAME, ""));
			this.mCurrentBuilding = building;
		}
		String selectFloorId = preference.getString(SELECT_FLOOR_ID, "");
		if (!StringUtil.isNullOrEmpty(selectFloorId)) {
			InnsmapModel floor = new InnsmapModel();
			floor.setId(selectFloorId);
			floor.setName(preference.getString(SELECT_FLOOR_NAME, ""));
			this.mCurrentFloor = floor;
		}
	}
	/**
	 * 返回唯一实例
	 *
	 * @param context
	 *          上下文
	 * @return
	 */
	public static synchronized InnsmapFactory getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new InnsmapFactory(context.getApplicationContext());
		}
		return sInstance;
	}

	public InnsmapModel getCurrentCountry() {
		return mCurrentCountry;
	}

	public void setCurrentCountry(InnsmapModel currentCountry) {
		mCurrentCountry = currentCountry;
	}

	public InnsmapModel getCurrentCity() {
		return mCurrentCity;
	}

	public void setCurrentCity(Context context, InnsmapModel currentCity) {
		if (this.mCurrentCity != null && currentCity.getId().equals(this.mCurrentCity.getId()))
			return;
		mCurrentCity = currentCity;
		SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(SELECT_CITY_ID, this.mCurrentCity.getId());
		editor.remove(SELECT_BUILDING_ID);
		editor.remove(SELECT_BUILDING_NAME);
		editor.remove(SELECT_FLOOR_ID);
		editor.remove(SELECT_FLOOR_NAME);
		editor.commit();
		this.mCurrentBuilding = null;
		this.mCurrentFloor = null;
	}

	public InnsmapModel getCurrentBuilding() {
		return mCurrentBuilding;
	}

	public void setCurrentBuilding(Context context, InnsmapModel currentBuilding) {
		if (this.mCurrentBuilding != null && currentBuilding.getId().equals(this.mCurrentBuilding.getId()))
			return;
		mCurrentBuilding = currentBuilding;
		SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(SELECT_BUILDING_ID, this.mCurrentBuilding.getId());
		editor.putString(SELECT_BUILDING_NAME, this.mCurrentBuilding.getName());
		editor.remove(SELECT_FLOOR_ID);
		editor.remove(SELECT_FLOOR_NAME);
		editor.commit();
		this.mCurrentFloor = null;
	}

	/**
	 * 获取当前的楼层
	 *
	 * @param isReplay
	 *          是否回放中
	 * @return
	 */
	public InnsmapModel getCurrentFloor(boolean isReplay) {
		if (isReplay) {
			if (StringUtil.isNullOrEmpty(this.mReplayFloorId))
				return null;
			InnsmapModel floor = new InnsmapModel();
			floor.setId(this.mReplayFloorId);
			return floor;
		}
		return mCurrentFloor;
	}

	public void setCurrentFloor(Context context, InnsmapModel currentFloor) {
		mCurrentFloor = currentFloor;
		SharedPreferences preference = context.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(SELECT_FLOOR_ID, this.mCurrentFloor.getId());
		editor.putString(SELECT_FLOOR_NAME, this.mCurrentFloor.getName());
		editor.commit();
	}
	public void setmCities(List<InnsmapModel> mCities) {
		this.mCities = mCities;
	}
	public String getReplayFloorId() {
		return mReplayFloorId;
	}

	public void setReplayFloorId(String replayFloorId) {
		mReplayFloorId = replayFloorId;
	}


	/**
	 * 获取保存在SharedPreferences里面的已选建筑id
	 * @return 已选建筑id
	 */
	public 	String getSelectedBuildingId(){
		SharedPreferences preference = mContext.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
		String selectedBuildingId = preference.getString(SELECT_BUILDING_ID,"");
		return selectedBuildingId;
	}

	/**
	 * 获取保存在SharedPreferences里面的已选楼层id
	 * @return 已选楼层id
	 */
	public String getSelectedFloorId(){
		SharedPreferences preference = mContext.getSharedPreferences(this.mSharedPrefsName, Context.MODE_PRIVATE);
		String SelectedFloorId = preference.getString(SELECT_FLOOR_ID,"");
		return SelectedFloorId;
	}
}

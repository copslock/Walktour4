package com.walktour.gui.newmap;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.gui.newmap.model.MyLatLng;
import com.walktour.model.AlarmModel;
import com.walktour.model.MapEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 地图工厂类，用于存放地图用到的共享参数
 *
 * @author jianchao.wang
 *
 */
public class NewMapFactory {
	// private final static String TAG = "MapFactory";
	/** 显示的地图类型：2D普通图 */
	public static final int MAP_TYPE_NORMAL_2D = 0;
	/** 显示的地图类型：3D普通图 */
	public static final int MAP_TYPE_NORMAL_3D = 1;
	/** 显示的地图类型：卫星图 */
	public static final int MAP_TYPE_SATELLITE = 2;
	/** 显示的地图类型：空图 */
	public static final int MAP_TYPE_NONE = 3;
	/** 唯一实例 */
	private static NewMapFactory sInstance;
	/** 当前所在的经纬度 */
	private MyLatLng nowLatLng;
	/** 当前地图的缩放等级 */
	private float zoomLevelNow;
	/** 缩放最大等级 */
	private float zoomLevelMax = 0;
	/** 缩放最小等级 */
	private float zoomLevelMin = 0;
	/** 是否进行测距操作 */
	private boolean isRanging = false;
	/** 基站数据 */
	private List<BaseStation> baseStationList = new ArrayList<BaseStation>();
	/** GPS轨迹点列表 */
	private List<MapEvent> locasList = new ArrayList<MapEvent>();
	/** 告警数组队列 */
	private List<AlarmModel> alarmList = new ArrayList<AlarmModel>();
	/** 当前的基站搜索的网络类型 */
	private int searchNetType = BaseStation.NETTYPE_GSM;
	/** 显示的地图类型 */
	private int mMapType = MAP_TYPE_NORMAL_2D;
	/** 地图名称 */
	private String mTitle;
	/** 选择的基站数据,不为空则居中高亮 */
	protected BaseStation mSelectBaseStation;

	/**选择的网络**/
	private List<Integer> netTypes=new LinkedList<>();
	private NewMapFactory() {

	}

	/**
	 * 返回唯一实例
	 *
	 * @return
	 */
	public static NewMapFactory getInstance() {
		if (sInstance == null) {
			sInstance = new NewMapFactory();
		}
		return sInstance;
	}

	public MyLatLng getNowLatLng() {
		return nowLatLng;
	}

	public void setNowLatLng(MyLatLng nowLatLng) {
		this.nowLatLng = nowLatLng;
	}

	public float getZoomLevelNow() {
		return zoomLevelNow;
	}

	public void setZoomLevelNow(float zoomLevelNow) {
		this.zoomLevelNow = zoomLevelNow;
	}

	public float getZoomLevelMax() {
		return zoomLevelMax;
	}

	public void setZoomLevelMax(float zoomLevelMax) {
		this.zoomLevelMax = zoomLevelMax;
	}

	public float getZoomLevelMin() {
		return zoomLevelMin;
	}

	public void setZoomLevelMin(float zoomLevelMin) {
		this.zoomLevelMin = zoomLevelMin;
	}

	public List<BaseStation> getBaseStationList() {
		return baseStationList;
	}

	public boolean isRanging() {
		return isRanging;
	}

	/**
	 * 设置测距操作
	 *
	 */
	public void setRanging() {
		this.isRanging = !this.isRanging;
	}

	public List<MapEvent> getLocasList() {
		return locasList;
	}

	public List<AlarmModel> getAlarmList() {
		return alarmList;
	}

	public int getSearchNetType() {
		return searchNetType;
	}

	public void setSearchNetType(int searchNetType) {
		this.searchNetType = searchNetType;
	}

	public int getMapType() {
		return mMapType;
	}

	public List<Integer> getNetTypes() {
		return netTypes;
	}


	/**
	 * 切换当前的地图类型
	 *
	 *          地图类型
	 */
	public void changeMapType() {
		switch (mMapType) {
			case MAP_TYPE_NORMAL_2D:
				this.mMapType = MAP_TYPE_SATELLITE;
				break;
			case MAP_TYPE_NORMAL_3D:
				this.mMapType = MAP_TYPE_NONE;
				break;
			case MAP_TYPE_SATELLITE:
				this.mMapType = MAP_TYPE_NORMAL_3D;
				break;
			case MAP_TYPE_NONE:
				this.mMapType = MAP_TYPE_NORMAL_2D;
				break;
		}
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public BaseStation getSelectBaseStation() {
		return mSelectBaseStation;
	}

	public void setSelectBaseStation(BaseStation selectBaseStation) {
		this.mSelectBaseStation = selectBaseStation;
	}

	public void setMapType(int mapType) {
		mMapType = mapType;
	}
}

package com.walktour.service.metro.model;

import com.walktour.Utils.StringUtil;
import com.walktour.gui.newmap.model.MyLatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 地铁线路
 * 
 * @author jianchao.wang
 *
 */
public class MetroRoute {
	/** 线路ID */
	private long mId;
	/** 线路序号 */
	private int mIndex;
	/** 线路名称 */
	private String mName = "";
	/** 站点列表 */
	private List<MetroStation> mStations = new ArrayList<MetroStation>();
	/** 当前线路轨迹点GPS经纬度坐标 */
	private List<MyLatLng> mKml = new ArrayList<MyLatLng>();
	/** 当前线路轨迹点百度经纬度坐标 */
	private List<MyLatLng> mBaiduKml = new ArrayList<MyLatLng>();
	/** 当前线路轨迹点高德经纬度坐标 */
	private List<MyLatLng> mGaoDeKml = new ArrayList<MyLatLng>();
	/** 设置的开始站点 */
	private MetroStation mStartStation = null;
	/** 设置的到达站点 */
	private MetroStation mEndStation = null;
	/** 是否正向 */
	private boolean isForward = true;
	/** 线路开始时间,小时分钟的(小时*60*60+分钟*60) */
	private long mStartTime = 0;
	/** 线路结尾时间,小时分钟的(小时*60*60+分钟*60) */
	private long mEndTime = 0;

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public List<MetroStation> getStations() {
		List<MetroStation> list = new ArrayList<MetroStation>();
		if (this.isForward)
			list.addAll(this.mStations);
		else
			for (int i = this.mStations.size() - 1; i >= 0; i--) {
				list.add(this.mStations.get(i));
			}
		return list;
	}

	public void setStations(List<MetroStation> stations) {
		mStations = stations;
	}

	public List<MyLatLng> getKml() {
		return this.mKml;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (!(o instanceof MetroRoute))
			return false;
		MetroRoute route = (MetroRoute) o;
		return route.mId == this.mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public boolean isForward() {
		return isForward;
	}

	public void setForward(boolean isForward) {
		this.isForward = isForward;
	}

	/**
	 * 获取路线的描述
	 * 
	 * @return
	 */
	public String getRouteDesc() {
		List<MetroStation> list = this.getStations();
		return list.get(0).getName() + "-" + list.get(list.size() - 1).getName();
	}

	/**
	 * 获取路线的选择的描述
	 * 
	 * @return
	 */
	public String getRouteSelectDesc() {
		String startStation = "";
		String endStation = "";
		for (MetroStation station : this.mStations) {
			if (station.getState() == MetroStation.STATE_START)
				startStation = station.getName();
			else if (station.getState() == MetroStation.STATE_END)
				endStation = station.getName();
		}
		if (StringUtil.isNullOrEmpty(startStation) || StringUtil.isNullOrEmpty(endStation))
			return this.getRouteDesc();
		return startStation + "-" + endStation;
	}

	/**
	 * 获取路线在数据管理的描述信息
	 * 
	 * @return
	 */
	public String getRouteFilterDesc() {
		return this.mName;// + "(" + this.getRouteDesc() + ")";
	}

	/**
	 * 初始化站点状态
	 */
	public void initStationState() {
		if (this.mStartStation == null || this.mEndStation == null) {
			for (MetroStation station : this.mStations) {
				station.setState(MetroStation.STATE_CAN_SELECT);
			}
			this.mStartStation = null;
			this.mEndStation = null;
		} else {
			this.isForward = this.mStartStation.getIndex() < this.mEndStation.getIndex();
			boolean isIn = false;
			for (int i = 0; i < this.mStations.size(); i++) {
				MetroStation station = this.mStations.get(i);
				station.setState(MetroStation.STATE_CAN_SELECT);
				if (station.equals(this.mStartStation)) {
					isIn = !isIn;
					station.setState(MetroStation.STATE_START);
				} else if (station.equals(this.mEndStation)) {
					isIn = !isIn;
					station.setState(MetroStation.STATE_END);
				} else {
					if (!isIn) {
						station.setState(MetroStation.STATE_CANT_SELECT);
					}
				}
			}
		}
	}

	public MetroStation getStartStation() {
		return mStartStation;
	}

	public void setStartStation(MetroStation startStation) {
		mStartStation = startStation;
	}

	public MetroStation getEndStation() {
		return mEndStation;
	}

	public void setEndStation(MetroStation endStation) {
		mEndStation = endStation;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public void setStartTime(long startTime) {
		mStartTime = startTime;
	}

	public long getEndTime() {
		return mEndTime;
	}

	public void setEndTime(long endTime) {
		mEndTime = endTime;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	public void setKml(List<MyLatLng> kml) {
		mKml = kml;
	}

	public List<MyLatLng> getBaiduKml() {
		return mBaiduKml;
	}

	public List<MyLatLng> getGaoDeKml() {
		return mGaoDeKml;
	}

	public void setGaoDeKml(List<MyLatLng> mGaoDeKml) {
		this.mGaoDeKml = mGaoDeKml;
	}

	public void setBaiduKml(List<MyLatLng> baiduKml) {
		mBaiduKml = baiduKml;
	}

	@Override
	public String toString() {
		return "MetroRoute{" +
				"mId=" + mId +
				", mIndex=" + mIndex +
				", mName='" + mName + '\'' +
				", mStations=" + mStations +
				", mKml=" + mKml +
				", mBaiduKml=" + mBaiduKml +
				", mGaoDeKml=" + mGaoDeKml +
				", mStartStation=" + mStartStation +
				", mEndStation=" + mEndStation +
				", isForward=" + isForward +
				", mStartTime=" + mStartTime +
				", mEndTime=" + mEndTime +
				'}';
	}
}

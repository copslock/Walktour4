package com.walktour.service.metro.model;

import com.walktour.gui.newmap.model.MyLatLng;

import java.io.Serializable;

/**
 * 地铁站点
 * 
 * @author jianchao.wang
 *
 */
public class MetroStation implements Serializable{
	/** 站点状态：可选择 */
	public static final int STATE_CAN_SELECT = 0;
	/** 站点状态：不可选择 */
	public static final int STATE_CANT_SELECT = 1;
	/** 站点状态：出发站点 */
	public static final int STATE_START = 2;
	/** 站点状态：到达站点 */
	public static final int STATE_END = 3;
	/** 站点ID */
	private long mId = 0;
	/** 站点名称 */
	private String mName = "";
	/** 当前站点的GPS经纬度 */
	private MyLatLng mLatLng;
	/** 当前站点的百度经纬度 */
	private MyLatLng mBaiduLatLng;
	/** 站点序号 */
	private int mIndex;
	/** 站点状态 */
	private int mState = STATE_CAN_SELECT;
	/** 是否到达当前站点 */
	private boolean isReach = false;
	/** 站点时刻表首班,小时分钟的(小时*60*60+分钟*60) 表示 */
	private long mFirstTime;

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public MyLatLng getLatLng() {
		return mLatLng;
	}

	public void setLatLng(MyLatLng latLng) {
		mLatLng = latLng;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (!(o instanceof MetroStation))
			return false;
		MetroStation station = (MetroStation) o;
		return station.mId == this.mId;
	}

	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}

	public boolean isReach() {
		return isReach;
	}

	public void setReach(boolean isReach) {
		this.isReach = isReach;
	}

	public long getFirstTime() {
		return mFirstTime;
	}

	public void setFirstTime(long firstTime) {
		mFirstTime = firstTime;
	}

	public MyLatLng getBaiduLatLng() {
		return mBaiduLatLng;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	public void setBaiduLatLng(MyLatLng baiduLatLng) {
		mBaiduLatLng = baiduLatLng;
	}

	@Override
	public String toString() {
		return "MetroStation{" +
				"mId=" + mId +
				", mName='" + mName + '\'' +
				", mLatLng=" + mLatLng +
				", mBaiduLatLng=" + mBaiduLatLng +
				", mIndex=" + mIndex +
				", mState=" + mState +
				", isReach=" + isReach +
				", mFirstTime=" + mFirstTime +
				'}';
	}
}

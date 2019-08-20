package com.walktour.service.metro.model;

import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 地铁城市
 * 
 * @author jianchao.wang
 *
 */
public class MetroCity {
	/** 城市名称 */
	private String mName = "";
	/** 城市所属地铁线路列表 */
	private List<MetroRoute> mRoutes = new ArrayList<MetroRoute>();
	/** 文件存放路径 */
	private String mFilePath = "";
	/** 城市映射表<城市ID，城市对象> */
	private LongSparseArray<MetroRoute> mRouteMap = new LongSparseArray<MetroRoute>();

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public List<MetroRoute> getRoutes() {
		return mRoutes;
	}

	public void setRoutes(List<MetroRoute> routes) {
		mRoutes = routes;
		this.mRouteMap.clear();
		for (MetroRoute route : routes) {
			this.mRouteMap.put(route.getId(), route);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o))
			return true;
		if (!(o instanceof MetroCity))
			return false;
		MetroCity route = (MetroCity) o;
		return route.mName.equals(this.mName);
	}

	public String getFilePath() {
		return mFilePath;
	}

	public void setFilePath(String filePath) {
		mFilePath = filePath;
	}

	/**
	 * 根据线路Id获得线路对象
	 * 
	 * @param routeId
	 *          线路Id
	 * @return
	 */
	public MetroRoute getRoute(long routeId) {
		return this.mRouteMap.get(routeId);
	}

	@Override
	public String toString() {
		return "MetroCity{" +
				"mName='" + mName + '\'' +
				", mRoutes=" + mRoutes +
				", mFilePath='" + mFilePath + '\'' +
				", mRouteMap=" + mRouteMap +
				'}';
	}
}

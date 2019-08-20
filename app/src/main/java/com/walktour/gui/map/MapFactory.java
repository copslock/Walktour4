package com.walktour.gui.map;

import com.walktour.framework.database.model.BaseStation;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图工厂类
 * 
 * @author jianchao.wang
 * 
 */
public class MapFactory {
	/** 地图数据 */
	private static SavedMapData mapData = null;
	/** 是否加载基站地图 */
	private static boolean isLoadBase = false;
	/** 是否加载MIF地图 */
	private static boolean isLoadMIF = false;
	/** 是否加载TAB地图 */
	private static boolean isLoadTAB = false;
	/** 是否加载室内地图 */
	private static boolean isLoadIndoor = true;
	/** 基站列表 */
	private static List<BaseStation> baseList = new ArrayList<BaseStation>();

	private MapFactory() {

	}

	public static SavedMapData getMapData() {
		if (mapData == null) {
			mapData = new SavedMapData();
		}
		return mapData;
	}

	public static boolean isLoadBase() {
		return isLoadBase;
	}

	public static void setLoadBase(boolean isBase) {
		isLoadBase = isBase;
	}

	public static boolean isLoadMIF() {
		return isLoadMIF;
	}

	public static void setLoadMIF(boolean isMIF) {
		isLoadMIF = isMIF;
	}

	public static boolean isLoadTAB() {
		return isLoadTAB;
	}

	public static void setLoadTAB(boolean isTAB) {
		isLoadTAB = isTAB;
	}

	public static boolean isLoadIndoor() {
		return isLoadIndoor;
	}

	public static void setLoadIndoor(boolean isIndoor) {
		isLoadIndoor = isIndoor;
	}

	public static List<BaseStation> getBaseList() {
		return baseList;
	}

	public static void setBaseList(List<BaseStation> baseList) {
		MapFactory.baseList = baseList;
	}

}

package com.walktour.model;

import java.util.List;

/**
 * 建筑物模型
 */
public class BuildingModel {
	/** 建筑物名称 */
	private String name;
	/** 建筑物路径 */
	private String dirPath;
	/** 建筑物图片路径 */
	private String buildMapPath;
	/** 建筑物地址 */
	private String buildAddress;
	/** 建筑物下属楼层列表 */
	private List<FloorModel> floorList;

	/**
	 * 建筑模型
	 * 
	 * @param name
	 *          建筑名字
	 * @param dirPath
	 *          建筑路径
	 * @param floors
	 *          建筑的楼层
	 * @param buildMapPath
	 *          建筑的外观图路径
	 * @param buildAddress
	 *          建筑位置信息
	 */
	public BuildingModel(String name, String dirPath, List<FloorModel> floors, String buildMapPath, String buildAddress) {
		this.name = name;
		this.dirPath = dirPath;
		this.buildMapPath = buildMapPath;
		this.floorList = floors;
		this.buildAddress = buildAddress;
	}

	public String getBuildAddress() {
		return buildAddress;
	}

	public String getBuildMapPath() {
		return buildMapPath;
	}

	public void setBuildMapPath(String buildMapPath) {
		this.buildMapPath = buildMapPath;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized List<FloorModel> getFloors() {
		return floorList;
	}

	public synchronized void setFloors(List<FloorModel> floors) {
		this.floorList = floors;
	}

	/**
	 * 返回该建筑物的楼层数
	 */
	public synchronized int getCounts() {
		if (this.floorList != null) {
			return this.floorList.size();
		}
		return 0;
	}

	public synchronized String getDirPath() {
		return dirPath;
	}

}
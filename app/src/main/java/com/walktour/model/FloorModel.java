package com.walktour.model;

import java.util.List;

/**
 * 楼层模型
 */
public class FloorModel {
	/** 日志标识 */
	// private static final String TAG = "FloorModel";
	/** 楼层名字 */
	private String name = "";
	/** 楼层目录路径 */
	private String dirPath = "";
	/** 楼层所属的建筑名字 */
	private String buildName;
	/** 正在测试的地图路径 */
	private String testMapPath = "";
	/** 楼层的所有地图路径 */
	private List<String> allMapPaths;
	/** 楼层所有拍摄的外观图路径 */
	private List<String> outViewMapPaths;
	public String tabfilePath;

	/**
	 * 
	 * @param name
	 *          楼层名字
	 * @param dirPath
	 *          楼层目录
	 * @param allMapPaths
	 *          楼层所有平面图
	 * @param outViewMapPaths
	 *          楼层外观图
	 * @param buildName
	 *          楼层所属的建筑名字
	 */
	public FloorModel(String name, String dirPath, List<String> allMapPaths, List<String> outViewMapPaths,
			String buildName) {
		this.name = name;
		this.dirPath = dirPath;
		this.buildName = buildName;
		this.allMapPaths = allMapPaths;
		this.outViewMapPaths = outViewMapPaths;
	}

	public List<String> getOutViewMapPaths() {
		return outViewMapPaths;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized String getBuildingName() {

		return buildName;
		/*
		 * dirPath.substring( dirPath.indexOf( "/indoor/" )+"/indoor/".length(),
		 * dirPath.length()-name.length()-1 );
		 */
	}

	public synchronized String getDirPath() {
		return dirPath;
	}

	public String getTestMapPath() {
		return testMapPath;
	}

	public void setTestMapPath(String testMapPath) {
		this.testMapPath = testMapPath;
	}

	public List<String> getAllMapPaths() {
		return allMapPaths;
	}

}
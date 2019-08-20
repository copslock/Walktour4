package com.walktour.gui.setting;

import android.content.Context;
import android.os.Environment;

import com.dinglicom.data.model.RecordBuild;
import com.dinglicom.data.model.RecordImg;
import com.walktour.control.config.ConfigIndoor;
import com.walktour.control.instance.FileDB;
import com.walktour.gui.R;
import com.walktour.model.BuildingModel;
import com.walktour.model.FloorModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SysBuildingManager {

	public static final String SYNC_BUILDING = "sync_building"; 
	private static SysBuildingManager instance;
	private static FileDB mFileDB;

	public static synchronized SysBuildingManager getInstance(Context context) {
		if (instance == null) {
			instance = new SysBuildingManager();
			mFileDB = FileDB.getInstance(context);
		}
		return instance;
	}
	
	private SysBuildingManager() {
	}
	
	/**
	 * 把以前旧的楼层结构同步到数据库
	 */
	public void syncDB(Context context) {
		System.out.println("同步旧楼层");
		ConfigIndoor config = ConfigIndoor.getInstance( context);
		List<BuildingModel> buildingList = config.getBuildings(context,false);
		for (BuildingModel buildingModel : buildingList) {
			//增加建筑物
			String buildingName = buildingModel.getName();
//			if (!isExist(buildingName, 0 + "")) {
//				addBuilding(buildingName);
//			}
			if (isExist(buildingName, 0 + "")) {
				continue;
			}
			addBuilding(context,buildingName);
			
			//增加楼层
			String path = buildingModel.getDirPath();
			File file = new File(path);
			List<FloorModel> floorList = config.getFloorList(context,file);
			String build_node_id = mFileDB.getNodeId(buildingName, "0");
			for (FloorModel floorModel : floorList) {
				String floor_name = floorModel.getName();
//				if (!isExist(floor_name, build_node_id)) {
//					addFloor(buildingName, floor_name);
//				}
				if (isExist(floor_name, build_node_id)) {
					continue;
				}
				addFloor(buildingName, floor_name);
				//增加楼层地图
				String floor_node_id = mFileDB.getNodeId(floor_name, build_node_id);
				List<String> mapList = floorModel.getAllMapPaths();
				for (String str_path : mapList) {
					addMap(floor_node_id, str_path);
				}
			}
		}
		System.out.println("同步旧楼层结束");
	}
	
	public List<String> getFolderList(Context context) {
		List<String> result = new ArrayList<String>();
		ConfigIndoor config = ConfigIndoor.getInstance( context);
		List<BuildingModel> buildingList = config.getBuildings(context,false);
		for (BuildingModel buildingModel : buildingList) {
			String path = buildingModel.getDirPath();
			File file = new File(path);
			List<FloorModel> floorList = config.getFloorList(context,file);
			for (FloorModel floorModel : floorList) {
				result.add(floorModel.getDirPath());
			}
		}
		result.add(Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/task");
		System.out.println("file dir size:" + result.size());
		return result;
	}
	
	/**
	 * 获取楼层图列表
	 * @param floor_node_id
	 * @return
	 */
	public ArrayList<RecordImg> buildRecordImgList(String floor_node_id) {
		return mFileDB.buildRecordImgList(floor_node_id);
	}
	
	/**
	 * 增加地图
	 * @param floor_node_id 楼层id
	 * @param filePath 图片路径
	 */
	public void addMap(String floor_node_id, String filePath) {
		String imgPath = filePath.substring(0, filePath.lastIndexOf("/"));
		String imgName = filePath.substring(filePath.lastIndexOf("/") + 1); 
		RecordImg recordImg = new RecordImg();
		recordImg.node_id = floor_node_id;
		recordImg.img_name = imgName;
		recordImg.img_path = imgPath;
		
		mFileDB.syncToTable(recordImg);
	}
	
	/**
	 * 增加建筑物
	 * @param buildingName
	 */
	public void addBuilding(Context context,String buildingName) {
		int node_id = mFileDB.getMaxNodeId(0 + "");
    	if (node_id == 0) {
    		node_id = 1001;
    	} else {
    		node_id++;
    	}
    	RecordBuild recordBuild = new RecordBuild();
    	recordBuild.node_id = "" + node_id;
    	recordBuild.parent_id = "0";
    	recordBuild.node_name = buildingName;
    	recordBuild.node_info = context.getResources().getString(R.string.main_building);
    	mFileDB.syncToTable(recordBuild);
	}
	
	/**
	 * 增加楼层
	 * @param buildingName
	 * @param floor_name
	 */
	public void addFloor(String buildingName, String floor_name) {
		String build_node_id = mFileDB.getNodeId(buildingName, "0");
		int floor_node_id = mFileDB.getMaxNodeId(build_node_id);
		int parent_id = Integer.parseInt(build_node_id);
		String floor_node_idStr = floor_node_id == 0 ? parent_id + "001" : ++floor_node_id + "";
		RecordBuild recordBuild = new RecordBuild();
		recordBuild.node_id = floor_node_idStr;
		recordBuild.parent_id = build_node_id;
		recordBuild.node_name = floor_name;
//		recordBuild.node_info = mContext.getResources().getString(R.string.main_floor);
		recordBuild.node_info = buildingName + "/" + floor_name;
		mFileDB.syncToTable(recordBuild);
	}
	
	/**
	 * 获取node_id
	 * @param name
	 * @param parent_id
	 * @return
	 */
	public String getNodeId(String name, String parent_id) {
		return mFileDB.getNodeId(name, parent_id);
	}
	
	/**
	 * 删除建筑物、楼层、楼层图
	 * @param buildingName
	 */
	public void deleteBuilding(String buildingName) {
		String node_id = mFileDB.getNodeId(buildingName, "0");
		mFileDB.deleteBuild(node_id);
	}
	
	/**
	 * 删除楼层、楼层图
	 * @param floorName
	 */
	public void deleteFloor(String buildingName, String floorName) {
		String build_node_id = mFileDB.getNodeId(buildingName, "0");
		String floor_node_id = mFileDB.getNodeId(floorName, build_node_id);
		mFileDB.deleteFloor(floor_node_id);
	}
	
	/**
	 * 删除楼层图
	 * @param recordImg
	 */
	public void deleteMap(RecordImg recordImg) {
		mFileDB.deleteRecordImg(recordImg);
	}
	
	/**
	 * 建筑物或者楼层是否存在
	 * @param node_name
	 * @param parent_id
	 * @return
	 */
	public boolean isExist(String node_name, String parent_id) {
		return mFileDB.isExist(node_name, parent_id);
	}
}

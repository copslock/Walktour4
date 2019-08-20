package com.walktour.gui.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.dinglicom.data.control.BuildWhere;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.data.model.RecordAbnormal;
import com.dinglicom.data.model.RecordBuild;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordDetailUpload;
import com.dinglicom.data.model.RecordTaskType;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ServerManager;
import com.walktour.control.instance.FileDB;
import com.walktour.gui.R;
import com.walktour.gui.data.ContentFragmentFactory;
import com.walktour.gui.data.FragmentBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

@SuppressLint("DefaultLocale")
public class DBManager {
	
	private final String TAG = "DataManager";
	public String language = "";//语音环境
	private static DBManager mDbManager = null;
	public static List<TestRecord> mTestRecords = new ArrayList<TestRecord>();
	private ArrayList<TestRecord> multiTestRecor = null;
	private Context mContext;
	private FileDB mFileDB;
	private ServerManager mServer;
	private  String server_setting = "";
	private  String allow_file_types = "";
	private boolean isUploadServerSet = true;
	private int invalidTimeLong = 0;
	private Handler mHandler;
	private ApplicationModel mApplicationModel;
	
	public static DBManager getInstance(Context context) {
		if (mDbManager == null) {
			mDbManager = new DBManager(context);
		}
		return mDbManager;
	}
	
	private DBManager(Context context) {
		mContext = context;
		mFileDB = FileDB.getInstance(mContext);
		mServer = ServerManager.getInstance(mContext);
		language = Locale.getDefault().getLanguage();
		mApplicationModel = ApplicationModel.getInstance();
	}
	
	private  void init() {
		isUploadServerSet = mServer.hasUploadServerSet();
		server_setting = getServerStr();
		allow_file_types = getAllowFileTypes();
	}
	
	/**
	 *  设置过滤时长
	 * @param timeLong 毫秒
	 */
	public void setInvalidTimeLong(int timeLong) {
		this.invalidTimeLong = timeLong;
	}
	
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	/**
	 * 查询加载数据
	 * @param map
	 */
	public void loadData(HashMap<String, ArrayList<String>> map) {
		init();
		List<TestRecord> list = mFileDB.buildTestRecordList(map);
		List<TestRecord> tmpList = new ArrayList<TestRecord>();
		if (list != null) {
			LogUtil.d(TAG, "testRecord数据大小:" + list.size());
			tmpList.clear();
			if (invalidTimeLong != 0) {
				for (TestRecord testRecord : list) {
					long duration = testRecord.time_end - testRecord.time_create;
					if (duration > invalidTimeLong) {
						tmpList.add(testRecord);
					}else if(testRecord.file_split_id>=1){//存在分组的文件文件,此时就要检测第一个文件到最后一个文件的时长
						TestRecord firstRecord=null;
						TestRecord lastRecord=null;
						for (TestRecord testRecordx : list) {
							if(testRecord.task_no.equals(testRecordx.task_no)) {//同一个task_no进行比较
								if (testRecordx.file_split_id == 1) {
									firstRecord = testRecordx;
								} else if (testRecordx.file_split_id > 1) {
									if (null == lastRecord) {
										lastRecord = testRecordx;
									} else {
										if (testRecordx.file_split_id > lastRecord.file_split_id) {
											lastRecord = testRecordx;
										}
									}
								}
							}
						}
						if(null!=firstRecord&&null!=lastRecord) {
							long durationx = lastRecord.time_end - firstRecord.time_create;
							if (durationx > invalidTimeLong) {
								tmpList.add(testRecord);
							}
						}
						firstRecord=null;
						lastRecord=null;
					}
				}
			} else {
				tmpList.addAll(list);
			}
			
			Collections.sort(tmpList, new Comparator<TestRecord>() {
				@Override
				public int compare(TestRecord lhs, TestRecord rhs) {
					return new Date(lhs.time_create).compareTo(new Date(rhs.time_create));
				}
			});
			
			mTestRecords.clear();
			for (int i =  tmpList.size() - 1; i >= 0; i--) {
				mTestRecords.add(tmpList.get(i));
			}
		}
	}
	
	/**
	 * 获得IPACK测试任务列表
	 * @return
	 */
	public ArrayList<TestRecord> getMultiRecordList(){
//		if(multiTestRecor == null){
			BuildWhere wheres = new BuildWhere();
			wheres.addWhere(TestRecord.class.getSimpleName(), "%s." + TestRecordEnum.type_scene.name() 
					+ " = " + SceneType.MultiTest.getSceneTypeId());
			multiTestRecor = mFileDB.buildTestRecordList(wheres.getWhere());
//		}
		
		return multiTestRecor;
	}
	
	/**
	 * 获取异常信息
	 * @return
	 */
	public List<ExceptionModel> getAbnormalList() {
		List<ExceptionModel> result = new ArrayList<ExceptionModel>();
		ArrayList<RecordAbnormal> abnormalList = mFileDB.getRecordAbnormals();
		LogUtil.d(TAG, "testRecord异常数据大小:" + abnormalList.size());
		for (RecordAbnormal rb : abnormalList) {
			if (rb.getAbnormal_type_str() != null && !rb.getAbnormal_type_str().trim().equals("")) {
				ExceptionModel item = new ExceptionModel();
				item.typeKey  = rb.abnormal_type;
				item.name = rb.getAbnormal_type_str();
				result.add(item);
			}
		}
		return result;
	}
	
	/**
	 * 获取测试业务列表
	 * @return
	 */
	public List<BusinessModel> getBusinessList() {
		List<BusinessModel> result = new ArrayList<BusinessModel>();
		List<RecordTaskType> taskTypeList = mFileDB.getRecordTaskTypes();
		for (RecordTaskType rtt : taskTypeList) {
			BusinessModel item = new BusinessModel();
			item.typeKey = rtt.task_type;
			item.name = rtt.getTask_type_str();
			result.add(item);
		}
		return result;
	}
	
	/**
	 * 获取有权限的场景id
	 * @return
	 */
	private ArrayList<Integer> getSceneTypesToShow() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(TestType.DT.getTestTypeId());
		result.add(TestType.CQT.getTestTypeId());
		if (mApplicationModel.isAnHuiTest()) {
			result.add(SceneType.Anhui.getSceneTypeId());
		}
		if (mApplicationModel.isHuaWeiTest()) {
			result.add(SceneType.Huawei.getSceneTypeId());
		}
		if (mApplicationModel.isBtu()) {
			result.add(SceneType.BTU.getSceneTypeId());
		}
		if (mApplicationModel.isFuJianTest()) {
			result.add(SceneType.Fujian.getSceneTypeId());
		}
//		if (mApplicationModel.isSingleStationTest()) {
//			result.add(SceneType.SingleSite.getSceneTypeId());
//		}
		if (mApplicationModel.isAtu()) {
			result.add(SceneType.ATU.getSceneTypeId());
		}
		return result;
	}

	/**
	 * 获取数据管理要显示的内容（如：DT\CQT\安徽工单\华为工单等）
	 * @return
	 */
	public List<Fragment> getContentFragment(SceneType sceneType){
		List<Fragment> result = new ArrayList<Fragment>();
		List<ContentModel> list = getContentModels(sceneType.getSceneTypeId());
		for (int i = 0; i < list.size(); i++) {
			result.add(ContentFragmentFactory.newIntance(list.get(i).getKey()));
		}
		return result;
	}
	
	public  List<ContentModel> getContentModels(int sceneTypeID) {
		List<ContentModel> list = new ArrayList<ContentModel>();
		ArrayList<Integer> sceneTypeIds = getSceneTypesToShow();
		if(sceneTypeID==SceneType.HighSpeedRail.getSceneTypeId()){
			sceneTypeIds.clear();
			sceneTypeIds.add(SceneType.HighSpeedRail.getSceneTypeId());
		}else if(sceneTypeID==SceneType.Metro.getSceneTypeId()){
			sceneTypeIds.clear();
			sceneTypeIds.add(SceneType.Metro.getSceneTypeId());
		}else if(sceneTypeID==SceneType.SingleSite.getSceneTypeId()){//单站验证
			sceneTypeIds.clear();
			sceneTypeIds.add(SceneType.SingleSite.getSceneTypeId());
		}
		for (Integer typeId : sceneTypeIds) {
			ContentModel item = new ContentModel();
			item.setKey(typeId);
			item.setName(getSceneTypeName(typeId));
			list.add(item);
		}
		return list;
	}
	
	/**
	 * 获取要显示内容的标题
	 * @return
	 */
	public List<String> getContentTabNames(SceneType sceneType) {
		List<String> result = new ArrayList<String>();
		List<ContentModel> list = getContentModels(sceneType.getSceneTypeId());
		for (int i = 0; i < list.size(); i++) {
			result.add(list.get(i).getName());
		}
		return result;
	}
	
	private String getSceneTypeName(int sceneTypeId) {
		String name = "";
		if (sceneTypeId == TestType.DT.getTestTypeId()) {
			name = getString(R.string.str_scene_type_name_dt);
		} else if (sceneTypeId == TestType.CQT.getTestTypeId()) {
			name = getString(R.string.str_scene_type_name_cqt);
		} else if (sceneTypeId == SceneType.Anhui.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_anhui);
		} else if (sceneTypeId == SceneType.Huawei.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_huawei);
		} else if (sceneTypeId == SceneType.BTU.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_btu);
		} else if (sceneTypeId == SceneType.Fujian.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_fujian);
		} else if (sceneTypeId == SceneType.SingleSite.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_danzhan);
		} else if (sceneTypeId == SceneType.MultiATU.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_multi_atu);
		} else if (sceneTypeId == SceneType.ATU.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_atu);
		} else if (sceneTypeId == SceneType.Metro.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_metro);
		}else if (sceneTypeId == SceneType.HighSpeedRail.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_highspeedrail);
		}else if (sceneTypeId == SceneType.SingleSite.getSceneTypeId()) {
			name = getString(R.string.str_scene_type_name_singlestation);
		}
		return name;
	}
	
	private String getString(int stringId) {
		return mContext.getResources().getString(stringId);
	}
	
	/***
	 * 按测试场景获取所有数据
	 * @param testSene 测试场景
	 * @return
	 */
	public ArrayList<DataModel> getAllFiles(SceneType testSene) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> testRecordSqlArray = new ArrayList<String>();
		String test_sence_value = "%s.type_scene ='" + testSene.getSceneTypeId() + "'";
		testRecordSqlArray.add(test_sence_value);
		map.put(TestRecord.class.getSimpleName(), testRecordSqlArray);
		return generateFileTreeNormal(map);
	}
	
	/**
	 * 获取手动测试所有数据 
	 * @param type 测试类型
	 * @return
	 */
	public ArrayList<DataModel> getAllFiles(TestType type) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> testRecordSqlArray = new ArrayList<String>();
		String test_sence_value = "%s.type_scene ='" + 1001 + "'";
		testRecordSqlArray.add(test_sence_value);
		String testTypeSql = "%s.test_type ='" + type.getTestTypeId() + "'";
		testRecordSqlArray.add(testTypeSql);
		map.put(TestRecord.class.getSimpleName(), testRecordSqlArray);
		return generateFileTreeNormal(map);
	}
	
	/**
	 * 获取数据
	 * @param map 过滤条件
	 * @return
	 */
	public ArrayList<DataModel> getFiles(boolean isWorkOrder, HashMap<String, ArrayList<String>> map) {
		return isWorkOrder ? generateFileTreeWorkOrder(map) : generateFileTreeNormal(map);
	}
	
	/**
	 * 查询数据 以一般模式显示
	 * @param map
	 * @return
	 */
	private ArrayList<DataModel> generateFileTreeNormal(HashMap<String, ArrayList<String>> map) {
		ArrayList<DataModel> result = new ArrayList<DataModel>();
		List<String> displayNames = new ArrayList<String>();
		List<String> dates = new ArrayList<String>();
		DateUtil.getCurrentWeekTodayPreviousDays(mContext, displayNames, dates);
		displayNames.add(mContext.getResources().getString(R.string.str_per_week));
		dates.add("week");
		displayNames.add(mContext.getResources().getString(R.string.str_other_time));
		dates.add("other");
		for (int i = 0; i < displayNames.size(); i++) {
			DataModel item = new DataModel();
			item.firstLevelTitle = displayNames.get(i);
			item.date = dates.get(i);
			result.add(item);
		}
		loadData(map);
		

		List<TestRecord> datas = new ArrayList<TestRecord>();
		datas.addAll(mTestRecords);
		List<DataModel> dataModelList = new ArrayList<DataModel>();
		for (int i = 0; i < datas.size(); i++) {
			TestRecord item = datas.get(i);
			DataModel dataModel = new DataModel();
			ArrayList<RecordTaskType> recordTaskTypes = item.getRecordTaskTypes();
			String taskName = "";
			if (recordTaskTypes == null || recordTaskTypes.size() == 0) {
				taskName = item.file_name;
			} else {
				for (int j = 0; j < recordTaskTypes.size(); j++) {
					taskName = taskName + recordTaskTypes.get(j).getTask_type_str() + ",";
				}
				if (taskName.contains(",")) {
					taskName = taskName.substring(0, taskName.lastIndexOf(","));
				}
			}
			dataModel.setTaskName(taskName);
			dataModel.setStartTime(DateUtil.Y_M_D.format(new Date(item.time_create)));
			dataModel.setCreateTime(item.time_create);
			dataModel.setEndTime(item.time_end);
			dataModel.setDuration(DateUtil.getTimeLengthString(mContext, item.time_create, item.time_end));
			dataModel.setExceptionCount(item.getRecordAbnormals().size());
			dataModel.setGo(item.go_or_nogo == 1 ? true : false);
			if (item.file_split_id <= 1 && item.getTaskNoNum() == 1) {//文件
				dataModel.testRecord = item;
				dataModelList.add(dataModel);
			} else {
				if (dataModelList.size() == 0 || !dataModelList.get(dataModelList.size() - 1).isFolder || !dataModelList.get(dataModelList.size() - 1).task_no.equals(item.task_no)) {//需要建文件夹
					DataModel folder = dataModel.clone();
					folder.isFolder = true;
					folder.task_no = item.task_no;
					dataModelList.add(folder);
					dataModel.testRecord = item;
					dataModelList.get(dataModelList.size() - 1).getChild().add(dataModel);
				} else {//文件夹存在，直接添加到文件中
					dataModel.testRecord = item;
					dataModelList.get(dataModelList.size() - 1).getChild().add(dataModel);
				}
			}
			setUploadState(dataModel);
		}
		//处理文件夹
		for (int i = 0; i < dataModelList.size(); i++) {
			DataModel dataModel = dataModelList.get(i);
			if (dataModel.isFolder) {
				if (hasTaskName(dataModel.getChild())) {
					dataModel.setTaskName(getFolderName(dataModel.getChild()));
				} else {
					String name = dataModel.getChild().get(0).testRecord.file_name;
					if (name.contains("(")) {
						name = name.substring(0, name.lastIndexOf("("));
					}
					dataModel.setTaskName(name);
				}
				dataModel.setCreateTime(getFolderCreateTime(dataModel.getChild()));
				dataModel.setDuration(getFolderDuration(dataModel.getChild()));
				for (int j = 0; j < dataModel.getChild().size(); j++) {
					DataModel childDataModel = dataModel.getChild().get(j);
					childDataModel.setTaskName(childDataModel.testRecord.file_name);
				}
			}
		}
		//分组
		for (int i = 0; i < dataModelList.size(); i++) {
			DataModel aDataModel = dataModelList.get(i);
			for (int j = 0; j < result.size(); j++) {
				DataModel aFileTree = result.get(j);
				if (aFileTree.date.equals(aDataModel.getStartTime())) {
					aFileTree.getChild().add(aDataModel);
					break;
				} else if (aFileTree.date.equals("week")) {
					if (contain(aDataModel.getStartTime())) {
						aFileTree.getChild().add(aDataModel);
						break;
					}
				} else if (aFileTree.date.equals("other")){
					aFileTree.getChild().add(aDataModel);
					break;
				}
			}
		}
		List<DataModel> tmp = new ArrayList<DataModel>(result);
		result.clear();
		for (int i = 0; i < tmp.size(); i++) {
			if (tmp.get(i).getChild().size() != 0) {
				result.add(tmp.get(i));
			}
		}
		
		return result;
	}
	
	private List<String> perviousWeekDates = DateUtil.getPerviousWeekDays();
	private boolean contain(String date) {
		for (int i = 0; i < perviousWeekDates.size(); i++) {
			if (perviousWeekDates.get(i).equals(date)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 查询数据 以工单形式显示
	 * @param map
	 * @return
	 */
	private ArrayList<DataModel> generateFileTreeWorkOrder(HashMap<String, ArrayList<String>> map) {
		ArrayList<DataModel> result = new ArrayList<DataModel>();
		loadData(map);
		//分组
		HashSet<String> groupSet = new HashSet<String>();
		for (TestRecord testRecord : mTestRecords) {
			String parent_node_id = testRecord.node_id.substring(0, 4);
			groupSet.add(parent_node_id);
		}
		for (String s : groupSet) {
			DataModel fileTree = new DataModel();
			fileTree.node_id = s;
			fileTree.firstLevelTitle = mFileDB.getNodeName(s);
			result.add(fileTree);
		}
		//根据分组插入数据
		for (DataModel fileTree : result) {
			for (TestRecord testRecord : mTestRecords) {
				if (testRecord.node_id.contains(fileTree.node_id)) {
					TestRecord item = testRecord;
					DataModel dataModel = new DataModel();
					ArrayList<RecordTaskType> recordTaskTypes = item.getRecordTaskTypes();
					String taskName = "";
					if (recordTaskTypes == null || recordTaskTypes.size() == 0) {
						taskName = item.file_name;
					} else {
						for (int j = 0; j < recordTaskTypes.size(); j++) {
							taskName = taskName + recordTaskTypes.get(j).getTask_type_str() + ",";
						}
						if (taskName.contains(",")) {
							taskName = taskName.substring(0, taskName.lastIndexOf(","));
						}
					}
					dataModel.setTaskName(taskName);
					dataModel.setStartTime(DateUtil.Y_M_D.format(new Date(item.time_create)));
					dataModel.setCreateTime(item.time_create);
					dataModel.setEndTime(item.time_end);
					dataModel.setDuration(DateUtil.getTimeLengthString(mContext, item.time_create, item.time_end));
					dataModel.setExceptionCount(item.getRecordAbnormals().size());
					dataModel.setGo(dataModel.getExceptionCount() == 0 ? true : false);
					//---------------------------------------------------------------------
					if (testRecord.file_split_id <= 1 && testRecord.getTaskNoNum() == 1) {//文件
						dataModel.testRecord = item;
						fileTree.getChild().add(dataModel);
					} else {//文件夹
						if (fileTree.getChild().size() == 0 || !fileTree.getChild().get(fileTree.getChild().size() - 1).isFolder || !fileTree.getChild().get(fileTree.getChild().size() - 1).task_no.equals(item.task_no)) {//需要建文件夹
							DataModel folder = dataModel.clone();
							folder.isFolder = true;
							folder.task_no = item.task_no;
							fileTree.getChild().add(folder);
							dataModel.testRecord = item;
							fileTree.getChild().get(fileTree.getChild().size() - 1).getChild().add(dataModel);
						} else {//文件夹存在，直接添加到文件中
							dataModel.testRecord = item;
							fileTree.getChild().get(fileTree.getChild().size() - 1).getChild().add(dataModel);
						}
					}
					setUploadState(dataModel);
				
				}
			}
		}
		//处理文件夹
		for (DataModel level1 : result) {
			for (DataModel dataModel : level1.getChild()) {
				if (dataModel.isFolder) {
					if (hasTaskName(dataModel.getChild())) {
						dataModel.setTaskName(getFolderName(dataModel.getChild()));
					} else {
						String name = dataModel.getChild().get(0).testRecord.file_name;
						if (name.contains("(")) {
							name = name.substring(0, name.lastIndexOf("("));
						}
						dataModel.setTaskName(name);
					}
					dataModel.setCreateTime(getFolderCreateTime(dataModel.getChild()));
					dataModel.setDuration(getFolderDuration(dataModel.getChild()));
					for (int j = 0; j < dataModel.getChild().size(); j++) {
						DataModel childDataModel = dataModel.getChild().get(j);
						childDataModel.setTaskName(childDataModel.testRecord.file_name);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 文件夹下所有分文件的task（只要有一个分文件的task为空，则返回false）
	 * @param list
	 * @return
	 */
	private boolean hasTaskName(List<DataModel> list) {
		for (int i = 0; i < list.size(); i++) {
			ArrayList<RecordTaskType> tasks = list.get(i).testRecord.getRecordTaskTypes();
			if (tasks == null || tasks.size() == 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 文件夹显示名
	 * @param list
	 * @return
	 */
	private String getFolderName(List<DataModel> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (!result.contains(list.get(i).getTaskName().trim())) {
				result += list.get(i).getTaskName().trim() + ",";
			}
		}
		if (result.contains(",")) {
			result = result.substring(0, result.lastIndexOf(","));
		}
		return result;
	}
	
	/**
	 * 文件夹创建时间
	 * @param list
	 * @return
	 */
	private long getFolderCreateTime(List<DataModel> list) {
		return list.get(list.size() - 1).testRecord.time_create;
	}
	
	/**
	 * 文件夹时长
	 * @param list
	 * @return
	 */
	private String getFolderDuration(List<DataModel> list) {
		long createTime = list.get(list.size() - 1).testRecord.time_create;
		long endTime = list.get(0).testRecord.time_end;
		return DateUtil.getTimeLengthString(mContext, createTime, endTime);
	}
	
	/**
	 * 获取建筑物、工单等列表
	 * @param type_sence
	 * @return
	 */
	public ArrayList<RecordBuild> getRecordBuilds(String type_sence) {
		return mFileDB.getRecordBuilds(getTypeScene(type_sence));
	}
	

	/***
	 * 获取对应场景ID下的做过测试的所有城市，返回的列表中包含重复的城市
	 * @param type_sence
	 * @return
	 */
	public List<RecordTestInfo> getRecordTestInfoCitys(String type_sence) {
		return mFileDB.getRecordTestInfoCitys(getTypeScene(type_sence));
	}
	
	/***
	 * 获取所有的线路信息
	 * @param taskNos 任务IDS
	 * @return
	 */
	public List<RecordTestInfo> getRecordTestInfoLines(List<String> taskNos,String keyInfo) {
		return mFileDB.getRecordTestInfoLines(taskNos,keyInfo);
	}
	
	/***
	 * 高铁模式下获取对应场景ID下的做过测试的所有线路，去除相同的线路
	 * @param type_sence
	 * @return
	 */
	public ArrayList<RecordTestInfo> getRecordTestInfoLines(int type_sence) {
		return mFileDB.getRecordTestInfoLines(type_sence);
	}

	/***
	 * 单站验证模式下获取对应场景ID下的做过测试的所有基站，去除相同的基站
	 * @param type_sence
	 * @return
	 */
	public ArrayList<RecordTestInfo> getRecordTestInfoStations(int type_sence) {
		return mFileDB.getRecordTestInfoStations(type_sence);
	}
	public void updateFile(DataModel dataModel) {

			mFileDB.syncTestRecord(dataModel.testRecord);
	}

	/**
	 * 删除文件数据库记录
	 * @param dataModel
	 */
	public void deleteFile(DataModel dataModel) {
		if (dataModel.testRecord != null) {
			deleteFile(dataModel.testRecord);
		}
	}
	
	/**
	 * 删除指定测试记录文件
	 * @param testRecord
	 */
	public void deleteFile(TestRecord testRecord){
		mFileDB.deleteFormTable(testRecord);
	}
	
	public void deleteFiles(ArrayList<DataModel> dataModels) {
		for (DataModel dataModel : dataModels) {
			deleteFile(dataModel);
		}
		sendEmptyMessage(FragmentBase.MSG_UPDATE_UI, 1000);
	}
	
	public long syncTestRecord(TestRecord testRecord) {
		sendEmptyMessage(FragmentBase.MSG_UPDATE_UI, 1000);
		return mFileDB.syncTestRecord(testRecord);
	}
	
	/**
	 * 重命名
	 * @param testRecord
	 * @param name
	 */
	public void modifyName(TestRecord testRecord, String name) {
		testRecord.file_name = name;
		for (RecordDetail recordDetail : testRecord.getRecordDetails()) {
			recordDetail.file_name = name + "." + recordDetail.getFile_type_str();
		}
		mFileDB.syncTestRecord(testRecord);
		sendEmptyMessage(FragmentBase.MSG_UPDATE_UI, 500);
	}
	
	/**
	 * 获取最大文件分割序号
	 * @param task_no
	 * @return
	 */
	public int getMaxFileSplitId(String task_no) {
		return mFileDB.getMaxFileSplitId(task_no);
	}
	
	/**
	 * 通知刷新界面
	 * @param action
	 * @param delay
	 */
	private void sendEmptyMessage(int action, int delay) {
		if (this.mHandler == null) return;
		this.mHandler.sendEmptyMessageDelayed(action, delay);
	}
	
	public int getTypeScene(String flag) {
		int result = 0;
		if (flag.equals(TestType.DT.name())  || flag.equals(TestType.CQT.name())) {
			result = SceneType.Manual.getSceneTypeId();
		} else if (flag.equals(SceneType.Auto.name())) {
			result = SceneType.Auto.getSceneTypeId();
		} else if (flag.equals(SceneType.MultiTest.name())) {
			result = SceneType.MultiTest.getSceneTypeId();
		} else if (flag.equals(SceneType.BTU.name())) {
			result = SceneType.BTU.getSceneTypeId();
		} else if (flag.equals(SceneType.Anhui.name())) {
			result = SceneType.Anhui.getSceneTypeId();
		} else if (flag.equals(SceneType.Huawei.name())) {
			result = SceneType.Huawei.getSceneTypeId();
		} else if (flag.equals(SceneType.Fujian.name())) {
			result = SceneType.Fujian.getSceneTypeId();
		} else if (flag.equals(SceneType.SingleSite.name())) {
			result = SceneType.SingleSite.getSceneTypeId();
		} else if (flag.equals(SceneType.MultiATU.name())) {
			result = SceneType.MultiATU.getSceneTypeId();
		} else if (flag.equals(SceneType.ATU.name())) {
			result = SceneType.ATU.getSceneTypeId();
		}else if (flag.equals(SceneType.HighSpeedRail.name())) {
			result = SceneType.HighSpeedRail.getSceneTypeId();
		}else if (flag.equals(SceneType.Metro.name())) {
			result = SceneType.Metro.getSceneTypeId();
		}else if (flag.equals(SceneType.SingleSite.name())) {
			result = SceneType.SingleSite.getSceneTypeId();
		}
		return result;
	}
	
	public int getTestTyp(String flag) {
		int result = 5001;
		if (flag.equals(TestType.DT.name())) {
			result = 5001;
		} else if (flag.equals(TestType.CQT.name())) {
			result = 5002;
		} 
		return result;
	}

	/**
	 * 根据设定的上传状态,返回状态对应值
	 * -1:未上传
	 * -2:上传失败
	 * 0:等待上传
	 * 100:上传成功
	 * 其它:上传比例
	 * @param state
	 * @return
	 */
	public String getUploadStateStr(int state) {
		String result = "";
		if (state == -1) {
			result = mContext.getResources().getString(R.string.str_unuploaded);
		} else if (state == -2) {
			result = mContext.getResources().getString(R.string.str_unuploaderror);
		} else if (state == 0){//
			result = mContext.getResources().getString(R.string.str_waitingUpload);
		} else if (state == 100) {
			result = mContext.getResources().getString(R.string.str_uploaded);
		} else {
			result = state + "%";
		}
		
		return result;
	}
	
	/**
	 * 获得当前文个的上传状态
	 * @param uploadList
	 * @return
	 */
	public int getUploadState(ArrayList<RecordDetailUpload> uploadList){
		//当前设置的服务器
		int state = -1;
		for(RecordDetailUpload uDetail : uploadList){
			if(uDetail.server_info.equals(server_setting)){
				state = uDetail.upload_type;
				break;
			}
		}
		
		return state;
	}
	
	/**
	 * 获得当前文件的当前服务器上传状态
	 * 根据当前明细文件的上传信息
	 * 比对当前设置的服务器中是否有上传的信息
	 * @author Tangwq
	 * @param uploadList
	 * @return
	 */
	public String getUploadStateStr(ArrayList<RecordDetailUpload> uploadList){
		return getUploadStateStr(getUploadState(uploadList));
	}
	
	private void setUploadState(DataModel dataModel) {
		dataModel.setState(-1);
		if (dataModel.isFirstLevel || dataModel.isFolder) {
			dataModel.setState(-1);
			return;
		} else {
			if (!isUploadServerSet) {
				dataModel.setState(-1);
				return;
			} else {
				String uploadTypes = "";
				for (RecordDetail recordDetail : dataModel.testRecord.getRecordDetails()) {
					try{
						FileType fType = FileType.getFileType(recordDetail.file_type);
						if (allow_file_types.contains(fType.getFileTypeName().toLowerCase())) {
							ArrayList<RecordDetailUpload> uploadDteails = recordDetail.getDetailUploads();
							
							if (uploadDteails == null || uploadDteails.size() == 0) {
								dataModel.setState(-1);
								return;
							} else {
								for (RecordDetailUpload recordDetailUpload : uploadDteails) {
									if (recordDetailUpload.server_info.equals(server_setting)) {
										uploadTypes += recordDetailUpload.upload_type + ",";
									}
								}
							
							}
						}
					}catch(Exception e){
						LogUtil.w(TAG, "setUploadState",e);
					}
				}
				//TODO
				System.out.println("uploadTypes:" + uploadTypes);
				if (uploadTypes.equals("")) {
					dataModel.setState(-1);
					return;
				}
				if (uploadTypes.contains("-2")) {
					dataModel.setState(-2);
					return;
				}
				String[] tmps = uploadTypes.split(",");
				for (String string : tmps) {
					if (!string.equals("")) {
						if(Integer.parseInt(string) == 100){
							dataModel.setState(100);
							break;
						}else{
							dataModel.setState(Integer.parseInt(string));
						}
					}
				}
			}
		}
	}
	
	/**
	 * 允许的文件上传格式
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getAllowFileTypes() {
		String result = "";
		Set<Entry<String, Boolean>> set = mServer.getUploadFileTypes(mContext).entrySet();
		Iterator<?> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Boolean> entry = (Entry<String, Boolean>) iterator.next();
			if (entry.getValue()) {
				result += entry.getKey() + ",";
			}
		}
		return result;
	}
	
	/**
	 * 当前设置的服务器ip
	 * @return
	 */
	public String getServerStr() {
		String result = "";
		int serverType = mServer.getUploadServer();
		if (serverType == ServerManager.SERVER_FLEET) {
			result = mServer.getUploadFleetIp() + "_" + mServer.getUploadFleetPort();
		} else if (serverType == ServerManager.SERVER_HTTPS) {
			result = mServer.getHttpsUrl();
		} else if (serverType == ServerManager.SERVER_FTP) {
			String ftpName = mServer.getFtpName();
			ConfigFtp configFtp = new ConfigFtp();
			if( configFtp.contains(ftpName) ){
				String ip = configFtp.getFtpIp( ftpName);
				String port = configFtp.getFtpPort( ftpName );
				result = ip + "_" + port;
			}
		} else if (serverType == ServerManager.SERVER_BTU  || serverType == ServerManager.SERVER_ATU) {
			result = mServer.getDTLogIp() + "_" + mServer.getDTLogPort();
		}
		System.out.println("当前服务器:" + result);
		return result;
	}
	
	/**
	 * 根据记录ID获取测试记录对象
	 * 
	 * @param recordId
	 *          记录ID
	 * @return
	 */
	public TestRecord getTestRecord(String recordId) {
		/* 1,根据recodrodId TestRecord对象 */
		BuildWhere wheres = new BuildWhere();
		wheres.addWhere(TestRecord.class.getSimpleName(),
				"%s." + TestRecordEnum.record_id.name() + " = '" + recordId + "'");

		ArrayList<TestRecord> records = mFileDB.buildTestRecordList(wheres.getWhere());
		if (records.size() >= 1) {
			return records.get(0);
		}
		return null;
	}
	
	/**
	 * 保存记录的文件路径和文件名称
	 * 
	 * @param recordId
	 *          记录ID
	 * @param fileType
	 *          文件类型
	 * @param filePath
	 *          文件路径
	 * @param fileName
	 *          文件名称
	 */
	public void saveRecordFilePath(String recordId, FileType fileType, String filePath, String fileName) {
		TestRecord testRecord = this.getTestRecord(recordId);
		/*2,根据filetype取得需要修改状态的明细文件类型*/
		if(testRecord != null){
			for(RecordDetail detail : testRecord.getRecordDetails()){
				if(detail.file_type == fileType.getFileTypeId()){
					detail.file_path = filePath;
					detail.file_name = fileName;
					break;
				}
			}
			//将修改过状态的对象同步到数据库中
			mFileDB.syncTestRecord(testRecord);
		}

	}
	/**
	 * 
	 * @param recordId	TestRecord主键ID
	 * @param fileType		当前上传文件类型
	 * @param state			上传状态:'-1表示未上传，100表示上传完成，-2表示上传失败；0，待上传；
	 * @param serverInfo	上传服务器信息,该字段以:“IP_Port”存储
	 */
	public void uploadStateChange(String recordId,FileType fileType,int state,String serverInfo){
		TestRecord testRecord = this.getTestRecord(recordId);
		/*2,根据filetype取得需要修改状态的明细文件类型*/
		if(testRecord != null){
			for(RecordDetail detail : testRecord.getRecordDetails()){
				if(detail.file_type == fileType.getFileTypeId()){
					/*3,修改状态,根据serverName决定当前记录是新增,还是修改状态*/
					boolean serverExist = false;
					for(RecordDetailUpload upload : detail.getDetailUploads()){
						if(upload.server_info.equals(serverInfo)){
							upload.upload_type = state;
							serverExist = true;
							break;
						}
					}
					//如果上传服务器不存在,则新增一服务记录
					if(!serverExist){
						RecordDetailUpload newUplad = new RecordDetailUpload();
						newUplad.detail_id = detail.detail_id;
						newUplad.server_info = serverInfo;
						newUplad.upload_type = state;
						
						detail.addDetailUpload(newUplad);
					}
					break;
				}
			}
			
			//将修改过状态的对象同步到数据库中
			mFileDB.syncTestRecord(testRecord);
		}
		
	}
}

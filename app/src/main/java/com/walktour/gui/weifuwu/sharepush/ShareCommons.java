package com.walktour.gui.weifuwu.sharepush;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dinglicom.data.model.RecordAbnormal;
import com.dinglicom.data.model.RecordBase;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordNetType;
import com.dinglicom.data.model.RecordTaskType;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.gui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
/***
 * 存储记录常量数据
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("SdCardPath")
public final class ShareCommons {
	/**
	 * 私有构造器，防止外部构造
	 */
	private ShareCommons() {
		super();
	}
	/**保存中间变量:服务器返回的device_code**/
	public static String device_code="";
	/**保存中间变量:服务器返回的session_id**/
	public static String session_id="";
	/**保存中间变量:服务器返回的token_id**/
	public static String token_id="";
	/** 从服务器查询数据的条数 **/
	public static final int SEARCH_PAGE_SIZE = 10;
	/** 分享过的历史记录数 **/
	public static final int SHARE_HISTORY_SIZE = 5;
	/** 分享的key */
	public static final String SHARE_FROM_KEY = "share_key";
	/** 数据管理是否分享数据的key,默认是false */
	public static final String SHARE_DATA_KEY = "share_data_key";
	/** 共享文件在SD卡目录 **/
	public static final String SHARE_PATH_BASE = "sharepush";
	public static final String SHARE_PATH_PROJECT = "projects";
	public static final String SHARE_PATH_TASKGROUP = "groups";
	public static final String SHARE_PATH_TASK = "tasks";
	public static final String SHARE_PATH_CQT = "cqt";
	public static final String SHARE_PATH_CQT_PIC = "cqtpic";
	public static final String SHARE_PATH_STATION = "station";
	public static final String SHARE_PATH_SCREENSHOT_PIC = "screenshots";
	public static final String SHARE_PATH_DATA = "data";
	public static final String SHARE_PATH_REPORT = "reports";
	/** 共享文件的value,注意顺序编号，不能有重复值 **/
	public static final int SHARE_FROM_PROJECT = 1;
	public static final int SHARE_FROM_TASKGROUP = 2;
	public static final int SHARE_FROM_TASK = 3;
	public static final int SHARE_FROM_CQT = 4;
	public static final int SHARE_FROM_CQT_PIC = 5;
	public static final int SHARE_FROM_STATION = 6;
	public static final int SHARE_FROM_SCREENSHOT_PIC = 7;
	public static final int SHARE_FROM_DATA = 8;
	public static final int SHARE_FROM_REPORT = 9;
	// 历史数据再次分享
	public static final int SHARE_FROM_SHARE = 10;
	// 接收到共享文件后广播
	// 接收到工程文件的推送
	public static final String SHARE_ACTION_1 = "com.walktour.gui.weifuwu.sharepush.share.project";
	// 接收到任务组的推送
	public static final String SHARE_ACTION_2 = "com.walktour.gui.weifuwu.sharepush.share.group";
	// 接收到任务的推送
	public static final String SHARE_ACTION_3 = "com.walktour.gui.weifuwu.sharepush.share.task";
	// 接收到CQT楼层关系的推送
	public static final String SHARE_ACTION_4 = "com.walktour.gui.weifuwu.sharepush.share.cqt";
	// 接收到地图图片的推送
	public static final String SHARE_ACTION_5 = "com.walktour.gui.weifuwu.sharepush.share.pic";
	// 接收到基站文件的推送
	public static final String SHARE_ACTION_6 = "com.walktour.gui.weifuwu.sharepush.share.station";
	// 接收到截屏文件的推送
	public static final String SHARE_ACTION_7 = "com.walktour.gui.weifuwu.sharepush.share.picscreenshot";
	// 接收到数据管理文件的推送
	public static final String SHARE_ACTION_8 = "com.walktour.gui.weifuwu.sharepush.share.data";
	// 接收到报表文件的推送
	public static final String SHARE_ACTION_9 = "com.walktour.gui.weifuwu.sharepush.share.report";
	// 接收完成后刷新整个数据管理界面
	public static final String SHARE_ACTION_REFRESH_DATA = "com.walktour.gui.weifuwu.sharepush.share.data_manager";
	// 接收完成后刷新工程文件数据管理界面
	public static final String SHARE_ACTION_REFRESH_PROJECT = "com.walktour.gui.weifuwu.sharepush.share.project_manager";
	/** 刷新第一个界面 **/
	public static final String SHARE_ACTION_MAIN_1 = "com.walktour.gui.weifuwu.sharepush.share.main.one";
	/** 刷新第二个界面 **/
	public static final String SHARE_ACTION_MAIN_2 = "com.walktour.gui.weifuwu.sharepush.share.main.two";
	// 当前截屏分享的文件信息
	public static String CURRENT_SCREEN_SHOT_PIC_NAME = "";
	// 当前室内地图分享
	public static String CURRENT_CQT_PIC_NAME = "";
	/** 数据管理共享信息时的描述文件 **/
	public static final String DATA_DESCRIBE_JSON = "task.json";
	/**
	 * 遍历sdcard下的报表目录,返回文件列表
	 */
	public static List<File> getReportFiles(Context context) {
		File root = new File(AppFilePathUtil.getInstance().createSDCardBaseDirectory(context.getString(R.string.path_data),context.getString(R.string.path_report)));
		File[] files = root.listFiles();
		List<File> filePathList = new LinkedList<File>();
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			} else {
				filePathList.add(file);
			}
		}
		Collections.sort(filePathList, new Comparator<File>() {
			@Override
			public int compare(File lhs, File rhs) {
				return new Date(rhs.lastModified()).compareTo(new Date(lhs.lastModified()));
			}
		});
		return filePathList;
	}
	/**
	 * 将数组转换为JSON格式的数据。
	 * 
	 * @param stoneList
	 *            数据源
	 * @return JSON格式的数据
	 */
	public static String changeArrayToJson(ArrayList<TestRecord> stoneList) {
		try {
			JSONObject array = new JSONObject();
			JSONObject obj1 = new JSONObject();
			int length = stoneList.size();
			for (int i = 0; i < length; i++) {
				TestRecord stone = stoneList.get(i);
				// data_test_record表
				Field[] fields1 = stone.getClass().getFields();
				JSONObject data_test_record = new JSONObject();
				for (Field f : fields1) {
					data_test_record.put(f.getName(), f.get(stone));
				}
				array.put("data_test_record", data_test_record);
				// data_record_nettype表
				JSONArray array1 = new JSONArray();
				ArrayList<RecordNetType> nettypes = stone.getRecordNetTypes();
				for (RecordNetType nt : nettypes) {
					putJson(array1, nt);
				}
				array.put("data_record_nettype", array1);
				// data_record_tasktype表
				JSONArray array2 = new JSONArray();
				ArrayList<RecordTaskType> tasks = stone.getRecordTaskTypes();
				for (RecordTaskType nt : tasks) {
					putJson(array2, nt);
				}
				array.put("data_record_tasktype", array2);
				// data_record_abnormal表
				JSONArray array3 = new JSONArray();
				ArrayList<RecordAbnormal> abnors = stone.getRecordAbnormals();
				for (RecordAbnormal nt : abnors) {
					putJson(array3, nt);
				}
				array.put("data_record_abnormal", array3);
				// data_record_detail表
				JSONArray array4 = new JSONArray();
				ArrayList<RecordDetail> details = stone.getRecordDetails();
				for (RecordDetail nt : details) {
					putJson(array4, nt);
				}
				array.put("data_record_detail", array4);
				// data_test_info表
				JSONArray array5 = new JSONArray();
				ArrayList<RecordTestInfo> infos = stone.getRecordTestInfo();
				for (RecordTestInfo nt : infos) {
					putJson(array5, nt);
				}
				array.put("data_test_info", array5);
			}
			JSONArray OB = new JSONArray();
			OB.put(array);
			obj1.put("testrecords", OB);
			return obj1.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static void putJson(JSONArray array, RecordBase base) throws Exception {
		Field[] fields2 = base.getClass().getFields();
		JSONObject jo = new JSONObject();
		for (Field f : fields2) {
			jo.put(f.getName(), f.get(base));
		}
		array.put(jo);
	}
	/**
	 * 将JSON转化为数组并返回。
	 * 
	 * @param Json
	 * @return ArrayList<TestRecord>
	 */
	public static ArrayList<TestRecord> changeJsonToArray(String Json) throws Exception {
		ArrayList<TestRecord> gameList = new ArrayList<TestRecord>();
		try {
			JSONObject jsonObject = new JSONObject(Json);
			if (!jsonObject.isNull("testrecords")) {
				String aString = jsonObject.getString("testrecords");
				JSONArray aJsonArray = new JSONArray(aString);
				int length = aJsonArray.length();
				for (int i = 0; i < length; i++) {
					JSONObject stoneJson = aJsonArray.getJSONObject(i);
					TestRecord stone = new TestRecord();
					// data_test_record
					if (!stoneJson.isNull("data_test_record")) {
						JSONObject obj = stoneJson.getJSONObject("data_test_record");
						getJson(stone, obj);
					}
					// data_record_tasktype
					if (!stoneJson.isNull("data_record_tasktype")) {
						JSONArray nettype = stoneJson.getJSONArray("data_record_tasktype");
						if (null != nettype) {
							int length1 = nettype.length();
							for (int j = 0; j < length1; j++) {
								JSONObject json1 = nettype.getJSONObject(j);
								RecordTaskType tm = new RecordTaskType();
								getJson(tm, json1);
								stone.getRecordTaskTypes().add(tm);
							}
						}
					}
					// data_record_abnormal
					if (!stoneJson.isNull("data_record_abnormal")) {
						JSONArray nettype = stoneJson.getJSONArray("data_record_abnormal");
						if (null != nettype) {
							int length1 = nettype.length();
							for (int j = 0; j < length1; j++) {
								JSONObject json1 = nettype.getJSONObject(j);
								RecordAbnormal tm = new RecordAbnormal();
								getJson(tm, json1);
								stone.getRecordAbnormals().add(tm);
							}
						}
					}
					// data_record_detail
					if (!stoneJson.isNull("data_record_detail")) {
						JSONArray nettype = stoneJson.getJSONArray("data_record_detail");
						if (null != nettype) {
							int length1 = nettype.length();
							for (int j = 0; j < length1; j++) {
								JSONObject json1 = nettype.getJSONObject(j);
								RecordDetail tm = new RecordDetail();
								getJson(tm, json1);
								stone.getRecordDetails().add(tm);
							}
						}
					}
					// data_record_nettype
					if (!stoneJson.isNull("data_record_nettype")) {
						JSONArray nettype = stoneJson.getJSONArray("data_record_nettype");
						if (null != nettype) {
							int length1 = nettype.length();
							for (int j = 0; j < length1; j++) {
								JSONObject json1 = nettype.getJSONObject(j);
								RecordNetType tm = new RecordNetType();
								getJson(tm, json1);
								stone.getRecordNetTypes().add(tm);
							}
						}
					}
					// data_test_info
					if (!stoneJson.isNull("data_test_info")) {
						JSONArray nettype = stoneJson.getJSONArray("data_test_info");
						if (null != nettype) {
							int length1 = nettype.length();
							for (int j = 0; j < length1; j++) {
								JSONObject json1 = nettype.getJSONObject(j);
								RecordTestInfo tm = new RecordTestInfo();
								getJson(tm, json1);
								stone.getRecordTestInfo().add(tm);
							}
						}
					}
					gameList.add(stone);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameList;
	}
	private static void getJson(RecordBase base, JSONObject json1) throws Exception {
		Field[] fields = base.getClass().getFields();
		for (Field f : fields) {
			String type = f.getType().toString();
			if (type.endsWith("String")) {
				if (json1.has(f.getName()))
					f.set(base, json1.getString(f.getName()));
			} else if (type.endsWith("Integer") || type.endsWith("int")) {
				if (json1.has(f.getName()))
					f.set(base, json1.getInt(f.getName()));
			} else if (type.endsWith("Long") || type.endsWith("long")) {
				if (json1.has(f.getName()))
					f.set(base, json1.getLong(f.getName()));
			}
		}
		fields = null;
	}
}

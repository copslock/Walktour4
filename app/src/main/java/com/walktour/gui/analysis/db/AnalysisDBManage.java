package com.walktour.gui.analysis.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.walktour.Utils.DateUtils;
import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.analysis.model.AnalysisModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/***
 * 智能分析数据库操作管理
 * 
 * @author weirong.fan
 *
 */
public class AnalysisDBManage {
	/** 单例 */
	private static final AnalysisDBManage instance = new AnalysisDBManage();
	private SQLiteDatabase db = null;
	/** 所有的数据表名 **/
	private List<String> tableList = new LinkedList<String>();

	/** CSFB主叫详情表 */
	private final String table_CSFB_Detail_MO = "'#DL_CSFB_Detail_MO'";
	/** CSFB被叫详情表 */
	private final String table_CSFB_Detail_MT = "'#DL_CSFB_Detail_MT'";
	/** CSFB主叫统计表 -覆盖率 */
	public static final String table_CSFB_COVERAGE = "'#DL_CSFB_FG'";
	/** CSFB主叫统计表-接通率和掉话率 */
	public static final String table_CSFB_TOTAL_MO = "'#DL_CSFB_Total_MO'";
	/** CSFB被叫统计表-接通率和掉话率 */
	public static final String table_CSFB_TOTAL_MT = "'#DL_CSFB_Total_MT'";

	/** CSFB主叫2G回落到LTE 时延 */
	public static final String table_CSFB_2G_LTE_MO_DELAY = "'#DL_2G Return to LTE Delay_MO'";
	/** CSFB被叫2G回落到LTE 时延 */
	public static final String table_CSFB_2G_LTE_MT_DELAY = "'#DL_2G Return to LTE Delay_MT'";
	/** CSFB主叫3G回落到LTE 时延 */
	public static final String table_CSFB_3G_LTE_MO_DELAY = "'#DL_3G Return to LTE Delay_MO'";
	/** CSFB主叫3G回落到LTE 时延 */
	public static final String table_CSFB_3G_LTE_MT_DELAY = "'#DL_3G Return to LTE Delay_MT'";
	/** CSFB主叫CSFB回落到2G 时延 */
	public static final String table_CSFB_TO_2G_MO_DELAY = "'#DL_CSFB to 2G Delay_MO'";
	/** CSFB被叫CSFB回落到2G 时延 */
	public static final String table_CSFB_TO_2G_MT_DELAY = "'#DL_CSFB to 2G Delay_MT'";
	/** CSFB主叫CSFB回落到2G 时延 */
	public static final String table_CSFB_TO_3G_MO_DELAY = "'#DL_CSFB to 3G Delay_MO'";
	/** CSFB被叫CSFB回落到2G 时延 */
	public static final String table_CSFB_TO_3G_MT_DELAY = "'#DL_CSFB to 3G Delay_MT'";

	/** VOLTE主叫详情表 */
	private final String table_VOLTE_Detail_MO = "'#DL_VoLTE_Detail_MO'";
	/** VOLTE被叫详情表 */
	private final String table_VOLTE_Detail_MT = "'#DL_VoLTE_Detail_MT'";
	/** VOLTE主叫统计表 */
	public static final String table_VOLTE_TOTAL_MO = "'#DL_VoLTE_Total_MO'";
	/** VOLTE被叫统计表 */
	public static final String table_VOLTE_TOTAL_MT = "'#DL_VoLTE_Total_MT'";

	/** VOLTE统计表 参数1 */
	public static final String table_VOLTE_TOTAL_PARAM_1 = "'#DL_RFC1889_Jitter'";
	public static final String table_VOLTE_TOTAL_PARAM_2 = "'#DL_Jitter_Buffer_Delay'";
	public static final String table_VOLTE_TOTAL_PARAM_3 = "'#DL_PDSCH_BLER'";
	public static final String table_VOLTE_TOTAL_PARAM_4 = "'#DL_PUSCH_BLER'";
	public static final String table_VOLTE_TOTAL_PARAM_5 = "'#DL_POLQA_MOS'";
	public static final String table_VOLTE_TOTAL_PARAM_6 = "'#DL_PESQ_MOS'";
	public static final String table_VOLTE_TOTAL_PARAM_7 = "'#DL_PDSCH_Scheduled_RB_Count_slot'";
	public static final String table_VOLTE_TOTAL_PARAM_8 = "'#DL_PUSCH_Scheduled_RB_Count_slot'";
	public static final String table_VOLTE_TOTAL_PARAM_9 = "'#DL_MCS_Average_code2_DL_s'";
	public static final String table_VOLTE_TOTAL_PARAM_10 = "'#DL_MCS_Average_code1_DL_s'";
	public static final String table_VOLTE_TOTAL_PARAM_11 = "'#DL_MCS_Average_DL_s'";
	public static final String table_VOLTE_TOTAL_PARAM_12 = "'#DL_Packet_Loss_Rate'";

	/** LTEDATA速率类FTPdownload详情表 */
	private final String table_LTEDATA_FTPDOWNLOAD = "'ContinousLowPDCPThr'";
	/** LTEDATA速率类FTPdownload统计表-RSRP */
	private final String table_LTEDATA_FTPDOWNLOAD_RSRP = "'#DL_RSRP_ByFtpDownload'";
	/** LTEDATA速率类FTPdownload统计表-RSRQ */
	private final String table_LTEDATA_FTPDOWNLOAD_RSRQ = "'#DL_RSRQ_ByFtpDownload'";
	/** LTEDATA速率类FTPdownload统计表-SINR */
	private final String table_LTEDATA_FTPDOWNLOAD_SINR = "'#DL_SINR_ByFtpDownload'";
	/** LTEDATA速率类FTPdownload统计表-应用层平均速率 */
	private final String table_LTEDATA_FTPDOWNLOAD_RATE = "'#DL_FTPDownloadRate'";
	/** LTEDATA速率类FTPdownload统计表-业务尝试次数，成功次数，掉线次数 */
	private final String table_LTEDATA_FTPDOWNLOAD_TOTAL = "'#DL_LTEData_Total_FTPDownload'";

	/**
	 * 私有构造器,防止外部构造
	 */
	private AnalysisDBManage() {
		super();
	}

	public static AnalysisDBManage getInstance() {
		return instance;
	}

	public void openDatabase(String dbPath) throws Exception {
		if (db != null) {
			db.close();
			db = null;
		}
		db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		getAllTables();
	}

	/***
	 * 获取时间列字段
	 * 
	 * @param cursor
	 *            游标
	 * @param columneName
	 *            时间列
	 * @return
	 */
	private String getColumnDateTime(Cursor cursor, String columneName) {
		return DateUtils.transferLongToDate(DateUtils.DATE_TIME_FORMAT, UtilsMethod.getTime(cursor.getDouble(cursor.getColumnIndex(columneName)) + ""));
	}

	private int getColumnInt(Cursor cursor, String columneName) {
		return Integer.parseInt(cursor.getString(cursor.getColumnIndex(columneName)));
	}

	/**
	 * 获取VOLTE主叫详情
	 * 
	 * @return
	 */
	public List<AnalysisModel> getCSFBDetail(String columnName, String where) {
		List<AnalysisModel> list = new LinkedList<AnalysisModel>();
		Cursor cursor = null;
		String sql = "";
		// 主叫
		if (tableList.contains(table_CSFB_Detail_MO)) {
			sql = "select b.LOGName,a.* from " + table_CSFB_Detail_MO + " as a,LogInfo as b where a.'LogId_999'=b.LogId and " + where;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				AnalysisModel model = new AnalysisModel();
				model.setExceptionType(AnalysisModel.TYPE_MO);
				if (cursor.getDouble(cursor.getColumnIndex("COMPUTERTIME_BLOCK")) != 0D) {
					model.setExceptionTime(getColumnDateTime(cursor, "COMPUTERTIME_BLOCK"));
				} else if (cursor.getDouble(cursor.getColumnIndex("COMPUTERTIME_REQUEST")) != 0D) {
					model.setExceptionTime(getColumnDateTime(cursor, "COMPUTERTIME_REQUEST"));
				} else {
					model.setExceptionTime(getColumnDateTime(cursor, "COMPUTERTIME_ATTEMPT"));
				}

				if (0D != cursor.getDouble(cursor.getColumnIndex("LATITUDE_BLOCK"))) {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_BLOCK")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LATITUDE_DROP"))) {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_DROP")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LATITUDE_END"))) {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_END")));
				} else {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_ATTEMPT")));
				}

				if (0D != cursor.getDouble(cursor.getColumnIndex("LONGITUDE_BLOCK"))) {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_BLOCK")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LONGITUDE_DROP"))) {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_DROP")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LONGITUDE_END"))) {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_END")));
				} else {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_ATTEMPT")));
				}

				model.setDdibFile(cursor.getString(cursor.getColumnIndex("LOGName")));
				if (null != cursor.getString(cursor.getColumnIndex("POINTINDEX_REQUEST"))) {
					model.setStartIndex(getColumnInt(cursor, "POINTINDEX_REQUEST"));
				} else {
					model.setStartIndex(getColumnInt(cursor, "POINTINDEX_ATTEMPT"));
				}

				if (cursor.getString(cursor.getColumnIndex("POINTINDEX_DROP")) == null) {
					if (cursor.getString(cursor.getColumnIndex("POINTINDEX_BLOCK")) == null) {
						if (cursor.getString(cursor.getColumnIndex("POINTINDEX_END")) == null) {
							if (cursor.getString(cursor.getColumnIndex("POINTINDEX_CONNECT")) == null) {
								if (cursor.getString(cursor.getColumnIndex("POINTINDEX_ALERTING")) == null) {
									// 都为空就算了
								} else {
									model.setEndIndex(getColumnInt(cursor, "POINTINDEX_ALERTING"));
								}
							} else {
								model.setEndIndex(getColumnInt(cursor, "POINTINDEX_CONNECT"));
							}
						} else {
							model.setEndIndex(getColumnInt(cursor, "POINTINDEX_END"));
						}
					} else {
						model.setEndIndex(getColumnInt(cursor, "POINTINDEX_BLOCK"));
					}
				} else {
					model.setEndIndex(getColumnInt(cursor, "POINTINDEX_DROP"));
				}
				model.setExceptionCode(cursor.getFloat(cursor.getColumnIndex(columnName)));
				list.add(model);
			}
			closeCursor(cursor);
		}
		// 被叫
		if (tableList.contains(table_CSFB_Detail_MT)) {
			sql = "select b.LOGName,a.* from " + table_CSFB_Detail_MT + " as a,LogInfo as b where a.'LogId_999'=b.LogId and " + where;

			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				AnalysisModel model = new AnalysisModel();
				model.setExceptionType(AnalysisModel.TYPE_MT);
				if (cursor.getDouble(cursor.getColumnIndex("COMPUTERTIME_BLOCK")) != 0D) {
					model.setExceptionTime(getColumnDateTime(cursor, "COMPUTERTIME_BLOCK"));
				} else if (cursor.getDouble(cursor.getColumnIndex("COMPUTERTIME_REQUEST")) != 0D) {
					model.setExceptionTime(getColumnDateTime(cursor, "COMPUTERTIME_REQUEST"));
				} else {
					model.setExceptionTime(getColumnDateTime(cursor, "COMPUTERTIME_ATTEMPT"));
				}

				if (0D != cursor.getDouble(cursor.getColumnIndex("LATITUDE_BLOCK"))) {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_BLOCK")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LATITUDE_DROP"))) {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_DROP")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LATITUDE_END"))) {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_END")));
				} else {
					model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_ATTEMPT")));
				}

				if (0D != cursor.getDouble(cursor.getColumnIndex("LONGITUDE_BLOCK"))) {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_BLOCK")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LONGITUDE_DROP"))) {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_DROP")));
				} else if (0D != cursor.getDouble(cursor.getColumnIndex("LONGITUDE_END"))) {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_END")));
				} else {
					model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_ATTEMPT")));
				}

				model.setDdibFile(cursor.getString(cursor.getColumnIndex("LOGName")));
				if (null != cursor.getString(cursor.getColumnIndex("POINTINDEX_REQUEST"))) {
					model.setStartIndex(getColumnInt(cursor, "POINTINDEX_REQUEST"));
				} else {
					model.setStartIndex(getColumnInt(cursor, "POINTINDEX_ATTEMPT"));
				}
				if (cursor.getString(cursor.getColumnIndex("POINTINDEX_DROP")) == null) {
					if (cursor.getString(cursor.getColumnIndex("POINTINDEX_BLOCK")) == null) {
						if (cursor.getString(cursor.getColumnIndex("POINTINDEX_END")) == null) {
							if (cursor.getString(cursor.getColumnIndex("POINTINDEX_CONNECT")) == null) {
								if (cursor.getString(cursor.getColumnIndex("POINTINDEX_ALERTING")) == null) {
									// 都为空就算了
								} else {
									model.setEndIndex(getColumnInt(cursor, "POINTINDEX_ALERTING"));
								}
							} else {
								model.setEndIndex(getColumnInt(cursor, "POINTINDEX_CONNECT"));
							}
						} else {
							model.setEndIndex(getColumnInt(cursor, "POINTINDEX_END"));
						}
					} else {
						model.setEndIndex(getColumnInt(cursor, "POINTINDEX_BLOCK"));
					}
				} else {
					model.setEndIndex(getColumnInt(cursor, "POINTINDEX_DROP"));
				}
				model.setExceptionCode(cursor.getFloat(cursor.getColumnIndex(columnName)));

				list.add(model);
			}
			closeCursor(cursor);
		}
		return list;
	}

	/**
	 * 获取VOLTE主叫详情
	 * 
	 * @return
	 */
	public List<Float> getCSFBDetail_MO_196() {
		List<Float> list = new LinkedList<Float>();
		Cursor cursor = null;
		String sql = "";
		if (tableList.contains(table_CSFB_Detail_MO)) {
			list = new LinkedList<Float>();
			sql = "select AVERAGE_196 from " + table_CSFB_Detail_MO;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				list.add(cursor.getFloat(cursor.getColumnIndex("AVERAGE_196")));
			}
			closeCursor(cursor);
		}

		return list;
	}

	/****
	 * 获取CSFB 回落数据
	 * 
	 * @return
	 */
	public List<Float> getCSFBDetail_MT_196() {
		List<Float> list = new LinkedList<Float>();
		Cursor cursor = null;
		String sql = "";

		if (tableList.contains(table_CSFB_Detail_MT)) {
			list = new LinkedList<Float>();
			sql = "select AVERAGE_196 from " + table_CSFB_Detail_MT;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				list.add(cursor.getFloat(cursor.getColumnIndex("AVERAGE_196")));
			}
			closeCursor(cursor);
		}
		return list;
	}

	/**
	 * 获取VOLTE主叫详情
	 * 
	 * @return
	 */
	public List<AnalysisModel> getVoLTEDetail() {
		List<AnalysisModel> list = new LinkedList<AnalysisModel>();
		Cursor cursor = null;
		String sql = "";
		if (tableList.contains(table_VOLTE_Detail_MO)) {

			sql = "select b.LOGName,a.* from " + table_VOLTE_Detail_MO + " as a,LogInfo as b where a.'LogId_999'=b.LogId and " + "AVERAGE_1>0";

			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				list.add(initAnalysisModel(AnalysisModel.TYPE_MO, cursor));
			}
			closeCursor(cursor);
		}
		if (tableList.contains(table_VOLTE_Detail_MT)) {

			list = new LinkedList<AnalysisModel>();
			sql = "select b.LOGName,a.* from " + table_VOLTE_Detail_MT + " as a,LogInfo as b where a.'LogId_999'=b.LogId and " + "AVERAGE_1>0";
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				list.add(initAnalysisModel(AnalysisModel.TYPE_MT, cursor));
			}
			closeCursor(cursor);
		}
		return list;
	}

	/***
	 * 根据指针初始化model
	 * 
	 * @param model
	 *            文件对象
	 * @param cursor
	 *            指针
	 */
	private AnalysisModel initAnalysisModel(int type, Cursor cursor) {
		AnalysisModel model = new AnalysisModel();
		model.setExceptionType(type);
		model.setExceptionTime(DateUtils.transferLongToDate(DateUtils.DATE_TIME_FORMAT, UtilsMethod.getTime(cursor.getDouble(cursor.getColumnIndex("COMPUTERTIME_ATTEMPT")) + "")));
		model.setLat(cursor.getDouble(cursor.getColumnIndex("LATITUDE_ATTEMPT")));
		model.setLon(cursor.getDouble(cursor.getColumnIndex("LONGITUDE_ATTEMPT")));
		model.setExceptionCode(cursor.getFloat(cursor.getColumnIndex("AVERAGE_1")));
		model.setDdibFile(cursor.getString(cursor.getColumnIndex("LOGName")));
		model.setStartIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("POINTINDEX_ATTEMPT"))));
		if (cursor.getString(cursor.getColumnIndex("POINTINDEX_DROP")) == null) {
			if (cursor.getString(cursor.getColumnIndex("POINTINDEX_BLOCK")) == null) {
				if (cursor.getString(cursor.getColumnIndex("POINTINDEX_END")) == null) {
					if (cursor.getString(cursor.getColumnIndex("POINTINDEX_CONNECT")) == null) {
						if (cursor.getString(cursor.getColumnIndex("POINTINDEX_ALERTING")) == null) {
							// 都为空就算了
						} else {
							model.setEndIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("POINTINDEX_ALERTING"))));
						}
					} else {
						model.setEndIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("POINTINDEX_CONNECT"))));
					}
				} else {
					model.setEndIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("POINTINDEX_END"))));
				}
			} else {
				model.setEndIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("POINTINDEX_BLOCK"))));
			}
		} else {
			model.setEndIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex("POINTINDEX_DROP"))));
		}
		return model;
	}

	/**
	 * 获取table中的数据，整形和浮点型
	 * 
	 * @return key--统计的项,value--统计的值
	 */
	public Map<String, String> getTaleInfo(String tableName) {
		Map<String, String> map = new HashMap<String, String>();
		if (tableList.contains(tableName)) {

			Cursor cursor = db.rawQuery("select * from " + tableName, null);
			String[] colums = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				for (int i = 0; i < colums.length; i++) {
					int type = cursor.getType(cursor.getColumnIndex(colums[i]));
					if (type == Cursor.FIELD_TYPE_INTEGER) {// 整形
						map.put(colums[i], cursor.getInt(cursor.getColumnIndex(colums[i])) + "");
					} else if (type == Cursor.FIELD_TYPE_FLOAT) {// 浮点型
						map.put(colums[i], cursor.getFloat(cursor.getColumnIndex(colums[i])) + "");
					}
				}
				// 只需要处理一条记录
				break;
			}
			closeCursor(cursor);

		}

		return map;
	}

	/**
	 * LTEData获取速率类异常详情
	 * 
	 * @return
	 */
	public List<AnalysisModel> getLTEDATAFtpDownload(String where, String exceptinInfo) {
		List<AnalysisModel> list = new LinkedList<AnalysisModel>();
		Cursor cursor = null;
		String sql = "";
		// FTP DOWNLOAD
		if (tableList.contains(table_LTEDATA_FTPDOWNLOAD)) {
			sql = "select b.LOGName,a.* from " + table_LTEDATA_FTPDOWNLOAD + " as a,LogInfo as b where a.LOGName=b.LOGName and " + where;
			cursor = db.rawQuery(sql, null);
			String[] colums = cursor.getColumnNames();
			while (cursor.moveToNext()) {
				AnalysisModel model = new AnalysisModel();
				model.setExceptionType(AnalysisModel.TYPE_FTPDOWNLOAD);
				model.setExceptionTime(cursor.getString(cursor.getColumnIndex("BeginTime")));
				model.setLat(cursor.getDouble(cursor.getColumnIndex("BeginLatitude")));
				model.setLon(cursor.getDouble(cursor.getColumnIndex("BeginLongitude")));
				model.setDdibFile(cursor.getString(cursor.getColumnIndex("LOGName")));
				model.setStartIndex(cursor.getInt(cursor.getColumnIndex("BeginPointIndex")));
				model.setEndIndex(cursor.getInt(cursor.getColumnIndex("EndPointIndex")));
				model.setExceptionInfo(exceptinInfo);

				for (int i = 0; i < colums.length; i++) {
					int type = cursor.getType(cursor.getColumnIndex(colums[i]));
					if (type == Cursor.FIELD_TYPE_INTEGER) {// 整形
						model.getOtherInfo().put(colums[i], cursor.getInt(cursor.getColumnIndex(colums[i])) + "");
					} else if (type == Cursor.FIELD_TYPE_FLOAT) {// 浮点型
						model.getOtherInfo().put(colums[i], cursor.getFloat(cursor.getColumnIndex(colums[i])) + "");
					} else {
						model.getOtherInfo().put(colums[i], cursor.getString(cursor.getColumnIndex(colums[i])) + "");
					}
				}

				list.add(model);
			}
			closeCursor(cursor);
		}
		return list;
	}

	/**
	 * LTEData获取统计信息
	 * 
	 * @return
	 */
	public Map<String, String> getLTEDataTotalFTPDownload() {
		Map<String, String> map = new HashMap<String, String>();
		String[] tables = new String[] { table_LTEDATA_FTPDOWNLOAD_RSRP, table_LTEDATA_FTPDOWNLOAD_RSRQ, table_LTEDATA_FTPDOWNLOAD_SINR, table_LTEDATA_FTPDOWNLOAD_RATE, table_LTEDATA_FTPDOWNLOAD_TOTAL };
		for (String table : tables) {
			if (tableList.contains(table)) {
				Cursor cursor = db.rawQuery("select * from " + table, null);
				String[] colums = cursor.getColumnNames();
				while (cursor.moveToNext()) {
					for (int i = 0; i < colums.length; i++) {
						int type = cursor.getType(cursor.getColumnIndex(colums[i]));
						if (type == Cursor.FIELD_TYPE_INTEGER) {// 整形
							map.put(colums[i], cursor.getInt(cursor.getColumnIndex(colums[i])) + "");
						} else if (type == Cursor.FIELD_TYPE_FLOAT) {// 浮点型
							map.put(colums[i], cursor.getFloat(cursor.getColumnIndex(colums[i])) + "");
						}
					}
					// 只需要处理一条记录
					break;
				}
				closeCursor(cursor);
			}
		}
		return map;
	}

	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			if (!cursor.isClosed())
				cursor.close();
			cursor = null;
		}
	}

	/**
	 * 获取所有的数据库的所有表名
	 * 
	 * @return
	 */
	private void getAllTables() {
		tableList.clear();
		Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
		while (cursor.moveToNext()) {
			tableList.add("'" + cursor.getString(cursor.getColumnIndex("name")) + "'");
		}
		closeCursor(cursor);
	}

}

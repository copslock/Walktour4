package com.walktour.gui.weifuwu.business.table;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.walktour.Utils.DateUtils;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareFileModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupRelationModel;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/***
 * 微服务共享信息数据库
 * 
 * @author weirong.fan
 *
 */
public class ShareDataBase {
	/** SD文件数据库名 */
	public final static String DATABASENAME = "share_db.db";
	/** 单例 */
	private static ShareDataBase instance = null;
	/** 数据库辅助类 */
	private DatabaseHelper dataBase = null;
	/** 数据库版本 */
	private final int DB_VERSION = 2;
	/** 上下文 **/
	private Context context;
	/** 共享文件信息表 **/
	private final String ID = "_id";
	public static final String TABLE_FILE_NAME = "table_share_file";
	private final String FILE_TYPE = "fileType";
	private final String FILE_PATH = "filePath";
	private final String FILE_NAME = "fileName";
	private final String FILE_ID = "fileID";
	private final String FILE_TOTAL_SIZE = "fileTotalSize";
	private final String FILE_DESCRIBE = "fileDescribe";
	private final String SEND_OR_RECEIVE = "sendOrReceive";
	private final String FROM_DEVICE_CODE = "fromDeviceCode";
	private final String FROM_GROUP_CODE = "fromGroupCode";
	private final String TARGET_DEVICE_CODES = "targetDeviceCodes";
	private final String TARGET_GROUP_CODES = "targetGroupCodes";
	private final String FILE_STATUS = "fileStatus";
	private final String FILE_REAL_SIZE = "fileRealSize";
	/** 终端信息表 **/
	public static final String TABLE_DEVICE_NAME = "table_share_device";
	/** 群组信息表 **/
	public static final String TABLE_GROUP_NAME = "table_share_group";
	/** 群组终端信息表 **/
	public static final String TABLE_GROUP_RELATION_NAME = "table_share_group_relation";
	/** 共享文件信息表,具体共享的文件 */
	private final String DEVICE_CODE = "deviceCode";
	private final String DEVICE_NAME = "deviceName";
	private final String DEVICE_OS = "deviceOS";
	private final String DEVICE_TYPE = "deviceType";
	private final String DEVICE_MESSAGE = "deviceMessage";
	private final String DEVICE_STATUS = "deviceStatus";
	private final String GROUP_CODE = "groupCode";
	private final String GROUP_NAME = "groupName";
	private final String CREATE_DEVICE_CODE = "createDeviceCode";
	/** 创建时间 */
	private final String CREATETIME = "createtime";
	private SQLiteDatabase db = null;
	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	private ShareDataBase(Context context) {
		try {
			this.context = context.getApplicationContext();
			dataBase = new DatabaseHelper(this.context, DATABASENAME);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 单例
	 * 
	 * @param context
	 *            上下文
	 * @return SQLiteDataBase
	 */
	public static synchronized ShareDataBase getInstance(Context context) {
		if (null == instance) {
			instance = new ShareDataBase(context);
		}
		return instance;
	}
	/**
	 * 内部类DatabaseHelper
	 * 
	 * @author weirong.fan
	 * 
	 */
	public class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String databaseName) {
			super(context, databaseName, null, DB_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// 创建数据表
			db.execSQL(createDeviceTalbe());
			db.execSQL(createGroupTalbe());
			db.execSQL(createGroupRelationTalbe());
			db.execSQL(createFileTable());
		}
		@Override
		public SQLiteDatabase getWritableDatabase() {
			if (db == null) {// 如果是空
				File dbf = AppFilePathUtil.getInstance().getSDCardBaseFile(ShareCommons.SHARE_PATH_BASE,"share_db.db");
				// 数据库文件是否创建成功
				boolean isFileCreateSuccess = false;
				if (!dbf.exists()) {
					try {
						isFileCreateSuccess = dbf.createNewFile();
						if (isFileCreateSuccess) {
							db = SQLiteDatabase.openOrCreateDatabase(dbf, null);
							db.execSQL(createDeviceTalbe());
							db.execSQL(createGroupTalbe());
							db.execSQL(createGroupRelationTalbe());
							db.execSQL(createFileTable());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					isFileCreateSuccess = true;
					db = SQLiteDatabase.openOrCreateDatabase(dbf, null);
				}
			}
			return db;
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// 删除数据表
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP_RELATION_NAME);
			// 创建数据表
			onCreate(db);
		}
	}
	/***
	 * 创建文件信息表
	 * 
	 * @return
	 */
	private String createFileTable() {
		StringBuffer CREATE_TABLE = new StringBuffer();
		CREATE_TABLE.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_FILE_NAME);
		CREATE_TABLE.append("( ");
		CREATE_TABLE.append(ID).append(" INTEGER PRIMARY KEY, ");
		CREATE_TABLE.append(FILE_TYPE).append(" INTEGER, ");
		CREATE_TABLE.append(FILE_PATH).append(" TEXT, ");
		CREATE_TABLE.append(FILE_NAME).append(" TEXT, ");
		CREATE_TABLE.append(FILE_ID).append(" TEXT, ");
		CREATE_TABLE.append(FILE_TOTAL_SIZE).append(" INTEGER, ");
		CREATE_TABLE.append(FILE_DESCRIBE).append(" TEXT, ");
		CREATE_TABLE.append(SEND_OR_RECEIVE).append(" INTEGER, ");
		CREATE_TABLE.append(FROM_DEVICE_CODE).append(" TEXT, ");
		CREATE_TABLE.append(FROM_GROUP_CODE).append(" TEXT, ");
		CREATE_TABLE.append(TARGET_DEVICE_CODES).append(" TEXT, ");
		CREATE_TABLE.append(TARGET_GROUP_CODES).append(" TEXT, ");
		CREATE_TABLE.append(FILE_STATUS).append(" INTEGER, ");
		CREATE_TABLE.append(FILE_REAL_SIZE).append(" INTEGER, ");
		CREATE_TABLE.append(CREATETIME).append(" TEXT");
		CREATE_TABLE.append(");");
		return CREATE_TABLE.toString();
	}
	/***
	 * 创建终端信息表
	 * 
	 * @param db
	 */
	private String createDeviceTalbe() {
		StringBuffer CREATE_TABLE = new StringBuffer();
		CREATE_TABLE.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_DEVICE_NAME);
		CREATE_TABLE.append("( ");
		CREATE_TABLE.append(ID).append(" INTEGER PRIMARY KEY, ");
		CREATE_TABLE.append(DEVICE_CODE).append(" TEXT, ");
		CREATE_TABLE.append(DEVICE_NAME).append(" TEXT, ");
		CREATE_TABLE.append(DEVICE_OS).append(" TEXT, ");
		CREATE_TABLE.append(DEVICE_TYPE).append(" TEXT,");
		CREATE_TABLE.append(DEVICE_MESSAGE).append(" TEXT,");
		CREATE_TABLE.append(DEVICE_STATUS).append(" INTEGER,");
		CREATE_TABLE.append(CREATETIME).append(" TEXT");
		CREATE_TABLE.append(");");
		return CREATE_TABLE.toString();
	}
	/***
	 * 创建群组信息表
	 * 
	 * @param db
	 */
	private String createGroupTalbe() {
		StringBuffer CREATE_TABLE = new StringBuffer();
		CREATE_TABLE.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_GROUP_NAME);
		CREATE_TABLE.append("( ");
		CREATE_TABLE.append(ID).append(" INTEGER PRIMARY KEY, ");
		CREATE_TABLE.append(GROUP_CODE).append(" TEXT, ");
		CREATE_TABLE.append(GROUP_NAME).append(" TEXT, ");
		CREATE_TABLE.append(CREATE_DEVICE_CODE).append(" TEXT");
		CREATE_TABLE.append(");");
		return CREATE_TABLE.toString();
	}
	/***
	 * 创建群组终端信息表
	 * 
	 * @param db
	 */
	private String createGroupRelationTalbe() {
		StringBuffer CREATE_TABLE = new StringBuffer();
		CREATE_TABLE.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_GROUP_RELATION_NAME);
		CREATE_TABLE.append("( ");
		CREATE_TABLE.append(ID).append(" INTEGER PRIMARY KEY, ");
		CREATE_TABLE.append(GROUP_CODE).append(" TEXT, ");
		CREATE_TABLE.append(DEVICE_CODE).append(" TEXT");
		CREATE_TABLE.append(");");
		return CREATE_TABLE.toString();
	}
	private void closeCursor(Cursor cursor) {
		if (cursor != null) {
			if (!cursor.isClosed())
				cursor.close();
			cursor = null;
		}
	}
	// 终端数据表处理------------------------------
	/****
	 * 插入终端数据表
	 * 
	 * @param model
	 * @return
	 */
	public long insertDevice(ShareDeviceModel model) throws Exception {
		long rowID = -1;
		boolean flag = existDeviceByDeviceCode(model.getDeviceCode());
		if (!flag) {// 存储数据
			// 特殊的判定,自身设备永远不会被显示
			if (null != model.getDeviceCode() && !model.getDeviceCode().equals("")
					&& model.getDeviceCode().equals(ShareCommons.device_code)) {
				model.setDeviceStatus(ShareDeviceModel.STATUS_DEFUALT);
			}
			ContentValues values = new ContentValues();
			values.put(DEVICE_CODE, model.getDeviceCode());
			values.put(DEVICE_NAME, model.getDeviceName());
			values.put(DEVICE_OS, model.getDeviceOS());
			values.put(DEVICE_TYPE, model.getDeviceType());
			values.put(DEVICE_MESSAGE, model.getDeviceMessage());
			values.put(DEVICE_STATUS, model.getDeviceStatus());
			values.put(CREATETIME, DateUtils.getCurrentDateTime());
			rowID = dataBase.getWritableDatabase().insert(TABLE_DEVICE_NAME, null, values);
		}
		return rowID;
	}
	/***
	 * 更新数据终端信息表
	 * 
	 * @param deviceCode
	 *            终端CODE,6位
	 * @param deviceName
	 *            终端备注名称
	 * @throws Exception
	 *             异常抛出
	 */
	public void updateDevice(ShareDeviceModel model) throws Exception {
		// 特殊的判定,自身设备永远不会被显示
		if (null != model.getDeviceCode() && !model.getDeviceCode().equals("")
				&& model.getDeviceCode().equals(ShareCommons.device_code)) {
			model.setDeviceStatus(ShareDeviceModel.STATUS_DEFUALT);
		}
		ContentValues values = new ContentValues();
		values.put(DEVICE_NAME, model.getDeviceName());
		values.put(DEVICE_MESSAGE, model.getDeviceMessage());
		values.put(CREATETIME, DateUtils.getCurrentDateTime());
		values.put(DEVICE_STATUS, model.getDeviceStatus());
		dataBase.getWritableDatabase().update(TABLE_DEVICE_NAME, values,
				DEVICE_CODE + "='" + model.getDeviceCode() + "'", null);
	}
	/***
	 * TABLE_DEVICE_NAME 是否存在此设备
	 * 
	 * @param deviceCode
	 *            设备编号
	 * @return
	 */
	private boolean existDeviceByDeviceCode(String deviceCode) throws Exception {
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append("where " + DEVICE_CODE + "='" + deviceCode + "'");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			flag = true;
			break;
		}
		sql.setLength(0);
		sql = null;
		this.closeCursor(cursor);
		return flag;
	}
	/***
	 * 检测是否存在指定编号，指定状态的设备
	 * 
	 * @param deviceCode
	 *            设备编号
	 * @param deviceStatus
	 *            指定状态
	 * @return
	 */
	public boolean existDevice(String deviceCode, List<Integer> deviceStatus) throws Exception {
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append("where " + DEVICE_CODE + "='" + deviceCode + "' and " + DEVICE_STATUS);
		sql.append(" in (");
		for (int i = 0; i < deviceStatus.size(); i++) {
			sql.append(deviceStatus.get(i));
			if (i < deviceStatus.size() - 1) {
				sql.append(",");
			}
		}
		sql.append(")");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			flag = true;
			break;
		}
		sql.setLength(0);
		sql = null;
		this.closeCursor(cursor);
		return flag;
	}
	/***
	 * 保存或更新设备信息
	 */
	public boolean saveOrUpdateDevice(ShareDeviceModel model) throws Exception {
		boolean flag = false;
		flag = existDeviceByDeviceCode(model.getDeviceCode());
		if (!flag) {// 存储数据
			this.insertDevice(model);
			flag = true;
		} else {
			this.updateDevice(model);
		}
		return flag;
	}
	// 文件表处理------------------------------------------------------
	public long insertFile(ShareFileModel model) {
		long rowID = -1;
		ContentValues values = new ContentValues();
		values.put(FILE_TYPE, model.getFileType());
		values.put(FILE_PATH, model.getFilePath());
		values.put(FILE_NAME, model.getFileName());
		values.put(FILE_ID, model.getFileID());
		values.put(FILE_TOTAL_SIZE, model.getFileTotalSize());
		values.put(FILE_DESCRIBE, model.getFileDescribe());
		values.put(SEND_OR_RECEIVE, model.getSendOrReceive());
		values.put(FROM_DEVICE_CODE, model.getFromDeviceCode());
		values.put(FROM_GROUP_CODE, model.getFromGroupCode());
		values.put(TARGET_DEVICE_CODES, model.getTargetDeviceCodes());
		values.put(TARGET_GROUP_CODES, model.getTargetGroupCodes());
		values.put(FILE_STATUS, model.getFileStatus());
		values.put(FILE_REAL_SIZE, model.getFileRealSize());
		values.put(CREATETIME, DateUtils.getCurrentDateTime());
		rowID = dataBase.getWritableDatabase().insert(TABLE_FILE_NAME, null, values);
		return rowID;
	}
	/***
	 * 更新文件信息表
	 * 
	 * @param model
	 */
	public void updateFile(ShareFileModel model) {
		StringBuffer sql = new StringBuffer();
		sql.append("update " + TABLE_FILE_NAME + " set ");
		sql.append(" " + FILE_TYPE + "=" + model.getFileType());
		sql.append(", " + FILE_PATH + "='" + model.getFilePath() + "'");
		sql.append(", " + FILE_NAME + "='" + model.getFileName() + "'");
		sql.append(", " + FILE_ID + "='" + model.getFileID() + "'");
		sql.append(", " + FILE_TOTAL_SIZE + "=" + model.getFileTotalSize());
		sql.append(", " + FILE_DESCRIBE + "='" + model.getFileDescribe() + "'");
		sql.append(", " + SEND_OR_RECEIVE + "=" + model.getSendOrReceive());
		sql.append(", " + FROM_DEVICE_CODE + "='" + model.getFromDeviceCode() + "'");
		sql.append(", " + FROM_GROUP_CODE + "='" + model.getFromGroupCode() + "'");
		sql.append(", " + TARGET_DEVICE_CODES + "='" + model.getTargetDeviceCodes() + "'");
		sql.append(", " + TARGET_GROUP_CODES + "='" + model.getTargetGroupCodes() + "'");
		sql.append(", " + FILE_STATUS + "=" + model.getFileStatus());
		sql.append(", " + FILE_REAL_SIZE + "=" + model.getFileRealSize());
		sql.append(" where " + ID + "=" + model.getId());
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql = null;
	}
	/**
	 * 根据数据类型，更新所有文件的状态
	 * 
	 * @param fileType
	 *            文件类型,如果为-1，表示所有文件类型
	 * @param fileStatus
	 *            文件状态
	 * @throws Exception
	 *             异常抛出
	 */
	public void updateFileStatusToStart(int fileType) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("update " + TABLE_FILE_NAME + " set ");
		sql.append(FILE_STATUS + "=" + ShareFileModel.FILE_STATUS_START + "");
		sql.append(" where 1=1 ");
		if (fileType != -1) {
			sql.append(" and " + FILE_TYPE + "=" + fileType);
		}
		sql.append(" and " + FILE_STATUS + "=" + ShareFileModel.FILE_STATUS_INIT);
		// System.out.println("ShareDataBase-SQL(updateFileStatus)=" + sql);
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql = null;
	}
	/****
	 * 查询文件表所有数据
	 * 
	 * @return
	 */
	public List<ShareFileModel> fetchAllFiles() {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		Cursor cursor = db.rawQuery("select * from " + TABLE_FILE_NAME + " order by " + ID + " desc", null);
		while (cursor.moveToNext()) {
			ShareFileModel model = new ShareFileModel();
			initShareFileModel(model, cursor);
			list.add(model);
		}
		closeCursor(cursor);
		return list;
	}
	/****
	 * 获取指定ID 的文件信息
	 * 
	 * @param IDS
	 * @return
	 */
	public List<ShareFileModel> fetchAllFilesByIDS(List<Integer> IDS) {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ");
		sb.append(TABLE_FILE_NAME);
		sb.append(" where " + ID + " in(");
		for (int i = 0; i < IDS.size(); i++) {
			if (i == IDS.size() - 1)
				sb.append(IDS.get(i));
			else
				sb.append(IDS.get(i) + ",");
		}
		sb.append(" )");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sb.toString(), null);
		while (cursor.moveToNext()) {
			ShareFileModel model = new ShareFileModel();
			initShareFileModel(model, cursor);
			list.add(model);
		}
		closeCursor(cursor);
		return list;
	}
	/***
	 * 获取终端的往来信息
	 * 
	 * @param deviceCode
	 *            关联的终端
	 * @return
	 */
	public List<ShareFileModel> fetchAllFilesByCode(String code) {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + TABLE_FILE_NAME + " where (");
		sb.append(FROM_DEVICE_CODE + "='" + code + "' OR ");
		sb.append(TARGET_DEVICE_CODES + " like '%" + code + "%')");
		// System.out.println("ShareDataBase-SQL(updateFile)=" + sb.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sb.toString(), null);
		while (cursor.moveToNext()) {
			ShareFileModel model = new ShareFileModel();
			initShareFileModel(model, cursor);
			list.add(model);
		}
		sb.setLength(0);
		sb = null;
		closeCursor(cursor);
		return list;
	}
	/***
	 * 获取终端的分享的历史信息
	 * 
	 * @param deviceCode
	 * @return
	 */
	public List<ShareFileModel> fetchAllFilesByDeviceCode(String deviceCode) {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + TABLE_FILE_NAME + " where (");
		sb.append(FROM_DEVICE_CODE + "='" + deviceCode + "') or (");
		sb.append(TARGET_DEVICE_CODES + " like '%" + deviceCode + "%')");
		// System.out.println("ShareDataBase-SQL(updateFile)=" + sb.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sb.toString(), null);
		while (cursor.moveToNext()) {
			ShareFileModel model = new ShareFileModel();
			initShareFileModel(model, cursor);
			list.add(model);
		}
		sb.setLength(0);
		sb = null;
		closeCursor(cursor);
		return list;
	}
	public List<ShareFileModel> fetchAllFilesByGroupCode(String groupCode) {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + TABLE_FILE_NAME + " where (");
		sb.append(FROM_GROUP_CODE + "='" + groupCode + "') or (");
		sb.append(TARGET_GROUP_CODES + " like '%" + groupCode + "%')");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sb.toString(), null);
		while (cursor.moveToNext()) {
			ShareFileModel model = new ShareFileModel();
			initShareFileModel(model, cursor);
			list.add(model);
		}
		sb.setLength(0);
		sb = null;
		closeCursor(cursor);
		return list;
	}
	/***
	 * 根据文件状态和文件类型获取所有数据文件
	 * 
	 * @param fileStatus
	 *            文件状态
	 * @param fileType
	 *            文件类型,为-1是表示选择所有文件
	 * @return
	 */
	public List<ShareFileModel> fetchAllFilesByFileStatusAndFileType(int fileType, int[] fileStatus) throws Exception {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + TABLE_FILE_NAME + " where ");
		sb.append("(");
		for (int i = 0; i < fileStatus.length; i++) {
			sb.append(FILE_STATUS + "=" + fileStatus[i]);
			if (i != fileStatus.length - 1) {
				sb.append(" or ");
			}
		}
		sb.append(")");
		if (fileType != -1) {
			sb.append(" AND " + FILE_TYPE + "=" + fileType);
		}
		// System.out.println("ShareDataBase-SQL(fetchAllFilesByFileStatusAndFileType)="
		// + sb.toString());
		Cursor cursor=null;
		try {
			cursor = dataBase.getWritableDatabase().rawQuery(sb.toString(), null);
			while (cursor.moveToNext()) {
				ShareFileModel model = new ShareFileModel();
				initShareFileModel(model, cursor);
				list.add(model);
			}
			sb.setLength(0);
			sb = null;

		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			closeCursor(cursor);
		}
		return list;
	}
	/***
	 * 根据数据类型获取历史纪录信息
	 * 
	 * @param fileType
	 *            文件类型
	 * @return
	 * @throws Exception
	 */
	public List<ShareFileModel> fetchFilesHistory(int fileType) throws Exception {
		List<ShareFileModel> list = new LinkedList<ShareFileModel>();
		StringBuffer sb = new StringBuffer();
		sb.append("select * from " + TABLE_FILE_NAME + " where ");
		sb.append(SEND_OR_RECEIVE + "=" + ShareFileModel.SEND_OR_RECEIVE_SEND);
		if (fileType != -1) {
			sb.append(" AND " + FILE_TYPE + "=" + fileType);
		}
		sb.append(" AND " + FILE_STATUS + "=" + ShareFileModel.FILE_STATUS_FINISH);
		sb.append(" group by " + FILE_NAME);
		sb.append(" order by " + ID + " DESC ");
		sb.append(" LIMIT " + ShareCommons.SHARE_HISTORY_SIZE);
		// System.out.println("ShareDataBase-SQL(fetchFilesHistory)=" +
		// sb.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sb.toString(), null);
		while (cursor.moveToNext()) {
			ShareFileModel model = new ShareFileModel();
			initShareFileModel(model, cursor);
			list.add(model);
		}
		sb.setLength(0);
		sb = null;
		closeCursor(cursor);
		return list;
	}
	/***
	 * 获取指定行号的文件记录
	 * 
	 * @param rowID
	 * @return
	 */
	public ShareFileModel fetchFile(long rowID) {
		ShareFileModel model = null;
		Cursor cursor = db.rawQuery("select * from " + TABLE_FILE_NAME + " where " + ID + "=" + rowID, null);
		while (cursor.moveToNext()) {
			model = new ShareFileModel();
			initShareFileModel(model, cursor);
			break;
		}
		closeCursor(cursor);
		return model;
	}
	/***
	 * 删除指定主键ID的文件
	 * 
	 * @param ID
	 *            主键key
	 */
	public void deleteFile(int ID) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from  " + TABLE_FILE_NAME);
		sql.append(" where " + this.ID + "=" + ID);
		// System.out.println("ShareDataBase-SQL(deleteFile)=" + sql);
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql = null;
	}
	/**
	 * 删除所有历史资源
	 */
	public void deleteAllFile() {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from  " + TABLE_FILE_NAME);
		// sql.append(" where " + this.ID + "=" + ID);
		// System.out.println("ShareDataBase-SQL(deleteAllFile)=" + sql);
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql = null;
	}
	/**
	 * 删除所有历史资源
	 */
	public void deleteAllFileByFromGroupCode(String fromGroupCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from  " + TABLE_FILE_NAME);
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql = null;
	}
	/**
	 * 删除指定设备的历史资源
	 */
	public void updateAllFileByDeviceCode(String deviceCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("update " + TABLE_FILE_NAME);
		sql.append(" set " + TARGET_DEVICE_CODES + "=REPLACE(" + TARGET_DEVICE_CODES + "," + deviceCode + ",\"\")");
		sql.append(" ," + FROM_DEVICE_CODE + "=REPLACE(" + FROM_DEVICE_CODE + "," + deviceCode + ",\"\")");
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql = null;
	}
	/**
	 * 删除指定群组历史资源
	 */
	public void updateAllFileByGroupCode(String groupCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("update " + TABLE_FILE_NAME);
		sql.append(" set " + TARGET_GROUP_CODES + "=REPLACE(" + TARGET_GROUP_CODES + "," + groupCode + ",\"\")");
		sql.append(" ," + FROM_GROUP_CODE + "=REPLACE(" + FROM_GROUP_CODE + "," + groupCode + ",\"\")");
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql = null;
	}
	// 设备组处理-----------------------------------------------------------
	public List<ShareGroupModel> fetchAllGroup() {
		List<ShareGroupModel> list = new LinkedList<ShareGroupModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_NAME + " ");
		sql.append("group by  " + GROUP_CODE);
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			ShareGroupModel model = new ShareGroupModel();
			model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
			model.setGroupName(cursor.getString(cursor.getColumnIndex(GROUP_NAME)));
			model.setGroupCode(cursor.getString(cursor.getColumnIndex(GROUP_CODE)));
			model.setCreateDeviceCode(cursor.getString(cursor.getColumnIndex(CREATE_DEVICE_CODE)));
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	public List<ShareGroupModel> fetchAllGroup(String nameOrcode) {
		List<ShareGroupModel> list = new LinkedList<ShareGroupModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_NAME);
		sql.append(" where " + GROUP_CODE + " like '%" + nameOrcode + "%'");
		sql.append(" or " + GROUP_NAME + " like '%" + nameOrcode + "%'");
		sql.append(" group by  " + GROUP_CODE);
		// System.out.println(sql.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			ShareGroupModel model = new ShareGroupModel();
			model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
			model.setGroupName(cursor.getString(cursor.getColumnIndex(GROUP_NAME)));
			model.setGroupCode(cursor.getString(cursor.getColumnIndex(GROUP_CODE)));
			model.setCreateDeviceCode(cursor.getString(cursor.getColumnIndex(CREATE_DEVICE_CODE)));
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	public ShareGroupModel fetchGroup(String groupCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			ShareGroupModel model = new ShareGroupModel();
			model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
			model.setGroupName(cursor.getString(cursor.getColumnIndex(GROUP_NAME)));
			model.setGroupCode(cursor.getString(cursor.getColumnIndex(GROUP_CODE)));
			model.setCreateDeviceCode(cursor.getString(cursor.getColumnIndex(CREATE_DEVICE_CODE)));
			this.closeCursor(cursor);
			return model;
		}
		this.closeCursor(cursor);
		return null;
	}
	public List<ShareGroupRelationModel> fetchGroupRelation(String groupCode) {
		List<ShareGroupRelationModel> list = new LinkedList<ShareGroupRelationModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_RELATION_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			ShareGroupRelationModel model = new ShareGroupRelationModel();
			model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
			model.setGroupCode(cursor.getString(cursor.getColumnIndex(GROUP_CODE)));
			model.setDeviceCode(cursor.getString(cursor.getColumnIndex(DEVICE_CODE)));
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	/***
	 * 插入组数据
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	private long insertGroup(ShareGroupModel model) throws Exception {
		boolean flag = this.exitGroup(model.getGroupCode(), model.getCreateDeviceCode());
		long rowID = -1;
		if (!flag) {
			ContentValues values = new ContentValues();
			values.put(GROUP_CODE, model.getGroupCode());
			values.put(GROUP_NAME, model.getGroupName());
			values.put(CREATE_DEVICE_CODE, model.getCreateDeviceCode());
			rowID = dataBase.getWritableDatabase().insert(TABLE_GROUP_NAME, null, values);
		}
		return rowID;
	}
	/***
	 * 是否存在此群组信息
	 * 
	 * @param groupCode
	 * @return
	 * @throws Exception
	 */
	private boolean exitGroup(String groupCode, String groupName, String createDeviceCode) throws Exception {
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		sql.append(" and " + CREATE_DEVICE_CODE + "='" + groupCode + "'");
		sql.append(" and " + GROUP_NAME + "='" + groupName + "'");
		// System.out.println("ShareDataBase-SQL(exitGroup)=" + sql);
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			flag = true;
			break;
		}
		sql.setLength(0);
		sql = null;
		this.closeCursor(cursor);
		return flag;
	}
	private boolean exitGroup(String groupCode, String createDeviceCode) throws Exception {
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		sql.append(" and " + CREATE_DEVICE_CODE + "='" + groupCode + "'");
		// System.out.println("ShareDataBase-SQL(exitGroup)=" + sql);
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			flag = true;
			break;
		}
		sql.setLength(0);
		sql = null;
		this.closeCursor(cursor);
		return flag;
	}
	private void updateGroup(ShareGroupModel model) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("update " + TABLE_GROUP_NAME + " set ");
		sql.append(GROUP_NAME + "='" + model.getGroupName() + "'");
		sql.append(" where " + GROUP_CODE + "='" + model.getGroupCode() + "'");
		// System.out.println("ShareDataBase-SQL(updateGroup)=" + sql);
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql = null;
	}
	/***
	 * 保存或更新群组
	 * 
	 * @param model
	 * @throws Exception
	 */
	public void saveOrUpdateGroup(ShareGroupModel model) throws Exception {
		boolean flag = this.exitGroup(model.getGroupCode(), model.getGroupName(), model.getCreateDeviceCode());
		if (!flag) {
			this.insertGroup(model);
		} else {
			this.updateGroup(model);
		}
	}
	/***
	 * 是否存在设备关系数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean exitDeviceRelation(String deviceCode) throws Exception {
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append("where " + DEVICE_CODE + "='" + deviceCode + "'");
		sql.append(" and " + DEVICE_STATUS + "=" + ShareDeviceModel.STATUS_ADDED);
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			flag = true;
			break;
		}
		sql.setLength(0);
		sql = null;
		this.closeCursor(cursor);
		return flag;
	}
	/***
	 * 是否存在组关系数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean exitGroupRelation(String groupCode, String deviceCode) throws Exception {
		boolean flag = false;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_GROUP_RELATION_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		sql.append(" and " + DEVICE_CODE + "='" + deviceCode + "'");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		while (cursor.moveToNext()) {
			flag = true;
			break;
		}
		sql.setLength(0);
		sql = null;
		this.closeCursor(cursor);
		return flag;
	}
	/***
	 * 删除所有的群组及群组关系信息
	 */
	public void deleteGroup() {
		StringBuffer sql = new StringBuffer();
		sql.setLength(0);
		sql.append("delete from ");
		sql.append(TABLE_GROUP_NAME + " ");
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql.append("delete from ");
		sql.append(TABLE_GROUP_RELATION_NAME + " ");
		dataBase.getWritableDatabase().execSQL(sql.toString());
	}
	/***
	 * 删除群组
	 * 
	 * @param groupCode
	 * @param deviceCode
	 */
	public void deleteGroup(String groupCode) {
		StringBuffer sql = new StringBuffer();
		sql.setLength(0);
		sql.append("delete from ");
		sql.append(TABLE_GROUP_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		dataBase.getWritableDatabase().execSQL(sql.toString());
		sql.setLength(0);
		sql.append("delete from ");
		sql.append(TABLE_GROUP_RELATION_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		dataBase.getWritableDatabase().execSQL(sql.toString());
	}
	/***
	 * 删除群组关系
	 * 
	 * @param groupCode
	 * @param deviceCode
	 */
	public void deleteGroupRelation(String groupCode, String deviceCode) {
		StringBuffer sql = new StringBuffer();
		sql.setLength(0);
		sql.append("delete from ");
		sql.append(TABLE_GROUP_RELATION_NAME + " ");
		sql.append("where " + GROUP_CODE + "='" + groupCode + "'");
		sql.append(" and " + DEVICE_CODE + "='" + deviceCode + "'");
		dataBase.getWritableDatabase().execSQL(sql.toString());
	}
	/***
	 * 删除设备
	 * 
	 * @param deviceCode
	 *            设备码
	 */
	// public void deleteDevice(String deviceCode) {
	// StringBuffer sql = new StringBuffer();
	// sql.setLength(0);
	// sql.append("delete from ");
	// sql.append(TABLE_DEVICE_NAME + " ");
	// sql.append("where " + DEVICE_CODE + "='" + deviceCode + "'");
	// dataBase.getWritableDatabase().execSQL(sql.toString());
	// }
	/***
	 * 插入组关系数据
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public boolean insertGroupRelation(ShareGroupRelationModel model) throws Exception {
		boolean flag = this.exitGroupRelation(model.getGroupCode(), model.getDeviceCode());
		if (!flag) {
			ContentValues values = new ContentValues();
			values.put(GROUP_CODE, model.getGroupCode());
			values.put(DEVICE_CODE, model.getDeviceCode());
			dataBase.getWritableDatabase().insert(TABLE_GROUP_RELATION_NAME, null, values);
			flag = true;
		}
		return flag;
	}
	/***
	 * 查询组内所有成员信息
	 * 
	 * @param groupCode
	 *            组code
	 * @return
	 */
	public List<ShareDeviceModel> fetchDeviceByGroupCode(String groupCode) throws Exception {
		List<ShareDeviceModel> list = new LinkedList<ShareDeviceModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append("where " + DEVICE_CODE + " in (");
		sql.append(" select " + DEVICE_CODE + " from " + TABLE_GROUP_RELATION_NAME);
		sql.append(" where " + GROUP_CODE + "='" + groupCode + "')");
		// System.out.println("fetDeviceByGroupCode(SQL=)" + sql.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		sql.setLength(0);
		sql = null;
		while (cursor.moveToNext()) {
			ShareDeviceModel model = new ShareDeviceModel();
			initShareDeviceModel(model, cursor);
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	public ShareDeviceModel fetchDeviceByDeviceCode(String deviceCode) throws Exception {
		ShareDeviceModel model = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append("where " + DEVICE_CODE + " ='" + deviceCode + "'");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		sql.setLength(0);
		sql = null;
		while (cursor.moveToNext()) {
			model = new ShareDeviceModel();
			initShareDeviceModel(model, cursor);
			break;
		}
		this.closeCursor(cursor);
		return model;
	}
	public List<ShareDeviceModel> fetDeviceByGroupCode(List<String> groupCodes) throws Exception {
		List<ShareDeviceModel> list = new LinkedList<ShareDeviceModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append("where " + DEVICE_CODE + " in (");
		sql.append(" select " + DEVICE_CODE + " from " + TABLE_GROUP_RELATION_NAME);
		sql.append(" where " + GROUP_CODE + " in (");
		for (int i = 0; i < groupCodes.size(); i++) {
			sql.append("'" + groupCodes.get(i) + "'");
			if (i != groupCodes.size() - 1) {
				sql.append(",");
			}
		}
		sql.append("))");
		// System.out.println("fetDeviceByGroupCode(SQL=)" + sql.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		sql.setLength(0);
		sql = null;
		while (cursor.moveToNext()) {
			ShareDeviceModel model = new ShareDeviceModel();
			initShareDeviceModel(model, cursor);
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	/***
	 * 获取所有的设备数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ShareDeviceModel> fetAllDevice() throws Exception {
		List<ShareDeviceModel> list = new LinkedList<ShareDeviceModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append(" ORDER BY " + ID + " DESC");
		// System.out.println("fetDeviceByGroupCode(fetDeviceFromFile=)" +
		// sql.toString());
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		sql.setLength(0);
		sql = null;
		while (cursor.moveToNext()) {
			ShareDeviceModel model = new ShareDeviceModel();
			initShareDeviceModel(model, cursor);
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	/****
	 * 获取指定状态的所有设备
	 * 
	 * @param 状态
	 * @return
	 */
	public List<ShareDeviceModel> fetchAllDeviceByStatus(List<Integer> Status) {
		StringBuffer sb = new StringBuffer();
		List<ShareDeviceModel> list = new LinkedList<ShareDeviceModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME + " ");
		sql.append(" where " + DEVICE_STATUS + " in(");
		for (int i = 0; i < Status.size(); i++) {
			if (i == Status.size() - 1)
				sb.append(Status.get(i));
			else
				sb.append(Status.get(i) + ",");
		}
		sql.append(sb);
		sql.append(" )");
		sql.append(" GROUP BY " + DEVICE_CODE);
		sql.append(" ORDER BY " + CREATETIME + " DESC");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		sql.setLength(0);
		sql = null;
		while (cursor.moveToNext()) {
			ShareDeviceModel model = new ShareDeviceModel();
			initShareDeviceModel(model, cursor);
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	/***
	 * 获取所有的设备数据 模糊查询,匹配code和name
	 * 
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public List<ShareDeviceModel> fetAllDevice(String info, List<Integer> Status) throws Exception {
		List<ShareDeviceModel> list = new LinkedList<ShareDeviceModel>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ");
		sql.append(TABLE_DEVICE_NAME);
		sql.append(" where ");
		if (null != info && info.length() > 0) {
			sql.append(" (" + DEVICE_CODE + " like '%" + info + "%' or ");
			sql.append(DEVICE_NAME + " like '%" + info + "%')  and ");
		}
		sql.append(DEVICE_STATUS + " in(");
		for (int i = 0; i < Status.size(); i++) {
			if (i == Status.size() - 1)
				sql.append(Status.get(i));
			else
				sql.append(Status.get(i) + ",");
		}
		sql.append(" )");
		sql.append(" ORDER BY " + CREATETIME + " DESC");
		Cursor cursor = dataBase.getWritableDatabase().rawQuery(sql.toString(), null);
		sql.setLength(0);
		sql = null;
		while (cursor.moveToNext()) {
			ShareDeviceModel model = new ShareDeviceModel();
			initShareDeviceModel(model, cursor);
			list.add(model);
		}
		this.closeCursor(cursor);
		return list;
	}
	/***
	 * 根据指针初始化model
	 * 
	 * @param model
	 *            设备对象
	 * @param cursor
	 *            指针
	 */
	private void initShareDeviceModel(ShareDeviceModel model, Cursor cursor) {
		model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
		model.setDeviceCode(cursor.getString(cursor.getColumnIndex(DEVICE_CODE)));
		model.setDeviceName(cursor.getString(cursor.getColumnIndex(DEVICE_NAME)));
		model.setDeviceOS(cursor.getInt(cursor.getColumnIndex(DEVICE_OS)));
		model.setDeviceType(cursor.getString(cursor.getColumnIndex(DEVICE_TYPE)));
		model.setDeviceStatus(cursor.getInt(cursor.getColumnIndex(DEVICE_STATUS)));
		model.setDeviceMessage(cursor.getString(cursor.getColumnIndex(DEVICE_MESSAGE)));
		model.setCreateTime(cursor.getString(cursor.getColumnIndex(CREATETIME)));
	}
	/***
	 * 根据指针初始化model
	 * 
	 * @param model
	 *            文件对象
	 * @param cursor
	 *            指针
	 */
	private void initShareFileModel(ShareFileModel model, Cursor cursor) {
		model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
		model.setFileType(cursor.getInt(cursor.getColumnIndex(FILE_TYPE)));
		model.setFilePath(cursor.getString(cursor.getColumnIndex(FILE_PATH)));
		model.setFileName(cursor.getString(cursor.getColumnIndex(FILE_NAME)));
		model.setFileID(cursor.getString(cursor.getColumnIndex(FILE_ID)));
		model.setFileTotalSize(cursor.getInt(cursor.getColumnIndex(FILE_TOTAL_SIZE)));
		model.setFileDescribe(cursor.getString(cursor.getColumnIndex(FILE_DESCRIBE)));
		model.setSendOrReceive(cursor.getInt(cursor.getColumnIndex(SEND_OR_RECEIVE)));
		model.setFromDeviceCode(cursor.getString(cursor.getColumnIndex(FROM_DEVICE_CODE)));
		model.setFromGroupCode(cursor.getString(cursor.getColumnIndex(FROM_GROUP_CODE)));
		model.setTargetDeviceCodes(cursor.getString(cursor.getColumnIndex(TARGET_DEVICE_CODES)));
		model.setTargetGroupCodes(cursor.getString(cursor.getColumnIndex(TARGET_GROUP_CODES)));
		model.setFileStatus(cursor.getInt(cursor.getColumnIndex(FILE_STATUS)));
		model.setFileRealSize(cursor.getInt(cursor.getColumnIndex(FILE_REAL_SIZE)));
		model.setCreateTime(cursor.getString(cursor.getColumnIndex(CREATETIME)));
	}
}

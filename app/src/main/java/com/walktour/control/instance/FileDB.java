package com.walktour.control.instance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dinglicom.data.control.DataDictoryInit;
import com.dinglicom.data.control.DataTableStruct;
import com.dinglicom.data.control.DataTableStruct.DataBaseInfoEnum;
import com.dinglicom.data.control.DataTableStruct.DataTableMap;
import com.dinglicom.data.control.DataTableStruct.DictionaryEnum;
import com.dinglicom.data.control.DataTableStruct.TestRecordEnum;
import com.dinglicom.data.model.Dictionary;
import com.dinglicom.data.model.RecordAbnormal;
import com.dinglicom.data.model.RecordBase;
import com.dinglicom.data.model.RecordBuild;
import com.dinglicom.data.model.RecordDetail;
import com.dinglicom.data.model.RecordDetailUpload;
import com.dinglicom.data.model.RecordImg;
import com.dinglicom.data.model.RecordNetType;
import com.dinglicom.data.model.RecordTaskType;
import com.dinglicom.data.model.RecordTestInfo;
import com.dinglicom.data.model.TestRecord;
import com.walktour.Utils.StringUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyFileWriter;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.model.TotalMeasureModel;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 操作数据表 tbFileList,此表记录的数据是所有 "测试任务生成的文件"
 * 
 * */
public class FileDB extends SQLiteOpenHelper{
	
	public final static int VERSION_20150305 = 20150305;
	public final static int VERSION_20151113 = 20151113;
	public final static int VERSION_DICTORY	 = 20150714;

	private static final String TAG 			= "FileDB";
//	private static final String DATABASE_NAME 	= "DBTask";

	private static final String DATABASE_NAME 	= "walktour.db";
	
	private Context 		mContext;
	private DataTableStruct dbStruct = null;
	//静态实例
	private FileDB(Context context,String dbName,SQLiteDatabase.CursorFactory factory,int version){
		super(context,dbName,factory,version);
		this.mContext = context;
		dbStruct = new DataTableStruct();
	}
	private static FileDB sInstance;
	private static SQLiteDatabase db ;
	public synchronized static FileDB getInstance(Context context){
		if(sInstance ==null){
			sInstance =new FileDB(context,DATABASE_NAME,null,VERSION_20151113 );
			db = sInstance.getWritableDatabase();
		}
		return sInstance;
	}
	
	@Override
	/**onCreate*/
	public void onCreate(SQLiteDatabase db) {
		//数据库没有表时创建表
		try{
			
			//数据管理表
			db.execSQL(dbStruct.getCreateTableStr(DataTableStruct.database_info,DataTableStruct.DataBaseInfoEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.Dictionary.getTableName(),DataTableStruct.DictionaryEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.TestRecord.getTableName(),DataTableStruct.TestRecordEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordDetail.getTableName(),DataTableStruct.RecordDetailEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordDetailUpload.getTableName(),DataTableStruct.RecordUploadEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordBuild.getTableName(),DataTableStruct.RecordBuildEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordTaskType.getTableName(),DataTableStruct.RecordTaskTypeEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordAbnormal.getTableName(),DataTableStruct.RecordAbnormalEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordNetType.getTableName(),DataTableStruct.RecordNetTypeEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordImg.getTableName(),DataTableStruct.RecordImgEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableMap.RecordTestInfo.getTableName(),DataTableStruct.RecordInfoEnum.values())); 

			
			//实时统计表
			db.execSQL(dbStruct.getCreateTableStr(DataTableStruct.tb_TotalUnifyTimes,DataTableStruct.TotalUnifyTimsEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableStruct.tb_TotalSpecialTimes,DataTableStruct.TotalSpecialTimesEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableStruct.tb_TotalPara,DataTableStruct.TotalParaEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableStruct.tb_TotalMeasurePara,DataTableStruct.TotalMeasureEnum.values())); 
			db.execSQL(dbStruct.getCreateTableStr(DataTableStruct.tb_TotalEvent,DataTableStruct.TotalEventEnum.values())); 
			
			initDatabaseInfo(db);
			initDictoryInfo(db);
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.w(TAG,"--oldVersion:" + oldVersion + "--newVersion:" + newVersion);
		// 从数据库基础表中获得当前字典版本信息,如果存储版本信息与当前定义的时间不一致,需要清空数据字典并重新初始化
		if( oldVersion != newVersion ){
			//0305的版本与当前版本相比差主表的go_or_nogo字段,此处需要添加该字段
			if(oldVersion == VERSION_20150305){
				db.execSQL("alter table " + DataTableMap.TestRecord.getTableName() + " add COLUMN " + TestRecordEnum.go_or_nogo.name() + " INTEGER");
			}
		}
	}
	
	/**
	 * 关闭数据库
	 * */
	public void close(){
		sInstance.close();
	}
	
	/**
	 * 初始化数据库版本信息
	 */
	private void initDatabaseInfo(SQLiteDatabase db){
		String clean = "delete from " + DataTableStruct.database_info;
		db.execSQL(clean);
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataBaseInfoEnum.version.name(),			VERSION_20150305);
		contentValues.put(DataBaseInfoEnum.dictory_version.name(),	VERSION_DICTORY);
		contentValues.put(DataBaseInfoEnum.imei.name(),				MyPhoneState.getInstance().getDeviceId(mContext));
		contentValues.put(DataBaseInfoEnum.mac_address.name(),		MyPhoneState.getInstance().getLocalMacAddress(mContext));
		contentValues.put(DataBaseInfoEnum.phone_model.name(),		android.os.Build.MODEL);
		
		db.insert(DataTableStruct.database_info, null, contentValues);
	}
	
	/**
	 * 初始化数据库字典信息
	 */
	private void initDictoryInfo(SQLiteDatabase db){
		String clean = "delete from " + DataTableMap.Dictionary.getTableName();
		db.execSQL(clean);
		
		//当需要初始化数据字典时,重新从包装包拷贝字典文件
		File desFile = AppFilePathUtil.getInstance().getAppConfigFile("data_dictionary.xml");
		ArrayList<ArrayList<Dictionary>> dictorys = new DataDictoryInit().initDictory(desFile);
		for(ArrayList<Dictionary> dictory : dictorys){
			for(Dictionary dic : dictory){
				ContentValues contentValues = new ContentValues();
				contentValues.put(DictionaryEnum.dictionary_type.name(),dic.dictionary_type);
				contentValues.put(DictionaryEnum.type_key.name(), 		dic.type_key);
				contentValues.put(DictionaryEnum.type_name_zh.name(), 	dic.type_name_zh);
				contentValues.put(DictionaryEnum.type_name_en.name(), 	dic.type_name_en);
				
				db.insert(DataTableMap.Dictionary.getTableName(), null, contentValues);
			}
		}
	}
	
	/**
	 * 获得数据对象列表
	 * @param cls
	 * @param sql
	 * @return
	 * 
	 * @author Tangwq
	 */
	private Object getDataObjectList(Class<?> cls,String sql){
		ArrayList<Object> objList = new ArrayList<Object>();
		try{
			Cursor cursor = db.rawQuery(sql, null );
			while(cursor.moveToNext()){
				Object testRecord = cls.newInstance();
				Field[] fields = cls.getDeclaredFields();
				for(Field field : fields){
					//如果定义属性是private的,则不能直接从数据库查询结果中获得所需结果.protected可直接从数据库获取结果,但值不写入数据库,public可直接获取写入数据库中
					if(!Modifier.isPrivate(field.getModifiers())){
						field.setAccessible(true);
						field.set(testRecord, convertValueType(field.getGenericType().toString(),
								cursor.getString( cursor.getColumnIndex(field.getName()))));
					}
				}
				
				objList.add(testRecord);
			}
			cursor.close();
			
		}catch(Exception e){
			LogUtil.w(TAG,"getDataObjectList",e);
		}
		
		return objList;
	}
	
	/**
	 * 构建数据管理记录
	 * 
	 * @param wheres
	 * @return
	 * 
	 * @author Tangwq
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<TestRecord> buildTestRecordList(HashMap<String, ArrayList<String>> wheres){
		
		try{
			LogUtil.w(TAG,"testRecord sql-----:[" + dbStruct.getMainRecordSql(mContext,wheres) + "]");
			ArrayList<TestRecord> objs = (ArrayList<TestRecord>)getDataObjectList(TestRecord.class,dbStruct.getMainRecordSql(mContext,wheres));
			ArrayList<TestRecord> testRecords = new ArrayList<TestRecord>();
			for(TestRecord obj : objs){
				String detailSql = "select *,(select type_name_en from data_dictionary where type_key=file_type)file_type_str from data_record_detail where record_id ='" + obj.record_id + "'";
				ArrayList<RecordDetail> recordDetails = (ArrayList<RecordDetail>) getDataObjectList(RecordDetail.class,detailSql);
				//此处需要重新每个文件的每个服务器上传状态
				printModelListStr(recordDetails,RecordDetail.class);
				obj.setRecordDetails(recordDetails);
				for (RecordDetail recordDetail : recordDetails) {
					String uploadStateSql = "select * from data_record_upload_detail where detail_id='" + recordDetail.detail_id + "'";
					ArrayList<RecordDetailUpload> recordDetailUploads = (ArrayList<RecordDetailUpload>) getDataObjectList(RecordDetailUpload.class, uploadStateSql);
					recordDetail.setDetailUploads(recordDetailUploads);
				}
				String taskTypeSql = "select *,(select type_name_en from data_dictionary where type_key=task_type)task_type_str from data_record_tasktype where record_id ='" + obj.record_id + "'";
				ArrayList<RecordTaskType> recordTaskTypes = (ArrayList<RecordTaskType>) getDataObjectList(RecordTaskType.class,taskTypeSql);
				//printModelListStr(recordTaskTypes,RecordTaskType.class);
				obj.setRecordTaskTypes(recordTaskTypes);
				
				String abnormalSql = "select *,(select type_name_en from data_dictionary where type_key=abnormal_type)abnormal_type_str from data_record_abnormal where record_id ='" + obj.record_id + "'";
				ArrayList<RecordAbnormal> abnormalTypes = (ArrayList<RecordAbnormal>) getDataObjectList(RecordAbnormal.class,abnormalSql);
				//printModelListStr(abnormalTypes,RecordAbnormal.class);
				obj.setRecordAbnormals(abnormalTypes);
				
				String netTypeSql = "select *,(select type_name_en from data_dictionary where type_key=net_type)net_type_str from data_record_nettype where record_id ='" + obj.record_id + "'";
				ArrayList<RecordNetType> recordNetTypes = (ArrayList<RecordNetType>) getDataObjectList(RecordNetType.class,netTypeSql);
				//printModelListStr(recordNetTypes,RecordNetType.class);
				obj.setRecordNetTypes(recordNetTypes);
				
				String testInfoSql = "select * from data_test_info where task_no ='" + obj.task_no + "'";
				ArrayList<RecordTestInfo> recordTestInfo = (ArrayList<RecordTestInfo>) getDataObjectList(RecordTestInfo.class,testInfoSql);
				//printModelListStr(recordTestInfo,RecordTestInfo.class);
				obj.setRecordTestInfo(recordTestInfo);
				
				testRecords.add(obj);
			}
//			printModelListStr(testRecords,TestRecord.class);//该方法只是打印日志，会导致ANR，先屏蔽
			return testRecords;
		}catch(Exception e){
			LogUtil.w(TAG,"buildTestRecordList",e);
		}
		return null;
	}
	// TEST BEGIN MSI ====================================================================================
	
	/**
	 * 获取异常类型列表
	 * @return
	 */
	public ArrayList<RecordAbnormal> getRecordAbnormals() {
		String abnormalSql = "select distinct abnormal_type,(select type_name_en from data_dictionary where type_key=abnormal_type)abnormal_type_str from data_record_abnormal";
		ArrayList<RecordAbnormal> abnormalTypes = new ArrayList<RecordAbnormal>();
		Cursor cursor = db.rawQuery(abnormalSql, null );
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
		{
			RecordAbnormal item = new RecordAbnormal();
		    int abnormal_typeColumn = cursor.getColumnIndex("abnormal_type");
		    int abnormal_type_nameColumn = cursor.getColumnIndex("abnormal_type_str");
		    String abnormal_type = cursor.getString(abnormal_typeColumn);
		    String abnormal_type_name = cursor.getString(abnormal_type_nameColumn);
		    item.abnormal_type = Integer.parseInt(abnormal_type);
		    item.setAbnormal_type_str(abnormal_type_name);
		    abnormalTypes.add(item);
		}
		cursor.close();
		return abnormalTypes;
	}
	
	/**
	 * 获取业务类型列表
	 * @return
	 */
	public ArrayList<RecordTaskType> getRecordTaskTypes() {
		ArrayList<RecordTaskType> recordTaskTypes = new ArrayList<RecordTaskType>();
		String taskTypeSql = "select distinct task_type,(select type_name_en from data_dictionary where type_key=task_type)task_type_str from data_record_tasktype";
		Cursor cursor = db.rawQuery(taskTypeSql, null );
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
		{
			RecordTaskType item = new RecordTaskType();
		    int task_typeColumn = cursor.getColumnIndex("task_type");
		    int task_type_nameColumn = cursor.getColumnIndex("task_type_str");
		    String task_type = cursor.getString(task_typeColumn);
		    String task_type_name = cursor.getString(task_type_nameColumn);
		    item.task_type = Integer.parseInt(task_type);
		    item.setTask_type_str(task_type_name);
		    recordTaskTypes.add(item);
		}
		cursor.close();
		return recordTaskTypes;
	}
	
	/**
	 * 获取建筑物、工单等列表
	 * @return
	 */
	public ArrayList<RecordBuild> getRecordBuilds(int type_sence) {
		ArrayList<RecordBuild> recordBuilds = new ArrayList<RecordBuild>();
		try {
			String recordBuildSql = "SELECT * FROM data_record_build WHERE substr( node_id, 1, 4 ) IN (SELECT substr( node_id, 1, 4 ) FROM data_test_record WHERE type_scene = '" + type_sence + "' AND node_id != 'null' )";
			Cursor cursor = db.rawQuery(recordBuildSql, null);
			while (cursor.moveToNext()) {
				RecordBuild item = new RecordBuild();
				int node_id_column = cursor.getColumnIndex("node_id");
				int parent_id_column = cursor.getColumnIndex("parent_id");
				int node_name_column = cursor.getColumnIndex("node_name");
				int node_info_column = cursor.getColumnIndex("node_info");
				item.node_id = cursor.getString(node_id_column);
				item.parent_id = cursor.getString(parent_id_column);
				item.node_name = cursor.getString(node_name_column);
				item.node_info = cursor.getString(node_info_column);
				recordBuilds.add(item);
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordBuilds",e);
		}
		return recordBuilds;
	} 
	
	/***
	 * 地铁模式下获取对应场景ID下的做过测试的所有城市，返回的列表中包含重复的城市
	 * @param type_sence
	 * @return
	 */
	public ArrayList<RecordTestInfo> getRecordTestInfoCitys(int type_sence) {
		ArrayList<RecordTestInfo> recordTestInfos = new ArrayList<RecordTestInfo>();
		try {
			String recordBuildSql = "SELECT * FROM data_test_info WHERE task_no IN (SELECT task_no FROM data_test_record WHERE type_scene = '" + type_sence + "' AND key_info = 'city' )";
			System.out.println("recordBuildSql="+recordBuildSql);
			Cursor cursor = db.rawQuery(recordBuildSql, null);
			while (cursor.moveToNext()) {
				RecordTestInfo item = new RecordTestInfo();
				item.task_no = cursor.getString(cursor.getColumnIndex("task_no"));
				item.key_info = cursor.getString(cursor.getColumnIndex("key_info"));
				item.key_value = cursor.getString(cursor.getColumnIndex("key_value"));
				recordTestInfos.add(item);
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordTestInfos",e);
		}
		return recordTestInfos;
		
	}
	
	/***
	 * 获取所有的线路信息
	 * @param taskNos 任务IDS
	 * @return
	 */
	public ArrayList<RecordTestInfo> getRecordTestInfoLines(List<String> taskNos,String keyInfo) {
		ArrayList<RecordTestInfo> recordTestInfos = new ArrayList<RecordTestInfo>();
		try {
			StringBuffer sql=new StringBuffer();
			sql.append(" select * from data_test_info where task_no in ( ");
			for(int i=0;i<taskNos.size();i++){
				sql.append("\'").append(taskNos.get(i)).append("\'");
				if(i<taskNos.size()-1)
					sql.append(",");
			}
			sql.append(" ) and key_info=\'");
			sql.append(keyInfo);
			sql.append("\'");
			sql.append(" group by key_value");
			System.out.println("getRecordTestInfoLines="+sql.toString());
			Cursor cursor = db.rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
				RecordTestInfo item = new RecordTestInfo();
				item.task_no = cursor.getString(cursor.getColumnIndex("task_no"));
				item.key_info = cursor.getString(cursor.getColumnIndex("key_info"));
				item.key_value = cursor.getString(cursor.getColumnIndex("key_value"));
				recordTestInfos.add(item);
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordTestInfos",e);
		}
		return recordTestInfos;
	}
	
	/***
	 * 地铁模式下的获取制定城市中选择线路的任务所有ID号
	 * @param city 选择的城市
	 * @param lines 选择城市的线路,以逗号分割的路线
	 * @return IDS
	 */
	public List<String> getRecordTestInfoTaskNo(String city,String lines){
		List<String> taskNos=new LinkedList<String>();
		try {
			StringBuffer sql=new StringBuffer();
			sql.append("SELECT * from data_test_info where (key_info='city' and key_value='");
			sql.append(city+"')");
			sql.append(" or (key_info='metro_line' and key_value in (");
			String[] linesArray=lines.split(",");
			for(int i=0;i<linesArray.length;i++){
				sql.append("'"+linesArray[i]+"'");
				if(i<linesArray.length-1)
					sql.append(",");
			}
			
			sql.append("))");
			System.out.println("recordBuildSql="+sql.toString());
			ArrayList<RecordTestInfo> recordTestInfos = new ArrayList<RecordTestInfo>();
			Cursor cursor = db.rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
				RecordTestInfo item = new RecordTestInfo();
				item.task_no = cursor.getString(cursor.getColumnIndex("task_no"));
				item.key_info = cursor.getString(cursor.getColumnIndex("key_info"));
				item.key_value = cursor.getString(cursor.getColumnIndex("key_value"));
				recordTestInfos.add(item);
			}
			ArrayList<RecordTestInfo> left = new ArrayList<RecordTestInfo>();
			ArrayList<RecordTestInfo> right = new ArrayList<RecordTestInfo>();
			for(RecordTestInfo r:recordTestInfos){
				if(r.key_info.equals("city")){
					left.add(r);
				}else if(r.key_info.equals("metro_line")){
					right.add(r);
				}
			}
			
			for(RecordTestInfo l:left){
				for(RecordTestInfo r:right){
					if(l.task_no.equals(r.task_no)){
						taskNos.add(l.task_no);
					}
				}
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordTestInfos",e);
		}
		return taskNos;
	}
	
	/***
	 * 高铁模式下的获取制定城市中选择线路的任务所有ID号
	 * @param lines 选择城市的线路,以逗号分割的路线
	 * @return IDS
	 */
	public List<String> getRecordTestInfoTaskNo(String lines){
		List<String> taskNos=new LinkedList<String>();
		try {
			StringBuffer sql=new StringBuffer();
			sql.append("SELECT * from data_test_info where ");
			sql.append(" key_info='high_speed_rail' and key_value in (");
			String[] linesArray=lines.split(",");
			for(int i=0;i<linesArray.length;i++){
				sql.append("'"+linesArray[i]+"'");
				if(i<linesArray.length-1)
					sql.append(",");
			}
			sql.append(")");
			System.out.println("getRecordTestInfoTaskNo="+sql.toString());
			Cursor cursor = db.rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
//				RecordTestInfo item = new RecordTestInfo();
//				item.task_no = cursor.getString(cursor.getColumnIndex("task_no"));
//				item.key_info = cursor.getString(cursor.getColumnIndex("key_info"));
//				item.key_value = cursor.getString(cursor.getColumnIndex("key_value"));
				taskNos.add(cursor.getString(cursor.getColumnIndex("task_no")));
			}
			
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordTestInfos",e);
		}
		return taskNos;
	}
	/***
	 * 高铁模式下获取对应场景ID下的做过测试的所有线路，去除相同的线路
	 * @param type_sence
	 * @return
	 */
	public ArrayList<RecordTestInfo> getRecordTestInfoLines(int type_sence) {
		ArrayList<RecordTestInfo> recordTestInfos = new ArrayList<RecordTestInfo>();
		try {
			String recordBuildSql = "SELECT * FROM data_test_info WHERE task_no IN (SELECT task_no FROM data_test_record WHERE type_scene = '" + type_sence + "' AND key_info = 'high_speed_rail' ) group by key_value";
			System.out.println("getRecordTestInfoLines="+recordBuildSql);
			Cursor cursor = db.rawQuery(recordBuildSql, null);
			while (cursor.moveToNext()) {
				RecordTestInfo item = new RecordTestInfo();
				item.task_no = cursor.getString(cursor.getColumnIndex("task_no"));
				item.key_info = cursor.getString(cursor.getColumnIndex("key_info"));
				item.key_value = cursor.getString(cursor.getColumnIndex("key_value"));
				recordTestInfos.add(item);
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordTestInfos",e);
		}
		return recordTestInfos;
		
	}

	/***
	 * 单站验证模式下获取对应场景ID下的做过测试的所有基站，去除相同的基站
	 * @param type_sence
	 * @return
	 */
	public ArrayList<RecordTestInfo> getRecordTestInfoStations(int type_sence) {
		ArrayList<RecordTestInfo> recordTestInfos = new ArrayList<RecordTestInfo>();
		try {
			String recordBuildSql = "SELECT * FROM data_test_info WHERE task_no IN (SELECT task_no FROM data_test_record WHERE type_scene = '" + type_sence + "' AND key_info = 'single_station' ) group by key_value";
			System.out.println("getRecordTestInfoStations="+recordBuildSql);
			Cursor cursor = db.rawQuery(recordBuildSql, null);
			while (cursor.moveToNext()) {
				RecordTestInfo item = new RecordTestInfo();
				item.task_no = cursor.getString(cursor.getColumnIndex("task_no"));
				item.key_info = cursor.getString(cursor.getColumnIndex("key_info"));
				item.key_value = cursor.getString(cursor.getColumnIndex("key_value"));
				recordTestInfos.add(item);
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"getRecordTestInfoStations",e);
		}
		return recordTestInfos;

	}
	/**
	 * 将传入的对象列表转换成指定类对象的键值对
	 * @param cls
	 * @return
	 * 
	 * @author Tangwq
	 */
	public void printModelListStr(Object obj,Class<?> cls){
		try{
			@SuppressWarnings("unchecked")
			ArrayList<Object> objs = (ArrayList<Object>) obj;
			for(Object record: objs){
				StringBuffer sb = new StringBuffer();
				Field[] fields = cls.getDeclaredFields();
				for(Field field : fields){
					if(!Modifier.isPrivate(field.getModifiers())){
						field.setAccessible(true);
						sb.append(field.getName());
						sb.append("=");
						sb.append(field.get(record));
						sb.append(";");
					}
				}
				LogUtil.w(TAG,sb.toString());
				//sb.append("\n\r");
			}
		}catch(Exception e){
			LogUtil.w(TAG,"buildTestRecordList",e);
		}
		//return sb.toString();
	}
	
	private Object convertValueType(String type,String value){
		//LogUtil.w(TAG, "--Record name:" + fieldName + "--type:" + type + "--value:" + value);
		
		if(value == null || value.equals("null")){
			value = "0";
		}
		
		if(type.equals("int")){
			return Integer.parseInt(value);
		}else if(type.equals("long")){
			return Long.parseLong(value);
		}else if(type.equals("double")){
			return Double.parseDouble(value);
		}else{
			return value;
		}
	}
	
	/**
	 * 同步测试数据,将测试数据添加或更新到数据表
	 * @param record
	 * 
	 * @author Tangwq
	 */
	public synchronized long syncTestRecord(TestRecord record){
		long rowId = syncToTable(record);
		
		for(RecordAbnormal abnormal : record.getRecordAbnormals()){
			syncToTable(abnormal);
		}
		for(RecordDetail detail : record.getRecordDetails()){
			syncToTable(detail);
			
			//将上传明细信息入库
			for(RecordDetailUpload upload : detail.getDetailUploads()){
				syncToTable(upload);
			}
		}
		for(RecordNetType nettype : record.getRecordNetTypes()){
			syncToTable(nettype);
		}
		for(RecordTaskType tasktype : record.getRecordTaskTypes()){
			syncToTable(tasktype);
		}
		for(RecordTestInfo testInfo : record.getRecordTestInfo()){
			syncToTable(testInfo);
		}
		
		return rowId;
	}
	
	/**
	 * 
	 * @param obj
	 * 
	 * @return
	 * @author Tangwq
	 */
	public long syncToTable(RecordBase obj){
		long rowId = 0;
		try{
			String className = obj.getClass().getSimpleName();
			DataTableMap tableMap = DataTableMap.valueOf(className);
			Class<? extends RecordBase> cls = obj.getClass();
			
			//主键列表中,存在联合主键的问题,多主键以","分割
			String[] primaryKeys = tableMap.getPrimaryKey().split(",");
			StringBuffer sbWhere = new StringBuffer();
			String[] updateValue = new String[primaryKeys.length];
			StringBuffer upWhere = new StringBuffer();
			
			for(int i = 0; i < primaryKeys.length; i++){
				if(!"".equals(primaryKeys[i])){
					Field primaryField = cls.getDeclaredField(primaryKeys[i]);
					primaryField.setAccessible(true);
					
					sbWhere.append("and ");
					sbWhere.append(primaryKeys[i] + " = '" + primaryField.get(obj) + "' ");
					
					//此处用于保存下面修改改记录时的关键条件
					upWhere.append("and " + primaryKeys[i] + "= ? ");
					updateValue[i] = primaryField.get(obj).toString();
				}
			}
			String sqlFmt = "select * from %s %s";
			String where  = sbWhere.length() > 0 ?  (" where " + sbWhere.substring(4)) : "";
			
			String querySql = String.format(sqlFmt, tableMap.getTableName(),where);
			Cursor cursor = db.rawQuery(querySql, null);
			
			ContentValues contentValues = new ContentValues();
			Field[] fields = obj.getClass().getDeclaredFields();
			for(Field field : fields){
				field.setAccessible(true);
				//只有属性为public的值,才需要更新到数据库中
//				if(Modifier.isPublic(field.getModifiers()) && field.get(obj) != null){
//				LogUtil.w("MyTAG",field.getName());
				if(this.isColumn(obj,field.getName())){
					if(field.get(obj) != null) {
						Log.w(TAG, "--key:" + field.getName() + "--Value:" + field.get(obj));
						if (!field.getName().equals(tableMap.getPrimaryKey())
								|| !cls.getField(tableMap.getPrimaryKey()).get(obj).toString().equals("0")) {
							contentValues.put(field.getName(), field.get(obj).toString());
						}
					}
				}
			}
			
			//如果前面的查询记录存在,那么当前执行更新,否则插入
			if(cursor.getCount() > 0){
				db.update(tableMap.getTableName(), contentValues,( upWhere.length() > 4 ? upWhere.substring(4) : ""), 
						upWhere.length() > 4 ? updateValue : new String[]{});
			}else{
				rowId = db.insert(tableMap.getTableName(), null, contentValues);
			}
			
			cursor.close();
		}catch(Exception e){
			Log.w(TAG,e);
		}
		
		return rowId;
	}
	/**
	 * 数据库总是否存在此列
	 * @param obj
	 * @return
	 */
	private boolean isColumn(RecordBase obj, String columnName) {
		boolean isFlag = false;
		String[] columnNames = null;
		if (obj instanceof TestRecord) {
			columnNames = DataTableStruct.TestRecordEnum.getTableColumnNames();
		} else if (obj instanceof RecordDetail) {
			columnNames = DataTableStruct.RecordDetailEnum.getTableColumnNames();
		} else if (obj instanceof RecordAbnormal) {
			columnNames = DataTableStruct.RecordAbnormalEnum.getTableColumnNames();
		} else if (obj instanceof RecordDetailUpload) {
			columnNames = DataTableStruct.RecordUploadEnum.getTableColumnNames();
		} else if (obj instanceof RecordNetType) {
			columnNames = DataTableStruct.RecordNetTypeEnum.getTableColumnNames();
		} else if (obj instanceof RecordTaskType) {
			columnNames = DataTableStruct.RecordTaskTypeEnum.getTableColumnNames();
		} else if (obj instanceof RecordTestInfo) {
			columnNames = DataTableStruct.RecordInfoEnum.getTableColumnNames();
		} else if (obj instanceof RecordBuild) {
			columnNames = DataTableStruct.RecordBuildEnum.getTableColumnNames();
		} else if (obj instanceof RecordImg) {
			columnNames = DataTableStruct.RecordImgEnum.getTableColumnNames();
		}
		if (columnNames != null) {
			for (String column : columnNames) {
				if (null!=column&&column.toString().equals(columnName)) {
					isFlag = true;
					break;
				}
			}
		}
		return isFlag;
	}
	
	public void deleteFormTable(TestRecord testRecord) {
		String record_id = testRecord.record_id;
		try {
			
			String sql_delete_data_test_record = "DELETE FROM data_test_record WHERE record_id = '" + record_id + "'";
			String sql_delete_data_record_detail = "DELETE FROM data_record_detail WHERE record_id = '" + record_id + "'";
			String sql_delete_data_record_abnormal = "DELETE FROM data_record_abnormal WHERE record_id = '" + record_id + "'";
			String sql_delete_data_record_nettype = "DELETE FROM data_record_nettype WHERE record_id = '" + record_id + "'";
			String sql_delete_data_record_tasktype = "DELETE FROM data_record_tasktype WHERE record_id = '" + record_id + "'";
			
			String sql_where = "";
			String sql_delete_data_record_detail_upload = "";
			if (testRecord.getRecordDetails() == null || testRecord.getRecordDetails().size() == 0) {
				sql_delete_data_record_detail_upload = "";
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("WHERE")
					.append(" ")
					.append("detail_id")
					.append(" ")
					.append("IN").append("(");
				for (RecordDetail recordDetail : testRecord.getRecordDetails()) {
					sb.append("'")
						.append(recordDetail.detail_id)
						.append("'")
						.append(",");
				}
				String tmp = sb.toString();
				tmp = tmp.substring(0, tmp.lastIndexOf(","));
				sql_where = tmp + ")";
				sql_delete_data_record_detail_upload = "DELETE FROM data_record_upload_detail " + sql_where;
			}

			System.out.println("sql_delete_data_record_detail_upload:" + sql_delete_data_record_detail_upload);
			if (!sql_delete_data_record_detail_upload.equals("")) {
				db.execSQL(sql_delete_data_record_detail_upload);
			}
			db.execSQL(sql_delete_data_test_record);
			db.execSQL(sql_delete_data_record_detail);
			db.execSQL(sql_delete_data_record_abnormal);
			db.execSQL(sql_delete_data_record_nettype);
			db.execSQL(sql_delete_data_record_tasktype);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, e.getMessage(), e.fillInStackTrace());
		}
	}
	
	/**
	 * 删除指定对象的统一方法
	 * @param obj
	 * 
	 * @author Tangwq
	 */
	public void deleteRecord(RecordBase obj){
		try {
			String className = obj.getClass().getSimpleName();
			DataTableMap tableMap = DataTableMap.valueOf(className);
			Class<? extends RecordBase> cls = obj.getClass();
			
			String existSql = "DELETE FROM %s where %s ='%s'";
			Field primaryField = cls.getDeclaredField(tableMap.getPrimaryKey());
			primaryField.setAccessible(true);
			String deleteSql = String.format(existSql, tableMap.getTableName(),tableMap.getPrimaryKey(),primaryField.get(obj));
		
			db.execSQL(deleteSql);
		} catch (Exception e) {
			LogUtil.w(TAG,"deleteFromTable",e);
		}
	}

	//***********************************************************************************************************
	
	/**
	 * 获取data_record_build中的node_id最大值
	 * @return
	 */
	public int getMaxNodeId(String parent_id) {
		String sql = "SELECT max(node_id) FROM data_record_build where parent_id='" + parent_id + "'";
		try {
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getColumnIndex("max(node_id)");
				String node_id = cursor.getString(id);
				return Integer.parseInt(node_id);
			}
		} catch (Exception e) {
		}
		return 0;
	}
	
	public String getNodeId(String node_name, String parent_id) {
		String sql = "select node_id from data_record_build where node_name='" + node_name + "'" + " and parent_id='" + (StringUtil.isEmpty(parent_id)?"0":parent_id) + "'";
		try {
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getColumnIndex("node_id");
				String node_id = cursor.getString(id);
				return node_id;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取节点名字
	 * @param nodeId
	 * @return
	 */
	public String getNodeName(String nodeId) {
		String sql = "select node_name from data_record_build where node_id='" + nodeId + "'";
		try {
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getColumnIndex("node_name");
				String node_name = cursor.getString(id);
				return node_name;
			}
		} catch (Exception e) {
		}
		return "";
	}
	
	/**
	 * 删除建筑物、楼层、楼层图
	 * @param node_id
	 */
	public void deleteBuild(String node_id) {
		String sql = "DELETE FROM data_record_build WHERE substr(node_id, 1, 4)='" + node_id + "'";
		String delete_img_sql = "DELETE FROM data_record_img WHERE substr(node_id, 1, 4) = '" + node_id + "'";
		try {
			db.execSQL(sql);
			db.execSQL(delete_img_sql);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 删除楼层、楼层图
	 * @param node_id
	 */
	public void deleteFloor(String node_id) {
		String sql = "DELETE FROM data_record_build WHERE node_id = '" + node_id + "'";
		String delete_img_sql = "DELETE FROM data_record_img WHERE node_id = '" + node_id + "'";
		try {
			db.execSQL(sql);
			db.execSQL(delete_img_sql);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 获取楼层地图信息
	 * @param node_id
	 * @return
	 */
	public ArrayList<RecordImg> buildRecordImgList(String node_id) {
		ArrayList<RecordImg> result = new ArrayList<RecordImg>();
		String sql = "select * from data_record_img where node_id='" + node_id + "'";
		try {
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				RecordImg item = new RecordImg();
				int img_id_column = cursor.getColumnIndex("img_id");
				int node_id_column = cursor.getColumnIndex("node_id");
				int img_path_column = cursor.getColumnIndex("img_path");
				int img_name_column = cursor.getColumnIndex("img_name");
				item.setImgId(cursor.getInt(img_id_column));
				item.node_id = cursor.getString(node_id_column);
				item.img_path = cursor.getString(img_path_column);
				item.img_name = cursor.getString(img_name_column);
				result.add(item);
			}
		} catch (Exception e) {
			LogUtil.w(TAG,"buildRecordImgList",e);
		}

		return result;
	}
	
	public void deleteRecordImg(RecordImg recordImg) {
		String sql = "delete from data_record_img where img_id='" + recordImg.getImgId() + "'" + " and node_id='" + recordImg.node_id + "'";
		try {
			db.execSQL(sql);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 判断建筑物楼层等是否存在
	 * @param node_name
	 * @param parent_id
	 * @return
	 */
	public boolean isExist(String node_name, String parent_id) {
		String sql = "select count(*) from data_record_build where node_name='" + node_name + "' and parent_id='" + parent_id + "'";
		try {
			Cursor cursor = db.rawQuery(sql, null);
			cursor.moveToFirst();
			if (cursor.getInt(0) > 0) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * 获取最大文件分割序号
	 * @param task_no
	 * @return
	 */
	public int getMaxFileSplitId(String task_no) {
		String sql = "SELECT max(file_split_id) FROM data_test_record WHERE task_no = '" + task_no + "'";
		try {
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getColumnIndex("max(file_split_id)");
				String file_split_id = cursor.getString(id);
				return Integer.parseInt(file_split_id);
			}
		} catch (Exception e) {
		}
		return 0;
	}
	
	/**
	 * 将当前语音拨打,FTP,WAP等与网络无关的相关统计结果存入该表中
	 * @return
	 */
	public synchronized long insertTotalUnifyTimes(int mainId,int testType,Iterator<?> itData){
		int count = 0;
		try{
			while(itData.hasNext()){
				Entry<?, ?> ent = (Entry<?, ?>)itData.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("_mainId", mainId);
				contentValues.put("_testType", testType);
				contentValues.put("_nettype", 1);
				contentValues.put("_keyname", ent.getKey().toString());
				contentValues.put("_keyvalue", ent.getValue().toString());
				contentValues.put("_scale", 1);
				contentValues.put("_back1", "");
				
				long num =db.insert(DataTableStruct.tb_TotalUnifyTimes,null,contentValues);
				Log.e(TAG,"--unify-main:"+mainId+"--testType:"+testType+"--name:"+ent.getKey()+"--v:"+ent.getValue()+"--num:"+num);
				count++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	
	/**
     * 将HTTP，PING等有特殊需求业务的相关统计结果存入该表中
     * @return
     */
    @SuppressWarnings("unchecked")
    public synchronized long insertTotalSpecialTimes(int mainId,int testType,Iterator<?> itData){
        int count = 0;
        try{
            while(itData.hasNext()){    //第一层，HTTP键值为HTTP LOGON/REF/DOWN，PING为GSM，WCDMA
                Entry<?, ?> ent = (Entry<?, ?>)itData.next();
                String mainKey1 = ent.getKey().toString();
                Iterator<?> main1It = ((HashMap<String, Object>)ent.getValue()).entrySet().iterator();
                while(main1It.hasNext()){   //第二层 HTTP键值 为URL地址，PING为TBF-OPEN/CLOSE等
                    Entry<?, ?> mani1Ent = (Entry<?, ?>)main1It.next();
                    String mainKey2 = mani1Ent.getKey().toString();
                    Iterator<?> main2It = ((HashMap<String, Object>)mani1Ent.getValue()).entrySet().iterator();
                    while(main2It.hasNext()){   //第三层，为原来的次数，时延等键值对
                        Entry<?, ?> mani2Ent = (Entry<?, ?>)main2It.next();
                        
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_mainId", mainId);
                        contentValues.put("_testType", testType);
                        contentValues.put("_nettype", 1);
                        contentValues.put("_mainkey1", mainKey1);
                        contentValues.put("_mainkey2", mainKey2);
                        contentValues.put("_keyname", mani2Ent.getKey().toString());
                        contentValues.put("_keyvalue", mani2Ent.getValue().toString());
                        contentValues.put("_scale", 1);
                        contentValues.put("_back1", "");
                        contentValues.put("_back2", "");
                        
                        long num =db.insert(DataTableStruct.tb_TotalSpecialTimes,null,contentValues);
                        Log.e(TAG,"--special-main:"+mainId+"--testType:"+testType+"--m1:"+mainKey1+"--m2:"+mainKey2
                                +"--name:"+mani2Ent.getKey().toString()+"--v:"+mani2Ent.getValue().toString()+"--num:"+num);
                        count++;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return count;
    }
	
	/**
	 * 将网络相关的参数统计信息存与该表中
	 * @return
	 */
	public synchronized long insertTotalPara(int mainId,int testType,Iterator<?> itData){
		int count = 0;
		try{
			while(itData.hasNext()){
				Entry<?, ?> ent = (Entry<?, ?>)itData.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("_mainId", mainId);
				contentValues.put("_testType", testType);
				contentValues.put("_nettype", 1);
				contentValues.put("_keyname", ent.getKey().toString());
				contentValues.put("_keyvalue", Long.parseLong(ent.getValue().toString()));
				contentValues.put("_scale", 1);
				contentValues.put("_back1", "");
				
				long num = db.insert(DataTableStruct.tb_TotalPara,null,contentValues);
				Log.e(TAG,"--para-main:"+mainId+"--testType:"+testType+"--name:"+ent.getKey()+"--v:"+ent.getValue()+"--num:"+num);
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 将与网络相关的参数测试信息，maxvalue,minvalue,meanvalue存与该表中
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized long insertTotalMeasurePara(int mainId,int testType,Iterator<?> itData){
		int count = 0;
		try{
			while(itData.hasNext()){
				Entry<String,TotalMeasureModel> ent = (Entry<String,TotalMeasureModel>)itData.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("_mainId", mainId);
				contentValues.put("_testType", testType);
				contentValues.put("_nettype", 1);
				contentValues.put("_keyname", ent.getKey());
				contentValues.put("_maxvalue", ent.getValue().getMaxValue());
				contentValues.put("_minvalue", ent.getValue().getMinValue());
				contentValues.put("_keysum", ent.getValue().getKeySum());
				contentValues.put("_keycounts", ent.getValue().getKeyCounts());
				contentValues.put("_scale", 1);
				contentValues.put("_back1", "");
				
				long num = db.insert(DataTableStruct.tb_TotalMeasurePara,null,contentValues);
				Log.e(TAG,"--measure-main:"+mainId+"--testType:"+testType+"--name:"+ent.getKey()
						+"--max:"+ent.getValue().getMaxValue()
						+"--min:"+ent.getValue().getMinValue()
						+"--sum:"+ent.getValue().getKeySum()
						+"--count:"+ent.getValue().getKeyCounts()
						+"--num:"+num);
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 将与事件相关的统计信息存与该表中
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public synchronized long insertTotalEvent(int mainId,int testType,Iterator itData){
		int count = 0;
		try{
			while(itData.hasNext()){
				Entry<?, ?> ent = (Entry<?, ?>)itData.next();
				ContentValues contentValues = new ContentValues();
				contentValues.put("_mainId", mainId);
				contentValues.put("_testType", testType);
				contentValues.put("_nettype", 1);
				contentValues.put("_keyname", ent.getKey().toString());
				contentValues.put("_keyvalue", ent.getValue().toString());
				contentValues.put("_scale", 1);
				contentValues.put("_back1", "");
				
				long num = db.insert(DataTableStruct.tb_TotalEvent,null,contentValues);
				Log.e(TAG,"--event-main:"+mainId+"--testType:"+testType+"--name:"+ent.getKey()+"--v:"+ent.getValue()+"--num:"+num);
				count++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	
	

	
	/**
	 * 获得指定文件ID对应的与网络无关的业务测试次数相关的统计信息
	 * @param wheres
	 * @return
	 */
	public HashMap<String,Long> getTotalUnifyTimes(String wheres){
		HashMap<String, Long> hMap = new HashMap<String, Long>();
		try{
			String sql = "select _keyname,"
				+ "sum(_keyvalue) _keyvalue"
				+ " from "+DataTableStruct.tb_TotalUnifyTimes
				+ wheres
				+ " group by _keyname";
				
			Log.e(TAG,"---getTotalUnifyTimes:"+sql);
			Cursor cursor = db.rawQuery(sql, null);
			while( cursor.moveToNext() ){
				hMap.put(cursor.getString( cursor.getColumnIndex("_keyname")),cursor.getLong(cursor.getColumnIndex("_keyvalue")));
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return hMap;
	}
	
	/**
     * 获得指定文件ID对应的与网络无关的业务测试次数相关的统计信息
     * @param wheres
     * @return
     */
    public Map<String, Map<String, Map<String, Long>>> getTotalSpecialTimes(String wheres){
        Map<String, Map<String, Map<String, Long>>> main1Map = new LinkedHashMap<>();
        Map<String, Map<String, Long>> main2Map = new LinkedHashMap<>();
        Map<String, Long> hMap = new LinkedHashMap<>();
        try{
            String sql = "select _mainkey1,_mainkey2,_keyname,"
                + "sum(_keyvalue) _keyvalue"
                + " from " + DataTableStruct.tb_TotalSpecialTimes
                + wheres
                + " group by _mainkey1,_mainkey2,_keyname"
                + " order by _mainkey1,_mainkey2";
                
            Log.e(TAG,"---getTotalUnifyTimes:"+sql);
            Cursor cursor = db.rawQuery(sql, null);
            String mainKey1 = "";
            String mainKey2 = "";
            
            while( cursor.moveToNext() ){
                //从第一条记录获得主键1，主键2的值
                if(mainKey1.equals("") && mainKey2.equals("")){
                    mainKey1 = cursor.getString( cursor.getColumnIndex("_mainkey1"));
                    mainKey2 = cursor.getString( cursor.getColumnIndex("_mainkey2"));
                }else{
                    //如果当前记录主键2的值与上次的值不相符，先把上一次的hMap表值存到上次的main2表中
                    if(!mainKey2.equals(cursor.getString( cursor.getColumnIndex("_mainkey2"))) 
                            || !mainKey1.equals(cursor.getString( cursor.getColumnIndex("_mainkey1"))) ){
                        main2Map.put(mainKey2, hMap);
                        
                        mainKey2 = cursor.getString( cursor.getColumnIndex("_mainkey2"));
                        hMap = new HashMap<>();
                    }
                    //如果当前记录主键1的值与上次的值不相符，先把上一次的main1表值存到上次的main1表中
                    if(!mainKey1.equals(cursor.getString( cursor.getColumnIndex("_mainkey1")))){
                        main1Map.put(mainKey1, main2Map);
                        
                        mainKey1 = cursor.getString( cursor.getColumnIndex("_mainkey1"));
                        main2Map = new HashMap<>();
                    }
                }
                hMap.put(cursor.getString( cursor.getColumnIndex("_keyname")),cursor.getLong(cursor.getColumnIndex("_keyvalue")));
            }
            //循环结束的时候，如果mainKey1和mainKey2不为空的时候，先把hMap表按mainKey2存入main2,再把main2表按mainKey1键存入main1表
            if(!mainKey1.equals("") && !mainKey2.equals("")){
                main2Map.put(mainKey2, hMap);
                main1Map.put(mainKey1, main2Map);
            }
            cursor.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        printMain1Map(main1Map);
        return main1Map;
    }
    
	private void printMain1Map(Map<String, Map<String, Map<String, Long>>> main1Map) {
    	for(String main1Key:main1Map.keySet()){
			Log.d(TAG, "--main1:" + main1Key);
			Map<String, Map<String, Long>> main2Map = main1Map.get(main1Key);
			for(String main2Key:main2Map.keySet()){
				Log.d(TAG, "--main2:" + main2Key);
				Map<String, Long> map = main2Map.get(main2Key);
				for(String key:map.keySet()){
					Log.e(TAG, "--key:" + key + "--value:" + map.get(key));
				}
			}
		}
	}
	
	/**
	 * 获得指定文件路径的FTP明细信息
	 * @return
	 */
	public HashMap<String, Long> getTotalPara(String wheres){
		HashMap<String, Long> hMap = new HashMap<String, Long>();
		try{
			String sql = "select _keyname,"
				+"sum(_keyvalue) _keyvalue"
				+" from " + DataTableStruct.tb_TotalPara
				+ wheres
				+" group by _keyname";
				
			Log.e(TAG,"---getTotalPara:"+sql);
			Cursor cursor = db.rawQuery(sql, null);
			while( cursor.moveToNext() ){
				hMap.put(cursor.getString( cursor.getColumnIndex("_keyname")),cursor.getLong(cursor.getColumnIndex("_keyvalue")));
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return hMap;
	}
	
	/**
	 *  获得指定文件路径的WAP明细信息
	 * @return
	 */
	public HashMap<String,TotalMeasureModel> getTotalMeasurePara(String wheres){
		HashMap<String, TotalMeasureModel> hMap = new HashMap<String, TotalMeasureModel>();
		try{
			String sql = "select _keyname,"
				+"max(_maxvalue) _maxvalue,"
				+"min(_minvalue)_minvalue,"
				+"sum(_keysum) _keysum,"
				+"sum(_keycounts) _keycounts"
				+" from "+DataTableStruct.tb_TotalMeasurePara
				+ wheres
				+" group by _keyname";
				
			Log.e(TAG,"---getTotalMeasurePara:"+sql);
			Cursor cursor = db.rawQuery(sql, null);
			while( cursor.moveToNext() ){
				TotalMeasureModel mesureModel = new TotalMeasureModel();
				mesureModel.setMaxValue(cursor.getLong(cursor.getColumnIndex("_maxvalue")));
				mesureModel.setMinValue(cursor.getLong(cursor.getColumnIndex("_minvalue")));
				mesureModel.setKeySum(cursor.getLong(cursor.getColumnIndex("_keysum")));
				mesureModel.setKeyCounts(cursor.getLong(cursor.getColumnIndex("_keycounts")));
				hMap.put(cursor.getString( cursor.getColumnIndex("_keyname")),mesureModel);
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return hMap;
	}
	
	/**
	 * 获得指定文件路径的GSM测试参数明细信息
	 * @param wheres
	 * @return
	 */
	public HashMap<String, Long> getTotalEvent(String wheres){
		HashMap<String, Long> hMap = new HashMap<String, Long>();
		try{
			String sql = "select _keyname,"
				+ "sum(_keyvalue) _keyvalue"
				+ " from "+DataTableStruct.tb_TotalEvent
				+ wheres
				+ " group by _keyname";
				
			Log.e(TAG,"---getTotalEvent:"+sql);
			Cursor cursor = db.rawQuery(sql, null);
			while( cursor.moveToNext() ){
				hMap.put(cursor.getString( cursor.getColumnIndex("_keyname")),cursor.getLong(cursor.getColumnIndex("_keyvalue")));
			}
			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return hMap;
	}
	
	/**
	 * @param fileName 要统计的文件名
	 * @return 根据文件名查询文件相关的所有统计记录，
	 * 把记录生成可以在iPad直接插入对应统计数据的SQL语句，
	 * SQL语句每行一句，分号结尾
	 */
	public File getTotalSQL( String fileName ,String mobileName){
		StringBuffer sb = new StringBuffer();
		
		//当前数据库表中的不文件名不再存储扩展名,防止传进来的值带扩展名,做如下处理
		fileName = fileName.indexOf(".") > 0 ?
				fileName.substring(0,fileName.lastIndexOf(".")) : fileName;
		
		//获取文件对应记录的id,此id是统计中的_mainId
		int id = -1;
		try{
			String sqlSelectId = String.format("select %s from %s where file_name='%s';"
					,DataTableMap.TestRecord.name(), TestRecordEnum.totalid.name(),fileName);
			Cursor cursor = db.rawQuery(sqlSelectId, null);
			cursor.moveToFirst();
			id = cursor.getInt( cursor.getColumnIndex( TestRecordEnum.totalid.name() ) );
		}catch(Exception e){
			Log.e(TAG, e.toString() );
		}
		
		if( id!=-1 ){
			sb.append( getTableSQL(DataTableStruct.tb_TotalMeasurePara,
					dbStruct.getCreateTableStr(DataTableStruct.tb_TotalMeasurePara,DataTableStruct.TotalMeasureEnum.values()),
					id,"_mainId", mobileName) );
			sb.append( getTableSQL(DataTableStruct.tb_TotalPara,
					dbStruct.getCreateTableStr(DataTableStruct.tb_TotalPara,DataTableStruct.TotalParaEnum.values()),
					id,"_mainId", mobileName) );
			sb.append( getTableSQL(DataTableStruct.tb_TotalSpecialTimes,
					dbStruct.getCreateTableStr(DataTableStruct.tb_TotalSpecialTimes,DataTableStruct.TotalSpecialTimesEnum.values()),
					id,"_mainId", mobileName) );
			sb.append( getTableSQL(DataTableStruct.tb_TotalUnifyTimes,
					dbStruct.getCreateTableStr(DataTableStruct.tb_TotalUnifyTimes,DataTableStruct.TotalUnifyTimsEnum.values()),
					id,"_mainId", mobileName) );
		}
		
		String sqlFilePath = String.format("%s/%s.rcu.txt", 
				mContext.getFilesDir().getAbsolutePath(),fileName);
		
		return MyFileWriter.write( sqlFilePath, sb.toString() );
	}
	
	
	/**
	 * @param tableName 要查询的表名
	 * @param createTableSQL 创建这个表的SQL语句
	 * @param mainId 表里的id
	 * @return
	 */
	private StringBuffer getTableSQL(String tableName,String createTableSQL,int mainId,String idName, String mobileName){
		StringBuffer result = new StringBuffer();
		
		String sqlSelect = String.format(Locale.getDefault(),"select * from %s where %s =%d", tableName,idName,mainId);
		ArrayList<Column> columns = getColumns( createTableSQL );
		try{
			Cursor cursor = db.rawQuery(sqlSelect, null);
			while( cursor.moveToNext() ){
				String insertColumns = "_MobileName" ;
				String insertValues = "'"  + mobileName + "'" ;
				//从_mainId开始之后的所有字段
				for(int i=0;i<columns.size();i++){
					Column column = columns.get(i);
					String columnName = column.getName();
					String columnType = column.getType();
					//这里不会用到long类型
					if( columnType.equals("INTEGER") ){
						long value = cursor.getLong( cursor.getColumnIndex(columnName) );
						insertColumns += String.format(Locale.getDefault(),"%s%s", ",",columnName );	//(i>0?",":"") --> ","
						insertValues += String.format(Locale.getDefault(),"%s%d",",",value );
					}else{
						String value = cursor.getString( cursor.getColumnIndex(columnName) );
						insertColumns += String.format(Locale.getDefault(),"%s%s", ",",columnName );
						insertValues += String.format(Locale.getDefault(),"%s'%s'",",",value );
					}
				}
				
				String sqlInsert = String.format("insert into %s (%s) values (%s);\r\n",
						tableName,insertColumns,insertValues);
				Log.i(TAG,sqlInsert );
				result.append( sqlInsert );
			}
		}catch( Exception e){
			LogUtil.w(TAG, "getTableSQL",e);
		}
				
		return result;
	}
	
	
	
	/**
	 * @param sqlCreateTable 创建表格的SQL
	 * @return 表格的所有字段和字段对应属性的Bundle
	 */
	private ArrayList<Column> getColumns(String sqlCreateTable){
		ArrayList<Column> columnArray = new ArrayList<Column>();
		Log.e(TAG, sqlCreateTable );
		String content = (sqlCreateTable.indexOf("(") > 0 ?
				  sqlCreateTable.substring( sqlCreateTable.indexOf(",")+1, sqlCreateTable.length())
				: sqlCreateTable);
		String[] columns = content.split(","); 
		for( int i=0;i<columns.length;i++){
			String columnNanme = columns[i].split(" ")[0];//字段名
			String columnType = columns[i].split(" ")[1];//字段属性
			Column column = new Column(columnNanme,columnType);
			columnArray.add(column);
		}
		return columnArray;
	}
	
	private class Column {
		private String name="";
		private String type="INTEGER";
		public Column(String name,String type){
			this.name = name;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		
	}
}
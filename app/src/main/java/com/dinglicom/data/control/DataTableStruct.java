package com.dinglicom.data.control;

import android.content.Context;

import com.dinglicom.data.model.MappingEnum;
import com.walktour.gui.data.model.DBManager;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * 数据表结构定类型定义
 * @author Tangwq
 *
 */
public class DataTableStruct {

	/**测试类型_城市名称_测试范围_仪表厂家_测试厂家_测试人员姓名_测试时间_扩展信息_测试终端编号*/
	public static final String CU_FILE_NAME_FORMAT  ="%s_%s_%s_%s_%s_%s_%s_%s_%s.%s";
	public static final int invalidValue = -9999;
	
	public static String tb_TotalUnifyTimes 		= "tab_total_unifytimes";
	public static String tb_TotalSpecialTimes 		= "tab_total_specialtimes";
	public static String tb_TotalPara 				= "tab_total_para";
	public static String tb_TotalMeasurePara 		= "tab_total_MeasurePara";
	public static String tb_TotalEvent 			= "tab_total_event";
	public static String database_info			 	= "database_info";
	

	private String buildFiledInfo(String nmae){
		return nmae.equals("") ? "" : " " + nmae;
	}
	
	public String getCreateTableStr(String tableName,MappingEnum[] values){
		StringBuilder builder = new StringBuilder("Create table ");
		builder.append(tableName);
		builder.append("(");
		for (MappingEnum column : values) {
			builder.append(column.name());
			builder.append(buildFiledInfo(column.getFiledType()));
			builder.append(buildFiledInfo(column.getDefaultStr()));
			builder.append(buildFiledInfo(column.getNotNull()));
			builder.append(buildFiledInfo(column.getPrimarykey()));
			builder.append(","); 
		}
		builder = new StringBuilder(builder.substring(0, builder.length() - 1)); 
		builder.append(");");
		
		System.out.println(builder.toString());
		return builder.toString();
	}
	
	/**
	 * 生成记录查询SQL语句
	 * 查询条件为Hashmap表格式如下:
	 * Key:结构存储对象的简单名字,如: RecordNetType.class.getSimpleName(),值为:RecordNetType
	 * Value:当前对象的查询条件,如网络类型明细表中类型为LTE,GSM的条件条件为:"%s.net_type in('6006','6001')"
	 * @param wheres
	 * @return
	 */
	public String getMainRecordSql(Context contex,HashMap<String, ArrayList<String>> wheres){
		StringBuffer sql = new StringBuffer();
		sql.append("select a.* ,(select type_name_en from data_dictionary where type_key = type_scene) type_scene_str,");
		sql.append("(select type_name_en from data_dictionary where type_key = test_type)test_type_str,"); 
		sql.append("(select node_name || ',' || node_info from data_record_build where node_id = a.node_id) node_id_str,");
		sql.append("(select count(task_no) from data_test_record where task_no = a.task_no) task_no_num ");
		sql.append(" from ");
		sql.append(DataTableMap.TestRecord.getTableName());
		sql.append(" ");
		sql.append(DataTableMap.TestRecord.getAliasName());
		
		if(wheres != null){
			StringBuffer where = new StringBuffer();
			for(String key:wheres.keySet()){
				try{
					ArrayList<String> values = wheres.get(key);
					DataTableMap tableMap = DataTableMap.valueOf(key);
					if(tableMap == DataTableMap.RecordDetailUpload){
						sql.append(String.format(tableMap.getLeftJoinOn(),DBManager.getInstance(contex).getServerStr()));
					}else{
						sql.append(tableMap.getLeftJoinOn());
					}
					for(String value : values){
						where.append(" and ");
						where.append(String.format(value, tableMap.getAliasName()));
					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
			
			if(where.length() > 4){
				sql.append(" where ");
				sql.append(where.substring(4));
			}
		}
		sql.append(" group by a.record_id");
		sql.append(" order by a.file_name");
		
		return sql.toString();
	}
	
	/**
	 * 测试信息扩展关键值
	 * @author Tangwq
	 *
	 */
	public static enum RecordInfoKey{
		software_vender,	//测试软件厂家
		software_version,	//软件版本号
		test_item,			//测试项目
		phone_imsi,		//手机imsi号码
		operator,			//运营商
		province,			//省
		city,				//CU城市
		tester,				//CU测试人员
		cu_Scope,			//CU测试范围
		cu_Company,		//CU测试厂家
		cu_Network,		//CU网络模式
		cu_PhoneNum,		//CU手机号码
		extendsInfo,		//CU扩展信息
		metro_line,//地铁测试线路
		high_speed_rail,//高铁测试线路
		single_station//单站验证基站名称
	}
	
	/**
	 * 数据管理表映射关系
	 * @author Tangwq
	 *
	 */
	public static enum DataTableMap {
		TestRecord			("data_test_record",			"a","record_id",		""),
		RecordDetail		("data_record_detail",			"b","detail_id",		" left join data_record_detail b on b.record_id = a.record_id"),
		RecordBuild		("data_record_build",			"c","node_id",			" left join data_record_build c on  c.[node_id] = a.[node_id]"),
		RecordTaskType	("data_record_tasktype",		"d","task_type_id",		" left join data_record_tasktype d on d.[record_id] = a.[record_id]"),
		RecordAbnormal	("data_record_abnormal",		"e","abnormal_id",		" left join data_record_abnormal e on e.[record_id] = a.[record_id]"),
		RecordNetType		("data_record_nettype",			"f","net_type_id",		" left join data_record_nettype  f on f.[record_id] = a.record_id"),
		RecordDetailUpload("data_record_upload_detail",	"g","detail_id,server_info"," left join (select record_id,detail_id,(select upload_type from data_record_upload_detail where cc.[detail_id] = detail_id  and server_info='%s') upload_type from data_record_detail cc) g on g.record_id = a.record_id"),
		RecordImg			("data_record_img",				"h","img_id",			""),
		RecordTestInfo	("data_test_info",				"i","test_info_id",		""),
		Dictionary			("data_dictionary",				"j","dictionary_type",	"");
		
		private String tableName;
		private String aliasName;
		private String primaryKey;
		private String leftJoinOn;
		DataTableMap(String name,String alias,String priKey,String joinOn){
			this.tableName = name;
			this.leftJoinOn = joinOn;
			this.primaryKey = priKey;
			this.aliasName = alias;
		}
		public String getTableName() {
			return tableName;
		}
		public String getLeftJoinOn() {
			return leftJoinOn;
		}
		public String getPrimaryKey(){
			return primaryKey;
		}
		public String getAliasName(){
			return aliasName;
		}
	}
	
	//-------------------------------------------数据库版本信息--------------------------------------------------------
	public static enum DataBaseInfoEnum implements MappingEnum{
		version			("INTEGER",		"",	"NOT NULL",	""),
		dictory_version ("INTEGER",		"",	"NOT NULL",	""),
		imei 			("NVARCHAR(64)","",	"",	""),
		mac_address 	("NVARCHAR(64)","",	"",	""),
		phone_model 	("NVARCHAR(64)","",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		DataBaseInfoEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}

		public String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(DataBaseInfoEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	//--------------------------------------------数据表模块相关表-------------------------------------------------------
	public static enum DictionaryEnum implements MappingEnum{
		dictionary_type	("INTEGER",		"",	"NOT NULL",	""),
		type_key 		("NVARCHAR(32)","",	"",	""),
		type_name_en 	("NVARCHAR(128)","",	"",	""),
		type_name_zh 	("NVARCHAR(128)","",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		DictionaryEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		public String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(DictionaryEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum TestRecordEnum implements MappingEnum{
		totalid		("INTEGER","DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		record_id	("NVARCHAR(64)","",	"NOT NULL",	""),
		type_scene	("INTEGER",		"",	"NOT NULL",	""),
		test_type	("INTEGER",		"",	"NOT NULL",	""),
		file_name 	("NVARCHAR(512)","",	"",	""),
		time_create("INT64",		"",	"",	""),
		time_end	("INT64",		"",	"",	""),
		file_split_id("INTEGER",		"",	"NOT NULL",	""),
		node_id	("NVARCHAR(64)","",	"",	""),
		task_no 	("NVARCHAR(20)","",	"",	""),
		port_id 	("NVARCHAR(20)","",	"",	""),
		test_index	("INTEGER",		"",	"",	""),
		group_info ("NVARCHAR(512)","",	"",	""),
		go_or_nogo	("INTEGER",		"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		TestRecordEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(TestRecordEnum value:values()){
				if(!(value.toString()).equals("totalid")) {
					names[pos++] = value.name();
				}
			}
			return names;
		}
	};
	
	public static enum RecordDetailEnum implements MappingEnum{
		detail_id	("INTEGER","DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		record_id	("NVARCHAR(64)","",	"NOT NULL",	""),
		file_type	("INTEGER",		"",	"NOT NULL",	""),
		file_name 	("NVARCHAR(512)","",	"",	""),
		file_path 	("NVARCHAR(512)","",	"",	""),
		file_size	("INT64",		"",	"",	""),
		file_guid 	("NVARCHAR(20)","",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordDetailEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordDetailEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordUploadEnum implements MappingEnum{
		detail_id	("INTEGER","DEFAULT '1'",	"NOT NULL",	""),
		server_info	("NVARCHAR(64)",	"",	"NOT NULL",	""),
		upload_type ("INTEGER","",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordUploadEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordUploadEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordBuildEnum implements MappingEnum{
		node_id	("NVARCHAR(64)",	"",	"NOT NULL",	""),
		parent_id	("NVARCHAR(64)",	"",	"NOT NULL",	""),
		node_name	("NVARCHAR(128)",	"",	"NOT NULL",	""),
		node_info	("NVARCHAR(128)",	"",	"NOT NULL",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordBuildEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordBuildEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordTaskTypeEnum implements MappingEnum{
		task_type_id("INTEGER",			"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		record_id	("NVARCHAR(64)",	"",	"NOT NULL",	""),
		task_type	("INTEGER",			"",	"NOT NULL",	""),
		task_info_id("NVARCHAR(128)",	"",	"",	""),
		test_plan	("NVARCHAR(1024)",	"",	"",	""),
		excute_time	("INT64",			"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordTaskTypeEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordTaskTypeEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordAbnormalEnum implements MappingEnum{
		abnormal_id	("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		record_id	("NVARCHAR(64)",	"",	"NOT NULL",	""),
		abnormal_type("INTEGER","",	"NOT NULL",	""),
		abnormal_point("INT64",	"",	"",	""),
		abnormal_time("INT64",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordAbnormalEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordAbnormalEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordNetTypeEnum implements MappingEnum{
		net_type_id	("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		record_id	("NVARCHAR(64)",	"",	"NOT NULL",	""),
		net_type	("INTEGER","",	"NOT NULL",	""),
		network_time("INTEGER","",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordNetTypeEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordNetTypeEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordImgEnum implements MappingEnum{
		img_id		("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		node_id		("NVARCHAR(64)",	"",	"NOT NULL",	""),
		img_path	("NVARCHAR(512)",	"",	"",	""),
		img_name	("NVARCHAR(512)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordImgEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(RecordImgEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	public static enum RecordInfoEnum implements MappingEnum{
		test_info_id("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		task_no		("NVARCHAR(64)",	"",	"NOT NULL",	""),
		key_info	("NVARCHAR(128)",	"",	"",	""),
		key_value	("NVARCHAR(128)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		RecordInfoEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length-1];
			int pos = 0;
			for(RecordInfoEnum value:values()){
				if(!(value.toString()).equals("test_info_id")) {
					names[pos++] = value.name();
				}
			}
			return names;
		}
	};
	
	//--------------------------------------------实时统计相关表-------------------------------------------------------
	/**创建与网络无关的相关次数统计信息*/
	public static enum TotalUnifyTimsEnum implements MappingEnum{
		_id			("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		_mainId		("INTEGER",	"",	"NOT NULL",	""),
		_testType	("INTEGER",	"",	"NOT NULL",	""),
		_nettype	("INTEGER",	"",	"NOT NULL",	""),
		_keyname	("NVARCHAR(128)",	"",	"",	""),
		_keyvalue	("INTEGER",	"",	"",	""),
		_scale		("INTEGER",	"",	"",	""),
		_back1		("NVARCHAR(512)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		TotalUnifyTimsEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(TotalUnifyTimsEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	/**创建与网络无关的相关次数统计信息*/
	public static enum TotalSpecialTimesEnum implements MappingEnum{
		_id			("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		_mainId		("INTEGER",	"",	"NOT NULL",	""),
		_testType	("INTEGER",	"",	"NOT NULL",	""),
		_nettype	("INTEGER",	"",	"NOT NULL",	""),
		_mainkey1	("NVARCHAR(128)",	"",	"",	""),
		_mainkey2	("NVARCHAR(128)",	"",	"",	""),
		_keyname	("NVARCHAR(128)",	"",	"",	""),
		_keyvalue	("INTEGER",	"",	"",	""),
		_scale		("INTEGER",	"",	"",	""),
		_back1		("NVARCHAR(512)",	"",	"",	""),
		_back2		("NVARCHAR(512)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		TotalSpecialTimesEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		/**获取属性名数组*/
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(TotalSpecialTimesEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
    
    /**创建存储与网络相关的参数统计信息*/
    public static enum TotalParaEnum implements MappingEnum{
		_id			("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		_mainId		("INTEGER",	"",	"NOT NULL",	""),
		_testType	("INTEGER",	"",	"NOT NULL",	""),
		_nettype	("INTEGER",	"",	"NOT NULL",	""),
		_keyname	("NVARCHAR(128)",	"",	"",	""),
		_keyvalue	("INTEGER",	"",	"",	""),
		_scale		("INTEGER",	"",	"",	""),
		_back1		("NVARCHAR(512)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		TotalParaEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(TotalParaEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	
	/**创建存储网络相关的测试参数信息*/
	public static enum TotalMeasureEnum implements MappingEnum{
		_id			("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		_mainId		("INTEGER",	"",	"NOT NULL",	""),
		_testType	("INTEGER",	"",	"NOT NULL",	""),
		_nettype	("INTEGER",	"",	"NOT NULL",	""),
		_keyname	("NVARCHAR(128)",	"",	"",	""),
		_maxvalue	("INTEGER",	"",	"",	""),
		_minvalue	("INTEGER",	"",	"",	""),
		_keysum		("INTEGER",	"",	"",	""),
		_keycounts	("INTEGER",	"",	"",	""),
		_scale		("INTEGER",	"",	"",	""),
		_back1		("NVARCHAR(512)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		TotalMeasureEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(TotalMeasureEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
	
	/**创建存储网络相关的事件信息*/
	public static enum TotalEventEnum implements MappingEnum{
		_id			("INTEGER",		"DEFAULT '1'",	"NOT NULL",	"PRIMARY KEY AUTOINCREMENT"),
		_mainId		("INTEGER",	"",	"NOT NULL",	""),
		_testType	("INTEGER",	"",	"NOT NULL",	""),
		_nettype	("INTEGER",	"",	"NOT NULL",	""),
		_keyname	("NVARCHAR(128)",	"",	"",	""),
		_keyvalue	("INTEGER",	"",	"",	""),
		_scale		("INTEGER",	"",	"",	""),
		_back1		("NVARCHAR(512)",	"",	"",	"");
		
		private String filedType;
		private String defaultStr;
		private String notNull;
		private String primarykey;
		
		TotalEventEnum(String filedType,String defaultStr,String notNull,String primaryKey){
			this.filedType 	= filedType;
			this.defaultStr	= defaultStr;
			this.notNull	= notNull;
			this.primarykey	= primaryKey;
		}
		/**当前字段类型类串*/
		public String getFiledType() {
			return filedType;
		}
		/**当字字段默认值*/
		public String getDefaultStr() {
			return defaultStr;
		}
		/**当前字段是否不允许为空*/
		public String getNotNull() {
			return notNull;
		}
		/**当前字段是否为自增主键*/
		public String getPrimarykey() {
			return primarykey;
		}
		public static String[] getTableColumnNames(){
			String[] names = new String[values().length];
			int pos = 0;
			for(TotalEventEnum value:values()){
				names[pos++] = value.name();
			}
			return names;
		}
	};
}

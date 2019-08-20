package com.walktour.workorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 存储工单状态
 * @author zhihui.lian
 *
 */
public class DBHelper extends SQLiteOpenHelper {
    
    private static final String TAG = "WorkOrderDBHelper";
    
    /**
     * 数据库名
     */
    public static String DB_NAME = "workorderdb";
    
    /**
     * 数据库版本号
     */
    public static int VERSION = 3;
    
    private static DBHelper dbHelper;
    
    /**
     * 数据操作对象
     */
    private SQLiteDatabase db = null;
    
    private Context mContext;
    
    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        db = this.getWritableDatabase();
        this.mContext = context;
    }
    
    public SQLiteDatabase getSqllitDb() {
        return this.db;
    }
    
    /**
     * @see{ UserPerceptionApplication#onCreate}
     * @param context
     * 传入 Context.getApplicationContext()，防止内存泄露
     * @return DBHelper对象
     */
    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
            
        }
        return dbHelper;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + " workorderdb ("
                + "workId INTEGER PRIMARY KEY," + "workorder_name TEXT,"
                 + " test_state  INTEGER)");
        
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除以前的旧表，创建一张新的空表
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + " workorderdb ("
                + "workId INTEGER PRIMARY KEY," + "workorder_name TEXT,"
                 + " test_state  INTEGER)");
    }
    
    public long insertData(Integer wordId , Integer state , String workOrderName) {
        ContentValues values = new ContentValues();
        values.put("workId", wordId);
        values.put("workorder_name", workOrderName);
        values.put("test_state", state);
        long rs = 0;
        if (db.isOpen()) {
            rs = db.insert(DB_NAME, null, values);
        }
        return rs;
    }
    
 

    /**
     * 通过WorkID获取工单测试状态<BR>
     * [功能详细描述]
     * @return
     */
    public int queryWorkState(int WorkId) {
        String[] offsetinfo = new String[] { "test_state" };
        int stateCode = 0;
        Cursor cs = db.query(DB_NAME, offsetinfo, "workId=" + WorkId, null, null, null, null);
        if (cs != null && cs.moveToNext()) {
        	stateCode = cs.getInt(0);
            cs.close();
            return stateCode;
        }
        return 0;
    }
    
    
    
    /**
     * 清空所有数据<BR>
     * [功能详细描述]
     */
    public void clearAllData() {
        db.execSQL("delete from " + DB_NAME);
    }
    
    
}

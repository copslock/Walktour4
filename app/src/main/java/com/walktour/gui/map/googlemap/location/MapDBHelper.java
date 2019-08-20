/*
 * 文件名: AddressTask.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 地图偏移量查询
 * 创建人: 黄广府
 * 创建时间:2012-8-13
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.map.googlemap.location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class MapDBHelper extends SQLiteOpenHelper {
    
    public final static String DB_NAME = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/walktour/gmoffset.db3";
    
    public final static int VERSION = 1;
    
    private static MapDBHelper dbHelper;
    private boolean databaseExists = false;
    
    private MapDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        
        File dbFile = new File(DB_NAME);
        if(dbFile.exists()){
            databaseExists = true;
        }
        dbFile = null;
    }
    
    public static synchronized MapDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new MapDBHelper(context);
        }
        return dbHelper;
    }
    
    /***
     * [判断纠偏数据库是否存在]<BR>
     * [如果纠偏数据库不存在，不执行后面的数据查询动作]
     * @return
     */
    public boolean isDatabaseExists(){
        return databaseExists;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

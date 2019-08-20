package com.walktour.framework.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.support.v4.util.LongSparseArray;

import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.newmap.basestation.service.BaseStationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基站数据库类
 *
 * @author jianchao.wang
 */
public class BaseStationDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    /**
     * 数据库名
     */
    public static final String DB_NAME = "basedata.db";
    /**
     * 数据库名
     */
    public static final String TABLE_NAME_MAIN = "base_station_main";
    /**
     * 数据库名
     */
    public static final String TABLE_NAME_DETAIL = "base_station_detail";
    /**
     * 数据库版本号
     */
    public static int VERSION = 7;
    /**
     * 唯一实例
     */
    private static BaseStationDBHelper sInstance;
    /**
     * 数据操作对象
     */
    private static SQLiteDatabase db = null;
    /**
     * 分批次保存的最大大小
     */
    private final int batchSize = 100;
    /**
     * 当前是否正在插入数据中
     */
    private boolean isInserting = false;
    private Map<String, List<BaseStationDetail>> mListMap = new HashMap<>();

    private BaseStationDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        db = this.getWritableDatabase();
    }

    public static BaseStationDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BaseStationDBHelper(context.getApplicationContext());
            // updateTable();
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d(TAG, "----onCreate----");
        this.createBaseStationMainTable(db);
        this.createBaseStationDetailTable(db);
    }

    /**
     * 生成基站数据表主表
     */
    private void createBaseStationMainTable(SQLiteDatabase db) {
        String[] textCols = new String[]{"siteName", "longitude", "latitude", "enodeb_id"};
        String[] floatCols = new String[]{"longitude_baidu", "latitude_baidu", "longitude_google", "latitude_google"};
        String[] intCols = new String[]{"mainId", "nettype", "mapType"};
        db.execSQL(this.createTableSQL(TABLE_NAME_MAIN, textCols, intCols, floatCols));
        db.execSQL("create index IX_MAIN_ID_NAME on " + TABLE_NAME_MAIN + "(mainId,longitude,latitude)");
    }

    /**
     * 生成基站数据表明细表
     */
    private void createBaseStationDetailTable(SQLiteDatabase db) {
        String[] textCols = new String[]{"cellName", "cellId", "lac", "bsic", "bcch", "cpi", "pci", "eafrcn", "uarfcn", "bid", "nid", "sid",
                "enodeb_ip", "pn", "ev_pn", "frequency", "ev_freq", "bearing", "psc", "sectorId"};
        String[] intCols = new String[]{"mainId", "antennaHeight"};
        db.execSQL(this.createTableSQL(TABLE_NAME_DETAIL, textCols, intCols, null));
        db.execSQL("create index IX_DETAIL_ID on " + TABLE_NAME_DETAIL + "(mainId)");
    }

    /**
     * 生成SQL语句
     *
     * @param tableName 表名
     * @param textCols  文本型列名
     * @param intCols   整型列名
     * @param floatCols 浮点型列名
     */
    private String createTableSQL(String tableName, String[] textCols, String[] intCols, String[] floatCols) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (id INTEGER PRIMARY KEY");
        if (textCols != null) {
            for (String col : textCols) {
                sql.append(",").append(col).append(" TEXT");
            }
        }
        if (intCols != null) {
            for (String col : intCols) {
                sql.append(",").append(col).append(" INTEGER");
            }
        }
        if (floatCols != null) {
            for (String col : floatCols) {
                sql.append(",").append(col).append(" FLOAT");
            }
        }
        sql.append(")");
        LogUtil.d(TAG, "tableSQL = " + sql.toString());
        return sql.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.d(TAG, "----onUpgrade----");
        // 删除以前的旧表，创建一张新的空表
        db.execSQL("DROP TABLE IF EXISTS basedata");
        if (newVersion > 5) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_MAIN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DETAIL);
        }
        this.createBaseStationMainTable(db);
        this.createBaseStationDetailTable(db);
    }

    /**
     * 把基站数据插入数据表。<BR>
     * 原始基站数据,同网络下新数据会覆盖旧数据
     *
     * @param dataList 基站数据
     * @param mapType  地图类型
     * @param handler  消息句柄
     * @return 成功标识
     */
    public void insertBaseData(List<BaseStation> dataList, int mapType, Handler handler) {
        if (dataList.size() == 0)
            return;
        if (!db.isOpen())
            return;
        LogUtil.d(TAG, "----insertBaseData----start----");
        try {
            db.beginTransaction();
            this.isInserting = true;
//            this.clearBaseData(dataList.get(0).netType, mapType);
            for (int i = 0; i < dataList.size(); i++) {
                BaseStation bsdata = dataList.get(i);
                bsdata.mainId = System.currentTimeMillis() + i;
                ContentValues values = new ContentValues();
                values.put("siteName", checkData(bsdata.name));
                values.put("enodeb_id", checkData(bsdata.enodebId));
                values.put("longitude", bsdata.longitude);
                values.put("latitude", bsdata.latitude);
                values.put("longitude_baidu", bsdata.baiduLongitude);
                values.put("latitude_baidu", bsdata.baiduLatitude);
                values.put("longitude_google", bsdata.googleLongitude);
                values.put("latitude_google", bsdata.googleLatitude);
                values.put("nettype", bsdata.netType);
                values.put("mapType", mapType);
                values.put("mainId", bsdata.mainId);
                db.insert(TABLE_NAME_MAIN, null, values);
                for (BaseStationDetail detail : bsdata.details) {
                    values = new ContentValues();
                    values.put("mainId", bsdata.mainId);
                    values.put("cellName", checkData(detail.cellName));
                    values.put("cellId", checkData(detail.cellId));
                    values.put("lac", checkData(detail.lac));
                    values.put("bsic", checkData(detail.bsic));
                    values.put("bcch", checkData(detail.bcch));
                    values.put("psc", checkData(detail.psc));
                    values.put("uarfcn", checkData(detail.uarfcn));
                    values.put("cpi", checkData(detail.cpi));
                    values.put("pci", checkData(detail.pci));
                    values.put("eafrcn", checkData(detail.earfcn));
                    values.put("bearing", detail.bearing);
                    values.put("bid", checkData(detail.bid));
                    values.put("sid", checkData(detail.sid));
                    values.put("nid", checkData(detail.nid));
                    values.put("enodeb_ip", checkData(detail.enodebIp));
                    values.put("pn", checkData(detail.pn));
                    values.put("ev_pn", checkData(detail.evPn));
                    values.put("frequency", checkData(detail.frequency));
                    values.put("ev_freq", checkData(detail.evFreq));
                    values.put("sectorId", checkData(detail.sectorId));
                    values.put("antennaHeight", detail.antennaHeight);
                    db.insert(TABLE_NAME_DETAIL, null, values);
                }
                if (i > 0 && i % batchSize == 0) {
                    handler.obtainMessage(BaseStationService.SHOW_PROGRESS, (int) ((float) i / (float) dataList.size() * 100)).sendToTarget();
                }
            }
            db.setTransactionSuccessful();
            handler.obtainMessage(BaseStationService.SHOW_PROGRESS, 100).sendToTarget();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
        this.isInserting = false;
        LogUtil.d(TAG, "----insertBaseData----end----");
    }

    /**
     * 判断值是否是空值
     *
     * @param data
     * @return
     */
    private String checkData(String data) {
        if (StringUtil.isNullOrEmpty(data)) {
            return " ";
        }
        return data;
    }

    /**
     * 清空基站信息表<BR>
     * [功能详细描述]
     */
    public void deleteData() {
        db.delete(TABLE_NAME_DETAIL, null, null);
        db.delete(TABLE_NAME_MAIN, null, null);
    }

    /**
     * 获得经纬度的列名
     *
     * @param latlngType 经纬度类型 0:原始数据 1:google数据2:百度数据
     * @return
     */
    private String[] getlnglatColName(int latlngType) {
        String[] lnglatCol = {"longitude", "latitude"};
        switch (latlngType) {
            case 1:// google数据
                lnglatCol[0] = "longitude_google";
                lnglatCol[1] = "latitude_google";
                break;
            case 2:// 百度数据
                lnglatCol[0] = "longitude_baidu";
                lnglatCol[1] = "latitude_baidu";
                break;
        }
        return lnglatCol;
    }

    /**
     * @param selection 查询条件
     */
    private List<BaseStationDetail> queryBaseStationDetail(String selection) {
//        if (this.mListMap.containsKey(selection))
//            return this.mListMap.get(selection);
        String[] offsetinfo = new String[]{"mainId", "lac", "bearing", "psc", "uarfcn", "bcch", "bsic", "cpi", "pci",
                "eafrcn", "sid", "bid", "nid", "pn", "ev_pn", "enodeb_ip", "frequency", "ev_freq", "sectorId",
                "id", "antennaHeight", "cellId", "cellName"};
        Cursor cs = db.query(TABLE_NAME_DETAIL, offsetinfo, selection, null, null, null, null);
        LogUtil.d(TAG, "queryBaseStationDetail：" + "Select * form " + TABLE_NAME_DETAIL + " where " + selection);
        List<BaseStationDetail> detailList = new ArrayList<BaseStationDetail>();
        while (cs != null && cs.moveToNext()) {
            BaseStationDetail detail = new BaseStationDetail();
            detail.mainId = cs.getLong(0);
            detail.lac = cs.getString(1);
            detail.bearing = cs.getInt(2);
            detail.psc = cs.getString(3);
            detail.uarfcn = cs.getString(4);
            detail.bcch = cs.getString(5);
            detail.bsic = cs.getString(6);
            detail.cpi = cs.getString(7);
            detail.pci = cs.getString(8);
            detail.earfcn = cs.getString(9);
            detail.sid = cs.getString(10);
            detail.bid = cs.getString(11);
            detail.nid = cs.getString(12);
            detail.pn = cs.getString(13);
            detail.evPn = cs.getString(14);
            detail.enodebIp = cs.getString(15);
            detail.frequency = cs.getString(16);
            detail.evFreq = cs.getString(17);
            detail.sectorId = cs.getString(18);
            detail.id = cs.getLong(19);
            detail.antennaHeight = cs.getInt(20);
            detail.cellId = cs.getString(21);
            detail.cellName = cs.getString(22);
            detailList.add(detail);
        }
        if (cs != null)
            cs.close();
//        this.mListMap.put(selection, detailList);
        return detailList;
    }


    /**
     * 根据基站对象主ID获得基站对象
     *
     * @param mainId 基站对象主ID
     * @return 基站对象
     */
    public BaseStation getBaseStationByID(long mainId) {
        List<BaseStation> list = this.queryBaseStation(0, "mainId = " + mainId, "mainId = " + mainId);
        if (!list.isEmpty())
            return list.get(0);
        return null;
    }

    /**
     * 根据基站对象主ID获得基站对象
     *
     * @param id 基站对象主ID
     * @return 基站对象
     */
    public BaseStation getBaseStationByID(String id) {
        List<BaseStation> list = this.queryBaseStation(0, "id = " + id, "mainId = " + id);
        if (!list.isEmpty())
            return list.get(0);
        return null;
    }

    /**
     * 通过经纬度范围查询相应基站<BR>
     * [功能详细描述]
     *
     * @param slongitude 最小经度
     * @param slatitude  最小纬度
     * @param elongitude 最大经度
     * @param elatitude  最大位置
     * @param nettype    网络类型
     * @param latlngType 经纬度类型 0:原始数据 1:google数据2:百度数据
     * @param mapType    地图类型
     * @param limitSize  获取的基站过滤后的点数
     * @return 基站数组
     */
    public List<BaseStation> queryBaseStation(double slongitude, double slatitude, double elongitude, double elatitude,
                                              String nettype, int latlngType, int mapType, int limitSize) {
        if (elongitude < 0){
            //这里涉及到地理知识，东经为正，西经为负，这里设置最大东经为 180E
            elongitude = 180;
        }
        String[] lnglatCol = this.getlnglatColName(latlngType);
        StringBuilder selection = new StringBuilder();
        selection.append(slongitude).append("<=").append(lnglatCol[0]).append(" and ").append(lnglatCol[0]).append("<=")
                .append(elongitude);
        selection.append(" and ").append(elatitude).append("<=").append(lnglatCol[1]).append(" and ").append(lnglatCol[1])
                .append("<=").append(slatitude);
        selection.append(" and nettype in (" + nettype + ")");
        if (mapType >= 0)
            selection.append(" and mapType = " + mapType);
        StringBuilder detail = new StringBuilder();
        detail.append("mainId in ( select mainId from ").append(TABLE_NAME_MAIN).append(" where ");
        detail.append(selection).append(")");
        List<BaseStation> list = this.queryBaseStation(latlngType, selection.toString(), detail.toString());
        if (limitSize <= 0)
            return list;
        int step = list.size() / limitSize;
        if (step == 0)
            step = 1;
        List<BaseStation> newList = new ArrayList<BaseStation>();
        for (int i = 0; i < list.size(); i = i + step) {
            newList.add(list.get(i));
        }
        return newList;
    }

    /**
     * 查询指定条件的基站
     *
     * @param latlngType      经纬度类型 0:原始数据 1:google数据2:百度数据
     * @param mainSelection   主表查询条件
     * @param detailSelection 明细表查询条件
     * @return 基站数组
     */
    private List<BaseStation> queryBaseStation(int latlngType, String mainSelection, String detailSelection) {
        List<BaseStation> baseList = new ArrayList<>();
        if (this.isInserting) {
            return baseList;
        }
        String[] lnglatCol = this.getlnglatColName(latlngType);
        String[] offsetinfo = new String[]{"id", "siteName", "enodeb_id", "mainId", lnglatCol[0], lnglatCol[1],
                "nettype", "mapType"};
        Cursor cs = db.query(TABLE_NAME_MAIN, offsetinfo, mainSelection, null, null, null,
                lnglatCol[0] + " asc," + lnglatCol[1] + " asc");
        LogUtil.d(TAG, "queryBaseStation：" + "Select * form " + TABLE_NAME_MAIN + " where " + mainSelection);
        LongSparseArray<BaseStation> mainMap = new LongSparseArray<BaseStation>();
        while (cs != null && cs.moveToNext()) {
            BaseStation base = new BaseStation();
            base.id = cs.getLong(0);
            base.name = cs.getString(1);
            base.enodebId = cs.getString(2);
            base.mainId = cs.getLong(3);
            base.longitude = cs.getDouble(4);
            base.latitude = cs.getDouble(5);
            base.netType = cs.getInt(6);
            base.mapType = cs.getInt(7);
            mainMap.put(base.mainId, base);
            baseList.add(base);
        }
        if (cs != null)
            cs.close();
        if (detailSelection != null) {
            List<BaseStationDetail> detailList = this.queryBaseStationDetail(detailSelection);
            for (BaseStationDetail detail : detailList) {
                if (mainMap.get(detail.mainId) != null) {
                    detail.main = mainMap.get(detail.mainId);
                    detail.main.details.add(detail);
                }
            }
        }
        mainMap.clear();
        return baseList;
    }

    /**
     * 查询搜索基本数据列表
     * [功能详细描述]
     *
     * @param netType    网络类型 <0为不区分网络类型
     * @param latlngType 经纬度类型 0:原始数据 1:google数据2:百度数据
     * @param mapType    地图类型
     * @return 基站列表
     */
    public List<BaseStation> queryBaseStation(int netType, int latlngType, int mapType) {
        StringBuilder selection = new StringBuilder();
        if (netType >= 0)
            selection.append("nettype in (" + netType + ")").append(" and ");
        selection.append(" mapType = ").append(mapType);
        StringBuilder detail = new StringBuilder();
        detail.append("mainId in ( select mainId from ").append(TABLE_NAME_MAIN).append(" where ");
        detail.append(selection).append(")");
        return this.queryBaseStation(latlngType, selection.toString(), detail.toString());
    }

    public List<BaseStation> queryBaseStation(String netType, int latlngType, int mapType) {
        StringBuilder selection = new StringBuilder();
        selection.append(" nettype = ").append(netType).append(" and ");
        selection.append(" mapType = ").append(mapType);
        StringBuilder detail = new StringBuilder();
        detail.append("mainId in ( select mainId from ").append(TABLE_NAME_MAIN).append(" where ");
        detail.append(selection).append(")");
        return this.queryBaseStation(latlngType, selection.toString(), detail.toString());
    }


    /**
     * 根据小区id查询小区详情
     * @param cellId 小区id
     * @return
     */
    public List<BaseStationDetail> queryCellByCellId(String cellId){
        String querySql = "cellId = '" + cellId + "'";
        return queryCellIDByFields(querySql);
    }

    /**
     * 查询小区ID
     *
     * @param condition 条件
     * @return 基站详情列表
     */
    public List<BaseStationDetail> queryCellIDByFields(String condition) {
        List<BaseStationDetail> detailList = this.queryBaseStationDetail(condition);
        if (detailList.isEmpty())
            return detailList;
        StringBuilder selection = new StringBuilder();
        selection.append(" mainId in (");
        for (BaseStationDetail detail : detailList) {
            selection.append(detail.mainId).append(",");
        }
        selection.deleteCharAt(selection.length() - 1);
        selection.append(")");
        List<BaseStation> baseList = this.queryBaseStation(0, selection.toString(), null);
        for (BaseStationDetail detail : detailList) {
            for (BaseStation base : baseList) {
                if (base.mainId == detail.mainId) {
                    detail.main = base;
                    break;
                }
            }
        }
        return detailList;
    }

    /**
     * 清空所有数据<BR>
     * [功能详细描述]
     */
    public void clearAllData() {
        db.execSQL("delete from " + TABLE_NAME_DETAIL);
        db.execSQL("delete from " + TABLE_NAME_MAIN);
    }

    /**
     * 清除基站数据 根据网络类型清除基站数据<BR>
     *
     * @param netType 网络类型
     * @param mapType 地图类型
     */
    private void clearBaseData(int netType, int mapType) {
        if (mapType == BaseStation.MAPTYPE_OUTDOOR) {
            db.execSQL("delete from " + TABLE_NAME_DETAIL + " where mainId in (select mainId from " + TABLE_NAME_MAIN
                    + " where nettype = " + netType + " and mapType = " + mapType + ")");
            db.execSQL("delete from " + TABLE_NAME_MAIN + " where nettype = " + netType + " and mapType = " + mapType);
        } else {
            this.clearBaseData(mapType);
        }
    }

    /**
     * 清除基站数据 根据地图类型清除基站数据<BR>
     *
     * @param mapType 地图类型
     */
    public void clearBaseData(int mapType) {
        db.execSQL("delete from " + TABLE_NAME_DETAIL + " where mainId in (select mainId from " + TABLE_NAME_MAIN
                + " where mapType = " + mapType + ")");
        db.execSQL("delete from " + TABLE_NAME_MAIN + " where mapType = " + mapType);
    }

}

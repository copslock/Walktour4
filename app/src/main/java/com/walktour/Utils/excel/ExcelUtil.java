package com.walktour.Utils.excel;

import android.content.Context;
import android.os.Environment;

import com.walktour.Utils.DateUtil;
import com.walktour.Utils.FileUtil;
import com.walktour.Utils.excel.jxlhelper.ExcelManager;
import com.walktour.Utils.excel.model.NoUpgradeExcelBean;
import com.walktour.Utils.excel.model.TotalExcelBean;
import com.walktour.Utils.excel.model.WorkTimeExcelBean;
import com.walktour.base.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * @author jinfeng.xie
 * @data 2019/3/19
 *      Excel 的共具类，可以导入，导出的
 */
public class ExcelUtil {
    private static final String TAG = "ExcelUtil";

    private static ExcelUtil instance;
    private final Context mContext;

    String sdPath = Environment.getExternalStorageDirectory().toString();

    String filePath = sdPath + "/walktour/export";

    String worktimePath =filePath+"/usersWorkTime.xls";
    String nogrPath=filePath+"/NoUpgrade.xls";
    private ExcelUtil(Context context){
        mContext=context;
    }
    /**
     * 获取设备信息单例
     * @return
     */
    public static synchronized  ExcelUtil getInstance(Context context){
        if(instance == null){
            instance = new ExcelUtil(context);
        }
        return instance;
    }

    public List<WorkTimeExcelBean> onImportWorkTime() {
        try {
            long t1 = System.currentTimeMillis();
            FileUtil.createFileDir(filePath);
            boolean isExist= FileUtil.checkFile(new File(worktimePath));
            if (!isExist){
                return null;
            }
            InputStream  excelStream = new FileInputStream(worktimePath);
            ExcelManager excelManager = new ExcelManager();
            List<WorkTimeExcelBean> users = excelManager.fromExcel(excelStream, WorkTimeExcelBean.class);
            long t2 = System.currentTimeMillis();
            double time = (t2 - t1) / 1000.0D;
            LogUtil.d(TAG,"读到User个数:" + users.size() + "\n用时:" + time + "秒");
            return users;

        } catch (Exception e) {
            LogUtil.d(TAG,"读取异常");
            e.printStackTrace();
        }
        return null;
    }
    public List<NoUpgradeExcelBean> onImportNoUpgrade() {
        try {
            long t1 = System.currentTimeMillis();
            FileUtil.createFileDir(filePath);
            boolean isExist= FileUtil.checkFile(new File(nogrPath));
            if (!isExist){
                return null;
            }
            InputStream  excelStream = new FileInputStream(nogrPath);
            ExcelManager excelManager = new ExcelManager();
            List<NoUpgradeExcelBean> users = excelManager.fromExcel(excelStream, NoUpgradeExcelBean.class);
            long t2 = System.currentTimeMillis();
            double time = (t2 - t1) / 1000.0D;
            LogUtil.d(TAG,"读到User个数:" + users.size() + "\n用时:" + time + "秒");
            return users;

        } catch (Exception e) {
            LogUtil.d(TAG,"读取异常");
            e.printStackTrace();
        }
        return null;
    }

    public boolean onExportWorkTime( List<WorkTimeExcelBean> users) {
        //实际使用的时候，不要在主线程做操作，demo的数据比较少
        try {
            long t1 = System.currentTimeMillis();

            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            ExcelManager excelManager = new ExcelManager();
            OutputStream excelStream = new FileOutputStream(worktimePath);

            boolean success = excelManager.toExcelWithFormat(excelStream, users);
            long t2 = System.currentTimeMillis();

            double time = (t2 - t1) / 1000.0D;
            if (success) {
                LogUtil.d(TAG,"导出成功：在存储卡根目录:\n"+ worktimePath + "\n用时:" + time + "秒");
                return true;
            } else {
                LogUtil.d(TAG,"导出失败");
                return false;
            }
        } catch (Exception e) {
            LogUtil.d(TAG,"导出异常");
            e.printStackTrace();
            return false;
        }
    }

    public boolean onExportTotal( List<TotalExcelBean> users) {
        //实际使用的时候，不要在主线程做操作，demo的数据比较少
        try {
            long t1 = System.currentTimeMillis();
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String dateTime= DateUtil.getDateTime();
           String totalPath= filePath+"/total"+dateTime+".xls";
            ExcelManager excelManager = new ExcelManager();
            OutputStream excelStream = new FileOutputStream(totalPath);

            boolean success = excelManager.toExcelWithFormat(excelStream, users);
            long t2 = System.currentTimeMillis();

            double time = (t2 - t1) / 1000.0D;
            if (success) {
                LogUtil.d(TAG,"导出成功：在存储卡根目录:\n"+ totalPath + "\n用时:" + time + "秒");
                return true;
            } else {
                LogUtil.d(TAG,"导出失败");
                return false;
            }
        } catch (Exception e) {
            LogUtil.d(TAG,"导出异常");
            e.printStackTrace();
            return false;
        }
    }

}

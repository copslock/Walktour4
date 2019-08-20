package com.walktour.base.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by LinYi on 2016/6/18.
 * </br>SD卡工具类
 */

public class SDCardUtil {

    private SDCardUtil() {
        //no-instances
    }


    /**
     * SD卡是否可用
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDCardPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()  + File.separator;
    }

}

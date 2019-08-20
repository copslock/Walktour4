package com.walktour.base.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Assets工具类
 */
public final class AssetsUtils {
    private static final String TAG="AssetsUtils";

    public static final AssetsUtils instance=new AssetsUtils();
    /**
     * 私有构造器
     */
    private AssetsUtils() {
    }

    /**
     * 获取单例
     * @return
     */
    public static AssetsUtils getInstance(){
        return instance;
    }

    /**
     *  从assets目录中复制整个文件夹内容,不覆盖,如果文件存在则忽略
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {
                File file=new File(newPath);
                if(!file.exists()) {//文件存在，则忽略
                    LogUtil.w(TAG,"oldPath="+oldPath+",newPath="+newPath);
                    InputStream is = context.getAssets().open(oldPath);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024*10];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  从assets目录中复制整个文件夹内容,不覆盖,如果文件存在则忽略
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     *                   @param  isReplace boolean 是否需要覆盖
     */
    public static void copyFilesFromAssets(Context context, String oldPath, String newPath,boolean isReplace) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context,oldPath + "/" + fileName,newPath+"/"+fileName,isReplace);
                }
            } else {
                File file=new File(newPath);
                if(isReplace){
                    if(file.exists()){
                        file.delete();
                    }
                }
                if(!file.exists()) {//文件存在，则忽略
                    LogUtil.w(TAG,"oldPath="+oldPath+",newPath="+newPath);
                    InputStream is = context.getAssets().open(oldPath);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024*10];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从assets目录下拷贝文件
     *
     * @param context            上下文
     * @param assetsFilePath     文件的路径名如：SBClock/0001cuteowl/cuteowl_dot.png
     * @param targetFileFullPath 目标文件路径如：/sdcard/SBClock/0001cuteowl/cuteowl_dot.png
     */
    public static void copyFileFromAssets(Context context, String assetsFilePath, String targetFileFullPath) {
        InputStream assestsFileImputStream;
        try {
            assestsFileImputStream = context.getAssets().open(assetsFilePath);
            copyFile(assestsFileImputStream, targetFileFullPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, String targetPath) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(targetPath));
            byte[] buffer = new byte[1024*10];
            int byteCount = 0;
            while ((byteCount = in.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            in.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

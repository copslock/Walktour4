package com.walktour.base.util;

import android.content.Context;
import android.os.Environment;

import com.walktour.base.R;

import java.io.File;
import java.io.IOException;

/**
 * 应用使用的文件路径工具类
 * Created by wangk on 2017/9/15.
 */

public class AppFilePathUtil {
    /**
     * 日志标识
     */
    private static final String TAG = "AppFilePathUtil";
    /**
     * 唯一实例
     */
    private static AppFilePathUtil sInstance;
    /**
     * 应用安装目录
     */
    private File mAppBaseDirectory;
    /**
     * 应用安装目录下的files目录
     */
    private File mAppFilesDirectory;
    /**
     * SD卡下的应用默认目录
     */
    private File mSDCardBaseDirectory;
    /**
     * SD卡下的应用默认目录
     */
    private File mSDCardDirectory;

    private AppFilePathUtil() {
        this.logDebug("---onCreate---");
    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public synchronized  static AppFilePathUtil getInstance() {
        if (sInstance == null) {
            sInstance = new AppFilePathUtil();
        }
        return sInstance;
    }

    /**
     * 初始化应用使用的文件路径
     *
     * @param context 上下文
     */
    public void init(Context context) {
        this.logDebug("---init---");
        if (this.mAppBaseDirectory == null) {
            this.mAppFilesDirectory = context.getFilesDir();
            this.mAppBaseDirectory = this.mAppFilesDirectory.getParentFile();
        }
        if (this.mSDCardBaseDirectory == null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            this.mSDCardDirectory = Environment.getExternalStorageDirectory();
            String filePath = this.createDirectory(this.mSDCardDirectory.getAbsolutePath(), context.getString(R.string.app_name));
            if (!StringUtil.isEmpty(filePath)) {
                this.mSDCardBaseDirectory = new File(filePath);
            }
        }
    }

    /**
     * 获取应用安装目录
     *
     * @return 目录
     */
    public String getAppBaseDirectory() {
        this.logDebug("---getAppBaseDirectory---");
        return this.mAppBaseDirectory.getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用安装目录下的files目录
     *
     * @return 目录
     */
    public String getAppFilesDirectory() {
        this.logDebug("---getAppFilesDirectory---");
        return this.mAppFilesDirectory.getAbsolutePath() + File.separator;
    }

    /**
     * 获取应用安装目录下的files目录下的目录
     *
     * @param subNames 下级目录名
     * @return 目录
     */
    public String getAppFilesDirectory(String... subNames) {
        this.logDebug("---getAppFilesDirectory---");
        return this.getDirectory(this.mAppFilesDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 获取应用安装目录下的lib目录下的文件
     *
     * @param subNames 下级目录名
     * @return 文件
     */
    public File getAppLibFile(String... subNames) {
        this.logDebug("---getAppLibFile---");
        return this.getFile(this.getAppLibDirectory(), subNames);
    }

    /**
     * 获取应用安装目录files目录下的配置文件目录的文件
     *
     * @param subNames 下级目录名
     * @return 文件
     */
    public File getAppConfigFile(String... subNames) {
        this.logDebug("---getAppConfigFile---");
        return this.getFile(this.getAppConfigDirectory(), subNames);
    }

    /**
     * 获取SD卡下的应用默认基础目录
     *
     * @return 目录
     */
    public String getSDCardBaseDirectory() {
        this.logDebug("---getSDCardBaseDirectory---");
        return this.mSDCardBaseDirectory.getAbsolutePath() + File.separator;
    }

    /**
     * 获取SD卡的目录
     *
     * @return 目录
     */
    public String getSDCardDirectory() {
        if (this.mSDCardDirectory == null)
            return null;
        this.logDebug("---getSDCardDirectory---");
        return this.mSDCardBaseDirectory.getParentFile().getAbsolutePath() + File.separator;
    }

    /**
     * 生成应用安装目录下的files目录路径
     *
     * @param subNames 下级目录名
     * @return 生成目录
     */
    public String createAppFilesDirectory(String... subNames) {
        if (this.mAppFilesDirectory == null)
            return null;
        this.logDebug("---createAppFilesDirectory---");
        return this.createDirectory(this.mAppFilesDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 生成SD卡下默认目录下的目录
     *
     * @param subNames 下级目录名
     * @return 生成目录
     */
    public String createSDCardBaseDirectory(String... subNames) {
        if (this.mSDCardBaseDirectory == null)
            return null;
        this.logDebug("---createSDCardBaseDirectory---");
        return this.createDirectory(this.mSDCardBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 生成目录
     *
     * @param basePath 基础目录
     * @param subNames 下级目录名
     * @return 生成目录
     */
    public String createDirectory(String basePath, String... subNames) {
        String filePath = this.getDirectory(basePath, subNames);
        if (StringUtil.isEmpty(filePath))
            return null;
        this.logDebug("---createDirectory---" + filePath);
        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory())
            if (!file.mkdirs()) {
                LogUtil.e(TAG, "Directory create Error!");
            }
        return filePath;
    }

//    /**
//     * 生成应用安装目录下的files目录下文件
//     *
//     * @param subNames 下级目录名
//     * @return 生成文件
//     */
//    public File createAppFilesFile(String... subNames) {
//        if (this.mAppFilesDirectory == null)
//            return null;
//        this.logDebug( "---createAppFilesFile---");
//        return this.createFile(this.mAppFilesDirectory.getAbsolutePath(), subNames);
//    }

//    /**
//     * 生成应用安装目录下的files目录下的config目录文件
//     *
//     * @param subNames 下级目录名
//     * @return 生成文件
//     */
//    public File createAppConfigFile(String... subNames) {
//        if (this.getAppConfigDirectory() == null)
//            return null;
//        this.logDebug( "---createAppConfigFile---");
//        return this.createFile(this.getAppConfigDirectory().getAbsolutePath(), subNames);
//    }

    /**
     * 生成SD卡下默认目录的文件
     *
     * @param subNames 下级目录名
     * @return 生成文件
     */
    public File createSDCardBaseFile(String... subNames) {
        if (this.mSDCardBaseDirectory == null)
            return null;
        this.logDebug("---createSDCardBaseFile---");
        return this.createFile(this.mSDCardBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 生成文件
     *
     * @param basePath 基础目录
     * @param subNames 下级目录名
     * @return 生成文件
     */
    public File createFile(String basePath, String... subNames) {
        File file = this.getFile(basePath, subNames);
        this.logDebug("---createFile---");
        if (file != null) {
            if (file.exists())
                return file;
            if (!file.getParentFile().exists())
                if (!file.getParentFile().mkdirs())
                    LogUtil.e(TAG, "Directory " + file.getParentFile().getAbsolutePath() + " create Error!");
            try {
                if (!file.createNewFile()) {
                    LogUtil.e(TAG, "File " + file.getAbsolutePath() + " create Error!");
                }
                return file;
            } catch (IOException e) {
                LogUtil.e(TAG, "File " + file.getAbsolutePath() + " create Error!");
            }
        }
        return null;
    }

    /**
     * 删除应用安装目录下的文件
     *
     * @param subNames 下级目录名
     * @return 是否删除成功
     */
    public boolean deleteAppFile(String... subNames) {
        if (this.mAppBaseDirectory == null)
            return false;
        this.logDebug("---deleteAppFile---");
        return this.deleteFile(this.mAppBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 删除SD卡下默认目录的文件
     *
     * @param subNames 下级目录名
     * @return 是否删除成功
     */
    public boolean deleteSDCardBaseFile(String... subNames) {
        if (this.mSDCardBaseDirectory == null)
            return false;
        this.logDebug("---deleteSDCardFile---");
        return this.deleteFile(this.mSDCardBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 删除文件
     *
     * @param basePath 基础目录
     * @param subNames 下级目录名
     * @return 是否删除成功
     */
    public boolean deleteFile(String basePath, String... subNames) {
        this.logDebug("---deleteFile---");
        File file = this.getFile(basePath, subNames);
        return (file != null && file.exists() && file.isFile() && file.delete());
    }

    /**
     * SD卡下默认目录是否有指定文件
     *
     * @param subNames 下级目录名
     * @return 是否有指定文件
     */
    public boolean hasSDCardFile(String... subNames) {
        if (this.mSDCardBaseDirectory == null)
            return false;
        this.logDebug("---hasSDCardFile---");
        return this.hasFile(this.mSDCardBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 应用安装目录files目录下是否有指定文件
     *
     * @param subNames 下级目录名
     * @return 是否有指定文件
     */
    public boolean hasAppFilesFile(String... subNames) {
        if (this.mAppFilesDirectory == null)
            return false;
        this.logDebug("---hasAppFilesFile---");
        return this.hasFile(this.mAppFilesDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 是否有指定文件
     *
     * @param basePath 基础目录
     * @param subNames 下级目录名
     * @return 是否有指定文件
     */
    private boolean hasFile(String basePath, String... subNames) {
        File file = this.getFile(basePath, subNames);
        this.logDebug("---hasFile---");
        return (file != null && file.exists());
    }

    /**
     * 获得应用安装目录files目录下的配置文件目录
     *
     * @return 文件目录
     */
    public String getAppConfigDirectory() {
        if (this.mAppFilesDirectory == null)
            return null;
        this.logDebug("---getAppConfigDirectory---");
        return this.getDirectory(this.mAppFilesDirectory.getAbsolutePath(), "config");
    }

    /**
     * 获得应用安装目录下的库目录
     *
     * @return 文件目录
     */
    public String getAppLibDirectory() {
        if (this.mAppBaseDirectory == null)
            return null;
        this.logDebug("---getAppLibDirectory---");
        return this.getDirectory(this.mAppBaseDirectory.getAbsolutePath(), "lib");
    }

    /**
     * 获取SD卡下默认目录下的指定目录
     *
     * @param subNames 下级目录名
     * @return 指定目录
     */
    public String getSDCardBaseDirectory(String... subNames) {
        if (this.mSDCardBaseDirectory == null)
            return null;
        this.logDebug("---getSDCardBaseDirectory---");
        return this.getDirectory(this.mSDCardBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 获取SD卡下默认目录的指定文件
     *
     * @param subNames 下级目录名
     * @return 指定文件
     */
    public File getSDCardBaseFile(String... subNames) {
        if (this.mSDCardBaseDirectory == null)
            return null;
        this.logDebug("---getSDCardBaseFile---");
        return this.getFile(this.mSDCardBaseDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 获取SD卡下的指定文件
     *
     * @param subNames 下级目录名
     * @return 指定文件
     */
    public File getSDCardFile(String... subNames) {
        if (this.mSDCardDirectory == null)
            return null;
        this.logDebug("---getSDCardFile---");
        return this.getFile(this.mSDCardDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 获取应用安装目录files目录下指定文件
     *
     * @param subNames 下级目录名
     * @return 指定文件
     */
    public File getAppFilesFile(String... subNames) {
        if (this.mAppFilesDirectory == null)
            return null;
        this.logDebug("---getAppFilesFile---");
        return this.getFile(this.mAppFilesDirectory.getAbsolutePath(), subNames);
    }

    /**
     * 获取指定文件
     *
     * @param basePath 基础目录
     * @param subNames 下级目录名
     * @return 指定文件
     */
    public File getFile(String basePath, String... subNames) {
        if (StringUtil.isEmpty(basePath))
            return null;
        StringBuilder sb = new StringBuilder(basePath);
        if (basePath.endsWith("/"))
            sb.deleteCharAt(sb.length() - 1);
        for (String subName : subNames) {
            if (StringUtil.isEmpty(subName))
                continue;
            sb.append(File.separator);
            if (subName.startsWith("/") && subName.endsWith("/"))
                sb.append(subName.substring(1, subName.length() - 1));
            else if (subName.startsWith("/"))
                sb.append(subName.substring(1));
            else if (subName.endsWith("/"))
                sb.append(subName.substring(0, subName.length() - 1));
            else
                sb.append(subName);
        }
        this.logDebug("---getFile---" + sb.toString());
        return new File(sb.toString());
    }

    /**
     * 获取指定文件
     *
     * @param basePath 基础目录
     * @param subNames 下级目录名
     * @return 指定文件
     */
    public String getDirectory(String basePath, String... subNames) {
        File file = this.getFile(basePath, subNames);
        this.logDebug("---getDirectory---");
        if (file != null) {
            return file.getAbsolutePath() + File.separator;
        }
        return null;
    }

    /**
     * 输出调试日志
     *
     * @param message 日志信息
     */
    private void logDebug(String message) {
        boolean isDebug = false;
        if (isDebug)
            LogUtil.d(TAG, message);
    }

}

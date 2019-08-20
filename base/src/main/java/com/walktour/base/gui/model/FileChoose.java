package com.walktour.base.gui.model;

/**
 * 文件选择对象
 * Created by wangk on 2017/6/28.
 */

public class FileChoose {
    /**
     * 文件类型：文件夹
     */
    public static final int FILE_TYPE_DIRECTORY = 0;
    /**
     * 文件类型：文件
     */
    public static final int FILE_TYPE_FILE = 1;
    /**
     * 文件类型：父文件夹
     */
    public static final int FILE_TYPE_PARENT = 2;
    /**
     * 文件类型
     */
    private int mFileType = FILE_TYPE_DIRECTORY;
    /**
     * 文件名称
     */
    private String mFileName;
    /**
     * 文件路径
     */
    private String mFilePath;
    /**
     * 当前显示的文件列表的等级，以最开始查询目录的子目录为1级，
     */
    private int mLevel;
    /**
     * 文件描述(编辑日期，文件大小)
     */
    private String mFileDescription;

    public int getFileType() {
        return mFileType;
    }

    public void setFileType(int fileType) {
        mFileType = fileType;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getFileDescription() {
        return mFileDescription;
    }

    public void setFileDescription(String fileDescription) {
        mFileDescription = fileDescription;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }
}

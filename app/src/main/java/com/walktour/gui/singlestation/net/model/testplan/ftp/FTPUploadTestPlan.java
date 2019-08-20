package com.walktour.gui.singlestation.net.model.testplan.ftp;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.singlestation.net.model.testplan.BaseTestPlan;

/**
 * Created by yi.lin on 2017/9/22.
 * <p>
 * 导入平台基站时平台下发的FTPUpload类型的测试计划
 */

public class FTPUploadTestPlan extends BaseTestPlan {
    @SerializedName("ConnectionSetting")
    private FTPConnectionSetting connectionSetting;
    @SerializedName("FTPHostSetting")
    private FTPHostSetting FTPHostSetting;
    @SerializedName("DetachEverytime")
    private boolean detachEveryTime;//true
    @SerializedName("DisconnectEverytime")
    private boolean disconnectEveryTime;//true,
    @SerializedName("Duration")
    private int duration;//300000
    @SerializedName("FileSize")
    private long fileSize;//10
    @SerializedName("Inteval")
    private int interval;//15000
    @SerializedName("Mode")
    private String mode;//"BIN"
    @SerializedName("NoDataTimeout")
    private int noDataTimeout;//180000
    @SerializedName("RemotePath")
    private String remotePath;//"/test"
    @SerializedName("ThreadCount")
    private int threadCount;//1
    @Override
    public String toString() {
        return "FTPUploadTestPlan{" +
                "connectionSetting=" + connectionSetting +
                ", FTPHostSetting=" + FTPHostSetting +
                ", detachEveryTime=" + detachEveryTime +
                ", disconnectEveryTime=" + disconnectEveryTime +
                ", duration=" + duration +
                ", fileSize=" + fileSize +
                ", interval=" + interval +
                ", mode='" + mode + '\'' +
                ", noDataTimeout=" + noDataTimeout +
                ", remotePath='" + remotePath + '\'' +
                ", threadCount=" + threadCount +
                '}';
    }

    public FTPConnectionSetting getConnectionSetting() {
        return connectionSetting;
    }

    public void setConnectionSetting(FTPConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    public FTPHostSetting getFTPHostSetting() {
        return FTPHostSetting;
    }

    public void setFTPHostSetting(com.walktour.gui.singlestation.net.model.testplan.ftp.FTPHostSetting FTPHostSetting) {
        this.FTPHostSetting = FTPHostSetting;
    }

    public boolean isDetachEveryTime() {
        return detachEveryTime;
    }

    public void setDetachEveryTime(boolean detachEveryTime) {
        this.detachEveryTime = detachEveryTime;
    }

    public boolean isDisconnectEveryTime() {
        return disconnectEveryTime;
    }

    public void setDisconnectEveryTime(boolean disconnectEveryTime) {
        this.disconnectEveryTime = disconnectEveryTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getNoDataTimeout() {
        return noDataTimeout;
    }

    public void setNoDataTimeout(int noDataTimeout) {
        this.noDataTimeout = noDataTimeout;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}

package com.walktour.gui.singlestation.net.model.testplan.ftp;

import com.google.gson.annotations.SerializedName;
import com.walktour.gui.singlestation.net.model.testplan.BaseTestPlan;

/**
 * Created by yi.lin on 2017/9/22.
 * <p>
 * 导入平台基站时平台下发的FTPDownload类型的测试计划
 */

public class FTPDownloadTestPlan extends BaseTestPlan {

    @SerializedName("ConnectionSetting")
    private FTPConnectionSetting connectionSetting;
    @SerializedName("FTPHostSetting")
    private FTPHostSetting FTPHostSetting;
    @SerializedName("DisconnectEverytime")
    private boolean disconnectEveryTime;//true,
    @SerializedName("DetachEverytime")
    private boolean detachEveryTime;//"true"
    @SerializedName("DownloadFile")
    private String downloadFile;//"/test.bin"
    @SerializedName("Duration")
    private int duration;//300000
    @SerializedName("Inteval")
    private int interval;//15000
    @SerializedName("Mode")
    private String mode;//"BIN"
    @SerializedName("NoDataTimeout")
    private int noDataTimeout;//180000,
    @SerializedName("ThreadCount")
    private int threadCount;//1

    @Override
    public String toString() {
        return "FTPDownloadTestPlan{" +
                "connectionSetting=" + connectionSetting +
                ", FTPHostSetting=" + FTPHostSetting +
                ", disconnectEveryTime=" + disconnectEveryTime +
                ", detachEveryTime=" + detachEveryTime +
                ", downloadFile='" + downloadFile + '\'' +
                ", duration=" + duration +
                ", interval=" + interval +
                ", mode='" + mode + '\'' +
                ", noDataTimeout=" + noDataTimeout +
                ", threadCount=" + threadCount +
                '}';
    }

    public boolean isDisconnectEveryTime() {
        return disconnectEveryTime;
    }

    public void setDisconnectEveryTime(boolean disconnectEveryTime) {
        this.disconnectEveryTime = disconnectEveryTime;
    }

    public FTPConnectionSetting getConnectionSetting() {
        return connectionSetting;
    }

    public void setConnectionSetting(FTPConnectionSetting connectionSetting) {
        this.connectionSetting = connectionSetting;
    }

    public com.walktour.gui.singlestation.net.model.testplan.ftp.FTPHostSetting getFTPHostSetting() {
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

    public String getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
}



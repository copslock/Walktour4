package com.walktour.gui.singlestation.net.model.testplan.ftp;

import com.google.gson.annotations.SerializedName;
import com.walktour.base.gui.model.BaseNetModel;

/**
 * Created by yi.lin on 2017/9/22.
 * 下发的FTP测试任务配置信息
 */
public class FTPConnectionSetting  implements BaseNetModel {
    private String APN;
    @SerializedName("Password")
    private String password;
    private String RASNumber;
    @SerializedName("Timeout")
    private String timeout;
    @SerializedName("User")
    private String user;

    @Override
    public String toString() {
        return "FTPConnectionSetting{" +
                "APN='" + APN + '\'' +
                ", password='" + password + '\'' +
                ", RASNumber='" + RASNumber + '\'' +
                ", timeout='" + timeout + '\'' +
                ", user='" + user + '\'' +
                '}';
    }

    public String getAPN() {
        return APN;
    }

    public void setAPN(String APN) {
        this.APN = APN;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRASNumber() {
        return RASNumber;
    }

    public void setRASNumber(String RASNumber) {
        this.RASNumber = RASNumber;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

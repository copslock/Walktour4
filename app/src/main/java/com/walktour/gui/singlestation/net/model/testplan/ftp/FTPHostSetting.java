package com.walktour.gui.singlestation.net.model.testplan.ftp;

import com.google.gson.annotations.SerializedName;
import com.walktour.base.gui.model.BaseNetModel;

/**
 * Created by yi.lin on 2017/9/22.
 * 下发的FTP测试任务配置信息
 */
public class FTPHostSetting  implements BaseNetModel {
    @SerializedName("Authentication")
    private String authentication;//1
    @SerializedName("Host")
    private String host;//"61.143.60.84"
    @SerializedName("Password")
    private String password;//"test"
    @SerializedName("Port")
    private int port;//"21"
    @SerializedName("User")
    private String User;//"test"

    @Override
    public String toString() {
        return "FTPHostSetting{" +
                "authentication='" + authentication + '\'' +
                ", host='" + host + '\'' +
                ", password='" + password + '\'' +
                ", port='" + port + '\'' +
                ", User='" + User + '\'' +
                '}';
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}

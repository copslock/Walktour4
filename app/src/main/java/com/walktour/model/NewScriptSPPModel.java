package com.walktour.model;

import com.google.gson.annotations.SerializedName;
import com.walktour.base.gui.model.BaseNetModel;

/**
 * Created by yi.lin on 2017/9/13.
 * <p>
 * 检测新scrip.spp文件请求返回的数据存放实体类
 * {
 * "url": "http://update.flvurl.cn/siteparser/rule/custom/dingli/script.spp",
 * "hash": "EB826AA76C720C49C38B598945A15244",
 * "time": 1505130471
 * }
 */

public class NewScriptSPPModel implements BaseNetModel {

    @SerializedName("url")
    private String url;
    @SerializedName("hash")
    private String hash;
    @SerializedName("time")
    private long time;

    @Override
    public String toString() {
        return "NewScriptSPPModel{" +
                "url='" + url + '\'' +
                ", hash='" + hash + '\'' +
                ", time=" + time +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

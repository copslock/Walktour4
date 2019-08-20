package com.walktour.service.app.datatrans.inns.net.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi.lin on 2017/12/1.
 *
 *
 * 上传数据到寅时服务器接受服务器响应数据的bean
 */

public class InnsUploadResult {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private String data;

    @Override
    public String toString() {
        return "InnsUploadResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

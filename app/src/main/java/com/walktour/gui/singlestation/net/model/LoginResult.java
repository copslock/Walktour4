package com.walktour.gui.singlestation.net.model;

import com.walktour.base.gui.model.BaseNetModel;

/**
 * 登录服务器返回结果
 * Created by wangk on 2017/8/17.
 */

public class LoginResult implements BaseNetModel{
    /**
     * 登录请求
     */
    private String Message;
    /**
     * 登录是否成功
     */
    private boolean Success;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }
}

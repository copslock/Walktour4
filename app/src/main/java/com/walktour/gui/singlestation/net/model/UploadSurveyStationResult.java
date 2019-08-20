package com.walktour.gui.singlestation.net.model;

import com.google.gson.annotations.SerializedName;
import com.walktour.base.gui.model.BaseNetModel;

/**
 * Created by yi.lin on 2017/9/4.
 * <p>
 * 请求上传勘察基站数据结果
 */

public class UploadSurveyStationResult  implements BaseNetModel {

    @SerializedName("Message")
    private String message;
    @SerializedName("Success")
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}

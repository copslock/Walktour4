package com.walktour.gui.singlestation.net.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yi.lin on 2017/10/20.
 * 上传基站勘察结果数据中的测试信息model
 */

public class TestInfoUploadModel {

    @SerializedName("TestDate")
    private String testDate;//"2016-10-3",
    @SerializedName("TestDeviceModel")
    private String testDeviceModel;//"设备型号",
    @SerializedName("TestPhoneNum")
    private String testPhoneNum;//"13800000000",
    @SerializedName("Tester")
    private String tester;//"测试人员",
    @SerializedName("TestPlatform")
    private String testPlatform;//"安卓"

    @Override
    public String toString() {
        return "TestInfoUploadModel{" +
                "testDate='" + testDate + '\'' +
                ", testDeviceModel='" + testDeviceModel + '\'' +
                ", testPhoneNum='" + testPhoneNum + '\'' +
                ", tester='" + tester + '\'' +
                ", testPlatform='" + testPlatform + '\'' +
                '}';
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTestDeviceModel() {
        return testDeviceModel;
    }

    public void setTestDeviceModel(String testDeviceModel) {
        this.testDeviceModel = testDeviceModel;
    }

    public String getTestPhoneNum() {
        return testPhoneNum;
    }

    public void setTestPhoneNum(String testPhoneNum) {
        this.testPhoneNum = testPhoneNum;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getTestPlatform() {
        return testPlatform;
    }

    public void setTestPlatform(String testPlatform) {
        this.testPlatform = testPlatform;
    }
}

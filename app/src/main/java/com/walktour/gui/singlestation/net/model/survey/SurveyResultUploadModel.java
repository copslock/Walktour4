package com.walktour.gui.singlestation.net.model.survey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yi.lin on 2017/10/20.
 * <p>
 * 上传基站勘察结果数据model
 */
public class SurveyResultUploadModel {

    @SerializedName("SiteInfo")
    private SiteInfoUploadModel siteInfo;

    @SerializedName("CellInfo")
    private List<CellInfoUploadModel> cellInfoList;

    @SerializedName("TestInfo")
    private TestInfoUploadModel testInfo;

    public SiteInfoUploadModel getSiteInfo() {
        return siteInfo;
    }

    public void setSiteInfo(SiteInfoUploadModel siteInfo) {
        this.siteInfo = siteInfo;
    }

    public List<CellInfoUploadModel> getCellInfoList() {
        return cellInfoList;
    }

    public void setCellInfoList(List<CellInfoUploadModel> cellInfoList) {
        this.cellInfoList = cellInfoList;
    }

    public TestInfoUploadModel getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(TestInfoUploadModel testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public String toString() {
        return "SurveyResultUploadModel{" +
                "siteInfo=" + siteInfo +
                ", cellInfoList=" + cellInfoList +
                ", testInfo=" + testInfo +
                '}';
    }
}

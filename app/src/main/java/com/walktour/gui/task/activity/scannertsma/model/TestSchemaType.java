package com.walktour.gui.task.activity.scannertsma.model;

/**
 * @author jinfeng.xie
 * @data 2019/1/30
 */
/**
 * 业务测试模板类型，控制界面模板
 *  **加入分组类型，方便界面操作
 * @author zhihui.lian
 */
public enum TestSchemaType {



    GSMCOLORCODE("GSM",1,2,"G-ColorCode"),
    GSMCW("GSM",1,1,"G-Cw"),
    GSMSPECTRUM("GSM",1,5,"G-Spectrum"),
    WCDMACW("WCDMA",2,1,"W-Cw"),
    WCDMAPILOT("WCDMA",2,3,"W-Pilot"),
    WCDMASPECTURM("WCDMA",2,5,"W-Spectrum"),
    WCDMABLIND("WCDMA",2,6,"W-Blind"),
    CDMACW("CDMA",3,1,"C-Cw"),
    CDMAPILOT("CDMA",3,3,"C-Pilot"),
    CDMASPECTURM("CDMA",3,5,"C-Spectrum"),
    CDMABLIND("CDMA",3,6,"C-Blind"),
    EVDOCW("EVDO",4,1,"E-Cw"),
    EVDOPILOT("EVDO",4,3,"E-Pilot"),
    TDSCDMACW("TDSCDMA",5,1,"T-Cw"),
    TDSCDMAPILOT("TDSCDMA",5,3,"T-Pilot"),
    TDSCDMASPCTRUM("TDSCDMA",5,5,"T-Spectrum"),
    LTECW("LTE",6,1,"L-Cw"),
    LTEPILOT("LTE",6,4,"L-Pilot"),
    LTESPECTRUM("LTE",6,5,"L-Spectrum"),
    LTEBLINE("LTE",6,6,"L-Blind"),
    NBLOTPILOT("NB-lOT",7,3,"NB-Pilot"),
    NBLOTBlind("NB-lOT",7,6,"NB-Blind");

    private String groupName;
    private int netWorkType;
    private int schemaTaskType;
    private String showFileName;

    public static final int GSM = 1;
    public static final int WCDMA = 2;
    public static final int CDMA = 3;
    public static final int EVDO = 4;
    public static final int TDSCDMA = 5;
    public static final int LTE = 6;
    public static final int NB_LOT=7;

    public static final int CWTEST = 1;
    public static final int COLORCODETEST = 2;
    public static final int PILOTTEST = 3;
    public static final int LTEPILOTTEST = 4;
    public static final int SPECTURM=5;
    public static final int BLIND=6;



     TestSchemaType(String groupName,int netWorkType,int schemaTaskType,String showFileName){
        this.groupName = groupName;
        this.netWorkType = netWorkType;
        this.schemaTaskType = schemaTaskType;
        this.showFileName = showFileName;
    }
    public String getGroupName() {
        return groupName;
    }
    public int getNetWorkType() {
        return netWorkType;
    }
    public int getSchemaTaskType() {
        return schemaTaskType;
    }
    public String getShowFileName() {
        return showFileName;
    }

}
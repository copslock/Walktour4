/*
 * 文件名: CellBean.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 保存小区监控的信息
 * 创建人: 黄广府
 * 创建时间:2012-8-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.netsniffer;

/**
 * 小区基站信息<BR>
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public class CellBean {
    public static final String UNKNOWN = "UNKNOWN";
    
    private int id;
    
    // mobile country code
    private int mcc = 0;
    
    // mobile network code
    private int mnc = 0;
    
    // cell id
    private int cid = 0;
    
    // location area code
    private int lac = 0;
    
    // insert timestamp
    private long timestamp;
    
    public CellBean() {
        mcc = 0;
        mnc = 0;
        cid = 0;
        lac = 0;
        timestamp = System.currentTimeMillis();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getMcc() {
        return mcc;
    }
    
    public void setMcc(int mcc) {
        this.mcc = mcc;
    }
    
    public int getMnc() {
        return mnc;
    }
    
    public void setMnc(int mnc) {
        this.mnc = mnc;
    }
    
    public int getCid() {
        return cid;
    }
    
    public void setCid(int cid) {
        this.cid = cid;
    }
    
    public int getLac() {
        return lac;
    }
    
    public void setLac(int lac) {
        this.lac = lac;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        if (mcc == -1) {
            return UNKNOWN;
        }
        return mcc + "-0" + mnc + "-" + lac + '-' + cid;
    }
}

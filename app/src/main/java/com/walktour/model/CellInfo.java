/*
 * 文件名: CellInfo.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-5-20
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-5-20] 
 */
public class CellInfo {
    
    private String cellName;
    
    private String cellId;
    
    public CellInfo(String cellName,String cellId,double distance){
        this.cellName = cellName;
        this.cellId = cellId;
        this.distance = distance;
    }
    
    /**
     * 距离，当前位置到Cell的距离
     */
    private double distance;

    /**
     * @return the cellName
     */
    public String getCellName() {
        return cellName == null ? "" : cellName;
    }

    /**
     * @param cellName the cellName to set
     */
    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    /**
     * @return the cellId
     */
    public String getCellId() {
        return cellId == null ? "" : cellId;
    }

    /**
     * @param cellId the cellId to set
     */
    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see java.lang.Object#toString()
     */
    
    @Override
    public String toString() {
        return "CellInfo [cellName=" + cellName + ", cellId=" + cellId
                + ", distance=" + distance + "]";
    }
    
    

}

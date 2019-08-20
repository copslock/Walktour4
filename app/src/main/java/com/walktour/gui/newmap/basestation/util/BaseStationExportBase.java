package com.walktour.gui.newmap.basestation.util;

import android.content.Context;

import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;

import java.util.ArrayList;
import java.util.List;

/**
 * 导出基站数据基础类
 *
 * @author jianchao.wang
 */
public abstract class BaseStationExportBase {
    /**
     * 文件存放路径
     */
    protected String path;
    /**
     * 要导出的基站列表
     */
    protected List<BaseStation> baseStationList = new ArrayList<>();

    /**
     * 导出生成基站数据文件
     *
     * @param context 上下文
     * @param path    文件路径
     */
    protected void exportFile(Context context, String path) {
        this.path = path;
        for (int netType = 1; netType < 6; netType++) {
            this.baseStationList = BaseStationDBHelper.getInstance(context).queryBaseStation(netType, 0,
                    BaseStation.MAPTYPE_OUTDOOR);
            if (this.baseStationList == null || this.baseStationList.isEmpty())
                continue;
            StringBuilder fileName = new StringBuilder();
            fileName.append("basestation_");
            switch (netType) {
                case BaseStation.NETTYPE_TDSCDMA:
                    fileName.append("TDSCDMA");
                    break;
                case BaseStation.NETTYPE_WCDMA:
                    fileName.append("WCDMA");
                    break;
                case BaseStation.NETTYPE_LTE:
                    fileName.append("LTE");
                    break;
                case BaseStation.NETTYPE_GSM:
                    fileName.append("GSM");
                    break;
                case BaseStation.NETTYPE_CDMA:
                    fileName.append("CDMA");
                    break;
            }
            try {
                this.exportFile(netType, fileName.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出指定网络类型的基站数据
     *
     * @param netType  网络类型
     * @param fileName 文件名称
     */
    protected abstract void exportFile(int netType, String fileName) throws Exception;
}

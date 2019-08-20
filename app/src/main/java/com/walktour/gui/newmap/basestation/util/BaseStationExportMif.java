package com.walktour.gui.newmap.basestation.util;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * 基站数据导出mapinfo格式文件
 *
 * @author jianchao.wang
 */
public class BaseStationExportMif extends BaseStationExportBase {
    /**
     * 基站点的样式
     */
    private final String pointSymbol = "Symbol (52,255,9)";

    @Override
    protected void exportFile(int netType, String fileName) throws Exception {
        this.createMifFile(netType, fileName);
        this.createMidFile(netType, fileName);
    }

    /**
     * 生成mapinfo格式的mid文件
     *
     * @param netType  网络类型
     * @param fileName 文件名称
     * @throws Exception 异常
     */
    private void createMidFile(int netType, String fileName) throws Exception {
        File file = new File(path + File.separator + fileName + ".mid");
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
        try {
            for (BaseStation bs : this.baseStationList) {
                for (BaseStationDetail detail : bs.details) {
                    osw.write(this.createMidRow(netType, detail));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            osw.close();
            fos.close();
        }
    }

    /**
     * 生成mid文件的行数据
     *
     * @param netType 网络类型
     * @param detail  基站详情
     * @return 行数据
     */
    private String createMidRow(int netType, BaseStationDetail detail) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(detail.main.name).append("\",");
        sb.append("\"").append(detail.cellId).append("\",");
        sb.append("\"").append(detail.cellName).append("\",");
        sb.append("\"").append(detail.bearing).append("\",");
        sb.append("\"").append(detail.antennaHeight).append("\",");
        switch (netType) {
            case BaseStation.NETTYPE_TDSCDMA:
                sb.append("\"").append(detail.lac).append("\",");
                sb.append("\"").append(detail.uarfcn).append("\",");
                sb.append("\"").append(detail.cpi).append("\"");
                break;
            case BaseStation.NETTYPE_WCDMA:
                sb.append("\"").append(detail.lac).append("\",");
                sb.append("\"").append(detail.psc).append("\",");
                sb.append("\"").append(detail.uarfcn).append("\"");
                break;
            case BaseStation.NETTYPE_LTE:
                sb.append("\"").append(detail.pci).append("\",");
                sb.append("\"").append(detail.earfcn).append("\",");
                sb.append("\"").append(detail.main.enodebId).append("\",");
                sb.append("\"").append(detail.enodebIp).append("\",");
                sb.append("\"").append(detail.sectorId).append("\"");
                break;
            case BaseStation.NETTYPE_GSM:
                sb.append("\"").append(detail.bsic).append("\",");
                sb.append("\"").append(detail.lac).append("\",");
                sb.append("\"").append(detail.bcch).append("\"");
                break;
            case BaseStation.NETTYPE_CDMA:
                sb.append("\"").append(detail.pn).append("\",");
                sb.append("\"").append(detail.evPn).append("\",");
                sb.append("\"").append(detail.nid).append("\",");
                sb.append("\"").append(detail.bid).append("\",");
                sb.append("\"").append(detail.sid).append("\",");
                sb.append("\"").append(detail.frequency).append("\",");
                sb.append("\"").append(detail.evFreq).append("\"");
                break;
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 生成mapinfo格式的mid文件
     *
     * @param netType  网络类型
     * @param fileName 文件名称
     * @throws Exception 异常
     */
    private void createMifFile(int netType, String fileName) throws Exception {
        File file = new File(path + File.separator + fileName + ".mif");
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
        try {
            osw.write(this.createMifHeader(netType));
            for (BaseStation bs : this.baseStationList) {
                for (BaseStationDetail detail : bs.details) {
                    osw.write(this.createMifPoint(detail));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            osw.close();
            fos.close();
        }
    }

    /**
     * 生成mif文件基站点
     *
     * @param detail 基站详情
     * @return 基站点数据
     */
    private String createMifPoint(BaseStationDetail detail) {
        StringBuilder sb = new StringBuilder();
        sb.append("Point ").append(detail.main.longitude).append(" ").append(detail.main.latitude).append("\n");
        sb.append(this.pointSymbol).append("\n");
        return sb.toString();
    }

    /**
     * 生成mif文件的头
     *
     * @return 文件头
     */
    private String createMifHeader(int netType) {
        StringBuilder sb = new StringBuilder();
        sb.append("Version 300\n");
        sb.append("Charset \"WindowsSimpChinese\"\n");
        sb.append("Delimiter \",\"\n");
        sb.append("CoordSys Earth Projection 1, 0\n");
        sb.append(this.createMifColumns(netType));
        sb.append("Data\n");
        return sb.toString();
    }

    /**
     * 生成mif文件的列属性
     *
     * @param netType 网络类型
     * @return 列属性
     */
    private String createMifColumns(int netType) {
        StringBuilder sb = new StringBuilder();
        sb.append("Columns ");
        switch (netType) {
            case BaseStation.NETTYPE_GSM:
                sb.append(8);
                break;
            case BaseStation.NETTYPE_TDSCDMA:
                sb.append(8);
                break;
            case BaseStation.NETTYPE_CDMA:
                sb.append(12);
                break;
            case BaseStation.NETTYPE_WCDMA:
                sb.append(8);
                break;
            case BaseStation.NETTYPE_LTE:
                sb.append(10);
                break;
        }
        sb.append("\n");
        sb.append("SITE_NAME Char(100)\n");
        sb.append("CELL_ID Char(100)\n");
        sb.append("CELL_NAME Char(100)\n");
        sb.append("AZIMUTH Char(10)\n");
        sb.append("ANTENNA_HEIGHT Char(10)\n");
        switch (netType) {
            case BaseStation.NETTYPE_TDSCDMA:
                sb.append("LAC Char(10)\n");
                sb.append("UARFCN Char(10)\n");
                sb.append("CPI Char(10)\n");
                break;
            case BaseStation.NETTYPE_WCDMA:
                sb.append("LAC Char(10)\n");
                sb.append("PSC Char(10)\n");
                sb.append("UARFCN Char(10)\n");
                break;
            case BaseStation.NETTYPE_LTE:
                sb.append("PCI Char(10)\n");
                sb.append("EARFCN Char(10)\n");
                sb.append("eNodeB_ID Char(10)\n");
                sb.append("eNodeB_IP Char(10)\n");
                sb.append("SECTOR_ID Char(10)\n");
                break;
            case BaseStation.NETTYPE_GSM:
                sb.append("BSIC Char(10)\n");
                sb.append("LAC Char(10)\n");
                sb.append("BCCH Char(10)\n");
                break;
            case BaseStation.NETTYPE_CDMA:
                sb.append("PN Char(10)\n");
                sb.append("EV_PN Char(10)\n");
                sb.append("NID Char(10)\n");
                sb.append("BID Char(10)\n");
                sb.append("SID Char(10)\n");
                sb.append("Frequency Char(10)\n");
                sb.append("EV_Freq Char(10)\n");
                break;
        }
        return sb.toString();
    }
}

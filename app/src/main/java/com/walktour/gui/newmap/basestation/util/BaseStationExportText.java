package com.walktour.gui.newmap.basestation.util;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * 基站数据导出text格式文件
 *
 * @author jianchao.wang
 */
public class BaseStationExportText extends BaseStationExportBase {
    /**
     * 生成text格式的行内容
     *
     * @param netType 网络类型
     * @param detail  基站详情对象
     * @return 行内容字符串
     */
    protected String createTextRow(int netType, BaseStationDetail detail) {
        StringBuilder sb = new StringBuilder();
        sb.append(detail.main.name).append("\t");
        sb.append(detail.cellId).append("\t");
        sb.append(detail.cellName).append("\t");
        sb.append(detail.main.longitude).append("\t");
        sb.append(detail.main.latitude).append("\t");
        sb.append(detail.bearing).append("\t");
        sb.append(detail.antennaHeight).append("\t");
        switch (netType) {
            case BaseStation.NETTYPE_TDSCDMA:
                sb.append(detail.lac).append("\t");
                sb.append(detail.uarfcn).append("\t");
                sb.append(detail.cpi);
                break;
            case BaseStation.NETTYPE_WCDMA:
                sb.append(detail.lac).append("\t");
                sb.append(detail.psc).append("\t");
                sb.append(detail.uarfcn);
                break;
            case BaseStation.NETTYPE_LTE:
                sb.append(detail.pci).append("\t");
                sb.append(detail.earfcn).append("\t");
                sb.append(detail.main.enodebId).append("\t");
                sb.append(detail.enodebIp).append("\t");
                sb.append(detail.sectorId);
                break;
            case BaseStation.NETTYPE_GSM:
                sb.append(detail.bsic).append("\t");
                sb.append(detail.lac).append("\t");
                sb.append(detail.bcch);
                break;
            case BaseStation.NETTYPE_CDMA:
                sb.append(detail.pn).append("\t");
                sb.append(detail.evPn).append("\t");
                sb.append(detail.nid).append("\t");
                sb.append(detail.bid).append("\t");
                sb.append(detail.sid).append("\t");
                sb.append(detail.frequency).append("\t");
                sb.append(detail.evFreq);
                break;
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 生成text格式的表头
     *
     * @param netType 网络类型
     * @return 表头字符串
     */
    protected String createTextHeader(int netType) {
        StringBuilder sb = new StringBuilder();
        sb.append("SITE NAME\t");
        sb.append("CELL ID\t");
        sb.append("CELL NAME\t");
        sb.append("LONGITUDE\t");
        sb.append("LATITUDE\t");
        sb.append("AZIMUTH\t");
        sb.append("ANTENNA HEIGHT\t");
        switch (netType) {
            case BaseStation.NETTYPE_TDSCDMA:
                sb.append("LAC\t");
                sb.append("UARFCN\t");
                sb.append("CPI");
                break;
            case BaseStation.NETTYPE_WCDMA:
                sb.append("LAC\t");
                sb.append("PSC\t");
                sb.append("UARFCN");
                break;
            case BaseStation.NETTYPE_LTE:
                sb.append("PCI\t");
                sb.append("EARFCN\t");
                sb.append("eNodeB ID\t");
                sb.append("eNodeB IP\t");
                sb.append("SECTOR ID");
                break;
            case BaseStation.NETTYPE_GSM:
                sb.append("BSIC\t");
                sb.append("LAC\t");
                sb.append("BCCH");
                break;
            case BaseStation.NETTYPE_CDMA:
                sb.append("PN\t");
                sb.append("EV PN\t");
                sb.append("NID\t");
                sb.append("BID\t");
                sb.append("SID\t");
                sb.append("Frequency\t");
                sb.append("EV Freq");
                break;
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    protected void exportFile(int netType, String fileName) throws Exception {
        File file = new File(path + File.separator + fileName + ".txt");
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
        try {
            osw.write(this.createTextHeader(netType));
            for (BaseStation bs : this.baseStationList) {
                for (BaseStationDetail detail : bs.details) {
                    osw.write(this.createTextRow(netType, detail));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            osw.close();
            fos.close();
        }
    }
}

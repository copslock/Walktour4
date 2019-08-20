package com.walktour.gui.newmap.basestation.util;

import android.util.Xml;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 基站数据导出kml格式文件
 *
 * @author jianchao.wang
 */
public class BaseStationExportKml extends BaseStationExportBase {
    /**
     * xml编辑类
     */
    private XmlSerializer serializer;

    /**
     * 把基站信息写成地标
     *
     * @param netType 网络类型
     * @param detail  基站详情
     * @throws Exception 异常
     */
    private void writeKmlPlacemark(int netType, BaseStationDetail detail) throws Exception {
        this.serializer.startTag(null, "Placemark");
        this.writeXMLValue("name", detail.main.name);
        this.writeKmlDescription(netType, detail);
        this.serializer.startTag(null, "Point");
        this.writeXMLValue("coordinates", detail.main.longitude + "," + detail.main.latitude + ",0");
        this.serializer.endTag(null, "Point");
        this.serializer.endTag(null, "Placemark");
    }

    /**
     * 把基站详情信息写入说明中
     *
     * @param netType 网络类型
     * @param detail  基站详情
     * @throws Exception 异常
     */
    private void writeKmlDescription(int netType, BaseStationDetail detail) throws Exception {
        this.serializer.startTag(null, "description");
        StringBuilder sb = new StringBuilder();
        sb.append("cellId:").append(detail.cellId).append(",");
        sb.append("cellName:").append(detail.cellName).append(",");
        sb.append("azimuth:").append(detail.bearing).append(",");
        sb.append("antennaHeight:").append(detail.antennaHeight).append(",");
        switch (netType) {
            case BaseStation.NETTYPE_TDSCDMA:
                sb.append("lac:").append(detail.lac).append(",");
                sb.append("uarfcn:").append(detail.uarfcn).append(",");
                sb.append("cpi:").append(detail.cpi);
                break;
            case BaseStation.NETTYPE_WCDMA:
                sb.append("lac:").append(detail.lac).append(",");
                sb.append("psc:").append(detail.psc).append(",");
                sb.append("uarfcn:").append(detail.uarfcn);
                break;
            case BaseStation.NETTYPE_LTE:
                sb.append("pci:").append(detail.pci).append(",");
                sb.append("earfcn:").append(detail.earfcn).append(",");
                sb.append("enodebId:").append(detail.main.enodebId).append(",");
                sb.append("enodebIp:").append(detail.enodebIp).append(",");
                sb.append("sectorId:").append(detail.sectorId);
                break;
            case BaseStation.NETTYPE_GSM:
                sb.append("bsic:").append(detail.bsic).append(",");
                sb.append("lac:").append(detail.lac).append(",");
                sb.append("bcch:").append(detail.bcch);
                break;
            case BaseStation.NETTYPE_CDMA:
                sb.append("pn:").append(detail.pn).append(",");
                sb.append("evPn:").append(detail.evPn).append(",");
                sb.append("nid:").append(detail.nid).append(",");
                sb.append("bid:").append(detail.bid).append(",");
                sb.append("sid:").append(detail.sid).append(",");
                sb.append("frequency:").append(detail.frequency).append(",");
                sb.append("evFreq:").append(detail.evFreq);
                break;
        }
        this.serializer.text(sb.toString());
        this.serializer.endTag(null, "description");
    }

    /**
     * 写xml属性值
     *
     * @param tagName 属性名
     * @param value   属性值
     * @throws Exception 异常
     */
    private void writeXMLValue(String tagName, String value) throws Exception {
        this.serializer.startTag(null, tagName);
        this.serializer.text(value);
        this.serializer.endTag(null, tagName);
    }

    @Override
    protected void exportFile(int netType, String fileName) throws Exception {
        File file = new File(path + File.separator + fileName + ".kml");
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        try {
            this.serializer = Xml.newSerializer();
            this.serializer.setOutput(fos, "GBK");
            this.serializer.startDocument("GBK", true);
            this.serializer.startTag(null, "kml");
            this.serializer.attribute(null, "xmlns", "http://earth.google.com/kml/2.2");
            this.serializer.startTag(null, "Document");
            String name = "";
            switch (netType) {
                case BaseStation.NETTYPE_GSM:
                    name = "GSM";
                    break;
                case BaseStation.NETTYPE_CDMA:
                    name = "CDMA";
                    break;
                case BaseStation.NETTYPE_LTE:
                    name = "LTE";
                    break;
                case BaseStation.NETTYPE_TDSCDMA:
                    name = "TDSCDMA";
                    break;
                case BaseStation.NETTYPE_WCDMA:
                    name = "WCDMA";
                    break;
            }
            name += " Sites";
            this.writeXMLValue("name", name);
            this.serializer.startTag(null, "Folder");
            this.writeXMLValue("name", name);
            this.writeXMLValue("open", "1");
            for (BaseStation bs : this.baseStationList) {
                for (BaseStationDetail detail : bs.details) {
                    this.writeKmlPlacemark(netType, detail);
                }
            }
            this.serializer.endTag(null, "Folder");
            this.serializer.endTag(null, "Document");
            this.serializer.endTag(null, "kml");
            this.serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }
}

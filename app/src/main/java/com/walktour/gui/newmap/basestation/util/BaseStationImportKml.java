package com.walktour.gui.newmap.basestation.util;

import android.util.Xml;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 导入基站数据kml格式
 *
 * @author jianchao.wang
 */
public class BaseStationImportKml extends BaseStationImportBase {

    /**
     * xml解析类
     */
    private XmlPullParser xmlParser;
    /**
     * 基站映射<经纬度,基站对象>
     */
    private Map<String, BaseStation> baseMap = new LinkedHashMap<>();

    @Override
    protected void importFile(File file) throws Exception {
        try {
            if (xmlParser == null) {
                xmlParser = Xml.newPullParser();
            }
            FileInputStream xmlInStream = new FileInputStream(file);
            xmlParser.setInput(xmlInStream, "GBK");
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("Placemark".equals(xmlParser.getName())) {
                            this.readBaseStation();
                        }
                }
                eventType = xmlParser.next();
            }
            this.baseStationList.addAll(this.baseMap.values());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取基站信息
     */
    private void readBaseStation() throws Exception {
        int eventType = xmlParser.getEventType();
        BaseStation base = new BaseStation();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("name".equals(xmlParser.getName())) {
                        base.name = xmlParser.nextText();
                    } else if ("description".equals(xmlParser.getName())) {
                        this.readKmlDescription(base, xmlParser.nextText());
                    } else if ("coordinates".equals(xmlParser.getName())) {
                        String coordinates = xmlParser.nextText();
                        String[] coords = coordinates.split(",");
                        base.longitude = Double.parseDouble(coords[0]);
                        base.latitude = Double.parseDouble(coords[1]);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("Placemark".equals(xmlParser.getName())) {
                        String key = base.longitude + "," + base.latitude;
                        if (!this.baseMap.containsKey(key))
                            this.baseMap.put(key, base);
                        else {
                            this.baseMap.get(key).details.addAll(base.details);
                        }
                        return;
                    }
                    break;
            }
            eventType = xmlParser.next();
        }
    }

    /**
     * 解析kml文件的description属性
     *
     * @param base        基站对象
     * @param description 描述字符串
     */
    private void readKmlDescription(BaseStation base, String description) {
        if (StringUtil.isNullOrEmpty(description))
            return;
        String[] params = description.split(",");
        BaseStationDetail detail = new BaseStationDetail();
        base.details.add(detail);
        for (String param : params) {
            String[] nameValue = param.split(":");
            String name = nameValue[0];
            String value = (nameValue.length == 2 ? nameValue[1] : "");
            if ("cellId".equals(name)) {
                detail.cellId = value;
            } else if ("cellName".equals(name)) {
                detail.cellName = value;
            } else if ("azimuth".equals(name)) {
                detail.bearing = Integer.parseInt(value);
            } else if ("antennaHeight".equals(name)) {
                detail.antennaHeight = Integer.parseInt(value);
            } else if ("lac".equals(name)) {
                detail.lac = value;
            } else if ("uarfcn".equals(name)) {
                detail.uarfcn = value;
            } else if ("cpi".equals(name)) {
                detail.cpi = value;
                base.netType = BaseStation.NETTYPE_TDSCDMA;
            } else if ("psc".equals(name)) {
                detail.psc = value;
                base.netType = BaseStation.NETTYPE_WCDMA;
            } else if ("pci".equals(name)) {
                detail.pci = value;
                base.netType = BaseStation.NETTYPE_LTE;
            } else if ("earfcn".equals(name)) {
                detail.earfcn = value;
            } else if ("enodebId".equals(name)) {
                detail.main.enodebId = value;
            } else if ("enodebIp".equals(name)) {
                detail.enodebIp = value;
            } else if ("sectorId".equals(name)) {
                detail.sectorId = value;
            } else if ("bsic".equals(name)) {
                detail.bsic = value;
                base.netType = BaseStation.NETTYPE_GSM;
            } else if ("bcch".equals(name)) {
                detail.bcch = value;
            } else if ("pn".equals(name)) {
                detail.pn = value;
                base.netType = BaseStation.NETTYPE_CDMA;
            } else if ("evPn".equals(name)) {
                detail.evPn = value;
            } else if ("nid".equals(name)) {
                detail.nid = value;
            } else if ("bid".equals(name)) {
                detail.bid = value;
            } else if ("sid".equals(name)) {
                detail.sid = value;
            } else if ("frequency".equals(name)) {
                detail.frequency = value;
            } else if ("evFreq".equals(name)) {
                detail.evFreq = value;
            }
        }
    }
}

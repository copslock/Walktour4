package com.walktour.gui.newmap.basestation.util;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.newmap.basestation.ImportBaseStationFilterManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 基站数据导入txt格式文件
 *
 * @author jianchao.wang
 */
public class BaseStationImportText extends BaseStationImportBase {

    @Override
    protected void importFile(File file) throws Exception {
        String lineStr;
        String[] arr;
        int count = 0;
        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            LogUtil.w("FilePath:", file.getAbsolutePath());
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            Map<COLNAME, Integer> colMap = null;
            Map<String, BaseStation> baseMap = new HashMap<>();
            int netType = -1;
            while ((lineStr = br.readLine()) != null) {
                count++;
                arr = lineStr.split("\t");
                if (count == 1) {
                    colMap = this.getColMap(arr);
                    if (colMap == null)
                        return;
                } else if (arr.length <= 2) {
                    continue;
                } else {
                    if (count == 2) {// 根据相关列是否有值来判断网络类型
                        if (this.isColsHasValue(arr, colMap, COLNAME.ENBID_LCELLID)) {
                            netType = BaseStation.NETTYPE_NBIOT;
                        } else if (this.isColsHasValue(arr, colMap, COLNAME.BCCH, COLNAME.BSIC)) {
                            netType = BaseStation.NETTYPE_GSM;
                        } else if (this.isColsHasValue(arr, colMap, COLNAME.PSC)) {
                            netType = BaseStation.NETTYPE_WCDMA;
                        } else if (this.isColsHasValue(arr, colMap, COLNAME.CPI)) {
                            netType = BaseStation.NETTYPE_TDSCDMA;
                        } else if (this.isColsHasValue(arr, colMap, COLNAME.PCI)) {
                            netType = BaseStation.NETTYPE_LTE;
                        } else if (this.isColsHasValue(arr, colMap, COLNAME.PN)) {
                            netType = BaseStation.NETTYPE_CDMA;
                        }
                    }
                    if (netType == -1)
                        return;
                    this.parseBaseStation(baseMap, arr, colMap, netType);
                }
            }
            this.baseStationList.addAll(baseMap.values());
            baseMap.clear();
            TraceInfoInterface.traceData.cleanCellIDHmKey();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br = null;
            }
        }
    }

    /**
     * 生成基站对象
     *
     * @param values  数据
     * @param colMap  列映射
     * @param netType 网络类型
     * @return 基站对象
     */
    private BaseStation createBaseStation(String[] values, Map<COLNAME, Integer> colMap, int netType) {
        BaseStation base = new BaseStation();
        base.latitude = this.getDoubleValue(values, colMap, COLNAME.LAT);
        base.longitude = this.getDoubleValue(values, colMap, COLNAME.LOT);
        base.name = this.getStringValue(values, colMap, COLNAME.SITENAME);
        base.netType = netType;
        switch (netType) {
            case BaseStation.NETTYPE_LTE:
                base.enodebId = this.getStringValue(values, colMap, COLNAME.ENODEBID);
                break;
            case BaseStation.NETTYPE_NBIOT:
                base.enodebId = this.getStringValue(values, colMap, COLNAME.ENODEBID);
                break;
        }
        return base;
    }

    /**
     * 生成基站明细对象
     *
     * @param base    主表对象
     * @param values  数据
     * @param colMap  列映射
     * @param netType 网络类型
     */
    private void createBaseStationDetail(BaseStation base, String[] values, Map<COLNAME, Integer> colMap, int netType) {
        BaseStationDetail detail = new BaseStationDetail();
        detail.bearing = this.getIntValue(values, colMap, COLNAME.AZIMUTH);
        detail.antennaHeight = this.getIntValue(values, colMap, COLNAME.ANTENNAHEIGHT);
        detail.cellId = this.getStringValue(values, colMap, COLNAME.CELLID);
        detail.cellName = this.getStringValue(values, colMap, COLNAME.CELLNAME);
        switch (netType) {
            case BaseStation.NETTYPE_TDSCDMA:
                detail.lac = this.getStringValue(values, colMap, COLNAME.LAC);
                detail.uarfcn = this.getStringValue(values, colMap, COLNAME.UARFCN);
                detail.cpi = this.getStringValue(values, colMap, COLNAME.CPI);
                break;
            case BaseStation.NETTYPE_WCDMA:
                detail.lac = this.getStringValue(values, colMap, COLNAME.LAC);
                detail.psc = this.getStringValue(values, colMap, COLNAME.PSC);
                detail.uarfcn = this.getStringValue(values, colMap, COLNAME.UARFCN);
                break;
            case BaseStation.NETTYPE_LTE:
                detail.pci = this.getStringValue(values, colMap, COLNAME.PCI);
                detail.earfcn = this.getStringValue(values, colMap, COLNAME.EARFCN);
                detail.enodebIp = this.getStringValue(values, colMap, COLNAME.ENODEBIP);
                detail.sectorId = this.getStringValue(values, colMap, COLNAME.SECTORID);
                break;
            case BaseStation.NETTYPE_GSM:
                detail.bsic = this.getStringValue(values, colMap, COLNAME.BSIC);
                detail.lac = this.getStringValue(values, colMap, COLNAME.LAC);
                detail.bcch = this.getStringValue(values, colMap, COLNAME.BCCH);
                break;
            case BaseStation.NETTYPE_CDMA:
                detail.pn = this.getStringValue(values, colMap, COLNAME.PN);
                detail.evPn = this.getStringValue(values, colMap, COLNAME.EVPN);
                detail.nid = this.getStringValue(values, colMap, COLNAME.NID);
                detail.bid = this.getStringValue(values, colMap, COLNAME.BID);
                detail.sid = this.getStringValue(values, colMap, COLNAME.SID);
                detail.frequency = this.getStringValue(values, colMap, COLNAME.FREQUENCY);
                detail.evFreq = this.getStringValue(values, colMap, COLNAME.EVFREQ);
                break;
            case BaseStation.NETTYPE_NBIOT:
                detail.pci = this.getStringValue(values, colMap, COLNAME.PCI);
                detail.earfcn = this.getStringValue(values, colMap, COLNAME.EARFCN);
                detail.enodebIp = this.getStringValue(values, colMap, COLNAME.ENODEBIP);
                detail.sectorId = this.getStringValue(values, colMap, COLNAME.SECTORID);
                break;
        }

        detail.main = base;
        if(netType!=BaseStation.NETTYPE_LTE) {
            base.details.add(detail);
        }else {
//            LogUtil.e("BaseStationImportText", "detail name:" + detail.cellName + ",pci:" + detail.pci + ",earfcn:" + detail.earfcn);
//            int pci = Integer.parseInt(detail.pci);
//            int earfcn = Integer.parseInt(detail.earfcn);
//            ImportBaseStationFilterManager filterManager = ImportBaseStationFilterManager.getInstance();
//            LogUtil.e("BaseStationImportText", filterManager.toString());
//            if (pci >= filterManager.getMinPCI() && pci <= filterManager.getMaxPCI()
//                    && earfcn >= filterManager.getMinEARFCN() && earfcn <= filterManager.getMaxEARFCN()) {
                //添加筛选，符合的小区才添加进去，过滤挪到地图过滤
                base.details.add(detail);
//                LogUtil.e("BaseStationImportText", "add detail name:" + detail.cellName + ",pci:" + detail.pci + ",earfcn:" + detail.earfcn);
//            }
        }

    }


    /**
     * 判断指定列是否都有值
     *
     * @param values   列值
     * @param colMap   列名映射
     * @param colNames 指定列
     * @return 是否都有值
     */
    private boolean isColsHasValue(String[] values, Map<COLNAME, Integer> colMap, COLNAME... colNames) {
        int count = 0;
        for (COLNAME colName : colNames) {
            if (colMap.containsKey(colName)) {
                int index = colMap.get(colName);
                if (!StringUtil.isNullOrEmpty(values[index])) {
                    count++;
                }
            }
        }
        return (count == colNames.length);
    }

    /**
     * 获取列名映射值
     *
     * @param colNames 列名数组
     * @return 映射值
     */
    private Map<COLNAME, Integer> getColMap(String[] colNames) {
        Map<COLNAME, Integer> colMap = new HashMap<COLNAME, Integer>();
        colMap.put(COLNAME.SITENAME,0);//第一列默认为基站名
        this.checkColName(colMap, COLNAME.LOT, colNames, "LONGITUDE", "Y");
        this.checkColName(colMap, COLNAME.LAT, colNames, "LATITUDE", "X");
//        this.checkColName(colMap, COLNAME.SITENAME, colNames, "SITE NAME");
        this.checkColName(colMap, COLNAME.CELLNAME, colNames, "CELL NAME", "CELLNAME");
        this.checkColName(colMap, COLNAME.AZIMUTH, colNames, "AZIMUTH");
        this.checkColName(colMap, COLNAME.CELLID, colNames, "CELL ID", "CELLID", "Local CellID", "Local CELLID");
        this.checkColName(colMap, COLNAME.BCCH, colNames, "BCCH");
        this.checkColName(colMap, COLNAME.BSIC, colNames, "BSIC");
        this.checkColName(colMap, COLNAME.LAC, colNames, "LAC");
        this.checkColName(colMap, COLNAME.CPI, colNames, "CPI");
        this.checkColName(colMap, COLNAME.UARFCN, colNames, "UARFCN");
        this.checkColName(colMap, COLNAME.PSC, colNames, "PSC");
        this.checkColName(colMap, COLNAME.PN, colNames, "PN");
        this.checkColName(colMap, COLNAME.FREQUENCY, colNames, "Frequency");
        this.checkColName(colMap, COLNAME.EVFREQ, colNames, "EV Freq");
        this.checkColName(colMap, COLNAME.EVPN, colNames, "EV PN");
        this.checkColName(colMap, COLNAME.BID, colNames, "BID");
        this.checkColName(colMap, COLNAME.NID, colNames, "NID");
        this.checkColName(colMap, COLNAME.SID, colNames, "SID");
        this.checkColName(colMap, COLNAME.ANTENNAHEIGHT, colNames, "ANTENNA HEIGHT");
        this.checkColName(colMap, COLNAME.EARFCN, colNames, "EARFCN");
        this.checkColName(colMap, COLNAME.PCI, colNames, "PCI");
        this.checkColName(colMap, COLNAME.ENODEBID, colNames, "eNodeB ID");
        this.checkColName(colMap, COLNAME.ENODEBIP, colNames, "eNodeB IP");
        this.checkColName(colMap, COLNAME.SECTORID, colNames, "SECTOR ID", "SectorID");
        this.checkColName(colMap, COLNAME.ENBID_LCELLID, colNames, "eNBID_LCellID");
        return colMap;
    }

    /**
     * 判断列名是否存在
     *
     * @param colMap     列名映射表
     * @param name       映射名
     * @param colNames   列名数组
     * @param checkNames 要校验的名称
     */
    private void checkColName(Map<COLNAME, Integer> colMap, COLNAME name, String[] colNames, String... checkNames) {
        for (int index = 0; index < colNames.length; index++) {
            for (String checkName : checkNames) {
                if (checkName.equalsIgnoreCase(colNames[index])) {
                    colMap.put(name, index);
                    return;
                }
            }
        }
    }

    /**
     * 解析基站数据
     *
     * @param baseMap 基站映射
     * @param values  数据
     * @param colMap  列名映射
     * @param netType 网络类型
     */
    private void parseBaseStation(Map<String, BaseStation> baseMap, String[] values, Map<COLNAME, Integer> colMap,
                                  int netType) {
        BaseStation base;
        String key = this.getStringValue(values, colMap, COLNAME.LOT) + "_"
                + this.getStringValue(values, colMap, COLNAME.LAT);
        if (baseMap.containsKey(key)) {
            base = baseMap.get(key);
        } else {
            base = this.createBaseStation(values, colMap, netType);
            baseMap.put(key, base);
        }
        this.createBaseStationDetail(base, values, colMap, netType);
    }

}

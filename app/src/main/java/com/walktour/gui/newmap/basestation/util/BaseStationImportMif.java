package com.walktour.gui.newmap.basestation.util;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入基站数据mapinfo格式
 *
 * @author jianchao.wang
 */
public class BaseStationImportMif extends BaseStationImportBase {
    /**
     * 属性列名
     */
    private List<String> colNameList = new ArrayList<>();

    @Override
    protected void importFile(File file) throws Exception {
        File midFile = new File(file.getAbsolutePath().replaceAll("mif", "mid"));
        if (!midFile.exists())
            return;
        boolean flag = this.importMifFile(file);
        if (!flag)
            return;
        this.importMidFile(midFile);
    }

    /**
     * 导入mid文件
     *
     * @param midFile 文件对象
     */
    private void importMidFile(File midFile) {
        String lineStr;
        String[] arr;
        BufferedReader br = null;
        List<BaseStationDetail> detailList = new ArrayList<BaseStationDetail>();
        for (BaseStation base : this.baseStationList) {
            for (BaseStationDetail detail : base.details) {
                detailList.add(detail);
            }
        }
        try {
            FileInputStream fis = new FileInputStream(midFile);
            LogUtil.w("FilePath:", midFile.getAbsolutePath());
            br = new BufferedReader(new InputStreamReader(fis, "GBK"));
            int pos = 0;
            while ((lineStr = br.readLine()) != null) {
                lineStr = lineStr.trim();
                if (lineStr.length() == 0)
                    continue;
                if (pos >= detailList.size())
                    break;
                arr = lineStr.split(",");
                if (arr.length != this.colNameList.size())
                    break;
                BaseStationDetail detail = detailList.get(pos);
                for (int i = 0; i < this.colNameList.size(); i++) {
                    String colName = this.colNameList.get(i);
                    String value = arr[i].replaceAll("\"", "");
                    if ("SITE_NAME".equals(colName)) {
                        detail.main.name = value;
                    } else if ("CELL_ID".equals(colName)) {
                        detail.cellId = value;
                    } else if ("CELL_NAME".equals(colName)) {
                        detail.cellName = value;
                    } else if ("AZIMUTH".equals(colName)) {
                        detail.bearing = Integer.parseInt(value);
                    } else if ("ANTENNA_HEIGHT".equals(colName)) {
                        detail.antennaHeight = Integer.parseInt(value);
                    } else if ("LAC".equals(colName)) {
                        detail.lac = value;
                    } else if ("UARFCN".equals(colName)) {
                        detail.uarfcn = value;
                    } else if ("CPI".equals(colName)) {
                        detail.cpi = value;
                    } else if ("PSC".equals(colName)) {
                        detail.psc = value;
                    } else if ("PCI".equals(colName)) {
                        detail.pci = value;
                    } else if ("EARFCN".equals(colName)) {
                        detail.earfcn = value;
                    } else if ("eNodeB_ID".equals(colName)) {
                        detail.main.enodebId = value;
                    } else if ("eNodeB_IP".equals(colName)) {
                        detail.enodebIp = value;
                    } else if ("SECTOR_ID".equals(colName)) {
                        detail.sectorId = value;
                    } else if ("BSIC".equals(colName)) {
                        detail.bsic = value;
                    } else if ("BCCH".equals(colName)) {
                        detail.bcch = value;
                    } else if ("PN".equals(colName)) {
                        detail.pn = value;
                    } else if ("EV_PN".equals(colName)) {
                        detail.evPn = value;
                    } else if ("NID".equals(colName)) {
                        detail.nid = value;
                    } else if ("BID".equals(colName)) {
                        detail.bid = value;
                    } else if ("SID".equals(colName)) {
                        detail.sid = value;
                    } else if ("Frequency".equals(colName)) {
                        detail.frequency = value;
                    } else if ("EV_Freq".equals(colName)) {
                        detail.evFreq = value;
                    }
                }
                pos++;
            }
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
     * 导入MIf文件
     *
     * @param file 文件对象
     * @return 是否导入成功
     */
    private boolean importMifFile(File file) {
        String lineStr;
        String[] arr;
        BufferedReader br = null;
        this.colNameList.clear();
        try {
            FileInputStream fis = new FileInputStream(file);
            LogUtil.w("FilePath:", file.getAbsolutePath());
            br = new BufferedReader(new InputStreamReader(fis, "GBK"));
            Map<String, BaseStation> baseMap = new LinkedHashMap<String, BaseStation>();
            int netType = -1;
            boolean isColumns = false;
            boolean isData = false;
            while ((lineStr = br.readLine()) != null) {
                lineStr = lineStr.trim();
                if (lineStr.length() == 0)
                    continue;
                if (lineStr.startsWith("Columns")) {
                    arr = lineStr.split(" ");
                    isColumns = true;
                    continue;
                }
                if (isColumns && !lineStr.equals("Data")) {
                    arr = lineStr.split(" ");
                    colNameList.add(arr[0]);
                }
                if (lineStr.equals("Data")) {
                    // 根据相关列是否有值来判断网络类型
                    if (colNameList.contains("CPI")) {
                        netType = BaseStation.NETTYPE_TDSCDMA;
                    } else if (colNameList.contains("PSC")) {
                        netType = BaseStation.NETTYPE_WCDMA;
                    } else if (colNameList.contains("BSIC")) {
                        netType = BaseStation.NETTYPE_GSM;
                    } else if (colNameList.contains("PN")) {
                        netType = BaseStation.NETTYPE_CDMA;
                    } else if (colNameList.contains("PCI")) {
                        netType = BaseStation.NETTYPE_LTE;
                    }
                    if (netType == -1)
                        return false;
                    isColumns = false;
                    isData = true;
                    continue;
                }
                if (isData) {
                    if (lineStr.startsWith("Point")) {
                        arr = lineStr.split(" ");
                        String key = arr[1] + "," + arr[2];
                        if (!baseMap.containsKey(key)) {
                            BaseStation base = new BaseStation();
                            base.netType = netType;
                            base.longitude = Double.parseDouble(arr[1]);
                            base.latitude = Double.parseDouble(arr[2]);
                            baseMap.put(key, base);
                        }
                        BaseStationDetail detail = new BaseStationDetail();
                        detail.main = baseMap.get(key);
                        baseMap.get(key).details.add(detail);
                    }
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

        return true;
    }

}

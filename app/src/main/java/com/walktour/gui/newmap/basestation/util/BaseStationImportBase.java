package com.walktour.gui.newmap.basestation.util;

import com.walktour.Utils.StringUtil;
import com.walktour.control.bean.Verify;
import com.walktour.framework.database.model.BaseStation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基站导入基础类
 *
 * @author jianchao.wang
 */
public abstract class BaseStationImportBase {
	/**
	 * 解析基站数据文件使用的映射列名
	 */
	public enum COLNAME {
		LOT, LAT, SITENAME, CELLNAME, AZIMUTH, CELLID, BCCH, BSIC, LAC, CPI, //
		UARFCN, PSC, PN, FREQUENCY, EVFREQ, EVPN, BID, NID, SID, ANTENNAHEIGHT, //
		EARFCN, PCI, ENODEBID, ENODEBIP, SECTORID,ENBID_LCELLID
	}

    /**
     * 要导入的基站列表
     */
    protected List<BaseStation> baseStationList = new ArrayList<BaseStation>();

    /**
     * 导入基站数据文件
     *
     * @param fileName 文件绝对路径
     * @throws Exception
     */
    public void importFile(String fileName) throws Exception {
        File file = new File(fileName);
        if (!file.exists())
            return;
        this.importFile(file);
    }

    /**
     * 导入基站数据文件
     *
     * @param file 文件对象
     * @throws Exception
     */
    protected abstract void importFile(File file) throws Exception;

    public List<BaseStation> getBaseStationList() {
        return baseStationList;
    }


	/**
	 * 获取整数值
	 *
	 * @param values  列值
	 * @param colMap  列名映射
	 * @param colName 指定列
	 * @return 整型值
	 */
	public int getIntValue(String[] values, Map<COLNAME, Integer> colMap, COLNAME colName) {
		String value = this.getStringValue(values, colMap, colName);
		if (value.length() == 0 || !Verify.checknum(value))
			return 0;
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获取双精度数值
	 *
	 * @param values  列值
	 * @param colMap  列名映射
	 * @param colName 指定列
	 * @return 双精度数值
	 */
	public double getDoubleValue(String[] values, Map<COLNAME, Integer> colMap, COLNAME colName) {
		String value = this.getStringValue(values, colMap, colName);
		if (value.length() == 0 || !Verify.checknum(value))
			return 0;
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 获取字符串值
	 *
	 * @param values  列值
	 * @param colMap  列名映射
	 * @param colName 指定列
	 * @return 字符串值
	 */
	public String getStringValue(String[] values, Map<COLNAME, Integer> colMap, COLNAME colName) {
		if (colMap.containsKey(colName)) {
			int index = colMap.get(colName);
			if (index < values.length && !StringUtil.isNullOrEmpty(values[index])) {
				return values[index].trim();
			}
		}
		return "";
	}


}

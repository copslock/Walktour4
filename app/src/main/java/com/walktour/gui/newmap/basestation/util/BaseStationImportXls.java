package com.walktour.gui.newmap.basestation.util;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 基站数据导入xls格式
 *
 * @author zhicheng.chen
 */
public class BaseStationImportXls extends BaseStationImportBase {

	private static final String EXTENSION_XLS = "xls";
	private static final String EXTENSION_XLSX = "xlsx";
	private Workbook mWorkbook;
	private Map<COLNAME, Integer> mColMap = new HashMap<COLNAME, Integer>();

	@Override
	protected void importFile(File file) throws Exception {
		mWorkbook = getWorkbook(file.getAbsolutePath());
		Map<String, BaseStation> baseMap = new HashMap<>();
		for (int i = 0; i < mWorkbook.getNumberOfSheets(); i++) {
			Sheet sheet = mWorkbook.getSheetAt(i);

			int firstRowIndex = sheet.getFirstRowNum();
			int lastRowIndex = sheet.getPhysicalNumberOfRows();

			// 读取首行 即,表头
			Row firstRow = sheet.getRow(firstRowIndex);
			String[] columns = new String[firstRow.getLastCellNum() - firstRow.getFirstCellNum() + 1];
			for (int j = firstRow.getFirstCellNum(); j < firstRow.getLastCellNum(); j++) {
				Cell cell = firstRow.getCell(j);
				columns[j] = getCellValueByCell(cell);
			}
			getColMap(columns);
			//第0行是列名
			for (int r = firstRowIndex + 1; r < lastRowIndex; r++) {
				Row currentRow = sheet.getRow(r);
				int netType = -1;
				String[] rows = new String[columns.length];
				for (int l = firstRow.getFirstCellNum(); l < firstRow.getLastCellNum(); l++) {
					Cell cell = currentRow.getCell(l);
					rows[l] = getCellValueByCell(cell);
				}
				if (this.isColsHasValue(rows, COLNAME.ENBID_LCELLID)) {
					netType = BaseStation.NETTYPE_NBIOT;
				} else if (this.isColsHasValue(rows, COLNAME.BCCH, COLNAME.BSIC)) {
					netType = BaseStation.NETTYPE_GSM;
				} else if (this.isColsHasValue(rows, COLNAME.PSC)) {
					netType = BaseStation.NETTYPE_WCDMA;
				} else if (this.isColsHasValue(rows, COLNAME.CPI)) {
					netType = BaseStation.NETTYPE_TDSCDMA;
				} else if (this.isColsHasValue(rows, COLNAME.PCI)) {
					netType = BaseStation.NETTYPE_LTE;
				} else if (this.isColsHasValue(rows, COLNAME.PN)) {
					netType = BaseStation.NETTYPE_CDMA;
				}
				if (netType == -1)
					continue;
				this.parseBaseStation(baseMap, rows, mColMap, netType);
			}
		}
		this.baseStationList.addAll(baseMap.values());
		TraceInfoInterface.traceData.cleanCellIDHmKey();
	}

	private Workbook getWorkbook(String filePath) throws IOException {
		Workbook workbook = null;
		InputStream is = new FileInputStream(filePath);
		if (filePath.endsWith(EXTENSION_XLS)) {
			workbook = new HSSFWorkbook(is);
		} else if (filePath.endsWith(EXTENSION_XLSX)) {
			workbook = new XSSFWorkbook(is);
		}
		return workbook;
	}

	//获取单元格各类型值，返回字符串类型
	private static String getCellValueByCell(Cell cell) {
		//判断是否为null或空串
		if (cell == null) {
			return "";
		}
		String cellValue = "";
		CellType celltype = cell.getCellTypeEnum();

		if (celltype == CellType.STRING) {
			cellValue = cell.getStringCellValue().trim();
			cellValue = com.walktour.base.util.StringUtil.isEmpty(cellValue) ? "" : cellValue;
		} else if (celltype == CellType.BOOLEAN) {
			cellValue = String.valueOf(cell.getBooleanCellValue());
		} else if (celltype == CellType.NUMERIC) {
			if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {  //判断日期类型
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				cellValue = sdf.format(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue())).toString();
			} else {
				cellValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
			}
		} else if (celltype == CellType.FORMULA) {
			cellValue = cell.getCellFormula() + "";
		} else {
			cellValue = "";
		}
		return cellValue;
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
		base.details.add(detail);
	}


	/**
	 * 获取列名映射值
	 *
	 * @param colNames 列名数组
	 * @return 映射值
	 */
	private void getColMap(String[] colNames) {
		this.checkColName(COLNAME.LOT, colNames, "LONGITUDE", "Y");
		this.checkColName(COLNAME.LAT, colNames, "LATITUDE", "X");
		this.checkColName(COLNAME.SITENAME, colNames, "SITE NAME");
		this.checkColName(COLNAME.CELLNAME, colNames, "CELL NAME", "CELLNAME");
		this.checkColName(COLNAME.AZIMUTH, colNames, "AZIMUTH");
		this.checkColName(COLNAME.CELLID, colNames, "CELL ID", "CELLID", "Local CellID", "Local CELLID");
		this.checkColName(COLNAME.BCCH, colNames, "BCCH");
		this.checkColName(COLNAME.BSIC, colNames, "BSIC");
		this.checkColName(COLNAME.LAC, colNames, "LAC");
		this.checkColName(COLNAME.CPI, colNames, "CPI");
		this.checkColName(COLNAME.UARFCN, colNames, "UARFCN");
		this.checkColName(COLNAME.PSC, colNames, "PSC");
		this.checkColName(COLNAME.PN, colNames, "PN");
		this.checkColName(COLNAME.FREQUENCY, colNames, "Frequency");
		this.checkColName(COLNAME.EVFREQ, colNames, "EV Freq");
		this.checkColName(COLNAME.EVPN, colNames, "EV PN");
		this.checkColName(COLNAME.BID, colNames, "BID");
		this.checkColName(COLNAME.NID, colNames, "NID");
		this.checkColName(COLNAME.SID, colNames, "SID");
		this.checkColName(COLNAME.ANTENNAHEIGHT, colNames, "ANTENNA HEIGHT");
		this.checkColName(COLNAME.EARFCN, colNames, "EARFCN");
		this.checkColName(COLNAME.PCI, colNames, "PCI");
		this.checkColName(COLNAME.ENODEBID, colNames, "eNodeB ID");
		this.checkColName(COLNAME.ENODEBIP, colNames, "eNodeB IP");
		this.checkColName(COLNAME.SECTORID, colNames, "SECTOR ID", "SectorID");
		this.checkColName(COLNAME.ENBID_LCELLID, colNames, "eNBID_LCellID");
	}

	/**
	 * 判断列名是否存在
	 *
	 * @param name       映射名
	 * @param colNames   列名数组
	 * @param checkNames 要校验的名称
	 */
	private void checkColName(COLNAME name, String[] colNames, String... checkNames) {
		for (int index = 0; index < colNames.length; index++) {
			for (String checkName : checkNames) {
				if (checkName.equalsIgnoreCase(colNames[index])) {
					if (!mColMap.containsKey(name)) {
						mColMap.put(name, index);
						break;
					}
				}
			}
		}
	}

	/**
	 * 判断指定列是否都有值
	 *
	 * @param values   列值
	 * @param colNames 指定列
	 * @return 是否都有值
	 */
	private boolean isColsHasValue(String[] values, COLNAME... colNames) {
		int count = 0;
		for (COLNAME colName : colNames) {
			if (mColMap.containsKey(colName)) {
				int index = mColMap.get(colName);
				if (!StringUtil.isNullOrEmpty(values[index])) {
					count++;
				}
			}
		}
		return (count == colNames.length);
	}
}

package com.walktour.gui.report.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.report.model.KpiReportModel;
import com.walktour.gui.report.model.TotalKpiSettingModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * ExcelKpi读取配置
 * 
 * @author zhihui.lian
 *
 */
@SuppressLint("SdCardPath")
public class ConfigExcelKpi {

	private MyXMLWriter writer;
	/** 从文件读取到内存 */
	private Document doc = null;
	/** CQT列表 */
	private ArrayList<KpiReportModel> cqtModels = new ArrayList<>();
	/** DT列表 */
	private ArrayList<KpiReportModel> dtModels = new ArrayList<>();
	/** 共享配置 */
	private SharedPreferences preferences;
	/** 唯一实例 */
	private static ConfigExcelKpi configExcelKpi = null;

	private ConfigExcelKpi(Context context) {
		this.writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_totalexcelkpi.xml"));
		initialParameter(context);
	}

	/**
	 * 初始化参数
	 */
	public void initialParameter(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		doc = writer.getDocument();
		// 读取配置文件
		readXmlFile();
	}

	/**
	 * 读取XMl文件
	 */
	private void readXmlFile() {
		Deviceinfo device = Deviceinfo.getInstance();
		NetType netType = NetType.getNetTypeByID(device.getNettype());
		String netTab = "";
		switch (netType) {
		case CDMA:
		case EVDO:
			netTab = "-C";
			break;
		case TDSCDMA:
		case LTETDD:
			netTab = "-TD";
			break;
		case WCDMA:
		case LTE:
			netTab = "-W";
			break;
		default:
			netTab = "";
			break;
		}
		Element e = doc.getDocumentElement();
		NodeList nodelist = e.getElementsByTagName("DTParams" + netTab);
		if (nodelist.getLength() > 0) {
			Element elment = (Element) nodelist.item(0);
			NodeList dtNodeList = elment.getElementsByTagName("kpi");
			for (int i = 0; i < dtNodeList.getLength(); i++) {
				KpiReportModel excelKpiModel = setNodeList(dtNodeList, i);
				dtModels.add(excelKpiModel);
			}
		}

		NodeList cqtNodelist = e.getElementsByTagName("CQTParams" + netTab);
		if (cqtNodelist.getLength() > 0) {
			Element cqtElment = (Element) cqtNodelist.item(0);
			NodeList cqtNodeList = cqtElment.getElementsByTagName("kpi");
			for (int i = 0; i < cqtNodeList.getLength(); i++) {
				KpiReportModel excelKpiModel = setNodeList(cqtNodeList, i);
				cqtModels.add(excelKpiModel);
			}
		}

	}

	/**
	 * 封装model
	 * 
	 * @param nodeList
	 * @param item
	 * @return
	 */
	private KpiReportModel setNodeList(NodeList nodeList, int item) {
		KpiReportModel excelKpiModel = new KpiReportModel();
		excelKpiModel.mShowName = nodeList.item(item).getAttributes().getNamedItem("kpiShowName").getNodeValue();
		String scale = nodeList.item(item).getAttributes().getNamedItem("scale").getNodeValue();
		excelKpiModel.mMolecule = nodeList.item(item).getAttributes().getNamedItem("molecule").getNodeValue();
		excelKpiModel.mDenominator = nodeList.item(item).getAttributes().getNamedItem("denominator").getNodeValue();
		excelKpiModel.mUnits = nodeList.item(item).getAttributes().getNamedItem("units").getNodeValue();
		String paramType = nodeList.item(item).getAttributes().getNamedItem("paramType").getNodeValue();
		String xlsSheets = nodeList.item(item).getAttributes().getNamedItem("xlsSheets").getNodeValue();
		String xlsCols = nodeList.item(item).getAttributes().getNamedItem("xlsCols").getNodeValue();
		String xlsRows = nodeList.item(item).getAttributes().getNamedItem("xlsRows").getNodeValue();
		excelKpiModel.mMarkKey = nodeList.item(item).getAttributes().getNamedItem("markKey").getNodeValue();
		if (scale.trim().length() != 0) {
			excelKpiModel.mScale = Float.valueOf(scale);
		}
		excelKpiModel.mParamType = Integer.valueOf(paramType);
		excelKpiModel.mXlsSheets = Integer.valueOf(xlsSheets);
		excelKpiModel.mXlsCols = Integer.valueOf(xlsCols);
		excelKpiModel.mXlsRows = Integer.valueOf(xlsRows);
		return excelKpiModel;
	}

	/**
	 * 获取配置项
	 * 
	 * @return
	 */
	public TotalKpiSettingModel getTotalExcelModel() {
		TotalKpiSettingModel totalKpiSettingModel = new TotalKpiSettingModel();
		totalKpiSettingModel.setShowReport(preferences.getBoolean(WalktourConst.TOTAL_SETTING_REPORT_ISSHOW_REPORT, false));
		totalKpiSettingModel.setReportTemplate(preferences.getInt(WalktourConst.TOTAL_SETTING_REPORT_TOTALTEMPLATE, 0));
		totalKpiSettingModel.setTotalNetWork(preferences.getInt(WalktourConst.TOTAL_SETTING_REPORT_TOTALNETWORK, 0));
		totalKpiSettingModel.setReportType(preferences.getInt(WalktourConst.TOTAL_SETTING_REPORT_REPORTTYPE, 0));
		return totalKpiSettingModel;
	}

	/**
	 * 获取CQT Model队列
	 * 
	 * @param reportType
	 *          报告类型
	 */
	public List<KpiReportModel> getKpiModelList(int reportType) {
		if (reportType == TotalKpiSettingModel.REPORT_TEMPLATE_CQT) {
			return cqtModels;
		} else {
			return dtModels;
		}
	}

	/**
	 * 获取ConfigKpi单例
	 * 
	 * @return
	 */
	public synchronized static ConfigExcelKpi getInstance(Context context) {
		if (configExcelKpi == null) {
			configExcelKpi = new ConfigExcelKpi(context);
		}
		return configExcelKpi;
	}

}
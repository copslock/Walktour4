package com.walktour.gui.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.report.config.ConfigExcelKpi;
import com.walktour.gui.report.model.KpiReportModel;
import com.walktour.gui.report.model.TotalKpiSettingModel;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.model.TotalCustomModel;
import com.walktour.model.TotalCustomModel.OneEvent;
import com.walktour.model.TotalMeasureModel;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * 报表导出工厂类
 * 
 * @author jianchao.wang
 *
 */
@SuppressWarnings("rawtypes")
public class ReportFactory {
	/** 反馈消息：生成报表文件成功 */
	public static final int EXTAR_SHOW_EXPORT_REPORT_SUCCESS = 1;
	/** 反馈消息:生成预览html成功 */
	public static final int EXTAR_SHOW_EXPORT_HTML_SUCCESS = 2;
	/** 报表类型：word */
	public static final int REPORT_TYPE_WORD = 1;
	/** 报表类型：excel */
	public static final int REPORT_TYPE_EXCEL = 2;
	/** 唯一实例 */
	private static ReportFactory sInstance = null;
	/** 当前执行的报表对象 */
	private Object mCurrReportObj;
	/** 当前执行的报表类 */
	private Class mCurrReportClass;
	/** 配置文件类 */
	private ConfigExcelKpi mConfig;
	/** dex文件加载路径 */
	private DexClassLoader mLoader = null;
	/** dex文件名 */
	private final static String DEX_FILE_NAME = "walktour_report.apk";

	private ReportFactory(Context context) {
		this.mConfig = ConfigExcelKpi.getInstance(context);
		String filePath = ConfigRoutine.getInstance().getStorgePath();
		filePath += "dex" + File.separator + DEX_FILE_NAME;
		File dexOutputDir = context.getDir("dex", 0);
		UtilsMethod.writeRawResource(context.getApplicationContext(), R.raw.walktour_report, new File(filePath), true);

		this.mLoader = new DexClassLoader(filePath, dexOutputDir.getAbsolutePath(), null,
				ClassLoader.getSystemClassLoader().getParent());
	}

	/**
	 * 返回唯一实例
	 * 
	 * @return
	 */
	public static ReportFactory getInstance(Context context) {
		if (sInstance == null)
			sInstance = new ReportFactory(context);
		return sInstance;
	}

	/**
	 * 获得报表对象
	 * 
	 * @param context
	 *          上下文
	 * @param settingModel
	 *          设置对象
	 * @param handler
	 *          消息处理句柄
	 * @return
	 */
	@SuppressLint("NewApi")
	public void createReport(Context context, TotalKpiSettingModel settingModel, Handler handler) {
		this.mCurrReportObj = null;
		List<KpiReportModel> kpiList = this.mConfig.getKpiModelList(settingModel.getReportType());
		if (kpiList != null && !kpiList.isEmpty()) {
			for (KpiReportModel model : kpiList) {
				this.setKpiValues(model);
			}
		}
		NetType netType = NetType.getNetTypeByID(Deviceinfo.getInstance().getNettype());
		boolean isDT = settingModel.getReportTemplate() == TotalKpiSettingModel.REPORT_TEMPLATE_DT;
		File reportFile = this.createReportFile(context, settingModel.getReportType(), isDT,"");
		int tempResourceId = this.getExcelTempIDByNet(netType, isDT);
		this.mCurrReportClass = null;
		try {
			switch (settingModel.getReportType()) {
			case TotalKpiSettingModel.REPORT_TYPE_EXCEL:
				kpiList.addAll(this.getCustomReportModels());
				this.mCurrReportClass = this.mLoader.loadClass("com.walktour.gui.report.ExcelReport");
				break;
			case TotalKpiSettingModel.REPORT_TYPE_WORD:
				this.mCurrReportClass = this.mLoader.loadClass("com.walktour.gui.report.WordReport");
				break;
			}
			if (this.mCurrReportClass != null) {
				this.mCurrReportObj = this.mCurrReportClass.newInstance();
				Method method = this.getMethod("init",
						new Class[] { Context.class, String.class, Integer.class, Boolean.class, Handler.class });
				method.invoke(this.mCurrReportObj, context, kpiList, tempResourceId, reportFile, handler);
				method = this.getMethod("runReportThread", null);
				method.invoke(this.mCurrReportObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得自定义事件报表对象
	 * 
	 * @return
	 */
	private List<KpiReportModel> getCustomReportModels() {
		List<KpiReportModel> list = new ArrayList<KpiReportModel>();
		CustomEventFactory factory = CustomEventFactory.getInstance();
		factory.queryCustomTotal();
		List<TotalCustomModel> lists = factory.getTotalEventList();
		int sort = 1;
		for (int i = 0; i < lists.size(); i++) {
			TotalCustomModel custom = lists.get(i);
			KpiReportModel model = new KpiReportModel();
			model.mXlsSheets = 3;
			model.mXlsRows = 2 + sort;
			model.mXlsCols = 1;
			String[][] values = new String[custom.getEventList().size()][6];
			for (int j = 0; j < custom.getEventList().size(); j++) {
				OneEvent event = custom.getEventList().get(j);
				values[j][0] = String.valueOf(sort);
				values[j][1] = custom.getName();
				values[j][2] = UtilsMethod.sdfhms.format(event.time);
				values[j][3] = UtilsMethod.decFormat.format(event.delay / 10000f);
				values[j][4] = UtilsMethod.decFarmat8Zero.format(event.logitude);
				values[j][5] = UtilsMethod.decFarmat8Zero.format(event.latitude);
				sort++;
			}
			model.mValues = values;
			list.add(model);
		}
		return list;
	}

	/**
	 * 获得报表对象
	 * 
	 * @param context
	 *          上下文
	 * @param stateDataJson
	 *          统计生成的json格式数据
	 * @param tempResourceId
	 *          模板的资源文件Id
	 * @param isDT
	 *          是否DT
	 * @param handler
	 *          消息处理句柄
	 * @return
	 */
	@SuppressLint("NewApi")
	public void createReport(Context context, String stateDataJson, int tempResourceId, boolean isDT,String fileName, Handler handler) {
		File reportFile = this.createReportFile(context, TotalKpiSettingModel.REPORT_TYPE_EXCEL, isDT,fileName);
		try {
			this.mCurrReportClass = this.mLoader.loadClass("com.walktour.gui.report.ExcelJsonReport");
			this.mCurrReportObj = this.mCurrReportClass.newInstance();
			Method method = this.getMethod("init",
					new Class[] { Context.class, Object.class, Integer.class, File.class, Handler.class });
			method.invoke(this.mCurrReportObj, context, stateDataJson, tempResourceId, reportFile, handler);
			method = this.getMethod("runReportThread", null);
			method.invoke(this.mCurrReportObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得报表对象
	 * 
	 * @param context
	 *          上下文
	 * @param stateDataJson
	 *          统计生成的json格式数据
	 * @param tempResourceFile
	 *          模板的资源文件绝对路径
	 * @param isDT
	 *          是否DT
	 * @param handler
	 *          消息处理句柄
	 * @return
	 */
	@SuppressLint("NewApi")
	public void createReport(Context context, String stateDataJson, String tempResourceFile, boolean isDT,String fileName,Handler handler) {
		File reportFile = this.createReportFile(context, TotalKpiSettingModel.REPORT_TYPE_EXCEL, isDT,fileName);
		try {
			this.mCurrReportClass = this.mLoader.loadClass("com.walktour.gui.report.ExcelJsonReport");
			this.mCurrReportObj = this.mCurrReportClass.newInstance();
			Method method = this.getMethod("init",
					new Class[] { Context.class, Object.class, String.class, File.class, Handler.class });
			method.invoke(this.mCurrReportObj, context, stateDataJson, tempResourceFile, reportFile, handler);
			method = this.getMethod("runReportThread", null);
			method.invoke(this.mCurrReportObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	public void createExcelReportTemplate(String jsonDescription,String excelFilePath){
		try {
			this.mCurrReportClass = this.mLoader.loadClass("com.walktour.gui.report.ReportTemplate");
			this.mCurrReportObj = this.mCurrReportClass.newInstance();
			Method method = this.getMethod("createReportTemplate",new Class[] { String.class,String.class});
			method.invoke(this.mCurrReportObj,jsonDescription, excelFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取指定方法名称的方法对象
	 * 
	 * @param methodName
	 *          方法名称
	 * @param paramTypes 方法参数类型
	 * @return
	 */
	private Method getMethod(String methodName, Class[] paramTypes) {
		loop: for (Method method : this.mCurrReportClass.getMethods()) {
			if (method.getName().equals(methodName)) {
				if (paramTypes == null)
					return method;
				else {
					Class[] types = method.getParameterTypes();
					if (paramTypes.length != types.length)
						continue loop;
					for (int i = 0; i < types.length; i++) {
						if (types[i] != paramTypes[i])
							continue loop;
					}
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * 显示生成的预览html文件
	 * 
	 * @param filePath
	 *          报表文件的绝对路径
	 * @return
	 */
	@SuppressLint("NewApi")
	public boolean createHtmlFile(String filePath) {
		try {
			this.mCurrReportClass = this.mLoader.loadClass("com.walktour.gui.report.ExcelJsonReport");
			this.mCurrReportObj = this.mCurrReportClass.newInstance();
			Method method = this.getMethod("createHtmlFile", null);
			method.invoke(this.mCurrReportObj, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 获取生成的报表文件
	 * 
	 * @return
	 */
	public File getReportFile() {
		if (this.mCurrReportObj == null)
			return null;
		try {
			Method method = this.getMethod("getReportFile", null);
			return (File) method.invoke(this.mCurrReportObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取生成的html文件
	 * 
	 * @return
	 */
	public File getHtmlFile() {
		if (this.mCurrReportObj == null)
			return null;
		try {
			Method method = this.getMethod("getHtmlFile", null);
			return (File) method.invoke(this.mCurrReportObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 测试功能模块
	 * 
	 * @param context
	 *          上下文
	 * @param handler
	 *          消息句柄
	 */
	public void test(Context context, Handler handler) {
		TotalKpiSettingModel settingModel = new TotalKpiSettingModel();
		settingModel.setReportTemplate(TotalKpiSettingModel.REPORT_TEMPLATE_DT);
		settingModel.setReportType(TotalKpiSettingModel.REPORT_TYPE_EXCEL);
		this.createReport(context, settingModel, handler);
		// InputStream is = context.getResources().openRawResource(R.raw.stat_json);
		// ByteArrayOutputStream os = new ByteArrayOutputStream();
		// try {
		// byte[] buffer = new byte[512];
		// int read = 0;
		// while ((read = is.read(buffer)) > 0) {
		// os.write(buffer, 0, read);
		// }
		// String state = os.toString();
		// is.close();
		// os.close();
		// this.createReport(context, state, R.raw.report_temp_http, true, handler);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 创建报告生成文件
	 * 
	 * @param context
	 *          上下文
	 * @param reportType
	 *          报告类型
	 * @param isDT
	 *          是否DT
	 * @return
	 */
	private File createReportFile(Context context, int reportType, boolean isDT,String fileName) {
		String filePath = Environment.getExternalStorageDirectory() + "/Walktour/data/report/";
		String fileNameStr = UtilsMethod.sdfhmsss.format(System.currentTimeMillis());
		String postfix = (reportType == TotalKpiSettingModel.REPORT_TYPE_WORD) ? ".doc" : ".xls";
		String reportFileName = String.format("%s%s%s%s", filePath, isDT ? "DT-" : "CQT-", fileNameStr, postfix);
		if (fileName != null && fileName.trim().length() != 0){
			reportFileName = String.format("%s%s",filePath,fileName);
		}
		File reportFile = new File(reportFileName);
		if (!reportFile.exists()) {
			try {
				reportFile.getParentFile().mkdirs();
				reportFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return reportFile;
	}

	/**
	 * 获取word格式报表模板
	 *
	 * @param netType
	 *          网络类型
	 * @param isDT
	 *          是否DT
	 * @return
	 */
	protected int getWordTempIDByNet(NetType netType, boolean isDT) {
		int tempResourceId = 0;
		switch (netType) {
		case CDMA:
		case EVDO:
			if (isDT) {
				tempResourceId = R.raw.dt_report_c;
			} else {
				tempResourceId = R.raw.cqt_report_c;
			}
			break;
		case TDSCDMA:
		case LTETDD:
			if (isDT) {
				tempResourceId = R.raw.dt_report_td;
			} else {
				tempResourceId = R.raw.cqt_report_td;
			}
			break;
		/*
		 * case WCDMA: case LTE: break;
		 */
		default:
			if (isDT) {
				tempResourceId = R.raw.dt_report_w;
			} else {
				tempResourceId = R.raw.cqt_report_w;
			}
			break;
		}
		return tempResourceId;
	}

	/**
	 * 获取excel格式报表模板
	 * 
	 * @param netType
	 *          网络类型
	 * @param isDT
	 *          是否DT
	 * @return
	 */
	protected int getExcelTempIDByNet(NetType netType, boolean isDT) {
		int tempResourceId = 0;
		switch (netType) {
		case CDMA:
		case EVDO:
			if (isDT) {
				tempResourceId = R.raw.dtreport_c;
			} else {
				tempResourceId = R.raw.cqtreport_c;
			}
			break;
		case TDSCDMA:
		case LTETDD:
			if (isDT) {
				tempResourceId = R.raw.dtreport_td;
			} else {
				tempResourceId = R.raw.cqtreport_td;
			}
			break;
		/*
		 * case WCDMA: case LTE: break;
		 */
		default:
			if (isDT) {
				tempResourceId = R.raw.dtreport_w;
			} else {
				tempResourceId = R.raw.cqtreport_w;
			}
			break;
		}
		return tempResourceId;
	}

	/**
	 * 根据传进来的配置指标相关信息，获得统计结果中该指标的返回值，用于保存到word中
	 * 
	 * @param param
	 *          参数对象
	 * @return
	 */
	private void setKpiValues(KpiReportModel param) {
		String[][] values = null;
		try {
			switch (param.mParamType) {
			case KpiReportModel.PARAM_TYPE_MEASURE:
				TotalMeasureModel measure = TotalDataByGSM.getHashMapMeasure(TotalDataByGSM.getInstance().getMeasuePara(),
						param.mMolecule);
				values = new String[][] { { TotalDataByGSM.getValueByScale(measure.getMinValue(), param.mScale),
						TotalDataByGSM.getValueByScale(measure.getMaxValue(), param.mScale),
						TotalDataByGSM.getIntMultiple(measure.getKeySum(), measure.getKeyCounts(), param.mScale, "") } };
				break;
			case KpiReportModel.PARAM_TYPE_UNIFY:
				// 此方法中的放大倍数是除的，但配置文件中的值是需要乘的，所以需要反过来
				values = new String[][] { { TotalDataByGSM.getHashMapValue(TotalDataByGSM.getInstance().getUnifyTimes(),
						param.mMolecule, 1 / param.mScale) } };
				break;
			case KpiReportModel.PARAM_TYPE_UNIFY2:
				values = new String[][] { { TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getUnifyTimes(),
						param.mMolecule, param.mDenominator, param.mScale, param.mUnits) } };
				break;
			case KpiReportModel.PARAM_TYPE_PARAM1:
				values = new String[][] { {
						// 此方法中的放大倍数是除的，但配置文件中的值是需要乘的，所以需要反过来
						TotalDataByGSM.getHashMapValue(TotalDataByGSM.getInstance().getPara(), param.mMolecule,
								1 / param.mScale) } };
				break;
			case KpiReportModel.PARAM_TYPE_PARAM2:
				values = new String[][] { { TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getPara(),
						param.mMolecule, param.mDenominator, param.mScale, param.mUnits) } };
				break;
			case KpiReportModel.PARAM_TYPE_EVENT1:
				values = new String[][] { {
						// 此方法中的放大倍数是除的，但配置文件中的值是需要乘的，所以需要反过来
						TotalDataByGSM.getHashMapValue(TotalDataByGSM.getInstance().getEvent(), param.mMolecule,
								1 / param.mScale) } };
				break;
			case KpiReportModel.PARAM_TYPE_EVENT2:
				values = new String[][] { { TotalDataByGSM.getHashMapMultiple(TotalDataByGSM.getInstance().getEvent(),
						param.mMolecule, param.mDenominator, param.mScale, param.mUnits) } };
				break;
			default:
				values = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		param.mValues = values;
	}

}

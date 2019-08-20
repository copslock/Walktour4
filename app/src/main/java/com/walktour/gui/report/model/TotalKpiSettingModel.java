package com.walktour.gui.report.model;

/**
 * 统计设置model
 * 
 * @author jianchao.wang
 *
 */
public class TotalKpiSettingModel {
	/** 输出报表模版类型:DT */
	public static final int REPORT_TEMPLATE_DT = 0;
	/** 输出报表模版类型:CQT */
	public static final int REPORT_TEMPLATE_CQT = 1;
	/** 输出报表类型:excel */
	public static final int REPORT_TYPE_EXCEL = 0;
	/** 输出报表类型:word */
	public static final int REPORT_TYPE_WORD = 1;
	/** 输出报表类型:text */
	public static final int REPORT_TYPE_TXT = 2;
	/** 是否显示报表 */
	private boolean isShowReport = false;
	/** 统计模板 */
	private int mReportTemplate = 0;
	/** 报表类型 */
	private int mReportType = 0;
	/** 统计网络 */
	private int mTotalNetWork = 0;
	/** 统计频点 */
	private int mTotalChannel = 0;

	public boolean isShowReport() {
		return isShowReport;
	}

	public void setShowReport(boolean isShowReport) {
		this.isShowReport = isShowReport;
	}

	public int getReportType() {
		return mReportType;
	}

	public void setReportType(int reportType) {
		mReportType = reportType;
	}

	public int getTotalNetWork() {
		return mTotalNetWork;
	}

	public void setTotalNetWork(int totalNetWork) {
		mTotalNetWork = totalNetWork;
	}

	public int getTotalChannel() {
		return mTotalChannel;
	}

	public void setTotalChannel(int totalChannel) {
		mTotalChannel = totalChannel;
	}

	public int getReportTemplate() {
		return mReportTemplate;
	}

	public void setReportTemplate(int reportTemplate) {
		mReportTemplate = reportTemplate;
	}

}

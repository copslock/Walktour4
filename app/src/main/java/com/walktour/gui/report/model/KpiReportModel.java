package com.walktour.gui.report.model;

/**
 * kpi报表输出对象
 * 
 * @author jianchao.wang
 *
 */
public class KpiReportModel {
	/** 参数类型：业务指标，仅单参数 */
	public static final int PARAM_TYPE_UNIFY = 1;
	/** 参数类型：求参数最大，最小，平均值 */
	public static final int PARAM_TYPE_MEASURE = 2;
	/** 参数类型：业务指标，需按分子分母处理 */
	public static final int PARAM_TYPE_UNIFY2 = 3;
	/** 参数类型：PARA表一个参数指标 */
	public static final int PARAM_TYPE_PARAM1 = 5;
	/** 参数类型：PARA表两个个参数指标 */
	public static final int PARAM_TYPE_PARAM2 = 4;
	/** 参数类型：EVENT表一个参数 */
	public static final int PARAM_TYPE_EVENT1 = 6;
	/** 参数类型：EVENT表两个参数指标 */
	public static final int PARAM_TYPE_EVENT2 = 7;
	/** kpi显示名称 */
	public String mShowName = "";
	/** 指标分子,参数关键字 */
	public String mMolecule = "";
	/** 指标分母，单一指标时无值 */
	public String mDenominator = "";
	/** 单位 */
	public String mUnits = "";
	/** 缩放比例 */
	public float mScale = 1f;
	/** 参数类型 */
	public int mParamType = PARAM_TYPE_UNIFY;
	/** excel页码 */
	public int mXlsSheets = 0;
	/** excel列号 */
	public int mXlsCols = 0;
	/** excel行号 */
	public int mXlsRows = 0;
	/** doc的对应位置key */
	public String mMarkKey = "";
	/** 关联的数据 */
	public String[][] mValues;

}

package com.walktour.gui.analysis.commons;

import android.annotation.SuppressLint;

/***
 * 记录智能分析的常量信息
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("SdCardPath")
public final class AnalysisCommons {

	/**
	 * 防止外部构造
	 */
	private AnalysisCommons() {
		super();
	}
	/**异常分析类型 LTE DATA,主要分析数据业务*/
	public static final int FAILD_TYPE_LTEDATA	= 3;
	/**分析场景CSFB**/
	public static final String ANALYSIS_CSFB = "CSFB";
	/**分析场景VOLTE**/
	public static final String ANALYSIS_VOLTE = "VoLTE";
	/**分析场景LTEDATA**/
	public static final String ANALYSIS_LTEDATA = "LTE Data";
	public static final int ANALYSIS_RESULT_CODE=0x1023;
	/***/
	public static final String ANALYSIS_ACTION_SELECT_FILE = "com.walktour.gui.analysis.commons.action.select.file";
	/** 保存选择的分享场景以,分割 **/
	public static final String ANALYSIS_SELECT_SCENE = "com.walktour.gui.analysis.commons.select.scene";
	public static final String ANALYSIS_SELECT_FILE_MERGEUK_PATH = "com.walktour.gui.analysis.commons.select.file.mergeuk.path";

	/** 智能分析数据存储路径 ***/
	public static final String ANALYSIS_PATH_ROOT = "TotalConfig";
	/** 智能分析数据存储路径 ***/
	public static final String ANALYSIS_PATH_ROOT_ANALYSIS = "analysis";
	/** 智能分析历史数据存储路径 ***/
	public static final String ANALYSIS_PATH_ROOT_ANALYSIS_HISTORY = "hisotory";

	/** 智能分析历史文件名 **/
	public static final String HISTORY_JSON_FILE_NAME = "history.json";
	/**调用库返回生成的uk名称**/
	public static final String ANALYSIS_MERGEUK_NAME = "mergeAnalysisUk.uk";
}

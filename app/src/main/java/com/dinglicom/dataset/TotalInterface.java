package com.dinglicom.dataset;

import android.content.Context;
import android.os.Environment;

import com.walktour.base.util.LogUtil;

/**
 * 统计接口
 * 
 * @author jianchao.wang
 *
 */
public class TotalInterface {
	/** 日志标识 */
	private final static String TAG = "TotalInterface";
	/** 唯一实例 */
	private static TotalInterface sInstance;
	/** 上下文 */
	private Context mContext = null;
	/** 是否已经初始化 */
	public static boolean isInit = false;
	/** 配置文件包名 */
	public String configPathName;

	private TotalInterface(Context mContext) {
		configPathName = Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig";
		this.mContext = mContext;
	}

	public static synchronized TotalInterface getInstance(Context mContext) {
		if (sInstance == null) {
			sInstance = new TotalInterface(mContext);
		}
		return sInstance;
	}

	/**
	 * 初始化库
	 */
	public void initLib() {
		isInit = true;
		// DataCenter.GetStatisticsLibraryVersion();
		DataCenter.SetStatisticsLibraryLogPath(configPathName + "/datacenterlog/");
		DataCenter.SetConfigPath(configPathName + "/ReportCfg/");
		DataCenter.InitialStatisticsLib(mContext.getFilesDir().getParent() + "/lib/");
		DataCenter.SetStatisticsSettingFile(configPathName + "/thresholdProfile.xml"); // 加载一次即可
		DataCenter.SetStatisticsWorkPath(configPathName + "/template/", configPathName + "/ukdir/");
	}

	/**
	 * 获取统计进度
	 * 
	 * @return
	 */
	public float queryProgress() {
		return DataCenter.GetStatProgress();
	}

	/**
	 * 执行一级页面统计
	 */
	public int excuteTotal() {
		return DataCenter.MergeTaskJsonWithScenes(configPathName + "/sceneXmlOne.xml",
				configPathName + "/resultJson/resultJsonOne.json");
	}

	/**
	 * 报表导出
	 */
	public int ExportReportJson() {
		return DataCenter.ExportReportJson(configPathName + "/sceneXls.xml",
				configPathName + "/resultJson/resultJsonXls.json");
	}

	/**
	 * 报表导出
	 */
	public int ExportReportUKFile() {
		return DataCenter.MergeAnalysisWithScenes(configPathName + "/sceneXls.xml",
				configPathName + "/resultJson/resultJsonXls.json");
	}
	/**
	 * 添加数据后台统计
	 * 
	 * @param ddibFile
	 */
	public void addStatisticDDIBFile(String ddibFile) {
		LogUtil.d(TAG, "-----addStatisticDDIBFile-----");
		DataCenter.AddStatisticDDIBFile(ddibFile);
	}

	/**
	 * 删除uk
	 * 
	 * @param ddibName
	 */
	public boolean deleteUk(String ddibName) {
		return DataCenter.DeleteUkRecord(ddibName);
	}

	/**
	 * 设置自动统计
	 * 
	 * @param flag
	 */
	public void setAutoStatistic(boolean flag) {
		DataCenter.SetAutoStatistic(flag);
	}

	/**
	 * 停止统计
	 */
	public void stopTotal() {
		DataCenter.StopStatistic();
	}

	/**
	 * 卸载库
	 */
	public void freeLib() {
		DataCenter.StopStatistic();
		DataCenter.UninitialStatisticsLib();
	}

	/**
	 * 生成统计结果,二级界面
	 * 
	 * @param ddibFileName
	 *          需要执行统计的DDIB文件
	 * @param scenesFile
	 *          需要统计的场景，含ddib
	 * @param jsonFileName
	 *          生成json存放的文件路径，调用者传入
	 */
	public int getTotalL2Result(String scenesFile) {
		return DataCenter.MakeParamJsonWithScenes(scenesFile, configPathName + "/resultJson/resultJsonTwo.json");
	}

}

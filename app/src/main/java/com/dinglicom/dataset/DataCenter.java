package com.dinglicom.dataset;

public class DataCenter {
	static{
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("datacenter");
	}
	
	
	public  void evtcallback(int current,int total){
		System.out.println("---current" + current  + "---total" + total);
	}
	
	/**获取统计库版本号 */
	public native static String GetStatisticsLibraryVersion();
	
	
	/**设置配置文件路径**/
	
	public native static void SetConfigPath(String path);
	
	/**
	 * 设置统计库日志路径
	 * @param fileName
	 * */
	public native static void SetStatisticsLibraryLogPath(String fileName);
	
	/**
	 * 初始化统计模块
	 * @param libPath	业务库路径
	 * */
	public native static void  InitialStatisticsLib(String libPath);
	
	/**释放统计模块*/
	public native static void UninitialStatisticsLib();
	
	/**
	 * 设置统计设置文件名称
	 * @param fileName	指定阀值文件路径
	 */
	public native static void  SetStatisticsSettingFile(String fileName);
	
	/**
	 * 设置统计库缓存路径、UK存储路径
	 * @param libTempDir	临时工作路径
	 * @param ukDir			UK生成路径
	 */
	public native static void SetStatisticsWorkPath(String libTempDir,String ukDir);
	
	/**
	 * 单个ddib生成uk（阻塞)
	 * @param ddibFileName	需要统计的ddib文件
	 */
	public native static void MakeStatisticsTaskUK(String ddibFileName);
	
	/**
	 * 合并任务UK结果生成json（阻塞)
	 * @param scenesFile	需要统计的场景，含ddib
	 * @param jsonFileName	生成json存放的文件路径，调用者传入
	 */
	public native static int MergeTaskJsonWithScenes(String scenesFile,String jsonFileName);
	
	/***
	 * 
	 * @param scenesFile 请求的文件
	 * 
	 * @param ukFileName 生成合成后的UK文件
	 * @return
	 */
	public native static int MergeAnalysisWithScenes(String scenesFile,String ukFileName);
	
	/**
	 * 合并任务UK结果生成json（阻塞）
	 * @param scenesFile	需要统计的场景，含ddib
	 * @param jsonFileName	生成json存放的文件路径，调用者传入
	 */
	public native static int MakeParamJsonWithScenes(String scenesFile,String jsonFileName);
	
	/**
	 * 停止统计
	 */
	public native static void StopStatistic();
	
	/**
	 * 获取统计状态 EStatisticsState
	 */
	public native static int GetStatisticsState();
	
	/**
	 * 获取统计进度 0.0~1.0
	 */
	public native static float GetStatProgress();
	
	/**
	 * ExportReportJson
     * @Function：导出报表Json
     * @para:
     * scenesFile: 需要统计的场景，含ddib、报表模板
     * jsonFileName: 生成json存放的文件路径，调用者传入
     * @return:SAErrorCode
	 */
	public native static int ExportReportJson(String scenesFile,String jsonFileName);
	
	
	/**
	 * 设置是否自动统计
	 * false空闲 true忙碌
	 */
	public native static void SetAutoStatistic(boolean flag);
	
	/**
	 * 添加DDIB记录到统计库，预备后续的后台统计
	 * @param ddibFile
	 */
	public native static void AddStatisticDDIBFile(String ddibFile);
	
	/**
	 * 
	 * @param ddibName(全路径)
	 * true: 成功
	 * false: 失败	
	 */
	public native static boolean DeleteUkRecord(String ddibName);
	
	
	
}

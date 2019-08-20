package com.dingli.watcher.jni;

import android.util.Log;

import com.dingli.watcher.model.MetroGPS;
import com.dingli.watcher.model.MetroRunParamInfo;

/**
 * 地铁高铁项目JNI
 * 
 * @author jianchao.wang
 *
 */
public class MetroJNI {

	static {
		try {
			System.loadLibrary("CryptoPPEncAlgorithm");
			System.loadLibrary("gnustl_shared");
			System.loadLibrary("DataMiningBase");
			System.loadLibrary("DataMiningParseKML");
			System.loadLibrary("DMMetroStationDataManager");
			System.loadLibrary("DMRealTimeFillGPS");
			System.loadLibrary("Railway");

		} catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个实时GPS补点管理对象句柄(地铁,高铁等)
	 * 
	 * @param AFillGPSType
	 *          实时GPS补点类型(0: 地铁补点, 1: 高铁补点)；
	 * @return 一个实时GPS补点管理句柄；
	 */
	public static native int CreateDMRealTimeFillGPS(int AFillGPSType);

	/**
	 * 释放一个实时GPS补点管理对象实例pRTFillGPSHandle，并将其设置为空
	 * 
	 * @param pRTFillGPSHandle
	 *          输出参数，此为CreateDMRealTimeFillGPS创建的实时GPS补点管理对象实例；
	 * @return 1-成功，非1-失败，错误代码；
	 */
	public static native int FreeDMRealTimeFillGPS(int pRTFillGPSHandle);

	/**
	 * 配置相关的整型属性值，对于启动补点过程之前的设置有效；
	 * 
	 * 相关APropertyKey定义：
	 * 
	 * //是否写自身日志信息 (0: 不写日志, 非0: 写日志)
	 * ConfigIntPropertyKey_RTFillGPS_IsWriteSelfLog = 10060;
	 * //设置间隔多少毫秒(ms)输出一个GPS点 ConfigIntPropertyKey_RTFillGPS_IntervalTimeMS =
	 * 10061; //到站后至从站点起动前的这段时间是否输出GPS点(0: 不输出GPS, 非0: 输出GPS)，(用于地铁补点)
	 * ConfigIntPropertyKey_RTFillGPS_IsFillGPSOnArrivalStation = 10062;
	 * //地铁停站等待时间ms ConfigIntPropertyKey_RTFillGPS_MetroStopStationWaitTimeMS =
	 * 10063; //发送加速度参数的时间间隔(ms)
	 * ConfigIntPropertyKey_RTFillGPS_SendMetroRunParamIntervalTimeMS = 10064;
	 * //判断状态连续检查时间数(ms) ConfigIntPropertyKey_RTFillGPS_ContinueCheckTimeMS =
	 * 10065; //连续检查多少次停止状态认为是到站
	 * ConfigIntPropertyKey_RTFillGPS_StationContinueCheckCount = 10066;
	 * 
	 * 
	 * @param ARTFillGPSHandle
	 *          此为CreateDMRealTimeFillGPS创建的实时GPS补点管理对象实例；
	 * @param APropertyKey
	 *          属性关键字Key；
	 * @param APropertyValue
	 *          相关属性值
	 */
	public static native void ConfigIntProperty(int ARTFillGPSHandle, int APropertyKey, long APropertyValue);

	/**
	 * 配置相关的浮点型属性值，对于启动补点过程之前的设置有效；
	 * 
	 * 相关APropertyKey定义：
	 * 
	 * //判断停止的最小阀值 ConfigDoublePropertyKey_RTFillGPS_MinStopThreshold = 1;
	 * //判断停止的最大阀值 ConfigDoublePropertyKey_RTFillGPS_MaxStopThreshold = 2;
	 * //判断起动的最大阀值 ConfigDoublePropertyKey_RTFillGPS_MaxStartThreshold= 3;
	 * //判断减速的最小阀值 ConfigDoublePropertyKey_RTFillGPS_MinReduceThreshold= 4;
	 * 
	 * 
	 * @param ARTFillGPSHandle
	 *          此为CreateDMRealTimeFillGPS创建的实时GPS补点管理对象实例
	 * @param APropertyKey
	 * @param APropertyValue
	 */
	public static native void ConfigDoubleProperty(int ARTFillGPSHandle, int APropertyKey, double APropertyValue);

	/**
	 * 配置相关的字符串型属性值，对于启动补点过程之前的设置有效；
	 * 
	 * 相关APropertyKey定义： //补点参考GPS轨迹地图文件(如:
	 * kml文件)的绝对路径名，配置此属性时会同时导入文件中的GPS轨迹点信息，导入的GPS信息作为测试不到GPS数据时的GPS补点操作用(用于高铁补点)
	 * ConfigStringPropertyKey_HRFillGPS_MapFileName = 3100;
	 * 
	 * 
	 * @param ARTFillGPSHandle
	 *          此为CreateDMRealTimeFillGPS创建的实时GPS补点管理对象实例
	 * @param APropertyKey
	 * @param APropertyValue
	 */
	public static native void ConfigStringProperty(int ARTFillGPSHandle, int APropertyKey, String APropertyValue);

	/**
	 * 
	 * 配置相关的复杂型(如：结构体类型)属性值，对于启动补点过程之前的设置有效；
	 * 
	 * @param ARTFillGPSHandle
	 * @param APropertyKey
	 * @param APropertyValue
	 */
	public static native void ConfigComplexProperty(int ARTFillGPSHandle, int APropertyKey, Object APropertyValue);

	/**
	 * 启动实时GPS补点过程，创建相关补点线程等资源；
	 * 
	 * @param ARTFillGPSHandle
	 * @return 1-成功，非1-失败，错误代码
	 */
	public static native int StartRTFillGPSPro(int ARTFillGPSHandle);

	/**
	 * 关闭实时GPS补点过程，释放相关补点线程等资源
	 * 
	 * @param ARTFillGPSHandle
	 * @return 1-成功，非1-失败，错误代码
	 */
	public static native int EndRTFillGPSPro(int ARTFillGPSHandle);

	/**
	 * 得到当前缓存的GPS点的个数，其可能包含原始输入的GPS点和按照规则补充的GPS点
	 * 
	 * @param ARTFillGPSHandle
	 * @return 当前输出GPS点的个数，0表示无GPS点输出
	 */
	public static native int GetRTFillGPSPointCount(int ARTFillGPSHandle);

	/**
	 * 读取一个GPS点的信息给AGPSInfo变量，调用此函数时要先判断GetRTFillGPSPointCount的值大于0；
	 * 
	 * @param ARTFillGPSHandle
	 * @param AGPSInfo
	 *          1-成功，非1-失败，错误代码；
	 */
	public static native int ReadOneRTFillGPSPoint(int ARTFillGPSHandle, MetroGPS AGPSInfo);

	/**
	 * 在启动实时GPS补点过程后，传入参考的GPS点信息，
	 * (1)对于高铁是实时把GPS设备测试得到的原始GPS点信息传入到本库中作为一段时间无GPS点时的补点操作的依据(注意：
	 * 高铁传入的至少要两个以上的原始GPS点)；
	 * (2)对于地铁为到站站点GPS信息，通过此函数通知当前到达的站点，补点过程会在输出到站站点的GPS信息后补点过程GPS点不变(相当于地铁停站)，
	 * 当地铁从站点起动运行时通过MetroRunFromStation通知补点过程继续开始计算新的GPS点；
	 * 
	 * @param ARTFillGPSHandle
	 * @param AGPSInfo
	 * @param  mode -1高铁扩展，0其他
	 * @return 1-成功，非1-失败，错误代码；
	 *
	 */
	public static native int SendOriGPSInfo(int ARTFillGPSHandle, MetroGPS AGPSInfo,int mode);

	/**
	 * 专门用于地铁补点过程，通过此函数通知继续开始补点过程，和SendOriGPSInfo配套使用，
	 * SendOriGPSInfo用于通知当前到站站点直到从站点起动运行前GPS点不变，
	 * MetroRunFromStation用于通知从当前站点起动运行并补点过程继续开始计算新的GPS点；
	 * 
	 * @param ARTFillGPSHandle
	 * @param AGPSInfo
	 * @return
	 */
	public static native int MetroRunFromStation(int ARTFillGPSHandle, MetroGPS AGPSInfo);

	/**
	 * 专门用于地铁补点过程，通过此函数实时传入传感器或陀螺仪等设备得到的加速度等信息，然后通过这些信息进行起停模式的判断；
	 * 
	 * @param ARTFillGPSHandle
	 *          此为CreateDMRealTimeFillGPS创建的实时GPS补点管理对象实例；
	 * @param AParamInfo
	 *          传入传感器或陀螺仪等设备得到的加速度等信息，其类定义见目录三中定义
	 * @return 1-成功，非1-失败，错误代码；
	 */
	public static native int SendMetroRunParamInfo(int ARTFillGPSHandle, MetroRunParamInfo AParamInfo);

	/**
	 * 在启动实时GPS补点过程前，导入的参考线路GPS轨迹信息， (1)对于高铁是高铁线路GPS轨迹信息KML文件；
	 * (2)对于地铁是地铁线路GPS轨迹信息XML文件(一个XML文件对应一个城市的地铁线路的信息)；
	 * 
	 * @param ARTFillGPSHandle
	 * @param AFileName
	 *          高铁为KML文件全路径名，地铁为XML文件全路径名
	 * @param ASecretKey
	 *          地铁XML文件的解密密钥
	 * @return
	 */
	public static native int ImportGPSAssitantMapFile(int ARTFillGPSHandle, String AFileName, String ASecretKey,
			String ASymmetricKey);

	/**
	 * 获取XML文件中地铁线路的数量(如1号线，2号线…)，使用前要先ImportGPSAssitantMapFile导入相关的XML文件；
	 * 一条线路的属性有LID, Name,Speed_KMperH,StartTime,EndTime
	 * 
	 * @param ARTFillGPSHandle
	 * @return
	 */
	public static native int RTGetMetroLineCount(int ARTFillGPSHandle);

	/**
	 * 
	 * 获取一个线路的整型属性的值；
	 * 
	 * 相关AKey定义： CNST_DMMSDataManager_KeyDefine_ID = 1;//LID属性(线路标识ID),在一个XML中其值唯一
	 * CNST_DMMSDataManager_KeyDefine_StartTime =
	 * 4;//StartTime属性(线路开始时间),小时分钟的(小时*60*60+分钟*60)表示
	 * CNST_DMMSDataManager_KeyDefine_EndTime = 5; //EndTime属性(线路结尾时间),同上
	 * 
	 * 
	 * @param ARTFillGPSHandle
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native long RTGetMetroLineIntKeyInfo(int ARTFillGPSHandle, int AIndex, int AKey);

	/**
	 * 获取一个线路的浮点型属性的值；
	 * 
	 * 相关AKey定义： CNST_DMMSDataManager_KeyDefine_SpeedKMH = 3;
	 * //Speed_KMperH属性(线路平均速度),单位km/h
	 * 
	 * 
	 * @param ARTFillGPSHandle
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native double RTGetMetroLineDoubleKeyInfo(int ARTFillGPSHandle, int AIndex, int AKey);

	/**
	 * 获取一个线路的字符串型属性的值； 相关AKey定义： CNST_DMMSDataManager_KeyDefine_Name = 2;
	 * //Name属性(线路名称)
	 * 
	 * @param ARTFillGPSHandle
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native String RTGetMetroLineStringKeyInfo(int ARTFillGPSHandle, int AIndex, int AKey);

	public static String RTGetMetroLineStringKeyInfo_UTF_8(int ARTFillGPSHandle, int AIndex, int AKey) {

		String data = RTGetMetroLineStringKeyInfo(ARTFillGPSHandle, AIndex, AKey);

		Log.i("MetroManager", "data = " + data.length() + " data = " + data);

		return data;

	}

	/**
	 * 获取XML文件中一条线路的站点数量，ALineIndex为线路的索引号；
	 * 
	 * 说明：一个站点的属性有SID, Name,StationTime,Longitude,Latitude
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @return
	 */
	public static native int RTGetMetroLineStationCount(int ARTFillGPSHandle, int ALineIndex);

	/**
	 * 获取一线路中一个站点的整型属性的值； CNST_DMMSDataManager_KeyDefine_ID =
	 * 1;//SID属性(线路中站点标识ID),在XML的一条线路中其值唯一
	 * CNST_DMMSDataManager_KeyDefine_StationTime =
	 * 8;//StationTime属性(站点时刻表首班),小时分钟的(小时*60*60+分钟*60)表示
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native long RTGetMetroLineStationIntKeyInfo(int ARTFillGPSHandle, int ALineIndex, int AIndex, int AKey);

	/**
	 * 获取一线路中一个站点的浮点型属性的值； 相关AKey定义： CNST_DMMSDataManager_KeyDefine_Longitude = 6;
	 * //Longitude属性(站点的经度) CNST_DMMSDataManager_KeyDefine_Latitude =
	 * 7;//Latitude属性(站点的纬度)
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native double RTGetMetroLineStationDoubleKeyInfo(int ARTFillGPSHandle, int ALineIndex, int AIndex,
			int AKey);

	/**
	 * 获取一线路中一个站点的字符串型属性的值； 相关AKey定义： CNST_DMMSDataManager_KeyDefine_Name = 2;
	 * //Name属性(站点名称)
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native String RTGetMetroLineStationStringKeyInfo(int ARTFillGPSHandle, int ALineIndex, int AIndex,
			int AKey);

	/**
	 * 获取XML文件中一条线路的两个站点之间辅助GPS点的数量，
	 * ALineIndex为线路的索引号，AStartSIndex和AEndSIndex为一条线路中两个站点的索引号，
	 * 这里输出辅助点信息是有方向的，是按照AStartSIndex到AEndSIndex的方向输出；
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @param AStartSIndex
	 * @param AEndSIndex
	 * @return
	 */
	public static native int RTGetMetroLineStationSubItemCount(int ARTFillGPSHandle, int ALineIndex, int AStartSIndex,
			int AEndSIndex);

	/**
	 * 获取一线路中两个站点之间一个辅助GPS点的浮点型属性的值，输出辅助GPS 点方向是从AStartSIndex到AEndSIndex
	 * CNST_DMMSDataManager_KeyDefine_Longitude = 6; //Longitude属性(辅助GPS点的经度)
	 * CNST_DMMSDataManager_KeyDefine_Latitude = 7;//Latitude属性(辅助GPS点的纬度)
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @param AStartSIndex
	 * @param AEndSIndex
	 * @param AIndex
	 * @param AKey
	 * @return
	 */
	public static native double RTGetMetroLineStationSubItemDoubleKeyInfo(int ARTFillGPSHandle, int ALineIndex,
			int AStartSIndex, int AEndSIndex, int AIndex, int AKey);

	/**
	 * 发送当前要测试的线路和线路的开始站点和结束站点；
	 * 
	 * @param ARTFillGPSHandle
	 * @param ALineIndex
	 * @param AStartSIndex
	 * @param AEndSIndex
	 * @param ASpeedKMperH
	 * @return 1-成功，非1-失败，错误代码；
	 */
	public static native int SendMetroTestLineInfo(int ARTFillGPSHandle, int ALineIndex, int AStartSIndex, int AEndSIndex,
			double ASpeedKMperH);

}

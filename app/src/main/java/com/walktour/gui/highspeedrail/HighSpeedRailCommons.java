package com.walktour.gui.highspeedrail;
import android.annotation.SuppressLint;
import android.content.Context;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkMessage;
/***
 * 环境变量
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("SdCardPath")
public class HighSpeedRailCommons {
	/***高铁测试的数据路径**/
	public static final String High_SPEED_RAIL_PATH="highspeedrail";
	/***高铁测试的接口库日志路径**/
	public static final String High_SPEED_RAIL_PATH_LOG="log";


	/**当前运行的线路名称**/
	private static String runningRoute="";
	/**
	 * 保存历史线路
	 * 
	 * @param rail 线路名称
	 */
	public static void saveHistoryRail(Context context,String rail){
		runningRoute=rail;
		if(StringUtil.getLanguage().equals("cn")){//中文
			SharePreferencesUtil.getInstance(context).saveString(WalkMessage.KEY_HIGHSPEEDRAIL_CURRENT_RAIL_ZH, rail);
		}else{//英文
			SharePreferencesUtil.getInstance(context).saveString(WalkMessage.KEY_HIGHSPEEDRAIL_CURRENT_RAIL_EN, rail);
		}
	}
	
	/**
	 *  获取历史线路
	 * @return 历史线路
	 */
	public static String getHistoryRail(Context context){
		if(StringUtil.getLanguage().equals("cn")){//中文
			runningRoute=SharePreferencesUtil.getInstance(context).getString(WalkMessage.KEY_HIGHSPEEDRAIL_CURRENT_RAIL_ZH);
		}else{//英文
			runningRoute=SharePreferencesUtil.getInstance(context).getString(WalkMessage.KEY_HIGHSPEEDRAIL_CURRENT_RAIL_EN);
		}
		return runningRoute;
	}

	public static String getRunningRoute() {
		return runningRoute;
	}

	public static void setRunningRoute(String runningRoute) {
		HighSpeedRailCommons.runningRoute = runningRoute;
	}

}

package com.walktour.Utils;

import android.content.Context;

import com.dinglicom.dataset.EventManager;
import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.Utils.WalkStruct.TelecomSetting;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.model.NetStateModel;

/**
 * 任务开始之前激活网络
 * @author Tangwq
 *
 */
public class NetWorkAlive {
	private static final String TAG = "NetWorkAlive";
	private static final String FAIL_TAG 		= "0 received";			//Ping不通的标志
	private static final String SUCC_TAG 		= "1 received";			//Ping通的标志
	private static final String SUCC_TAG_2		= "1 packets received";	//Ping通的第二标志
	
	/**
	 * 执行网络激活动作
	 * 
	 * 当前如果为LTE网络,且LTE状态为Idle
	 * 
	 * 执行PING动作,实时查询指定事件,如果PING成功或指定事件返回值为真,则退出激活动作,否则5S后退出动作
	 */
	public static void doNetWorkAlive(Context context,long networkAliveTime){
		NetStateModel netState = NetStateModel.getInstance();
		
		LogUtil.w(TAG,"--currentNet:" + netState.getCurrentNetType().name() + "--State:" + netState.isIdleState());
		//当前为LTE网络且状态为idle,才需要执行里面的操作
		if(netState.getCurrentNetType() == CurrentNetState.LTE && netState.isIdleState()){
			long startTime = System.currentTimeMillis();
			//当前PING失败且LTE激活失败,且时间小于5000继续 
			try{
				while(!getPingResult(context) && !EventManager.getInstance().EventContainsRCUID(DataSetEvent.ET_LTE_ERAB_Success, networkAliveTime)
						&& System.currentTimeMillis() - startTime < 5000){
						Thread.sleep(500);
				}
			}catch(Exception e){
				LogUtil.w(TAG,"doNetWorkAlive",e);
			}
		}
	}
	
	/**
	 * 检查电信设置项
	 * 
	 * 如果设置的非普通项,则需要查询当前网络情况是否
	 * @param context
	 * @return 返回值为真的时候,表示当前为匹配网络,为假的时候正常情况是不返回的,反回就是只断的情况下才返回
	 */
	public static boolean checkTelecomSet(Context context,EventManager eventManager){
		//设置当前检查项默认有效,如果需要检测,则根据实际值修改
		boolean telecomEnable = true;
		//当前的电信设置非普通模式
		if(ConfigRoutine.getInstance().getTelecomDataNetSet(context) != TelecomSetting.Normal){
			NetStateModel netState = NetStateModel.getInstance();
			final String tabStr = "Not specified network, test paused...";
			int sleepTimes = 0;
			//如果当前的网络类型与指定的网络类型不一至,则等待
			try{
				telecomEnable = netState.getCurrentNetType().getGeneral()
						== ConfigRoutine.getInstance().getTelecomDataNetSet(context).getNetWorkGeneral();
				while (!ApplicationModel.getInstance().isTestInterrupt() && !telecomEnable) {
					Thread.sleep(1000);
					//每待15秒钟写一次 "Not specified network, test paused..." Tab信息
					if(sleepTimes++ % 15 == 0){
						eventManager.addTagEvent(context, System.currentTimeMillis(), tabStr);
					}
					telecomEnable = netState.getCurrentNetType().getGeneral()
							== ConfigRoutine.getInstance().getTelecomDataNetSet(context).getNetWorkGeneral();
				}
			}catch(Exception e){
				LogUtil.w(TAG,"checkTelecomSet",e);
			}
		}
		
		return telecomEnable;
	}
	/**
	 * 检查电信语音发起网络设置项
	 *
	 * 如果设置的非普通项,则需要查询当前网络情况是否
	 * @param context
	 * @return 返回值为真的时候,表示当前为匹配网络,为假的时候正常情况是不返回的,反回就是只断的情况下才返回
	 */
	public static boolean checkTelecomVoiceSet(Context context,EventManager eventManager){
		//设置当前检查项默认有效,如果需要检测,则根据实际值修改
		boolean telecomEnable = true;
		//当前的电信设置非普通模式
		if(ConfigRoutine.getInstance().getTelecomVoiceNetSet(context) != TelecomSetting.Normal){
			NetStateModel netState = NetStateModel.getInstance();
			final String tabStr = "Not specified network, test paused...";
			int sleepTimes = 0;
			//如果当前的网络类型与指定的网络类型不一至,则等待
			try{
				telecomEnable = netState.getCurrentNetType().getGeneral()
						== ConfigRoutine.getInstance().getTelecomVoiceNetSet(context).getNetWorkGeneral();
				while (!ApplicationModel.getInstance().isTestInterrupt() && !telecomEnable) {
					Thread.sleep(1000);
					//每待15秒钟写一次 "Not specified network, test paused..." Tab信息
					if(sleepTimes++ % 15 == 0){
						eventManager.addTagEvent(context, System.currentTimeMillis(), tabStr);
					}
					telecomEnable = netState.getCurrentNetType().getGeneral()
							== ConfigRoutine.getInstance().getTelecomVoiceNetSet(context).getNetWorkGeneral();
				}
			}catch(Exception e){
				LogUtil.w(TAG,"checkTelecomSet",e);
			}
		}

		return telecomEnable;
	}

	/**
	 * 执行ping命令,返回ping结果
	 * @return
	 */
	private static boolean getPingResult(Context context){
	    StringBuffer pingSB = new StringBuffer();
	    if(Deviceinfo.getInstance().isBusyboxPing()){
	    	pingSB.append(context.getFilesDir().getParent());
	    	pingSB.append("/busybox ");
	    }
	    pingSB.append("ping -c 1 -s ");
	    pingSB.append(1000);
	    pingSB.append(" -w ");
	    pingSB.append(Deviceinfo.getInstance().getPingTimeOutValue()+" ");
    	pingSB.append(Deviceinfo.getInstance().getPingAddress()+"");
    	LogUtil.w(TAG,"--PING:" + pingSB.toString());
    	String result = UtilsMethod.getLinuxCommandResult(pingSB.toString()).toLowerCase();
    	LogUtil.w(TAG,"--PING Result:" + result);
		return (result.contains(SUCC_TAG) || result.contains(SUCC_TAG_2));
	}
}

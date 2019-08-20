package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;

import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.task.TestDialog;
import com.walktour.model.APNModel;

/**
 * 调用拨号服务的上下文，context
 *
 * @author tangwq
 */
public class PPPRule {
    private String tag = "PPPRule";
    public static final int pppNullSuccess = 0;    //无动作成功
    public static final int PPP_RESULT_SUCCESS = 1;    //拨号成功
    public static final int PPP_RESULT_FAIL = 2;    //拨号失败
    public static final int PPP_RESULT_ApnNull = 3;    //Apn无效，拨号失败
    public static final int pppFaildOther = 4;    //其它状态断开拨号
    public static final int PPP_DEFAULT_STATE = 5;    //未拨号初始化状态
    //告警提示相关
    //private NotificationManager mNotificationManager;//通知管理器 
    //private Notification mNotification;//通知

    /**
     * 不断开拨号
     */
    public static final int pppHangupNone = 0;
    /**
     * 每次断开拨号
     */
    public static final int pppHangupEvery = 1;
    /**
     * 任务结束断开拨号
     */
    public static final int pppHangupJobDone = 2;

    /**
     * 拨号失败重拨次数
     */
    private int dPPPFaildTimes = 3;
    /**
     * 拨号失败重拨间隔
     */
    private int pppInterval = 15;
    /**
     * 重置网络多长时间无连接,测试失败
     */
    private int dPPPTimeOut = 30 * 1000;
    /**
     * 拨号处理结果 0:无动作成功，继续执行，1：PPP Diag成功；2：PPP拨号失败；3：无可用的接入点需手工打开;4:其它错误
     */
    private static int pppDiagResult = PPP_DEFAULT_STATE;


    private Context context = null;
    private APNOperate apnOperate = null;
    private ApplicationModel appModel = null;
    private ConfigAPN configApn = null;
    private Deviceinfo deviceInfo = null;
    //private int 	adtType = 0;
    private String apnName = "";

    public PPPRule(Context ctx) {
        this.context = ctx;
        apnOperate = APNOperate.getInstance(ctx);
        appModel = ApplicationModel.getInstance();
        configApn = ConfigAPN.getInstance();
        deviceInfo = Deviceinfo.getInstance();

        //mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    /**
     * APN重置
     */
    public void reSetConfigAPN(ConfigAPN apn) {
        configApn = apn;
    }

    /**
     * 初始化当前数据任务的相关参数信息
     * 包括写入RCU事件的PPP类型
     *
     * @param taskType 根据任务类型获取PPP类型
     */
    private void initDataPara(int netType) {
        if (netType == WalkCommonPara.TypeProperty_Net) {
            apnName = configApn.getDataAPN();
        } else if (netType == WalkCommonPara.TypeProperty_Wap) {
            apnName = configApn.getWapAPN();
        }
    }

    /**
     * 事件窗口内容修改消息
     *
     * @param event
     * @author tangwq
     */
    private void sendEventChange(String event) {
        //ShowInfo.data.addEventParaAddTime(context,event);
		
		/*Intent intent = new Intent(  WalkMessage.testNotifyTestservice );
		intent.putExtra( "eventValue",event );
		context.sendBroadcast( intent );*/
    }

    /**
     * 脱网情况下等待恢复网络动作
     */
    public synchronized void waitForSignal(TaskType taskType) {
        //先判断是否脱网(电信规范中为了统计脱网下拨打的次数，参考文档<脱 网-前台脱 网事件算法文档>)
        int waitServiceTime = 0;
        while (!MyPhoneState.getInstance().isServiceAlive() && !appModel.isTestInterrupt()) {
            LogUtil.w(tag, "---waitServiceTime:" + waitServiceTime + "--mode:" + (waitServiceTime % 15));
            //先检测15秒直到有信号
            if (waitServiceTime % 15 == 0) {
                sendEventChange("Out Of Service");
                if (waitServiceTime != 0) {
                    EventBytes.Builder(context, RcuEventCommand.OUT_OF_SERVICE)
                            .addInteger(RcuEventCommand.getTaskType(taskType))
                            .writeToRcu(System.currentTimeMillis() * 1000);
                }
            }
            try {
                Thread.sleep(1000);
                if (MyPhoneState.getInstance().isServiceAlive()) {
                    Thread.sleep(1000 * 3);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            waitServiceTime++;
        }
    }

    /**
     * 拨号流程添加超时控制方法，如果超时不为-9999，则修改默认的超时为传入，且，超时次数改为1
     *
     * @param isFirstData
     * @param taskType
     * @param netType
     * @return
     */
    public synchronized int pppDial(boolean isFirstData, WalkStruct.TaskType taskType, int netType) {
        return pppDial(isFirstData, taskType, netType, -9999);
    }

    /**
     * 如果当前没有有效接入点，按照传入的网络类型打开有效接和点
     * #################注意不能跨进程调用##################
     *
     * @param isFirstData   是否第一个数据业务，如是且当前接入点有效，断开接入点连接
     * @param pppFaildTimes ppp拨号失败次数
     * @param pppInterval   重新拨号间隔
     * @param apnId         接入点ID，如当前接入点ID与指定接入点ID不一致，断开APN接入点
     * @param adtType       写入RCU事件中的拨号类型
     * @param other         写入RCU事件中的其它参数信息
     * @return 返回值  0:无动作成功，继续执行，1：PPP Diag成功；2：PPP拨号失败；3：无可用的接入点需手工打开;4:其它错误
     * @author tangwq
     */
    public synchronized int pppDial(boolean isFirstData, WalkStruct.TaskType taskType, int netType, int timeOut) {
        pppDiagResult = pppNullSuccess;
        try {
            int pppFaildTimes = dPPPFaildTimes;
            int pppTimeOut = dPPPTimeOut;
            if (timeOut != -9999 && timeOut > 0) {
                pppFaildTimes = 1;
                pppTimeOut = timeOut;
            }

            initDataPara(netType);
            //long time = System.currentTimeMillis() * 1000;
            APNModel jobApnType = apnOperate.getApnByName(apnName);
            if (jobApnType != null || !deviceInfo.getApnList()) {
                String apnId = jobApnType != null ? jobApnType.getId() : "";

                APNModel currentApn = apnOperate.getCurrentApn();
                boolean targetEquqlsCurrent = (currentApn == null || currentApn.getName().equals(apnName));

                //1.如果当前为第一个数据业务，且当前的接入点有效，并闭当前的接入点
                //2.如果当前接入点有效，但当前APN ID与指定的APNID不互时，关闭当前的APN接入点
                if ((isFirstData && apnOperate.checkNetWorkIsConnected())
                        || (apnOperate.checkNetWorkIsConnected() &&
                        (currentApn != null && !currentApn.getName().equals(apnName)))) {
                    pppHangup(pppHangupEvery, false, pppFaildOther);
                }

                //如果当前没有可用的APN接入点，进入PPP拨号流程,建立可用的APN接入点
                if (!apnOperate.checkNetWorkIsConnected()) {
                    //此处用于处理包含3次都无法成功登陆网络的处理情况
                    for (int i = 0; i < pppFaildTimes && !appModel.isTestInterrupt(); i++) {
                        boolean isNetWorkConnect = false;    //网络是否连接正常
                        //当不是第一次切网络时暂停间隔时间重切网络
                        if (i != 0) {
                            LogUtil.w(tag, "--to wait next ppp by " + i);
                            Thread.sleep(1000 * pppInterval);
                        }

                        if (ConfigRoutine.getInstance().checkOutOfService()) {
                            waitForSignal(taskType);
                        }

                        //写拨号开始事件
                        //全部拨号事件都添加APN类型
                        //if(NetStateModel.getInstance(context).getCurrentNetType() == CurrentNetState.LTE){
                        EventBytes.Builder(context, RcuEventCommand.Network_Connect_Start)
                                .addInteger(taskType.getTestType())
                                .writeToRcu(System.currentTimeMillis() * 1000);


                        sendEventChange(WalkStruct.DataTaskEvent.PPP_Dial_Start.toString());

                        long startTime = System.currentTimeMillis();


                        //打开指定网络APN
                        long networkAliveTime = System.currentTimeMillis();
                        LogUtil.w(tag, "---todo ppp open Apn:" + i + "--apnId:" + apnId + "--faildT:" + pppFaildTimes
                                + "--timeO:" + pppTimeOut + "--targetEqualsCurr:" + targetEquqlsCurrent + "--nt:" + networkAliveTime);

                        apnOperate.setMobileDataEnabled(true, apnId, true, pppTimeOut);

                        long endTime = System.currentTimeMillis();

                        NetWorkAlive.doNetWorkAlive(context, networkAliveTime);

                        LogUtil.w(tag, "--doNetWorkAlive Start:" + UtilsMethod.sdFormat.format(endTime)
                                + "--End:" + UtilsMethod.sdFormat.format(System.currentTimeMillis()));

                        int checkNetOut = 0;
                        //twq20131219 仅在目标APN ID 与当前APN ID不一致的情况下，才需要执行当前的切换成功后的二秒等待
                        if (!targetEquqlsCurrent) {
                            //如果在网络重连超时时间内无有效连,则测试失败
                            while (checkNetOut < pppTimeOut && !isNetWorkConnect && !appModel.isTestInterrupt()) {
                                Thread.sleep(50);
                                //此处为防止连上网络后又断开的情况,等待2秒
			    				/*if(apnOperate.checkNetWorkIsConnected()){
			    					Thread.sleep(1000 * 2);
			    					isNetWorkConnect = apnOperate.checkNetWorkIsConnected();
			    				}*/
                                checkNetOut += 50;
                                LogUtil.w(tag, "--waitConnect--" + checkNetOut);
                            }
                        }

                        //此处检测如果无有效APN连接,发送连接失败消息及停止测试消息,不再继续执行
                        LogUtil.w(tag, "---connect:" + (apnOperate.checkNetWorkIsConnected())
                                + "----current:" + (apnOperate.getCurrentApn() == null || !apnOperate.getCurrentApn().getName().equals(apnName))
                                + "--apnName:" + apnName
                                + "--curN:" + (currentApn != null ? currentApn.getName() : "null"));
                        if (!apnOperate.checkNetWorkIsConnected() ||
                                (deviceInfo.getApnList() &&
                                        (apnOperate.getCurrentApn() == null || !apnOperate.getCurrentApn().getName().equals(apnName)))
                                ) {
                            //写拨号失败事件,其紧跟的第一个0为失败状态
                            //UtilsMethod.sendWriteRcuEvent(context,RcuEventCommand.PPP_Dial_Finished, 0);
                            //if(NetStateModel.getInstance(context).getCurrentNetType() == CurrentNetState.LTE){
                            EventBytes.Builder(context, RcuEventCommand.Network_Connect_Failure)
                                    .addInteger(UtilsMethod.convertIpString2Int(MyPhoneState.getInstance().getLocalIpv4Address()))
                                    .writeToRcu(System.currentTimeMillis() * 1000);
							/*}else{
								EventBytes.Builder(context,RcuEventCommand.PPP_Dial_Finished)
								.addInteger(0)
								.writeToRcu(System.currentTimeMillis() * 1000);
							}*/
                            sendEventChange(WalkStruct.DataTaskEvent.PPP_Dial_Failure.toString());
                            pppDiagResult = PPP_RESULT_FAIL;

                            //twq20130624如果拨号失败，且当前WIFI开着，表示当前为WIFI数据享模式，做重新切换绑定WIFI，数据动作
                            if (deviceInfo.getWifiDataOnly().equals("1")) {
                                UtilsMethod.runRootCommand("svc data prefer");
                            } else if (i == (pppFaildTimes - 1) && WifiOperate.checkWifiConnected(context)
                                    && deviceInfo.getWifiDataOnly().equals("2")) {
                                WifiOperate.setDataWifiEnable(context, deviceInfo.getWifiDataOnly());
                            }

                            //UtilsMethod.runRootCommand("svc data prefer");
                            LogUtil.w(tag, "---ppp faild to for--");

                            //统计
                            TotalDataByGSM.getInstance().totalPPP(context, false, 0);

                        } else {
                            //写拨号成功事件,其紧跟的第一个1为成功状态
                            //if(NetStateModel.getInstance(context).getCurrentNetType() == CurrentNetState.LTE){
                            EventBytes.Builder(context, RcuEventCommand.Network_Connect_Success)
                                    .addInteger((int) (endTime - startTime))
                                    .addInteger(UtilsMethod.convertIpString2Int(MyPhoneState.getInstance().getLocalIpv4Address()))
                                    .writeToRcu(System.currentTimeMillis() * 1000);
							/*}else{
								EventBytes.Builder(context,RcuEventCommand.PPP_Dial_Finished)
								.addInteger(1)
								.writeToRcu(System.currentTimeMillis() * 1000);
							}*/
                            sendEventChange(WalkStruct.DataTaskEvent.PPP_Dial_Success.toString());
                            pppDiagResult = PPP_RESULT_SUCCESS;
                            LogUtil.w(tag, "---ppp success to break--");

                            //统计
                            TotalDataByGSM.getInstance().totalPPP(context, true, (int) (endTime - startTime));
                            break;
                        }
                    }
                }
            } else {
                pppDiagResult = PPP_RESULT_ApnNull;
                //showNotification(context.getString(R.string.Sys_Intent_APN_Null),SysSettingAPN.class);
                Intent intent = new Intent(context, TestDialog.class);
                intent.putExtra(TestDialog.EXTRA_FROM, 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.w(tag, "---pppDial:" + pppDiagResult);
        return pppDiagResult;
    }

    /**
     * @param hangupType
     * @param isLastTimes
     */
    public synchronized void pppHangup(int hangupType, boolean isLastTimes) {
        pppHangup(hangupType, isLastTimes, pppDiagResult);
    }

    /**
     * @param hangupType  断开类型: 0:不断开，1:每次断开，2：任务结束断开
     * @param isLastTimes 当前是否任务的最后一次
     * @param pppResult   0无动作成功,1拨号成功,2拨号失败,3Apn无效，拨号失败,4其它状态断开拨号
     * @return
     * @author tangwq
     */
    public synchronized void pppHangup(int hangupType, boolean isLastTimes, int pppResult) {
        //每次断开或任务结束断开且当前为最后一次
        try {
            if (ApplicationModel.getInstance().isNBTest()) {
                //NB测试模式,直接返回
                LogUtil.w(tag, "--pppHangup type:isNBModule");
                return;
            }
            LogUtil.w(tag, "--pppHangup type:" + hangupType + "--last:" + isLastTimes + "--result:" + pppResult);
            if (hangupType == pppHangupEvery ||
                    (hangupType == pppHangupJobDone && isLastTimes)) {
                //只有当前业务拨号成功，此处才需要写入HANGPUP事件
                if (pppResult == PPP_RESULT_SUCCESS || pppResult == pppNullSuccess) {
                    //if(NetStateModel.getInstance(context).getCurrentNetType() == CurrentNetState.LTE){
                    EventBytes.Builder(context, RcuEventCommand.Network_Disconnect)
                            .writeToRcu(System.currentTimeMillis() * 1000);
					/*}else{
				        EventBytes.Builder(context, RcuEventCommand.PPP_Hangup).writeToRcu(System.currentTimeMillis() * 1000);
				        sendEventChange(WalkStruct.DataTaskEvent.PPP_Hungup.toString());
					}*/

                    pppDiagResult = PPP_DEFAULT_STATE;
                }

                if (apnOperate.checkNetWorkIsConnected()) {
                    apnOperate.setMobileDataEnabled(false, "", true, 1000 * 15);
					/*int waitDisconectOut = 0;
					while(apnOperate.checkNetWorkIsConnected() && waitDisconectOut < 1000*15 && !appModel.isTestInterrupt()){
						LogUtil.w(tag,"--wait disconnected2--"+waitDisconectOut+
						        "--version:"+UtilsMethod.getSDKVersionNumber());
						Thread.sleep(200);
						waitDisconectOut += 200;
					}*/

                    if (PPPRule.pppFaildOther == pppResult) {
                        //当前如果SDK版本大于9用的是反注册的方法关闭有效网络时等待三秒才能做打开网络的动作
                        if (UtilsMethod.getSDKVersionNumber() > 9) {
                            Thread.sleep(1000 * 3);
                        }
                    }
                }
            }/*else if(appModel.isTestInterrupt()){
				//如果当前为中断测试或
			}*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭可用接入点
     */
    public synchronized void setDataEnabled(boolean isWlanTest, boolean enabled) {
        try {
            if (isWlanTest) {//如果是wifi测试
                apnOperate.setMobileDataEnabled(enabled, "", true, 1000 * 15);
            } else {
                if (enabled || (!enabled && apnOperate.checkNetWorkIsConnected())) {
                    apnOperate.setMobileDataEnabled(enabled, "", true, 1000 * 15);
                }
            }
        } catch (Exception e) {
            LogUtil.w(tag, e.getMessage());
        }
    }
}
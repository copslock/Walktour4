package com.walktour.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.model.APNModel;
import com.walktour.setting.MyProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class APNOperate {
    static String tag = "APNOperate";
    /**
     * 取得全部apn列表
     */
    static Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
    /**
     * 取得当前设置的apn
     */
    static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    /**
     * 取得current=1的apn列表
     */
    static Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/current");

    private static APNOperate _apnOperat = null;
    private static List<APNModel> mAPNList = null;
    private MyPhoneState myPhoneState;
    String numeric = "46000";
    private Context ctx;
    private int sdkVersion = UtilsMethod.getSDKVersionNumber();


    private APNOperate(Context context) {
        this.ctx = context;
        LogUtil.w(tag, "--APNOperate--");
        myPhoneState = MyPhoneState.getInstance();
        myPhoneState.listenPhoneState(context);
        TelephonyManager telMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        numeric = (telMgr.getSimOperator() != null ? telMgr.getSimOperator() : numeric);
    }

    /**
     * 获得APN操作实例
     *
     * @return
     */
    public static APNOperate getInstance(Context context) {
        if (_apnOperat == null) {
            _apnOperat = new APNOperate(context);
        }
        return _apnOperat;
    }

    /**
     * 根据APN名称设置当前APN网络
     *
     * @param apnName
     */
    public void setSelectApn(final String apnName) {
        if (!Deviceinfo.getInstance().getApnList()) {
            return;
        }

        if (ConfigRoutine.getInstance().checkOutOfService() && !myPhoneState.isServiceAlive()) {
            LogUtil.w(tag, "---setSelectApn " + apnName + "--faild:Out Of Service-");
            return;
        }

        if (sdkVersion < 15) {
            APNModel apn = getApnByName(apnName);
            if (apn == null)
                return;
            LogUtil.w(tag, "--set apn:" + apn.getApn() + ";--id:" + apn.getId() + "--type:" + apn.getType());
            openAPN(apn.getId());
        } else {
            Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_APN_SELECT);
            intent.putExtra(WalktourConst.APN_NAME, apnName);
            this.ctx.sendBroadcast(intent);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得当前接入点信息
     *
     * @return
     */
    public APNModel getCurrentApn() {
        APNModel currentNetApn = null;
        Cursor c = null;

        if (!Deviceinfo.getInstance().getApnList()) {
            return null;
        }

        if (sdkVersion < 15) {
            c = ctx.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        } else {
            c = ctx.getContentResolver().query(MyProvider.APNColumns.PREFERRED_APN_URI, null, null, null, null);

            if (c == null) {
                c = ctx.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
            }
        }
        if (c != null && c.getCount() >= 1) {
            c.moveToFirst();
            currentNetApn = new APNModel();
            currentNetApn.setId(c.getString(c.getColumnIndex("_id")));
            currentNetApn.setApn(APNMatchTools.matchAPN(c.getString(c.getColumnIndex("apn"))));
            currentNetApn.setName(c.getString(c.getColumnIndex("name")));
            currentNetApn.setType(c.getString(c.getColumnIndex("type")));
            currentNetApn.setNumeric(c.getString(c.getColumnIndex("numeric")));
            currentNetApn.setUser(c.getString(c.getColumnIndex("user")));

            c.close();

        } else {
            LogUtil.i(tag, "---currentApn=null");
        }
        return currentNetApn;
    }

    /**
     * 检查当前是否有网络连接
     *
     * @return
     */
    public boolean checkNetWorkIsAvailable() {
        ConnectivityManager cwjManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null) {
            return cwjManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    /**
     * 检查当前网络是否已连接
     *
     * @return
     */
    public boolean checkNetWorkIsConnected() {
        boolean isConnected = false;
        try {
            if (ApplicationModel.getInstance().isNBTest()) {//存在NB模块，判断是否有ppp0，如果存在则表示已连接上
                String strInfo = UtilsMethod.execRootCmdx("ifconfig");
//                LogUtil.w(tag,"strInfo="+strInfo);
                if (strInfo.toLowerCase().contains("ppp0")) {
                    isConnected = true;
                }
            } else {
                if (TaskListDispose.getInstance().isCurrentTaskNeedAssitPermission()){
                    // czc : OTT 相关业务不需要检测网络，后续优化
                    return true;
                }
                ConnectivityManager connectManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
                    isConnected = connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
                } else {
                    isConnected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        }
        return isConnected;
    }

    /**
     * 生成APN列表
     */
    private void buildApnList() {
        mAPNList = new ArrayList<APNModel>();
        String projection[] = {"_id,apn,type,current,name,user,mcc,mnc,numeric"};
        Cursor cr = null;

        if (!Deviceinfo.getInstance().getApnList()) {
            return;
        }

        if (sdkVersion < 16) {
            cr = ctx.getContentResolver().query(APN_TABLE_URI, projection, "numeric=?", new String[]{numeric}, null);
        } else {
            cr = ctx.getContentResolver().query(MyProvider.APNColumns.CONTENT_URI, projection, "numeric=?",
                    new String[]{numeric}, null);
        }

        LogUtil.w(tag, "--buildApnList--crnull:" + (cr == null) + "--crsize:" + ((cr == null) ? -1 : cr.getCount())
                + "--numeric:" + numeric);
        while (cr != null && cr.moveToNext()) {
            try {
                APNModel apn = new APNModel();
                apn.setId(cr.getString(cr.getColumnIndex("_id")));
                apn.setApn(cr.getString(cr.getColumnIndex("apn")));
                apn.setType(cr.getString(cr.getColumnIndex("type")));
                apn.setName(cr.getString(cr.getColumnIndex("name")));
                apn.setUser(cr.getString(cr.getColumnIndex("user")));
                apn.setCurrent(cr.getString(cr.getColumnIndex("current")));
                apn.setMcc(cr.getString(cr.getColumnIndex("mcc")));
                apn.setMnc(cr.getString(cr.getColumnIndex("mnc")));
                apn.setNumeric(cr.getString(cr.getColumnIndex("numeric")));
                mAPNList.add(apn);
            } catch (Exception e) {
                LogUtil.e(tag, "--error:" + e.getMessage());
                e.printStackTrace();
            }

        }
        if (cr != null)
            cr.close();
    }

    /**
     * 获得APN列表,列表对象为APN对象
     *
     * @return
     */
    public List<APNModel> getAPNList() {
        if (mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric)) {
            // updateCurrentNetApn(currentApn.getNumeric());
            buildApnList();
        }
        return mAPNList;
    }

    /**
     * 获得APN命字列表
     *
     * @return
     */
    public String[] getAPNNameList() {
        if (mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric)) {
            // updateCurrentNetApn(currentApn.getNumeric());
            buildApnList();
        }
        String[] names = new String[mAPNList.size()];
        int i = 0;
        for (APNModel apn : mAPNList) {
            names[i++] = apn.getName();
        }
        return names;
    }

    /**
     * 获得APN名称列表,第一个为空
     *
     * @param context
     * @return
     */
    public String[] getAPNNameListByFirstEmpty(Context context) {

        // APNModel currentApn = getCurrentApn();
        if (mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric)) {
            // updateCurrentNetApn(currentApn.getNumeric());
            buildApnList();
        }
        String[] names = new String[mAPNList.size() + 1];
        names[0] = context.getString(R.string.none);
        int i = 1;
        for (APNModel apn : mAPNList) {
            names[i++] = apn.getName();
        }
        return names;
    }

    /**
     * 根据APN名称,获得APN位置序号,APN列表第一个为空
     *
     * @param name
     * @return
     */
    public int getPositonFirstEmpty(String name) {
        int result = getPositon(name);
        if (result >= 0)
            return result + 1;
        return 0;
    }

    /**
     * 根据APN名称,获得APN位置序号
     *
     * @param name
     * @return
     */
    public int getPositon(String name) {
        int result = 0;
        /* 当_apnList列表为空或者列表中numerice的类型与当前APN的类型不同 */
        // APNModel currentApn = getCurrentApn();
        if (mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric)) {
            // updateCurrentNetApn(currentApn.getNumeric());
            buildApnList();
        }
        for (APNModel apn : mAPNList) {
            if (apn.getName().equals(name))
                break;
            result++;
        }
        return result;
    }

    /**
     * 根据传入的APN位置序列号,获得相应APN名称,第一个为空
     *
     * @param position
     * @param context
     * @return
     */
    public String getNameFirstEmpty(int position, Context context) {
        // APNModel currentApn = getCurrentApn();
        if ((mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric))) {
            buildApnList();
        }
        if (position > 0 && position < mAPNList.size())
            return getName(position - 1, context);
        return context.getString(R.string.none);
    }

    /**
     * 根据传入的APN位置序列号,获得相应APN名称
     *
     * @param position
     * @return
     */
    public String getName(int position, Context context) {
        if ((mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric))) {
            // updateCurrentNetApn(getCurrentApn().getNumeric());
            buildApnList();
        }
        if (position >= 0 && position < mAPNList.size())
            return mAPNList.get(position).getName();
        return context.getString(R.string.none);
    }

    /**
     * 根据APN名称,获得APN该APN相关信息
     *
     * @param apnName
     * @return
     */
    public APNModel getApnByName(String apnName) {
        APNModel turnApn = null;
        // APNModel currentApn = getCurrentApn();
        if ((mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric))) {
            // updateCurrentNetApn(getCurrentApn().getNumeric());
            buildApnList();
        }
        for (APNModel apn : mAPNList) {
            if (apn.getName().equals(apnName)) {
                turnApn = apn;
                break;
            }
        }
        return turnApn;
    }

    /**
     * 检查APN列表中是否存在当前名字APN
     *
     * @param apnName
     * @return
     */
    public boolean anpIsExists(String apnName) {
        if (!Deviceinfo.getInstance().getApnList()) {
            return true;
        }

        boolean isExists = false;

        if ((mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric))) {
            buildApnList();
        }
        if (apnName == null || mAPNList.size() <= 0) {
            LogUtil.w(tag, "---apnNameNull:" + (apnName == null) + "--apnList:" + (mAPNList.size()));
            return isExists;
        }
        for (APNModel apn : mAPNList) {
            // LogUtil.w(tag,"---Name:"+apn.getName()+"--apnName:"+apnName);
            if (apn.getName().equals(apnName)) {
                isExists = true;
                break;
            }
        }
        LogUtil.w(tag, "--isExists:" + isExists + "--_apnList" + mAPNList.size());
        return isExists;
    }

    public boolean setMobileDataEnabled(boolean enabled, String apnId, boolean waitResult, int waitTimeOut) {
        return setMobileDataEnabled(true, enabled, apnId, waitResult, waitTimeOut);
    }

    /**
     * 设置GPRS开启关闭<BR>
     * 对Android2.2以上版本以及Android2.2版本以下做适配 android2.2以上使用反射机制进行开启与关闭
     * android2.2以下包括2.2使用修改系统apn达到开启与关闭
     *
     * @param enabled
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public boolean setMobileDataEnabled(final boolean checkSignal, boolean enabled, String apnId, boolean waitResult,
                                        int waitTimeOut) {
        if (TaskListDispose.getInstance().isCurrentTaskNeedAssitPermission()) {
            // czc : OTT 相关业务不需要关闭网络，后续优化
            return true;
        }
        boolean result = true;
        if (ConfigRoutine.getInstance().checkOutOfService() && checkSignal) {
            while (!myPhoneState.isServiceAlive() || ctx.getSystemService(Context.CONNECTIVITY_SERVICE) == null) {
                LogUtil.w(tag, "---setDataEnabled " + apnId + "--faild:Out Of Service-");
                return result;
            }
        }

        if (ApplicationModel.getInstance().isNBTest() && !Deviceinfo.getInstance().isS8()) {//只有S8才测试NB
            return result;
        }
        if (ApplicationModel.getInstance().isNBTest() && Deviceinfo.getInstance().isS8()) {//直接关闭有线数据网络，NB模式下只有S8才能用拨号
            UtilsMethod.runRootCommand(String.format("svc data %s&", "disable"));
//            return result;
        }
        LogUtil.w(tag, "---DataEnabled:" + enabled + "--version:" + sdkVersion + "--apnId:" + apnId);
        try {
            if (Deviceinfo.getInstance().getPPPMode() == Deviceinfo.PPP_MODEL_NONE) {
                LogUtil.w(tag, "ppp module none.");
                return result;
            } else {
                LogUtil.w(tag, "nb is " + ApplicationModel.getInstance().isNBTest());
                if (ApplicationModel.getInstance().isNBTest() && Deviceinfo.getInstance().isS8()) {//NB Iot模式，只有三星S8才支持PPP0拨号
                    String cmdNB = "";
                    if (enabled) {//拨号指令
                        String strInfo = UtilsMethod.execRootCmdx("ifconfig");
                        LogUtil.w(tag, "strInfo=" + strInfo);
                        if (strInfo.toLowerCase().contains("ppp0")) {//如果存在则先断开
                            cmdNB = "pppd  " + ConfigNBModuleInfo.getInstance(ctx).getNbAtPort() + "  115200  disconnect /data/local/chat1";
                            LogUtil.w(tag, cmdNB);
                            UtilsMethod.execRootCmdx(cmdNB);
                            Thread.sleep(2000);
                        }

                        cmdNB = "pppd  " + ConfigNBModuleInfo.getInstance(ctx).getNbAtPort() + "  115200  connect  /data/local/chat1";
                        LogUtil.w(tag, cmdNB);
                        UtilsMethod.execRootCmdx(cmdNB);
                        Thread.sleep(4000);
                        cmdNB = "ip route add default dev ppp0";
                        LogUtil.w(tag, cmdNB);
                        UtilsMethod.execRootCmdx(cmdNB);
                        Thread.sleep(2000);
                        //添加路由
                        boolean isf=false;
                        while(!isf) {
                            strInfo = UtilsMethod.execRootCmdx("getprop net.dns1");
                            LogUtil.w(tag, strInfo);
                            if(strInfo.contains("218.2.2.2")){
                                break;
                            }
                            Thread.sleep(2000);
                            cmdNB = "setprop net.dns1 218.2.2.2";
                            UtilsMethod.execRootCmdx(cmdNB);
                            LogUtil.w(tag, cmdNB);
                        }

                    } else {//去拨号指令：
                        cmdNB = "pppd  " + ConfigNBModuleInfo.getInstance(ctx).getNbAtPort() + "  115200  disconnect /data/local/chat1";
                        LogUtil.w(tag, cmdNB);
                        UtilsMethod.execRootCmdx(cmdNB);
                    }
                    return result;
                } else {


                    if (!ApplicationModel.getInstance().isGeneralMode()
                            && Deviceinfo.getInstance().getPPPMode() == Deviceinfo.PPP_MODEL_SVC) {
                        if (Deviceinfo.getInstance().isSamsungCustomRom()) {
//                            UtilsMethod.runCMD(String.format("svc data %s", (enabled ? "enable" : "disable")));
                            return true;
                        } else {
                            UtilsMethod.runRootCommand(String.format("svc data %s&", (enabled ? "enable" : "disable")));
                        }
                    } else if (sdkVersion > 9) {
                        LogUtil.i(tag, "--version > 9--");
                        // 当当前APN ID与指定APN不符时，修改当前APNID
                        if (!apnId.equals("") && (getCurrentApn() == null || !getCurrentApn().getId().equals(apnId))) {
                            LogUtil.i(tag, "当当前APN ID与指定APN不符时，修改当前APNID");
                            setCurrentApnById(apnId);
                        }
                        if (sdkVersion < 15) {
                            final ConnectivityManager connectManager = (ConnectivityManager) ctx
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                            Class<?> clazz = Class.forName(connectManager.getClass().getName());
                            final Field serviceField = clazz.getDeclaredField("mService");
                            serviceField.setAccessible(true);
                            final Object obj = serviceField.get(connectManager);
                            clazz = Class.forName(obj.getClass().getName());
                            final Method setMobileDataEnabledMethod = clazz.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                            setMobileDataEnabledMethod.setAccessible(true);
                            setMobileDataEnabledMethod.invoke(obj, enabled);
                        } else {
                            LogUtil.i(tag, "--sdk version > 15 --");
                            if (enabled != this.checkNetWorkIsConnected()) {
                                Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_DATA);
                                intent.putExtra(WalktourConst.IS_ENABLE, enabled);
                                this.ctx.sendBroadcast(intent);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            // 如果需要等待返回结果，在此处等待执行状态
            if (waitResult) {
                Thread.sleep(200);
                int waitDisconectOut = 200;

                boolean isNetWorkConnect = checkNetWorkIsConnected();
                // 如果当前设置为关，且接入点开关，或当前高设为开当接入点无效时，在此等待，超时15S
                while ((!enabled && isNetWorkConnect || enabled && !isNetWorkConnect)
                        && !ApplicationModel.getInstance().isTestInterrupt() && waitDisconectOut < waitTimeOut) {
                    LogUtil.w(tag, "--wait DataEnabled result:" + waitDisconectOut + "--isNetWorkConnect:" + isNetWorkConnect);
                    Thread.sleep(200);
                    waitDisconectOut += 200;
                    isNetWorkConnect = checkNetWorkIsConnected();

                    // 此处为防止连上网络后又断开的情况,等待2秒
                    if (enabled && isNetWorkConnect) {
                        Thread.sleep(20);
                        isNetWorkConnect = checkNetWorkIsConnected();
                    }
                }
                result = (waitDisconectOut < waitTimeOut);
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;

    }

    private void openAPN(final String apnId) {
        if (!Deviceinfo.getInstance().getApnList()) {
            return;
        }

        if (ConfigRoutine.getInstance().checkOutOfService() && !myPhoneState.isServiceAlive()) {
            LogUtil.w(tag, "---openAPN " + apnId + "--faild:Out Of Service-");
            return;
        }

        if (Deviceinfo.getInstance().getPPPMode() == Deviceinfo.PPP_MODEL_NONE) {
            return;
        } else if (Deviceinfo.getInstance().getPPPMode() == Deviceinfo.PPP_MODEL_SVC) {
            UtilsMethod.runRootCommand("svc data enable&");
        } else if (sdkVersion < 15) {
            if (mAPNList == null || mAPNList.size() <= 0 || !mAPNList.get(0).getNumeric().equals(numeric)) {
                buildApnList();
            }

            for (APNModel apn : mAPNList) {
                // 当传入的apnId不为空时，只设置指定的APN有效
                if (apnId.equals("") || apn.getId().equals(apnId)) { // 仅设定指定APN有效
                    ContentValues cv = null;
                    if (!getCurrentApn().getId().equals(apnId)) {
                        cv = new ContentValues();
                        cv.put("apn_id", apn.getId());
                        ctx.getContentResolver().update(PREFERRED_APN_URI, cv, null, null);
                    }

                    cv = new ContentValues();
                    cv.put("apn", APNMatchTools.matchAPN(apn.getApn()));
                    cv.put("type", APNMatchTools.matchAPN(apn.getType()));
                    if (Deviceinfo.getInstance().getNettype() == NetType.CDMA.getNetType()
                            || Deviceinfo.getInstance().getNettype() == NetType.EVDO.getNetType()) {
                        cv.put("user", APNMatchTools.matchAPN(apn.getUser()));
                    }
                    ctx.getContentResolver().update(APN_TABLE_URI, cv, "_id=?", new String[]{apn.getId()});

                    if (!apnId.equals("")) {
                        break;
                    }
                }
            }
        } else {
            Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_APN);
            intent.putExtra(WalktourConst.IS_ENABLE, true);
            intent.putExtra(WalktourConst.APN_ID, apnId);
            this.ctx.sendBroadcast(intent);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentApnById(final String apnId) {
        if (!Deviceinfo.getInstance().getApnList()) {
            return;
        }

        if (ConfigRoutine.getInstance().checkOutOfService() && !myPhoneState.isServiceAlive()) {
            LogUtil.w(tag, "---setCurApnId " + apnId + "--faild:Out Of Service-");
            return;
        }
        if (Deviceinfo.getInstance().getPPPMode() == Deviceinfo.PPP_MODEL_NONE) {
            return;
        } else if (Deviceinfo.getInstance().getPPPMode() == Deviceinfo.PPP_MODEL_SVC) {
            UtilsMethod.runRootCommand("svc data enable&");
        } else if (sdkVersion < 15) { // ROM版本小于4.0的才有效
            ContentValues cv = new ContentValues();
            cv.put("apn_id", apnId);
            ctx.getContentResolver().update(PREFERRED_APN_URI, cv, null, null);
        } else {
            Intent intent = new Intent(WalktourConst.WALKTOUR_SYS_SETTING_APN);
            intent.putExtra(WalktourConst.IS_ENABLE, true);
            intent.putExtra(WalktourConst.APN_ID, apnId);
            this.ctx.sendBroadcast(intent);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

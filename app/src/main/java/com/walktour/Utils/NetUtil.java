package com.walktour.Utils;

import android.content.Context;

import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.setting.SysMap;

/**
 * @date on 2018/6/9
 * @describe 网络状态工具
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class NetUtil {
    /**
     * 获取当前显示的基站的网络类型
     *
     * @return
     */
    public static String getNetTypes(Context context) {
        StringBuffer netTypeSB = new StringBuffer();
        SharePreferencesUtil util = SharePreferencesUtil.getInstance(context.getApplicationContext());
        if (util.getInteger(SysMap.BASE_DISPLAY_TYPE, 0) == 1) {
            if (util.getBoolean(SysMap.BASE_GSM, false)) {
                netTypeSB.append(WalktourConst.NetWork.GSM + ",");
            }
            if (util.getBoolean(SysMap.BASE_WCDMA, false)) {
                netTypeSB.append(WalktourConst.NetWork.WCDMA + ",");
            }
            if (util.getBoolean(SysMap.BASE_CDMA, false)) {
                netTypeSB.append(WalktourConst.NetWork.CDMA + ",");
            }
            if (util.getBoolean(SysMap.BASE_TDSCDMA, false)) {
                netTypeSB.append(WalktourConst.NetWork.TDSDCDMA + ",");
            }
            if (util.getBoolean(SysMap.BASE_LTE, false)) {
                netTypeSB.append(WalktourConst.NetWork.LTE + ",");
            }
            if (util.getBoolean(SysMap.BASE_NB_IoT, false)) {
                netTypeSB.append(WalktourConst.NetWork.NB_IoT + ",");
            }
            if (!StringUtil.isNullOrEmpty(netTypeSB.toString())) {
                return netTypeSB.toString().substring(0, netTypeSB.toString().length() - 1);
            }
            return "";
        }
        MyPhoneState state = MyPhoneState.getInstance();
        WalkStruct.NetType nettype = state.getCurrentNetType(context.getApplicationContext());
        switch (nettype) {
            case GSM:
                netTypeSB.append(WalktourConst.NetWork.GSM);
                break;
            case WCDMA:
                netTypeSB.append(WalktourConst.NetWork.WCDMA);
                break;
            case EVDO:
            case CDMA:
                netTypeSB.append(WalktourConst.NetWork.CDMA);
                break;
            case TDSCDMA:
                netTypeSB.append(WalktourConst.NetWork.TDSDCDMA);
                break;
            case LTE:
                netTypeSB.append(WalktourConst.NetWork.LTE);
                break;
            case NBIoT:
                netTypeSB.append(WalktourConst.NetWork.NB_IoT);
                break;
            case CatM:
                netTypeSB.append(WalktourConst.NetWork.CatM);
                break;
            default:
                break;
        }
        return netTypeSB.toString();
    }
}

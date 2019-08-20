/*
 * 文件名: ICellDataListener.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 监控手机小区的切换
 * 创建人: 黄广府
 * 创建时间:2012-8-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.netsniffer;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.walktour.base.util.LogUtil;

import java.util.ArrayList;

/**
 * 监控手机小区的切换<BR>
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public class CellMonitor extends AbsMonitor {
    private static final String TAG = "CellMonitor";
    
    private TelephonyManager tm;
    
    //private CellDao dao;
    private ArrayList<ICellDataListener> listeners;
    
    // 单例
    private static CellMonitor instance = null;
    
    private CellMonitor() {
        super();
        listeners = new ArrayList<ICellDataListener>();
    }
    
    private static CellBean CURRENT_CELLBEAN = null;
    
    public static synchronized CellMonitor getInstance() {
        if (instance == null) {
            instance = new CellMonitor();
        }
        return instance;
    }
    
    /**
     * 获取当前小区
     * 
     * @return
     */
    public static CellBean getCurrentCell() {
        CellBean curBean = CellMonitor.CURRENT_CELLBEAN;
        if (curBean == null) {
            return new CellBean();
        }
        return curBean;
    }
    
    /**
     * 获取当前小区
     * 
     * @return
     */
    public static String getCurrentCellString() {
        return getCurrentCell().toString();
    }
    
    public synchronized void setContext(Context context) {
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        
        CellLocation location = tm.getCellLocation();
        try{
            if (location != null) {
                int phoneType = tm.getPhoneType();
                String operator = tm.getNetworkOperator();
                int mcc = -1, mnc = -1, cid = -1, lac = -1;
                //处理无网络情况
                if(operator != null && operator.length()>3){
                    mcc = Integer.parseInt(operator.substring(0, 3));
                }
                if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                    // type = "gsm";
                    GsmCellLocation gsm = (GsmCellLocation) location;
                    //处理无网络情况
                    if(operator != null && operator.length()>3){
                        mnc = Integer.parseInt(operator.substring(3));
                    }
                    cid = gsm.getCid();
                    lac = gsm.getLac();
                } else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                    // type = "cdma";
                    CdmaCellLocation cdma = (CdmaCellLocation) location;
                    mnc = cdma.getSystemId();
                    cid = cdma.getBaseStationId();
                    lac = cdma.getNetworkId();
                }
                CellBean bean = new CellBean();
                bean.setCid(cid);
                bean.setLac(lac);
                bean.setMcc(mcc);
                bean.setMnc(mnc);
                LogUtil.d(TAG, "小区切换，Cell id：" + cid);
                CellMonitor.CURRENT_CELLBEAN = bean;
                /*            if (dao != null) {
                                NetSniffer.sendCellInfo(bean);
                                dao.insert(bean);
                            }*/
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void addCellDataListener(ICellDataListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void localStart() {
        LogUtil.i(TAG, "启动小区监控.");
        tm.listen(cellListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        CellLocation.requestLocationUpdate();
    }
    
    @Override
    public void localStop() {
        LogUtil.i(TAG, "停止小区监控.");
        tm.listen(cellListener, PhoneStateListener.LISTEN_NONE);
    }
    
    private PhoneStateListener cellListener = new PhoneStateListener() {
        public void onCellLocationChanged(CellLocation location) {
            int phoneType = tm.getPhoneType();
            String operator = tm.getNetworkOperator();
            
            CellBean bean = null;
            if (!"".equals(operator)) {
                // String type="";
                int mcc = -1, mnc = -1, cid = -1, lac = -1;
                mcc = Integer.parseInt(operator.substring(0, 3));
                if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                    // type = "gsm";
                    GsmCellLocation gsm = (GsmCellLocation) location;
                    mnc = Integer.parseInt(operator.substring(3));
                    cid = gsm.getCid();
                    lac = gsm.getLac();
                } else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                    // type = "cdma";
                    CdmaCellLocation cdma = (CdmaCellLocation) location;
                    mnc = cdma.getSystemId();
                    cid = cdma.getBaseStationId();
                    lac = cdma.getNetworkId();
                }
                bean = new CellBean();
                bean.setCid(cid);
                bean.setLac(lac);
                bean.setMcc(mcc);
                bean.setMnc(mnc);
                LogUtil.d(TAG, "小区切换，Cell id：" + cid);
                CellMonitor.CURRENT_CELLBEAN = bean;
                /*                if (dao != null) {
                                    NetSniffer.sendCellInfo(bean);
                                    dao.insert(bean);
                                }*/
                
            } else {
                CellMonitor.CURRENT_CELLBEAN = null;
            }
            for (ICellDataListener listener : listeners) {
                listener.onChangeCell(bean);
            }
        };
    };
    
}

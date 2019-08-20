/*
 * 文件名: RefreshEventManager.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 刷新事件操作类
 * 创建人: 黄广府
 * 创建时间:2012-9-10
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.framework.view;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;

import java.util.ArrayList;
import java.util.List;


/**
 * 刷新事件操作类<BR>
 * 通过此类提供的刷新管理，尽量减少使用广播机制来进行刷新
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-9-10] 
 */
public class RefreshEventManager {
    
    /**
     * 监听器数组列表
     */
    private static List<RefreshEventListener> refreshEventList = new ArrayList<RefreshEventManager.RefreshEventListener>();
    
    /**
     * 刷新事件<BR>
     * 枚举类型，可自行增加事件
     * @author 黄广府
     * @version [WalkTour Client V100R001C03, 2012-9-10]
     */
    public enum RefreshType {
        //刷新仪表盘事件
        REFRESH_DASHBOARD,
        
        //刷新测试任务界面
        REFRESH_BASETASK,
        
        //刷新参数界面TAB
        REFRESH_PARAM_TAB,
        
        //刷新扫频仪界面
        REFRESH_SCANNER,
        
        //WalkTour定时刷新事件
        ACTION_WALKTOUR_TIMER_CHANGED,
        
        REFRESH_STREAM,   
        
        //刷新地图基站,自动居中
        REFRSH_GOOGLEMAP_BASEDATA
        
    }
    
    
    /**
     * 添加一个刷新监听事件<BR>
     * 请确认已经实现了RefreshEventListener接口
     * @param listener
     */
    public static void addRefreshListener(RefreshEventListener listener) {
        synchronized (refreshEventList) {
            if (!refreshEventList.contains(listener)) {
                refreshEventList.add(listener);
                LogUtil.i("RefreshEventManager", "addRefreshListener:" + listener.getClass());
            }
        }
    }
    
    /**
     * 移除一个刷新监听器<BR>
     * 请确认已经实现了RefreshEventListener接口
     * @param listener
     */
    public static void removeRefreshListener(RefreshEventListener listener) {
        synchronized (refreshEventList) {
            if (refreshEventList.contains(listener)) {
                refreshEventList.remove(listener);
                LogUtil.i("RefreshEventManager", "removeRefreshListener:" + listener.getClass());
            }
        }
    }
    
    /**
     * 通知界面刷新<BR>
     * 遍历事件列表，通知刷新
     * @param refreshType  刷新类型
     */
    public static void notifyRefreshEvent(final RefreshType refreshType,
            final Object object) {
        synchronized (refreshEventList) {
            for (int i = 0; i < refreshEventList.size(); i++) {
                refreshEventList.get(i).onRefreshed(refreshType, object);
                if (TraceInfoInterface.currentNetType!=null) {
                    MyPhoneState.saveNetTypeToSpf(TraceInfoInterface.currentNetType.getNetTypeName());
                }
            }
        }
    }
    
    
    
    
    /**
     *  刷新界面接口，暂时只定义一个参数RefreshType：刷新类型<BR>
     * [功能详细描述]
     * @author 黄广府
     * @version [WalkTour Client V100R001C03, 2012-9-10]
     */
    public interface RefreshEventListener {
        void onRefreshed(final RefreshType refreshType,Object object);
    }
    
    
}

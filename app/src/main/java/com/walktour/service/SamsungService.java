package com.walktour.service;

import android.content.Context;

import com.b2b.rom.ISamsungDevice;

/***
 * 三星S9定制机非root版特殊机型
 */
public class SamsungService {
    private static SamsungService instance = null;
    private ISamsungDevice service=null;

    /***
     * 私有构造器
     * @param mContext
     */
    private  SamsungService(Context mContext)
    {
        service=new ISamsungDevice(mContext);
    }

    /**
     * 绑定服务
     * @param mContext
     * @return
     */
    public static SamsungService getInStance(Context mContext){
        if(instance==null)
            synchronized (SamsungService.class) {
                if(instance==null)
                    instance = new SamsungService(mContext);
            }

        return instance;

    }

    /***
     * 获取接口服务
     * @return
     */
    public ISamsungDevice getService()
    {
        return service;
    }

    /**
     * 解绑服务
     */
    public void release(){
        if(null!=service){
            service.Release();
            service=null;
        }
    }
}

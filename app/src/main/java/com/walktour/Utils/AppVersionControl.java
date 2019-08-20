package com.walktour.Utils;

/***
 * App版本控制,发布版本使用,注意：这个类对外提供的方法是互斥的,只能有一个为true,目前这个类的方法都是写死的，发版本时使用.
 */
public class AppVersionControl {

    private static AppVersionControl instance = new AppVersionControl();

    /**
     * 防止外部构造
     */
    private AppVersionControl()
    {

    }

    /**
     * 获取单例
     *
     * @return
     */
    public static AppVersionControl getInstance()
    {
        return instance;
    }

    /**
     * 是否电信巡检版本
     *
     * @return
     */
    public boolean isTelecomInspection()
    {
        return false;
    }


    /***
     * 是否时联通集团版本,使用的手机是三星S8 定制机(非root版)
     * @return
     */
    public boolean isUnicomGroup()
    {
        return false;
    }


    /***
     * 是否是感知测试,北京招标，包含单站验证的功能
     * @return
     */
    public boolean isPerceptionTest(){
        return false;
    }


    /**
     * 是否是华为版本,注意华为版本必须要有华为权限
     *
     * @return
     */
    public boolean isHuaWeiTest(){
        return false;
    }
}

package com.dingli.watcher.jni;
import com.dingli.watcher.model.*;

public class GNVControllerJNI {
   public static   GNVManager mManager;//管理实现协议的方法
    static {
        try {
            System.loadLibrary("CommonDataSetBase");
            System.loadLibrary("GNWDataProtocolParse");
            System.loadLibrary("GNVController");

        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static void setManager(GNVManager manager){
        mManager=manager;

    }
    ///////////////////////////
    //回调函数
    //要发送的下行命令数据
    public static  void GNVShouldWriteData(byte[] data)
    {
        //需要广播到蓝牙连接，发送出去
        if (mManager!=null){
            mManager.GNVShouldWriteData(data);
        }

    }

    //接收到的数据更新
    public static void GNVDidGotUpdate(int mode,int isStop, byte[] data)
    {
        //mode 模式 0：室内测试 1：地铁测试 2：加速度获取测试
        //iStop  是否结束测试，这个有时后不返回，建议 在 StopTest后 延时2秒断开连接
        if (mManager!=null){
            mManager.GNVDidGotUpdate(mode, isStop, data);
        }
    }
    //回调函数  状态
    public  static  void GNVDidChangeState(int state){
        if (state==1){
            Start();
        }
    };


    ///////////////////////

    /**
     * 创建一个格纳微数据业务管理对象句柄
     * @return 一个格纳微数据业务管理句柄；
     */
    public static native int CreateController();

    /**
     * 释放一个格纳微数据业务管理对象实例pGNVController，并将其设置为空
     *
     * @param pGNVController
     *          输出参数，此为CreateController创建实例；
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int FreeController(int pGNVController);

    //开始测试时，请确保蓝牙连接已经建立
    /**
     * 开始室内测试
     *
     * @param pGNVController
     *          输出参数，此为CreateController创建实例；
     * @param floorCheck
     *          是否开启楼层识别 0：关闭 1：开启
     *@param longCheck
     *          是否开启长距离识别 0：关闭 1：开启
     *@param liftCheck
     *          是否开启电梯识别 0：关闭 1：开启
     *@param floorheight
     *          楼层高度，单位米，精度1位小数；
     *@param groundHeight
     *          地面层高度，单位米，精度1位小数；
     *@param currentFloor
     *          当前楼层，默认1楼；
     *@param devModel
     *          格纳微设备型号： 0：L1:l2  1：3合一；
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int StartRoomTest(int pGNVController,int floorCheck,int liftCheck,int longCheck,float floorheight,float groundHeight,int currentFloor,int devModel);

    /**
     * 开始地铁测试
     *
     * @param pGNVController
     *          输出参数，此为CreateController创建实例；
     * @param cityXmlPath
     *          城市xml文件路径
     *@param lineIndex
     *          线路下标
     *@param startStatationIndex
     *          出发站点下标
     *@param endStationIndex
     *          目的站点下标
     *@param devModel
     *          格纳微设备型号： 0：L1:l2  1：3合一；
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int StartMetrolTest(int pGNVController,String cityXmlPath,int lineIndex,int startStatationIndex,int endStationIndex,int devModel);

    /**
     * 开始加速度测试
     *
     * @param pGNVController
     *          输出参数，此为CreateController创建实例；
     *@param devModel
     *          格纳微设备型号： 0：L1:l2  1：3合一；
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int StartAccTest(int pGNVController,int devModel);

    /**
     * 停止测试 调用后建议延时2秒断开蓝牙连接
     *
     * @param pGNVController
     *          输出参数，此为CreateController创建实例；
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int StopTest(int pGNVController);

    /**
     * 接收到的数据
     *
     * @param pGNVController
     *          输出参数，此为CreateController创建实例；
     * @param data
     *          蓝牙上传数据
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int RecieveData(int pGNVController,byte[] data);

    /////////////////////////////////////
    //数据转换
    /**
     * 把输出的结构转为java对象
     * @param data
     *          定位结构
     * @param roomPoint
     *          java对象
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int ParseRoomPoints(byte[] data,GNVRoomPoint roomPoint);


    /**
     * 接收到的数据
     *
     * @param data
     *          定位结构
     * @param mGps
     *          java对象
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int ParseMetroGPS(byte[] data,GNVMetroGPS mGps);

    /**
     * 接收到的数据
     *
     * @param data
     *          定位结构
     * @param mAcc
     *          java对象
     * @return 0-成功，非0-失败，错误代码；
     */
    public static native int ParseAccValue(byte[] data,GNVAcceleration mAcc);



    public static native int SetLogPath(int ahandle,String path);
    public static native int Start();


}

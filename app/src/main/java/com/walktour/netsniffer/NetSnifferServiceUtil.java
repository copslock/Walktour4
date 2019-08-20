package com.walktour.netsniffer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.service.INetSniffer;

import java.util.List;

/**
 * 抓包工具单例类
 */
public class NetSnifferServiceUtil {
    private final String TAG=NetSnifferServiceUtil.class.getSimpleName();
    private static final NetSnifferServiceUtil instance= new NetSnifferServiceUtil();
    private INetSniffer iNetSniffer;
    private NetSnifferServiceUtil() {
    }
    public static final NetSnifferServiceUtil getInstance() {
        return instance;
    }
    public void bindService(Context context,String pcapFileName){
        Intent servicex = new Intent(context,NetSnifferService.class);
        servicex.putExtra("pcapFileName",pcapFileName);
        context.bindService(servicex, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context){
        context.unbindService(connection);
    }
    //连接服务
    private ServiceConnection connection=new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            iNetSniffer=null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取绑定的接口
            iNetSniffer=INetSniffer.Stub.asInterface(service);
        }

    };

    public List<packet_dissect_info> getDatas(){
        try {
            if(null!=iNetSniffer) {
                return iNetSniffer.getDatas();
            }else{
//                LogUtil.w(TAG,"iNetSniffer is null.");
            }
        } catch (RemoteException e) {
            LogUtil.w(TAG,"y="+e.getMessage());
        }
        return null;
    }
    public String getStringInfo(int packet_idx){
        try {
            if(null!=iNetSniffer) {
                String x=iNetSniffer.getStringInfo(packet_idx);
                LogUtil.w(TAG,"x="+x);
                return x;
            }else{
//                LogUtil.w(TAG,"iNetSniffer is null.");
            }
        } catch (RemoteException e) {
            LogUtil.w(TAG,"y="+e.getMessage());
        }
        return "";
    }

    public void buildTcpipSimpleInfo(){
        try {
            if(null!=iNetSniffer) {
                iNetSniffer.buildTcpipSimpleInfo();
            }else{
//                LogUtil.w(TAG,"iNetSniffer is null.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
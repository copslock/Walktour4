package com.walktour.netsniffer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.control.netsniffer.NetSniffer;
import com.walktour.service.INetSniffer;

import java.util.List;

/***
 * 抓包独立进程服务
 */
public class NetSnifferService extends Service {

    private String TAG = NetSnifferService.class.getSimpleName();

    /**
     * 构造函数
     */
    public NetSnifferService() {

    }

    private INetSniffer.Stub bind = new INetSniffer.Stub() {

        @Override
        public List<packet_dissect_info> getDatas() throws  RemoteException{

            return NetSniffer.getInstance().getDatas();
        }
        @Override
        public String getStringInfo(int packet_idx) throws RemoteException {
            LogUtil.w(TAG, "getStringInfo,packet_idx=" + packet_idx);
            return NetSniffer.getInstance().buildTcpIpDetailInfo(packet_idx).proto_tree;
        }

        @Override
        public void buildTcpipSimpleInfo() throws RemoteException {
            LogUtil.w(TAG, "buildTcpipSimpleInfo");
            NetSniffer.getInstance().buildTcpipSimpleInfo();
        }
    };

    public IBinder onBind(Intent intent) {
        LogUtil.w(TAG, "ServiceServer onBind");
        String pcapFileName = intent.getStringExtra("pcapFileName");
        LogUtil.w(TAG, "onStartCommand,pcapFileName=" + pcapFileName);
        NetSniffer.getInstance().start(getBaseContext(), pcapFileName);
        return bind;
    }

    public void onCreate() {
        LogUtil.w(TAG, "ServiceServer onCreate");
        super.onCreate();
    }

    public void onDestroy() {
        LogUtil.w(TAG, "ServiceServer onDestroy");
        NetSniffer.getInstance().stop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
 
}

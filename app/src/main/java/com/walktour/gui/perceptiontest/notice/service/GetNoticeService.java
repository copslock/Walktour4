package com.walktour.gui.perceptiontest.notice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.NetRequest;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.notice.bean.MessageBean;
import com.walktour.gui.perceptiontest.notice.bean.MessageListBean;
import com.walktour.gui.perceptiontest.notice.dao.MessageDaoUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

/**
 * @author Max
 * @data 2018/11/21
 */
public class GetNoticeService extends Service {
    private static final String TAG = "GetNoticeService";
    Gson gson;
    private int isReadNum=0;
    MessageDaoUtil daoUtil;
    private ServerManager mServerManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG,"on Create");
        init();

    }

    private void init() {
        gson=new Gson();
        daoUtil=new MessageDaoUtil(this);
        this.mServerManager = ServerManager.getInstance(this);
        new GetNoticeThread().start();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG,"onDestroy");
        super.onDestroy();
    }
    class GetNoticeThread extends Thread{
        @Override
        public void run() {
            try {
                while (isAlive()){
                    getNotice();
                    Thread.sleep(30*1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 得到公告
     */
    private void getNotice() {
        String ip = this.mServerManager.getDownloadFleetIp();
        int port = this.mServerManager.getDownloadFleetPort();
        String url = "http://"+ip+":"+port+"/services/AppService.svc/GetNotice?Start=0&PageSize=200";
        HashMap<String, String> params = new HashMap<>();
        NetRequest.getFormRequest(url, params, new NetRequest.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                // fromJson 将json字符串转为bean对象
                MessageListBean bean= gson.fromJson(result, MessageListBean.class);
                if (bean!=null){
                    LogUtil.d(TAG,""+bean);
                    List<MessageBean> lists= bean.getBeans();
                    for (int i=isReadNum;i<lists.size();i++){
                       daoUtil.insertMessage(lists.get(i));
                   }
                    isReadNum=lists.size();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {
                LogUtil.d(TAG,getString(R.string.init_server_failed));
                LogUtil.d(TAG,""+e);
            }
        });
    }
}

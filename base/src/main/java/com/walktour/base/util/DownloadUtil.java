package com.walktour.base.util;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @date on 2018/7/9
 * @describe 基于okhttp的下载工具
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */



public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    public static final int DOWNLOAD_FAIL=0;

    public static final int DOWNLOAD_PROGRESS=1;

    public static final int DOWNLOAD_SUCCESS=2;

    private static DownloadUtil downloadUtil;

    private final OkHttpClient okHttpClient;

    public static DownloadUtil getInstance() {

        if (downloadUtil == null) {

            downloadUtil = new DownloadUtil();

        }

        return downloadUtil;

    }



    private DownloadUtil() {

        okHttpClient = new OkHttpClient.Builder().
                connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS).
                        build();
    }



    /**

     *

     */

    public void download(final String url,final String saveDir,final OnDownloadListener listener){

        this.listener=listener;

        Request request=new Request.Builder().url(url).addHeader("Accept-Encoding", "gzip,deflate").build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override

            public void onFailure(Call call, IOException e) {

                Message message= Message.obtain();

                message.what=DOWNLOAD_FAIL;

                mHandler.sendMessage(message);

            }



            @Override

            public void onResponse(Call call, Response response) throws IOException {

                InputStream is=null;

                byte[] buf=new byte[2048];

                int len=0;

                FileOutputStream fos=null;

                //储存下载文件的目录

                String savePath=isExistDir(saveDir);

                try{

                    is=response.body().byteStream();

                    long total=response.body().contentLength();
                    LogUtil.d(TAG,"總大小："+total);
                    File file=new File(savePath,getHeaderFileName(response));

                    fos=new FileOutputStream(file);

                    long sum=0;

                    while((len = is.read(buf))!=-1){
//[ (DownloadUtil.java:111) # onResponse() ] DownloadUtil:總大小：-1
//[ (DownloadUtil.java:123) # onResponse() ] DownloadUtil:已經下載大小：1521
//[ (DownloadUtil.java:125) # onResponse() ] DownloadUtil:已經下載進度：-152100
                        fos.write(buf,0,len);

                        sum+=len;
                        LogUtil.d(TAG,"已經下載大小："+sum);
                        int progress=total==-1?0:(int)(sum*1.0f/total*100);//总大小未识别是-1的时候，则进度为0；
                        LogUtil.d(TAG,"已經下載進度："+progress);
                        //下载中

                        Message message=Message.obtain();

                        message.what=DOWNLOAD_PROGRESS;

                        message.obj=progress;

                        mHandler.sendMessage(message);



                    }

                    fos.flush();

                    //下载完成

                    Message message=Message.obtain();

                    message.what=DOWNLOAD_SUCCESS;

                    message.obj=file.getAbsolutePath();

                    mHandler.sendMessage(message);

                }catch (Exception e){

                    Message message=Message.obtain();

                    message.what=DOWNLOAD_FAIL;

                    mHandler.sendMessage(message);

                }finally{

                    try{

                        if(is!=null)

                            is.close();

                    }catch (IOException e){
                        e.printStackTrace();


                    }

                    try {

                        if(fos!=null){

                            fos.close();

                        }

                    }catch (IOException e){
                        e.printStackTrace();


                    }

                }

            }

        });

    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return "";
    }


    private String getNameFromUrl(String url) {

        return url.substring(url.lastIndexOf("/")+1);

    }





    private String isExistDir(String saveDir) throws IOException {

        File downloadFile=new File(saveDir);

        if(!downloadFile.mkdirs()){

            downloadFile.createNewFile();

        }

        String savePath=downloadFile.getAbsolutePath();

        return savePath;

    }









    private Handler mHandler=new Handler(){

        @Override

        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            switch (msg.what){

                case DOWNLOAD_PROGRESS:

                    listener.onDownloading((Integer) msg.obj);

                    break;

                case DOWNLOAD_FAIL:

                    listener.onDownloadFailed();

                    break;

                case DOWNLOAD_SUCCESS:

                    listener.onDownloadSuccess((String) msg.obj);

                    break;

            }

        }

    };





    OnDownloadListener listener;

    public interface OnDownloadListener{

        /**

         * 下载成功

         */

        void onDownloadSuccess(String path);

        /**

         * 下载进度

         * @param progress

         */

        void onDownloading(int progress);

        /**

         * 下载失败

         */

        void onDownloadFailed();

    }

}

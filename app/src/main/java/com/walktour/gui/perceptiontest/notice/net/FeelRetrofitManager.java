package com.walktour.gui.perceptiontest.notice.net;

import android.content.Context;

import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.RetrofitUtil;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.notice.bean.MessageListBean;
import com.walktour.gui.singlestation.net.model.LoginResult;
import com.walktour.gui.singlestation.net.model.StationPlatformInfo;
import com.walktour.gui.singlestation.net.model.StationSearch;
import com.walktour.gui.singlestation.net.model.UploadSurveyStationResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 感知测试ihttp请求管理类
 * Created by jinfeng.xie on 2018/11/20.
 */

public class FeelRetrofitManager {
    /**
     * 日志标识
     */
    private static final String TAG = "SingleStationRetrofitManager";
    /**
     * 实例对象
     */
    private static FeelRetrofitManager sInstance;
    /**
     * 基础URL，除ip和端口外要添加的
     */
    private RetrofitService mRetrofitService;
    /**
     * http请求类
     */
    private RetrofitUtil mRetrofitUtil;
    /**
     * 请求基础URL
     */
    private static String sBaseUrl;
    /**
     * 是否已经登录
     */
    private boolean isLogin = false;

    /**
     */
    private FeelRetrofitManager() {
        StringBuilder url = new StringBuilder("http://172.16.23.253/services/AppService.svc/");
        sBaseUrl = url.toString();
        this.mRetrofitUtil = RetrofitUtil.getInstance();
        this.mRetrofitService = this.mRetrofitUtil.createRetrofitService(sBaseUrl, RetrofitService.class);
    }

    public boolean isLogin() {
        return this.isLogin;
    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static FeelRetrofitManager getInstance() {
        if (sInstance == null)
            sInstance = new FeelRetrofitManager();
        return sInstance;
    }


    public void getNoticeList(final Context context, final int start, final int pageSize , final SimpleCallBack callBack) {
        Call<MessageListBean> call = this.mRetrofitService.getNotice(start,pageSize);
        call.enqueue(new Callback<MessageListBean>() {
            @Override
            public void onResponse(Call<MessageListBean> call, Response<MessageListBean> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callBack.onSuccess();
                    LogUtil.d(TAG,""+response.body());
                } else {
                    callBack.onFailure(context.getString(R.string.str_http_request_fail));
                    LogUtil.d(TAG,""+context.getString(R.string.str_http_request_fail));
                }
            }

            @Override
            public void onFailure(Call<MessageListBean> call, Throwable throwable) {
                callBack.onFailure(context.getString(R.string.str_http_request_fail));
                LogUtil.d(TAG,""+context.getString(R.string.str_http_request_fail));
            }
        });
    }



    /**
     * 返回的数据写为一个文件
     * @param body 文件内容
     * @param fileName 文件名
     * @return
     */
    private boolean writeResponseBodyToDisk(ResponseBody body,String fileName) {
        try {
            File futureStudioIconFile = new File(fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream=null;
                }

                if (outputStream != null) {
                    outputStream.close();
                    outputStream=null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 调用服务端接口
     */
    private interface RetrofitService {

        /**
         * 登录服务器
         *
         * @param loginUser     登录账号
         * @param loginPassword 登录密码
         * @return 登录结果
         */
        @GET("Authorize")
        Call<LoginResult> login(@Query("Account") String loginUser, @Query("Password") String loginPassword);

        /**
         * 查询基站数据
         *
         * @param loginUser 登录账号
         * @param keyword   基站名称或ENodeBID
         * @return 基站列表
         */
        @GET("SiteList")
        Call<List<StationSearch>> searchStationList(@Query("Account") String loginUser, @Query("Name") String keyword);

        /**
         * 查询基站数据
         *
         * @param loginUser 登录账号
         * @param lat       纬度
         * @param lon       经度
         * @param distance  和当前位置的距离(米)
         * @return 基站列表
         */
        @GET("SiteList")
        Call<List<StationSearch>> searchStationList(@Query("Account") String loginUser, @Query("Lat") double lat, @Query("Lon") double lon, @Query("Distance") int distance);

        /**
         * 获取平台基站配置信息
         *
         * @param siteId 平台基站ID
         * @return 基站配置信息
         */
        @GET("GetSiteInfo/{siteId}")
        Call<StationPlatformInfo> getStationInfo(@Path("siteId") int siteId);


        /**
         * 基站勘察的数据上传
         *
         * @param siteId  平台基站ID
         * @param zipFile 勘察结果json和勘察图片压缩后的zip文件
         * @return
         */
        @Multipart
        @POST("UploadSiteInfo/{siteId}")
        Call<UploadSurveyStationResult> uploadSurveyStationInfo(@Path("siteId") int siteId,
                                                                @Part("Account") String loginAccount,
                                                                @Part("surveyResult\"; filename=\"surveyResult.zip") RequestBody zipFile);

        /**
         * 获取平台基站配置信息
         *
         * @param siteId 平台基站ID
         * @return 基站配置信息
         */
        @GET("GetSiteReport/{siteId}")
        Call<ResponseBody> getStationReport(@Path("siteId") String siteId);
        /**
         * 获取平公告信息
         *
         */
        @GET("GetNotice")
        Call<MessageListBean> getNotice(@Path("Start") int start, @Path("PageSize") int pageSize);
    }
}

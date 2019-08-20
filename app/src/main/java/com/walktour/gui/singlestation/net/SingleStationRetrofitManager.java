package com.walktour.gui.singlestation.net;

import android.content.Context;

import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.MD5Util;
import com.walktour.base.util.RetrofitUtil;
import com.walktour.gui.R;
import com.walktour.gui.singlestation.net.model.LoginResult;
import com.walktour.gui.singlestation.net.model.StationPlatformInfo;
import com.walktour.gui.singlestation.net.model.StationPlatformInfoCallBack;
import com.walktour.gui.singlestation.net.model.StationSearch;
import com.walktour.gui.singlestation.net.model.StationSearchCallBack;
import com.walktour.gui.singlestation.net.model.UploadSurveyStationResult;
import com.walktour.gui.singlestation.net.model.UploadSurveyStationResultCallback;

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
 * 单站验证http请求管理类
 * Created by wangk on 2017/8/21.
 */

public class SingleStationRetrofitManager {
    /**
     * 日志标识
     */
    private static final String TAG = "SingleStationRetrofitManager";
    /**
     * 实例对象
     */
    private static SingleStationRetrofitManager sInstance;
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
     * @param ip   服务端地址
     * @param port 服务端端口
     */
    private SingleStationRetrofitManager(String ip, int port) {
        LogUtil.d(TAG, "---new()----");
        StringBuilder url = new StringBuilder("http://");
        url.append(ip).append(":").append(port).append("/Services/SSVService.svc/");
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
     * @param ip   服务端地址
     * @param port 服务端端口
     * @return 唯一实例
     */
    public static SingleStationRetrofitManager getInstance(String ip, int port) {
        if (sInstance == null)
            sInstance = new SingleStationRetrofitManager(ip, port);
        return sInstance;
    }

    /**
     * 登录服务器
     *
     * @param context       上下文
     * @param loginUser     登录账号
     * @param loginPassword 登录密码
     * @param callBack      回调类
     */
    public void login(final Context context, String loginUser, String loginPassword, final SimpleCallBack callBack) {
        Call<LoginResult> call = this.mRetrofitService.login(loginUser, MD5Util.encode(loginPassword));
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    mRetrofitUtil.setLogin(sBaseUrl);
                    isLogin = true;
                    callBack.onSuccess();
                } else {
                    callBack.onFailure(context.getString(R.string.str_http_request_fail));
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable throwable) {
                callBack.onFailure(context.getString(R.string.str_http_request_fail));
            }
        });
    }

    /**
     * 查询基站数据
     *
     * @param context   上下文
     * @param loginUser 登录账号
     * @param keyword   基站名称或ENodeBID
     * @param callBack  回调类
     */
    public void searchStationList(final Context context, String loginUser, String keyword, final StationSearchCallBack callBack) {
        Call<List<StationSearch>> call = this.mRetrofitService.searchStationList(loginUser, keyword);
        call.enqueue(new Callback<List<StationSearch>>() {
            @Override
            public void onResponse(Call<List<StationSearch>> call, Response<List<StationSearch>> response) {
                callBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<StationSearch>> call, Throwable throwable) {
                callBack.onFailure(context.getString(R.string.str_http_request_fail));
            }
        });
    }

    /**
     * 查询基站数据
     *
     * @param context   上下文
     * @param loginUser 登录账号
     * @param lat       纬度
     * @param lon       经度
     * @param distance  和当前位置的距离(米)
     * @param callBack  回调类
     */
    public void searchStationList(final Context context, String loginUser, double lat, double lon, int distance, final StationSearchCallBack callBack) {
        Call<List<StationSearch>> call = this.mRetrofitService.searchStationList(loginUser, lat, lon, distance);
        call.enqueue(new Callback<List<StationSearch>>() {
            @Override
            public void onResponse(Call<List<StationSearch>> call, Response<List<StationSearch>> response) {
                callBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<StationSearch>> call, Throwable throwable) {
                callBack.onFailure(context.getString(R.string.str_http_request_fail));
            }
        });
    }

    /**
     * 获取平台基站配置信息
     *
     * @param siteId   平台基站ID
     * @param callBack 回调类
     */
    public void getStationInfo(final Context context, int siteId, final StationPlatformInfoCallBack callBack) {
        Call<StationPlatformInfo> call = this.mRetrofitService.getStationInfo(siteId);
        call.enqueue(new Callback<StationPlatformInfo>() {
            @Override
            public void onResponse(Call<StationPlatformInfo> call, Response<StationPlatformInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callBack.onSuccess(response.body());
                } else {
                    callBack.onFailure(context.getString(R.string.single_station_toast_import_platform_station_failed));
                }
            }

            @Override
            public void onFailure(Call<StationPlatformInfo> call, Throwable throwable) {
                callBack.onFailure(throwable.getMessage());
            }
        });
    }

    /**
     * 基站勘察的数据上传
     *
     * @param context
     * @param loginUser
     * @param siteId    平台基站ID
     * @param zipFile   勘察结果json和图片压缩后的zip文件
     * @param callback  回调
     */
    public void uploadSurveyStationInfo(final Context context, String loginUser, int siteId, RequestBody zipFile, final UploadSurveyStationResultCallback callback) {
        Call<UploadSurveyStationResult> call = this.mRetrofitService.uploadSurveyStationInfo(siteId, loginUser, zipFile);
        call.enqueue(new Callback<UploadSurveyStationResult>() {
            @Override
            public void onResponse(Call<UploadSurveyStationResult> call, Response<UploadSurveyStationResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UploadSurveyStationResult result = response.body();
                    if (result.isSuccess()) {
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure(result.getMessage());
                    }
                } else {
                    callback.onFailure(context.getString(R.string.network_request_failed));
                }
            }

            @Override
            public void onFailure(Call<UploadSurveyStationResult> call, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
    }

    public void getStationReport(final Context context, final String siteID, final String fileName, final SimpleCallBack callBack) {
        Call<ResponseBody> call = this.mRetrofitService.getStationReport(siteID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {
                    //保存文件
                    boolean flag=writeResponseBodyToDisk(response.body(),fileName);
                    if(flag) {
                        callBack.onSuccess();
                    }else{
                        callBack.onFailure(context.getString(R.string.str_http_request_fail));
                    }
                } else {
                    callBack.onFailure(context.getString(R.string.str_http_request_fail));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callBack.onFailure(context.getString(R.string.str_http_request_fail));
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
    }
}

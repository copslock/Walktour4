package com.walktour.service.app.datatrans.inns.net;

import android.content.Context;

import com.walktour.base.gui.model.SimpleCallBack;
import com.walktour.base.util.RetrofitUtil;
import com.walktour.gui.inns.dao.model.InnsFtpParams;
import com.walktour.gui.inns.dao.model.InnsVoLTEParams;
import com.walktour.service.app.datatrans.inns.net.model.InnsUploadResult;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by yi.lin on 2017/11/27.
 * <p>
 * 寅时地图http请求管理类
 */

public class InnsmapRetrofitManager {

    /**
     * 日志标识
     */
    private static final String TAG = "InnsmapRetrofitManager";

    /**
     * 实例对象
     */
    private static InnsmapRetrofitManager sInstance;

    /**
     * 服务端接口实例对象
     */
    private RetrofitService mRetrofitService;

    /**
     * http请求类
     */
    private RetrofitUtil mRetrofitUtil;


    public InnsmapRetrofitManager(String baseUrl) {
        this.mRetrofitUtil = RetrofitUtil.getInstance();
        String fixedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.mRetrofitService = this.mRetrofitUtil.createRetrofitService(fixedBaseUrl, RetrofitService.class);
    }


    /**
     * @param userId   UserId
     * @param zipFile  上传的压缩文件
     * @param callback 回调接口
     */
    public void uploadLogFile(final Context context, String userId, MultipartBody.Part zipFile, final SimpleCallBack callback) {
        Call<InnsUploadResult> call = this.mRetrofitService.uploadLogFile(userId, zipFile);
        call.enqueue(new Callback<InnsUploadResult>() {
            @Override
            public void onResponse(Call<InnsUploadResult> call, Response<InnsUploadResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InnsUploadResult result = response.body();
                    if (result != null && result.getCode() == 1) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("");
                    }
                } else {
                    callback.onFailure("onFailed!!");
                }
            }

            @Override
            public void onFailure(Call<InnsUploadResult> call, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
    }


    /**
     * @param innsFtpParams
     * @param innsVoLTEParams
     * @param callback
     */
    public void uploadMeasInfo(int testType,InnsFtpParams innsFtpParams, InnsVoLTEParams innsVoLTEParams, final SimpleCallBack callback) {
        String fileId = innsFtpParams.getLogFileUUID();
        //VoLTE日志属性
        double connRate = innsVoLTEParams.getConnRate();
        double dropRate = innsVoLTEParams.getDropRate();
        int callDelay = innsVoLTEParams.getCallDelay();
        double mos3Rate = innsVoLTEParams.getMos3Rate();
        double mos35Rate = innsVoLTEParams.getMos35Rate();
        double imsSuccessRate = innsVoLTEParams.getImsSuccessRate();
        double esrvccSuccessRate = innsVoLTEParams.getEsrvccSuccessRate();
        int esrvccDelay = innsVoLTEParams.getEsrvccDelay();
        double rtpLostRate = innsVoLTEParams.getRtpLostRate();
        double rtpShakeRate = innsVoLTEParams.getRtpShakeRate();
        //FTP日志属性
        double ftpDownAve = innsFtpParams.getFtpDownAve();
        double ftpUpAve = innsFtpParams.getFtpUpAve();
        double ftpDownMax = innsFtpParams.getFtpDownMax();
        double ftpDownMin = innsFtpParams.getFtpDownMin();
//        int testType = (ftpDownAve == 0 && ftpUpAve == 0 && ftpDownMax == 0 && ftpDownMin == 0) ? 3 : 4;
        //先上传volte测试数据
        Call<InnsUploadResult> call = this.mRetrofitService.uploadMeasInfo(testType, fileId,
                connRate, dropRate, callDelay, mos3Rate, mos35Rate, imsSuccessRate, esrvccSuccessRate, esrvccDelay, rtpLostRate, rtpShakeRate,
                ftpDownAve, ftpUpAve, ftpDownMax, ftpDownMin);
        call.enqueue(new Callback<InnsUploadResult>() {
            @Override
            public void onResponse(Call<InnsUploadResult> call, Response<InnsUploadResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InnsUploadResult result = response.body();
                    if (result != null && result.getCode() == 1) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure("");
                    }
                } else {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Call<InnsUploadResult> call, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
    }

    /**
     * 调用服务端接口
     */
    private interface RetrofitService {

        /**
         * 上传测试数据
         *
         * @param userId  Userid
         * @param zipFile log 文件流
         * @return code 1 请求成功,  -1 请求失败
         */
        @Multipart
        @POST("volte/api/uploadLogFile")
        Call<InnsUploadResult> uploadLogFile(@Query("userId") String userId, @Part MultipartBody.Part zipFile);

        /**
         * @param testType          测试类型 Volte类型为3，ftp类型为4
         * @param fileId            Log文件唯一标识
         * @param connRate          接通率
         * @param dropRate          掉话率
         * @param callDelay         呼叫建立时延
         * @param mos3Rate          MOS 3.0 以上占比
         * @param mos35Rate         MOS 3.5 以上占比
         * @param imsSuccessRate    IMS注册成功率（%）
         * @param esrvccSuccessRate eSRVCC成功率（%）
         * @param esrvccDelay       eSRVCC切换时延-用户面（ms）
         * @param rtpLostRate       RTP丢包率
         * @param rtpShakeRate      RTP抖动(ms)
         * @param ftpDownAve        FTP平均下载速率
         * @param ftpUpAve          FTP平均上传速率
         * @param ftpDownMax        最高下载速率
         * @param ftpDownMin        最低下载速率
         * @return
         */
        @FormUrlEncoded
        @POST("volte/api/uploadMeasInfo")
        Call<InnsUploadResult> uploadMeasInfo(@Field("testType") int testType,
                                              @Field("fileId") String fileId,
                                              //VoLTE日志属性
                                              @Field("connRate") double connRate,
                                              @Field("dropRate") double dropRate,
                                              @Field("callDelay") int callDelay,
                                              @Field("mos3Rate") double mos3Rate,
                                              @Field("mos35Rate") double mos35Rate,
                                              @Field("imsOkRate") double imsSuccessRate,
                                              @Field("esrvccOkRate") double esrvccSuccessRate,
                                              @Field("esrvccDelay") int esrvccDelay,
                                              @Field("rtpLostRate") double rtpLostRate,
                                              @Field("rtpShakeRate") double rtpShakeRate,
                                              //FTP日志属性
                                              @Field("ftpDownAve") double ftpDownAve,
                                              @Field("ftpUpAve") double ftpUpAve,
                                              @Field("ftpDownMax") double ftpDownMax,
                                              @Field("ftpDownMin") double ftpDownMin
        );
    }
}

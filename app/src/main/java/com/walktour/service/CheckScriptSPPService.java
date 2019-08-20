package com.walktour.service;

import android.content.Context;

import com.walktour.Utils.FileUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.RetrofitUtil;
import com.walktour.model.NewScriptSPPModel;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by yi.lin on 2017/9/13.
 * script.cpp文件自动更新功能服务
 */
public class CheckScriptSPPService {

    private static final String TAG = "CheckScriptSPPService";


    private RetrofitService mRetrofitService;

    /**
     * 保存script.spp时间戳到SharedPreference里面的key
     */
    private static final String KEY_SCRIPT_FILE_TIME = "script_file_time";
    /**
     * script.spp文件名
     */
    private static final String SCRIPT_FILE_NAME = "script.spp";

    /**
     * 检测是否有新的script.spp文件
     */
    public void checkHasNewScriptSPP(final Context context) {
        LogUtil.d(TAG, "----checkHasNewScriptSPP----");
        String url = "http://update.flvurl.cn/siteparser/rule/custom/dingli/";
        mRetrofitService = RetrofitUtil.getInstance().createRetrofitService(url, RetrofitService.class);
        mRetrofitService.checkHasNewFile().enqueue(new Callback<NewScriptSPPModel>() {
            @Override
            public void onResponse(Call<NewScriptSPPModel> call, Response<NewScriptSPPModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewScriptSPPModel model = response.body();
                    SharePreferencesUtil spUtil = SharePreferencesUtil.getInstance(context);
                    long oldFileTime = spUtil.getLong(KEY_SCRIPT_FILE_TIME, -1L);
                    if (oldFileTime == -1 || oldFileTime < model.getTime()) {
                        //保存获取到的文件时间戳
                        spUtil.saveLong(KEY_SCRIPT_FILE_TIME, model.getTime());
                        //下载并覆盖"/data/data/com.walktour.gui/files/script.spp"文件
                        final File file = context.getFileStreamPath(SCRIPT_FILE_NAME);
                        final File fileBack = AppFilePathUtil.getInstance().getAppLibFile(SCRIPT_FILE_NAME);
                        mRetrofitService.downloadFile(model.getUrl()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    try {
                                        FileUtil.writeToFile(file, response.body().bytes());
                                        UtilsMethod.writeRawResource(context,file,fileBack,false); //需要特殊处理才能写入
                                    } catch (Exception e) {
                                        LogUtil.e(TAG, e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                                //no-op
                                LogUtil.e(TAG, throwable.getMessage());
                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<NewScriptSPPModel> call, Throwable throwable) {
                LogUtil.e(TAG, throwable.getMessage());
            }
        });
    }


    private interface RetrofitService {
        /**
         * 检查是否有新的script.spp文件
         *
         * @return
         */
        @GET("update.json")
        Call<NewScriptSPPModel> checkHasNewFile();

        /**
         * 下载script.spp文件
         *
         * @param scriptUrl script.spp下载地址
         * @return
         */
        @GET
        Call<ResponseBody> downloadFile(@Url String scriptUrl);
    }

}

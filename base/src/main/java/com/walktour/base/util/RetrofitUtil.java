package com.walktour.base.util;

import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * http请求工具类
 * Created by wangk on 2017/8/8.
 */

public class RetrofitUtil {
    /**
     * 日志标识
     */
    private static final String TAG = "RetrofitUtil";
    /**
     * 唯一实例
     */
    private static RetrofitUtil sInstance = null;
    /**
     * json转换成对象工厂类
     */
    private GsonConverterFactory mGsonConverterFactory = GsonConverterFactory.create(new GsonBuilder().create());
    /**
     * Retrofit服务类映射 < 基础URL，对象>
     */
    private Map<String, Object> mRetrofitServiceMap = new HashMap<>();
    /**
     * 登录的基础URL集合
     */
    private Set<String> mLoginUrlMap = new HashSet<>();
    /**
     * http请求类
     */
    private OkHttpClient mOkHttpClient;

    /**
     * 是否处于调试模式
     */
    private static final boolean DEBUG = false;

    private RetrofitUtil() {
        this.mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        LogUtil.d(TAG, message);
                    }
                }).setLevel(DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .build();
    }

    /**
     * 返回唯一实例
     *
     * @return 唯一实例
     */
    public static RetrofitUtil getInstance() {
        if (sInstance == null)
            sInstance = new RetrofitUtil();
        return sInstance;
    }

    /**
     * 生成Retrofit服务类对象
     *
     * @param baseUrl      基础请求URL
     * @param serviceClass 服务类
     * @return etrofit服务类对象
     */
    public <T> T createRetrofitService(String baseUrl, Class<T> serviceClass) {
        LogUtil.d(TAG, "----createRetrofitService----" + baseUrl);
        if (StringUtil.isEmpty(baseUrl))
            return null;
        if (this.mRetrofitServiceMap.containsKey(baseUrl))
            return (T) this.mRetrofitServiceMap.get(baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addConverterFactory(this.mGsonConverterFactory)
                .build();
        T service = retrofit.create(serviceClass);
        this.mRetrofitServiceMap.put(baseUrl, service);
        return service;
    }

    /**
     * 判断当前网址是否已登录
     *
     * @param baseUrl 基础请求URL
     * @return 是否已登录
     */
    public boolean isLogin(String baseUrl) {
        if (StringUtil.isEmpty(baseUrl))
            return false;
        return this.mLoginUrlMap.contains(baseUrl);
    }

    /**
     * 判断当前网址是否已登录
     *
     * @param baseUrl 基础请求URL
     */
    public void setLogin(String baseUrl) {
        if (StringUtil.isEmpty(baseUrl))
            return;
        this.mLoginUrlMap.add(baseUrl);
    }

}

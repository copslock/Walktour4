package com.walktour.control.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/***
 * 调试模式设置
 *
 * @author weirong.fan
 *
 */
public class ConfigDebugModel {

    // 打开调试模式
    private final String KEY_OPEN_DEBUGMODEL = "open_debugmodel";
    // 是否压数据集
    private final String KEY_PUSH_DATASET = "debug_push_dataset";
    // 仅分帧
    private final String KEY_DIVIDE_FRAME = "debug_divide_frame";
    // 仅解码
    private final String KEY_DECODER = "debug_decoder";
    // 解码并读取解码结果
    private final String KEY_DECODER_AND_READ = "debug_decoder_and_read";
    // 事件判断
    private final String KEY_EVENT_JUDGE = "debug_event_judge";
    // 调试正常模式
    private final String KEY_DEBUG_NORMAL = "debug_normal";
    // 查询事件
    private final String KEY_QUERY_EVENT = "debug_query_event";
    // 查询信令
    private final String KEY_QUERY_SIGNAL = "debug_query_signal";
    // 查询参数
    private final String KEY_QUERY_PARAM = "debug_query_param";
    // 详细解码
    private final String KEY_DETAIL_DECODER = "debug_detail_decoder";

    private static ConfigDebugModel sInstance = null;
    /**
     * 是否打开调试模式
     */
    private boolean isDebugModel;
    /**
     * 是否解码并读取解码结果
     */
    private boolean isDecoderAndRead;
    /**
     * 是否调试正常模式
     */
    private boolean isDebugNormal;
    /**
     * 是否详细解码
     */
    private boolean isDetailDecoder;
    /**
     * 是否事件判断
     */
    private boolean isEventJudge;
    /**
     * 是否仅解码
     */
    private boolean isOnlyDecoder;
    /**
     * 是否仅分帧
     */
    private boolean isOnlyFrame;
    /**
     * 是否压数据集
     */
    private boolean isPushDataSet;
    /**
     * 是否查询事件
     */
    private boolean isQueryEvent;
    /**
     * 是否查询参数
     */
    private boolean isQueryParam;
    /**
     * 是否查询信令
     */
    private boolean isQuerySignal;

    private ConfigDebugModel(Context context) {
        init(context);
    }

    public synchronized static ConfigDebugModel getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ConfigDebugModel(context);
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    private void init(Context context) {
        this.isDebugModel = this.getSharedPreferences(context, KEY_OPEN_DEBUGMODEL, false);
        this.isDebugNormal = this.getSharedPreferences(context, KEY_DEBUG_NORMAL, true);
        this.isQueryEvent = this.getSharedPreferences(context, KEY_QUERY_EVENT, true);
        this.isDecoderAndRead = this.getSharedPreferences(context, KEY_DECODER_AND_READ, false);
        this.isDetailDecoder = this.getSharedPreferences(context, KEY_DETAIL_DECODER, true);
        this.isEventJudge = this.getSharedPreferences(context, KEY_EVENT_JUDGE, false);
        this.isOnlyDecoder = this.getSharedPreferences(context, KEY_DECODER, false);
        this.isOnlyFrame = this.getSharedPreferences(context, KEY_DIVIDE_FRAME, false);
        this.isPushDataSet = this.getSharedPreferences(context, KEY_PUSH_DATASET, false);
        this.isQueryParam = this.getSharedPreferences(context, KEY_QUERY_PARAM, true);
        this.isQuerySignal = this.getSharedPreferences(context, KEY_QUERY_SIGNAL, true);
    }

    /**
     * 获得属性值
     *
     * @param context      上下文
     * @param key          关键字
     * @param defaultValue 默认值
     * @return 属性值
     */
    private boolean getSharedPreferences(Context context, String key, boolean defaultValue) {
        SharedPreferences share = context.getSharedPreferences("debug_model_config_preferences",
                Context.MODE_PRIVATE);
        return share.getBoolean(key, defaultValue);
    }

    /**
     * 设置属性值
     *
     * @param context 上下文
     * @param key     关键字
     * @param value   默认值
     */
    private void setSharedPreferences(Context context, String key, boolean value) {
        SharedPreferences share = context.getSharedPreferences("debug_model_config_preferences",
                Context.MODE_PRIVATE);
        Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 是否打开调试模式
     */
    public boolean isDebugModel() {
        return this.isDebugModel;
    }

    /**
     * 设置是否打开调试模式
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setDebugModel(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_OPEN_DEBUGMODEL, isSet);
        this.isDebugModel = isSet;
    }

    /**
     * 是否压数据集
     */
    public boolean isPushDataSet() {
        if (this.isDebugModel) {
            return this.isPushDataSet;
        }
        return true;
    }

    /**
     * 设置是否压数据集
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setPushDataSet(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_PUSH_DATASET, isSet);
        this.isPushDataSet = isSet;
    }

    /**
     * 设置是否只做分帧
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setOnlyFrame(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_DIVIDE_FRAME, isSet);
        this.isOnlyFrame = isSet;
    }

    /**
     * 只做分帧处理
     */
    public boolean isOnlyFrame() {
        if (this.isDebugModel) {
            return this.isOnlyFrame;
        }
        return true;
    }

    /**
     * 设置是否只做解码
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setOnlyDecoder(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_DECODER, isSet);
        this.isOnlyDecoder = isSet;
    }

    /**
     * 是否只做解码
     */
    public boolean isOnlyDecoder() {
        if (this.isDebugModel) {
            return this.isOnlyDecoder;
        }
        return true;
    }

    /**
     * 设置是否解码并且读取解码结果
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setDecoderAndRead(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_DECODER_AND_READ, isSet);
        this.isDecoderAndRead = isSet;
    }

    /**
     * 是否解码并且读取解码结果
     */
    public boolean isDecoderAndRead() {
        if (this.isDebugModel) {
            return this.isDecoderAndRead;
        }
        return true;
    }

    /**
     * 设置是否事件判断
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setEventJudge(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_EVENT_JUDGE, isSet);
        this.isEventJudge = isSet;
    }

    /**
     * 是否事件判断
     */
    public boolean isEventJudge() {
        if (this.isDebugModel) {
            return this.isEventJudge;
        }
        return true;
    }

    /**
     * 设置调试正常模式
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setDebugNormal(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_DEBUG_NORMAL, isSet);
        this.isDebugNormal = isSet;
    }

    /**
     * 是否调试正常模式
     */
    public boolean isDebugNormal() {
        if (this.isDebugModel) {
            return this.isDebugNormal;
        }
        return true;
    }

    /**
     * 设置是否查询事件
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setQueryEvent(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_QUERY_EVENT, isSet);
        this.isQueryEvent = isSet;
    }

    /**
     * 是否事件判断
     */
    public boolean isQueryEvent() {
        if (this.isDebugModel) {
            return this.isQueryEvent;
        }
        return true;
    }

    /**
     * 设置是否查询信令
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setQuerySignal(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_QUERY_SIGNAL, isSet);
        this.isQuerySignal = isSet;
    }

    /**
     * 是否查询信令
     */
    public boolean isQuerySignal() {
        if (this.isDebugModel) {
            return this.isQuerySignal;
        }
        return true;
    }

    /**
     * 设置是否查询参数
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setQueryParam(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_QUERY_PARAM, isSet);
        this.isQueryParam = isSet;
    }

    /**
     * 是否查询参数
     */
    public boolean isQueryParam() {
        if (this.isDebugModel) {
            return this.isQueryParam;
        }
        return true;
    }

    /**
     * 设置是否查询详细解码
     *
     * @param context 上下文
     * @param isSet   是否设置
     */
    public void setDetailDecoder(Context context, boolean isSet) {
        this.setSharedPreferences(context, KEY_DETAIL_DECODER, isSet);
        this.isDetailDecoder = isSet;
    }

    /**
     * 是否查询详细解码
     */
    public boolean isDetailDecoder() {
        if (this.isDebugModel) {
            return this.isDetailDecoder;
        }
        return true;
    }
}

package com.walktour.Utils.audios;

import android.content.Context;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;

/**
 * Created by Yi.Lin on 2018/2/27.
 * 录放音播放器工厂类
 */

public class AudioManagerFactory {

    private static final String TAG = "AudioManagerFactory";

    /**
     * 播放器类型
     */
    public enum AudioManagerType {
        VIVO, XIAOMI, SAMSUNG
    }


    /**
     * 根据不同类型获取对应播放器
     *
     * @param context 上下文
     * @param type    播放器类型
     * @return IAudioManager具体实现l
     */
    public IAudioManager getAudioManager(Context context, AudioManagerType type) {
        LogUtil.d(TAG, "type = " + ((type == null)  ? "null" : type));
        if (null == type) return null;
        switch (type) {
            case VIVO:
                return new VivoAudioManager(context);
            case SAMSUNG:
                return new SamsungAudioManager(context);
            case XIAOMI:
                return new XiaomiAudioManager(context);
            default:
                return null;
        }
    }

    public IAudioManager getAudioManager(Context context) {
        AudioManagerFactory.AudioManagerType type = null;
        if (Deviceinfo.getInstance().isSamsungCustomRom()) {
            type = AudioManagerFactory.AudioManagerType.SAMSUNG;
        } else if (Deviceinfo.getInstance().isVivo()) {
            type = AudioManagerFactory.AudioManagerType.VIVO;
        } else if (Deviceinfo.getInstance().isXiaomi()) {
            type = AudioManagerFactory.AudioManagerType.XIAOMI;
        }
        return getAudioManager(context, type);
    }



}

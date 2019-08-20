package com.walktour.Utils.audios;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yi.Lin on 2018/2/9.
 * 三星MOC录音、播音管理器
 */

public class SamsungAudioManager implements IAudioManager {

    private static final String TAG = "SamsungAudioManager";
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private AudioManager mAudioManager;
    private static SimpleDateFormat mDF = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    public SamsungAudioManager(Context context) {
        mContext = context;
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
    }

    /**
     * 开始录音
     *
     * @param filePath
     * @param fileName
     * @param sampleRate
     */
    @Override
    public void startRecording(String filePath, String fileName, int sampleRate) {
        LogUtil.d(TAG,"----startRecording filePath:" + filePath + " ,fileName:" + fileName + " ,sampleRate:" + sampleRate + "----");
        //开始录音
        Intent i = new Intent("com.android.CUSTOMER_REQUEST_ACTION");
        i.putExtra("customer_action", "record"); //录音动作
        i.putExtra("action_enable", true); //true 代表开启；false 关闭
        i.putExtra("record_path", filePath); //保存路径 path: String 类型,如：/sdcard/...
        i.putExtra("record_name", fileName); //录音文件命名 name: String 类型
        i.putExtra("record_samplingRate", sampleRate); //采样率 samplingRate: int 类型
        mContext.sendBroadcast(i);
    }

    /**
     * 结束录音
     */
    @Override
    public void stopRecording() {
        LogUtil.d(TAG,"----stopRecording----");
        Intent i = new Intent("com.android.CUSTOMER_REQUEST_ACTION");
        i.putExtra("customer_action", "record");
        i.putExtra("action_enable", false);
        mContext.sendBroadcast(i);
    }


    /**
     * 开始播放音频文件
     */
    @Override
    public void startPlaying(int rawId) {
        LogUtil.d(TAG, "----startPlaying path:" + rawId + "----");
        setParameters(true);
        //Samsung的不需要设置mute
//        mAudioManager.setMicrophoneMute(true);
        AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawId);
        try {
            mediaPlayer = new MediaPlayer();
            // 设置指定的流媒体地址
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            // 设置音频流的类型
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕 开始播放流媒体
                    mediaPlayer.start();
                }
            });
            // 设置循环播放
            // mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    stopPlaying();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 如果发生错误
                    stopPlaying();
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    @Override
    public void stopPlaying() {
        LogUtil.d(TAG, "stopPlaying");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            setParameters(false);
        }
    }



    @Override
    public String getRecordFilePath(String rcuFileName) {
        String filePath = AppFilePathUtil.getInstance().createSDCardBaseDirectory(mContext.getString(R.string.path_voice),
                UtilsMethod.getSimpleDateFormat6(System.currentTimeMillis()), (rcuFileName.equals("") ? "" : rcuFileName));
        LogUtil.d(TAG,"---getXiaomiRecordFilePath:" + filePath + "----");
        return filePath;
    }

    @Override
    public String getRecordFileName(TaskInitiativeCallModel callModel) {
        String fileNameExtends = callModel.getTestConfig().getMosAlgorithm() + "_Default";
        if (callModel.getTestConfig().getMosAlgorithm().equals(MOCTestConfig.MOSAlgorithm_POLQA)) {
            String sampleType = callModel.getTestConfig().getSampleType();
            fileNameExtends = fileNameExtends + "_"
                    + (sampleType.indexOf(" ") == -1 ? sampleType : sampleType.substring(sampleType.indexOf(" ") + 1)) + "_"
                    + callModel.getTestConfig().getCalcMode();
        }
        String fileName = MyPhoneState.getInstance().getMyDeviceId(mContext) + "_" + mDF.format(new Date())
                + (fileNameExtends.equals("") ? "" : "_" + fileNameExtends);
        LogUtil.d(TAG,"---getSamsungRecordFileName:" + fileName + "----");
        return fileName;
    }

    /**
     * 设置通话过程中播放指定音频
     *
     * @param value
     */
    private void setParameters(boolean value) {
        LogUtil.d(TAG, "setParameters: " + value);
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (value) {
            am.setParameters("inCall_music=on");
        } else {
            am.setParameters("inCall_music=off");
        }
    }

}

package com.walktour.Utils.audios;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;

import com.vivo.api.netcustom.interfaces.DeviceAssistant;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yi.Lin on 2017/12/27.
 * vivo MOC录音、播音管理器
 */

public class VivoAudioManager implements IAudioManager {

    private static final String TAG = "VivoAudioManager";

    private MediaPlayer mediaPlayer;
    private AudioManager mAudioManager;
    private DeviceAssistant mDeviceAssistant;
    private Context mContext;
    private static SimpleDateFormat mDF = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());


    public VivoAudioManager(Context context) {
        this.mContext = context;
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        if (mDeviceAssistant == null) {
            mDeviceAssistant = new DeviceAssistant();
        }
    }


    public void startRecording(String filePath, String fileName, int sampleRate) {
        mAudioManager.setMicrophoneMute(true);
        LogUtil.d(TAG, "startRecording filePath: " + filePath + " , fileName:" + fileName + " , sampleRate:" + sampleRate);
        boolean rst = mDeviceAssistant.startCallRecord(filePath, fileName, 8000, sampleRate);
        LogUtil.d(TAG, "startCallRecord: " + rst);
    }

    public void stopRecording() {
        LogUtil.d(TAG, "----stopRecording----");
        mDeviceAssistant.stopCallRecord();
    }

    @Override
    public String getRecordFilePath(String rcuFileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = "/Walktour/voice/"+ UtilsMethod.getSimpleDateFormat6(System.currentTimeMillis())+"/"+(rcuFileName.equals("") ? "" : rcuFileName);
            LogUtil.d(TAG,"---getVivoRecordFilePath:" + filePath + "----");
            return filePath;
        }
        return "";
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
        LogUtil.d(TAG,"---getVivoRecordFileName:" + fileName + "----");
        return fileName;
    }

    /**
     * 开始播放音频文件
     */
    public void startPlaying(int rawId) {
        LogUtil.d(TAG, "----startPlaying path:" + rawId + "----");
        setParameters(true);
        mAudioManager.setMicrophoneMute(true);
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
    public void stopPlaying() {
        LogUtil.d(TAG, "stopPlaying");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            setParameters(false);
        }
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
            am.setParameters("incall_music_enabled=true");
        } else {
            am.setParameters("incall_music_enabled=false");
        }
    }
}

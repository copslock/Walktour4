package com.walktour.Utils.audios;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.voix.RVoixSrv;
import com.voix.VoiceRecorder;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Yi.Lin on 2018/2/7.
 * 小米 MOC录音、播音管理器
 */

public class XiaomiAudioManager implements IAudioManager {

    private static final String TAG = "XiaomiAudioManager";

    /**
     * 小米使用该类开始录音、结束录音方案
     */
    private VoiceRecorder mVoiceRecorder;
    private Context mContext;
    private AudioManager mAudioManager;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mMediaRecorder;
    private static SimpleDateFormat mDF = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    public XiaomiAudioManager(Context context) {
        this.mContext = context;
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
    }


    public void startRecording(String filePath, String fileName, int sampleRate) {
        String fPath = filePath + fileName + ".wav";
        LogUtil.d(TAG, "startXiaomiRecording file path:" + fPath + " , sampleRate:" + sampleRate);
        mVoiceRecorder = new VoiceRecorder();
        mVoiceRecorder.init(new File(fPath), RVoixSrv.MODE_RECORD_WAV, sampleRate, MediaRecorder.AudioSource.VOICE_DOWNLINK, 1,
                16, true);
        mVoiceRecorder.startRecord();
        /*File file = new File(fPath);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
        //保存文件为MP4格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        //设置采样率
        mMediaRecorder.setAudioSamplingRate(sampleRate);
        //通用的AAC编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        //设置音质频率
//        mMediaRecorder.setAudioEncodingBitRate(96000);
        //设置文件录音的位置
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        //开始录音
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage());
        }*/
    }

    public void stopRecording() {
        LogUtil.d(TAG, "----stopXiaomiRecording----");
        mVoiceRecorder.stopRecord();
       /*
        try {
            mMediaRecorder.stop();
            releaseRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
        LogUtil.d(TAG,"---getXiaomiRecordFileName:" + fileName + "----");
        return fileName;
    }

    private void releaseRecorder() {
        LogUtil.e(TAG, "----releaseRecorder----");
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 开始播放音频文件
     */
    public void startPlaying(int rawId) {
        LogUtil.w(TAG, "----startPlaying path:" + rawId + "----");
        setParameters(true);
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
    public void stopPlaying() {
        LogUtil.w(TAG, "stopPlaying");
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
        LogUtil.d(TAG, "setParameters " + value);
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (value) {
            am.setParameters("incall_music_enabled=true");
        } else {
            am.setParameters("incall_music_enabled=false");
        }
    }

}

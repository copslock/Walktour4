package com.walktour.gui.mos;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.MOCTestConfig;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.command.BaseCommand;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

import de.opticom.polqa.PolqaWrapper;
import de.opticom.utils.PolqaCalculator;
import de.opticom.utils.PolqaJob;
import de.opticom.utils.PolqaResult;

/**
 * Polqa 算分器
 *
 * @author zhicheng.chen
 * @date 2019/3/20
 */
public class MosCaculator {

    private static final String TAG = "czc";
    /**
     * POLQA MOS测试的参照文件
     */
    public static final String MOS_POLQA_NB_8K = "polaqvoice_nb_8k.wav";
    public static final String MOS_POLQA_WB_16K = "polaqvoice_wb_16k.wav";
    public static final String MOS_POLQA_SWB_48K = "polaqvoice_swb_48k.wav";
    /**
     * 保存MOS分值低的目录
     */
    public static final String MOS_LOW_DIR = "/Voice-Low/";
    /**
     * POLQA LICENSE 文件名
     */
    public static final String POLQA_LICENSE_FILE = "PolqaLicenseFile.txt";

    private Context context;
    private String licenseFilePath;


    public MosCaculator(Context context, String licenseFilePath) {
        this.context = context;
        this.licenseFilePath = licenseFilePath;
    }

    /**
     * 该方法是通过播放一份样本语音，mos录音，然后对比样本跟录音文件，得出分数
     *
     * @param voiceType
     * @param testFilePath
     * @return
     */
    public PolqaResult calculateBySelfFile(BaseCommand.FileType voiceType, String testFilePath) {
        //        final String referFilePath = initVoiceSimple(voiceType);

        PolqaCalculator polqaCalculator = new PolqaCalculator(licenseFilePath, context.getSystemService(Context.TELEPHONY_SERVICE));
        PolqaJob polqaJob = new PolqaJob();
        //自己跟自己对比
        polqaJob.input.referenceFilename = testFilePath;
        polqaJob.input.testFilename = testFilePath;
        // 是否SWB算分,false为NB算分
        polqaJob.input.superwideband = true;
        polqaJob.input.sampleRate = voiceType.getSampleRate() * 1000;
        polqaJob.input.disableLevelAlignment = false;
        polqaJob.input.disableSrConversion = true;
        polqaJob.input.ituVersion = PolqaWrapper.POLQA_V1_1;
        polqaJob.result.reset();

        polqaCalculator.Calc(polqaJob);

        final PolqaResult result = polqaJob.result;
        return result;
    }


    /**
     * 初始化音量管理
     */
    public static void setVoice(Context context) {
        setVoice(context, AudioManager.MODE_NORMAL);
    }

    /**
     * 初始化音量管理
     */
    public static void setVoice(Context context, int mode) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        // 调节媒体播放器的音量至5(实验证明此时音质最好)
        audioManager.setMode(mode);
        int mediaMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int voiceMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int mediaVolume = Deviceinfo.getInstance().getMicroMosMediaVolume();
        int voiceVolume = Deviceinfo.getInstance().getMicroMosVoiceVolume();
        //        Log.i(TAG, "mediaVolume=" + mediaVolume + ",voiceVolume=" + voiceVolume);
        if (mediaMaxVolume < mediaVolume)
            mediaVolume = mediaMaxVolume;
        if (voiceMaxVolume < voiceVolume)
            voiceVolume = voiceMaxVolume;
        //        Log.i(TAG, "mediaMaxVolume=" + mediaMaxVolume + ",voiceMaxVolume=" + voiceMaxVolume + ",mediaVolume=" + mediaVolume + ",voiceVolume=" + voiceVolume);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(false);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voiceVolume, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
    }

    public static void setMicrophoneMute(boolean isMute) {
        Context appContext = WalktourApplication.getAppContext();
        AudioManager audioManager = (AudioManager) appContext.getSystemService(Service.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(isMute);
    }


    /**
     * @param fileType              样本文件
     * @param testFilePath          录音文件路径
     * @param isSwb                 是否swb
     * @param isRealTimeCalculation 是否实时算分
     * @return
     */
    public static double caculate(BaseCommand.FileType fileType, String testFilePath, boolean isSwb, boolean isRealTimeCalculation) {
        if (testFilePath == null || testFilePath.equals("") || testFilePath.equals("error")) {
            EventBus.getDefault().post(String.format(Locale.getDefault(), "POLQA:%.2f", 0.0f));
            return 0.00;
        }
        Context appContext = WalktourApplication.getAppContext();
        String referFilePath = initVoiceSimple(fileType);
        File licenseFile = AppFilePathUtil.getInstance().getAppFilesFile(POLQA_LICENSE_FILE);
        if (!licenseFile.exists()) {
            licenseFile = AppFilePathUtil.getInstance().getSDCardBaseFile(POLQA_LICENSE_FILE);
        }
        PolqaJob polqaJob = null;
        if (isRealTimeCalculation) {
            PolqaCalculator polqaCalculator = new PolqaCalculator(licenseFile.getAbsolutePath(), appContext.getSystemService(Context.TELEPHONY_SERVICE));
            polqaJob = new PolqaJob();
            polqaJob.input.referenceFilename = referFilePath;
            polqaJob.input.testFilename = testFilePath;
            polqaJob.input.superwideband = isSwb; // 是否SWB算分
            polqaJob.input.sampleRate = fileType.getSampleRate() * 1000;
            polqaJob.input.disableLevelAlignment = false;
            polqaJob.input.disableSrConversion = true;
            polqaJob.input.ituVersion = PolqaWrapper.POLQA_V1_1;
            polqaJob.result.reset();
            polqaCalculator.Calc(polqaJob);
        }

        PolqaResult result = polqaJob == null ? new PolqaResult() : polqaJob.result;
        if (result.result == PolqaWrapper.POLQA_LAST_ERROR){
            Log.w(TAG, String.format("POLQA Error: %s", PolqaWrapper.getErrorString(result.result)));
            return PolqaWrapper.POLQA_LAST_ERROR;
        }
        if (result.result == PolqaWrapper.POLQA_OK || !isRealTimeCalculation) {
            double mosResult = result.mfMOSLQO;
            Log.w(TAG, String.format(Locale.getDefault(), "POLQA:%.2f", mosResult));
            EventBus.getDefault().post(String.format(Locale.getDefault(), "POLQA:%.2f", mosResult));
            EventBytes.Builder(appContext, RcuEventCommand.POLQA_RESULT)
                    .addSingle((float) 1.22)// Version
                    .addSingle((float) 2.4)// P863Version 等效的ITU参考代码版本
                    .addInteger(isSwb ? 3 : 2) // 算分处理模式(NB/SWB)//参考2013.7.2 NB为2,SWB为3
                    .addInteger(fileType.getSampleRate() * 1000)// SampleRate
                    .addInteger((int) (mosResult * 1000)) // POLQA_Score 实际分数乘以1000以后的整数值，如果分值为零，则需要采用事后算分；
                    .addSingle((float) result.mfMinDelay) // MinDelay 最小时延(ms)
                    .addSingle((float) result.mfMaxDelay) // MaxDelay 最大时延(ms)
                    .addSingle((float) result.mfAvgDelay) // MeanDelay 平均时延(ms)
                    .addSingle(0.0f) // EModelRValue G.107 等级 (只适用NB 模式)
                    .addSingle(result.nrSamplesRef) // PitchReference参考信号的音调频率(Hz)
                    .addSingle(result.nrSamplesRef) // PitchDegraded
                    .addSingle(0.0f) // EstimatedSampleRate Estimated sample rate of
                    .addInteger(1) // ResamplingApplied if internal resampling was
                    .addSingle((float) result.mfLevelReference) // LevelReference
                    .addSingle((float) result.mfLevelDegraded) // LevelDegraded Level
                    .addSingle(0.0f) // P56ActiveSpeechLevelRefdBLevel of the active
                    .addSingle(0.0f) // P56ActiveSpeechLevelDegdB Level of the active
                    .addSingle(0.0f) // P56PauseLevelRefdB Level of the pause parts of
                    .addSingle(0.0f) // P56PauseLevelDegdB Level of the pause parts of
                    .addSingle((float) result.mfAttenuation) // Attenuation in dB
                    .addSingle((float) result.mfSnrReference) // SnrReference SNR of the reference signal in dB
                    .addSingle((float) result.mfSnrDegraded) // SnrDegraded SNR of the degraded signal in dB
                    .addSingle((float) result.mfActiveSpeechRatioRef) //ActiveSpeechRatioRef Active speech ratio of the deference signal
                    .addSingle((float) result.mfActiveSpeechRatioDeg) //ActiveSpeechRatioDeg Active speech ratio of the deference signal
                    .addChar('D') // Direction 上行: 'U'下行:’D’
                    .addStringBuffer(testFilePath.substring(testFilePath.lastIndexOf("/") + 1))
                    .addSingle(0f) // Jitter 抖动，单位：毫秒
                    .addStringBuffer(referFilePath) // SourceFileName样本文件名（带路径）
                    .writeToRcu(System.currentTimeMillis() * 1000);
            return mosResult;
        } else {
            Log.w(TAG, String.format("POLQA Error: %s", PolqaWrapper.getErrorString(result.result)));
            return 0.00;
        }
    }

    private static String initVoiceSimple(BaseCommand.FileType fileType) {
        String voicePath = WalktourApplication.getAppContext().getFilesDir() + File.separator;
        if (fileType == BaseCommand.FileType.polqa_16k) {
            voicePath = voicePath + MOS_POLQA_WB_16K;
            initVoiceFile(voicePath, R.raw.sample_wb_16k);
        } else if (fileType == BaseCommand.FileType.polqa_48k) {
            voicePath = voicePath + MOS_POLQA_SWB_48K;
            initVoiceFile(voicePath, R.raw.sample_swb_48k);
        } else if (fileType == BaseCommand.FileType.polqa_8k) {
            voicePath = voicePath + MOS_POLQA_NB_8K;
            initVoiceFile(voicePath, R.raw.sample_nb_8k);
        }

        return voicePath;
    }

    private static void initVoiceFile(String filePath, int rawId) {
        File refFile = new File(filePath);
        if (!refFile.exists()) {
            UtilsMethod.writeRawResource(WalktourApplication.getAppContext(), rawId, refFile);
        }
    }

    public static double pesqCalculate(IBluetoothMOSServiceBinder binder, String filePath) {
        double mosResult = 0.00;
        try {
            double[] pesqResult;
            pesqResult = binder.getCalculatePESQ(R.raw.pesqvoice, filePath);
            // 下面3个值都已经放大1000倍
            double pesqScore = pesqResult[0];
            double pesqLq = pesqResult[1];
            double pesq = pesqResult[2];
            mosResult = pesq / 1000f;

            Log.i(TAG, String.format(Locale.getDefault(), "PESQ:%.2f", mosResult));

            Context appContext = WalktourApplication.getAppContext();
            EventBytes.Builder(appContext, RcuEventCommand.PESQ_score)
                    .addInteger((int) pesqScore)
                    .addInteger((int) pesqLq)
                    .addCharArray(filePath.substring(filePath.lastIndexOf("/") + 1).toCharArray(), 128)
                    .addCharArray("D".toCharArray(), 1)
                    .writeToRcu(System.currentTimeMillis() * 1000);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return mosResult;
    }

    /**
     * 复制低分的文件
     *
     * @param filePath  文件路径
     * @param mosResult 分数
     */
    public static void copyLowValueFile(String filePath, double mosResult) {
        Log.d(TAG, "copyLowValueFile:" + filePath + ",mosResult:" + mosResult);
        String lowDir = AppFilePathUtil.getInstance().createSDCardBaseDirectory(MOS_LOW_DIR,
                UtilsMethod.getSimpleDateFormat6(System.currentTimeMillis()), String.valueOf((int) mosResult));
        File fileSource = new File(filePath);
        DecimalFormat format = new DecimalFormat("0.0000");
        String file = lowDir + File.separator + fileSource.getName();
        file = file.substring(0, file.length() - 4);
        file += "_" + format.format(mosResult) + ".wav";
        File fileDes = new File(file);
        Log.d(TAG, "fileDes:" + fileDes);
        if (fileSource.exists() && fileSource.isFile()) {
            try {
                UtilsMethod.copyFile(fileSource, fileDes);
                fileSource.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

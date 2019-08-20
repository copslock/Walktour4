package com.walktour.gui.mos;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dingli.ott.MultipleAppTestMain;
import com.dingli.ott.event.MultipleEnvConfig;
import com.dingli.ott.task.service.WalktourAutoService;
import com.dingli.ott.util.NodeUtil;
import com.dingli.ott.util.OttUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.audios.AudioManagerFactory;
import com.walktour.Utils.audios.IAudioManager;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.Main;
import com.walktour.gui.R;
import com.walktour.gui.setting.bluetoothmos.BluetoothMosTabActivity;
import com.walktour.service.ApplicationInitService;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.BluetoothMTCService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.command.BaseCommand;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;
import com.walktour.service.test.OttLibsManager;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.opticom.polqa.PolqaWrapper;
import de.opticom.utils.PolqaCalculator;
import de.opticom.utils.PolqaJob;
import de.opticom.utils.PolqaResult;

public class OttDebugActivity extends BasicActivity {
//
//    static {
//        //        System.loadLibrary("crystax_shared");
//        //        System.loadLibrary("gnustl_shared");
//        //        System.loadLibrary("miniSDL");
//        //        System.loadLibrary("ipc2");
//        //        System.loadLibrary("ipc2tooljni");
//        //        System.loadLibrary("ipc2tool");
//        //        System.loadLibrary("mysock");
//        //        System.loadLibrary("iconv");
//        //        System.loadLibrary("myglib");
//        //        System.loadLibrary("mypcap");
//        //        System.loadLibrary("CustomWireshark");
//        //        System.loadLibrary("MultipleAppAnalysisModule");
//        OttLibsManager.loadLib();
//    }
//
//    public static final String TAG = "czc";
//    @BindView(R.id.title_txt)
//    TextView mTitle;
//    private MultipleAppTestMain appTestMain;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_ott_debug);
//        ButterKnife.bind(this);
//
//        mTitle.setText("微信Mos算分测试");
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                OttUtil.openServicePermissonCompat(OttDebugActivity.this, WalktourAutoService.class);
//            }
//        }, 1500);
//
//        Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                //                Log.w("czc", "" + msg.what /*+ "," + msg.obj.toString()*/);
//            }
//        };
//        appTestMain = MultipleAppTestMain.getInstance(this);
//        appTestMain.SetRespHandler(handler);
//    }
//
//
//    @OnClick(R.id.btn_ott_test)
//    void clickWxMoc() {
//
//
//        if (!OttUtil.hasServicePermission(this, WalktourAutoService.class)) {
//            OttUtil.openServicePermissonCompat(this, WalktourAutoService.class);
//        }
//
//
//        String cmd = "WeiXin -m " + "testtest"
//                + " -p " + 1
//                + " -c " + "诚"
//                + " -v " + 20
//                + " -t " + 60000;
//
//        //        String cmd = "WeiXinVF -u " + 0
//        //                + " -c " + "诚"
//        //                + " -s " + 15
//        //                + " -t " + 60;
//        //        TaskManager.getInstance().setTask(null);
//
//        //        String cmd = "FacebookApp -h " + "jay zhou"
//        //                + " -t " + 20000;
//        //        String cmd = "WhatsApp -m " + "how are you?"
//        //                + " -p " + 1
//        //                + " -c " + "mali"
//        //                + " -t " + 100000;
//
//        //        String cmd = "WhatsAppVF -u " + 1
//        //                + " -c " + "dingli"
//        //                + " -d " + 1
//        //                + " -s " + 15
//        //                + " -t " + 60 * 1000;
//
//
//        //        MultipleAppTestMain m_multipleAppTestMain = new MultipleAppTestMain(this, new Handler());
//
//        appTestMain.Run(cmd);
//    }
//
//    @OnClick(R.id.btn_ott_qq)
//    void clickQq() {
//
//
//        if (!OttUtil.hasServicePermission(this, WalktourAutoService.class)) {
//            OttUtil.openServicePermissonCompat(this, WalktourAutoService.class);
//        }
//
//        String cmd = "QQ -m " + "testtest"
//                + " -p " + 1
//                + " -c " + "诚"
//                + " -a " + 20
//                + " -t " + 60000;
//
//        appTestMain.Run(cmd);
//    }
//
//    @OnClick(R.id.btn_ott_weibo)
//    void clickSina() {
//
//        if (!OttUtil.hasServicePermission(this, WalktourAutoService.class)) {
//            OttUtil.openServicePermissonCompat(this, WalktourAutoService.class);
//        }
//
//        String cmd2 = "SinaWeibo -s 0 -t 60000 -m Hello world! -p 0";
//
//        appTestMain.Run(cmd2);
//    }
//
//
//    @OnClick(R.id.btn_ott_stop)
//    void clickStop() {
//        appTestMain.Stop();
//    }
//
//    @OnClick(R.id.btn_vivo_play)
//    void clickVivoPlay() {
//        setVoice();
//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        am.setParameters("incall_music_enabled=true");
//        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.sample_swb_48k);
//        try {
//            final MediaPlayer mediaPlayer = new MediaPlayer();
//            // 设置指定的流媒体地址
//            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
//                    file.getLength());
//            // 设置音频流的类型
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//            // 通过异步的方式装载媒体资源
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    am.setParameters("incall_music_enabled=false");
//                }
//            });
//
//            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
//                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    am.setParameters("incall_music_enabled=false");
//                    return false;
//                }
//            });
////            mediaPlayer.prepareAsync();
////            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                @Override
////                public void onPrepared(MediaPlayer mp) {
////                    // 装载完毕 开始播放流媒体
////                    mediaPlayer.start();
////                }
////            });
//            // 设置循环播放
//            // mediaPlayer.setLooping(true);
////            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
////
////                @Override
////                public void onCompletion(MediaPlayer mp) {
////                    // 在播放完毕被回调
////                }
////            });
////
////            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
////
////                @Override
////                public boolean onError(MediaPlayer mp, int what, int extra) {
////                    // 如果发生错误
////                    return false;
////                }
////            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.w("czc", "play");
//                        setVoice();
//                IAudioManager audioManager = new AudioManagerFactory().getAudioManager(getApplicationContext());
//                audioManager.startPlaying(R.raw.sample_swb_48k);
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    audioManager.stopPlaying();
//                }
////        Log.w("czc", "stop");
//    }
//
//    private void setVoice() {
//        // 音频管理器
//        AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
//        // 调节媒体播放器的音量至5(实验证明此时音质最好)
//        int mediaVolume = 0;
//        int voiceVolume = 0;
//        audioManager.setMode(AudioManager.MODE_IN_CALL);
//        mediaVolume = Deviceinfo.getInstance().getMediaVoice();
//        voiceVolume = mediaVolume;
//        audioManager.setSpeakerphoneOn(false);
//        audioManager.setMicrophoneMute(true);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_SHOW_UI);
//        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voiceVolume, AudioManager.FLAG_SHOW_UI);
//        muteSystemOrNotification(true);
//    }
//
//    /**
//     * 关闭或开启系统音或提示音
//     *
//     * @param isMute 是否关闭
//     */
//    private void muteSystemOrNotification(boolean isMute) {
//        AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
//        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, isMute);
//        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, isMute);
//        if (!isMute)
//            audioManager.setMode(AudioManager.MODE_NORMAL);
//    }
}

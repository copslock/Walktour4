package com.walktour.gui.newmap2.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.walktour.Utils.WalktourConst;
import com.walktour.gui.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/9/7
 * @describe 录音工具
 */
public class AudioUtil {
    public static final int RECORD_SUCCESS = 0;
    public static final int RECORD_FAIL = 1;
    public static final int RECORD_TOO_SHORT = 2;
    public static final int PLAY_COMPLETION = 3;
    public static final int PLAY_ERROR = 4;
    public static final int PERMISSIONS_REQUEST_FOR_AUDIO = 5;
    private  ExecutorService mExecutorService;
    Button btBinder;
    Handler mHandler;
    Activity activity;
    //录音API
    private MediaRecorder mMediaRecorder;
    //录音开始时间与结束时间
    private long startTime, endTime;
    //录音所保存的文件
    private File mAudioFile;
    //录音文件保存位置
    public AudioUtil(Activity activity) {
//录音及播放要使用单线程操作
        mExecutorService = Executors.newSingleThreadExecutor();
        this.activity=activity;
    }

    public void binderButton(Button btBinder) {
        this.btBinder = btBinder;
        initEvents();
    }

    public void setHanlder(Handler hanlder) {
        this.mHandler = hanlder;
    }
    protected void initEvents() {
        //类似微信等应用按住说话进行录音，所以用OnTouch事件
        btBinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    switch (motionEvent.getAction()) {
                        //按下操作
                        case MotionEvent.ACTION_DOWN:
                            //安卓6.0以上录音相应权限处理
                            if (Build.VERSION.SDK_INT > 22) {
                                permissionForM();
                            } else {
                                startRecord();
                            }
                            break;
                        //松开操作
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            stopRecord();
                            break;
                    }
                    //对OnTouch事件做了处理，返回true
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }

            }
        });
    }
    /**
     * @description 开始进行录音
     * @author
     * @time 2017/2/9 9:18
     */
    public void startRecord() {
        btBinder.setText(R.string.stop);
        //异步任务执行录音操作
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //播放前释放资源
                releaseRecorder();
                //执行录音操作
                recordOperation();
            }
        });
    }
    public File getAudioFile(){
        return mAudioFile;
    }
    /**
     * @description 录音失败处理
     * @author
     * @time 2017/2/9 9:35
     */
    private void recordFail() {
        mAudioFile = null;
        mHandler.sendEmptyMessage(RECORD_FAIL);
    }
    /**
     * @description 录音操作
     * @author 
     * @time 2017/2/9 9:34
     */
    private void recordOperation() {
        //创建MediaRecorder对象
        mMediaRecorder = new MediaRecorder();
        //创建录音文件,.m4a为MPEG-4音频标准的文件的扩展名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//设置日期格式
        mAudioFile = new File(WalktourConst.SAVE_ATTACH_DIR +File.separator+ df.format(new Date()) + ".m4a");
        //创建父文件夹
        mAudioFile.getParentFile().mkdirs();
        try {
            //创建文件
            mAudioFile.createNewFile();
            //配置mMediaRecorder相应参数
            //从麦克风采集声音数据
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置保存文件格式为MP4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
            mMediaRecorder.setAudioSamplingRate(44100);
            //设置声音数据编码格式,音频通用格式是AAC
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //设置编码频率
            mMediaRecorder.setAudioEncodingBitRate(96000);
            //设置录音保存的文件
            mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            //开始录音
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //记录开始录音时间
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            recordFail();
        }
    }
    /**
     * @description 结束录音操作
     * @author
     * @time 2017/2/9 9:18
     */
    private void stopRecord() {
        btBinder.setText(R.string.speak_by_press);
        btBinder.setBackgroundResource(R.drawable.btn_blue_radius_selector);
        //停止录音
        mMediaRecorder.stop();
        //记录停止时间
        endTime = System.currentTimeMillis();
        //录音时间处理，比如只有大于2秒的录音才算成功
        int time = (int) ((endTime - startTime) / 1000);
        if (time >= 0) {
            //录音成功,添加数据
//            FileBean bean = new FileBean();
//            bean.setFile(mAudioFile);
//            bean.setFileLength(time);
//            dataList.add(bean);
            //录音成功,发Message
            mHandler.sendEmptyMessage(RECORD_SUCCESS);
        } else {
            mAudioFile = null;
            mHandler.sendEmptyMessage(RECORD_TOO_SHORT);
        }
        //录音完成释放资源
        releaseRecorder();
    }
    /**
     * @description 翻放录音相关资源
     * @author jinfeng.xie
     * @time 2017/2/9 9:33
     */
    private void releaseRecorder() {
        if (null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    /**
     * @date on
     * @describe 停止操作，在onDetory使用
     * @author jinfeng.xie

     */
    public void stop(){
        mExecutorService.shutdownNow();
    }
    /*******6.0以上版本手机权限处理***************************/
    /**
     * @description 兼容手机6.0权限管理
     * @author
     * @time 2016/5/24 14:59
     */
    private void permissionForM() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_FOR_AUDIO);
        } else {
            startRecord();
        }

    }

}

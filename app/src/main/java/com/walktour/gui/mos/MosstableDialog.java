package com.walktour.gui.mos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.DateUtil;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.SafeHandler;
import com.walktour.base.util.StringUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.command.BaseCommand;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.opticom.polqa.PolqaWrapper;
import de.opticom.utils.PolqaResult;

/**
 * mos 算分过程
 *
 * @author zhicheng.chen
 * @date 2019/3/20
 */
public class MosstableDialog extends DialogFragment {

    private static final String TAG = "MosstableDialog";
    private static final int UPDATE_RESULT = 1;
    private static final int UPDATE_TIME = 2;
    private static final int SHOW_PROGRESS = 3;
    private static final int HIDE_PROGRESS = 4;

    @BindView(R.id.lv_caculate_result)
    ListView mLvCaculate;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.btn_pause_caculate)
    Button mBtnPauseOrStart;
    @BindView(R.id.btn_export_excel)
    Button mBtnExportExcel;

    private boolean mIsMocConnected;
    private ProgressDialog mProgress;
    private String mExportExcelPath;
    private long mStartTime;
    private long mPauseTime;
    private Timer mTimer;
    private MosTask mTask;
    private List<String> mDataList = new ArrayList<>();
    private List<MosResult> mResultList = new ArrayList<>();
    private MosDialogAdapter mAdapter;
    private final Object mLock = new Object();
    private Context mContext;
    private MosCallback<String> mCallback;

    /**
     * 主叫蓝牙MOS头服务
     */
    private IBluetoothMOSServiceBinder mMOCService = null;
    private BluetoothMOSDevice mCurrMOCDevice;


    /**
     * 调用PESQ的远程服务连接
     */
    private ServiceConnection mMOCConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsMocConnected = true;
            mMOCService = IBluetoothMOSServiceBinder.Stub.asInterface(service);
            startCaculate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private SafeHandler mHandler = new SafeHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if (what == UPDATE_RESULT) {
                String result = (String) msg.obj;
                mDataList.add(result);
                mAdapter.notifyDataSetChanged();

                if (mDataList.size() > 1) {
                    mLvCaculate.setSelection(mDataList.size() - 1);
                }
            } else if (what == UPDATE_TIME) {
                long interval = System.currentTimeMillis() - mStartTime - mPauseTime * 1000;
                if (interval < 60 * 1000) {
                    mTvTime.setText(String.format("耗时：%ds", ((int) interval / 1000)));
                } else if (interval < 60 * 60 * 1000) {
                    mTvTime.setText(String.format("耗时：%dmin %ds", ((int) interval / (60 * 1000)), ((int) interval % (60 * 1000) / 1000)));
                }
            } else if (what == SHOW_PROGRESS) {
                mProgress.show();
            } else if (what == HIDE_PROGRESS) {
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
            }
            return true;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.activity_dialog2);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(DensityUtil.dip2px(mContext, 280), DensityUtil.dip2px(mContext, 440));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MosCallback) {
            mCallback = (MosCallback<String>) getActivity();
        }

        mCurrMOCDevice = BluetoothMOSFactory.get().getCurrMOCDevice();
        if (mCurrMOCDevice != null) {
            Intent intentMos = new Intent(getActivity(), BluetoothMOSService.class);
            intentMos.putExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS, mCurrMOCDevice);
            intentMos.setExtrasClassLoader(BluetoothMOSDevice.class.getClassLoader());
            intentMos.putExtra(BluetoothMOSService.EXTRA_KEY_CALCULATE_PESQ, false);
            getActivity().bindService(intentMos, mMOCConnection, Context.BIND_AUTO_CREATE);
        }

        Bundle b = getArguments();
        String dir8k = b.getString("8k_dir");
        String dir48k = b.getString("48k_dir");

        mTask = new MosTask(dir8k, dir48k);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_mos_caculate, null);
        ButterKnife.bind(this, layout);
        mProgress = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);
        mDataList.add(">>开始算分");
        mAdapter = new MosDialogAdapter(mContext, mDataList);
        mLvCaculate.setAdapter(mAdapter);
        return layout;
    }


    private void startCaculate() {
        // 提高音量
        setVoice();
        // 删除录音文件
        deleteVoiceFile();
        //算分
        caculate();
        // 计时
        countTime();
    }

    private void setVoice() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        int maxValue = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(true);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxValue, AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
    }

    private void deleteVoiceFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //因为测试过程会生成很多录音文件，删除5天前的文件
                final long day = 24 * 60 * 60 * 1000;
                String dir = AppFilePathUtil.getInstance().createSDCardBaseDirectory(mContext.getString(R.string.path_voice));
                UtilsMethod.removeFiles(dir, 5 * day);
            }
        }).start();
    }

    private void caculate() {
        // 启动线程开始算分
        new Thread(mTask).start();
    }

    private void countTime() {
        mStartTime = System.currentTimeMillis();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!mTask.isPause() && !mTask.isFinish()) {
                    mHandler.sendEmptyMessage(UPDATE_TIME);
                } else if (mTask.isPause()) {
                    mPauseTime++;
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(task, 1000, 1000);
    }

    @OnClick(R.id.tv_close)
    void clickClose() {
        mTask.pause();
        dismiss();
    }

    @OnClick(R.id.btn_pause_caculate)
    void clickPause() {
        if (!mTask.isFinish()) {
            if (!mTask.isPause()) {
                mTask.pause();
                mBtnPauseOrStart.setText("继续算分");
            } else {
                mTask.start();
                mBtnPauseOrStart.setText("暂停算分");
            }
        } else {
            ToastUtil.showShort(mContext, "算分已完成！");
        }
    }

    @OnClick(R.id.btn_export_excel)
    void clickExport() {
        if (!mTask.isPause()) {
            mTask.pause();
        }
        if (!mTask.isError()) {
            mBtnExportExcel.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveToExcel();
                }
            }).start();
        }
    }

    /**
     * 保存到 Excel 表格
     */
    private void saveToExcel() {

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        boolean isError = false;

        // Excel 模版
        String dir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("mos", "report");
        File dirF = new File(dir);
        if (!dirF.exists()) {
            dirF.mkdirs();
        }

        File file = new File(dir, "mos_stable_template.xls");
        if (!file.exists()) {
            File zipF = new File(dir, "mostesttemplate.zip");
            if (!zipF.exists()) {
                UtilsMethod.writeRawResource(mContext, R.raw.mostesttemplate, zipF);
            }
            try {
                ZipUtil.unzip(zipF.getAbsolutePath(), dir, false);
                zipF.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        POIFSFileSystem fs = null;
        FileOutputStream fos = null;

        try {
            fs = new POIFSFileSystem(new FileInputStream(dir + File.separator + "mos_stable_template.xls"));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            if (wb != null) {
                HSSFSheet sheet = wb.getSheetAt(0);
                if (sheet != null) {
                    // 浮动次数（方差值 > 0.01为浮动)
                    int flowSum = 0;
                    for (int i = 0; i < mResultList.size(); i++) {
                        int beginIndex = sheet.getFirstRowNum() + 3 + i;
                        HSSFRow row = sheet.getRow(beginIndex);

                        MosResult mosResult = mResultList.get(i);

                        HSSFCell name = row.getCell(0);
                        name.setCellValue(mosResult.name);
                        HSSFCell type = row.getCell(1);
                        type.setCellValue(mosResult.type == BaseCommand.FileType.polqa_8k ? "polqa_8k" : "polqa_48k");

                        // 错误次数
                        int errorSum = 0;
                        // 总分
                        double scoreSum = 0.0;
                        // 分数
                        int number = mosResult.scores.length;
                        for (int j = 0; j < number; j++) {
                            HSSFCell scoreCell = row.getCell(2 + j);
                            String value = mosResult.scores[j];
                            if (StringUtil.isEmpty(value) || value.equals("0.0")) {
                                value = "0.0";
                                errorSum++;
                            }

                            double score = 0;
                            try {
                                score = Double.valueOf(value).doubleValue();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            scoreSum += score;

                            if (scoreCell != null && value != null) {
                                scoreCell.setCellValue(value);
                            }
                        }

                        // 均差和
                        double powSum = 0.0;
                        // 平均数
                        double average = scoreSum / (number - errorSum);

                        LogUtil.w(TAG, "平均数：" + average);
                        for (int m = 0; m < number; m++) {
                            String value = mosResult.scores[m];
                            if (StringUtil.isEmpty(value) || value.equals("0.0")) {
                                continue;
                            }
                            double score = 0;
                            try {
                                score = Double.valueOf(value).doubleValue();
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            powSum += Math.pow((average - score), 2);

                        }

                        // 方差
                        double variance = powSum / (number - errorSum);
                        if (variance > 0.01) {
                            flowSum++;
                        }
                        LogUtil.w(TAG, "方差：" + String.format("%.5f", variance));

                        // 方差
                        HSSFCell fc = row.getCell(27);
                        if (fc != null) {
                            fc.setCellValue(String.format("%.5f", variance));
                        }

                        // 备注
                        HSSFCell mark = row.getCell(28);
                        if (mark != null && !StringUtil.isEmpty(mosResult.mark)) {
                            mark.setCellValue(mosResult.mark);
                        }

                    }

                    // 浮动概率
                    float flowPercent = ((float) flowSum / mResultList.size());
                    HSSFRow row = sheet.getRow(5);
                    if (row != null) {
                        HSSFCell flowCell = row.getCell(30);
                        if (flowCell != null) {
                            flowCell.setCellValue(String.format("%.2f", flowPercent));
                        }
                    }

                    // 输出文件
                    String reportFile = mExportExcelPath = dir + "stable-score-" + DateUtil.formatDate(new Date()) + ".xls";
                    fos = new FileOutputStream(reportFile);
                    wb.write(fos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            isError = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showShort(mContext, "导出失败！");
                }
            });
        } finally {


            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            mHandler.sendEmptyMessage(HIDE_PROGRESS);
            dismiss();

            if (!isError) {
                if (mCallback != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.handleResult(mExportExcelPath);
                        }
                    });
                }
            }
        }
    }

    class MosTask implements Runnable {
        private boolean mIsPause;
        private boolean mIsFinish;
        private boolean mIsError;
        private String mDir8k;
        private String mDir48k;

        public MosTask(String dir8k, String dir48k) {
            mDir8k = dir8k;
            mDir48k = dir48k;
        }

        /**
         * 出错标识
         */
        public boolean isError() {
            return mIsError;
        }

        public boolean isFinish() {
            return mIsFinish;
        }

        public boolean isPause() {
            return mIsPause;
        }

        @Override
        public void run() {

            Boolean ret = true;
            try {
                ret = doInBackground();
            } catch (Exception e) {
                e.printStackTrace();
                ret = false;
            } finally {
                sendResultMsg(ret.booleanValue() ? ">>完成算分" : ">>算分出错");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtnPauseOrStart.setText("算分完成");
                    }
                });

                finish();
            }


        }

        protected Boolean doInBackground() {

            //check mos
            BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMOCDevice();
            if (device == null || mMOCService == null) {
                sendResultMsg("     Mosbox is unconnect！");
                error();
                finish();
                return false;
            }

            // check license
            File licenseFile = AppFilePathUtil.getInstance().getAppFilesFile(MosCaculator.POLQA_LICENSE_FILE);
            if (!licenseFile.exists()) {
                licenseFile = AppFilePathUtil.getInstance().getSDCardBaseFile(MosCaculator.POLQA_LICENSE_FILE);
            }
            if (!licenseFile.exists()) {
                sendResultMsg("     License not exist！");
                error();
                finish();
                return false;
            }


            String licenseFilePath = licenseFile.getAbsolutePath();
            loopTest(licenseFilePath, BaseCommand.FileType.polqa_8k, mDir8k);
            loopTest(licenseFilePath, BaseCommand.FileType.polqa_48k, mDir48k);

            return true;
        }

        private void prepareLoopTest(BaseCommand.FileType fileType) {
            if (mMOCService != null) {
                try {
                    mMOCService.initMOS(fileType.getName(), false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        private void loopTest(String licenseFilePath, BaseCommand.FileType fileType, String dirPath) {

            prepareLoopTest(fileType);

            File dir8kFile = new File(dirPath);
            File[] file = dir8kFile.listFiles();

            //  loop dir files
            out:
            for (int i = 0; i < file.length; i++) {
                final File f = file[i];
                String fileName = f.getName();
                boolean isWav = fileName.endsWith("wav") || fileName.endsWith("WAV");
                // filter wav file
                if (f.isFile() && isWav) {

                    // every voice test 25 times
                    final int testTimes = 25;

                    MosResult mosResult = new MosResult();
                    mResultList.add(mosResult);
                    mosResult.name = fileName;
                    mosResult.type = fileType;
                    mosResult.scores = new String[testTimes];
                    StringBuilder markSb = new StringBuilder();
                    in:
                    for (int j = 0; j < testTimes; j++) {

                        if (isFinish()) {
                            break out;
                        }

                        if (mIsPause) {
                            synchronized (mLock) {
                                try {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBtnPauseOrStart.setText("继续算分");
                                        }
                                    });
                                    mLock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (j == 0) {
                            sendResultMsg(">>" + f.getName());
                        }

                        try {
                            if (mMOCService != null) {
                                MediaPlayer player = createMediaPlayer(f);

                                boolean excFlag = mMOCService.runRecord(fileType.getName());
                                if (excFlag) {
                                    boolean recordRet = true;
                                    player.start();

                                    String recordFile = mMOCService.getRecordFile();
                                    int t = 0;
                                    while (recordFile == null && t < 30) {
                                        Thread.sleep(500);
                                        recordFile = mMOCService.getRecordFile();
                                        t++;
                                    }

                                    if (StringUtil.isEmpty(recordFile)) {
                                        LogUtil.e(TAG, "录音失败");
                                        recordRet = false;
                                    } else if (recordFile.equals("error")) {
                                        LogUtil.e(TAG, "录音失败");
                                        recordRet = false;
                                    } else {
                                        LogUtil.w(TAG, recordFile);
                                    }
                                    stopMediaPlayer(player);

                                    if (i < 2 && j < 2 && !recordRet) {
                                        //try again
                                        recordRet = true;
                                        LogUtil.e(TAG, "try again");
                                        MediaPlayer player2 = createMediaPlayer(f);
                                        player2.start();
                                        Thread.sleep(player2.getDuration() + 2000);
                                        recordFile = mMOCService.getRecordFile();
                                        if (StringUtil.isEmpty(recordFile)) {
                                            LogUtil.e(TAG, "录音失败");
                                            recordRet = false;
                                        } else if (recordFile.equals("error")) {
                                            LogUtil.e(TAG, "录音失败");
                                            recordRet = false;
                                        } else {
                                            LogUtil.w(TAG, recordFile);
                                        }
                                        stopMediaPlayer(player2);
                                    }


                                    final int tempIndex = j + 1;
                                    String rLog;
                                    if (recordRet) {
                                        MosCaculator caculator = new MosCaculator(mContext, licenseFilePath);
                                        PolqaResult polqaResult = caculator.calculateBySelfFile(fileType, recordFile);

                                        if (polqaResult.result == PolqaWrapper.POLQA_OK) {
                                            double mfMOSLQO = polqaResult.mfMOSLQO;
                                            mosResult.scores[j] = String.format(Locale.getDefault(), "%.2f", mfMOSLQO);
                                            rLog = "    " + tempIndex + "." + String.format(Locale.getDefault(), "Score:%.2f", mfMOSLQO);
                                        } else {
                                            mosResult.scores[j] = String.format(Locale.getDefault(), "%.2f", 0.0);
                                            markSb.append(String.format("第%d次算分出错", tempIndex));
                                            rLog = "    " + tempIndex + ".Error:" + PolqaWrapper.getErrorString(polqaResult.result);
                                        }
                                    } else {
                                        rLog = "    " + tempIndex + ".Error: No Record File！";
                                        markSb.append(String.format("第%d次算分出错", tempIndex));
                                    }
                                    sendResultMsg(rLog);
                                } else {
                                    sendResultMsg("    error");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e(TAG, "录音异常");
                            sendResultMsg("    error");
                        }
                    }
                    mosResult.mark = markSb.toString();
                }
            }
        }

        private MediaPlayer createMediaPlayer(File f) throws IOException {
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(f.getAbsolutePath());
            player.setLooping(false);
            player.setVolume(1f, 1f);
            player.setScreenOnWhilePlaying(true);
            player.prepare();
            return player;
        }

        private void stopMediaPlayer(MediaPlayer player) {
            if (player != null) {
                player.stop();
                player.release();
            }
        }

        public void start() {
            synchronized (mLock) {
                mIsPause = false;
                mLock.notify();
            }
        }

        public void error() {
            mIsError = true;
        }

        public void pause() {
            mIsPause = true;
        }

        public void finish() {
            mIsFinish = true;
        }


        private void sendResultMsg(String message) {
            Message msg = new Message();
            msg.obj = message;
            msg.what = UPDATE_RESULT;
            mHandler.sendMessage(msg);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTask.finish();
        if (mMOCService != null && mIsMocConnected) {
            getActivity().unbindService(mMOCConnection);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mTask.pause();
    }

}

package com.walktour.gui.mos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.walktour.base.util.SafeHandler;
import com.walktour.base.util.StringUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.command.BaseCommand;

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
public class MosOfflineDialog extends DialogFragment {

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

    private ProgressDialog mProgress;
    private String mExportExcelPath;
    private long mStartTime;
    private long mPauseTime;
    private Timer mTimer;
    private MosRunnable mTask;
    private List<String> mDataList = new ArrayList<>();
    private List<MosResult> mResultList = new ArrayList<>();
    private MosDialogAdapter mAdapter;
    private final Object mLock = new Object();
    private Context mContext;
    private MosCallback<String> mCallback;


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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.activity_mos_caculate, null);

        ButterKnife.bind(this, layout);

        initView();
        startCaculate();

        return layout;
    }

    private void initView() {

        mProgress = new ProgressDialog(mContext, ProgressDialog.THEME_HOLO_LIGHT);

        mDataList.add(">>开始算分");
        mAdapter = new MosDialogAdapter(mContext, mDataList);
        mLvCaculate.setAdapter(mAdapter);
    }


    private void startCaculate() {
        Bundle b = getArguments();
        String dir8k = b.getString("8k_dir");
        String dir48k = b.getString("48k_dir");

        // 启动线程开始算分
        mTask = new MosRunnable(dir8k, dir48k);
        new Thread(mTask).start();

        // 计算耗时
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

        File file = new File(dir, "mos_offline_template.xls");
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
            fs = new POIFSFileSystem(new FileInputStream(dir + File.separator + "mos_offline_template.xls"));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            if (wb != null) {
                HSSFSheet sheet = wb.getSheetAt(0);
                if (sheet != null) {
                    for (int i = 0; i < mResultList.size(); i++) {
                        int beginIndex = sheet.getFirstRowNum() + 3 + i;
                        HSSFRow row = sheet.getRow(beginIndex);

                        MosResult mosResult = mResultList.get(i);

                        HSSFCell name = row.getCell(0);
                        name.setCellValue(mosResult.name);
                        HSSFCell type = row.getCell(1);
                        type.setCellValue(mosResult.type == BaseCommand.FileType.polqa_8k ? "polqa_8k" : "polqa_48k");

                        // 分数
                        for (int j = 0; j < mosResult.scores.length; j++) {
                            HSSFCell score = row.getCell(2 + j);
                            String value = mosResult.scores[j];
                            if (value != null) {
                                score.setCellValue(value);
                            }

                        }

                        // 备注
                        HSSFCell mark = row.getCell(7);
                        if (!StringUtil.isEmpty(mosResult.mark)) {
                            mark.setCellValue(mosResult.mark);
                        }

                    }

                    // 输出文件
                    String reportFile = mExportExcelPath = dir + "offline-score-" + DateUtil.formatDate(new Date()) + ".xls";
                    fos = new FileOutputStream(reportFile);
                    wb.write(fos);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(mContext, "导出成功！");
                        }
                    });

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

    class MosRunnable implements Runnable {
        private boolean mIsPause;
        private boolean mIsFinish;
        private boolean mIsError;
        private String mDir8k;
        private String mDir48k;

        public MosRunnable(String dir8k, String dir48k) {
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
                Message msg = new Message();
                msg.obj = ret.booleanValue() ? ">>完成算分" : ">>算分出错";
                msg.what = UPDATE_RESULT;
                mHandler.sendMessage(msg);
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

            // check license
            File licenseFile = AppFilePathUtil.getInstance().getAppFilesFile(MosCaculator.POLQA_LICENSE_FILE);
            if (!licenseFile.exists()) {
                licenseFile = AppFilePathUtil.getInstance().getSDCardBaseFile(MosCaculator.POLQA_LICENSE_FILE);
            }
            if (!licenseFile.exists()) {
                Message msg = new Message();
                msg.obj = "     License not exist！";
                msg.what = UPDATE_RESULT;
                mHandler.sendMessage(msg);
                error();
                finish();
                return false;
            }

            loopTest(licenseFile.getAbsolutePath(), BaseCommand.FileType.polqa_8k, mDir8k);
            loopTest(licenseFile.getAbsolutePath(), BaseCommand.FileType.polqa_48k, mDir48k);

            return true;
        }

        private void loopTest(String licenseFilePath, BaseCommand.FileType fileType, String dirPath) {
            File dir8kFile = new File(dirPath);
            File[] file = dir8kFile.listFiles();

            //  loop dir files
            out:
            for (int j = 0; j < file.length; j++) {
                final File f = file[j];
                String fileName = f.getName();
                boolean isWav = fileName.endsWith("wav") || fileName.endsWith("WAV");
                // filter wav file
                if (f.isFile() && isWav) {
                    MosResult mosResult = new MosResult();
                    mResultList.add(mosResult);
                    mosResult.name = fileName;
                    mosResult.type = fileType;
                    mosResult.scores = new String[5];
                    // every voice test 5 times
                    int testTimes = 5;
                    in:
                    for (int k = 0; k < testTimes; k++) {

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
                        if (k == 0) {
                            Message msg = new Message();
                            msg.obj = ">>" + f.getName();
                            msg.what = UPDATE_RESULT;
                            mHandler.sendMessage(msg);
                        }

                        MosCaculator caculator = new MosCaculator(mContext, licenseFilePath);
                        PolqaResult polqaResult = caculator.calculateBySelfFile(fileType, f.getAbsolutePath());

                        final int tempIndex = k + 1;

                        String rLog;
                        if (polqaResult.result == PolqaWrapper.POLQA_OK) {
                            double mfMOSLQO = polqaResult.mfMOSLQO;
                            mosResult.scores[k] = String.format(Locale.getDefault(), "%.2f", mfMOSLQO);
                            rLog = "    " + tempIndex + "." + String.format(Locale.getDefault(), "Score:%.2f", mfMOSLQO);
                        } else {
                            mosResult.scores[k] = PolqaWrapper.getErrorString(polqaResult.result);
                            mosResult.mark = "算分出错";
                            rLog = "    " + tempIndex + ".Error:" + PolqaWrapper.getErrorString(polqaResult.result);
                        }

                        Message msg = new Message();
                        msg.obj = rLog;
                        msg.what = UPDATE_RESULT;
                        mHandler.sendMessage(msg);
                    }
                }
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


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTask.finish();
    }


    @Override
    public void onPause() {
        super.onPause();
        mTask.pause();
    }

}

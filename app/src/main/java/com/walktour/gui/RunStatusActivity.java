package com.walktour.gui;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.Html;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walktour.Utils.GlobalExceptionHandler;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.model.StateInfoModel;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 运行状态显示Activity(测试状态、手机状态等)
 * 
 * @author zhihui.lian
 *
 */
public class RunStatusActivity extends BasicActivity implements RefreshEventListener {

	private DecimalFormat df = new DecimalFormat("#00");
	private StateInfoModel stateInfoModel = null;
	private String[] runTimeArray;
    private TextView title;
    private TextView phoneversion;
    private TextView androidversion;
    private TextView currentNet;
    private TextView netState;
    private TextView currentJon;
    private TextView testTimes;
    private TextView successRate;
    private TextView delay;
    private TextView avgThrRate;
    private TextView surplusSize;
    private TextView temperature;
    private TextView cpuState;
    private TextView freecpu;
    private TextView storagePath;
    private TextView sdcardTotalTitle;
    private TextView sdcardUsed;
    private TextView sdcardFree;
    private TextView keepDurationTxt;
    private TextView logRecordTxt;
    private TextView logNameTxt;
    private TextView exceptionTxt;
    private ProgressBar cpuProgress;
    private ProgressBar sdProgress;
    private ImageButton pointer;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.running_status_page);
		RefreshEventManager.addRefreshListener(this);
		stateInfoModel = TraceInfoInterface.traceData.getStateInfo();
		queryCpuAndData();
		findView();
		runTimeArray = getResources().getStringArray(R.array.run_time_array);
	}

	/**
	 * 加载数据控件
	 */
	private void findView() {
        title = initTextView(R.id.title_txt);
        pointer = initImageButton(R.id.pointer);
        phoneversion = initTextView(R.id.phone_version); /** 手机型号 */
        androidversion = initTextView(R.id.system_version); /** 手机android版本 */
        currentNet = initTextView(R.id.currentNet_txt); /** 当前网络 */
        netState = initTextView(R.id.netstate_txt); /** 网络状态 */
        currentJon = initTextView(R.id.currentjon_txt); /** 当前业务 */
        testTimes = initTextView(R.id.testtimes_txt); /** 测试次数 */
        successRate = initTextView(R.id.successrate_txt); /** 成功率 */
        delay = initTextView(R.id.delay_txt); /** 时延 */
        avgThrRate = initTextView(R.id.avgThrRate_txt); /** 平均速率 */
        surplusSize = initTextView(R.id.surplusSize_txt); /** 存储卡剩余空间 */
        temperature = initTextView(R.id.temperature_txt); /** 手机温度 */
        cpuState = initTextView(R.id.cpuState_txt); /** 手机CPU已用 */
        freecpu = initTextView(R.id.freecpu_txt); /** 手机CPU空闲 */
        storagePath = initTextView(R.id.storagePath_txt); /** 存储路径 */
        sdcardTotalTitle = initTextView(R.id.sdcard_title_txt); /** 存储总内存 */
        sdcardUsed = initTextView(R.id.sdcard_used_txt); /** 存储已内存 */
        sdcardFree = initTextView(R.id.sdcard_free_txt); /** 存储空闲内存 */
        keepDurationTxt = initTextView(R.id.keep_duration_txt); /** 系统运行时长 */
        logRecordTxt = initTextView(R.id.log_record_txt); /** Log记录状态 */
        logNameTxt = initTextView(R.id.log_name_txt); /** Log名字 */
        exceptionTxt = initTextView(R.id.tv_exception_times);
        cpuProgress = initProgressBar(R.id.cpu_progress); /** cpu状态条 */
        sdProgress = initProgressBar(R.id.sdcard_progress); /** Sdcard状态条 */


        title.setText(R.string.running_status);
        pointer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                RunStatusActivity.this.finish();
                RefreshEventManager.removeRefreshListener(RunStatusActivity.this);
            }
        });

        initData();

	}

    private void initData() {
        if (stateInfoModel != null) {
            phoneversion.setText(getModel());
            androidversion.setText("Android" + getAndroidVersion());
            currentNet.setText(stateInfoModel.getCurrentNet());
            netState.setText(stateInfoModel.getNetState());
            currentJon.setText((stateInfoModel.getCurrentJon() != null && stateInfoModel.getCurrentJon().equals("")) ? "-"
                    : stateInfoModel.getCurrentJon());
            testTimes.setText((stateInfoModel.getTestTimes() != null && stateInfoModel.getTestTimes().equals("")) ? "-"
                    : stateInfoModel.getTestTimes());
            successRate.setText((stateInfoModel.getSuccessRate() != null && stateInfoModel.getSuccessRate().equals("")) ? "-"
                    : stateInfoModel.getSuccessRate());
            delay.setText((stateInfoModel.getDelay() != null && stateInfoModel.getDelay().equals("")) ? "-"
                    : stateInfoModel.getDelay());
            avgThrRate.setText((stateInfoModel.getAvgThrRate() != null && stateInfoModel.getAvgThrRate().equals("")) ? "-"
                    : stateInfoModel.getAvgThrRate());
            surplusSize.setText(stateInfoModel.getSurplusSize() != -1
                    ? Formatter.formatFileSize(getApplicationContext(), stateInfoModel.getSurplusSize())
                    : getString(R.string.sys_alarm_storge_sdcardnon));
            temperature.setText(Html.fromHtml(stateInfoModel.getTemperature() + "&#176" + "C"));
            cpuState.setText(Html.fromHtml(getString(R.string.used) + stateInfoModel.getCpuState() + "%"));
            freecpu.setText(Html
                    .fromHtml(getString(R.string.free) + (100 - Integer.valueOf(stateInfoModel.getCpuState())) + "%"));
            storagePath.setText(ConfigRoutine.getInstance().getStorgePath());
            cpuProgress.setProgress(Integer.valueOf(stateInfoModel.getCpuState()));
            sdProgress.setProgress((int) (100 - ((stateInfoModel.getSurplusSize() * 100) / getTotalSD())));
            sdcardTotalTitle.setText(Html.fromHtml(
                    "<font color=#333333>SDCard</font> " + Formatter.formatFileSize(getApplicationContext(), getTotalSD())));
            sdcardUsed.setText(Html.fromHtml(getString(R.string.used)
                    + Formatter.formatFileSize(getApplicationContext(), (getTotalSD() - stateInfoModel.getSurplusSize()))));
            sdcardFree.setText(Html.fromHtml(getString(R.string.free)
                    + Formatter.formatFileSize(getApplicationContext(), stateInfoModel.getSurplusSize())));
            keepDurationTxt.setText(runTimeFormat(stateInfoModel.getRunTime()));
            logRecordTxt.setText((stateInfoModel.getLogRecordSize() != null && stateInfoModel.getLogRecordSize().equals(""))
                    ? "-" : stateInfoModel.getLogRecordSize()); // 这边显示可能还要转换单位

            if (!logNameTxt.getText().toString().equals(stateInfoModel.getLogName())) {
                logNameTxt.setText((stateInfoModel.getLogName() != null && stateInfoModel.getLogName().equals("")) ? "-"
                        : stateInfoModel.getLogName());
            }
        }
        //2018.07.02 移动招标项
        exceptionTxt.setText(String.valueOf(GlobalExceptionHandler.getExceptionTimes()));
    }

    /**
	 * 计算运行时长
	 */

	private String runTimeFormat(long runTime) {
		String runTimeStr = "";
		try {
			long day1 = runTime / (24 * 3600);
			long hour1 = runTime % (24 * 3600) / 3600;
			long minute1 = runTime % 3600 / 60;
			long second1 = runTime % 60;
			runTimeStr = day1 + runTimeArray[0] + " " + hour1 + runTimeArray[1] + df.format(minute1) + runTimeArray[2]
					+ df.format(second1) + runTimeArray[3];
			return runTimeStr;
		} catch (Exception e) {
			e.printStackTrace();
			return runTimeStr;
		}
	}

	/**
	 * 获取手机型号信息
	 * 
	 * @return
	 */
	public static String getModel() {
		return Build.MODEL;
	}

	/**
	 * 获得Android版本
	 * 
	 * @return
	 */
	public static String getAndroidVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 
	 * 获取SDcard卡总内存，单位是字节(Byte)
	 */
	@SuppressWarnings("deprecation")
	public static Long getTotalSD() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 取得sdcard文件路径
			StatFs sf = new StatFs(sdcardDir.getPath());
			// 获取block的SIZE
			long bSize = sf.getBlockSize();
			// 可使用的Block的数量
			long totalBlocks = sf.getBlockCount();
			return bSize * totalBlocks;
		} else {
			return (long) -1;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RefreshEventManager.removeRefreshListener(this);
		finish();
	}

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
		case ACTION_WALKTOUR_TIMER_CHANGED:
			stateInfoModel = TraceInfoInterface.traceData.getStateInfo();
			queryCpuAndData();
			initData();
			break;

		default:
			break;
		}
	}

	/**
	 * 查询cpu与存储状态
	 */
	private void queryCpuAndData() {
		stateInfoModel.setCpuState(String.valueOf((int) UtilsMethod.readUsage()));
		stateInfoModel.setSurplusSize(UtilsMethod.getAvaiableSD());
	}

}

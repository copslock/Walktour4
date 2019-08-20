/*
 * 文件名: PlaybackManager.java
 * 版    权：  Copyright Dingli Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-8-31
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.dinglicom.dataset;

import android.content.Context;

import com.dinglicom.dataset.logic.ControlPanelLinstener;
import com.walktour.Utils.UtilsWalktour;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.service.test.TestTaskService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 回放管理<BR>
 * 回放逻辑流程：
 * 1、先初始化回放对象PlaybackManager
 * 2、调用openPlayback传入回放文件路径,及回放速度
 * 3、开始回放....
 * 3、结束回放closePlayback
 * 注:回放过程可设置回放速度,回放方向
 *
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-8-31]
 */
public class PlaybackManager {
	private static final String TAG = "PlaybackManager";
	/**
	 * 回放速度1X
	 */
	public static final int PLAYBACK_SPEED_1X = 1;

	/**
	 * 回放速度2X
	 */
	public static final int PLAYBACK_SPEED_2X = 2;

	/**
	 * 回放速度3X
	 */
	public static final int PLAYBACK_SPEED_3X = 3;

	/**
	 * 回放方向类型 正向
	 */
	public static final int PLAYBACK_DIRECTION_NEXT = 1;
	/**
	 * 回放主向类型 反向
	 */
	public static final int PLAYBACK_DIRECTION_PREV = 0;

	/**
	 * 回放速度采样点系数
	 */
	public static int PLAYBACK_PSEED_MULTIPLE = 100;

	/**
	 * 数据集管理对象
	 */
	private DatasetManager mDatasetMgr;

	/**
	 * 控制面板回调接口
	 */
	private ControlPanelLinstener controlPanelLinstener;

	/**
	 * czc:控制面板回调接口
	 */
	private List<ControlPanelLinstener> mControlListener = new ArrayList<>();

	/**
	 * 采样点总数
	 */
	private int pointIndexTotal = -1;

	/**
	 * 当前采样点
	 */
	private int currentIndex = -1;

	/**
	 * 当前回放速度
	 */
	private int currentSpeed = 1;

	/**
	 * 回放方向 ,1为正向播放,0为反向播放
	 */
	private int playbackDirection = 1;

	private Context mContext = null;

//    private ApplicationModel appModel = null;

	/**
	 * 是否暂停
	 */
	private boolean isOnPasue = false;

	/**
	 * 没回放文件
	 */
	public static final int NO_FILE_FAIL = -1;

	/**
	 * 打开回放文件失败
	 */
	public static final int OPEN_FILE_FAIL = -2;

	/**
	 * 文件数单位
	 */
	public static final int UINT_FILE = 101;
	/**
	 * 时间单位
	 */
	public static final int UINT_TIME = 102;
	private int uint = UINT_FILE;

	/**
	 * [构造简要说明]
	 */
	public PlaybackManager(Context context, ControlPanelLinstener controlPanelLinstener) {
		mContext = context;
		mDatasetMgr = DatasetManager.getInstance(context);
		mDatasetMgr.setPlaybackManager(this);
		this.controlPanelLinstener = controlPanelLinstener;
//        appModel = ApplicationModel.getInstance();
		if (!mControlListener.contains(controlPanelLinstener)) {
			mControlListener.add(controlPanelLinstener);
		}
	}

	public void addControPanelListener(ControlPanelLinstener linstener) {
		if (linstener != null) {
			if (!mControlListener.contains(linstener)) {
				mControlListener.add(linstener);
			}
		}
	}

	/**
	 * 打开文件回放<BR>
	 * 回放端口号默认为3
	 *
	 * @param filepath          全路径
	 * @param speed             回放速度
	 * @param playbackDirection 回放方向 ,1为正向播放,0为方向播放
	 * @return -1为文件不存在   大于0为正常的采样点   -2为打开回放失败，需要关闭
	 */
	public int openPlayback(String filepath, int speed, int playbackDirection) {
		if (!new File(filepath).exists()) {
			return NO_FILE_FAIL;
		}
		boolean flag = mDatasetMgr.openPlayback(DatasetManager.PORT_4, filepath);
		if (flag) {
			pointIndexTotal = mDatasetMgr.getTotalPointCount(DatasetManager.PORT_4);
			this.playbackDirection = playbackDirection;
			mDatasetMgr.getModuleInfo().playbackDirection = playbackDirection;
			currentIndex = 0;
			currentSpeed = speed;

			return pointIndexTotal;
		}
		return OPEN_FILE_FAIL;
	}

	public int openPlayback(String filepath, int speed, int playbackDirection, int startIndex, int endIndex) {
		if (!new File(filepath).exists()) {
			return NO_FILE_FAIL;
		}
		boolean flag = mDatasetMgr.openPlayback(DatasetManager.PORT_4, filepath, startIndex, endIndex);
		if (flag) {
			PLAYBACK_PSEED_MULTIPLE = 20;
			pointIndexTotal = endIndex;
			this.playbackDirection = playbackDirection;
			mDatasetMgr.getModuleInfo().playbackDirection = playbackDirection;
			currentIndex = startIndex;
			currentSpeed = speed;

			return pointIndexTotal;
		}
		return OPEN_FILE_FAIL;
	}

	/**
	 * 播放<BR>
	 * [功能详细描述]
	 *
	 * @param filePath 回放文件
	 * @return
	 */
	public boolean onPlay(String filePath, int speed, int playbackDirection) {
		int flag = openPlayback(filePath, speed, playbackDirection);
		return controlPanelLinstener.onPlay(flag);
	}

	;

	/**
	 * 播放<BR>
	 * [功能详细描述]
	 *
	 * @param filePath 回放文件
	 * @return
	 */
	public boolean onPlay(String filePath, int speed, int playbackDirection, int startIndex, int endIndex) {
		int flag = openPlayback(filePath, speed, playbackDirection, startIndex, endIndex);
		return controlPanelLinstener.onPlay(flag);
	}

	;

	/**
	 * 停止<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public boolean onStop() {
		boolean flag = closePlayback(DatasetManager.PORT_4);
//        mDatasetMgr.startDataSet(false);

		if (flag) {
			return controlPanelLinstener.onStop();
		}
		return false;
	}

	;


	/**
	 * 暂停<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public boolean onPasue() {
		//TODO采样点更新线程暂停
		controlPanelLinstener.onPasue();
		return true;

	}

	;


	/**
	 * 控制界面调用暂停方法
	 *
	 * @param isOnPasue
	 */
	//twq20140214当回放点暂停测试时，屏幕进入冻屏状态，屏暮的冻屏状态也控制当前回放进入暂这中
	public void setIsPasue(boolean isOnPasue) {
		this.isOnPasue = isOnPasue;
	}

	/**
	 * 快进<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public boolean onFastForward(int speed) {
		currentSpeed = speed;
		return controlPanelLinstener.onFastForward(speed);
	}

	;

	/**
	 * 快退<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public boolean onRewind(int speed) {
		currentSpeed = speed;
		return controlPanelLinstener.onRewind(speed);
	}

	;

	/**
	 * 下一个<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public boolean onNext(boolean isSync) {
		currentIndex = currentIndex + PLAYBACK_PSEED_MULTIPLE;
		if (currentIndex >= pointIndexTotal) {
			currentIndex = pointIndexTotal;
		}

		if (isSync) {
			long pointTime = mDatasetMgr.getPointTime(currentIndex);
			UtilsWalktour.sendNormalMsgToBluetooth(mContext, TestTaskService.MSG_PLAYBACK_INDEXTIMES_SYNC + pointTime);
		}
		return controlPanelLinstener.onNext();
	}

	;


	/**
	 * 获取数据时长
	 *
	 * @return 总时长
	 */
	public long getTotalTime() {
		if(pointIndexTotal == -1){
			return 0;
		}
		long end = mDatasetMgr.getPointTime(pointIndexTotal);
		long begin = mDatasetMgr.getPointTime(0);
		return (end - begin) / 1000;//微秒转毫秒
	}

	public int getTotalCount() {
		return pointIndexTotal;
	}


	/**
	 * 上一个<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public boolean onUp(boolean isSync) {
		currentIndex = currentIndex - PLAYBACK_PSEED_MULTIPLE;
		if (currentIndex < 0) {
			currentIndex = 0;
		}


		if (isSync) {
			long pointTime = mDatasetMgr.getPointTime(currentIndex);
			UtilsWalktour.sendNormalMsgToBluetooth(mContext, TestTaskService.MSG_PLAYBACK_INDEXTIMES_SYNC + pointTime);
		}

		return controlPanelLinstener.onUp();
	}

	;


	/**
	 * 更新进度<BR>
	 * [功能详细描述]
	 *
	 * @param progress
	 */
	private void onSeekBar(int progress) {
		if (uint == UINT_FILE) {
//				controlPanelLinstener.onSeekBar(progress);
			for (ControlPanelLinstener linstener : mControlListener) {
				linstener.onSeekBar(progress);
			}
		} else if (uint == UINT_TIME) {
			long end = mDatasetMgr.getPointTime(progress);
			long begin = mDatasetMgr.getPointTime(0);
//			controlPanelLinstener.onSeekBar((end - begin) / 1000);
			for (ControlPanelLinstener linstener : mControlListener) {
				linstener.onSeekBar((end - begin) / 1000, progress);
			}
		}
	}

	/**
	 * 获取当前回放单位类型
	 *
	 * @return
	 */
	public int getPlaybackUintType() {
		return uint;
	}

	;


	/**
	 * 获取当前采样点<BR>
	 * [功能详细描述]
	 *
	 * @return
	 */
	public int getCurrentIndex() {
		//twq20140214响应屏幕冻屏时为暂停回放状态
		if (isOnPasue) {
			//if(appModel.isFreezeScreen()){
			onPasue();
			onSeekBar(currentIndex);
			return currentIndex;
		}
		if (this.playbackDirection == PLAYBACK_DIRECTION_PREV) {
			currentIndex -= (pointIndexTotal - currentIndex >= currentSpeed * PLAYBACK_PSEED_MULTIPLE ? currentSpeed * PLAYBACK_PSEED_MULTIPLE : pointIndexTotal - currentIndex);
		} else {
			currentIndex += (pointIndexTotal - currentIndex >= currentSpeed * PLAYBACK_PSEED_MULTIPLE ? currentSpeed * PLAYBACK_PSEED_MULTIPLE : pointIndexTotal - currentIndex);
		}
		if (currentIndex < 0) {
			currentIndex = 0;
			//onStop();
		}
		if (currentIndex >= pointIndexTotal) {
			mDatasetMgr.getModuleInfo().playbackStartPointIndex = currentIndex;
			onSeekBar(currentIndex);
			//onStop();
			return currentIndex;
		}

		mDatasetMgr.getModuleInfo().playbackStartPointIndex = currentIndex;
		onSeekBar(currentIndex);
		LogUtil.d(TAG, "----getCurrentIndex---:" + currentIndex);
		return currentIndex;
	}


	/**
	 * 跳到指定采样点  网络大数据
	 *
	 * @param appointIndex 指定采样点
	 * @author zhihui.lian
	 */

	public void setSkipIndex(int appointIndex) {
		if (uint == PlaybackManager.UINT_FILE) {
			setSkipIndex(appointIndex, false);
		} else if (uint == PlaybackManager.UINT_TIME) {
			int radio = (int) ((float) appointIndex * 100 / getTotalCount());
			setSkipIndex(radio, false);
		}
	}

	/**
	 * 跳到指定采样点
	 *
	 * @param appointIndex 指定采样点
	 * @param isSync       是否同步过来的消息,如果否且当前为蓝牙连接状态,需要发送同步消息
	 */
	public void setSkipIndex(int appointIndex, boolean isSync) {
		if (uint == UINT_FILE) {
			mDatasetMgr.getModuleInfo().playbackStartPointIndex = appointIndex;
			onSeekBar(appointIndex);
			currentIndex = appointIndex;
		} else if (uint == UINT_TIME) {
			int index = pointIndexTotal * appointIndex / 100;
			mDatasetMgr.getModuleInfo().playbackStartPointIndex = index;
			onSeekBar(index);
			currentIndex = index;
		}

		mDatasetMgr.notifyPointIndexChange(currentIndex, true);

		//如果当前非因同步消息触动的采样点跳变,且当前为蓝牙同步状态,则需要发送同步消息
		if (!isSync && ConfigRoutine.getInstance().isBluetoothSync(mContext)) {
			long pointTime = mDatasetMgr.getPointTime(currentIndex);
			UtilsWalktour.sendNormalMsgToBluetooth(mContext, TestTaskService.MSG_PLAYBACK_INDEXTIMES_SYNC + pointTime);
		}
	}


	/**
	 * 关闭文件回放<BR>
	 * [功能详细描述]
	 *
	 * @param port 端口号
	 * @return
	 */
	public boolean closePlayback(int port) {
		currentIndex = -1;
		return mDatasetMgr.closePlayback(port);
	}


	/**
	 * 获取当前速度
	 *
	 * @return
	 */
	public int getCurrentSpeed() {
		return currentSpeed;
	}


	/**
	 * 设置正放/倒放
	 *
	 * @param playbackDirection
	 */
	public void setPlaybackDirection(int playbackDirection) {
		this.playbackDirection = playbackDirection;
	}


	/**
	 * 获取当前播放顺序
	 *
	 * @return
	 */
	public int getPlaybackDirection() {
		return playbackDirection;
	}

	/**
	 * 设置单位
	 *
	 * @param uint
	 */
	public void setUnit(int uint) {
		this.uint = uint;
	}

	/**
	 * 切换单位
	 */
	public void switchUint() {
		uint = (uint == UINT_TIME) ? UINT_FILE : UINT_TIME;
	}
}

package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalWeChat;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.model.task.wechat.TaskWeChatModel;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

//import com.dingli.app.AppConst;
//import com.dingli.app.AppTestMain;
//import com.dingli.app.EnvConfig;
//import com.dingli.app.weixin.WeiXinConst;

/**
 * 微信测试服务类
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("SdCardPath")
@Deprecated
public class WeChatTest extends TestTaskService {
	/** 日志标识 */
	private static final String TAG = "WeChatTest";
	/** 执行结束时的结果 */
	private Map<String, String> resultMap;
	/** 执行测试类 */
//	private AppTestMain mAppTestMain;
	/** 应用操作模式 0:api 1:touch */
	private int mAppTestMode = 1;
	/** 1:do dial. 0:默认不拨打(UI及RCU不应记录，只用于微信视频拨打单独业务) */
	private int mDoDial = 0;
	/** 视频时长(UI及RCU不应记录，只用于微信视频拨打单独业务) */
	private int mVideoSeconds = 15;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(TAG, "onStartCommand");

		int startFlag = super.onStartCommand(intent, flags, startId);

		if (taskModel == null) {
			stopSelf();
		} else {
		/*	TaskWeChatModel wechatModel = (TaskWeChatModel) taskModel;
			StringBuilder cmd = new StringBuilder();
			cmd.append("app_type=").append(AppConst.APP_TYPE_ID_WEIXIN).append("\r\n");
			cmd.append("cmd=").append("CMD_APPTEST_START").append("\r\n");
			cmd.append("apptest_mode=").append(this.mAppTestMode).append("\r\n");
			cmd.append("friend_name=").append(wechatModel.getFriendName()).append("\r\n");
			cmd.append("send_text=").append(wechatModel.getSendText()).append("\r\n");
			cmd.append("picture_quality=").append(wechatModel.getSendPictureType() + 1).append("\r\n");
			cmd.append("voice_seconds=").append(wechatModel.getVoiceDuration()).append("\r\n");
			cmd.append("do_dial=").append(this.mDoDial).append("\r\n");
			cmd.append("video_seconds=").append(this.mVideoSeconds).append("\r\n");
			cmd.append("work_mode=").append(wechatModel.getOperationType() + 1).append("\r\n");
			dataTestHandler = new WeChatHandler(wechatModel);
			this.mAppTestMain = new AppTestMain(dataTestHandler, mContext, getPackageManager());
			EnvConfig.getInstance().dll_path = AppFilePathUtil.getInstance().getAppLibDirectory();
			EnvConfig.getInstance().exe_path = AppFilePathUtil.getInstance().getAppLibDirectory();
			this.mAppTestMain.Cmd(cmd.toString());*/
		}
		return startFlag;
	}

	@SuppressLint("HandlerLeak")
	private class WeChatHandler extends DataTestHandler {
		/** 发送消息总次数 */
		public long mSendMsgCount = 0;
		/** 发送消息总成功次数 */
		public long mSendMsgSuccessCount = 0;
		/** 发送消息总延时 */
		public long mSendMsgDelaySum = 0;
		/** 发送消息上行总字节数 */
		public long mSendMsgUpbytesSum = 0;
		/** 发送消息下行总字节数 */
		public long mSendMsgDownbytesSum = 0;
		/** 发送图片总次数 */
		public long mSendImgCount = 0;
		/** 发送图片总成功次数 */
		public long mSendImgSuccessCount = 0;
		/** 发送图片总延时 */
		public long mSendImgDelaySum = 0;
		/** 发送图片上行总字节数 */
		public long mSendImgUpbytesSum = 0;
		/** 发送图片下行总字节数 */
		public long mSendImgDownbytesSum = 0;
		/** 发送语音总次数 */
		public long mSendVoiceCount = 0;
		/** 发送语音总成功次数 */
		public long mSendVoiceSuccessCount = 0;
		/** 发送语音总延时 */
		public long mSendVoiceDelaySum = 0;
		/** 发送语音上行总字节数 */
		public long mSendVoiceUpbytesSum = 0;
		/** 发送语音下行总字节数 */
		public long mSendVoiceDownbytesSum = 0;
		/** 测试对象 */
		private TaskWeChatModel mTaskModel;

		private WeChatHandler(TaskWeChatModel taskModel) {
			this.mTaskModel = taskModel;
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			/*if (msg.what != AppConst.APPTEST_RESP) {
				return;
			}
			LogUtil.d(TAG, "msg.what = " + msg.what + ",msg.obj = " + msg.obj.toString());
			resultMap = AppConst.parseKeyValues(msg.obj.toString());
			String eventName = resultMap.get("event_name");
			if (eventName.equals("APPTEST_START")) {
				onAppTestStart();
			} else if (eventName.equals("ACTION_START")) {
				onActionStart();
			} else if (eventName.equals("ACTION_SUCCESS")) {
				onActionSuccess();
			} else if (eventName.equals("ACTION_FAILED")) {
				onActionFailed();
			} else if (eventName.equals("APPTEST_END")) {
				onAppTestEnd();
			}*/
		}

		/**
		 * 应用测试结束
		 * 
		 */
		private void onAppTestEnd() {
			LogUtil.i(TAG, "recv APPTEST_END");
			lastDataTime = System.currentTimeMillis() * 1000;
			int result = this.getIntValue("result");
			if (result == 0) {// 成功
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeChat_Test_Success);
				eb.writeToRcu(lastDataTime);
			} else {// 失败
				int code = this.getIntValue("code");
				String desc = resultMap.get("desc");
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeChat_Test_Failure);
				eb.addInteger(code).addStringBuffer(desc);
				eb.writeToRcu(lastDataTime);
			}
			// 统计页面
			totalResult(this);
			// 设置主进程中的firstdata状态
			setMainFirstDataState(false);
			stopProcess(TestService.RESULT_SUCCESS);
		}

		/**
		 * 操作执行失败
		 * 
		 */
		private void onActionFailed() {
			/*LogUtil.i(TAG, "recv ACTION_FAILED");
			int delay = this.getIntValue("inner_delay");
			int code = this.getIntValue("code");
			String desc = resultMap.get("desc");
			int actionType = 0;
			switch (this.getIntValue("action_type")) {
			case WeiXinConst.ACTION_TYPE_SEND_MSG:
				actionType = 1;
				break;
			case WeiXinConst.ACTION_TYPE_SEND_IMG:
				actionType = 2;
				break;
			case WeiXinConst.ACTION_TYPE_SEND_VOICE:
				actionType = 3;
				break;
			default:
				break;
			}
			// 存储事件
			EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeChat_Action_Failure);
			eb.addInteger(actionType).addInteger(delay);
			eb.addInteger(code).addStringBuffer(desc);
			eb.writeToRcu(System.currentTimeMillis() * 1000);*/
		}

		/**
		 * 操作执行成功
		 * 
		 */
		private void onActionSuccess() {
			/*LogUtil.i(TAG, "recv ACTION_SUCCESS");
			int delay = this.getIntValue("inner_delay");
			int upBytes = this.getIntValue("up_bytes");
			int downBytes = this.getIntValue("down_bytes");
			int clickDelay = this.getIntValue("click_delay");
			int actionType = 0;
			switch (this.getIntValue("action_type")) {
			case WeiXinConst.ACTION_TYPE_SEND_MSG:
				actionType = 1;
				mSendMsgSuccessCount++;
				mSendMsgDelaySum += delay;
				mSendMsgUpbytesSum += upBytes;
				mSendMsgDownbytesSum += downBytes;
				break;
			case WeiXinConst.ACTION_TYPE_SEND_IMG:
				actionType = 2;
				mSendImgSuccessCount++;
				mSendImgDelaySum += delay;
				mSendImgUpbytesSum += upBytes;
				mSendImgDownbytesSum += downBytes;
				break;
			case WeiXinConst.ACTION_TYPE_SEND_VOICE:
				actionType = 3;
				mSendVoiceSuccessCount++;
				mSendVoiceDelaySum += delay;
				mSendVoiceUpbytesSum += upBytes;
				mSendVoiceDownbytesSum += downBytes;
				break;
			default:
				break;
			}
			// 存储事件
			EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeChat_Action_Success);
			eb.addInteger(actionType).addInteger(delay);
			eb.addInteger(upBytes).addInteger(downBytes);
			eb.addInteger(clickDelay);
			eb.writeToRcu(System.currentTimeMillis() * 1000);*/
		}

		/**
		 * 获取参数整型值
		 * 
		 * @param key
		 *          参数key
		 * @return
		 */
		private int getIntValue(String key) {
			if (resultMap.containsKey(key))
				return Integer.parseInt(resultMap.get(key));
			return 0;
		}

		/**
		 * 操作执行开始
		 * 
		 */
		private void onActionStart() {
			/*LogUtil.i(TAG, "recv ACTION_START");
			String contentText = "";
			int contentSize = 0;
			int actionType = 0;
			switch (this.getIntValue("action_type")) {
			case WeiXinConst.ACTION_TYPE_SEND_MSG:
				actionType = 1;
				contentText = this.mTaskModel.getSendText();
				contentSize = 2;
				mSendMsgCount++;
				break;
			case WeiXinConst.ACTION_TYPE_SEND_IMG:
				actionType = 2;
				switch (this.mTaskModel.getSendPictureType()) {
				case 0:
					contentText = "1M";
					contentSize = 1048576;
					break;
				case 1:
					contentText = "3M";
					contentSize = 3145728;
					break;
				case 2:
					contentText = "5M";
					contentSize = 5242880;
					break;
				case 3:
					contentText = "10M";
					contentSize = 10485760;
					break;
				}
				mSendImgCount++;
				break;
			case WeiXinConst.ACTION_TYPE_SEND_VOICE:
				actionType = 3;
				contentText = "voice";
				contentSize = 2;
				mSendVoiceCount++;
				break;
			default:
				break;
			}
			// 存储事件
			EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeChat_Action_Start);
			eb.addInteger(actionType).addStringBuffer(contentText);
			eb.addInteger(contentSize);
			eb.writeToRcu(System.currentTimeMillis() * 1000);*/
		}

		/**
		 * 应用执行开始
		 * 
		 */
		private void onAppTestStart() {
			LogUtil.i(TAG, "recv APPTEST_START");
			firstDataTime = System.currentTimeMillis() * 1000;
			String friendName = this.mTaskModel.getFriendName();
			int pictureQuality = 0;
			switch (this.mTaskModel.getSendPictureType()) {
			case 0:
				pictureQuality = 1048576;
				break;
			case 1:
				pictureQuality = 3145728;
				break;
			case 2:
				pictureQuality = 5242880;
				break;
			case 3:
				pictureQuality = 10485760;
				break;
			}
			String sendText = this.mTaskModel.getSendText();
			int operationType = this.mTaskModel.getOperationType() + 1;
			// 存储事件
			EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeChat_Test_Start);
			eb.addInteger(mAppTestMode).addStringBuffer(friendName);
			eb.addInteger(pictureQuality).addStringBuffer(sendText);
			eb.addInteger(operationType);
			eb.writeToRcu(firstDataTime);
			// 设置主进程中的firstdata状态
			setMainFirstDataState(true);
		}

		@Override
		protected void prepareTest() {

		}

		@Override
		protected void drop(int dropReason, long time) {

		}

		@Override
		protected void sendCurrentRate() {
		}

		@Override
		protected void lastData(long time) {
		}

		/**
		 * 统计
		 * 
		 * @param handler
		 *          句柄
		 */
		private void totalResult(DataTestHandler handler) {
			HashMap<String, Long> map = new HashMap<String, Long>();
			map.put(TotalWeChat._sendMsgCount.name(), this.mSendMsgCount);
			map.put(TotalWeChat._sendMsgSuccessCount.name(), this.mSendMsgSuccessCount);
			map.put(TotalWeChat._sendMsgTotalDelay.name(), this.mSendMsgDelaySum);
			map.put(TotalWeChat._sendMsgTotalUpbytes.name(), this.mSendMsgUpbytesSum);
			map.put(TotalWeChat._sendMsgTotalDownbytes.name(), this.mSendMsgDownbytesSum);
			map.put(TotalWeChat._sendImgCount.name(), this.mSendImgCount);
			map.put(TotalWeChat._sendImgSuccessCount.name(), this.mSendImgSuccessCount);
			map.put(TotalWeChat._sendImgTotalDelay.name(), this.mSendImgDelaySum);
			map.put(TotalWeChat._sendImgTotalUpbytes.name(), this.mSendImgUpbytesSum);
			map.put(TotalWeChat._sendImgTotalDownbytes.name(), this.mSendImgDownbytesSum);
			map.put(TotalWeChat._sendVoiceCount.name(), this.mSendVoiceCount);
			map.put(TotalWeChat._sendVoiceSuccessCount.name(), this.mSendVoiceSuccessCount);
			map.put(TotalWeChat._sendVoiceTotalDelay.name(), this.mSendVoiceDelaySum);
			map.put(TotalWeChat._sendVoiceTotalUpbytes.name(), this.mSendVoiceUpbytesSum);
			map.put(TotalWeChat._sendVoiceTotalDownbytes.name(), this.mSendVoiceDownbytesSum);
			handler.totalResult(map);
		}

		@Override
		protected void fail(int failReason, long time) {
		}

		@Override
		protected void sendStopCommand() {
/*			StringBuilder cmd = new StringBuilder();
			cmd.append("operate=").append("0").append("\r\n");
			cmd.append("app_type=").append(AppConst.APP_TYPE_ID_WEIXIN).append("\r\n");
			cmd.append("cmd=").append("CMD_APPTEST_STOP").append("\r\n");
			cmd.append("system_quit_static=").append("1").append("\r\n");
			mAppTestMain.Cmd(cmd.toString());*/
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.WeChatTest", false);
	}

}

package com.walktour.service.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import com.dingli.service.test.ipc2msg;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalStruct.TotalWeiBo;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.parsedata.model.task.weibo.TaskWeiBoModel;
import com.walktour.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * 微博测试服务类
 * 
 * @author jianchao.wang
 * 
 */
@SuppressLint("SdCardPath")
public class WeiBoTest extends TestTaskService {
	private static final String TAG = "WeiBoTest";
	/** 执行结束时的结果 */
	private Map<String, String> result;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(TAG, "onStart");

		int startFlag = super.onStartCommand(intent, flags, startId);

		if (taskModel == null) {
			stopSelf();
		} else {
			TaskWeiBoModel weiboModel = (TaskWeiBoModel) taskModel;
			dataTestHandler = new WeiBoHandler(weiboModel);
			dataTestHandler.startTest();

		}
		return startFlag;
	}

	@SuppressLint("HandlerLeak")
	private class WeiBoHandler extends DataTestHandler {
		/** 业务ID */
		static final int WEIBO_TEST = 77;
		/* 对外发出事件 */
		/** 初始化完毕 */
		static final int WEIBO_INITED = 1;
		/** 业务正式开始测试(收到START_TEST命令后报出) */
		static final int WEIBO_START = 2;
		/** 事件参数: 业务执行开始 */
		static final int WEIBO_ACTION_START = 10;
		/** 事件参数: 业务执行成功 */
		static final int WEIBO_ACTION_SUCCESS = 11;
		/** 事件参数: 业务执行失败 */
		static final int WEIBO_ACTION_FAILURE = 12;
		/** 事件参数: 业务正常结束 (RCU:) */
		static final int WEIBO_FINISH = 15;
		/** 事件参数: 业务退出 */
		static final int WEIBO_QUIT = 17;
		/* 外部发来事件 */
		/** 事件参数: 开始业务(ID不可变) */
		static final int WEIBO_START_TEST = 1001;
		/** 事件参数: 停止业务(ID不可变) */
		static final int WEIBO_STOP_TEST = 1006;
		/** 任务 */
		private TaskWeiBoModel weiboModel;
		/** 事件开始时间 */
		private long startTime;
		/** 登录次数 */
		private long loginTimes;
		/** 登录成功次数 */
		private long loginSuccessTimes;
		/** 发送微博次数 */
		private long sentTextTimes;
		/** 发生微博成功次数 */
		private long sentTextSuccessTimes;
		/** 发生微博总时延 */
		private long sentTextTotalDelay;
		/** 发送图片次数 */
		private long sentPicTimes;
		/** 发送图片成功次数 */
		private long sentPicSuccessTimes;
		/** 发送图片总时延 */
		private long sentPicTotalDelay;
		/** 粉丝刷新微博次数 */
		private long refreshTimes;
		/** 粉丝刷新微博成功次数 */
		private long refreshSuccessTimes;
		/** 粉丝刷新微博总时延 */
		private long refreshTotalDelay;
		/** 粉丝评论次数 */
		private long commentTimes;
		/** 粉丝评论成功次数 */
		private long commentSuccessTimes;
		/** 粉丝评论总时延 */
		private long commentTotalDelay;
		/** 粉丝转发次数 */
		private long relayTimes;
		/** 粉丝转发成功次数 */
		private long relaySuccessTimes;
		/** 粉丝转发总时延 */
		private long relayTotalDelay;
		/** 粉丝查看原图次数 */
		private long readPicTimes;
		/** 粉丝查看原图次数 */
		private long readPicSuccessTimes;
		/** 粉丝查看原图总时延 */
		private long readPicTotalDelay;

		WeiBoHandler(TaskWeiBoModel weiboModel) {
			// 可执行文件，绑定3G网卡需要root// 动态库,无法进行3G网卡绑定
			super("-m weibo -z "+ AppFilePathUtil.getInstance().getAppConfigDirectory(),
					AppFilePathUtil.getInstance().getAppLibFile(useRoot?"datatests_android":"libdatatests_so.so").getAbsolutePath(),
					WEIBO_TEST, WEIBO_START_TEST, WEIBO_STOP_TEST);
			LogUtil.i(TAG, "useRoot = " + useRoot);
			this.weiboModel = weiboModel;
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			ipc2msg aMsg = (ipc2msg) msg.obj;
			if (aMsg.test_item != WEIBO_TEST) {
				return;
			}
			switch (aMsg.event_id) {
			case WEIBO_INITED:
				LogUtil.i(TAG, "recv WEIBO_INITED\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				StringBuilder event_data = new StringBuilder();
				event_data.append("local_if::").append("").append("\n");
				event_data.append("user_name::").append(weiboModel.getWeiboTestConfig().getMyAccount().getUserName()).append("\n");
				event_data.append("password::").append(weiboModel.getWeiboTestConfig().getMyAccount().getPassword()).append("\n");
				event_data.append("fans_name::").append(weiboModel.getWeiboTestConfig().getFansAccount().getUserName()).append("\n");
				event_data.append("fans_pass::").append(weiboModel.getWeiboTestConfig().getFansAccount().getPassword()).append("\n");
				event_data.append("login_timeout_s::").append(weiboModel.getWeiboTestConfig().getLoginTimeout()).append("\n");
				event_data.append("send_timeout_s::").append(weiboModel.getWeiboTestConfig().getSendTimeout()).append("\n");
				event_data.append("picture_name::").append(weiboModel.getWeiboTestConfig().getSendFile());
				LogUtil.w(TAG, event_data.toString());
				this.sendStartCommand(event_data.toString());
				break;
			case WEIBO_START:
				LogUtil.i(TAG, "recv WEIBO_START\r\n");
				firstDataTime = aMsg.getRealTime();
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeiBo_Start);
				eb.addStringBuffer(this.weiboModel.getWeiboTestConfig().getMyAccount().getUserName()).addStringBuffer(this.weiboModel.getWeiboTestConfig().getMyAccount().getPassword());
				eb.addStringBuffer(this.weiboModel.getWeiboTestConfig().getFansAccount().getUserName()).addStringBuffer(this.weiboModel.getWeiboTestConfig().getFansAccount().getPassword());
				eb.addInteger(this.weiboModel.getWeiboTestConfig().getLoginTimeout()).addInteger(this.weiboModel.getWeiboTestConfig().getSendTimeout());
				eb.addStringBuffer(this.weiboModel.getWeiboTestConfig().getSendFile());
				eb.writeToRcu(aMsg.getRealTime());
				// 设置主进程中的firstdata状态
				setMainFirstDataState(true);
				break;
			case WEIBO_ACTION_START:
				LogUtil.i(TAG, "recv WEIBO_ACTION_START\r\n");
				LogUtil.i(TAG, aMsg.data);
				this.startTime = aMsg.getRealTime();
				Map<String, String> map = this.getValue(aMsg.data);
				int actionType = Integer.parseInt(map.get("type"));
				switch (actionType) {
				case 1:
					this.loginTimes++;
					break;
				case 2:
					this.sentTextTimes++;
					break;
				case 3:
					this.sentPicTimes++;
					break;
				case 4:
					this.refreshTimes++;
					break;
				case 5:
					this.commentTimes++;
					break;
				case 6:
					this.relayTimes++;
					break;
				case 7:
					this.readPicTimes++;
					break;
				}
				// 存储事件
				eb = EventBytes.Builder(mContext, RcuEventCommand.WeiBo_Action_Start);
				eb.addInteger(actionType);
				if (map.containsKey("text"))
					eb.addStringBuffer(map.get("text"));
				if (map.containsKey("user_type"))
					eb.addInteger(Integer.parseInt(map.get("user_type")));
				if (map.containsKey("user_name"))
					eb.addStringBuffer(map.get("user_name"));
				eb.writeToRcu(aMsg.getRealTime());
				break;
			case WEIBO_ACTION_SUCCESS:
				LogUtil.i(TAG, "recv WEIBO_ACTION_SUCCESS\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				map = this.getValue(aMsg.data);
				actionType = Integer.parseInt(map.get("type"));
				long delay = (aMsg.getRealTime() - this.startTime) / 1000;
				switch (actionType) {
				case 1:
					this.loginSuccessTimes++;
					break;
				case 2:
					this.sentTextSuccessTimes++;
					this.sentTextTotalDelay += delay;
					break;
				case 3:
					this.sentPicSuccessTimes++;
					this.sentPicTotalDelay += delay;
					break;
				case 4:
					this.refreshSuccessTimes++;
					this.refreshTotalDelay += delay;
					break;
				case 5:
					this.commentSuccessTimes++;
					this.commentTotalDelay += delay;
					break;
				case 6:
					this.relaySuccessTimes++;
					this.relayTotalDelay += delay;
					break;
				case 7:
					this.readPicSuccessTimes++;
					this.readPicTotalDelay += delay;
					break;
				}
				// 存储事件
				eb = EventBytes.Builder(mContext, RcuEventCommand.WeiBo_Action_Success);
				eb.addInteger(actionType);
				if (map.containsKey("id"))
					eb.addStringBuffer(map.get("id"));
				if (map.containsKey("user_type"))
					eb.addInteger(Integer.parseInt(map.get("user_type")));
				if (map.containsKey("user_name"))
					eb.addStringBuffer(map.get("user_name"));
				eb.addInteger((int) delay);
				eb.writeToRcu(aMsg.getRealTime());
				break;
			case WEIBO_ACTION_FAILURE:
				LogUtil.i(TAG, "recv WEIBO_ACTION_FAILURE\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				result = this.getValue(aMsg.data);
				fail(aMsg.getRealTime());
				break;
			case WEIBO_FINISH:
				LogUtil.i(TAG, "recv WEIBO_FINISH\r\n");
				LogUtil.i(TAG, aMsg.data + "\r\n");
				result = this.getValue(aMsg.data);
				lastDataTime = aMsg.getRealTime();
				lastData(aMsg.getRealTime());
				break;

			case WEIBO_QUIT:
				LogUtil.i(TAG, "recv WEIBO_QUIT\r\n");
				break;
			default:
				LogUtil.i(TAG, "recv EVENT_ID:" + aMsg.event_id + "\r\n");
				break;
			}
		}

		/**
		 * 解析返回值
		 * 
		 * @param msg
		 *          返回值字符串
		 * @return 返回值映射
		 */
		private Map<String, String> getValue(String msg) {
			Map<String, String> map = new HashMap<String, String>();
			if (msg == null || msg.trim().length() == 0)
				return map;
			String[] values = msg.split("\n");
			for (String value : values) {
				if (value != null && value.indexOf("::") > 0) {
					String[] values1 = value.split("::");
					if (values1.length == 2)
						map.put(values1[0].trim(), values1[1].trim());
					else if (values1.length == 1)
						map.put(values1[0].trim(), "");
				}
			}
			return map;
		}

		@Override
		protected void prepareTest() {

		}

		/**
		 * 失败记录
		 * 
		 */
		private void fail(long time) {
			int actionType = Integer.parseInt(result.get("type"));
			int failReason = Integer.parseInt(result.get("reason"));
			String desc = result.get("desc");
			int userType = Integer.parseInt(result.get("user_type"));
			String userName = result.get("user_name");
			if (!hasFail) {
				hasFail = true;
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeiBo_Action_Failure);
				eb.addInteger(actionType);
				eb.addInteger(failReason);
				eb.addStringBuffer(desc);
				eb.addInteger(userType);
				eb.addStringBuffer(userName);
				eb.writeToRcu(time);
				// 结束本次测试
				this.stopProcess(TestService.RESULT_FAILD);
			}
		}

		@Override
		protected void drop(int dropReason, long time) {

		}

		@Override
		protected void sendCurrentRate() {
		}

		@Override
		protected void lastData(long time) {
			// fail、drop、lastData三个事件互斥
			if (!hasLastData && firstDataTime != 0 && !hasFail) {
				// 存储事件
				EventBytes eb = EventBytes.Builder(mContext, RcuEventCommand.WeiBo_Finished);
				eb.addInteger(Integer.parseInt(result.get("result")));
				eb.addInteger(Integer.parseInt(result.get("reason")));
				eb.addStringBuffer(result.get("desc"));
				eb.writeToRcu(time);
				// 统计参数
				sendTotalWeiBoPara((lastDataTime - firstDataTime) / 1000);
			}
			// 统计页面
			totalResult(this);
			// 设置主进程中的firstdata状态
			setMainFirstDataState(false);
			stopProcess(TestService.RESULT_SUCCESS);
		}

		/**
		 * 业务成功时写入weibo相关的参数统计
		 * 
		 * @param weiboJobTime
		 *          pbm的业务时间(last - firstDataTime)
		 */
		protected void sendTotalWeiBoPara(long weiboJobTime) {
			Intent weiboTotalFull = new Intent(WalkMessage.TotalByWeiBoIsFull);
			weiboTotalFull.putExtra("WeiBoJobTimes", weiboJobTime);
			sendBroadcast(weiboTotalFull);
		}

		/**
		 * 统计
		 * 
		 * @param handler
		 */
		private void totalResult(DataTestHandler handler) {
			HashMap<String, Long> map = new HashMap<String, Long>();
			map.put(TotalWeiBo._weiboLoginTimes.name(), this.loginTimes);
			map.put(TotalWeiBo._weiboLoginTimes.name(), this.loginTimes);
			map.put(TotalWeiBo._weiboLoginSuccessTimes.name(), this.loginSuccessTimes);
			map.put(TotalWeiBo._weiboSentTextTimes.name(), this.sentTextTimes);
			map.put(TotalWeiBo._weiboSentTextSuccessTimes.name(), this.sentTextSuccessTimes);
			map.put(TotalWeiBo._weiboSentTextTotalDelay.name(), this.sentTextTotalDelay);
			map.put(TotalWeiBo._weiboSentPicTimes.name(), this.sentPicTimes);
			map.put(TotalWeiBo._weiboSentPicSuccessTimes.name(), this.sentPicSuccessTimes);
			map.put(TotalWeiBo._weiboSentPicTotalDelay.name(), this.sentPicTotalDelay);
			map.put(TotalWeiBo._weiboRefreshTimes.name(), this.refreshTimes);
			map.put(TotalWeiBo._weiboRefreshSuccessTimes.name(), this.refreshSuccessTimes);
			map.put(TotalWeiBo._weiboRefreshTotalDelay.name(), this.refreshTotalDelay);
			map.put(TotalWeiBo._weiboCommentTimes.name(), this.commentTimes);
			map.put(TotalWeiBo._weiboCommentSuccessTimes.name(), this.commentSuccessTimes);
			map.put(TotalWeiBo._weiboCommentTotalDelay.name(), this.commentTotalDelay);
			map.put(TotalWeiBo._weiboRelayTimes.name(), this.relayTimes);
			map.put(TotalWeiBo._weiboRelaySuccessTimes.name(), this.relaySuccessTimes);
			map.put(TotalWeiBo._weiboRelayTotalDelay.name(), this.relayTotalDelay);
			map.put(TotalWeiBo._weiboReadPicTimes.name(), this.readPicTimes);
			map.put(TotalWeiBo._weiboReadPicSuccessTimes.name(), this.readPicSuccessTimes);
			map.put(TotalWeiBo._weiboReadPicTotalDelay.name(), this.readPicTotalDelay);
			handler.totalResult(map);
		}

		@Override
		protected void fail(int failReason, long time) {
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		// 因为业务库的原因，此服务关闭后进程未退出，在此直接杀本进程
		UtilsMethod.killProcessByPname("com.walktour.service.WeiBoTest", false);
		UtilsMethod.killProcessByPname(AppFilePathUtil.getInstance().getAppLibFile("datatests_android").getAbsolutePath(), false);
	}

}

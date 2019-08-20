package com.walktour.gui.task.parsedata.txt;

import android.annotation.SuppressLint;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigAutoTest;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 测试计划: 从Fleet服务器下载的测试计划
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("SdCardPath")
public class TestPlan {
	// 下载的测试计划文件中的参数名
	private static final String TAG = "TestPlan";// 按端口号来取设备
	/** 从Fleet服务器下载的测试计划在本地的临时路径 */
	public static final String FILE_NAME = "test_task.txt";
	private static File sTempFile;
	/** 上传GPS的超时时间 */
	public final static int UPLOAD_GPS_TIMEOUT = 75;
	/** 每天重启Walktour的时间 */
	public final static int TIME_RESTART = 1;
	/** 是否是有效测试计划 **/
	private static boolean isEffcet = false;
	/** 设定一周内的测试日:0,1,2,3,4,5,6 **/
	private String weekday = "";
	/** 是否间隔上传GPS */
	private boolean uploadInterval = false;
	private boolean uploadConstantly = false;
	/** 是否禁用GPS */
	private boolean suspendGps = false;
	/** Fleet生成的Rcu文件名 */
	private String fleetRcuName = "";

	private ArrayList<TimeRange> timeRangeList = new ArrayList<>();// 时间段
	private static TestPlan sInstance;

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static synchronized TestPlan getInstance() {
		if (sInstance == null) {
			sInstance = new TestPlan();
			sInstance.getTestPlanFromFile();
		}
		return sInstance;
	}

	/**
	 * 防止外部构造
	 */
	private TestPlan() {
		sTempFile = AppFilePathUtil.getInstance().getAppConfigFile(FILE_NAME);
	}

	/**
	 * 清除测试计划
	 */
	private void clearTestPlan() {
		isEffcet = false;
		weekday = "";
		fleetRcuName = "";
		uploadInterval = false;
		uploadConstantly = new ConfigAutoTest().isLocationOn();
		suspendGps = false;
		timeRangeList.clear();
	}

	/**
	 * @return 是否存在有效的测试任务
	 * 
	 */
	public boolean getTestPlanFromFile() {
		clearTestPlan();
		FileInputStream inStream = null;
		try {
			if (!sTempFile.exists()) {
				LogUtil.w(TAG, "--->test plan is not found");
				return false;
			}

			// 获取所有测试任务,tasks对应的是test_task.xml中<TestTasks>以下的所有内容
			TestTasks tasks = new TestTasks(sTempFile, false);
			if (tasks.getTestTasks().size() == 0) {
				LogUtil.w(TAG, "--->no task in test plan");
				return false;
			}
			// 获取测试时间段
			inStream = new FileInputStream(sTempFile);
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(inStream, "gbk"));
			String line;
			// 文件从头到尾每行只读一次
			while ((line = bfReader.readLine()) != null) {

				if (line.startsWith("[ConfigInfo]")) {
					while ((line = bfReader.readLine()) != null) {
						if (line.startsWith("ConfigTag")) {
							this.fleetRcuName = line.split("=")[1];
							break;
						}
					}
				}

				// 是否禁用GPS
				if (line != null && line.startsWith("Suspend")) {
					try {
						this.suspendGps = line.split("=")[1].equals("1");
					} catch (Exception e) {
						e.printStackTrace();
						this.suspendGps = false;
					}
				}

				// 是否间隔上传GPS
				if (line != null && line.startsWith("UploadInterval")) {
					try {

						this.uploadInterval = line.split("=")[1].equals("1");
					} catch (Exception e) {
						e.printStackTrace();
						this.uploadInterval = false;
					}
				}

				// 先找出对应的设备种类，再逐行解析
				if (line != null && line.startsWith("<devices>")) {
					// 设定一周内的测试日
					while ((line = bfReader.readLine()) != null) {
						if (line.startsWith("weekday=")) {
							this.weekday = line.split("=")[1];
							break;
						}
					}
					timeRangeList.clear();
					// 读取时间段,并为每个时间段添加任务列表
					while ((line = bfReader.readLine()) != null) {
						if (line.startsWith("start_time=")) {
							TimeRange timeRange = new TimeRange();

							// 开始时间(需要加上时区偏移值)
							String str = line.split("=")[1];
							String hour = str.split(":")[0];
							String minute = str.split(":")[1];
							String second = str.split(":")[2];
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
							calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
							calendar.set(Calendar.SECOND, Integer.valueOf(second));
							calendar.set(Calendar.MILLISECOND, 000);
							// fleet下载的时间是国际时间，需要加上本时区的偏移
							long time = calendar.getTimeInMillis() + TimeZone.getDefault().getRawOffset();
							String startTime = UtilsMethod.getSimpleDateFormat1(time);
							timeRange.setStartTime(startTime);

							// 持续时间
							line = bfReader.readLine();
							while (!line.startsWith("continuous_time")) {
								line = bfReader.readLine();
							}

							timeRange.setContinuousTimeInMillon(line.split("=")[1]);

							// 2011.11.23 平台中增加了buildonlyoneppp属性，
							// 找到这个时间段的TestTaskCount
							boolean hasTaskInRange = false;
							while ((line = bfReader.readLine()) != null) {
								if (line.startsWith("TestTaskCount")) {
									hasTaskInRange = true;
									break;
								}
								if (line.startsWith("}")) {
									break;
								}
							}

							if (line != null && hasTaskInRange) {
								// 任务数总数
								int testTaskCount = Integer.valueOf(line.split("=")[1]);
								LogUtil.w(TAG, "testCount:" + testTaskCount);

								// 读取所有测试任务的引用
								ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();
								for (int i = 0; i < testTaskCount; i++) {
									// 测试任务类：未指定测试类型
									line = bfReader.readLine();
									while (!line.startsWith("TestTask" + i)) { // 先取任务TestTask标记
										line = bfReader.readLine();
									}

									while (line.split("=")[0].equals("TestTask" + i)) {
										String tag = "[" + line.split("=")[1] + "]";// 对应test_task.txt中的TestTask(i)=****;
										while (!line.startsWith("TestTask" + i + "Repeat")) { // 通过名字匹配重复次数
											line = bfReader.readLine();
										}
										int repeat = Integer.parseInt(line.split("=")[1]);// 记下该测试任务的重复次数
										LogUtil.w(tag, "---tag:" + tag + ",repeat:" + repeat);
										try {
											/*
											 * 克隆测试任务,因为测试计划文件中，
											 * 一个测试任务是被多个时间段引用的，
											 * 不有直接修改已有的任务列表tasks中的对象
											 */
											if (tasks.getTaskModelByTag(tag) != null) {
												TaskModel model = new TaskModel();
												// 对象克隆
												model = (TaskModel) tasks.getTaskModelByTag(tag).deepClone();
												// 不是上传任务的添加到测试时间段中
												if (model.getTaskType()
														.equals(WalkStruct.TaskType.WalktourUpload.name())) {
													timeRange.setHasUpload(true);
												} else {
													model.setRepeat(repeat);
													taskList.add(model);
													timeRange.setHasTestTask(true);
												}
											} else {
												LogUtil.w(tag, "---" + tag + " is null");
											}
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
								}
								timeRange.setTaskList(taskList);

								timeRangeList.add(timeRange);

							}

						}

						if (line != null && line.startsWith("<TestTasks>")) {
							break;
						}
					}

				}
			}

			// 当两个时间段是相连的，强行把下一个时间段的开始时间延后2分钟
			// 显示时间段到log
			for (int i = 0; i < timeRangeList.size(); i++) {
				TimeRange timeRange = timeRangeList.get(i);
				if (i + 1 <= timeRangeList.size() - 1) {
					TimeRange nextTimeRange = timeRangeList.get(i + 1);
					long endTime = timeRange.getStartTimeInMillis() + timeRange.getContinuousTimeInMillis();
					long startTime = nextTimeRange.getStartTimeInMillis();
					if (startTime - endTime < 2 * UtilsMethod.Minute) {
						startTime += 2 * UtilsMethod.Minute;
						nextTimeRange.setStartTime(UtilsMethod.getSimpleDateFormat1(startTime));
					}
				}
				// 打印
				// ArrayList<TaskModel> modelList = timeRange.getTaskList();
				// LogUtil.w("TestPlan", "---timeRange" + i + ",start:" +
				// timeRange.getStartTime() + ",continus:"
				// + timeRange.getContinuousTime() + ",modelList size:" +
				// modelList.size());
				// for (int m = 0; m < modelList.size(); m++) {
				// TaskModel taskModel = modelList.get(m);
				// LogUtil.i("TestPlan", "---repeat:" +
				// taskModel.getRepeat());
				// LogUtil.i("TestPlan", "---tag:" + taskModel.getTag());
				// }
			}

			// 如果时间段的数据为0
			LogUtil.d("TestPlan", "---timeRangeList size:" + timeRangeList.size());
			if (timeRangeList.size() == 0) {
				isEffcet = false;
			} else {
				isEffcet = true;
			}
			return isEffcet;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
					inStream = null;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * @return 设备下的所有测试任务
	 */
	public ArrayList<TimeRange> getTimeRangeList() {
		return this.timeRangeList;
	}
 

	/**
	 * @return stopTime节点距离当前时间最近的一个时间段
	 */
	public TimeRange getNearestTimeRange() {
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		ArrayList<TimeRange> timeRangeList = getTimeRangeList();
		for (int i = 0; i < timeRangeList.size(); i++) {
			TimeRange timeRange = timeRangeList.get(i);
			if (now < (timeRange.getStartTimeInMillis() + timeRange.getContinuousTimeInMillis())) {
				return timeRange;
			}
		}

		if (isEffcet) {
			return timeRangeList.get(0);
		}
		return null;
	}

	/**
	 * @return startTime节点距离当前时间最近的下一个时间段
	 */
	public TimeRange getNextStartTimeRange() {
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		ArrayList<TimeRange> timeRangeList = getTimeRangeList();
		for (int i = 0; i < timeRangeList.size(); i++) {
			TimeRange timeRange = timeRangeList.get(i);
			if (now < timeRange.getStartTimeInMillis()) {
				return timeRange;
			}
		}

		if (isEffcet) {
			return timeRangeList.get(0);
		}
		return null;
	}

	/**
	 * @return startTime节点距离当前时间最近的上一个时间段
	 */
	public TimeRange getLastTimeRange() {
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		ArrayList<TimeRange> timeRangeList = getTimeRangeList();
		for (int i = timeRangeList.size() - 1; i >= 0; i--) {
			TimeRange timeRange = timeRangeList.get(i);
			if (timeRange.getStartTimeInMillis() < now) {
				return timeRange;
			}
		}

		if (isEffcet) {
			return timeRangeList.get(0);
		}
		return null;
	}

	/** Walktour下次重启的时间 */
	public long getRestartTimeInMillis() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 000);
		return calendar.getTimeInMillis() + 24 * UtilsMethod.Hour;
	}

	/** 是否禁用GPS */
	public synchronized boolean isSuspendGps() {
		return suspendGps;
	}

	/** 是否禁用GPS */
	public synchronized void setSuspendGps(boolean suspendGps) {
		this.suspendGps = suspendGps;
	}

	/**
	 * 是否在测试间隔上传GPS
	 */
	public synchronized boolean isUploadInterval() {
		return uploadInterval;
	}

	/** 设置测试间隔是否上传GPS */
	public synchronized void setUploadInterval(boolean uploadInterval) {
		this.uploadInterval = uploadInterval;
	}

	/** 当前是否连续上传GPS */
	public synchronized boolean isUploadConstantly() {
		return uploadConstantly;
	}

	/** 设置当前是否连续上传GPS */
	public synchronized void setUploadConstantly(boolean uploadConstantly) {
		this.uploadConstantly = uploadConstantly;
	}

	/**
	 * 设置当前rcu的命名
	 */
	public synchronized String getFleetRcuName() {
		return fleetRcuName;
	}

	/**
	 * 时间段，对应是是平台中测试计划里的TimeRange 包含开始时间，持续时间和测试任务列表
	 */
	public class TimeRange {
		private String startTime = "";
		private String continuousTime = "";
		private boolean hasUpload = false;
		private boolean hasTestTask = false;

		/** 是否有测试任务 */
		public synchronized boolean hasTestTask() {
			return hasTestTask;
		}

		/** 是否有测试任务 */
		public synchronized void setHasTestTask(boolean hasTestTask) {
			this.hasTestTask = hasTestTask;
		}

		/** 是否在此时间段测试完成后上传 */
		public synchronized boolean hasUpload() {
			return hasUpload;
		}

		/** 是否在此时间段测试完成后上传 */
		public synchronized void setHasUpload(boolean hasUpload) {
			this.hasUpload = hasUpload;
		}

		private ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();

		public TimeRange() {

		}

		/**
		 * 开始时间
		 */
		public String getStartTime() {
			return startTime;
		}

		/**
		 * @return 此时间段的本日开始时间
		 */
		public long getStartTimeInMillis() {
			Calendar calendar = Calendar.getInstance();
			String hour = this.startTime.split(":")[0];
			String minute = this.startTime.split(":")[1];
			calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
			calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 000);
			return calendar.getTimeInMillis();
		}

		/**
		 * @return 此时间段的持续时间
		 */
		public long getContinuousTimeInMillis() {
			String hour = this.continuousTime.split(":")[0];
			String minute = this.continuousTime.split(":")[1];
			return UtilsMethod.Hour * Integer.valueOf(hour) + UtilsMethod.Minute * Integer.valueOf(minute);
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		/**
		 * 持续时间
		 */
		public String getContinuousTime() {
			return continuousTime;
		}

		public void setContinuousTimeInMillon(String continuousTime) {
			this.continuousTime = continuousTime;
		}

		public void setContinuousTime(long continuousTime) {
			long hour = continuousTime / UtilsMethod.Hour;
			long minute = (continuousTime / UtilsMethod.Minute) % 60;
			String strHour = hour >= 10 ? String.valueOf(hour) : "0" + String.valueOf(hour);
			String strMin = minute >= 10 ? String.valueOf(minute) : "0" + String.valueOf(minute);
			this.continuousTime = strHour + ":" + strMin;
		}

		/**
		 * 测试列表
		 */
		public ArrayList<TaskModel> getTaskList() {
			return taskList;
		}

		public void setTaskList(ArrayList<TaskModel> taskList) {
			this.taskList = taskList;
		}
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	/**
	 * 测试计划是否有效(未下载过或者下载的测试计划中无测试任务时)
	 */
	public synchronized boolean isEffcet() {
		return isEffcet;
	}

}
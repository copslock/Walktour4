package com.walktour.service.phoneinfo.logcat;

import android.content.Context;

import com.walktour.control.config.Deviceinfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;

/**
 * 日志监听类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseLogMonitor extends LogSubject implements IMonitor {

	protected Context context;
	protected Logger logger;

	protected LogFilter filter;

	protected Boolean isMonitoring = false;

	protected String type = "";
	protected String CMD = "";

	protected EventListener eventListener = null;

	private enum RootState {
		Normal, Enable, Disable
	}

	private RootState rootState = RootState.Normal;

	public BaseLogMonitor(Context context, EventListener eventListener) {
		this.context = context;
		this.eventListener = eventListener;
		init();
	}

	public abstract void init();

	protected Runnable logRunnable = new Runnable() {
		@Override
		public void run() {
			Process process;
			InputStream inputstream;
			BufferedReader bufferedreader;
			long nowTime = Calendar.getInstance().getTimeInMillis(); // System.currentTimeMillis();
			boolean filtercontent = filter.needFilte();

			while (isMonitoring) {
				try {
					if (isRootSystem() == true) {
						process = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
						DataOutputStream dos = new DataOutputStream(process.getOutputStream());
						dos.writeBytes(CMD + "\n");
						dos.flush();
					} else {
						process = Runtime.getRuntime().exec(CMD);
					}

					inputstream = process.getInputStream();
					InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
					bufferedreader = new BufferedReader(inputstreamreader);
					String str = null;
					while (isMonitoring) {
						if ((str = bufferedreader.readLine()) == null) {
							Thread.sleep(1000);
							continue;
						}
						if (filtercontent) {
							LogcatBean bean = new LogcatBean();
							if (buildLogBean(bean, str) != null) {
								// System.out.println(nowTime+"==wsnnn=="+bean.getLogTime()+"
								// "+str);
								if (bean.getLogTime() >= nowTime) {
									// if(str.contains("[CDMAConn]")){

									// }
									notifyObservers(bean);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
				}
			}
		}
	};

	protected LogcatBean buildLogBean(LogcatBean bean, String data) {
		// LogcatBean bean = new LogcatBean();
		try {
			String time = data.substring(0, 18);
			data = data.substring(18 + 1);
			bean.setLogTime(getTime(time));
			if (data.contains("/")) {
				String level = data.substring(0, data.indexOf("/"));
				data = data.substring(data.indexOf("/") + 1);
				bean.setLevel(level);
			}
			if (data.contains(":")) {
				String fields = data.substring(0, data.indexOf(":"));
				data = data.substring(data.indexOf(":") + 1);

				if (fields.contains("(")) {
					bean.setAppName(fields.substring(0, fields.indexOf("(")).trim());
					bean.setPid(Long.parseLong(fields.substring(fields.indexOf("(") + 1, fields.indexOf(")")).trim()));
				}
			}
			bean.setMsg(data);
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getTime
	 * 
	 * @param timeStr
	 * @return
	 */
	protected long getTime(String timeStr) {
		String[] dateArray = timeStr.split(" ");
		long logTime = 0;

		try {
			logTime = (LogInfoBean.logDateFormat
					.parse(Calendar.getInstance().get(Calendar.YEAR) + "-" + dateArray[0] + " " + dateArray[1])).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return logTime;
	}

	@Override
	public void start() {
		if (!isMonitoring) {
			isMonitoring = true;
			new Thread(logRunnable).start();
		}
	}

	@Override
	public void stop() {
		isMonitoring = false;
	}

	public boolean isRootSystem() {
		if (rootState == RootState.Enable) {
			return true;
		} else if (rootState == RootState.Disable) {
			return false;
		}

		File file = null;
		final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/" };
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				file = new File(kSuSearchPaths[i] + Deviceinfo.getInstance().getSuOrShCommand());
				if (file.exists()) {
					rootState = RootState.Enable;
					return true;
				}
			}
		} catch (Exception e) {
		}

		rootState = RootState.Disable;
		return false;
	}
}

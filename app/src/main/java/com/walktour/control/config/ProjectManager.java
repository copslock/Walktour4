package com.walktour.control.config;

import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyFileWriter;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TestPlanConfig;
import com.walktour.model.UrlModel;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * 工程管理类 工程的保存、导入功能
 */
public class ProjectManager {
	private static final String TAG = "ProjectManager";
	// 配置文件的路径，全部都是相对于/data/data/myapp/files/
	/** 测试任务的路径,/data/data/myapp/files/ */
	// public static final String PATH_TASK_LIST = "/tasklist.xml"; // 主任务文件
	public static final String PATH_TASK_LIST_URL = "/config/config_url.xml"; // 引用的URL列表
	public static final String PATH_TASK_LIST_MUILT = "/multitasklist.xml"; // 并发引用的子任务

	/** 地图图表参数,/data/data/myapp/files/ */
	public static final String PATH_MAP_PARA = "/config/config_map_chart.xml";

	/** 系统设置,/data/data/myapp/files/ */
	public static final String PATH_SYS_SETTING = "/config/config_setting.xml";

	/** 告警设置路径,/data/data/myapp/files/ */
	public static final String PATH_ALARM = "/config/config_alarm.xml";

	/** FTP设置,/data/data/myapp/files/ */
	public static final String PATH_FTP = "/config/config_ftp.xml";

	/** 保存工程的目录，相对于/sdcard/walktour/目录 */
	public static final String PATH_PROJECT_DIR = "/project";

	/** 加载工程文件的结果：文件不存在 */
	public static final int LOAD_RESULT_FILE_NOT_EXIST = -1;

	/** 加载工程文件的结果：出现错误 */
	public static final int LOAD_RESULT_ERROR = 0;

	/** 加载工程文件的结果：成功加载 */
	public static final int LOAD_RESULT_SUCCESS = 1;

	/** 导入的版本号与当前不一致 */
	public static final int LOAD_RESULT_VERSIONEROR = 2;

	public ConfigFtp configFtp = new ConfigFtp();

	private Context mContext;
	/** 参数存储 */
//	private SharedPreferences preferences;
	public ProjectManager(Context context) {
		this.mContext = context;
//		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	/**
	 * 从xml配置文件中截取部分内容
	 * 
	 * @param filePath
	 *            要读取文件的绝对路径
	 * @param begin
	 *            要读取的开头一行
	 * @param end
	 *            要截取的结尾一行
	 * @param needTag
	 *            是否需要返回头和尾
	 */
	private String getStringByTagName(String filePath, String begin, String end, boolean needTag) {
		StringBuffer result = new StringBuffer();
		result.append("\n\r");
		FileInputStream inStream = null;
		BufferedReader bfReader = null;
		try {
			File file = new File(filePath);

			if (!file.exists()) {
				LogUtil.w(TAG, "--->test plan is not found");
			}

			inStream = new FileInputStream(file);
			bfReader = new BufferedReader(new InputStreamReader(inStream));

			String line;
			while ((line = bfReader.readLine()) != null) {
				// 先找到begin
				if (line.contains(begin)) {
					if (needTag) {
						result.append(line.substring(line.indexOf(begin), line.length()) + "\n\r");
					}
					// tag的begin和end在同一行
					if (line.contains(end)) {
						break;
					}
					// 读取begin到end之间的内容
					while ((line = bfReader.readLine()) != null) {
						if (line.contains(end)) {
							if (needTag) {
								result.append(line + "\n\r");
							}
							break;
						} else if (line.trim().length() > 0) {
							result.append(line + "\n\r");
						}
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
					inStream = null;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			if (bfReader != null) {
				try {
					bfReader.close();
					bfReader = null;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return result.toString();
	}

	/** 从文件获取测试任务设置 */
	private String getTaskList(String filePath) {
		return getStringByTagName(filePath, "<TestPlanConfig>", "</TestPlanConfig>", true);
	}

	/***
	 * 获取版本信息
	 * 
	 * @param filePath
	 * @return
	 */
	private int getTaskVersion(String filePath) {
		String ver = getStringByTagName(filePath, "<TestPlanVersion>", "</TestPlanVersion>", true).replace("\n\r", "");
		try {
			if (ver.indexOf("<TestPlanVersion>") >= 0 && ver.indexOf("</TestPlanVersion>") > 0) {
				ver = ver.substring(ver.indexOf("<TestPlanVersion>"), ver.indexOf("</TestPlanVersion>"))
						.replace("<TestPlanVersion>", "");
				int version = Integer.parseInt(ver);
				return version;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return LOAD_RESULT_ERROR;
	}

//	private int getModelVersion(String taskModel) {
//		if (taskModel.indexOf("<TaskModelVersion>") > 0 && taskModel.indexOf("</TaskModelVersion>") > 0) {
//			return Integer.parseInt(taskModel.substring(taskModel.indexOf("<TaskModelVersion>") + 18,
//					taskModel.indexOf("</TaskModelVersion>")));
//		}
//		return 1;
//	}

	/** 从文件获取测试任务引用 的URL */
	private String exportTaskUrlList() {

		ConfigUrl urlList = new ConfigUrl();
		ArrayList<UrlModel> modelList = urlList.getAllUrl();
		StringBuffer sb = new StringBuffer();
		sb.append("<urls>\n");
		if (modelList != null) {
			for (UrlModel u : modelList) {
				sb.append("\t<url name=" + "\"" + u.getName() + "\"" + " enble=\"" + u.getEnable() + "\"" + "/>\n");
			}
		}
		sb.append("</urls>\n");

		return sb.toString();
	}

	private String inportTaskUrlList(String filePath) {
		return getStringByTagName(filePath, "<urls>", "</urls>", true);
	}

	/** 从文件获取地图参数设置 */
	private String getMapPar(String filePath) {
		return getStringByTagName(filePath, "<parameters>", "</parameters>", true);
	}

	/** 从文件获取系统设置 */
	private String getSystemConfig(String filePath) {
		return getStringByTagName(filePath, "<config>", "</config>", true);
	}

	/** 从文件获取告警设置 */
	private String getAlarmSetting(String filePath) {
		return getStringByTagName(filePath, "<alarm>", "</alarm>", true);
	}

	/** 从文件获取Ftp设置 */
	private String getFtp(String filePath) {
		return getStringByTagName(filePath, "<ftps>", "</ftps>", true);
	}

	// /**从文件获取map值*/
	// private String getPreferences( String filePath){
	// return getStringByTagName( filePath,"<map>","</map>",true);
	// }

	/**
	 * 保存工程
	 * 
	 * @param name
	 *            工程名
	 */
	public void saveProject(String name) {
		// 先确保project目录存在
		try {
			String filePath = AppFilePathUtil.getInstance().createSDCardBaseDirectory("project");

			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
			// 保存测试任务列表
			String taskList = getTaskList(AppFilePathUtil.getInstance().getAppFilesDirectory()+TaskListDispose.FILENAME_MANUAL);
			sb.append(taskList);

			// 保存URL列表
			sb.append(exportTaskUrlList());

			// 保存地图和参数设置
			String mapPara = getMapPar(mContext.getFilesDir() + PATH_MAP_PARA);
			sb.append(mapPara);

			// 保存系统设置
			String sysSetting = getSystemConfig(mContext.getFilesDir() + PATH_SYS_SETTING);
			sb.append(sysSetting);

			// 保存告警设置
			String alarmSetting = getAlarmSetting(mContext.getFilesDir() + PATH_ALARM);
			sb.append(alarmSetting);

			// 保存Ftp设置
			String ftpSetting = getFtp(mContext.getFilesDir() + PATH_FTP);
			sb.append(ftpSetting);

			// 保存其它SharePreferecnce属性
			String preferenceDir = mContext.getFilesDir().getParent() + "/shared_prefs/";
			String prefer1 = preferenceDir + mContext.getPackageName() + ".xml";
			sb.append(getStringByTagName(prefer1, "<map>", "</map>", true));

			String prefer2 = preferenceDir + mContext.getPackageName() + "_preferences.xml";
			sb.append("<preferences>\n\r");
			sb.append(getStringByTagName(prefer2, "<map>", "</map>", false) + "\n\r");
			sb.append("</preferences>\n\r");

			String preferAlert = preferenceDir + mContext.getPackageName() + "_alarm.xml";
			sb.append("<alert>\n\r");
			sb.append(getStringByTagName(preferAlert, "<map>", "</map>", false) + "\n\r");
			sb.append("</alert>\n\r");

			// 保存自定义事件
			File customFile = AppFilePathUtil.getInstance().createSDCardBaseFile(CustomEventFactory.CUSTOM_FILE_NAME);
			String customEvents = getStringByTagName(customFile.getAbsolutePath(), "<custom>", "</custom>", true);
			sb.append(customEvents + "\n\r");
			MyFileWriter.write(filePath + "/" + name + ".xml", sb.toString());
		} catch (Exception e) {
			LogUtil.w(TAG, e.toString());
		}

	}

	/**
	 * 保存测试任务
	 * 
	 * @param name
	 *            任务列表名
	 * @param isGroup
	 *            是否是任务组界面
	 */
	public void saveTask(String name,boolean isGroup) {
		// 先确保task目录存在
		try {
//			boolean isGroup=preferences.getBoolean(WalktourConst.SYS_SETTING_taskgroup_control, true);
			String directory;
			if(isGroup){
				directory=mContext.getString(R.string.path_group);
			}else{
				directory=mContext.getString(R.string.path_task);
			}
			// 将文件导出到指定目录下面
			TaskListDispose.getInstance().writeXml(AppFilePathUtil.getInstance().createSDCardBaseFile(directory,name + ".xml").getAbsolutePath());
		} catch (Exception e) {
			LogUtil.w(TAG, e.toString());
		}
	}

	/**
	 * 保存测试任务
	 * 
	 * @param name
	 *            任务列表名
	 */
	public void saveTask(TestPlanConfig testPlanConfig, String name) {
		// 先确保task目录存在
		try {
			// 将文件导出到指定目录下面
			TaskListDispose.getInstance().writeXml(testPlanConfig, AppFilePathUtil.getInstance().createSDCardBaseFile(mContext.getString(R.string.path_task), name + ".xml").getAbsolutePath());
		} catch (Exception e) {
			LogUtil.w(TAG, e.toString());
		}
	}

	/**
	 * 加载工程到配置文件
	 * 
	 * @param projectFile
	 *            要加载的工程文件
	 * @return
	 */
	public int loadProject(File projectFile) {
		if (projectFile.exists() && projectFile.isFile()) {
			String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r";

			// 加载测试任务列表
			int proVersion = getTaskVersion(projectFile.getAbsolutePath());
			if (proVersion == TaskListDispose.TASK_MODEL_VERSION) {
				// 测试任务
				String taskList = xmlHead + getTaskList(projectFile.getAbsolutePath());
				MyFileWriter.write(AppFilePathUtil.getInstance().getAppFilesDirectory()+TaskListDispose.FILENAME_MANUAL, taskList);

				TaskListDispose.getInstance().reloadFromXML();

				String url = xmlHead + inportTaskUrlList(projectFile.getAbsolutePath());
				MyFileWriter.write(mContext.getFilesDir() + PATH_TASK_LIST_URL, url);

				// 加载地图和参数设置
				String mapPara = getMapPar(projectFile.getAbsolutePath());
				if (!mapPara.equals("\n\r")) {
					mapPara = xmlHead + mapPara;
					MyFileWriter.write(mContext.getFilesDir() + PATH_MAP_PARA, mapPara);
				}

				// 加载系统设置
				String sysSetting = getSystemConfig(projectFile.getAbsolutePath());
				if (!sysSetting.equals("\n\r")) {
					sysSetting = xmlHead + sysSetting;
					MyFileWriter.write(mContext.getFilesDir() + PATH_SYS_SETTING, sysSetting);
				}

				// 加载告警设置
				String alarmSetting = getAlarmSetting(projectFile.getAbsolutePath());
				if (!alarmSetting.equals("\n\r")) {
					alarmSetting = xmlHead + alarmSetting;
					MyFileWriter.write(mContext.getFilesDir() + PATH_ALARM, alarmSetting);
				}

				// 加载Ftp设置
				String ftpSetting = getFtp(projectFile.getAbsolutePath());
				if (!ftpSetting.equals("\n\r")) {
					ftpSetting = xmlHead + ftpSetting;
					MyFileWriter.write(mContext.getFilesDir() + PATH_FTP, ftpSetting);
				}

				// 加载引用SharedPreferences的设置
				String pre = getStringByTagName(projectFile.getAbsolutePath(), "<map>", "</map>", true);
				if (!pre.equals("\n\r")) {
					pre = xmlHead + pre;
					MyFileWriter.write(
							mContext.getFilesDir().getParent() + "/shared_prefs/" + mContext.getPackageName() + ".xml",
							pre);
				}

				String preference = getStringByTagName(projectFile.getAbsolutePath(), "<preferences>", "</preferences>",
						false);
				if (!preference.equals("\n\r")) {
					preference = xmlHead + "<map>\n\r" + preference + "\n\r</map>\n\r";
					MyFileWriter.write(mContext.getFilesDir().getParent() + "/shared_prefs/" + mContext.getPackageName()
							+ "_preferences.xml", preference);
				}

				String alert = getStringByTagName(projectFile.getAbsolutePath(), "<alert>", "</alert>", false);
				if (!alert.equals("\n\r")) {
					alert = xmlHead + "<map>\n\r" + alert + "\n\r</map>\n\r";
					MyFileWriter.write(mContext.getFilesDir().getParent() + "/shared_prefs/" + mContext.getPackageName()
							+ "_alarm.xml", alert);
				}

				// 加载自定义事件
				String customs = getStringByTagName(projectFile.getAbsolutePath(), "<custom>", "</custom>", true)
						+ "\n\r";
				if (!customs.equals("\n\r\n\r")) {
					File customFile = AppFilePathUtil.getInstance().createSDCardBaseFile(CustomEventFactory.CUSTOM_FILE_NAME);
					MyFileWriter.write(customFile.getAbsolutePath(), customs);
				}

				// 重新加载一些设置
				ServerManager.resetInstance(mContext);
				TaskListDispose.getInstance().reloadFromXML();
				AlertManager.getInstance(mContext).initValues();
				CustomEventFactory.getInstance().initCustomEvent();

				// 重新加载图表和地图设置
				ParameterSetting.getInstance().initialParameter();
				return LOAD_RESULT_SUCCESS;
			}
			return LOAD_RESULT_VERSIONEROR;
		}
		return LOAD_RESULT_FILE_NOT_EXIST;
	}

	/**
	 * 加载测试任务列表到配置文件
	 * 
	 * @param taskFile
	 *            要加载的任务文件
	 * @param loadModel
	 *            加载模式 0-覆盖加载 1-追加加载
	 * @return
	 */
	public int loadTask(File taskFile, int loadModel) {
		if (taskFile.exists() && taskFile.isFile()) {
			try {
				// 加载测试任务列表
				int proVersion = getTaskVersion(taskFile.getAbsolutePath());
				if (proVersion == TaskListDispose.TASK_MODEL_VERSION) {
					TaskListDispose.getInstance().parseXml(taskFile.getAbsolutePath(), loadModel);
					TaskListDispose.getInstance().writeXml();
					return LOAD_RESULT_SUCCESS;
				}
				return LOAD_RESULT_VERSIONEROR;
			} catch (Exception ex) {
				ex.printStackTrace();
				return LOAD_RESULT_ERROR;
			}
		}
		return LOAD_RESULT_FILE_NOT_EXIST;
	}

	public TestPlanConfig loadTask(File taskFile) {
		TestPlanConfig config = null;
		if (taskFile.exists() && taskFile.isFile()) {
			try {
				config = TaskListDispose.getInstance().parseXml(taskFile.getAbsolutePath(),0);
				if (null == config || !config.getTestPlanInfo().getTestPlanVersion()
						.equals(TaskListDispose.TASK_MODEL_VERSION + ""))
					config = null;
				;
			} catch (Exception ex) {
				config = null;
				ex.printStackTrace();
			}
			return config;
		}
		return config;
	}

	/**
	 * String 转换为dom
	 * 
	 * @param xmlstr
	 * @return
	 */
	public Document stringToDom(String xmlstr) {

		StringReader sr = new StringReader(xmlstr);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document dom = null;

		try {

			builder = factory.newDocumentBuilder();
			dom = builder.parse(is);

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
		}

		return dom;

	}

	/**
	 * 返回工程文件列表
	 */
	public ArrayList<File> getProjectList() {
		ArrayList<File> result = new ArrayList<File>();
		try {
			File dirProject = AppFilePathUtil.getInstance().getSDCardBaseFile("project");
			File[] projects = dirProject.listFiles();
			for (File f : projects) {
				if (f.getName().endsWith(".xml")) {
					result.add(f);
				}
			}
		} catch (Exception e) {
			LogUtil.w(TAG, e.toString());
		}

		return result;
	}

	/**
	 * @param isGroup 是否是任务组模式
	 * 返回任务文件列表
	 */
	public ArrayList<File> getTaskFileList(boolean isGroup) {
		ArrayList<File> result = new ArrayList<File>();
		try {
			String directory;
			if (isGroup) {
				directory = mContext.getString(R.string.path_group);
			} else {
				directory = mContext.getString(R.string.path_task);
			}
			File dirProject = AppFilePathUtil.getInstance().getSDCardBaseFile(directory);
			File[] projects = dirProject.listFiles();
			for (File f : projects) {
				if (f.getName().endsWith(".xml")) {
					result.add(f);
				}
			}
		} catch (Exception e) {
			LogUtil.w(TAG, e.toString());
		}

		return result;
	}

}
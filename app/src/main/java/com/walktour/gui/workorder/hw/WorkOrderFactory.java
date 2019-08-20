package com.walktour.gui.workorder.hw;

import android.content.Context;
import android.util.Xml;

import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ConfigUrl;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.idle.TaskEmptyModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.workorder.hw.model.TestPlan;
import com.walktour.gui.workorder.hw.model.TestSchema;
import com.walktour.gui.workorder.hw.model.TestTaskGroup;
import com.walktour.model.FtpServerModel;
import com.walktour.model.UrlModel;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 华为工单模块工厂类
 *
 * @author jianchao.wang
 */
public class WorkOrderFactory {
	/**
	 * 日志标识
	 */
	private static final String TAG = "WorkOrderFactory";
	/**
	 * 唯一实例
	 */
	private static WorkOrderFactory sInstance = null;
	/**
	 * 文件名称
	 */
	private static String fileName;
	/**
	 * xml解析类
	 */
	private XmlPullParser xmlParser;
	/**
	 * 计划对象列表
	 */
	private List<TestSchema> schemaList = new ArrayList<>();

	private WorkOrderFactory() {
		this.init();
	}

	/**
	 * 返回唯一实例
	 *
	 * @param context 上下文
	 * @return 唯一实例
	 */
	public static WorkOrderFactory getInstance(Context context) {
		if (sInstance == null) {
			fileName = AppFilePathUtil.getInstance().getSDCardBaseFile("workorder","workorder.xml").getAbsolutePath();
			AppFilePathUtil.getInstance().createSDCardBaseDirectory("workorder");
			sInstance = new WorkOrderFactory();
		}
		return sInstance;
	}

	/**
	 * 获取计划列表
	 *
	 * @return 计划列表
	 */
	public List<TestSchema> getSchemaList() {
		return this.schemaList;
	}

	/**
	 * 获取计划
	 *
	 * @param schemaID 计划ID
	 * @return 计划对象
	 */
	public TestSchema getTestSchema(long schemaID) {
		for (TestSchema schema : this.schemaList) {
			if (schema.getId() == schemaID) {
				return schema;
			}
		}
		return null;
	}

	/**
	 * 初始化工单对象
	 */
	public void init() {
		File file = new File(fileName);
		if (file.exists()) {
			this.schemaList.clear();
			FileInputStream fis = null;
			try {
				if (xmlParser == null) {
					xmlParser = Xml.newPullParser();
				}
				fis = new FileInputStream(file);
				xmlParser.setInput(fis, "UTF-8");
				int eventType = xmlParser.getEventType();
				TestPlan testPlan = new TestPlan();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if ("Id".equals(xmlParser.getName())) {
								testPlan.setId(Long.parseLong(xmlParser.nextText()));
							} else if ("Version".equals(xmlParser.getName())) {
								testPlan.setVersion(xmlParser.nextText());
							} else if ("TimeZone".equals(xmlParser.getName())) {
								testPlan.setTimeZone(xmlParser.nextText());
							} else if ("TestSchemaConfig".equals(xmlParser.getName())) {
								this.readSchemas(testPlan);
							}
					}
					eventType = xmlParser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
						fis = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			LogUtil.d(TAG, "workorder.xml no exists!");
		}
	}

	/**
	 * 读取计划对象
	 *
	 * @param testPlan 父计划
	 * @throws Exception 异常
	 */
	private void readSchemas(TestPlan testPlan) throws Exception {
		int eventType = xmlParser.getEventType();
		TestSchema schema = new TestSchema();
		schema.setPlan(testPlan);
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Id".equals(xmlParser.getName())) {
						schema.setId(Long.parseLong(xmlParser.nextText()));
					} else if ("Name".equals(xmlParser.getName())) {
						schema.setName(xmlParser.nextText());
					} else if ("TestTaskGroup".equals(xmlParser.getName())) {
						this.readTestTaskGroup(schema);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("TestSchemaConfig".equals(xmlParser.getName())) {
						if (schema.getName() == null && schema.getTaskGroupList().size() > 0) {
							schema.setName("TestGroup" + schema.getTaskGroupList().get(0).getSequence());
						}
						this.schemaList.add(schema);
						return;
					}
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 读取任务组对象
	 *
	 * @param schema 计划对象
	 * @throws Exception 异常
	 */
	private void readTestTaskGroup(TestSchema schema) throws Exception {
		int eventType = xmlParser.getEventType();
		TestTaskGroup group = new TestTaskGroup();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Id".equals(xmlParser.getName())) {
						group.setId(Long.parseLong(xmlParser.nextText()));
					} else if ("Sequence".equals(xmlParser.getName())) {
						group.setSequence(this.getIntValue(false));
					} else if ("GroupRepeatCount".equals(xmlParser.getName())) {
						group.setRepeatCount(this.getIntValue(false));
					} else if ("GroupInterval".equals(xmlParser.getName())) {
						group.setInterval(this.getIntValue(true));
					} else if ("GroupDisconnetNetwork".equals(xmlParser.getName())) {
						group.setDisconnetNetwork(this.getBooleanValue());
					} else if ("TestTaskInfo".equals(xmlParser.getName())) {
						this.readTestTask(group);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("TestTaskGroup".equals(xmlParser.getName())) {
						schema.getTaskGroupList().add(group);
						return;
					}
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 读取测试业务对象
	 *
	 * @param group 所属业务组
	 */
	private void readTestTask(TestTaskGroup group) throws Exception {
		long id = 0;
		String name = "";
		int sequence = 0;
		int repeat = 0;
		int interTaskInterval = 10;
		TaskModel model = null;
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("TaskID".equals(xmlParser.getName())) {
						id = Long.parseLong(xmlParser.nextText());
					} else if ("Name".equals(xmlParser.getName())) {
						name = xmlParser.nextText();
					} else if ("Sequence".equals(xmlParser.getName())) {
						sequence = this.getIntValue(false);
					} else if ("ExecuteCount".equals(xmlParser.getName())) {
						repeat = this.getIntValue(false);
					} else if ("InterTaskInterval".equals(xmlParser.getName())) {
						interTaskInterval = this.getIntValue(true);
					} else if ("PingTestConfig".equals(xmlParser.getName())) {
						model = this.readPingTest();
					} else if ("FTPDownloadTestConfig".equals(xmlParser.getName())) {
						model = this.readFTPDownloadTest();
					} else if ("FTPUploadTestConfig".equals(xmlParser.getName())) {
						model = this.readFTPUploadTest();
					} else if ("IdleTestConfig".equals(xmlParser.getName())) {
						model = this.readIdleTest();
					} else if ("HttpVsTestConfig".equals(xmlParser.getName())) {
						model = this.readHttpVsTest();
					} else if ("HTTPPageTestConfig".equals(xmlParser.getName())) {
						model = this.readHttpPageTest();
					} else if ("MOCTestConfig".equals(xmlParser.getName())) {
						model = this.readMOCTest();
					} else if ("MTCTestConfig".equals(xmlParser.getName())) {
						model = this.readMTCTest();
					} else if ("HTTPDownTestConfig".equals(xmlParser.getName())) {
						model = this.readHttpDownloadTest();
					} else if ("HTTPUpTestConfig".equals(xmlParser.getName())) {
						model = this.readHttpUploadTest();
					}
					break;
				case XmlPullParser.END_TAG:
					if ("TestTaskInfo".equals(xmlParser.getName()) && model != null) {
						model.setId(id);
						model.setTaskName(name);
						model.setSequence(sequence);
						model.setRepeat(repeat);
						model.setCheck(true);
						model.setEditType(1);
						model.setFromType(1);
						model.setInterTaskInterval(interTaskInterval);
						group.getTaskList().add(model);
						return;
					}
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 读取http上传测试业务
	 *
	 * @return 业务对象
	 */
	private TaskModel readHttpUploadTest() throws Exception {
		TaskHttpUploadModel model = new TaskHttpUploadModel();
		model.setTaskType(WalkStruct.TaskType.HttpUpload.name());
		model.setServerType(0);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
					} else if ("PSCallMode".equals(xmlParser.getName())) {
						model.setTestMode("By File".equals(xmlParser.nextText()) ? TaskHttpUploadModel.BY_FILE
								: TaskHttpUploadModel.BY_TIME);
					} else if ("WebsiteType".equals(xmlParser.getName())) {
						String website = xmlParser.nextText();
						int serverType = 3;
						if ("Youtube".equals(website))
							serverType = 0;
						else if ("BaiduYun".equals(website))
							serverType = 1;
						model.setServerType(serverType);
					} else if ("YoutubeAccount".equals(xmlParser.getName())) {
						model.setUsername(this.getStringAttr("Username"));
						model.setPassword(this.getStringAttr("Password"));
					} else if ("BaiduYunAccount".equals(xmlParser.getName())) {
						boolean isDeveloperAccount = this.getBooleanAttr("IsDeveloperAccount");
						model.setAccountType(isDeveloperAccount ? 0 : 1);
						model.setAccountKey(this.getStringAttr("APIKey"));
						model.setSecretKey(this.getStringAttr("SecretKey"));
						model.setFilePath(this.getStringAttr("UploadPath"));
					} else if ("FileSource".equals(xmlParser.getName())) {
						model.setFileSource("Local File".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("FileSize".equals(xmlParser.getName())) {
						model.setFileSize(this.getIntValue(false));
					} else if ("LocalFile".equals(xmlParser.getName())) {
						model.setFilePath(xmlParser.nextText());
					} else if ("UploadTimeout".equals(xmlParser.getName())) {
						model.setTimeout(this.getIntValue(true));
					} else if ("NoDataTimeout".equals(xmlParser.getName())) {
						model.setNoDataTimeout(this.getIntValue(true));
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("HTTPUpTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 获取字符串属性值
	 *
	 * @param attrName 属性名
	 * @return 属性值
	 * @throws Exception 异常
	 */
	private String getStringAttr(String attrName) throws Exception {
		return xmlParser.getAttributeValue(null, attrName);
	}

	/**
	 * 获取整型属性值
	 *
	 * @param attrName 属性名
	 * @return 属性值
	 * @throws Exception 异常
	 */
	private int getIntAttr(String attrName) throws Exception {
		String value = this.getStringAttr(attrName);
		return Integer.parseInt(value);
	}

	/**
	 * 获取布尔属性值
	 *
	 * @param attrName 属性名
	 * @return 属性值
	 * @throws Exception 异常
	 */
	private boolean getBooleanAttr(String attrName) throws Exception {
		String flag = this.getStringAttr(attrName).toLowerCase(Locale.getDefault());
		return "true".equals(flag);
	}

	/**
	 * 获取布尔值
	 *
	 * @return 属性值
	 * @throws Exception 异常
	 */
	private boolean getBooleanValue() throws Exception {
		String flag = xmlParser.nextText().toLowerCase(Locale.getDefault());
		return "true".equals(flag);
	}

	/**
	 * 读取http下载测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskModel readHttpDownloadTest() throws Exception {
		TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.HttpDownload.name());
		model.setTaskType(WalkStruct.TaskType.HttpDownload.name());
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
					} else if ("WebsiteType".equals(xmlParser.getName())) {
						model.setServerType("Normal".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("BaiduYunAccount".equals(xmlParser.getName())) {
						boolean isDeveloperAccount = this.getBooleanAttr("IsDeveloperAccount");
						model.setAccountType(isDeveloperAccount ? 0 : 1);
						model.setAccountKey(this.getStringAttr("APIKey"));
						model.setSecretKey(this.getStringAttr("SecretKey"));
					} else if ("URLInfo".equals(xmlParser.getName())) {
						boolean isCheck = this.getBooleanAttr("IsCheck");
						if (isCheck)
							model.setXmlUrl(this.getStringAttr("URL"));
					} else if ("UserProxy".equals(xmlParser.getName())) {
						model.setHasProxy(this.getBooleanValue());
					} else if ("ProxySetting".equals(xmlParser.getName())) {
						model.setAddress(this.getStringAttr("ProxyIP"));
						model.setPort(this.getIntAttr("ProxyPort"));
						model.setUser(this.getStringAttr("Username"));
						model.setPass(this.getStringAttr("Password"));
					} else if ("DownloadTimeout".equals(xmlParser.getName())) {
						model.setTimeOut(this.getIntValue(true));
					} else if ("NoDataTimeout".equals(xmlParser.getName())) {
						model.setReponse(this.getIntValue(true));
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("HTTPDownTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 读取语音被叫测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskPassivityCallModel readMTCTest() throws Exception {
		TaskPassivityCallModel model = new TaskPassivityCallModel();
		model.setTaskType(WalkStruct.TaskType.PassivityCall.name());
//		model.setDisConnect(1);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
					} else if ("CallType".equals(xmlParser.getName())) {
						model.setCallMode(0);
//                    } else if ("MOCDeviceID".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("MOCDevicePort".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("UMTS_AMR_Rate".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("CDMA_AMR_Rate".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("MOSTest".equals(xmlParser.getName())) {
						model.setCallMOSServer(this.getBooleanValue() ? 1 : 0);
//                    } else if ("LowMOSThreshold".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("MosAlgorithm".equals(xmlParser.getName())) {
						model.setCallMOSCount("PESQ".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("SynchTest".equals(xmlParser.getName())) {
						model.setUnitTest(this.getBooleanValue());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("MTCTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 读取语音主叫测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskInitiativeCallModel readMOCTest() throws Exception {
		TaskInitiativeCallModel model = new TaskInitiativeCallModel();
		model.setTaskType(WalkStruct.TaskType.InitiativeCall.name());
//		model.setDisConnect(1);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
					} else if ("CallType".equals(xmlParser.getName())) {
						model.setCallMode(0);
					} else if ("DialNumber".equals(xmlParser.getName())) {
						model.setCallNumber(xmlParser.nextText());
//                    } else if ("MTCDevicePort".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("MOSTest".equals(xmlParser.getName())) {
						model.setMosTest(this.getBooleanValue() ? 1 : 0);
//                    } else if ("LowMOSThreshold".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("MosAlgorithm".equals(xmlParser.getName())) {
						model.setCallMOSCount("PESQ".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("SynchTest".equals(xmlParser.getName())) {
						model.setUnitTest(this.getBooleanValue());
					} else if ("ConnectionTime".equals(xmlParser.getName())) {
						model.setConnectTime(this.getIntValue(true));
					} else if ("Duration".equals(xmlParser.getName())) {
						model.setKeepTime(this.getIntValue(true));
//                    } else if ("LongCall".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("UMTS_AMR_Rate".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("CDMA_AMR_Rate".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("UseRandomDialTime".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("RandomDialDurationMax".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("RandomDialDurationMin".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("DialControlMode".equals(xmlParser.getName())) {
						model.setCallMOSTestType(this.getIntValue(false));
					} else if ("SampleType".equals(xmlParser.getName())) {
						int polqaSample = 0;
						String type = xmlParser.nextText().toUpperCase(Locale.getDefault());
						if ("WB 16K".equals(type))
							polqaSample = 1;
						else if ("SWB 48K".equals(type))
							polqaSample = 2;
						model.setPolqaSample(polqaSample);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("MOCTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 获取整型值
	 *
	 * @param isms 是否毫秒
	 * @return 属性值
	 * @throws Exception 异常
	 */
	private int getIntValue(boolean isms) throws Exception {
		String value = xmlParser.nextText();
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			if (isms) {
				return (int) (Long.parseLong(value) / 1000);
			} else {
				return Integer.parseInt(value);
			}
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 读取网页测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskHttpPageModel readHttpPageTest() throws Exception {
		TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.Http.name());
		model.setTaskType(WalkStruct.TaskType.Http.name());
		model.setDisConnect(1);
		ArrayList<UrlModel> urlList = new ArrayList<>();
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
//                    } else if ("DetachEverytime".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("TCPDump".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("Mode".equals(xmlParser.getName())) {
						model.setHttpTestMode("Login".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("URL".equals(xmlParser.getName())) {
						String[] urls = xmlParser.nextText().split("\n");
						for (String url : urls) {
							urlList.add(new UrlModel(url, "1"));
						}
//                    } else if ("RefreshMode".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("RefreshLayer".equals(xmlParser.getName())) {
						model.setRefreshDeep(this.getIntValue(false));
//                    } else if ("NoDataTimeout".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("Timeout".equals(xmlParser.getName())) {
						model.setTimeOut(this.getIntValue(true));
//                    } else if ("LoadImage".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("ClearCache".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("ProxyType".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("UseProxy".equals(xmlParser.getName())) {
						model.setHasProxy(this.getBooleanValue());
					} else if ("ProxyAddress".equals(xmlParser.getName())) {
						model.setAddress(xmlParser.nextText());
					} else if ("ProxyPort".equals(xmlParser.getName())) {
						model.setPort(this.getIntValue(false));
//                    } else if ("UseProxy".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("PSCallMode".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("ThreadCount".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("HTTPPageTestConfig".equals(xmlParser.getName())) {
						model.setUrlModelList(urlList);
						this.addUrl(model);
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		model.setUrlModelList(urlList);
		this.addUrl(model);
		return model;
	}

	/**
	 * 添加Url
	 *
	 * @param httpModel http对象
	 */
	private void addUrl(TaskHttpPageModel httpModel) {
		ConfigUrl configUrl = new ConfigUrl();
		for (UrlModel url : httpModel.getUrlModelList()) {
			if (!configUrl.contains(url.getName())) {
				configUrl.addUrl(url);
			}
		}
	}

	/**
	 * 读取流媒体测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	@SuppressWarnings("deprecation")
	private TaskVideoPlayModel readHttpVsTest() throws Exception {
		TaskVideoPlayModel model = new TaskVideoPlayModel();
		model.setTaskType(WalkStruct.TaskType.HTTPVS.name());
		model.setDisConnect(1);
		model.setVideoShow(true);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
//                    } else if ("DetachEverytime".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("TCPDump".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("PlayMode".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("PlayerType".equals(xmlParser.getName())) {
						String type = xmlParser.nextText().toLowerCase(Locale.getDefault());
						if (type.startsWith("dingli"))
							model.setPlayerType(TaskVideoPlayModel.PLAYER_TYPE_DINGLI);
						else if (type.startsWith("youtube"))
							model.setPlayerType(TaskVideoPlayModel.PLAYER_TYPE_YOUTUBE);
						else
							model.setPlayerType(TaskVideoPlayModel.PLAYER_TYPE_VITAMIO);
					} else if ("PlayTimeBy".equals(xmlParser.getName())) {
						model.setPlayTimerMode("Time".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("PlayDuration".equals(xmlParser.getName())) {
						model.setPlayDuration(this.getIntValue(true));
					} else if ("PlayPercentage".equals(xmlParser.getName())) {
						model.setPlayDuration(this.getIntValue(false));
					} else if ("MaxBufferCounts".equals(xmlParser.getName())) {
						int maxBufCounts = this.getIntValue(false);
						if (maxBufCounts > 0)
							model.setMaxBufCounts(maxBufCounts);
					} else if ("BufferTimeBy".equals(xmlParser.getName())) {
						model.setBufTimerMode("Time".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("MaxBufferTimeout".equals(xmlParser.getName())) {
						int maxBufTime = this.getIntValue(true);
						if (maxBufTime > 0)
							model.setMaxBufferTimeout(maxBufTime);
					} else if ("MaxBufferPercentage".equals(xmlParser.getName())) {
						if (model.getBufTimerMode() == 1)
							model.setMaxBufferPercentage(this.getIntValue(false));
					} else if ("BufferTime".equals(xmlParser.getName())) {
						int bufTime = this.getIntValue(true);
						if (bufTime > 0)
							model.setBufTime(bufTime);
					} else if ("BufferPlayThreshold".equals(xmlParser.getName())) {
						int bufThread = this.getIntValue(true);
						if (bufThread > 0)
							model.setBufThred(bufThread);
					} else if ("SaveVideo".equals(xmlParser.getName())) {
						model.setSave(xmlParser.nextText().equals("1"));
					} else if ("URL".equals(xmlParser.getName())) {
						model.setUrl(xmlParser.nextText());
					} else if ("Timeout".equals(xmlParser.getName())) {
						model.setPlayTimeout(this.getIntValue(true));
					} else if ("NoDataTimeout".equals(xmlParser.getName())) {
						model.setNoDataTimeout(this.getIntValue(true));
//                    } else if ("UDPProtocol".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("Type".equals(xmlParser.getName())) {
						String type = xmlParser.nextText().toLowerCase(Locale.getDefault());
						switch (type) {
							case "youku":
								model.setVideoType(TaskVideoPlayModel.VIDEO_TYPE_YOUKU);
								break;
							case "youtube":
								model.setVideoType(TaskVideoPlayModel.VIDEO_TYPE_YOUTUBE);
								break;
							default:
								model.setVideoType(TaskVideoPlayModel.VIDEO_TYPE_OTHER);
								break;
						}
					} else if ("Quality".equals(xmlParser.getName())) {
						String quality = xmlParser.nextText();
						switch (quality.toUpperCase(Locale.getDefault())) {
							case "NORMAL":
								model.setVideoQuality(0);
								break;
							case "HIGH":
								model.setVideoQuality(1);
								break;
							case "SUPER":
								model.setVideoQuality(2);
								break;
							case "720P":
								model.setVideoQuality(3);
								break;
							case "1080P":
								model.setVideoQuality(4);
								break;
							case "2K":
								model.setVideoQuality(5);
								break;
							case "4K":
								model.setVideoQuality(6);
								break;
							case "AUTO":
								model.setVideoQuality(7);
								break;
						}
					} else if ("PSCallMode".equals(xmlParser.getName())) {
						model.setPlayType(this.getBooleanValue() ? TaskVideoPlayModel.PLAY_TYPE_TIME : TaskVideoPlayModel.PLAY_TYPE_FILE);
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("HttpVsTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 读取空测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskEmptyModel readIdleTest() throws Exception {
		TaskEmptyModel model = new TaskEmptyModel();
		model.setTaskType(WalkStruct.TaskType.EmptyTask.name());
//		model.setDisConnect(1);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
					} else if ("Duration".equals(xmlParser.getName())) {
						model.getIdleTestConfig().setKeepTime(this.getIntValue(true));
//                    } else if ("CollectData".equals(xmlParser.getName())) {
						// 暂时不处理
					}
					break;
				case XmlPullParser.END_TAG:
					if ("IdleTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 读取FTP上传测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskFtpModel readFTPUploadTest() throws Exception {
		TaskFtpModel model = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());
		model.setTaskType(WalkStruct.TaskType.FTPUpload.name());
		model.setDisConnect(1);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
//                    } else if ("DetachEverytime".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("TCPDump".equals(xmlParser.getName())) {
						model.setTcpipCapture(this.getBooleanValue() ? 1 : 0);
					} else if ("UploadFileMode".equals(xmlParser.getName())) {
						model.setFileSource("LocalFile".equals(xmlParser.nextText()) ? 0 : 1);
					} else if ("FileSize".equals(xmlParser.getName())) {
						model.setFileSize(Long.parseLong(xmlParser.nextText()) / 1000);
					} else if ("RemotePath".equals(xmlParser.getName())) {
						model.setRemoteFile(xmlParser.nextText());
					} else if ("UploadTimeout".equals(xmlParser.getName())) {
						model.setTimeOut(this.getIntValue(true));
					} else if ("NoDataTimeout".equals(xmlParser.getName())) {
						model.setNoAnswer(this.getIntValue(true));
					} else if ("ThreadCount".equals(xmlParser.getName())) {
						model.setThreadNumber(this.getIntValue(false));
//                    } else if ("Mode".equals(xmlParser.getName())) {
						// 传输模式0：二进制 1：文本，暂时不处理
					} else if ("Host".equals(xmlParser.getName())) {
						model.setFtpServer(xmlParser.nextText());
					} else if ("Port".equals(xmlParser.getName())) {
						model.setPort(this.getIntValue(false));
					} else if ("User".equals(xmlParser.getName())) {
						model.setUser(xmlParser.nextText());
					} else if ("Password".equals(xmlParser.getName())) {
						model.setPass(xmlParser.nextText());
					} else if ("IsPassiveMode".equals(xmlParser.getName())) {
						model.setIsPassiveMode(this.getBooleanValue() ? 1 : 0);
					} else if ("Authentication".equals(xmlParser.getName())) {
						model.setAnonymous("false".equals(xmlParser.nextText()));
					} else if ("PSCallMode".equals(xmlParser.getName())) {
						model.setPsCall(this.getBooleanValue() ? 1 : 0);
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("FTPUploadTestConfig".equals(xmlParser.getName())) {
						String ftpIp = model.getFtpServerName();
						if (ftpIp == null || ftpIp.trim().length() == 0)
							return null;
						this.addFtp(model);
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		this.addFtp(model);
		return model;
	}

	/**
	 * 读取FTP下载测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskFtpModel readFTPDownloadTest() throws Exception {
		TaskFtpModel model = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
		model.setTaskType(WalkStruct.TaskType.FTPDownload.name());
		model.setDisConnect(1);
		model.setFileSource(1);
		model.setFileSize(1000 * 2);
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
//                    } else if ("DetachEverytime".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("TCPDump".equals(xmlParser.getName())) {
						model.setTcpipCapture(this.getBooleanValue() ? 1 : 0);
					} else if ("DownloadFile".equals(xmlParser.getName())) {
						model.setRemoteFile(xmlParser.nextText());
					} else if ("DownloadTimeout".equals(xmlParser.getName())) {
						model.setTimeOut(this.getIntValue(true));
					} else if ("NoDataTimeout".equals(xmlParser.getName())) {
						model.setNoAnswer(this.getIntValue(true));
					} else if ("ThreadCount".equals(xmlParser.getName())) {
						model.setThreadNumber(this.getIntValue(false));
//                    } else if ("SaveFile".equals(xmlParser.getName())) {
						// 暂时不处理
//                    } else if ("Mode".equals(xmlParser.getName())) {
						// 暂时不处理
					} else if ("Host".equals(xmlParser.getName())) {
						model.setFtpServer(xmlParser.nextText());
					} else if ("Port".equals(xmlParser.getName())) {
						model.setPort(this.getIntValue(false));
					} else if ("User".equals(xmlParser.getName())) {
						model.setUser(xmlParser.nextText());
					} else if ("Password".equals(xmlParser.getName())) {
						model.setPass(xmlParser.nextText());
					} else if ("IsPassiveMode".equals(xmlParser.getName())) {
						model.setIsPassiveMode(this.getBooleanValue() ? 1 : 0);
					} else if ("Authentication".equals(xmlParser.getName())) {
						model.setAnonymous(!this.getBooleanValue());
					} else if ("PSCallMode".equals(xmlParser.getName())) {
						model.setPsCall(this.getBooleanValue() ? 1 : 0);
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("FTPDownloadTestConfig".equals(xmlParser.getName())) {
						String ftpIp = model.getFtpServerName();
						if (ftpIp == null || ftpIp.trim().length() == 0)
							return null;
						this.addFtp(model);
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		this.addFtp(model);
		return model;
	}

	/**
	 * 添加解析后的Ftp到Ftp设置列表中
	 *
	 * @param ftpTaskModel ftp模型类
	 */
	private void addFtp(TaskFtpModel ftpTaskModel) {
		String ftpIp = ftpTaskModel.getFtpServerName();
		if (ftpIp == null || ftpIp.trim().length() == 0)
			return;
		ConfigFtp configFtp = new ConfigFtp();
		String name = ftpIp + "(" + ftpTaskModel.getUser() + ")";
		FtpServerModel ftpServerModel = new FtpServerModel(name, ftpIp, ftpTaskModel.getPort() + "",
				ftpTaskModel.getUser(), ftpTaskModel.getPass());
		ftpServerModel.setAnonymous(ftpTaskModel.isAnonymous());
		ftpTaskModel.setFtpServer(name);
		configFtp.addFtp(ftpServerModel);
	}

	/**
	 * 读取ping测试业务
	 *
	 * @return 业务对象
	 * @throws Exception 异常
	 */
	private TaskPingModel readPingTest() throws Exception {
		TaskPingModel model = new TaskPingModel();
		model.setTaskType(WalkStruct.TaskType.Ping.name());
		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:
					if ("Interval".equals(xmlParser.getName())) {
						model.setInterVal(this.getIntValue(true));
					} else if ("PingAddress".equals(xmlParser.getName())) {
						model.setIp(xmlParser.nextText());
					} else if ("PackSize_B".equals(xmlParser.getName())) {
						model.setSize(this.getIntValue(false));
					} else if ("Timeout".equals(xmlParser.getName())) {
						model.setTimeOut(this.getIntValue(true));
					} else if ("DisconnectEverytime".equals(xmlParser.getName())) {
						model.setDisConnect(this.getBooleanValue() ? 1 : 0);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("PingTestConfig".equals(xmlParser.getName())) {
						return model;
					}
			}
			eventType = xmlParser.next();
		}
		return model;
	}

	/**
	 * 保存下载的xml数据
	 *
	 * @param xml 数据
	 */
	public void saveXmlString(String xml) {
		if (xml == null)
			xml = "";
		File file = new File(fileName);
		try {
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(xml.getBytes());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xml.trim().length() == 0)
			return;
		this.init();
	}

}

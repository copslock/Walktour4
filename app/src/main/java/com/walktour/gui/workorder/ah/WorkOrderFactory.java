package com.walktour.gui.workorder.ah;

import android.util.Xml;

import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.workorder.ah.model.WorkOrder;
import com.walktour.gui.workorder.ah.model.WorkOrderPoint;
import com.walktour.model.FtpServerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安徽电信工单模块工厂类
 * 
 * @author jianchao.wang 2014年6月13日
 */
public class WorkOrderFactory {
	private static final String TAG = "WorkOrderFactory";
	/** 唯一实例 */
	private static WorkOrderFactory sInstance = null;
	/** 工单对象列表 */
	private List<WorkOrder> orderList = new ArrayList<WorkOrder>();
	/** 工单映射<信息点编号,工单对象> */
	private Map<String, WorkOrder> orderMap = new HashMap<String, WorkOrder>();
	/** 信息点映射<信息点编号,信息点对象> */
	private Map<String, WorkOrderPoint> pointMap = new HashMap<String, WorkOrderPoint>();
	/** json文件名全路径，该文件是从平台上下载的工单数据 */
	private static String jsonFileName;
	/** xml文件名全路径， 该文件是记录手动新增的信息点信息 */
	private static String xmlFileName;
	/** xml解析类 */
	private XmlPullParser xmlParser;
	/** xml编辑类 */
	private XmlSerializer serializer;

	private WorkOrderFactory() {
		this.init();
	}

	/**
	 * 获取唯一实例
	 * 
	 * @return
	 */
	public static WorkOrderFactory getInstance() {
		if (sInstance == null) {
			AppFilePathUtil.getInstance().createSDCardBaseDirectory("workorder");
			jsonFileName = AppFilePathUtil.getInstance().getSDCardBaseFile("workorder","workorder.json").getAbsolutePath();
			xmlFileName = AppFilePathUtil.getInstance().getSDCardBaseFile("workorder","workorder_add.xml").getAbsolutePath();
			sInstance = new WorkOrderFactory();
		}
		return sInstance;
	}

	/**
	 * 初始化工单对象
	 */
	public void init() {
		this.initJsonFile();
		this.initXmlFile();
	}

	/**
	 * 初始化Xml工单对象
	 */
	public void initXmlFile() {
		File file = new File(xmlFileName);
		if (file.exists() && !this.orderList.isEmpty()) {
			FileInputStream fis = null;
			try {
				if (xmlParser == null) {
					xmlParser = Xml.newPullParser();
				}
				fis = new FileInputStream(file);
				xmlParser.setInput(fis, "UTF-8");
				int eventType = xmlParser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("WorkOrderList".equals(xmlParser.getName())) {
							this.readXmlOrders();
						}
						break;
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
			LogUtil.d(TAG, "workorder_add.xml no exists!");
		}
	}

	/**
	 * 读取xml文件工单对象
	 * 
	 * @throws Exception
	 */
	private void readXmlOrders() throws Exception {
		int eventType = xmlParser.next();
		WorkOrder orderTemp = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("WorkItemInfo".equals(xmlParser.getName())) {
					orderTemp = new WorkOrder();
				} else if ("WorkItemCode".equals(xmlParser.getName())) {
					orderTemp.setWorkItemCode(xmlParser.nextText());
				} else if ("Name".equals(xmlParser.getName())) {
					orderTemp.setName(xmlParser.nextText());
				} else if ("CQTPointList".equals(xmlParser.getName())) {
					this.readXmlPoints(orderTemp);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("WorkItemInfo".equals(xmlParser.getName())) {
					if (orderTemp != null) {
						for (WorkOrder order : this.orderList) {
							if (order.getWorkItemCode().equals(orderTemp.getWorkItemCode())) {
								for (WorkOrderPoint point : orderTemp.getPointList()) {
									point.setOrder(order);
									order.getPointList().add(point);
								}
								break;
							}
						}
						orderTemp = null;
					}
				} else if ("WorkOrderList".equals(xmlParser.getName())) {
					return;
				}
				break;
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 读取xml信息点对象
	 * 
	 * @param order
	 *          工单对象
	 */
	private void readXmlPoints(WorkOrder order) throws Exception {
		int eventType = xmlParser.next();
		WorkOrderPoint point = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("CQTPointInfo".equals(xmlParser.getName())) {
					point = new WorkOrderPoint();
					point.setCreate(true);
				} else if ("Name".equals(xmlParser.getName())) {
					point.setName(xmlParser.nextText());
				} else if ("PointID".equals(xmlParser.getName())) {
					point.setPointID(xmlParser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if ("CQTPointInfo".equals(xmlParser.getName())) {
					if (point != null) {
						order.getPointList().add(point);
						point = null;
					}
				} else if ("CQTPointList".equals(xmlParser.getName())) {
					return;
				}
				break;
			}
			eventType = xmlParser.next();
		}
	}

	/**
	 * 初始化Json工单对象
	 */
	public void initJsonFile() {
		File file = new File(jsonFileName);
		if (file.exists()) {
			this.orderList.clear();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String data = reader.readLine();
				StringBuilder json = new StringBuilder();
				while (data != null) {
					json.append(data);
					data = reader.readLine(); // 接着读下一行
				}
				reader.close();
				this.readJsonOrders(json.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			LogUtil.d(TAG, "workorder.json no exists!");
		}
	}

	/**
	 * 读取json工单列表
	 * 
	 * @param orderJson
	 *          工单json
	 */
	private void readJsonOrders(String orderJson) {
		try {
			JSONTokener jsonParser = new JSONTokener(orderJson);
			JSONArray orders = (JSONArray) jsonParser.nextValue();
			for (int i = 0; i < orders.length(); i++) {
				JSONObject order = orders.getJSONObject(i);
				WorkOrder orderObj = new WorkOrder();
				orderObj.setDescription(this.getString(order, "Description", false));
				orderObj.setName(this.getString(order, "Name", false));
				orderObj.setWorkItemCode(this.getString(order, "WorkItemCode", false));
				orderObj.setWorkItemID(this.getString(order, "WorkItemID", false));
				orderObj.setPlanToStart(this.getString(order, "PlanToStart", false));
				orderObj.setPlanToFinish(this.getString(order, "PlanToFinish", false));
				if (order.has("TestTask"))
					this.readJsonTasks(orderObj, order.getJSONObject("TestTask"));
				this.readJsonPoints(orderObj, order.getJSONArray("CQTPointList"));
				this.orderList.add(orderObj);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取json工单任务
	 * 
	 * @param order
	 *          工单对象
	 * @param tasks
	 *          任务元素
	 */
	private void readJsonTasks(WorkOrder order, JSONObject tasks) {
		try {
			StringBuffer strBuffer = new StringBuffer();
			String ftpDownConfig = this.getString(tasks, "FTPDownConfig", false);
			String ftpUpConfig = this.getString(tasks, "FTPUpConfig", false);
			String moLongConfig = this.getString(tasks, "MOLongConfig", false);
			String moShortConfig = this.getString(tasks, "MOShortConfig", false);
			if (!ftpDownConfig.isEmpty())
				order.getTaskXmlMap().put("FTPDownConfig", ftpDownConfig);
			if (!ftpUpConfig.isEmpty())
				order.getTaskXmlMap().put("FTPUpConfig", ftpUpConfig);
			if (!moLongConfig.isEmpty())
				order.getTaskXmlMap().put("MOLongConfig", moLongConfig);
			if (!moShortConfig.isEmpty())
				order.getTaskXmlMap().put("MOShortConfig", moShortConfig);
			strBuffer.append(ftpDownConfig).append(ftpUpConfig).append(moLongConfig).append(moShortConfig);
			if (xmlParser == null) {
				xmlParser = Xml.newPullParser();
			}
			ByteArrayInputStream xmlInStream = new ByteArrayInputStream(strBuffer.toString().getBytes("UTF-8"));
			xmlParser.setInput(xmlInStream, "UTF-8");
			int eventType = xmlParser.getEventType();
			TaskFtpModel ftpTaskModel = null;
			TaskFtpModel ftpDownTaskModel = null;
			TaskInitiativeCallModel callTaskModel = null;
			boolean isFTPHostSetting = false;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					// FTP上传
					if ("FTPUploadTestConfig".equals(xmlParser.getName())) {
						ftpTaskModel = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());
						ftpTaskModel.setTaskName("FTPUploadTest");
						ftpTaskModel.setDisConnect(1);
						ftpTaskModel.setTaskType(WalkStruct.TaskType.FTPUpload.name());
						ftpTaskModel.setFileSource(1);
						ftpTaskModel.setFileSize(1000 * 2);
						ftpTaskModel.setLoginInterval(15);
						ftpTaskModel.setLoginTimes(3);
						ftpTaskModel.setLoginTimeOut(60);
					}
					if (ftpTaskModel != null) {
						if ("ExecCount".equals(xmlParser.getName())) {
							ftpTaskModel.setRepeat(parseInt(xmlParser.nextText(), 10));
						} else if ("Inteval".equals(xmlParser.getName())) {
							ftpTaskModel.setInterVal(parseInt(xmlParser.nextText(), 10000) / 1000);
						} else if ("FTPHostSetting".equals(xmlParser.getName())) {
							isFTPHostSetting = true;
						}
						if (isFTPHostSetting) {
							if ("Host".equals(xmlParser.getName())) {
								String ip = xmlParser.nextText();
								ftpTaskModel.setFtpServer(ip); // 以ip作为名称
							} else if ("Port".equals(xmlParser.getName())) {
								ftpTaskModel.setPort(Integer.parseInt(xmlParser.nextText()));
							} else if ("User".equals(xmlParser.getName())) {
								ftpTaskModel.setUser(xmlParser.nextText());
							} else if ("Password".equals(xmlParser.getName())) {
								ftpTaskModel.setPass(xmlParser.nextText());
								addFtp(ftpTaskModel);
							} else if ("IsPassiveMode".equals(xmlParser.getName())) {
								ftpTaskModel.setIsPassiveMode(parseInt(xmlParser.nextText(), 0));
								isFTPHostSetting = false;
							}
						} else if ("RemotePath".equals(xmlParser.getName())) {
							ftpTaskModel.setRemoteFile(xmlParser.nextText());
						} else if ("FileSize".equals(xmlParser.getName())) {
							ftpTaskModel.setFileSize(Long.valueOf(xmlParser.nextText()));
						} else if ("UploadTimeout".equals(xmlParser.getName())) {
							ftpTaskModel.setTimeOut(parseInt(xmlParser.nextText(), 300000) / 1000);
						} else if ("PSCallMode".equals(xmlParser.getName())) {
							ftpTaskModel.setPsCall(parseInt(xmlParser.nextText(), 0));
						} else if ("NoDataTimeout".equals(xmlParser.getName())) {
							ftpTaskModel.setNoAnswer(parseInt(xmlParser.nextText(), 60));
						} else if ("ThreadCount".equals(xmlParser.getName())) { // 登录超时
							ftpTaskModel.setThreadNumber(parseInt(xmlParser.nextText(), 1));
						}

					}
					// FTP下载
					if ("FTPDownloadTestConfig".equals(xmlParser.getName())) {
						ftpDownTaskModel = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
						ftpDownTaskModel.setTaskName("FTPDownloadTest");
						ftpDownTaskModel.setDisConnect(1);
						ftpDownTaskModel.setTaskType(WalkStruct.TaskType.FTPDownload.name());
						ftpDownTaskModel.setFileSource(1);
						ftpDownTaskModel.setFileSize(1000 * 2);
						ftpDownTaskModel.setLoginInterval(15);
						ftpDownTaskModel.setLoginTimes(3);
						ftpDownTaskModel.setLoginTimeOut(60);
					}
					if (ftpDownTaskModel != null) {
						if ("ExecCount".equals(xmlParser.getName())) {
							ftpDownTaskModel.setRepeat(parseInt(xmlParser.nextText(), 10));
						} else if ("Inteval".equals(xmlParser.getName())) {
							ftpDownTaskModel.setInterVal(parseInt(xmlParser.nextText(), 10000) / 1000);
						} else if ("FTPHostSetting".equals(xmlParser.getName())) {
							isFTPHostSetting = true;
						}
						if (isFTPHostSetting) {
							if ("Host".equals(xmlParser.getName())) {
								String ip = xmlParser.nextText();
								ftpDownTaskModel.setFtpServer(ip); // 以ip作为名称
							} else if ("Port".equals(xmlParser.getName())) {
								ftpDownTaskModel.setPort(Integer.parseInt(xmlParser.nextText()));
							} else if ("User".equals(xmlParser.getName())) {
								ftpDownTaskModel.setUser(xmlParser.nextText());
							} else if ("Password".equals(xmlParser.getName())) {
								ftpDownTaskModel.setPass(xmlParser.nextText());
								addFtp(ftpDownTaskModel);
							} else if ("IsPassiveMode".equals(xmlParser.getName())) {
								ftpDownTaskModel.setIsPassiveMode(parseInt(xmlParser.nextText(), 0));
								isFTPHostSetting = false;
							}
						} else if ("DownloadFile".equals(xmlParser.getName())) {
							ftpDownTaskModel.setRemoteFile(xmlParser.nextText());
						} else if ("DownloadTimeout".equals(xmlParser.getName())) {
							ftpDownTaskModel.setTimeOut(parseInt(xmlParser.nextText(), 300000) / 1000);
						} else if ("PSCallMode".equals(xmlParser.getName())) {
							ftpDownTaskModel.setPsCall(parseInt(xmlParser.nextText(), 0));
						} else if ("NoDataTimeout".equals(xmlParser.getName())) {
							ftpDownTaskModel.setNoAnswer(parseInt(xmlParser.nextText(), 60));
						} else if ("ThreadCount".equals(xmlParser.getName())) { // 登录超时
							ftpDownTaskModel.setThreadNumber(parseInt(xmlParser.nextText(), 1));
						}

					}

					// 语音解析
					if ("MOCTestConfig".equals(xmlParser.getName())) {
						callTaskModel = new TaskInitiativeCallModel();
						callTaskModel.setTaskName("LongCallTest");
						callTaskModel.setTaskType(WalkStruct.TaskType.InitiativeCall.name());
						callTaskModel.setMosTest(0);
					}
					if (callTaskModel != null) {
						if ("ExecCount".equals(xmlParser.getName())) {
							callTaskModel.setRepeat(parseInt(xmlParser.nextText(), 10));
						} else if ("DialNumber".equals(xmlParser.getName())) {
							callTaskModel.setCallNumber(xmlParser.nextText());
						} else if ("Inteval".equals(xmlParser.getName())) {
							callTaskModel.setInterVal(parseInt(xmlParser.nextText(), 10000) / 1000);
						} else if ("ConnectionTime".equals(xmlParser.getName())) {
							callTaskModel.setConnectTime(parseInt(xmlParser.nextText(), 30000) / 1000);
						} else if ("Duration".equals(xmlParser.getName())) {
							callTaskModel.setKeepTime(parseInt(xmlParser.nextText(), 60000) / 1000);
						} else if ("LongCall".equals(xmlParser.getName())) {
							callTaskModel.setTaskName(parseInt(xmlParser.nextText(), 0) == 0 ? "ShortCallTest" : "LongCallTest");
						}

					}

					break;
				case XmlPullParser.END_TAG:
					if ("FTPUploadTestConfig".equals(xmlParser.getName())) {
						order.getTaskList().add(ftpTaskModel);
						ftpTaskModel = null;
					}
					if ("FTPDownloadTestConfig".equals(xmlParser.getName())) {
						order.getTaskList().add(ftpDownTaskModel);
						ftpDownTaskModel = null;
					}
					if ("MOCTestConfig".equals(xmlParser.getName())) {
						order.getTaskList().add(callTaskModel);
						callTaskModel = null;
					}
					break;
				default:
					break;
				}
				eventType = xmlParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加解析后的Ftp到Ftp设置列表中
	 * 
	 * @param ftpTaskModel
	 *          ftp模型类
	 */
	private void addFtp(TaskFtpModel ftpTaskModel) {
		ConfigFtp configFtp = new ConfigFtp();
		String ftpIp = ftpTaskModel.getFtpServerName();
		FtpServerModel ftpServerModel = new FtpServerModel(ftpIp, ftpIp, ftpTaskModel.getPort() + "",
				ftpTaskModel.getUser(), ftpTaskModel.getPass());
		if (!configFtp.contains(ftpIp)) {
			configFtp.addFtp(ftpServerModel);
		}
	}

	/**
	 * 解析xml中的整型，但是可能会为空或者为字符，将会造成异常
	 * 
	 * @param value
	 *          要转化的字符串
	 * @param defaultVal
	 *          如果异常，则默认值设置为什么
	 * @return 转化后的整型 zhihui.lian
	 */
	protected int parseInt(String value, int defaultVal) {
		int result;
		try {
			result = Integer.parseInt(value);
			// 0为无效值，需要改为默认值
			if (result == 0) {
				result = defaultVal;
			}
		} catch (Exception e) {
			result = defaultVal;
		}
		return result;
	}

	/**
	 * 获取json对象的指定属性值
	 * 
	 * @param obj
	 *          json对象
	 * @param name
	 *          属性名
	 * @param isNum
	 *          属性值是否为数值
	 * @return
	 * @throws JSONException
	 */
	private String getString(JSONObject obj, String name, boolean isNum) throws JSONException {
		String value = "";
		if (obj.has(name))
			value = obj.getString(name).trim();
		if ("null".equals(value))
			value = "";
		if (value.length() == 0 && isNum)
			value = "0";
		return value;
	}

	/**
	 * 获取json对象的指定属性值
	 * 
	 * @param obj
	 *          json对象
	 * @param name
	 *          属性名
	 * @return
	 * @throws JSONException
	 */
	private boolean getBoolean(JSONObject obj, String name) throws JSONException {
		String value = "";
		if (obj.has(name))
			value = obj.getString(name).trim();
		if ("true".equals(value))
			return true;
		return false;
	}

	/**
	 * 读取json工单信息点
	 * 
	 * @param order
	 *          工单对象
	 * @param points
	 *          信息点元素
	 */
	private void readJsonPoints(WorkOrder order, JSONArray points) {
		try {
			for (int i = 0; i < points.length(); i++) {
				JSONObject point = points.getJSONObject(i);
				WorkOrderPoint pointObj = new WorkOrderPoint();
				pointObj.setAddress(this.getString(point, "Address", false));
				pointObj.setCity(this.getString(point, "City", false));
				pointObj.setName(this.getString(point, "Name", false));
				pointObj.setLongitude(Double.parseDouble(this.getString(point, "Longitude", true)));
				pointObj.setLatitude(Double.parseDouble(this.getString(point, "Latitude", true)));
				pointObj.setPointID(this.getString(point, "PointID", false));
				pointObj.setRegion(this.getString(point, "Region", false));
				pointObj.setTestTask(this.getString(point, "TestTask", false));
				pointObj.setType(this.getString(point, "Type", false));
				pointObj.setClosed(this.getBoolean(point, "isClosed"));
				this.orderMap.put(pointObj.getPointID(), order);
				this.pointMap.put(pointObj.getPointID(), pointObj);
				order.getPointList().add(pointObj);
				pointObj.setOrder(order);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获得工单列表
	 * 
	 * @return
	 */
	public List<WorkOrder> getOrderList() {
		return orderList;
	}

	/**
	 * 根据工单编号获得工单对象
	 * 
	 * @param no
	 *          工单编号
	 * @return 工单对象
	 */
	public WorkOrder getOrderByNo(String no) {
		for (WorkOrder order : this.orderList) {
			if (order.getWorkItemCode().equals(no)) {
				return order;
			}
		}
		return null;
	}

	/**
	 * 根据工单编号和信息点编号获取信息点对象
	 * 
	 * @param orderNo
	 *          工单编号
	 * @param no
	 *          信息点编号
	 * @return 信息点对象
	 */
	public WorkOrderPoint getPointByNo(String orderNo, String no) {
		for (WorkOrder order : this.orderList) {
			if (order.getWorkItemCode().equals(orderNo)) {
				for (WorkOrderPoint point : order.getPointList()) {
					if (point.getPointID().equals(no))
						return point;
				}
			}
		}
		return null;
	}

	/**
	 * 保存下载的json数据
	 * 
	 * @param json
	 *          数据
	 */
	public void saveJsonString(String json) {
		if (json == null || json.trim().length() == 0)
			return;
		File file = new File(jsonFileName);
		try {
			if (!file.exists())
				file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(json.getBytes());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.init();
	}

	/**
	 * 根据信息点编号获得所属工单对象
	 * 
	 * @param pointNo
	 *          信息点编号
	 * @return 工单对象
	 */
	public WorkOrder getOrderByPointNo(String pointNo) {
		if (this.orderMap.containsKey(pointNo))
			return this.orderMap.get(pointNo);
		return null;
	}

	/**
	 * 根据信息点编号获得所属信息点对象
	 * 
	 * @param pointNo
	 *          信息点编号
	 * @return 信息点对象
	 */
	public WorkOrderPoint getPointByPointNo(String pointNo) {
		if (this.pointMap.containsKey(pointNo))
			return this.pointMap.get(pointNo);
		return null;
	}

	/**
	 * 关闭信息点
	 * 
	 * @param point
	 *          信息点对象
	 */
	public void closePoint(WorkOrderPoint point) {
		point.setClosed(true);
		this.writeJsonToFile();
	}

	/**
	 * 手工新增信息点
	 * 
	 * @param point
	 *          信息点对象
	 */
	public void createPoint(WorkOrderPoint point) {
		point.setCreate(true);
		WorkOrder order = point.getOrder();
		this.orderMap.put(point.getPointID(), order);
		this.pointMap.put(point.getPointID(), point);
		this.writeXmlToFile();
	}

	/**
	 * 把当前的对象写到Json文件中
	 */
	private void writeJsonToFile() {
		File file = new File(jsonFileName);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			JSONArray orders = this.writeJsonOrders();
			out.write(orders.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存工单数据到文件中
	 */
	public void writeXmlToFile() {
		if (this.orderList.isEmpty())
			return;
		FileOutputStream os = null;
		try {
			File file = new File(xmlFileName);
			if (!file.exists())
				file.createNewFile();
			os = new FileOutputStream(file);
			this.serializer = Xml.newSerializer();
			this.serializer.setOutput(os, "utf-8");
			this.serializer.startDocument("utf-8", true);
			this.serializer.startTag(null, "WorkOrderList");
			for (WorkOrder order : this.orderList) {
				this.writeXmlOrder(order);
			}
			this.serializer.endTag(null, "WorkOrderList");
			this.serializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 写xml工单对象
	 * 
	 * @param order
	 *          工单对象
	 * @throws
	 */
	private void writeXmlOrder(WorkOrder order) throws Exception {
		int count = 0;
		for (WorkOrderPoint point : order.getPointList()) {
			if (point.isCreate())
				count++;
		}
		if (count == 0)
			return;
		this.serializer.startTag(null, "WorkItemInfo");
		this.writeValue("WorkItemCode", order.getWorkItemCode());
		this.writeValue("Name", order.getName());
		this.serializer.startTag(null, "CQTPointList");
		for (WorkOrderPoint point : order.getPointList()) {
			if (point.isCreate())
				this.writeXmlPoint(point);
		}
		this.serializer.endTag(null, "CQTPointList");
		this.serializer.endTag(null, "WorkItemInfo");
	}

	/**
	 * 写xml信息点对象
	 * 
	 * @param point
	 *          信息点对象
	 * @throws Exception
	 */
	private void writeXmlPoint(WorkOrderPoint point) throws Exception {
		this.serializer.startTag(null, "CQTPointInfo");
		this.writeValue("Name", point.getName());
		this.writeValue("PointID", point.getPointID());
		this.serializer.endTag(null, "CQTPointInfo");
	}

	/**
	 * 写工单值
	 * 
	 * @param tagName
	 *          属性名
	 * @param value
	 *          属性值
	 * @throws Exception
	 */
	private void writeValue(String tagName, String value) throws Exception {
		this.serializer.startTag(null, tagName);
		this.serializer.text(value);
		this.serializer.endTag(null, tagName);
	}

	/**
	 * 生成工单对象JSON数组
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONArray writeJsonOrders() throws JSONException {
		JSONArray orders = new JSONArray();
		for (WorkOrder order : this.orderList) {
			JSONObject orderObj = new JSONObject();
			orderObj.put("WorkItemCode", order.getWorkItemCode());
			orderObj.put("Name", order.getName());
			orderObj.put("Description", order.getDescription());
			orderObj.put("WorkItemID", order.getWorkItemID());
			orderObj.put("PlanToStart", order.getPlanToStart());
			orderObj.put("PlanToFinish", order.getPlanToFinish());
			this.writeJsonTasks(order, orderObj);
			int count = 0;
			for (WorkOrderPoint point : order.getPointList()) {
				if (!point.isCreate())
					count++;
			}
			if (count > 0)
				orderObj.put("CQTPointList", this.writeJsonPoints(order));
			orders.put(orderObj);
		}
		return orders;
	}

	/**
	 * 生成任务JSON对象
	 * 
	 * @param order
	 *          工单对象
	 * @param orderObj
	 *          json对象
	 * @throws JSONException
	 */
	private void writeJsonTasks(WorkOrder order, JSONObject orderObj) throws JSONException {
		JSONObject taskObj = new JSONObject();
		for (String key : order.getTaskXmlMap().keySet()) {
			taskObj.put(key, order.getTaskXmlMap().get(key));
		}
		orderObj.put("TestTask", taskObj);
	}

	/**
	 * 生成信息点对象JSON数组
	 * 
	 * @param order
	 *          工单对象
	 * @return
	 * @throws JSONException
	 */
	private JSONArray writeJsonPoints(WorkOrder order) throws JSONException {
		JSONArray points = new JSONArray();
		for (WorkOrderPoint point : order.getPointList()) {
			if (point.isCreate())
				continue;
			JSONObject pointObj = new JSONObject();
			pointObj.put("PointID", point.getPointID());
			pointObj.put("Name", point.getName());
			pointObj.put("Type", point.getType());
			pointObj.put("TestTask", point.getTestTask());
			pointObj.put("Region", point.getRegion());
			pointObj.put("City", point.getCity());
			pointObj.put("Address", point.getAddress());
			pointObj.put("isClosed", point.isClosed());
			points.put(pointObj);
		}
		return points;
	}
}

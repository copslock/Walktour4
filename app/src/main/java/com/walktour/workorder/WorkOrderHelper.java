package com.walktour.workorder;

import android.content.Context;
import android.util.Log;

import com.dinglicom.UnicomInterface;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.workorder.model.ServerInfo;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkOrderDict;
import com.walktour.workorder.model.WorkOrderList;
import com.walktour.workorder.model.WorkSubItem;
import com.walktour.workorder.model.WorkSubItem.CommandItem;
import com.walktour.workorder.parser.ServerInfoParser;
import com.walktour.workorder.parser.WorkOrderDetailParser;
import com.walktour.workorder.parser.WorkOrderDictParser;
import com.walktour.workorder.parser.WorkOrderListParser;
import com.walktour.workorder.parser.XmlParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单列表页面
 * @deprecated 不用了，替换成dal的具体Helper
 * Author: ZhengLei
 *   Date: 2013-6-7 下午9:15:10
 */
public class WorkOrderHelper {
	private static final String TAG = "WorkOrderHelper";
	public static final int DEFAULT_LOOP_TIME = 1; // 默认循环1次
	public static final int DEFAULT_LOOP_INTERVAL = 10; // 默认间隔时间为5秒
	public static final String DEFAULT_BUILDING_NAME = "Buildings"; // 默认建筑物名称
	
	private static WorkOrderHelper instance = null;
	private XmlParser xmlParser;
	// 所有服务器类型
	private int[] servers = new int[]{	UnicomInterface.TASK_SERVER, 
										UnicomInterface.FTP_UPLOAD_SERVER,
										UnicomInterface.FTP_DOWNLOAD_SERVER,
//										UnicomInterface.HTTP_UPLOAD_SERVER,
//										UnicomInterface.HTTP_DOWNLOAD_SERVER
									};
	private Context context;
	
	private WorkOrderHelper(Context context) {
		this.context = context;
	}
	
	/**
	 * 单例
	 */
	public synchronized static WorkOrderHelper newInstance(Context context) {
		if(instance == null) {
			return instance = new WorkOrderHelper(context);
		}
		return instance;
	}

	// ------------------------------------以下方法为调用jni获得xml------------------------------------
	public String getResourceServerByLib(String server, int port, int type) {
		return UnicomInterface.getResourceServer(server, port, type);
	}
	
	public boolean initTaskServerByLib(String server, int port, String username, String password) {
		return UnicomInterface.initTaskServer(server, port, username, password);
	}
	
	// ------------以下三个方法，必须先登录------------
	/**
	 * 查询工单类型字典
	 * @return 工单字典xml，参见dict.xml
	 */
	public String getWorkTypeDictByLib() {
		return UnicomInterface.getWorkTypeDict();
	}
	
	/**
	 * 查询工单列表
	 * @param workType 工单类型
	 * @return 工单列表的xml，参见list.xml
	 */
	public String getWorkOrderListByLib(int workType) {
		return UnicomInterface.getWorkOrderList(workType);
	}
	
	/**
	 * 查询工单明细
	 * @param workId 工单Id
	 * @return 工单明细的xml，参见detail.xml
	 */
	public String getWorkOrderDetailByLib(int workId) {
		return UnicomInterface.getWorkOrderDetail(workId);
	}
	
	// ------------------------------------以下方法为解析xml，赋值给实体类------------------------------------
	/**
	 * 获取服务器（业务、回传等五种服务器）相关信息
	 * @param server 公开的ip
	 * @param port 公开的端口
	 * @param type 服务器类型，UnicomInterface中的五个常量
	 * @return 服务器信息实体类
	 * @throws ClassCastException
	 */
	public ServerInfo getResourceServer(String server, int port, int type) throws ClassCastException {
//		xmlParser = new ServerInfoParser("/mnt/sdcard/server.txt"); // 用于测试，由于jni那块暂未写好，只提供了xml文件供开发测试
		String resSrvXml = getResourceServerByLib(server, port, type);
//		Log.i(TAG, "xml parse result by lib:" + resSrvXml);
		if(resSrvXml==null || "".equals(resSrvXml)) return null;
		xmlParser = new ServerInfoParser(new StringReader(resSrvXml));
		xmlParser.parse();
		
		Object obj = xmlParser.getParseResult();
		Log.i(TAG, obj.toString());
		
		return (ServerInfo)obj;
	}
	
	/**
	 * 初始化（相当于登录）业务服务器
	 * @param info 服务器信息模型
	 * @return 是否初始化成功
	 */
	public boolean initTaskServer(ServerInfo info) {
		if(info != null) {
			return initTaskServerByLib(info.getIpAddr(), info.getPort(), info.getAccount(), info.getPassword());
		}
		return false;
	}

	/**
	 * 获取所有的资源服务器信息
	 * @param server
	 * @param port
	 * @return 资源服务器List
	 */
	public List<ServerInfo> getAllResourceServer(String server, int port) {
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		for(int i=0; i<servers.length; i++) {
			ServerInfo info = getResourceServer(server, port, servers[i]);
			if(info != null) {
				serverInfos.add(info);
			}
		}
		return serverInfos;
	}

	/**
	 * 解析工单类型字典
	 * @return 工单字典实体类对象
	 */
	public WorkOrderDict getWorkTypeDict() {
//		xmlParser = new WorkOrderDictParser("/mnt/sdcard/dict.txt"); // 用于测试，由于jni那块暂未写好，只提供了xml文件供开发测试
		
		String dictXml = getWorkTypeDictByLib();
		if(dictXml==null || "".equals(dictXml)) return null;
		xmlParser = new WorkOrderDictParser(new StringReader(dictXml));
		xmlParser.parse();
		
		Log.i(TAG, xmlParser.getParseResult().toString());
		return (WorkOrderDict)xmlParser.getParseResult();
		
	}
	/**
	 * 解析工单列表
	 * @return 工单列表实体类对象
	 */
	public WorkOrderList getWorkOrderList(int workType) {
//		xmlParser = new WorkOrderListParser("/mnt/sdcard/list.txt"); // 用于测试，由于jni那块暂未写好，只提供了xml文件供开发测试
		
		String listXml = getWorkOrderListByLib(workType);
		if(listXml==null || "".equals(listXml)) return null;
		xmlParser = new WorkOrderListParser(new StringReader(listXml));
		xmlParser.parse();
		
		Log.i(TAG, xmlParser.getParseResult().toString());
		return (WorkOrderList)xmlParser.getParseResult();
		
	}

	/**
	 * 解析工单明细
	 * @param workId 工单Id
	 * @return 工单字典实体类对象
	 */
	public WorkOrderDetail getWorkOrderDetail(int workId) {
//		xmlParser = new WorkOrderDetailParser("/mnt/sdcard/detail.txt"); // 用于测试，由于jni那块暂未写好，只提供了xml文件供开发测试
		
		String detailXml = getWorkOrderDetailByLib(workId);
		if(detailXml==null || "".equals(detailXml)) return null;
		xmlParser = new WorkOrderDetailParser(new StringReader(detailXml));
		xmlParser.parse();
		
		Log.i(TAG, xmlParser.getParseResult().toString());
		return (WorkOrderDetail)xmlParser.getParseResult();
		
	}
	
	/**
	 * 获取工单子项列表，即detail.xml中的多个<WorkSubItems>标签
	 * @param workId 工单Id
	 * @return 工单子项的List
	 */
	public List<WorkSubItem> getWorkSubItems(int workId) {
		WorkOrderDetail detail = getWorkOrderDetail(workId);
		return detail!=null ? detail.getWorkSubItems() : null;
	}
	
	/**
	 * 获取工单子项单个对象
	 * @param workId 工单Id
	 * @return 工单子项的List
	 */
	public WorkSubItem getWorkSubItem(int workId, int workSubId) {
		List<WorkSubItem> workSubItems = getWorkOrderDetail(workId).getWorkSubItems();
		return getWorkSubItem(workSubItems, workSubId);
	}
	
	/**
	 * 查找工单子项集合中为某一workSubId的工单子项
	 * @param workSubItems 工单子项List
	 * @param workSubId 工单子项ID
	 * @return 工单子项对象
	 */
	private WorkSubItem getWorkSubItem(List<WorkSubItem> workSubItems, int workSubId) {
		for(WorkSubItem sub : workSubItems) {
			if(sub.getItemId() == workSubId) {
				return sub; // 暂定ItemsID无重复
			}
		}
		return null;
	}
	
	/**
	 * 查找某一工单下的某一workSubId的工单子项
	 * @param detail 工单对象
	 * @param workSubId 工单子项ID
	 * @return 工单子项对象
	 */
	private WorkSubItem getWorkSubItem(WorkOrderDetail detail, int workSubId) {
		if(detail == null) return null;
		return getWorkSubItem(detail.getWorkSubItems(), workSubId);
	}
	
	/**
	 * 根据选择的工单子项的位置，获得工单子项对应的ID
	 * @param position 单选框选择的位置
	 * @return 选择的工单子项ID
	 */
	public int getSelectedSubId(WorkOrderDetail detail, int position) {
		if(detail == null) return 0;
		return detail.getWorkSubItems().get(position).getItemId();
	}
	
	/*start***********************************获取TaskModel的List的重载方法***********************************start*/
	/**
	 * 获得任务模型ArrayList
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @param workSubId 项Id，即detail.xml中的<ItemsID>标签。即工单子项的Id。即工单子项的Id
	 * @return 任务模型基类的List
	 */
	public ArrayList<TaskModel> getTask(int workId, int workSubId) {
		List<CommandItem> commandItems = new ArrayList<CommandItem>();
		WorkSubItem sub = getWorkSubItem(workId, workSubId);
		if(sub != null) {
			commandItems = sub.getCommandItems();
		}
		return getTask(commandItems);
	}
	
	/**
	 * 获得任务模型，通过CommandItem的List转换为TaskModel的List
	 * @param commandItems 工单子项List
	 * @return 任务List
	 */
	private ArrayList<TaskModel> getTask(List<CommandItem> commandItems) {
		ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
		for(CommandItem item : commandItems) {
			taskModels.add(item.getTaskModel());
		}
		return taskModels;
	}
	
	// 以下两个接口是不访问库，所以可认为无延迟，不用启新线程
	public ArrayList<TaskModel> getTask(WorkOrderDetail detail, int workSubId) {
		if(detail == null) return null;
		WorkSubItem sub = getWorkSubItem(detail.getWorkSubItems(), workSubId);
		return getTask(sub);
	}
	
	private ArrayList<TaskModel> getTask(WorkSubItem sub) {
		if(sub == null) return null;
		return getTask(sub.getCommandItems());
	}
	/*end***********************************获取TaskModel的List的重载方法***********************************end*/
	
	/**
	 * 获取外循环次数，即Walktour“开始测试”对话框中“外循环次数”，对应detail.txt中的<LoopSum>标签
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @param workSubId 项Id，即detail.xml中的<ItemsID>标签。即工单子项的Id
	 * @return 外循环次数
	 */
	private int getLoopTime(int workId, int workSubId) {
		WorkSubItem sub = getWorkSubItem(workId, workSubId);
		return (sub!=null) ? sub.getLoopSum() : DEFAULT_LOOP_TIME;
	}
	
	/**
	 * 功能同上，但是本方法无阻塞，不从网络获取，而是从传过来的工单对象中查找
	 * @param detail 工单对象
	 * @param workSubId 工单子项ID
	 * @return
	 */
	public int getLoopTime(WorkOrderDetail detail, int workSubId) {
		WorkSubItem  sub = getWorkSubItem(detail, workSubId);
		return (sub!=null) ? sub.getLoopSum() : DEFAULT_LOOP_TIME;
	}
	
	/**
	 * 获取外循环间隔（单位秒），对应detail.txt中的<LoopInterval>标签
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @param workSubId 项Id，即detail.xml中的<ItemsID>标签。即工单子项的Id
	 * @return 外循环间隔秒数
	 */
	private int getLoopInterval(int workId, int workSubId) {
		WorkSubItem sub = getWorkSubItem(workId, workSubId);
		return (sub!=null) ? sub.getLoopInterval() : DEFAULT_LOOP_INTERVAL;
	}
	
	/**
	 * 功能同上，但是本方法无阻塞，不从网络获取，而是从传过来的工单对象中查找
	 * @param detail 工单对象
	 * @param workSubId 工单子项ID
	 * @return
	 */
	public int getLoopInterval(WorkOrderDetail detail, int workSubId) {
		WorkSubItem  sub = getWorkSubItem(detail, workSubId);
		return (sub!=null) ? sub.getLoopInterval() : DEFAULT_LOOP_INTERVAL;
	}
	
	/**
	 * 获取建筑物名称，对应detail.txt中的<TestBuilding>标签
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @return 建筑物名称
	 */
	private String getBuilding(int workId) {
		// 下面的这种方法返回的是一长串字符，方杰说解不了，所以不用这个作为地图的名称
//		return getWorkOrderDetail(workId).getTestBuilding();
		
		// 这种方法是取楼层（如<FloorMap>Buildings/北京市_三区_中宣部扩容3号_地上1层_G.png</FloorMap>）
		// 中“Buildings”这个字符串作为建筑物名称
		
		// 获得工单子项的List，即xml中<WorkSubItems>标签
		List<WorkSubItem> workSubItems = getWorkSubItems(workId);
		if(workSubItems.size() > 0) {
			String floorMap = workSubItems.get(0).getFloorMap();
			return floorMap.substring(0, floorMap.indexOf("/"));
		}
		return DEFAULT_BUILDING_NAME;
	}
	
	/**
	 * 功能同上，但是本方法无阻塞，不从网络获取，而是从传过来的工单对象中查找
	 * @param detail 工单对象
	 * @return
	 */
	public String getBuilding(WorkOrderDetail detail) {
		List<WorkSubItem> workSubItems = detail.getWorkSubItems();
		if(workSubItems.size() > 0) {
			String floorMap = workSubItems.get(0).getFloorMap();
			return floorMap.substring(0, floorMap.indexOf("/"));
		}
		return DEFAULT_BUILDING_NAME;
	}
	
	/**
	 * 获取楼层名称，对应detail.txt中的<TestFloors>标签
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @param workSubId 项Id，即detail.xml中的<ItemsID>标签。即工单子项的Id
	 * @return 楼层名称名称
	 */
	private String getFloor(int workId, int workSubId) {
		WorkSubItem sub = getWorkSubItem(workId, workSubId);
		return (sub!=null) ? sub.getTestFloors() : null;
	}
	
	/**
	 * 功能同上，但是本方法无阻塞，不从网络获取，而是从传过来的工单对象中查找
	 * @param detail 工单对象
	 * @param workSubId 工单子项ID
	 * @return
	 */
	public String getFloor(WorkOrderDetail detail, int workSubId) {
		WorkSubItem  sub = getWorkSubItem(detail, workSubId);
		return (sub!=null) ? sub.getTestFloors() : null;
	}
	
	/**
	 * 获取楼层地图，对应detail.txt中的<FloorMap>标签
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @param workSubId 项Id，即detail.xml中的<ItemsID>标签。即工单子项的Id
	 * @return 楼层地图名称
	 */
	private String getFloorMap(int workId, int workSubId) {
		WorkSubItem sub = getWorkSubItem(workId, workSubId);
		return (sub!=null) ? sub.getFloorMap() : null;
	}
	
	/**
	 * 功能同上，但是本方法无阻塞，不从网络获取，而是从传过来的工单对象中查找
	 * @param detail 工单对象
	 * @param workSubId 工单子项ID
	 * @return
	 */
	public  String getFloorMap(WorkOrderDetail detail, int workSubId) {
		WorkSubItem  sub = getWorkSubItem(detail, workSubId);
		return (sub!=null) ? sub.getFloorMap() : null;
	}
	
	/**
	 * 从Sharereferences中获取Ftp的基地址
	 * @return 基地址，如ftp://219.216.103.8:21/
	 */
	private String getBaseUrl() {
		// 从Sharereferences中获取Ftp的地址
		ServerInfo info = ServerManager.getInstance(context).readUnicomServer(UnicomInterface.FTP_DOWNLOAD_SERVER);
		String url = null;
		if(info != null) {
			url = "ftp://" + info.getIpAddr() + ":" + info.getPort() + "/";
		}
		return url;
	}
	
	/**
	 * 获取楼层地图的下载Url地址
	 * @param workId 工单Id，即detail.xml中的<WorkID>标签
	 * @param workSubId 项Id，即detail.xml中的<ItemsID>标签。即工单子项的Id
	 * @return 楼层地图的下载Url地址
	 */
	private String getFloorMapUrl(int workId, int workSubId) {
		String baseUrl = getBaseUrl();
		if(baseUrl == null) return null;
		return baseUrl + getFloorMap(workId, workSubId);
	}
	
	/**
	 * 功能同上，但是本方法无阻塞，不从网络获取，而是从传过来的工单对象中查找
	 * @param detail 工单对象
	 * @param workSubId 工单子项ID
	 * @return
	 */
	public String getFloorMapUrl(WorkOrderDetail detail, int workSubId) {
		String baseUrl = getBaseUrl();
		if(baseUrl == null) return null;
		return baseUrl + getFloorMap(detail, workSubId);
	}

}

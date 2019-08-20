package com.walktour.workorder.model;

import com.walktour.Utils.WalkCommonPara;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单子项，一个工单详细（WorkOrderDetail）里包含多个工单子项
 * Author: ZhengLei
 *   Date: 2013-6-7 上午11:06:26
 */
public class WorkSubItem implements Serializable{
	private static final long serialVersionUID = -7060210544600464488L; 
	private int itemId;
	private String itemName;
	private String testContent;
	private String siteId;
	private String siteName;
	private double siteGpsLat;
	private double siteGpsLon;
	private String siteAddress;
	private String testFloors;
	private int testScene;
	private int testType;
	private String floorMap;
	private int serverType;
	private int loopSum;
	private int loopInterval;
	private int itemsCount;
	// 命令项
	private List<CommandItem> commandItems;

	// 测试类型，CS、PS、混合
	public static final int TEST_TYPE_UNKNOWN = -1;
	public static final int TEST_TYPE_COMPLEX = 0;
	public static final int TEST_TYPE_CS = 1;
	public static final int TEST_TYPE_PS = 2;
	private int workTestType = TEST_TYPE_UNKNOWN;

	public WorkSubItem() {
		super();
		commandItems = new ArrayList<CommandItem>();
	}
	
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getTestContent() {
		return testContent;
	}

	public void setTestContent(String testContent) {
		this.testContent = testContent;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public double getSiteGpsLat() {
		return siteGpsLat;
	}

	public void setSiteGpsLat(double siteGpsLat) {
		this.siteGpsLat = siteGpsLat;
	}

	public double getSiteGpsLon() {
		return siteGpsLon;
	}

	public void setSiteGpsLon(double siteGpsLon) {
		this.siteGpsLon = siteGpsLon;
	}

	public String getSiteAddress() {
		return siteAddress;
	}

	public void setSiteAddress(String siteAddress) {
		this.siteAddress = siteAddress;
	}

	public String getTestFloors() {
		return testFloors;
	}

	public void setTestFloors(String testFloors) {
		this.testFloors = testFloors;
	}

	public int getTestScene() {
		return testScene;
	}

	public void setTestScene(int testScene) {
		this.testScene = testScene;
	}

	public int getTestType() {
		return testType;
	}

	public void setTestType(int testType) {
		this.testType = testType;
	}

	public String getFloorMap() {
		return floorMap;
	}

	public void setFloorMap(String floorMap) {
		this.floorMap = floorMap;
	}

	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	public int getLoopSum() {
		return loopSum;
	}

	public void setLoopSum(int loopSum) {
		this.loopSum = loopSum;
	}

	public int getLoopInterval() {
		return loopInterval;
	}

	public void setLoopInterval(int loopInterval) {
		this.loopInterval = loopInterval;
	}

	public int getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(int itemsCount) {
		this.itemsCount = itemsCount;
	}

	public List<CommandItem> getCommandItems() {
		return commandItems;
	}

	public void setCommandItems(List<CommandItem> commandItems) {
		this.commandItems = commandItems;
	}

	/**
	 * 业务类型，如CS、PS、混合业务
	 * @return 业务类型
	 */
	public int getWorkTestType() {
		boolean cs = false;
		boolean ps = false;
		for(CommandItem item : commandItems) {
			int type = item.getTaskModel().getTypeProperty();
			switch (type) {
				case WalkCommonPara.TypeProperty_Voice:
					cs = true;
					break;
				case WalkCommonPara.TypeProperty_Net:
					ps = true;
					break;
				default:
					break;
			}
		}
		if(cs && ps) workTestType = TEST_TYPE_COMPLEX;
		else if(cs && !ps) workTestType = TEST_TYPE_CS;
		else if(!cs && ps) workTestType = TEST_TYPE_PS;
		else if(!cs && !ps) workTestType = TEST_TYPE_UNKNOWN;
		return workTestType;
	}

	@Override
	public String toString() {
		return "WorkSubItem [itemId=" + itemId + ", itemName=" + itemName
				+ ", testContent=" + testContent + ", siteId=" + siteId
				+ ", siteName=" + siteName + ", siteGpsLat=" + siteGpsLat
				+ ", siteGpsLon=" + siteGpsLon + ", siteAddress=" + siteAddress
				+ ", testFloors=" + testFloors + ", testScene=" + testScene
				+ ", testType=" + testType + ", floorMap=" + floorMap
				+ ", serverType=" + serverType + ", loopSum=" + loopSum
				+ ", loopInterval=" + loopInterval + ", itemsCount="
				+ itemsCount + ", commandItems=" + commandItems + "]";
	}

	public class CommandItem  implements Serializable{
		private static final long serialVersionUID = -7060210544600464489L; 
		private int commandID;
		private String commandName;
		private int commandOrder;
		private String commandType; //命令描述类型，比如FTP,CALL PING等 李方杰 2013/9/24
		// 任务模型
		private TaskModel taskModel;
		
		public CommandItem() {
			super();
		}

		public int getCommandID() {
			return commandID;
		}

		public void setCommandID(int commandID) {
			this.commandID = commandID;
		}

		public String getCommandName() {
			return commandName;
		}
		public void setCommandDesc(String desc ) {
			this.commandType = desc;
		}
		public String getCommandDesc() {
			return this.commandType;
		}
		
		public void setCommandName(String commandName) {
			this.commandName = commandName;
		}

		public int getCommandOrder() {
			return commandOrder;
		}

		public void setCommandOrder(int commandOrder) {
			this.commandOrder = commandOrder;
		}

		public TaskModel getTaskModel() {
			return taskModel;
		}

		public void setTaskModel(TaskModel taskModel) {
			this.taskModel = taskModel;
		}

		@Override
		public String toString() {
			return "CommandItem [commandID=" + commandID + ", commandName="
					+ commandName + ", commandOrder=" + commandOrder
					+ ", taskModel=" + taskModel + "]";
		}

	} // end "public class CommandItem"
}

package com.walktour.workorder.dal;

import com.dinglicom.UnicomInterface;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkSubItem;
import com.walktour.workorder.model.WorkSubItem.CommandItem;
import com.walktour.workorder.model.XmlFileType;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单详情的数据访问层类
 * Author: ZhengLei
 *   Date: 2013-6-20 下午5:32:32
 */
public class WorkOrderDetailHelper extends BaseHelper implements IQueryHelper {
	public static final int DEFAULT_LOOP_TIME = 1; // 默认循环1次
	public static final int DEFAULT_LOOP_INTERVAL = 10; // 默认间隔时间为5秒
	public static final String DEFAULT_BUILDING_NAME = "Buildings"; // 默认建筑物名称
	
	private int workId;
	
	/**
	 * 构造方法
	 */
	public WorkOrderDetailHelper() {
		super();
	}
	
	public WorkOrderDetailHelper(int workId) {
		this.workId = workId;
	}

	@Override
	public String getXmlByLib() {
		return UnicomInterface.getWorkOrderDetail(workId);
	}

	@Override
	public boolean exist(String fileName) {
		return super.exist(fileName);
	}

	@Override
	public boolean saveXmlAsFile(String content, String fileName) {
		return super.saveXmlAsFile(content, fileName);
	}

	@Override
	public Object getContentFromFile(String fileName, XmlFileType type) {
		return super.getContentFromFile(fileName, type);
	}

	@Override
	public List<TaskModel> getTask(WorkOrderDetail detail, int subId) {
		if(detail == null) return null;
		WorkSubItem sub = getWorkSubItem(detail.getWorkSubItems(), subId);
		return getTask(sub);
	}

	@Override
	public List<TaskModel> getTask(WorkSubItem sub) {
		if(sub == null) return null;
		return getTask(sub.getCommandItems());
	}

	@Override
	public List<TaskModel> getTask(List<CommandItem> commandItems) {
		ArrayList<TaskModel> taskModels = new ArrayList<TaskModel>();
		for(CommandItem item : commandItems) {
			if (item.getTaskModel() != null){
				taskModels.add(item.getTaskModel());
			}
		}
		return taskModels;
	}

	@Override
	public WorkSubItem getWorkSubItem(WorkOrderDetail detail, int subId) {
		if(detail == null) return null;
		return getWorkSubItem(detail.getWorkSubItems(), subId);
	}

	@Override
	public WorkSubItem getWorkSubItem(List<WorkSubItem> subItems, int subId) {
		for(WorkSubItem sub : subItems) {
			if(sub.getItemId() == subId) {
				return sub; // 暂定ItemsID无重复
			}
		}
		return null;
	}

	@Override
	public int getSubId(WorkOrderDetail detail, int position) {
		if(detail == null) return 0;
		return detail.getWorkSubItems().get(position).getItemId();
	}

	@Override
	public int getLoopTime(WorkOrderDetail detail, int subId) {
		WorkSubItem sub = getWorkSubItem(detail, subId);
		return (sub!=null) ? sub.getLoopSum() : DEFAULT_LOOP_TIME;
	}

	@Override
	public int getLoopInterval(WorkOrderDetail detail, int subId) {
		WorkSubItem  sub = getWorkSubItem(detail, subId);
		return (sub!=null) ? sub.getLoopInterval() : DEFAULT_LOOP_INTERVAL;
	}

	@Override
	public String getBuilding(WorkOrderDetail detail) {
		List<WorkSubItem> workSubItems = detail.getWorkSubItems();
		if(workSubItems.size() > 0) {
			String floorMap = workSubItems.get(0).getFloorMap();
			return (floorMap==null || "".equals(floorMap)) ?
					null : floorMap.substring(0, floorMap.indexOf("/"));
		}
		return DEFAULT_BUILDING_NAME;
	}

	@Override
	public String getFloor(WorkOrderDetail detail, int subId) {
		WorkSubItem  sub = getWorkSubItem(detail, subId);
		return (sub!=null) ? sub.getTestFloors() : null;
	}

	@Override
	public String getFloorMap(WorkOrderDetail detail, int subId) {
		WorkSubItem  sub = getWorkSubItem(detail, subId);
		return (sub!=null) ? sub.getFloorMap() : null;
	}

}

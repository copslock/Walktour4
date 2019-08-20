package com.walktour.workorder.dal;

import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkSubItem;
import com.walktour.workorder.model.WorkSubItem.CommandItem;

import java.util.List;

/**
 * 定义工单查询的接口规范
 * Author: ZhengLei
 *   Date: 2013-6-21 上午10:31:33
 */
public interface IQueryHelper {
	/*start***********************************获取TaskModel的List的重载方法***********************************start*/
	/**
	 * 通过工单详情和子工单ID，查询到TaskModel的List
	 * @param detail
	 * @param subId
	 * @return
	 */
	public abstract List<TaskModel> getTask(WorkOrderDetail detail, int subId);
	
	/**
	 * 通过子工单，查询到TaskModel的List
	 * @param sub
	 * @return
	 */
	public abstract List<TaskModel> getTask(WorkSubItem sub);

	/**
	 * 获得任务模型，通过CommandItem的List转换为TaskModel的List
	 * @param commandItems 工单子项List
	 * @return 任务List
	 */
	public abstract List<TaskModel> getTask(List<CommandItem> commandItems);
	/*end***********************************获取TaskModel的List的重载方法***********************************end*/
	
	
	/*start***********************************公用方法***********************************start*/
	public abstract int getSubId(WorkOrderDetail detail, int position);

	public abstract WorkSubItem getWorkSubItem(WorkOrderDetail detail, int subId);
	
	public abstract WorkSubItem getWorkSubItem(List<WorkSubItem> subItems, int subId);
	/*end***********************************公用方法***********************************end*/
	
	
	/*start***********************************获取“开始测试”弹出框的一些参数***********************************start*/
	public int getLoopTime(WorkOrderDetail detail, int subId);
	
	public int getLoopInterval(WorkOrderDetail detail, int subId);

	public String getBuilding(WorkOrderDetail detail);
	
	public String getFloor(WorkOrderDetail detail, int subId);
	
	public String getFloorMap(WorkOrderDetail detail, int subId);
	/*end***********************************获取“开始测试”弹出框的一些参数***********************************end*/

}

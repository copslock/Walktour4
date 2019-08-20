package com.walktour.control.config;

import com.walktour.Utils.WalkStruct.TaskType;

import java.util.ArrayList;
import java.util.List;

public class ParamInfo {
	/**
	 * 分段阀值信自己列表
	 * 用于存放每个分段的阀值信息
	 */
	public List<ParamItem> paramItemList = new ArrayList<ParamItem>();
	/**
	 * 参数，事件ID
	 * 与数据集给出来的参数，事件ID一一对应
	 */
	public int id;
	/**
	 * 参数统计类型：
	 * 1，最大值，最小值，平均值统计；
	 * 2，事件统计，
	 * 3，GSM质量统计，关键参数,同时按最大最小值统计存储；
	 * 4，质量统计参数，辅助参数
	 * 5，其实特殊参数统计，需要在实现中特殊处理
	 */
	public int totalType;
	/**
	 * 当前统计参数名称
	 * 该值用于定义存到数据库中用于被查询时的关键字名
	 */
	public String paramName;
	
	/**
	 * 参数中用于标题显示的名称
	 * 显示时，如果该名存在不为空则取，否则取paramName
	 */
	public String paramShowName = null;
	/**
	 * 当前参数的网络类型
	 * 与WalkStruct.NetType中的值对应
	 * 此值用于权限控制，如果权限中无指定网络权限，该参数不参与后面统计
	 * 1,WCDMA;2,GSM;3,CDMA,4,EVDO,5,TDSCDMA,6,LTE,7,LTETDD
	 */
	public String netType;
	/**
	 * 当前参数是否在归类柱状图位置显示
	 * 目前柱状态图仅在GSM页有显示
	 */
	public boolean showThreshold = false;
	/**
	 * 用于存放当前统计参数，事件有指定业务要求的情况
	 * 该列表用于存放需要统计的业务结构名称，取任务结构中的值
	 */
	public List<TaskType> limitTask = null;
	
	/**
	 * 当limitTask不为空时当前有效，表示当前统计限制是否为数据业务限制
	 * 如果当前为数据业务限制，那么仅统计firstData到lastData之前的参数，事件
	 * */
	public boolean isDataLimit	= false;
	/**
	 * 该值用于当前统计参数，事件有指定业务的情况
	 * 如果当前参数需要指定业务，且存到数据库中的统计的关键字需要分开指定的情况，该数组的长度与位置与任务数组limitTask一一对应
	 * 如果当前不指定的话，默认关键字名以paramName存储
	 */
	public String[]	  taskKeyName = null;
	
	/**
	 * 根据当前任务，返回当前参数的统计关键字，对应于当前业务
	 * 限制任务列表limitTask不为空，且任务对应关键字名列表taskKeyName不为空，且此两项的长度一致时，返回任务对应位置的关键名
	 * 否则返回参数名
	 * @param taskName
	 * @return
	 */
	public String getKeyNameByTask(TaskType taskName){
		if(limitTask != null && taskKeyName != null && limitTask.size() == taskKeyName.length){
			return taskKeyName[limitTask.indexOf(taskName)];
		}
		return paramName;
	}
	
	/**
	 * 获得参数显示在标题中的名字
	 * @return
	 */
	public String getShowName(){
		return paramShowName != null ? paramShowName : paramName;
	}
	
	/**
	 * 参数查询是否查询真实点值，为false时表示查询继承值
	 */
	public boolean isFilter = true;
	
	/**是否仅查询指定参数当前点*/
	public boolean currentPoint = false;
	
	/**质量统计辅助参数列表*/
	public ArrayList<Integer> assistParamList = null;
	
	/**参数放大倍数*/
	public float scale = 1f;
}

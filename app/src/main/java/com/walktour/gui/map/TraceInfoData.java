package com.walktour.gui.map;

import android.content.Context;
import android.location.Location;
import android.util.SparseArray;

import com.walktour.gui.task.activity.scannertsma.model.ScanEventModel;
import com.dinglicom.DataSetLib;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DateUtil;
import com.walktour.Utils.GpsInfo;
import com.walktour.Utils.TotalResultByTask;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.TypeConver;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.packet_dissect_info;
import com.walktour.control.config.ParameterSetting;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.model.AlarmModel;
import com.walktour.model.BaseStructParseModel;
import com.walktour.model.CellInfo;
import com.walktour.model.ChartPointModel;
import com.walktour.model.MapEvent;
import com.walktour.model.NetStateModel;
import com.walktour.model.Parameter;
import com.walktour.model.StateInfoModel;
import com.walktour.model.TdL3Model;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 保存"查看信息"页面要显示的数据，这些页面包括图表，GSM,UMTS,EVDO,CDMA,
 * 
 * */
public class TraceInfoData implements Serializable {
	
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tag = "TraceInfoData";
	public static final int disableValue	= -9999;
	public static final int GPSLIMITSIZE	= 100;
	public double currentMapEvent	 		= disableValue;
	
	//L3Context
	public List<TdL3Model> l3MsgList= new ArrayList<TdL3Model>();
	//抓包详解信息存储
	public List<packet_dissect_info> tcpipInfoList = new ArrayList<packet_dissect_info>();

	//扫频仪自定义列表
	public List<ScanEventModel> scanEventList = new ArrayList<ScanEventModel>();
	
	//Map View Event Queue
	private Queue<AlarmModel> mapEvent = new LinkedBlockingQueue<AlarmModel>();
	private List<MapEvent> gpsLocas = new ArrayList<MapEvent>();		//GPS轨迹队列
	
	//Chart View Data Set
	//private Hashtable<String,String> chartTable = new Hashtable<String,String>();
	private Hashtable<String,String> chartLine = new Hashtable<String,String>();
	private Hashtable<String,Queue<ChartPointModel>> chartLineQ = new Hashtable<String, Queue<ChartPointModel>>();
	
	private java.util.Map<String, Object> videoRealPara = new HashMap<String, Object>();
	
	/**分布参数数据存储*/
	private Hashtable<String, int[]> distributionData = new Hashtable<String, int[]>();
	
	/**
	 * 存储基站ID HashMap
	 * 键:BCCH_BSIC_NetworkType
	 * 值:CELLID
	 */
	private HashMap<String, CellInfo> cellIDHm = new LinkedHashMap<String, CellInfo>();
	
	/**测试里程*/
	private double testMileage 	= 0 ;
	/**测试开始经度*/
	private double startLongitude = 0;
	/**测试开始纬度*/
	private double startLatitude= 0;
	/**测试开始时间*/
	private long testTimeLength = 0;
	/**测试过程中时长值存储，用于测试结束后显示用*/
	private String testTimeLengthStr = "0 S";
	
	/**测试过程中记录信令文件，从该文件获得所需的相关属性*/
	private File testLogFile 	= null;
	private String testFileName	= null;
	
	/**扫频仪参数界面存储，用于界面显示取值用*/
	private SparseArray<ArrayList<BaseStructParseModel>> scanResultMap = new SparseArray<ArrayList<BaseStructParseModel>>();
	
	
	/**
	 * 根据显示类型ID,取相应值
	 * @param showTypeId
	 * @return
	 * @author zhihui.lian
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends BaseStructParseModel> ArrayList<T> getScanResultList(Integer showTypeId) {
		return (ArrayList<T>) scanResultMap.get(showTypeId);
	}
	/**
	 * 往Map里头设置对象
	 * @param showTypeId
	 * @param ParseModelList
	 * @author zhihui.lian
	 */
	public synchronized void putScanResultMap(Integer showTypeId,ArrayList<BaseStructParseModel> ParseModelList) {
		this.scanResultMap.put(showTypeId, ParseModelList);
	}
	

	// Video play real para
	public synchronized java.util.Map<String, Object> getVideoRealPrar() {
		return videoRealPara;
	}

	public synchronized void setVideoRealPara(
			java.util.Map<String, Object> values) {
		videoRealPara = values;
	}
	
	
	

	/**
	 * 获得测试开始到当前位置的里程数
	 * 如果里程数小于1，单位转换成M，否则单位是KM
	 * @return the testMileage
	 */
	public synchronized String getTestMileageStr() {
		if(testMileage < 1){
			return UtilsMethod.decFormat4.format(testMileage * 1000) + " M";
		}
		return UtilsMethod.decFormat4.format(testMileage) + " KM";
	}
	
	/**
	 * 获得当前测试里程(单位：KM)
	 * 
	 * @return
	 */
	public synchronized double getTestMileage(){
		return testMileage;
	}
	
	/**
	 * 当经纬度位置发生变化时
	 * 调用此方法将当前经纬度传入，并计算出此点与上一个点之前的距离，累加到当前里程中
	 */
	public synchronized double theLocationChange(double longitude,double latitude){
		double midTestMileage = 0;
		if(longitude == 0.0 && latitude == 0.0){
			//twq20140818当当前经纬度都为00,00时,当前点不计算上下点的差值,当无效值处理,此处不做任何处理,条件结果返回0
		}else if(startLongitude == 0 && startLatitude == 0){
			startLongitude = longitude;
			startLatitude = latitude;
			testMileage = 0;
		}else{
			//TODO 暂时取消速度判断，只要收到GPS信息则计算里程
		    /*if(location.getSpeed() > 0){*/
				midTestMileage = UtilsMethod.getDistance(startLatitude, startLongitude, latitude, longitude);
		        testMileage += midTestMileage;
		    /*}*/
			startLatitude = latitude;
			startLongitude = longitude;
		}
		
		return midTestMileage;
	}

	/**
	 * 获得测试到当前时长
	 * 如果开始测试时间为0，则把当前时间赋给开始测试时间，返回时长0
	 * 测试时长为当前时间减去测试开始时间的秒数
	 * @return the testTimeLength
	 */
	public synchronized String getTestTimeLength() {
		if(testTimeLength == 0 ){
			testTimeLength = System.currentTimeMillis();
			return "0 S";
		}
		//只有在测试的时候才取当前值相减,测试结束的时候需要调一下此方法获得阳最后时长
		if(ApplicationModel.getInstance().isTestJobIsRun()){
			testTimeLengthStr = String.valueOf((System.currentTimeMillis() - testTimeLength) / 1000) + " S";
		}
		return testTimeLengthStr;
	}
	
	/**
	 * 获取测试时长<BR>
	 * [功能详细描述]
	 * @return 测试时长
	 */
	public long getTestTime(){
	    Date begin = null;
        try {
            begin = DateUtil.Y_M_D_H_M.parse("1970-01-01 00:00");
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        if(testTimeLength == 0){
            return 0 + begin.getTime();
        }
        return (begin.getTime() + System.currentTimeMillis() - testTimeLength);
	}
	
	/**获得测试时间HH:mm:ss*/
	public String getTestTimeHHmmss(){
		return DateUtil.secToHHmmss((int)((System.currentTimeMillis() - testTimeLength) / 1000));
	}
	/**
	 * 设置开始测试信息
	 * 目前包含测试开始时间，测试开始时经纬度
	 * 
	 */
	public synchronized void setTestStartInfo() {
		LogUtil.w(tag,"--test start init start info--");
		this.testTimeLength = System.currentTimeMillis();
		this.testMileage = 0;
		Location location = GpsInfo.getInstance().getLocation();
		if(location != null){
			startLongitude = location.getLongitude();
			startLatitude = location.getLatitude();
		}else{
			//如果GPS仍未获得时，测试开始要先对之前的值清零
			startLongitude = 0;
			startLatitude = 0;
		}
	}
	
	public static synchronized long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * 获取CELLID<BR>
	 * 根据网络相关参数获取CELLID
	 * @param key
	 * @return
	 */
	public synchronized CellInfo getNetworkCellInfo(String key){
	    return cellIDHm.get(key);
	}
	
	/**
	 * 根据相应关键字段组成key存储CellInfo<BR>
	 * [功能详细描述]
	 * @param key
	 * @param cellInfo
	 */
	public synchronized void setNetworkCellInfo(String key,CellInfo cellInfo){
	    if(cellIDHm.size() > 30){
	        cellIDHm.remove(cellIDHm.get(0));
	    }
	    cellIDHm.put(key, cellInfo);
	}
	
	/**
	 * 比较HashMap是否存在该Key<BR>
	 * [功能详细描述]
	 * @param key
	 * @return
	 */
	public synchronized boolean containsCellIDHmKey(String key){
	    return cellIDHm.containsKey(key);
	}
	
	/**
	 * 清除网络基站信息<BR>
	 * 只有在基站信息改变的情况下才进行清除
	 * @return
	 */
	public synchronized boolean cleanCellIDHmKey(){
		TraceInfoInterface.traceData.cellIDHm.clear();
	    return true;
	}
	
	// 通过解码获得手机网络状态信息
	private StateInfoModel stateInfo = new StateInfoModel();

	/**设置当前任务测试次数*/
	public void setTestTimes(TaskModel taskModel,String currentTimes){
		LogUtil.i(tag,"----setTestTimes:" + currentTimes);
		String curTimes = currentTimes;
		//http刷新业务特殊处理
		if(null != taskModel && taskModel instanceof TaskHttpPageModel){
			if(((TaskHttpPageModel) taskModel).getHttpRefreshType() == TaskHttpPageModel.REFRESH_TYPE_DEEPLY){
                            int originCurTimes = Integer.parseInt(currentTimes);
                            int realCurTimes = (int) Math.ceil(originCurTimes / Double.valueOf(((TaskHttpPageModel) taskModel).getHttpRefreshDepth()));
				curTimes = String.valueOf(realCurTimes > taskModel.getRepeat() ? taskModel.getRepeat() : realCurTimes);
			}
		}
		stateInfo.setCurTestTime(curTimes);
	}
	
	/**设置运行时间总时长*/
	public void setRunTimes(long runtimes){
		stateInfo.setRunTime(runtimes);
	}
	
	/**
	 * 当当前测试文件发生变化，创建或关闭时，更新当前保存文件连接信息
	 */
	public void setTestLogFile(String fileName){
		try{
			testFileName = fileName;
			if(fileName == null){
				stateInfo.setLogName("");
				stateInfo.setLogRecordSize("");
				testLogFile = null;
				DataSetLib.currentFileLength = -9999;
			}else{
				LogUtil.w(tag, "--tesFile:" + fileName);
				testLogFile = new File(testFileName);
				stateInfo.setLogName(testLogFile.getName());
			}
		}catch(Exception e){
			LogUtil.w(tag, "SetTestLogFile",e);
		}
	}
	
	/**
	 * 构建测试过程中生测试文件显示信息
	 */
	private void buildTestFileInfo(){
		if(testFileName != null && (testLogFile == null || !testLogFile.exists())){
			testLogFile = new File(testFileName);
		}
		
		if(DataSetLib.currentFileLength > 0){
			//LogUtil.w(tag, "--length:" + testLogFile.length() + "--" + (testLogFile.length() / 1024));
			stateInfo.setLogRecordSize( DataSetLib.currentFileLength < 1000 * 1000 ? 
					 String.format("%s kbyte",UtilsMethod.decFormat.format(DataSetLib.currentFileLength / UtilsMethod.kbyteRage))
					: String.format("%s M",UtilsMethod.decFormat.format(DataSetLib.currentFileLength / 
							(UtilsMethod.kbyteRage * UtilsMethod.kbyteRage))));
		}
	}
	
   
	/**业务测试过程中设置当前任务总次数及当前次数*/
	public void setTestTimes(String curJobsName,String allTimes,String currentTimes,String allCircles,String currCircles){
		stateInfo.setCurrentJon(curJobsName);
		stateInfo.setCurAllTimes(allTimes);
		stateInfo.setCurTestTime(currentTimes);
		stateInfo.setCurAllCircles(allCircles);
		stateInfo.setCurTestCircles(currCircles);
	}
	
	/**返回当前不需要查询的当状态*/
	public StateInfoModel getStateInfoNoQuery() {
		return stateInfo;
	}
	
	/**
	 * 获得Walktou当前运行状态 获得手机的一些系统状态
	 * */
	public StateInfoModel getStateInfo() {
		NetStateModel netState = NetStateModel.getInstance();
		stateInfo.setCurrentNet(netState.getCurrentNetType().name());						//当前网络类型
		stateInfo.setNetState(netState.getCurrentNetState());								//当前网络的网络状态
		//stateInfo.setCurrentJon(ApplicationModel.getInstance().getCurrentTask().name());	//当前任务类型
		//stateInfo.setCpuState(Math.round(UtilsMethod.readUsage())+"");     					//手机cpu使用率
    	//stateInfo.setSurplusSize(UtilsMethod.getAvaiableSD());								//sdcard剩余空间
    	stateInfo.setCurrentJon(ApplicationModel.getInstance().getCurrentTask() == TaskType.Default ?
    			"-" : ApplicationModel.getInstance().getCurrentTask().name());	//
    	TotalResultByTask.totalTargeTask(ApplicationModel.getInstance().getCurrentTask(), stateInfo);
    	buildTestFileInfo();
		return stateInfo;
	}

	/**
	 * 获取图形队列事件点数据,并将队列清空
	 * @return
	 */
	public synchronized Queue<AlarmModel> getMapEvent() {
		Queue<AlarmModel> tempMapEvent = new LinkedBlockingQueue<AlarmModel>();
		tempMapEvent.addAll(mapEvent);
		mapEvent.clear();
		return tempMapEvent;
	}
	
	/**
	 * 获取图形队列事件点数据,不清空队列<BR>
	 * [功能详细描述]
	 * @return List<MapEvent> 
	 */
    public synchronized List<AlarmModel> getMapEventList() {
        List<AlarmModel> mapEvents = new ArrayList<AlarmModel>();
        mapEvents.addAll(mapEvent);
        return mapEvents;
    }
	
	/**
	 * 添加事件点信息到图形数据队列中
	 * 如果当前点大于500时，去掉最前面加入的点
	 * @param event
	 */
	public synchronized void addMapEvent(AlarmModel event) {
		this.mapEvent.add(event);
		while(this.mapEvent.size() > 600 && !cleanEventRun){
			cleanEventRun = true;
			new RemoveMoreMapEvent().start();
		}
	}
	
	private boolean cleanEventRun = false;
	class RemoveMoreMapEvent extends Thread{
		public void run(){
			LogUtil.w(tag, "--remove more MapEvent--");
			int num = 0;
			while(num < 100){
				mapEvent.poll();
				num ++;
			}
			
			cleanEventRun = false;
		}
	}
	
	
	
	/**
	 * 添加事件点信息到图形数据队列前面中
	 */
	public synchronized void addMapEventToFirst(Queue<AlarmModel> events) {
		Queue<AlarmModel> tempMapEvent = new LinkedBlockingQueue<AlarmModel>();
		tempMapEvent.addAll(mapEvent);
		this.mapEvent.clear();
		this.mapEvent.addAll(events);
		this.mapEvent.addAll(tempMapEvent);
	}
	
	/**
	 * 当地图参数改变时,将之前图形数据队列的状态改变1
	 */
	public synchronized void paraChnaged(){
		for(AlarmModel event:mapEvent){
			event.getMapEvent().setStatus(1);
		}
	}
	
	/**
	 * 获得GPS轨迹列表
	 * @return
	 */
	public synchronized List<MapEvent> getGpsLocas() {
		return gpsLocas;
	}
	
	/**
	 * 添加GPS轨迹列表点,已包含X，Y值，颜色
	 * 如果当前点大于500时，不显示最前面加入的点
	 * @param event
	 */
	public synchronized void addGpsLocas(MapEvent event){
		this.gpsLocas.add(event);
//		这个是限制加载采样点的，为了防止加载卡顿的问题，限制解决了，不需要限制
		//队列限制3600 即1小时数据避免数组过大
//		while (this.gpsLocas.size() > GPSLIMITSIZE ) {	//GPSLIMITSIZE
//			MapEvent rmEvent = this.gpsLocas.remove(0);
//			LogUtil.w(tag, "--removeGps pointIndex:" + rmEvent.getBeginPointIndex() + " to " + rmEvent.getEndPointIndex()
//					+ "--QEvent:" +  MapFactory.getMapData().getEventQueue().size()
//					+ "--QStack:" + MapFactory.getMapData().getQueueStack().size());
//			if(MapFactory.getMapData().getEventQueue().size() > 0){
//				for(AlarmModel alarm : MapFactory.getMapData().getEventQueue()){
//					if(alarm != null){
//						//alarm.getMapEvent().getBeginPointIndex() >= rmEvent.getBeginPointIndex() &&
//						if(alarm.getMapEvent().getEndPointIndex() <= rmEvent.getEndPointIndex()){
//							LogUtil.w(tag, "--removeEventQueue pointIndex:" + MapFactory.getMapData().getEventQueue().poll().getMapEvent().getEndPointIndex());
//						}else{
//							LogUtil.w(tag, "--removeEventQueue EndIndex:" + alarm.getMapEvent().getEndPointIndex());
//							break;
//						}
//					}
//				}
//			}
//
//			while(MapFactory.getMapData().getQueueStack().size() > 0 ){
//				AlarmModel aEvent = MapFactory.getMapData().getQueueStack().get(0).peek();
//				if(aEvent == null){
//					MapFactory.getMapData().getQueueStack().remove(0);
//					LogUtil.w(tag, "--getQueueStack alarmModel isNull del--");
//				}else if(aEvent.getMapEvent().getEndPointIndex() <= rmEvent.getEndPointIndex()){
//					LogUtil.w(tag, "--removeQueueStack :" + MapFactory.getMapData().getQueueStack().remove(0).peek().getMapEvent().getEndPointIndex());
//				}else{
//					break;
//				}
//			}
//		}
	}
	
	/**
	 * 改变GPS轨迹参数
	 */
	public synchronized void changeGpsLocasEventColor(){
		for(MapEvent event : gpsLocas){
			if(event.getStatus() == 0)
				event.setColor(ParameterSetting.getInstance().getGpsEventColor(event.getValue()));
		}
	}
	
	/**
	 * 当参数改变时，将之前参数产生的值的颜色改为轨迹颜色，并将状态置为历史状态
	 */
	public synchronized void changeGpsLocasPara(){
	    TraceInfoInterface.traceData.currentMapEvent = TraceInfoData.disableValue;
		for(MapEvent e: this.gpsLocas){
			if(e.getStatus() == 0){
				e.setStatus(1);
				e.setColor(ParameterSetting.getInstance().getGpsColor());
			}
		}
	}
	
	/**
	 * 当GPS轨迹颜色改变时，将历史轨迹颜色改为当前设置的轨迹颜色
	 */
	public synchronized void changeGpsLocasColor(){
		//twq20131206网络参数变化时，不重置历史轨迹颜色
		/*for(MapEvent e:gpsLocas){
			if(e.status == 1){
				e.color = ParameterSetting.getInstance().getGpsColor();
			}
		}*/
	}

	/**
	 * 返回图表队列参数信息,柱状图使用
	 * @return
	 */
	public synchronized Hashtable<String, String> getChartLine() {
		return chartLine;
	}
	/**
	 * 根据参数设置列表,初始化图表队列,曲线队列信息,值都为空
	 * @param chartLinePara
	 */
	public synchronized void initChartLine(String[] chartLinePara) {
		try{
			chartLine = new Hashtable<String, String>();
			chartLineQ = new Hashtable<String, Queue<ChartPointModel>>();
			
			for(String para:chartLinePara){
				chartLine.put(para,"");
				chartLineQ.put(para,getEmpty81Q());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * 返回长度为21的空值队列对象
	 * @return
	 */
	private Queue<ChartPointModel> getEmpty81Q(){
		Queue<ChartPointModel> temp = new LinkedBlockingQueue<ChartPointModel>();
		for(int i=0;i<81;i++){
		    ChartPointModel chartPoint = new ChartPointModel("",-1,null);
			temp.add(chartPoint);
		}
		return temp;
	}
	/**
	 * 当设图表设定参数值发生改变时,通过此方法修改图表参数值
	 * @param key
	 * @param values
	 */
	public synchronized void updateChartLine(String key,String values){
		for(Entry<String,String> ent:chartLine.entrySet()){
			if(ent.getKey().equals(key)){
				ent.setValue(values);
				break;
			}
		}
	}
	/**
	 * 当系统设置中图表显示参数发生变化时,更新图表显示队列
	 * @param chartLinePara
	 */
	public synchronized void chartLineChanged(String[] chartLinePara){
		Hashtable<String,String> tempChart = new Hashtable<String, String>();
		Hashtable<String,Queue<ChartPointModel>> tempChartQ = new Hashtable<String, Queue<ChartPointModel>>();
		for(String chartName : chartLinePara){
			if(chartLine.containsKey(chartName))
				tempChart.put(chartName, chartLine.get(chartName));
			else
				tempChart.put(chartName,"");
			if(chartLineQ.contains(chartName))
				tempChartQ.put(chartName, chartLineQ.get(chartName));
			else
				tempChartQ.put(chartName, getEmpty81Q());
		}
		chartLine = new Hashtable<String,String>();
		chartLineQ = new Hashtable<String, Queue<ChartPointModel>>();
		chartLine.putAll(tempChart);
		chartLineQ.putAll(tempChartQ);
	}
	/**
	 * 返回图表指定参数的曲线队列
	 * @return
	 */
	public synchronized Hashtable<String,Queue<ChartPointModel>> getChartLineQ(){
		return chartLineQ;
	}
	/**
	 * 将解码所得的指定系统指定参数值添加到相应的曲线图队列中,TRACE服务每秒执行一次,如果当前值使用继承值
	 */
	public synchronized void setChartLineQValue(int beginIndex,int endIndex,Context context){
	    //twq20140506回放暂停时,不再调用折线参数查询动作,因暂停时采样点不变,会让取出来的值一至面出现折线平移.
		//冻屏时串口是有数据的,只是界面不刷新,故不在此限制
		if(!DatasetManager.isPlayback || (DatasetManager.isPlayback && beginIndex != endIndex)){
			List<AlarmModel> alarmList = AlertManager.getInstance(context).getChartAlarmList(beginIndex,endIndex);
			for(Entry<String,Queue<ChartPointModel>> ent:chartLineQ.entrySet()){
				ent.getValue().add(new ChartPointModel(TraceInfoInterface.getParaValue(Integer.parseInt(ent.getKey(),16)),endIndex,alarmList));
				ent.getValue().remove();
			}
	    }
	}
	
	private long lastEventTime = 0;
	/**将当前参数值写入地图，图表，图表折线图中*/
	public synchronized void setMapParamInfo(String id,String valueStr){
		if(ParameterSetting.getInstance().getMapParameter() == null)
			return;
		int scale = ParameterSetting.getInstance().getMapParameter().getScale();
		
		/**
         * 当地图当前参数与当前解码参数一致时,将当前解码结果数据添加到地图显示事件队列中
         * 条件,当前正在做业务测中,如不做测试的打点为预打点，之前的点记录没有意义
         */
        if(ApplicationModel.getInstance().isTestJobIsRun()
        		&& ParameterSetting.getInstance().getMapParameter().getId().equalsIgnoreCase(id)){
			if(GpsInfo.getInstance().isJobTestGpsOpen()){
				if(valueStr.equals("")){
					currentMapEvent = -9999;
				}else{
					currentMapEvent = TypeConver.StringToDouble(valueStr) / scale;
				}
			}else if( !valueStr.equals("") && System.currentTimeMillis() - lastEventTime > 200){
				//参数取值频率在200毫秒之前，之内的值仅取一次
				lastEventTime = System.currentTimeMillis();
				
				MapEvent mapEvent = new MapEvent(); 
				mapEvent.setValue(TypeConver.StringToDouble(valueStr) / scale);
				AlarmModel alarmModel = new AlarmModel();
				alarmModel.setMapEvent(mapEvent);
				addMapEvent(alarmModel);
			}
		}
        
        /**
         * 当当前解码参数名存在于图表页的曲线组中时,将该值添加到相应的折线队列中
         * 此处只是把当前值更新到指定位置chartLine中,界面显示时通过每次从chartLine中获取并添加到折线显示队列中并刷新
         * 每次刷新的方法：traceData.setChartLineQValue();
         */
        /*if(getChartLine().containsKey(id.toUpperCase())){
            updateChartLine(id.toUpperCase(), valueStr);
        }*/
        
        
        /**
         * 解码结果存到相应的HashMap表中,界面，IPAD端等参数显示从该位置获得
         * twq20130805，如果当前为冻屏状态，将参数变化结果存到冻屏备份结果中
         */
        //LogUtil.w(tag, "--id:" + id + "--v:" + valueStr);
        //TraceInfoInterface.decodeResultUpdate(Integer.parseInt(id,16), valueStr);
	}
	
	private void initDistributionData(String paraId){
		//此处的长度,要根据设定的参数阀值+1来定.其中0用于存总值,1到N存各阀值相应个数
		Parameter params[] = ParameterSetting.getInstance().getDistributionParams();
		Parameter param = null;
		for(int i = 0; i < params.length && params[i] != null; i++){
			if(params[i].equals(paraId)){
				param = params[i];
				break;
			}
		}
		
		int arrrayLen = (param != null && param.getThresholdList().size() > 0 ? 
								param.getThresholdList().size() + 1 : 6);
		int[] array = new int[arrrayLen];
		
		distributionData.put(paraId, array);
	}
	
	/**获得分布参数数据存储*/
	public int[] getDistributionData(String paraId){
		if(!distributionData.containsKey(paraId)){
			initDistributionData(paraId);
		}
		return distributionData.get(paraId);
	}
	
	/**
	 * 添加当前参数归属阀值中
	 * @param arrayIndex	当前参数对应阀值序号
	 * @param value			当前阀值段个数
	 */
	public void addDistributionData(String paramId,int arrayIndex,int value){
		if(!distributionData.containsKey(paramId)){
			initDistributionData(paramId);
		}
		
		int[] array = distributionData.get(paramId);
		array[0] = array[0] + value;
		array[arrayIndex] = array[arrayIndex] + value;
		
		distributionData.put(paramId, array);
	}
	
	/**
	 * 当界面重新设置分布参数时,需要调用此方法,将hash表中不需要的参数分布信息清空
	 */
	public void reSetDistributionData(){
		Parameter params[] = ParameterSetting.getInstance().getDistributionParams();
		Iterator<String> keyParam = distributionData.keySet().iterator();
		while(keyParam.hasNext()){
			String paramId = keyParam.next();
			boolean exists = false;
			for(Parameter param : params){
				if(paramId.equals(param.getId())){
					exists = true;
					break;
				}
			}
			
			if(!exists){
				distributionData.remove(paramId);
				keyParam = distributionData.keySet().iterator();
			}
		}
	}
	
	/**
	 * 业务开始测试前，清空上次分布信息内容
	 */
	public void cleanDistributionData(){
		distributionData.clear();
	}
}

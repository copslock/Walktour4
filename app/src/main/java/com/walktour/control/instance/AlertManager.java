package com.walktour.control.instance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.speech.tts.TextToSpeech;

import com.dinglicom.dataset.model.EventModel;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UmpcSwitchMethod;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.Utils.WalkStruct.UMPCEventType;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.gui.AlarmDialog;
import com.walktour.gui.R;
import com.walktour.gui.map.TraceInfoData;
import com.walktour.gui.setting.eventfilter.EventFilterSettingFactory;
import com.walktour.gui.setting.eventfilter.model.EventFilterSetModel;
import com.walktour.model.AlarmModel;
import com.walktour.model.CellInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 告警管理者
 * 1.初始化所有告警配置的文件，
 * 2.告警的设置、显示、记录 
 * */
public class AlertManager{
	private String tag = "AlertManager";
	public static final String KEY_DEVICE_SOUND= "DEVICE_SOUND";
	public static final String KEY_NETWORK_SOUND= "NETWORK_SOUND";
	public static final String KEY_NETWORK_MAP = "NETWORK_MAP";
	public static final String KEY_TEST_SOUND= "TEST_SOUND";
	public static final String KEY_TEST_MAP = "TEST_MAP";
	
	/** 单位1M = 1,024*1,024字节 */
	public static final long UNIT_M = 1000 * 1000;
	/** 蓝牙MOS电量不足的警告线（15%）  */
	public static  int MOS_POWER_LOW = 15;
	/** 电池电量不足的警告线（20%）  */
	public static  int BATTERY_LOW = 25;
	/** 电池电量消耗殆尽的警告线（10%）  */
	public static final int BATTERY_ALMOSTNONE = 10;
	/** 手机温度过高警告线（38摄氏度）  */
	public static  int PHONE_OVERHEAT = 50; 
	/** 存储空间低告警线 10M   */
	public static final long STORAGE_RARE = 200 * UNIT_M; 
	/** 存储空间消耗殆尽告警线  5M   */
	public static final long STORAGE_ALMOSTNONE = 5 * UNIT_M; 
	
	public static final String TD_FMT = "UARFCN:%s\t\tCPI:%s\r\nPCCPCH RSCP:%s\t\tPCCPCH C/I:%s\r\nCellId:%s\t\t";
	public static final int[] TD_ID = new int[]{
			  UnifyParaID.TD_Ser_UARFCN ,
			  UnifyParaID.TD_Ser_CPI ,
			  UnifyParaID.TD_Ser_PCCPCHRSCP ,
			  UnifyParaID.TD_Ser_PCCPCHC2I,
			  UnifyParaID.TD_Ser_CellID
	};
	public static final String CDMA_FMT = 
	"Freq:%s\t\t" +	"Refer PN:%s\r\n" +
	"Total Ec/Io:%s\t\t" +"RxAGC:%s\r\n" +
	"EV_Freq:%s\t\t" +"EV_PN:%s\r\n" +
	"RxAGC0:%s\t\t" +"TotalSINR:%s\r\n";
	public static final int[] CDMA_ID = new int[]{
			 UnifyParaID.C_Frequency ,
			 UnifyParaID.C_ReferencePN ,
			 UnifyParaID.C_TotalEcIo ,
			 UnifyParaID.C_RxAGC ,
			 UnifyParaID.E_EV_Frequenc ,
			 UnifyParaID.E_ServingSectorPN ,
			 UnifyParaID.E_Carrier1_EV_RxAGC0  ,
			 UnifyParaID.E_Carrier1_TotalSINR 
	};
	public static final String LTE_FMT = "DL EARFCN:%s\t\tPCI:%s\r\nRSRP:%s\t\tRSRQ:%s\r\nECI:%s-%s\t\t";
	public static final int[] LTE_ID = new int[]{
			 UnifyParaID.L_SRV_EARFCN ,
			 UnifyParaID.L_SRV_PCI ,
			 UnifyParaID.L_SRV_RSRP ,
			 UnifyParaID.L_SRV_RSRQ,
			 UnifyParaID.L_SRV_ECIP2,
			 UnifyParaID.L_SRV_ECIP3
	
	};
	public static final String WCDMA_FMT = "Freq:%s\t\tPSC:%s\r\nTotal RSCP:%sdBm\t\tTotal EcIo:%sdB\r\nCellId:%s\t\t";
	public static final int[] WCDMA_ID = new int[]{
			 UnifyParaID.W_Ser_DL_UARFCN ,
			 UnifyParaID.W_Ser_Ref_PSC ,
			 UnifyParaID.W_Ser_Total_RSCP ,
			 UnifyParaID.W_Ser_Total_EcIo ,
			 UnifyParaID.W_Ser_Cell_ID ,
	};
	public static final String GSM_FMT = "BCCH:%s\t\tBSIC:%s\r\nRxLevFull:%s\t\tRxLevSub:%s\r\n" +
	"TxPower:%s\t\tTA:%s\r\nCellId:%s\t\t";
	public static final int[] GSM_ID = new int[]{
			 UnifyParaID.G_Ser_BCCH ,
			 UnifyParaID.G_Ser_BSIC ,
			 UnifyParaID.G_Ser_RxLevFull ,
			 UnifyParaID.G_Ser_RxLevSub ,
			 UnifyParaID.G_Ser_TxPower ,
			 UnifyParaID.G_Ser_TA,
			 UnifyParaID.G_Ser_Cell_ID 
	};

	private TextToSpeech mTts;  
	
	//静态实例
	private AlertManager(Context context){
		mContext = context;
		shared_prefs_name = mContext.getPackageName()+"_alarm";
		share = mContext.getSharedPreferences( shared_prefs_name, Context.MODE_PRIVATE );
		
		initValues();
	}
	
	private static AlertManager sInstance;
	public synchronized static AlertManager getInstance(Context context){
		if(sInstance ==null){
			sInstance =new AlertManager(context);
		}
		return sInstance;
	}
	
	private static Context mContext;
	private SharedPreferences share;
	private String shared_prefs_name = "";
	private ArrayList<AlarmModel> alarmListView = new ArrayList<AlarmModel>() ;
	private ArrayList<AlarmModel> alarmListSound = new ArrayList<AlarmModel>();
	private ArrayList<AlarmModel> alarmListMap = new ArrayList<AlarmModel>();
	/**
	 * Chart显示图标的Map
	 * Key为采样点,value为告警对象
	 */
	private ArrayList<AlarmModel> alarmListChart = new ArrayList<AlarmModel>();
	
	/**
	 * 是否正在进行MOS测试，是的话不进行语音告警
	 */
	private boolean isTestingMos = false;
	
	/**
	 * 初始化告警配置的文件,以告警名的枚举值作为KEY
	 */
	public void initValues(){
		WalkStruct.Alarm[] alarms = WalkStruct.Alarm.values();
		for( WalkStruct.Alarm a:alarms){
			if( !share.contains( a.name() ) ){
				Editor editor = share.edit();
				boolean open = false;
				if( a.getType() == Alarm.TYPE_DEVICE ){
					if(!a.name().endsWith("_TEST")){
						open = true;
					}
					if (a.name().contains("STOP_TEST")) {
						open = false;
					}
				}else if( a.getType()== Alarm.TYPE_TEST ){
					if( a.name().contains("BLOCKED")
							|| a.name().contains("DROP")
							|| a.name().contains("FAIL")
							|| a.name().contains("LOW") ){
						open = true;
					}
				}else if( a.getType() == Alarm.TYPE_NETWORK ){
					if( a.name().contains("FAIL") 
							|| ( a.name().contains("RESELECTION") && a.name().contains("2") )
//							|| a == Alarm.NET_LOW_COVERAGE 
//							|| a == Alarm.NET_POOR_QUALITY 
							|| a == Alarm.NET_OUT_OF_SERVICE ){
						open = (a == Alarm.NET_OUT_OF_SERVICE) ? false : true;
					}
				}
				
				editor.putBoolean(a.name(),open );
	 			editor.commit();
			}
		}
		
		//设置声音告警和地图显示
		Editor editor = share.edit();
		if( !share.contains( KEY_DEVICE_SOUND ) ){
			editor.putBoolean( KEY_DEVICE_SOUND, true );
		}
		if( !share.contains( KEY_NETWORK_SOUND ) ){
			editor.putBoolean( KEY_NETWORK_SOUND, true );
		}
		if( !share.contains( KEY_NETWORK_MAP ) ){
			editor.putBoolean( KEY_NETWORK_MAP, true );
		}
		if( !share.contains( KEY_TEST_SOUND) ){
			editor.putBoolean( KEY_TEST_SOUND, true );
		}
		if( !share.contains( KEY_TEST_MAP ) ){
			editor.putBoolean( KEY_TEST_MAP, true );
		}
		
		//设置
		
		editor.commit();
	}
	
	/**
	 * 设置指定的告警是否开启
	 * @param alarm
	 * @param open
	 */
	public void setAlarm(WalkStruct.Alarm alarm,boolean open){
		Editor editor = share.edit();
		editor.putBoolean(alarm.name(),open );
		editor.commit();
	}

	/**
	 * @param alarm
	 * @return 指定的告警是否开启
	 */
	public boolean isAlarmOn(WalkStruct.Alarm alarm){
		
		if( alarm == Alarm.CUSTOM_EVENT ){
			return true ;
		}
		
		if( !share.contains( alarm.name() ) ){
			Editor editor = share.edit();
			editor.putBoolean(alarm.name(),false );
			editor.commit();
		}
		
		return share.getBoolean( alarm.name(), false);
		
	}

	/**
	 * @param prefs ONE OF AlarmManager.KEY_*
	 * @return 指定的告警是否开启
	 */
	public boolean getPrefs(String prefs){
		if( !share.contains( prefs ) ){
			Editor editor = share.edit();
			editor.putBoolean( prefs,false );
			editor.commit();
		}
		
		return share.getBoolean( prefs, false);
		
	}

	/**
	 * 设置指定的告警是否开启
	 * @param prefs ONE OF AlarmManager.KEY_*
	 * @param open
	 */
	public void setPrefs(String prefs,boolean open){
		Editor editor = share.edit();
		editor.putBoolean( prefs,open );
		editor.commit();
	}

	/**
	 * 增加到声音列表里，重复未播放的不添加，防止过多重复的声音
	 * @param newModel 告警对象
	 */
	 private void addToSoundList(AlarmModel newModel){
		 if( mTts!=null){
			 for( AlarmModel model :alarmListSound){
				 if( newModel.getAlarm() == model.getAlarm() ){
					 LogUtil.i(tag, newModel.getAlarm()+" is existed");
					 return;
				 }
			 }
			 
			 alarmListSound.add( newModel );
			 
			 //只有在通话状态为idle下才进行语音播报
			 if( !playing && !isTestingMos ){
				 new Thread( new ThreadPlay() ).start();
			 }
		 }
	}

	private void showAlarmDialog(AlarmModel alarmModel){
		Intent intent = new Intent( mContext,AlarmDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putSerializable("alarm", alarmModel);
		intent.putExtras(bundle);
		mContext.startActivity(intent);
	}
	
	//	TraceInfoInterface.getParaValue(),
	//	TraceInfoInterface.getParaValue(),
	//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_RxLevFull),
	//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_RxLevSub),
	//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_TxPower),
	//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_TA)
		
	
	/**
	 * 获取当前地图告警需要查询的参数
	 * @return
	 */
	public static int[] getCurrentParams(int netType) {
		switch (netType) {
			case UnifyParaID.NET_GSM:
				return GSM_ID;
			case UnifyParaID.NET_WCDMA:
				return WCDMA_ID;
			case UnifyParaID.NET_LTE:
			case UnifyParaID.NET_NB_IoT:
			case UnifyParaID.NET_CAT_M:
				return LTE_ID;
			case UnifyParaID.NET_CDMA_EVDO:
				return CDMA_ID;
			case UnifyParaID.NET_TDSCDMA:
				return TD_ID;
			default:
				return new int[]{};
		}
	}
	
    /**
     * 设置图标弹出框的内容，带具体网络参数
     * @param netType 当前网络类型
     * @param parValues 参数值
     */
	public static String genMapPopInfo(int netType, String parValues) {
		String result = "";
		if (StringUtil.isNullOrEmpty(parValues))
			return result;
		Object[] values = parValues.split("@@");

		// 格式化从JNI查询输出的结果
		for (int i = 0; i < values.length; i++) {
			String fmValue = values[i].toString();
			try {
				values[i] = UtilsMethod.decFormat.format(Float.parseFloat(fmValue));
			} catch (Exception e) {
				values[i] = fmValue;
			}
			if (values[i].equals("-9999"))
				values[i] = "";
		}

		try {
			int networkType = -1;
			String[] paramNames = null;
			String[] paramValues = null;
			switch (netType) {
			case UnifyParaID.NET_GSM:
				result = String.format(GSM_FMT, values);
				networkType = WalktourConst.NetWork.GSM;
				paramNames = new String[] { "bcch", "bsic" };
				paramValues = new String[] { String.valueOf(values[0]), String.valueOf(values[1]) };
				break;
			case UnifyParaID.NET_WCDMA:
				// 告警窗口CELLID显示短码
				values[0] = UtilsMethod.getLongTosShortCellID(values[0].toString());
				result = String.format(WCDMA_FMT, values);
				networkType = WalktourConst.NetWork.WCDMA;
				paramNames = new String[] { "uarfcn","psc" };
				paramValues = new String[] { String.valueOf(values[0]), String.valueOf(values[1]) };
				break;
			case UnifyParaID.NET_LTE:
				result = String.format(LTE_FMT, values);
				networkType = WalktourConst.NetWork.LTE;
				paramNames = new String[] { "eafrcn", "pci" };
				paramValues =	new String[] { String.valueOf(values[0]), String.valueOf(values[1]) };
				break;
			case UnifyParaID.NET_CDMA_EVDO:
				result = String.format(CDMA_FMT, values);
				networkType = WalktourConst.NetWork.CDMA;
				if(StringUtil.isNullOrEmpty(String.valueOf(values[0]))){
					paramNames = new String[] { "ev_freq","ev_pn" };
					paramValues =	new String[] { String.valueOf(values[4]), String.valueOf(values[5]) };
				}else{
					paramNames = new String[] { "frequency","pn" };
					paramValues =	new String[] { String.valueOf(values[0]), String.valueOf(values[1]) };
				}
				break;
			case UnifyParaID.NET_TDSCDMA:
				result = String.format(TD_FMT, values);
				networkType = WalktourConst.NetWork.TDSDCDMA;
				paramNames = new String[] { "uarfcn","cpi" };
				paramValues =	new String[] { String.valueOf(values[0]), String.valueOf(values[1]) };
				break;
			default:
				break;
			}
			if (paramNames != null) {
				CellInfo cell = getCellInfo(networkType, paramNames, paramValues);
				if (cell != null) {
					result += "CellName:" + cell.getCellName()+"\r\n";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获得小区信息
	 * @param networkType 网络类型
	 * @param paramNames 参数名称数组
	 * @param paramValues 参数值数组
	 * @return
	 */
	private static CellInfo getCellInfo(int networkType,String[] paramNames,String[] paramValues) {
		StringBuilder cellidKey = new StringBuilder();
		cellidKey.append(networkType);
		for (int i = 0; i < paramValues.length; i++) {
			if (StringUtil.isNullOrEmpty(paramValues[i])) {
				return null;
			}
			cellidKey.append("_").append(paramNames[i]).append("_").append(paramValues[i]);
		}
		TraceInfoData traceData = TraceInfoInterface.traceData;
		if (traceData.containsCellIDHmKey(cellidKey.toString())) {
			return traceData.getNetworkCellInfo(cellidKey.toString());
		}
		StringBuilder sb = new StringBuilder();
		int length = paramNames.length;
		String[] queryParams = new String[length + 4];
		for (int i = 0; i < length; i++) {
			if (i > 0)
				sb.append(" and ");
			sb.append(paramNames[i]).append(" = '");
			sb.append(paramValues[i]).append("' ");
			queryParams[i] = paramNames[i];
		}
		queryParams[length] = "cellName";
		return getCellFromDB(queryParams, networkType, sb.toString());
	}
	
	/**
	 * 从数据库中获得小区信息
	 * 
	 * @param queryParams
	 *          查询参数
	 * @param networkType
	 *          网络类型
	 * @param condition
	 *          查询条件
	 */
	private static CellInfo getCellFromDB(String[] queryParams, int networkType, String condition) {
		List<BaseStationDetail> list = BaseStationDBHelper.getInstance(mContext).queryCellIDByFields(condition);
		for (BaseStationDetail detail : list) {
			if (detail.main.netType != networkType)
				continue;
			StringBuffer buffer = new StringBuffer();
			buffer.append(networkType);
			buffer.append("_").append(queryParams[0]).append("_").append(detail.getParamValue(queryParams[0]));
			buffer.append("_").append(queryParams[1]).append("_").append(detail.getParamValue(queryParams[1]));
			CellInfo cell = TraceInfoInterface.traceData.getNetworkCellInfo(buffer.toString());
			if (cell == null) {
				CellInfo cellInfo = new CellInfo(detail.cellName, detail.cellId, 0);
				TraceInfoInterface.traceData.setNetworkCellInfo(buffer.toString(), cellInfo);
				return cellInfo;
			}
		}
		return null;
	}

	/**
	 * 生成带采样点序号的告警
	 * @param model 告警对象
	 * @return 根据Alarm类型生成的的AlarmModel
	 */
	public AlarmModel addAlarm(AlarmModel model) {
		if (isAlarmOn(model.getAlarm())) {
			this.alarmListView.add(model);
			while (alarmListView.size() > 1000) {
				alarmListView.remove(0);
			}
			mContext.sendBroadcast(new Intent(WalkMessage.ACTION_ALARM_LIST));
			switch (model.getAlarm().getType()) {
			case Alarm.TYPE_DEVICE:// 设备告警
				if(ApplicationModel.getInstance().isNBTest()&&model.getAlarm()==WalkStruct.Alarm.DEVICE_SIMCARD) {
					break;
				}
				// 弹框显示
				showAlarmDialog(model);
				// 声音告警
				if (getPrefs(KEY_DEVICE_SOUND)) {
					addToSoundList(model);
				}
				break;
			case Alarm.TYPE_NETWORK: // 网络告警
				// 显示地图
				if (getPrefs(KEY_TEST_MAP)) {
					alarmListMap.add(model);
				}
				// 告警声音
				if (getPrefs(KEY_NETWORK_SOUND)) {
					addToSoundList(model);
				}
				// 添加到Chart图里
				alarmListChart.add(model);
				break;
			case Alarm.TYPE_TEST:// 业务告警
				// 显示地图
				if (getPrefs(KEY_TEST_MAP)) {
					alarmListMap.add(model);
				}
				if (getPrefs(KEY_TEST_SOUND)) {
					addToSoundList(model);
				}
				// 添加到Chart图里
				alarmListChart.add(model);
				break;
			case Alarm.TYPE_CUSTOM:// 自定义事件的告警
				// just add to list and do nothing else
				break;
			case Alarm.TYPE_FILTER_EVENT:// 事件过滤告警
				break;
			}
		}

		return model;
	}

	/**
	 * 添加回放事件列表
	 * @param eventList
	 */
	public void addReplayEvents(List<EventModel> eventList){
		for(EventModel event:eventList){
			this.addAlarmFromEvent(event);
		}
	}
	
	/**
	 * 根据事件生成告警
	 * @param event 
	 * @return 事件添加到告警时返回该告警，事件没有作为告警时返回null
	 */
	public void addAlarmFromEvent(EventModel event){
		//RCU事件
		if( event.getType() == EventModel.TYPE_RCU ){
			Alarm alarm = Alarm.getAlarm( event.getRcuId() );
			if( alarm!=null ){
				AlarmModel model = new AlarmModel( event.getTime(),alarm);
				model.setDrawableId( alarm.getDrawable() );
				model.setMsgIndex( event.getPointIndex() );
				addAlarm( model );
			}else{//判断当前事件是否在事件过滤中设置了要在地图显示且有相关图标的事件
				EventFilterSetModel filter = EventFilterSettingFactory.getInstance().getModel(event.getRcuId());
				if (filter != null && (filter.isShowMap() || filter.isShowChart())
						&& !StringUtil.isNullOrEmpty(filter.getImagePath())) {
					AlarmModel model = new AlarmModel(event.getTime(), Alarm.FILTER_EVENT);
					model.setDescrition(filter.getName());
					model.setDrawableFile(filter.getImagePath());
					model.setMsgIndex(event.getPointIndex());
					if (filter.isShowMap())
						alarmListMap.add(model);
					if (filter.isShowChart())
						alarmListChart.add(model);
				}
			}
		}
		//自定义事件
		else if( event.getType()== EventModel.TYPE_DEFINE ){
			AlarmModel alarm = new AlarmModel( event.getTime(),Alarm.CUSTOM_EVENT);
	    	alarm.setDescrition( event.getEventStr() );
	    	alarm.setDrawableFile( event.getIconDrawablePath() );
	    	alarm.setMsgIndex( event.getPointIndex() );
	    	if( event.isAlarm() ){
	    		addAlarm(alarm);
	    	}
	    	if( event.isShowOnMap() ){
	    		alarmListMap.add(alarm);
	    	}
	    	if( event.isShowOnChart() ){
	    		alarmListChart.add(alarm);
	    	}
		}
	}

	/**
	 * 添加设备告警(不用在地图上显示的告警)
	 * @param alarm
	 * @return
	 */
	public AlarmModel addDeviceAlarm(Alarm alarm,int msgIndex){
		AlarmModel  model = new AlarmModel(System.currentTimeMillis(),alarm );
		model.setMsgIndex( msgIndex );
		return this.addAlarm(model);
	}

	public void clearAlarms(boolean cleanMap){
		this.alarmListView.clear();
		mContext.sendBroadcast( new Intent( WalkMessage.ACTION_ALARM_LIST ) );
		this.alarmListSound.clear();
		this.alarmListChart.clear();
		if( cleanMap ){
			this.alarmListMap.clear();
		}
	}
	
	
	/**
	 * 获取告警事件列表
	 * */
	public ArrayList<AlarmModel> getAlarmList() {
		return alarmListView;
	}
	
	/**
	 * 获取告警事件列表
	 * */
	@SuppressWarnings("unchecked")
	public synchronized ArrayList<AlarmModel> getAlarmListClone() {
		return (ArrayList<AlarmModel>) alarmListView.clone();
	}
	
	/**
	 * 根据采样点区间判断该区间内是否有地图告警
	 * @param startMsgIndex 起始采样点
	 * @param endMsgIndex 终止采样点
	 * @return 该区间有告警时返回该告警列表
	 */
	public ArrayList<AlarmModel> getMapAlarmByIndex(int startMsgIndex ,int endMsgIndex){
		ArrayList<AlarmModel> result = new ArrayList<AlarmModel>();
		for( AlarmModel model : this.alarmListMap){
			if( startMsgIndex <= model.getMsgIndex() 
							  && model.getMsgIndex() <= endMsgIndex ){
				result.add(model);
			}
		}
		return result;
	}
	
	/**
	 * [需要在地图上显示图标的告警]<BR>
	 */
	public ArrayList<AlarmModel> getMapAlarmList(){
	    return alarmListMap;
	}
	
	/**
	 * @param startPoint 开始采样点
	 * @param endPoint 结束采样点
	 * @return
	 */
	public ArrayList<AlarmModel> getChartAlarmList(int startPoint,int endPoint){
		ArrayList<AlarmModel> result = new ArrayList<AlarmModel>();
		
		if( startPoint>0 && endPoint>0){
			for(int i=alarmListChart.size()-1;i>=0;i--){
				AlarmModel x = alarmListChart.get(i);
				if( x.getMsgIndex() >= startPoint){
					if(  x.getMsgIndex() <= endPoint){
						result.add(x);
					}
				}else{
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 *判断当前手机的存储空间是否触发了警告线
	 *如果触发了警告线则告警
	 */
	@SuppressWarnings("deprecation")
	public void checkStorge() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				//获得内置剩余空间大小
				//File root = Environment.getRootDirectory();
				//获得扩展存储卡的剩余空间
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availCount = stat.getAvailableBlocks();
				long availableSize = blockSize * availCount;
				
				if(availableSize < STORAGE_RARE){
					UmpcSwitchMethod.sendEventToUmpc(mContext, UMPCEventType.Alarm.getUMPCEvnetType()
					        ,mContext.getString(R.string.sdcardlow_alarm),true );
					addDeviceAlarm( Alarm.DEVICE_STORGE_LOW ,-1);
				}
			} catch (Exception e) {
				LogUtil.w(tag,e.toString() );
			}
		}else{
			addDeviceAlarm( Alarm.DEVICE_STORGE_INVALID ,-1);
		}
	}
	
	public void setTTS(TextToSpeech mts){
		mTts = mts;
	}
	
	public TextToSpeech getTTS(){
		return this.mTts;
	}
	
	
	public void setTestingMos(boolean isTestingMos) {
		this.isTestingMos = isTestingMos;
	}

	@SuppressWarnings("deprecation")
	public void speak(String string){
		if( mTts!=null){
			try{
				mTts.speak( string, TextToSpeech.QUEUE_ADD, null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 设置告警的音量
	 */
	public void setVolume(){
		//音频管理器
		AudioManager audioManager = (AudioManager)
				mContext.getSystemService(Service.AUDIO_SERVICE);
		int volume = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		if( volume < 7  ){
			volume = 10 ;
			audioManager.setStreamVolume(
					AudioManager.STREAM_MUSIC, 
					volume, 
					AudioManager.FLAG_VIBRATE
			);
		}
	}
	
	private boolean playing = false;
	private class ThreadPlay implements Runnable{
		@Override
		public void run() {
			
			playing = true;
			
			while( alarmListSound.size()>0 && !isTestingMos ){
				AlarmModel alarmodel = alarmListSound.get(0);
				if( alarmodel!=null ){
					String alarm = alarmodel.getAlarm().getTtx();
					
					//语音告警等听筒关闭后再播音
					if( alarmodel.getAlarm() == Alarm.TEST_OUTGOING_BLOCKED_CALL
							|| alarmodel.getAlarm()== Alarm.TEST_OUTGOING_DROPPED_CALL 
							|| alarmodel.getAlarm() == Alarm.TEST_INCOMING_BLOCKED_CALL 
							|| alarmodel.getAlarm() == Alarm.TEST_INCOMING_DROPPED_CALL ){
						try {
							Thread.sleep( 3*1000 );
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					speak( alarm );
					LogUtil.i(tag, "playback:"+alarm+","+UtilsMethod.getSimpleDateFormat1(
							alarmodel.getTime() ) );
					
					if( mTts != null ){
						long s = System.currentTimeMillis();
						while( mTts.isSpeaking() && !isTestingMos ){
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if( System.currentTimeMillis()-s > 10*1000 ){
								break;
							}
						}
					}
					
					//告警声音之间间隔200ms
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//播放完后删除
				if( alarmListSound.size()>0 ){
					alarmListSound.remove(0);
				}
			}
			
			playing = false;
		}
	}
	
	
	
//	TraceInfoInterface.getParaValue(),
//	TraceInfoInterface.getParaValue(),
//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_RxLevFull),
//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_RxLevSub),
//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_TxPower),
//	TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_TA)

	
}
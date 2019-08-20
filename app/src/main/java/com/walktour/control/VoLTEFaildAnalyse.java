package com.walktour.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.TotalInterface;
import com.dinglicom.dataset.model.DataSetEvent;
import com.dinglicom.totalreport.ReportXlsConfigModel;
import com.dinglicom.totalreport.RequestSceneXMl;
import com.dinglicom.totalreport.SubDlMessageItem;
import com.walktour.Utils.UnifyL3Decode;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.control.VoiceAnalyse.CSFBNetParams;
import com.walktour.gui.R;
import com.walktour.model.CsfbFaildEventModel;
import com.walktour.model.TdL3Model;
import com.walktour.model.VoLteEventModel;
import com.walktour.model.VoLteFaildModel;
import com.walktour.model.VoiceFaildModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Volte异常分析
 * @author Tangwq
 *
 */
public class VoLTEFaildAnalyse extends VoiceAnalyseInterface {
	/**单例模式**/
	private static VoLTEFaildAnalyse sInstance = null;
	/**当前已分析过的ddib文件名**/
	private String hasAnalysisFile = "";
	/**用于保存当前异常分析结果*/
	protected HashMap<String, ArrayList<VoiceFaildModel>>  volteFildList = null;
	public enum VolteException{
		VoLTEBlock21000(0x00021000,R.string.volte_faild_21_00,R.string.volte_faild_21_00_dec),
		VoLTEBlock21001(0x00021001,R.string.volte_faild_21_01,R.string.volte_faild_21_01_dec),
		VoLTEBlock21002(0x00021002,R.string.volte_faild_21_02,R.string.volte_faild_21_02_dec),
		VoLTEBlock21003(0x00021003,R.string.volte_faild_21_03,R.string.volte_faild_21_03_dec),
		VoLTEBlock21004(0x00021004,R.string.volte_faild_21_04,R.string.volte_faild_21_04_dec),
		VoLTEBlock21011(0x00021011,R.string.volte_faild_21_11,R.string.volte_faild_21_11_dec),
		VoLTEBlock21012(0x00021012,R.string.volte_faild_21_12,R.string.volte_faild_21_12_dec),
		VoLTEBlock21013(0x00021013,R.string.volte_faild_21_13,R.string.volte_faild_21_13_dec),
		VoLTEBlock21014(0x00021014,R.string.volte_faild_21_14,R.string.volte_faild_21_14_dec),
		VoLTEBlock21021(0x00021021,R.string.volte_faild_21_21,R.string.volte_faild_21_21_dec),
		VoLTEBlock21022(0x00021022,R.string.volte_faild_21_22,R.string.volte_faild_21_22_dec),
		VoLTEBlock21023(0x00021023,R.string.volte_faild_21_23,R.string.volte_faild_21_23_dec),
		VoLTEBlock21041(0x00021041,R.string.volte_faild_21_41,R.string.volte_faild_21_41_dec),
		VoLTEBlock21042(0x00021042,R.string.volte_faild_21_42,R.string.volte_faild_21_42_dec),
		VoLTEBlock21043(0x00021043,R.string.volte_faild_21_43,R.string.volte_faild_21_43_dec),
		VoLTEBlock21044(0x00021044,R.string.volte_faild_21_44,R.string.volte_faild_21_44_dec),
		VoLTEBlock21045(0x00021045,R.string.volte_faild_21_45,R.string.volte_faild_21_45_dec),
		VoLTEBlock21046(0x00021046,R.string.volte_faild_21_46,R.string.volte_faild_21_46_dec),
		VoLTEBlock21047(0x00021047,R.string.volte_faild_21_47,R.string.volte_faild_21_47_dec),
		VoLTEDrop22000(0x00022000,R.string.volte_faild_22_00,R.string.volte_faild_22_00_dec),
		VoLTEDrop22002(0x00022002,R.string.volte_faild_22_02,R.string.volte_faild_22_02_dec),
		VoLTEDrop22003(0x00022003,R.string.volte_faild_22_03,R.string.volte_faild_22_03_dec),
		VoLTEDrop22005(0x00022005,R.string.volte_faild_22_05,R.string.volte_faild_22_05_dec),
		VoLTEDrop22006(0x00022006,R.string.volte_faild_22_06,R.string.volte_faild_22_06_dec),
		VoLTEDrop22011(0x00022011,R.string.volte_faild_22_11,R.string.volte_faild_22_11_dec),
		VoLTEDrop22013(0x00022013,R.string.volte_faild_22_13,R.string.volte_faild_22_13_dec),
		VoLTEDrop22014(0x00022014,R.string.volte_faild_22_14,R.string.volte_faild_22_14_dec),
		VoLTEDrop22015(0x00022015,R.string.volte_faild_22_15,R.string.volte_faild_22_15_dec),
		NoVoLTE30000(0x00030000,R.string.volte_faild_30_00,R.string.volte_faild_30_00_dec),

		VolteDefault(-9999,	R.string.csfb_faild_default,R.string.csfb_faild_default);
		
		private final int reasion;
		private final int showNameId;
		private final int showDecNameId;
		private VolteException(int reasion,int showNameId,int showDecNameId){
			this.reasion = reasion;
			this.showNameId = showNameId;
			this.showDecNameId = showDecNameId;
		}
		
		/**获得错误原因码*/
		public int getReasion(){
			return reasion;
		}
		
		/**获得显示名称ID*/
		public int getShowNameId(){
			return showNameId;
		}
		
		/**获得异常事件的详细描述*/
		public int getShowDecNameId(){
			return showDecNameId;
		}
		
		public static VolteException getVolteExceptionByReasion(int reasion){
			VolteException[] reasions = VolteException.values();
			for(VolteException reas : reasions){
				if(reas.getReasion() == reasion){
					return reas;
				}
			}
			return VolteDefault;
		}
	}
	
	/**
	 * 此处定义异常产生是默认存在的事件节点
	 * 并且默认都为无效，或者无效不显示节点
	 */
	private final int[][] volteMOFixedEvents = new int[][]{
		{DataSetEvent.ET_MO_Attempt						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_LTEActiveDedicatedEPSRequest	,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_LTEActiveDedicatedEPSAccept	,NODE_SHOW_DISABLE	,EVENT_TYPE_SUCCESS},
		{DataSetEvent.ET_LTEActiveDedicatedEPSReject	,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MO_Alerting						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Connect						,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Block							,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MO_End							,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Drop							,NODE_SHOW_GONE		,EVENT_TYPE_FAILD}
	};
	
	/**
	 * 此处定义异常产生是默认存在的事件节点
	 * 并且默认都为无效，或者无效不显示节点
	 */
	private final int[][] volteMTFixedEvents = new int[][]{
		{DataSetEvent.ET_MT_Attempt						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_LTEActiveDedicatedEPSRequest	,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_LTEActiveDedicatedEPSAccept	,NODE_SHOW_DISABLE	,EVENT_TYPE_SUCCESS},
		{DataSetEvent.ET_LTEActiveDedicatedEPSReject	,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MT_Alerting						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Connect						,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Block							,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MT_End							,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Drop							,NODE_SHOW_GONE		,EVENT_TYPE_FAILD}
	};

	
	
	//将当前异常任务对象存放到相庆的异常类型列表中
    protected void addCsfbModel(String csfbFaildKey,VoiceFaildModel csfbModel){
    	ArrayList<VoiceFaildModel> csfbList = null;
    	if(volteFildList.containsKey(csfbFaildKey)){
    		csfbList = volteFildList.get(csfbFaildKey);
    	}else{
    		csfbList = new ArrayList<VoiceFaildModel>();
    	}
    	csfbList.add(csfbModel);
    	volteFildList.put(csfbFaildKey, csfbList);
    }
	/**
	 * 构建每个异常的明细信息
	 * @param volteFailList
	 */
	private void buildAnalyseResult(ArrayList<VoLteFaildModel> volteFailList){
		
		for(VoLteFaildModel volteFaild : volteFailList){
			VolteException volte = VolteException.getVolteExceptionByReasion(volteFaild.getReasonCode());
			buildCsfbFaildDetail(volteFaild.getCallType(),volte.getShowNameId(),volte.getShowDecNameId(),volteFaild.getEventList());
		}
	}
	
	/**
	 * VoLTE分析流程，返回ArrayList<VoLteFaildModel>
	 */
	private ArrayList<VoLteFaildModel> getVoLteFaildList(String ddibFile){
		createRequestXML(ddibFile);			//报表请求XML
		TotalInterface.getInstance(mContext).ExportReportJson();  //输出报表请求Json
		return analyzeJson();
	}
	private void getVoLteFaildList(List<String> ddibFiles){
		createRequestXML(ddibFiles);			//报表请求XML
		TotalInterface.getInstance(mContext).ExportReportUKFile();  //输出报表请求Json 
	}
	/**
	 * 创建报表请求XML
	 * @param entry
	 */
	private void createRequestXML(String ddibFile) {
		List<String> ddibPathList = new ArrayList<String>();
		ddibPathList.add(ddibFile);
		ReportXlsConfigModel dlMessageModel = new ReportXlsConfigModel();
		List<SubDlMessageItem> subDlMessageItems = new ArrayList<SubDlMessageItem>();
		SubDlMessageItem subDlMessageItem = new SubDlMessageItem();
		dlMessageModel.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
		subDlMessageItem.setSceneName("");
		dlMessageModel.setTemplateFile(Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig/VoLTE.xml");			//加载报表模板
		subDlMessageItem.setDataFileName(ddibPathList);
		subDlMessageItems.add(subDlMessageItem);
		dlMessageModel.setSubDlMessageItems(subDlMessageItems);
		RequestSceneXMl.getInstance().xmlXlsCreator(dlMessageModel);
	}
	
	/**
	 * 创建报表请求XML
	 * @param entry
	 */
	private void createRequestXML(List<String> ddibFiles) {
		List<String> ddibPathList = new ArrayList<String>();
		ddibPathList.addAll(ddibFiles);
		ReportXlsConfigModel dlMessageModel = new ReportXlsConfigModel();
		List<SubDlMessageItem> subDlMessageItems = new ArrayList<SubDlMessageItem>();
		SubDlMessageItem subDlMessageItem = new SubDlMessageItem();
		dlMessageModel.setSendTime(UtilsMethod.sdFormatss.format(System.currentTimeMillis()));
		subDlMessageItem.setSceneName("");			//加载报表模板
		dlMessageModel.setTemplateFile(Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig/template/AnalysisTemplate.xml");			//加载报表模板
		subDlMessageItem.setDataFileName(ddibPathList);
		subDlMessageItems.add(subDlMessageItem);
		dlMessageModel.setSubDlMessageItems(subDlMessageItems);
		RequestSceneXMl.getInstance().xmlXlsCreator(dlMessageModel);
	}
	/**
	 * 解析报表JSON 整合成LIst
	 */
	private HashMap<String, Object[][]> hashMap = new HashMap<String, Object[][]>();
	
	@SuppressLint({ "UseSparseArrays", "DefaultLocale" })
	private ArrayList<VoLteFaildModel> analyzeJson(){
		ArrayList<VoLteFaildModel> voLteFaildModels = new ArrayList<VoLteFaildModel>();
		String ss = UtilsMethod.readFile(Environment.getExternalStorageDirectory() + "/Walktour/TotalConfig/resultJson/resultJsonXls.json");
		JSONTokener parser = new JSONTokener(ss);
		try {
			JSONObject parent = (JSONObject) parser.nextValue();
			for (Iterator<?> iter = parent.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				JSONArray arr = parent.getJSONArray(key);
				Object[][] arrValues = new Object[arr.length()][];
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					arrValues[i] = new Object[obj.length()];
					int pos = 0;
					for (Iterator<?> iter1 = obj.keys(); iter1.hasNext();) {
						String key1 = (String) iter1.next();
						arrValues[i][pos++] = key1 + ":" + obj.get(key1);
					}
				}
				this.hashMap.put(key, arrValues);								//解析无规则对应节点，不需指定字符
			}
			Iterator<Entry<String, Object[][]>> iter = hashMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object[][]>  entry = iter.next();
				String key = entry.getKey();
				Object[][] value = entry.getValue();
				System.out.println("key---" + key + "     " + value.length);
				for (int i = 0; i < value.length; i++) {
					VoLteFaildModel voLteFaildModel = new VoLteFaildModel();
					voLteFaildModel.setCallType(key.equalsIgnoreCase("mo") ? SP_ID_MO : SP_ID_MT);
					Object[] valueLine =  value[i];
					HashMap<Integer, Long>  difCodeMap = new HashMap<Integer,Long>();
					List<VoLteEventModel> voLteEventModels = new ArrayList<VoLteEventModel>();
					for (int j = 0; j < valueLine.length; j++) {
						VoLteEventModel voLteEventModel = new VoLteEventModel();				//初始化异常model类
						String valueLineStr = valueLine[j].toString();
						if(valueLineStr.startsWith("POINTINDEX_0X")){			//开始解析
							voLteEventModel.setEventId(Integer.valueOf(getSpiltKey(getSpiltValue(valueLineStr)[0]).toLowerCase().replace("0x", ""), 16));
							voLteEventModel.setPointIndex(getSpiltValue(valueLineStr)[1].equals("null") ? 0 : (int)Double.parseDouble(getSpiltValue(valueLineStr)[1]));
							if(voLteEventModel.getPointIndex() != 0 ){
								voLteEventModels.add(voLteEventModel);
							}
						}else if(valueLineStr.startsWith("COMPUTERTIME_0X")){
							if(!getSpiltValue(valueLineStr)[1].equalsIgnoreCase("null")){
								difCodeMap.put(Integer.valueOf(getSpiltKey(getSpiltValue(valueLineStr)[0]).toLowerCase().replace("0x", ""), 16) , UtilsMethod.getTime(getSpiltValue(valueLineStr)[1]));
							}
						}else if(valueLineStr.startsWith("AVERAGE_1")){
							voLteFaildModel.setReasonCode(getSpiltValue(valueLineStr)[1].equalsIgnoreCase("null") ? 0 : (int)Double.parseDouble(getSpiltValue(valueLineStr)[1]));
						}														//节点解析结束
					}
					for (int j = 0; j < voLteEventModels.size(); j++) {
						VoLteEventModel voLteEventModel = voLteEventModels.get(j);
						for (Integer codeID : difCodeMap.keySet()) {
							if(voLteEventModel.getEventId() == codeID){					//采取事件ID对应时间
								voLteEventModel.setTime(difCodeMap.get(codeID));
								break;
							}
						}
					}
					voLteFaildModel.setEventList(voLteEventModels);						//设置事件Model列表	
					if(voLteFaildModel.getReasonCode() != 0 ){
						voLteFaildModels.add(voLteFaildModel);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return voLteFaildModels;
	}
	
	
	
	/**
	 * 取value值
	 * @param valueStr
	 * POINTINDEX_0X30172": 248027
	 * @return
	 */
	public String[] getSpiltValue(String valueStr){
		try {
			String[] valueStrings = valueStr.split(":");
			return valueStrings;
		} catch (Exception e) {
			e.printStackTrace();
			return new String[2];
		}
	}
	
	
	/**
	 * 取key值
	 * @param valueStr
	 * POINTINDEX_0X30172": 248027
	 * @return
	 */
	public String getSpiltKey(String  KeyStr){
		try {
			String[] keyStrings =  KeyStr.split("_");
			return keyStrings[1];
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	private void buildCsfbFaildDetail(int spid,int faildId,int faildDecId,List<VoLteEventModel> eventList){
    	VoiceFaildModel csfbModel = new VoiceFaildModel(spid == SP_ID_MT
    			? volteMTFixedEvents : volteMOFixedEvents);
    	csfbModel.setCsfbFaildStr(mContext.getString(faildId));
    	csfbModel.setCsfbFaildStrDec(mContext.getString(faildDecId));
    	
    	if(spid == SP_ID_MT){
    		csfbModel.setCallType(mContext.getString(R.string.csfb_faild_type_mt));
    	}else{
    		csfbModel.setCallType(mContext.getString(R.string.csfb_faild_type_mo));
    	}
    	
		
		//第一个事件时间，用于后面的两事件间的时延计算
		long firstEventTime = 0;
		for(VoLteEventModel volteEvent : eventList){
			/*String event = dataSetManager.getEvent(Integer.parseInt(events[i]), 
					Integer.parseInt(events[i]), true, 0);
			String[] params = event.split("@@");
			long time = 0;
			if (!params[0].startsWith("f")) {
				time = Long.parseLong(params[0], 16) / 1000; // 时间(微秒转毫秒)
			} else {
				time = 0;
			}*/
			long time = volteEvent.getTime() ;
			int eventCode 	= volteEvent.getEventId();		//Integer.parseInt(params[2], 16);
			int pointIndex 	= volteEvent.getPointIndex();	//Integer.parseInt(params[3], 16);
			
			CsfbFaildEventModel faildEvent = csfbModel.getCsfbEventList().get(eventCode);
			if(faildEvent != null){
				faildEvent.setL3DetailStr( pointIndex != -1 ? dataSetManager.queryL3Detail(pointIndex) : "");
				faildEvent.setNodeShowType(NODE_SHOW_ENABLE);
				faildEvent.setEventTimes(UtilsMethod.sdfhms.format(time));
				if(firstEventTime != 0){
					faildEvent.setDelayByLastEvent(UtilsMethod.decFormat.format(( time - firstEventTime) / 1000f));
				}
				
				int netType = Double.valueOf(dataSetManager.getRealParam(DatasetManager.PORT_4,
	                    UnifyParaID.CURRENT_NETWORKTYPE,
	                    pointIndex,
	                    pointIndex,
	                    false,
	                    true)).intValue();
				CSFBNetParams currentNetParam = VoiceAnalyse.CSFBNetParams.getCSFBNetParamsByType(netType);
				if(currentNetParam != null){
					String paramValues = dataSetManager.batchGetRealParam(
		                    currentNetParam.getNetParamId(),
		                    currentNetParam.getNetParamId().length,
		                    pointIndex,
		                    false);
					
					faildEvent.setCurrentNet(currentNetParam.getNetShowStr());
					faildEvent.setParamValues(UtilsMethod.formatParamToShow(currentNetParam.getNetFmtStr(),paramValues));
				}
				
				//查询指定采样点对应的信令
				String signalStr = dataSetManager.getMsgCode(
						pointIndex,
						pointIndex,
						true,
						false);
				String[] singnalIds = signalStr.split("@@");
				if(singnalIds != null && singnalIds.length == 4){
					String direction = singnalIds[3];
					long msgCode = Long.valueOf(singnalIds[2],16);
					TdL3Model l3 = UnifyL3Decode.disposeL3Info(msgCode);
					faildEvent.setDirection(direction);
					faildEvent.setSignalMsg(l3.getL3Msg());
					
					//如果当前值为Disconnect需查询该原因值
					if(msgCode == UnifyParaID.Disconnect){
						int discReasion = Double.valueOf(dataSetManager.getRealParam(DatasetManager.PORT_4,
			                    UnifyParaID.Disconnect_Reasion,
			                    pointIndex,
			                    pointIndex,
			                    false,
			                    true)).intValue();
						faildEvent.setDisconnectReasion(UtilsMethodPara.getCCDisconnectCause(discReasion));
					}
				}
				
				//如果时间为0表示为第一个事件
				if(firstEventTime == 0){
					//LogUtil.w(TAG, "--firstEvent time:" + faildEvent.getEventTimes() + "--Net:" + faildEvent.getCurrentNet() + "--Param:" + faildEvent.getParamValues());
					csfbModel.setFaildTime(faildEvent.getEventTimes());
					csfbModel.setFaildNetType(faildEvent.getCurrentNet());
					csfbModel.setFaildParamValues(faildEvent.getParamValues());
				}
				
				//将当前事件时间修改到初始时间中用于下次时延计算
				firstEventTime = time;
			}
		}
		
		//csfbModel.removeGoneItem();
		csfbModel.buildCSFBEventArray();
		//将当前异常任务对象添加到列表中
		//csfbFildList.add(csfbModel);
		addCsfbModel(csfbModel.getCsfbFaildStr(),csfbModel);
		//LogUtil.w(TAG, "--getSPEvenDetail:" + csfbModel.toString());
    }
	
	@Override
	public HashMap<String, ArrayList<VoiceFaildModel>> getFaildAnalyseResult(
			String ddibFile, boolean reDecoder) {
		//如果当前异常分柝文件存在,且上次的分析类型与当前要分析的一致
		if(ddibFile.equals(hasAnalysisFile) && volteFildList != null && !reDecoder){
			return volteFildList;
		}
		volteFildList = new HashMap<String, ArrayList<VoiceFaildModel>>();
		hasAnalysisFile = ddibFile;
		
		ArrayList<VoLteFaildModel> volteFailList = getVoLteFaildList(ddibFile);			//调用统计分析模块，并解析异常等操作
		
		dataSetManager.openPlaybackData(DatasetManager.PORT_4, ddibFile);
		buildAnalyseResult(volteFailList);
		dataSetManager.closePlayback(DatasetManager.PORT_4);
		//dataSetManager.startDataSet(false);
		
		return volteFildList;
	}
	
	public void getFaildAnalyseResult(List<String> ddibFiles){
		getVoLteFaildList(ddibFiles);			//调用统计分析模块，并解析异常等操作
 	}
	
	private VoLTEFaildAnalyse(Context context){
		mContext = context;
		dataSetManager = DatasetManager.getInstance(mContext);
		volteFildList = new HashMap<String, ArrayList<VoiceFaildModel>>();
	}
	//异常分析单例对象
	public synchronized static VoLTEFaildAnalyse getInstance(Context context){
			if(sInstance == null){
				sInstance = new VoLTEFaildAnalyse(context);
			}
			return sInstance;
		}
}

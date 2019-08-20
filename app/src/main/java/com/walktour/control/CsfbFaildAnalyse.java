package com.walktour.control;

import android.content.Context;

import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.model.DataSetEvent;
import com.walktour.Utils.UnifyL3Decode;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.base.util.LogUtil;
import com.walktour.control.VoiceAnalyse.CSFBNetParams;
import com.walktour.gui.R;
import com.walktour.model.CsfbFaildEventModel;
import com.walktour.model.TdL3Model;
import com.walktour.model.VoiceFaildModel;

import java.util.ArrayList;
import java.util.HashMap;

public class CsfbFaildAnalyse extends VoiceAnalyseInterface {
	/**单例模式**/
	private static CsfbFaildAnalyse sInstance = null;
	/**当前已分析过的ddib文件名**/
	private String hasAnalysisFile = "";
	/**用于保存当前异常分析结果*/
	protected HashMap<String, ArrayList<VoiceFaildModel>>  csfbFildList = null;
	private final String TAG = "CsfbFaildAnalyse";
	
	//--定义查询类型
	/**查询类型:0所有业务，1只包含应用层业务，2只包含信令层业务*/
	private final int SP_TYPE_ALL		= 0;
	/**查询类型:0所有业务，1只包含应用层业务，2只包含信令层业务*/
//	private final int SP_TYPE_APP		= 1;
	/**查询类型:0所有业务，1只包含应用层业务，2只包含信令层业务*/
//	private final int SP_TYPE_SIGNAL	= 2;
	
	private final int SP_PROPERTY_DISABLE	= -9999;
	
	/**此处为异常分析开始时执行的方法中需要指定的异常分析类型，173,174,161固定为84*/
	//private final int SP_Stat_CalcCSFBInfo	= 84;
	/**统计175,192,193,194,195几种异常计算时需要调此值*/
	//public static final int SP_Stat_CalcCSFBInfo86= 86;
	/**上面84,86两项合成统一接口87,仅需要调用一次即可
	 * 173,174,161;175,192,193,194,195*/
	private final int SP_START_CALCCSFB										= 87;
	private final int SP_PROPERTY_161_0										= 0;
	
	//---查询异常类型
	/**语音业务类型，0：传统方式，1：CSFB方式，2：VoLTE方式*/
	private final int SP_Property_ID_VoiceSPIsCSFB						= 161;
	/**CSFB异常分析，0：未配置CSFB；1：超时；2：配置网络与起呼网络不一致*/
	private final int SP_Property_ID_CSFBException						= 173;
	/**Return Back To LTE失败原因，0：TAUReject，1：TAUTimeout*/
	private final int SP_Property_ID_ReturnToLTEFailure					= 174;
	/**CSFB异常，TAC和LAC不一致	-1：一致	>=0：不一致，及该点的采样点序号*/
	private final int SP_Property_ID_CSFBException_TACDiffLAC				= 175;
	/** CSFB异常，通话结束后没有SIB19 0：有SIB19 1：没有SIB19*/
//	private final int SP_Property_ID_CSFBException_NoTDSSIB19				= 192;
	/**0：TMSI寻呼,1：IMSI寻呼*/
	private final int SP_Property_ID_CSFBException_PagingByIMSI			= 193;
	/**CSFB异常，SIB7是否配置了频点信息 -1：配置了频点信息 >=0：未配置，及SIB7的采样点序号*/
//	private final int SP_Property_ID_CSFBException_SIB7NoFreqInfo 		= 194;
	/**CSFB异常，SIB7配置的频点信息是否不一致 -1：一致 >=0：不一致，及SIB7的采样点序号*/
//	private final int SP_Property_ID_CSFBException_SIB7FreqInfoDifference= 195;
	
	public enum CSFBException{
		CSFB1730(0,		R.string.csfb_faild_173_0,	R.string.csfb_faild_173_0_dec),
		CSFB1731(1,		R.string.csfb_faild_173_1,	R.string.csfb_faild_173_1_dec),
		CSFB1732(2,		R.string.csfb_faild_173_2,	R.string.csfb_faild_173_2_dec),
		CSFB1733(3,		R.string.csfb_faild_173_3,	R.string.csfb_faild_173_3_dec),
		CSFB1734(4,		R.string.csfb_faild_173_4,	R.string.csfb_faild_173_4_dec),
		CSFB1735(5,		R.string.csfb_faild_173_5,	R.string.csfb_faild_173_5_dec),
		CSFB1736(6,		R.string.csfb_faild_173_6,	R.string.csfb_faild_173_6_dec),
		CSFB1737(7,		R.string.csfb_faild_173_7,	R.string.csfb_faild_173_7_dec),
		CSFB1738(8,		R.string.csfb_faild_173_8,	R.string.csfb_faild_173_8_dec),
		CSFB1739(9,		R.string.csfb_faild_173_9,	R.string.csfb_faild_173_9_dec),
		CSFB17310(10,	R.string.csfb_faild_173_10,	R.string.csfb_faild_173_10_dec),
		CSFB173 (-9999,	R.string.csfb_faild_default,R.string.csfb_faild_default);
		
		private final int reasion;
		private final int showNameId;
		private final int showDecNameId;
		private CSFBException(int reasion,int showNameId,int showDecNameId){
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
		
		public static CSFBException getCSFBExceptionByReasion(int reasion){
			CSFBException[] reasions = CSFBException.values();
			for(CSFBException reas : reasions){
				if(reas.getReasion() == reasion){
					return reas;
				}
			}
			return CSFB173;
		}
	}
	
	public enum CSFBReturnFaild{
		CSFB1740(0,		R.string.csfb_faild_174_0,	R.string.csfb_faild_174_0_dec),
		CSFB1741(1,		R.string.csfb_faild_174_1,	R.string.csfb_faild_174_1_dec),
		CSFB1742(2,		R.string.csfb_faild_174_2,	R.string.csfb_faild_174_2_dec),
		CSFB1743(3,		R.string.csfb_faild_174_3,	R.string.csfb_faild_174_3_dec),
		CSFB1744(4,		R.string.csfb_faild_174_4,	R.string.csfb_faild_174_4_dec),
		CSFB1745(5,		R.string.csfb_faild_174_5,	R.string.csfb_faild_174_5_dec),
		CSFB1746(6,		R.string.csfb_faild_174_6,	R.string.csfb_faild_174_6_dec),
		CSFB174 (-9999,	R.string.csfb_faild_default,R.string.csfb_faild_default);
		
		private final int reasion;
		private final int showNameId;
		private final int showDecNameId;
		private CSFBReturnFaild(int reasion,int showNameId,int showDecNameId){
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
		public static CSFBReturnFaild getCSFBReturnFaildByReasion(int reasion){
			CSFBReturnFaild[] reasions = CSFBReturnFaild.values();
			for(CSFBReturnFaild reas : reasions){
				if(reas.getReasion() == reasion){
					return reas;
				}
			}
			return CSFB174;
		}
	}
	
	/**
	 * 此处定义异常产生是默认存在的事件节点
	 * 并且默认都为无效，或者无效不显示节点
	 */
	private final int[][] csfbMOFixedEvents = new int[][]{
		{DataSetEvent.ET_MO_CSFB_Request				,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_CSFB_RRCRelease				,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_CSFB_Coverage				,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Attempt						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Alerting					,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Connect						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_CSFB_Success				,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_CSFB_Failure				,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MO_End							,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MO_Drop						,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MO_Block						,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_ReturnToLTE_Request			,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_ReturnToLTE_Complete			,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_ReturnToLTE_Failure			,NODE_SHOW_GONE		,EVENT_TYPE_FAILD}
	};
	
	/**
	 * 此处定义异常产生是默认存在的事件节点
	 * 并且默认都为无效，或者无效不显示节点
	 */
	private final int[][] csfbMTFixedEvents = new int[][]{
		{DataSetEvent.ET_MT_CSFB_Request				,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_CSFB_RRCRelease				,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_CSFB_Coverage				,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Attempt						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Alerting					,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Connect						,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_CSFB_Success				,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_CSFB_Failure				,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MT_End							,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_MT_Drop						,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_MT_Block						,NODE_SHOW_GONE		,EVENT_TYPE_FAILD},
		{DataSetEvent.ET_ReturnToLTE_Request			,NODE_SHOW_DISABLE	,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_ReturnToLTE_Complete			,NODE_SHOW_GONE		,EVENT_TYPE_NORMAL},
		{DataSetEvent.ET_ReturnToLTE_Failure			,NODE_SHOW_GONE		,EVENT_TYPE_FAILD}
	};

	
	
	//将当前异常任务对象存放到相庆的异常类型列表中
    protected void addCsfbModel(String csfbFaildKey,VoiceFaildModel csfbModel){
    	ArrayList<VoiceFaildModel> csfbList = null;
    	if(csfbFildList.containsKey(csfbFaildKey)){
    		csfbList = csfbFildList.get(csfbFaildKey);
    	}else{
    		csfbList = new ArrayList<VoiceFaildModel>();
    	}
    	csfbList.add(csfbModel);
    	csfbFildList.put(csfbFaildKey, csfbList);
    }
	/**
     * 统计分析项接口测试
     */
	private void TotalAnalyseResult() {

    	LogUtil.w(TAG, "--testDdibTotalResult start--");
    	String splist = dataSetManager.getExistSPList(SP_TYPE_ALL);
    	String[] sps = splist.split("@@");
    	try {
    		for(int i=0; i< sps.length; i++){
    			LogUtil.w(TAG, "sps>>>>" + sps[i]);
    			int SPID = Integer.parseInt(sps[i]);
    			if(SPID == SP_ID_MO || SPID == SP_ID_MT ){
    				int appCount = dataSetManager.getAppointSPCount(SPID);
    				//dataSetManager.calcSPSinglePropertyValue(SPID,CsfbFaild.SP_Stat_CalcCSFBInfo,-9999);
    				//dataSetManager.calcSPSinglePropertyValue(SPID,CsfbFaild.SP_Stat_CalcCSFBInfo86,-9999);
    				dataSetManager.calcSPSinglePropertyValue(SPID,SP_START_CALCCSFB,-9999);
    				
    				for(int j = 0; j < appCount; j++){
    					double isCsfb = dataSetManager.getSPPropertyDoubleValue(SPID,j,SP_Property_ID_VoiceSPIsCSFB);
    					dataSetManager.getSPBaseInfoValue(SPID,j);
    					if(isCsfb == SP_PROPERTY_161_0){
    						// 非CSFB异常处理
    						buildCsfbFaildDetail(SPID,j,R.string.csfb_faild_161_0,R.string.csfb_faild_161_0_dec);
    					}else{
    						double reasion173 = dataSetManager.getSPPropertyDoubleValue(SPID,j,SP_Property_ID_CSFBException);
    						if(reasion173 != SP_PROPERTY_DISABLE){
    							CSFBException exceptione = CSFBException.getCSFBExceptionByReasion((int)reasion173);
    							buildCsfbFaildDetail(SPID,j,exceptione.getShowNameId(),exceptione.getShowDecNameId());
    						}
    						double reasion174 = dataSetManager.getSPPropertyDoubleValue(SPID,j,SP_Property_ID_ReturnToLTEFailure);
    						if(reasion174 != SP_PROPERTY_DISABLE){
    							CSFBReturnFaild returnFaild = CSFBReturnFaild.getCSFBReturnFaildByReasion((int)reasion174);
    							buildCsfbFaildDetail(SPID,j,returnFaild.getShowNameId(),returnFaild.getShowDecNameId());
    						}
    						
    						int reasion175 = (int)dataSetManager.getSPPropertyDoubleValue(SPID,j,
    								SP_Property_ID_CSFBException_TACDiffLAC);
    						if(reasion175 >= 0){
    							buildCsfbFaildDetail(SPID,j,R.string.csfb_faild_175_0A,R.string.csfb_faild_175_0A_dec);
    						}
    						
//    						int reasion192 = (int)dataSetManager.getSPPropertyDoubleValue(SPID,j,
//    								SP_Property_ID_CSFBException_NoTDSSIB19);
//    						if(reasion192 == 1){
//    							buildCsfbFaildDetail(SPID,j,R.string.csfb_faild_192_1,R.string.csfb_faild_192_1_dec);
//    						}
    						
    						int reasion193 = (int)dataSetManager.getSPPropertyDoubleValue(SPID,j,
    								SP_Property_ID_CSFBException_PagingByIMSI);
							if (reasion193 == 1 || reasion193 == 0){
    							buildCsfbFaildDetail(SPID,j,R.string.csfb_faild_193_1,R.string.csfb_faild_193_1_dec);
    						}
    						
//    						int reasion194 = (int)dataSetManager.getSPPropertyDoubleValue(SPID,j,
//    								SP_Property_ID_CSFBException_SIB7NoFreqInfo);
//    						if(reasion194 >= 0){
//    							buildCsfbFaildDetail(SPID,j,R.string.csfb_faild_194_0A,R.string.csfb_faild_194_0A_dec);
//    						}     新总说194ID不做异常,暂时屏蔽
    						
//    						int reasion195 = (int)dataSetManager.getSPPropertyDoubleValue(SPID,j,
//    								SP_Property_ID_CSFBException_SIB7FreqInfoDifference);
//    						if(reasion195 >= 0){
//    							buildCsfbFaildDetail(SPID,j,R.string.csfb_faild_195_0A,R.string.csfb_faild_195_0A_dec);
//    						}
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	LogUtil.w(TAG, "--testDdibTotalResult end--");
    }
    
    private void buildCsfbFaildDetail(int spid,int appN,int faildId,int faildDecId){
    	VoiceFaildModel csfbModel = new VoiceFaildModel(spid == SP_ID_MT
    			? csfbMTFixedEvents : csfbMOFixedEvents);
    	csfbModel.setCsfbFaildStr(mContext.getString(faildId));
    	csfbModel.setCsfbFaildStrDec(mContext.getString(faildDecId));
    	
    	if(spid == SP_ID_MT){
    		csfbModel.setCallType(mContext.getString(R.string.csfb_faild_type_mt));
    	}else{
    		csfbModel.setCallType(mContext.getString(R.string.csfb_faild_type_mo));
    	}
    	
    	//dataSetManager.getSPRangeInfo(spid,appN,true);
		//dataSetManager.getSPEventCount(spid,appN);
		String eventList = dataSetManager.getSPEventIndexList(spid,appN);
		String[] events = eventList.split("@@");
		
		//第一个事件时间，用于后面的两事件间的时延计算
		long firstEventTime = 0;
		for(int i = 0; i < events.length; i++){
			String event = dataSetManager.getEvent(Integer.parseInt(events[i]), 
					Integer.parseInt(events[i]), true, 0);
			String[] params = event.split("@@");
			long time = 0;
			if (!params[0].startsWith("f")) {
				time = Long.parseLong(params[0], 16) / 1000; // 时间(微秒转毫秒)
			} else {
				time = 0;
			}
			int eventCode = Integer.parseInt(params[2], 16);
			int pointIndex = Integer.parseInt(params[3], 16);
			
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
    
	private CsfbFaildAnalyse(Context context) {
		mContext = context;
		dataSetManager = DatasetManager.getInstance(mContext);
		csfbFildList = new HashMap<String, ArrayList<VoiceFaildModel>>();
	}

	// 异常分析单例对象
	public synchronized static CsfbFaildAnalyse getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new CsfbFaildAnalyse(context);
		}
		return sInstance;
	}

	@Override
	public HashMap<String, ArrayList<VoiceFaildModel>> getFaildAnalyseResult(String ddibFile, boolean reDecoder) {

		// 如果当前异常分柝文件存在,且上次的分析类型与当前要分析的一致
		if (ddibFile.equals(hasAnalysisFile) && csfbFildList != null && !reDecoder) {
			return csfbFildList;
		}
		csfbFildList = new HashMap<String, ArrayList<VoiceFaildModel>>();
		hasAnalysisFile = ddibFile;
		dataSetManager.openPlaybackData(DatasetManager.PORT_4, ddibFile);
		TotalAnalyseResult();
		dataSetManager.closePlayback(DatasetManager.PORT_4);
		return csfbFildList;
	}
}

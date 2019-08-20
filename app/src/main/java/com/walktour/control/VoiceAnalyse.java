package com.walktour.control;

import android.content.Context;

import com.walktour.Utils.UnifyParaID;
import com.walktour.model.VoiceFaildModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * CSFB异常分析控制类
 * 包括相关类型的常量定义
 * @author tangwq
 *
 */
public class VoiceAnalyse {
	/**异常分析类型 CSFB*/
	public static final int FAILD_TYPE_CSFB 	= 1;
	/**异常分析类型 VOLTE*/
	public static final int FAILD_TYPE_VOLTE	= 2;
	
	public static final String CSFB_LTE_FMT	= "EARFCN:%s@@PCI:%s@@RSRP:%s@@SINR:%s";
	public static final int[]  CSFB_LTE_ID	= new int[]{
			UnifyParaID.L_SRV_EARFCN, UnifyParaID.L_SRV_PCI,
			UnifyParaID.L_SRV_RSRP, UnifyParaID.L_SRV_SINR
	};
	public static final String CSFB_TD_FMT	= "UARFCN:%s@@CPI:%s@@PCCPCH RSCP:%s@@PCCPCH C/I:%s";
	public static final int[]  CSFB_TD_ID	= new int[]{
			UnifyParaID.TD_Ser_UARFCN, UnifyParaID.TD_Ser_CPI,
			UnifyParaID.TD_Ser_PCCPCHRSCP, UnifyParaID.TD_Ser_PCCPCHC2I
	};
	public static final String CSFB_WCDMA_FMT	= "UARFCN:%s@@PSC:%s@@Total RSCP:%s@@Total Ec/Io:%s";
	public static final int[]  CSFB_WCDMA_ID	= new int[]{
			UnifyParaID.W_Ser_DL_UARFCN, UnifyParaID.W_Ser_Ref_PSC,
			UnifyParaID.W_Ser_Total_RSCP, UnifyParaID.W_Ser_Total_EcIo
	};
	public static final String CSFB_GSM_FMT	= "BCCH:%s@@BSIC:%s@@RxLevFull:%s@@RxLevSub:%s";
	public static final int[]  CSFB_GSM_ID	= new int[]{
			UnifyParaID.G_Ser_BCCH,UnifyParaID.G_Ser_BSIC, 
			UnifyParaID.G_Ser_RxLevFull,UnifyParaID.G_Ser_RxLevSub
	};
	public static final String CSFB_CDMA_FMT= "Freq:%s@@Refer PN:%s@@Total Ec/Io:%s@@RxAGC:%s";
	public static final int[]  CSFB_CDMA_ID	= new int[]{
			UnifyParaID.C_Frequency, UnifyParaID.C_ReferencePN,
			UnifyParaID.C_TotalEcIo, UnifyParaID.C_RxAGC
	};
	
	
	//供界面透传数据用
	private HashMap<String, ArrayList<VoiceFaildModel>>  transmissionDataMap = new HashMap<String, ArrayList<VoiceFaildModel>>();
	
	
	public HashMap<String, ArrayList<VoiceFaildModel>> getTransmissionDataMap() {
		return transmissionDataMap;
	}
	public void setTransmissionDataMap(
			HashMap<String, ArrayList<VoiceFaildModel>> transmissionDataMap) {
		this.transmissionDataMap = transmissionDataMap;
	}

	public static enum CSFBNetParams{
		LTE		(UnifyParaID.NET_LTE,		CSFB_LTE_FMT,	CSFB_LTE_ID		,"LTE"),
		TDSCDMA	(UnifyParaID.NET_TDSCDMA,	CSFB_TD_FMT,	CSFB_TD_ID		,"TD-SCDMA"),
		WCDMA	(UnifyParaID.NET_WCDMA,		CSFB_WCDMA_FMT,	CSFB_WCDMA_ID	,"WCDMA"),
		GSM		(UnifyParaID.NET_GSM,		CSFB_GSM_FMT,	CSFB_GSM_ID		,"GSM"),
		CDMA	(UnifyParaID.NET_CDMA_EVDO,	CSFB_CDMA_FMT,	CSFB_CDMA_ID	,"CDMA");
		
		private final int    netType;
		private final String netFmtStr;
		private final String netShowStr;
		private final int[]  netParamId;
		private CSFBNetParams(int netType,String netFmtStr,int[] netParamId,String netShowStr){
			this.netType = netType;
			this.netFmtStr = netFmtStr;
			this.netParamId = netParamId;
			this.netShowStr = netShowStr;
		}
		
		public int getNetType(){
			return netType;
		}
		public String getNetFmtStr(){
			return netFmtStr;
		}
		public String getNetShowStr(){
			return netShowStr;
		}
		public int[] getNetParamId(){
			return netParamId;
		}
		/**通过网络类型获取相应的网络结构*/
		public static CSFBNetParams getCSFBNetParamsByType(int netType){
			CSFBNetParams[] params = CSFBNetParams.values();
			for(CSFBNetParams param : params){
				if(param.getNetType() == netType){
					return param;
				}
			}
			
			return null;
		}
	}
	
//	private static String lastDdibFile = "";
//	/**
//	 * 获得当前应用中最后一个DDIB文件路径
//	 * 如果当前没有选中文件时值为""
//	 * @return
//	 */
//	public static String getLastDdbiFile(){
//		return lastDdibFile;
//	}
//	/**
//	 * 设置当前应用的最后一个ddib文件路径
//	 * 此路径为测试结束后的最后一个文件或都在异常分析中选择的某个ddib文件
//	 * @param ddibFile
//	 */
//	public static void setLastDdbiFile(String ddibFile){
//		lastDdibFile = ddibFile;
//	}
	
	/**
	 * 统计ID
	 */
	private static String mTotalFileId = "-1";

	
	public String getTotalFileId() {
		return mTotalFileId;
	}
	public static void setTotalFileId(String totalFileId) {
		mTotalFileId = totalFileId;
	}
	
    
	private static VoiceAnalyse sInstance = null;
	private Context mContext = null;
	private VoiceAnalyseInterface voiceAnalyse;
	
	
	private VoiceAnalyse(Context context){
		this.mContext = context;
	}
	
	//异常分析单例对象
	public synchronized static VoiceAnalyse getInstance(Context context){
		if(sInstance == null){
			sInstance = new VoiceAnalyse(context);
		}
		
		return sInstance;
	}
//	
//	/**
//	 * 获得异常分析结果
//	 * 不传入文件名的时候由当前方法自己去取测试结束时存储的文件名
//	 * @return
//	 */
//	public HashMap<String, ArrayList<VoiceFaildModel>> getCsfbFaildResult(){
//		return getCsfbFaildResult(FAILD_TYPE_CSFB,lastDdibFile,false);
//	}
//	
//	/**
//	 * 获得异常分析结果
//	 * 不传入文件名的时候由当前方法自己去取测试结束时存储的文件名
//	 * @return
//	 */
//	public HashMap<String, ArrayList<VoiceFaildModel>> getCsfbFaildResult(int analyseType){
//		return getCsfbFaildResult(analyseType,lastDdibFile,false);
//	}
	
	/**
	 * 根据传进来的ddib文件获得该文件对的CSFB分析结果
	 * 如果传进来的文件与上一次的文件名一致，则不执行分析而是直接反回当前存在的分结果
	 * @param ddibFile		ddib文件名全路径
	 * @return
	 */
	public  HashMap<String, ArrayList<VoiceFaildModel>> getCsfbFaildResult(String ddibFile){
		return getCsfbFaildResult(FAILD_TYPE_CSFB,ddibFile,false);
	}
	
	/**
	 * 根据传进来的ddib文件获得该文件对的CSFB分析结果
	 * 如果传进来的文件与上一次的文件名一致，则不执行分析而是直接反回当前存在的分结果
	 * @param ddibFile		ddib文件名全路径
	 * @return
	 */
	public  HashMap<String, ArrayList<VoiceFaildModel>> getCsfbFaildResult(int analyseType,String ddibFile){
		return getCsfbFaildResult(analyseType,ddibFile,false);
	}
	
	/**
	 * 根据传进来的ddib文件获得该文件对的CSFB分析结果
	 * 如果传进来的文件与上一次的文件名一致，则不执行分析而是直接反回当前存在的分结果
	 * @param analyseType		异常分析类型 1 csfb,2 volte
	 * @param ddibFile		ddib文件名全路径
	 * @param reDecoder		重解当前文件
	 * @return
	 */
	public  HashMap<String, ArrayList<VoiceFaildModel>> getCsfbFaildResult(int analyseType,String ddibFile,boolean reDecoder){
		if(!(new File(ddibFile)).exists()){
//			lastDdibFile = "";
			return null;
		}
//		lastDdibFile = ddibFile;
		
		if(analyseType == FAILD_TYPE_VOLTE){
			voiceAnalyse = VoLTEFaildAnalyse.getInstance(mContext);
		}else{
			
			voiceAnalyse = CsfbFaildAnalyse.getInstance(mContext);
		}
		transmissionDataMap=voiceAnalyse.getFaildAnalyseResult(ddibFile,reDecoder);
		return transmissionDataMap;
	}
	
}

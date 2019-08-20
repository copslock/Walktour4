package com.walktour.Utils;

import android.content.Context;

import com.walktour.Utils.UnifyStruct.ParaStruct;
import com.walktour.gui.map.TraceInfoData;
import com.walktour.model.DecodeResult;
import com.walktour.model.GsmStructModel;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public abstract class TraceInfoInterface implements Serializable{
	protected Context context;
	
	public TraceInfoInterface(Context context){
		this.context = context;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ActivateSetShow	=	"A";	//激活集状态显示符
	public static final String MonitorSetShow	=	"M";	//监视集状态显示符
	public static final String PHONE_STATE_IN_SERVICE = "In Service";	/*用api判断的手机状态*/
	public static final String PHONE_STATE_OUTOFSERVICE = "Out Of Service";	/*用api判断的手机状态*/
	public static final String PHONE_STATE_POWEROFF = "Power Off";			/*用api判断的手机状态*/
	
	public static TraceInfoData traceData 	= new TraceInfoData();
	public static String totalDetailPara   	= "";	//统计明细参数收集
	public static String callEventMsg 		= "";
	public static String callAbnormityPara 	= "";	//异常监控时监控参数的字符串
	public static String dataAbnormityPara 	= "";	//访问网络时监控参数管子符串
	public static int chartCurrentPage 		= 1;
	public static WalkStruct.ShowInfoType currentShowTab = WalkStruct.ShowInfoType.Default;
	public static WalkStruct.ShowInfoType currentShowChildTab = WalkStruct.ShowInfoType.Default;
	public static WalkStruct.CurrentNetState currentNetType = WalkStruct.CurrentNetState.Unknown;		//判断当前网络类型
	public static WalkStruct.CurrentNetState  decodeFreezeNetType = WalkStruct.CurrentNetState.Unknown;		//冻屏锁定网络
	public static WalkStruct.ShowInfoType saveEndShowChildTab = WalkStruct.ShowInfoType.Default;
	/**当前显示的默认地图*/
	public static WalkStruct.ShowInfoType currentMapChildTab = WalkStruct.ShowInfoType.Default;
	
	/**存储解码过程的参数结果*/
	protected static HashMap<Integer, String> decodeParaResult 	= new HashMap<Integer,String>();//id参数
	protected static HashMap<Integer, ParaStruct> decodeParaStruct 	= new HashMap<Integer,ParaStruct>();//结构体参数

	/**是否处于专业测试-》事件界面标志**/
	public static boolean sIsOnEvent = false;
	/**是否处于专业测试-》信令界面标志**/
	public static boolean sIsOnL3Msg = false;
	
	/**冻屏时解存储之后的解码结果*/
	protected static HashMap<Integer, String> decodeFreezeBak 	= new HashMap<Integer,String>();
	
	protected DecimalFormat 		df 	= new DecimalFormat("#.##");  
	protected SimpleDateFormat	sdf = new SimpleDateFormat("HH:mm:ss");	//事件前时间格式
	/** 是否保存文件轨迹图片 */
	public static boolean isSaveFileLocus = false;
	/** 保存文件轨迹图片 */
	public static String saveFileLocusPath;
	
	public static GsmStructModel gsmStructModel = null;
	
	/**
	 * 每秒钟刷新时，根据当前解析实体类设置界面显示值
	 */
	public abstract void buildTraceShowData();
	/**
	 * 根据每条信令，解出相应的数据信息
	 * 信令格式为：code##flag@@id@value@scale##flag@@id@value@scale...如此循环
	 * 其中 flag = 1表示该值有效，-1表示该值无效需清值
	 * id指的是每个参数的唯一ID值，value为该ID的对应值，scale指的是value的放大比例
	 */
	public abstract DecodeResult disposeDecoderResult(String result);
	
	public void setPhoneState(String state){};
	public void phoneStateIdleToCleanValue(){}
	
	/**获取指定参数ID对应的参数值*/
	public static String getParaValue(int paraId){
	    try{
	        return decodeParaResult.get(paraId) == null ? "" : decodeParaResult.get(paraId);
	    }catch(Exception e){
	        e.printStackTrace();
	        return "";
	    }
	}
	
	/**
	 * 获取指定参数ID对应的参数整形值
	 * 该方法如果取不到值时返回0
	 * */
	public static int getIntParaValue(int paraId){
		return getIntParaValue(paraId,10);
	}
	
	/**
	 * 获取指定参数ID对应的参数整形值
	 * 该方法如果取不到值时返回0
	 * */
	public static String getFloatParaValue(int paraId){
		return getFloatParaValue(paraId,1f);
	}
	
	public static String getFloatParaValue(int paraId,float scale){
		try{
			if(!getParaValue(paraId).equals("")){
				return UtilsMethod.decFormat.format(Float.parseFloat(getParaValue(paraId)) / scale);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 获取指定参数ID对应的参数整形值
	 * 该方法如果取不到值时返回0
	 * */
	public static int getIntParaValue(int paraId,int radix){
		try{
			if(!getParaValue(paraId).equals("")){
				String value = getParaValue(paraId);
				if(value.indexOf(".") > 0){
					value = value.substring(0,value.indexOf("."));
				}
				return Integer.parseInt(value,radix);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	/**获得实时解码参数*/
	public static String getRealParaValue(int paraId){
		try{
			if(ApplicationModel.getInstance().isFreezeScreen()){
				return decodeFreezeBak.get(paraId) == null ? "" : decodeFreezeBak.get(paraId);
	        }else{
	        	return decodeParaResult.get(paraId) == null ? "" : decodeParaResult.get(paraId);
	        }
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	/**冻屏时备份当前的解码结果用于存放冻屏后的相关参数变化*/
	@SuppressWarnings("unchecked")
	public static synchronized void freezeBakupResult(){
		try{
			decodeFreezeBak = (HashMap<Integer, String>)decodeParaResult.clone();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**解冻时，将当前的解码结果还原到解码结果中，并将冻屏备份结果清空*/
	@SuppressWarnings("unchecked")
	public static synchronized void unFreezeReductionResult(){
		try{
			decodeParaResult = (HashMap<Integer, String>)decodeFreezeBak.clone();
			decodeFreezeBak.clear();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static synchronized void decodeResultUpdate(int id,String valueStr){
		/**
         * 解码结果存到相应的HashMap表中,界面，IPAD端等参数显示从该位置获得
         * twq20130805，如果当前为冻屏状态，将参数变化结果存到冻屏备份结果中
         */
        if(ApplicationModel.getInstance().isFreezeScreen()){
        	decodeFreezeBak.put(id, valueStr);
        }else{
        	decodeParaResult.put(id, valueStr);
        }
        traceData.setMapParamInfo(Integer.toHexString(id), valueStr);
	}
	
	public static synchronized void decodeStructUpdate(int flag,ParaStruct struct){
		decodeParaStruct.put(flag, struct);
	}
	
	/**
	 * 根据flag或取结构体，获取之后请下播为flag对应的结构体,例如 
	 * int flag = UnifyStruct.TDPhysChannelInfoDataV2.FLAG;
	 * UnifyStruct.TDPhysChannelInfoDataV2 tdChanInfo = 
	 * 			(UnifyStruct.TDPhysChannelInfoDataV2) getParaStruct( flag );
	 * @param flag
	 * @return
	 */
	public static synchronized ParaStruct getParaStruct(int flag){
		ParaStruct result = null;
		if( decodeParaStruct.containsKey(flag) ){
			result = decodeParaStruct.get( flag );
		}
		return result;
	}
	
	public static GsmStructModel getGsmStructModel() {
		return gsmStructModel;
	}
	public static void setGsmStructModel(GsmStructModel gsmStructModel) {
		TraceInfoInterface.gsmStructModel = gsmStructModel;
	}
	
}

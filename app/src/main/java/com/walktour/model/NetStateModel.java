package com.walktour.model;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.base.util.LogUtil;

/**
 * [手机当前信号状态]<BR>
 * 存储手机当前网络状态的相关信息
 * 该网络状态通过解码获得
 * 
 * @author tangwq
 * @version [WalkTour Client V100R001C03, 2012-10-24] 
 */
public class NetStateModel {
	
    private CurrentNetState currentNetType  = CurrentNetState.Unknown;
    private static NetStateModel sInstance = null;
    
    private NetStateModel(){

    }
    
    public synchronized static NetStateModel getInstance(){
    	if(sInstance == null){
    		sInstance = new NetStateModel();
    	}
    	return sInstance;
    }
    
    /**
     * 获取PBM业务所要使用的网络类型
     * @return 0=unknown;1=GSM-GPRS;2=EGPRS;3=CDMA_1X; 
     * 			4=CDMA_EVDO_RA;5=CDMA_EVDO;RB 6=TDSCDMA_HSPA; 
     * 			7=TDSCDMA_HSPA;8=WCDMA_R99;9=WCDMA_HSPA;
     * 			10=WCDMA_HSPA_PLUS;11=WCDMA_HSPA_PLUS_DC;12=LTE_1;
     * 			13=LTE_2;14=LTE_3;15=LTE_4;16=LTE_5;17=LTE_6;18=wifi;
     */
	public int getCurrentNetTypeForPBM() {
		int currNetType = 0;
		try {
			LogUtil.d("PBMTest", "start getCurrentNetTypeForPBM");
			if (!ApplicationModel.getInstance().isTraceInitSucc()) {
				return currNetType;
			}
			currNetType = this.getIntParaValue("RBC", UnifyParaID.PBM_RBC);
			//当获取PBM网络类型异常是则根据当前的网络类型设置个默认的类型用于PBM测试
			CurrentNetState net = this.getCurrentNetType();
			switch (net) {
			case TDSCDMA:
				currNetType = 7;
				break;
			case CDMA:
				currNetType = 5;
				break;
			case GSM:
				currNetType = 2;
				break;
			case LTE:
				currNetType = 15;
				break;
			case WCDMA:
				currNetType = 9;
				break;
			default:
				currNetType = 0;
				break;
			}
			return currNetType;
		} catch (Exception e) {
			LogUtil.e("PBMTest", e.getMessage());
			e.printStackTrace();
		}
		return currNetType;
	}
	
	/**
	 * 获得参数的整型值
	 * @param name 参数名称
	 * @param id 参数ID
	 * @return 参数值
	 */
	private int getIntParaValue(String name,int id){
		String value = TraceInfoInterface.getParaValue(id);
		LogUtil.d("PBMTest", name + " = " + value);
		try{
			return Integer.parseInt(value);
		} catch (Exception e) {
		}
		return -1;
	}
	
    /**
     * 0x7F1D2001
     * 当前网络GSM = 0x01,WCDMA = 0x02,TD-SCDMA = 0x04,CDMA\EVDO = 0x08,LTE = 0x10,Unknown = 0x20,NoService = 0x80
     * @return the currentNetType
     */
    public CurrentNetState getCurrentNetType() {
    	try{
    		if(!TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_NETWORKTYPE).equals("")){
    			currentNetType = CurrentNetState.getNetStateById(Double.valueOf(TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_NETWORKTYPE)).intValue());
    		}else{
    			currentNetType = CurrentNetState.Unknown;
    		}
    	}catch(Exception e){
    		currentNetType = CurrentNetState.Unknown;
    		e.printStackTrace();
    	}
        return currentNetType;
    }
    
    /**
     * 0x7F1D2001
     * 阻塞的查询当前网络类型
     * 当前网络GSM = 0x01,WCDMA = 0x02,TD-SCDMA = 0x04,CDMA\EVDO = 0x08,LTE = 0x10,Unknown = 0x20,NoService = 0x80
     * @return the currentNetType
     */
    public CurrentNetState getCurrentNetTypeSync() {
    	try{
    		if(!TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_NETWORKTYPE).equals("")){
    			currentNetType = CurrentNetState.getNetStateById(Double.valueOf(TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_NETWORKTYPE)).intValue());
    		}else{
    			currentNetType = CurrentNetState.Unknown;
    		}
    	}catch(Exception e){
    		currentNetType = CurrentNetState.Unknown;
    		e.printStackTrace();
    	}
    	return currentNetType;
    }
    
    /**
     * 获取当前已存在的网络类型
     * @return
     */
    public CurrentNetState getCurrentNetByHistory(){
    	try{
    		if(!TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_NETWORKTYPE).equals("")){
    			currentNetType = CurrentNetState.getNetStateById(Double.valueOf(TraceInfoInterface.getParaValue(UnifyParaID.CURRENT_NETWORKTYPE)).intValue());
    		}else{
    			currentNetType = CurrentNetState.Unknown;
    		}
    	}catch(Exception e){
    		currentNetType = CurrentNetState.Unknown;
    		e.printStackTrace();
    	}
    	return currentNetType;
    }
    
    /**
     * [当前是否空闲状态]<BR>
     * [这个空闲状态不同于API获取到的语音idle状态]
     * @return
     */
    public boolean isIdelState(){
    	getCurrentNetType();
    	return (TraceInfoInterface.getParaValue(currentNetType.getCurrentNetStateId()).equals("")
    			|| TraceInfoInterface.getIntParaValue(currentNetType.getCurrentNetStateId(),16) == 0);
    }
    
    /**
     * 获得当前网络的网络状态
     * @param
     * @return
     */
    public boolean isIdleState(){
    	return (TraceInfoInterface.getParaValue(currentNetType.getCurrentNetStateId()).equals("")
    			|| TraceInfoInterface.getIntParaValue(currentNetType.getCurrentNetStateId(),16) == 0);
    }
    
    /**
     * 获得EVDO网络状态状态是否为IDLE
     * @return
     */
    public boolean isEvdoIdle(){
    	getCurrentNetType();
    	return (TraceInfoInterface.getParaValue(currentNetType.getCurrentNetStateId()).equals("")
    			|| TraceInfoInterface.getIntParaValue(UnifyParaID.CURRENT_STATE_EVDO,16) == 0);
    }
    
    /**
     * 获得当前网络的网络状态信息
     * @return
     */
    public String getCurrentNetState(){
    	getCurrentNetType();
    	switch(currentNetType){
    	case GSM:
    		return UtilsMethodPara.getGsmNetState(TraceInfoInterface.getIntParaValue(currentNetType.getCurrentNetStateId(),16));
    	default :
    		return UtilsMethodPara.getNetState(TraceInfoInterface.getIntParaValue(currentNetType.getCurrentNetStateId(),16));
    	}
    }
    
    /**
     * 根据传入的当前网络对象
     * 获得当前网络的网络状态信息
     * @return
     */
    public String getCurrentNetState(CurrentNetState netState){
    	switch(netState){
    	case GSM:
    		return UtilsMethodPara.getGsmNetState(netState.getCurrentNetStateId());
    	default :
    		return UtilsMethodPara.getNetState(netState.getCurrentNetStateId());
    	}
    }
}

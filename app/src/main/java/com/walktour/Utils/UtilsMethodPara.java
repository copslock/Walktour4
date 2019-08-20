package com.walktour.Utils;

import android.content.Context;
import android.util.SparseArray;

import com.walktour.Utils.UnifyStruct.LTEAPN;
import com.walktour.Utils.UnifyStruct.LTEEPSBearerContext02C1;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ParameterSetting;
import com.walktour.model.Especial;
import com.walktour.model.EspecialRow;
import com.walktour.model.GsmStructModel;
import com.walktour.model.Parameter;

import java.util.Map;

/**
 * [与Walktour业务相关的统一方法]<BR>
 * [功能详细描述]
 * @author tangwq
 * @version [WalkTour Client V100R001C03, 2013-2-25] 
 */
public class UtilsMethodPara {
		
	/**
	 * 返回GSM下State参数的对应的枚举值
	 * @param gmsState
	 * @return
	 */
	public static String getGsmRRStateStr(String state){
		if(state.equals("0")){
			state = "Inactive";
		}else if(state.equals("1")){
			state = "Going Active";
		}else if(state.equals("2")){
			state = "Going Inactive";
		}else if(state.equals("3")){
			state = "Cell Selection";
		}else if(state.equals("4")){
			state = "PLMN List Construction";
		}else if(state.equals("5")){
			state = "Idle";
		}else if(state.equals("6")){
			state = "Cell Reselection";
		}else if(state.equals("7")){
			state = "Connection Pending";
		}else if(state.equals("8")){
			state = "Choose Cell";
		}else if(state.equals("9")){
			state = "Data Transfer";
		}else if(state.equals("10")){
			state = "No Channels";
		}else if(state.equals("11")){
			state = "Connection Release";
		}else if(state.equals("12")){
			state = "Early Camped Wait For SI";
		}else if(state.equals("13")){
			state = "W2G Interrat Handover Progress";
		}else if(state.equals("14")){
			state = "W2G Interrat Reselection Progress";
		}else if(state.equals("15")){
			state = "W2G Interrat CC Order Progress";
		}else if(state.equals("16")){
			state = "G2W Interrat Reselection Progress";
		}else if(state.equals("17")){
			state = "Wait For Early PScan";
		}else if(state.equals("18")){
			state = "GRR";
		}else if(state.equals("19")){
			state = "G2W Interrat Handover Progress";
		}else if(state.equals("20")){
			state = "Backgroud PLMN Search";
		}else if(state.equals("21")){
			state = "W2G Service Redirection In Progress";
		}else if(state.equals("22")){
			state = "Lower Level Failure";
		}else if(state.equals("23")){
			state = "W2G Backgroud PLMN Search";
		}
		 
		 return state;
	}
	
	/**返回GSM网络下 V-Codec相应值*/
	public static String getGsmVCodec(String str){
		if(str.equals("0")){
            return "FR";
        }else if(str.equals("1")){
            return "EFR";
        }else if(str.equals("2")){
            return "AMR-Full";
        }else if(str.equals("3")){
            return "HR";
        }else if(str.equals("4")){
            return "AMR-Half";
        }else{
        	return "";
        }
	}
	/**获得CDMA电话状态显示字符串 //cpsIdle =0, cpsWait=1, cpsBusy=2, cpsRelease =3, cpsPage=3*/
    public static String getCdmaPhoneState(String str){
        if(str.equals("1")){
            return "Wait";
        }else if(str.equals("2")){
            return "Busy";
        }else if(str.equals("3")){
            return "Release";
        }else if(str.equals("4")){
            return "Page";
        }else{
            return "Idle";
        }
    }
    
    /**返回 CDMA State下*/
    public static String getCdmaState(String str){
    	if(str.equals("0")){
            return "Idle";
        }else if(str.equals("1")){
            return "Connect";
        }else if(str.equals("2")){
            return "Dedicated";
        }else{
        	return "";
        }
    }
    
    /**获得LTE网络工作模式*/
    public static String getLteWorkModel(String str){
    	if(str.equals("1")){
    		return "FDD";
    	}else if(str.equals("2")){
    		return "TDD";
    	}else if(str.equals("0")){
    		return "Unknow";
    	}else{
    		return "";
    	}
    }
    
    /**获得LTE网络NAS 状态*/
    public static String getLteNasState(String str){
    	if(str.equals("1")){
    		return "PS";
    	}else if(str.equals("2")){
    		return "CS";
    	}else if(str.equals("3")){
    		return "PS and CS";
    	}else if(str.equals("0")){
    		return "Unknow";
    	}else{
    		return "";
    	}
    }
    
    /**获得LTE网络Roaming State*/
    public static String getLteRoamingState(String str){
    	if(str.equals("1")){
    		return "Roaming";
    	}else if(str.equals("2")){
    		return "No Roaming";
    	}else if(str.equals("0")){
    		return "Unknow";
    	}else{
    		return "";
    	}
    }
    
    /**获得LTE 下EMM State状态*/
    public static String getLteEMMState(String str){
    	if(str.equals("1")){
    		return "DeRegistered";
    	}else if(str.equals("2")){
    		return "Registered Initiated";
    	}else if(str.equals("3")){
    		return "Registered";
    	}else if(str.equals("4")){
    		return "DeRegistered Initiated";
    	}else if(str.equals("5")){
    		return "Tracking Area Updating Initiated";
    	}else if(str.equals("6")){
    		return "Service Request Initiated";
    	}else if(str.equals("0")){
    		return "Unknow";
    	}else{
    		return "";
    	}
    }
    
    /**获得LTE 下EMM Sub State状态*/
    public static String getLteEMMSubState(String str){
    	if(str.equals("1")){
    		return "Deregistered.Normal-Service";
    	}else if(str.equals("2")){
    		return "Deregistered.Limited-Service";
    	}else if(str.equals("3")){
    		return "Deregistered.Attempting-To-Attach";
    	}else if(str.equals("4")){
    		return "Deregistered.Plmn-Search";
    	}else if(str.equals("5")){
    		return "Deregistered.No-Imsi";
    	}else if(str.equals("6")){
    		return "Deregistered.Attach-Needed";
    	}else if(str.equals("7")){
    		return "Deregistered.No-Cell-Available";
    	}else if(str.equals("8")){
    		return "Registered.Normal-Service";
    	}else if(str.equals("9")){
    		return "Registered.Attempting-To-Update";
    	}else if(str.equals("10")){
    		return "Registered.Limited-Service";
    	}else if(str.equals("11")){
    		return "Registered.Plmn-Search";
    	}else if(str.equals("12")){
    		return "Registered.Update-Needed";
    	}else if(str.equals("13")){
    		return "Registered.No-Cell-Available";
    	}else if(str.equals("14")){
    		return "Registered.Attempting-To-Update-Mm";
    	}else if(str.equals("15")){
    		return "Registered.Imsi-Detach-Initiated";
    	}else if(str.equals("0")){
    		return "Unknow";
    	}else{
    		return "";
    	}
    }
    
    /**获得LTE RRC State*/
    public static String getLteRRCState(String str){
    	if(str.equals("1")){
    		return "Connecting";
    	}else if(str.equals("2")){
    		return "Connected";
    	}else if(str.equals("0")){
    		return "Idle";
    	}else{
    		return "";
    	}
    }
    
    /**获得Lte IntraFreq Reselection*/
    public static String getLteIntraFreqReselection(String str){
    	if(str.equals("1")){
    		return "Not Allowed";
    	}else if(str.equals("2")){
    		return "Allowed";
    	}else if(str.equals("0")){
    		return "Unknown";
    	}else{
    		return "";
    	}
    }
    
    /**获得LTE下QCI状态*/
    public static String getLteQCI(String str){
    	if(str.equals("0")){
    		return "Unknown";
    	}
   		return str;
    }
    
    /**获得WCDMA Radio下 DL/UL ARM 对应表*/
    public static String getWcdmaAmrStr(String amrCodel){
    	if(amrCodel.equals("1")){
    		return "4.75k";
	    }else if(amrCodel.equals("2")){
	    	return "5.15k";
	    }else if(amrCodel.equals("3")){
	    	return "5.90k";
	    }else if(amrCodel.equals("4")){
	    	return "6.70k";
	    }else if(amrCodel.equals("5")){
	    	return "7.40k";
	    }else if(amrCodel.equals("6")){
	    	return "7.95k";
	    }else if(amrCodel.equals("7")){
	    	return "10.2k";
	    }else if(amrCodel.equals("8")){
	    	return "12.2k";
	    }else if(amrCodel.equals("9")){
	    	return "WB6.6k";
	    }else if(amrCodel.equals("10")){
	    	return "WB8.85k";
	    }else if(amrCodel.equals("11")){
	    	return "WB12.65k";
	    }else if(amrCodel.equals("12")){
	    	return "WB14.25k";
	    }else if(amrCodel.equals("13")){
	    	return "WB15.85k";
	    }else if(amrCodel.equals("14")){
	    	return "WB18.25k";
	    }else if(amrCodel.equals("15")){
	    	return "WB19.85k";
	    }else if(amrCodel.equals("16")){
	    	return "WB23.05k";
	    }else if(amrCodel.equals("17")){
	    	return "WB23.85k";
	    }else if(amrCodel.equals("18")){
	    	return "WB+13.6k";
	    }else if(amrCodel.equals("19")){
	    	return "WB+18k";
	    }else if(amrCodel.equals("20")){
	    	return "WB+24k";
	    }else{
	    		return "";
	    }
    }
    
    /**获得Lte TM*/
    public static String getLteTM(String str){
    	if(str.equals("1")){
    		return "TM1";
    	}else if(str.equals("2")){
    		return "TM2";
    	}else if(str.equals("3")){
    		return "TM3";
    	}else if(str.equals("4")){
    		return "TM4";
    	}else if(str.equals("5")){
    		return "TM5";
    	}else if(str.equals("6")){
    		return "TM6";
    	}else if(str.equals("7")){
    		return "TM7";
    	}else if(str.equals("8")){
    		return "TM8";
    	}else if(str.equals("9")){
    		return "TM9";
    	}else if(str.equals("10")){
    		return "TM10";
    	}else if(str.equals("0")){
    		return "Unknown";
    	}else{
    		return "";
    	}
    }
    
    public static String getLteCQIReportMode(String str){
    	if(str.equals("1")){
    		return "Mode 1-0";
    	}else if(str.equals("2")){
    		return "Mode 1-1";
    	}else if(str.equals("3")){
    		return "Mode 1-2";
    	}else if(str.equals("4")){
    		return "Mode 2-0";
    	}else if(str.equals("5")){
    		return "Mode 2-1";
    	}else if(str.equals("6")){
    		return "Mode 2-2";
    	}else if(str.equals("7")){
    		return "Mode 3-0";
    	}else if(str.equals("8")){
    		return "Mode 3-1";
    	}else if(str.equals("9")){
    		return "Mode 3-2";
    	}else if(str.equals("0")){
    		return "Unknown";
    	}else{
    		return "";
    	}
    }
    
    /**返回TD网络下Connected_State*/
    public static String getTDConnectedState(String str){
    	if(str.equals("0")){
    		return "Cell_DCH";
    	}else if(str.equals("1")){
    		return "Cell_FACH";
    	}else if(str.equals("2")){
    		return "Cell_PCH";
    	}else if(str.equals("3")){
    		return "URA_PCH";
    	}else if(str.equals("4")){
    		return "Idle";
    	}else{
    		return "";
    	}
    }
    
    /**返回TD网络下Main State状态*/
    public static String getTDMainState(String str){
    	if(str.equals("0")){
    		return "inactive";
    	}else if(str.equals("1")){
    		return "Idle";
    	}else if(str.equals("2")){
    		return "Connect";
    	}else if(str.equals("3")){
    		return "Dedicate";
    	}else if(str.equals("4")){
    		return "Release";
    	}else{
    		return "";
    	}
    }
    
    /**返回WCDMA网络下Service Type*/
    public static String getWcdmaServiceType(String str){
    	if(str.equals("0")){
    		return "NO_SERVICE";
    	}else if(str.equals("1")){
    		return "CS_ONLY";
    	}else if(str.equals("2")){
    		return "PS_ONLY";
    	}else if(str.equals("3")){
    		return "CS_PS_SERVICE";
    	}else if(str.equals("4")){
    		return "LIMITED_SERVICE";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVDO下SessionState*/
    public static String getEvdoSessionState(String str){
    	if(str.equals("0")){
    		return "Closed";
    	}else if(str.equals("1")){
    		return "AMP Setup";
    	}else if(str.equals("2")){
    		return "AT - Initiated Negotiation";
    	}else if(str.equals("3")){
    		return "AN - Initiated Negotiation";
    	}else if(str.equals("4")){
    		return "Open";
    	}else{
    		return "";
    	}
    }
    
    /**获得EVOD下AT State*/
    public static String getEvdoATState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Acquisition";
    	}else if(str.equals("2")){
    		return "Sync";
    	}else if(str.equals("3")){
    		return "Idle";
    	}else if(str.equals("4")){
    		return "Access";
    	}else if(str.equals("5")){
    		return "Connected";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD下ALMP State*/
    public static String getEvdoALMPState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Initialization";
    	}else if(str.equals("2")){
    		return "Idle";
    	}else if(str.equals("3")){
    		return "Connected";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD 下Init State*/
    public static String getEvdoInitState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Network Determination";
    	}else if(str.equals("2")){
    		return "Pilot Acquisition	";
    	}else if(str.equals("3")){
    		return "Sync";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD下Idle State*/
    public static String getEvdoIdleState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Monitor";
    	}else if(str.equals("2")){
    		return "Sleep";
    	}else if(str.equals("3")){
    		return "Connection Setup";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD下Over head State*/
    public static String getEvdoOverheadState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Process All Messages";
    	}else if(str.equals("2")){
    		return "Sleep";
    	}else if(str.equals("3")){
    		return "Frequency Change In Progress";
    	}else if(str.equals("4")){
    		return "Access Handoff In Progress";
    	}else if(str.equals("5")){
    		return "Wait For Link";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD下Connected State*/
    public static String getEvdoConnectedState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Open";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD下Route Update State*/
    public static String getEvdoRouteUpdateState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Idle";
    	}else if(str.equals("2")){
    		return "Connection Setup";
    	}else if(str.equals("3")){
    		return "Connected";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVOD下HDRHybrid Mode State*/
    public static String getEvdoHDRHybridModeState(String str){
    	if(str.equals("0")){
    		return "Off";
    	}else if(str.equals("1")){
    		return "On";
    	}else{
    		return "";
    	}
    }
    
    /**返回EVDO下Session Release*/
    public static String getEvdoSessionCloseCause(String str){
    	if(str.equals("0")){
    		return "Normal Close";
    	}else if(str.equals("1")){
    		return "Close Reply";
    	}else if(str.equals("2")){
    		return "Protocol Error";
    	}else if(str.equals("3")){
    		return "Protocol Configuration Failure";
    	}else if(str.equals("4")){
    		return "Protocol Negotiation Error";
    	}else if(str.equals("5")){
    		return "Session Configuration Failure";
    	}else if(str.equals("6")){
    		return "Session Lost";
    	}else if(str.equals("7")){
    		return "Session Unreachable";
    	}else if(str.equals("8")){
    		return "All session resources busy";
    	}else{
    		return "";
    	}
    }

    /**返回EVOD下Connection Release*/
	public static String getEvodConnectionCloseCause(String str) {
		if (str.equals("0")) {
			return "AN Connection Close";
		} else if (str.equals("1")) {
			return "AT Connection Colse";
		} else if (str.equals("2")) {
			return "System Lost";
		} else if (str.equals("3")) {
			return "Not Preferred";
		} else if (str.equals("4")) {
			return "Redirect";
		} else if (str.equals("5")) {
			return "Power Down Received";
		} else if (str.equals("6")) {
			return "Offline Received";
		} else if (str.equals("7")) {
			return "NAM Change Received";
		} else if (str.equals("8")) {
			return "Page Message Received";
		} else if (str.equals("9")) {
			return "Unspecified";
		} else {
			return "";
		}
	}
    
	/**返回EVDO 下EVVersion*/
	public static String getEvdoVersion(String str){
		if (str.equals("0")) {
			return "EVDO Rev.0";
		} else if (str.equals("1")) {
			return "EVDO Rev.A";
		} else if (str.equals("2")) {
			return "EVDO Rev.B";
		} else{
			return "";
		}
	}
	
	/**返回EVDO 下Band参数*/
	public static String getEvdoBand(String str){
		if (str.equals("0")) {
			return "800 MHz Cellular";
		}  else if (str.equals("1")) {
			return "1.8-2.0 GHz PCS";
		}  else if (str.equals("2")) {
			return "872-960 MHz TACS";
		}  else if (str.equals("3")) {
			return "832-925 MHz JTACS";
		}  else if (str.equals("4")) {
			return "1.75-1.87 GHz Korean PCS";
		}  else if (str.equals("5")) {
			return "450 MHz";
		}  else if (str.equals("6")) {
			return "2 GHz";
		}  else if (str.equals("7")) {
			return "700 MHz";
		}  else if (str.equals("8")) {
			return "1800 MHz";
		}  else if (str.equals("9")) {
			return "900 MHz";
		}  else if (str.equals("10")) {
			return "Secondary 800 MHz";
		}  else if (str.equals("11")) {
			return "400 MHz European PAMR";
		}  else if (str.equals("12")) {
			return "800 MHz European PAMR";
		}  else {
			return "";
		}
	}
	
	/**获得EVDO Set Type*/
	public static String getEvdoSetType(String str){
    	if(str.equals("0")){		//ActiveSet
    		return "A";
    	}else if(str.equals("1")){	//MonitorSet
    		return "M";
    	}else if(str.equals("2")){	//NeighborSet
    		return "N";
    	}else if(str.equals("3")){	//DetectedSet
    		return "D";
    	}else{
    		return "A";				//ActiveSet
    	}
	}
	
    /**返回WCDMA网络下GMM State*/
    public static String getWcdmaGMMState(String str){
    	if(str.equals("0")){
    		return "GMM_NULL";
    	}else if(str.equals("1")){
    		return "GMM_DEREGISTERED";
    	}else if(str.equals("2")){
    		return "GMM_REGISTERED_INITIATED";
    	}else if(str.equals("3")){
    		return "GMM_REGISTERED";
    	}else if(str.equals("4")){
    		return "GMM_DEREGISTERED_INITIATED";
    	}else if(str.equals("5")){
    		return "GMM_ROUTING_AREA_UPDATING_INITIATED";
    	}else if(str.equals("6")){
    		return "GMM_SERVICE_REQUEST_INITIATED";
    	}else{
    		return "Unknown";
    	}
    }
    
    /**返回WCDMA网络下Service Type*/
    public static String getWcdmaGMMUpdate(String str){
    	if(str.equals("0")){
    		return "GMM_GU1_UPDATED";
    	}else if(str.equals("1")){
    		return "GMM_GU2_NOT_UPDATED";
    	}else if(str.equals("2")){
    		return "GMM_GU3_PLMN_NOT_ALLOWED";
    	}else if(str.equals("3")){
    		return "GMM_GU3_ROUTING_AREA_NOT_ALLOWED";
    	}else{
    		return "Unknown";
    	}
    }
    
    /**返回WCDMA网络下 DL AMR Codec*/
    public static String getWcdmaDLAMRCodec(String str){
    	if(str.equals("1")){
    		return "4.75K";
    	}else if(str.equals("2")){
    		return "5.15K";
    	}else if(str.equals("3")){
    		return "5.90K";
    	}else if(str.equals("4")){
    		return "6.70K";
    	}else if(str.equals("5")){
    		return "7.40K";
    	}else if(str.equals("6")){
    		return "7.95K";
    	}else if(str.equals("7")){
    		return "1.02K";
    	}else if(str.equals("8")){
    		return "1.22K";
    	}else{
    		return "";
    	}
    }
    
    /**返回WCDMA网络下 UL AMR Codec*/
    public static String getWcdmaULAMRCodec(String str){
    	if(str.equals("1")){
    		return "4.75K";
    	}else if(str.equals("2")){
    		return "5.15K";
    	}else if(str.equals("3")){
    		return "5.90K";
    	}else if(str.equals("4")){
    		return "6.70K";
    	}else if(str.equals("5")){
    		return "7.40K";
    	}else if(str.equals("6")){
    		return "7.95K";
    	}else if(str.equals("7")){
    		return "1.02K";
    	}else if(str.equals("8")){
    		return "1.22K";
    	}else{
    		return "";
    	}
    }
    
    /**返回WCDMA网络下 RRC State*/
    public static String getWcdmaRRCState(String str){
    	if(str.equals("0")){
    		return "Disconnected";
    	}else if(str.equals("1")){
    		return "Connecting";
    	}else if(str.equals("2")){
    		return "CELL_FACH";
    	}else if(str.equals("3")){
    		return "CELL_DCH";
    	}else if(str.equals("4")){
    		return "CELL_PCH";
    	}else{
    		return "";
    	}
    }
    
    /**返回WCDMA网络下AICH Status*/
    public static String getWcdmaAICHStatus(String str){
    	if(str.equals("0")){
    		return "No ACK";
    	}else if(str.equals("1")){
    		return "Positive ACK";
    	}else if(str.equals("2")){
    		return "Negative ACK";
    	}else{
    		return "";
    	}
    }
    
    /**返回 WCDMA网络下邻区列表的Set 类型*/
    public static String getWcdmaSetType(String str){
    	if(str.equals("0")){		//ActiveSet
    		return "A";
    	}else if(str.equals("1")){	//MonitorSet
    		return "M";
    	}else if(str.equals("2")){	//NeighborSet
    		return "N";
    	}else if(str.equals("3")){	//DetectedSet
    		return "D";
    	}else if(str.equals("4")){	//VirtualActiveSet
    		return "V";
    	}else{
    		return "A";				//ActiveSet
    	}
	}
    
    /**返回EDGE网络下MAC Model*/
    public static String getEdgeMACModel(String str){
    	if(str.equals("0")){
    		return "DynamicAllocation";
    	}else if(str.equals("1")){
    		return "ExtendedDynami Allocation";
    	}else if(str.equals("2")){
    		return "Fixed Allocation, not half duplex";
    	}else if(str.equals("3")){
    		return "Fixed Allocation, half duplex mode";
    	}else{
    		return "";
    	}
    }
    
    /**返回EDGE网络下GMM State*/
    public static String EdgeGMMState(String str){
    	if(str.equals("0")){
    		return "GMM NULL";
    	}else if(str.equals("1")){
    		return "GMM DEREGISTERED";
    	}else if(str.equals("2")){
    		return "GMM REGISTERED INITIATED";
    	}else if(str.equals("3")){
    		return "GMM REGISTERED";
    	}else if(str.equals("4")){
    		return "GMM DEREGISTERED INITIATED";
    	}else if(str.equals("5")){
    		return "GMM ROUTING AREA UPDATING INITIATED";
    	}else if(str.equals("6")){
    		return "GMM SERVICE REQUEST INITIATED";
    	}else{
    		return "";
    	}
    }
    
    /***
     * 获得当前网络为GSM的网络状态
     * 0x7F1D2002 GSM网络状态:GSMIdle = 0x00，GSMSDCCH = 0x01，GSMTCH = 0x02，Unknown = 0xFF
     * @param netState
     * @return
     */
    public static String getGsmNetState(int netState){
    	switch(netState){
    	case 0x00:
    		return "Idle";
    	case 0x01:
    		return "SDCCH";
    	case 0x02:
    		return "TCH";
    		default :
    			return "Unknown";
    	}
    }
    
    /**
     * 获得除GSM外其它网络类型的当前网状态，WCDMA，TDSCDMA，CDMA，EVDO，LTE
     * 0x7F1D2003 td网络状态:TDSCDMAIdle = 0x00，TDSCDMAConnect = 0x01，TDSCDMADedicated = 0x02,Unknown = 0xFF
     * 0x7F1D2004 WCDMA网络状态:WCDMAIdle = 0x00，WCDMAConnect = 0x01，WCDMADedicated = 0x02,Unknown = 0xFF
     * 0x7F1D2005 CDMAIdle = 0x00，CDMAConnect = 0x01，CDMADedicated = 0x02,Unknown = 0xFF
     * 0x7F1D2006 EVDOIdle = 0x00，EVDOConnect = 0x01，EVDODedicated = 0x02,Unknown = 0xFF
     * 0x7F1D2007 Lte网络状态:LTEIdle = 0x00，LTEConnect = 0x01，LTEDedicated = 0x02,Unknown = 0xFF
     * @param netState
     * @return
     */
    public static String getNetState(int netState){
    	switch(netState){
    	case 0x00:
    		return "Idle";
    	case 0x01:
    		return "Connect";
    	case 0x02:
    		return "Dedicated";
    		default :
    			return "Unknown";
    	}
    }
    
    /**返回TD网络下Main State*/
    public static String TDScdmaMainState(String str){
    	if(str.equals("0")){
    		return "Inactive";
    	}else if(str.equals("1")){
    		return "Idle";
    	}else if(str.equals("2")){
    		return "Connect";
    	}else if(str.equals("3")){
    		return "Dedicate";
    	}else if(str.equals("4")){
    		return "Release";
    	}else{
    		return "";
    	}
    }
    
    
    /**返回TD网络下Connect State*/
    public static String TDScdmaConnectState(String str){
    	if(str.equals("0")){
    		return "Cell_DCH";
    	}else if(str.equals("1")){
    		return "Cell_FACH";
    	}else if(str.equals("2")){
    		return "Cell_PCH";
    	}else if(str.equals("3")){
    		return "URA_PCH";
    	}else if(str.equals("4")){
    		return "Idle";
    	}else{
    		return "";
    	}
    }
    
    /**返回TD网络下Attach State*/
    public static String TDScdmaAttachState(String str){
    	if(str.equals("0")){
    		return "Disable";
    	}else if(str.equals("1")){
    		return "Enable";
    	}else{
    		return "";
    	}
    }
    
    
    /**返回TD网络下Cell State*/
    public static String TDScdmaCellState(String str){
    	if(str.equals("0")){
    		return "Barred";
    	}else if(str.equals("1")){
    		return "Not barred";
    	}else{
    		return "";
    	}
    }
    
    /**返回Gsm网络下Channel type*/
    public static String getGSMChannelType(String str){
    	if(str.equals("0")){
    		return "FR";
    	}else if(str.equals("1")){
    		return "HR";
    	}else if(str.equals("2")){
    		return "SDCCH/4";
    	}else if(str.equals("3")){
    		return "SDCCH/8";
    	}else{
    		return "";
    	}
    }
    
    /**返回Gsm网络下Channelmode*/
    public static String getGSMChannelMode(String str){
    	if(str.equals("0")){
    		return "SingnalOnly";
    	}else if(str.equals("1")){
    		return "SpeechV1";
    	}else if(str.equals("33")){
    		return "SpeechV2";
    	}else if(str.equals("65")){
    		return "SpeechV3";
    	}else{
    		return "";
    	}
    }
  
    /***
     * 获得当前网络为GSM的网络状态
     * 0x7F1D2002 GSM网络状态:GSMIdle = 0x00，GSMSDCCH = 0x01，GSMTCH = 0x02，Unknown = 0xFF
     * @param netState
     * @return
     */
    public static String getGsmServiceState(String str){
    	
    	if(str.equals("0")){
    		return "Idle";
    	}else if(str.equals("1")){
    		return "SDCCH";
    	}else if(str.equals("2")){
    		return "TCH";
    	}else{
    		return "Unknown";
    	}
    }
    
    /**Rlc Mode**/
    public static String getRlcMode(String str){
    	
    	if(str.equals("0")){
    		return "ACK";
    	}else if(str.equals("1")){
    		return "UnACK";
    	}else{
    		return "";
    	}
    }
    
    /**GRR State**/
    public static String getGSMGRRState(String str){
    	
    	if(str.equals("0")){
    		return "Null";
    	}else if(str.equals("1")){
    		return "Acquire";
    	}else if(str.equals("2")){
    		return "Camped";
    	}else if(str.equals("3")){
    		return "Connection Pending";
    	}else if(str.equals("4")){
    		return "CellReselection";
    	}
    	else{
    		return "";
    	}
    }
    
    
    /**GPRS Support**/
    public static String getGSMGPRSSupport(String str){
    	
    	if(str.equals("1") || str.equals("2")){
    		return "Support";
    	}
   		return "Not Support";
    }
    
    /**EGPRS Support**/
    public static String getGSMEGPRSSupport(String str){
    	
    	if(str.equals("2")){
    		return "Support";
    	}
   		return "Not Support";
    }
    
    /**GMM State**/
    public static String getGSMGMMState(String str){
    	
    	if(str.equals("0")){
    		return "NULL";
    	}
    	if(str.equals("1")){
    		return "DEREGISTERED";
    	}
    	if(str.equals("2")){
    		return "REGISTERED INITIATED";
    	}
    	if(str.equals("3")){
    		return "REGISTERED";
    	}
    	if(str.equals("4")){
    		return "DEREGISTERED INITIATED";
    	}
    	if(str.equals("5")){
    		return "ROUTING AREA UPDATING INITIATED";
    	}
    	if(str.equals("6")){
    		return "SERVICE REQUEST INITIATED";
    	}
   		return "";
    }
    
    /**NMO**/
    public static String getNMO(String str){
    	
    	if(str.equals("0")){
    		return "Mode I";
    	}
    	if(str.equals("1")){
    		return "Mode II";
    	}
    	if(str.equals("2")){
    		return "Mode III";
    	}
   		return "";
    }
    
    /**
     * 十进制转换IP地址
     * @param ip
     * @return
     */
    public static String ToIP(String ipc) {
    	String s = "0";
    	try {
			long ip = Long.parseLong(ipc);
			int a = (int) (ip / 16777216);
			int b = (int) ((ip % 16777216) / 65536);
			int c = (int) (((ip % 16777216) % 65536) / 256);
			int d = (int) (((ip % 16777216) % 65536) % 256);
			s = String.valueOf(a) + "." + String.valueOf(b) + "." + String.valueOf(c) + "."
					+ String.valueOf(d);
		} catch (Exception e) {
			return s;
		}
    	
		return s;
	}
    
    
    /**SIP Codec Type**/
    public static String getVoLteSIPCode(String str){
    	
    	if(str.equals("0")){
    		return "AMR NB";
    	}else if(str.equals("1")){
    		return "AMR WB";
    	}else if(str.equals("2")){
    		return "Mixed";
    	}else{
    		return "";
    	}
    }
    
    
    
    /**处理Connect Previous State**/
    public static String getPreviousState(String str){

			try {
				if (str.equals("0")) {
					return "4";
				}
				if (!str.equals("")) {
					return "" + (Integer.valueOf(str) - 1);
				}
				return "";
			} catch (Exception e) {
			}
    	
    	return "";
    }

    public static String getCCDisconnectCause(int cause){
    	String result="";
    	switch(cause){
    	case 1:
    		result = " Unassigned number";
    		break;
    	case 3:
    		result = " No route to destination";
    		break;
    	case 6:
    		result = " Channel unacceptable";
    		break;
    	case 8:
    		result = " Operator determined barring";
    		break;
    	case 16:
    		result = " Normal call clearing";
    		break;
    	case 17:
    		result = " User busy";
    		break;
    	case 18:
    		result = " No user responding";
    		break;
    	case 19:
    		result = " User alerting, no answer";
    		break;
    	case 21:
    		result = " Call rejected";
    		break;
    	case 22:
    		result = " Number changed";
    		break;
    	case 25:
    		result = " Pre-emption";
    		break;
    	case 26:
    		result = " Non selected user clearing";
    		break;
    	case 27:
    		result = " Destination out of order";
    		break;
    	case 28:
    		result = " Invalid number format";
    		break;
    	case 29:
    		result = " Facility rejected";
    		break;
    	case 30:
    		result = " Response to STATUS ENQUIRY";
    		break;
    	case 0:
    	case 2:
    	case 4:
    	case 5:
    	case 7:
    	case 9:
    	case 10:
    	case 11:
    	case 12:
    	case 13:
    	case 14:
    	case 15:
    	case 20:
    	case 23:
    	case 24:
    	case 31:
    		result = "Normal, unspecified";
    		break;
    	case 34:
    		result = " No circuit/channel available";
    		break;
    	case 38:
    		result = " Network out of order";
    		break;
    	case 41:
    		result = " Temporary failure";
    		break;
    	case 42:
    		result = " Switching equipment congestion";
    		break;
    	case 43:
    		result = " Access information discarded";
    		break;
    	case 44:
    		result = " requested circuit/channel not available";
    		break;
    	case 32:
    	case 33:
    	case 35:
    	case 36:
    	case 37:
    	case 39:
    	case 40:
    	case 45:
    	case 46:
    	case 47:
    		result = "Resources unavailable, unspecified";
    		break;
    	case 49:
    		result = " Quality of service unavailable";
    		break;
    	case 50:
    		result = " Requested facility not subscribed";
    		break;
    	case 55:
    		result = " Incoming calls barred within the CUG";
    		break;
    	case 57:
    		result = " Bearer capability not authorized";
    		break;
    	case 58:
    		result = " Bearer capability not presently available";
    		break;
    	case 48:
    	case 51:
    	case 52:
    	case 53:
    	case 54:
    	case 56:
    	case 59:
    	case 60:
    	case 61:
    	case 62:
    	case 63:
    		result = "Service or option not available, unspecified";
    		break;
    	case 65:
    		result = " Bearer service not implemented";
    		break;
    	case 68:
    		result = " ACM equal to or greater than ACMmax";
    		break;
    	case 69:
    		result = " Requested facility not implemented";
    		break;
    	case 70:
    		result = "Only restricted digital information bearer capability is available";
    		break;
    	case 64:
    	case 66:
    	case 67:
    	case 71:
    	case 72:
    	case 73:
    	case 74:
    	case 75:
    	case 76:
    	case 77:
    	case 78:
    	case 79:
    		result = "Service or option not implemented, unspecified";
    		break;
    	case 81:
    		result = " Invalid transaction identifier value";
    		break;
    	case 87:
    		result = " User not member of CUG";
    		break;
    	case 88:
    		result = " Incompatible destination";
    		break;
    	case 91:
    		result = " Invalid transit network selection";
    		break;
    	case 80:
    	case 82:
    	case 83:
    	case 84:
    	case 85:
    	case 86:
    	case 89:
    	case 90:
    	case 92:
    	case 93:
    	case 94:
    	case 95:
    		result = "Semantically incorrect message";
    		break;
    	case 96:
    		result = " Invalid mandatory information";
    		break;
    	case 97:
    		result = " Message type non-existent or not implemented";
    		break;
    	case 98:
    		result = " Message type not compatible with protocol state";
    		break;
    	case 99:
    		result = " Information element non-existent or not implemented";
    		break;
    	case 100:
    		result = "Conditional IE error";
    		break;
    	case 101:
    		result = "Message not compatible with protocol state";
    		break;
    	case 102:
    		result = "Recovery on timer expiry";
    		break;
    	case 103:
    	case 104:
    	case 105:
    	case 106:
    	case 107:
    	case 108:
    	case 109:
    	case 110:
    	case 111:
    		result = "Protocol error, unspecified";
    		break;
    	case 112:
    	case 113:
    	case 114:
    	case 115:
    	case 116:
    	case 117:
    	case 118:
    	case 119:
    	case 120:
    	case 121:
    	case 122:
    	case 123:
    	case 124:
    	case 125:
    	case 126:
    	case 127:
    		result = "Interworking, unspecified";
    		break;
    	}
    	
    	return result;
    }
    
    
    /**
     * 根据标识获取网络类型
     * @param type
     * @return
     */
    public static String netWorkCaType(String type){
    	int typeInt = 0;
    	String netWorkStr = "";
    	if(type.length()!=0) {
			try {
				typeInt = Integer.valueOf(type);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else
    		return netWorkStr;
    	
    	switch (typeInt) {
		case 0:
			netWorkStr = "Unknown";
			break;
		case 1:
			netWorkStr = "GSM";
			break;	
		case 2:
			netWorkStr = "GPRS";
			break;	
		case 3:
			netWorkStr = "EDGE";
			break;	
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 15:
		case 16:
		case 17:
		case 18:	
			netWorkStr = "WCDMA";
			break;	
		case 9:
			netWorkStr = "LTE";
			break;	
		case 10:
			netWorkStr = "LTE-CA";
			break;	
		case 11:
			netWorkStr = "CDMA 1X";
			break;	
		case 12:
			netWorkStr = "CDMA EVDO";
			break;	
		case 13:
			netWorkStr = "CDMA EVDV";
			break;	
		case 14:
			netWorkStr = "UMB";
			break;	
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
			netWorkStr = "TD-SCDMA";
			break;
			case 41:
				netWorkStr = "NB-Iot";
				break;
			default:
			break;
		}
    	return netWorkStr;
    }
    
	/**
     	* Scanner根据网络类型判断网络值
     	* 1---GSM
		2---CDMA
		3---EVDO
		4---WCDMA
		5---TDSCDMA
		6---FDD_LTE
		7---TDD_LTE
     */
	public static String getNetWorkStr(int netWork) {
		String netWorkStr = "";
		switch (netWork) {
		case 1:
			netWorkStr = "GSM";
			break;
		case 2:
			netWorkStr = "CDMA";
			break;
		case 3:
			netWorkStr = "EVDO";
			break;
		case 4:
			netWorkStr = "WCDMA";
			break;
		case 5:
			netWorkStr = "TDSCDMA";
			break;
		case 6:
			netWorkStr = "FDD_LTE";
			break;
		case 7:
			netWorkStr = "TDD_LTE";
			break;
		default:
			netWorkStr = "IDLE";
			break;
		}
		return netWorkStr;
	}
	
	
	private static String INVALID = "FFFFFFFF";					//无效值
	

	/**
	 * 根据类型值，取枚举值,特殊结构
	 * @param keyId
	 * @param mContext
	 * @return
	 */
	public static String byValue2Enum(String keyId){
		return byValue2Enum(ParameterSetting.getInstance().getParameterById(keyId.toUpperCase()),null);
	}
	
	/**
	 * 根据类型值，取枚举值
	 */
	public static String byValue2Enum(Parameter p,Context mContext){
		String value = "";
		try {
			if(p == null){
				return value;
			}
			
			if(p.getId().equals(INVALID)){
				return "-";
			}
			value = changeScaleValue(p.getId().trim(),p.getScale(),p.getDecimal());
			switch (p.getEspecialType()) {
			case Especial.TYPE_TWO:
				if (p.getEspecial() != null && value.length() != 0) {
					for (int i = 0; i < p.getEspecial().getEspecialEnums().length; i++) {
						if ((p.getEspecial().getEspecialEnums()[i]).getValue() == Integer.valueOf(value)) {
							value = (p.getEspecial().getEspecialEnums()[i]).getDetail();
							break;
						}
					}
				}
				break;

			case Especial.TYPE_FOUR:
			case Especial.TYPE_THREE:
					value = switchValue(p,Integer.valueOf(p.getId(), 16),mContext);
				break;
			case Especial.TYPE_FIVE:
					if(!value.equals("")){
						value = "0x" + Long.toHexString(Long.valueOf(value));
					}
				break;
			case Especial.TYPE_SEVEN:
				float numerator 		= 0;
				float denominator 	= 0;
				
				for(EspecialRow row : p.getEspecial().getTableRows()){
					for(String key : row.getKeys()){
						String keyValue = changeScaleValue(key,row.getScale(),row.getDecimal());
						if(keyValue!= null && !keyValue.equals("") && UtilsMethod.isNumeric(keyValue)){
							if(row.getName().equals("numerator")){
								numerator += Float.parseFloat(keyValue);
							}else{
								denominator += Float.parseFloat(keyValue);
							}
						}
					}
				}
				value = UtilsMethod.getIntMultiple(numerator, denominator);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	/**
	 * 当前参数值是否需要做转义
	 * @param value
	 * @param enumSet
	 * @return
	 */
	public static String byValue2Enum(String value,boolean hasEnum,SparseArray<String> enumSet){
		try{
			if(hasEnum && enumSet != null && enumSet.size() > 0 && !value.equals("")){
				if(enumSet.get(Integer.parseInt(value)) != null){
					value = enumSet.get(Integer.parseInt(value));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return value;
	}
	
	/**
	 * 特殊值处理
	 * @param id
	 * @return
	 */
	private static String switchValue(Parameter p ,int id,Context mContext){
		String value = "";
		try {
			switch (id) {
			case 0x7F020017:
				value = UtilsMethod.getLongCellIdToRNCId(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_RNC_ID))
	                    + "/" + UtilsMethod.getLongTosShortCellID(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Cell_ID));
				break;
			case 0x7F06002A:
				value = 
				TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP1) + "(" +
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP2) + "-" +
						TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_ECIP3) + ")";
				break;
			case 0x7F060E0D:
				LTEEPSBearerContext02C1 pdnAddress = ((LTEEPSBearerContext02C1)TraceInfoInterface.getParaStruct(UnifyParaID.LTE_EPS_BearerContext_02C1));
				value = pdnAddress != null ? pdnAddress.pndAddress : "";
				break;
			case 0x7F060E14:
				LTEAPN lteapn = ((LTEAPN)TraceInfoInterface.getParaStruct(UnifyParaID.LTE_APN));
				value = lteapn != null ? lteapn.lteApn : "";
				break;	
			case 0x7F060D16:
				value = (mContext == null || MyPhoneState.getInstance().getIMSI(mContext) == null ? "" : MyPhoneState.getInstance().getIMSI(mContext));
				break;
			case 0x7F1D7101:
				value = gsmStructValue(p);
				break;	
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			value = "";
		}
		return value;
	}

	/**
	 * 获取GSM结构体取值
	 * @return 单个参数取值
	 */
	private static String gsmStructValue(Parameter p){
		String value = "";
		GsmStructModel gsmStructModel = TraceInfoInterface.getGsmStructModel();
		if (gsmStructModel != null){
			if (p.getStructure().trim().equalsIgnoreCase("Delay_Class")) {
				value = gsmStructModel.getDelay_Class() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("Reliability_Class")) {
				value = gsmStructModel.getReliability_Class() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("Peak_Throughput")) {
				value = gsmStructModel.getPeak_Throughput() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("Precedence_Class")) {
				value = gsmStructModel.getPrecedence_Class() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("Mean_Throughput")) {
				value = gsmStructModel.getMean_Throughput() + "";
			}else if (p.getStructure().trim().equalsIgnoreCase("Radio_Priority")) {
				value = gsmStructModel.getRadio_Priority() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("IP")) {
				value = intToIp(gsmStructModel.getiP()) + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("LLC_SAPI")) {
				value = gsmStructModel.getlLC_SAPI() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("UL_Max_bit_Rate")) {
				value = gsmStructModel.getuL_Max_bit_Rate() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("DL_Max_bit_Rate")) {
				value = gsmStructModel.getdL_Max_bit_Rate() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("UL_Guarante_bit_Rate")) {
				value = gsmStructModel.getuL_Guarante_bit_Rate() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("DL_Guarante_bit_Rate")) {
				value = gsmStructModel.getdL_Guarante_bit_Rate() + "";
			} else if (p.getStructure().trim().equalsIgnoreCase("Max_SDU_size")) {
				value = gsmStructModel.getMax_SDU_size() + "";
			}
		}
		return value;
		}
		
	/**
	 * int类型ip转换
	 * @param i
	 * @return
	 */
	public static String intToIp(int i) {  
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."  
                + ((i >> 8) & 0xFF) + "." + (i & 0xFF);  
    } 
	
	
	/**
	 * 计算缩放比例
	 */
	public static String changeScaleValue(String id,int scale,int decimal){
		String value = "";
		if(id.equals(INVALID)){
			return "-";
		}
		try {
			value = TraceInfoInterface.getParaValue(Integer.valueOf(id.trim(),16)) + "";
			if(scale > 1 ){
				value = UtilsMethod.narrowMultiple(value, scale);
			}
			value = UtilsMethod.decimalMath(value, decimal);
		} catch (Exception e) {
			e.printStackTrace();
			value =  "-";
		}
		return value;
	}
	
	
	
	/**
	 * 将上下行时间隙值转换成char[8]数组返回
	 * @param timeSlot
	 * @return
	 */
	public static char[] getTSBinaryChar(String timeSlot){
		char[] ts = new char[8];
		try{
			//当timeSlot不为空时才需进行处理
			if(!timeSlot.equals("")){
				int tsi = Integer.parseInt(timeSlot);
				//不在时隙值允许范围内
				if(tsi <0 || tsi > 255){
					return ts;
				}
				String tss = Integer.toBinaryString(tsi);
				char[] ts2 = tss.toCharArray();
				int num = 0;
				for(int i=ts2.length - 1; i>=0; i--){
					ts[7-num] = ts2[i];
					num ++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ts;
	}
	
	/**
	 * 返回传入Map对象中指定键值的字符串内容
	 * @param values
	 * @param key
	 * @return
	 */
	public static String getValueOnHashMap(Map<String, Object> values, String key) {
		if (values.containsKey(key)) {
			if (values.get(key) == null)
				return "";
			String value = values.get(key).toString();
			return value.startsWith("-9999") ? "" : value;
		}
		return "";
	}
}

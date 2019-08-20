package com.dinglicom.dataset.logic;

import android.content.Context;
import android.content.Intent;

import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.model.NetStateModel;
import com.walktour.service.phoneinfo.utils.MobileUtil;

import java.util.ArrayList;

import static com.walktour.Utils.WalkStruct.CurrentNetState.CatM;
import static com.walktour.Utils.WalkStruct.CurrentNetState.GSM;
import static com.walktour.Utils.WalkStruct.CurrentNetState.NBIoT;
import static com.walktour.Utils.WalkStruct.CurrentNetState.TDSCDMA;
import static com.walktour.Utils.WalkStruct.CurrentNetState.WCDMA;

/**
 * 监控网络切换操作
 * @author Tangwq
 *
 */
public class MonitorNetSwitch {

	private static final String TAG = "MonitorNetSwitch";
	private Context mContext = null;
	private int fddCounts;			//fdd统计次数
	private int tddCounts;			//tdd统计次数
	private int caCounts;			//ca统计次数
	private int caCounts3D;		//下行3载波
	private int caCounts2U;		//上行2载波
	private int fdd800mCounts;		//LTE FDD 800M计数
	private ArrayList<WalkStruct.ShowInfoType> netlist=null;
	public MonitorNetSwitch(Context context){
		this.mContext = context;
		netlist=ApplicationModel.getInstance().getNetList();
	}

	/**
	 * 监控网络信息
	 *
	 * FDD,TDD监控,参数ID:
	 * UnifyParaId.L_SRV_Work_Mode = 0x7F06001B; 1:FDD,2:TDD,0:Unknow
	 * 当值为1且没有FDD权限,或值为2且没有TDD权限连续30S则中断当前测试,写入TAG事件表示当然未有相应权限
	 *
	 * LTECA监控参数: UnifyParaId.LTECA_Capacity_Packet_Capability = 0x7F1D2008;
	 * 当前值为10且连续10S,则认为当前占上CA网络.如无权限则中断当前测试,写TAG事件
	 *
	 * VoLTE监控参数:UnifyParaId.VOLTE_Invite_Request_Cause = 0x7F1D401F;1:audio;2:video + audio
	 * 如果获得当前值为1表示当前进入VOLTE语音网络,如果无权限中断当前测试写TAG事件
	 * 如果获得当前值为2表示当前进入VOLTE视频网络,如果无权限中断当前测试写TAG事件
	 *
	 * TAG信息
	 * 无FDD LTE权限= No FDD LTE License
	 * 无CA LTE权限=No CA LTE License
	 * 无VoLTE语音权限=No VoLTE Voice License
	 * 无VoLTE视频权限=No VoLTE Video License
	 *
	 * 上述用到的参数ID在PageQueryParam.OtherParamQuery 数组一中定义,该数组在查询当前网络时执行
	 *
	 */
	public void MonitorNet(){
		try{
			String workModel = TraceInfoInterface.getRealParaValue(UnifyParaID.L_SRV_Work_Mode);

			LogUtil.w(TAG,"--workModel:" + workModel
					+ "--cad:" + TraceInfoInterface.getRealParaValue(UnifyParaID.LTECA_Active_SCell_Count)
					+ "--cau:" + TraceInfoInterface.getRealParaValue(UnifyParaID.LTECA_UL_Active_SCell_Count)
					+ "--cause:" + TraceInfoInterface.getRealParaValue(UnifyParaID.VOLTE_Invite_Request_Cause));

			if(!TraceInfoInterface.getRealParaValue(UnifyParaID.CURRENT_NETWORKTYPE).equals("") &&
					Double.valueOf(TraceInfoInterface.getRealParaValue(UnifyParaID.CURRENT_NETWORKTYPE)).intValue() == 0x10){
				if(workModel.equals("1")){
					fddCounts ++;
					tddCounts = 0;
				}else if(workModel.equals("2")){
					fddCounts = 0;
					tddCounts ++;
				}else{
					fddCounts = 0;
					tddCounts = 0;
				}
			}else{
				fddCounts = 0;
				tddCounts = 0;
			}

			if(TraceInfoInterface.getRealParaValue(UnifyParaID.LTECA_Active_SCell_Count).equals("1")){
				caCounts ++;
				caCounts3D = 0;
			}else if(TraceInfoInterface.getRealParaValue(UnifyParaID.LTECA_Active_SCell_Count).equals("2")){
				caCounts =0;
				caCounts3D ++;
			}else{
				caCounts = 0;
				caCounts3D = 0;
			}

			if(TraceInfoInterface.getRealParaValue(UnifyParaID.LTECA_UL_Active_SCell_Count).equals("1")){
				caCounts2U ++;
			}else{
				caCounts2U = 0;
			}

			if(TraceInfoInterface.getRealParaValue(UnifyParaID.L_SRV_Band).equals("5")
					|| TraceInfoInterface.getRealParaValue(UnifyParaID.L_SRV_Band).equals("26")){
				fdd800mCounts ++;
			}else{
				fdd800mCounts = 0;
			}

			//如果统计值超30且FDD权限
			if(fddCounts >= 30 && !netlist.contains(WalkStruct.ShowInfoType.LTEFDD)){
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_fddlte));
			}

			if(tddCounts >= 30 && !netlist.contains(WalkStruct.ShowInfoType.LTETDD)){
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_tddlte));
			}

			if(caCounts >= 10 && !netlist.contains(WalkStruct.ShowInfoType.LTECA)){
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_calte));
			}

			//当占3载波下行连续10秒,判断是TDD,FDD,看是否存在相应权限
			if(caCounts3D >= 10){
				if(fddCounts  >= 10 && !netlist.contains(WalkStruct.ShowInfoType.FDD3CaD)){
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_fdd3cad));
				}else if(tddCounts  >= 10 && !netlist.contains(WalkStruct.ShowInfoType.TDD3CaD)){
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_tdd3cad));
				}
			}
			//当占2载波上行连续10秒,判断是TDD,FDD,看是否存在相应权限
			if(caCounts2U >= 10){
				if(fddCounts  >= 10 && !netlist.contains(WalkStruct.ShowInfoType.FDD2CaU)){
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_fdd2cau));
				}else if(tddCounts  >= 10 && !netlist.contains(WalkStruct.ShowInfoType.TDD2CaU)){
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_tdd2cau));
				}
			}

			if(fdd800mCounts >= 15 && !netlist.contains(WalkStruct.ShowInfoType.FDD800M)){
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_fdd800m));
			}


			WalkStruct.CurrentNetState status = NetStateModel.getInstance().getCurrentNetTypeSync();
			if (status == NBIoT&& !netlist.contains(WalkStruct.ShowInfoType.NBIoT)) {//是NBIot网络,有没有权限则通知
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_nbiot));
			}else if(status == CatM&& !netlist.contains(WalkStruct.ShowInfoType.CatM)){//是CatM网络,有没有权限则通知
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_catm));
			}else if(status == WCDMA&& isNoWcdma()){//是WCDMA网络,有没有权限则通知
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_wcdma));
			}else if(status == TDSCDMA&& isNoTdscdma()){//是TDSCDMA网络,有没有权限则通知
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_tdscdma));
			}else if(status == GSM&& isNoGSM()){//是GSM网络,有没有权限则通知
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_gsm));
			}

			if(TraceInfoInterface.getRealParaValue(UnifyParaID.VOLTE_Invite_Request_Cause).equals("1")){
				boolean isChinaTelecom = MobileUtil.isChinaTelecom(mContext);
				boolean isChinaUnicom = MobileUtil.isChinaUnicom(mContext);
				boolean hasTelecomVoLTE = netlist.contains(WalkStruct.ShowInfoType.TelecomVoLTE);
				boolean hasUnicomVoLTE = netlist.contains(WalkStruct.ShowInfoType.UnicomVoLTE);
				boolean hasVolTE = netlist.contains(WalkStruct.ShowInfoType.VoLTE);
				LogUtil.d(TAG, "----isChinaTelecom:" + isChinaTelecom + "----isChinaUnicom:" + isChinaUnicom + "----hasTelecomVoLTE:" + hasTelecomVoLTE + "----hasUnicomVoLTE:" + hasUnicomVoLTE + "----hasVolTE:" + hasVolTE);
				if(isChinaTelecom && !hasTelecomVoLTE)
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_telecom_volte));
				else if(isChinaUnicom && !hasUnicomVoLTE)
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_unicom_volte));
				else if(!hasVolTE)
					noLicenseInterrupt(mContext.getString(R.string.main_license_no_volte));
			}else if(TraceInfoInterface.getRealParaValue(UnifyParaID.VOLTE_Invite_Request_Cause).equals("2")
					&& !netlist.contains(WalkStruct.ShowInfoType.VoLTEVideo)){
				noLicenseInterrupt(mContext.getString(R.string.main_license_no_voltevideo));
			}
		}catch(Exception e){
			LogUtil.w(TAG, "MonitorNet",e);
		}
	}

	/**
	 * 没有WCDMA权限
	 * @return
	 */
	private boolean isNoWcdma(){
		return (!netlist.contains(WalkStruct.ShowInfoType.WCDMA))&&
				(!netlist.contains(WalkStruct.ShowInfoType.Umts))&&
				(!netlist.contains(WalkStruct.ShowInfoType.Hspa))&&
				(!netlist.contains(WalkStruct.ShowInfoType.HspaPlus));
	}

	/**
	 * 没有TDSCDMA权限
	 * @return
	 */
	private boolean isNoTdscdma(){
		return (!netlist.contains(WalkStruct.ShowInfoType.TDSCDMA))&&
				(!netlist.contains(WalkStruct.ShowInfoType.TDHspaPlus));
	}

	/**
	 * 没有GSM权限
	 * @return
	 */
	private boolean isNoGSM(){
		return (!netlist.contains(WalkStruct.ShowInfoType.Edge))&&
				(!netlist.contains(WalkStruct.ShowInfoType.Gsm));
	}
	/**
	 * 无权限中断测试
	 * @param reasion	中断原因以TAG方式写入文件
	 */
	private void noLicenseInterrupt(String reasion){
		LogUtil.w(TAG, "--noLicenseInterrupt:" + reasion + ApplicationModel.getInstance().isTestInterrupt());
		if(!ApplicationModel.getInstance().isTestInterrupt()){
			EventManager.getInstance().addTagEvent(mContext,System.currentTimeMillis(),reasion);
			mContext.sendBroadcast(new Intent(WalkMessage.Action_Walktour_Test_Interrupt));
		}
	}
}

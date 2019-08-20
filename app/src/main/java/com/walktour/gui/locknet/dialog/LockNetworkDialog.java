package com.walktour.gui.locknet.dialog;

import android.app.Activity;
import android.content.DialogInterface;

import com.walktour.Utils.ApplicationModel;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

import java.util.ArrayList;
import java.util.Set;

/**
 * 锁网选择窗
 * @author qihang.li
 */
public class LockNetworkDialog{
	/** 上下文 */
	private Activity context;

	private OnDialogChangeListener callback;

    private void addNetworkListNormalNoRestart(Deviceinfo deviceInfo, ArrayList<String> list)
    {
        list.clear();
        list.add(ForceNet.NET_AUTO.getDescrition());
        list.add(ForceNet.NET_GSM.getDescrition());
        list.add(ForceNet.NET_CDMA.getDescrition());
        list.add(ForceNet.NET_EVDO.getDescrition());
        list.add(ForceNet.NET_WCDMA.getDescrition());
        list.add(ForceNet.NET_TDSCDMA.getDescrition());
        list.add(ForceNet.NET_LTE.getDescrition());
        list.add(ForceNet.NET_CDMA_EVDO.getDescrition());
    }

	private void addNetworkListNormal(Deviceinfo deviceInfo, ArrayList<String> list)
	{
		Set<String> types = deviceInfo.getNettypes();

		if( types.contains(Deviceinfo.NET_TYPES_CDMA )){
			list.add( ForceNet.NET_CDMA.getDescrition());
			if( types.contains(Deviceinfo.NET_TYPES_EVDO ) ){
				list.add(  ForceNet.NET_EVDO.getDescrition() );
				list.add(  ForceNet.NET_CDMA_EVDO.getDescrition() );
			}
		}
		if( types.contains(Deviceinfo.NET_TYPES_GSM ) ){

			//添加CDMA
			list.add(  ForceNet.NET_GSM.getDescrition() );

			//添加WCDMA
			if( types.contains(Deviceinfo.NET_TYPES_WCDMA ) ){
				list.add(  ForceNet.NET_WCDMA.getDescrition() );
				list.add(  ForceNet.NET_GSM_WCDMA.getDescrition() );
				if( types.contains(Deviceinfo.NET_TYPES_LTE ) ){
					list.add(  ForceNet.NET_WCDMA_LTE.getDescrition() );
				}
			}
			//添加TDSCDMA
			if( types.contains(Deviceinfo.NET_TYPES_TDSCDMA ) ){
				list.add(  ForceNet.NET_TDSCDMA.getDescrition() );
				list.add(  ForceNet.NET_GSM_TDSCDMA.getDescrition() );
				if( types.contains(Deviceinfo.NET_TYPES_LTE ) ){
					list.add(  ForceNet.NET_TDSCDMA_LTE.getDescrition() );
				}
			}
		}
		//添加LTE
		if( types.contains(Deviceinfo.NET_TYPES_LTE ) ){
			list.add(  ForceNet.NET_LTE.getDescrition() );
			list.add(  ForceNet.NET_FDD_LTE.getDescrition() );
			list.add(  ForceNet.NET_TDD_LTE.getDescrition() );
		}
	}

	private void addNetworkListNbIot(Deviceinfo deviceInfo, ArrayList<String> list) {
		ConfigNBModuleInfo configNBModuleInfo = ConfigNBModuleInfo.getInstance(context);
		switch (configNBModuleInfo.getNbModuleName()) {
			case Deviceinfo.DeviceName_YaoYuan_Really: {
				list.add(ForceNet.NET_NBIot_WB.getDescrition());
				list.add(ForceNet.NET_NBIot_CatM1.getDescrition());
				list.add(ForceNet.NET_NBIot_NB1.getDescrition());
				list.add(ForceNet.NET_NBIot_CatM1_NB1.getDescrition());
				list.add(ForceNet.NET_NBIot_WB_CatM1.getDescrition());
				list.add(ForceNet.NET_NBIot_WB_NB1.getDescrition());
				list.add(ForceNet.NET_NBIot_WB_CatM1_NB1.getDescrition());
			}
			break;
		}
	}

	private void addNetworkListS8CumstomRom(Deviceinfo deviceinfo, ArrayList<String> list){
		list.clear();
		list.add(ForceNet.NET_AUTO.getDescrition());
		list.add(ForceNet.NET_GSM.getDescrition());
		list.add(ForceNet.NET_CDMA.getDescrition());
		list.add(ForceNet.NET_EVDO.getDescrition());
		list.add(ForceNet.NET_WCDMA.getDescrition());
		list.add(ForceNet.NET_TDSCDMA.getDescrition());
		list.add(ForceNet.NET_LTE.getDescrition());
	}

	private void addNetworkListS9CumstomRom(Deviceinfo deviceinfo, ArrayList<String> list) {
		list.clear();
		list.add(ForceNet.NET_AUTO.getDescrition());
		list.add(ForceNet.NET_GSM.getDescrition());
		list.add(ForceNet.NET_CDMA.getDescrition());
		list.add(ForceNet.NET_EVDO.getDescrition());
		list.add(ForceNet.NET_WCDMA.getDescrition());
		list.add(ForceNet.NET_TDSCDMA.getDescrition());
		list.add(ForceNet.NET_LTE.getDescrition());
		list.add(ForceNet.NET_GSM_WCDMA.getDescrition());
		list.add(ForceNet.NET_CDMA_EVDO.getDescrition());
		list.add(ForceNet.NET_WCDMA_LTE.getDescrition());
		list.add(ForceNet.NET_TDSCDMA_LTE.getDescrition());
	}

	private void addNetworkListVivoCumstomRom(Deviceinfo deviceinfo, ArrayList<String> list) {
		list.clear();
		list.add(ForceNet.NET_AUTO.getDescrition());
		list.add(ForceNet.NET_GSM.getDescrition());
		list.add(ForceNet.NET_CDMA.getDescrition());
		list.add(ForceNet.NET_EVDO.getDescrition());
		list.add(ForceNet.NET_WCDMA.getDescrition());
		list.add(ForceNet.NET_TDSCDMA.getDescrition());
		list.add(ForceNet.NET_LTE.getDescrition());
		list.add(ForceNet.NET_GSM_WCDMA.getDescrition());
		list.add(ForceNet.NET_GSM_TDSCDMA.getDescrition());
		list.add(ForceNet.NET_CDMA_EVDO.getDescrition());
		list.add(ForceNet.NET_WCDMA_LTE.getDescrition());
		list.add(ForceNet.NET_TDSCDMA_LTE.getDescrition());
	}

	public LockNetworkDialog(Activity context,OnDialogChangeListener diagListener) {
		callback = diagListener;
		this.context = context;
	}


	public void show(){
		ArrayList<String> list = new ArrayList<>();
		list.add( ForceNet.NET_AUTO.getDescrition());

		Deviceinfo device = Deviceinfo.getInstance();
		if (ApplicationModel.getInstance().isNBTest())
			addNetworkListNbIot(device, list);
		else {
			if (device.isS8CustomRom() && (!device.isS8CustomRomV1())) {
				addNetworkListS8CumstomRom(device, list);
			} else if(device.isS9CustomRom()) {
				addNetworkListS9CumstomRom(device, list);
			} else if (device.isVivo()) {
				addNetworkListVivoCumstomRom(device, list);
			} else {
                addNetworkListNormalNoRestart(device, list);
			}
		}
		
		//单选列表
		final String [] networks = new String[list.size()];
		list.toArray(networks);
		ForceNet lockNet = ForceManager.getInstance().getLockNet(context);
		int checkItem = 0;
		for(int i=0;i<networks.length;i++){
			if( networks[i].equals(lockNet.getDescrition()) ){
				checkItem = i;
				break;
			}
		}
		
		new BasicDialog.Builder(context)
		.setTitle( R.string.lock_locknet)
		.setSingleChoiceItems(networks, checkItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ForceNet forceNet = ForceNet.getForceNet( networks[which]);
				new LockNetworkProgress(context, forceNet,callback).execute();
			}
		}).show();
	}

}

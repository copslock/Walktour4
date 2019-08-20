/**
 * com.walktour.gui.locknet.dialog
 * LockBandDialog.java
 * 类功能：
 * 2014-6-26-上午9:41:42
 * 2014鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * LockBandDialog
 * 
 * 2014-6-26 上午9:41:42
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("InflateParams")
public class LockBandDialog {
	private Activity context;
//	private SharedPreferences preference;
	private OnDialogChangeListener callback;
	public LockBandDialog(Activity context,OnDialogChangeListener diagListener) {
		callback = diagListener;
		this.context = context;
//		preference = context.getSharedPreferences( context.getPackageName(),
//				Context.MODE_PRIVATE );
	}
	
	public void show(){
		//设备配置文件
		Deviceinfo device = Deviceinfo.getInstance();

		//增加可以锁频的网络类型
		ArrayList<String> netList = new ArrayList<>();

		if (ApplicationModel.getInstance().isNBTest()){
			addLockNetworkListNbIot(device, netList);
		} else if (device.isSamsungCustomRom() || (device.isCustomS8RomRoot())) {
			addLockNetworkListS8CumstomRom(device, netList);
		} else	{
			addLockNetworkListNormal(device, netList);
		}

		final String[] networks = new String[netList.size()];
		netList.toArray(networks);

		new BasicDialog.Builder(context)
		.setTitle( R.string.lock_lockband)
		.setItems(networks, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				String network = networks[which];
				
				ArrayList<Band> bandList = new ArrayList<>();

				ForceNet netType  = addLockBadnList(network, bandList);
				
				if( null != netType){
					showBandDialog(netType,bandList);
				}
			}
		}).show();
	}

	private void addLockNetworkListNormal(Deviceinfo device, ArrayList<String> netList) {
		Set<String> types = device.getNettypes();
		Iterator<String> interator = types.iterator();

		while( interator.hasNext() ){
			String network=interator.next();
			if( network.equals(Deviceinfo.NET_TYPES_GSM)
					|| network.equals(Deviceinfo.NET_TYPES_WCDMA)
					//|| network.equals(Deviceinfo.NET_TYPES_TDSCDMA) 暂时不支持
					|| network.equals(Deviceinfo.NET_TYPES_LTE)){
				netList.add(network);
			}
		}
	}

	private void addLockNetworkListNbIot(Deviceinfo deviceInfo, ArrayList<String> netList){
		ConfigNBModuleInfo configNBModuleInfo = ConfigNBModuleInfo.getInstance(context);
		switch (configNBModuleInfo.getNbModuleName()) {
			case Deviceinfo.DeviceName_YaoYuan_Really: {
				netList.add(ForceNet.NET_NBIot_NB1.getDescrition());
				netList.add(ForceNet.NET_NBIot_CatM1.getDescrition());
			}
			break;
		}
	}

	private void addLockNetworkListS8CumstomRom(Deviceinfo deviceinfo, ArrayList<String> netList) {
		netList.clear();
		netList.add(ForceNet.NET_GSM.getNetStr());
		netList.add(ForceNet.NET_WCDMA.getNetStr());
		netList.add(ForceNet.NET_LTE.getNetStr());
	}


	private ForceNet addLockBadnList(String network, ArrayList<Band> bandList){
		Deviceinfo deviceinfo = Deviceinfo.getInstance();
		if (ApplicationModel.getInstance().isNBTest()){
			return  addLockBadnListNBIot(network, bandList);
		} else if ((deviceinfo.isSamsungCustomRom()) || (deviceinfo.isCustomS8RomRoot())) {
			return  addLockBadnListS8CustomRom(network, bandList);
		} else
			return addLockBadnListNormal(network, bandList);
	}

	private ForceNet addLockBadnListNormal(String network, ArrayList<Band> bandList) {
		ForceNet netType = null;
		switch (network) {
			case Deviceinfo.NET_TYPES_GSM: {
				bandList.addAll(Band.getGsmBands());
				netType = ForceNet.NET_GSM;
			}
			break;
			case Deviceinfo.NET_TYPES_WCDMA: {
				bandList.addAll(Band.getWCDMABands());
				netType = ForceNet.NET_WCDMA;
			}
			break;
			case Deviceinfo.NET_TYPES_TDSCDMA: {
				bandList.addAll(Band.getTDSCDMABands());
				netType = ForceNet.NET_TDSCDMA;
			}
			break;
			case Deviceinfo.NET_TYPES_LTE: {
				bandList.addAll(Band.getLTEBands());
				netType = ForceNet.NET_LTE;
			}
			break;
		}

		return netType;
	}

	private ForceNet addLockBadnListNBIot(String network, ArrayList<Band> bandList) {
		ForceNet netType = null;

		if (network.equals(ForceNet.NET_NBIot_NB1.getDescrition())) {
			netType = ForceNet.NET_NBIot_NB1;
		} else if (network.equals(ForceNet.NET_NBIot_CatM1.getDescrition())) {
			netType = ForceNet.NET_NBIot_CatM1;
		} else
			return null;

		ArrayList<Band> arrayList = new ArrayList<>();
		arrayList.add(Band.Auto);
		for (Band b : Band.values()) {
			if (b.name().startsWith("NB_Band")) {
				arrayList.add(b);
			}
		}
		bandList.addAll(arrayList);

		return netType;
	}

	private ForceNet addLockBadnListS8CustomRom(String network, ArrayList<Band> bandList) {
		ForceNet netType = null;
		switch (network) {
			case Deviceinfo.NET_TYPES_GSM: {
				bandList.add(Band.Auto);
				bandList.add(Band.G900);
				bandList.add(Band.G1900);
				bandList.add(Band.G1800);
				bandList.add(Band.G850);

				netType = ForceNet.NET_GSM;
			}
			break;
			case Deviceinfo.NET_TYPES_WCDMA: {
				bandList.add(Band.Auto);
				bandList.add(Band.W2100);
				bandList.add(Band.W1900);
				bandList.add(Band.W850);
				bandList.add(Band.W900);

				netType = ForceNet.NET_WCDMA;
			}
			break;
			case Deviceinfo.NET_TYPES_LTE: {
				bandList.add(Band.Auto);

				bandList.add(Band.L1);
				bandList.add(Band.L2);
				bandList.add(Band.L3);
				bandList.add(Band.L4);
				bandList.add(Band.L5);
				bandList.add(Band.L7);
				bandList.add(Band.L8);
				bandList.add(Band.L12);
				bandList.add(Band.L13);
				bandList.add(Band.L17);
				bandList.add(Band.L18);
				bandList.add(Band.L19);
				bandList.add(Band.L20);
				bandList.add(Band.L25);
				bandList.add(Band.L26);
				bandList.add(Band.L28);
				bandList.add(Band.L38);
				bandList.add(Band.L39);
				bandList.add(Band.L40);
				bandList.add(Band.L41);

				netType = ForceNet.NET_LTE;
			}
			break;
		}

		return netType;
	}

	private void showBandDialog(final ForceNet netType ,final ArrayList<Band> bandList){
		
//		final String[] bands = new String[bandList.size()];
//		final boolean[] checkedItems = new boolean[bandList.size()];
		
		//Adapter
		final ArrayList<HashMap<String, Object>> hashList = new ArrayList<>();
		for(int i=0;i<bandList.size();i++){
			Band band = bandList.get(i);
			boolean isCheck = ForceManager.getInstance().getLockBand(context).contains(band);
			HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemText",band.toString() );
            map.put("ItemIcon", isCheck ? R.drawable.btn_check_on : R.drawable.btn_check_off);
            map.put("ItemChecked",isCheck );
            map.put("ItemBand",band );
            hashList.add(map);
		}
		final SimpleAdapter adapter = new SimpleAdapter(context, hashList,
				R.layout.lock_list_item,
				new String[] { "ItemText", "ItemIcon" },
				new int[] { R.id.ItemText, R.id.ItemIcon });
		
		//ListView
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lock_listview, null);
		ListView listView = (ListView)view.findViewById(R.id.ListView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener( new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                    View view, int position, long id) {
            	
            	HashMap<String, Object> map = hashList.get(position);
            	boolean check = !(Boolean) map.get("ItemChecked");
            	map.put("ItemChecked",check);
        		map.put("ItemIcon",check?R.drawable.btn_check_on:R.drawable.btn_check_off);
            	
            	if( position ==0 ){
        			for(HashMap<String, Object> hm:hashList){
        				hm.put("ItemIcon", check?R.drawable.btn_check_on:R.drawable.btn_check_off);
        				hm.put("ItemChecked",check );
        			}
            	}else{
            		for(int i=1;i<hashList.size();i++){
            			if( !(Boolean)hashList.get(i).get("ItemChecked") ){
            				hashList.get(0).put("ItemChecked",false);
            				hashList.get(0).put("ItemIcon",R.drawable.btn_check_off);
            				break;
            			}
            		}
            	}
            	adapter.notifyDataSetChanged();
            }
        });
		
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		int weight = RelativeLayout.LayoutParams.MATCH_PARENT;
		int height = ((netType==ForceNet.NET_LTE) || (netType == ForceNet.NET_NBIot_NB1) || (netType == ForceNet.NET_NBIot_CatM1))?
				(int)(400 * metric.density):RelativeLayout.LayoutParams.WRAP_CONTENT;

		BasicDialog.Builder builder = new BasicDialog.Builder(context);
		builder.setTitle( R.string.lock_lockband);
		builder.setView(view,new RelativeLayout.LayoutParams(weight,height));

		/*
		if (Deviceinfo.getInstance().isSamsungCustomRom())
			newDialogS8CunstomRom(hashList, builder, (BaseAdapter)listView.getAdapter(), netType);
		else
		*/
		newDialogNormal(hashList, builder, netType);
	}

	private void newDialogNormal(final ArrayList<HashMap<String, Object>> hashList, BasicDialog.Builder builder, final ForceNet netType) {

		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						ArrayList<Band> lockList = new ArrayList<Band>();

						for(int i=0;i<hashList.size();i++){
							Band band =  (Band) hashList.get(i).get("ItemBand") ;
							boolean check = (Boolean) hashList.get(i).get("ItemChecked"); ;
							if( check ){
								lockList.add(band);
							}
						}

						Band[] lockBands = new Band[ lockList.size()];
						lockList.toArray(lockBands);
						if(Deviceinfo.getInstance().getDevicemodel().equals("HuaweiMT7") && lockList.size() > 1){
							Toast.makeText(context, R.string.lock_alarm_onlyone_band, Toast.LENGTH_LONG).show();
							return;
						}
						new LockBandProgress(context, netType, lockBands,callback).execute();

					}
				}).setNegativeButton(R.string.str_cancle);

		builder.create().show();
	}

	private int mCheckItem = 0;
	private void newDialogS8CunstomRom(final ArrayList<HashMap<String, Object>> hashList, BasicDialog.Builder builder, BaseAdapter adapter, final ForceNet netType) {
		builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mCheckItem = which;

						ArrayList<Band> lockList = new ArrayList<>();

						Band band =  (Band) hashList.get(mCheckItem).get("ItemBand") ;
						lockList.add(band);

						Band[] lockBands = new Band[ lockList.size()];
						lockList.toArray(lockBands);

						new LockBandProgress(context, netType, lockBands,callback).execute();
					}
				});

		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						ArrayList<Band> lockList = new ArrayList<>();

						Band band =  (Band) hashList.get(mCheckItem).get("ItemBand") ;
						lockList.add(band);

						Band[] lockBands = new Band[ lockList.size()];
						lockList.toArray(lockBands);

						new LockBandProgress(context, netType, lockBands,callback).execute();

					}
				});

		builder.setNegativeButton(R.string.str_cancle);

		builder.create().show();
	}
}

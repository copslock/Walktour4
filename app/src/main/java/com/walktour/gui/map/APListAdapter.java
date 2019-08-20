package com.walktour.gui.map;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dingli.wlan.apscan.APInfoModel;
import com.dingli.wlan.apscan.WifiScanner;
import com.dingli.wlan.apscan.WifiTools;
import com.walktour.gui.R;

import java.util.List;

public class APListAdapter extends BaseAdapter {
	private Context context;
	private int[] wifi_signal_level = {R.drawable.ic_wifi_signal_1,R.drawable.ic_wifi_signal_2,
										R.drawable.ic_wifi_signal_3,R.drawable.ic_wifi_signal_4};
	private int[] wifi_locked_signal = {R.drawable.ic_wifi_lock_signal_1,R.drawable.ic_wifi_lock_signal_2,
										R.drawable.ic_wifi_lock_signal_3,R.drawable.ic_wifi_lock_signal_4};
	private List<APInfoModel> localResults;
	// 当前Wifi正在连接的AP的bssid
	private String strCurrConnApBssid = WifiTools.getCurrentBssid();;
	
	public APListAdapter(Context c) {
		this.context = c;
		localResults = WifiScanner.instance(c).getApList();
	}
	public void setAPList(List<APInfoModel> list) {
		localResults = list;
	}
	
	@Override
	public int getCount() {
		if (localResults != null) {
			return localResults.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position < localResults.size()) {
			return localResults.get(position);
		}
		else {
			return 0;
		}
//		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.apscan_listview_ui, null);
			
			viewHolder.ssid_bssid = (TextView) convertView.findViewById(R.id.apscan_listview_ui_ssid_bssid);
			viewHolder.channel = (TextView) convertView.findViewById(R.id.apscan_listview_ui_channel);
			viewHolder.txtIsConnected = (TextView) convertView.findViewById(R.id.is_connected);
			viewHolder.frequency = (TextView) convertView.findViewById(R.id.apscan_listview_ui_frequency);
			viewHolder.rssibar = (ProgressBar) convertView.findViewById(R.id.apscan_listview_ui_rssi);
			viewHolder.mode = (TextView) convertView.findViewById(R.id.apscan_listview_ui_mode);
			viewHolder.authmode = (TextView) convertView.findViewById(R.id.apscan_listview_ui_authmode);
			viewHolder.rssiImg = (ImageView) convertView.findViewById(R.id.rssiImg);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// TextView ssid_bssid =
		// (TextView)itemView.findViewById(R.id.apscan_listview_ui_ssid_bssid);
		// TextView txtIsConnected =
		// (TextView)itemView.findViewById(R.id.is_connected);
		APInfoModel localResult = null;
		try {
			localResult = localResults.get(position);
		} catch (Exception e) {
			return new View(context);
		}
		
		viewHolder.ssid_bssid.setText(localResult.ssid + " ("+ localResult.bssid + ")");
//				
////		this.strCurrConnApBssid = WifiTools.getCurrentBssid();
		// 如果是当前正在连接的AP，则显示“已连接”
//		// String bssidText = null;
		if (localResult.bssid.equals(strCurrConnApBssid)) {
			viewHolder.txtIsConnected.setVisibility(View.VISIBLE);
			viewHolder.txtIsConnected.setText("["+ context.getString(R.string.connected) + "]");
		} else {
			viewHolder.txtIsConnected.setVisibility(View.GONE);
		}
		
		int frequency = localResult.frequency;
		
		viewHolder.channel.setText(context.getString(R.string.channel)+" "+WifiTools.getChannel(frequency));
		viewHolder.frequency.setText(frequency + " MHz");
		String automode = WifiTools.getScanResultSecurity(localResult.encryptionType);
		int level = WifiManager.calculateSignalLevel(localResult.rssi, 4);
		if (automode.equals(WifiTools.OPEN)) {
			viewHolder.rssiImg.setImageResource(wifi_signal_level[level]);
		} else {
			viewHolder.rssiImg.setImageResource(wifi_locked_signal[level]);
		}
		viewHolder.rssibar.setProgress(Math.abs(localResult.rssi + (110)));
		viewHolder.authmode.setText(WifiTools.getScanResultSecurity(localResult.encryptionType));
		viewHolder.mode.setText(localResult.mode);
		return convertView;
	}
	
	
	private class ViewHolder {
		TextView ssid_bssid;
		TextView txtIsConnected;
		TextView channel;
		TextView frequency;
		ProgressBar rssibar;
		TextView mode;
		TextView authmode;
		ImageView rssiImg;
	}
	
	

}

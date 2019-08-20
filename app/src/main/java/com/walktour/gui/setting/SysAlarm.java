package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.Alarm;
import com.walktour.control.adapter.AlarmSetAdapter;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.msg.CustomEventMsgListActivity;
import com.walktour.gui.setting.customevent.param.CustomEventParamListActivity;

import java.util.List;

public class SysAlarm extends BasicActivity implements OnClickListener,OnCheckedChangeListener{
	private final int SHOW_DEVICE_ALARMS = 1;
	private final int SHOW_NETWORK_ALARMS = 2;
	private final int SHOW_TEST_ALARMS = 3;
	
	private RelativeLayout layoutDevice;
	private RelativeLayout layoutNetwork;
	private RelativeLayout layoutTest;
	private RelativeLayout layoutDefineMsg;
	private RelativeLayout layoutDefineParam;
	private CheckBox checkDeviceSound;
	private CheckBox checkNetSound;
	private CheckBox checkNetMap;
	private CheckBox checkTestSound;
	private CheckBox checkTestMap;
	private ListView listView;
	private Context mContext;
	private AlarmSetAdapter adapter = null;
	private boolean[] checked;
	private BasicDialog basicDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView( R.layout.sys_alarm2);
		findView();
	}

	@Override
	public void onClick(View arg0) {
		switch( arg0.getId() ){
		case R.id.setting_alarm_device:
			showAlarmsDialog( SHOW_DEVICE_ALARMS );
			break;
		case R.id.setting_alarm_network:
			showAlarmsDialog( SHOW_NETWORK_ALARMS );
			break;
		case R.id.setting_alarm_test:
			showAlarmsDialog( SHOW_TEST_ALARMS );
			break;
		case R.id.setting_alarm_define_msg:
			Intent intent = new Intent(this,CustomEventMsgListActivity.class );
			startActivity( intent );
			break;
		case R.id.setting_alarm_define_param:
			intent = new Intent(this,CustomEventParamListActivity.class );
			startActivity( intent );
			break;
		}
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch( buttonView.getId() ){
		case R.id.check_device_sound:
			AlertManager.getInstance(mContext).setPrefs( AlertManager.KEY_DEVICE_SOUND, isChecked );
			break;
		case R.id.check_network_sound:
			AlertManager.getInstance(mContext).setPrefs( AlertManager.KEY_NETWORK_SOUND, isChecked );
			break;
		case R.id.check_network_map:
			AlertManager.getInstance(mContext).setPrefs( AlertManager.KEY_NETWORK_MAP, isChecked );
			break;
		case R.id.check_test_sound:
			AlertManager.getInstance(mContext).setPrefs( AlertManager.KEY_TEST_SOUND, isChecked );
			break;
		case R.id.check_test_map:
			AlertManager.getInstance(mContext).setPrefs( AlertManager.KEY_TEST_MAP, isChecked );
			break;
		
		}
	}

	private void findView(){
		layoutDevice = initRelativeLayout(R.id.setting_alarm_device);
		layoutNetwork = initRelativeLayout(R.id.setting_alarm_network);
		layoutTest = initRelativeLayout(R.id.setting_alarm_test);
		layoutDefineMsg = initRelativeLayout(R.id.setting_alarm_define_msg);
		layoutDefineParam = initRelativeLayout(R.id.setting_alarm_define_param);
		checkDeviceSound = (CheckBox) findViewById(R.id.check_device_sound);
		checkNetSound = (CheckBox) findViewById(R.id.check_network_sound);
		checkNetMap = (CheckBox) findViewById(R.id.check_network_map);
		checkTestSound = (CheckBox) findViewById(R.id.check_test_sound);
		checkTestMap = (CheckBox) findViewById(R.id.check_test_map);
		
		checkDeviceSound.setChecked( AlertManager.getInstance(mContext)
				.getPrefs( AlertManager.KEY_DEVICE_SOUND) );
		checkNetSound.setChecked( AlertManager.getInstance(mContext)
				.getPrefs( AlertManager.KEY_NETWORK_SOUND) );
		checkNetMap.setChecked( AlertManager.getInstance(mContext)
				.getPrefs( AlertManager.KEY_NETWORK_MAP) );
		checkTestSound.setChecked( AlertManager.getInstance(mContext)
				.getPrefs( AlertManager.KEY_TEST_SOUND) );
		checkTestMap.setChecked( AlertManager.getInstance(mContext)
				.getPrefs( AlertManager.KEY_TEST_MAP) );
		
		layoutDevice.setOnClickListener(this);
		layoutNetwork.setOnClickListener(this);
		layoutTest.setOnClickListener(this);
		layoutDefineMsg.setOnClickListener(this);
		layoutDefineParam.setOnClickListener(this);
		
		checkDeviceSound.setOnCheckedChangeListener( this );
		checkNetSound.setOnCheckedChangeListener( this );
		checkNetMap.setOnCheckedChangeListener( this );
		checkTestSound.setOnCheckedChangeListener( this );
		checkTestMap.setOnCheckedChangeListener( this );
		
	}
	
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showAlarmsDialog( int type ){
		 LayoutInflater inflater = LayoutInflater.from( mContext );
         View view = inflater.inflate(R.layout.list_alarm, null);
         listView = (ListView) view.findViewById(R.id.list);
         
         String title = "";
         switch( type ){
         case SHOW_DEVICE_ALARMS:
        	 title = getString( R.string.sys_alarm_device );
        	 adapter = new AlarmSetAdapter( mContext ,WalkStruct.Alarm.getDeviceAlarms() );
        	 break;
         case SHOW_NETWORK_ALARMS:
        	 title = getString( R.string.sys_alarm_network );
        	 adapter = new AlarmSetAdapter( mContext ,WalkStruct.Alarm.getNetworkAlarms() );
        	 break;
         case SHOW_TEST_ALARMS:
        	 title = getString( R.string.sys_alarm_test );
        	 adapter = new AlarmSetAdapter( mContext ,WalkStruct.Alarm.getTestAlarms() );
        	 break;
         }
         
         
         List<Alarm> listAlarm = adapter.getAllItems();
         checked = new boolean[ listAlarm.size() ];
         for( int i=0;i<checked.length;i++){
        	 if( AlertManager.getInstance(mContext).isAlarmOn( listAlarm.get(i) ) ){
        		 checked[i] = true;
        	 }else{
        		 checked[i] = false;
        	 }
         }
         
         listView.setAdapter(adapter);
         
         listView.setOnItemClickListener( new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Alarm alarm = (Alarm)adapter.getItem( arg2 );
				boolean isOpen = AlertManager.getInstance(mContext).isAlarmOn(alarm);
				AlertManager.getInstance(mContext).setAlarm(alarm, !isOpen );
				adapter.notifyDataSetChanged();
			}
        	 
         });
         
         DisplayMetrics metric = new DisplayMetrics();
         getWindowManager().getDefaultDisplay() .getMetrics(metric);
         basicDialog = new BasicDialog.Builder( mContext )
         .setTitle( title )
         .setView( view, new RelativeLayout.LayoutParams(
        		 LayoutParams.FILL_PARENT, (int)(350 * metric.density)) 
          )
          .setPositiveButton( R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//donothing ,在listView的item事件里已经处理
			}
		})
		.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//取消时恢复原来的值
				for( int i=0;i<checked.length;i++ ){
					Alarm alarm = (Alarm)adapter.getItem( i );
					AlertManager.getInstance(mContext).setAlarm(alarm, checked[i] );
				}
			}
		})
		.setNegativeButton( R.string.str_cancle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//取消时恢复原来的值
				for( int i=0;i<checked.length;i++ ){
					Alarm alarm = (Alarm)adapter.getItem( i );
					AlertManager.getInstance(mContext).setAlarm(alarm, checked[i] );
				}
			}
		})
         .show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (basicDialog != null && basicDialog.isShowing()) {
			basicDialog.dismiss();
		}
	}
	
	
	
}

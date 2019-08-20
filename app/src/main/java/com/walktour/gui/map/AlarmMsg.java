package com.walktour.gui.map; 

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.base.util.LogUtil;
import com.walktour.control.adapter.AlarmAdapter;
import com.walktour.control.instance.AlertManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

public class AlarmMsg extends BasicActivity  {  
	private static final String tag ="AlarmMsg";
	private static final int Refresh = 1;
	
	
	//页面显示的事件列表
	private AlertManager alarmMng;
	private AlarmAdapter adapter ;
	private ListView listView;
	private boolean needToScroll = true;	//是否需要滚动到最底端
    
	//BroadcastReceiver
	private  MyBroadcastReceiver mEventReceiver;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        alarmMng = AlertManager.getInstance( this );
		adapter = new AlarmAdapter( this,alarmMng.getAlarmListClone() );
         
    } 
	
	@Override
	public void onStart(){
		super.onStart();
		LogUtil.i(tag,"--->onStart");
		regedit();
		findView();
	}

	
	@Override
	public void onPause(){
		super.onPause();
		LogUtil.i(tag,"--->onPause");
	}
	
    @Override  
    protected void onRestart() {  
        LogUtil.i(tag, "--->onRestart");  
        super.onRestart();  
    }
	
	@Override
	public void onDestroy(){
		unregisterReceiver(mEventReceiver);//反注册事件监听
		super.onDestroy();
	}
	
	/**
	 * 注册广播接收器
	 */
	private void regedit() {
		IntentFilter filter = new IntentFilter();
		filter.addAction( WalkMessage.ACTION_ALARM_LIST );
		mEventReceiver = new MyBroadcastReceiver();
		this.registerReceiver(mEventReceiver, filter);
	}
	
	private void findView(){
		setContentView(R.layout.mutily_listview); 
		LinearLayout search = (LinearLayout)findViewById(R.id.LinearLayoutMapSearch);
		search.setVisibility(View.GONE);
		listView = (ListView) findViewById(R.id.ListView01);
        listView.setAdapter( adapter );
        listView.setOnScrollListener( new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if( firstVisibleItem < (totalItemCount- visibleItemCount-2) ){
					needToScroll = false;
				}else{
					needToScroll = true;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
        	
        });
        listView.setSelection( adapter.getCount());
	}
	
	protected Handler mHandler = new Handler(){
		public void  handleMessage(Message msg){
			switch(msg.what){
			case Refresh: 
				if( adapter != null ){
					//有可能出现03-06 09:48:30.394: E/AndroidRuntime(7429): java.lang.IllegalStateException: The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. [in ListView(2131296547, class android.widget.ListView) with Adapter(class com.walktour.control.adapter.AlarmAdapter)]
					//暂时用clone来避免
					adapter.setAlarmList( alarmMng.getAlarmListClone() );
					adapter.notifyDataSetChanged();
					if( needToScroll ){
						listView.setSelection( adapter.getCount() );
					}
				}
			break;
			}
		}
	};
	
	
	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 * */
	private class MyBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//fleet事件
			if(intent.getAction().equals(WalkMessage.ACTION_ALARM_LIST )
			        && !ApplicationModel.getInstance().isFreezeScreen() ){
				
				Message msg = mHandler.obtainMessage(Refresh);
				msg.sendToTarget();
			}
		}
		
	}//end inner class EventBroadcastReceiver
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
		LogUtil.i(tag,"onSaveInstanceState");
    }
	
}  
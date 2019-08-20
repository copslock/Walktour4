package com.walktour.gui.auto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TabHost;

import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.gui.R;


/**************************************** 
 * 系统设置 界面
 * **************************************/

public class AutoTest extends BasicTabActivity {     
	    private TabHost tabHost; 
		//BroadcastReceiver
		private  static MyBroadcastReceiver mEventReceiver;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {    
	        super.onCreate(savedInstanceState);	
	        findView();
	    }
		
		@Override
		public void onStart(){
			super.onStart();
			regedit();
		}
		
		@Override
		public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
		
		@Override
		public void onDestroy(){
			unregisterReceiver(mEventReceiver);//反注册事件监听
			super.onDestroy();
		}
		
		  public void findView(){    	  	
		    	
		    	tabHost = getTabHost();	    	

		        tabHost.addTab(tabHost.newTabSpec("tab1")	        	
		                .setIndicator( getString(R.string.fleet_setting),
		                		getResources().getDrawable(android.R.drawable.ic_menu_preferences) )
		                .setContent(new Intent(this, Setting.class))
		                );    
		        
		        tabHost.addTab(tabHost.newTabSpec("tab2")
		                .setIndicator( 
			                		getString(R.string.fleet_event),
			                		getResources().getDrawable(android.R.drawable.ic_dialog_info)
		                		)
		                .setContent(new Intent(this, Event.class))
	            );     
	    
		        setContentView(tabHost); //tabhost必须实例化后才能setContentView
		        
		    }
	    
		//注册广播接收器
		private void regedit() {
			IntentFilter filter = new IntentFilter();
			this.registerReceiver(mEventReceiver, filter);
		}
		
	    /**
		 * 广播接收器:
		 * */
		private class MyBroadcastReceiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, Intent intent) {
					
			}
			
		}//end inner class EventBroadcastReceiver
	     
} //end class Sys
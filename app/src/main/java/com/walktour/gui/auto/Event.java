package com.walktour.gui.auto;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.util.ArrayList;

public class Event extends BasicActivity  {  
//	private static final String TAG ="Walktour.AutoTest.Event";
	
	private static ListView listView;
	private static ArrayAdapter<String> adapter;
	
	//FleetEvent界面要显示的内容;
	public static ArrayList<String> events ;
	
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.listview_with_title);  
        findView();
    }

	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater mInflater = getMenuInflater();
		mInflater.inflate(R.menu.fleet_event, menu);	
		return true;
	}
	 @Override//菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item)
	 {    	
		 super.onOptionsItemSelected(item);
		 switch(item.getItemId())
		 {
		 case R.id.menu_fleet_clearEvent:
			 //清除事件
			 //Fleet.clearEvent();
			 //更新listView
			 adapter.notifyDataSetChanged();
			 break;
		 }
		 return true;
	 }
	
	//显示Fleet Event的TextView
	private void findView(){
        listView = (ListView) findViewById(R.id.ListView01);
		        
    	adapter = new ArrayAdapter<String>(this,
    			R.layout.listview_item_style7, 
    			R.id.ItemText,
    			events);
    	listView.setAdapter(adapter);
    	listView.setSelection( events.size() );
	}
	
	
	/**
	 * 广播接收器:接收来自Fleet.java的广播更新界面
	 * */
//	private class MyBroadcastReceiver extends BroadcastReceiver{
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			//fleet事件
//		}
//		
//	}//end inner class EventBroadcastReceiver
	
}  
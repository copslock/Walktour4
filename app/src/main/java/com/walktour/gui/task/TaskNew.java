package com.walktour.gui.task;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskNew extends ExpandableListActivity {
    private static final String NAME = "NAME";
    private static final String TYPE = "Type";
    
    private ExpandableListAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //存储组内容的列表
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        
        //存储子组内容的列表
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        
        //任务菜单第一级列表
        String task_type[] = getResources().getStringArray( R.array.task_type );        
        
        //生成第一棵树的根，把根的内容放入groupData       
        Map<String, String> groupMap1 = new HashMap<String, String>();
        groupData.add(groupMap1);
        groupMap1.put(NAME, task_type[0] );
        //生成第一棵树的孩子，
        List<Map<String, String>> children1 = new ArrayList<Map<String, String>>();
        String task_type_free[] = getResources().getStringArray( R.array.task_type_free );   
        for (int j = 0; j < task_type_free.length; j++) {
            Map<String, String> curChildMap = new HashMap<String, String>();
            children1.add(curChildMap);
            curChildMap.put(NAME, task_type_free[j]);
            curChildMap.put(TYPE, "新添加"+task_type_free[j]);
        }
        //把第一棵树的孩子放入childData
        childData.add(children1);  
        
      //生成第二棵树的根，把根的内容放入groupData       
        Map<String, String> groupMap2 = new HashMap<String, String>();
        groupData.add(groupMap2);
        groupMap2.put(NAME, task_type[1] );
        //生成第二棵树的孩子，
        List<Map<String, String>> children2 = new ArrayList<Map<String, String>>();
        String task_type_call[] = getResources().getStringArray( R.array.task_type_call );   
        for (int j = 0; j < task_type_call.length; j++) {
            Map<String, String> curChildMap = new HashMap<String, String>();
            children2.add(curChildMap);
            curChildMap.put(NAME, task_type_call[j]);            
        }
        //把第二棵树的孩子放入childData
        childData.add(children2); 
        
        
      //生成第三棵树的根，把根的内容放入groupData       
        Map<String, String> groupMap3 = new HashMap<String, String>();
        groupData.add(groupMap3);
        groupMap3.put(NAME, task_type[2] );
        //生成第三棵树的孩子，
        List<Map<String, String>> children3 = new ArrayList<Map<String, String>>();
        String task_type_data[] = getResources().getStringArray( R.array.task_type_data );   
        for (int j = 0; j < task_type_data.length; j++) {
            Map<String, String> curChildMap = new HashMap<String, String>();
            children3.add(curChildMap);
            curChildMap.put(NAME, task_type_data[j]);            
        }
        //把第三棵树的孩子放入childData
        childData.add(children3);
        
      //生成第四棵树的根，把根的内容放入groupData       
        Map<String, String> groupMap4 = new HashMap<String, String>();
        groupData.add(groupMap4);
        groupMap4.put(NAME, task_type[3] );
        //生成第四棵树的孩子，
        List<Map<String, String>> children4 = new ArrayList<Map<String, String>>();
        String task_type_more[] = getResources().getStringArray( R.array.task_type_more );   
        for (int j = 0; j < task_type_more.length; j++) {
            Map<String, String> curChildMap = new HashMap<String, String>();
            children4.add(curChildMap);
            curChildMap.put(NAME, task_type_more[j]);            
        }
        //把第四棵树的孩子放入childData
        childData.add(children4);
        
        // Set up our adapter
        mAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME, TYPE },
                new int[] { android.R.id.text1, android.R.id.text2 },
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME, TYPE },
                new int[] { android.R.id.text1, android.R.id.text2 }
                );
        setListAdapter(mAdapter);
        
        getExpandableListView().setOnChildClickListener(new OnChildClickListener(){
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				createNewTask(groupPosition,childPosition);
				return true;
			}
        });
    }
    
    private void createNewTask(int groupPosition, int childPosition){
    	Intent intent= null;
    	switch(groupPosition){
    	case 0:	//空闲测试
    		intent = new Intent(this,TaskEmpty.class);
    		break;
    	case 1:	//拨打测试
    		switch(childPosition){
    		case 0:	//语音主叫
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskMOCCall.class);
    			break;
    		case 1:	//语音被叫
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskMTCCall.class);
    			break;
    		case 2:	//视频主叫
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskInitiativeVideoCall.class);
    			break;
    		case 3:	//视频被叫
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskPassivityVideoCall.class);
    			break;
    		}
    		break;
    	case 2:	//数据测试
    		switch(childPosition){
    		case 0:	//Ping
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskPing.class);
    			break;
    		case 1:	//Attach
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskAttach.class);
    			break;
    		case 2:	//PDP
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskPDP.class);
    			break;
    		case 3:	//FTP上传
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskFtpUpload.class);
    			break;
    		case 4:	//FTP下载
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskFtpDownload.class);
    			break;
    		case 5:	//Http
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskHttpLogon.class);
    			break;
    		case 6:	//Email
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskEmailPop3.class);
    			break;
    		case 7:	//Email
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskEmailSmtp.class);
    			break;
    		}
    		break;
    	case 3:	//增值测试
    		switch(childPosition){
    		case 0:	//接收短信
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskSmsIncept.class);
    			break;
    		case 1:	//发送短信
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskSmsSend.class);
    			break;
    		case 2:	//接收彩信
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskMmsReceive.class);
    			break;
    		case 3:	//发送彩信
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskMmsSend.class);
    			break;
    		case 4:	//wap登陆
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskWapLogin.class);
    			break;
    		case 5:	//wap刷新
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskWapRefurbish.class);
    			break;
    		case 6:	//wap下载
    			intent = new Intent(com.walktour.gui.task.TaskNew.this,TaskWapDownload.class);
    			break;
    		}    		
    		break;
    	}
    	turnToTaskList(intent);
    }
    
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			turnToTaskList(null);
			return false;
		}
		return true;
	}
	private void turnToTaskList(Intent intent){
		if(intent==null)
			intent = new Intent(com.walktour.gui.task.TaskNew.this ,Task.class);
		startActivity(intent);
		com.walktour.gui.task.TaskNew.this.finish();
	}
}

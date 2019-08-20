package com.walktour.gui.mutilytester;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct.UMPCConnectStatus;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigAPN;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.config.ConfigUmpc;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.sysroutine.SysRoutineAPNActivity;
import com.walktour.model.UmpcEnvModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MutilyTesterSet extends BasicActivity {
	private final String tag = "MutilyTesterSet";
	private final int PROGRESS_OFF = 0;
	private final int PROGRESS_ON = 1;
	private final int STOP_UMPCSERVICE= 2;
	private final int WIFI_ON = 3;
	private final int WIFI_OFF = 4;
	private final int CHANGE_APN=5;
	private final int REDO_BANDWIFI = 99;	//发送绑定WIFI消息
	private ConfigUmpc configUmpc = ConfigUmpc.getInstance();
	private UmpcEnvModel umpcModel = configUmpc.getUmpcModel();
	private Deviceinfo deviceInfo = null;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private ProgressDialog progressDialog ;
	private ProgressDialog progressWifi ;
	private boolean showProgressOut = false;	//是否退出显示连接进度框
	private boolean isApnPointChange= false;	//是否改变数据测试时APN接入点,如果是在onResume的时候刷新页面
	private int showProgressBackTimes = 0;		//显示连接进度过程中点返回键的次数，如果三次以上断开UMPC连接服务
//	private int cipherPosition = 0;
//	private int controlerService	= UmpcTestInfo.ControlForNone;	//控制端类型
	private ListView list;
//	private View viewAP ;						//接入点选择界面
	private EditText editName;
	private BasicDialog alertDialog ;
	private List<ScanResult> scanResultList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mutily_listview);
		deviceInfo = Deviceinfo.getInstance();
		//controlerService = getIntent().getIntExtra(UmpcTestInfo.controlKey, UmpcTestInfo.ControlForNone);
		
		findView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(isApnPointChange){
			LogUtil.w(tag,"---onResume---");
			isApnPointChange = false;
			findView();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		showProgressOut = true;
	}
	
	private void findView(){
		list = (ListView) findViewById(R.id.ListView01);
		//获取listview每个 item 的字符串
        String[] title = getResources().getStringArray(R.array.system_mutilytester_set);
        String[] text = {
        		//umpcModel.isAutoStart() ? getString(R.string.on) : getString(R.string.off),
        		/*umpcModel.getWifiName(),
        		umpcModel.getServerIp(),
        		umpcModel.getMobileName(),*/
        		ConfigAPN.getInstance().getDataAPN(),
        		getString(R.string.mutilytester_umpc_Alarm),
        		ConfigRoutine.getInstance().canRunScript()?getString(R.string.on):getString(R.string.off)
        };
        //生成动态数组，每个数组单元对应一个item
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<title.length;i++){  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("ItemTitle", title[i] ); 
            if(title[i].contains("密码") || title[i].contains("Password"))
            	map.put("ItemText","******");
            else
            	map.put("ItemText",text[i]);
            listItem.add(map);  
        }
        
        /*if(deviceInfo.getWifiDataOnly().equals("1") || deviceInfo.getWifiDataOnly().equals("2")){
            listItem.remove(0);
        }*/
        
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,
        	 listItem,//ListItem的数据源   
            R.layout.listview_item_style1,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemTitle", "ItemText"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.ItemTitle,R.id.ItemText}  
        );
        
        //添加并且显示  
        list.setAdapter(listItemAdapter);
        
        //添加点击item事件  
        list.setOnItemClickListener(new OnItemClickListener() {            
		       @SuppressWarnings("deprecation")
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		                
	               try{ 
		    			showDialog(arg2);
	               }catch(Exception e){
	            	  	e.printStackTrace();
	               }
		       }  
        });
        
        //上下文菜单
        list.setOnCreateContextMenuListener( contextMenuListener );
        list.setLongClickable( false );
        
        if(progressDialog == null){
	        progressDialog = new ProgressDialog(this){
				@Override
				public boolean onKeyDown(int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
						//showProgressOut = true;
						showProgressBackTimes++;
			        	LogUtil.w(tag,"---ProgressDialog BACK---");
			        	if(showProgressBackTimes < 3)
			        		return true;
			        	else
			        		showProgressOut = true;
			        }
					return super.onKeyDown(keyCode, event);
				}
				
			};
			progressDialog.setCancelable(false);
        }
	}
	
	/***************************************************
     * 继承方法:
     * 重写activity的onCreateDialog弹出对话框     * 
     * @see com.walktour.framework.ui.BasicActivity#onCreateDialog(int)
     ****************************************************/
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
    	//从XML获取弹出窗口中的内容:EditText
    	LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext, null);
    	//twq20120710 自动开关屏幕后，点击的ID 0 实际上是下面的的故 + 1;如果当前是S3(i9305)，需要去掉WIFI连接设置，ID又+1
        id = id + (deviceInfo.getWifiDataOnly().equals("1") || deviceInfo.getWifiDataOnly().equals("2") ? 2 : 1);
        switch( id ){    	
    	/*case 0://弹出 "启动UMPC开关"对话框 
            return new BasicDialog.Builder(MutilyTesterSet.this)
                .setIcon(android.R.drawable.ic_menu_edit)
                .setTitle( getResources().getStringArray(R.array.system_mutilytester_set)[id-1] )
                .setSingleChoiceItems(
                		getResources().getStringArray(R.array.public_switch),
                		umpcModel.isAutoStart() ? 0 : 1,
                		new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog,int which) {
									umpcModel.setAutoStart(which == 0 ? true : false);
									configUmpc.writeToFile();
									
									//处理测试服务的开启，关闭
									Intent umpcChanged = new Intent(umpcModel.isAutoStart() ? 
											WalkMessage.MutilyTester_ReDo_ConnectServer : WalkMessage.MutilyTester_Close_UmpcServer);
									sendBroadcast(umpcChanged);
	    							
									findView();
									dialog.dismiss();
									
									//new Thread( new ThreadStatus() ).start();
									//new doBandWifki().start();
							}
                }).create();
            
    	case 1:   //接入点名称和密码
    		cipherPosition = umpcModel.getWifiCiphermode();
    		LogUtil.w(tag, "----cipherPosition="+cipherPosition);
        	 viewAP = factory.inflate(R.layout.alert_dialog_edittext3, null);
        	 TextView tvName = (TextView) viewAP.findViewById( R.id.alert_textView1 );
        	 TextView tvPass = (TextView) viewAP.findViewById( R.id.alert_textView2 );
        	 Button btnScan = (Button) viewAP.findViewById( R.id.ButtonScan );
        	 btnScan.setVisibility( View.GONE );
        	 tvName.setText( getResources().getStringArray(R.array.system_mutilytester_set)[id-1] );
        	 tvPass.setText( getString( R.string.pass ) );
        	 editName = (EditText) viewAP.findViewById( R.id.alert_textEditText1 );
        	 final EditText editPass = (EditText) viewAP.findViewById( R.id.alert_textEditText2 );
        	 editName.setText( umpcModel.getWifiName().trim() );
        	 editPass.setText( umpcModel.getWifiPassword() );
        	
        	 final Spinner spinnerCipherMode = (Spinner) viewAP.findViewById( R.id.spinnerCipherMode );
        	 String[] cipherModes = getResources().getStringArray(R.array.system_apn_ciphermode);
        	 ArrayAdapter<String> adapter = new ArrayAdapter<String>(MutilyTesterSet.this, 
        			 R.layout.simple_spinner_custom_layout, cipherModes);
        	 adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        	 spinnerCipherMode.setAdapter(adapter);
        	 spinnerCipherMode.setSelection(umpcModel.getWifiCiphermode(), true);
        	 spinnerCipherMode.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					cipherPosition = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
        	 btnScan.setOnClickListener( new OnClickListener(){
				@Override
				public void onClick(View v) {
					if( alertDialog!=null ){
						if( alertDialog.isShowing() ){
							alertDialog.hide();
						}
					}
					new Thread( new ThreadWifiScanner() ).start();
				}
        	 });
        	 alertDialog =  new BasicDialog.Builder(MutilyTesterSet.this)
             .setIcon(android.R.drawable.ic_menu_edit)
             .setView( viewAP )
             .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
 					public void onClick(DialogInterface dialog, int which) {
 						if( editName.getText().toString().trim().equals("") ){
 							editName.setText( umpcModel.getWifiName() );
 							Toast.makeText(MutilyTesterSet.this.getApplicationContext(), 
 									MutilyTesterSet.this.getString(R.string.mutilytester_apname_fail), 
 									Toast.LENGTH_LONG).show();
 							
 						}else{
 							umpcModel.setWifiName(  editName.getText().toString().trim() );
 							umpcModel.setWifiPassword( editPass.getText().toString() );
 							umpcModel.setWifiCiphermode(cipherPosition);
 							configUmpc.writeCipherToLocal(cipherPosition);
 							configUmpc.writeToFile();
 							findView();
 						}
 					}
                
             })	
             .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 editName.setText( umpcModel.getWifiName() );
                	 editPass.setText( umpcModel.getWifiPassword() );
                 }
             })
             .create(); 
        	 return alertDialog;
        	 
    	case 2://弹出 "Umpc服务器IP地址"输入对话框 
    		final EditText serverip = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
    		serverip.setSelectAllOnFocus(true);
    		serverip.setText(umpcModel.getServerIp());
    		serverip.setKeyListener( new MyKeyListener().getIpKeyListener() );
    		 return new BasicDialog.Builder(MutilyTesterSet.this)
             .setIcon(android.R.drawable.ic_menu_edit)
             .setTitle( getResources().getStringArray(R.array.system_mutilytester_set)[id-1] )
             .setView(textEntryView)
             .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(serverip.getText().toString().equals("")||!verify.isIP(serverip.getText().toString()) ){
							serverip.setText(umpcModel.getServerIp());
							Toast.makeText(MutilyTesterSet.this.getApplicationContext(), 
									MutilyTesterSet.this.getString(R.string.mutilytester_serverip_faild), 
									Toast.LENGTH_LONG).show();
							MutilyTesterSet.this.showDialog(0);
						}else{
							umpcModel.setServerIp(serverip.getText().toString());
							configUmpc.writeToFile();
							findView();
						}
					}
                
             })	
             .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                      User clicked cancel so do some stuff 
                 }
             })
             .create(); */

    	/*case 3://弹出 "Umpc服务器端口号"输入对话框    	
    		final View view = factory.inflate(R.layout.alert_dialog_edittext_number, null);
        	final EditText serverport = (EditText) view.findViewById(R.id.alert_textEditText);
        	serverport.setSelectAllOnFocus(true);
        	serverport.setText(Integer.toString(umpcModel.getServerPort()));
       		return new BasicDialog.Builder(MutilyTesterSet.this)
                .setIcon(android.R.drawable.ic_menu_edit)
                .setTitle( getResources().getStringArray(R.array.system_mutilytester_set)[3] )
                .setView(view)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						if(serverport.getText().toString().equals("")||!verify.isPort(serverport.getText().toString()) ){
    							serverport.setText(Integer.toString(umpcModel.getServerPort()));
    							Toast.makeText(MutilyTesterSet.this.getApplicationContext(), 
    									MutilyTesterSet.this.getString(R.string.mutilytester_serverport_faild), 
    									Toast.LENGTH_LONG).show();
    						}else{
    							umpcModel.setServerPort(Integer.parseInt(serverport.getText().toString()));
    							configUmpc.writeToFile();
    							findView();
    						}
    					}
                   
                })	
                .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                         User clicked cancel so do some stuff 
                    }
                })
                .create();
   		
    	case 4://弹出 "连接服务器超时时间"输入对话框    
    		final View timeview = factory.inflate(R.layout.alert_dialog_edittext_number, null);
        	final EditText connecttime = (EditText) timeview.findViewById(R.id.alert_textEditText);
        	connecttime.setSelectAllOnFocus(true);
        	connecttime.setText(Integer.toString(umpcModel.getConnectTime()));
       		return new BasicDialog.Builder(MutilyTesterSet.this)
                .setIcon(android.R.drawable.ic_menu_edit)
                .setTitle( getResources().getStringArray(R.array.system_mutilytester_set)[4] )
                .setView(timeview)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						if(connecttime.getText().toString().equals("")||!verify.isInteger(connecttime.getText().toString()) ){
    							connecttime.setText(Integer.toString(umpcModel.getConnectTime()));
    							Toast.makeText(MutilyTesterSet.this.getApplicationContext(), 
    									MutilyTesterSet.this.getString(R.string.mutilytester_connecttime_faild), 
    									Toast.LENGTH_LONG).show();
    						}else{
    							umpcModel.setConnectTime(Integer.parseInt(connecttime.getText().toString()));
    							configUmpc.writeToFile();
    							findView();
    						}
    					}
                   
                })	
                .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // User clicked cancel so do some stuff
                    }
                })
                .create();*/
    	case 0://弹出 "手机端用户名"输入对话框    	
    		final EditText username = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
    		username.setText(umpcModel.getMobileName());
    		username.setSelectAllOnFocus(true);
    		 return new BasicDialog.Builder(MutilyTesterSet.this)
             .setIcon(android.R.drawable.ic_menu_edit)
             .setTitle( getResources().getStringArray(R.array.system_mutilytester_set)[id-1])
             .setView(textEntryView)
             .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						EditText editText = (EditText)textEntryView.findViewById(R.id.alert_textEditText);
						umpcModel.setMobileName(editText.getText().toString().trim());
						configUmpc.writeToFile();
						findView();//根据配置参数重新生成界面组件
					}
                
             })	
             .setNegativeButton(R.string.str_cancle)
             .create();
    	/*case 6://弹出 "手机端密码"输入对话框
    		final View passView = factory.inflate(R.layout.alert_dialog_edittext_pass, null);
    		final EditText mobilepass = (EditText)passView.findViewById(R.id.alert_textEditText);
    		mobilepass.setSelectAllOnFocus(true);
    		return new BasicDialog.Builder(MutilyTesterSet.this)
	        .setIcon(android.R.drawable.ic_menu_edit)
	        .setTitle( getResources().getStringArray(R.array.system_mutilytester_set)[6] )
	        .setView(passView)
	        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					umpcModel.setMobilePassword(mobilepass.getText().toString());
					configUmpc.writeToFile();
					
					//LogUtil.w(tag,"--netcfg result:"+getEth0IP());
					findView();//根据配置参数重新生成界面组件
				}
	        })	
	        .setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                // User clicked cancel so do some stuff 
	            }
	        })
	        .create();*/
    	case 1:
    		mHandler.obtainMessage( CHANGE_APN ).sendToTarget() ;
    		return null;
    	case 2:
    		/*Intent alarm = new Intent(MutilyTesterSet.this,SysAlarmDevice.class);
    		alarm.putExtra("AlarmType", WalkStruct.AppType.MutilyTester.name());
    		startActivity(alarm);*/
    		return null;
    		
    	case 3:
    		return new BasicDialog.Builder(MutilyTesterSet.this)
    		.setTitle(R.string.str_lock_script)
    		.setSingleChoiceItems( R.array.public_switch, ConfigRoutine.getInstance().canRunScript()?0:1, 
    				new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ConfigRoutine.getInstance().setCanRunScript( which==0 );
							findView();
							dialog.dismiss();
						}
					})
			.setNeutralButton( R.string.str_lock, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intentActivity = new Intent( "android.intent.action.MAIN"  );
		    		ComponentName componentName = new ComponentName(
		    				"com.android.settings",
		    				"com.android.settings.RadioInfo"
		    		);  
		    		intentActivity.setComponent(componentName);  
		    		intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    		intentActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清除
		    		startActivity( intentActivity );
		  	  		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			})
    		.create();
    	}//end switch
		return null;    	
    }//end  Dialog onCreateDialog
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		String umpcMsg = "";
		
		/*if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnecting)){
			umpcMsg = getString(R.string.mutilytester_umpc_connecting);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnected)){
			umpcMsg = getString(R.string.mutilytester_umpc_connected);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnectFaild)){
			umpcMsg = getString(R.string.mutilytester_umpc_connectfaild);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcLogined)){
			umpcMsg = getString(R.string.mutilytester_umpc_logined);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcLoginFaild)){
			umpcMsg = getString(R.string.mutilytester_umpc_loginfaild);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcDisconnecting)){
			umpcMsg = getString(R.string.mutilytester_umpc_disconnecting);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcDisconnected)){
			umpcMsg = getString(R.string.mutilytester_umpc_disconnected);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcServiceClose)){
			umpcMsg = getString(R.string.mutilytester_umpc_serverclosed);
		}else*/
		if(appModel.getUmpcStatus().equals(UMPCConnectStatus.ServereCreating)){
			umpcMsg = getString(R.string.mutilytester_umpc_serverCreatting);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.ServerCreated)){
			umpcMsg = getString(R.string.mutilytester_umpc_serverCred);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.TerminalConnected)){
			umpcMsg = getString(R.string.mutilytester_umpc_ipadconnected);
		}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.TerminalConnectFaild)){
			umpcMsg = getString(R.string.mutilytester_umpc_ipadconnectfaild);
		}else{
			umpcMsg = getString(R.string.mutilytester_umpc_Default);
		}
		
		menu.add(1,1,1,umpcMsg).setIcon(
				appModel.getUmpcStatus().equals(UMPCConnectStatus.TerminalConnected) ?
						R.drawable.stop_s : R.drawable.main_auto);
		menu.add(1,2,2,getString(R.string.mutilytester_umpc_stopTest))
			.setIcon(appModel.isTestJobIsRun() ? R.drawable.pause : R.drawable.pause_disabled)
			.setEnabled(appModel.isTestJobIsRun() ? true : false);
		return super.onPrepareOptionsMenu(menu);
	}
	
    @Override//菜单点击事件
	public boolean onOptionsItemSelected(MenuItem item){    	
		super.onOptionsItemSelected(item);
		LogUtil.w(tag,"--mutily Onclick:"+item.getItemId()+"--status:"+appModel.getUmpcStatus().name());
		if(item.getItemId() == 0){
			Intent wifiSetIntent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
			startActivity(wifiSetIntent);
		}else if(item.getItemId() == 1){
			//只有登陆成功状态下单击时关闭服务
			/*if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcLogined)){
				appModel.setUmpcStatus( UMPCConnectStatus.UmpcServiceClose );
				Intent interruptIntent = new Intent(WalkMessage.MutilyTester_Close_UmpcServer);
				sendBroadcast(interruptIntent);
				sendBroadcast(new Intent(WalkMessage.rcuFileUpToPinner));
			}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.Default)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcServiceClose)){
				LogUtil.w(tag,"--start UmpcServer--");
				sendBroadcast(new Intent(WalkMessage.MutilyTester_Start_UmpcServer));
				//.putExtra(UmpcTestInfo.controlKey, controlerService)
				//new Thread( new ThreadStatus() ).start();
			}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.WifiConnecting)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.WifiConnected)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnecting)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnected)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcDisconnecting)//){
				//连接正在进行中时不做任务动作
			//}else if(
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.WifiConnectFaild)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnectFaild)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcLoginFaild)
					||appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcDisconnected)){
				//操作失败后点击重新打开服务
				Intent interruptIntent = new Intent(WalkMessage.MutilyTester_ReDo_ConnectServer);
				//interruptIntent.putExtra(UmpcTestInfo.controlKey, controlerService);
				sendBroadcast(interruptIntent);
				appModel.setUmpcStatus( UMPCConnectStatus.Default );
				//new Thread( new ThreadStatus() ).start();
			}*/
			
			if(appModel.getUmpcStatus().equals(UMPCConnectStatus.Default)){
				appModel.setUmpcStatus(UMPCConnectStatus.ServereCreating);
				sendBroadcast(new Intent(WalkMessage.MutilyTester_Start_UmpcServer));
			}else{
				appModel.setUmpcStatus(UMPCConnectStatus.Default);
				Intent interruptIntent = new Intent(WalkMessage.MutilyTester_Close_UmpcServer);
				sendBroadcast(interruptIntent);
				
				sendBroadcast(new Intent(WalkMessage.rcuFileUpToPinner));
			}
		}else if(item.getItemId() == 2){
			LogUtil.w(tag,"---test stop---");
			Toast.makeText(this, getString(R.string.main_testStoping), Toast.LENGTH_LONG).show();
			Intent interruptIntent = new Intent(WalkMessage.Action_Walktour_Test_Interrupt);
			sendBroadcast(interruptIntent);
		}
		return true;
	}
    
    /**
  	 * 上下文菜单监听器
  	 * */
  	private OnCreateContextMenuListener contextMenuListener = new OnCreateContextMenuListener(){
		@Override
		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenuInfo contextMenuInfo) {
			menu.setHeaderTitle( getString(R.string.task_accepoint) );
			
			if( scanResultList != null ){
				for(ScanResult x: scanResultList){
					menu.add( x.SSID );
				}
			}
		}
  	};
  	
  	/**
  	 * 上下文菜单点击事件
  	 * */
 	@Override  
    public boolean onContextItemSelected(MenuItem item) {  
 		if( editName !=null ){
 			editName.setText( item.getTitle() );
 		}
 		if( alertDialog!=null ){
 			alertDialog.show();
 		}
 		LogUtil.i(tag, "---"+item.getTitle() );
 		return super.onContextItemSelected( item );
 	}
  	
  	/**
  	 * 选择Wifi接入点时扫描Wifi的线程
  	 * */
//  	private class ThreadWifiScanner implements Runnable {
//		@Override
//		public void run() {
//			//显示进度
//			mHandler.obtainMessage( WIFI_ON ).sendToTarget();
//			
//			//打开wifi
//			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//			do{
//				if( wifiManager.getWifiState()!=WifiManager.WIFI_STATE_DISABLING 
//						&& wifiManager.getWifiState()!= WifiManager.WIFI_STATE_ENABLING ){
//					wifiManager.setWifiEnabled( true );
//				}
//				try {
//					Thread.sleep( 500 );
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}while( wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED );
//			
//			//等到有扫描结果
//			for( int i =0;i<=20;i++){
//				wifiManager.startScan();
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				//扫描 wifi
//				scanResultList = wifiManager.getScanResults();
//				if( scanResultList != null){
//					if( scanResultList.size()>0 ){
//						//去掉相同ssid的信号
//						for(int m=0;m<scanResultList.size();m++){
//							String name = scanResultList.get(m).SSID;
//							LogUtil.w(tag, "AP's name:"+name);
//							try{
//								for( int n = scanResultList.size()-1;n>m;n-- ){
//									ScanResult result = scanResultList.get(n); 
//									if( result.SSID .equals( name )){
//										scanResultList.remove( result );
//									}
//								}
//							}catch(Exception e){
//								LogUtil.w(tag, "---ArrayOutException");
//							}
//						}
//						break;
//					}else{
//						LogUtil.w(tag,"wifi list's size is 0");				
//					}
//				}else{
//					LogUtil.w(tag,"wifi list is null");				
//				}
//			}
//			
//			//关闭wifi
//			do{
//				if( wifiManager.getWifiState()!=WifiManager.WIFI_STATE_DISABLING 
//						&& wifiManager.getWifiState()!= WifiManager.WIFI_STATE_ENABLING ){
//					wifiManager.setWifiEnabled( false );
//				}
//				try {
//					Thread.sleep( 500 );
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}while( wifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED  );
//			
//			// 关闭进度
//			mHandler.obtainMessage( WIFI_OFF ).sendToTarget() ; 
//		}
//  	}
  	
    /**
     * 读取状态并显示到进度框的线程
     * */
//    private class ThreadStatus implements Runnable {
//		@Override
//		public void run() {
//			showProgressOut = false;
//			showProgressBackTimes = 0;
//			int i = 0;
//			for(i=0;i<200 && !showProgressOut;i++){
//				String msg ="";
//				/*if(appModel.getUmpcStatus().equals(UMPCConnectStatus.WifiConnecting)){
//					msg = getString(R.string.mutilytester_wifi_connecting);
//					mHandler.obtainMessage( PROGRESS_ON, msg ).sendToTarget();
//				}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.WifiConnected)){
//					msg = getString(R.string.mutilytester_wifi_connected);
//					mHandler.obtainMessage( PROGRESS_ON, msg ).sendToTarget();
//				}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.WifiConnectFaild)){
//					msg = getString(R.string.mutilytester_wifi_connectfaild);
//					mHandler.obtainMessage( PROGRESS_ON, msg ).sendToTarget();
//					showProgressOut = true;
//				}else if(appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnecting)){
//					msg = getString(R.string.mutilytester_umpc_connecting);
//					mHandler.obtainMessage( PROGRESS_ON, msg ).sendToTarget();
//				}else if( appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnected) ){
//					msg = getString(R.string.mutilytester_umpc_connected);
//					mHandler.obtainMessage( PROGRESS_ON, msg );
//				}else if( appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcConnectFaild)){
//					//mHandler.sendEmptyMessage(REDO_BANDWIFI);	//发送重新绑定WIFI的消息
//					
//					msg = getString(R.string.mutilytester_umpc_connectfaild);
//					mHandler.obtainMessage( PROGRESS_ON, msg );
//					showProgressOut = true;
//				}else if( appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcLogined) ){
//					msg = getString(R.string.mutilytester_umpc_logined);
//					mHandler.obtainMessage( PROGRESS_ON, msg );
//					showProgressOut = true;
//				}else if( appModel.getUmpcStatus().equals(UMPCConnectStatus.UmpcLoginFaild)){
//					msg = getString(R.string.mutilytester_umpc_loginfaild);
//					mHandler.obtainMessage( PROGRESS_ON, msg );
//					showProgressOut = true;
//				}else*/ {
//					msg = getString(R.string.mutilytester_umpc_Default);
//					mHandler.obtainMessage( PROGRESS_ON, msg ).sendToTarget();
//				}
//				sleep( 1 );
//				LogUtil.w(tag,"--umpc state:"+appModel.getUmpcStatus().name()+"---i:"+i+"--showProgressOut:"+showProgressOut);
//			}
//			LogUtil.w(tag,"--umpc state end:"+appModel.getUmpcStatus().name()+"--i:"+i+"--showProgressBackTimes:"+showProgressBackTimes);
//			mHandler.obtainMessage( PROGRESS_OFF ).sendToTarget();
//			//当为连接超时退出进度显示框或连续点三次退出框时停止UMPC连接服务
//			if(i >= 200 || showProgressBackTimes >=3){
//				mHandler.obtainMessage(STOP_UMPCSERVICE).sendToTarget();
//			}
//		}
		
//		private void sleep( int second ){
//			try {
//				Thread.sleep( second * 1000 );
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//    }
    
    private Handler mHandler =new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		try{
	    		switch ( msg .what ){
	    		case PROGRESS_ON :
	    			if( progressDialog == null ){
	    				progressDialog = new ProgressDialog( MutilyTesterSet.this ){
	    					@Override
	    					public boolean onKeyDown(int keyCode, KeyEvent event) {
	    						if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
	    							//showProgressOut = true;
	    							showProgressBackTimes++;
	    				        	LogUtil.w(tag,"---ProgressDialog BACK---");
	    				        	if(showProgressBackTimes < 3)
	    				        		return true;
	    				        	else
	    				        		showProgressOut = true;
	    				        }
	    						return super.onKeyDown(keyCode, event);
	    					}
	    				};
	    			}
	    			progressDialog.setMessage( (String)msg.obj );
	    			progressDialog.show();
	    			break;
	    			
	    		case PROGRESS_OFF:
	    			if(progressDialog !=null ){
	    				progressDialog.dismiss();
	    			}
	    			progressDialog = null;
	    			break;
	    		case STOP_UMPCSERVICE:
	    			Intent interruptIntent = new Intent(WalkMessage.MutilyTester_Close_UmpcServer);
					sendBroadcast(interruptIntent);
	    			break;
	    		
	    		//打开wifi 
	    		case WIFI_ON:
	    			progressWifi = null;
	    			progressWifi = new ProgressDialog( MutilyTesterSet.this );
	    			progressWifi.setMessage( getString (R.string.str_scanning) );
	    			progressWifi.show();
	    			break;
	    		
	    		//关闭Wifi	
	    		case WIFI_OFF:
	    			if( progressWifi!=null ){
	    				progressWifi.dismiss();
	    				progressWifi = null;
	    			}	    			
	    			list.showContextMenu();
	    			break;
	    		case CHANGE_APN:
	    			isApnPointChange = true;
	        		Intent intent = new Intent(getApplicationContext(),SysRoutineAPNActivity.class);
	      			startActivity(intent);
	    			break;
	    		case REDO_BANDWIFI:
	    			LogUtil.w(tag,"--to send redo bandwifi--");
	    			/*Intent redoBandWifi = new Intent(WalkMessage.MutilyTester_ReDo_BandWifi);
	    			//MutilyTesterSet.this.sendBroadcast(redoBandWifi);
	    			getApplicationContext().sendBroadcast(redoBandWifi);*/
	    			break;
	    		}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    };
    
}

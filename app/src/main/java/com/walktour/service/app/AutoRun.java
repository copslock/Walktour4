
package com.walktour.service.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.gui.WalkTour;

public class AutoRun extends Service{
	
	//Logcat标签
	private final static String TAG = "com.walktour.service.app.AutoRun";
	private final static String ACTION = "walktour.runservice";
	
    //告警提示相关
    private NotificationManager notice_manager;//通知管理器 
    private final static int ALARM = 1;//Message.what
    private final static int NOALARM = 0;
    private static boolean hasAlarm = false;
    private String obj =" ";//通知传递的对象
    
    //广播接收对象
	private String msg="";  
    private UpdateReceiver receiver;  
    
//---重载方法-----------------------------------------------------------------------------
    @Override  
    public void onCreate() {  
    	createBroacastReceiver();
    }  
  
    @Override
    @SuppressWarnings("deprecation")
    public void onStart(Intent intent, int startId) {
        LogUtil.i(TAG, "------------->Alarm.onStart");  
    }  
  
    @Override  
    public void onDestroy() {  
        LogUtil.i(TAG, "-------------> Alarm.onDestroy");
    }  
    
   @Override  
    public IBinder onBind(Intent i) {  
        return null;  
    }  

//---私有方法-----------------------------------------------------------------------------
   
    //方法：创建广播接收器
	private void createBroacastReceiver() {//注：此方法也可以在Androidmanifest.xml文件中指定
		receiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction( ACTION );
		filter.addAction(Intent.ACTION_BOOT_COMPLETED);
		this.registerReceiver(receiver, filter);
	}
	
	//对象：广播接收器
    public class UpdateReceiver extends BroadcastReceiver{  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            //msg = intent.getStringExtra("msg");  
            //text_view.append(msg + "\n");  
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ){
            	showNotification("Start Walktour");
            }//end if
        }  
    }//end class UpdateReceiver
	
    //方法：发送广播
    private void sendMyBroadCast(String msg){  
        // 指定广播目标的 action （注：指定了此 action 的 receiver 会接收此广播）  
        Intent intent = new Intent(ACTION);  
        // 需要传递的参数  
        intent.putExtra("msg", msg);  
        // 发送广播  
        this.sendBroadcast(intent);  
    } 
   
   //方法：显示通知
   private void showNotification(String alarm_content) {  
       Notification.Builder notification = new Notification.Builder(this);
       notification.setSmallIcon(R.mipmap.walktour);
       notification.setWhen(System.currentTimeMillis());
       //点击通知跳转页面
       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,  
               new Intent(this, WalkTour.class), 0);
       notification.setContentIntent(contentIntent);
       notification.setContentTitle(getString(R.string.sys_alarm));
       notification.setContentText(alarm_content);

       notice_manager.notify(R.string.service_started, notification.build());
   } 
   
   	//对象：消息接收器
	class AlarmHandler extends Handler{
		public AlarmHandler (Looper l){
			super(l);
		}
		
		public AlarmHandler(){
			super();
		}
		
		@Override//接收消息
		public void handleMessage(Message msg){
			if(msg.what==ALARM){
				notice_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
		        showNotification( (String)msg.obj );  
			}
		}
	}//end class AlarmHandler 
	
}
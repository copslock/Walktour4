package com.walktour.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.walktour.gui.map.GenericData;
import com.walktour.gui.map.GenericPara;

import java.util.Timer;
import java.util.TimerTask;

public class DataService extends Service{

	public static final String DataUpdate = "DataUpdate";
	private Timer timer = new Timer();
	private TimerTask task = null;
	//private Vector<GenericData> data = new Vector<GenericData>();
    public  static  GenericData data = new GenericData();
    private String data2="";
    
    public String para1 = "FtpDlRate";
	public String para2 = "FTP DL RATE";
	public String para3 = "SIR";
	public String para4 = "CellID_W";
    

    private void addqueue()
    {
    	GenericPara tmppara;
    	data.ClearCurrentPara();
    	for(int i= 0; i< data.getTableParaSize();i++)
		 {
			 tmppara = data.getTablePara(i);
			 if (para1.equalsIgnoreCase(tmppara.paraname))
			 {
				 data.addCurrentPara(tmppara);
				 if (data.getPara_Queue1().size()<21)
				 {
					 data.addPara_Queue1(tmppara);
				 }
				 else
				 {
					 data.removePara_Queue1();
					 data.addPara_Queue1(tmppara);
				 }
			 }
			 if (para2.equalsIgnoreCase(tmppara.paraname))
			 {
				 data.addCurrentPara(tmppara);
				 if (data.getPara_Queue2().size()<21)
				 {
					 data.addPara_Queue2(tmppara);
				 }
				 else
				 {
					 data.removePara_Queue2();
					 data.addPara_Queue2(tmppara);
				 }
				 
			 }
			 if (para3.equalsIgnoreCase(tmppara.paraname))
			 {
				 data.addCurrentPara(tmppara);
				 if (data.getPara_Queue3().size()<21)
				 {
					 data.addPara_Queue3(tmppara);
				 }
				 else
				 {
					 data.removePara_Queue3();
					 data.addPara_Queue3(tmppara);
				 }
				 
			 }
			 if (para4.equalsIgnoreCase(tmppara.paraname))
			 {
				 data.addCurrentPara(tmppara);
				 if (data.getPara_Queue4().size()<21)
				 {
					 data.addPara_Queue4(tmppara);
				 }
				 else
				 {
					 data.removePara_Queue4();
					 data.addPara_Queue4(tmppara);
				 }
			 }
		 }
    }
    private void GetData()
    {
    	Clear();
    	GenericPara para = new GenericPara();
    	para.paraname = "FtpDlRate";
    	para.value = String.valueOf(Double.valueOf(Math.random() * 100).intValue());
    	data.addTablePara(para);
    	/*GenericPara para1 = new GenericPara();
    	para1.paraname = "FTP DL RATE";
    	para1.value = String.valueOf(Double.valueOf(Math.random() * 100).intValue());
    	data.addTablePara(para1);
    	GenericPara para2 = new GenericPara();
    	para2.paraname = "SIR";
    	para2.value = String.valueOf(Double.valueOf(Math.random() * 1000).intValue());
    	data.addTablePara(para2);
    	GenericPara para3 = new GenericPara();
    	para3.paraname = "CellID_W";
    	para3.value = String.valueOf(Double.valueOf(Math.random() * 10).intValue());
    	data.addTablePara(para3);*/
    }
    private void Clear()
    {
    	data.clearTablePara();
    }
    private void SendIntent()
    {
        	//GetData();
        	addqueue();
        	Intent intent= new Intent(); 
			intent.setAction(DataUpdate); 			
			intent.putExtra("data", data2);
			sendBroadcast(intent);
			
    } 
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	public void onCreate()
	{
		super.onCreate();
		task = new TimerTask() { 
	    @Override 
	    public void run() 
	    { 
	          SendIntent();
	    } 
	    }; 
	    timer.schedule(task,500, 500); 

	}
	public void onDestroy()
	{
		super.onDestroy();
		data.clearPara_Queue1();
		data.clearPara_Queue2();
		data.clearPara_Queue3();
		data.clearPara_Queue4(); 
		if (task!=null)
		{
			task.cancel();
			task = null;
		}
		timer = null;
		
	}

	public class LocalBinder extends Binder {
		public DataService getService() {
		return DataService.this;
		}
		}

	

}

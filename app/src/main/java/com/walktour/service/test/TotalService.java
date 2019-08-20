package com.walktour.service.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * 统计报表独立线程 
 * @author zhihui.lian
 */
public class TotalService extends Service  {
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mServiceBinder;
	}
	
	//绑定Binder
	private ServiceBinder mServiceBinder = new ServiceBinder();
	
	
	public class ServiceBinder extends Binder implements ITotalService{

		@Override
		public void initLib() {
		}

		@Override
		public void requestL1Report() {
		}

		@Override
		public int requestL2Report() {
			return 0;
		}

		@Override
		public void freeLib() {
			
		}
		
		
	}
	
	//抽象类执行接口
	public interface ITotalService {
		//初始化
		public abstract void initLib(); 
		
		//执行一级请求
		public abstract void requestL1Report();
		
		//执行二级请求
		public abstract int requestL2Report();
		
		//报表导出
		public abstract void freeLib();
		
		
	}
	

	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	


}
package com.walktour.Utils;

import android.content.Context;
import android.os.PowerManager;

import com.walktour.base.util.LogUtil;

@SuppressWarnings("deprecation")
public class AlertWakeLock {
	private static String tag ="AlertWakeLock"; 
	private static PowerManager.WakeLock sWakeLock;
	public static void acquire(Context context) {
        LogUtil.v(tag,"Acquiring wake lock");
        try{
	        if (sWakeLock != null) {
	            sWakeLock.release();
	        }
	
	        PowerManager pm =
	                (PowerManager) context.getSystemService(Context.POWER_SERVICE);

	        sWakeLock = pm.newWakeLock(
	                PowerManager.FULL_WAKE_LOCK |
	                PowerManager.ACQUIRE_CAUSES_WAKEUP |
	                PowerManager.ON_AFTER_RELEASE, tag);
	        
	        sWakeLock.acquire();
        }catch(Exception e){
        	e.printStackTrace();
        }
    }

    public static void release() {
        LogUtil.v(tag,"Releasing wake lock");
        try{
	        if (sWakeLock != null) {
	            sWakeLock.release();
	            sWakeLock = null;
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
}

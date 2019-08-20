package com.walktour.workorder;

import android.content.DialogInterface;
import android.os.Bundle;

import com.walktour.framework.ui.BasicActivity;

import java.lang.reflect.Field;




public abstract class WorkOrderBasic extends BasicActivity{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
    
    protected void dismissDialog(DialogInterface dialog, boolean isShow){
		try {  
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
            field.setAccessible(true);  
            field.set(dialog, isShow);  
            dialog.dismiss();
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
	}
}

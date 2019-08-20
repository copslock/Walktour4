
package com.walktour.framework.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkMessage;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
*
*控制ftp群组，在测试进行时的控制
*	lzh
 */
@SuppressWarnings("deprecation")
public class GroupBasicActivity extends BasicActivity{
    
    
	
	
	private TextView saveTV;
	private TextView cancleTV;
	private boolean isJobRun;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	 
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		regeditBroadcast();
	}
	/**
     * 注册广播接收器
     */
    protected void regeditBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
        filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
        this.registerReceiver(mBroadcastReceiver, filter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mBroadcastReceiver);
    }
    
    
    @Override
	protected void onResume() {
		super.onResume();
		initBaseTask();
	}
	/**
     * 增加公共基类方法，供子类任务调用。主要操作正在时，任务模板保存按钮不可用
     */
    public void initBaseTask() {
        isJobRun = appModel.isTestJobIsRun();
        saveTV = initTextView(R.id.btn_ok);
        saveTV.setEnabled(isJobRun ? false : true);
        saveTV.setTextColor(isJobRun ? getResources().getColor(R.color.app_click_disable_grey_color) :
			getResources().getColor(R.color.app_main_text_color));
        cancleTV = initTextView(R.id.btn_cencle);
        cancleTV.setEnabled(isJobRun ? false : true);
        cancleTV.setTextColor(isJobRun ? getResources().getColor(R.color.app_click_disable_grey_color) :
			getResources().getColor(R.color.app_main_text_color));
        getAllChildViews();
    }
    
    /**
     * 
     * @获取页面的所有控件，判断控件的类型
     */
    public List<View> getAllChildViews() {
        View view = this.getWindow().getDecorView();
        return getAllChildViews(view);
    }
    
    private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild instanceof EditText) {
                    ((EditText) viewchild).setEnabled(isJobRun ? false : true);
                }
                if (viewchild instanceof BasicSpinner) {
                    ((BasicSpinner) viewchild).setEnabled(isJobRun ? false
                            : true);
                }
                if (viewchild instanceof Button) {
                    if (((Button) viewchild).getId() == R.id.btn_download
                            		|| ((Button) viewchild).getId() == R.id.btn_upload
                            || ((Button) viewchild).getId() == R.id.btn_default) {
                        ((Button) viewchild).setEnabled(isJobRun ? false : true);
                        ((Button) viewchild).setTextColor(isJobRun ? getResources().getColor(R.color.gray)
                                : getResources().getColor(R.color.app_main_text_color));
                    }
                }
                if (viewchild instanceof CheckBox) {
                    if (((CheckBox) viewchild).getId() == R.id.server_ck
                    		|| ((CheckBox) viewchild).getId() == R.id.savefile_ck
                    		) {
                        ((CheckBox) viewchild).setEnabled(isJobRun ? false : true);
                    }
                }
                
                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }
    
    
    /**
     * 广播接收器:接收来广播更新界面
     * */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WalkMessage.NOTIFY_TESTJOBDONE)
                    || intent.getAction()
                            .equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
                initBaseTask();
            }
        }
        
    };
}

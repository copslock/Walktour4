package com.walktour.gui.task.activity.scannertsma.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.control.adapter.ScanEventAdapter;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;

/**
 * 扫频仪自定义列表事件
 * @author jinfeng.xie
 */
public class ScanEvent extends BasicActivity implements RefreshEventListener{

	private ListView lvSimpleList;
	
    private ScanEventAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanevent_list);
		RefreshEventManager.addRefreshListener(this);
		initView();
	}
	
	private void initView(){
		lvSimpleList = (ListView) findViewById(R.id.ListView01);
		adapter = new ScanEventAdapter(getApplicationContext());
		lvSimpleList.setAdapter(adapter);
	}
	
	
	 
   
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	RefreshEventManager.removeRefreshListener(this);
    }

    

	@Override
	public void onRefreshed(RefreshType refreshType, Object object) {
		switch (refreshType) {
        case ACTION_WALKTOUR_TIMER_CHANGED:
            if(!ApplicationModel.getInstance().isFreezeScreen()){
            	adapter.getDataList().clear();
            	adapter.getDataList().addAll(TraceInfoInterface.traceData.scanEventList);
    			adapter.notifyDataSetChanged();
            }
            break;
		default:
			break;
		}
	}
	
}

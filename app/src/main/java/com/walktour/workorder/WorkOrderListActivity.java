package com.walktour.workorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dinglicom.UnicomInterface;
import com.walktour.gui.R;
import com.walktour.workorder.bll.InitServer;
import com.walktour.workorder.bll.ManipulateWorkOrderList;
import com.walktour.workorder.bll.ManipulateWorkOrderList.OnManipulateListListener;
import com.walktour.workorder.model.WorkOrderList;
import com.walktour.workorder.model.WorkOrderList.WorkOrderInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工单列表页面
 * Author: ZhengLei
 *   Date: 2013-6-7 下午6:43:10
 */
public class WorkOrderListActivity extends WorkOrderBaseActivity implements OnManipulateListListener , OnClickListener {
	private static final String TAG = "WorkOrderListActivity";
	public static final String KEY_WORK_ID = "WorkId";
	public static final String KEY_WORK_NAME = "WorkName";
	public static final int OBTAIN_DATA_OK = 1001;
	public static final int OBTAIN_DATA_FAILURE = 1002;
	public static final int EXECUTE_TIMEOUT = 1003;
	public static final int DEFAULT_TIMEOUT = 60; // 同步工单列表超时时间为60秒
	
	private Button btnGetWorkOrderList = null;
	private ListView listWorkOrder = null;
	
	private int workOrderType;
	private ManipulateWorkOrderList maniList = null;
	private List<WorkOrderInfo> workOrderInfos = new ArrayList<WorkOrderInfo>();
	private WorkOrderAdapter mAdapter;
	private boolean isSynchFinished = false; // 和服务器交互的调用jni的函数是否都执行完了
	private Button updateOrder;
	private ImageView ivDeleteText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.work_order_list);
		findView();
		getIntentData();
		
		maniList = new ManipulateWorkOrderList(this, this.workOrderType);
		maniList.addListener(this);
		
		loadData();
		fillListView();
		ivDeleteText = (initImageView(R.id.ivDeleteText));
		ivDeleteText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(WorkOrderListActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
				(initEditText(R.id.search_content_edit)).setText("");
			}
		});
		(initEditText(R.id.search_content_edit)).addTextChangedListener(textWatcher);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//UnicomInterface.free();
	}

	private void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.work_order_list);
		btnGetWorkOrderList = initButton(R.id.btn_get_work_order_list);
		listWorkOrder = (ListView)findViewById(R.id.list_work_order);
		listWorkOrder.setOnItemClickListener(itemClickListener);
		findViewById(R.id.pointer).setOnClickListener(clickListener);
		btnGetWorkOrderList.setOnClickListener(clickListener);
		(initButton(R.id.freeze_btn)).setVisibility(View.GONE);
		updateOrder = initButton(R.id.capture_btn);
		updateOrder.setText(R.string.update_order_str);
		updateOrder.setOnClickListener(this);
	}

	private void loadData() {
		WorkOrderList workOrderList = (WorkOrderList)maniList.load();
		if(workOrderList != null) {
			this.workOrderInfos = workOrderList.getWorkOrderInfos();
			Log.i(TAG, "work order size:" + workOrderInfos.size());
		}
	}
	
	private void fillListView() {
		List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
		for(WorkOrderInfo info : this.workOrderInfos) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_WORK_ID, info.getWorkId()+"");
			map.put(KEY_WORK_NAME, info.getWorkName());
			listData.add(map);
		}
		mAdapter = new WorkOrderAdapter(WorkOrderListActivity.this,this.workOrderInfos);
		listWorkOrder.setAdapter(mAdapter);
	}
	
	private void getIntentData() {
		Intent intent = getIntent();
		this.workOrderType = intent.getIntExtra(WorkOrderDictActivity.KEY_WORK_ORDER_TYPE, 0);
	}
	
	/**
	 * 点击View的监听器
	 */
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.pointer:
					finish();
					break;
				case R.id.btn_get_work_order_list:
					// 如果没有初始化服务器信息，则先初始化。否则，直接同步工单列表
					if(!InitServer.hasInit()) {
						initServer(); // 调用父类的初始化方法
					} else {
						synchWorkOrderList();
					}
					break;
	
				default:
					break;
			}
		}
	};
	
	
    /**
     * 监听数据
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            
        }
        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            
        }
        
        @Override
        public void afterTextChanged(Editable s) {
        	if( s.length() == 0 ){
        		ivDeleteText.setVisibility(View.GONE);
        	}else{
        		ivDeleteText.setVisibility(View.VISIBLE);
        	}
        	mAdapter.getFilter().filter(s.toString());
        }
    };
	
	
	
	/**
	 * 点击ListView的Item监听器
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			List<WorkOrderInfo> workOrderInfoList = mAdapter.getFilteredBaseDatas();
			int workId = workOrderInfoList.get(position).getWorkId();
			Intent intent = new Intent(WorkOrderListActivity.this, WorkOrderDetailActivity.class);
			intent.putExtra(KEY_WORK_ID, workId);
			startActivity(intent);
		}

	};
	
	
	
	
	
	private void synchWorkOrderList() {
		showProgressDialog(getString(R.string.getting_work_order_list), false);
		new Thread(new SynchData()).start();
		new Thread(new MonitorTimeout(DEFAULT_TIMEOUT)).start(); // 监视同步的超时
	}
	
	@Override
	protected void handleInitOk() {
		// 初始化服务器失败，则提示。否则，开始获取工单字典的线程
		if(!InitServer.hasInit()) {
			Toast.makeText(WorkOrderListActivity.this, R.string.init_server_failed, Toast.LENGTH_LONG).show();
		} else {
			synchWorkOrderList();
		}
	}

	@Override
	public void onStartLoad() {
		
	}

	@Override
	public void onStartSynchronize() {
		
	}

	@Override
	public void onEndLoad(WorkOrderList workOrderList) {
		
	}
	
	@Override
	public void onEndSynchronize(WorkOrderList workOrderList) {
		this.isSynchFinished = true;
		Message msg = null;
		if(workOrderList != null) {
			this.workOrderInfos = workOrderList.getWorkOrderInfos();
			msg = mHandler.obtainMessage(OBTAIN_DATA_OK);
		} else {
			msg = mHandler.obtainMessage(OBTAIN_DATA_FAILURE);
		}
		msg.sendToTarget();
	}
	
	private class SynchData implements Runnable {

		@Override
		public void run() {
			isSynchFinished = false;
			maniList.synchronize();
		}
		
	}

	private class MonitorTimeout implements Runnable {
		private int timeout;

		public MonitorTimeout(int timeout) {
			this.timeout = timeout;
		}

		@Override
		public void run() {
			int counter = timeout * 1000 / 50;
			for(int i=1; i<=counter&&!isSynchFinished; i++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// 如果超时了还没执行完，则断开连接
			if(!isSynchFinished) {
				UnicomInterface.disconnect();
				Message msg = mHandler.obtainMessage(EXECUTE_TIMEOUT);
				msg.sendToTarget();
			}
			isSynchFinished = false;
		}
		
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismissProgressDialog();
			switch (msg.what) {
				case OBTAIN_DATA_OK:
					fillListView();
					break;
				case OBTAIN_DATA_FAILURE:
					Toast.makeText(WorkOrderListActivity.this, R.string.get_work_order_list_failure, Toast.LENGTH_LONG).show();
					break;
				case EXECUTE_TIMEOUT:
					Toast.makeText(WorkOrderListActivity.this, R.string.connect_timeout, Toast.LENGTH_LONG).show();
				default:
					break;
			}
		}
		
	};
	
	
	
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.capture_btn:
			// 如果没有初始化服务器信息，则先初始化。否则，直接同步工单列表
			if(!InitServer.hasInit()) {
				initServer(); // 调用父类的初始化方法
			} else {
				synchWorkOrderList();
			}
			break;

		default:
			break;
		}
	}
	
}

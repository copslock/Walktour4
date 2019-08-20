package com.walktour.workorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dinglicom.UnicomInterface;
import com.walktour.gui.R;
import com.walktour.workorder.bll.InitServer;
import com.walktour.workorder.bll.ManipulateWorkOrderDict;
import com.walktour.workorder.bll.ManipulateWorkOrderDict.OnManipulateDictListener;
import com.walktour.workorder.model.WorkOrderDict;
import com.walktour.workorder.model.WorkOrderDict.WorkType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工单类型（字典）页面
 * Author: ZhengLei
 *   Date: 2013-6-18 下午3:56:17
 */
public class WorkOrderDictActivity extends WorkOrderBaseActivity implements OnManipulateDictListener {
	public static final int OBTAIN_DATA_OK = 1001;
	public static final int OBTAIN_DATA_FAILURE = 1002;
	public static final int EXECUTE_TIMEOUT = 1003;
	public static final String KEY_CODE_ID = "CodeId";
	public static final String KEY_CN_NAME = "CnName";
	public static final String KEY_COUNT = "Count";
	public static final String KEY_WORK_ORDER_TYPE = "WorkOrderType";
	public static final int DEFAULT_TIMEOUT = 60; // 默认超时时长为60秒
	
	private Button btnGetWorkOrderDict = null;
	private ListView listWorkOrderDict = null;
	private SimpleAdapter mAdapter;
	
	private ManipulateWorkOrderDict maniDict = null;
	private List<WorkType> workTypes = new ArrayList<WorkType>();
	private boolean isSynchFinished = false; // 和服务器交互的调用jni的函数是否都执行完了
	private Button updateOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.work_order_dict);
		findView();
		
		maniDict = new ManipulateWorkOrderDict(this);
		maniDict.addListener(this);
		
		loadData();
		fillListView();
	}

	public void findView() {
		(initTextView(R.id.title_txt)).setText(R.string.work_order_dict);
		btnGetWorkOrderDict = initButton(R.id.btn_get_work_order_dict);
		listWorkOrderDict = (ListView)findViewById(R.id.list_work_dict);
		listWorkOrderDict.setOnItemClickListener(itemClickListener);
		findViewById(R.id.pointer).setOnClickListener(buttonClickListener);
		btnGetWorkOrderDict.setOnClickListener(buttonClickListener);
		(initButton(R.id.freeze_btn)).setVisibility(View.GONE);
		updateOrder = initButton(R.id.capture_btn);
		updateOrder.setText(R.string.update_order_str);
		updateOrder.setOnClickListener(buttonClickListener);
		
	}
	
	private void loadData() {
		WorkOrderDict dict = (WorkOrderDict)maniDict.load();
		if(dict != null) {
			this.workTypes = dict.getWorkTypes();
		}
	}
	
	/**
	 * 填充ListView的数据，有刷新ListView的功能，不用notifyDataSetChanged方法，因为该方法适合List.add相关的操作
	 */
	private void fillListView() {
		List<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
		for(WorkType type : this.workTypes) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_CODE_ID, type.getCodeId()+"");
			map.put(KEY_CN_NAME, type.getCnName());
			// 显示工单个数
			int count = type.getCount();
			String info = count==0 ? getString(R.string.workorder_empty) : (count+getString(R.string.workorder_count));
			map.put(KEY_COUNT, info);
			listData.add(map);
		}
		mAdapter = new SimpleAdapter(this, listData, R.layout.listview_item_work_order_list, new String[]{KEY_CODE_ID, KEY_CN_NAME, KEY_COUNT}, new int[]{R.id.txt_title, R.id.txt_content, R.id.txt_count});
		listWorkOrderDict.setAdapter(mAdapter);
	}
	
	/**
	 * 点击View的监听器
	 */
	private View.OnClickListener buttonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.pointer:
					finish();
					break;
				case R.id.btn_get_work_order_dict:
					
					break;
					
				case R.id.capture_btn:
					// 如果没有初始化服务器信息，则先初始化。否则，直接同步工单字典
					if(!InitServer.hasInit()) {
						initServer(); // 调用父类的初始化方法
					} else {
						synchWorkOrderDict();
					}
					break;
	
				default:
					break;
			}
		}
	};
	
	/**
	 * 点击ListView的Item监听器
	 */
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			int codeId = workTypes.get(position).getCodeId();
			Intent intent = new Intent(WorkOrderDictActivity.this, WorkOrderListActivity.class);
			intent.putExtra(KEY_WORK_ORDER_TYPE, codeId);
			startActivity(intent);
		}

	};
	
	/**
	 * 获取工单字典的线程
	 */
	private void synchWorkOrderDict() {
		showProgressDialog(getString(R.string.getting_work_order_dict), false);
		new Thread(new SynchData()).start();
		new Thread(new MonitorTimeout(DEFAULT_TIMEOUT)).start(); // 监视同步的超时
	}
	
	/**
	 * 同步数据线程
	 */
	private class SynchData implements Runnable {

		@Override
		public void run() {
			isSynchFinished = false;
			maniDict.synchronize();
		}
		
	}

	@Override
	protected void handleInitOk() {
		// 初始化服务器失败，则提示。否则，开始获取工单字典的线程
		if(!InitServer.hasInit()) {
			Toast.makeText(WorkOrderDictActivity.this, R.string.init_server_failed, Toast.LENGTH_LONG).show();
		} else {
			synchWorkOrderDict();
		}
	}

	@Override
	public void onStartLoad() {
		
	}

	@Override
	public void onEndLoad(WorkOrderDict dict) {
		
	}

	@Override
	public void onStartSynchronize() {
		
	}

	@Override
	public void onEndSynchronize(WorkOrderDict dict) {
		this.isSynchFinished = true;
		Message msg = null;
		if(dict != null) {
			this.workTypes = dict.getWorkTypes();
			msg = mHandler.obtainMessage(OBTAIN_DATA_OK);
		} else {
			msg = mHandler.obtainMessage(OBTAIN_DATA_FAILURE);
		}
		msg.sendToTarget();
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
					Toast.makeText(WorkOrderDictActivity.this, R.string.get_work_order_dict_failure, Toast.LENGTH_LONG).show();
					break;
				case EXECUTE_TIMEOUT:
					Toast.makeText(WorkOrderDictActivity.this, R.string.connect_timeout, Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}
		
	};

	/**
	 * 监视同步工单字典的操作是否超时
	 */
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

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	
	
	@Override
	protected void onDestroy() {
		if(InitServer.hasInit()){
			UnicomInterface.disconnect();
			UnicomInterface.free();
			InitServer.setInit(false);
		}
		super.onDestroy();
	}
	
	
}

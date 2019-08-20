package com.walktour.gui.workorder.ah;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.workorder.ah.model.WorkOrder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 安徽电信工单项目主界面->工单列表
 * 
 * @author jianchao.wang
 * 
 */
public class WorkOrderMainActivity extends BasicActivity implements OnClickListener, OnItemClickListener {
	/** 日志标识 */
	public final static String TAG = "WorkOrderMainActivity";
	/** 传递的工单编号参数名 */
	public final static String EXTRA_ORDRE_NO = "order_no";
	/** 传递的信息点编号参数名 */
	public final static String EXTRA_POINT_NO = "point_no";
	/** 下载结束标识 */
	private static final int DOWNLOAD_END = 12;
	/** 下载失败标识 */
	private static final int DOWNLOAD_FAIL = 16;
	/** 工单列表 */
	private ListView workOrderList;
	/** 工单列表 */
	private List<WorkOrder> orderList = new ArrayList<WorkOrder>();
	/** 过滤的工单列表 */
	private List<WorkOrder> filterList = new ArrayList<WorkOrder>();
	/** 检索文本 */
	private EditText searchText;
	/** 工单列表适配类 */
	private OrderArrayAdapter ordersAdapter;
	/** 服务器管理类 */
	private ServerManager mServer;
	/** 下载进度条 */
	private ProgressDialog progress;
	/** 处理删除过程 */
	private Handler handler = new MyHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mServer = ServerManager.getInstance(WorkOrderMainActivity.this);
		setContentView(R.layout.work_order_ah_main_list);
		findView();
		initValue();
	}

	/**
	 * 视图关联设置
	 */
	private void findView() {
		this.findViewById(R.id.update_btn).setOnClickListener(this);
		this.findViewById(R.id.pointer).setOnClickListener(this);
		this.searchText = (EditText) this.findViewById(R.id.search_content_edit);
		this.searchText.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				filterOrder();
			}

		});
		this.workOrderList = (ListView) this.findViewById(R.id.workOrderList);
		this.workOrderList.setOnItemClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initValue() {
		WorkOrderFactory.getInstance().init();
		this.orderList = WorkOrderFactory.getInstance().getOrderList();
		this.filterList.addAll(this.orderList);
		this.ordersAdapter = new OrderArrayAdapter(this.getApplicationContext(), R.layout.work_order_ah_main_row,
				this.filterList);
		this.workOrderList.setAdapter(ordersAdapter);
		this.filterOrder();
	}

	/**
	 * 过滤工单
	 */
	private void filterOrder() {
		String keyword = this.searchText.getText().toString().trim();
		this.filterList.clear();
		if (keyword.length() > 0) {
			for (WorkOrder order : this.orderList) {
				if (order.getWorkItemCode().indexOf(keyword) >= 0 || order.getName().indexOf(keyword) >= 0)
					this.filterList.add(order);
			}
		} else {
			this.filterList.addAll(this.orderList);
		}
		this.ordersAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_btn:
			updateOrderList();
			break;
		case R.id.pointer:
			this.finish();
			break;
		}

	}

	/**
	 * 工单列表适配类
	 * 
	 * @author jianchao.wang 2014年6月13日
	 */
	private class OrderArrayAdapter extends ArrayAdapter<WorkOrder> {
		/** 资源ID */
		private int resourceId;

		public OrderArrayAdapter(Context context, int textViewResourceId, List<WorkOrder> objectList) {
			super(context, textViewResourceId, objectList);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
				view = vi.inflate(resourceId, null, true);
			}
			String keyword = searchText.getText().toString().trim();
			String no = this.getItem(position).getWorkItemCode();
			SpannableStringBuilder noStyle = new SpannableStringBuilder(no);
			int start = no.indexOf(keyword);
			if (keyword.length() > 0 && start >= 0) {
				noStyle.setSpan(new ForegroundColorSpan(Color.RED), start, start + keyword.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			((TextView) view.findViewById(R.id.order_no)).setText(noStyle);
			String name = this.getItem(position).getName();
			SpannableStringBuilder nameStyle = new SpannableStringBuilder(name);
			start = name.indexOf(keyword);
			if (keyword.length() > 0 && start >= 0) {
				nameStyle.setSpan(new ForegroundColorSpan(Color.RED), start, start + keyword.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			((TextView) view.findViewById(R.id.order_name)).setText(nameStyle);
			return view;
		}
	}

	/**
	 * 从服务器端下载工单任务
	 */
	private void updateOrderList() {
		String ip = this.mServer.getDownloadFleetIp();
		if (!this.mServer.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
			Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_ip_null), Toast.LENGTH_SHORT).show();
			return;
		}
		String account = this.mServer.getFleetAccount();
		if (account == null || account.length() == 0) {
			Toast.makeText(getApplicationContext(), getString(R.string.work_order_fleet_account_null), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (!MyPhoneState.getInstance().isNetworkAvirable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_speech_neterr), Toast.LENGTH_SHORT).show();
			return;
		}
		this.progress = ProgressDialog.show(WorkOrderMainActivity.this, getString(R.string.work_order_download),
				getString(R.string.work_order_download_message), true);
		new DownloadThread().start();
	}

	private static class MyHandler extends Handler {
		WeakReference<WorkOrderMainActivity> reference;

		public MyHandler(WorkOrderMainActivity activity) {
			reference = new WeakReference<WorkOrderMainActivity>(activity);
		}

		public void handleMessage(android.os.Message msg) {
			WorkOrderMainActivity activity = reference.get();

			switch (msg.what) {
			// 下载结束
			case DOWNLOAD_END:
				activity.progress.dismiss();
				Toast.makeText(activity, activity.getString(R.string.work_order_download_success), Toast.LENGTH_SHORT).show();
				activity.filterOrder();
				break;
			case DOWNLOAD_FAIL:
				activity.progress.dismiss();
				Toast.makeText(activity, activity.getString(R.string.work_order_download_fail), Toast.LENGTH_SHORT).show();
				break;
			}
		};
	}

	/**
	 * 下载线程
	 * 
	 * @author jianchao.wang 2014年6月20日
	 */
	private class DownloadThread extends Thread {
		@Override
		public void run() {
			String ip = mServer.getDownloadFleetIp();
			int port = mServer.getDownloadFleetPort();
			String account = mServer.getFleetAccount();
			StringBuilder http = new StringBuilder();
			http.append("http://").append(ip).append(":").append(port);
			http.append("/Services/CQTTestWFItemService.svc/GetCQTTestWFItemList/").append(account);
			LogUtil.d(TAG, http.toString());
			try {
				URL url = new URL(http.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5 * 1000);
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream input = connection.getInputStream();
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int read = -1;
					read = input.read(buffer);
					while (read > 0) {
						output.write(buffer, 0, read);
						read = input.read(buffer);
					}
					String result = output.toString();
					WorkOrderFactory.getInstance().saveJsonString(result);
					Message msg = handler.obtainMessage(DOWNLOAD_END);
					handler.sendMessage(msg);
				} else {
					Message msg = handler.obtainMessage(DOWNLOAD_FAIL);
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = handler.obtainMessage(DOWNLOAD_FAIL);
				handler.sendMessage(msg);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position >= 0) {
			WorkOrder order = this.ordersAdapter.getItem(position);
			Intent intent = new Intent(this, WorkOrderPointActivity.class);
			intent.putExtra(EXTRA_ORDRE_NO, order.getWorkItemCode());
			this.startActivity(intent);
		}
	}

}

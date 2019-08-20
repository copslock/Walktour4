package com.walktour.gui.workorder.ah;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.workorder.ah.model.WorkOrder;
import com.walktour.gui.workorder.ah.model.WorkOrderPoint;

import java.util.List;

/**
 * 安徽电信工单项目主界面->工单详情列表
 * 
 * @author jianchao.wang
 * 
 */
public class WorkOrderDetailActivity extends BasicActivity implements OnClickListener {
	/** 日志标识 */
	public final static String TAG = "WorkOrderDetailActivity";
	/** 工单详情列表 */
	private ListView workOrderDetailList;
	/** 所属工单 */
	private WorkOrder order;
	/** 明细列表适配类 */
	private DetailArrayAdapter detailsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = this.getIntent();
		String orderNo = intent.getStringExtra(WorkOrderMainActivity.EXTRA_ORDRE_NO);
		this.order = WorkOrderFactory.getInstance().getOrderByNo(orderNo);
		setContentView(R.layout.work_order_ah_detail_list);
		findView();
		initValue();
	}

	/**
	 * 视图关联设置
	 */
	private void findView() {
		this.workOrderDetailList = (ListView) this.findViewById(R.id.workOrderDetailList);
		((TextView) this.findViewById(R.id.order_no)).setText(this.order.getWorkItemCode());
		((TextView) this.findViewById(R.id.order_name)).setText(this.order.getName());
		((TextView) this.findViewById(R.id.order_desc)).setText(this.order.getDescription());
		((TextView) this.findViewById(R.id.plan_start_date)).setText(this.order.getPlanToStart());
		((TextView) this.findViewById(R.id.plan_finish_date)).setText(this.order.getPlanToFinish());
		this.findViewById(R.id.pointer).setOnClickListener(this);
	}

	/**
	 * 初始化数据
	 */
	private void initValue() {

		this.detailsAdapter = new DetailArrayAdapter(this.getApplicationContext(), R.layout.work_order_ah_detail_row,
				this.order.getPointList());
		this.workOrderDetailList.setAdapter(detailsAdapter);
	}

	/**
	 * 信息点（建筑物）列表适配类
	 * 
	 * @author jianchao.wang 2014年6月13日
	 */
	private class DetailArrayAdapter extends ArrayAdapter<WorkOrderPoint> {
		/** 资源ID */
		private int resourceId;

		public DetailArrayAdapter(Context context, int textViewResourceId, List<WorkOrderPoint> objectList) {
			super(context, textViewResourceId, objectList);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
				view = vi.inflate(resourceId, null, true);
			}
			WorkOrderPoint point = this.getItem(position);
			((TextView) view.findViewById(R.id.point_name)).setText(point.getName());
			((TextView) view.findViewById(R.id.point_no)).setText(point.getPointID());
			((TextView) view.findViewById(R.id.point_addr)).setText(point.getAddress());
			((TextView) view.findViewById(R.id.point_city)).setText(point.getCity());
			((TextView) view.findViewById(R.id.point_region)).setText(point.getRegion());
			((TextView) view.findViewById(R.id.point_task)).setText(point.getTestTask());
			return view;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
			break;
		}
	}

}

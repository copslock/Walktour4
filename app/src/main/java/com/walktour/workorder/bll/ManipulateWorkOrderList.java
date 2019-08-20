package com.walktour.workorder.bll;

import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.gui.R;
import com.walktour.workorder.dal.WorkOrderListHelper;
import com.walktour.workorder.model.WorkOrderList;
import com.walktour.workorder.model.XmlFileType;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取工单列表的业务逻辑类
 * Author: ZhengLei
 *   Date: 2013-6-20 下午3:25:47
 */
public class ManipulateWorkOrderList extends BaseManipulate {
	private Context context = null;
	private int workOrderType;
	private WorkOrderList mWorkOrderList = null;
	private List<OnManipulateListListener> callbacks = new ArrayList<OnManipulateListListener>();
	
	public ManipulateWorkOrderList(Context context, int workOrderType) {
		this.context = context;
		this.workOrderType = workOrderType;
		super.fileName = AppFilePathUtil.getInstance().getSDCardBaseFile(context.getString(R.string.work_order_dir),"list-" + this.workOrderType + ".xml").getAbsolutePath();
		super.xmlType = XmlFileType.TypeWorkOrderList;
		super.mHelper = new WorkOrderListHelper(this.workOrderType);
	}

	public Object load() {
		this.mWorkOrderList = (WorkOrderList)super.load();
		return this.mWorkOrderList;
		
	}

	@Override
	public Object synchronize() {
		// start事件点回调
		for(OnManipulateListListener c : callbacks) {
			c.onStartSynchronize();
		}
		
		this.mWorkOrderList = (WorkOrderList)super.synchronize();
		
		// end事件点回调
		for(OnManipulateListListener c : callbacks) {
			c.onEndSynchronize(this.mWorkOrderList);
		}
		return mWorkOrderList;
	}

	public void addListener(OnManipulateListListener callback) {
		if(!callbacks.contains(callback)) {
			callbacks.add(callback);
		}
	}

	public void removeListener(OnManipulateListListener callback) {
		if(callbacks.contains(callback)) {
			callbacks.remove(callback);
		}
	}

	public interface OnManipulateListListener {
		public abstract void onStartLoad();
		public abstract void onEndLoad(WorkOrderList workOrderList);
		public abstract void onStartSynchronize();
		public abstract void onEndSynchronize(WorkOrderList workOrderList);

	}
}

package com.walktour.workorder.bll;

import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.gui.R;
import com.walktour.workorder.dal.WorkOrderDetailHelper;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.XmlFileType;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取工单详情的业务逻辑类
 * Author: ZhengLei
 *   Date: 2013-6-20 下午5:40:19
 */
public class ManipulateWorkOrderDetail extends BaseManipulate {
	private Context context = null;
	private int workId;
	private WorkOrderDetail mWorkOrderDetail = null;
	private List<OnObtainDetailListener> callbacks = new ArrayList<OnObtainDetailListener>();
	
	public ManipulateWorkOrderDetail(Context context, int workId) {
		this.context = context;
		this.workId = workId;
		super.fileName = AppFilePathUtil.getInstance().getSDCardBaseFile(context.getString(R.string.work_order_dir),"detail-" + this.workId + ".xml").getAbsolutePath();
		super.xmlType = XmlFileType.TypeWorkOrderDetail;
		super.mHelper = new WorkOrderDetailHelper(this.workId);
	}

	public Object load() {
		this.mWorkOrderDetail = (WorkOrderDetail)super.load();
		return this.mWorkOrderDetail;
		
	}

	@Override
	public Object synchronize() {
		// start事件点回调
		for(OnObtainDetailListener c : callbacks) {
			c.onStartSynchronize();
		}
		
		this.mWorkOrderDetail = (WorkOrderDetail)super.synchronize();
		
		// end事件点回调
		for(OnObtainDetailListener c : callbacks) {
			c.onEndSynchronize(this.mWorkOrderDetail);
		}
		return mWorkOrderDetail;
	}

	public void addListener(OnObtainDetailListener callback) {
		if(!callbacks.contains(callback)) {
			callbacks.add(callback);
		}
	}

	public void removeListener(OnObtainDetailListener callback) {
		if(callbacks.contains(callback)) {
			callbacks.remove(callback);
		}
	}

	public interface OnObtainDetailListener {
		public abstract void onStartLoad();
		public abstract void onEndLoad(WorkOrderDetail workOrderDetail);
		public abstract void onStartSynchronize();
		public abstract void onEndSynchronize(WorkOrderDetail workOrderDetail);

	}
	
}

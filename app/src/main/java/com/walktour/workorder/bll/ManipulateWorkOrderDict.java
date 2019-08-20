package com.walktour.workorder.bll;

import android.content.Context;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.gui.R;
import com.walktour.workorder.dal.WorkOrderDictHelper;
import com.walktour.workorder.model.WorkOrderDict;
import com.walktour.workorder.model.XmlFileType;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取工单字典的业务逻辑类
 * Author: ZhengLei
 *   Date: 2013-6-19 下午5:15:11
 */
public class ManipulateWorkOrderDict extends BaseManipulate {
	private static final String TAG = "ObtainWorkOrderDict";
	private Context context = null;
	private WorkOrderDict mDict = null;
	private List<OnManipulateDictListener> callbacks = new ArrayList<OnManipulateDictListener>();
	
	public ManipulateWorkOrderDict(Context context) {
		this.context = context;
		super.fileName = AppFilePathUtil.getInstance().getSDCardBaseFile(context.getString(R.string.work_order_dir),"dict.xml").getAbsolutePath();
		super.xmlType = XmlFileType.TypeWorkOrderDict;
		super.mHelper = new WorkOrderDictHelper();
	}

	public Object load() {
		this.mDict = (WorkOrderDict)super.load();
		return this.mDict;
		
	}

	@Override
	public Object synchronize() {
		// start事件点回调
		for(OnManipulateDictListener c : callbacks) {
			c.onStartSynchronize();
		}
		
		this.mDict = (WorkOrderDict)super.synchronize();
		
		// end事件点回调
		for(OnManipulateDictListener c : callbacks) {
			c.onEndSynchronize(this.mDict);
		}
		return mDict;
	}

	public void addListener(OnManipulateDictListener callback) {
		if(!callbacks.contains(callback)) {
			callbacks.add(callback);
		}
	}

	public void removeListener(OnManipulateDictListener callback) {
		if(callbacks.contains(callback)) {
			callbacks.remove(callback);
		}
	}

	public interface OnManipulateDictListener {
		public abstract void onStartLoad();
		public abstract void onEndLoad(WorkOrderDict dict);
		public abstract void onStartSynchronize();
		public abstract void onEndSynchronize(WorkOrderDict dict);

	}
}

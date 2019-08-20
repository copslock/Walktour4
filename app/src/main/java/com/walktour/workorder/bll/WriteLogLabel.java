package com.walktour.workorder.bll;

import android.content.Context;
import android.content.IntentFilter;

import com.dinglicom.UnicomInterface;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.workorder.model.Loglabel;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkSubItem;

/**
 * 写Log标签业务类
 * @deprecated
 * Author: ZhengLei
 *   Date: 2013-6-21 上午9:39:25
 */
public class WriteLogLabel {
	private Context context = null;
	private WorkOrderDetail workOrderDetail = null;
	private int subPosition;
	private Loglabel label = null;
	private String fileName = null;
	
	public WriteLogLabel(Context context, WorkOrderDetail workOrderDetail, int subPosition) {
		this.context = context;
		this.workOrderDetail = workOrderDetail;
		this.subPosition = subPosition;
//		label = new Loglabel();
	}
	
	public void write() {
		// 先注册相关事件点。不立刻写，而是在相关接收器中触发写
		registerReceiver();
		writeWorkOrderInfo();
		writeSystemInfo();
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		// 开始和结束测试的事件点
		filter.addAction(WalkMessage.ACTION_WALKTOUR_START_TEST);
		filter.addAction(WalkMessage.NOTIFY_TESTJOBDONE);
		filter.addAction(WalkMessage.NOTIFY_INTERRUPTJOBDONE);
//		context.registerReceiver(receiver, filter);
	}
	
	/**
	 * 写工单内的相关信息
	 */
	private void writeWorkOrderInfo() {
		// 先获取选择位置，就能得到子工单ID，这样有了工单ID（workId）和子工单ID（subId）就可以获取detail.xml中的相关标签了
		if(workOrderDetail != null) {
			label.work_order_id = workOrderDetail.getWorkId() + ""; // 工单ID
			label.produc_id = workOrderDetail.getProjectId() + ""; // 项目编号ID
			label.province_code = workOrderDetail.getProvinceId() + ""; // 省编号
			label.city_code = workOrderDetail.getCityId() + ""; // 市编号
			
			// 获取选择位置，进而获得子工单
			WorkSubItem sub = workOrderDetail.getWorkSubItems().get(this.subPosition);
			if(sub != null) {
				// 子工单相关的值
				label.work_order_sub = sub.getItemId() + ""; // 子工单ID
				label.photo_md5 = ""; // 楼层地图的md5，等待练志辉那边提供图片的全路径
				label.work_test_type = sub.getTestType() + ""; // 测试日志类型
				label.work_test_scene = sub.getTestScene() + ""; // 测试场景
				label.work_test_mode = "1"; // 测试模式，遍历测试
			}
		}
	}
	
	/**
	 * 写系统相关信息
	 */
	private void writeSystemInfo() {
		label.product_name = context.getString(R.string.app_name); // 产品名称
		label.prodcut_version = UtilsMethod.getCurrentVersionName(context); // 程序版本名
		label.fileformat_version = "1.0"; // 日志格式的版本
		label.device_name = android.os.Build.MODEL; // 设备名称
		label.number_of_supported_systems = "2"; // 支持的网络数目
		label.supported_systems = "1,5"; // 支持的网络编号
		label.device_id = MyPhoneState.getInstance().getDeviceId(context); // 系统ID，即IMEI
		label.service_type = "normal"; // 业务类型
		label.scene = "indoor"; // 应用场景
		label.terminal_os = "Android" + MyPhoneState.getInstance().getAndroidVersion(); // 系统版本
	}
	
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if(action.equals(WalkMessage.ACTION_WALKTOUR_START_TEST)) {
//				// 添加开始测试事件的标签
//				label.start_time = UtilsMethod.sdFormatYmdHms.format(new Date());
//				
//			} else if(action.equals(WalkMessage.ACTION_WALKTOUR_START_TEST) || action.equals(WalkMessage.NOTIFY_INTERRUPTJOBDONE)) {
//				label.stop_time = UtilsMethod.sdFormatYmdHms.format(new Date()); // 结束时间
//				label.stop_time_millseconds = UtilsMethod.sdFormat.format(new Date()); // 结束时间，带毫秒
//				
//				String rcuFileName = intent.getStringExtra(WalkMessage.NOTIFY_TESTJOBDONE_PARANAME);
//				String logPath = Environment.getExternalStorageDirectory() + "/Walktour/data/taskdcf/";
//				String logName = rcuFileName.substring(0, rcuFileName.lastIndexOf("."));
//				fileName = logPath + logName + ".log";
////				Log.i(TAG, "log file name:" + fileName);
//				
//				flush();
//			}
//		}
//	};

	/**
	 * 真正写文件，调用jni接口函数创建Log文件
	 */
	protected void flush() {
		UnicomInterface.createLoglabelFile(this.fileName, label.format());
	}

}

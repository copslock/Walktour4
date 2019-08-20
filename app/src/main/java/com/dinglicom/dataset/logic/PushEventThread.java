package com.dinglicom.dataset.logic;

import android.content.Context;

import com.dinglicom.DataSetLib;
import com.dinglicom.dataset.DatasetManager;
import com.dinglicom.dataset.EventManager;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 压入数据到数据集的线程
 * 
 * @author jianchao.wang
 *
 */
public class PushEventThread {
	private static final String TAG = "PushEventThread";
	private DatasetBuilder mDatasetBuilder = null;
	private List<BaseModel> eventsList;
	private long mLastTime;
	private Context mContext;
	public PushEventThread(Context context) {
		LogUtil.w(TAG, "--init PushEventThread--");
		eventsList = new ArrayList<>();
		mContext=context;
	}

	/**
	 * 压入采样点
	 * 
	 * @param builder
	 * @param datasetHandle
	 * @param port
	 * @param time
	 * @param buffer
	 * @param size
	 */
	public void pushPoint(DatasetBuilder builder, int datasetHandle, final int port, final long time, final byte[] buffer,
			final int size) {
		eventsList.add(new PointModel(datasetHandle, port, time, buffer, size));
		if (mDatasetBuilder == null) {
			mDatasetBuilder = builder;
		}
	}

	/**
	 * 压入事件
	 * 
	 * @param builder
	 * @param datasetHandle
	 * @param flag
	 * @param type
	 * @param port
	 * @param time
	 * @param buffer
	 * @param size
	 * @param isAddNewGPS
	 * @param currentIndex
	 */
	public void pushEvent(DatasetBuilder builder, int datasetHandle, int flag, int type, int port, long time,
			byte[] buffer, int size, boolean isAddNewGPS, int currentIndex) {
		eventsList.add(new EventModel(datasetHandle, flag, type, port, time, buffer, size, isAddNewGPS, currentIndex));
		if (mDatasetBuilder == null) {
			mDatasetBuilder = builder;
		}
	}

	/**
	 * 压数据到数据集
	 */
	public void pushData(){
		try {
			BaseModel[] models = getPushEventByList();

			if (models != null) {
//				LogUtil.d(TAG,"----pushData----start----");
				for(BaseModel model:models) {
					if (model.modelType == BaseModel.MODEL_TYPE_EVENT)
						pushEvent((EventModel) model);
					else
						pushPoint((PointModel) model);
					Thread.sleep(10);
				}
//				LogUtil.d(TAG,"----pushData----end----");
			}
		} catch (Exception e) {
			LogUtil.w(TAG, e.getMessage(), e);
		}
	}

	//对写入事件列表加同步锁
	private BaseModel[] getPushEventByList(){
        int size = eventsList.size();
		if (size <= 0) {
			if(System.currentTimeMillis() - this.mLastTime > 5000) {
				LogUtil.w(TAG, "-----getPushEventByList size:0-----");
				this.mLastTime = System.currentTimeMillis();
			}
			return null;
		}
		LogUtil.w(TAG, "-----getPushEventByList size:" + size);
		BaseModel[] list = new BaseModel[size];
		for (int i = 0; i < size; i++) {
			list[i] = eventsList.remove(0);
		}
		return list;
	}

	/**
	 * 压入采样点
	 * 
	 * @param model
	 *          采样点对象
	 */
	private void pushPoint(PointModel model) {
		long startPush = System.currentTimeMillis();
//		LogUtil.i(TAG, "--startPushPoint:" + startPush + "--EVStr:"
//				+ EventManager.getInstance().getEventStr(UtilsMethod.getInt(model.buffer, 0)));
		int msgFlag = 0x30000;// WalkCommonPara.MsgDataFlag_D;
		int flag = DatasetManager.getInstance(mContext).getDatasetLib().pushData(model.datasetHandle, model.port, msgFlag, model.time, model.buffer, model.size,
				-9999);
//		LogUtil.i(TAG, "--DataSetLib.pushData result:" + flag);
		if (flag == 0)
			DatasetManager.getInstance(mContext).getDatasetLib().pushData(model.datasetHandle, model.port, msgFlag, model.time, model.buffer, model.size, 0);
		int delay = (int) (System.currentTimeMillis() - startPush);
//		LogUtil.i(TAG, "--endPushPoint:" + startPush + "--Delay:" + delay);

		if (delay > 1000) {
			LogUtil.e(TAG, "-->>>Point Push too Long<<<--" + "--EVStr"
					+ EventManager.getInstance().getEventStr(UtilsMethod.getInt(model.buffer, 0)) + "--delay:" + delay);
		}

		model=null;
	}

	/**
	 * 压入事件
	 * 
	 * @param model
	 *          事件对象
	 */
	private void pushEvent(EventModel model) {
		long startPush = System.currentTimeMillis();
//		LogUtil.i(TAG, "--startPushEvent:" + startPush + "--EVStr:"
//				+ EventManager.getInstance().getEventStr(UtilsMethod.getInt(model.buffer, 0)));
//		LogUtil.i(TAG,
//				"--startPush:" + startPush + "--EVStr:"
//						+ EventManager.getInstance().getEventStr(UtilsMethod.getInt(model.buffer, 0)) + "--id:"
//						+ Integer.toHexString(UtilsMethod.getInt(model.buffer, 0)) + "--flag:" + model.flag + "--ix:"
//						+ model.currentIndex + "--size:" + model.size);

		// 2014.5.4 不用查询当前采样点再插入，修改为插入当前采样点，-9999
		int eventFlag = model.flag;
		int flag = DatasetManager.getInstance(mContext).getDatasetLib().pushData(model.datasetHandle, model.port, eventFlag, model.time, model.buffer, model.size, -9999);
		if((eventFlag > 0x00100000) && (eventFlag < 0x00FFFFFF)){
			//这个区间为自定义事件
			int pushCustomEventResult = DatasetManager.getInstance(mContext).getDatasetLib().pushCustomEventData(model.datasetHandle,model.port,eventFlag,-9999,model.time,"");
			LogUtil.w(TAG, "----DataSetLib push custom event result:" + pushCustomEventResult);
		}
//		LogUtil.i(TAG, "--DataSetLib.pushData result:" + flag);
		if (flag == 0) {
			DatasetManager.getInstance(mContext).getDatasetLib().pushData(model.datasetHandle, model.port, eventFlag, model.time, model.buffer, model.size, 0);
		}
//		if (flag == 1 && (eventFlag == 'D' || eventFlag == 0x30002 || eventFlag == 0x30007)) {
//			if (model.type != -1 && mDatasetBuilder != null && model.isAddNewGPS) {
//				mDatasetBuilder.buildNewGPSInfo(DatasetManager.PORT_2, model.type == 0);
//			}
//		}

		int delay = (int) (System.currentTimeMillis() - startPush);
//		LogUtil.i(TAG, "--endPushEvent:" + startPush + "--Delay:" + delay);

		if (delay > 1000) {
			LogUtil.e(TAG, "-->>>Event Push too Long<<<--" + "--EVStr"
					+ EventManager.getInstance().getEventStr(UtilsMethod.getInt(model.buffer, 0)) + "--delay:" + delay);
		}
		model=null;
	}

	/**
	 * 事件对象
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class EventModel extends BaseModel {
		int flag;
		int type;
		boolean isAddNewGPS;
		int currentIndex;

		public EventModel(int datasetHandle, int flag, int type, int port, long time, byte[] buffer, int size,
				boolean isAddNewGPS, int currentIndex) {
			super(BaseModel.MODEL_TYPE_EVENT, datasetHandle, port, time, buffer, size);
			this.flag = flag;
			this.type = type;
			this.isAddNewGPS = isAddNewGPS;
			this.currentIndex = currentIndex;
		}
	}

	private abstract class BaseModel {
		private static final int MODEL_TYPE_POINT = 1;
		private static final int MODEL_TYPE_EVENT = 2;
		private int modelType;
		int datasetHandle;
		int port;
		long time;
		byte[] buffer;
		int size;

		BaseModel(int modelType, int datasetHandle, int port, long time, byte[] buffer, int size) {
			this.modelType = modelType;
			this.datasetHandle = datasetHandle;
			this.port = port;
			this.time = time;
			this.buffer = buffer;
			this.size = size;
		}
	}

	/**
	 * 采样点对象
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class PointModel extends BaseModel {

		PointModel(int datasetHandle, int port, long time, byte[] buffer, int size) {
			super(BaseModel.MODEL_TYPE_POINT, datasetHandle, port, time, buffer, size);
		}
	}

}

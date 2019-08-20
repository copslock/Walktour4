package com.walktour.gui.mos;

import android.content.Context;
import android.util.Log;

import com.walktour.gui.BuildConfig;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 通过manager选择合适的算分
 *
 * @author zhicheng.chen
 * @date 2019/4/12
 */
public class CaculateModeFacade {

    private final String TAG = "CaculateModeFacade";
    private Context mContext;
    private ICaculateMode mStrategy;

    public CaculateModeFacade(Context context) {
        mContext = context;
    }

    public void setDevice(BluetoothMOSDevice device) {
        if (mStrategy == null) {
            if (device != null) {
                Log.w(TAG, "Use mosstrategy to caculate result");
                mStrategy = new MosBoxMode(mContext);
            } else {
                Log.w(TAG, "Use mobilestrategy to caculate result");
                mStrategy = new MobileMode(mContext);
            }
            mStrategy.setMosDevice(device);
        }
    }


    public void setRcuFile(String rcuFileName) {
        if (mStrategy != null) {
            mStrategy.setRcuFile(rcuFileName);
        }
    }

    public void setModel(TaskModelWrapper wrapper) {
        if (mStrategy != null) {
            mStrategy.setModel(wrapper);
        }
    }

    /**
     * 开始录放音以及算分操作
     */
    public void start() {
        if (mStrategy != null) {
            mStrategy.start();
        }
    }

    /**
     * 停止录放音以及算分操作
     */
    public void stop() {
        if (mStrategy != null) {
            mStrategy.stop();
        }
    }

}

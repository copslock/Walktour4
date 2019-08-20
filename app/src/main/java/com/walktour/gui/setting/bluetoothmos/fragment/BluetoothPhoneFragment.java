package com.walktour.gui.setting.bluetoothmos.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.walktour.base.util.StringUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.setting.bluetoothmos.BluetoothPipeLine;
import com.walktour.gui.setting.bluetoothmos.adapter.RecyclerBlueToothAdapter;
import com.walktour.gui.setting.bluetoothmos.bean.BlueTooth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 连接手机蓝牙
 *
 * @author zhicheng.chen
 * @date 2019/3/29
 */
public class BluetoothPhoneFragment extends Fragment implements
        CompoundButton.OnCheckedChangeListener,
        SwipeRefreshLayout.OnRefreshListener,
        RecyclerBlueToothAdapter.OnItemClickListener {


    @BindView(R.id.switch_bluetooth)
    Switch mSwitch;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ProgressDialog mProgressDialog;

    private Timer mTimer;
    private BluetoothAdapter mBluetoothAdapter;
    private RecyclerBlueToothAdapter mAdapter;
    private List<BlueTooth> list = new ArrayList<>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //当扫描到设备的时候
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //提取强度信息
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                if (!StringUtil.isEmpty(device.getName()) && !device.getName().startsWith("DL_MicroMOS")) {
                    list.add(new BlueTooth(device.getName(), device.getAddress(), rssi + ""));
                    mAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mSwipeRefreshLayout.setRefreshing(false);
                ToastUtil.showShort(getContext(), "扫描完成");
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BluetoothAdapter.STATE_ON:
                case BluetoothAdapter.STATE_OFF: {
                    if (msg.what == BluetoothAdapter.STATE_ON) {
                        mSwitch.setText("蓝牙已开启");
                        //自动刷新
                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(true);
                                onRefresh();
                            }
                        }, 300);
                        //开启socket监听

                        BluetoothPipeLine.getInstance().setOnBluetoothPipeLineListener(new BluetoothPipeLine.OnBluetoothPipeLineListener() {
                            @Override
                            public void callback(int status, String message) {
                                mHandler.obtainMessage(status, message).sendToTarget();
                            }
                        });
                        BluetoothPipeLine.getInstance().waitForConnect();
                    } else if (msg.what == BluetoothAdapter.STATE_OFF) {
                        mSwitch.setText("蓝牙已关闭");
                        mAdapter.setBlueToothData(null);
                        mAdapter.notifyDataSetChanged();
                    }
                    mTimer.cancel();
                    mTimer = null;
                    mSwitch.setClickable(true);
                }
                break;
                case BluetoothPipeLine.BLUE_TOOTH_DIALOG: {
                    showProgressDialog((String) msg.obj);
                }
                break;
                case BluetoothPipeLine.BLUE_TOOTH_TOAST: {
                    dismissProgressDialog();
                    ToastUtil.showShort(getContext(), (String) msg.obj);
                }
                break;
                case BluetoothPipeLine.BLUE_TOOTH_SUCCESS: {
                    dismissProgressDialog();
                    ToastUtil.showShort(getContext(), "连接设备" + (String) msg.obj + "成功");
                    getActivity().finish();
                }
                break;

                default:
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //注册扫描设备广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        BluetoothPipeLine.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_bluetooth_phone, null);
        ButterKnife.bind(this, layout);
        initView();
        setListener();
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //获取本地蓝牙实例
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        onCheckedChanged(mSwitch, true);

    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mAdapter = new RecyclerBlueToothAdapter(getActivity());
        mAdapter.setBlueToothData(list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mSwitch.setChecked(true);
    }

    private void setListener() {
        mAdapter.setOnItemClickListener(this);
        mSwitch.setOnCheckedChangeListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * 进度对话框
     *
     * @param msg
     */
    public void showProgressDialog(String msg) {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == true) {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
                mBluetoothAdapter.enable();  //打开蓝牙
                mSwitch.setText("正在开启蓝牙");
                ToastUtil.showShort(getContext(), "正在开启蓝牙");
            }
        } else {
            if (mBluetoothAdapter.getState() != BluetoothAdapter.STATE_OFF) {
                mBluetoothAdapter.disable();  //打开蓝牙
                mSwitch.setText("正在关闭蓝牙");
                ToastUtil.showShort(getContext(), "正在关闭蓝牙");
            }
        }
        mSwitch.setClickable(false);
        if (mTimer == null) {
            mTimer = new Timer();
            BlueToothTask task = new BlueToothTask();
            task.setChecked(isChecked);
            mTimer.schedule(task, 0, 1000);
        }
    }

    @Override
    public void onRefresh() {
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            list.clear();
            //扫描的是已配对的
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                list.add(new BlueTooth("已配对的设备", BlueTooth.TAG_TOAST));
                for (BluetoothDevice device : pairedDevices) {
                    list.add(new BlueTooth(device.getName(), device.getAddress(), ""));
                }
                list.add(new BlueTooth("已扫描的设备", BlueTooth.TAG_TOAST));
            } else {
                ToastUtil.showShort(getContext(), "没有找到已匹对的设备！");
                list.add(new BlueTooth("已扫描的设备", BlueTooth.TAG_TOAST));
            }
            mAdapter.notifyDataSetChanged();
            //开始扫描设备
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();
            ToastUtil.showShort(getContext(), "开始扫描设备");
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            ToastUtil.showShort(getContext(), "请开启蓝牙");
        }
    }

    @Override
    public void onItemClick(int position) {
        String mac = list.get(position).getMac();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac);
        BluetoothPipeLine.getInstance().connectDevice(device);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
        if (mTimer != null) {
            mTimer.cancel();
        }

        mBluetoothAdapter.cancelDiscovery();
        mSwipeRefreshLayout.setRefreshing(false);

        BluetoothPipeLine.getInstance().setOnBluetoothPipeLineListener(null);
    }

    private class BlueToothTask extends TimerTask {
        private boolean isChecked;

        public void setChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        @Override
        public void run() {
            if (isChecked) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
                    mHandler.sendEmptyMessage(BluetoothAdapter.STATE_ON);
            } else {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
                    mHandler.sendEmptyMessage(BluetoothAdapter.STATE_OFF);
            }
        }
    }
}

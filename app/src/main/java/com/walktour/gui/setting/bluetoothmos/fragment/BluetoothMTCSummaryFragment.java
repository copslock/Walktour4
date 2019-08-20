package com.walktour.gui.setting.bluetoothmos.fragment;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.walktour.Utils.APNOperate;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMTCService;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceBinder;
import com.walktour.service.bluetoothmos.IBluetoothMOSServiceCallback;
import com.walktour.service.bluetoothmos.command.BaseCommand;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Yi.Lin on 2017/12/18.
 */

public class BluetoothMTCSummaryFragment extends Fragment implements View.OnClickListener {

    /**
     * 日志标识
     */
    private final static String TAG = "BluetoothMTCSummaryFragment";
    /**
     * 对话框类型：显示进度条
     */
    private static final String DIALOG_SHOW_PROGRESS = "show_progress";
    /**
     * 显示当前的设备信息
     */
    private static final int SHOW_DEVICE_INFO = 99;
    /**
     * 显示算分结果
     */
    private static final int SHOW_CALCULATE_RESULT = 108;

    /**
     * 弹出扫描到的设备列表
     */
    private static final int SHOW_DISCOVERED_DEVICES = 110;

    /**
     * 更换文件选择的文件类型
     */
    private String mFileType;
    /**
     * 蓝牙MOS头服务类
     */
    private IBluetoothMOSServiceBinder mServiceBinder;
    /**
     * 是否绑定了蓝牙MOS头服务
     */
    private boolean isBindMOSService = false;
    /**
     * 消息处理句柄
     */
    private MyHandler mHandler = new MyHandler(this);
    /**
     * 文件类型名称数组
     */
    private String[] mFileTypes;
    /**
     * 进度条对话框
     */
    private ProgressFragment mProgressDialog = null;
    /**
     * 连接设备选择按钮
     */
    private CheckBox mConnectDevice;
    /**
     * 蓝牙MOS头工厂类
     */
    private BluetoothMOSFactory mFactory;

    /**
     * 连接状态：已连接
     */
    public static final int CONNECT_STATE_YES = 1;
    /**
     * 连接状态：未连接
     */
    public static final int CONNECT_STATE_NO = 0;
    /**
     * 设备名称编辑框
     */
    private EditText mDeviceName;
    /**
     * MAC地址编辑框
     */
    private EditText mMacAddress;
    /**
     * 信号强度编辑框
     */
    private EditText mSignalLevel;
    /**
     * 设备电量编辑框
     */
    private EditText mDevicePower;
    /**
     * 设备放音音量编辑框
     */
    private EditText mDevicePlaybackVolume;
    /**
     * 设备录音音量编辑框
     */
    private EditText mDeviceRecordVolume;
    /**
     * 设备软件版本号
     */
    private EditText mDeviceVersion;
    /**
     * 设备已有文件浏览按钮
     */
    private Button mDeviceHaveFilesBtn;
    /**
     * 回环分值编辑框
     */
    private EditText mRingResult;
    /**
     * 当前的设备
     */
    private BluetoothMOSDevice mDevice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "----onCreateView----");
        View rootView = inflater.inflate(R.layout.fragment_bluetooth_mtc_summary, container, false);
        this.mFactory = BluetoothMOSFactory.get();
        this.findView(rootView);
        this.bindMOSService();
        this.showDeviceInfo();
        this.showRingTestResult();
        this.syncNetWorkTime();
        return rootView;
    }

    /**
     * 绑定蓝牙MOS头服务
     */
    private void bindMOSService() {
        LogUtil.d(TAG, "---------bindMOSService----------------");
        Intent intent = new Intent(getContext(), BluetoothMTCService.class);
        intent.putExtra(BluetoothMTCService.EXTRA_KEY_CALCULATE_PESQ, true);
        intent.putExtra(BluetoothMTCService.EXTRA_KEY_MOS_TYPE, BluetoothMOSDevice.DEVICE_TYPE_MTC);
        if (this.mFactory.getCurrMTCDevice() != null) {
            intent.putExtra(BluetoothMTCService.EXTRA_KEY_BLUETOOTH_MTC, this.mFactory.getCurrMTCDevice());
            intent.setExtrasClassLoader(BluetoothMOSDevice.class.getClassLoader());
        }
        getContext().bindService(intent, conn, BIND_AUTO_CREATE);
    }

    /**
     * 视图匹配
     */
    private void findView(View rootView) {
        this.mDeviceName = (EditText) rootView.findViewById(R.id.edit_device_name);
        this.mMacAddress = (EditText) rootView.findViewById(R.id.edit_mac_address);
        this.mSignalLevel = (EditText) rootView.findViewById(R.id.edit_signal_level);
        this.mDevicePower = (EditText) rootView.findViewById(R.id.edit_device_power);
        this.mDevicePlaybackVolume = (EditText) rootView.findViewById(R.id.edit_device_playback_volume);
        this.mDeviceRecordVolume = (EditText) rootView.findViewById(R.id.edit_device_record_volume);
        this.mDeviceVersion = (EditText) rootView.findViewById(R.id.edit_device_version);
        this.mDeviceHaveFilesBtn = (Button) rootView.findViewById(R.id.show_device_have_files_btn);
        this.mDeviceHaveFilesBtn.setOnClickListener(this);
        this.mRingResult = (EditText) rootView.findViewById(R.id.edit_ring_result);
        Button changeFile = (Button) rootView.findViewById(R.id.btn_change_file);
        changeFile.setOnClickListener(this);
        Button ringTest = (Button) rootView.findViewById(R.id.btn_ring_test);
        ringTest.setOnClickListener(this);
        Button setting = (Button) rootView.findViewById(R.id.btn_setting);
        setting.setOnClickListener(this);
        mConnectDevice = (CheckBox) rootView.findViewById(R.id.bluetooth_mos_check);
        if (BluetoothMOSFactory.get().getCurrMTCDevice() != null)
            mConnectDevice.setChecked(true);
        mConnectDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                connectDevice(isChecked);
            }
        });

    }

    /**
     * 创建进度条对话框
     *
     * @param message 进度条内容
     */
    private void showProgressDialog(String message) {
        if (this.mProgressDialog != null)
            return;
        FragmentManager fm = getChildFragmentManager();
        this.mProgressDialog = new ProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ProgressFragment.PROGRESS_MESSAGE, message);
        this.mProgressDialog.setArguments(bundle);
        this.mProgressDialog.show(fm, DIALOG_SHOW_PROGRESS);
        this.mProgressDialog.setCancelable(false);
    }

    /**
     * 中断进度条显示
     */
    private void dismissProgress() {
        if (this.mProgressDialog != null && !this.mProgressDialog.isHidden()) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    /**
     * 连接设备
     *
     * @param isOn 是否连接
     */
    private void connectDevice(boolean isOn) {
        if (mServiceBinder == null) {
            return;
        }
        if (isOn) {
            AlertWakeLock.acquire(getContext());
            boolean flag = false;
            try {
                flag = mServiceBinder.findCorrectDevice(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (!flag) {
                Toast.makeText(getContext(), R.string.task_callMOS_device_hadset_off, Toast.LENGTH_SHORT).show();
                this.mConnectDevice.setChecked(flag);
                AlertWakeLock.release();
            }
        } else {
            try {
                this.mServiceBinder.disconnect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.mFactory.setCurrMTCDevice(null);
            this.showDeviceInfo();
            this.showRingTestResult();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_file:
                this.showChangeFileDialog();
                break;
            case R.id.btn_ring_test:
                this.ringTest();
                break;
            case R.id.btn_setting:
                this.showMOSParamsSetDialog();
                break;
            case R.id.show_device_have_files_btn:
                this.showDeviceHaveFilesDialog();
                break;
            default:
                break;
        }
    }


    /**
     * 显示设备当前已有文件列表
     */
    private void showDeviceHaveFilesDialog() {
        if (this.mDevice == null)
            return;
        BasicDialog.Builder builder = new BasicDialog.Builder(this.getActivity());
        String[] fileIDs = new String[0];
        if (!StringUtil.isNullOrEmpty(this.mDevice.getFileIDs()))
            fileIDs = this.mDevice.getFileIDs().split(",");
        builder.setTitle(R.string.task_callMOS_device_have_files).setItems(fileIDs, null)
                .setNegativeButton(R.string.str_cancle).show();
    }

    /**
     * 设置MOS头参数
     */
    @SuppressLint("InflateParams")
    private void showMOSParamsSetDialog() {
        if (mServiceBinder == null || this.mFactory.getCurrMTCDevice() == null) {
            Toast.makeText(getContext(), R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bluetoothmos_setting, null);
        builder.setTitle(R.string.task_callMOS_setting);
        builder.setView(view);
        BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMTCDevice();
        final EditText playbackVolumeEdit = (EditText) view.findViewById(R.id.edit_playback_volume);
        playbackVolumeEdit.setText(String.valueOf(device.getPlaybackVolume()));
        final EditText recordVolumeEdit = (EditText) view.findViewById(R.id.edit_record_volume);
        recordVolumeEdit.setText(String.valueOf(device.getRecordVolume()));
        builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int playbackVolume = Integer.parseInt(playbackVolumeEdit.getText().toString());
                if (playbackVolume > 255)
                    playbackVolume = 255;
                else if (playbackVolume < 0)
                    playbackVolume = 0;
                int recordVolume = Integer.parseInt(recordVolumeEdit.getText().toString());
                if (recordVolume > 7)
                    recordVolume = 7;
                else if (recordVolume < 0)
                    recordVolume = 0;
                BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMTCDevice();
                device.setPlaybackVolume(playbackVolume);
                device.setRecordVolume(recordVolume);
                setParamsToMOS();
            }
        }).setNegativeButton(R.string.str_cancle).show();
    }

    /**
     * 设置蓝牙MOS头的参数
     */
    private void setParamsToMOS() {
        BluetoothMOSDevice device = mFactory.getCurrMTCDevice();
        try {
            this.mServiceBinder.setDeviceParams(device.getPlaybackVolume(), device.getRecordVolume());
            this.showDeviceInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回环检测
     */
    private void ringTest() {
        if (mServiceBinder == null || this.mFactory.getCurrMTCDevice() == null) {
            Toast.makeText(getContext(), R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            this.mServiceBinder.testPlaybackAndRecord();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示更换音频文件对话框
     */
    private void showChangeFileDialog() {
        if (mServiceBinder == null || this.mFactory.getCurrMTCDevice() == null) {
            Toast.makeText(getContext(), R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
        BaseCommand.FileType[] fileTypes = BaseCommand.FileType.values();
        this.mFileTypes = new String[fileTypes.length - 1];
        int pos = 0;
        for (int i = 0; i < fileTypes.length; i++) {
            if (fileTypes[i] == BaseCommand.FileType.check_8k)
                continue;
            this.mFileTypes[pos++] = fileTypes[i].getName();
        }
        this.mFileType = this.mFileTypes[0];
        builder.setTitle(R.string.task_callMOS_choose_file_type)
                .setSingleChoiceItems(this.mFileTypes, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFileType = mFileTypes[which];
                        writeFileToMOS();
                    }
                }).setPositiveButton(R.string.task_callMOS_clear_files, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAudioFiles();
            }
        }, false).setNegativeButton(R.string.str_cancle);
        builder.show();
    }

    /**
     * 清理蓝牙MOS头里的所有样本文件
     */
    private void clearAudioFiles() {
        if (mServiceBinder == null || this.mFactory.getCurrMTCDevice() == null) {
            Toast.makeText(getContext(), R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        this.showProgressDialog(this.getString(R.string.task_callMOS_clear_files_doing));
        try {
            this.mServiceBinder.clearAudioFiles();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示连接的设备信息
     */
    private void showDeviceInfo() {
        BluetoothMOSDevice device = this.mFactory.getCurrMTCDevice();
        this.setDeviceInfo(device);
        this.setConnectState(
                device == null ? BluetoothSummaryFragment.CONNECT_STATE_NO : BluetoothSummaryFragment.CONNECT_STATE_YES);
    }

    /**
     * 设置连接状态
     *
     * @param state 0 未连接，1 已连接
     */
    public void setConnectState(int state) {
        if (state != CONNECT_STATE_YES) {
            this.mDevicePower.setText("--");
            this.mRingResult.setText("--/--");
        }
    }

    /**
     * 显示回环检测分值
     */
    private void showRingTestResult() {
        BluetoothMOSDevice device = this.mFactory.getCurrMTCDevice();
        if (device != null) {
            this.setRingResult(device.getPlaybackResult(), device.getRecordResult());
        }
    }


    /**
     * 设置回环检测分值
     *
     * @param playbackResult 放音检测分值
     * @param recordResult   录音检测分值
     */
    public void setRingResult(double playbackResult, double recordResult) {
        this.mRingResult.setText(String.format(Locale.getDefault(), "↓%.2f/↑%.2f", playbackResult, recordResult));
    }

    /**
     * 设置设备信息
     *
     * @param device 设备对象
     */
    public void setDeviceInfo(BluetoothMOSDevice device) {
        this.mDevice = device;
        if (device != null) {
            this.mDeviceName.setText(device.getName());
            this.mMacAddress.setText(device.getAddress());
            this.mSignalLevel.setText(device.getSignalLevel() + "dB");
            if (device.getPower() > 0)
                this.mDevicePower.setText(device.getPower() + "%");
            if (device.getPlaybackVolume() > 0)
                this.mDevicePlaybackVolume.setText(String.valueOf(device.getPlaybackVolume()));
            if (device.getRecordVolume() > 0)
                this.mDeviceRecordVolume.setText(String.valueOf(device.getRecordVolume()));
            if (!StringUtil.isNullOrEmpty(device.getVersion()))
                this.mDeviceVersion.setText(device.getVersion());
            this.mDeviceHaveFilesBtn.setEnabled(true);
        } else {
            this.mDeviceName.setText("--");
            this.mMacAddress.setText("--");
            this.mSignalLevel.setText("--");
            this.mDevicePower.setText("--");
            this.mDevicePlaybackVolume.setText("--");
            this.mDeviceRecordVolume.setText("--");
            this.mDeviceVersion.setText("--");
            this.mDeviceHaveFilesBtn.setEnabled(false);
        }
    }

    /**
     * 消息处理句柄
     *
     * @author jianchao.wang
     */
    private static class MyHandler extends Handler {
        private WeakReference<BluetoothMTCSummaryFragment> reference;

        public MyHandler(BluetoothMTCSummaryFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            LogUtil.d(TAG, "handleMessage msg = " + msg);
            BluetoothMTCSummaryFragment fragment = reference.get();
            BluetoothMOSDevice device = fragment.mFactory.getCurrMTCDevice();
//			String msg1 = (String) msg.obj;
            switch (msg.what) {
                case BluetoothMTCService.EXTRA_SHOW_PROGRESS:
                    fragment.showProgressDialog((String) msg.obj);
                    break;
                case BluetoothMTCService.EXTRA_SHOW_PROGRESS_DISMISS:
                    AlertWakeLock.release();
                    fragment.dismissProgress();
                    break;
                case BluetoothMTCService.EXTRA_CONNECT_INTERRUPT:
                    AlertWakeLock.release();
                    fragment.connectInterrupt();
                    break;
                case BluetoothMTCService.EXTRA_FIND_DEVICE_FAIL:
                    AlertWakeLock.release();
                    fragment.findDeviceFail();
                    break;
                case BluetoothMTCService.EXTRA_SHOW_RING_RESULT:
                    AlertWakeLock.release();
                    if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
                        String[] values = ((String) msg.obj).split(",");
                        device.setPlaybackResult(Double.parseDouble(values[0]));
                        device.setRecordResult(Double.parseDouble(values[1]));
                        device.setRingResult(Double.parseDouble(values[2]));
                    }
                    fragment.showRingTestResult();
                    break;
                case BluetoothMTCService.EXTRA_HADSET_OFF:
                    AlertWakeLock.release();
                    fragment.mConnectDevice.setChecked(false);
                    fragment.showDeviceInfo();
                    fragment.showRingTestResult();
                    break;
                case BluetoothMTCService.EXTRA_SHOW_PARAMS:
                    AlertWakeLock.release();
                    if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
                        String[] values = ((String) msg.obj).split(",");
                        device.setPower(Integer.parseInt(values[0]));
                        device.setPlaybackVolume(Integer.parseInt(values[1]));
                        if (values.length > 2) {
                            device.setRecordVolume(Integer.parseInt(values[2]));
                            device.setVersion(values[3]);
                        }
                    }
                    fragment.showDeviceInfo();
                    break;
                case SHOW_DEVICE_INFO:
                    AlertWakeLock.release();
                    fragment.dismissProgress();
                    fragment.showDeviceInfo();
                    fragment.showRingTestResult();
                    break;
                case BluetoothMTCService.EXTRA_SHOW_FILES:
                    AlertWakeLock.release();
                    if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
                        device.setFileIDs((String) msg.obj);
                    }
                    fragment.showDeviceInfo();
                    break;
                case SHOW_CALCULATE_RESULT:
                    Toast.makeText(fragment.getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                case SHOW_DISCOVERED_DEVICES:
                    List<BluetoothMOSDevice> discoveredDevices = (List<BluetoothMOSDevice>) msg.obj;
                    BluetoothMOSDevice currMOCDevice = BluetoothMOSFactory.get().getCurrMOCDevice();
                    if(null == currMOCDevice){
                        fragment.showDiscoveredBluetoothList(discoveredDevices);
                    }else{
                        //过滤掉主叫设备
                        if(null != discoveredDevices && !discoveredDevices.isEmpty()){
                            List<BluetoothMOSDevice> filteredDevices = new ArrayList<>();
                            for (BluetoothMOSDevice mosDevice : discoveredDevices) {
                                if(mosDevice.getAddress().equals(currMOCDevice.getAddress())){
                                    continue;
                                }
                                filteredDevices.add(mosDevice);
                            }
                            fragment.showDiscoveredBluetoothList(filteredDevices);
                        }
                    }

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 连接中断
     */
    private void connectInterrupt() {
        Toast.makeText(getContext(), R.string.task_callMOS_device_disconnected, Toast.LENGTH_SHORT).show();
        this.mFactory.setCurrMTCDevice(null);
        this.dismissProgress();
        this.showDeviceInfo();
        this.showRingTestResult();
        this.mConnectDevice.setChecked(false);
    }

    /**
     * 查找设备失败操作
     */
    private void findDeviceFail() {
        Toast.makeText(getContext(), R.string.task_callMOS_device_no_finded, Toast.LENGTH_SHORT).show();
        this.mFactory.setCurrMTCDevice(null);
        this.dismissProgress();
        this.showDeviceInfo();
        this.showRingTestResult();
        this.mConnectDevice.setChecked(false);
    }

    /**
     * 服务类链接类
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "--------onServiceDisconnected---------");
            if (mServiceBinder != null) {
                mServiceBinder = null;
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LogUtil.d(TAG, "--------onServiceConnected---------");
            mServiceBinder = IBluetoothMOSServiceBinder.Stub.asInterface(binder);
            isBindMOSService = true;
            try {
                mServiceBinder.registerCallback(mCallback);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        LogUtil.d(TAG, "----onServiceConnected----getDeviceParams------");
                        try {
                            mServiceBinder.getDeviceParams();
                            mServiceBinder.getHaveFileIDs();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 回调方法
     */
    private IBluetoothMOSServiceCallback mCallback = new IBluetoothMOSServiceCallback.Stub() {

        @Override
        public void handleMessage(int what, String message) throws RemoteException {
            LogUtil.d(TAG, "--------handleMessage----what:" + what + "--message:" + message + "---");
            if (message == null) {
                mHandler.sendEmptyMessage(what);
            } else {
                mHandler.obtainMessage(what, message).sendToTarget();
            }

        }

        @Override
        public void handleDevice(BluetoothMOSDevice device) throws RemoteException {
            mFactory.setCurrMTCDevice(device);
            mHandler.sendEmptyMessage(SHOW_DEVICE_INFO);
        }

        @Override
        public void onFinishDiscoverBluetoothDevices(List<BluetoothMOSDevice> discoveredDevices) throws RemoteException {
            Message msg = new Message();
            msg.obj = discoveredDevices;
            msg.what = SHOW_DISCOVERED_DEVICES;
            mHandler.sendMessage(msg);
        }
    };

    /**
     * 显示扫描到的蓝牙设备列表
     */
    private void showDiscoveredBluetoothList(final List<BluetoothMOSDevice> discoveredDevices) {
        LogUtil.d(TAG, "----showDiscoveredBluetoothList----");
        if (null != discoveredDevices && !discoveredDevices.isEmpty()) {
            String[] deviceMacAddresses = new String[discoveredDevices.size()];
            for (int i = 0; i < discoveredDevices.size(); i++) {
                BluetoothMOSDevice device = discoveredDevices.get(i);
                deviceMacAddresses[i] = device.getAddress();
            }
            BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
            BasicDialog dialog = builder.setTitle(R.string.task_callMOS_select_mtc_bluetooth_device).setSingleChoiceItems(deviceMacAddresses, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BluetoothMOSDevice selectedDev = discoveredDevices.get(which);
                    try {
                        mServiceBinder.connect(selectedDev.getAddress());
                    } catch (RemoteException e) {
                        LogUtil.e(TAG, e.getMessage());
                    }
                }
            }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mHandler.sendEmptyMessage(BluetoothMTCService.EXTRA_SHOW_PROGRESS_DISMISS);
                    mConnectDevice.setChecked(false);
                }
            }).show();
            dialog.setCancelable(false);
        } else {
            mHandler.sendEmptyMessage(BluetoothMTCService.EXTRA_SHOW_PROGRESS_DISMISS);
            this.mConnectDevice.setChecked(false);
            Toast.makeText(getContext(), R.string.task_callMOS_no_bluetooth_mos_device_found, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "------onDestroy------");
        this.unbindMOSService();
        super.onDestroy();
    }

    /**
     * 解除蓝牙MOS头服务绑定
     */
    private void unbindMOSService() {
        LogUtil.d(TAG, "--------------unbindMOSService---------------");
        if (isBindMOSService) {
            isBindMOSService = false;
            this.mServiceBinder = null;
            Intent intent = new Intent(getContext(), BluetoothMTCService.class);
            getContext().stopService(intent);
            getContext().unbindService(conn);
        }
    }

    /**
     * 写文件到MOS头
     */
    private void writeFileToMOS() {
        if (mServiceBinder == null || this.mFactory.getCurrMTCDevice() == null) {
            Toast.makeText(getContext(), R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (this.mFileType != null) {
            this.showProgressDialog(this.getString(R.string.task_callMOS_change_file_doing));
            try {
                this.mServiceBinder.writeFileTo(this.mFileType);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭可用接入点
     */
    private synchronized void setDataEnabled(boolean enabled) {
        try {
            APNOperate apnOperate = APNOperate.getInstance(getContext());
            if (enabled || (!enabled && apnOperate.checkNetWorkIsConnected())) {
                apnOperate.setMobileDataEnabled(enabled, "", true, 1000 * 15);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    /**
     * 获取网络时间
     */
    private void syncNetWorkTime() {
        new Thread() {

            @Override
            public void run() {
                if (!MyPhoneState.getInstance().isNetworkAvirable(getContext().getApplicationContext())) {
                    setDataEnabled(true);
                }
                long time = UtilsMethod.getNetTimeByBjtime();
                // 分别取得时间中的小时，分钟和秒，并输出
                LogUtil.d(TAG, "sync net work time:" + UtilsMethod.sdFormat.format(new Date(time)));
                LogUtil.d(TAG, "server time offset:" + (System.currentTimeMillis() - time));
                ApplicationModel.getInstance().setServerTimeOffset(System.currentTimeMillis() - time);
//				UtilsMethod.setTime(time,getApplicationContext());
            }

        }.start();
    }

}

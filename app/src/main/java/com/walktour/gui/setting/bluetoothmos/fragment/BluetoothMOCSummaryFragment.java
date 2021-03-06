package com.walktour.gui.setting.bluetoothmos.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.APNOperate;
import com.walktour.Utils.AlertWakeLock;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DensityUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.ScreenUtils;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.BluetoothMOSService;
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

import butterknife.ButterKnife;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Yi.Lin on 2017/12/18.
 */

public class BluetoothMOCSummaryFragment extends Fragment implements View.OnClickListener {


    /**
     * ????????????
     */
    private final static String TAG = "BluetoothMOCSummaryFragment";
    /**
     * ?????????????????????????????????
     */
    private static final String DIALOG_SHOW_PROGRESS = "show_progress";
    /**
     * ???????????????????????????
     */
    private static final int SHOW_DEVICE_INFO = 99;
    /**
     * ??????????????????
     */
    private static final int SHOW_CALCULATE_RESULT = 108;

    /**
     * ??????????????????????????????
     */
    private static final int SHOW_DISCOVERED_DEVICES = 110;

    /**
     * ?????????????????????????????????
     */
    private String mFileType;
    /**
     * ??????MOS????????????
     */
    private IBluetoothMOSServiceBinder mServiceBinder;
    /**
     * ?????????????????????MOS?????????
     */
    private boolean isBindMOSService = false;
    /**
     * ??????????????????
     */
    private MyHandler mHandler = new MyHandler(this);
    /**
     * ????????????????????????
     */
    private String[] mFileTypes;
    /**
     * ??????????????????
     */
    private ProgressFragment mProgressDialog = null;
    /**
     * ????????????????????????
     */
    private CheckBox mConnectDevice;
    /**
     * ??????MOS????????????
     */
    private BluetoothMOSFactory mFactory;

    /**
     * ????????????????????????
     */
    public static final int CONNECT_STATE_YES = 1;
    /**
     * ????????????????????????
     */
    public static final int CONNECT_STATE_NO = 0;
    /**
     * ?????????????????????
     */
    private EditText mDeviceName;
    /**
     * MAC???????????????
     */
    private EditText mMacAddress;
    /**
     * ?????????????????????
     */
    private EditText mSignalLevel;
    /**
     * ?????????????????????
     */
    private EditText mDevicePower;
    /**
     * ???????????????????????????
     */
    private EditText mDevicePlaybackVolume;
    /**
     * ???????????????????????????
     */
    private EditText mDeviceRecordVolume;
    /**
     * ?????????????????????
     */
    private EditText mDeviceVersion;
    /**
     * ??????????????????????????????
     */
    private Button mDeviceHaveFilesBtn;
    /**
     * ?????????????????????
     */
    private EditText mRingResult;
    /**
     * ???????????????
     */
    private BluetoothMOSDevice mDevice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "----onCreateView----");
        View rootView = inflater.inflate(R.layout.fragment_bluetooth_moc_summary, container, false);
        this.mFactory = BluetoothMOSFactory.get();
        this.findView(rootView);
        this.bindMOSService();
        this.showDeviceInfo();
        this.showRingTestResult();
        this.syncNetWorkTime();
        return rootView;
    }

    /**
     * ????????????MOS?????????
     */
    private void bindMOSService() {
        LogUtil.d(TAG, "---------bindMOSService----------------");
        Intent intent = new Intent(getContext(), BluetoothMOSService.class);
        intent.putExtra(BluetoothMOSService.EXTRA_KEY_CALCULATE_PESQ, true);
        intent.putExtra(BluetoothMOSService.EXTRA_KEY_MOS_TYPE, BluetoothMOSDevice.DEVICE_TYPE_MOC);
        if (this.mFactory.getCurrMOCDevice() != null) {
            intent.putExtra(BluetoothMOSService.EXTRA_KEY_BLUETOOTH_MOS, this.mFactory.getCurrMOCDevice());
            intent.setExtrasClassLoader(BluetoothMOSDevice.class.getClassLoader());
        }
        getContext().bindService(intent, conn, BIND_AUTO_CREATE);
    }

    /**
     * ????????????
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
        if (BluetoothMOSFactory.get().getCurrMOCDevice() != null)
            mConnectDevice.setChecked(true);
        mConnectDevice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                connectDevice(isChecked);
            }
        });

    }

    /**
     * ????????????????????????
     *
     * @param message ???????????????
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
     * ?????????????????????
     */
    private void dismissProgress() {
        if (this.mProgressDialog != null && !this.mProgressDialog.isHidden()) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    /**
     * ????????????
     *
     * @param isOn ????????????
     */
    private void connectDevice(boolean isOn) {
        if (mServiceBinder == null) {
            ToastUtil.showToastLong(getContext(), getString(R.string.service_opening_try_later));
            mConnectDevice.setChecked(!isOn);
            return;
        }
        if (isOn) {
            AlertWakeLock.acquire(getContext());
            boolean flag = false;
            try {
                flag = mServiceBinder.findCorrectDevice(true);
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
            this.mFactory.setCurrMOCDevice(null);
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
     * ????????????????????????????????????
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
     * ??????MOS?????????
     */
    @SuppressLint("InflateParams")
    private void showMOSParamsSetDialog() {
        if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
            Toast.makeText(getContext(), R.string.task_callMOS_device_no_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bluetoothmos_setting, null);
        builder.setTitle(R.string.task_callMOS_setting);
        builder.setView(view);
        BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMOCDevice();
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
                BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMOCDevice();
                device.setPlaybackVolume(playbackVolume);
                device.setRecordVolume(recordVolume);
                setParamsToMOS();
            }
        }).setNegativeButton(R.string.str_cancle).show();
    }

    /**
     * ????????????MOS????????????
     */
    private void setParamsToMOS() {
        BluetoothMOSDevice device = mFactory.getCurrMOCDevice();
        try {
            this.mServiceBinder.setDeviceParams(device.getPlaybackVolume(), device.getRecordVolume());
            this.showDeviceInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     */
    private void ringTest() {
        if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
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
     * ?????????????????????????????????
     */
    private void showChangeFileDialog() {
        if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
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
     * ????????????MOS???????????????????????????
     */
    private void clearAudioFiles() {
        if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
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
     * ???????????????????????????
     */
    private void showDeviceInfo() {
        BluetoothMOSDevice device = this.mFactory.getCurrMOCDevice();
        this.setDeviceInfo(device);
        this.setConnectState(
                device == null ? BluetoothSummaryFragment.CONNECT_STATE_NO : BluetoothSummaryFragment.CONNECT_STATE_YES);
    }

    /**
     * ??????????????????
     *
     * @param state 0 ????????????1 ?????????
     */
    public void setConnectState(int state) {
        if (state != CONNECT_STATE_YES) {
            this.mDevicePower.setText("--");
            this.mRingResult.setText("--/--");
        }
    }

    /**
     * ????????????????????????
     */
    private void showRingTestResult() {
        BluetoothMOSDevice device = this.mFactory.getCurrMOCDevice();
        if (device != null) {
            this.setRingResult(device.getPlaybackResult(), device.getRecordResult());
        }
    }


    /**
     * ????????????????????????
     *
     * @param playbackResult ??????????????????
     * @param recordResult   ??????????????????
     */
    public void setRingResult(double playbackResult, double recordResult) {
        this.mRingResult.setText(String.format(Locale.getDefault(), "???%.2f/???%.2f", playbackResult, recordResult));
    }

    /**
     * ??????????????????
     *
     * @param device ????????????
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
     * ??????????????????
     *
     * @author jianchao.wang
     */
    private static class MyHandler extends Handler {
        private WeakReference<BluetoothMOCSummaryFragment> reference;

        public MyHandler(BluetoothMOCSummaryFragment fragment) {
            reference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            LogUtil.d(TAG, "handleMessage msg = " + msg);
            BluetoothMOCSummaryFragment fragment = reference.get();
            BluetoothMOSDevice device = fragment.mFactory.getCurrMOCDevice();
            //			String msg1 = (String) msg.obj;
            switch (msg.what) {
                case BluetoothMOSService.EXTRA_SEARCH_FINISHED:
                    if (fragment.dialog != null) {
                        ButterKnife.findById(fragment.dialog, R.id.pb_loading).setVisibility(View.GONE);
                    }
                    ToastUtil.showToastShort(fragment.getContext(), "????????????");
                    break;
                case BluetoothMOSService.EXTRA_SHOW_PROGRESS:
                    fragment.showProgressDialog((String) msg.obj);
                    break;
                case BluetoothMOSService.EXTRA_SHOW_PROGRESS_DISMISS:
                    AlertWakeLock.release();
                    fragment.dismissProgress();
                    break;
                case BluetoothMOSService.EXTRA_CONNECT_INTERRUPT:
                    AlertWakeLock.release();
                    fragment.connectInterrupt();
                    break;
                case BluetoothMOSService.EXTRA_FIND_DEVICE_FAIL:
                    AlertWakeLock.release();
                    fragment.findDeviceFail();
                    break;
                case BluetoothMOSService.EXTRA_SHOW_RING_RESULT:
                    AlertWakeLock.release();
                    if (device != null && !StringUtil.isNullOrEmpty((String) msg.obj)) {
                        String[] values = ((String) msg.obj).split(",");
                        device.setPlaybackResult(Double.parseDouble(values[0]));
                        device.setRecordResult(Double.parseDouble(values[1]));
                        device.setRingResult(Double.parseDouble(values[2]));
                    }
                    fragment.showRingTestResult();
                    break;
                case BluetoothMOSService.EXTRA_HADSET_OFF:
                    AlertWakeLock.release();
                    fragment.mConnectDevice.setChecked(false);
                    fragment.showDeviceInfo();
                    fragment.showRingTestResult();
                    break;
                case BluetoothMOSService.EXTRA_SHOW_PARAMS:
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
                case BluetoothMOSService.EXTRA_SHOW_FILES:
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
                    BluetoothMOSDevice currMTCDevice = BluetoothMOSFactory.get().getCurrMTCDevice();
                    if (null == currMTCDevice) {
                        fragment.showDiscoveredBluetoothList(discoveredDevices);
                    } else {
                        //?????????????????????
                        if (null != discoveredDevices && !discoveredDevices.isEmpty()) {
                            List<BluetoothMOSDevice> filteredDevices = new ArrayList<>();
                            for (BluetoothMOSDevice mosDevice : discoveredDevices) {
                                if (mosDevice.getAddress().equals(currMTCDevice.getAddress())) {
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
     * ????????????
     */
    private void connectInterrupt() {
        Toast.makeText(getContext(), R.string.task_callMOS_device_disconnected, Toast.LENGTH_SHORT).show();
        this.mFactory.setCurrMOCDevice(null);
        this.dismissProgress();
        this.showDeviceInfo();
        this.showRingTestResult();
        this.mConnectDevice.setChecked(false);
    }

    /**
     * ????????????????????????
     */
    private void findDeviceFail() {
        Toast.makeText(getContext(), R.string.task_callMOS_device_no_finded, Toast.LENGTH_SHORT).show();
        this.mFactory.setCurrMOCDevice(null);
        this.dismissProgress();
        this.showDeviceInfo();
        this.showRingTestResult();
        this.mConnectDevice.setChecked(false);
    }

    /**
     * ??????????????????
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
                if (BluetoothMOSFactory.get().getCurrMOCDevice() != null) {
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
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * ????????????
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
            mFactory.setCurrMOCDevice(device);
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
     * ????????????????????????????????????
     */
    private void showDiscoveredBluetoothList(final List<BluetoothMOSDevice> discoveredDevices) {
        LogUtil.d(TAG, "----showDiscoveredBluetoothList----");
        if (null != discoveredDevices && !discoveredDevices.isEmpty()) {
            //            String[] deviceMacAddresses = new String[discoveredDevices.size()];
            //            for (int i = 0; i < discoveredDevices.size(); i++) {
            //                BluetoothMOSDevice device = discoveredDevices.get(i);
            //                deviceMacAddresses[i] = device.getAddress();
            //            }
            //            BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
            //            BasicDialog dialog = builder.setTitle(R.string.task_callMOS_select_moc_bluetooth_device).setSingleChoiceItems(deviceMacAddresses, 0, new DialogInterface.OnClickListener() {
            //                @Override
            //                public void onClick(DialogInterface dialog, int which) {
            //                    BluetoothMOSDevice selectedDev = discoveredDevices.get(which);
            //                    try {
            //                        mServiceBinder.connect(selectedDev.getAddress());
            //                    } catch (RemoteException e) {
            //                        e.printStackTrace();
            //                    }
            //                }
            //            }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
            //                @Override
            //                public void onClick(DialogInterface dialog, int which) {
            //                    dialog.dismiss();
            //                    mHandler.sendEmptyMessage(BluetoothMTCService.EXTRA_SHOW_PROGRESS_DISMISS);
            //                    mConnectDevice.setChecked(false);
            //                }
            //            }).show();
            //            dialog.setCancelable(false);
            showDeviceDialog(discoveredDevices);
        } else {
            mHandler.sendEmptyMessage(BluetoothMOSService.EXTRA_SHOW_PROGRESS_DISMISS);
            this.mConnectDevice.setChecked(false);
            Toast.makeText(getContext(), R.string.task_callMOS_no_bluetooth_mos_device_found, Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog dialog;
    private List<BluetoothMOSDevice> mDiscoveredDevices = new ArrayList<>();

    private void showDeviceDialog(final List<BluetoothMOSDevice> discoveredDevices) {
        mHandler.sendEmptyMessage(BluetoothMOSService.EXTRA_SHOW_PROGRESS_DISMISS);
        if (discoveredDevices != null) {
            mDiscoveredDevices.clear();
            mDiscoveredDevices.addAll(discoveredDevices);
        }
        if (dialog == null) {
            dialog = new Dialog(getContext(), R.style.Translucent_NoTitle);
            dialog.setCanceledOnTouchOutside(false);
            View layout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_single_select, null);
            TextView title = ButterKnife.findById(layout, R.id.tv_title);
            ListView list = ButterKnife.findById(layout, R.id.lv_list);

            title.setText("????????????");
            ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.simple_list_item_single_choice, mDiscoveredDevices);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    BluetoothMOSDevice selectedDev = mDiscoveredDevices.get(position);
                    try {
                        String tips = getResources().getString(R.string.task_callMOS_connect_device_doing);
                        mHandler.obtainMessage(BluetoothMOSService.EXTRA_SHOW_PROGRESS, tips).sendToTarget();
                        mServiceBinder.connect(selectedDev.getAddress());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.setContentView(layout);
        } else {
            ListView list = ButterKnife.findById(dialog, R.id.lv_list);
            ((ArrayAdapter) list.getAdapter()).notifyDataSetChanged();
        }
        if (!dialog.isShowing()) {
            dialog.show();
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = DensityUtil.dip2px(getContext(), 280);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "------onDestroy------");
        this.unbindMOSService();
        super.onDestroy();
    }

    /**
     * ????????????MOS???????????????
     */
    private void unbindMOSService() {
        LogUtil.d(TAG, "--------------unbindMOSService---------------");
        if (isBindMOSService) {
            isBindMOSService = false;
            this.mServiceBinder = null;
            Intent intent = new Intent(getContext(), BluetoothMOSService.class);
            getContext().stopService(intent);
            getContext().unbindService(conn);
        }
    }

    /**
     * ????????????MOS???
     */
    private void writeFileToMOS() {
        if (mServiceBinder == null || this.mFactory.getCurrMOCDevice() == null) {
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
     * ?????????????????????
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
     * ??????????????????
     */
    private void syncNetWorkTime() {
        new Thread() {

            @Override
            public void run() {
                if (!MyPhoneState.getInstance().isNetworkAvirable(getContext().getApplicationContext())) {
                    setDataEnabled(true);
                }
                long time = UtilsMethod.getNetTimeByBjtime();
                // ?????????????????????????????????????????????????????????
                LogUtil.d(TAG, "sync net work time:" + UtilsMethod.sdFormat.format(new Date(time)));
                LogUtil.d(TAG, "server time offset:" + (System.currentTimeMillis() - time));
                ApplicationModel.getInstance().setServerTimeOffset(System.currentTimeMillis() - time);
                //				UtilsMethod.setTime(time,getApplicationContext());
            }

        }.start();
    }


}


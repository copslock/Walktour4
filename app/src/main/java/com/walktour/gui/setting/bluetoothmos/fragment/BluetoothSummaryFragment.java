package com.walktour.gui.setting.bluetoothmos.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.util.Locale;

/**
 * 蓝牙MOS头的汇总消息
 * 
 * @author jianchao.wang
 *
 */
public class BluetoothSummaryFragment extends Fragment implements OnClickListener {
	/** 连接状态：已连接 */
	public static final int CONNECT_STATE_YES = 1;
	/** 连接状态：未连接 */
	public static final int CONNECT_STATE_NO = 0;
	/** 设备名称编辑框 */
	private EditText mDeviceName;
	/** MAC地址编辑框 */
	private EditText mMacAddress;
	/** 信号强度编辑框 */
	private EditText mSignalLevel;
	/** 设备电量编辑框 */
	private EditText mDevicePower;
	/** 设备放音音量编辑框 */
	private EditText mDevicePlaybackVolume;
	/** 设备录音音量编辑框 */
	private EditText mDeviceRecordVolume;
	/** 设备软件版本号 */
	private EditText mDeviceVersion;
	/** 设备已有文件浏览按钮 */
	private Button mDeviceHaveFilesBtn;
	/** 回环分值编辑框 */
	private EditText mRingResult;
	/** 当前的设备 */
	private BluetoothMOSDevice mDevice;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bluetoothmos_summary, container);
		this.mDeviceName = (EditText) view.findViewById(R.id.edit_device_name);
		this.mMacAddress = (EditText) view.findViewById(R.id.edit_mac_address);
		this.mSignalLevel = (EditText) view.findViewById(R.id.edit_signal_level);
		this.mDevicePower = (EditText) view.findViewById(R.id.edit_device_power);
		this.mDevicePlaybackVolume = (EditText) view.findViewById(R.id.edit_device_playback_volume);
		this.mDeviceRecordVolume = (EditText) view.findViewById(R.id.edit_device_record_volume);
		this.mDeviceVersion = (EditText) view.findViewById(R.id.edit_device_version);
		this.mDeviceHaveFilesBtn = (Button) view.findViewById(R.id.show_device_have_files_btn);
		this.mDeviceHaveFilesBtn.setOnClickListener(this);
		this.mRingResult = (EditText) view.findViewById(R.id.edit_ring_result);
		return view;
	}

	/**
	 * 设置设备信息
	 * 
	 * @param device
	 *          设备对象
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
	 * 设置连接状态
	 * 
	 * @param state
	 *          0 未连接，1 已连接
	 */
	public void setConnectState(int state) {
		if (state != CONNECT_STATE_YES) {
			this.mDevicePower.setText("--");
			this.mRingResult.setText("--/--");
		}
	}

	/**
	 * 设置回环检测分值
	 * 
	 * @param playbackResult
	 *          放音检测分值
	 * @param recordResult
	 *          录音检测分值
	 */
	public void setRingResult(double playbackResult, double recordResult) {
		this.mRingResult.setText(String.format(Locale.getDefault(), "↓%.2f/↑%.2f", playbackResult, recordResult));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_device_have_files_btn:
			this.showDeviceHaveFilesDialog();
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

}

package com.walktour.service.bluetoothmos.command;

import com.walktour.base.util.LogUtil;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 获取MOS头参数命令
 * 
 * @author jianchao.wang
 *
 */
public class GetParamsCommand extends BaseCommand {
	/** MOS头当前的放音音量 */
	private int mPlaybackVolume;
	/** MOS头当前电量 */
	private int mPower;
	/** 获取当前设备的录音音量 */
	private int mRecordVolume;
	/** 获取当前设备的软件版本号 */
	private String mVersion;

	public GetParamsCommand(BluetoothMOSDevice device) {
		super(CommandType.get_params, device);
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		try {
			String[] params = new String(super.mACKMessage, "ASCII").split(",");
			this.mPlaybackVolume = Integer.parseInt(params[0]);
			this.mPower = Integer.parseInt(params[1]);
			if (params.length > 2) {
				this.mRecordVolume = Integer.parseInt(params[2]);
				this.mVersion = params[3];
			}
			LogUtil.d(mTag, "power=" + this.mPower + "%,playbackVolume=" + this.mPlaybackVolume + ",recordVolume="
					+ this.mRecordVolume + ",version=" + this.mVersion);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(mTag, e.getMessage(), e.fillInStackTrace());
		}
		super.finish();
	}

	@Override
	protected void dealRespData(byte[] data) {
		// 无需实现

	}

	public int getPlaybackVolume() {
		return mPlaybackVolume;
	}

	public int getPower() {
		return mPower;
	}

	public int getRecordVolume() {
		return mRecordVolume;
	}

	public String getVersion() {
		return mVersion;
	}

}

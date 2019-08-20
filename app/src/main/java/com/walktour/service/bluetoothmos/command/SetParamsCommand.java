package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 设置MOS头参数命令
 * 
 * @author jianchao.wang
 *
 */
public class SetParamsCommand extends BaseCommand {
	/** MOS头当前的放音音量 */
	private int mPlaybackVolume;
	/** MOS头当前的录音音量 */
	private int mRecordVolume;

	public SetParamsCommand(BluetoothMOSDevice device, int playbackVolume, int recordVolume) {
		super(CommandType.set_params, device);
		this.mPlaybackVolume = playbackVolume;
		this.mRecordVolume = recordVolume;
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		super.finish();
	}

	@Override
	protected void dealRespData(byte[] data) {
		// 无需实现

	}

	public int getPlaybackVolume() {
		return mPlaybackVolume;
	}

	@Override
	public void runCommand() {
		String str = this.mPlaybackVolume + "," + this.mRecordVolume;
		byte[] cmd = super.createRequestCmd(str.getBytes());
		super.sendRequestCmd(cmd);
	}

}

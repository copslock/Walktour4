package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 初始化MOS头芯片命令
 * 
 * @author jianchao.wang
 *
 */
public class InitMOSCommand extends BaseCommand {

	/** 文件类型 */
	private final FileType mFileType;
	/** 是否放音，否则是录音 */
	private final boolean isPlayer;

	/**
	 * 初始化
	 * 
	 * @param device
	 *          设备对象
	 * @param fileType
	 *          文件类型
	 * @param isPlayer
	 *          是否放音，否则是录音
	 */
	public InitMOSCommand(BluetoothMOSDevice device, FileType fileType, boolean isPlayer) {
		super(CommandType.init_mos, device);
		this.mFileType = fileType;
		this.isPlayer = isPlayer;
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

	@Override
	public void runCommand() {
		byte[] msg = new byte[2];
		msg[0] = hexIntToBytes(this.isPlayer ? 0 : 1, 1)[0];
		msg[1] = hexIntToBytes(this.mFileType.getSampleRate(), 1)[0];
		byte[] cmd = super.createRequestCmd(msg);
		super.sendRequestCmd(cmd);
	}

}

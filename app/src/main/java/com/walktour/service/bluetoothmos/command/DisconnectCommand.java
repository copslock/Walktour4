package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 断开连接命令，不用发送到蓝牙头
 * 
 * @author jianchao.wang
 *
 */
public class DisconnectCommand extends BaseCommand {

	public DisconnectCommand(BluetoothMOSDevice device) {
		super(CommandType.disconnect, device);
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		// 无需实现
	}

	@Override
	protected void dealRespData(byte[] data) {
		// 无需实现

	}

	@Override
	public void runCommand() {
		super.finish();
	}

}

package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 反初始化MOS头芯片命令
 * 
 * @author jianchao.wang
 *
 */
public class UninitMOSCommand extends BaseCommand {

	public UninitMOSCommand(BluetoothMOSDevice device) {
		super(CommandType.uninit_mos, device);
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

}

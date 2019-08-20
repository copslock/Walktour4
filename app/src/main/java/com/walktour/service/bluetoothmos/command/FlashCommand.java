package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 擦除数据命令
 * 
 * @author jianchao.wang
 *
 */
public class FlashCommand extends BaseCommand {

	public FlashCommand(BluetoothMOSDevice device) {
		super(CommandType.flash_data, device);
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

package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 连接验证命令
 * 
 * @author jianchao.wang
 *
 */
public class ConnectCheckCommand extends BaseCommand {
	/** 当前MOS头是否可以连接 */
	private boolean isCanConnect = false;
	/** 本机地址 */
	private String mAddress;

	/**
	 * 初次连接时调用
	 * 
	 * @param device
	 * @param address
	 */
	public ConnectCheckCommand(BluetoothMOSDevice device, String address) {
		super(CommandType.connect_check, device);
		this.mAddress = address;
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		if (super.isACKOK)
			this.isCanConnect = true;
		super.finish();
	}

	@Override
	protected void dealRespData(byte[] data) {
		// 无需实现

	}

	public boolean isCanConnect() {
		return isCanConnect;
	}

	@Override
	public void runCommand() {
		byte[] cmd = super.createRequestCmd(mAddress.getBytes());
		super.sendRequestCmd(cmd);
	}

}

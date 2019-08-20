package com.walktour.service.bluetoothmos.command;

import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 连接确认命令
 * 
 * @author jianchao.wang
 *
 */
public class ConnectConfirmCommand extends BaseCommand {
	/** 验证反馈标识:成功 */
	public static final int FLAG_SUCCESS = 1;
	/** 验证反馈标识:失败 */
	public static final int FLAG_FAIL = 2;
	/** 信息标识 1验证成功反馈,2验证失败反馈 */
	private int mFlag = FLAG_FAIL;

	/**
	 * 确认连接时调用
	 * 
	 * @param device
	 */
	public ConnectConfirmCommand(BluetoothMOSDevice device, int flag) {
		super(CommandType.connect_confirm, device);
		this.mFlag = flag;
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
		byte[] cmd = super.createRequestCmd(hexIntToBytes(mFlag, 1));
		super.sendRequestCmd(cmd);
	}

	public int getFlag() {
		return mFlag;
	}

}

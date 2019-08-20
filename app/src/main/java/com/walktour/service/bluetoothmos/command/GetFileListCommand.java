package com.walktour.service.bluetoothmos.command;

import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

/**
 * 获取MOS头已有音频文件列表命令
 * 
 * @author jianchao.wang
 *
 */
public class GetFileListCommand extends BaseCommand {
	/** MOS头已有音频文件ID数组 */
	private String mFileIDs;

	public GetFileListCommand(BluetoothMOSDevice device) {
		super(CommandType.get_file_ids, device);
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		try {
			this.mFileIDs = new String(super.mACKMessage, "ASCII");
			if (!StringUtil.isNullOrEmpty(mFileIDs)) {
				String[] ids = this.mFileIDs.split(",");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < ids.length; i++) {
					if (!StringUtil.isNullOrEmpty(ids[i])) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append(ids[i].trim());
					}
				}
				this.mFileIDs = sb.toString();
			}
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

	public String getFileIDs() {
		return mFileIDs;
	}

}

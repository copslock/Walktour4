package com.walktour.gui.share.download;

import com.walktour.gui.weifuwu.business.model.ShareFileModel;

/**
 * 下载业务连接器
 * 
 * @author zhihui.lian
 */
public class ViewHolder {

	protected ShareFileModel shareFileModel;

	public ViewHolder(ShareFileModel shareFileModel) {
		this.shareFileModel = shareFileModel;
	}

	public ShareFileModel getShareFileModel() {
		return shareFileModel;
	}

	public void update(ShareFileModel shareFileModel) {
		this.shareFileModel = shareFileModel;
	}

}

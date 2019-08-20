package com.walktour.gui.setting.customevent.msg;

import android.content.Intent;

import com.walktour.Utils.StringUtil;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.BaseCustomEventListActivity;
import com.walktour.gui.setting.customevent.model.CustomEventMsg;

/**
 * 自定义信令事件管理类
 * 
 * @author jianchao.wang
 *
 */
public class CustomEventMsgListActivity extends BaseCustomEventListActivity<CustomEventMsg> {

	@Override
	protected void genAdapter() {
		mAdapter = new CustomEventMsgAdapter(this, R.layout.listview_item_customevent,
				super.mFactory.getCustomEventMsgList(), false);
		eventType= BaseCustomEventListActivity.TYPE_MSG;
	}

	@Override
	protected void toEditEvent(String eventName) {
		Intent intent = new Intent(mContext, CustomEventMsgEditActivity.class);
		if (!StringUtil.isNullOrEmpty(eventName))
			intent.putExtra("name", eventName);
		startActivity(intent);
	}
}

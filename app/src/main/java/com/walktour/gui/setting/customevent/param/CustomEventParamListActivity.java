package com.walktour.gui.setting.customevent.param;

import android.content.Intent;

import com.walktour.Utils.StringUtil;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.BaseCustomEventListActivity;
import com.walktour.gui.setting.customevent.model.CustomEventParam;

/**
 * 自定义参数事件管理类
 * 
 * @author jianchao.wang
 *
 */
public class CustomEventParamListActivity extends BaseCustomEventListActivity<CustomEventParam> {

	@Override
	protected void genAdapter() {
		mAdapter = new CustomEventParamAdapter(this, R.layout.listview_item_customevent,
				super.mFactory.getCustomEventParamList(), false);
		eventType= BaseCustomEventListActivity.TYPE_PARAM;
	}

	@Override
	protected void toEditEvent(String eventName) {
		Intent intent = new Intent(mContext, CustomEventParamEditActivity.class);
		if (!StringUtil.isNullOrEmpty(eventName))
			intent.putExtra("name", eventName);
		startActivity(intent);
	}

}

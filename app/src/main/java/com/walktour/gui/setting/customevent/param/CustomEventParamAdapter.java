package com.walktour.gui.setting.customevent.param;

import android.annotation.SuppressLint;

import com.walktour.control.config.ParameterSetting;
import com.walktour.gui.setting.customevent.BaseCustomEventAdapter;
import com.walktour.gui.setting.customevent.model.CustomEventParam;
import com.walktour.gui.setting.customevent.model.Param;

import java.util.List;
import java.util.Locale;

/**
 * 自定义参数事件列表适配类
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("InflateParams")
public class CustomEventParamAdapter extends BaseCustomEventAdapter<CustomEventParam> {
	/** 参数设置 */
	protected ParameterSetting mParamSetting;

	public CustomEventParamAdapter(CustomEventParamListActivity context, int textViewResourceId,
			List<CustomEventParam> objects, boolean isCheckMode) {
		super(context, textViewResourceId, objects, isCheckMode);
		this.mParamSetting = ParameterSetting.getInstance();
	}

	@Override
	protected String getDescription(CustomEventParam define) {
		String content = "";
		if (define.getParams() != null) {
			Param[] params = define.getParams();
			for (Param param : params) {
				if (param != null) {
					String name = mParamSetting.getParamShortName(param.id);
					name = (name == null ? "" : name);
					content += String.format(Locale.getDefault(), "%s%s%.2f\n", name, param.getComapreStr(),
							name.toLowerCase(Locale.getDefault()).endsWith("(k)") ? param.value / 1000 : param.value);
				}
			}
		}
		return content;
	}
}

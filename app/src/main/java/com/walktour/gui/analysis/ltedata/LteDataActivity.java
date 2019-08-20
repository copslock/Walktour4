package com.walktour.gui.analysis.ltedata;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.analysis.db.AnalysisDBManage;
import com.walktour.gui.analysis.view.ColumnView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/***
 * LTE DATA数据业务异常详情
 * 
 * @author weirong.fan
 *
 */
@SuppressWarnings("deprecation")
public class LteDataActivity extends BasicActivity {
	/** 具体异常原因占比 **/
	private Map<String, Integer> mapTotal = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.analysis_activity_ltedata);
		this.initTextView(R.id.title_txt).setText(R.string.intelligent_analysis_ltedata_title);
		Bundle bundle = getIntent().getExtras();
		ArrayList<String> ltetotal = bundle.getStringArrayList("ltetotal");
		for (String key : ltetotal) {
			if (mapTotal.keySet().contains(key)) {
				mapTotal.put(key, mapTotal.get(key) + 1);
			} else {
				mapTotal.put(key, 1);
			}
		}
		initView();
	}


	private void initView() {
		this.initImageButton(R.id.pointer).setOnClickListener(this);
		Map<String, String> map = AnalysisDBManage.getInstance().getLTEDataTotalFTPDownload();
		if (null != map && map.size() > 0) {
			this.initTextView(R.id.ltedown1).setText(StringUtil.formatString(map.containsKey("Attempts") ? map.get("Attempts") + "" : ""));
			this.initTextView(R.id.ltedown2).setText(StringUtil.formatString(map.containsKey("SuccessCount") ? map.get("SuccessCount") + "" : ""));
			this.initTextView(R.id.ltedown3).setText(StringUtil.formatString(map.containsKey("DroppedCount") ? map.get("DroppedCount") + "" : ""));
			this.initTextView(R.id.ltedown4).setText(StringUtil.formatString(map.containsKey("Average_FTP Download Rate") ? map.get("Average_FTP Download Rate") + "" : ""));
			this.initTextView(R.id.ltersrp).setText(StringUtil.formatString(map.containsKey("Average_RSRP") ? map.get("Average_RSRP") + "" : ""));
			this.initTextView(R.id.ltersrq).setText(StringUtil.formatString(map.containsKey("Average_RSRQ") ? map.get("Average_RSRQ") + "" : ""));
			this.initTextView(R.id.ltesinr).setText(StringUtil.formatString(map.containsKey("Average_SINR") ? map.get("Average_SINR") + "" : ""));
		}

		LinearLayout exception1Layout = this.initLinearLayout(R.id.exception1Layout);
		LinearLayout exception2Layout = this.initLinearLayout(R.id.exception2Layout);

		String[] xSteps = null;
		float[] values = null;
		if (mapTotal.size() > 0) {
			Set<String> keys = mapTotal.keySet();
			xSteps = new String[keys.size()];
			values = new float[keys.size()];
			Iterator<String> iter = keys.iterator();
			int index = 0;
			while (iter.hasNext()) {
				String key = iter.next();
				xSteps[index] = key;
				values[index] = mapTotal.get(key);
				index += 1;
			}
		}
		ColumnView exception1View = new ColumnView(this, xSteps, values);
		exception1View.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 900));
		exception1Layout.addView(exception1View);
		// 只有一个速率类异常
		xSteps = new String[] { getString(R.string.intelligent_analysis_ltedata_type) };
		if (mapTotal.size() > 0)
			values = new float[] { 1 };
		else
			values = new float[] {};
		ColumnView exception2View = new ColumnView(this, xSteps, values);
		exception2View.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 900));
		exception2Layout.addView(exception2View);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pointer:
			this.finish();
		}
		super.onClick(v);

	}

}

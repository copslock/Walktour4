package com.walktour.gui.analysis.volte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.analysis.db.AnalysisDBManage;
import com.walktour.model.VoiceFaildModel;

import java.util.ArrayList;
import java.util.Map;

/***
 * VoLTE异常统计分析界面
 * 
 * @author weirong.fan
 *
 */
public class VoLTEPieChartActivity extends BasicActivity implements OnClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.volte_piechart_main);
		initTextView(R.id.title_txt).setText(getString(R.string.volte_faild_title_p));
		initTextView(R.id.total_volte_exception_count).setText(this.getIntent().getStringExtra("TotalCount") + "");
		initImageView(R.id.pointer).setOnClickListener(this);
		findView();
	}

	private void findView() {
		TextView total_volte_jtl_text = initTextView(R.id.total_volte_jtl_text);
		TextView total_volte_jtl_text_mt = initTextView(R.id.total_volte_jtl_text_mt);
		TextView total_volte_dhl_text = initTextView(R.id.total_volte_dhl_text);
		TextView total_volte_dhl_text_mt = initTextView(R.id.total_volte_dhl_text_mt);
		TextView total_volte_jtsy_text = initTextView(R.id.total_volte_jtsy_text);
		TextView total_volte_jtsy_text_mt = initTextView(R.id.total_volte_jtsy_text_mt);
		AnalysisDBManage db = AnalysisDBManage.getInstance();
		try {
			Map<String, String> moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_MO);
			if (null != moMap) {
				total_volte_jtl_text.setText(StringUtil.formatPercent(moMap.get("SuccessRate")+""));
				total_volte_dhl_text.setText(StringUtil.formatPercent(moMap.get("DroppedRate")+""));
				total_volte_jtsy_text.setText(StringUtil.formatString(moMap.get("BusinessDelay_1")));
			}
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_MT);
			if (null != moMap) {
				total_volte_jtl_text_mt.setText(StringUtil.formatPercent(moMap.get("SuccessRate")+""));
				total_volte_dhl_text_mt.setText(StringUtil.formatPercent(moMap.get("DroppedRate")+""));
				total_volte_jtsy_text_mt.setText(StringUtil.formatString(moMap.get("BusinessDelay_1")));
			}

			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_1);
			initTextView(R.id.textvv1).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_2);
			initTextView(R.id.textvv2).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_3);
			initTextView(R.id.textvv3).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_4);
			initTextView(R.id.textvv4).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_5);
			initTextView(R.id.textvv5).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_6);
			initTextView(R.id.textvv6).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_7);
			initTextView(R.id.textvv7).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_8);
			initTextView(R.id.textvv8).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_9);
			initTextView(R.id.textvv9).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_10);
			initTextView(R.id.textvv10).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_11);
			initTextView(R.id.textvv11).setText(StringUtil.formatString(moMap.get("Average_average_1")));
			moMap = db.getTaleInfo(AnalysisDBManage.table_VOLTE_TOTAL_PARAM_12);
			initTextView(R.id.textvv12).setText(StringUtil.formatPercent(moMap.get("Average_average_1")));
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(final View v) {
		int viewId = v.getId();
		switch (viewId) {
		// 返回
		case R.id.pointer:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * 列表适配器
	 *
	 */

	class CustomListAdapter extends BaseAdapter {

		// 上下文
		Context context;

		ArrayList<VoiceFaildModel> adapteFiles = new ArrayList<VoiceFaildModel>();

		// 构造器
		public CustomListAdapter(Context context, ArrayList<VoiceFaildModel> adapteFiles) {
			this.context = context;
			this.adapteFiles = adapteFiles;
		}

		@Override
		public int getCount() {
			if (adapteFiles == null) {
				return 0;
			}
			return adapteFiles.size();
		}

		@Override
		public Object getItem(int position) {
			return adapteFiles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;

			if (convertView == null) {

				convertView = LayoutInflater.from(context).inflate(R.layout.csfb_err_listview_item, null);
				viewHolder = new ViewHolder();
				// 设置item中indexText的文本
				viewHolder.ItemTime = (TextView) convertView.findViewById(R.id.ItemTime);
				viewHolder.taskType = (TextView) convertView.findViewById(R.id.taskType);
				viewHolder.errDetail = (TextView) convertView.findViewById(R.id.errDetail);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			VoiceFaildModel csfbFaildModel = adapteFiles.get(position);
			viewHolder.ItemTime.setText(csfbFaildModel.getFaildTime());
			viewHolder.taskType.setText(adapteFiles.get(position).getCallType());
			viewHolder.errDetail.setText(adapteFiles.get(position).getCsfbFaildStr());

			return convertView;
		}

		private class ViewHolder {

			TextView ItemTime;

			TextView taskType;

			TextView errDetail;
		}

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.slide_in_down);
	}

}

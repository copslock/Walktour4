package com.walktour.gui.analysis.csfb;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.analysis.db.AnalysisDBManage;

import java.util.List;
import java.util.Map;

/***
 * CSFB异常汇总界面
 * 
 * @author weirong.fan
 *
 */
public class CsfbPieChartActivity extends BasicActivity implements OnClickListener { 

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		findView();
	}

	private void findView() {
		setContentView(R.layout.csfb_piechart_main);
		initTextView(R.id.title_txt).setText(getString(R.string.csfb_faild_title_p)); 
		initImageView(R.id.pointer).setOnClickListener(this);
		AnalysisDBManage db = AnalysisDBManage.getInstance();
		try {

			Map<String, String> moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_COVERAGE);
			if (null != moMap) {// 覆盖率
				initTextView(R.id.csfb_fgl_txt)
						.setText(StringUtil.formatPercent(moMap.get("SampleCoverageRate")+""));
			}
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_TOTAL_MO);
			if (null != moMap) {// CSFB主叫接通率和掉话率
				initTextView(R.id.csfb_Call_Connected_Rate_txt)
						.setText(StringUtil.formatPercent(moMap.get("SuccessRate")+""));
				initTextView(R.id.total_csfb_Call_Dropped_Rate_txt)
						.setText(StringUtil.formatPercent(moMap.get("DroppedRate")+""));
			}
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_TOTAL_MT);
			if (null != moMap) {// CSFB被叫接通率和掉话率
				initTextView(R.id.csfb_Call_Connected_Rate_txt_mt)
						.setText(StringUtil.formatPercent(moMap.get("SuccessRate")+""));
				initTextView(R.id.total_csfb_Call_Dropped_Rate_txt_mt)
						.setText(StringUtil.formatPercent(moMap.get("DroppedRate")+""));
			}
			// 主叫回落数据
			List<Float> mos = db.getCSFBDetail_MO_196();
			int sum2g = 0;
			int sum3g = 0;
			for (Float f : mos) {
				if (null != f && (f == 1 || f == 2)) {// 值为1,2时表示回落到2g
					sum2g += 1;
				}
				if (null != f && (f == 3 || f == 4 || f == 5)) {// 值为3,4,5时表示回落到3g
					sum3g += 1;
				}
			}
			if(mos.size()!=0){
				initTextView(R.id.csfb_fallback_to_2g_Rate_txt)
						.setText(StringUtil.formatPercent(sum2g / Float.parseFloat(mos.size() + "")+""));
				initTextView(R.id.csfb_fallback_to_3g_Rate_text)
						.setText(StringUtil.formatPercent(sum3g / Float.parseFloat(mos.size() + "")+""));
			}
			// 被叫回落数据
			List<Float> mts = db.getCSFBDetail_MT_196();
			sum2g = 0;
			sum3g = 0;
			for (Float f : mts) {
				if (null != f && (f == 1 || f == 2)) {// 值为1,2时表示回落到2g
					sum2g += 1;
				}
				if (null != f && (f == 3 || f == 4 || f == 5)) {// 值为3,4,5时表示回落到3g
					sum3g += 1;
				}
			}
			if(mts.size()!=0){
				initTextView(R.id.csfb_fallback_to_2g_Rate_txt_mt)
						.setText(StringUtil.formatPercent(sum2g / Float.parseFloat(mts.size() + "")+""));
				initTextView(R.id.csfb_fallback_to_3g_Rate_text_mt)
						.setText(StringUtil.formatPercent(sum3g / Float.parseFloat(mts.size() + "")+""));
			}
			
			//csfb回落2g时延主叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_TO_2G_MO_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_fallback_to_2g_Delay_text)
						.setText(StringUtil.formatString(moMap.get("DelayAvg_2")+"")); 
			}
			//csfb回落2g时延被叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_TO_2G_MT_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_fallback_to_2g_Delay_text_mt)
						.setText(StringUtil.formatString(moMap.get("DelayAvg_2")+"")); 
			}
			
			//csfb回落3g时延主叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_TO_3G_MO_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_fallback_to_3g_Delay_text)
						.setText(StringUtil.formatString(moMap.get("DelayAvg_2")+"")); 
			}
			//csfb回落3g时延被叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_TO_3G_MT_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_fallback_to_3g_Delay_text_mt)
						.setText(StringUtil.formatString(moMap.get("DelayAvg_2")+"")); 
			}
			
			//2g返回LTE时延主叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_2G_LTE_MO_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_2G_Return_to_LTE_Delay_text)
						.setText(StringUtil.formatString(moMap.get("Average_2")+"")); 
			}
			//2g返回LTE时延被叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_2G_LTE_MT_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_2G_Return_to_LTE_Delay_text_mt)
						.setText(StringUtil.formatString(moMap.get("Average_2")+"")); 
			}
			
			//3g返回LTE时延主叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_3G_LTE_MO_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_3G_Return_to_LTE_Delay_text)
						.setText(StringUtil.formatString(moMap.get("Average_2")+"")); 
			}
			//3g返回LTE时延被叫
			moMap = db.getTaleInfo(AnalysisDBManage.table_CSFB_3G_LTE_MT_DELAY);
			if (null != moMap) {
				initTextView(R.id.total_csfb_3G_Return_to_LTE_Delay_text_mt)
						.setText(StringUtil.formatString(moMap.get("Average_2")+"")); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(final View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.pointer:
			finish();
			break;
		default:
			break;
		}
	}

}

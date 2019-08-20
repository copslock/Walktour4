package com.walktour.gui.analysis.ltedata;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.walktour.Utils.StringUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.InfoTabHost;
import com.walktour.gui.R;
import com.walktour.gui.analysis.model.AnalysisModel;
import com.walktour.gui.newmap2.NewInfoTabActivity;

/***
 * LTE DATA数据业务统计
 * 
 * @author weirong.fan
 *
 */
public class LteDataDetaiActivity extends BasicActivity {
	private AnalysisModel model =null;
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		this.setContentView(R.layout.analysis_activity_ltedata_detail); 
		initView();
	}

	private void initView() {
		this.initImageButton(R.id.pointer).setOnClickListener(this);
		this.initImageButton(R.id.replaybtn).setOnClickListener(this);
		initTextView(R.id.title_txt).setText(String.format(getString(R.string.csfb_faild_detail_title),"LTE Data"));
		initTextView(R.id.reasonidsid).setText(getString(R.string.csfb_faild_reasion)+":");
		Bundle bundle = getIntent().getExtras();
		model = (AnalysisModel) bundle.get("map");
		if(null!=model){
			this.initTextView(R.id.lteexception1).setText(StringUtil.formatDate(model.getExceptionTime()+""));
			this.initTextView(R.id.lteexception2).setText(StringUtil.formatString(model.getExceptionInfo()+""));
			this.initTextView(R.id.lteexception3).setText(StringUtil.formatString(model.getOtherInfo().get("SCellEARFCN")+""));
			this.initTextView(R.id.lteexception4).setText(StringUtil.formatString(model.getOtherInfo().get("SCellPCI")+""));
			this.initTextView(R.id.lteexception5).setText(StringUtil.formatString(model.getOtherInfo().get("SCellRSRP")+""));
			this.initTextView(R.id.lteexception6).setText(StringUtil.formatString(model.getOtherInfo().get("SCellSINR")+""));
			this.initTextView(R.id.lteexception7).setText(StringUtil.formatString(model.getOtherInfo().get("BestNCellEARFCN")+""));
			this.initTextView(R.id.lteexception8).setText(StringUtil.formatString(model.getOtherInfo().get("BestNCellPCI")+""));
			this.initTextView(R.id.lteexception9).setText(StringUtil.formatString(model.getOtherInfo().get("BestNCellRSRP")+""));
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.replaybtn:
			if(null!=model){
			Intent intent = new Intent(this, NewInfoTabActivity.class);
			if (TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Map
					|| TraceInfoInterface.currentShowTab == WalkStruct.ShowInfoType.Default)
				intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_MAP);
			else
				intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, InfoTabHost.INFO_TYPE_OTHER);
			intent.putExtra("isReplay", true);
			intent.putExtra("filePath", model.getDdibFile());
			intent.putExtra("isReplayNow",true);
			intent.putExtra("startIndex",model.getStartIndex());
			intent.putExtra("endIndex",model.getEndIndex()); 
			startActivity(intent);
			overridePendingTransition(R.anim.open_next, R.anim.close_main);
			}
			break;
		case R.id.pointer:
			this.finish();
		} 
	}
}

package com.walktour.gui.analysis;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.WalkMessage;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.analysis.commons.AnalysisCommons;
import com.walktour.gui.data.FileManagerFragmentActivity;
/**
 * 分析项选择界面
 * 
 * @author weirong.fan
 *
 */
public class AnalysisMainActivity extends BasicActivity {
	/***
	 * 配置文件工具类
	 */
	SharePreferencesUtil sharePreferencesUtil=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.analysis_main);
		sharePreferencesUtil=SharePreferencesUtil.getInstance(this);
		initView();
	}

	private void initView() {
		findViewById(R.id.backBtn).setOnClickListener(this);
		findViewById(R.id.btn_analysis_cancel).setOnClickListener(this);
		findViewById(R.id.btn_analysis_submit).setOnClickListener(this);

		GridView gridview = (GridView) findViewById(R.id.analysis_main_gridview);
		gridview.setAdapter(new AnalysisMainAdapter(this));
		gridview.setOnItemClickListener(new ItemClickListener());

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_analysis_cancel:
			this.finish();
			break;
		case R.id.btn_analysis_submit:
			if(sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).trim().length()<=0){
				ToastUtil.showToastShort(AnalysisMainActivity.this,R.string.intelligent_analysis_select_scene);
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putBoolean(WalkMessage.KEY_IS_FROM_Intelligent_Analysis, true);
			jumpActivity(FileManagerFragmentActivity.class, bundle);
			break;
		}
	}

	/************************************
	 * 九宫格item按下事件类
	 **********************************/
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			AnalysisMainAdapter.ViewHolder holder=(AnalysisMainAdapter.ViewHolder)arg1.getTag();
			switch (arg1.getId()) {
			case R.drawable.ltedataanalyse_press:// lte data分析
			case R.drawable.ltedataanalyse:// lte data分析
				saveSelect(holder,AnalysisCommons.ANALYSIS_LTEDATA);
				break;
			case  R.drawable.csfbanalysis_press:// csfb分析
			case  R.drawable.csfbanalysis:// csfb分析
				saveSelect(holder,AnalysisCommons.ANALYSIS_CSFB);
				break;
			case R.drawable.volteanalyse_press:// volte分析
			case R.drawable.volteanalyse:// volte分析
				saveSelect(holder,AnalysisCommons.ANALYSIS_VOLTE);
				break;
			}
		}
	}

	/**
	 * 保存选择的场景,供下次进入使用还原
	 * 
	 * @param scene
	 */
	private void saveSelect(AnalysisMainAdapter.ViewHolder holder,String scene) {
		String value = scene + ",";
		if (sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).contains(value)) {// 如果已经选择,再次选择就是取消
			sharePreferencesUtil.saveString(AnalysisCommons.ANALYSIS_SELECT_SCENE,
					sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE).replace(value, ""));
			if(scene.equals(AnalysisCommons.ANALYSIS_CSFB)){
				holder.iconImageView.setImageResource(R.drawable.csfbanalysis);
			}else if(scene.equals(AnalysisCommons.ANALYSIS_VOLTE)){
				holder.iconImageView.setImageResource(R.drawable.volteanalyse);
			}else if(scene.equals(AnalysisCommons.ANALYSIS_LTEDATA)){
				holder.iconImageView.setImageResource(R.drawable.ltedataanalyse);
			}
			
		} else {// 如果没有选择则直接新增
			sharePreferencesUtil.saveString(AnalysisCommons.ANALYSIS_SELECT_SCENE,
					sharePreferencesUtil.getString(AnalysisCommons.ANALYSIS_SELECT_SCENE) + value);
			if(scene.equals(AnalysisCommons.ANALYSIS_CSFB)){
				holder.iconImageView.setImageResource(R.drawable.csfbanalysis_press);
			}else if(scene.equals(AnalysisCommons.ANALYSIS_VOLTE)){
				holder.iconImageView.setImageResource(R.drawable.volteanalyse_press);
			}else if(scene.equals(AnalysisCommons.ANALYSIS_LTEDATA)){
				holder.iconImageView.setImageResource(R.drawable.ltedataanalyse_press);
			}
		}
	}
}

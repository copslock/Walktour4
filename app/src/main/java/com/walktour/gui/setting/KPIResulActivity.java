package com.walktour.gui.setting;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.model.KpiResultModel;

import java.util.ArrayList;

/**
 * KPI业务结果显示
 * @author zhihui.lian
 *
 */
public class KPIResulActivity extends BasicActivity implements OnClickListener{
	
	private TextView title_txt;
	private ListView listView ;
	public String [] kpiArray = null;			//可选指标数组
	private TextView thresholdTxt;
	private TextView testResultTxt;
	
	private ArrayList<KpiResultModel> kpiResultModels = null;
	private String testName = "";
	private String testTime = "";
	private String testResult = "";
	//当所有值都为go的时候，此处才为go
	private boolean isAllGo	 = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_kpi_go);
		getIntentExtra();
		findView();
		testReportGoOrNogo();
		listView.setAdapter(new KpiSettingAdapter(this));
	}
	
	/**
	 * 获取携带过来的数据
	 */
	private void getIntentExtra(){
		if(getIntent().getExtras()!=null){
		   testName = getIntent().getStringExtra("TASK_NAME");				//任务名称
		   testTime = getIntent().getStringExtra("TEST_TIME");				//测试时间
		   testResult = getIntent().getStringExtra("TEST_RESULT");			//测试结果
//		   isAllGo = getIntent().getBooleanExtra("IS_GO", false);
		}
	}
	
	/**
	 * 测试结束时，判断是否需要生成测试报告
	 * 此处仅start报告结果的activity
	 */
	private void testReportGoOrNogo(){
		/*ArrayList<KpiSettingModel> models = ConfigKpi.getInstance().getKpiModelList();
		for(int i=0; i < models.size(); i++){
			if(models.get(i).getEnable() == 1){
				KpiResultModel kpiModel = TotalDataByGSM.isTestKpiScratch(models.get(i),this);
				if(kpiModel != null){
					kpiResultModels.add(kpiModel);
					if(!kpiModel.isScratch()){
						isAllGo = false;
					}
				}
				//LogUtil.w("=========","--" + models.get(i).getKpiShowName() + ":" + TotalDataByGSM.isTestKpiScratch(models.get(i),this));
			}
		}*/
		
//		isAllGo = TotalDataByGSM.getInstance().getGoOrNogoReport(getApplicationContext());
		kpiResultModels = TotalDataByGSM.getInstance().getGoOrNogoResult(getApplicationContext());
		for (int i=0;i<kpiResultModels.size();i++){
			if (!kpiResultModels.get(i).isScratch()){
				isAllGo=false;
				break;
			}
		}
		//如果所有值都是go，此处修改状态为go
		if(isAllGo){
			testResultTxt.setText("Go");
			testResultTxt.setTextColor(Color.GREEN);
		}else{
			testResultTxt.setText("No-Go");
			testResultTxt.setTextColor(Color.RED);
		}
		
	}
	
	/**
	 * 加载view
	 */
	private void findView(){
		title_txt = initTextView(R.id.title_txt);
		title_txt.setText("Test Result");
		listView = (ListView)findViewById(R.id.show_kpi_setting_list);
		TextView testNameTxt = initTextView(R.id.show_dataName);
		testNameTxt.setText(testName);
		TextView testTimeTxt = initTextView(R.id.show_time);
		testTimeTxt.setText(testTime);
		testResultTxt = initTextView(R.id.show_result);
		testResultTxt.setText("No-Go");
		testResultTxt.setTextColor(Color.RED);
		
		ImageView pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				KPIResulActivity.this.finish();
			}
		});
	}
	
	/**
	 * listview模拟表格Adapter
	 *
	 */
	class KpiSettingAdapter extends BaseAdapter{
		private Context context;
		public KpiSettingAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return kpiResultModels.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return kpiResultModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.show_kpi_go_item,null);
				viewHolder = new ViewHolder();
				// 设置item中indexText的文本
				viewHolder.kpi = (TextView) convertView.findViewById(R.id.text_KPI);
				viewHolder.threshold = (TextView) convertView.findViewById(R.id.text_Threshold);
				viewHolder.actualValue = (TextView) convertView.findViewById(R.id.text_ActualValue);
				viewHolder.result = (TextView)convertView.findViewById(R.id.text_Result);
				viewHolder.showLine = (View)convertView.findViewById(R.id.show_least_line);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			KpiResultModel  kpiResultModel =  kpiResultModels.get(position);
			
			viewHolder.kpi.setText(kpiResultModel.getShowKpiName());
			
			if(kpiResultModels.size() - 1 == position){
				viewHolder.showLine.setVisibility(View.VISIBLE);
			}else{
				viewHolder.showLine.setVisibility(View.GONE);
			}
			
			viewHolder.actualValue.setText(kpiResultModel.getKpiRealValue());
			viewHolder.actualValue.setTextColor(kpiResultModel.isScratch() ? Color.GREEN : Color.RED);
			
			viewHolder.threshold.setText(kpiResultModel.getActualValue());
			
			viewHolder.result.setText(kpiResultModel.getGoNoGo());
			viewHolder.result.setTextColor(kpiResultModel.isScratch() ? Color.GREEN : Color.RED);
			
			return convertView;
		}
        
        private class ViewHolder {
            TextView kpi;
            TextView threshold;
            TextView actualValue;
            TextView result;
            View showLine;
        } 
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
}

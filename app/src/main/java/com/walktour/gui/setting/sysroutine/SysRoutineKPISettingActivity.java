package com.walktour.gui.setting.sysroutine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.UtilsMethod;
import com.walktour.control.adapter.KPIAdapter;
import com.walktour.control.config.ConfigKpi;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.HeadListView;
import com.walktour.gui.R;
import com.walktour.model.KpiSettingModel;

import java.util.ArrayList;



/**
 * KPI业务指标设置界面
 * @author zhihui.lian
 *
 */
@SuppressLint("InflateParams")
public class SysRoutineKPISettingActivity extends BasicActivity implements OnClickListener , OnItemClickListener{
	
	private TextView title_txt;
	private TextView taskName;
	private TextView kpi;
	private Spinner formula;
	private HeadListView listView ;
	private EditText thresholdTxt;
	
	public String [] kpiArray = null;			//可选指标数组
	
	private ArrayList<KpiSettingModel> kpiSettingModels = new ArrayList<KpiSettingModel>();
	private KPIAdapter kpiSettingAdapters;
	private TextView unit;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sys_setting_kpi);
		findView();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	
	
	/**
	 * 加载view
	 */
	private void findView(){
		title_txt = initTextView(R.id.title_txt);
		title_txt.setText(getString(R.string.sys_setting_kpi_setting_str));
		listView = (HeadListView)findViewById(R.id.show_kpi_setting_list);
		kpiSettingModels = ConfigKpi.getInstance().getKpiModelList();
		kpiSettingAdapters = new KPIAdapter(this,kpiSettingModels);
		listView.setAdapter(kpiSettingAdapters);
		listView.setOnScrollListener(kpiSettingAdapters);
		listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.kpi_item_section, listView, false));
		listView.setOnItemClickListener(this);
		ImageView pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SysRoutineKPISettingActivity.this.finish();
			}
		});
	}
	
	
	
	
	/**
	 * 弹出对话框
	 */
	public void showDialogTip(final Integer itemPosition){
		
		KpiSettingModel  kpiModel  = kpiSettingModels.get(itemPosition);
		
		final String [] showFormulaArray = {"		>","	>=","	=","	<=","	<"};
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.show_kpi_dialog, null);
		taskName = (TextView)view.findViewById(R.id.kpi_setting_task_id);
		kpi = (TextView)view.findViewById(R.id.kpi_setting_kpi_id);
		formula = (Spinner)view.findViewById(R.id.kpi_setting_formula_id);
		unit = (TextView)view.findViewById(R.id.show_unit);
		ArrayAdapter<String> formulaArrayAP = new ArrayAdapter<String>(SysRoutineKPISettingActivity.this,
				R.layout.simple_spinner_custom_layout, showFormulaArray);
		formulaArrayAP.setDropDownViewResource(R.layout.spinner_dropdown_item);
		formula.setAdapter(formulaArrayAP);	
		
		thresholdTxt = (EditText)view.findViewById(R.id.kpi_setting_threshold_id);
		taskName.setText(kpiModel.getGroupby());
		unit.setText(String.format(getResources().getString(R.string.sys_setting_kpi_threshold_str), kpiModel.getUnits()));
		kpi.setText(UtilsMethod.getStringsByFieldName(this, kpiModel.getKpiShowName()));
		int selection = 0;
		for (int i = 0; i < showFormulaArray.length; i++) {
			if(showFormulaArray[i].trim().equals(kpiModel.getOperator())){
				selection = i;
				break;
			}
		}
		formula.setSelection(selection);
		thresholdTxt.setText(String.valueOf(kpiModel.getValue()));
		thresholdTxt.setSelection(String.valueOf(kpiModel.getValue()).length());
		
		new BasicDialog.Builder(SysRoutineKPISettingActivity.this)
		.setTitle(R.string.edit)
		.setView(view)
		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(thresholdTxt.getText().toString().trim().length() != 0){
					ConfigKpi.getInstance().setKpiEdit(itemPosition, showFormulaArray[formula.getSelectedItemPosition()].trim(), thresholdTxt.getText().toString());
					kpiSettingAdapters.notifyDataSetChanged();
				}else{
					Toast.makeText(getApplicationContext(),getString(R.string.task_alert_nullInut) , Toast.LENGTH_SHORT).show();
				}
				
			}
		})
		.setNegativeButton(R.string.str_cancle).show();
		
	}
	
	


	@Override
	public void onClick(View v) {
		
	}
	
	/**
	 * item监听
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showDialogTip(arg2);
		
	}
	
	
	
	/**
	 * 自定义listview Adapter
	 * @author Administrator
	 *
	 */
	class kpiSettingAdapter extends BaseAdapter{
		
		private Context context;
		
		public kpiSettingAdapter(Context context) {
			this.context = context;
		}
		

		@Override
		public int getCount() {
			return kpiSettingModels.size();
		}

		@Override
		public Object getItem(int position) {
			return kpiSettingModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.kpi_setting_item,null);
				viewHolder = new ViewHolder();
				// 设置item中indexText的文本
				viewHolder.showTxt = (TextView) convertView.findViewById(R.id.show_txt);
//				viewHolder.groupTxt = (TextView) convertView.findViewById(R.id.show_group_txt);
				viewHolder.isEnble = (CheckBox)convertView.findViewById(R.id.enble_checkbox);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			KpiSettingModel  kpiSettingModel =  kpiSettingModels.get(position);
			viewHolder.showTxt.setText(
					UtilsMethod.getStringsByFieldName(getApplicationContext(), kpiSettingModel.getKpiShowName()) +
					kpiSettingModel.getOperator() + kpiSettingModel.getValue() + kpiSettingModel.getUnits()
			);
			
			viewHolder.groupTxt.setText(kpiSettingModel.getGroupby());
			viewHolder.isEnble.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox buttonView = (CheckBox)v;
					ConfigKpi.getInstance().setKpiEnable(position, buttonView.isChecked() ? 1 : 0 );
					notifyDataSetChanged();
				}
			});
			
			viewHolder.isEnble.setChecked(kpiSettingModel.getEnable() == 1 ? true :false);
			
			return convertView;
		}
        
        
        private class ViewHolder {
            
            TextView showTxt;
            
            CheckBox isEnble;
            
            TextView groupTxt;
            
        } 
		
	}



	
	
}

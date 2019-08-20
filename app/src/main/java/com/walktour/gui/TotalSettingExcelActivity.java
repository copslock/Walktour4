package com.walktour.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.walktour.Utils.WalktourConst;
import com.walktour.framework.ui.BasicActivity;

/**
 * 统计设置
 * @author zhihui.lian
 *
 */
public class TotalSettingExcelActivity extends BasicActivity implements OnClickListener{
    
    private SharedPreferences preferences;
    
    private CheckBox show_report;
    
	private Spinner totalTemplate;

	private Spinner reportType;

	private Spinner totalNetWork;
    
	
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see com.walktour.framework.ui.BasicActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.total_setting_excel_activity);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        findView();
    }
    
    private void findView(){
    	(initTextView(R.id.title_txt)).setText(getResources().getString(R.string.total_totalsetting_str));
    	findViewById(R.id.pointer).setOnClickListener(this);
    	show_report = (CheckBox) findViewById(R.id.is_show_report);
    	totalTemplate = (Spinner)findViewById(R.id.Spinnertotal1);
    	reportType = (Spinner)findViewById(R.id.Spinnertotal2);
    	totalNetWork = (Spinner)findViewById(R.id.Spinnertotal3);
    	
    	findViewById(R.id.total_default).setOnClickListener(this);
    	show_report.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				preferences.edit().putBoolean(WalktourConst.TOTAL_SETTING_REPORT_ISSHOW_REPORT, isChecked).commit();
			}
		});
    	
    	
    	ArrayAdapter<String> totalTemplateSp = new ArrayAdapter<String>(TotalSettingExcelActivity.this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.total_dtCqt_array));
    	totalTemplateSp.setDropDownViewResource(R.layout.spinner_dropdown_item);
    	totalTemplate.setAdapter(totalTemplateSp);	
    	
    	totalTemplate.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				preferences.edit().putInt(WalktourConst.TOTAL_SETTING_REPORT_TOTALTEMPLATE, arg2).commit();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		
    	});
    	
    	ArrayAdapter<String> reportTypeSp = new ArrayAdapter<String>(TotalSettingExcelActivity.this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.total_showType_array));
    	reportTypeSp.setDropDownViewResource(R.layout.spinner_dropdown_item);
    	reportType.setAdapter(reportTypeSp);	
    	
    	reportType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				preferences.edit().putInt(WalktourConst.TOTAL_SETTING_REPORT_REPORTTYPE, arg2).commit();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		
    	});
    	
    	ArrayAdapter<String> totalNetWorkSp = new ArrayAdapter<String>(TotalSettingExcelActivity.this,
				R.layout.simple_spinner_custom_layout, getResources().getStringArray(R.array.total_netType_array));
    	totalNetWorkSp.setDropDownViewResource(R.layout.spinner_dropdown_item);
    	totalNetWork.setAdapter(totalNetWorkSp);	
    	
    	totalNetWork.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				preferences.edit().putInt(WalktourConst.TOTAL_SETTING_REPORT_TOTALNETWORK, arg2).commit();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		
    	});
    	
    	 show_report.setChecked(preferences.getBoolean(WalktourConst.TOTAL_SETTING_REPORT_ISSHOW_REPORT, false));
         totalTemplate.setSelection(preferences.getInt(WalktourConst.TOTAL_SETTING_REPORT_TOTALTEMPLATE, 0));
         reportType.setSelection(preferences.getInt(WalktourConst.TOTAL_SETTING_REPORT_REPORTTYPE, 0));
         totalNetWork.setSelection(preferences.getInt(WalktourConst.TOTAL_SETTING_REPORT_TOTALNETWORK, 0));
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer:
                TotalSettingExcelActivity.this.finish();
                break;
            case R.id.total_default:
            	startActivity(new Intent(TotalSettingExcelActivity.this, TotalSettingActivity.class));
            	break;
            default:
                break;
        }
        
    }
    

}

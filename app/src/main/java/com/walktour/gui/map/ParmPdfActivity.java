package com.walktour.gui.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.model.Parameter;

import java.util.ArrayList;

/**
 * 参数PDF界面呈现
 * @author zhihui.lian
 */
public class ParmPdfActivity extends Activity {

	private View viewLyout;
	private ParameterSetting parmSetting;				//设置参数列表对象
	private ArrayList<Parameter> parameterListNew = new ArrayList<Parameter>();
	private ParmPDFView parmPDFView;	
	
	private static int sp1Index = 0;
	
	private static int sp2Index = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.pdf_page);
		viewLyout = View.inflate(this.getParent().getParent(), R.layout.pdf_page, null);
		setContentView(viewLyout);
		parmSetting = ParameterSetting.getInstance();
		buildDistributeLise();
		buildDistributeIndex();
		findView();
	}
	
	/**
	 * 获取拥有阀值的集合对象
	 * @return
	 */
	private void buildDistributeLise(){
		try {
			parameterListNew.clear();
			
			ArrayList<Parameter>  paraMeterList  = parmSetting.getParameters();
			for (int i = 0; i < paraMeterList.size(); i++) {
				if (paraMeterList.get(i).getThresholdList() != null
						&& paraMeterList.get(i).getThresholdList().size() != 0 && paraMeterList.get(i).isPdfView()){
					parameterListNew.add(paraMeterList.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 进入分布界面
	 */
	private void buildDistributeIndex(){
		sp1Index = getDistributeIndex(parmSetting.getDistributionParams()[0],0);
		sp2Index = getDistributeIndex(parmSetting.getDistributionParams()[1],1);
	}
	
	private int getDistributeIndex(Parameter param,int defIndex){
		for (int i = 0; param != null && i < parameterListNew.size(); i++) {
			if (parameterListNew.get(i).getId().equals(param.getId())){
				return i;
			}
		}
		return defIndex;
	}
	
	/**
	 * 加载view
	 */
	private void findView(){
		parmPDFView = new ParmPDFView(ParmPdfActivity.this);
		if (parameterListNew != null && parameterListNew.size() > 0) {
			parmPDFView.setDefsp1Andsp2(parameterListNew.get(sp1Index).getId(), parameterListNew.get(sp2Index).getId()); 							//设置默认值
		}
//		LinearLayout  pdfLay = (LinearLayout)viewLyout.findViewById(R.id.pdfview_id);
		LinearLayout  pdfLay = (LinearLayout)findViewById(R.id.pdfview_id);
		pdfLay.addView(parmPDFView);
//		Spinner spinner_1 = (Spinner)viewLyout.findViewById(R.id.parm_pdf_sp_1);
		Spinner spinner_1 = (Spinner)findViewById(R.id.parm_pdf_sp_1);
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(ParmPdfActivity.this,R.layout.simple_spinner_custom_layout, getThrArray());
		 adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	     spinner_1.setAdapter(adapter);
	     spinner_1.setSelection(sp1Index,true);
	     spinner_1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int index, long id) {
				sp1Index = index;
				parmPDFView.setSp1Id(parameterListNew.get(sp1Index).getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		
	     });
	     
//	     BasicSpinner spinner_2 = (BasicSpinner)viewLyout.findViewById(R.id.parm_pdf_sp_2);
	     BasicSpinner spinner_2 = (BasicSpinner)findViewById(R.id.parm_pdf_sp_2);
	     adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		 spinner_2.setAdapter(adapter);
		 spinner_2.setSelection(sp2Index,true);
		 spinner_2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int index, long id) {
				sp2Index = index;
				parmPDFView.setSp2Id(parameterListNew.get(sp2Index).getId());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		
	     });
		
	}
	
	
	/**
	 * 集合转为列表数组对象
	 * @return
	 */
	private String[] getThrArray() {
		String[] arrayThr = {""};
		try {
			arrayThr = new String[parameterListNew.size()];
			for (int i = 0; i < arrayThr.length; i++) {
				arrayThr[i] = parameterListNew.get(i).getShowName();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return arrayThr;
		}
		
		return arrayThr;
	}
	
}

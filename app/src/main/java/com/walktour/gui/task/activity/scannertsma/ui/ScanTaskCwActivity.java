package com.walktour.gui.task.activity.scannertsma.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dingli.seegull.SeeGullFlags;
import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.constant.ScanTSMAConstant;
import com.walktour.gui.task.activity.scannertsma.model.CW;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;

import java.util.ArrayList;

/**
 *	RSSI配置模板界面
 * @author jinfeng.xie
 *
 */
public class ScanTaskCwActivity extends BaseScanTaskActivity implements OnItemSelectedListener {

	private TextView addChannelBtn;
	private CheckBox is3GppCbx;
	private Spinner bandwidthSpinner;
	private EditText taskNameEdt;
	private EditText scanIntervalEt;
	private Spinner detectorTypeSpinner;
	private Button saveBtn;
	private Button cancelBtn;
	private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();
	private CW cwModel = null;
	private String testType;
	private int modelPosition = 0;
	private boolean isChannelSave = false;
	private int bandwidthPostion = 0;

	private ScanTask5GOperateFactory taskFactoryIns;				//获取单例
	private int currentType; //检索类型


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_task5g_cw);
		initDate();
		getIntentData();
		initView();
		taskFactoryIns.setChannelList(cwModel.getChannelList());

	}

	private void initDate() {
		taskFactoryIns = ScanTask5GOperateFactory.getInstance();
		taskModels = taskFactoryIns.getTestModelList();
	}


	/**
	 * 获取父界面传过来的数据
	 */
	private void getIntentData(){

		Bundle bundle = getIntent().getExtras();
		testType = bundle.getString(ScanTask5GOperateFactory.TESTTYPE);
		for (int i = 0; i < taskModels.size(); i++) {
			if(taskModels.get(i).getTaskType().equals(testType)){
				cwModel = (CW)taskModels.get(i);
				modelPosition = i;
				currentType=cwModel.getDetectorType();
				break;
			}
		}

	}

	/**
	 * 处理tdscdma带宽下标为7的问题
	 * @param index  下标
	 * @param fromType 0为来自界面设回配置   1为来自配置设回界面
	 */
	private int specialTdBandWIndex(int index,int fromType){
		if(cwModel.getProtocolCode() == ProtocolCodes.PROTOCOL_TDSCDMA){
			return fromType == 0  ?  (index == 2 ? index + 5 : index) : (index == 7 ? index - 5 : index);
		}
		return index;
	}



	/**
	 * 初始化界面布局
	 */
	private void initView() {
		(initTextView(R.id.title_txt)).setText("CW");//设置标题
		findViewById(R.id.pointer).setOnClickListener(this);
		addChannelBtn = initTextView(R.id.cw_add_btn);
		addChannelBtn.setOnClickListener(this);
		is3GppCbx = (CheckBox) findViewById(R.id.cw_is3Gpp_cbx);
		is3GppCbx.setOnClickListener(this);
		bandwidthSpinner = initSpinner(R.id.cw_bandwidth_spn);
		ArrayAdapter<String> bandWidth = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				SeeGullFlags.produceBandArray(cwModel.getProtocolCode()));
		bandWidth.setDropDownViewResource(R.layout.spinner_dropdown_item);
		bandwidthSpinner.setAdapter(bandWidth);
		bandwidthSpinner.setOnItemSelectedListener(this);

		taskNameEdt = initEditText(R.id.cw_task_name_edt);
		scanIntervalEt=initEditText(R.id.cw_scanInterval_edt);
		scanIntervalEt.setText(""+cwModel.getScanInterval());
		detectorTypeSpinner =initSpinner(R.id.cw_detector_type_edt);
		String[] types=ScanTSMAConstant.Spinner.SP_SPECTURM_DETECTOR_TYPE;
		ArrayAdapter<String> detectorTypeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				types);
		detectorTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		detectorTypeSpinner.setAdapter(detectorTypeAdapter);
		detectorTypeSpinner.setSelection(currentType);
		saveBtn = initButton(R.id.btn_ok);
		saveBtn.setOnClickListener(this);
		cancelBtn = initButton(R.id.btn_cencle);
		cancelBtn.setOnClickListener(this);
		setViewValue();
	}


	/**
	 * 为控件设置值
	 */
	private void setViewValue(){
		taskNameEdt.setText(cwModel.getTaskName());
		is3GppCbx.setChecked(cwModel.isUpload());
		bandwidthPostion = specialTdBandWIndex(cwModel.getStyle(), 1);
		bandwidthSpinner.setSelection(bandwidthPostion);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ok:
				saveTestTask();
				break;

			case R.id.cw_add_btn:
				Intent intent = new Intent(ScanTaskCwActivity.this,ChannelListActivity.class);				//跳转到频点配置界面
				isChannelSave = true;
				intent.putExtra(ScanTask5GOperateFactory.NETTYPE, TestSchemaType.valueOf(cwModel.getTaskType()).getNetWorkType());
//			if(difUlOrDl){
				intent.putExtra(ScanTask5GOperateFactory.IS_UPLOAD, is3GppCbx.isChecked());
//			}
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				break;

			case R.id.btn_cencle:
				finish();
				break;
			case R.id.pointer:
				finish();
				break;
			case R.id.cw_is3Gpp_cbx:

				break;

			default:
				break;
		}
	}


	/**
	 * 获取控件值并保存
	 */

	private void saveValue(){
		cwModel.setTaskName(taskNameEdt.getText().toString());
		cwModel.setStyle(specialTdBandWIndex(bandwidthSpinner.getSelectedItemPosition(),0));
		cwModel.setUpload(is3GppCbx.isChecked());
		cwModel.setDetectorType(currentType);
		cwModel.setScanInterval(Long.valueOf(scanIntervalEt.getText().toString().trim()));
		cwModel.setChannelList(isChannelSave  ? taskFactoryIns.getChannelList() : cwModel.getChannelList());
		taskModels.remove(modelPosition);
		taskModels.add(modelPosition, cwModel);
		taskFactoryIns.setTaskModelToFile(taskModels);
		Toast.makeText(getApplicationContext(), R.string.task_alert_newSucess, Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		recoverSettingValue();
	}

	/**
	 * 设置回初始值
	 */
	private void recoverSettingValue(){
		isChannelSave = false;
		taskFactoryIns.getRestoreChannelList().clear();
	}



	@Override
	public void saveTestTask() {
		if(taskNameEdt.getText().toString().trim().length()==0){    //任务名为空
			return;
		}else if(isChannelSave  ? taskFactoryIns.getChannelList().size() == 0 : cwModel.getChannelList().size()==0){
			Toast.makeText(getApplicationContext(), R.string.sc_channels_null, Toast.LENGTH_SHORT).show();
			return;
		}else if (scanIntervalEt.getText().toString().trim().length()==0){
			Toast.makeText(getApplicationContext(), R.string.scan_intercal_null, Toast.LENGTH_SHORT).show();
			return;
		}
		saveValue();
	}



	@Override
	public void onItemSelected(AdapterView<?> parent, View view,final int position, long id) {
		if(parent.getId() == R.id.cw_bandwidth_spn){
			if(bandwidthPostion != position){
				new BasicDialog.Builder(this).setTitle(R.string.delete).setIcon(android.R.drawable.ic_menu_delete)
						.setMessage(R.string.sc_frequency_list_will_clear)
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								bandwidthPostion = position;
								taskFactoryIns.getChannelList().clear();
							}
						}).setNegativeButton(R.string.str_cancle,new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						bandwidthSpinner.setSelection(bandwidthPostion);
					}
				}).show();
			}
		}else if(parent.getId()==R.id.cw_detector_type_edt){
			currentType=position;
		}
	}



	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// 无须实现

	}
}

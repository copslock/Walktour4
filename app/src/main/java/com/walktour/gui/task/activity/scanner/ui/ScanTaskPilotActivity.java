package com.walktour.gui.task.activity.scanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dingli.seegull.ScanTaskOperateFactory;
import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.dingli.seegull.model.TopNModel;
import com.walktour.gui.R;

import java.util.ArrayList;

/**
 *
 * @author zhihui.lian
 *
 */
public class ScanTaskPilotActivity extends BaseScanTaskActivity{

	private TextView addChannelBtn;
	private CheckBox isUploadCbx;
	private EditText taskNameEdt;
	private Button saveBtn;
	private Button cancelBtn;
	private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();
	private TopNModel topNModel = null;
	private String testType;
	private EditText numberOfPilotEdt;
	private CheckBox isPscCbx;
	private EditText pscStrEdt;
	private boolean isChannelSave = false;
	private ScanTaskOperateFactory taskFactoryIns;
	private int modelPosition;
	private boolean difUlOrDl = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scantask_pilot);
		taskFactoryIns =  ScanTaskOperateFactory.getInstance();
		taskModels = taskFactoryIns.getTestModelList();
		getIntentData();
		initView();
		difUlOrDl = (topNModel.getProtocolCode() == ProtocolCodes.PROTOCOL_3GPP_WCDMA);
		taskFactoryIns.setChannelList(topNModel.getChannelList());
	}



	/**
	 * 获取父界面传过来的数据
	 */
	private void getIntentData(){

		Bundle bundle = getIntent().getExtras();
		testType = bundle.getString(ScanTaskOperateFactory.TESTTYPE);
		for (int i = 0; i < taskModels.size(); i++) {
			if(taskModels.get(i).getTaskType().equals(testType)){
				topNModel = (TopNModel)taskModels.get(i);
				modelPosition = i;
				break;
			}
		}

	}



	/**
	 * 初始化界面布局
	 */
	private void initView() {
		(initTextView(R.id.title_txt)).setText("Pilot");//设置标题
		findViewById(R.id.pointer).setOnClickListener(this);
		addChannelBtn = initTextView(R.id.pilot_add_btn);
		addChannelBtn.setOnClickListener(this);
		isUploadCbx = (CheckBox) findViewById(R.id.pilot_isUp_cbx);
		isUploadCbx.setOnClickListener(this);
		isPscCbx = (CheckBox)findViewById(R.id.pilot_isPscOrPn_cbx);
		pscStrEdt = initEditText(R.id.pilot_psc_edt);
		numberOfPilotEdt = initEditText(R.id.pilot_numberOfPilots_txt);
		taskNameEdt = initEditText(R.id.pilot_task_name_edt);
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
		taskNameEdt.setText(topNModel.getTaskName());
		isUploadCbx.setChecked(topNModel.isUpload());
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ok:
				saveTestTask();
				break;

			case R.id.btn_cencle:
				finish();
				break;
			case R.id.pilot_add_btn:
				Intent intent = new Intent(ScanTaskPilotActivity.this,ChannelListActivity.class);		//跳转到频点配置界面
				isChannelSave  = true;
				intent.putExtra(ScanTaskOperateFactory.IS_PILOT, true);
				intent.putExtra(ScanTaskOperateFactory.PROTOCOL_CODE, topNModel.getProtocolCode());
				intent.putExtra(ScanTaskOperateFactory.CHANNEL_STYLE, topNModel.getStyle());
				intent.putExtra(ScanTaskOperateFactory.IS_UPLOAD, isUploadCbx.isChecked());
				taskFactoryIns.setChannelList(topNModel.getChannelList());
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				break;
			case R.id.pointer:
				finish();
				break;
			case R.id.pilot_isUp_cbx:
				if(difUlOrDl){
					if(!taskFactoryIns.isRecoverChannelModels()){
						taskFactoryIns.setRestoreChannelList((ArrayList<ChannelModel>)taskFactoryIns.getChannelList().clone());
						taskFactoryIns.getChannelList().clear();
						taskFactoryIns.setRecoverChannelModels(true);
					}else{
						taskFactoryIns.setChannelList(taskFactoryIns.getRestoreChannelList());
						taskFactoryIns.setRecoverChannelModels(false);
					}
					Toast.makeText(getApplicationContext(), R.string.sc_frequency_list_change, Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}


	@Override
	public void finish() {
		super.finish();
		isChannelSave = false;
	}

	/**
	 * 获取控件值并保存
	 */

	private void saveValue(){
		topNModel.setTaskName(taskNameEdt.getText().toString());
		topNModel.setUpload(isUploadCbx.isChecked());
		topNModel.setChannelList(isChannelSave  ? taskFactoryIns.getChannelList() : topNModel.getChannelList());
		taskModels.remove(modelPosition);
		taskModels.add(modelPosition, topNModel);
		taskFactoryIns.setTaskModelToFile(taskModels);
		Toast.makeText(getApplicationContext(), R.string.task_alert_newSucess, Toast.LENGTH_SHORT).show();
		finish();
	}



	@Override
	public void saveTestTask() {
		boolean isSave = true;
		if(taskNameEdt.getText().toString().trim().length()==0){    //任务名为空
			isSave = false;
		}else if(numberOfPilotEdt.getText().toString().trim().length()==0){
			isSave = false;
		}else if(!isSave){
			Toast.makeText(getApplicationContext(), R.string.task_dialog_title, Toast.LENGTH_SHORT).show();
			return;
		}
		else if(isChannelSave  ? taskFactoryIns.getChannelList().size() == 0 : topNModel.getChannelList().size()==0){
			Toast.makeText(getApplicationContext(), R.string.sc_channels_null, Toast.LENGTH_SHORT).show();
			return;
		}
		saveValue();
	}

}

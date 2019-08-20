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
import com.dingli.seegull.model.ColorCodeModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.walktour.gui.R;

import java.util.ArrayList;

/**
 *
 * @author zhihui.lian
 *
 */
public class ScanTaskColorCodeActivity extends BaseScanTaskActivity{

	private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();

	private ColorCodeModel colorCodeModel = null;

	private TextView addChannelBtn;

	private CheckBox bsicCbx;

	private CheckBox ciCbx;

	private CheckBox isUploadCbx;

	private CheckBox isL3Cbx;

	private EditText taskNameEdt;

	private EditText thrEdt;

	private Button saveBtn;

	private Button cancelBtn;

	private boolean isChannelSave = false;

	private int modelPosition;

	private ScanTaskOperateFactory taskFactoryIns;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scantask_colorcode);
		taskFactoryIns =  ScanTaskOperateFactory.getInstance();
		taskModels = taskFactoryIns.getTestModelList();

		if (getIntent().getExtras() != null){
			for (int i = 0; i < taskModels.size(); i++) {
				String testType =  getIntent().getExtras().getString(ScanTaskOperateFactory.TESTTYPE);
				if(taskModels.get(i).getTaskType().equals(testType)){
					colorCodeModel = (ColorCodeModel)taskModels.get(i);
					modelPosition = i;
					break;
				}
			}
		}
		initView();
	}


	/**
	 * 初始化界面布局
	 */
	private void initView() {
		(initTextView(R.id.title_txt)).setText("Color Code");//设置标题
		findViewById(R.id.pointer).setOnClickListener(this);
		addChannelBtn = initTextView(R.id.colorcode_add_btn);
		addChannelBtn.setOnClickListener(this);
		bsicCbx = (CheckBox) findViewById(R.id.colorcode_bsic_cbx);
		ciCbx = (CheckBox) findViewById(R.id.colorcode_ci_cbx);
		isUploadCbx = (CheckBox) findViewById(R.id.colorcode_isUp_cbx);
		isL3Cbx = (CheckBox) findViewById(R.id.colorcode_l3_cbx);
		taskNameEdt = initEditText(R.id.colorcode_task_name_edt);
		thrEdt = initEditText(R.id.colorcode_Thr_edt);
		saveBtn = (Button) super.findViewById(R.id.btn_ok);
		saveBtn.setOnClickListener(this);
		cancelBtn = initButton(R.id.btn_cencle);
		cancelBtn.setOnClickListener(this);
		setViewValue();

	}

	/**
	 * 设置初始值
	 */
	private void setViewValue(){
		taskNameEdt.setText(colorCodeModel.getTaskName());
		thrEdt.setText(colorCodeModel.getRssiThreshold()+"");
		bsicCbx.setChecked(colorCodeModel.isColorCode());
		isL3Cbx.setChecked(colorCodeModel.isL3Msg());
		ciCbx.setChecked(colorCodeModel.isCI());
		isUploadCbx.setChecked(colorCodeModel.isUpload());
	}



	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.colorcode_add_btn:
				Intent intent = new Intent(ScanTaskColorCodeActivity.this,ChannelListActivity.class);		//跳转到频点配置界面
				isChannelSave = true;
				intent.putExtra(ScanTaskOperateFactory.PROTOCOL_CODE, colorCodeModel.getProtocolCode());
				intent.putExtra(ScanTaskOperateFactory.CHANNEL_STYLE, colorCodeModel.getStyle());
				intent.putExtra(ScanTaskOperateFactory.IS_UPLOAD, isUploadCbx.isChecked());
				taskFactoryIns.setChannelList(colorCodeModel.getChannelList());
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				break;

			case R.id.btn_ok:
				saveTestTask();
				break;
			case R.id.pointer:
				finish();
				break;
			case R.id.btn_cencle:
				finish();
				break;
			default:
				break;
		}

	}


	/**
	 * 获取控件值并保存
	 */

	private void saveValue(){
		colorCodeModel.setTaskName(taskNameEdt.getText().toString());
		colorCodeModel.setUpload(isUploadCbx.isChecked());
		colorCodeModel.setChannelList(isChannelSave  ? taskFactoryIns.getChannelList() : colorCodeModel.getChannelList());
		colorCodeModel.setColorCode(bsicCbx.isChecked());
		colorCodeModel.setCI(ciCbx.isChecked());
		colorCodeModel.setL3Msg(isL3Cbx.isChecked());
		colorCodeModel.setRssiThreshold(Double.valueOf(thrEdt.getText().toString()));
		taskModels.remove(modelPosition);
		taskModels.add(modelPosition, colorCodeModel);
		taskFactoryIns.setTaskModelToFile(taskModels);
		Toast.makeText(getApplicationContext(), R.string.task_alert_newSucess, Toast.LENGTH_SHORT).show();
		finish();
	}


	@Override
	public void finish() {
		super.finish();
		isChannelSave = false;
	}


	@Override
	public void saveTestTask() {
		boolean isSave = true;
		if(taskNameEdt.getText().toString().trim().length()==0){    //任务名为空
			isSave = false;
		}else if(thrEdt.getText().toString().trim().length()==0){
			isSave = false;
		}else if(!isSave){
			Toast.makeText(getApplicationContext(), R.string.task_dialog_title, Toast.LENGTH_SHORT).show();
			return;
		}
		else if(isChannelSave  ? taskFactoryIns.getChannelList().size() == 0 : colorCodeModel.getChannelList().size()==0){
			Toast.makeText(getApplicationContext(), R.string.sc_channels_null, Toast.LENGTH_SHORT).show();
			return;
		}
		saveValue();
	}



}

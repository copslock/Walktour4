package com.walktour.gui.task.activity.scanner.ui;

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

import com.dingli.seegull.ScanTaskOperateFactory;
import com.dingli.seegull.SeeGullFlags;
import com.dingli.seegull.SeeGullFlags.ProtocolCodes;
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.model.RssiModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.util.ArrayList;

/**
 *	RSSI配置模板界面
 * zhihui.lian
 *
 */
public class ScanTaskCwActivity extends BaseScanTaskActivity implements OnItemSelectedListener {

	private TextView addChannelBtn;
	private CheckBox isUploadCbx;
	private Spinner bandwidthSpinner;
	private EditText taskNameEdt;
	private Button saveBtn;
	private Button cancelBtn;
	private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();
	private RssiModel cwModel = null;
	private String testType;
	private int modelPosition = 0;
	private boolean isChannelSave = false;
	private int bandwidthPostion = 0;
	private boolean difUlOrDl = false;

	private ScanTaskOperateFactory taskFactoryIns;				//获取单例




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.scan_task_cw);
		taskFactoryIns = ScanTaskOperateFactory.getInstance();
		taskModels = taskFactoryIns.getTestModelList();
		getIntentData();
		initView();
		difUlOrDl = (cwModel.getProtocolCode() == ProtocolCodes.PROTOCOL_3GPP_WCDMA)
				||  (cwModel.getProtocolCode() == ProtocolCodes.PROTOCOL_LTE);
		taskFactoryIns.setChannelList(cwModel.getChannelList());

	}



	/**
	 * 获取父界面传过来的数据
	 */
	private void getIntentData(){

		Bundle bundle = getIntent().getExtras();
		testType = bundle.getString(ScanTaskOperateFactory.TESTTYPE);
		for (int i = 0; i < taskModels.size(); i++) {
			if(taskModels.get(i).getTaskType().equals(testType)){
				cwModel = (RssiModel)taskModels.get(i);
				modelPosition = i;
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
		isUploadCbx = (CheckBox) findViewById(R.id.cw_isUp_cbx);
		isUploadCbx.setOnClickListener(this);
		bandwidthSpinner = initSpinner(R.id.cw_bandwidth_spn);
		ArrayAdapter<String> bandWidth = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				SeeGullFlags.produceBandArray(cwModel.getProtocolCode()));
		bandWidth.setDropDownViewResource(R.layout.spinner_dropdown_item);
		bandwidthSpinner.setAdapter(bandWidth);
		bandwidthSpinner.setOnItemSelectedListener(this);
		taskNameEdt = initEditText(R.id.cw_task_name_edt);
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
		isUploadCbx.setChecked(cwModel.isUpload());
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
				intent.putExtra(ScanTaskOperateFactory.PROTOCOL_CODE, cwModel.getProtocolCode());
				intent.putExtra(ScanTaskOperateFactory.CHANNEL_STYLE, specialTdBandWIndex(bandwidthSpinner.getSelectedItemPosition(),0));
//			if(difUlOrDl){
				intent.putExtra(ScanTaskOperateFactory.IS_UPLOAD, isUploadCbx.isChecked());
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
			case R.id.cw_isUp_cbx:
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


	/**
	 * 获取控件值并保存
	 */

	private void saveValue(){
		cwModel.setTaskName(taskNameEdt.getText().toString());
		cwModel.setStyle(specialTdBandWIndex(bandwidthSpinner.getSelectedItemPosition(),0));
		cwModel.setUpload(isUploadCbx.isChecked());
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
		taskFactoryIns.setRecoverChannelModels(false);
		taskFactoryIns.getRestoreChannelList().clear();
	}



	@Override
	public void saveTestTask() {
		if(taskNameEdt.getText().toString().trim().length()==0){    //任务名为空
			return;
		}else if(isChannelSave  ? taskFactoryIns.getChannelList().size() == 0 : cwModel.getChannelList().size()==0){
			Toast.makeText(getApplicationContext(), R.string.sc_channels_null, Toast.LENGTH_SHORT).show();
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
		}
	}



	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// 无须实现

	}
}

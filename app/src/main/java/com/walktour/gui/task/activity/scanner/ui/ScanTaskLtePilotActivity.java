package com.walktour.gui.task.activity.scanner.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.dingli.seegull.model.ChannelModel;
import com.dingli.seegull.model.EtopNModel;
import com.dingli.seegull.model.ScanTaskModel;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhihui.lian
 */
public class ScanTaskLtePilotActivity extends BaseScanTaskActivity implements OnItemSelectedListener{

	private TextView addChannelBtn;
	private CheckBox isUploadCbx;
	private EditText taskNameEdt;
	private Button saveBtn;
	private Button cancelBtn;
	private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();
	private EtopNModel eTopNModel = null;
	private String testType;
	private EditText numberOfPilotEdt;
	private boolean isChannelSave = false;
	private ScanTaskOperateFactory taskFactoryIns;
	private int modelPosition;
	private EditText pilot_rssi_txt;
	private Spinner bandwidthSpinner;
	private Spinner eTOPN_refDataMode_sp;
	private int bandwidthPostion = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scantask_ltepilot);
		taskFactoryIns =  ScanTaskOperateFactory.getInstance();
		taskModels = taskFactoryIns.getTestModelList();
		getIntentData();
		initView();
//		taskFactoryIns.setChannelList(eTopNModel.getChannels());
	}



	/**
	 * 获取父界面传过来的数据
	 */
	private void getIntentData(){

		Bundle bundle = getIntent().getExtras();
		testType = bundle.getString(ScanTaskOperateFactory.TESTTYPE);
		for (int i = 0; i < taskModels.size(); i++) {
			if(taskModels.get(i).getTaskType().equals(testType)){
				eTopNModel = (EtopNModel)taskModels.get(i);
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
		numberOfPilotEdt = initEditText(R.id.pilot_numberOfPilots_txt);
		pilot_rssi_txt = initEditText(R.id.pilot_rssi_txt);
		taskNameEdt = initEditText(R.id.pilot_task_name_edt);
		bandwidthSpinner = initSpinner(R.id.eTOPN_wideBand);
		bandwidthSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<String> wideBand = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, SeeGullFlags.produceBandArray(eTopNModel.getProtocolCode()));
		wideBand.setDropDownViewResource(R.layout.spinner_dropdown_item);
		bandwidthSpinner.setAdapter(wideBand);

		eTOPN_refDataMode_sp = initSpinner(R.id.eTOPN_refDataMode);
		ArrayAdapter<String> refDataMode = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_custom_layout, RefDataMode.DB.getRefDataMode());
		refDataMode.setDropDownViewResource(R.layout.spinner_dropdown_item);
		eTOPN_refDataMode_sp.setAdapter(refDataMode);

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
		taskNameEdt.setText(eTopNModel.getTaskName());
		isUploadCbx.setChecked(eTopNModel.isUpload());
//		pscStrEdt.setText(eTopNModel.getPscStr());
//		numberOfPilotEdt.setText(eTopNModel.getNumberOfSignals()+"");
		bandwidthSpinner.setSelection(eTopNModel.getStyle());
//		eTOPN_refDataMode_sp.setSelection(eTopNModel.getRefWideBand());
//		pilot_rssi_txt.setText(String.valueOf(eTopNModel.getCarrierRssiThreshold()));
	}


	/**
	 * @author zhihui.lian
	 * 数据参考模式枚举
	 */
	public enum RefDataMode{

		DB("WIDEBAND DB",18,25,0),
		SUBBAND_DB("WIDEBAND+SUBBAND DB",19,26,1),
		RF_PATH_DB("WIDEBAND+RF_PATH DB",20,27,2),
		RF_PATH_SUB_DB("WIDEBAND+RF PATH+ SUBBAND DB",21,28,3);


		private String dataModeName;
		private int fdModeCode;
		private int tdModeCode;
		private int uiIndex;


		RefDataMode(String dataModeName,int fdModeCode,int tdModeCode,int uiIndex){
			this.dataModeName = dataModeName;
			this.fdModeCode = fdModeCode;
			this.tdModeCode = tdModeCode;
			this.uiIndex = uiIndex;
		}


		public String getDataModeName() {
			return dataModeName;
		}

		public int getFdModeCode() {
			return fdModeCode;
		}

		public int getTdModeCode() {
			return tdModeCode;
		}

		public int getUiIndex() {
			return uiIndex;
		}

		/**
		 * 返回数据模式所有数组
		 * @return
		 */
		public String[] getRefDataMode(){
			List<String> dataModeList = new ArrayList<String>();
			for (int i = 0; i < RefDataMode.values().length; i++) {
				dataModeList.add(RefDataMode.values()[i].getDataModeName());
			}
			return dataModeList.toArray(new String[dataModeList.size()]);
		}

		/**
		 * 根据界面index查出对应枚举
		 * @param index
		 * @return
		 */
		public RefDataMode getDateCode(int index){
			RefDataMode refDataMode = null;

			for (int i = 0; i < RefDataMode.values().length; i++) {
				if(RefDataMode.valueOf(RefDataMode.values()[i].name()).getUiIndex() == index){
					refDataMode = RefDataMode.values()[i];
					break;
				}
			}
			return refDataMode;
		}

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
				Intent intent = new Intent(ScanTaskLtePilotActivity.this,ChannelListActivity.class);		//跳转到频点配置界面
				isChannelSave  = true;
				intent.putExtra(ScanTaskOperateFactory.IS_LTE, true);
				intent.putExtra(ScanTaskOperateFactory.IS_PILOT, true);
				intent.putExtra(ScanTaskOperateFactory.PROTOCOL_CODE, eTopNModel.getProtocolCode());
				intent.putExtra(ScanTaskOperateFactory.CHANNEL_STYLE, bandwidthSpinner.getSelectedItemPosition());
				intent.putExtra(ScanTaskOperateFactory.IS_UPLOAD, isUploadCbx.isChecked());
//				taskFactoryIns.setChannelList(eTopNModel.getChannelList());
				startActivity(intent);
				break;
			case R.id.pointer:
				finish();
				break;
			case R.id.pilot_isUp_cbx:
				if(!taskFactoryIns.isRecoverChannelModels()){
					taskFactoryIns.setRestoreChannelList((ArrayList<ChannelModel>)taskFactoryIns.getChannelList().clone());
					taskFactoryIns.getChannelList().clear();
					taskFactoryIns.setRecoverChannelModels(true);
				}else{
					taskFactoryIns.setChannelList(taskFactoryIns.getRestoreChannelList());
					taskFactoryIns.setRecoverChannelModels(false);
				}
				Toast.makeText(getApplicationContext(), R.string.sc_frequency_list_change, Toast.LENGTH_SHORT).show();
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
		eTopNModel.setTaskName(taskNameEdt.getText().toString());
		eTopNModel.setUpload(isUploadCbx.isChecked());
//		eTopNModel.setChannelList(isChannelSave  ? taskFactoryIns.getChannelList() : eTopNModel.getChannelList());
//		eTopNModel.setNumberOfSignals(Integer.valueOf(numberOfPilotEdt.getText().toString()));
//		eTopNModel.setCarrierRssiThreshold(Double.valueOf(pilot_rssi_txt.getText().toString()));
//		eTopNModel.setRefWideBand(eTOPN_refDataMode_sp.getSelectedItemPosition());
		eTopNModel.setStyle(bandwidthSpinner.getSelectedItemPosition());
//		eTopNModel.setPscStr(pscStrEdt.getText().toString());
		taskModels.remove(modelPosition);
		taskModels.add(modelPosition, eTopNModel);
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
		else if(isChannelSave  ? taskFactoryIns.getChannelList().size() == 0 : eTopNModel.getChannelList().size()==0){
			Toast.makeText(getApplicationContext(), R.string.sc_channels_null, Toast.LENGTH_SHORT).show();
			return;
		}
		saveValue();
	}



	@Override
	public void onItemSelected(AdapterView<?> parent, View view,final int position, long id) {
		if(parent.getId() == R.id.eTOPN_wideBand){
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

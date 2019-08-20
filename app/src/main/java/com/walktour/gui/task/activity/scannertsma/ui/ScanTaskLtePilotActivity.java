package com.walktour.gui.task.activity.scannertsma.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.constant.ScanTSMAConstant;
import com.walktour.gui.task.activity.scannertsma.ennnnum.LTEL3msg;
import com.walktour.gui.task.activity.scannertsma.model.Demodulation;
import com.walktour.gui.task.activity.scannertsma.model.PilotLTE;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;
import com.walktour.model.TdL3Model;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author jinfeng.xie
 */
public class ScanTaskLtePilotActivity extends BaseScanTaskActivity implements AdapterView.OnItemSelectedListener {
	@BindView(R.id.title_txt)
	TextView titleTxt;
	@BindView(R.id.pilot_task_name_edt)
	EditText pilotTaskNameEdt;
	@BindView(R.id.pilot_add_btn)
	TextView pilotAddBtn;
	@BindView(R.id.pilot_isUp_cbx)
	CheckBox pilotIsUpCbx;
	@BindView(R.id.sp_scan_type)
	BasicSpinner spScanType;
	@BindView(R.id.sp_rssi_antenna_ports)
	BasicSpinner spRssiAntennaPorts;
	@BindView(R.id.sp_rssi_mode)
	BasicSpinner spRssiMode;
	@BindView(R.id.sp_wb_antenna_ports)
	BasicSpinner spWbAntennaPorts;
	@BindView(R.id.sp_wb_scan_speed)
	EditText spWbScanSpeed;
	@BindView(R.id.sp_wb_test_type)
	BasicSpinner spWbTestType;
	@BindView(R.id.sp_wb_show_type)
	BasicSpinner spWbShowType;
	@BindView(R.id.sp_mimo_type)
	BasicSpinner spMimoType;
	@BindView(R.id.sp_channel_zhen_type)
	BasicSpinner spChannelZhenType;
	@BindView(R.id.sp_channel_bandwidth)
	BasicSpinner spChannelBandwidth;
	@BindView(R.id.sp_channel_PCI)
	TextView spChannelPCI;
	@BindView(R.id.sp_nb_antenna_ports)
	BasicSpinner spNbAntennaPorts;
	@BindView(R.id.sp_nb_scan_speed)
	EditText spNbScanSpeed;
	@BindView(R.id.sp_nb_output_type)
	BasicSpinner spNbOutputType;
	@BindView(R.id.sp_l3msg_antenna_ports)
	BasicSpinner spL3msgAntennaPorts;
	@BindView(R.id.pilot_add_l3msg_btn)
	TextView pilotAddL3msgBtn;
	@BindView(R.id.btn_cencle)
	Button btnCencle;
	@BindView(R.id.btn_ok)
	Button btnOk;

	private int currentScantype;
	private  int currentRSSIPort;
	private  int currentRSSIMode;
	private  int currentWBPort;
	private int currentWBTestMode;
	private int currentShowMode;
	private int currentMIMOMode;
	private int currentFDDType;
	private int currentBandWidth;
	private int currentNBPort;
	private int currentNBOutmode;
	private int currentL3Port;

	private boolean isChannelSave = false;
	private ScanTask5GOperateFactory taskFactoryIns;
	private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();
	private int modelPosition;
	private PilotLTE pilotModel = null;
	private boolean difUlOrDl = false;
	private String testType;


	private Demodulation demodulationModel;
	private String[] l3Msg;
	private boolean[] l3MsgBoolean;
	private LTEL3msg[] ltel3msgs=LTEL3msg.values();

	private String[] PCis;
	private boolean[] pcisBoolean;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scantask5g_pilot_lte);
		ButterKnife.bind(this);
		taskFactoryIns = ScanTask5GOperateFactory.getInstance();
		taskModels = taskFactoryIns.getTestModelList();
		initData();
		initView();
		taskFactoryIns.setChannelList(pilotModel.getChannels());
	}


	/**
	 * 获取父界面传过来的数据
	 */
	private void initData() {

		Bundle bundle = getIntent().getExtras();
		testType = bundle.getString(ScanTask5GOperateFactory.TESTTYPE);
		for (int i = 0; i < taskModels.size(); i++) {
			if (taskModels.get(i).getTaskType().equals(testType)) {
				pilotModel = (PilotLTE) taskModels.get(i);
				modelPosition = i;

				currentL3Port=pilotModel.getDemodulation().getFrontEndSelectionMask();
				currentNBPort=pilotModel.getFrontEndSelectionMask();
				currentNBOutmode=pilotModel.getDecodeOutputMode();
				currentRSSIPort=pilotModel.getRssiSetting().getFrontEndSelectionMask();
				currentRSSIMode=pilotModel.getRssiSetting().getRssiMeasMode();
				currentWBPort=pilotModel.getwBSetting().getFrontEndSelectionMask();
				currentWBTestMode=pilotModel.getwBSetting().getMode();
				currentShowMode=pilotModel.getwBSetting().getShowMode();
				demodulationModel=pilotModel.getDemodulation();
				if (demodulationModel == null) {
					demodulationModel = new Demodulation();
				}
				break;
			}
		}
		l3Msg = new String[ltel3msgs.length];
		for (int i=0;i<ltel3msgs.length;i++){
			l3Msg[i]=ltel3msgs[i].name();
		}
		l3MsgBoolean = new boolean[l3Msg.length];
		if (demodulationModel != null && demodulationModel.getL3Models() != null) {
			for (int i = 0; i < ltel3msgs.length; i++) {
				for (int j = 0; j < demodulationModel.getL3Models().size();j++){
					if (demodulationModel.getL3Models().get(j).getId()==ltel3msgs[i].getCode()){
						l3MsgBoolean[i]=true;
					}
				}
			}
		}
		//0-503
		PCis=new String[504];
		for (int i=0;i<504;i++){
			PCis[i]=""+i;
		}
		pcisBoolean=new boolean[PCis.length];
	}

	/**
	 * 初始化界面布局
	 */
	private void initView() {
		titleTxt.setText("Pilot");//设置标题
		setSpinner(spScanType, ScanTSMAConstant.Spinner.spScanType);
		setSpinner(spRssiAntennaPorts,ScanTSMAConstant.Spinner.spRssiAntennaPorts);
		setSpinner(spWbAntennaPorts,ScanTSMAConstant.Spinner.spWbAntennaPorts);
		setSpinner(spRssiMode,ScanTSMAConstant.Spinner.spRssiMode);
		setSpinner(spWbTestType,ScanTSMAConstant.Spinner.spWbTestType);
		setSpinner(spWbShowType,ScanTSMAConstant.Spinner.spWbShowType);
		setSpinner(spMimoType,ScanTSMAConstant.Spinner.spMimoType);
		setSpinner(spChannelZhenType,ScanTSMAConstant.Spinner.spChannelZhenType);
		setSpinner(spChannelBandwidth,ScanTSMAConstant.Spinner.spChannelBandwidth);
		setSpinner(spNbAntennaPorts,ScanTSMAConstant.Spinner.spNbAntennaPorts);
		setSpinner(spNbOutputType,ScanTSMAConstant.Spinner.spNbOutputType);
		setSpinner(spL3msgAntennaPorts,ScanTSMAConstant.Spinner.spL3msgAntennaPorts);
			pilotTaskNameEdt.setText(pilotModel.getTaskName());
			spNbScanSpeed.setText(""+pilotModel.getValuePerSec());
			spWbScanSpeed.setText(""+pilotModel.getwBSetting().getMeaRate());
			spNbAntennaPorts.setSelection(pilotModel.getFrontEndSelectionMask()-1);
			spL3msgAntennaPorts.setSelection(pilotModel.getDemodulation().getFrontEndSelectionMask()-1);
			spNbOutputType.setSelection(pilotModel.getDecodeOutputMode());
			spRssiAntennaPorts.setSelection(pilotModel.getRssiSetting().getFrontEndSelectionMask()-1);
			spRssiMode.setSelection(pilotModel.getRssiSetting().getRssiMeasMode());
			spWbAntennaPorts.setSelection(pilotModel.getwBSetting().getFrontEndSelectionMask()-1);
			spWbTestType.setSelection(pilotModel.getwBSetting().getMode());
			spWbShowType.setSelection(pilotModel.getwBSetting().getShowMode()-1);

	}


	private void setSpinner(Spinner spinner, String[] objects) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				objects);
		arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(this);
	}


	@Override
	public void finish() {
		super.finish();
		isChannelSave = false;
	}

	/**
	 * 获取控件值并保存
	 */

	private void saveValue() {
		pilotModel.setTaskType(testType);
		pilotModel.setFrontEndSelectionMask(currentNBPort);
		pilotModel.getDemodulation().setFrontEndSelectionMask(currentL3Port);
		pilotModel.setDecodeOutputMode(currentNBOutmode);
		pilotModel.getRssiSetting().setFrontEndSelectionMask(currentRSSIPort);
		pilotModel.getRssiSetting().setRssiMeasMode(currentRSSIMode);
		pilotModel.getwBSetting().setFrontEndSelectionMask(currentWBPort);
		pilotModel.getwBSetting().setMode(currentWBTestMode);
		pilotModel.getwBSetting().setShowMode(currentShowMode);
		pilotModel.setValuePerSec(Integer.parseInt(spNbScanSpeed.getText().toString().trim()));
		pilotModel.getwBSetting().setMeaRate(Integer.parseInt(spWbScanSpeed.getText().toString()));
		pilotModel.setDemodulation(demodulationModel);

		pilotModel.setChannels(isChannelSave ? taskFactoryIns.getChannelList() : pilotModel.getChannels());
		taskModels.remove(modelPosition);
		taskModels.add(modelPosition, pilotModel);
		taskFactoryIns.setTaskModelToFile(taskModels);
		Toast.makeText(getApplicationContext(), R.string.task_alert_newSucess, Toast.LENGTH_SHORT).show();
		finish();
	}


	@Override
	public void saveTestTask() {
		boolean isSave = true;
		if (StringUtil.isEmpty(spWbScanSpeed.getText().toString().trim())){
			isSave=false;
		}else if(StringUtil.isEmpty(spNbScanSpeed.getText().toString().trim())){
			isSave=false;
		}
		if (!isSave){
			Toast.makeText(getApplicationContext(), R.string.task_dialog_title, Toast.LENGTH_SHORT).show();
			return;
		}
		saveValue();
	}

	void showMsgDialog(){
		new BasicDialog.Builder(this).setTitle("" + getString(R.string.custom_export_l3msg)).
					setMultiChoiceItems(l3Msg, l3MsgBoolean, new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							l3MsgBoolean[which]=isChecked;
						}
					}).setNegativeButton(R.string.str_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<TdL3Model> l3models = new ArrayList<>();
					for (int i=0;i<l3MsgBoolean.length;i++){
						if (l3MsgBoolean[i]){
							TdL3Model l3Model=new TdL3Model();
							l3Model.setId(ltel3msgs[i].getCode());
							l3Model.setL3Msg(ltel3msgs[i].name());
							l3models.add(l3Model);
							demodulationModel.setL3Models(l3models);
						}
					}
				}
			}).show();

	}

	void showPCIDialog(){
		new BasicDialog.Builder(this).setTitle("" + getString(R.string.custom_export_l3msg)).
				setMultiChoiceItems(PCis, pcisBoolean, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						pcisBoolean[which]=isChecked;
					}
				}).setNegativeButton(R.string.str_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).show();
	}

	@OnClick({R.id.pilot_add_btn, R.id.sp_channel_PCI, R.id.pilot_add_l3msg_btn,
			R.id.btn_ok, R.id.btn_cencle, R.id.pointer})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.sp_channel_PCI:
				showPCIDialog();
				break;
			case R.id.pilot_add_l3msg_btn:
				showMsgDialog();
				break;
			case R.id.btn_ok:
				saveTestTask();
				break;

			case R.id.btn_cencle:
				finish();
				break;
			case R.id.pilot_add_btn:
				Intent intent = new Intent(ScanTaskLtePilotActivity.this, ChannelListActivity.class);        //跳转到频点配置界面
				intent.putExtra(ScanTask5GOperateFactory.NETTYPE,  TestSchemaType.valueOf(pilotModel.getTaskType()).getNetWorkType());
				intent.putExtra(ScanTask5GOperateFactory.IS_LTE,true);
				intent.putExtra(ScanTask5GOperateFactory.IS_UPLOAD, pilotIsUpCbx.isChecked());
				taskFactoryIns.setChannelList(pilotModel.getChannels());
				startActivity(intent);
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				break;
			case R.id.pointer:
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()){
			case R.id.sp_rssi_mode:
				currentRSSIMode=position;
				break;
			case R.id.sp_scan_type:
				currentScantype=position;

				break;
			case R.id.sp_rssi_antenna_ports:
				currentRSSIPort=position+1;

				break;
			case R.id.sp_wb_antenna_ports:
				currentWBPort=position+1;

				break;
			case R.id.sp_wb_test_type:
				currentWBTestMode=position;

				break;
			case R.id.sp_wb_show_type:
				currentShowMode=position+1;

				break;
			case R.id.sp_mimo_type:
				currentMIMOMode=position;

				break;
			case R.id.sp_channel_zhen_type:
				currentFDDType=position;

				break;
			case R.id.sp_channel_bandwidth:
				currentBandWidth=position;

				break;
			case R.id.sp_nb_antenna_ports:
				currentNBPort=position+1;

				break;
			case R.id.sp_nb_output_type:
				currentNBOutmode=position;

				break;
			case R.id.sp_l3msg_antenna_ports:
				currentL3Port=position+1;
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}

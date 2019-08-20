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
import com.walktour.gui.task.activity.scannertsma.model.Demodulation;
import com.walktour.gui.task.activity.scannertsma.model.Pilot;
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
public class ScanTaskPilotActivity extends BaseScanTaskActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.title_txt)
    TextView titleTxt;
    @BindView(R.id.pilot_task_name_edt)
    EditText pilotTaskNameEdt;
    @BindView(R.id.pilot_isUp_cbx)
    CheckBox pilotIsUpCbx;
    @BindView(R.id.sp_scan_type)
    BasicSpinner spScanType;
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
    private Pilot pilotModel = null;
    private boolean difUlOrDl = false;
    private String testType;


    private Demodulation demodulationModel;
    private String[] l3Msg;
    private boolean[] l3MsgBoolean;
    private String[] PCis;
    private boolean[] pcisBoolean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scantask5g_pilot);
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
                pilotModel = (Pilot) taskModels.get(i);
                modelPosition = i;

                currentL3Port=pilotModel.getFrontEndSelectionMask();
                currentNBOutmode=pilotModel.getDecodeOutputMode();
                demodulationModel=pilotModel.getDemodulation();
                if (demodulationModel == null) {
                    demodulationModel = new Demodulation();
                }
                break;
            }
        }
        l3Msg = new String[]{"1", "2", "3", "4"};
        l3MsgBoolean = new boolean[l3Msg.length];
        if (demodulationModel != null && demodulationModel.getL3Models() != null) {
            for (int i = 0; i < l3Msg.length; i++) {
                for (int j = 0; j < demodulationModel.getL3Models().size();j++){
                    if (String.valueOf(demodulationModel.getL3Models().get(j).getId()).equals(l3Msg[i])){
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
        setSpinner(spScanType,new String[]{"TopN","User list"});
        setSpinner(spMimoType,new String[]{"Not Test","2*2"});
        setSpinner(spChannelZhenType,new String[]{"FDD","TDD"});
        setSpinner(spChannelBandwidth,new String[]{"Auto","1.4MHz","3MHz","5MHz","10MHz","15MHz","20MHz"});
        setSpinner(spNbAntennaPorts,new String[]{"RF1","RF2"});
        setSpinner(spNbOutputType,new String[]{"Real Time","Buffer"});
        setSpinner(spL3msgAntennaPorts,new String[]{"RF1","RF2"});
        pilotTaskNameEdt.setText(pilotModel.getTaskName());
        spNbScanSpeed.setText(""+pilotModel.getValuePerSec());
        spL3msgAntennaPorts.setSelection(pilotModel.getFrontEndSelectionMask());
        spNbOutputType.setSelection(pilotModel.getDecodeOutputMode());

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

        pilotModel.setFrontEndSelectionMask(currentL3Port);
        pilotModel.setDecodeOutputMode(currentNBPort);
        pilotModel.setValuePerSec(Integer.parseInt(spNbScanSpeed.getText().toString().trim()));

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
        if(StringUtil.isEmpty(spNbScanSpeed.getText().toString().trim())){
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
                        l3Model.setId(i);
                        l3Model.setL3Msg(l3Msg[i]);
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
                Intent intent = new Intent(ScanTaskPilotActivity.this, ChannelListActivity.class);        //跳转到频点配置界面
                intent.putExtra(ScanTask5GOperateFactory.IS_PILOT, true);
                intent.putExtra(ScanTask5GOperateFactory.NETTYPE, TestSchemaType.valueOf(pilotModel.getTaskType()).getNetWorkType());
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
            case R.id.sp_scan_type:
                currentScantype=position;

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
                currentNBPort=position;

                break;
            case R.id.sp_nb_output_type:
                currentNBOutmode=position;

                break;
            case R.id.sp_l3msg_antenna_ports:
                currentL3Port=position;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

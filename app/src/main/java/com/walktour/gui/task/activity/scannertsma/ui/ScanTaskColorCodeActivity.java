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
import com.walktour.gui.task.activity.scannertsma.ennnnum.GSML3msg;
import com.walktour.gui.task.activity.scannertsma.model.ColorCode;
import com.walktour.gui.task.activity.scannertsma.model.Demodulation;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;
import com.walktour.model.TdL3Model;

import java.util.ArrayList;

/**
 * @author jinfeng.xie
 */
public class ScanTaskColorCodeActivity extends BaseScanTaskActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ScanTaskColorCodeActivi";
    private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();

    private ColorCode colorCodeModel = null;

    private TextView addChannelBtn;

    private TextView addL3msgBtn;


    private CheckBox isUploadCbx;


    private Spinner outputTypeSpinner;

    private EditText taskNameEdt;

    private EditText speedEdt;

    private Button saveBtn;

    private Button cancelBtn;

    private boolean isChannelSave = false;

    private int modelPosition;

    private ScanTask5GOperateFactory taskFactoryIns;

    private Demodulation demodulationModel;
    private int decodeOutputMode;
    private String[] l3Msg;
    private boolean[] l3MsgBoolean;
    private GSML3msg[] gsml3msgs= GSML3msg.values();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scantask5g_colorcode);
        initDate();
        initView();
    }

    private void initDate() {
        taskFactoryIns = ScanTask5GOperateFactory.getInstance();
        taskModels = taskFactoryIns.getTestModelList();
        if (getIntent().getExtras() != null) {
            for (int i = 0; i < taskModels.size(); i++) {
                String testType = getIntent().getExtras().getString(ScanTask5GOperateFactory.TESTTYPE);
                if (taskModels.get(i).getTaskType().equals(testType)) {
                    colorCodeModel = (ColorCode) taskModels.get(i);
                    demodulationModel = colorCodeModel.getDemodulation();
                    if (demodulationModel == null) {
                        demodulationModel = new Demodulation();
                    }
                    decodeOutputMode=colorCodeModel.getDecodeOutputMode();
                    modelPosition = i;
                    break;
                }
            }
        }
        l3Msg=new String[gsml3msgs.length];
       for (int i=0;i<gsml3msgs.length;i++){
            l3Msg[i]=gsml3msgs[i].name();
       }
        l3MsgBoolean = new boolean[gsml3msgs.length];
        if (demodulationModel != null && demodulationModel.getL3Models() != null) {
            for (int i = 0; i < gsml3msgs.length; i++) {
                for (int j = 0; j < demodulationModel.getL3Models().size();j++){
                    if (demodulationModel.getL3Models().get(j).getId()==(gsml3msgs[i].getCode())){
                        l3MsgBoolean[i]=true;
                    }
                }
            }
        }
    }


    /**
     * 初始化界面布局
     */
    private void initView() {
        (initTextView(R.id.title_txt)).setText("Color Code");//设置标题
        findViewById(R.id.pointer).setOnClickListener(this);
        addChannelBtn = initTextView(R.id.colorcode_add_btn);
        addChannelBtn.setOnClickListener(this);
        addL3msgBtn = initTextView(R.id.colorcode_add_l3msg_btn);
        addL3msgBtn.setOnClickListener(this);
        outputTypeSpinner = (Spinner) findViewById(R.id.colorcode_ouput_type);
        initSpinner();
        isUploadCbx = (CheckBox) findViewById(R.id.colorcode_isUp_cbx);
        taskNameEdt = initEditText(R.id.colorcode_task_name_edt);
        speedEdt = initEditText(R.id.colorcode_speed);
        saveBtn = (Button) super.findViewById(R.id.btn_ok);
        saveBtn.setOnClickListener(this);
        cancelBtn = initButton(R.id.btn_cencle);
        cancelBtn.setOnClickListener(this);
        setViewValue();

    }

    private void initSpinner() {
        ArrayList<String> outputType = new ArrayList<>();
        outputType.add("RealTime");
        outputType.add("Buffer");
        ArrayAdapter<String> bandWidth = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                outputType);
        bandWidth.setDropDownViewResource(R.layout.spinner_dropdown_item);
        outputTypeSpinner.setAdapter(bandWidth);
        outputTypeSpinner.setSelection(colorCodeModel.getDecodeOutputMode());
        outputTypeSpinner.setOnItemSelectedListener(this);
    }

    /**
     * 设置初始值
     */
    private void setViewValue() {
        taskNameEdt.setText(colorCodeModel.getTaskName());
        speedEdt.setText(colorCodeModel.getValuePerSec() + "");
        isUploadCbx.setChecked(colorCodeModel.isUpload());
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.colorcode_add_btn:
                Intent intent = new Intent(ScanTaskColorCodeActivity.this, ChannelListActivity.class);        //跳转到频点配置界面
                isChannelSave = true;
                intent.putExtra(ScanTask5GOperateFactory.NETTYPE, TestSchemaType.valueOf(colorCodeModel.getTaskType()).getNetWorkType());
                intent.putExtra(ScanTask5GOperateFactory.IS_UPLOAD, isUploadCbx.isChecked());
                taskFactoryIns.setChannelList(colorCodeModel.getChannelList());
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                break;
            case R.id.colorcode_add_l3msg_btn:

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
                                l3Model.setId(gsml3msgs[i].getCode());
                                l3Model.setL3Msg(gsml3msgs[i].name());
                                l3models.add(l3Model);
                                demodulationModel.setL3Models(l3models);
                            }
                        }
                    }
                }).show();
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

    private void saveValue() {
        colorCodeModel.setTaskName(taskNameEdt.getText().toString());
        colorCodeModel.setUpload(isUploadCbx.isChecked());
        colorCodeModel.setDemodulation(demodulationModel);
        colorCodeModel.setDecodeOutputMode(decodeOutputMode);
        colorCodeModel.setChannelList(isChannelSave ? taskFactoryIns.getChannelList() : colorCodeModel.getChannelList());
        colorCodeModel.setValuePerSec(Long.valueOf(speedEdt.getText().toString()));
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
        if (taskNameEdt.getText().toString().trim().length() == 0) {    //任务名为空
            isSave = false;
        } else if (speedEdt.getText().toString().trim().length() == 0) {
            isSave = false;
        } else if (!isSave) {
            Toast.makeText(getApplicationContext(), R.string.task_dialog_title, Toast.LENGTH_SHORT).show();
            return;
        } else if (isChannelSave ? taskFactoryIns.getChannelList().size() == 0 : colorCodeModel.getChannelList().size() == 0) {
            Toast.makeText(getApplicationContext(), R.string.sc_channels_null, Toast.LENGTH_SHORT).show();
            return;
        }
        saveValue();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG,"是否ID相等1："+(parent.getId() == R.id.colorcode_ouput_type));
        LogUtil.d(TAG,"position："+position);
        if (parent.getId() == R.id.colorcode_ouput_type) {
            decodeOutputMode = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

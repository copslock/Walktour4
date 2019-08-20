package com.walktour.gui.task.activity.scannertsma.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.constant.ScanTSMAConstant;
import com.walktour.gui.task.activity.scannertsma.ennnnum.NetType;
import com.walktour.gui.task.activity.scannertsma.model.Blind;
import com.walktour.gui.task.activity.scannertsma.model.ReqElement;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Blind显示页面
 * @author jinfeng.xie
 */
public class ScanTaskBlindActivity extends BaseScanTaskActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ScanTaskBlindActivity";
    @BindView(R.id.title_txt)
    TextView titleTxt;
    @BindView(R.id.blind_task_name_edt)
    EditText blindTaskNameEdt;
    @BindView(R.id.blind_sp_type)
    BasicSpinner blindSpType;
    @BindView(R.id.blind_tv_band)
    TextView blindTvBand;
    @BindView(R.id.blind_sp_sensitivity)
    BasicSpinner blindSpSensitivity;
    @BindView(R.id.blind_sp_min_bandwith)
    BasicSpinner blindSpMinBandwith;
    @BindView(R.id.blind_muti_band)
    CheckBox blindMutiBand;

    private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();

    private Blind blindModel = null;

    private int modelPosition;

    private ScanTask5GOperateFactory taskFactoryIns;

    private int currentType; //检索类型
    private int currentSensitivity;//
    private int currentMinBandwith;//
    private boolean[] bandsBoolean;
    private String[] bands;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_task_blind);
        ButterKnife.bind(this);
        initDate();
        initView();
    }

    private void initDate() {
        taskFactoryIns = ScanTask5GOperateFactory.getInstance();
        taskModels = taskFactoryIns.getTestModelList();
        String testType = getIntent().getExtras().getString(ScanTask5GOperateFactory.TESTTYPE);
        if (getIntent().getExtras() != null) {
            for (int i = 0; i < taskModels.size(); i++) {
                if (taskModels.get(i).getTaskType().equals(testType)) {
                    blindModel = (Blind) taskModels.get(i);
                    modelPosition = i;
                    currentType = blindModel.getMeasurementMode();
                    currentSensitivity = blindModel.getSensitivity();
                    currentMinBandwith = blindModel.getMinimumDetectedBwInHz();
                    LogUtil.d(TAG, "blindModel:" + blindModel);
                    break;
                }
            }
        }
        bands = ScanTSMAConstant.Spinner.SP_SPECTURM_BLIND_REQELEMENT;
        bandsBoolean = new boolean[bands.length];
        if (blindModel != null && blindModel.getReqElements() != null) {
            for (int i = 0; i < blindModel.getReqElements().size(); i++) {
                ReqElement model = blindModel.getReqElements().get(i);
                if (model != null) {
                    bandsBoolean[model.getBandldMask() - 1] = true;
                    blindTvBand.append("" + model.getBandldMask() + ",");
                }
            }
            replaceLastString(blindTvBand);
        }
    }

    void replaceLastString(TextView tv){
        String str=tv.getText().toString().trim();
        if (str.length()>0){
            tv.setText(str.substring(0,str.length()-1));
        }
    }


    /**
     * 初始化界面布局
     */
    private void initView() {
        titleTxt.setText("Blind");//设置标题
        setSpinner(blindSpType, ScanTSMAConstant.Spinner.blindSpType);
        setSpinner(blindSpSensitivity,  ScanTSMAConstant.Spinner.blindSpSensitivity);
        setSpinner(blindSpMinBandwith,  ScanTSMAConstant.Spinner.blindSpMinBandwith);
        if (blindModel != null) {
            blindTaskNameEdt.setText("" + blindModel.getTaskName());
            blindSpType.setSelection(blindModel.getMeasurementMode());
            blindSpSensitivity.setSelection(blindModel.getSensitivity());
            switch (blindModel.getMinimumDetectedBwInHz()) {
                case 1500000:
                    blindSpMinBandwith.setSelection(0);
                    break;
                case 3000000:
                    blindSpMinBandwith.setSelection(1);
                    break;
                case 5000000:
                    blindSpMinBandwith.setSelection(2);
                    break;
                case 10000000:
                    blindSpMinBandwith.setSelection(3);
                    break;
                case 15000000:
                    blindSpMinBandwith.setSelection(4);
                    break;
                case 20000000:
                    blindSpMinBandwith.setSelection(5);
                    break;
                default:
                    blindSpMinBandwith.setSelection(0);
                    break;
            }

        }
    }

    @Override
    public void saveTestTask() {
        if (blindTaskNameEdt.getText().toString().trim().length() == 0) {    //任务名为空
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        }
        blindModel.setTaskName(blindTaskNameEdt.getText().toString());
        blindModel.setMeasurementMode(currentType);
        blindModel.setSensitivity(currentSensitivity);
        switch (currentMinBandwith) {
            case 0:
                blindModel.setMinimumDetectedBwInHz(1500000);
                break;
            case 1:
                blindModel.setMinimumDetectedBwInHz(3000000);
                break;
            case 2:
                blindModel.setMinimumDetectedBwInHz(5000000);
                break;
            case 3:
                blindModel.setMinimumDetectedBwInHz(10000000);
                break;
            case 4:
                blindModel.setMinimumDetectedBwInHz(15000000);
                break;
            case 5:
                blindModel.setMinimumDetectedBwInHz(20000000);
                break;
        }

        saveValue();
    }

    /**
     * 获取控件值并保存
     */

    private void saveValue() {

        taskModels.remove(modelPosition);
        taskModels.add(modelPosition, blindModel);
        taskFactoryIns.setTaskModelToFile(taskModels);
        Toast.makeText(getApplicationContext(), R.string.task_alert_newSucess, Toast.LENGTH_SHORT).show();
        finish();
    }
    @OnClick(R.id.pointer)
    public void pointerClick(){
        finish();
    }
    @OnClick(R.id.blind_tv_band)
    public void onViewClicked() {
        new BasicDialog.Builder(this).setTitle("" + getString(R.string.single_station_survey_cell_band)).
                setMultiChoiceItems(bands, bandsBoolean, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        bandsBoolean[which] = isChecked;
                    }
                }).setNegativeButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                blindTvBand.setText("");
                ArrayList<ReqElement> models = new ArrayList<>();
                for (int i = 0; i < bandsBoolean.length; i++) {
                    if (bandsBoolean[i]) {
                        ReqElement model = new ReqElement();
                        model.setBandldMask(i + 1);
                        model.setNetType(NetType.LTE);
                        models.add(model);
                        blindModel.setReqElements(models);
                        blindTvBand.append("" + model.getBandldMask() + ",");
                    }
                }
                replaceLastString(blindTvBand);
            }
        }).show();

    }

    void setSpinner(Spinner spinner, String[] objects) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                objects);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.blind_sp_type:
                currentType = position;
                break;
            case R.id.blind_sp_sensitivity:
                currentSensitivity = position;
                break;
            case R.id.blind_sp_min_bandwith:
                currentMinBandwith = position;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @OnClick({R.id.btn_cencle, R.id.btn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cencle:
                this.finish();
                break;
            case R.id.btn_ok:
                saveTestTask();
                break;
        }
    }
}

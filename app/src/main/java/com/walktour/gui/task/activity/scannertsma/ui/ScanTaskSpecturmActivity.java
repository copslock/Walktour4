package com.walktour.gui.task.activity.scannertsma.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.constant.ScanTSMAConstant;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.gui.task.activity.scannertsma.model.Spectrum;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.gui.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
/**
 * @author jinfeng.xie
 */
public class ScanTaskSpecturmActivity extends BaseScanTaskActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ScanTaskSpecturmActivit";
    @BindView(R.id.pointer)
    ImageButton pointer;
    @BindView(R.id.title_txt)
    TextView titleTxt;
    @BindView(R.id.pointersetting)
    Button pointersetting;
    @BindView(R.id.specturm_task_name_edt)
    EditText taskNameEdt;
    @BindView(R.id.specturm_start_hz)
    EditText specturmStartHz;
    @BindView(R.id.specturm_end_hz)
    EditText specturmEndHz;
    @BindView(R.id.specturm_speed)
    EditText specturmSpeed;
    @BindView(R.id.specturm_sp_bandwidth)
    EditText specturmSpBandwidth;
    @BindView(R.id.specturm_fft_bandwidth)
    EditText specturmFftBandwidth;
    @BindView(R.id.specturm_fft_size)
    BasicSpinner specturmFftSize;
    @BindView(R.id.specturm_fft_cb)
    CheckBox specturmFftCb;
    @BindView(R.id.specturm_point_sum)
    EditText specturmPointSum;
    @BindView(R.id.cw_detector_type_edt)
    BasicSpinner cwDetectorTypeEdt;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    @BindView(R.id.btn_cencle)
    Button btnCencle;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private ArrayList<ScanTaskModel> taskModels = new ArrayList<ScanTaskModel>();

    private Spectrum spectrumModel = null;

    private int modelPosition;

    private ScanTask5GOperateFactory taskFactoryIns;

    private int currentType; //检索类型
    private int fftSize;

    final int[] fftS = new int[]{128, 256, 512};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scantask_specturm);
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
                    spectrumModel = (Spectrum) taskModels.get(i);
                    modelPosition = i;
                    currentType = spectrumModel.getDetectorType();
                    if (spectrumModel.getRfDataBlocks() != null && spectrumModel.getRfDataBlocks().size() > 0) {
                        fftSize = spectrumModel.getRfDataBlocks().get(0).getFFTSize();
                    }
                    LogUtil.d(TAG, "spectrumModel:" + spectrumModel);
                    break;
                }
            }
        }
    }

    /**
     * 初始化界面布局
     */
    private void initView() {
        titleTxt.setText("Specturm");//设置标题
        String[] strs = new String[fftS.length];
        for (int i=0;i<fftS.length;i++){
            strs[i]=""+fftS[i];
        }
        setSpinner(specturmFftSize,strs);
        setSpinner(cwDetectorTypeEdt, ScanTSMAConstant.Spinner.SP_SPECTURM_DETECTOR_TYPE);
        if (spectrumModel != null) {
            taskNameEdt.setText("" + spectrumModel.getTaskName());
            cwDetectorTypeEdt.setSelection(currentType);
            if (spectrumModel.getRfDataBlocks().size() > 0 && spectrumModel.getRfDataBlocks().get(0) != null) {
                Spectrum.RFDataBlock rfDataBlock = spectrumModel.getRfDataBlocks().get(0);
                specturmFftBandwidth.setText("" + rfDataBlock.getFFTBandwidth());
                specturmStartHz.setText("" + rfDataBlock.getStartFrequency());
                specturmEndHz.setText("" + rfDataBlock.getStopFrequency());
                specturmSpeed.setText("" + rfDataBlock.getRBW());
                specturmSpBandwidth.setText("" + rfDataBlock.getSpacing());
                specturmPointSum.setText("" + rfDataBlock.getMeasRate());
                if (fftSize == fftS[0]) {
                    specturmFftSize.setSelection(0);
                } else if (fftSize == fftS[1]) {
                    specturmFftSize.setSelection(1);
                } else if (fftSize == fftS[2]) {
                    specturmFftSize.setSelection(2);
                }
            }
        }
        specturmFftCb.setOnCheckedChangeListener(this);
        pointer.setOnClickListener(this);

    }

    void setSpinner(Spinner spinner, String[] objects) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                objects);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    @OnClick({R.id.pointer, R.id.btn_ok, R.id.btn_cencle})
    public void onViewClick(View v) {
        switch (v.getId()) {

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
        taskModels.remove(modelPosition);
        taskModels.add(modelPosition, spectrumModel);
        taskFactoryIns.setTaskModelToFile(taskModels);
        Toast.makeText(getApplicationContext(), R.string.task_alert_newSucess, Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void saveTestTask() {
        String taskNameStr = taskNameEdt.getText().toString().trim();
        String specturmStartStr = specturmStartHz.getText().toString().trim();
        String specturmEndStr = specturmEndHz.getText().toString().trim();
        String specturmSpeedStr = specturmSpeed.getText().toString().trim();
        String specturmSpBandwidthStr = specturmSpBandwidth.getText().toString().trim();
        String specturmFftBandwidthStr = specturmFftBandwidth.getText().toString().trim();
        String specturmPointSumStr = specturmPointSum.getText().toString().trim();
        if (taskNameStr.length() == 0) {    //任务名为空
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (specturmStartStr.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (specturmEndStr.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (specturmSpeedStr.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (specturmSpBandwidthStr.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (specturmFftBandwidthStr.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        } else if (specturmPointSumStr.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.task_alert_input, Toast.LENGTH_SHORT).show();
            return;
        }
        spectrumModel.setTaskName(taskNameStr);
        spectrumModel.setTaskType(getIntent().getExtras().getString(ScanTask5GOperateFactory.TESTTYPE));
        spectrumModel.setDetectorType(currentType);
        ArrayList<Spectrum.RFDataBlock> rfDataBlocks = new ArrayList<>();
        Spectrum.RFDataBlock rfDataBlock = new Spectrum.RFDataBlock();
        rfDataBlock.setFFTBandwidth(Long.parseLong(specturmFftBandwidthStr));
        rfDataBlock.setStartFrequency(Long.parseLong(specturmStartStr));
        rfDataBlock.setStopFrequency(Long.parseLong(specturmEndStr));
        rfDataBlock.setRBW(Long.parseLong(specturmSpeedStr));
        rfDataBlock.setFFTSize(fftSize);
        rfDataBlock.setSpacing(Integer.parseInt(specturmSpBandwidthStr));
        rfDataBlock.setMeasRate(Integer.parseInt(specturmPointSumStr));
        rfDataBlocks.add(rfDataBlock);
        spectrumModel.setRfDataBlocks(rfDataBlocks);
        spectrumModel.setBlockCount(rfDataBlocks.size());
        saveValue();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
        if (parent.getId() == R.id.specturm_fft_size) {
            fftSize = fftS[position];
        } else if (parent.getId() == R.id.cw_detector_type_edt) {
            currentType = position;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}

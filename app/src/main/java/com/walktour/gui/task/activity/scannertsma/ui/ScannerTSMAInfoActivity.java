
package com.walktour.gui.task.activity.scannertsma.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.walktour.base.util.LogUtil;
import com.walktour.customView.tableview.TableLayout;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.adapter.ScanInfoTableAdapter;
import com.walktour.gui.task.activity.scannertsma.constant.ScanTSMAConstant;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫频仪信息界面
 * jinfeng.xie
 */
public class ScannerTSMAInfoActivity extends BasicActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "ScannerTSMAInfoActivity";
    private List<String[]> contentList;
    private TableLayout tableLayout;
    private ScanTask5GOperateFactory mScanTaskFactory;
    private Spinner spScanType;
    private ScanInfoTableAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_info_tsma);
        initViews();
        initData();


    }

    private void initViews() {
        tableLayout = (TableLayout) findViewById(R.id.main_table);
        spScanType= (Spinner) findViewById(R.id.sp_scan_type);
    }

    private void initData() {
        setSpinner(spScanType, ScanTSMAConstant.Spinner.infoSpScanType);
        contentList = new ArrayList<>();
        mScanTaskFactory= ScanTask5GOperateFactory.getInstance();
        //        firstRowAsTitle();
    }

    private void updateContent(ArrayList<Content> contents){
        LogUtil.d(TAG,"updateContent");
        contentList.clear();
        for (int i=0;i<contents.size();i++){
            contentList.add(contents.get(i).toArray());
        }
        mAdapter=null;
        mAdapter=new ScanInfoTableAdapter(contentList);
        tableLayout.setAdapter(mAdapter);
//        mAdapter.notify();
    }


    private void setSpinner(Spinner spinner, String[] objects) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
                objects);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.sp_scan_type:
                updateContent(mScanTaskFactory.getTableData(TestSchemaType.CWTEST));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class Content {

        private String ResultBufferDepth;//ResultBufferDepth：缓存，范围为1 -1024，Pioneer默认为1024；
        private String ReceiverIndex;//ReceiverIndex：扫频仪索引，暂不使用，固定赋0；
        private String FrontEndSelectionMask;//FrontEndSelectionMask：物理接收天线（天线1、天线2），Pioneer界面初始值为1；
        private String ValuePerSec; //ValuePerSec：扫频速率，单位Hz，Pioneer界面初始值为10Hz，因为配置项是每1000秒，所以传进来的配置需要乘上1000；
        private String DecodeOutputMode;//DecodeOutputMode：0 为实时显示，1 为缓存显示，Pioneer界面初始值为0；
        private String MeasurementMode;//MeasurementMode：测量模式，0为高速，1为定点，Pioneer界面初始值为0；

        public Content(String ResultBufferDepth, String ReceiverIndex, String FrontEndSelectionMask, String ValuePerSec, String DecodeOutputMode, String MeasurementMode) {
            this.ResultBufferDepth = ResultBufferDepth;
            this.ReceiverIndex = ReceiverIndex;
            this.FrontEndSelectionMask = FrontEndSelectionMask;
            this.ValuePerSec = ValuePerSec;
            this.DecodeOutputMode = DecodeOutputMode;
            this.MeasurementMode = MeasurementMode;
        }

        public String[] toArray() {
            return new String[]{ResultBufferDepth, ReceiverIndex, FrontEndSelectionMask, ValuePerSec, DecodeOutputMode, MeasurementMode};
        }

    }
}

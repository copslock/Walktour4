package com.walktour.gui.mos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import com.walktour.base.util.DateUtil;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.service.bluetoothmos.BluetoothMOSFactory;
import com.walktour.service.bluetoothmos.command.BaseCommand;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MosTestActivity extends AppCompatActivity {

    private MosBoxMode mHelper;
    @BindView(R.id.lv)
    ListView mLv;
    private List<String> mdata = new ArrayList<>();
    private MosDialogAdapter mosDialogAdapter;
    private CaculateModeFacade manager;

    private boolean mIsInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mos_test);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mosDialogAdapter = new MosDialogAdapter(this, mdata);
        mLv.setAdapter(mosDialogAdapter);
        manager = new CaculateModeFacade(this);
    }

    @OnClick({R.id.btn_mos, R.id.btn_phone, R.id.btn_start_moc, R.id.btn_start_mtc, R.id.btn_stop, R.id.btn_play, R.id.btn_record, R.id.btn_stable, R.id.btn_offline})
    void clickButton(Button btn) {
        Intent intent = null;
        BluetoothMOSDevice device = BluetoothMOSFactory.get().getCurrMOCDevice();
        if (!mIsInit && device != null) {
            mIsInit = true;
            manager.setDevice(device);
        }
        switch (btn.getId()) {
            case R.id.btn_mos:
                intent = new Intent(this, MosMatchActivity.class);
                intent.putExtra(MosMatchActivity.EXTRA_FRAGMENT_TYPE, MosMatchActivity.EXTRA_MACTCH_MOC_MOSBOX);
                startActivity(intent);
                break;
            case R.id.btn_phone:
                intent = new Intent(this, MosMatchActivity.class);
                intent.putExtra(MosMatchActivity.EXTRA_FRAGMENT_TYPE, MosMatchActivity.EXTRA_MACTCH_PHONE);
                startActivity(intent);
                break;
            case R.id.btn_start_moc:
                //                if (device != null) {
                //                mHelper = new MosBoxMode(this);
                //                mHelper.setMosDevice(device);
                //                mHelper.setFileTypeAndTime(BaseCommand.FileType.polqa_48k, 240, true);
                //                mHelper.executeTwoWay(true);
                //                } else {
                //                    intent = new Intent(this, BluetoothMosTabActivity.class);
                //                    startActivity(intent);
                //                }


                manager.setRcuFile("rcufile");
                manager.setModel(getModel(true, true));
                manager.start();
                break;
            case R.id.btn_start_mtc:
                //                if (device != null) {
                //                mHelper = new MosBoxMode(this);
                //                mHelper.setMosDevice(device);
                //                mHelper.setFileTypeAndTime(BaseCommand.FileType.polqa_48k, 240, true);
                //                mHelper.executeTwoWay(false);
                //                } else {
                //                    intent = new Intent(this, BluetoothMosTabActivity.class);
                //                    startActivity(intent);
                //                }
                manager.setRcuFile("rcufile");
                manager.setModel(getModel(false, true));
                manager.start();
                break;
            case R.id.btn_stop:
                //                mHelper.stop();
                manager.stop();
                break;
            case R.id.btn_play:
                //                mHelper = new MosBoxMode(this);
                //                mHelper.setMosDevice(device);
                //                mHelper.setFileTypeAndTime(BaseCommand.FileType.polqa_48k, 240, true);
                //                mHelper.executeOneWay(false);

                manager.setRcuFile("rcufile");
                manager.setModel(getModel(false, false));
                manager.start();

                break;
            case R.id.btn_record:
                //                mHelper = new MosBoxMode(this);
                //                mHelper.setMosDevice(device);
                //                mHelper.setFileTypeAndTime(BaseCommand.FileType.polqa_48k, 240, true);
                //                mHelper.executeOneWay(true);
                manager.setRcuFile("rcufile");
                manager.setModel(getModel(true, false));
                manager.start();
                break;
            case R.id.btn_stable:
                startActivity(new Intent(this, MosStableActivity.class));
                break;
            case R.id.btn_offline:
                startActivity(new Intent(this, MosOfflineActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventHandler(String s) {
        mdata.add(DateUtil.formatDate("HH:mm:ss ", new Date()) + s);
        mosDialogAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.stop();
        EventBus.getDefault().unregister(this);
    }

    private TaskModelWrapper getModel(boolean mocTest, boolean isAlter) {
        TaskModelWrapper wrapper = new TaskModelWrapper(new TaskInitiativeCallModel());
        wrapper.isMocTest = mocTest;
        wrapper.isAlternaterTest = isAlter;
        wrapper.isSwb = true;
        wrapper.polqaSample = TaskModel.POLQA_48K;
        wrapper.callMosCount = TaskModel.MOS_POLQA;
        wrapper.keepTime = 120;
        wrapper.mosAlgorithm = "POLQA";
        wrapper.sampleType = "48K";
        wrapper.calcMode = "SWB";
        wrapper.isMultiTest = true;
        wrapper.cycleInterval = 0;
        wrapper.cycleTimes = 3;
        ArrayList<BaseCommand.FileType> cycleDatas = new ArrayList<>();
        cycleDatas.add(BaseCommand.FileType.polqa_8k);
        cycleDatas.add(BaseCommand.FileType.polqa_16k);
        wrapper.cycleDatas = cycleDatas;
        return wrapper;
    }
}

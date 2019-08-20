package com.walktour.gui.setting.sysroutine;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.service.ApplicationInitService;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/***
 * NB模块选择界面
 */
public class NBSelectActivity extends BasicActivity {
    /**
     * 标签名
     */
    private final String TAG = NBSelectActivity.class.getSimpleName();

    /**
     * NB模块设备名列表
     */
    @BindView(R.id.listnbnames)
    ListView listview;

   // @BindView(R.id.relativelayout_set_apn)


    /**
     * 返回按钮
     */
    @BindView(R.id.pointer)
    ImageButton imgBtn;
    /**
     * 标题
     */
    @BindView(R.id.title_txt)
    TextView tv;

    /**
     * 列表适配器
     */
    private ArrayAdapter adapter = null;
    /**
     * 所有设备信息
     */
    private Map<String, String> nbModuleNames = new LinkedMap<>();
    /**
     * 所有信息的键值
     */
    private List<String> keys = new LinkedList<>();

    int select = -1;

    /**
     * 参数存储
     */
    private SharePreferencesUtil sharePreferencesUtil;

    private ConfigNBModuleInfo configNBModuleInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nbselect);
        ButterKnife.bind(this);
        sharePreferencesUtil = SharePreferencesUtil.getInstance(this);
        configNBModuleInfo=ConfigNBModuleInfo.getInstance(this);
        this.initViews();
    }


    /**
     * 初始化键值
     * @param nbModuleNames
     */
    private void setKeys(Map<String, String> nbModuleNames) {
        Iterator<Map.Entry<String, String>> entries = nbModuleNames.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            keys.add(entry.getKey());
        }
    }



    /**
     * 初始化所有控件
     */
    private void initViews() {
        imgBtn.setVisibility(View.GONE);
        try {

            //默认就是选择设备名
            select = this.getIntent().getIntExtra(SysRoutineNBModuleActivity.NBSELECT, SysRoutineNBModuleActivity.NBSELECT_DEVICENAME);

            switch (select) {
                case SysRoutineNBModuleActivity.NBSELECT_DEVICENAME:
                    Map<String,ConfigNBModuleInfo.NBModule>  map=configNBModuleInfo.getNbmodels();
                    Iterator<String> iter = map.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        ConfigNBModuleInfo.NBModule value = map.get(key);
                        nbModuleNames.put(key,value.getDeviceValue());
                    }
                    setKeys(nbModuleNames);
                    selectDeviceName();
                    break;
                case SysRoutineNBModuleActivity.NBSELECT_DEVICEPORT:
                    nbModuleNames = configNBModuleInfo.getNbPorts();
                    setKeys(nbModuleNames);
                    selectDevicePort();
                    break;
                case SysRoutineNBModuleActivity.NBSELECT_DEVICEATPORT:
                    nbModuleNames = configNBModuleInfo.getNbPorts();
                    setKeys(nbModuleNames);
                    selectDeviceAtPort();
                    break;
                case SysRoutineNBModuleActivity.NBSELECT_DEVICE_SCRAMBLESTATE:
                case SysRoutineNBModuleActivity.NBSELECT_DEVICE_SETAPN:
                case SysRoutineNBModuleActivity.NBSELECT_DEVICE_VOLTESETTING:
                case SysRoutineNBModuleActivity.NBSELECT_DEVICE_PSMSETTING:
                case SysRoutineNBModuleActivity.NBSELECT_DEVICE_EDRXSETTING:
                     nbSpecSetting(select);
                     break;
            }
        }catch (Exception ex){
            ex.getMessage();
            LogUtil.w(TAG,ex.getMessage());

        }

    }

    /**
     * 选择设备名
     */
    private void selectDeviceName() {
        tv.setText(getString(R.string.nb_iot_select_name));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, keys);
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(null==configNBModuleInfo.getNbModuleName()||configNBModuleInfo.getNbModuleName().length()<=0) {
            listview.setSelection(0);
        }else{
            String nbname=sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control,"");
            if(null!=nbname&&nbname.length()>0){
                nbname=nbname.split(",")[1];
            }
            Iterator<Map.Entry<String, String>> entries = nbModuleNames.entrySet().iterator();
            boolean isbreak=false;
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                LogUtil.w(TAG,"key:"+entry.getKey()+",value="+entry.getValue()+",tt="+nbname);
                if(entry.getValue().equals(nbname)){
                    for(int i=0;i< keys.size();i++){
                        if(entry.getKey().equals(keys.get(i))){
                            listview.setItemChecked(i,true);
                            isbreak=true;
                            break;
                        }
                    }
                }
                if(isbreak)
                    break;
            }
        }
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strNBModuleName = nbModuleNames.get(keys.get(position));

                configNBModuleInfo.setNbModuleName(strNBModuleName);

                sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control,
                        keys.get(position) + "," + strNBModuleName);

                sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICENAME,  keys.get(position));

                configNBModuleInfo.setChipvendor(configNBModuleInfo.getNbmodels().get(keys.get(position)).getChipvendor());

                //启动服务
                startService(new Intent(NBSelectActivity.this, ApplicationInitService.class));
            }
        });
    }

    /**
     * 选择串口
     */
    private void selectDevicePort() {
        tv.setText(R.string.nbmodule_setting_port);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, keys);
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(null== configNBModuleInfo.getNbPort()|| configNBModuleInfo.getNbPort().length()<=0) {
            listview.setSelection(0);
        }else{
            String nbPort=sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceport_control,ConfigNBModuleInfo.NBPORT_DEFAULT);
            if(null!=nbPort&&nbPort.split(",").length>=2){
                nbPort=nbPort.split(",")[1];
            }
            Iterator<Map.Entry<String, String>> entries = nbModuleNames.entrySet().iterator();
            boolean isbreak=false;
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                LogUtil.w(TAG,"key:"+entry.getKey()+",value="+entry.getValue()+",tt="+nbPort);
                if(entry.getValue().equals(nbPort)){
                    for(int i=0;i< keys.size();i++){
                        if(entry.getKey().equals(keys.get(i))){
                            listview.setItemChecked(i,true);
                            isbreak=true;
                            break;
                        }
                    }
                }
                if(isbreak)
                    break;
            }
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                configNBModuleInfo.setNbPort(nbModuleNames.get(keys.get(position)));

                sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmoduele_deviceport_control,
                        keys.get(position)+","+nbModuleNames.get(keys.get(position)));

                sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICEPORT,  keys.get(position));
            }
        });
    }

    /**
     * 选择AT口
     */
    private void selectDeviceAtPort() {
        tv.setText(R.string.nbmodule_setting_atport);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, keys);
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(null== configNBModuleInfo.getNbAtPort()|| configNBModuleInfo.getNbAtPort().length()<=0) {
            listview.setSelection(0);
        }else{

            String nbAtPort=sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceatport_control,ConfigNBModuleInfo.NBATPORT_DEFAULT);
            if(null!=nbAtPort&&nbAtPort.split(",").length>=2){
                nbAtPort=nbAtPort.split(",")[1];
            }

            Iterator<Map.Entry<String, String>> entries = nbModuleNames.entrySet().iterator();
            boolean isbreak=false;
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                LogUtil.w(TAG,"key:"+entry.getKey()+",value="+entry.getValue()+",tt="+nbAtPort);
                if(entry.getValue().equals(nbAtPort)){
                    for(int i=0;i< keys.size();i++){
                        if(entry.getKey().equals(keys.get(i))){
                            listview.setItemChecked(i,true);
                            isbreak=true;
                            break;
                        }
                    }
                }
                if(isbreak)
                    break;
            }
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                configNBModuleInfo.setNbAtPort(nbModuleNames.get(keys.get(position)));

                sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmoduele_deviceatport_control,
                        keys.get(position)+","+nbModuleNames.get(keys.get(position)));

                sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICEATPORT, keys.get(position));
            }
        });
    }

    /*
     *  NB终端特殊设置
     */
    private void nbSpecSetting(int iSettingId){
        switch (iSettingId) {
            case SysRoutineNBModuleActivity.NBSELECT_DEVICE_SCRAMBLESTATE:
                specSettingRemoScrambleState();
                break;
            case SysRoutineNBModuleActivity.NBSELECT_DEVICE_SETAPN:
                specSettingAPN();
                break;
            case SysRoutineNBModuleActivity.NBSELECT_DEVICE_VOLTESETTING:
                specSettingVolte();
                break;
            case SysRoutineNBModuleActivity.NBSELECT_DEVICE_PSMSETTING:
                specSettingPSM();
                break;
            case SysRoutineNBModuleActivity.NBSELECT_DEVICE_EDRXSETTING:
                specSettingEDRX();
                break;
        }
    }


    /*
     * Remo 终端扰码设置
     */
    private void specSettingRemoScrambleState(){
        tv.setText(R.string.nbmodule_setting_remo_scramble_state);

        keys.clear();
        keys.add("Open");
        keys.add("Close");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, keys);
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strScrambleState = keys.get(position);

                sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_devicescramblestate,
                        strScrambleState);

                sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICE_SCRAMBLE_STATE, strScrambleState);
            }
        });

        return ;
    /*
        tv.setText(R.string.nbmodule_setting_remo_scramble_state);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, keys);
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if(null== configNBModuleInfo.getNbAtPort()|| configNBModuleInfo.getNbAtPort().length()<=0) {
            listview.setSelection(0);
        }else {

            //String nbAtPort = sharePreferencesUtil.getString(WalktourConst.SYS_SETTING_nbmoduele_remoscramblestate_control, "open");
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //..
            }
        });
        */
    }

    /*
     * Remo APN设置
     */
    private void specSettingAPN(){
        tv.setText(R.string.nbmodule_setting_apn);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.lock_dev_frequency_edit, null);
        RelativeLayout relativeLayoutBand = (RelativeLayout)view.findViewById(R.id.relativelayout_lock_freq_band);
        relativeLayoutBand.setVisibility(View.GONE);
        TextView textViewAPN = (TextView)view.findViewById(R.id.earfcn_txt);
        textViewAPN.setText("APN");
        final EditText editTextAPN = (EditText)view.findViewById(R.id.earfcn_edit);
        editTextAPN.setInputType(InputType.TYPE_CLASS_TEXT);

        new BasicDialog.Builder(this).setTitle(R.string.nbmodule_setting_apn)
                .setView(view)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strAPN = editTextAPN.getText().toString().trim();

                        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_devicesetapn,
                                strAPN);

                        sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICE_SET_APN, strAPN);
                    }
                }).setNegativeButton(R.string.str_cancle).show();
        return ;
    }

    /*
     * Remo Volte开关设置
     */
    private void specSettingVolte(){
        tv.setText(R.string.nbmodule_setting_volte);

        keys.clear();
        keys.add("Open");
        keys.add("Close");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, keys);
        listview.setAdapter(adapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strVolteSetting = keys.get(position);

                sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_devicevoltesetting,
                        strVolteSetting);

                sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICE_LOCK_VOLTE_SETTING, strVolteSetting);
            }
        });

        return ;
    }

    private void specSettingPSM(){
        tv.setText(R.string.nbmodule_setting_psm);

        keys.clear();
        keys.add("Open");
        keys.add("Close");
        keys.add("Wake up");

        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.lock_dev_cell_lte_edit, null);

        TextView textViewState = (TextView)view.findViewById(R.id.lock_dev_cell_lte_band_text);
        textViewState.setText("State: ");

        final Spinner bandSpinner = (Spinner) view.findViewById(R.id.band_select_edit);
        adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout, keys);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        bandSpinner.setAdapter(adapter);

        TextView textViewT3412_Unit= (TextView)view.findViewById(R.id.earfcn_text);
        textViewT3412_Unit.setText("T3412 Unit(0-6)");

        TextView textViewT3412_Value= (TextView)view.findViewById(R.id.pci_text);
        textViewT3412_Value.setText("T3412 Value(0-31)");

        RelativeLayout relativeLayoutEx1 = (RelativeLayout)view.findViewById(R.id.lock_dev_cell_lte_edit_ex_1);
        relativeLayoutEx1.setVisibility(View.VISIBLE);
        TextView textViewT3324_Unit = (TextView)view.findViewById(R.id.lock_cell_edit_ex_1_text);
        textViewT3324_Unit.setText("T3324 Unit(0-2)");

        RelativeLayout relativeLayoutEx2 = (RelativeLayout)view.findViewById(R.id.lock_dev_cell_lte_edit_ex_2);
        relativeLayoutEx2.setVisibility(View.VISIBLE);
        TextView textViewT3324_Value = (TextView)view.findViewById(R.id.lock_cell_edit_ex_2_text);
        textViewT3324_Value.setText("T3324 Value(0-31)");

        new BasicDialog.Builder(this).setTitle(R.string.nbmodule_setting_psm)
                .setView(view)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editT3412_Unit = (EditText)view.findViewById(R.id.earfcn_edit);
                        EditText editT3412_Value = (EditText)view.findViewById(R.id.pci_edit);
                        EditText editT3324_Unit = (EditText)view.findViewById(R.id.lock_cell_edit_ex_1_edit);
                        EditText editT3324_Value = (EditText)view.findViewById(R.id.lock_cell_edit_ex_2_edit);

                        String strState = bandSpinner.getSelectedItem().toString();
                        String strPSMSetting = "State=";
                        strPSMSetting +=  bandSpinner.getSelectedItem().toString().toLowerCase();
                        strPSMSetting += "\r\n";
                        strPSMSetting += "T3412_Unit=" + editT3412_Unit.getText() + "\r\n";
                        strPSMSetting += "T3412_Value=" + editT3412_Value.getText() + "\r\n";
                        strPSMSetting += "T3324_Unit=" + editT3324_Unit.getText() + "\r\n";
                        strPSMSetting += "T3324_Value=" + editT3324_Value.getText() + "\r\n";

                        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_devicesetpsm,
                                strState);

                        sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICE_LOCK_PSM_SETTING, strPSMSetting);

                    }
                }).setNegativeButton(R.string.str_cancle).show();

        return ;
    }

    private void specSettingEDRX(){
        tv.setText(R.string.nbmodule_setting_edrx);

        keys.clear();
        keys.add("Open");
        keys.add("Close");

        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.lock_dev_cell_lte_edit, null);

        TextView textViewState = (TextView)view.findViewById(R.id.lock_dev_cell_lte_band_text);
        textViewState.setText("State: ");

        final Spinner bandSpinner = (Spinner) view.findViewById(R.id.band_select_edit);
        adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout, keys);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        bandSpinner.setAdapter(adapter);

        List<String> strKeys = new LinkedList<>();
        strKeys.add("NB1");
        strKeys.add("GSM");

        RelativeLayout relativeLayoutEx1 = (RelativeLayout)view.findViewById(R.id.lock_dev_cell_lte_band_select_layout_ex1) ;
        relativeLayoutEx1.setVisibility(View.VISIBLE);
        TextView textViewEx1 = (TextView)view.findViewById(R.id.lock_dev_cell_lte_band_text_ex1);
        textViewEx1.setText("RAT: ");

        final Spinner bandSpinnerEx1 = (Spinner) view.findViewById(R.id.band_select_edit_ex1);
        adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_custom_layout, strKeys);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        bandSpinnerEx1.setAdapter(adapter);

        RelativeLayout relativeLayoutEarfcn = (RelativeLayout)view.findViewById(R.id.lock_dev_cell_lte_band_earfcn_layout);
        relativeLayoutEarfcn.setVisibility(View.GONE);

        TextView textViewT3412_Value= (TextView)view.findViewById(R.id.pci_text);
        textViewT3412_Value.setText("eDRXCycleLength(0-15)");


        new BasicDialog.Builder(this).setTitle(R.string.nbmodule_setting_edrx)
                .setView(view)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editT3412_Value = (EditText)view.findViewById(R.id.pci_edit);
                        String strState = bandSpinner.getSelectedItem().toString();
                        String strEDRXSetting = "State=" + strState.toLowerCase() + "\r\n";
                        strEDRXSetting += "RAT=" + bandSpinnerEx1.getSelectedItem().toString() + "\r\n";
                        strEDRXSetting += "eDRXCycleLength=" + editT3412_Value.getText() + "\r\n";

                        sharePreferencesUtil.saveString(WalktourConst.SYS_SETTING_nbmodule_devicesetedrx,
                                strState);

                        sendBroad(SysRoutineNBModuleActivity.ACTION_DEVICE_LOCK_EDRX_SETTING, strEDRXSetting);
                    }
                }).setNegativeButton(R.string.str_cancle).show();


        return ;
    }

    /**
     * 发送广播更新界面
     * @param action
     * @param msg
     */
    private void sendBroad(String action,String msg){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("msg", msg);
        sendBroadcast(intent);
        this.finish();
    }
}

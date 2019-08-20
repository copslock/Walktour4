package com.walktour.gui.setting.sysroutine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.locknet.dialog.LockActivity;
import com.walktour.service.NBHandlerService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.walktour.Utils.WalkStruct.CurrentNetState.CatM;
import static com.walktour.Utils.WalkStruct.CurrentNetState.NBIoT;
import static com.walktour.gui.locknet.dialog.LockActivity.LockEDRXSetting;
import static com.walktour.gui.locknet.dialog.LockActivity.LockPSMSetting;
import static com.walktour.gui.locknet.dialog.LockActivity.LockProAPN;
import static com.walktour.gui.locknet.dialog.LockActivity.LockProScrambleState;
import static com.walktour.gui.locknet.dialog.LockActivity.LockVolteSetting;
import static com.walktour.gui.locknet.dialog.LockActivity.STREDRXSETTING;
import static com.walktour.gui.locknet.dialog.LockActivity.STRLOCKTYPE;
import static com.walktour.gui.locknet.dialog.LockActivity.STRPSMSETTING;
import static com.walktour.gui.locknet.dialog.LockActivity.STRSCRAMBLESTATE;
import static com.walktour.gui.locknet.dialog.LockActivity.STRSETAPN;
import static com.walktour.gui.locknet.dialog.LockActivity.STRVOLTESETTING;


/***
 * NB 模块设置
 * 
 * @author weirong.fan
 *
 */
public class SysRoutineNBModuleActivity extends BasicActivity implements OnClickListener {
	private final String TAG=SysRoutineNBModuleActivity.class.getSimpleName();
	public static final String NBSELECT="com.walktour.gui.setting.sysroutine.nbselect";
	public static final String ACTION_DEVICENAME="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.deviceName";
	public static final String ACTION_DEVICEPORT="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.devicePort";
	public static final String ACTION_DEVICEATPORT="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.deviceAtPort";
	public static final String ACTION_DEVICE_SCRAMBLE_STATE="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.deviceScramleState";
	public static final String ACTION_DEVICE_SET_APN="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.deviceSetAPN";

	public static final String ACTION_DEVICE_LOCK_VOLTE_SETTING="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.deviceVolteSetting";
	public static final String ACTION_DEVICE_LOCK_PSM_SETTING="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.devicePSMSetting";
	public static final String ACTION_DEVICE_LOCK_EDRX_SETTING="com.walktour.gui.setting.sysroutine.SysRoutineNBModuleActivity.deviceEDRXSetting";
	/**
	 * 选择设备名
	 */
	public static final int NBSELECT_DEVICENAME=1;
	/**
	 * 选择设备串口
	 */
	public static final int NBSELECT_DEVICEPORT=2;
	/**
	 * 选择设备AT口
	 */
	public static final int NBSELECT_DEVICEATPORT=3;

	/**
	 *设置终端的扰码状态
	 * 当前支持的终端有 Remo
	 */
	public static final int NBSELECT_DEVICE_SCRAMBLESTATE = 0x10;

	/**
	 *设置终端的APN
	 * 当前支持的终端有 Remo
	 */
	public static final int NBSELECT_DEVICE_SETAPN = 0x11;

	public static final int NBSELECT_DEVICE_VOLTESETTING = 0x12;

	public static final int NBSELECT_DEVICE_PSMSETTING = 0x13;

	public static final int NBSELECT_DEVICE_EDRXSETTING = 0x14;

	private Context mContext;
	private ApplicationModel appModel = ApplicationModel.getInstance();
	@BindView(R.id.nbmodule_select_deviname)
	Button devieName;
	@BindView(R.id.nbmodule_select_port)
	Button deviePort;
	@BindView(R.id.nbmodule_select_atport)
	Button devieAtPort;
	@BindView(R.id.nbmodule_scramble_state)
	Button deviceScrambleState;
	@BindView(R.id.nbmodule_set_apn)
	Button deviceSetAPN;
	@BindView(R.id.nbmodule_power_on_delay)
    EditText powerOnDelay;
	@BindView(R.id.nbmodule_volte_setting)
	Button deviceVolteSetting;
	@BindView(R.id.nbmodule_psm_setting)
	Button devicePSMSetting;
	@BindView(R.id.nbmodule_edrx_setting)
	Button deviceEDRXSetting;

	/** 参数存储 */
	private SharePreferencesUtil preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		setContentView(R.layout.sys_routine_setting_nbmodule);
		ButterKnife.bind(this);

		preferences = SharePreferencesUtil.getInstance(this.getApplicationContext());
		//设备名
		String msg=preferences.getString(WalktourConst.SYS_SETTING_nbmoduele_devicename_control, "")+"";
		devieName.setText(msg.length()==0?"":msg.split(",")[0]);
		setSepcRemoLayout(devieName.getText().toString());
		//串口名
		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceport_control, "")+"";
		deviePort.setText(msg.length()==0?"com0":msg.split(",")[0]);
		//AT口名
		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmoduele_deviceatport_control, "")+"";
		String atPortName="com2";
		if(ConfigNBModuleInfo.getInstance(this).getNbPorts().size()<=2){
			atPortName="com1";
		}else if(ConfigNBModuleInfo.getInstance(this).getNbPorts().size()<=4){
			atPortName="com2";
		}else{
			atPortName="com2";
		}
		devieAtPort.setText(msg.length()==0?atPortName:msg.split(",")[0]);

		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmodule_devicescramblestate, "Open")+"";
		deviceScrambleState.setText(msg.length()==0?"Open":msg.split(",")[0]);

		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmodule_devicesetapn, "")+"";
		deviceSetAPN.setText(msg.length()==0?"":msg.split(",")[0]);

		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60")+"";
		powerOnDelay.setText(msg);

		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmodule_devicevoltesetting, "open") + "";
		deviceVolteSetting.setText(msg.length() == 0 ? "open" : msg.split(",")[0]);

		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmodule_devicesetpsm, "close") + "";
		devicePSMSetting.setText(msg.length() == 0 ? "Wake up" : msg.split(",")[0]);

		msg=preferences.getString(WalktourConst.SYS_SETTING_nbmodule_devicesetedrx, "close") + "";
		deviceEDRXSetting.setText(msg.length() == 0 ? "close" : msg.split(",")[0]);


		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DEVICENAME);
		filter.addAction(ACTION_DEVICEPORT);
		filter.addAction(ACTION_DEVICEATPORT);
		filter.addAction(ACTION_DEVICE_SCRAMBLE_STATE);
		filter.addAction(ACTION_DEVICE_SET_APN);
		filter.addAction(ACTION_DEVICE_LOCK_VOLTE_SETTING);
		filter.addAction(ACTION_DEVICE_LOCK_PSM_SETTING);
		filter.addAction(ACTION_DEVICE_LOCK_EDRX_SETTING);
		registerReceiver(valueReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(valueReceiver);
	}


	@OnClick(R.id.nbmodule_select_deviname)
	void clickDeviceName(){
		jumpActivity(NBSELECT_DEVICENAME);
	}
	@OnClick(R.id.nbmodule_select_port)
	void clickPort(){
		jumpActivity(NBSELECT_DEVICEPORT);
	}
	@OnClick(R.id.nbmodule_select_atport)
	void clickAtPort(){
		jumpActivity(NBSELECT_DEVICEATPORT);
	}
	@OnClick(R.id.nbmodule_scramble_state)
	void clickRemoScrambleState() {
		jumpActivity(NBSELECT_DEVICE_SCRAMBLESTATE);
	}
	@OnClick(R.id.nbmodule_set_apn)
	void clickSetAPN(){
		jumpActivity(NBSELECT_DEVICE_SETAPN);
	}
	@OnClick(R.id.nbmodule_volte_setting)
	void clickVotlteSetting() {
		jumpActivity(NBSELECT_DEVICE_VOLTESETTING);
	}
	@OnClick(R.id.nbmodule_psm_setting)
	void clickPSMSetting() {
		jumpActivity(NBSELECT_DEVICE_PSMSETTING);
	}
	@OnClick(R.id.nbmodule_edrx_setting)
	void clickEDRXSetting() {
		jumpActivity(NBSELECT_DEVICE_EDRXSETTING);
	}

	/**
	 * NB模块上电
	 */
	@OnClick(R.id.nbmodule_power_on)
	void clickPowerOn(){
		if (appModel.isTestJobIsRun()) {
			ToastUtil.showToastShort(mContext, R.string.str_testing);
			return;
		}
		//NB设备上电
		WalkStruct.CurrentNetState netType = !ApplicationModel.getInstance().isFreezeScreen() ? TraceInfoInterface.currentNetType
				: TraceInfoInterface.decodeFreezeNetType;
		if ((netType == NBIoT||netType == CatM)&&Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_SUCCESS) {//已经上电成功，无需再上电
			ToastUtil.showToastShort(this,R.string.nbmodule_setting_device_power_on_success);
		}else{
			if(Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_ING){//上电过程中
                String strVal=String.format(getResources().getString(R.string.nbmodule_setting_device_power_on_ing), preferences.getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60")+"");
				ToastUtil.showToastShort(this,strVal);
			}else if(Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_FAILURE){//上电失败，需要重新上电

				if(Deviceinfo.getInstance().isATModel()){
					ToastUtil.showToastShort(mContext,R.string.at_command_info);
					return ;
				}
				LogUtil.w(TAG,"power on.");
				Intent intentThree = new Intent(mContext, NBHandlerService.class);
				intentThree.putExtra("select",NBHandlerService.COMMAND_UP);
				mContext.startService(intentThree);
			}
		}

	}

	/**
	 * NB模块下电
	 */
	@OnClick(R.id.nbmodule_power_off)
	void clickPowerOff(){
		if (appModel.isTestJobIsRun()) {
			ToastUtil.showToastShort(mContext, R.string.str_testing);
			return;
		}

		if(Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_ING){//上电过程中
            String strVal=String.format(getResources().getString(R.string.nbmodule_setting_device_power_on_ing), preferences.getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60")+"");
            ToastUtil.showToastShort(this,strVal);
		}else {
			if(Deviceinfo.getInstance().isATModel()){
				ToastUtil.showToastShort(mContext,R.string.at_command_info);
				return ;
			}
            Deviceinfo.getInstance().setNbPowerOnStaus(Deviceinfo.POWN_ON_FAILURE);//上电失败
		    Intent intentThree = new Intent(mContext, NBHandlerService.class);
			intentThree.putExtra("select", NBHandlerService.COMMAND_DOWNX);
			mContext.startService(intentThree);
		}
 	}

	/**
	 * NB模块附着
	 */
	@OnClick(R.id.nbmodule_attach)
 	void clickAttach(){
		if(Deviceinfo.getInstance().isATModel()){
			ToastUtil.showToastShort(mContext,R.string.at_command_info);
			return ;
		}
		Intent intentThree = new Intent(mContext, NBHandlerService.class);
		intentThree.putExtra("select",NBHandlerService.COMMAND_MODEL_ATTACH);
		mContext.startService(intentThree);
	}

	/**
	 * NB模块去附着
	 */
	@OnClick(R.id.nbmodule_detach)
	void clickDetach(){
		if(Deviceinfo.getInstance().isATModel()){
			ToastUtil.showToastShort(mContext,R.string.at_command_info);
			return ;
		}
		Intent intentThree = new Intent(mContext, NBHandlerService.class);
		intentThree.putExtra("select",NBHandlerService.COMMAND_MODEL_DETTACH);
		mContext.startService(intentThree);
	}
    @OnTextChanged(value = R.id.nbmodule_power_on_delay, callback = OnTextChanged.Callback.BEFORE_TEXT_CHANGED)
    void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @OnTextChanged(value = R.id.nbmodule_power_on_delay, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    @OnTextChanged(value = R.id.nbmodule_power_on_delay, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {
//        if(Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_ING){
//            String strVal=String.format(getResources().getString(R.string.nbmodule_setting_device_power_on_ing), preferences.getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60")+"");
//            ToastUtil.showToastShort(this,strVal);
//        }else {
            if(Deviceinfo.getInstance().getNbPowerOnStaus()==Deviceinfo.POWN_ON_FAILURE) {//上电时失败
                try {
                    if (null != s) {
                        int val = Integer.parseInt(s.toString());
                        if (val <= 0) {
                            String msg = preferences.getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60") + "";
                            powerOnDelay.setText(msg);
                        } else {
                            preferences.saveString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, s.toString());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();

                    String msg = preferences.getString(WalktourConst.SYS_SETTING_nbmodule_powerondelay, "60") + "";
                    powerOnDelay.setText(msg);

                }
            }
//        }
    }

	private void jumpActivity(int select){
		Bundle bundle = new Bundle();
		bundle.putInt(NBSELECT,select);
		jumpActivity(NBSelectActivity.class,bundle);
	}

	/**
	 * 数据值接收,更新界面
	 */
	private BroadcastReceiver valueReceiver = new BroadcastReceiver() {
		String msg="";
		@Override
		public void onReceive(Context context, Intent data) {
			LogUtil.w(TAG, data.getAction());
			LogUtil.w(TAG, data.getStringExtra("msg"));
			if(data.getAction().equals(ACTION_DEVICENAME)){
				msg = data.getStringExtra("msg");
				devieName.setText(msg);

				setSepcRemoLayout(devieName.getText().toString());
			}else if(data.getAction().equals(ACTION_DEVICEPORT)){
				msg = data.getStringExtra("msg");
				deviePort.setText(msg);
			}else if(data.getAction().equals(ACTION_DEVICEATPORT)){
				msg = data.getStringExtra("msg");
				devieAtPort.setText(msg);
			} else if (data.getAction().equals(ACTION_DEVICE_SCRAMBLE_STATE)) {
				msg = data.getStringExtra("msg");
				deviceScrambleState.setText(msg);

				Intent intentLock = new Intent(SysRoutineNBModuleActivity.this, LockActivity.class);
				Bundle bundleLock = new Bundle();
				bundleLock.putInt(STRLOCKTYPE, LockProScrambleState);
				bundleLock.putString(STRSCRAMBLESTATE, msg);
				intentLock.putExtras(bundleLock);
				startActivity(intentLock);
			} else if (data.getAction().equals(ACTION_DEVICE_SET_APN)) {
				msg = data.getStringExtra("msg");
				deviceSetAPN.setText(msg);

				Intent intentLock = new Intent(SysRoutineNBModuleActivity.this, LockActivity.class);
				Bundle bundleLock = new Bundle();
				bundleLock.putInt(STRLOCKTYPE, LockProAPN);
				bundleLock.putString(STRSETAPN, msg);
				intentLock.putExtras(bundleLock);
				startActivity(intentLock);
			} else if (data.getAction().equals(ACTION_DEVICE_LOCK_VOLTE_SETTING)) {
				msg = data.getStringExtra("msg");
				deviceVolteSetting.setText(msg);

				Intent intentLock = new Intent(SysRoutineNBModuleActivity.this, LockActivity.class);
				Bundle bundleLock = new Bundle();
				bundleLock.putInt(STRLOCKTYPE, LockVolteSetting);
				bundleLock.putString(STRVOLTESETTING, msg);
				intentLock.putExtras(bundleLock);
				startActivity(intentLock);
			} else if (data.getAction().equals(ACTION_DEVICE_LOCK_PSM_SETTING)) {
				msg = data.getStringExtra("msg");

				int iB_Index = msg.indexOf("State=");
				iB_Index += 6;
				int iE_Index = msg.indexOf("\r\n", iB_Index);
				String strText = msg.substring(iB_Index, iE_Index);
				devicePSMSetting.setText(strText);

				Intent intentLock = new Intent(SysRoutineNBModuleActivity.this, LockActivity.class);
				Bundle bundleLock = new Bundle();
				bundleLock.putInt(STRLOCKTYPE, LockPSMSetting);
				bundleLock.putString(STRPSMSETTING, msg);
				intentLock.putExtras(bundleLock);
				startActivity(intentLock);
			} else if (data.getAction().equals(ACTION_DEVICE_LOCK_EDRX_SETTING)) {
				msg = data.getStringExtra("msg");

				int iB_Index = msg.indexOf("State=");
				iB_Index += 6;
				int iE_Index = msg.indexOf("\r\n", iB_Index);
				String strText = msg.substring(iB_Index, iE_Index);
				deviceEDRXSetting.setText(strText);

				Intent intentLock = new Intent(SysRoutineNBModuleActivity.this, LockActivity.class);
				Bundle bundleLock = new Bundle();
				bundleLock.putInt(STRLOCKTYPE, LockEDRXSetting);
				bundleLock.putString(STREDRXSETTING, msg);
				intentLock.putExtras(bundleLock);
				startActivity(intentLock);
			}
		}
	};

	/*
	* 设置Remo 终端的一些特殊设置,当前支持
	* 	1. ScambleState的设置
	 */
	private void setSepcRemoLayout(String strDeviceName){
		RelativeLayout relativeLayoutRemoScrameState = (RelativeLayout)findViewById(R.id.nbmodule_remo_scramble_state_style);
		RelativeLayout relativeLayoutSetApn = (RelativeLayout)findViewById(R.id.nbmodule_set_apn_style);
		RelativeLayout relativeLayoutVolteSetting = (RelativeLayout)findViewById(R.id.nbmodule_set_volte_style);
		RelativeLayout relativeLayoutPSMSetting = (RelativeLayout)findViewById(R.id.nbmodule_set_psm_style);
		RelativeLayout relativeLayoutEDRXSetting = (RelativeLayout)findViewById(R.id.nbmodule_set_edrx_style);

		if (strDeviceName.startsWith("Remo")) {
			relativeLayoutRemoScrameState.setVisibility(View.VISIBLE);
			relativeLayoutSetApn.setVisibility(View.VISIBLE);
			relativeLayoutVolteSetting.setVisibility(View.VISIBLE);
			relativeLayoutPSMSetting.setVisibility(View.VISIBLE);
			relativeLayoutEDRXSetting.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutRemoScrameState.setVisibility(View.GONE);
			relativeLayoutSetApn.setVisibility(View.GONE);
		}
	}
}

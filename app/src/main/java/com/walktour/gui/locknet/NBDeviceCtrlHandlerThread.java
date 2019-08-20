package com.walktour.gui.locknet;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.dingli.service.test.ipc2jni;
import com.dingli.service.test.ipc2msg;
import com.dinglicom.dataset.DatasetManager;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigNBModuleInfo;
import com.walktour.control.config.Deviceinfo;

/**
 * Created by luojun on 2017/12/18.
 */

public class NBDeviceCtrlHandlerThread extends Thread {
    private static String TAG = "NBDeviceCtrlHandlerThread";

    private static final int NB_TEST_COMMAND        = 8001;
    private static final int NB_TEST_INITED         = 1;
    private static final int NB_TEST_STOP           = 1006;

    private static final int NB_TEST_DEV_PARAM      = 80011;

    private static final int NB_DEV_Force_LockNetwork    = 0xC0001;
    private static final int NB_DEV_Force_LockFreq        = 0xC0002;
    private static final int NB_DEV_Force_LockBand        = 0xC0003;
    private static final int NB_DEV_Force_LockCell        = 0xC0004;
    private static final int NB_DEV_Force_ScarmbleSetting      = 0xC0005;
    private static final int NB_DEV_Force_SetAPN                = 0xC0006;
    private static final int NB_DEV_Force_PSM_Switch		       = 0xC0007;
    private static final int NB_DEV_Force_eDRX_Switch		   = 0xC0008;

    private Context mContext;
    private ipc2jni aIpc2Jni = null;
    private ConfigNBModuleInfo configNBModuleInfo;
    private boolean initServiceSuccess = false;
    private boolean initProcSuccess = false;

    private Looper mLooper = null;
    private Handler mForceEventHandler = null;

    public NBDeviceCtrlHandlerThread(Context context) {
        LogUtil.w(TAG, "Create NBDeviceCtrlHandlerThread");
        configNBModuleInfo = ConfigNBModuleInfo.getInstance(context);
        LogUtil.w(TAG, "Create NBDeviceCtrlHandlerThread - 1");

        mContext = context;
    }

    public void onDestroy(){
        LogUtil.w(TAG, "onDestroy start.");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (null != aIpc2Jni) {
            try {
                LogUtil.w(TAG, "onDestroy send 1006.");
                aIpc2Jni.send_command(NB_TEST_COMMAND, NB_TEST_STOP, "", 0);
                Thread.sleep(3000);
                aIpc2Jni.uninit_server();
                aIpc2Jni = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            exit();

            LogUtil.w(TAG, "onDestroy finish.");
        }
    }

    public void run()
    {
        super.run();

        LogUtil.w(TAG, "handleMessage run");

        Looper.prepare();

        LogUtil.w(TAG, "handleMessage run - 1");

        mLooper = Looper.myLooper();

        LogUtil.w(TAG, "handleMessage run - 2");

        mForceEventHandler = new Handler(mLooper) {
            public void handleMessage(android.os.Message msg) {
                LogUtil.w(TAG, "handleMessage");
                ipc2msg aMsg = (ipc2msg) msg.obj;
                LogUtil.w(TAG, "aMsg.test_item=" + aMsg.test_item + ",aMsg.event_id=" + aMsg.event_id);
                if (aMsg.test_item != NB_TEST_COMMAND) {
                    LogUtil.w(TAG, "Error");
                    return;
                }

                switch (aMsg.event_id) {
                    case NB_TEST_INITED:
                        //防止程序已经退出，但库没完全退出再次执行的问题
                        if (configNBModuleInfo.isProccessStop()) {
                            stopServProc();
                            return;
                        }
                        initProcSuccess = true;

                        LogUtil.w(TAG, "TEST_INITED Start");
                        String dev_params = "DevName::" + configNBModuleInfo.getNbModuleName() + "\n" +
                                "port::" + configNBModuleInfo.getNbAtPort() + "\n" +
                                "Baudrate::115200\n";
                        LogUtil.w(TAG, "TEST_ITEM=" + NB_TEST_DEV_PARAM + ",80011,dev_params=" + dev_params);
                        LogUtil.w(TAG, "configNBModuleInfo.getNbAtPort()=" + configNBModuleInfo.getNbAtPort());

                        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_TEST_DEV_PARAM, dev_params, dev_params.length());
                        try {
                            Thread.currentThread();
                            Thread.sleep(1 * 1000);
                        } catch (Exception e) {
                            LogUtil.w(TAG, "Exception1 =" + e.getMessage());
                        }
                        break;
                }
            }
        };

        if (null == mForceEventHandler)
            LogUtil.w(TAG, "handleMessage run - 3.0");
        else
            LogUtil.w(TAG, "handleMessage run - 3.1");

        Looper.loop();
    }

    private void stopServProc(){
        initServiceSuccess = false;
        initProcSuccess = false;
    }

    private void exit() {
        if (mLooper != null) {
            mLooper.quit();
            mLooper = null;
        }
    }

    private boolean CreateJni() {
        LogUtil.w(TAG, "Enter Create jni");
        if (configNBModuleInfo.isProccessStop()) {
            stopServProc();
            LogUtil.w(TAG, "Create jni ProccessStop");
            return false;
        }

        int iMaxLp = 20;
        while (iMaxLp -- > 0) {
            if (null != mForceEventHandler)
                break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (null == mForceEventHandler) {
            LogUtil.w(TAG, "Create jni Handler null");
            return false;
        }

        aIpc2Jni = new ipc2jni(mForceEventHandler);

        if (null == aIpc2Jni) {
            LogUtil.w(TAG, "Create jni failed: null");
            return false;
        }

        initServiceSuccess = aIpc2Jni.initServer(AppFilePathUtil.getInstance().getSDCardBaseDirectory("liblog"));

        return initServiceSuccess;
    }

    private boolean startJni(String args) {
        LogUtil.w(TAG, "startJni initServiceSuccess = " + initServiceSuccess);
        if (false == initServiceSuccess) {
            return false;
        }

        if (true == configNBModuleInfo.isProccessStop()){
            LogUtil.w(TAG, "startJni ProccessStop");
            return false;
        }

        String client_path;
        if (Deviceinfo.getInstance().isUseRoot()){
            client_path = AppFilePathUtil.getInstance().getAppLibDirectory() + "datatests_android_devmodem";
        } else
            client_path = AppFilePathUtil.getInstance().getAppLibDirectory() + "libdatatests_so_devmodem.so";
        aIpc2Jni.set_su_file_path(Deviceinfo.getInstance().getSuOrShCommand() + " -c", "export LD_LIBRARY_PATH=" + AppFilePathUtil.getInstance()
                .getAppLibDirectory());
        boolean isSuccess = aIpc2Jni.run_client(client_path, args);
        if (isSuccess) {
            LogUtil.w(TAG, "startJni Success.");
        } else {
            LogUtil.w(TAG, "startJni failure.");
            LogUtil.i(TAG, "client_path = " + client_path);
            LogUtil.i(TAG, "args = " + args);
        }

        return isSuccess;
    }

    private boolean nbInitProcSuccess(){
        int iMaxLp = 20;
        while (iMaxLp -- > 0) {
            if (initProcSuccess)
                return true;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean nbServiceLockNetwork(ForceControler.ForceNet networkType) {
        LogUtil.w(TAG, "Enter nbServiceLockNetwork");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "Enter nbServiceLockNetwork init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "Enter nbServiceLockNetwork init failed");
            return false;
        }

        String strLockNetwork = "Network Type=" + networkType.getDescrition();
        LogUtil.w(TAG, "Enter nbServiceLockNetwork LockNetwork: " + strLockNetwork);
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_LockNetwork, strLockNetwork, strLockNetwork.length());

        return true;
    }

    public boolean nbLockNetwork(ForceControler.ForceNet networkType) {
        LogUtil.w(TAG, "Enter nbLockNetwork");
        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbLockNetwork Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbLockNetwork start Jni failed Args: " + strArgs);
        }

        bResult =  nbServiceLockNetwork(networkType);
        if (false == bResult) {
            LogUtil.w(TAG, "nbLockNetwork false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceLockFrequency(ForceControler.ForceNet networkType, String... args){
        LogUtil.w(TAG, "Enter nbServiceLockFrequency");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceLockFrequency init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceLockFrequency init failed");
            return false;
        }

        String strLockFrequency = "EARFCN=" + args[0]+"\r\n";
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_LockFreq, strLockFrequency, strLockFrequency.length());
        LogUtil.w(TAG, "nbServiceLockFrequency Freq: " + strLockFrequency);

        return true;
    }

    public boolean nbLockFrequency(ForceControler.ForceNet networkType, String... args) {
        LogUtil.w(TAG, "Enter nbLockFrequency");
        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbLockFrequency Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbLockFrequency start Jni failed Args: " + strArgs);
        }

        bResult = nbServiceLockFrequency(networkType, args);
        if (false == bResult) {
            LogUtil.w(TAG, "nbLockFrequency false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceUnLockFrequency(ForceControler.ForceNet networkType){
        LogUtil.w(TAG, "Enter nbServiceUnLockFrequency");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceUnLockFrequency init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceUnLockFrequency init failed");
            return false;
        }

        String strUnlockFreq = "";
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_LockFreq, strUnlockFreq, strUnlockFreq.length());

        return true;
    }

    public boolean nbUnLockFrequency(ForceControler.ForceNet networkType) {
        LogUtil.w(TAG, "Enter nbUnLockFrequency");
        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbUnLockFrequency Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbUnLockFrequency start Jni failed Args: " + strArgs);
        }

        bResult = nbServiceUnLockFrequency(networkType);
        if (false == bResult) {
            LogUtil.w(TAG, "nbUnLockFrequency false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceLockBand(ForceControler.ForceNet networkType, ForceControler.Band[] band) {
        LogUtil.w(TAG, "Enter nbServiceLockBand " + networkType.getDescrition());

        if (band.length <= 0)
            return false;

        String strLockBand = "Network Type=";
        if (networkType == ForceControler.ForceNet.NET_NBIot_CatM1)
            strLockBand += networkType.getDescrition();
        else if (networkType == ForceControler.ForceNet.NET_NBIot_NB1)
            strLockBand += networkType.getDescrition();
        else
            return false;
        strLockBand += "\r\n";

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceLockBand init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceLockBand init failed");
            return false;
        }

        for (ForceControler.Band band1: band) {
            strLockBand += band1.des;
            strLockBand += "\r\n";
        }
        strLockBand += "\r\n";

        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_LockBand, strLockBand, strLockBand.length());

        LogUtil.w(TAG, "nbServiceLockBand: " + strLockBand);

        return true;
    }

    public boolean nbLockBand(ForceControler.ForceNet networkType, ForceControler.Band[] band) {
        LogUtil.w(TAG, "Enter nbLockBand");
        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbLockBand Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbLockBand start Jni failed Args: " + strArgs);
        }

        bResult = nbServiceLockBand(networkType, band);
        if (false == bResult) {
            LogUtil.w(TAG, "nbLockBand false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceLockCell(ForceControler.ForceNet networkType, String... args){
        LogUtil.w(TAG, "Enter nbServiceLockCell");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceLockCell init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceLockCell init failed");
            return false;
        }

        String strLockCell = "EARFCN=" + args[0] + "\r\n";
        strLockCell += "PCI=" + args[1] + "\r\n";
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_LockCell, strLockCell, strLockCell.length());
        LogUtil.w(TAG, "nbServiceLockCell Freq: " + strLockCell);

        return true;
    }

    public boolean nbLockCell(ForceControler.ForceNet networkType, String... args) {
        LogUtil.w(TAG, "Enter nbLockCell");
        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbLockCell Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbLockCell start Jni failed Args: " + strArgs);
        }

        bResult = nbServiceLockCell(networkType, args);
        if (false == bResult) {
            LogUtil.w(TAG, "nbLockCell false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceUnLockCell(ForceControler.ForceNet networkType){
        LogUtil.w(TAG, "Enter nbServiceUnLockCell");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceUnLockCell init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceUnLockCell init failed");
            return false;
        }

        String strUnLockCell = "";
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_LockCell, strUnLockCell, strUnLockCell.length());
        LogUtil.w(TAG, "nbServiceUnLockCell Freq: " + strUnLockCell);

        return true;
    }

    public boolean nbUnLockCell(ForceControler.ForceNet networkType) {
        LogUtil.w(TAG, "Enter nbUnLockCell");
        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbUnLockCell Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbUnLockCell start Jni failed Args: " + strArgs);
        }

        bResult = nbServiceUnLockCell(networkType);
        if (false == bResult) {
            LogUtil.w(TAG, "nbUnLockCell false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceSettingScrambleState(boolean bFlag){
        LogUtil.w(TAG, "Enter nbServiceSettingScrambleState");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceSettingScrambleState init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceSettingScrambleState init failed");
            return false;
        }

        String strScrambleState = "Scramble State=" +  Boolean.toString(bFlag);
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_ScarmbleSetting, strScrambleState, strScrambleState.length());
        LogUtil.w(TAG, "nbServiceSettingScrambleState: " + strScrambleState);

        return true;
    }

    public boolean nbSettingScrambleState(boolean bFlag) {
        LogUtil.w(TAG, "Enter nbSettingScrambleState");

        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbSettingScrambleState Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbSettingScrambleState start Jni failed Args: " + strArgs);
            return false;
        }

        bResult = nbServiceSettingScrambleState(bFlag);
        if (false == bResult) {
            LogUtil.w(TAG, "nbSettingScrambleState false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceSetAPN(String arg){
        LogUtil.w(TAG, "Enter nbServiceSetAPN");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceSetAPN init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceSetAPN init failed");
            return false;
        }

        String strSetAPN = "APN=" +  arg;
        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_SetAPN, strSetAPN, strSetAPN.length());
        LogUtil.w(TAG, "nbServiceSetAPN: " + strSetAPN);

        return true;
    }

    public boolean nbSetAPN(String arg) {
        LogUtil.w(TAG, "Enter nbSetAPN");

        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbSetAPN Create Jni failed");
            return false;
        }

        String  strArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strArgs)) {
            LogUtil.w(TAG, "nbSetAPN start Jni failed Args: " + strArgs);
            return false;
        }

        bResult = nbServiceSetAPN(arg);
        if (false == bResult) {
            LogUtil.w(TAG, "nbSetAPN false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceSettingPSMSatae(Context context, String strArg){
        LogUtil.w(TAG, "Enter nbServiceSettingPSMState");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceSettingPSMState init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceSettingPSMState init failed");
            return false;
        }

        LogUtil.w(TAG, "nbServiceSettingPSMState: " + strArg);
        byte[] bytePSMConfig = {(byte) 0xFF, 0x00, 0x00, 0x00, 0x00};
        int iB_Index = strArg.indexOf("open");
        if (iB_Index > 0) {
            aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_PSM_Switch, strArg, strArg.length());
            bytePSMConfig[1] = 0x01;
            DatasetManager.getInstance(context).devWritePort(bytePSMConfig);
        } else {
            iB_Index = strArg.indexOf("close");
            if (iB_Index > 0) {
                aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_PSM_Switch, strArg, strArg.length());
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                aIpc2Jni.send_command(NB_TEST_COMMAND, 80016, strArg, strArg.length());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                aIpc2Jni.send_command(NB_TEST_COMMAND, 80017, strArg, strArg.length());

                bytePSMConfig[1] = 0x02;
                DatasetManager.getInstance(context).devWritePort(bytePSMConfig);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                aIpc2Jni.send_command(NB_TEST_COMMAND, 80012, strArg, strArg.length());
            }
        }
        return true;
    }

    public boolean nbSettingPSMSatae(Context context, String strArg) {
        LogUtil.w(TAG, "Enter nbSettingPSMSatae");

        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbSettingPSMSatae Create Jni failed");
            return false;
        }

        String strSettingArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strSettingArgs)) {
            LogUtil.w(TAG, "nbSettingPSMSatae start Jni failed Args: " + strSettingArgs);
            return false;
        }

        bResult = nbServiceSettingPSMSatae(context, strArg);
        if (false == bResult) {
            LogUtil.w(TAG, "nbSettingPSMSatae false");
        }

        onDestroy();

        return  bResult;
    }

    private boolean nbServiceSettingEDRXSatae(String strArg){
        LogUtil.w(TAG, "Enter nbServiceSettingEDRXState");

        if (false == initServiceSuccess) {
            LogUtil.w(TAG, "nbServiceSettingEDRXState init Service failed");
            return false;
        }

        if (false == nbInitProcSuccess()) {
            LogUtil.w(TAG, "nbServiceSettingEDRXState init failed");
            return false;
        }

        aIpc2Jni.send_command(NB_TEST_COMMAND, NB_DEV_Force_eDRX_Switch, strArg, strArg.length());
        LogUtil.w(TAG, "nbServiceSettingEDRXState: " + strArg);

        return true;
    }

    public boolean nbSettingEDRXSatae(String strArg) {
        LogUtil.w(TAG, "Enter nbSettingEDRXSatae");

        boolean bResult =  false;

        if (false == CreateJni()) {
            LogUtil.w(TAG, "nbSettingEDRXSatae Create Jni failed");
            return false;
        }

        String strSettingArgs = "-m command -z " + AppFilePathUtil.getInstance().getAppFilesDirectory("config");
        if (false == startJni(strSettingArgs)) {
            LogUtil.w(TAG, "nbSettingEDRXSatae start Jni failed Args: " + strSettingArgs);
            return false;
        }

        bResult = nbServiceSettingEDRXSatae(strArg);
        if (false == bResult) {
            LogUtil.w(TAG, "nbSettingEDRXSatae false");
        }

        onDestroy();

        return  bResult;
    }
}

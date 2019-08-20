package com.walktour.gui.locknet;

import android.content.Context;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.base.util.LogUtil;


/**
 * Created by luojun on 2017/12/7.
 */
public class NBDeviceControler extends ForceControler {
    private static final String TAG = "NBDeviceControler";

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public boolean lockNetwork(ForceNet networkType) {
        return false;
    }

    @Override
    public boolean lockNetwork(Context context, ForceNet networkType) {
        LogUtil.w(TAG, "Enter lockNetwork");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbLockNetwork(networkType);

        LogUtil.w(TAG, "lockNetwork Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public boolean unLockAll(ForceNet forceNets) {
        return false;
    }

    @Override
    public boolean unlockFrequency(Context context, ForceNet networkType) {
        LogUtil.w(TAG, "Enter unlockFrequency");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbUnLockFrequency(networkType);

        LogUtil.w(TAG, "unlockFrequency Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public boolean unlockCell(Context context, ForceNet networkType) {
        LogUtil.w(TAG, "Enter unlockCell");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbUnLockCell(networkType);

        LogUtil.w(TAG, "unlockCell Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public void release() {

    }

    @Override
    public boolean queryBand(ForceNet netType) {
        return false;
    }

    @Override
    public boolean queryFrequency(ForceNet netType) {
        return false;
    }

    @Override
    public boolean queryCell(ForceNet netType) {
        return false;
    }

    @Override
    public boolean lockBand(ForceNet netType, String arg) {
        return false;
    }

    @Override
    public boolean lockBand(ForceNet netType, Band[] band) {
        return false;
    }

    @Override
    public boolean lockBand(Context context, ForceNet netType, Band[] band) {
        LogUtil.w(TAG, "Enter LockBand");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbLockBand(netType, band);

        LogUtil.w(TAG, "lockBand Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public boolean lockFrequency(Context context, ForceNet netType, String... args) {
        LogUtil.w(TAG, "Enter lockFrequency");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbLockFrequency(netType, args);

        LogUtil.w(TAG, "lockFrequency Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public boolean lockCell(Context context, ForceNet netType, String... args) {
        LogUtil.w(TAG, "Enter unlockCell");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbLockCell(netType, args);

        LogUtil.w(TAG, "unlockCell Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public boolean campCell(ForceNet netType, String arg1, String arg2) {
        return false;
    }

    //@Override
    public boolean setAirplaneModeSwitch(Context context, boolean flag) {
        return false;
    }

    //@Override
    public boolean setVolteSwitch(Context context, boolean flag) {
        LogUtil.w(TAG, "Flag: " + Boolean.toString(flag));
        String strNVDirPath = "/nv/item_files/ims/IMS_enable\0";

        byte[] byteNVDirPathFront = {0x4B, 0x13, 0x09, 0x00, 0x7F, 0x1F};
        byte[] byteNVDirFullPath = new byte[strNVDirPath.length() + byteNVDirPathFront.length];

        System.arraycopy(byteNVDirPathFront, 0, byteNVDirFullPath, 0, byteNVDirPathFront.length);
        System.arraycopy(strNVDirPath.getBytes(), 0, byteNVDirFullPath, byteNVDirPathFront.length, strNVDirPath.length());

        DatasetManager.getInstance(context).devWritePort(byteNVDirFullPath);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] byteNVPutParamFront = {0x4B, 0x13, 0x26, 0x00, 0x01, 0x00, 0x00, 0x00, 0x7F, 0x1F, 0x00, 0x00, (byte) 0x93, 0x06};
        byte[] byteNVPutParamFull = new byte[byteNVPutParamFront.length + 1 + strNVDirPath.length()];
        System.arraycopy(byteNVPutParamFront, 0, byteNVPutParamFull, 0, byteNVPutParamFront.length);
        if (flag)
            byteNVPutParamFull[byteNVPutParamFront.length] = 1;
        else
            byteNVPutParamFull[byteNVPutParamFront.length] = 0;
        System.arraycopy(strNVDirPath.getBytes(), 0, byteNVPutParamFull,(byteNVPutParamFront.length + 1), strNVDirPath.length());

        return DatasetManager.getInstance(context).devWritePort(byteNVPutParamFull);
    }

    //@Override
    public boolean setScrambleState(Context context, boolean flag){
        LogUtil.w(TAG, "Enter setScrambleState");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbSettingScrambleState(flag);

        LogUtil.w(TAG, "lockFrequency Result: " + Boolean.toString(bResult));

        return bResult;
    }

    public boolean setAPN(Context context, String arg) {
        LogUtil.w(TAG, "Enter setAPN");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbSetAPN(arg);

        LogUtil.w(TAG, "setAPN Result: " + Boolean.toString(bResult));

        return bResult;
    }

    @Override
    public void makeVideoCall(Context context, String number) { return; }

    public boolean setPSMState(Context context, String strArg) {
        LogUtil.w(TAG, "Enter setPSMState");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbSettingPSMSatae(context, strArg);

        LogUtil.w(TAG, "setPSMState Result: " + Boolean.toString(bResult));



        return  bResult;
    }

    public boolean setEDRXState(Context context, String strArg) {
        LogUtil.w(TAG, "Enter setEDRXState");

        NBDeviceCtrlHandlerThread nbDeviceCtrlHandlerThread = new NBDeviceCtrlHandlerThread(context);

        nbDeviceCtrlHandlerThread.start();

        boolean bResult = nbDeviceCtrlHandlerThread.nbSettingEDRXSatae(strArg);

        LogUtil.w(TAG, "setEDRXState Result: " + Boolean.toString(bResult));

        return  bResult;
    }
}

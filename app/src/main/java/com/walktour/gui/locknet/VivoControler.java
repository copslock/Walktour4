package com.walktour.gui.locknet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.vivo.networkstate.RemoteNetProt;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AssetsUtils;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.share.model.DeviceInfoModel;


/**
 * vivo设备强制
 * Created by luojun on 2017/10/10.
 */

public class VivoControler extends ForceControler {
    private static final String TAG = "VIVOCONTROLER";

    /*
    * Vivo操作类
    * */
    private class VivoServiceControler {
        /*支持的网络类型*/
        private static final int NETWORK_MODE_WCDMA_PREF                  = 0; /* GSM/WCDMA (WCDMA preferred) */
        private static final int NETWORK_MODE_GSM_ONLY                    = 1; /* GSM only */
        private static final int NETWORK_MODE_WCDMA_ONLY                  = 2; /* WCDMA only */
        private static final int NETWORK_MODE_GSM_UMTS                    = 3; /* GSM/WCDMA (auto mode, according to PRL)
                                                                         AVAILABLE Application Settings menu*/
        private static final int NETWORK_MODE_CDMA                       = 4; /* CDMA and EvDo (auto mode, according to PRL)
                                                                         AVAILABLE Application Settings menu*/
        private static final int NETWORK_MODE_CDMA_NO_EVDO              = 5; /* CDMA only */
        private static final int NETWORK_MODE_EVDO_NO_CDMA              = 6; /* EvDo only */
        private static final int NETWORK_MODE_GLOBAL                     = 7; /* GSM/WCDMA, CDMA, and EvDo (auto mode, according to PRL)
                                                                        AVAILABLE Application Settings menu*/
        private static final int NETWORK_MODE_LTE_CDMA_EVDO             = 8; /* LTE, CDMA and EvDo */
        private static final int NETWORK_MODE_LTE_GSM_WCDMA             = 9; /* LTE, GSM/WCDMA */
        private static final int NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA = 10; /* LTE, CDMA, EvDo, GSM/WCDMA */
        private static final int NETWORK_MODE_LTE_ONLY                     = 11; /* LTE Only mode. */
        private static final int NETWORK_MODE_LTE_WCDMA                = 12; /* LTE/WCDMA */
        private static final int NETWORK_MODE_TDSCDMA_ONLY            = 13; /* TD-SCDMA only */
        private static final int NETWORK_MODE_TDSCDMA_WCDMA           = 14; /* TD-SCDMA and WCDMA */
        private static final int NETWORK_MODE_LTE_TDSCDMA             = 15; /* TD-SCDMA and LTE */
        private static final int NETWORK_MODE_TDSCDMA_GSM             = 16; /* TD-SCDMA and GSM */
        private static final int NETWORK_MODE_LTE_TDSCDMA_GSM         = 17; /* TD-SCDMA,GSM and LTE */
        private static final int NETWORK_MODE_TDSCDMA_GSM_WCDMA       = 18; /* TD-SCDMA, GSM/WCDMA */
        private static final int NETWORK_MODE_LTE_TDSCDMA_WCDMA       = 19; /* TD-SCDMA, WCDMA and LTE */
        private static final int NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA   = 20; /* TD-SCDMA, GSM/WCDMA and LTE */
        private static final int NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA       = 21; /*TD-SCDMA,EvDo,CDMA,GSM/WCDMA*/
        private static final int NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA   = 22; /* TD-SCDMA/LTE/GSM/WCDMA, CDMA, and EvDo */

        /*支持的操作类型*/
        private static final int VIVO_COMMAND_ID_GAIN_CELL_INFO             = 2;
        private static final int VIVO_COMMAND_ID_REBOOT_MODEM               = 13;
        private static final int VIVO_COMMAND_ID_READ_EFS_FILE              = 15;
        private static final int VIVO_COMMAND_ID_WRITE_EFS_FILE             = 16;
        private static final int VIVO_COMMAND_ID_DEL_EFS_FILE               = 16;
        private static final int VIVO_COMMAND_ID_SET_BAND                   = 20;

        private int m_PhoneID = -1; //当前操作的SIM卡,默认为无效, 优先操作SIM 0, 再操作SIM1 1
        private RemoteNetProt mRemoteNetProt = null;
        private Context mContext = null;
        private int mServiceType = -1;

        /*
        *监听服务
        * */
        private class VivoServiceConnection implements ServiceConnection{
            private boolean isServiceBindfFlag = false;

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                LogUtil.w(TAG, "isServiceBindfFlag: " + Boolean.toString(isServiceBindfFlag));

                mRemoteNetProt = RemoteNetProt.Stub.asInterface(service);

                if (null == mRemoteNetProt) {
                    LogUtil.w(TAG, "bind service failled");
                    return ;
                }
                try {
                    service.linkToDeath(mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                isServiceBindfFlag = true;

                LogUtil.w(TAG, "Service Connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isServiceBindfFlag = false;

                LogUtil.w(TAG, "service disconnected");
            }

            public boolean isServiceBindfFlag(){
                LogUtil.w(TAG, "getServiceBindfFlag: " + Boolean.toString(isServiceBindfFlag));
                return isServiceBindfFlag && (null != mRemoteNetProt);
            }
        }

        private Intent intentVivoService = null;
        private VivoServiceConnection mVivoServiceConnection = new VivoServiceConnection();
        private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                LogUtil.w(TAG, "binderDied");
                if (null == mRemoteNetProt)
                    return ;

                mRemoteNetProt.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mRemoteNetProt = null;
                startBindService();
            }
        };


        /*函数功能 ：将byte[] 数组转化为Vivo需求的特殊格式的 String
        * @param   ：待转化数组
        * @return  ：转化之后的数组
        * */
        private String bytesToHexString(byte[] src) {
            if ((null == src) || (src.length <= 0))
                return null;

            StringBuilder stringBuilder = new StringBuilder("");

            for (int iLp = 0; iLp < src.length; iLp ++) {
                int v = src[iLp] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        }

        /**
         * 函数功能 ：开启服务
         * @param  ：
         * @return ：绑定成功为true, 否则为flase
         */
        private boolean startBindService() {
            if (null == mContext){
                LogUtil.w(TAG, "Start Bind Service Connext Null!");
                return false;
            }

            if (null != intentVivoService)
                return true;

            intentVivoService = new Intent("com.vivo.networkstate.drivetest");

            boolean bRes = mContext.bindService(intentVivoService, mVivoServiceConnection, Context.BIND_AUTO_CREATE);

            LogUtil.w(TAG, "startBindService: " + Boolean.toString(bRes));

            return bRes;
        }

        /**
         * 函数功能 ：停止服务
         * @param  ：
         * @return ：
         */
        private void stopVivoService()
        {
            LogUtil.w(TAG, "Enter Stop Service");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogUtil.w(TAG, "Enter Stop Service 1");
            if (mVivoServiceConnection.isServiceBindfFlag()) {
                LogUtil.w(TAG, "Enter Stop Service 1.1");
                mContext.unbindService(mVivoServiceConnection);
                if (null != intentVivoService) {
                    LogUtil.w(TAG, "Enter Stop Service 1.2");
                    mContext.stopService(intentVivoService);
                    intentVivoService = null;
                }
            }
            LogUtil.w(TAG, "Enter Stop Service 2");
        }

        /**
         * 函数功能 ：重启Modem, 部分终端发送命令,重启Modem生效;
         * @param  ：
         * @return ：正确完成操作为true, 否则为flase
         * @note ： 插入电信SIM卡时发送该指令会崩溃,故屏蔽该功能,改为通过重启手机实现操作;
         */
        private boolean vivoRestartModem() {
            if (true)
                return true;

            LogUtil.w(TAG, "Enter vivoRestartModem Fasle");

            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_REBOOT_MODEM, "empty");
                LogUtil.w(TAG, "vivoRestartModem Result: " + strResult);

                if (strResult.indexOf("ok") >= 0)
                    return true;

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            return false;
        }

        /**
         * 函数功能 ：初始化VivoServiceControler类, 成功开启服务才能发送命令;
         * @param  ：
         * @return ：
         */
        public VivoServiceControler(Context context, int iServiceType) {
            mContext = context;
            mServiceType = iServiceType;

            if (startBindService()) {
                int iMaxCount = 10;
                while (iMaxCount > 0) {
                    if (null != mRemoteNetProt)
                        break;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    iMaxCount --;
                }

                if (null == mRemoteNetProt) {
                    LogUtil.w(TAG, "vivo service failed!");

                    return ;
                }

                try {
                    m_PhoneID = mRemoteNetProt.getDefaultDataPhoneId();
                    LogUtil.w(TAG, "vivo Default Data PhoneId: " + Integer.toString(m_PhoneID));
                } catch (RemoteException e) {
                    e.printStackTrace();
                    LogUtil.w(TAG, e);
                }
            }
        }

        /**
         * 函数功能 ：锁网操作;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * 注    释 ：需要将网络类型同步后完成操作;
         * @return ：正确完成操作为true, 否则为flase
         */
        private boolean vivoLockNetTypeCtrl(ForceNet networkType) {
            LogUtil.w(TAG, "Vivo Lock NetType: " + networkType + " PhoneiID:" + Integer.toString(m_PhoneID) + "SeviceType: " + mServiceType);
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            int type = NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA;
            switch (networkType) {
                case NET_AUTO:
                    type = NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA;
                    break;
                case NET_GSM:
                    type = NETWORK_MODE_GSM_ONLY;
                    break;
                case NET_WCDMA:
                    type = NETWORK_MODE_WCDMA_ONLY;
                    break;
                case NET_TDSCDMA:
                    type = NETWORK_MODE_TDSCDMA_ONLY;
                    break;
                case NET_CDMA:
                    type = NETWORK_MODE_CDMA_NO_EVDO;
                    break;
                case NET_EVDO:
                    type = NETWORK_MODE_EVDO_NO_CDMA;
                    break;
                case NET_CDMA_EVDO:
                    type = NETWORK_MODE_CDMA;
                    break;
                case NET_FDD_LTE:
                case NET_TDD_LTE:
                case NET_LTE:
                    type = NETWORK_MODE_LTE_ONLY;
                    break;
                case NET_GSM_WCDMA:
                    type = NETWORK_MODE_GSM_UMTS;
                    break;
                case NET_GSM_TDSCDMA:
                    type = NETWORK_MODE_TDSCDMA_GSM;
                    break;
                case NET_TDSCDMA_LTE:
                    type = NETWORK_MODE_LTE_TDSCDMA;
                    break;
                case NET_WCDMA_LTE:
                    type = NETWORK_MODE_LTE_WCDMA;
                    break;
                default:
                    break;
            }

            try {
                int iNetworkType = mRemoteNetProt.getPreferredNetworkType(m_PhoneID);
                LogUtil.w(TAG, "vivoLockNetTypeCtrl NetworkType: " + iNetworkType + " type: " + type);

                if (iNetworkType == type) {
                    return true;
                } else {
                    //if (vivoLockAllBandCtrl())
                        return mRemoteNetProt.setPreferredNetworkType(m_PhoneID, type);
                }
            } catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            //return false;
        }

        /**
         * 函数功能：锁网操作;
         * @param ：networkType-网络类型,为外部定义统一类型
         * 注    释：操作完成后需停止服务,以免挂起;
         * @return ：正确完成操作为true, 否则为flase
         */
        public boolean vivoLockNetType(ForceNet networkType) {
            boolean bResult = vivoLockNetTypeCtrl(networkType);
            LogUtil.w(TAG, "vivoLockNetType Result: " + Boolean.toString(bResult));
            //不需要重启

            if (bResult)
                vivoRestartModem();

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：解锁频点;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * 注    释 ：
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private  boolean vivoUnlockFrequencyCtrl(ForceNet networkType) {
            LogUtil.w(TAG, "Enter vivoUnlockFrequencyCtrl PhoneID: " + Integer.toString(m_PhoneID));
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            String strFreqParams = "";
            if (networkType == ForceNet.NET_WCDMA) {
                strFreqParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_freq_lock_item,";
            } else if(networkType == ForceNet.NET_LTE) {
                strFreqParams = "/nv/item_files/modem/lte/rrc/csp/earfcn_lock,";
            }

            if (strFreqParams.length() <= 0)
                return  false;

            /*
            if(false == vivoLockNetTypeCtrl(networkType))
                return false;
             */
            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_DEL_EFS_FILE, strFreqParams);
                LogUtil.w(TAG, "Enter vivoUnlockFrequencyCtrl Result: " + strResult);
                if (strResult.indexOf("ok") >= 0)
                    return true;
            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return  false;
        }

        private  boolean vivoUnlockFrequencyCtrlOnePlus(ForceNet networkType) {
            LogUtil.w(TAG, "Enter vivoUnlockFrequencyCtrl PhoneID: " + Integer.toString(m_PhoneID));
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            String strFreqParams = "";
            if (networkType == ForceNet.NET_WCDMA) {
                strFreqParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_freq_lock_item,";
            } else if(networkType == ForceNet.NET_LTE) {
                strFreqParams = "/nv/item_files/modem/lte/rrc/efs/cell_restrict_opt_params,";
            }

            if (strFreqParams.length() <= 0)
                return  false;

            /*
            if(false == vivoLockNetTypeCtrl(networkType))
                return false;
             */
            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_DEL_EFS_FILE, strFreqParams);
                LogUtil.w(TAG, "Enter vivoUnlockFrequencyCtrl Result: " + strResult);
                if (strResult.indexOf("ok") >= 0)
                    return true;
            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return  false;
        }

        /**
         * 函数功能 ：解锁频点;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * 注    释 ：1. 成功发送命令后,需重启Modem才能生效
         *            2. 完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoUnlockFrequency(ForceNet networkType) {
            boolean bResult;

            Deviceinfo deviceinfo = Deviceinfo.getInstance();
            if (deviceinfo.getDevicemodel().equals("V1824A"))
                bResult = vivoUnlockFrequencyCtrlOnePlus(networkType);
            else
                bResult = vivoUnlockFrequencyCtrl(networkType);

            if (bResult)
                vivoRestartModem();

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：锁定频点;
         * @param  ：networkType-网络类型,为外部定义统一类型
         *            args - args[0] 为Band值,无用; args[1] 为待锁定 Freq值
         * 注    释 ：
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoLockFrequencyCtrl(ForceNet netType, String... args) {
            LogUtil.w(TAG, "vivoLockFrequencyCtrl m_PhoneID: " + Integer.toString(m_PhoneID));
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt) || (args.length < 2))
                return false;

            String strFreqParams = "";
            if (netType == ForceNet.NET_WCDMA) {
                byte[] frequency = UtilsMethod.shortToBytes((short) Integer.parseInt(args[1]));
                String strFreq = bytesToHexString(frequency);

                strFreqParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_freq_lock_item,";
                strFreqParams += strFreq;
            } else if(netType == ForceNet.NET_LTE) {
                byte[] frequency = UtilsMethod.shortToBytes((short) Integer.parseInt(args[1]));
                String strFreq = bytesToHexString(frequency);

                strFreqParams = "/nv/item_files/modem/lte/rrc/csp/earfcn_lock,";
                strFreqParams += strFreq;
                strFreqParams += strFreq;
            }

            if (strFreqParams.length() <= 0)
                return  false;

            try {
                LogUtil.w(TAG, "vivoLockFrequencyCtrl Frequency: " + strFreqParams);

                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_WRITE_EFS_FILE, strFreqParams);

                LogUtil.w(TAG, "vivoLockFrequencyCtrl Result: " + strResult);

                //String strReadResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_READ_EFS_FILE,"/nv/item_files/modem/lte/rrc/csp/earfcn_lock");
                //LogUtil.w(TAG, "vivoLockFrequencyCtrl Read Result: " + strReadResult);

                if (strResult.indexOf("ok") >= 0)
                    return true;

            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return false;
        }

        private boolean vivoLockFrequencyCtrlOnePlus(ForceNet netType, String... args) {
            LogUtil.w(TAG, "vivoLockFrequencyCtrl m_PhoneID: " + Integer.toString(m_PhoneID));
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt) || (args.length < 2))
                return false;

            String strFreqParams = "";
            if (netType == ForceNet.NET_WCDMA) {
                byte[] frequency = UtilsMethod.shortToBytes((short) Integer.parseInt(args[1]));
                String strFreq = bytesToHexString(frequency);

                strFreqParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_freq_lock_item,";
                strFreqParams += strFreq;
            } else if(netType == ForceNet.NET_LTE) {
                byte[] frequency = UtilsMethod.intToByteArray1(Integer.parseInt(args[1]));
                String strFreq = bytesToHexString(frequency);

                byte[] frequency_padding = new byte[28];
                for (int iLp = 0; iLp < frequency_padding.length; iLp ++)
                    frequency_padding[iLp] = 0x00;
                String strFreq_padding = bytesToHexString(frequency_padding);
                strFreqParams = "/nv/item_files/modem/lte/rrc/efs/cell_restrict_opt_params,";
                strFreqParams += strFreq_padding;
                strFreqParams += strFreq;
                strFreqParams += strFreq;
            }

            if (strFreqParams.length() <= 0)
                return  false;

            try {
                LogUtil.w(TAG, "vivoLockFrequencyCtrl Frequency: " + strFreqParams);

                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_WRITE_EFS_FILE, strFreqParams);

                LogUtil.w(TAG, "vivoLockFrequencyCtrl Result: " + strResult);

                //String strReadResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_READ_EFS_FILE,"/nv/item_files/modem/lte/rrc/csp/earfcn_lock");
                //LogUtil.w(TAG, "vivoLockFrequencyCtrl Read Result: " + strReadResult);

                if (strResult.indexOf("ok") >= 0)
                    return true;

            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return false;
        }

        /**
         * 函数功能 ：锁定频点;
         * @param  ：networkType-网络类型,为外部定义统一类型
         *            args - args[0] 为Band值,无用; args[1] 为待锁定 Freq值
         * 注    释 ：1. 成功发送命令后,需重启Modem才能生效
         *            2. 完成操作后,需要停止服务,以免挂起
         *            3. 当前无法真正锁住频点,当移动到信号较差点时,会切换到其他地方. 移动到有覆盖地方时,飞行模式后可切换回锁定频点
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoLockFrequency(ForceNet networkType, String... args) {
            boolean bResult = false;

            Deviceinfo deviceinfo = Deviceinfo.getInstance();
            if (deviceinfo.getDevicemodel().equals("V1824A"))
                bResult = vivoLockFrequencyCtrlOnePlus(networkType, args);
            else
                bResult = vivoLockFrequencyCtrl(networkType, args);

            if (bResult)
                vivoRestartModem();

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：解锁小区;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * 注    释 ：
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoUnlockCellCtrl(ForceNet networkType) {
            LogUtil.w(TAG, "Enter vivoUnlockCellCtrl!");
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            String strESFParams = "";
            if (networkType == ForceNet.NET_WCDMA) {
                strESFParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_enable_psc_lock,";
            } else if(networkType == ForceNet.NET_LTE) {
                strESFParams = "/nv/item_files/modem/lte/rrc/csp/pci_lock,";
            }

            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_DEL_EFS_FILE, strESFParams);
                LogUtil.w(TAG, "vivoUnlockCellCtrl Result: " + strResult);
                if (strResult.indexOf("ok") >= 0)
                    return true;
            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }
            return false;
        }

        private boolean vivoUnlockCellCtrlOnePlus(ForceNet networkType) {
            LogUtil.w(TAG, "Enter vivoUnlockCellCtrl!");
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            String strESFParams = "";
            if (networkType == ForceNet.NET_WCDMA) {
                strESFParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_enable_psc_lock,";
            } else if(networkType == ForceNet.NET_LTE) {
                strESFParams = "/nv/item_files/modem/lte/rrc/efs/cell_lock_list,";
            }

            /*
            if(false == vivoLockNetTypeCtrl(networkType))
                return false;
             */

            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_DEL_EFS_FILE, strESFParams);
                LogUtil.w(TAG, "vivoUnlockCellCtrl Result: " + strResult);
                if (strResult.indexOf("ok") >= 0)
                    return true;
            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }
            return false;
        }

        /**
         * 函数功能 ：解锁小区;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * 注    释 ：1. 成功发送命令后,需重启Modem才能生效
         *            2. 完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoUnlockCell(ForceNet networkType) {
            boolean  bResult;

            Deviceinfo deviceinfo = Deviceinfo.getInstance();
            if (deviceinfo.getDevicemodel().equals("V1824A"))
                bResult = vivoUnlockCellCtrlOnePlus(networkType);
            else
                bResult = vivoUnlockCellCtrl(networkType);

            if (bResult)
                vivoRestartModem();

            stopVivoService();

            return  bResult;

        }

        /**
         * 函数功能 ：锁定小区;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * @param  ：args - args[0] 为Band值,无用; args[1] 为待锁定 Freq值; args[2] 为待锁定 PCI 值
         * 注    释 ：1. WCDMA 只支持锁定当前服务小区
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoLockCellCtrl(ForceNet networkType, String... args) {
            LogUtil.w(TAG, "Enter vivoLockCellCtrl!");
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt) || (args.length < 3))
                return false;

            String strESFParams = "";
            if (networkType == ForceNet.NET_WCDMA) {
                strESFParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_enable_psc_lock,";

                strESFParams += "01"; //lock
            } else if(networkType == ForceNet.NET_LTE) {
                strESFParams = "/nv/item_files/modem/lte/rrc/csp/pci_lock,";

                byte[] earfcn = UtilsMethod.shortToBytes((short) Integer.parseInt(args[1]));
                byte[] pci = UtilsMethod.shortToBytes((short) Integer.parseInt(args[2]));
                strESFParams += bytesToHexString(earfcn);
                strESFParams += bytesToHexString(pci);
            }

            /*
            if(false == vivoLockNetTypeCtrl(networkType))
                return false;
             */

            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_WRITE_EFS_FILE, strESFParams);
                LogUtil.w(TAG, "vivoLockCellCtrl Result: " + strResult);
                if (strResult.indexOf("ok") >= 0)
                    return true;
            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return  false;
        }

        private boolean vivoLockCellCtrlOnePlus(ForceNet networkType, String... args) {
            LogUtil.w(TAG, "Enter vivoLockCellCtrlOnePlus!");
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt) || (args.length < 3))
                return false;

            String strESFParams = "";
            if (networkType == ForceNet.NET_WCDMA) {
                strESFParams = "/nv/item_files/wcdma/rrc/wcdma_rrc_enable_psc_lock,";

                strESFParams += "01"; //lock
            } else if(networkType == ForceNet.NET_LTE) {
                strESFParams = "/nv/item_files/modem/lte/rrc/efs/cell_lock_list,";

                byte[] earfcn = UtilsMethod.intToByteArray1(Integer.parseInt(args[1]));
                byte[] pci = UtilsMethod.intToByteArray1(Integer.parseInt(args[2]));
                byte[] cellCount = new byte[4];
                cellCount[0] = 0x01;
                cellCount[1] = 0x00;
                cellCount[2] = 0x00;
                cellCount[3] = 0x00;
                strESFParams += bytesToHexString(cellCount);
                strESFParams += bytesToHexString(pci);
                strESFParams += bytesToHexString(earfcn);
                byte[] cell_padding = new byte[152];
                for (int iLp = 0; iLp < cell_padding.length; iLp ++)
                    cell_padding[iLp] = 0x00;
                strESFParams += bytesToHexString(cell_padding);
            }

            /*
            if(false == vivoLockNetTypeCtrl(networkType))
                return false;
             */

            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_WRITE_EFS_FILE, strESFParams);
                LogUtil.w(TAG, "vivoLockCellCtrl Result: " + strResult);
                if (strResult.indexOf("ok") >= 0)
                    return true;
            }catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return  false;
        }


        /**
         * 函数功能 ：锁定小区;
         * @param  ：networkType-网络类型,为外部定义统一类型
         * @param  ：args - args[0] 为 Band 值,无用; args[1] 为待锁定 Freq 值; args[2] 为待锁定 PCI 值
         * 注    释 ：1. WCDMA 只支持锁定当前服务小区
         *            2. 成功发送命令后,需重启Modem才能生效
         *            3. 完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoLockCell(ForceNet networkType, String... args) {
            boolean bResult;

            Deviceinfo deviceinfo = Deviceinfo.getInstance();
            if (deviceinfo.getDevicemodel().equals("V1824A"))
                bResult = vivoLockCellCtrlOnePlus(networkType, args);
            else
                bResult = vivoLockCellCtrl(networkType, args);

            if (bResult)
                vivoRestartModem();

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：锁定所有的Band;
         * @param  ：
         * 注    释 ：
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoLockAllBandCtrl(){
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            String stringBand = "1111111111111111111111111111111111111111111111111111111111111111" +
                    "1111111111111111111111111111111111111111111111111111111111111111" +
                    "1111111111111111111111111111111111111111111111111111111111111111";
            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_SET_BAND, stringBand);
                LogUtil.w(TAG, "Lock Band: " + strResult);

                //if (strResult.indexOf("ok") > 0)
                    return true;
            } catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            //return  false;
        }

        /**
         * 函数功能 ：锁定Band;
         * @param  ：networkType-网络类型,为外部定义统一类型
         *            band - 待锁定band列表
         * 注    释 ：
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoLockBandCtrl(ForceNet networkType, Band[] band){
            LogUtil.w(TAG, "Enter vivoLockBandCtrl");
            if (band.length == 0)
                return false;
            if ((-1 == m_PhoneID) || (null == mRemoteNetProt))
                return false;

            long iband_pref = 0, itdscdma_band_pref = 0, ilte_band_pref = 0;
            for (Band b : band) {
                switch (b) {
                    case Auto:
                        return vivoLockAllBandCtrl();
                    case G850:
                        iband_pref += ((long) 1 << 19);
                        break;
                    case G900:
                        iband_pref += ((long) 1 << 8);
                        iband_pref += ((long) 1 << 9);
                        iband_pref += ((long) 1 << 20);
                        break;
                    case G1800:
                        iband_pref += ((long) 1 << 7);
                        break;
                    case G1900:
                        iband_pref += ((long) 1 << 21);
                        break;
                    case G450:
                        iband_pref += ((long) 1 << 16);
                        break;
                    case G480:
                        iband_pref += ((long) 1 << 17);
                        break;
                    case G750:
                        iband_pref += ((long) 1 << 18);
                        break;
                    case G9000:
                        iband_pref += ((long) 1 << 20);
                        break;

                    case W2100:
                        iband_pref += ((long) 1 << 22);
                        break;
                    case W1900:
                        iband_pref += ((long) 1 << 23);
                        break;
                    case W1800:
                        iband_pref += ((long) 1 << 24);
                        break;
                    case W1700:
                        iband_pref += ((long) 1 << 25);		//WCDMA 1700(U.S.)
                        iband_pref += ((long) 1 << 50);	    //WCDMA 1700(Japan.)
                        break;
                    case W850:
                        iband_pref += ((long) 1 << 26);
                        break;
                    case W800:
                        iband_pref += ((long) 1 << 27);
                        break;
                    case W2600:
                        iband_pref += ((long) 1 << 48);
                        break;
                    case W900:
                        iband_pref += ((long) 1 << 49);
                        break;
                    case TBandA:
                        itdscdma_band_pref += ((long)0x01);
                        break;
                    case TBandF:
                        itdscdma_band_pref += ((long)0x20);
                        break;
                    default:
                        if (b.name().startsWith("L")) {
                            String strBand = b.name();
                            strBand = strBand.substring(strBand.indexOf("L") + 1);
                            int iBv = Integer.parseInt(strBand);
                            switch (iBv) {
                                case 15:
                                case 16:
                                case 22:
                                case 27:
                                case 30:
                                case 31:
                                case 32:
                                    ilte_band_pref += 0; //未定义
                                    break;
                                default: {
                                    ilte_band_pref += ((long) 1 << (iBv - 1));
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
            //String preIf = "1";
            String stringBand = "0000000000000000000000000000000000000000000000000000000000000000" +
                    "0000000000000000000000000000000000000000000000000000000000000000" +
                    "0000000000000000000000000000000000000000000000000000000000000000";
            StringBuilder stringBandBuilder = new StringBuilder(stringBand);
            int iLength = Long.SIZE;
            for (int iLp = 0; iLp < iLength; iLp ++) {
                if (0x01 == ((iband_pref >> iLp) & 0x01)) {
                    stringBandBuilder.setCharAt(iLp, '1');
                }
                if (0x01 == ((itdscdma_band_pref >> iLp) & 0x01)) {
                    stringBandBuilder.setCharAt(64 + iLp, '1');
                }
                if (0x01 == ((ilte_band_pref >> iLp) & 0x01)) {
                    stringBandBuilder.setCharAt(64 * 2 + iLp, '1');
                }
            }
            stringBand = stringBandBuilder.toString();
            LogUtil.w(TAG, "Band Params: " +  stringBand);

            try {
                String strResult = mRemoteNetProt.sendMiscInfo(m_PhoneID, VIVO_COMMAND_ID_SET_BAND, stringBand);
                LogUtil.w(TAG, "Lock Band: " + strResult);

                if (strResult.indexOf("ok") > 0)
                    return true;
            } catch (RemoteException e) {
                e.printStackTrace();

                return false;
            }

            return false;
        }

        /**
         * 函数功能 ：锁定Band;
         * @param  ：networkType-网络类型,为外部定义统一类型
         *            band - 待锁定band列表
         * 注    释 ：完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoLockBand(ForceNet networkType, Band[] band){
            boolean bResult = vivoLockBandCtrl(networkType, band);

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：开启飞行模式;
         * @param  ：flag - 为true则开启飞行模式,为false则关闭飞行模式
         * 注    释 ：完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoSetAirplaneSwitchCtrl(boolean flag){
            if (null == mRemoteNetProt)
                return false;

            try {
                mRemoteNetProt.airplaneModeOnoff(flag);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        /**
         * 函数功能 ：开启飞行模式;
         * @param  ：flag - 为true则开启飞行模式,为false则关闭飞行模式
         * 注    释 ：完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoSetAirplaneSwitch(boolean flag){
            boolean bResult = vivoSetAirplaneSwitchCtrl(flag);

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：volte开关;
         * @param  ：flag - 为true则开启,为false则关闭
         * 注    释 ：完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        private boolean vivoSetVolteSwitchCtrl(boolean flag) {
            if (null == mRemoteNetProt)
                return  false;

            try {
                mRemoteNetProt.setVolteOnOff(flag, 0);
            } catch (RemoteException e) {
                e.printStackTrace();

                return  false;
            }

            return true;
        }

        /**
         * 函数功能 ：volte开关;
         * @param  ：flag - 为true则开启,为false则关闭
         * 注    释 ：完成操作后,需要停止服务,以免挂起
         * @return ：成功发送命令返回true, 否则返回false;
         */
        public boolean vivoSetVolteSwitch(boolean flag) {
            boolean bResult = vivoSetVolteSwitchCtrl(flag);

            stopVivoService();

            return bResult;
        }

        /**
         * 函数功能 ：Vivo的视频电话
         *
         * @param ：number - 电话号码
         *                注    释 ：完成操作后,需要停止服务,以免挂起
         */
        public void vivoMakeVideoCall(String number) {
            if (null == mRemoteNetProt)
                return;

            try {
                mRemoteNetProt.makecustomphonecall(number, "", true, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
                return;
            }

            stopVivoService();
        }
    }

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
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 0);

        boolean bResult = vivoServiceControler.vivoLockNetType(networkType);

        return  bResult;
    }

    @Override
    public boolean unLockAll(ForceNet forceNets) {
        return true;
    }

    @Override
    public boolean unlockFrequency(Context context, ForceNet networkType) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 1);

        boolean bResult = vivoServiceControler.vivoUnlockFrequency(networkType);

        return  bResult;
    }

    @Override
    public boolean unlockCell(Context context, ForceNet networkType) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 2);

        boolean bResult = vivoServiceControler.vivoUnlockCell(networkType);

        return  bResult;
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
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 3);

        boolean bResult = vivoServiceControler.vivoLockBand(netType, band);

        return  bResult;
    }

    @Override
    public boolean lockFrequency(Context context, ForceNet netType, String... args) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 4);

        boolean bResult = vivoServiceControler.vivoLockFrequency(netType, args);

        return  bResult;
    }

    @Override
    public boolean lockCell(Context context, ForceNet netType, String... args) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 5);

        boolean bResult = vivoServiceControler.vivoLockCell(netType, args);

        return  bResult;
    }

    @Override
    public boolean campCell(ForceNet netType, String arg1, String arg2) {
        return false;
    }

    public boolean setAirplaneModeSwitch(Context context, boolean flag) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 6);

        boolean bResult = vivoServiceControler.vivoSetAirplaneSwitch(flag);

        return  bResult;
    }

    public boolean setVolteSwitch(Context context, boolean flag) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 7);

        boolean bResult = vivoServiceControler.vivoSetVolteSwitch(flag);

        return  bResult;
    }

    //@Override
    public boolean setScrambleState(Context context, boolean flag){
        return  false;
    }

    @Override
    public boolean setAPN(Context context, String arg) {
        return false;
    }

    public void makeVideoCall(Context context, String number) {
        VivoServiceControler vivoServiceControler = new VivoServiceControler(context, 6);

        vivoServiceControler.vivoMakeVideoCall(number);
    }
}

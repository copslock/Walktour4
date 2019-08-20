package com.walktour.externalinterface.zte;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.walktour.base.util.ToastUtil;
import com.walktour.externalinterface.AidlTestControllor;
import com.walktour.externalinterface.event.AidlEvnet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Zte Aidl
 *
 * @author zhicheng.chen
 * @date 2018/12/5
 */
public class WalktourZteService extends Service {

    private AidlTestControllor mControllor = new AidlTestControllor();
    private ITaskCallback mCallback;
    private IWalktourZTE.Stub mZte = new IWalktourZTE.Stub() {
        @Override
        public int startLogging(String key, String logmask, boolean isEnablePcap) throws RemoteException {
            return 0;
        }

        @Override
        public int stopLogging(String key) throws RemoteException {
            return 0;
        }

        @Override
        public boolean isLogging(String key) throws RemoteException {
            return false;
        }

        @Override
        public int startTest(String key) throws RemoteException {
            return getController().startTest(WalktourZteService.this);
        }

        @Override
        public int stopTest(String key) throws RemoteException {
            return getController().stopTest(WalktourZteService.this);
        }

        @Override
        public boolean isTesting(String key) throws RemoteException {
            return getController().isTesting();
        }

        @Override
        public int getLoggingTime(String key) throws RemoteException {
            return (int) getController().getLoggingTime();
        }

        @Override
        public double getDataSize(String key) throws RemoteException {
            return getController().getDataSize();
        }

        @Override
        public int getLogFileCount(String key) throws RemoteException {
            return getController().getLogFileCount();
        }

        @Override
        public String getLogFileDir(String key) throws RemoteException {
            return getController().getLogFileDir();
        }

        @Override
        public int getVersion(String key) throws RemoteException {
            return getController().getVersionCode();
        }

        @Override
        public boolean controlCommunication(String key, boolean isEnable) throws RemoteException {
            return false;
        }

        @Override
        public int loadTaskFile(String key, String fileName) throws RemoteException {
            getController().loadTaskFile(AidlTestControllor.TYPE_XML, fileName);
            return 0;
        }

        @Override
        public int clearTaskFile(String key) throws RemoteException {
            getController().clearTaskFile();
            return 0;
        }

        @Override
        public int startTCPCapture(String key) throws RemoteException {
            return 0;
        }

        @Override
        public int stopTCPCapture(String key) throws RemoteException {
            return 0;
        }

        @Override
        public boolean isTCPTesting(String key) throws RemoteException {
            return false;
        }

        @Override
        public String getLicenseInfo(String key) throws RemoteException {
            return getController().getLicenseInfo();
        }

        @Override
        public void uploadFileFinished(String tskExtInf, String fileName) throws RemoteException {

        }

        @Override
        public void registerCallback(ITaskCallback cb) throws RemoteException {
            mCallback = cb;
        }

        @Override
        public void unregisterCallback(ITaskCallback cb) throws RemoteException {
            mCallback = null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mZte;
    }

    private void toast(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTestStatus(AidlEvnet evnet) {
        try {
            if (evnet.eventType == AidlEvnet.NOTIFY_START_TEST) {
                getController().setStartTime(System.currentTimeMillis());
                if (mCallback != null) {
                    mCallback.notifyStartedTask("");
                }
            } else if (evnet.eventType == AidlEvnet.NOTIFY_STOP_TEST) {
                getController().setStopTime(System.currentTimeMillis());
                if (mCallback != null) {
                    mCallback.notifyFinishedTask(null);
                }
            } else if (evnet.eventType == AidlEvnet.NOTIFY_FILE_CREATE) {
                if (evnet.bundle != null) {
                    int count = evnet.bundle.getInt(AidlTestControllor.EXTRA_FILE_COUNT, 0);
                    getController().setLogFileCount(count);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private AidlTestControllor getController() {
        return mControllor;
    }
}

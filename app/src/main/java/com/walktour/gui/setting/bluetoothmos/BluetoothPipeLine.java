package com.walktour.gui.setting.bluetoothmos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.walktour.base.util.SafeHandler;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.WalktourApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 蓝牙传输管道，处理连接，通信等操作
 *
 * @author zhicheng.chen
 * @date 2019/4/24
 */
public class BluetoothPipeLine {

    private static final String TAG = "BluetoothPipeLine";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothPipeLine";

    private static final UUID MY_UUID_SECURE = UUID.fromString("12d64a43-a279-48c4-91db-55fea7b2adc5");

    // 显示当前连接状态
    private int mState;
    public static final int STATE_NONE = 0;       // 什么都不做
    public static final int STATE_LISTEN = 1;     // 监听连接
    public static final int STATE_CONNECTING = 2; // 正在建立连接
    public static final int STATE_TRANSFER = 3;  // 现在连接到一个远程的设备，可以进行传输

    public static final int BLUE_TOOTH_DIALOG = 0x111;
    public static final int BLUE_TOOTH_WRAITE = 0X222;
    public static final int BLUE_TOOTH_READ = 0X333;
    public static final int BLUE_TOOTH_SUCCESS = 0x444;
    public static final int BLUE_TOOTH_TOAST = 0x555;

    //用来向主线程发送消息
    private BluetoothAdapter bluetoothAdapter;

    //用来连接端口的线程
    private AcceptThread mAcceptThread;
    private TransferThread mTransferThread;
    private ConnectThread mConnectThread;

    private boolean isTransferError = false;
    public static volatile BluetoothPipeLine instance = null;
    private BluetoothDevice mCurConnectDevice;


    private Handler mHd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BLUE_TOOTH_TOAST) {
                ToastUtil.showShort(WalktourApplication.getAppContext(), (String) msg.obj);
            }
        }
    };

    public static BluetoothPipeLine getInstance() {
        if (instance == null) {
            synchronized (BluetoothPipeLine.class) {
                if (instance == null) {
                    instance = new BluetoothPipeLine();
                }
            }
        }
        return instance;
    }


    public BluetoothPipeLine() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    /**
     * 获取当前连接的蓝牙手机
     *
     * @return
     */
    public BluetoothDevice getConnectDevice() {
        return mCurConnectDevice;
    }

    /**
     * 开启服务监听
     */
    public synchronized void waitForConnect() {
        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }

        setState(STATE_LISTEN);

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void close() {
        Log.w(TAG, "close");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        if (mTransferThread != null) {
            mTransferThread.cancel();
            mTransferThread = null;
        }

        setState(STATE_NONE);
    }

    private void setState(int state) {
        this.mState = state;
    }

    /**
     * 连接访问
     *
     * @param device
     */
    public synchronized void connectDevice(BluetoothDevice device) {
        Log.w(TAG, "connectDevice: ");
        // 如果有正在传输的则先关闭
        if (mState == STATE_CONNECTING) {
            if (mTransferThread != null) {
                mTransferThread.cancel();
                mTransferThread = null;
            }
        }

        //如果有正在连接的则先关闭
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        sendMessageToUi(BLUE_TOOTH_DIALOG, "正在与" + device.getName() + "连接");
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        //标志为正在连接
        setState(STATE_CONNECTING);
    }

    /**
     * 连接等待线程
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            //获取服务器监听端口
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            super.run();
            //监听端口
            BluetoothSocket socket = null;
            while (mState != STATE_TRANSFER) {
                try {
                    Log.w(TAG, "run: AcceptThread 阻塞调用，等待连接");
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "run: ActivityThread fail");
                    break;
                }
                //获取到连接Socket后则开始通信
                if (socket != null) {
                    synchronized (BluetoothPipeLine.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                //传输数据，服务器端调用
                                Log.w(TAG, "run: 服务器AcceptThread传输");
                                sendMessageToUi(BLUE_TOOTH_DIALOG, "正在与" + socket.getRemoteDevice().getName() + "连接");
                                dataTransfer(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_TRANSFER:
                                // 没有准备好或者终止连接
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket" + e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            Log.w(TAG, "close: activity Thread");
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "close: activity Thread fail");
            }
        }
    }

    private void showToast(String s) {
        Message message = new Message();
        message.what = BLUE_TOOTH_TOAST;
        message.obj = s;
        mHd.sendMessage(message);
    }

    private void sendMessageToUi(int what, String s) {
        if (mListener != null) {
            mListener.callback(what, s);
        }
    }

    /**
     * 开始连接通讯
     *
     * @param socket
     * @param remoteDevice 远程设备
     */
    private void dataTransfer(BluetoothSocket socket, final BluetoothDevice remoteDevice) {
        //标志状态为连接
        setState(STATE_TRANSFER);

        //关闭连接线程，这里只能连接一个远程设备
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // 启动管理连接线程和开启传输
        mTransferThread = new TransferThread(socket);
        mTransferThread.start();

        if (!isTransferError) {
            mCurConnectDevice = remoteDevice;
        }
        mHd.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isTransferError) {
                    sendMessageToUi(BLUE_TOOTH_SUCCESS, remoteDevice.getName());
                }
            }
        }, 300);
    }

    /**
     * 传输数据
     *
     * @param out
     */
    public synchronized void sendData(byte[] out) {
        if (mState != STATE_TRANSFER)
            return;
        TransferThread r = mTransferThread;
        r.write(out);
    }

    /**
     * 传输数据的线程
     */
    class TransferThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream out;
        private final InputStream in;

        public TransferThread(BluetoothSocket mBluetoothSocket) {
            socket = mBluetoothSocket;
            OutputStream mOutputStream = null;
            InputStream mInputStream = null;
            try {
                if (socket != null) {
                    mOutputStream = socket.getOutputStream();
                    mInputStream = socket.getInputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = mOutputStream;
            in = mInputStream;
            isTransferError = false;
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = in.read(buffer);
                    // uiHandler.obtainMessage(BLUE_TOOTH_READ, bytes, -1, buffer).sendToTarget();
                    String msg = new String(buffer, 0, bytes);
                    Log.d("czc", "接收蓝牙消息：" + msg);

                    sendMsgByBroadcast(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "run: Transform error" + e.toString());
                    BluetoothPipeLine.this.waitForConnect();
                    showToast("设备连接失败/传输关闭");
                    sendMsgByBroadcast("stop");
                    mCurConnectDevice = null;
                    isTransferError = true;
                    break;
                }
            }
        }

        /**
         * 写入数据传输
         *
         * @param buffer
         */
        public void write(byte[] buffer) {
            try {
                out.write(buffer);
                Log.d("czc", "发送蓝牙消息：" + new String(buffer));
                //uiHandler.obtainMessage(BLUE_TOOTH_WRAITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write " + e);
            }
        }

        public void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed" + e);
            }
        }
    }

    private synchronized void sendMsgByBroadcast(String msg) {
        Intent intent = new Intent();
        intent.setAction("ACTION_BLUE_TOOTH");
        intent.putExtra("EXTRA_BLUE_TOOTH_READ", msg);
        WalktourApplication.getAppContext().sendBroadcast(intent);
    }

    /**
     * 连接的线程
     */
    class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket mSocket = null;
            try {
                //建立通道
                mSocket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ConnectThread: fail");
                showToast("连接失败，请重新连接");
            }
            socket = mSocket;
        }

        @Override
        public void run() {
            super.run();
            //建立后取消扫描
            bluetoothAdapter.cancelDiscovery();

            try {
                Log.w(TAG, "run: connectThread 等待");
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e(TAG, "run: unable to close");
                }

                showToast("连接失败，请重新连接");
                BluetoothPipeLine.this.waitForConnect();
            }


            // 重置
            synchronized (BluetoothPipeLine.this) {
                mConnectThread = null;
            }

            mCurConnectDevice = device;

            //Socket已经连接上了，默认安全,客户端才会调用
            Log.w(TAG, "run: connectThread 连接上了,准备传输");
            dataTransfer(socket, device);
        }

        public void cancel() {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private OnBluetoothPipeLineListener mListener;

    public interface OnBluetoothPipeLineListener {
        void callback(int status, String message);
    }

    public void setOnBluetoothPipeLineListener(OnBluetoothPipeLineListener listener) {
        mListener = listener;
    }
}

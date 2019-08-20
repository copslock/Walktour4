package com.walktour.gui.highspeedrail;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class HsGpsDataManager {

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String NAME = "BluetoothData";

	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mAcceptThread;// 请求连接的监听进程
	private ConnectThread mConnectThread;// 连接一个设备的进程
	public ConnectedThread mConnectedThread;// 已经连接之后的管理进程
	private int mState;// 当前状态

	// 指明连接状态的常量
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	private Queue<Byte> queueBuffer = new LinkedList<Byte>();

	private byte[] packBuffer = new byte[11];

	public HsGpsDataManager(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
	}
	public void Send(byte[] buffer){
		if (mState==STATE_CONNECTED)
			mConnectedThread.write(buffer);
	}
	private synchronized void setState(int state) {
		mState = state;
		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(GService.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	public synchronized int getState() {
		return mState;
	}

	public synchronized void start() {

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();
		}
		setState(STATE_LISTEN);
	}

	public synchronized void connect(BluetoothDevice device) {

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel the accept thread because we only want to connect to one device
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(GService.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString("device_name", device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	public synchronized void stop() {

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		setState(STATE_NONE);
	}

	private void connectionFailed() {
		setState(STATE_LISTEN);

		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(GService.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString("toast", "未能连接设备");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	private void connectionLost() {
		setState(STATE_LISTEN);
		Message msg = mHandler.obtainMessage(GService.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString("toast", "蓝牙设备失去连接");
		msg.setData(bundle);
		mHandler.sendMessage(msg);
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;
			// Create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
			} 
			catch (IOException e) {}
			mmServerSocket = tmp;
		}

		public void run() {
			setName("AcceptThread");
			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (HsGpsDataManager.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:// Situation normal. Start the connected thread.							
							connected(socket, socket.getRemoteDevice());
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate new socket.
							try {
								socket.close();
							} 
							catch (IOException e) {}
							break;
						}
					}
				}
			}

		}

		public void cancel() {

			try {
				mmServerSocket.close();
			} 
			catch (IOException e) {}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);// Get a BluetoothSocket for a connection with the given BluetoothDevice
			} 
			catch (IOException e) {}
			mmSocket = tmp;
		}

		public void run() {

			setName("ConnectThread");
			mAdapter.cancelDiscovery();// Always cancel discovery because it will slow down a connection

			// Make a connection to the BluetoothSocket
			try {				
				mmSocket.connect();// This is a blocking call and will only return on a successful connection or an exception
			} 
			catch (IOException e) {
				connectionFailed();				
				try {
					mmSocket.close();
				} catch (IOException e2) {}

				HsGpsDataManager.this.start();// 引用来说明要调用的是外部类的方法 run
				return;
			}
			
			synchronized (HsGpsDataManager.this) {// Reset the ConnectThread because we're done
				mConnectThread = null;
			}			
			connected(mmSocket, mmDevice);// Start the connected thread
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {

			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {

			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}
		
		private float [] fData=new float[32];
		private String strDate,strTime;
		public void run() {
			byte[] tempInputBuffer = new byte[1024];
			int acceptedLen = 0;
			byte sHead;
			// Keep listening to the InputStream while connected
			long lLastTime = System.currentTimeMillis(); // 获取开始时间
			while (true) {

				try {
					// 每次对inputBuffer做覆盖处理
					Thread.sleep(500);
					acceptedLen = mmInStream.read(tempInputBuffer);
					Log.d("BTL",""+acceptedLen);
					for (int i = 0; i < acceptedLen; i++) queueBuffer.add(tempInputBuffer[i]);// 从缓冲区读取到的数据，都存到队列里
					

					while (queueBuffer.size() >= 11) {						
						if ((queueBuffer.poll()) != 0x55) continue;// peek()返回对首但不删除 poll 移除并返回
							sHead = queueBuffer.poll();
							for (int j = 0; j < 9; j++) packBuffer[j] = queueBuffer.poll();
							switch (sHead) {//
								case 0x50:
									int ms = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff));
									strDate = String.format("20%02d-%02d-%02d",packBuffer[0],packBuffer[1],packBuffer[2]);
									strTime = String.format(" %02d:%02d:%02d.%03d",packBuffer[3],packBuffer[4],packBuffer[5],ms);
									RecordData(sHead,strDate+strTime);
									break;
								case 0x51:
									fData[0] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 16;
									fData[1] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 16;
									fData[2] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff)) / 32768.0f * 16;
									fData[16] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
									RecordData(sHead, String.format("% 10.2f", fData[0])+ String.format("% 10.2f", fData[1])+ String.format("% 10.2f", fData[2])+" ");
									break;
								case 0x52:
									fData[3] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 2000;
									fData[4] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 2000;
									fData[5] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff)) / 32768.0f * 2000;
									fData[16] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
									RecordData(sHead, String.format("% 10.2f", fData[3])+ String.format("% 10.2f", fData[4])+ String.format("% 10.2f", fData[5])+" ");
									break;
								case 0x53:
									fData[6] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f * 180;
									fData[7] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) / 32768.0f * 180;
									fData[8] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff)) / 32768.0f * 180;
									fData[16] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
									RecordData(sHead, String.format("% 10.2f", fData[6])+ String.format("% 10.2f", fData[7])+ String.format("% 10.2f", fData[8]));
									break;
								case 0x54://磁场
									fData[9] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff));
									fData[10] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff));
									fData[11] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff));
									fData[16] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff)) / 100.0f;
									RecordData(sHead, String.format("% 10.2f", fData[9])+ String.format("% 10.2f", fData[10])+ String.format("% 10.2f", fData[11]));
									break;
								case 0x55://端口
									fData[12] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff));
									fData[13] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff));
									fData[14] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff));
									fData[15] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff));
									RecordData(sHead, String.format("% 7.0f", fData[12])+ String.format("% 7.0f", fData[13])+ String.format("% 7.0f", fData[14])+ String.format("% 7.0f", fData[15]));
									break;
								case 0x56://气压、高度
									fData[17] = ((((long) packBuffer[3]) << 24)&0xff000000) |((((long) packBuffer[2]) << 16)&0xff0000) |((((long) packBuffer[1]) << 8)&0xff00) | ((((long) packBuffer[0])&0xff));
									fData[18] = (((((long) packBuffer[7]) << 24)&0xff000000) |((((long) packBuffer[6]) << 16)&0xff0000) |((((long) packBuffer[5]) << 8)&0xff00) | ((((long) packBuffer[4])&0xff)))/100.0f;

									RecordData(sHead, String.format("% 10.2f", fData[17])+ String.format("% 10.2f", fData[18]));;
									break;
								case 0x57://经纬度
									long Longitude = ((((long) packBuffer[3]) << 24)&0xff000000) |((((long) packBuffer[2]) << 16)&0xff0000) |((((long) packBuffer[1]) << 8)&0xff00) | ((((long) packBuffer[0])&0xff));
									fData[19]=(float) (Longitude / 10000000 + ((float)(Longitude % 10000000) / 100000.0 / 60.0));
									long Latitude = (((((long) packBuffer[7]) << 24)&0xff000000) |((((long) packBuffer[6]) << 16)&0xff0000) |((((long) packBuffer[5]) << 8)&0xff00) | ((((long) packBuffer[4])&0xff)));
									fData[20]=(float) (Latitude / 10000000 + ((float)(Latitude % 10000000) / 100000.0 / 60.0));
									RecordData(sHead, String.format("% 14.6f", fData[19])+ String.format("% 14.6f", fData[20]));;
									break;
								case 0x58://海拔、航向、地速
									fData[21] = (float)((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) /10;
									fData[22]=(float)((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff)) /100;
									fData[23]=(float)(((((long) packBuffer[7]) << 24)&0xff000000) |((((long) packBuffer[6]) << 16)&0xff0000) |((((long) packBuffer[5]) << 8)&0xff00) | ((((long) packBuffer[4])&0xff)))/1000;
									RecordData(sHead, String.format("% 10.2f", fData[21])+ String.format("% 10.2f", fData[22])+ String.format("% 10.2f", fData[23]));;
									break;
								case 0x59://四元数
									fData[24] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff)) / 32768.0f;
									fData[25] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff))/32768.0f;
									fData[26] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff))/32768.0f;
									fData[27] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff))/32768.0f;
									RecordData(sHead, String.format("% 7.3f", fData[24])+ String.format("% 7.3f", fData[25])+ String.format("% 7.3f", fData[26])+ String.format("% 7.3f", fData[27]));
									break;
								case 0x5a://卫星数
									fData[28] = ((((short) packBuffer[1]) << 8) | ((short) packBuffer[0] & 0xff));
									fData[29] = ((((short) packBuffer[3]) << 8) | ((short) packBuffer[2] & 0xff))/100.0f;
									fData[30] = ((((short) packBuffer[5]) << 8) | ((short) packBuffer[4] & 0xff))/100.0f;
									fData[31] = ((((short) packBuffer[7]) << 8) | ((short) packBuffer[6] & 0xff))/100.0f;
									RecordData(sHead, String.format("% 5.0f", fData[28])+ String.format("% 7.1f", fData[29])+ String.format("% 7.1f", fData[30])+ String.format("% 7.1f", fData[31]));
									break;
							}//switch
					}//while (queueBuffer.size() >= 11)

					long lTimeNow = System.currentTimeMillis(); // 获取开始时间
					if (lTimeNow - lLastTime > 500) {
						lLastTime = lTimeNow;
						Message msg = mHandler.obtainMessage(GService.MESSAGE_READ);
						Bundle bundle = new Bundle();
						bundle.putFloatArray("Data", fData);
						bundle.putString("Date", strDate);
						bundle.putString("Time", strTime);
						msg.setData(bundle);
						mHandler.sendMessage(msg);
					}

				} catch (IOException e) {
					connectionLost();
					break;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
//				mHandler.obtainMessage(DataMonitor.MESSAGE_WRITE, -1, -1,buffer).sendToTarget();// Share the sent message back to the UI Activity
			} catch (IOException e) {}
		}

		public void cancel() {
			try {
				mmSocket.close();
				mmInStream.close();
				mmOutStream.close();
			} catch (IOException e) {}
		}
	}
	MyFile myFile;
	
	private short IDSave=0;
	private short IDNow;
	private int SaveState=-1;
	private int sDataSave=0;

	public void RecordData(byte ID,String str) throws IOException
	{
		boolean Repeat=false;
		short sData=(short) (0x01<<(ID&0x0f));
		if (((IDNow&sData)==sData)&&(sData<sDataSave)) {IDSave=IDNow;	IDNow=sData;Repeat=true;}		
		else IDNow|=sData;
		sDataSave = sData;
		switch (SaveState) {
		case 0:
			if (myFile!=null){
				myFile.Close();
			}
			SaveState = -1;
			break;
		case 1:
			myFile=new MyFile("/mnt/sdcard/Record.txt");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			String s="开始时间："+formatter.format(curDate)+"\r\n" ;
			if ((IDSave&0x02)>0) s+= "  加速度X： 加速度Y： 加速度Z：" ;
			if ((IDSave&0x04)>0) s+="  角速度X： 角速度Y： 角速度Z：";
			if ((IDSave&0x08)>0) s+="    角度X：   角度Y：   角度Z：";
			if ((IDSave&0x10)>0) s+="   磁场X：   磁场Y：   磁场Z：";
			if ((IDSave&0x20)>0) s+="端口0：端口1：端口2：端口3：";
			if ((IDSave&0x40)>0) s+="    气压：    高度：";
			if ((IDSave&0x80)>0) s+="        经度：        纬度：";
			if ((IDSave&0x100)>0) s+="    海拔：    航向：    地速：";
			if ((IDSave&0x200)>0) s+="   q0：   q1：   q2：   q3：";
			if ((IDSave&0x400)>0) s+="星数：PDOP： HDOP： VDOP：";
			myFile.Write(s+"\r\n");
			if (Repeat)  {myFile.Write(str);SaveState = 2;}
			break;
		case 2:
			if (Repeat) myFile.Write("  \r\n");
			myFile.Write(str);
			break;
		case -1:
			break;
		default:
			break;
		} 		
	}
	public void setRecord(boolean record)
	{
		if (record) SaveState = 1;
		else SaveState = 0;
		
	}
}
class MyFile{
	FileOutputStream fout;
	public MyFile(String fileName) throws FileNotFoundException {
		fout = new FileOutputStream(fileName,false);
	}
	public void Write( String str) throws IOException {
		byte[] bytes = str.getBytes();
		fout.write(bytes);
	}
	public void Close() throws IOException{
		fout.close();
		fout.flush();
	}
}
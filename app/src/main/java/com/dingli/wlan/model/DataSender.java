package com.dingli.wlan.model;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/*
 * wlan信令发送，本地socket
*/
public class DataSender {
	private Socket mSocket;
	private static final int SOCKET_CONNECT_TIMEOUT = 10000;
	private static final int SOCKET_READ_TIMEOUT = 60000;
	// private InputStream mIn;
	private OutputStream mOut;

	public boolean initSocket(int port) {

		try {
			// Socket s = new Socket(mailServer, 465); //25
			SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", port);
			mSocket = new Socket();
			mSocket.connect(socketAddress, SOCKET_CONNECT_TIMEOUT);
			mSocket.setSoTimeout(SOCKET_READ_TIMEOUT);
			// mIn = new BufferedInputStream(mSocket.getInputStream(), 1024);
			mOut = new BufferedOutputStream(mSocket.getOutputStream(), 1024);
		} catch (IOException e) {
			Log.d("data", "init socket error = " + e.getMessage());
			return false;
		}
		Log.d("data", "init socket succ");
		return true;
	}

	/*
	 * 
	 * send data
	 */
	public boolean sendData(byte[] buffer, int len) {
		if (mOut == null)
			return false;
		try {
			mOut.write(buffer, 0, len);
			mOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
			mOut = null;
			return false;
		}
		return true;
	}

	public void close() {
		try {
			mSocket.close();
			if (mOut != null)
				mOut.close();
			// if (mIn != null)
			// mIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

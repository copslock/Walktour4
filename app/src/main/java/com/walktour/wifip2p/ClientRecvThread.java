package com.walktour.wifip2p;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.walktour.Utils.NArchive;
import com.walktour.Utils.NBits;
import com.walktour.gui.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientRecvThread extends Thread{
	
	private boolean needStop=false;
	private String server;
	private int port;
	private Socket socket;
	private String tag = "p2p";
//	private boolean hasLogin=false;
	private Context mContext;
	public ClientRecvThread(String server,int port,Context context)
	{
		this.server = server;
		this.port = port;
		this.mContext = context;
	}
	public void stopIt() {
		needStop=true;
		try{
		socket.close();
		}catch(IOException e) {
			
		}
	}
	public void sendFile(String filepath,String filename) {
		try {
			if (SocketProcess.sendFile(socket.getOutputStream(),filepath,filename))
			     Toast.makeText(mContext, mContext.getString(R.string.send_file_end), Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//client Login 
//		public void login(String deviceName) {
//			Log.d(tag,"login server ");
//			byte[] buffer = new byte[1024];
//			NArchive ar = NArchive.createFromBuffer(buffer,false);
//			ar.writeInt(100);
//			ar.writeInt(deviceName.length());
//			ar.writeBytes(deviceName.getBytes(), deviceName.length());
//			int realLen = ar.getOffset();
//			try {
//				//send filename;
//				OutputStream outstream = socket.getOutputStream();
//				outstream.write(buffer,0,realLen);
//				outstream.flush();
//				hasLogin=true;
//			}catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	  private boolean readEnoughData (InputStream in,byte[]result,int len) {
//			int offset = 0;
//			while(true) {
//				try {
//					int readed = in.read(result, offset,len-offset);
//					if (readed == -1)
//						break;
//					offset += readed;
//					if (offset == len) {
//					    return true;				
//					}
//				}
//				catch(IOException e) {
//					Log.e("p2p"," read error " + e.getMessage());
//					return false;
//				}
//			}
//			return false;
//		}
	@SuppressLint("NewApi")
	@Override
	public void run() {
		 
		socket = new Socket();
         //int port = 8988;
         int SOCKET_TIMEOUT = 5000;
        
         String fileSavePath = Environment.getExternalStorageDirectory().getPath() + "/Walktour/task/";
         String filename=""; 
         byte[] buffer = new byte[1024];
         try {
       	  try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
              Log.d("p2p", "Opening client socket - " + server +  " "+port);
             socket.bind(null);
             
             String deviceName = DeviceListFragment.getSelfDevice().deviceName; //local
             socket.connect((new InetSocketAddress(server, port)), SOCKET_TIMEOUT);
             SocketProcess.login(socket.getOutputStream(),deviceName);
     	     InputStream inputstream = socket.getInputStream();
     	     while(!needStop) {
				 //Log.d(tag,"run........");
            	 SocketProcess.readEnoughData(inputstream,buffer,4);
			     int id = NBits.getInt(buffer, 0);
		        if (id == 101) {
		        	 //get file name  ,client send file to server
		        	SocketProcess.readEnoughData(inputstream,buffer,4);
		        	 int len = NBits.getInt(buffer, 0);
		        	 SocketProcess.readEnoughData(inputstream,buffer,len);
		        	 filename = new String(buffer,0,len);
		        	 Log.d(tag,"recev filename = " + filename); 
		        	 
		         }else if (id == 102) {
		        	 Log.d(tag,"recev filename content = ");
		        	 //get file content,,client send file to server
		        	 SocketProcess.readEnoughData(inputstream,buffer,4);
		        	 int len = NBits.getInt(buffer, 0);
		        	 byte [] content = new byte[len];
		        	 SocketProcess.readEnoughData(inputstream,content,len);
		        	 NArchive ar = NArchive.createFile(fileSavePath+filename, false);
		        	 if (ar.open()) {
		        	  ar.writeBytes(content, len);
		        	  ar.close();
		        	 }
		     		Intent intent = new Intent(WiFiDirectActivity.RecvTemplateComplete);
		     		intent.putExtra("file", fileSavePath+filename);
		        	mContext.sendBroadcast(intent);
		         }
             }
           }
             catch (IOException e) {
                 Log.e("p2p", "client Recv Thread Exception = " + e.getMessage());
             } 
         }
	
}

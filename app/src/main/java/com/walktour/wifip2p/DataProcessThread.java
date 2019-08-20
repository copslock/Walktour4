package com.walktour.wifip2p;

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
import java.net.Socket;



/*
 * server socket with client
 */
public class DataProcessThread  extends Thread{

	private Socket client;
	private String tag = "p2p";
	private boolean needStop=false;
	private String fileSavePath = Environment.getExternalStorageDirectory().getPath() + "/Walktour/task/";
//	private boolean hasLogin=false;
	private String deviceName="";
	private boolean isAlive=false;
	private Context mContext;
	
	public Socket getSocket() {
		return client;
	}
	public DataProcessThread(Socket client,Context context)
	{
		this.client = client;
		needStop=false;
		this.mContext = context;
	}
	public String getDevice() {
		return this.deviceName;
	}
	public void stopIt() {
		needStop=true;
		try{
			client.close();
			}catch(IOException e) {
				
			}
	}
	public boolean state() {
		return isAlive;
	}
	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		 InputStream inputstream;
		 String filename="";
		try {
			inputstream = client.getInputStream();
			isAlive=true;
			while(!needStop) {
				 //Log.d(tag,"run........");
				 SocketProcess.readEnoughData(inputstream,buffer,4);
			     int id = NBits.getInt(buffer, 0);
		         if (id == 100)  { //login
		      	   //login success
		        	 SocketProcess.readEnoughData(inputstream,buffer,4);
		        	 int len = NBits.getInt(buffer, 0);
		        	 SocketProcess.readEnoughData(inputstream,buffer,len);
		        	 deviceName = new String(buffer,0,len);
		        	 Log.d(tag,"get peer device name = " + new String(buffer,0,len) +  " peer ip = " + client.getInetAddress().getHostAddress());
//		        	 hasLogin=true;
		        	 //sendFile("/sdcard/Walktour/task/","Iperf.xml");
		         }else if (id == 101) {
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
		
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("p2p","thread has exit");
		}
       isAlive=false;
	}
	public void sendFile(String filepath,String filename) {
		try {
			if (SocketProcess.sendFile(client.getOutputStream(),filepath,filename))
			     Toast.makeText(mContext, mContext.getString(R.string.send_file_end), Toast.LENGTH_LONG).show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	//client Login 
//	public void login(String deviceName) {
//		Log.d(tag,"login server ");
//		byte[] buffer = new byte[1024];
//		NArchive ar = NArchive.createFromBuffer(buffer,false);
//		ar.writeInt(100);
//		ar.writeInt(deviceName.length());
//		ar.writeBytes(deviceName.getBytes(), deviceName.length());
//		int realLen = ar.getOffset();
//		try {
//			//send filename;
//			OutputStream outstream = client.getOutputStream();
//			outstream.write(buffer,0,realLen);
//			outstream.flush();
//			hasLogin=true;
//		}catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	//owner send file to client
//	public void sendFile(String filepath,String filename) {
//		if (hasLogin) {
//			Log.d("p2p","begin to send file ....");
//			byte[] buffer = new byte[1024];
//			NArchive ar = NArchive.createFromBuffer(buffer,false);
//			ar.writeInt(101);
//			ar.writeInt(filename.length());
//			ar.writeBytes(filename.getBytes(), filename.length());
//			int realLen = ar.getOffset();
//			try {
//				//send filename;
//				OutputStream outstream = client.getOutputStream();
//				outstream.write(buffer,0,realLen);
//				outstream.flush();
//				Log.d("p2p","send filename ,send bytes = " + realLen);
//				//send file content;
//				
//				 NArchive arFile = NArchive.createFile(filepath+filename, true);
//	        	 if (arFile.open()) {
//	        		 File f = new File(filepath+filename);
//	        		 int  fileLen = (int)f.length();
//	        		
//	        	  //arFile.getFile().seek(0, NFile.SEEK_END);
//	              //int fileLen = arFile.getFile().tell();
//	              //arFile.getFile().seek(0, NFile.SEEK_SET);
//	      		  Log.d("p2p","has open file  ...." + filepath+filename + " filelen = " + fileLen);
//	  	          
//	              byte[] content = new byte[fileLen];
//	              arFile.readBytes(content, fileLen);
//	              NArchive ar1 = NArchive.createFromBuffer(buffer,false);
//	  			  ar1.writeInt(102);
//	  			  ar1.writeInt(fileLen);
//	  			  realLen = ar1.getOffset();
//	  			  outstream.write(buffer, 0, realLen);
//	        	  outstream.write(content, 0, fileLen);
//	        	  outstream.flush();
//	        	  arFile.close();
//	        	  
//	        	 }
//	        	 
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}
//	}
	
//	private boolean readEnoughData (InputStream in,byte[]result,int len) {
//		int offset = 0;
//		while(true) {
//			try {
//				int readed = in.read(result, offset,len-offset);
//				if (readed == -1)
//					break;
//				offset += readed;
//				if (offset == len) {
//				    return true;				
//				}
//			}
//			catch(IOException e) {
//				Log.e("p2p"," read error " + e.getMessage());
//				return false;
//			}
//		}
//		return false;
//	}
}

package com.dingli.wlan.model;

import android.util.Log;

import com.walktour.Utils.BitsLittleEndian;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * 
 * 
 */
public class WlanDataReceiver  implements Runnable  {

	   private WlanDataObserver mObserver;
	   private ServerSocket serverSocket = null;  
	   private int mPort=45536;
	   private String tag = "wlanreceive";
	   public  WlanDataReceiver(WlanDataObserver observer,int port) {
		   mObserver = observer;
		   mPort = port;
	   }
	   
	   public void run()  
	    {  
	        try  
	        {  
	            //创建ServerSocket  
	            serverSocket = new ServerSocket(mPort);  
	            while (true)  
	            {  
	                //接受客户端请求  
	                Socket client = serverSocket.accept();  
	                System.out.println("accept");  
	                Log.d(tag,"accept");
	                try  
	                {  
	                    //接收客户端消息
	                	InputStream in = client.getInputStream();
	                	byte[] buffer  =new byte[10240];
	                	ByteArrayOutputStream output = new ByteArrayOutputStream(10240);
	                	while(true) {
	                		if (!readEnoughData(in,buffer,0,4))
	                			break;
	                		int dataLen = BitsLittleEndian.getInt(buffer, 0);
	                		Log.d(tag,"datalen="+dataLen);
	                		if (!readEnoughData(in,buffer,4,dataLen-4)) {
	                			break;
	                		}
	                		if (mObserver!=null) {
	                			output.write(buffer, 0, dataLen);
	                			mObserver.onGetData(output.toByteArray(),dataLen);
	                			output.reset();
	                		}
	                	}
	                	Log.d("data","recv data exception");
	                	in.close();
	                	output.close();
	                }  
	                catch (Exception e)  
	                {  
	                    System.out.println(e.getMessage());  
	                    Log.d(tag,e.getMessage());
	                    e.printStackTrace();  
	                }  
	                finally  
	                {  
	                    //关闭  
	                    client.close();  
	                    System.out.println("close");  
	                    Log.d(tag,"close");
	                }  
	            }  
	        }  
	        catch (Exception e)  
	        {  
	            System.out.println(e.getMessage());  
	        }  
	    }
	   /*
	    * 
	    * offset is buffer offset
	    */
	   private boolean readEnoughData (InputStream in,byte[]result,int offset,int len) {
		   		int totalen=0;
				while(true) {
				try {
					int readed = in.read(result,offset,len);
					if (readed == -1)
						break;
					offset += readed;
					totalen+=readed;
					if (totalen == len) {
					    return true;				
					}
				}
				catch(IOException e) {
					Log.e("wlan"," read error " + e.getMessage());
					return false;
				}
			}
			return false;
		}
	   
	  public  void close() {
		   if (serverSocket!=null) {
			   try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	   }
}

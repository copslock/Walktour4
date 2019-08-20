package com.walktour.wifip2p;

import android.util.Log;

import com.walktour.Utils.NArchive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SocketProcess {
	  public static String tag="p2p";
	  public static boolean readEnoughData (InputStream in,byte[]result,int len) {
			int offset = 0;
			while(true) {
				try {
					int readed = in.read(result, offset,len-offset);
					if (readed == -1)
						break;
					offset += readed;
					if (offset == len) {
					    return true;				
					}
				}
				catch(IOException e) {
					Log.e("p2p"," read error " + e.getMessage());
					return false;
				}
			}
			return false;
		}
		public static boolean sendFile(OutputStream outstream,String filepath,String filename) {
			  Log.d("p2p","begin to send file ....");
				byte[] buffer = new byte[1024];
				NArchive ar = NArchive.createFromBuffer(buffer,false);
				ar.writeInt(101);
				ar.writeInt(filename.length());
				ar.writeBytes(filename.getBytes(), filename.length());
				int realLen = ar.getOffset();
				try {
					//send filename;
					//OutputStream outstream = client.getOutputStream();
					outstream.write(buffer,0,realLen);
					outstream.flush();
					Log.d("p2p","send filename ,send bytes = " + realLen);
					//send file content;
					
					 NArchive arFile = NArchive.createFile(filepath+filename, true);
		        	 if (arFile.open()) {
		        		 File f = new File(filepath+filename);
		        		 int  fileLen = (int)f.length();
		        		
		        	  //arFile.getFile().seek(0, NFile.SEEK_END);
		              //int fileLen = arFile.getFile().tell();
		              //arFile.getFile().seek(0, NFile.SEEK_SET);
		      		  Log.d("p2p","has open file  ...." + filepath+filename + " filelen = " + fileLen);
		  	          
		              byte[] content = new byte[fileLen];
		              arFile.readBytes(content, fileLen);
		              NArchive ar1 = NArchive.createFromBuffer(buffer,false);
		  			  ar1.writeInt(102);
		  			  ar1.writeInt(fileLen);
		  			  realLen = ar1.getOffset();
		  			  outstream.write(buffer, 0, realLen);
		        	  outstream.write(content, 0, fileLen);
		        	  outstream.flush();
		        	  arFile.close();
		        	  return true;
		        	 }
		        	 
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(tag," " + e.getMessage());
				}
				return false;
				
		}

		public static void login(OutputStream outstream,String deviceName) {
			Log.d(tag,"login server ");
			byte[] buffer = new byte[1024];
			NArchive ar = NArchive.createFromBuffer(buffer,false);
			ar.writeInt(100);
			ar.writeInt(deviceName.length());
			ar.writeBytes(deviceName.getBytes(), deviceName.length());
			int realLen = ar.getOffset();
			try {
				//send filename;
				//OutputStream outstream = socket.getOutputStream();
				outstream.write(buffer,0,realLen);
				outstream.flush();
				//hasLogin=true;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

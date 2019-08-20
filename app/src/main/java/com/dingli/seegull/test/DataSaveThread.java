package com.dingli.seegull.test;

import android.os.Environment;
import android.util.Log;

import com.dingli.seegull.SeeGullFlags.ScanTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataSaveThread extends Thread {
	private static final String TAG = "SocketDataSaveThread";	
	private List<byte[]> mDataBufList= new ArrayList<byte[]>();
	private boolean mBreakFlag = false;		
	private FileOutputStream mFileOutputStream = null;
	private long mClickOfPacket = System.currentTimeMillis();
	private boolean mSaveForReplay = false;
	
	public boolean addData(byte[] buf) {
		synchronized(mDataBufList) {
			mDataBufList.add(buf);
			mDataBufList.notify();
		}
		return true;
	}
	
	private void checkOpenFile(byte[] buf) throws IOException {
		if (mFileOutputStream == null || System.currentTimeMillis()- mClickOfPacket > 2000) {
			String head = "TestData";
			if (buf[3] == 1) {
				int type = 0x00FF & buf[11];
				if (type == ScanTypes.eScanType_RssiChannel) {
					head = "RssiData";
				} else if (type == ScanTypes.eScanType_TopNPilot) {
					head = "TopNPilotData";
				} else if (type == ScanTypes.eScanType_ColorCode) {
					head = "ColorCodeData";
				} else if (type == ScanTypes.eScanType_eTopNSignal) {
					head = "ETopNSignalData";
				} else if (type == ScanTypes.eScanType_EnhancedPowerScan) {
					head = "EnhancedPowerData";
				}
			}
			
			Calendar calendar = Calendar.getInstance();		
			String rootname = String.format("%s_%d%02d%02d_%02d%02d%02d.bin", head,
					calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH)+1, 
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.HOUR_OF_DAY), 
					calendar.get(Calendar.MINUTE), 
					calendar.get(Calendar.SECOND));
			String dirname = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Scanner";
			File dir = new File(dirname);  
		    if (!dir.exists() || !dir.isDirectory())   
		    	dir.mkdirs();  
		    File file = new File(dir, rootname);
		    if (mFileOutputStream != null)
		    	mFileOutputStream.close();
		    mFileOutputStream = new FileOutputStream(file);
		}
		mClickOfPacket = System.currentTimeMillis();
	}
	
	public void run() { 
		while (!mBreakFlag) {	  
			byte[] buf = null;
        	synchronized(mDataBufList) {
        		if (mDataBufList.size() <= 0) {
        			try {                			
        				mDataBufList.wait();
					} catch (InterruptedException e) {
					}
    			}            		
        		if (mDataBufList.size() > 0) {
        			buf = mDataBufList.get(0);
        			mDataBufList.remove(0);						
        		}            		
       		}
        	if (mBreakFlag)
        		break;
        	if (buf != null) {
        		try {
        			checkOpenFile(buf);
            		if (mFileOutputStream != null) {
            			//if need for replay, just add length and time 
            			if (mSaveForReplay) {
            				long timeclick = System.currentTimeMillis();
            				byte[] timebytes = ByteBuffer.allocate(8).putLong(timeclick).array();
            				mFileOutputStream.write(timebytes);	
            			}            			
            			
            			mFileOutputStream.write(buf);	        	
            			mFileOutputStream.flush();	
            		}
            	} catch (FileNotFoundException e1) {
        			e1.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();	
        		}
        		
        		buf = null;
        	}
		} 		
		if (mFileOutputStream != null) {
			try {
				mFileOutputStream.close();
			} catch (Exception e) {					
			}				
		}
		Log.i(TAG, "run() finish.");            
    }	
	public void cancel() {  
    	this.interrupt();
    	mBreakFlag = true;
    }	
}

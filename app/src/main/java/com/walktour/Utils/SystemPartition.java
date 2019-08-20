package com.walktour.Utils;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/**
 * 系统分区操作
 * @author Jone
 *
 */
public class SystemPartition {
	private static final String TAG = "SystemMount";
	private static String TMP_PATH = "/dev/block/mtdblock3";
	//mount -o remount,rw -t yaffs2 /dev/block/mtdblock3 /system 

	private static String mMountPiont = null;
	private static boolean mWriteable = false;
	
	private SystemPartition() {
		LogUtil.i(TAG, "new SystemMount()");
	}
	
	private static class SystemPartitionHolder {
		private static SystemPartition instance = new SystemPartition();
	}
	
	public SystemPartition getInstance() {
		return SystemPartitionHolder.instance;
	}

	@SuppressWarnings("deprecation")
	public static String getSystemMountPiont() {
		DataInputStream dis = null;
		if (mMountPiont == null) { 
			try {
				execRootCmdSilent("mount > " + TMP_PATH);
//				Runtime.getRuntime().exec("mount > " + TMP_PATH);
				
				dis = new DataInputStream(new FileInputStream(TMP_PATH));
				
				String line = null;
				int index = -1;
				while ( (line = dis.readLine()) != null ) {
					index = line.indexOf(" /system ");
					if (index > 0) {
						mMountPiont = line.substring(0, index);
						if (line.indexOf(" rw") > 0) {
							mWriteable = true;
							LogUtil.i(TAG, "/system is writeable !");
						} else {
							mWriteable = false;
							LogUtil.i(TAG, "/system is readonly !");
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dis != null) {
					try {
						dis.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					dis = null;
				}
				
				File f = new File(TMP_PATH);
				if (f.exists()) {
					f.delete();
				}
			}
		}
		
		if (mMountPiont != null) {
			LogUtil.i(TAG, "/system mount piont: " + mMountPiont);
		} else {
			LogUtil.i(TAG, "get /system mount piont failed !!!");
		}
		
		return mMountPiont;
	}
	
	public static boolean isWriteable() {
		mMountPiont = null;
		getSystemMountPiont();
		return mWriteable;
	}
	
	public static void remountSystem(boolean writeable) {
		LogUtil.v(TAG, "writeable=" + writeable);
		String cmd = null;
		getSystemMountPiont();
		if (mMountPiont != null) {
			if (writeable) {
				cmd = "mount -o remount,rw " + mMountPiont + " /system";
			} else {
				cmd = "mount -o remount,ro " + mMountPiont + " /system";
			}
			execRootCmdSilent(cmd);
			isWriteable();
		}
	}
	
	// 执行命令但不关注结果输出
			public static int execRootCmdSilent(String cmd) {
				int result = -1;
				DataOutputStream dos = null;
				
				try {
					Process p = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
					dos = new DataOutputStream(p.getOutputStream());
					
					LogUtil.i(TAG, cmd);
					dos.writeBytes(cmd + "\n");
					dos.flush();
					dos.writeBytes("exit\n");
					dos.flush();
					p.waitFor();
					result = p.exitValue();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (dos != null) {
						try {
							dos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return result;
			}
}

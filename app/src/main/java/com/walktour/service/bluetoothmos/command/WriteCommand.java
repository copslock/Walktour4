package com.walktour.service.bluetoothmos.command;

import com.walktour.base.util.LogUtil;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 写数据到mos头的命令类
 * 
 * @author jianchao.wang
 *
 */
public class WriteCommand extends BaseCommand {
	/** 要发送的文件名称 */
	private final File mSendFile;
	/** 要发送的文件大小 */
	private float mFileMaxSize = 0;
	/** 已发送的文件大小 */
	private float mFileCurrSize = 0;
	/** 写文件类型 */
	private FileType mFileType;
	/** 测速线程 */
	private SpeedTestThread mSpeedTestThread = null;

	public WriteCommand(BluetoothMOSDevice device, File sendFile, FileType fileType) {
		super(CommandType.write_data, device);
		this.mSendFile = sendFile;
		this.mFileMaxSize = sendFile.length();
		this.mFileType = fileType;
		super.mTimeout = 2 * 60 * 1000;
	}

	@Override
	protected void dealRespData(byte[] data) {
		// 无需实现
	}

	@Override
	protected void dealRespACK(byte[] ack) {
		super.resolveRespACK(ack);
		if (!super.isACKOK || super.mACKType.equals(ACK_TYPE_DATA))
			this.finish();
		else {
			if (this.mSpeedTestThread == null)
				this.mSpeedTestThread = new SpeedTestThread();
			this.mSpeedTestThread.start();
			this.sendFileData();
		}
	}

	/**
	 * 发送文件数据
	 */
	private void sendFileData() {
		LogUtil.d(mTag, "send file:" + this.mSendFile.getName() + " start\nlen:" + this.mFileMaxSize);
		FileInputStream is = null;
		try {
			is = new FileInputStream(mSendFile);
			byte[] temp = new byte[512];
			int read;
			while ((read = is.read(temp)) > 0) {
				if (read == temp.length) {
					super.sendData(temp);
				} else {
					byte[] newTemp = new byte[read];
					System.arraycopy(temp, 0, newTemp, 0, read);
					super.sendData(newTemp);
				}
				this.mFileCurrSize += read;
				if (this.mFileCurrSize >= this.mFileMaxSize) {
					this.finish();
				}
			}
		} catch (Exception e) {
			LogUtil.d(mTag, e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LogUtil.e(mTag, e.getMessage());
				}
			}
		}
	}

	@Override
	public void runCommand() {
		byte[] fileID = this.mFileType.getName().getBytes();
		byte[] fileSize = hexIntToBytes((int) this.mFileMaxSize, 4);
		byte[] msg = new byte[fileID.length + fileSize.length + 1];
		msg[0] = hexIntToBytes(this.mFileType.getSampleRate(), 1)[0];
		System.arraycopy(fileSize, 0, msg, 1, fileSize.length);
		System.arraycopy(fileID, 0, msg, 1 + fileSize.length, fileID.length);
		byte[] cmd = super.createRequestCmd(msg);
		super.sendRequestCmd(cmd);
	}

	@Override
	protected void init() {
		super.init();
		this.mFileCurrSize = 0;
	}

	/**
	 * 传输速度测速线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class SpeedTestThread extends Thread {
		/** 计算速度的间隔时间（毫秒） */
		private final static long COUNT_TIME_INTERVAL = 500;
		/** 开始计算时间 */
		private long mStartTime = 0;
		/** 最大速度 */
		private float mMaxSpeed = 0;
		/** 最小速度 */
		private float mMinSpeed = 0;
		/** 平均速度 */
		private float mAvgSpeed = 0;
		/** 上次计算速度时的获取总大小 */
		private float mFileCountSize = 0;
		/** 中断测速线程 */
		private boolean isStop = false;

		@Override
		public void run() {
			this.mStartTime = System.currentTimeMillis();
			while (!isStop) {
				try {
					Thread.sleep(COUNT_TIME_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.countSpeed();
			}
		}

		/**
		 * 计算发送速度
		 * 
		 * @return
		 */
		private void countSpeed() {
			float speed = (mFileCurrSize - this.mFileCountSize) / COUNT_TIME_INTERVAL / 1000 * 1000;
			speed = new BigDecimal(speed).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (speed > this.mMaxSpeed)
				this.mMaxSpeed = speed;
			if (this.mMinSpeed == 0 || speed < this.mMinSpeed)
				this.mMinSpeed = speed;
			long time = System.currentTimeMillis() - this.mStartTime;
			mAvgSpeed = mFileCurrSize / time / 1000 * 1000;
			mAvgSpeed = new BigDecimal(mAvgSpeed).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			if (speed < 76)
				LogUtil.d(mTag, "↑speed(kbps): " + speed + "(now) " + this.mMaxSpeed + "(max) " + this.mMinSpeed + "(min) "
						+ mAvgSpeed + "(avg)");
			this.mFileCountSize = mFileCurrSize;
		}

		/**
		 * 停止当前线程
		 */
		private void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}

	@Override
	protected void finish() {
		if (this.mSpeedTestThread != null) {
			this.mSpeedTestThread.stopThread();
			this.mSpeedTestThread = null;
		}
		LogUtil.d(mTag, "send file:" + this.mSendFile.getName() + " end");
		super.finish();
	}

}

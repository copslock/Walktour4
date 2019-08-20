package com.walktour.service.bluetoothmos.command;

import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.R;
import com.walktour.service.bluetoothmos.model.BluetoothMOSDevice;

import java.util.Arrays;
import java.util.Locale;

/**
 * 基础命令类
 * 
 * @author jianchao.wang
 *
 */
public abstract class BaseCommand {
	/** 应答类型：数据 */
	protected final static String ACK_TYPE_DATA = "00";
	/** 应答结果：成功 */
	private final static String ACK_OK = "01";
	/** 距离上次响应的超时时长 */
	protected final static int RESPONSE_TIMEOUT = 5;
	/** 日志标识 */
	protected final String mTag;
	/** 调用的设备对象 */
	protected final BluetoothMOSDevice mDevice;
	/** 传输数据开始字节 */
	protected final static byte[] sDataStart = hexStringToBytes("E9");
	/** 传输数据结束字节 */
	protected final static byte[] sDataEnd = hexStringToBytes("EA");
	/** 命令类型 */
	protected final CommandType mType;
	/** 当前命令是否执行完成 */
	private boolean isFinish = false;
	/** 当前命令是否超时无响应而中断执行 */
	private boolean isTimeOutInterrupt = false;
	/** 当前命令是否强制中断执行 */
	private boolean isForceInterrupt = false;
	/** 应答消息内容 */
	protected byte[] mACKMessage;
	/** 应答类型 */
	protected String mACKType;
	/** 应答是否成功 */
	protected boolean isACKOK = false;
	/** 响应数据的是否应答，否则是数据 */
	protected boolean isResponseACK = true;
	/** 命令超时监听线程 */
	private TimeoutThread mTimeoutThread = null;
	/** 上次获取响应的时间 */
	private long mCatchResponseTime = 0;
	/** 指令执行超时时长 */
	protected int mTimeout = RESPONSE_TIMEOUT * 1000;

	/**
	 * 
	 * @param type
	 *          命令类型
	 * @param device
	 *          关联的设备对象
	 */
	public BaseCommand(CommandType type, BluetoothMOSDevice device) {
		this.mDevice = device;
		this.mType = type;
		this.mTag = this.getClass().getSimpleName();
	}

	/**
	 * 初始化命令属性
	 */
	protected void init() {
		this.isFinish = false;
		this.isResponseACK = true;
		this.mACKType = null;
		this.mACKMessage = null;
	}

	/** 文件类型 */
	public static enum FileType {
		pesq_8k("PESQ-8K", 8, 8, R.raw.pesqvoice), polqa_8k("POLQA-8K", 8, 8, R.raw.sample_nb_8k), polqa_16k("POLQA-16K",
				16, 8, R.raw.sample_wb_16k), polqa_48k("POLQA-48K", 48, 8,
						R.raw.sample_swb_48k), check_8k("CHECK-8K", 8, 2, R.raw.mos_check_8k);
		/** 文件类型名称 */
		private String mName;
		/** 采样率(K) */
		private int mSampleRate;
		/** 文件时长(秒) */
		private int mRecordTime;
		/** 对应的样本文件的资源Id */
		private int mRawId;

		private FileType(String name, int sampleRate, int recordTime, int rawId) {
			this.mName = name;
			this.mSampleRate = sampleRate;
			this.mRecordTime = recordTime;
			this.mRawId = rawId;
		}

		public int getSampleRate() {
			return mSampleRate;
		}

		/**
		 * 根据文件类型名称获得文件类型
		 * 
		 * @param name
		 *          文件类型名称
		 * @return
		 */
		public static FileType getType(String name) {
			for (FileType fileType : values()) {
				if (fileType.mName.equals(name))
					return fileType;
			}
			return null;
		}

		public String getName() {
			return mName;
		}

		@Override
		public String toString() {
			return this.mName;
		}

		public int getRecordTime() {
			return mRecordTime;
		}

		public int getRawId() {
			return mRawId;
		}
	}

	/** 命令类型 */
	public enum CommandType {
		write_data("01", "write data"), playback("03", "playback"), flash_data("07", "flash data"), get_params("08",
				"get params"), set_params("09", "set params"), record_and_read("0A", "record and read"), connect_check("0B",
						"connect check"), connect_confirm("0B", "connect confirm"), get_file_ids("0C",
								"get file ids"), interrupt("0D", "interrupt"), init_mos("0E",
										"init mos"), uninit_mos("0F", "uninit mos"), disconnect("FF", "disconnect");
		/** 命令类型 */
		private String mType;
		/** 命令名称 */
		private String mName;

		private CommandType(String type, String name) {
			this.mType = type;
			this.mName = name;
		}

		public String getType() {
			return mType;
		}

		/**
		 * 根据命令类型获得命令
		 * 
		 * @param type
		 *          命令类型
		 * @return
		 */
		public static CommandType getType(String type) {
			for (CommandType cmd : values()) {
				if (cmd.mType.equals(type))
					return cmd;
			}
			return null;
		}

		public String getName() {
			return mName;
		}

		@Override
		public String toString() {
			return this.mName;
		}

	}

	/**
	 * 生成请求命令
	 * 
	 * @param msg
	 *          附带消息内容
	 * @return
	 */
	protected byte[] createRequestCmd(byte[] msg) {
		return this.createRequestCmd(this.mType, msg);
	}

	/**
	 * 生成请求命令
	 * 
	 * @param type
	 *          命令类型
	 * @param msg
	 *          附带消息内容
	 * @return
	 */
	protected byte[] createRequestCmd(CommandType type, byte[] msg) {
		byte[][] cmds = new byte[5][];
		cmds[0] = sDataStart;
		cmds[1] = hexStringToBytes(type.getType());
		if (msg == null)
			msg = new byte[0];
		cmds[2] = hexIntToBytes(msg.length, 1);
		cmds[3] = msg;
		cmds[4] = sDataEnd;
		int size = 0;
		for (byte[] cmd : cmds) {
			size += cmd.length;
		}
		byte[] cmd = new byte[size];
		int pos = 0;
		for (byte[] bytes : cmds) {
			System.arraycopy(bytes, 0, cmd, pos, bytes.length);
			pos += bytes.length;
		}
		return cmd;
	}

	/**
	 * 发送请求命令
	 * 
	 * @param cmd
	 *          命令字节数组
	 * @param isTimeLimit
	 *          是否有超时判断
	 */
	protected void sendRequestCmd(byte[] cmd) {
		this.sendRequestCmd(cmd, true);
	}

	/**
	 * 发送请求命令
	 * 
	 * @param cmd
	 *          命令字节数组
	 * @param isTimeLimit
	 *          是否有超时判断
	 */
	private void sendRequestCmd(byte[] cmd, boolean isTimeLimit) {
		this.mDevice.sendMsg(cmd);
		StringBuilder msg = new StringBuilder();
		msg.append("-----send cmd:").append(this.mType.getName());
		String[] req = hexBytesToString(cmd);
		msg.append("\n-----content:").append(this.arraysToString(req));
		LogUtil.d(mTag, msg.toString());
		if (!isTimeLimit)
			return;
		if (this.mTimeoutThread != null) {
			this.mTimeoutThread.stopThread();
		}
		this.mTimeoutThread = new TimeoutThread(this.mTimeout);
		this.mTimeoutThread.start();
	}

	/**
	 * 发送数据
	 * 
	 * @param data
	 *          数据
	 */
	protected void sendData(byte[] data) {
		this.mDevice.sendMsg(data);
	}

	/**
	 * 执行命令
	 * 
	 * @param isTimeLimit
	 *          是否有超时判断
	 */
	public void runCommand() {
		byte[] cmd = this.createRequestCmd(null);
		this.sendRequestCmd(cmd, true);
	}

	/**
	 * 处理反馈的数据
	 * 
	 * @param resp
	 *          反馈数据
	 */
	public void dealResponse(byte[] resp) {
		this.mCatchResponseTime = System.currentTimeMillis();
		if (this.isResponseACK)
			this.dealRespACK(resp);
		else
			this.dealRespData(resp);
	}

	/**
	 * 处理反馈的应答信息
	 * 
	 * @param ack
	 *          应答信息
	 */
	protected abstract void dealRespACK(byte[] ack);

	/**
	 * 解析反馈的应答消息
	 * 
	 * @param ack
	 */
	protected void resolveRespACK(byte[] ack) {
		String[] resp = hexBytesToString(ack);
		this.mACKType = resp[1];
		int len = hexBytesToInt(new byte[] { ack[2] });
		StringBuilder sb = new StringBuilder();
		if (len == 0 || len > ack.length - 4) {
			this.isACKOK = false;
			sb.append("\n-----content:");
			sb.append(this.arraysToString(hexBytesToString(ack)));
		} else {
			this.mACKMessage = new byte[len];
			System.arraycopy(ack, 3, this.mACKMessage, 0, len);
			CommandType type = CommandType.getType(this.mACKType);
			sb.append("-----get ack:");
			if (type == null) {
				this.isACKOK = true;
				sb.append("data");
			} else {
				this.isACKOK = hexBytesToString(mACKMessage)[0].equals(ACK_OK);
				sb.append(type.getName());
			}
			sb.append("\n-----content:");
			sb.append(this.arraysToString(hexBytesToString(this.mACKMessage)));
		}
		LogUtil.d(mTag, sb.toString());
	}

	/**
	 * 处理接收的数据信息
	 * 
	 * @param resp
	 *          数据信息
	 */
	protected abstract void dealRespData(byte[] resp);

	/**
	 * 命令是否执行完成
	 * 
	 * @return
	 */
	public boolean isFinish() {
		return isFinish;
	}

	/**
	 * 转换16进制字符窜为byte数组
	 * 
	 * @param hexStr
	 *          16进制字符串
	 * @return byte[]
	 */
	protected static byte[] hexStringToBytes(String hexStr) {
		if (StringUtil.isNullOrEmpty(hexStr)) {
			return null;
		}
		byte[] bytes = new byte[hexStr.length() / 2];
		for (int i = 0, j = 0; i < hexStr.length(); i = i + 2, j++) {
			bytes[j] = (byte) (charToByte(hexStr.charAt(i)) << 4 | charToByte(hexStr.charAt(i + 1)));
		}
		return bytes;
	}

	/**
	 * 把16进制byte数组转成整型
	 * 
	 * @param buf
	 *          比特数组
	 * @return
	 */
	protected static int hexBytesToInt(byte[] buf) {
		int r = 0;
		for (int i = buf.length - 1; i >= 0; i--) {
			r <<= 8;
			r |= (buf[i] & 0x000000ff);
		}
		return r;
	}

	/**
	 * 字符转换成byte
	 * 
	 * @param c
	 *          字符串
	 * @return
	 */
	private static byte charToByte(char c) {
		return Byte.decode("0x" + c).byteValue();
	}

	/**
	 * 转换整型为16进制byte数组
	 * 
	 * @param num
	 *          整型
	 * @param size
	 *          大小
	 * @return
	 */
	protected static byte[] hexIntToBytes(int num, int size) {
		byte[] bytes = new byte[4];
		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) ((num >> (8 * i)) & 0xff);
		}
		if (size < 4) {
			byte[] newBytes = new byte[size];
			System.arraycopy(bytes, 0, newBytes, 0, size);
			return newBytes;
		}
		return bytes;
	}

	/**
	 * 转换16进制byte数组为字符串
	 * 
	 * @param src
	 * @return
	 */
	protected static String[] hexBytesToString(byte[] src) {
		if (src == null || src.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < src.length; i++) {
			if (i > 0)
				sb.append(",");
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v).toUpperCase(Locale.getDefault());
			if (hv.length() < 2) {
				sb.append("0");
			}
			sb.append(hv);
		}
		return sb.toString().split(",");
	}

	public CommandType getType() {
		return mType;
	}

	@Override
	public String toString() {
		return this.mType.getName();
	}

	/**
	 * 生成打印输出的字符串数组
	 * 
	 * @param arr
	 *          字符串数组
	 * @return
	 */
	protected String arraysToString(String[] arr) {
		return Arrays.toString(arr);
	}

	/**
	 * 完成当前命令
	 */
	protected void finish() {
		if (this.mTimeoutThread != null)
			this.mTimeoutThread.stopThread();
		this.isFinish = true;
		this.mDevice.finishCurrentCommand();
	}

	/**
	 * 强制中断当前命令
	 * 
	 * @param isSendCmd
	 *          是否要发送中断指令
	 */
	public void interrupt(boolean isSendCmd) {
		LogUtil.d(mTag, "-----interrupt-----");
		this.isForceInterrupt = true;
		if (isSendCmd)
			this.interruptCommand();
		else {
			if (this.mTimeoutThread != null)
				this.mTimeoutThread.stopThread();
			this.isFinish = true;
		}
	}

	/**
	 * 超时监控线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class TimeoutThread extends Thread {
		/** 是否停止当前线程 */
		private boolean isStop = false;
		/** 超时时长 */
		private int mTimeout = 0;

		private TimeoutThread(int timeout) {
			this.mTimeout = timeout;
		}

		@Override
		public void run() {
			LogUtil.d(mTag, "-----TimeoutThread-----run-----");
			long startTime = System.currentTimeMillis();
			if (mCatchResponseTime == 0)
				mCatchResponseTime = startTime;
			while (!isStop) {
				if (mType != CommandType.write_data && mType != CommandType.playback) {
					startTime = mCatchResponseTime;
				}
				if (System.currentTimeMillis() - startTime > this.mTimeout) {
					break;
				}
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
			}
			if (!isStop) {
				if (isTimeOutInterrupt) {
					LogUtil.d(mTag, "-----TimeoutThread-----interrupt run time out-----");
					finish();
					return;
				}
				LogUtil.d(mTag, "-----TimeoutThread-----command run time out-----");
				interruptCommand();
				isTimeOutInterrupt = true;
				mTimeoutThread = null;
				if (mType != CommandType.record_and_read && mType != CommandType.playback) {
					finish();
				} else {
					mTimeoutThread = new TimeoutThread(RESPONSE_TIMEOUT * 1000);
					mTimeoutThread.start();
				}
			}
		}

		/**
		 * 停止当前线程
		 */
		public void stopThread() {
			this.isStop = true;
			this.interrupt();
		}
	}

	/**
	 * 发送指令中断当前命令
	 * 
	 * @return
	 */
	private void interruptCommand() {
		if (this.isFinish)
			return;
		byte[] cmd = this.createRequestCmd(CommandType.interrupt, null);
		this.sendRequestCmd(cmd, false);
	}

	/**
	 * 把MOS头传过来的pcm格式的音频文件转成wav格式的音频文件
	 * 
	 * @param recordFilePath
	 *          录音文件
	 * @param sampleRate
	 *          采样率(K)
	 */
	protected String convertAudioFile(String recordFilePath, int sampleRate) {
		return this.mDevice.convertAudioFile(recordFilePath, sampleRate);
	}

	public boolean isTimeOutInterrupt() {
		return isTimeOutInterrupt;
	}

	protected boolean isForceInterrupt() {
		return isForceInterrupt;
	}

}

package com.walktour.service.bluetoothmos.util;

import com.walktour.base.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 音频文件转换处理类
 * 
 * @author jianchao.wang
 *
 */
public class FileUtil {
	/** 日志标识 */
	private static final String TAG = "FileUtil";

	/**
	 * 把pcm格式的文件转成wav格式
	 * 
	 * @param src
	 *          pcm格式源文件
	 * @param target
	 *          wav格式目标文件
	 * @param sampleRate
	 *          采样率(K)
	 * @throws Exception
	 */
	public static void convertPCMtoWAV(File src, File target, int sampleRate) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		LogUtil.d(TAG, "Convert PCM to WAV Start!");
		try {
			int pcmSize = (int) src.length();
			// 填入参数，比特率等等。这里用的是16位单声道 8000 hz
			WaveHeader header = new WaveHeader();
			// 长度字段 = 内容的大小（pcmSize) + 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
			header.fileLength = pcmSize + (44 - 8);
			header.fmtHdrLeth = 16;
			header.bitsPerSample = 16;
			header.channels = 1;
			header.formatTag = 0x0001;
			header.samplesPerSec = sampleRate * 1000;
			header.blockAlign = (short) (header.channels * header.bitsPerSample / 8);
			header.avgBytesPerSec = header.blockAlign * header.samplesPerSec;
			header.dataHdrLeth = pcmSize;

			fos = new FileOutputStream(target);
			byte[] h = header.getHeader();
			// WAV标准，头部应该是44字节
			if (h.length != 44) {
				LogUtil.d(TAG, "Convert PCM to WAV Failure!");
				return;
			}
			fos.write(h, 0, h.length);
			fis = new FileInputStream(src);
			byte[] buf = new byte[512];
			int size = 0;
			while ((size = fis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage(), e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage(), e);
			}
		}
		LogUtil.d(TAG, "Convert PCM to WAV End!");
	}

	/**
	 * 把wav格式的文件转成pcm格式,wav格式比pcm格式是多了开头的格式说明
	 * 
	 * @param src
	 *          wav格式源文件
	 * @param target
	 *          pcm格式目标文件
	 * @throws Exception
	 */
	public static void convertWAVtoPCM(File src, File target) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			LogUtil.d(TAG, "Convert WAV to PCM Start!");

			fos = new FileOutputStream(target);
			fis = new FileInputStream(src);
			// 刨除wav的开始文件
			fis.read(new byte[44]);
			byte[] buf = new byte[512];
			int size = 0;
			while ((size = fis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage(), e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				LogUtil.e(TAG, e.getMessage(), e);
			}
		}
		LogUtil.d(TAG, "Convert WAV to PCM End!");
	}

	/**
	 * wav文件格式头
	 * 
	 * @author jianchao.wang
	 *
	 */
	private static class WaveHeader {
		/** 生成的文件ID */
		private final char fileID[] = { 'R', 'I', 'F', 'F' };
		/** 生成的文件长度 */
		public int fileLength;
		/** wav格式标签 */
		private char wavTag[] = { 'W', 'A', 'V', 'E' };
		/** 格式头ID */
		private char fmtHdrID[] = { 'f', 'm', 't', ' ' };
		/** 格式头长度 */
		public int fmtHdrLeth;
		/** 格式化标签 */
		public short formatTag;
		/** 声道数 */
		public short channels;
		/** 采样率 */
		public int samplesPerSec;
		/** 平均位数 */
		public int avgBytesPerSec;
		/** 数据块的起点 */
		public short blockAlign;
		/** 样本位数 */
		public short bitsPerSample;
		/** 数据头ID */
		private char dataHdrID[] = { 'd', 'a', 't', 'a' };
		/** 数据长度 */
		public int dataHdrLeth;

		/**
		 * 获取生成的头字节数组
		 * 
		 * @return
		 */
		public byte[] getHeader() {
			ByteArrayOutputStream bos = null;
			byte[] bytes = null;
			try {
				bos = new ByteArrayOutputStream();
				writeChar(bos, fileID);
				writeInt(bos, fileLength);
				writeChar(bos, wavTag);
				writeChar(bos, fmtHdrID);
				writeInt(bos, fmtHdrLeth);
				writeShort(bos, formatTag);
				writeShort(bos, channels);
				writeInt(bos, samplesPerSec);
				writeInt(bos, avgBytesPerSec);
				writeShort(bos, blockAlign);
				writeShort(bos, bitsPerSample);
				writeChar(bos, dataHdrID);
				writeInt(bos, dataHdrLeth);
				bos.flush();
				bytes = bos.toByteArray();
			} catch (Exception e) {
				LogUtil.e(TAG, e.getMessage(), e);
			} finally {
				try {
					if (bos != null)
						bos.close();
				} catch (IOException e) {
					LogUtil.e(TAG, e.getMessage(), e);
				}

			}
			return bytes;
		}

		/**
		 * 写短整型
		 * 
		 * @param bos
		 *          输出流
		 * @param s
		 *          短整型
		 * @throws IOException
		 */
		private void writeShort(ByteArrayOutputStream bos, int s) throws IOException {
			byte[] mybyte = new byte[2];
			mybyte[1] = (byte) ((s << 16) >> 24);
			mybyte[0] = (byte) ((s << 24) >> 24);
			bos.write(mybyte);
		}

		/**
		 * 写整数
		 * 
		 * @param bos
		 *          输出流
		 * @param n
		 *          整数
		 * @throws IOException
		 */
		private void writeInt(ByteArrayOutputStream bos, int n) throws IOException {
			byte[] buf = new byte[4];
			buf[3] = (byte) (n >> 24);
			buf[2] = (byte) ((n << 8) >> 24);
			buf[1] = (byte) ((n << 16) >> 24);
			buf[0] = (byte) ((n << 24) >> 24);
			bos.write(buf);
		}

		/**
		 * 写字符
		 * 
		 * @param bos
		 *          输出流
		 * @param id
		 *          字符数组
		 * @throws IOException
		 */
		private void writeChar(ByteArrayOutputStream bos, char[] id) throws IOException {
			for (int i = 0; i < id.length; i++) {
				char c = id[i];
				bos.write(c);
			}
		}
	}
}

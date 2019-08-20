package com.walktour.control.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReader {

	/**
	 * 读取文件文件的内容
	 * 
	 * @param filePath
	 * @return 返回文本文件的字符,如果文件不存在时返回null.
	 */
	public String getFileText(String filePath) {
		return getFileText(new File(filePath));
	}

	/**
	 * 读取文件文件的内容
	 * 
	 * @param file
	 *          传入文件
	 * @return
	 */
	public String getFileText(File file) {
		if (!file.exists()) {
			return null;
		}

		FileInputStream inStream = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			inStream = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(inStream));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * 读取文件文件的内容
	 *
	 * @param filePath
	 * @return 返回文本文件的字符,如果文件不存在时返回空数组
	 */
	public byte[] getFileBytes(String filePath) {
		return this.getFileBytes(new File(filePath));
	}

	/**
	 * 读取文件文件的内容
	 *
	 * @param file
	 *          传入文件
	 * @return
	 */
	public byte[] getFileBytes(File file) {
		if (!file.exists()) {
			return new byte[0];
		}
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			int length = inStream.available();
			byte[] buffer = new byte[length];
			inStream.read(buffer);
			inStream.close();
			return buffer;
		} catch (Exception e) {
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new byte[0];
	}

}
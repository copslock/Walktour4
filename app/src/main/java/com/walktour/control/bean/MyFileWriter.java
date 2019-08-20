package com.walktour.control.bean;

import com.walktour.base.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 数据写入指定文件
 * 
 * @author jianchao.wang
 *
 */
public class MyFileWriter {

	/**
	 * 写数据到指定路径文件
	 * 
	 * @param filePath
	 *          文件路径
	 * @param data
	 *          数据流
	 */
	public static File write(String filePath, InputStream data) {
		File file = new File(filePath);

		// 文件不存在时创建文件
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			int len;
			while ((len = data.read(buf)) > 0)
				out.write(buf, 0, len);
			out.flush();
		} catch (IOException e) {
			LogUtil.e("MyFileWriter", e.toString());
		} finally {
			try {
				if (out != null)
					out.close();
				if (data != null)
					data.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 写数据到指定路径文件
	 *
	 * @param desFile
	 *          文件路径
	 * @param srcFilePath
	 *          数据流
	 */
	public static void write(File desFile, String  srcFilePath) {
		File srcFile = new File(srcFilePath);
		if(!srcFile.exists())
			return;
		// 文件不存在时创建文件
		if (!desFile.exists()) {
			try {
				desFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutputStream outputStream = null;
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(srcFile);
			outputStream = new FileOutputStream(desFile);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0)
				outputStream.write(buf, 0, len);
			outputStream.flush();
		} catch (IOException e) {
			LogUtil.e("MyFileWriter", e.toString());
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写数据到指定路径文件
	 * 
	 * @param filePath
	 *          文件路径
	 * @param data
	 *          数据
	 * @return
	 */
	public static File write(String filePath, String data) {
		File file = new File(filePath);

		// 文件不存在时创建文件
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data.getBytes("UTF-8"));
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return file;
	}

	/**
	 * 以追加的形式把字符写入到文本文件中
	 */
	public static void appendToFile(String filePath, String strArray) {
		FileWriter fw = null;
		try {
			// 第二个参数 true 表示写入方式是追加方式
			fw = new FileWriter(filePath, true);
			fw.write(strArray);
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			try {
				if(fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
package com.walktour.Utils;

import android.text.TextUtils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * 执行zip压缩<BR>
 * [功能详细描述]
 * 
 * @author 黄广府
 * @version [RCS Client V100R001C03, 2013-8-23]
 */
public class ZipUtil {
	/**
	 * 压缩文件file成zip文件zipFile
	 * 
	 * @param file
	 *          要压缩的文件
	 * @param zipFile
	 *          压缩文件存放地方
	 * @throws Exception
	 */
	public static void zip(File file, File zipFile) {
		ZipOutputStream output = null;
		try {
			output = new ZipOutputStream(new FileOutputStream(zipFile));
			output.setEncoding("UTF-8");
			// 顶层目录开始
			zipFile(output, file, "");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				// 关闭流
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 压缩文件file成zip文件zipFile
	 * 
	 * @param files
	 *          要压缩的文件
	 * @param zipFile
	 *          压缩文件存放地方
	 * @throws Exception
	 */
	public static void zip(Set<File> files, File zipFile) throws Exception {
		zip(files, zipFile, "UTF-8");
	}

	/**
	 * 压缩文件file成zip文件zipFile
	 * 
	 * @param files
	 *          要压缩的文件
	 * @param zipFile
	 *          压缩文件存放地方
	 * @throws Exception
	 */
	public static void zip(Set<File> files, File zipFile, String charSet) throws Exception {
		ZipOutputStream output = null;
		try {
			output = new ZipOutputStream(new FileOutputStream(zipFile));
			if (!StringUtil.isNullOrEmpty(charSet))
				output.setEncoding(charSet);
			for (File file : files) {
				zipFile(output, file, "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 关闭流
			if (output != null) {
				output.flush();
				output.close();
			}
		}
	}

	/**
	 * 压缩文件为zip格式
	 * 
	 * @param output
	 *          ZipOutputStream对象
	 * @param file
	 *          要压缩的文件或文件夹
	 * @param basePath
	 *          条目根目录
	 * @throws IOException
	 */
	private static void zipFile(ZipOutputStream output, File file, String basePath) throws IOException {
		FileInputStream input = null;
		try {
			// 文件为目录
			if (file.isDirectory()) {
				// 得到当前目录里面的文件列表
				File list[] = file.listFiles();
				basePath = basePath + (basePath.length() == 0 ? "" : "/") + file.getName();
				// 循环递归压缩每个文件
				for (File f : list)
					zipFile(output, f, basePath);
			} else {
				// 压缩文件
				basePath = (basePath.length() == 0 ? "" : basePath + "/") + file.getName();
				// System.out.println(basePath);
				output.putNextEntry(new ZipEntry(basePath));
				input = new FileInputStream(file);
				int readLen = 0;
				byte[] buffer = new byte[1024 * 8];
				while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
					output.write(buffer, 0, readLen);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 关闭流
			if (input != null)
				input.close();
		}
	}

	/**
	 * 压缩整个文件夹中的所有文件，生成指定名称的zip压缩包
	 * 
	 * @param filepath
	 *          文件所在目录
	 * @param zippath
	 *          压缩后zip文件名称
	 * @param dirFlag
	 *          zip文件中第一层是否包含一级目录，true包含；false没有 2015年6月9日
	 * @param filterHolder
	 *          压缩时需要过滤的文件夹,只会过滤第一级文件夹，
	 * @param filterFileType
	 *          压缩时需要过滤的文件夹里面的文件类型
	 */
	public static void zipShareFile(String filepath, String zippath, boolean dirFlag, List<String> filterHolder,
			List<String> filterFileType, List<String> filterFileName) {
		try {
			zippath = zippath.replace("//", "/");
			File file = new File(filepath);// 要被压缩的文件夹
			File zipFile = new File(zippath);
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
			// 设置filenames的编码
			zipOut.setEncoding("UTF-8");
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (null != files && files.length > 0) {
					for (File fileSec : files) {
						if (dirFlag) {
							recursionZip(zipOut, fileSec, file.getName() + File.separator, filterHolder, filterFileType,
									filterFileName);
						} else {
							recursionZip(zipOut, fileSec, "", filterHolder, filterFileType, filterFileName);
						}
					}
				} else {// 支持空文件夹
					ZipEntry ze = new ZipEntry(file.getName() + File.separator);
					ze.setCompressedSize(0);
					zipOut.putNextEntry(ze);
				}
			}
			zipOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 递归压缩文件
	 * 
	 * @param zipOut
	 *          压缩的文件流
	 * @param file
	 *          要压缩的文件或目录
	 * @param baseDir
	 *          压缩文件所在的父目录
	 * @param filterHolder
	 *          过滤的文件类型
	 * @param filterFileType
	 *          要压缩的文件类型
	 * @param filterFileName
	 *          要压缩的文件名称
	 * @throws Exception
	 */
	private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir, List<String> filterHolder,
			List<String> filterFileType, List<String> filterFileName) throws Exception {
		if (file.isDirectory()) {
			if (null != filterHolder) {
				for (String s : filterHolder) {
					if (file.getAbsolutePath().endsWith(s))
						return;
				}
			}
			File[] files = file.listFiles();
			if (null != files && files.length > 0) {
				ZipEntry ze = new ZipEntry(baseDir + file.getName() + File.separator);
				ze.setCompressedSize(0);
				zipOut.putNextEntry(ze);
				for (File fileSec : files) {
					recursionZip(zipOut, fileSec, baseDir + file.getName() + File.separator, null, filterFileType,
							filterFileName);
				}
			} else {// 支持空文件夹
				ZipEntry ze = new ZipEntry(baseDir + file.getName() + File.separator);
				ze.setCompressedSize(0);
				zipOut.putNextEntry(ze);
			}
		} else {
			boolean isF = false;
			// 需要过滤的文件名称
			if (null != filterFileType) {
				for (String s : filterFileType) {
					if (file.getAbsolutePath().endsWith(s)) {
						isF = true;
						break;
					}
				}
			}
			if (null != filterFileName) {
				for (String s : filterFileName) {
					if (file.getName().equals(s)) {
						isF = true;
						break;
					}
				}
			}
			if (!isF) {
				byte[] buf = new byte[8 * 1024];
				InputStream input = new FileInputStream(file);
				zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
				int len;
				while ((len = input.read(buf)) != -1) {
					zipOut.write(buf, 0, len);
				}
				input.close();
			} else {
			}
		}
	}

	/**
	 * 解压zip文件
	 * 
	 * @param zipFilePath
	 *          zip文件绝对路径
	 * @param unzipDirectory
	 *          解压到的目录
	 * @throws Exception
	 */
	public static void unzip(String zipFilePath, String unzipDirectory) throws Exception {
		unzip(zipFilePath, unzipDirectory, true);
	}

	/**
	 * 解压zip文件
	 *
	 * @param zipFilePath    zip文件绝对路径
	 * @param unzipDirectory 解压到的目录
	 * @param createFile     是否在解压目录下创建同名文件目录
	 */
	public static void unzip(String zipFilePath, String unzipDirectory, boolean createFile) throws Exception {
		unzip(zipFilePath, unzipDirectory, null, createFile);
	}

	/**
	 * 解压zip文件
	 * 
	 * @param zipFilePath
	 *          zip文件绝对路径
	 * @param unzipDirectory
	 *          解压到的目录
	 * @param encoding       编码格式
	 * @param createFile
	 *          是否在解压目录下创建同名文件目录
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void unzip(String zipFilePath, String unzipDirectory, String encoding, boolean createFile) throws Exception {
		// 定义输入输出流对象
		InputStream input = null;
		OutputStream output = null;
		try {
			// 创建文件对象
			File file = new File(zipFilePath);
			// 创建zip文件对象
			ZipFile zipFile;
			if(TextUtils.isEmpty(encoding)){
				zipFile = new ZipFile(file);
			}else{
				zipFile = new ZipFile(file,encoding);
			}
			// 创建本zip文件解压目录
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			File unzipFile = null;
			if (createFile) {
				unzipFile = new File(unzipDirectory + "/" + name);
				if (unzipFile.exists())
					unzipFile.delete();
			} else {
				unzipFile = new File(unzipDirectory);
			}
			if (!unzipFile.exists())
				unzipFile.mkdir();
			// 得到zip文件条目枚举对象
			Enumeration zipEnum = zipFile.getEntries();
			// 定义对象
			ZipEntry entry = null;
			String entryName = null, path = null;
			String names[] = null;
			int length;
			// 循环读取条目
			while (zipEnum.hasMoreElements()) {
				// 得到当前条目
				entry = (ZipEntry) zipEnum.nextElement();
				entryName = new String(entry.getName());
				// 用/分隔条目名称
				names = entryName.split("\\/");
				length = names.length;
				path = unzipFile.getAbsolutePath();
				for (int v = 0; v < length; v++) {
					if (v < length - 1) {// 最后一个目录之前的目录
						createDirectory(path += "/" + names[v] + "/");
					} else { // 最后一个
						if (entryName.endsWith("/")) // 为目录,则创建文件夹
							createDirectory(unzipFile.getAbsolutePath() + "/" + entryName);
						else { // 为文件,则输出到文件
							if (new File(unzipFile.getAbsolutePath() + "/" + entryName).exists()) {
								if ((entry.getTime() >= new File(unzipFile.getAbsolutePath() + "/" + entryName).lastModified())) {
									input = zipFile.getInputStream(entry);
									output = new FileOutputStream(new File(unzipFile.getAbsolutePath() + "/" + entryName));
									byte[] buffer = new byte[1024 * 8];
									int readLen = 0;
									while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
										output.write(buffer, 0, readLen);
								}
							} else {
								input = zipFile.getInputStream(entry);
								output = new FileOutputStream(new File(unzipFile.getAbsolutePath() + "/" + entryName));
								byte[] buffer = new byte[1024 * 8];
								int readLen = 0;
								while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
									output.write(buffer, 0, readLen);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 关闭流
			if (input != null)
				input.close();
			if (output != null) {
				output.flush();
				output.close();
			}
		}
	}

	/***
	 * 此解压专门用于解压微服务中共享文件信息
	 * 
	 * @param zipFilePath
	 * @param unzipDirectory
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void unsharezip(String zipFilePath, String unzipDirectory) throws Exception {
		// 定义输入输出流对象
		InputStream input = null;
		OutputStream output = null;
		try {
			// 创建文件对象
			File file = new File(zipFilePath);
			// 创建zip文件对象
			ZipFile zipFile = new ZipFile(file, "UTF-8");
			// 创建本zip文件解压目录
			File unzipFile = new File(unzipDirectory);
			if (unzipFile.exists())
				unzipFile.delete();
			unzipFile.mkdir();
			// 得到zip文件条目枚举对象
			Enumeration zipEnum = zipFile.getEntries();
			// 定义对象
			ZipEntry entry = null;
			String entryName = null, path = null;
			String names[] = null;
			int length;
			// 循环读取条目
			while (zipEnum.hasMoreElements()) {
				// 得到当前条目
				entry = (ZipEntry) zipEnum.nextElement();
				entryName = new String(entry.getName());
				// 用/分隔条目名称
				names = entryName.split("\\/");
				length = names.length;
				path = unzipFile.getAbsolutePath();
				for (int v = 0; v < length; v++) {
					if (v < length - 1) {// 最后一个目录之前的目录
						createDirectory(path += "/" + names[v] + "/");
					} else { // 最后一个
						if (entryName.endsWith("/")) // 为目录,则创建文件夹
							createDirectory(unzipFile.getAbsolutePath() + "/" + entryName);
						else { // 为文件,则输出到文件
							if (new File(unzipFile.getAbsolutePath() + "/" + entryName).exists()) {
								if ((entry.getTime() >= new File(unzipFile.getAbsolutePath() + "/" + entryName).lastModified())) {
									input = zipFile.getInputStream(entry);
									output = new FileOutputStream(new File(unzipFile.getAbsolutePath() + "/" + entryName));
									byte[] buffer = new byte[1024 * 8];
									int readLen = 0;
									while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
										output.write(buffer, 0, readLen);
								}
							} else {
								input = zipFile.getInputStream(entry);
								output = new FileOutputStream(new File(unzipFile.getAbsolutePath() + "/" + entryName));
								byte[] buffer = new byte[1024 * 8];
								int readLen = 0;
								while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1)
									output.write(buffer, 0, readLen);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 关闭流
			if (input != null)
				input.close();
			if (output != null) {
				output.flush();
				output.close();
			}
		}
	}

	private static void createDirectory(String path) {
		File dfile = new File(path);
		if (!dfile.exists()) {
			dfile.mkdir();
		}
	}

	/***
	 * 数组转为zip文件
	 * 
	 * @param zipBytes
	 * @param zipFile
	 */
	public static void zipFile(byte[] zipBytes, String zipFile) {
		InputStream is = new ByteArrayInputStream(zipBytes);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(zipFile);
			byte[] b = new byte[1024];
			while ((is.read(b)) != -1) {
				fos.write(b);
			}
			is.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			is = null;
			fos = null;
		}
	}

	/**
	 * 测试
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// unzip("d:/桌面.zip", "f:/");
		System.out.println("over...................." + System.currentTimeMillis());
		zip(new File("D:/zip"), new File("d:/dd4.zip"));
		System.out.println("over.............." + System.currentTimeMillis());
	}
}

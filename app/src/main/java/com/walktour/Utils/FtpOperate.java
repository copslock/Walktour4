package com.walktour.Utils;

import android.content.Context;
import android.util.Log;

import com.walktour.Utils.FtpTranserStatus.DownloadStatus;
import com.walktour.Utils.FtpTranserStatus.UploadStatus;
import com.walktour.base.util.LogUtil;
import com.walktour.model.FtpServerModel;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.Deflater;

public class FtpOperate {

	public static int FTP_TIMEOUT = 40 * 1000;

	public static int PIC_FTP_TIMEOUT = 9 * 1000;
	public static int BUFFER_SIZE = 32 * 1000;

	public FTPClient ftpClient = new FTPClient();

	private static final String tag = "FtpOperate";
	// private Context mContext;
	// private List<FTPFile> list = new LinkedList<FTPFile>();;
	private OnProgressChangeListener mProgressChangeListener;
	private boolean hasStop = false;

	private String Encoding = System.getProperty("file.encoding");
	private final String ENCODING_FTP = "ISO-8859-1";
	private final String ENCODING_GBK = "GBK";
	private FileOutputStream fos;

	public FtpOperate() {

	}

	public FtpOperate(Context context) {
		// 设置将过程中使用到的命令输出到控制台
		ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		// this.mContext = context;
	}

	public FtpOperate(Context context, OnProgressChangeListener onProgressChangeListener) {
		// 设置将过程中使用到的命令输出到控制台
		ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		// this.mContext = context;
		this.mProgressChangeListener = onProgressChangeListener;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	/**
	 * 连接到FTP服务器
	 * 
	 * @param hostname
	 *          主机名
	 * @param port
	 *          端口
	 * @param username
	 *          用户名
	 * @param password
	 *          密码
	 * @return 是否连接成功
	 * @throws IOException
	 */
	public boolean connect(String hostname, int port, String username, String password) {
		return this.connect(hostname, port, username, password, FTP_TIMEOUT);
	}

	/**
	 * 连接到FTP服务器
	 * 
	 * @param hostname
	 *          主机名
	 * @param port
	 *          端口
	 * @param username
	 *          用户名
	 * @param password
	 *          密码
	 * @param timeout
	 *          超时时长
	 * @return 是否连接成功
	 * @throws IOException
	 */
	public boolean connect(String hostname, int port, String username, String password, int timeout) {
		try {
			if (ftpClient.isConnected()) {
				LogUtil.w(tag, "--connect isConnected--");
				return true;
			}
//			LogUtil.w(tag, "-----connect hostname:" + hostname + "-----port:" + port + "-----username:" + username + "-----password:" + password);
			ftpClient.setDefaultTimeout(timeout);
			ftpClient.setConnectTimeout(timeout);
			ftpClient.setDataTimeout(timeout);
			ftpClient.connect(hostname, port);
			ftpClient.setControlEncoding(Encoding = ENCODING_GBK);
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				if (ftpClient.login(username, password)) {
					// 设置为passive模式
					ftpClient.enterLocalPassiveMode();
					return true;
				}
			}
			LogUtil.w(tag, "---connect resule faild,to disconnect--");

			disconnect();
		} catch (Exception e) {
			LogUtil.e(tag, e.getMessage(), e.fillInStackTrace());
			try {
				disconnect();
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获取服务器指定路径下的所有文件
	 * 
	 * @param pathname
	 *          路径名称
	 * @param isActive
	 *          是否主动模式
	 * @return
	 */
	public FTPFile[] listFiles(String pathname, boolean isActive) {
		FTPFile[] files = new FTPFile[0];
		try {
			if (isActive)
				ftpClient.enterLocalActiveMode();
			else
				ftpClient.enterLocalPassiveMode();
			// 设置以二进制方式传输
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			files = ftpClient.listFiles(pathname);
		} catch (Exception e) {
			LogUtil.e(tag, "listFiles:" + e.getMessage());
		}
		return files;
	}

	/**
	 * 判断FTP端的指定文件是否存在
	 * 
	 * @param remote
	 * @return
	 */
	public boolean ftpFileExist(String remote) {
		try {
			ftpClient.enterLocalPassiveMode();
			// 设置以二进制方式传输
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			// 检查远程文件是否存在
			FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes(Encoding), ENCODING_FTP));

			LogUtil.w(tag, "--ftpFileExist:" + files.length);
			if (files.length == 1) {
				return true;
			}
		} catch (Exception e) {
			LogUtil.w(tag, "ftpFileExist:" + remote, e);
		}
		return false;
	}

	/**
	 * 连接到FTP服务器
	 * 
	 * @param server
	 *          连接参数
	 * @return 是否连接成功
	 * @throws IOException
	 */
	public boolean connect(FtpServerModel server) throws IOException {
		return connect(server.getIp(), Integer.parseInt(server.getPort()), server.getLoginUser(),
				server.getLoginPassword());
	}

	/**
	 * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
	 * 
	 * @param remote
	 *          远程文件路径
	 * @param local
	 *          本地文件路径
	 * @return 上传的状态
	 * @throws IOException
	 */
	public DownloadStatus download(String remote, String local) throws IOException {
		return this.download(remote, local, true, false);
	}

	/**
	 * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
	 * 
	 * @param remote
	 *          远程文件路径
	 * @param local
	 *          本地文件路径
	 * @param isReport
	 *          是否报告
	 * @param isActive
	 *          是否主动模式
	 * @return 上传的状态
	 * @throws IOException
	 */
	public DownloadStatus download(String remote, String local, boolean isReport, boolean isActive) throws IOException {
		hasStop = false;
		// 设置被动模式
		if (isActive)
			ftpClient.enterLocalActiveMode();
		else
			ftpClient.enterLocalPassiveMode();
		// 设置以二进制方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		DownloadStatus result = null;

		if (!ftpClient.isConnected()) {
			LogUtil.w(tag, "---download isConnected false");
		}

		// 检查远程文件是否存在
		FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes(Encoding), ENCODING_FTP));
		LogUtil.w(tag, "--download files:" + files.length);
		if (files.length != 1) {
			return DownloadStatus.Remote_File_Noexist;
		}

		long remoteSize = files[0].getSize();
		File f = new File(local);
		long localSize = f.length();

		// 判断本地文件大小是否大于远程文件大小
		/*
		 * if (localSize > remoteSize) { Log.e(tag, String.format("%s:%d",
		 * f.getAbsolutePath(), f.length()));
		 * System.out.println("本地文件大于远程文件，删除本地文件"); return
		 * DownloadStatus.Local_Bigger_Remote; } else
		 */ if (localSize >= remoteSize) {
			f.delete();
			localSize = 0;
		}
		FileOutputStream out = null;
		InputStream in = null;
		boolean isContinue = false;
		try {
			// 本地存在文件，进行断点下载，并记录状态
			LogUtil.w(tag, "--file exists:" + f.exists() + "--local size:" + local);
			if (f.exists() && localSize < remoteSize) {
				out = new FileOutputStream(f, true);
				ftpClient.setRestartOffset(localSize);
				isContinue = true;
			} else {
				// 下载目录不存在时创建
				File dir = new File(f.getParent());
				if (!(dir.exists() && dir.isDirectory())) {
					dir.mkdirs();
				}
				out = new FileOutputStream(f);
			}
			in = ftpClient.retrieveFileStream(new String(remote.getBytes(Encoding), ENCODING_FTP));
			byte[] bytes = new byte[1024];
			int size;
			int process = (int) ((double) localSize / (double) remoteSize * 100);
			int logPrecess = process;
			while ((size = in.read(bytes)) != -1 && !hasStop) {
				out.write(bytes, 0, size);
				localSize += size;
				int nowProcess = (int) ((double) localSize / (double) remoteSize * 100);
				if (nowProcess > process) {
					process = nowProcess;
					if (process > logPrecess + 10) {
						logPrecess = process;
						LogUtil.d(tag, "download progress:" + process);
					}
					if (isReport && mProgressChangeListener != null) {
						mProgressChangeListener.onProgressChange(localSize, remoteSize);
					}
				}
			}
			in.close();
			out.close();
			boolean isDo = ftpClient.completePendingCommand();
			if (isDo) {
				if (isContinue)
					result = DownloadStatus.Download_From_Break_Success;
				else
					result = DownloadStatus.Download_New_Success;
			} else {
				if (hasStop) {
					result = DownloadStatus.Download_Stopped;
				} else {
					result = DownloadStatus.Download_New_Failed;
				}
			}
		} catch (Exception e) {
			LogUtil.w(tag, "Downlaod Ftp" + result, e);
			result = DownloadStatus.Download_New_Failed;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception ee) {
				LogUtil.w(tag, "Close FTP", ee);
				result = DownloadStatus.Download_New_Failed;
			}
		}
		LogUtil.d(tag, "download finish!" + result);
		return result;
	}

	/**
	 * 上传文件到FTP服务器，支持断点续传
	 * 
	 * @param local
	 *          本地文件名称，绝对路径
	 * @param remote
	 *          远程文件路径 支持多级目录嵌套，支持递归创建不存在的目录结构
	 * @return 上传结果
	 * @throws IOException
	 */
	public UploadStatus uploadFile(String local, String remote) throws IOException {

		hasStop = false;

		// 设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();
		// ftpClient.enterLocalActiveMode();
		// 设置以二进制流的方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding(ENCODING_FTP);
		UploadStatus result;
		// 对远程目录的处理
		String remoteFileName = remote;
		if (remote.contains("/")) {
			remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
			// 创建服务器远程目录结构，创建失败直接返回
			if (CreateDirecroty(remote, ftpClient) == UploadStatus.Create_Directory_Fail) {
				return UploadStatus.Create_Directory_Fail;
			}
		}

		// 检查远程是否存在文件
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes(Encoding), ENCODING_FTP));
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if (remoteSize == localSize) {
				return UploadStatus.File_Exits;
			} else if (remoteSize > localSize) {
				return UploadStatus.Remote_Bigger_Local;
			}

			// 尝试移动文件内读取指针,实现断点续传
			result = uploadFile(remoteFileName, f, ftpClient, remoteSize);

			// 如果断点续传没有成功，则删除服务器上文件，重新上传
			if (result == UploadStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return UploadStatus.Delete_Remote_Faild;
				}
				result = uploadFile(remoteFileName, f, ftpClient, 0);
			}
		} else {
			result = uploadFile(remoteFileName, new File(local), ftpClient, 0);
		}

		return result;
	}

	/**
	 * 中断下载的标志位
	 */
	public void interrupt() {
		hasStop = true;
	}

	/**
	 * 断开与远程服务器的连接
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		LogUtil.w(tag, "----disconnect isConnected:" + ftpClient.isConnected());
		try {
			ftpClient.disconnect();
		} catch (Exception e) {
			LogUtil.w(tag, "disconnect", e);
		}
	}

	/**
	 * 递归创建远程服务器目录
	 * 
	 * @param remote
	 *          远程服务器文件绝对路径
	 * @param ftpClient
	 *          FTPClient对象
	 * @return 目录创建是否成功
	 * @throws IOException
	 */
	public UploadStatus CreateDirecroty(String remote, FTPClient ftpClient) throws IOException {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!directory.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(new String(directory.getBytes(Encoding), ENCODING_FTP))) {
			// 如果远程目录不存在，则递归创建远程服务器目录
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes(Encoding), ENCODING_FTP);
				if (!ftpClient.changeWorkingDirectory(subDirectory)) {
					if (ftpClient.makeDirectory(subDirectory)) {
						ftpClient.changeWorkingDirectory(subDirectory);
					} else {
						System.out.println("创建目录失败");
						return UploadStatus.Create_Directory_Fail;
					}
				}

				start = end + 1;
				end = directory.indexOf("/", start);

				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return status;
	}

	/**
	 * 上传文件到服务器,新上传和断点续传
	 * 
	 * @param remoteFile
	 *          远程文件名，在上传之前已经将服务器工作目录做了改变
	 * @param localFile
	 *          本地文件File句柄，绝对路径
	 * @param ftpClient
	 *          FTPClient引用
	 * @return
	 * @throws IOException
	 */
	private UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize)
			throws IOException {

		hasStop = false;

		UploadStatus status;
		// 显示进度的上传
		long step = localFile.length() / 100;
		long process = 0;
		long localreadbytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes(Encoding), ENCODING_FTP));
		// 断点续传
		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localreadbytes = remoteSize;
		}
		byte[] bytes = new byte[BUFFER_SIZE];
		int c;
		long lastTime = System.currentTimeMillis();
		try {
			while ((c = raf.read(bytes)) != -1 && !hasStop) {
				out.write(bytes, 0, c);
				localreadbytes += c;

				if (localreadbytes / step != process) {
					process = localreadbytes / step;
				}
				if (mProgressChangeListener != null) {
					if (System.currentTimeMillis() - lastTime > 500) {
						LogUtil.i(tag, "upload progress:" + process + "," + localreadbytes + "/" + localFile.length());
						mProgressChangeListener.onProgressChange(localreadbytes, localFile.length());
						lastTime = System.currentTimeMillis();
					}
				}
			}
			out.flush();
		} catch (Exception err) {
			LogUtil.e(tag, err.getMessage());
		} finally {
			if (raf != null) {
				raf.close();
				raf = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
		}
		boolean result = ftpClient.completePendingCommand();

		if (hasStop) {
			status = UploadStatus.Upload_Interrupted;
		} else {

			if (remoteSize > 0) {
				status = result ? UploadStatus.Upload_From_Break_Success : UploadStatus.Upload_From_Break_Failed;
			} else {
				status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
			}
		}

		return status;
	}

	/**
	 * 函数功能：边压缩边上传文本文件
	 * 
	 * @param remoteFile
	 * @param localFile
	 * @param ftpClient
	 * @return
	 * @throws IOException
	 */
	public UploadStatus uploadTextFileAndZip(String remoteFile, File localFile, FTPClient ftpClient) throws IOException {

		hasStop = false;

		UploadStatus status;
		// 显示进度的上传
		long step = localFile.length() / 100;
		long process = 0;
		long localreadbytes = 0L;

		// RandomAccessFile raf = new RandomAccessFile(localFile,"r");
		OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes(Encoding), ENCODING_FTP));

		final int Buffer_Size = 64 * 1000;

		InputStream in = new FileInputStream(localFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		ArrayList<Byte> buffer = new ArrayList<Byte>();
		long lastTime = System.currentTimeMillis();

		while ((line = br.readLine()) != null && !hasStop) {
			byte[] lineBytes = line.getBytes();
			for (byte b : lineBytes) {
				buffer.add(b);
			}
			if (buffer.size() >= Buffer_Size) {
				byte[] zipBytes = compress(getByteArray(buffer));
				out.write(zipBytes);
				buffer.clear();

				localreadbytes += zipBytes.length;
				if (localreadbytes / step != process) {
					process = localreadbytes / step;
				}
				if (mProgressChangeListener != null) {
					if (System.currentTimeMillis() - lastTime > 1000) {
						LogUtil.i(tag, "upload progress:" + process + "," + localreadbytes + "/" + localFile.length());
						mProgressChangeListener.onProgressChange(localreadbytes, localFile.length());
						lastTime = System.currentTimeMillis();
					}
				}
			}
		}

		out.flush();
		in.close();
		out.close();
		boolean result = ftpClient.completePendingCommand();
		if (hasStop) {
			status = UploadStatus.Upload_Interrupted;
		} else {
			status = result ? UploadStatus.Upload_New_File_Success : UploadStatus.Upload_New_File_Failed;
		}
		return status;
	}

	public byte[] getByteArray(ArrayList<Byte> byteArray) {
		byte[] result = new byte[byteArray.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = byteArray.get(i).byteValue();
		}
		return result;
	}

	// /**
	// * 发送当前进度的广播
	// **/
	// private void sendProgressBroadcast( String fileName, int progress ) {
	// Intent intentProgress = new Intent();
	// String text = mContext.getString(R.string.str_uploading)
	// + fileName + "\n" + progress
	// + "%";
	// intentProgress.putExtra(ServerMessage.KEY_MSG, text );
	// intentProgress.setAction(ServerMessage.ACTION_FLEET_PROGRESS);
	// mContext.sendBroadcast(intentProgress);
	// }

	/**
	 * 直接获取文件列表先
	 * 
	 * @param
	 */ 
	public FTPFile[] getFTPLists(String remotePath, FtpServerModel ftpServerModel) throws Exception {
		try {
			this.openConnect(ftpServerModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try { 
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setDataTimeout(15000);
			ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(Encoding), ENCODING_FTP));
			Log.i("----------", ftpClient.printWorkingDirectory());
			FTPFile[] files = ftpClient.listFiles();
			boolean flag = false;
			for (FTPFile ff : files) {
				if (StringUtil.isMessyCode(ff.getName())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				ftpClient.setControlEncoding("GB2312");
				files = ftpClient.listFiles();
			} 
			return files;
		} catch (Exception e) {
			throw new Exception(e.toString());

		}
	}
    

	public interface RemotePathListener {
		void getRemotePath(String path);
	}

	// def "2f 39 32 32 38 e2 a2 8a d4 87 e9 bf b6 2f " (id=830048630376)
	// utf8 "2f 39 32 32 38 e2 a2 8a d4 87 e9 bf b6 2f " (id=830041630680)
	// gbk "2f 39 32 32 38 3f 3f 3f 2f " (id=830048050192)
	// iso "2f 39 32 32 38 3f 3f 3f 2f " (id=830041006152)

	// utf8 "2f 39 32 32 38 e6 b5 8b e8 af 95 e6 83 85 e5 86 b5 2f "
	// (id=830053376832)
	// gbk "2f 39 32 32 38 b2 e2 ca d4 c7 e9 bf f6 2f " (id=830055704832)
	// iso "2f 39 32 32 38 3f 3f 3f 3f 2f " (id=830042352032)

	/**
	 * 
	 * 判断是否ftp连接状态
	 */
	public boolean isConnect() {

		if (ftpClient != null) {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.setSoTimeout(15000);
					ftpClient.getStatus();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (ftpClient.getReplyCode() == 421) { // 这里判断服务器闲置超过3分钟，将重新连接
					return false;
				}
			}
			return ftpClient.isConnected();
		}

		return false;

	}

	/**
	 * 打开连接
	 * 
	 * @return boolean
	 */
	private boolean openConnect(FtpServerModel ftpServerModel) {
		boolean flag = false;
		try {
			if (!this.isConnect()) {
				flag = this.connect(ftpServerModel);
				if (flag == false) {
					return flag;
				}
				System.out.println("连接服务器成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public interface OnProgressChangeListener {
		void onProgressChange(long currentSize, long totalSize);
	}

	public void setOnProgressChangeListener(OnProgressChangeListener listener) {
		this.mProgressChangeListener = listener;
	}

	public static byte[] compress(byte[] data) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

		deflater.finish();
		byte[] buffer = new byte[8192];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer); // returns the generated
			// code... index
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		byte[] output = outputStream.toByteArray();

		return output;
	}

	/**
	 * 泰和服务器图片资源下载
	 * 
	 * @param remote
	 *          远程文件路径
	 * @param local
	 *          本地文件路径
	 */
	public DownloadStatus singerDownload(String remote, String local) throws IOException {
		// 设置主动模式
		ftpClient.setControlEncoding("utf-8");
		ftpClient.setSoTimeout(PIC_FTP_TIMEOUT);
		ftpClient.setDataTimeout(PIC_FTP_TIMEOUT);
		ftpClient.enterLocalActiveMode();
		// 设置以二进制方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		DownloadStatus result = null;
		fos = new FileOutputStream(creatFile(local)); // 指定下载到本地的地址和生成的新的图片名字
		try {
			// 处理传输
			boolean isSuccess = ftpClient.retrieveFile(new String(remote.getBytes("utf-8"), "iso-8859-1"), fos);
			if (isSuccess) {
				result = DownloadStatus.Download_New_Success;
			} else {
				result = DownloadStatus.Download_New_Failed;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fos.close();
			if (result.equals(DownloadStatus.Download_New_Failed)) {
				new File(local).delete();
			}

		}

		return result;
	}

	/**
	 * 远程服务器重命名
	 * 
	 * @param oldName 传递原始文件名
	 * @throws IOException
	 */
	public void renameFileName(String oldName, String newName) throws IOException {
		ftpClient.rename(oldName, newName);
	}

	/**
	 * 主要功能当传入的文件路径不存在时,自动迭代创建文件目录及其文件
	 * 
	 * @param filePath 传入文件路径
	 * @return File
	 */
	public File creatFile(String filePath) {
		String[] fileArray = filePath.split("/");
		String filePathTemp = "";
		for (int i = 0; i < fileArray.length - 1; i++) {
			filePathTemp += fileArray[i];
			filePathTemp += File.separator;
		}
		filePathTemp += fileArray[fileArray.length - 1];
		if (filePathTemp.equals("")) {
			filePathTemp = filePath;
		}
		File f = new File(filePathTemp);
		File pf = f.getParentFile();
		if (null != pf && !pf.exists()) {
			pf.mkdirs();
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return f;
		}
		return f;
	}

	/*
	 * public static void main(String[] args) { FtpOperate myFtp = new
	 * FtpOperate(); try { System.out.println("longin == "
	 * +myFtp.connect("61.143.60.83", 21, "twq", "twq"));
	 * //myFtp.ftpClient.makeDirectory(new String("电视剧".getBytes(),FTP_ENCODING));
	 * //myFtp.ftpClient.changeWorkingDirectory(new
	 * String("电视剧".getBytes(),FTP_ENCODING)); //myFtp.ftpClient.makeDirectory(new
	 * String("走西口".getBytes(),FTP_ENCODING));
	 * //System.out.println(myFtp.upload("E:\\yw.flv", "/yw.flv",5)); //System.out
	 * .println(myFtp.upload("E:\\Download\\高焕堂第4本Android书籍第1-5章.rar"
	 * ,"/高焕堂第4本Android书籍第1-5章.rar"));
	 * System.out.println(myFtp.download("/zzggs.mp3", "E:\\zzggs.mp3"));
	 * myFtp.disconnect(); } catch (IOException e) {
	 * System.out.println("连接FTP出错："+e.getMessage()); } }
	 */
}

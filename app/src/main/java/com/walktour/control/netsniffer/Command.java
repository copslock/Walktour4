package com.walktour.control.netsniffer;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;

import java.io.DataOutputStream;
import java.io.InputStream;

/**
 * 文件名: Command.java<BR>
 * 描 述: 命令封装与执行<BR>
 * 创建人: 黄广府<BR>
 * 创建时间:2012-8-29<BR>
 * 
 * 修改人：<BR>
 * 修改时间:<BR>
 * 修改内容：[修改内容]<BR>
 */
public class Command {

	private String TAG = "Command";

	private String cmd;

	/**
	 * 是否Root
	 */
	private boolean root = false;

	/**
	 * 底层反馈信息
	 */
	private String info = null;

	/**
	 * 底层反馈错误信息
	 */
	private String error = null;

	/**
	 * 是否具备Root权限
	 */
	public static boolean hasRootPermition = false;

	static {
		hasRootPermition = checkRootPermition();
	}

	public Command(String cmd) {
		this(cmd, false);
	}

	public Command(String cmd, boolean root) {
		this.cmd = cmd;
		this.root = root;
	}

	/**
	 * 检测手机是否已经Root<BR>
	 * 发送SU命令检查程序是否具备Root权限
	 * 
	 * @return 是否Root
	 */
	public static boolean checkRootPermition() {
		return new Command(Deviceinfo.getInstance().getSuOrShCommand()).exec();
	}

	/**
	 * 执行需要root权限的名令，并等待返回结果<BR>
	 * 
	 * @param cmd
	 *          命令参数
	 * @return
	 */
	public static boolean sudo(String cmd) {
		return exec(cmd, true);
	}

	/**
	 * 执行不需要root权限的名令，并等待返回结果
	 * 
	 * @param cmd
	 * @return
	 */
	public static boolean exec(String cmd) {
		return exec(cmd, false);
	}

	public static boolean exec(String cmd, boolean root) {
		Command command = new Command(cmd);
		command.root = root;
		return command.exec();
	}

	/**
	 * 执行命令BR>
	 * 
	 * @return 执行结果
	 */
	public boolean exec() {
		LogUtil.d(TAG, cmd);
		Process process = null;
		DataOutputStream os = null;
		boolean result = true;
		try {
			if (root) {
				process = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
				os = new DataOutputStream(process.getOutputStream());
				os.writeBytes(cmd + "\n");
			} else {
				process = Runtime.getRuntime().exec(cmd);
				os = new DataOutputStream(process.getOutputStream());
			}
			os.writeBytes("exit\n");
			os.flush();

			doWaitFor(process);
			if (!error.replace(" ", "").replace("\n", "").equals("")) {
				result = false;
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "Unexpected error - Here is what I know: " + e.getMessage());
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				// process.destroy();
				process = null;
			} catch (Exception e) {
				// nothing
			}
		}
		LogUtil.i(TAG, "feedback(normal): " + info);
		LogUtil.e(TAG, "feedback(error): " + error);
		return result;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public boolean isRoot() {
		return root;
	}

	/**
	 * 设置是否使用root权限
	 * 
	 * @param root
	 */
	public void setRoot(boolean root) {
		this.root = root;
	}

	/**
	 * 获取正常的返回信息
	 * 
	 * @return
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * 获取错误返回信息
	 * 
	 * @return
	 */
	public String getError() {
		return error;
	}

	/**
	 * 对process.waitFor()的改造，被启动的进程会因为缓冲区不够而被阻塞无法启动，调用该方法可以成功
	 * 
	 * @param p
	 * @return
	 */
	private int doWaitFor(Process p) {
		int exitValue = -1; // returned to caller when p is finished
		try {
			InputStream in = p.getInputStream();
			InputStream err = p.getErrorStream();
			boolean finished = false; // Set to true when p is finished
			while (!finished) {
				try {
					StringBuffer infoBuffer = new StringBuffer();
					while (in.available() > 0) {
						// Print the output of our system call
						Character c = Character.valueOf((char) in.read());
						infoBuffer.append(c);
					}
					info = infoBuffer.toString();

					StringBuffer errorBuffer = new StringBuffer();
					while (err.available() > 0) {
						// Print the output of our system call
						Character c = Character.valueOf((char) err.read());
						errorBuffer.append(c);
					}
					error = errorBuffer.toString();

					// Ask the process for its exitValue. If the process
					// is not finished, an IllegalThreadStateException
					// is thrown. If it is finished, we fall through and
					// the variable finished is set to true.
					exitValue = p.exitValue();
					finished = true;
				} catch (IllegalThreadStateException e) {
					// Process is not finished yet;
					// Sleep a little to save on CPU cycles
					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			// unexpected exception! print it out for debugging...
			System.err.println("doWaitFor(): unexpected exception - " + e.getMessage());
		}
		// return completion status to caller
		return exitValue;
	}

}

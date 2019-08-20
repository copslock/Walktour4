/**
 * 
 */
package com.walktour.gui.locknet;

import android.os.AsyncTask;
import android.os.Handler;

import com.walktour.base.util.LogUtil;
import com.ylb.engineeringMode.DiagInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 希母通手机的网络锁定异步任务
 * @author XieJihong
 *
 */
public class SimLockTask extends AsyncTask<String, Void, Boolean> {
	private static final String TAG = "LockNetWorkTask";
	public static final int OPT_SEARCH = 1;
	public static final int OPT_LOCK = 0;
	private boolean isReturn = false;
	/**
	 * 任务完成后监听
	 * @author XieJihong
	 *
	 */
	public interface TaskFinishedListener {
		
		/**
		 * 完成后触发
		 * @param result
		 * @param resultContent
		 */
		void onFinished(Boolean result, String resultContent, int opt);
	}
	
	private TaskFinishedListener listener;
	private String content;
	private DiagInterface ai;
	private int opt;
	
	public SimLockTask(DiagInterface ai, 
			TaskFinishedListener listener, 
			int opt) {
		this.listener = listener;
		this.ai = ai;
		this.opt = opt;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(String... arg0) {
		//如果15秒内没有读取到内容，则请求取消线程
		handler.sendEmptyMessageDelayed(0, 15000);
		boolean flag = false;
		try {
			LogUtil.w(TAG, "run lock cmd=" + arg0[0]);
			this.ai.SendAtCmd(arg0[0]);
			StringBuffer strBuf = new StringBuffer();
			content = readReadAtEcho(strBuf);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		if(!isReturn){
			isReturn = true;
			LogUtil.w(TAG, "run lock result=" + content);
			this.listener.onFinished(result, content, this.opt);
		}
	}
	
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			if(!isReturn){
				isReturn = true;
				LogUtil.w(TAG, "beyoned 15 second no response thread will be cancel");
				result = true;
				SimLockTask.this.cancel(true);
				SimLockTask.this.listener.onFinished(false, content, opt);
			}
		};
	};
	
	String resultContent;
	String regEx = "ok|error"; 
	boolean result;
	private String readReadAtEcho(StringBuffer strBuf){
		byte[] buf = new byte[1024];
		this.ai.SimReadAtEcho(buf, 1024);
		resultContent = new String(buf);
		resultContent = Pattern.compile("\u0000").matcher(resultContent).replaceAll("");
		
		strBuf.append(resultContent);
		
		//正则匹配忽略大小写
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(resultContent);
		result = m.find();
		resultContent = null;
		if(!result){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			readReadAtEcho(strBuf);
		}
		return strBuf.toString();
	}
}

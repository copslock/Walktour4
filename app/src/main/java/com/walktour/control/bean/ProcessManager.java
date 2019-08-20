package com.walktour.control.bean;

import android.content.Context;

import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * */
public class ProcessManager {
	private final String tag ="ProcessKiller";
	private Context mContext;
	
	private  ProcessManager(Context context){
		this.mContext = context;
	}
	
	public static ProcessManager getManager(Context context){
		ProcessManager manager = new ProcessManager(context);
		return manager;
	}
	
	
	/**
	 * 执行kill方法
	 */
	public void killProcessByName(String processName){
		
		//运行PS命令获取所有进程信息
		List<String> process = getAllProcess();
		//程序包名
		String pckName = mContext.getPackageName();
		//以进程类包装的所有进程信息
		ArrayList<ProcessItem> processItems = getProcessInfo(process);
		//本应用程序用户
		String username = getAppUser(processItems,pckName);
		//本应用程序所有进程
		List<ProcessItem> localProcess = getLocalProcess(processItems,username);
		try {
			for(ProcessItem item:localProcess){
				LogUtil.w(tag, "item.pid:"+item.pid+",item.name:"+item.name);
				if( item.name.equalsIgnoreCase( processName ) ){
					android.os.Process.sendSignal( Integer.valueOf(item.pid), android.os.Process.SIGNAL_KILL );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 运行ps命令获取所有进程信息
	 * @return
	 */
	private List<String> getAllProcess(){
		Process process = null;
		List<String> pros = new ArrayList<String>();
		try {
			process = Runtime.getRuntime().exec("ps");
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while((line = reader.readLine())!= null){
				pros.add(line);
			}
		} catch (Exception e) {
			// TODO: handle exception
			
		}finally{
			if(process != null){
				process.destroy();
			}
		}
		return pros;
	}
	
	/**
	 * 以进程类包装的进程信息
	 * @param process 运行ps命令获取的所有进程信息
	 * @return
	 */
	private ArrayList<ProcessItem> getProcessInfo(List<String> process){
		ArrayList<ProcessItem> processInfo = new ArrayList<ProcessItem>();
		for(String s:process){
			//LogUtil.w(tag, s);
			String[] items = s.split(" ");
			List<String> list = new ArrayList<String>();
			for(int i = 0;i<items.length;i++){
				if(!items[i].equals("")){
					list.add(items[i]);
				}
			}
			if(list.size() == 9){
				ProcessItem item = new ProcessItem();
				item.user = list.get(0);
				item.pid =  list.get(1); 
				item.name = list.get(8);
				processInfo.add(item);
			}else if(list.size() == 5){
				ProcessItem pi = new ProcessItem();
				pi.user = list.get(1);
				pi.pid =  list.get(0);
				pi.name = list.get(4);
				processInfo.add(pi);
			}
		}
		return processInfo;
	}
	
	/**
	 * 获取本应用程序用户名
	 * @param processItems 进程信息
	 * @param pckName 程序包名
	 * @return
	 */
	private String getAppUser(ArrayList<ProcessItem> processItems,String pckName){
		for(ProcessItem item:processItems){
			if(item.name.equalsIgnoreCase(pckName)){
				return item.user;
			}
		}
		return null;
	}
	
	/**
	 * 获取本应用程序所有进程
	 * @param allProcess 所有进程
	 * @param username   本应用程序用户名
	 * @return
	 */
	private List<ProcessItem> getLocalProcess(ArrayList<ProcessItem> allProcess,String username){
		List<ProcessItem> localProcess = new ArrayList<ProcessItem>();
		for(ProcessItem item:allProcess){
			if(item.user.equals(username)){
				localProcess.add(item);
			}
		}
		return localProcess;
		
	}
	
	/**
	 * 进程信息类
	 * @author Administrator
	 *name 进程名字 
	 *user 进程用户名
	 *pid  进程pid
	 */
	class ProcessItem{
		public String name;
		public String user;
		public String pid;
		
	}
}

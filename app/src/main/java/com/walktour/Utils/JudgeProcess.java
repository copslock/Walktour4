package com.walktour.Utils;

import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 判断某个进程是否已经存在的工具类
 * @author li.bie
 *
 */
public class JudgeProcess {
  public boolean isRunning(String processName){
    	//运行PS命令获取所有进程信息
		List<String> process = getAllProcess();
		//以进程类包装的所有进程信息
		ArrayList<ProcessItem> processItems = getProcessInfo(process);
		for(ProcessItem item:processItems){
//				LogUtil.w(tag, "---uyou item.name="+item.name);
			if(item.name.equals(processName)){
				return true;
			}
		}
    	return false;
    }
	
  
  public static boolean isRunnServer(String processName){
  	//运行PS命令获取所有进程信息
		List<String> process = getAllProcess();
		//以进程类包装的所有进程信息
		ArrayList<ProcessItem> processItems = getProcessInfo(process);
		for(ProcessItem item:processItems){
//				LogUtil.w(tag, "---uyou item.name="+item.name);
			if(item.name.contains(processName)){
				return true;
			}
		}
  	return false;
  }
	
  
  	/**
	 * 运行ps命令获取所有进程信息
	 * @return
	 */
	private static  List<String> getAllProcess(){
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
			LogUtil.w("getAllProcess1",e.getMessage());
		}finally{
			if(process != null){
				try {
					process.destroy();
				}catch(Exception ex){
					LogUtil.w("getAllProcess1",ex.getMessage());
				}finally {
					process=null;
				}
			}
		}
		return pros;
	}
	
	/**
	 * 以进程类包装的进程信息
	 * @param process 运行ps命令获取的所有进程信息
	 * @return
	 */
	private static ArrayList<ProcessItem> getProcessInfo(List<String> process){
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
			}
		}
		return processInfo;
	}
	
	/**
	 * 进程信息类
	 * @author Administrator
	 *name 进程名字 
	 *user 进程用户名
	 *pid  进程pid
	 */
	static class ProcessItem{
		public String name;
		public String user;
		public String pid;
		
	}
}

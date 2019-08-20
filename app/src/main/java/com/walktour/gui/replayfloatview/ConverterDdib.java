package com.walktour.gui.replayfloatview;

import android.app.Activity;
import android.content.Context;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.ActivityManager;
import com.walktour.gui.R;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * FMT转换DDIB操作类
 * @author zhihui.lian
 */
public class ConverterDdib {

	private Context mContext;
	
	private static ConverterDdib mConverterDdib;
	
	private static Vector<ConverterDdibI> observerList;
	
	public ConverterDdib(Context mContext) {
		this.mContext = mContext;
	}
	
	public static ConverterDdib getInstance(Context context) {
		if (mConverterDdib == null) {
			mConverterDdib = new ConverterDdib(context);
			observerList = new Vector<ConverterDdibI>();
		}
		return mConverterDdib;
	}
	
	/**
	 * 执行转换操作
	 * @param filePath
	 */
	public void doConverterDdib(final String filePath){
		new Thread(new Runnable() {
			private int isSuccess;
			@Override
			public void run() {
				try {
					if(filePath.lastIndexOf(".FMT") == -1 && filePath.lastIndexOf(".rcu") == -1&& filePath.lastIndexOf(".cu") == -1){
						replayDdibFile(filePath);
					}else{
						String desPath = AppFilePathUtil.getInstance().getSDCardBaseDirectory(mContext.getString(R.string.path_data), mContext.getString(R.string.path_ddib));
						
						isSuccess = DatasetManager.getInstance(mContext).fileConverterDdib(filePath,desPath);
						filterFileDdibPath(filePath,desPath);
					}
					
					for (int i = 0; i < observerList.size(); i++) {
						observerList.get(i).doFinish(isSuccess);
					}
				} catch (Exception e) {
					e.printStackTrace();
					for (int i = 0; i < observerList.size(); i++) {
						observerList.get(i).doFinish(-1);
					}
				}
			}
		}).start();
	}
	
	
	/**
	 * 扫描转换生成目录，筛选转换得到的Ddib大文件进行回放，以后做成列表选择就不需要此操作
	 */
	private void filterFileDdibPath(String filePath,String desPath){
		File file = new File(filePath);
		List<File> files = Arrays.asList(new File(desPath).listFiles());
		 Collections.sort(files, new Comparator< File>() {
		     public int compare(File f1, File f2) {
			long diff = f2.length() - f1.length();
			if (diff > 0)
			  return 1;
			else if (diff == 0)
			  return 0;
			else
			  return -1;
		     }
		    public boolean equals(Object obj) {
			return true;
		    }
		   });
		   for (File f : files) {
		      if(f.isDirectory()) continue;
		      if(f.getName().startsWith(file.getName().substring(0,file.getName().lastIndexOf(".")) + "_Port")){
		    	  for (int i = 0; i < observerList.size(); i++) {
						observerList.get(i).desDdibPath(f.getAbsolutePath());
					}
		    	  break;
		      }
		   }
	}
	
	
	/**
	 * 选择回放文件直接回放
	 * @author zhihui.lian
	 */
	private void replayDdibFile(String filePath){
		for (int i = 0; i < observerList.size(); i++) {
			observerList.get(i).desDdibPath(filePath);
		}
	}
	
	
	

	public interface ConverterDdibI {
		
		void doFinish(int isSuccess);

		void desDdibPath(String path);
		
	}
	
	public synchronized void registerObserver(ConverterDdibI observer) {
		synchronized(observerList) {
			observerList.add(observer);
		}
	}
	public synchronized void removeObserver(ConverterDdibI observer) {
		observerList.remove(observer);
	}
	
	
	/**
	 * 弹出框销毁
	 */
	public void dissView(){
		List<Activity> activities = ActivityManager.getActivities();
		for (int i =  activities.size() - 1; i >= 0; i--) {
			if(activities.get(i).getLocalClassName().equals("replayfloatview.ShowDialogTip")){
				Activity activity = activities.get(i);
				if (!(activity.isChild() || activity.isFinishing()))
					activity.finish();
				break;
			}

		}
	}
		
	
}

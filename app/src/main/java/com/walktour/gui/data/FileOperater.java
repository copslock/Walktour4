package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.content.Context;

import com.dinglicom.dataset.TotalInterface;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.applet.ClipExplorer;
import com.walktour.gui.data.model.DBManager;
import com.walktour.gui.data.model.DataModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作
 * @author haohua
 *
 */
public class FileOperater {
	
	private Context mContext;
	
	public FileOperater(Context context) {
		this.mContext = context;
	}
	
	
	/**
	 * 删除单个文件
	 * @param file
	 */
	public void delete(DataModel file) {
		ArrayList<DataModel> list = new ArrayList<DataModel>();
		list.add(file);
		delete(list);
	}
	
	/**
	 * 删除多个文件
	 * @param fileList
	 */
	public void delete(ArrayList<DataModel> fileList) {
		new Thread(new Cleaner(fileList)).start();
	}
	
	/**
	 * 复制单个文件
	 * @param file
	 */
	public void copyFile(DataModel file) {
		List<DataModel> files = new ArrayList<DataModel>();
		files.add(file);
		copyFiles(files);
	}
	
	/**
	 * 复制多个文件
	 * @param fileList
	 */
	public void copyFiles(List<DataModel> fileList) {
		List<String> filePaths = new ArrayList<String>();
		for (DataModel d : fileList) {
			filePaths.addAll(d.getAllFilePath());
		}
		String[] copyFiles = new String[filePaths.size()];
		filePaths.toArray(copyFiles);//正确数据
		new ClipExplorer(mContext, DataModel.FILE_INCONTROL, copyFiles, false).start();
	}
	
	/**
	 * 移动单个文件
	 * @param file
	 */
	public void moveFile(DataModel file) {
		List<DataModel> files = new ArrayList<DataModel>();
		files.add(file);
		moveFiles(files);
	}
	
	/**
	 * 移动多个文件
	 * @param fileList
	 */
	public void moveFiles(List<DataModel> fileList) {
		List<String> filePaths = new ArrayList<String>();
		
		for (DataModel d : fileList) {
			filePaths.addAll(d.getAllFilePath());
		}
		String[] copyFiles = new String[filePaths.size()];
		filePaths.toArray(copyFiles);//正确数据
		new ClipExplorer(mContext, DataModel.FILE_INCONTROL, copyFiles, true).start();
	}
	

	/**
	 * 文件删除线程
	 * */
	@SuppressLint("DefaultLocale")
	private class Cleaner implements Runnable {
		
		ArrayList<DataModel> mDataModelList = null;
		
		public Cleaner(ArrayList<DataModel> fileList) {
			this.mDataModelList = fileList;
		}

		@Override
		public void run() {
			for (DataModel d : mDataModelList) {
				if (d.testRecord != null) {
					ArrayList<String> filePaths = d.getAllFilePath();
					for (String path : filePaths) {
						if(path.toLowerCase().endsWith(".ddib")){
							LogUtil.i(getClass().toString(), "delete uk " + path);
							deleteUk(path);
						}
						File file = new File(path);
						file.delete();
					}
				}
			}
			DBManager.getInstance(mContext).deleteFiles(mDataModelList);
		}
		
	}
	
	
	/**
	 * 删除关联UK
	 * @param ddibPath
	 */
	public void deleteUk(String ddibPath){
		TotalInterface.getInstance(mContext).deleteUk(ddibPath);
	}
	
	
}

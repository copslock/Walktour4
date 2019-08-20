package com.walktour.control.bean;

import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileOperater{
	
	/**
	 * 复制文件
	 * @param filePath 原文件路径 
	 * @param desFilePath 目标文件路径
	 * */
	public void copyFileInThread(String filePath ,String desFilePath){
		new Thread( new Copy(filePath,desFilePath) ).start();
	}
	
	/**
	 * 移动文件
	 * @param filePath 原文件路径 
	 * @param desFilePath 目标文件路径
	 * */
	public void moveFileInThread(String filePath ,String desFilePath){
		new Thread( new Move(filePath,desFilePath) ).start();
	}
	
	private class Copy implements Runnable{
		private String filePath = "";
		private String desFilePath = "";
		
		public Copy(String filePath ,String desFilePath){
			this.filePath = filePath;
			this.desFilePath = desFilePath;
		}
		
		@Override
		public void run() {
			copy(filePath,desFilePath);
		}
	}
	
	private class Move implements Runnable{
		private String filePath = "";
		private String desFilePath = "";
		
		public Move(String filePath ,String desFilePath){
			this.filePath = filePath;
			this.desFilePath = desFilePath;
		}
		
		@Override
		public void run() {
			move(filePath,desFilePath);
		}
	}
	
	/**
     * 复制文件，目标文件如果存在先删除<BR>
     * [功能详细描述]
     * @param filePath
     * @param desFilePath
     */
    public void overCopy(String filePath ,String desFilePath){
        //建立目标文件
        File outFile = new File( desFilePath );
        if( outFile.exists() ){
            outFile.delete();
        }
        
        //目录不存在时创建
        if( !outFile.getParentFile().exists() ){
        	outFile.getParentFile().mkdirs();
        }
        
        copy(filePath,desFilePath);
    }
    
    /**
	 * 复制文件，文件如果存在不覆盖
	 * */
	public void copy(String filePath ,String desFilePath){
		//建立目标文件
		File outFile = new File( desFilePath );
		if( outFile.exists() ){
			//do nothing
		}else{
			try {
				OutputStream  out = new FileOutputStream(outFile);
				//读取asset文件
				try {
					InputStream  in  = new  FileInputStream(filePath);
					byte[] buf = new byte[1024];    
			        int len;    
			        while ((len = in.read(buf)) > 0)    
			        {    
			            out.write(buf, 0, len);    
			        } 
			        
			        in.close();
			        out.close();
			        
				} catch (IOException e) {
					LogUtil.w("FileOperater", "IOException");
					e.printStackTrace();
					outFile = new File( desFilePath );
					if( outFile.exists() ){
						outFile.delete();
					}
				}
				
			} catch (FileNotFoundException e1) {
				LogUtil.w("FileOperater", "FileNotFoundException");
				e1.printStackTrace();
			}
		}
	}
	
	public void move(String filePath ,String desFilePath){
		copy(filePath,desFilePath);
		File file = new File(filePath);
		if( file.exists() ){
			file.delete();
		}
	}
	
	/**
	 * 创建一个示例文件
	 * @param filePath	文件的路径
	 * @param fileSize 	文件的大小
	 */
	public static void createTempFile(String filePath ,int fileSize){
		File file = new File(filePath);
		if(file.exists()){
			return;
		}
		FileOutputStream out = null;
		try {
			file.createNewFile();
			
			out = new FileOutputStream(file);
			byte[] data = new byte[8*1024];
			
			while(fileSize>0){
				out.write(data, 0, Math.min(fileSize, data.length));
				fileSize -= Math.min(fileSize, data.length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 自动迭代创建文件目录
	 * @param filePath
	 * @return
	 */
	public static File creatFile(String filePath) {
		  String[] fileArray = filePath.split("/");
		  String filePathTemp=""; 
		  for (int i = 0; i < fileArray.length-1; i++) {
		   filePathTemp += fileArray[i];
		   filePathTemp += File.separator;
		  }
		  filePathTemp += fileArray[fileArray.length-1];
		  if(filePathTemp.equals("")){
		   filePathTemp=filePath;
		  }
		  File f = new File(filePathTemp);
		  File pf = f.getParentFile();
		  if (null !=pf && !pf.exists()) {
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
	
	/**
	 * 从一个文本文件读取
	 * @param file
	 * @return
	 */
	public static StringBuffer getTxtFromFile(File file){
		StringBuffer result = new StringBuffer(); 
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		 BufferedReader	br = new BufferedReader(  new InputStreamReader(inStream));
		 String line;
		 try {
			while( (line=br.readLine()) != null ){
				result.append(line);
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 return result;
	}
	
}
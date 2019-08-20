package com.walktour.control.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author qihang.li@dinglicom.com
 * @descrip InputStream和String的转换器,实现两者之间的转换 
 */


public class StreamConventor {
	private static InputStream stream;	
	
    /**
     * 
     * @param inStream 要转换的InputStream
     */
	public StreamConventor(InputStream inStream){
		stream = inStream;
	}//end constructor
	
	/**
	 * 
	 * @return 转换后的字符串
	 */
	public String getString(){
		BufferedReader reader = new BufferedReader( new InputStreamReader(stream));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try{			
			while( (line=reader.readLine()) != null){
				sb.append(line+"\n");				
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				stream.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}//end try&catch		
		return sb.toString();
	}//end method getString
	
}
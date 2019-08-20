package com.walktour.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @version 1.0
 * @created 29-九月-2010 11:35:54
 */
public class NDiskFile implements NFile {
	
	private FileInputStream m_fileReader;
	private FileOutputStream m_fileWriter;
	private boolean m_isLoading;
	private boolean m_isOpened = false;
	private String m_fileName;
	
	public NDiskFile(String fileName,boolean isload){
		m_isLoading = isload;
		m_fileName = fileName;
	}
	@Override
	public boolean open(){
		if(m_isLoading){
			try {
				m_fileReader = new FileInputStream(m_fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			}
		}
		else{
			try {
				m_fileWriter = new FileOutputStream(m_fileName,false);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		m_isOpened = true;
		return true;
	}
	@Override
	public synchronized int read(byte[] buf, int len){
		if(!m_isOpened||!m_isLoading)
			return 0;
		
		try {
			return m_fileReader.read(buf,0,len);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	public synchronized int read(byte[] buf, int offset,int len){
		if(!m_isOpened||!m_isLoading)
			return 0;
		
		try {
			return m_fileReader.read(buf,offset,len);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public synchronized int write(byte[] buf, int len){
		if(!m_isOpened||m_isLoading)
			return 0;
		
		try{
			m_fileWriter.write(buf,0,len);
			return len;
		}catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public synchronized void flush(){
		if(!m_isOpened||m_isLoading)
			return;
		try {
			m_fileWriter.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return; 
		}
	}
	@Override
	public synchronized void seek(int offset, int from){
		if(!m_isOpened)
			return;
		
		try{
			long newPos = 0;
			if(m_isLoading){	
				if(from == SEEK_SET){
					newPos = offset;
				}
				else if(from == SEEK_END){
					newPos = offset + m_fileReader.getChannel().size();
				}
				else{
					newPos = offset + m_fileReader.getChannel().position();
				}
				m_fileReader.getChannel().position(newPos);
			}
			else{
				if(from == SEEK_SET){
					newPos = offset;
				}
				else if(from == SEEK_END){
					newPos = offset + m_fileWriter.getChannel().size();
				}
				else{
					newPos = offset + m_fileWriter.getChannel().position();
				}
				m_fileWriter.getChannel().position(newPos);
			}
		}		
		catch(IOException e){
			e.printStackTrace();
		}
	}
	@Override
	public synchronized int tell(){
		if(!m_isOpened)
			return 0;
		
		try{
			if(m_isLoading){
				return (int)m_fileReader.getChannel().position();
			}
			else{
				return (int)m_fileWriter.getChannel().position();
			}
		}
		catch(IOException e){
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public synchronized boolean isEof(){
		if(!m_isOpened)
			return true;
		if(m_isLoading){
			try {
				return m_fileReader.getChannel().position()
				>= m_fileReader.getChannel().size() ? true : false;
			} catch (IOException e) {
				e.printStackTrace();
				return true;
			}
		}
		else{
			return false;
		}
	}
	@Override
	public void close(){
		if(!m_isOpened)
			return;
        flush();
		try {
			if(m_isLoading){
				m_fileReader.close();
			}
			else{
				m_fileWriter.close();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		m_isOpened = false;
	}
}
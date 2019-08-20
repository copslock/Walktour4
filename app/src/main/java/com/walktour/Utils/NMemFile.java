package com.walktour.Utils;

/**
 * @version 1.0
 * @created 29-九月-2010 11:35:55
 */
public class NMemFile implements NFile {

	private boolean 			m_isLoading;
	private byte[] 				m_buffer = new byte[0];
	private int 				m_offSet;
	private boolean 			m_isOpened = false;
	
	public NMemFile(boolean isload){
		m_isLoading = isload;
	}
	public NMemFile(byte[] buf,boolean isload){
		m_buffer = buf;
		m_isLoading = isload;
		m_isOpened = true;
	}
	@Override
	public boolean open(){
		if(m_isOpened == true)
			return true;
		
		m_buffer = new byte[1024];
		
		if(m_buffer == null)
			return false;
		else{
			m_isOpened = true;
			return true;
		} 
	}
	@Override
	public synchronized int read(byte[] buf, int len){
		if(!m_isOpened || !m_isLoading)
			return 0;
		int copyLen = 0;
		if(len+m_offSet >= m_buffer.length){
			copyLen = m_buffer.length - m_offSet;
		}
		else{
			copyLen = len;
		}
		System.arraycopy(m_buffer,m_offSet,buf,0,copyLen);
		m_offSet += copyLen;
		return copyLen;
	}
	@Override
	public synchronized int write(byte[] buf, int len){
		if(!m_isOpened || m_isLoading)
			return 0;
		if (buf.length < len)
			len = buf.length;
		if (m_offSet + len > m_buffer.length) {
			len = m_buffer.length - m_offSet;
		}
//		if(len + m_offSet >= m_buffer.length){
//			byte[] bufNew = new byte[(len + m_offSet)*2];
//			System.arraycopy(m_buffer,0,bufNew,0,m_buffer.length);
//			m_buffer = bufNew;
//		}
		System.arraycopy(buf,0,m_buffer,m_offSet,len);
		//if (buf.length < len)
		//	System.arraycopy(buf,0,m_buffer,m_offSet,buf.length);
		//else 
		//	System.arraycopy(buf,0,m_buffer,m_offSet,len);
		m_offSet += len;
		return len;
	}
	@Override
	public synchronized void seek(int offset, int from){
		if(!m_isOpened)
			return;
		
		if(from == NFile.SEEK_SET){
			m_offSet = 0;
		}
		else if(from == SEEK_END){
			m_offSet = m_buffer.length;
		}
		m_offSet += offset;
		if(m_offSet >= m_buffer.length){
			byte[] buf = new byte[m_offSet * 2];
			System.arraycopy(m_buffer,0,buf,0,m_buffer.length);
			m_buffer = buf;
		}
	}
	@Override
	public synchronized int tell(){
		return m_offSet;
	}
	
	@Override
	public synchronized boolean isEof(){
		return m_offSet >= m_buffer.length ? true : false;
	}
	@Override
	public synchronized void flush(){
		return;
	}
	@Override
	public void close(){
		m_isOpened = false;
		m_buffer = new byte[0];
		m_offSet = 0;
	}
}
package com.walktour.Utils;

/**
 * @version 1.0
 * @created 29-九月-2010 11:35:54
 */
public interface NFile {

	public static final int SEEK_SET = 0;//from begin
	public static final int SEEK_CUR = 1;
	public static final int SEEK_END = 2;
	
	/**
	 * there is a param isload,when isload = true,you can only read from file,
	 * otherwise you can only write to file
	 * @return
	 */
	public boolean open();

	/**
	 * 
	 * @param buf
	 * @param len
	 */
	public int read(byte[] buf, int len);

	/**
	 * 
	 * @param offset
	 * @param from
	 */
	public void seek(int offset, int from);
	public int tell();
	/**
	 * 
	 * @param buf
	 * @param len
	 */
	public int write(byte[] buf, int len);
	public void flush();
	public boolean isEof();
	public void close();
}
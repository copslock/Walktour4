package com.dingli.service.test;

public class oswsharemjni {
	private int mHandle = 0;

	// C functions we call
	public native int OswCreate(String key, int length);
	public native int OswDelete(int handle);
	public native int OswOpen(String key, int length, boolean write);
	public native int OswClose(int handle);
	public native byte[] OswRead(int handle, int dst_pos, int dst_len);
	public native int OswWrite(int handle, byte[] src, int dst_pos, int dst_len);
	public native byte[] OswGetVideoFrame(int handle);
	
	public oswsharemjni()
	{
		
	}
	
	public boolean Create(String key, int length)
	{
		mHandle = OswCreate(key, length);
		return (mHandle==0)?false:true;
	}
	
	public void Delete()
	{
		if (mHandle == 0)
			return;
		
		OswDelete(mHandle);
		mHandle = 0;
	}
	
	public boolean Open(String key, int length, boolean write)
	{
		mHandle = OswOpen(key, length, write);
		return (mHandle==0)?false:true;
	}
	
	public void Close()
	{
		if (mHandle == 0)
			return;
		
		OswClose(mHandle);
		mHandle = 0;
	}
	
	public byte[] Read(int dst_pos, int dst_len)
	{
		if (mHandle == 0)
		{
			return null;
		}
		
		return OswRead(mHandle, dst_pos, dst_len);
	}
	
	public int Write(byte[] src, int dst_pos, int dst_len)
	{
		if (mHandle == 0)
			return 0;
		
		return OswWrite(mHandle, src, dst_pos, dst_len);
	}
	
	public byte[] GetVideoFrame()
	{
		if (mHandle == 0)
		{
			return null;
		}
		
		return OswGetVideoFrame(mHandle);
	}
}
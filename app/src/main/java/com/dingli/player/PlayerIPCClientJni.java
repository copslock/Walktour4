package com.dingli.player;

import android.util.Log;

public class PlayerIPCClientJni 
{
	private int mHandle = 0;
	private PlayerIPCClientListener mListener = null;
	
	// C functions we call
	private native int PlayerIPCClientInit(String logFilePath);
	private native void PlayerIPCClientUninit(int handle);
	private native int PlayerIPCClientStart(int handle, String shareMemorykey);
	private native void PlayerIPCClientStop(int handle);
	
	public PlayerIPCClientJni(PlayerIPCClientListener listener)
	{
		mListener = listener;
	
	}
	
	static {
		System.loadLibrary("PlayerIPCClientJni");
	}
	
	public boolean Init(String logFilePath)
	{
		mHandle = PlayerIPCClientInit(logFilePath);
		
		if (mHandle == 0)
		{
			Log.e("PlayerIPCClientJni", "PlayerIPCClientInit failed");
			return false;
		}
		
		return true;
	}
	
	public void Uninit()
	{
		if (mHandle != 0)
		{
			PlayerIPCClientUninit(mHandle);
			mHandle = 0;	
		}
	}
	
	public boolean Start(String shareMemoryKey)
	{
		if (mHandle == 0)
			return false;
		
		int ret = PlayerIPCClientStart(mHandle, shareMemoryKey);
		
		if (ret <= 0)
		{
			Log.e("PlayerIPCClientJni", "PlayerIPCClientStart failed");
			return false;
		}
		
		return true;
	}
	
	public void Stop()
	{
		if (mHandle == 0)
			return;
		
		PlayerIPCClientStop(mHandle);
	}
	
	// Call by C functions
	public void VideoCallback(byte[] frame_data, int frame_size, int width, int height)
	{
		//Log.i("PlayerIPCClientJni", "VideoCallback");
		if (mListener == null)
			return;
		
		mListener.VideoCallback(frame_data, frame_size, width, height);
	}
	
	public void AudioCallback(byte[] frame_data, int frame_size, int channels, int sample_rate)
	{
		//Log.i("PlayerIPCClientJni", "AudioCallback");
		if (mListener == null)
			return;
		
		mListener.AudioCallback(frame_data, frame_size, channels, sample_rate);
	}
}

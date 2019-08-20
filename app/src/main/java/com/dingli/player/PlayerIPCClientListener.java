package com.dingli.player;

public interface PlayerIPCClientListener
{
	public void VideoCallback(byte[] frame_data, int frame_size, int width, int height);
	public void AudioCallback(byte[] frame_data, int frame_size, int channels, int sample_rate);
}



package com.dingli.player;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.walktour.base.util.LogUtil;

import java.nio.ByteBuffer;

public class VideoStreamPlayer extends Activity implements PlayerIPCClientListener
{
	private PlayerIPCClientJni mPlayerJni;
	private String mShareMemoryName;
	
	//video
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ByteBuffer mByteBuffer;
	private Bitmap mBitmap;
	private Rect mDrawRect;
	
	private int mVideoWidth;
	private int mVideoHeight;
	
	private int mFrameWidth;
	private int mFrameHeight;
	
	//audio
//	private AudioTrack mAudioTrack;
	
//	private int mAudioChannels;
//	private int mAudioSampleRate;
	
	public VideoStreamPlayer(String logFilePath)
	{
		mPlayerJni = new PlayerIPCClientJni(this);
		mPlayerJni.Init(logFilePath);
		mShareMemoryName = "player_share_memory";

		mSurfaceView = null;
		mSurfaceHolder = null;
		mByteBuffer = null;
		mBitmap = null;
		mDrawRect = null;
		
		mVideoWidth = 0;
		mVideoHeight = 0;
		
		mFrameWidth = 0;
		mFrameHeight = 0;
		
//		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
//				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, 
//				0, AudioTrack.MODE_STREAM);
//		mAudioTrack = null;
//		mAudioChannels = 0;
//		mAudioSampleRate = 0;
	}

	public void SetDisplay(SurfaceView surfaceView)
	{
		mSurfaceView = surfaceView;
		mSurfaceHolder = mSurfaceView.getHolder();
	}

	public boolean Start(String shareMemoryPath)
	{
		if (!shareMemoryPath.endsWith("/"))
			shareMemoryPath += "/";
		
		String key = shareMemoryPath + mShareMemoryName;
		
		return mPlayerJni.Start(key);
	}
	
	public void Stop()
	{
		mPlayerJni.Stop();
	}
	
	public void Destory()
	{
		mPlayerJni.Uninit();
	}

	@Override
	public void VideoCallback(byte[] frame_data, int frame_size, int width, int height) 
	{
		//Log.i("VideoStreamPlayer", "frame_size:"+frame_size+" width:"+width+" height:"+height); 
		if (mSurfaceHolder == null)
			return;
		
		if ((mByteBuffer == null) || (mBitmap == null) ||
		    (mVideoWidth != width) || (mVideoHeight != height))
		{
			mVideoWidth = width;
			mVideoHeight = height;
			
			mByteBuffer = ByteBuffer.allocateDirect(frame_size);
			mBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
			mDrawRect = null;
		}

		Rect frameRect = mSurfaceHolder.getSurfaceFrame();
		if ((mDrawRect == null) || 
			(mFrameWidth != frameRect.width()) || (mFrameHeight != frameRect.height()))	
		{
			mFrameWidth = frameRect.width();
			mFrameHeight = frameRect.height();
			
			mDrawRect = ScaleRect(width, height, mFrameWidth, mFrameHeight);
		}
		
		mByteBuffer.clear();
		mByteBuffer.put(frame_data, 0, frame_size);
		mByteBuffer.position(0);
		
		try 
		{
	        Canvas c = mSurfaceHolder.lockCanvas();
	        mBitmap.copyPixelsFromBuffer(mByteBuffer);
	        c.drawColor(Color.BLACK);
	        //c.drawBitmap(mBitmap, 0, 0, null);
	        c.drawBitmap(mBitmap, null, mDrawRect, null);
	        mSurfaceHolder.unlockCanvasAndPost(c);
	    } 
		catch (Exception e) 
		{
	        LogUtil.w("VideoStreamPlayer", "",e);
	    }
	}

	@Override
	public void AudioCallback(byte[] frame_data, int frame_size, int channels, int sample_rate) 
	{
		
	}
	
	private Rect ScaleRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight)
	{
		int left=0, top=0, right=0, bottom=0;
		int width=0, height=0;
		
		if ((srcWidth == 0) || (srcHeight == 0))
		{
			width = dstWidth;
			height = dstHeight;
		}
		else if ((srcWidth * dstHeight) > (srcHeight * dstWidth))
		{
			width = dstWidth;
			height = (srcHeight * dstWidth) / srcWidth;
		}
		else
		{
			width = (srcWidth * dstHeight) / srcHeight;
			height = dstHeight;
		}
		
		// Center
		left = (dstWidth - width) / 2;
		top = (dstHeight - height) / 2;
		right = left + width;
		bottom = top + height;
		
		return new Rect(left, top, right, bottom);
	}
}

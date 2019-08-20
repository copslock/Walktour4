/**
 * 
 */
package com.walktour.model;

import java.io.Serializable;

/**
 * @author Xie Jihong
 *  流媒体内容
 */
public class StreamingItem implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String EMPTY = "";
	public static final int FIRSTDATA_ARRIVED = 1;
	public static final int QOS_ARRIVED = 2;
	public static final int LAST_DATA = 3;
	
	public FirstDataItem firstDataItem;
	public Qos qos;
	public LastData lastData;
	
	private static StreamingItem instance = new StreamingItem();
	
	private StreamingItem(){
		firstDataItem = new FirstDataItem();
		qos = new Qos();
		lastData = new LastData();
	}
	
	public static final StreamingItem getInstance(){
		return instance;
	}
	
	public FirstDataItem getFirstDataItem(){
		return firstDataItem;
	}
	
	public final class FirstDataItem implements Serializable {
		private static final long serialVersionUID = -7398048864997453717L;
		public int duration_ms;
		public String video_codec = EMPTY;
		public String audio_codec = EMPTY;
		public int video_width;
		public int video_height;
		public int video_fps;
		public int total_bitrate;
		public int media_quality;
		public String video_title = EMPTY;
	}
	
	public final class Qos implements Serializable {
		private static final long serialVersionUID = -2048497259354367705L;
		public  int prevMeasureTime;
		public  int MeasureTime;
		public  int RecvTotalBytes;
		public  int DownloadProgress;
		public  int ReBufferTimes;
		public  int ReBufferTimeMS;
		public  float DVSNR_VMOS;
		public  int AV_DeSync;
		public  int RecvVideoPacket;
		public  int RecvAudioPacket;
		public  int LostVideoPacket;
		public  int LostAudioPacket;
		public  int AvgVideoPacketGap;
		public  int AvgAudioPacketGap;
		public  int CurVideoPacketJitter;
		public  int CurAudioPacketJitter;
		public  int MaxVideoPacketJitter;
		public  int MaxAudioPacketJitter;
		public  int MinVideoPacketJitter;
		public  int MinAudioPacketJitter;
		public  int AvgVideoPacketJitter;
		public  int AvgAudioPacketJitter;
		public	int	CurRecvSpeed;
		public 	int CurFps;
		public	int BufferRatio;
		public 	int CurVideoLostFraction;
		public 	int CurAudioLostFraction;
		public	int MaxVideoPacketGap;
		public	int MaxAudioPacketGap;
		public	int MinVideoPacketGap;
		public	int MinAudioPacketGap;
		
		@Override
		public String toString() {
			return "Qos [prevMeasureTime=" + prevMeasureTime + ", MeasureTime="
					+ MeasureTime + ", RecvTotalBytes=" + RecvTotalBytes
					+ ", DownloadProgress=" + DownloadProgress
					+ ", ReBufferTimes=" + ReBufferTimes + ", ReBufferTimeMS="
					+ ReBufferTimeMS + ", DVSNR_VMOS=" + DVSNR_VMOS
					+ ", AV_DeSync=" + AV_DeSync + ", RecvVideoPacket="
					+ RecvVideoPacket + ", RecvAudioPacket=" + RecvAudioPacket
					+ ", LostVideoPacket=" + LostVideoPacket
					+ ", LostAudioPacket=" + LostAudioPacket
					+ ", AvgVideoPacketGap=" + AvgVideoPacketGap
					+ ", AvgAudioPacketGap=" + AvgAudioPacketGap
					+ ", CurVideoPacketJitter=" + CurVideoPacketJitter
					+ ", CurAudioPacketJitter=" + CurAudioPacketJitter
					+ ", MaxVideoPacketJitter=" + MaxVideoPacketJitter
					+ ", MaxAudioPacketJitter=" + MaxAudioPacketJitter
					+ ", MinVideoPacketJitter=" + MinVideoPacketJitter
					+ ", MinAudioPacketJitter=" + MinAudioPacketJitter
					+ ", AvgVideoPacketJitter=" + AvgVideoPacketJitter
					+ ", AvgAudioPacketJitter=" + AvgAudioPacketJitter 
					+ ", CurRecvSpeed=" 		+ CurRecvSpeed 
					+ ", CurFps=" 				+ CurFps 
					+ ", BufferRatio=" 			+ BufferRatio 
					+ ", CurVideoLostFraction=" + CurVideoLostFraction 
					+ ", CurAudioLostFraction=" + CurAudioLostFraction 
					+ ", MaxVideoPacketGap=" 	+ MaxVideoPacketGap 
					+ ", MaxAudioPacketGap=" 	+ MaxAudioPacketGap 
					+ ", MinVideoPacketGap=" 	+ MinVideoPacketGap 
					+ ", MinAudioPacketGap=" 	+ MinAudioPacketGap 
					+ "]";
		}
	}
	
	public  final class LastData implements Serializable{
		private static final long serialVersionUID = -2352868837116007432L;
		public  float VSNR_VMOS;
		public  int AV_DeSync_Rate;
		public  String AudioSaveFile = EMPTY;
		public  String VideoSaveFile = EMPTY;
		public  String RTSPSaveFile = EMPTY;
		public  String RTCPAudioSaveFile = EMPTY;
		public  String RTCPVideoSaveFile = EMPTY;
		public  String RTPAudioSaveFile = EMPTY;
		public  String RTPVideoSaveFile = EMPTY;
		public  int reason;
	}
	
	public final StreamingItem analyseMsg(String data, int type){
		StreamingItem item = new StreamingItem();
		String[] arr_info = data.split("\n");
		for (int i=0; i<arr_info.length; i++) {
			String[] key_value = arr_info[i].split("::");
			if (key_value.length < 2) {
				continue;
			}
			
			switch (type) {
			case FIRSTDATA_ARRIVED:
				analyseFirstData(key_value);
				break;
			case QOS_ARRIVED:
				analyseQos(key_value);
				break;
			case LAST_DATA:
				analyseLastData(key_value);
				break;
			default:
				break;
			}
		}
		return item;
	}
	
	private void analyseLastData(String[] key_value) {
		if(key_value[0].equals("VSNR_VMOS")){
			lastData.VSNR_VMOS = Float.parseFloat(key_value[1]);
		}else if(key_value[0].equals("AV_DeSync_Rate")){
			lastData.AV_DeSync_Rate = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("AudioSaveFile")){
			lastData.AudioSaveFile = key_value[1];
		}else if(key_value[0].equals("VideoSaveFile")){
			lastData.VideoSaveFile = key_value[1];
		}else if(key_value[0].equals("RTSPSaveFile")){
			lastData.RTSPSaveFile = key_value[1];
		}else if(key_value[0].equals("RTCPAudioSaveFile")){
			lastData.RTCPAudioSaveFile = key_value[1];
		}else if(key_value[0].equals("RTCPVideoSaveFile")){
			lastData.RTCPVideoSaveFile = key_value[1];
		}else if(key_value[0].equals("RTPAudioSaveFile")){
			lastData.RTPAudioSaveFile = key_value[1];
		}else if(key_value[0].equals("RTPVideoSaveFile")){
			lastData.RTPVideoSaveFile = key_value[1];
		}else if(key_value[0].equals("reason")){
			lastData.reason = Integer.parseInt(key_value[1]);
		}
	}

	private void analyseQos(String[] key_value) {
		if(key_value[0].equals("AV_DeSync")){
			qos.AV_DeSync = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("AvgAudioPacketGap")){
			qos.AvgAudioPacketGap = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("AvgVideoPacketGap")){
			qos.AvgVideoPacketGap = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("AvgAudioPacketJitter")){
			qos.AvgAudioPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("AvgVideoPacketJitter")){
			qos.AvgVideoPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("CurAudioPacketJitter")){
			qos.CurAudioPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("CurVideoPacketJitter")){
			qos.CurVideoPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("DownloadProgress")){
			qos.DownloadProgress = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("DVSNR_VMOS")){
			qos.DVSNR_VMOS = Float.parseFloat(key_value[1]);
		}else if(key_value[0].equals("LostAudioPacket")){
			qos.LostAudioPacket = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("LostVideoPacket")){
			qos.LostVideoPacket = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MaxAudioPacketJitter")){
			qos.MaxAudioPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MaxVideoPacketJitter")){
			qos.MaxVideoPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MeasureTime")){
			qos.prevMeasureTime = qos.MeasureTime;
			qos.MeasureTime = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MinAudioPacketJitter")){
			qos.MinAudioPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MinVideoPacketJitter")){
			qos.MinVideoPacketJitter = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("ReBufferTimeMS")){
			qos.ReBufferTimeMS = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("ReBufferTimes")){
			qos.ReBufferTimes = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("RecvAudioPacket")){
			qos.RecvAudioPacket = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("RecvTotalBytes")){
			qos.RecvTotalBytes = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("RecvVideoPacket")){
			qos.RecvVideoPacket = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("CurRecvSpeed")){
			qos.CurRecvSpeed = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("CurFps")){
			qos.CurFps = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("BufferRatio")){
			qos.BufferRatio = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("CurVideoLostFraction")){
			qos.CurVideoLostFraction = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("CurAudioLostFraction")){
			qos.CurAudioLostFraction = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MaxVideoPacketGap")){
			qos.MaxVideoPacketGap = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MaxAudioPacketGap")){
			qos.MaxAudioPacketGap = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MinVideoPacketGap")){
			qos.MinVideoPacketGap = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("MinAudioPacketGap")){
			qos.MinAudioPacketGap = Integer.parseInt(key_value[1]);
		}
	}

	private void analyseFirstData(String[] key_value) {
		if(key_value[0].equals("duration_ms")){
			firstDataItem.duration_ms = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("video_title")){
			firstDataItem.video_title = key_value[1];
		}else if(key_value[0].equals("video_codec")){
			firstDataItem.video_codec = key_value[1];
		}else if(key_value[0].equals("media_quality")){
			firstDataItem.media_quality = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("total_bitrate")){
			firstDataItem.total_bitrate = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("video_fps")){
			firstDataItem.video_fps = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("video_height")){
			firstDataItem.video_height = Integer.parseInt(key_value[1]);
		}else if(key_value[0].equals("video_width")){
			firstDataItem.video_width = Integer.parseInt(key_value[1]);
		}
	}

	public final void reset(){
		firstDataItem.audio_codec = EMPTY;
		firstDataItem.video_title = EMPTY;
		firstDataItem.video_codec = EMPTY;
		firstDataItem.duration_ms 	= 0;
		firstDataItem.media_quality = 0;
		firstDataItem.total_bitrate = 0;
		firstDataItem.video_fps 	= 0;
		firstDataItem.video_height 	= 0;
		firstDataItem.video_width 	= 0;
		
		qos.AV_DeSync 				= 0;
		qos.AvgAudioPacketGap 		= 0;
		qos.AvgAudioPacketJitter 	= 0;
		qos.AvgVideoPacketJitter 	= 0;
		qos.AvgVideoPacketGap 		= 0;
		qos.CurAudioPacketJitter 	= 0;
		qos.CurVideoPacketJitter 	= 0;
		qos.DownloadProgress	 	= 0;
		qos.DVSNR_VMOS 				= 0;
		qos.LostAudioPacket 		= 0;
		qos.LostVideoPacket 		= 0;
		qos.MaxAudioPacketJitter 	= 0;
		qos.MaxVideoPacketJitter 	= 0;
		qos.MeasureTime 			= 0;
		qos.MinAudioPacketJitter 	= 0;
		qos.MinVideoPacketJitter 	= 0;
		qos.ReBufferTimeMS 			= 0;
		qos.ReBufferTimes 			= 0;
		qos.RecvAudioPacket 		= 0;
		qos.RecvTotalBytes 			= 0;
		qos.RecvVideoPacket 		= 0;
		qos.CurFps					= 0;
		qos.BufferRatio				= 0;
		qos.CurVideoLostFraction	= 0;
		qos.CurAudioLostFraction	= 0;
		qos.MaxVideoPacketGap	 	= 0;
		qos.MaxAudioPacketGap		= 0;
		qos.MinVideoPacketGap		= 0;
		qos.MinAudioPacketGap		= 0;
		
		lastData.VSNR_VMOS 			= 0;
		lastData.AV_DeSync_Rate 	= 0;
		lastData.AudioSaveFile 		= EMPTY;
		lastData.VideoSaveFile 		= EMPTY;
		lastData.RTSPSaveFile 		= EMPTY;
		lastData.RTCPAudioSaveFile 	= EMPTY;
		lastData.RTCPVideoSaveFile 	= EMPTY;
		lastData.RTPAudioSaveFile 	= EMPTY;
		lastData.RTPVideoSaveFile 	= EMPTY;
		lastData.reason 			= 0;
	}
}

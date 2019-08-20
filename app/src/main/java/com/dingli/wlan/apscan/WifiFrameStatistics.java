package com.dingli.wlan.apscan;
/*
 * 
 * MAC Frame 与信道相关
 * 
 */
public class WifiFrameStatistics {
	public int framesCount;
	public int directedFrames;
	public int multicastFrames;
	public int broadcastFrames;
	public int collisionFrames; //碰撞
	public int retryFrames;
	public int errorFrames; //FCS error frames
	public int managementFrames;
	public int controlFrames;
	public int dataFrames;
	public int bytesCount;
	public int directedBytes;
	public int multicastBytes;
	public int broadcastBytes;
	public int retryBytes;
	public int errorBytes; // FCS 
	public int managementBytes;
	public int controlBytes;
	public int dataBytes;
}

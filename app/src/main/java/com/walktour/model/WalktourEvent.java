package com.walktour.model;

import android.content.Context;

import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;

public class WalktourEvent {
	private byte[] eventBytes ;
	private String name ="UNKNOWN";
	private long time ;
	/**
	 * [要上传的Walktour事件]
	 * @param name 事件名称
	 * @param time 事件发生的时间点，这个会一并上传到平台,这个已经包含在eventBytes里，
	 */
	public WalktourEvent(Context context,String name,long time,int event,int reason){
		this.eventBytes =
			EventBytes.Builder(context,"Command=Event\r\n\r\n".toCharArray() )
			.addByte( (byte)2 ) 
			.addInteger( (int) (time/1000 ) ) 
			.addInteger( RcuEventCommand.WALKTOUR_EVENT )
			.addInteger( event )
			.addInteger( reason )
			.getByteArray();
			this.name = name ;
			this.time = time ;
	}
	
	/**
	 * @return 直接发送给平台的事件结构体内存块
	 */
	public byte[] getEventBytes() {
		return eventBytes;
	}
	
	public String getName() {
		return name;
	}
	
	public long getTime(){
		return time;
	}
}
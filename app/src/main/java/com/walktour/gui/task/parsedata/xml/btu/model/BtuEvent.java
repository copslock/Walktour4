package com.walktour.gui.task.parsedata.xml.btu.model;

/**
 * BtuEvent
 * 
 * 2014-4-11 上午11:12:31
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class BtuEvent {
	private long time ;
	private int code;
	private int moudle =1;
	
	public BtuEvent(long time,int btuCode,int moudle){
		this.time = time;
		this.code = btuCode;
		this.moudle = moudle;
	}

	public long getTime() {
		return time;
	}

	public int getCode() {
		return code;
	}
	
	public int getMoudle(){
		return moudle;
	}
	
	public static BtuEvent convertFormRcu(long time,int rcuId,int moudle){
		int btuCode = getBtuCode(rcuId);
		if( btuCode >0 ){
			BtuEvent event = new BtuEvent(time,btuCode,moudle);
			return event;
		}
		return null;
	}
	
	/**
	 * 函数功能：根据RcuCode获取BTU code
	 * @param rcuCode
	 * @return
	 */
	private static int getBtuCode(int rcuCode){
		BtuEventCode[] btuCodes = BtuEventCode.values();
		
		for( int i=0;i<btuCodes.length;i++){
			BtuEventCode event = btuCodes[i];
			int[] rcuCodes = event.RcuCodes();
			for(int c:rcuCodes){
				if( c==rcuCode ){
					return event.BTUCODE();
				}
			}
		}
		return 0;
	}
	
}

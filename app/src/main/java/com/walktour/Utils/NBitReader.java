package com.walktour.Utils;

/**
 * @version 1.0
 * @created 29-九月-2010 11:35:53
 */
public final class NBitReader {
	
	private boolean 	m_lowFirstRead;
	private byte[] 		m_buffer;
	private int 		m_length;
	private int 		m_offSet;
	private int 		m_bitBuffered;
	private byte		m_bufferedByte;
	
	public static NBitReader createLowFirst(byte[] buf,int len){
		return new NBitReader(buf,len,true);
	}
	public static NBitReader createHighFirst(byte[] buf,int len){
		return new NBitReader(buf,len,false);
	}
	private NBitReader(byte[] buf,int len,boolean lowFirst){
		m_buffer = buf;
		m_length = len;
		m_lowFirstRead = lowFirst;
		m_offSet = 0;
		m_bitBuffered = 0;
	}
	public int getOffSet(){
		return m_offSet;
	}
	public byte getByte(){
		if (m_offSet>=m_length)
		{
			//Log.e("indoor","bad data!!");
			return 0;
		}
		if(m_bitBuffered == 0)
			return m_buffer[m_offSet++];
		else
			return (byte)getBits(8);
	}
	public short getUByte()
	{
		byte value = getByte();
		int signedByte = (byte)value;
		int result = signedByte <0 ? 256 + signedByte: signedByte ;
		return (short)result;
	}
	public int getUShort()
	{
		short a = getUByte();
		short b = getUByte();
		short value =  m_lowFirstRead == true ? (short)(a|(b<<8)):(short)(b|(a<<8));
		int signedShort = (short)value;
		int result = signedShort <0 ? 65536 + signedShort: signedShort ;
		return result;
	}
	public short getShort(){
		short a = getUByte();
		short b = getUByte();
		return m_lowFirstRead == true ? (short)(a|(b<<8)):(short)(b|(a<<8));
	}
	public int getInt(){
		int a = getUShort();
		int b = getUShort();
		return m_lowFirstRead == true ? (a|(b<<16)):(b|(a<<16));
	}
	public long getUInt(){
		int a = getUShort();
		int b = getUShort();
		long value =  m_lowFirstRead == true ? (a|(b<<16)):(b|(a<<16));
		return value <0 ? 0xffffffff+1 + value: value ;
	}
	public long getLong(){
		long a = getUInt();
		long b = getUInt();
		return m_lowFirstRead == true ? (a|(b<<32)):(b|(a<<32));
	}
	public float getFloat(){
        byte[] buf = new byte[4];
        getBytes(buf,4);
        return m_lowFirstRead == true ? NBits.getFloat(buf,0):NBits.getFloat(buf,0);
	}
	public double getDouble(){
		byte[] buf = new byte[8];
        getBytes(buf,8);
        return m_lowFirstRead == true ? NBits.getDouble(buf,0):NBits.getDouble(buf,0);
	}

	public byte getBit(){
		return (byte)getBits(1);
	}
    /**
	 * @param count: 0 < count <= 32
	 */
	public int getBits(int count){
		if(count > 32 || count == 0){
			return 0;
		}
		
		if (m_offSet>=m_length)
		{
		//	Log.d("Pocket","bad data!!");
			return 0;
		}
		if(m_bitBuffered == 0){
			m_bufferedByte = m_buffer[m_offSet++];
			m_bitBuffered = 8;
		}
		
		int rt = 0;	
		if(count <= m_bitBuffered){
			rt = ((m_bufferedByte<<(8-m_bitBuffered))>>>(8-count))&((1<<count) -1);
			m_bitBuffered -= count;
		}
		else{
			int bufed = m_bitBuffered;
			rt = m_bufferedByte&((1<<bufed) -1);
			m_bitBuffered = 0;
			rt <<= count - bufed;
			rt |= getBits(count - bufed);//recurrence
		}
		return rt;
	}
	public void getBytes(byte[] buf,int len){
		for(int i=0;i<len;i++){
			buf[i] = getByte();
		}
	}
	public void skipBits(int count){
		getBits(count);
	}
	public void skipByte(){
		getByte();
	}
	public void skipBytes(int count){
		for(int i=0;i<count;i++){
			getByte();
		}
	}
	public void backByte(){
		if(m_bitBuffered != 0){
		    //Log.e("Pocket","backByte() m_bitBuffered != 0");
        }
		m_offSet--;
	}
    public int getBitsBufferd(){
        return m_bitBuffered;
    }
	public int getBitsLeft(){
		if(m_length <= m_offSet)
			return 0;
		return (m_length - m_offSet)*8 + m_bitBuffered;
	}
	public int getBytesLeft(){
		return m_length-m_offSet;
	}
	public int getLength(){
		return m_length;
	}
	public byte[] getBuffer(){
		return m_buffer;
	}
}
package com.walktour.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @created 29-九月-2010 11:35:54
 */
public final class NArchive {

	private NFile m_file;
	private boolean m_isLoading;
	private static final String CODE = ("UTF-8");

	public static NArchive createFromBuffer(byte[] buf,boolean isload){
		return new NArchive(isload,buf,null,null,true);
	}
	public static NArchive createBuffer(boolean isload){
		return new NArchive(isload,null,null,null,true);
	}
	public static NArchive createFromFile(NFile f,boolean isload){
		return new NArchive(isload,null,f,null,false);
	}
	public static NArchive createFile(String fileName,boolean isload){
		return new NArchive(isload,null,null,fileName,false);
	}
	private NArchive(boolean isload,byte[] buf,NFile f,String name,boolean useBuf){
		m_isLoading = isload;
		if(useBuf){
			if(buf == null){
				m_file = new NMemFile(isload);
			}
			else{
				m_file = new NMemFile(buf,isload);
			}
		}
		else{
			if(f == null){
				m_file = new NDiskFile(name,isload);
			}
			else{
				m_file = f;
			}
		}
	}
	private int readStringLen(){
		int strLen = 0;
		for(int i=0;i<5;i++){
			byte raw = this.readByte();
			byte flag = (byte) (raw & 0x80);
			strLen = (raw&0x7f)<<(7*i)|strLen;
			if(flag == 0)
				break;
		}
		return strLen;
	}
	private void writeStringLen(int len){
		while(len >= 0x80){
			this.writeByte((byte)(len%0x80+0x80));
			len -= len % 0x80;
			len /= 0x80;
		}
		this.writeByte((byte)len);
	}
	public boolean open(){
		return m_file.open();
	}
	public void close(){
		m_file.close();
	}
	public NFile getFile(){
		return m_file;
	}
	public int getOffset(){
		return m_file.tell();
	}
	public boolean isLoading(){
		return m_isLoading;
	}
	public byte readByte(){
		byte[] b = new byte[1];
		m_file.read(b, 1);
		return b[0];
	}
	public void writeByte(byte v){
		byte[] b = {v};
		m_file.write(b,1);
	}
	public int readInt(){
		byte[] b = new byte[4];
		m_file.read(b, 4);
		return NBits.getInt(b, 0);
	}
	public void writeInt(int v){
		byte[] b = new byte[4];
		NBits.putInt(b,0,v);
		m_file.write(b,4);
	}
	public void writeIntBigEndian(int v){
		byte[] b = new byte[4];
		NBits.putIntBigEndian(b,0,v);
		m_file.write(b,4);
	}
	public void writeBytes(byte[] buffer) {
		m_file.write(buffer,buffer.length);
	}
	public void writeBytes(byte[] buffer,int length) {
		m_file.write(buffer,length);
	}
	public void readBytes(byte[]buffer,int len) {
		m_file.read(buffer, len);
	}
	public void readBytes(byte[]buffer) {
		m_file.read(buffer, buffer.length);
	}
	public long readLong(){
		byte[] b = new byte[8];
		m_file.read(b,8);
		return NBits.getLong(b, 0);
	}
	public void writeLong(long v){
		byte[] b = new byte[8];
		NBits.putLong(b,0,v);
		m_file.write(b,8);
	}
	public float readFloat(){
		byte[] b = new byte[4];
		m_file.read(b,4);
		return NBits.getFloat(b, 0);
	}
	public void writeFloat(float v){
		byte[] b = new byte[4];
		NBits.putFloat(b,0,v);
		m_file.write(b,4);
	}
	public double readDouble(){
		byte[] b = new byte[8];
		m_file.read(b,8);
		return NBits.getDouble(b, 0);
	}
	public void writeDouble(double v){
		byte[] b = new byte[8];
		NBits.putDouble(b,0,v);
		m_file.write(b,8);
	}
	public String readString(){
		int len = readStringLen();
		byte[] b = new byte[len];
		m_file.read(b,len);
		
		try {
			String s = new String(b,CODE);//"UTF-8");
			return s;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	public void writeString(String v){
		try {
			byte[] buf = v.getBytes(CODE);
			writeStringLen(buf.length);
			m_file.write(buf, buf.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
    //alarm
    //the read write wstring funtion is not support wstring length >128
    public String readWString(){
        int len = readByte();
        StringBuffer s = new StringBuffer();
        for(int i=0;i<len/2;i++){
            s.append(readChar());
        }
        return s.toString();
    }
    public void writeWString(String v){
        byte len = (byte)v.length();
        writeByte((byte)(len*2));
        char[] b = v.toCharArray();
        for(int i=0;i<(byte)(len);i++){
            writeChar(b[i]);
        }
    }
	public short readShort(){
		byte[] b = new byte[2];
		m_file.read(b,2);
		return NBits.getShort(b, 0);
	}
	public void writeShort(short v){
		byte[] b = new byte[2];
		NBits.putShort(b,0,v);
		m_file.write(b,2);
	}
	public char readChar(){
		byte[] b = new byte[2];
		m_file.read(b,2);
		return NBits.getChar(b, 0);
	}
	public void writeChar(char v){
		byte[] b = new byte[2];
		NBits.putChar(b,0,v);
		m_file.write(b,2);
	}
	public boolean readBoolean(){
		byte[] b = new byte[1];
		m_file.read(b,1);
		return NBits.getBoolean(b, 0);
	}
	public void writeBoolean(boolean v){
		byte[] b = new byte[1];
		NBits.putBoolean(b,0,v);
		m_file.write(b,1);
	}
	public List<String> readStringList(int count){
		List<String> lt = new ArrayList<String>();
		for(int i=0;i<count;i++)
			lt.add(this.readString());
		return lt;
	}
	public void writeStringList(List<String> v){
		for(int i=0;i<v.size();i++)
			this.writeString(v.get(i));
	}
}
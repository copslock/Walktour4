package com.walktour.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class ByteUitils {
	
	public static DecimalFormat decFormat 	= new DecimalFormat("#.##");//鏄剧ず鏁版嵁灏忔暟浣嶆牸寮?

	//杞棤绗﹀佛
	public static short getUnsignedByte(byte value) {
		int signedByte = (short)value;
		int result = signedByte <0 ? 256 + signedByte: signedByte ;
		return (short)result;
	}
	
	/**
     * 瀛楃涓茶浆鎹㈡垚鍗佸叚杩涘埗瀛楃涓?
     */ 
    public static String str2HexStr(byte[] data) { 
        char[] chars = "0123456789ABCDEF".toCharArray(); 
        StringBuilder sb = new StringBuilder("");
        int bit; 
        for (int i = 0; i < data.length; i++) { 
            bit = (data[i] & 0x0f0) >> 4; 
            sb.append(chars[bit]); 
            bit = data[i] & 0x0f; 
            sb.append(chars[bit]); 
        } 
        return sb.toString(); 
    }  
	
	
	
	/**
	 * [灏哹ps链艰浆鎹㈡垚kbps]<BR>
	 * [锷熻兘璇︾粏鎻忚堪]
	 * @param bps
	 * @return
	 */
	public static String bps2Kbps(String bps){
	    String kbps = "";
	    try{
	        if(bps != null && !bps.equals("")){
	            float bpsf = Float.parseFloat(bps) / 1024f;
	            kbps = decFormat.format(bpsf);
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return kbps;
	}
	
	
	/**
	 * byte鏁扮粍copy
	 * 
	 * @param b
	 * @param s
	 *            闇€瑕佽浆鎹㈢殑short
	 * @param index
	 */
	public static byte[] copyOfRange(byte[] original, int from, int to) {
       int newLength = to - from;
       if (newLength < 0)
           throw new IllegalArgumentException(from + " > " + to);
       byte[] copy = new byte[newLength];
       System.arraycopy(original, from, copy, 0,
                        Math.min(original.length - from, newLength));
       return copy;
   }
	/**
	 * 杞崲short涓篵yte
	 * 
	 * @param b
	 * @param s
	 *            闇€瑕佽浆鎹㈢殑short
	 * @param index
	 */
	public static void putShort(byte b[], short s, int index) {
		b[index + 1] = (byte) (s >> 8);
		b[index + 0] = (byte) (s >> 0);
	}

	/**
	 * 阃氲绷byte鏁扮粍鍙栧埌short
	 * 
	 * @param b
	 * @param index
	 *            绗嚑浣嶅紑濮嫔彇
	 * @return
	 */
	public static short getShort(byte[] b, int index) {
		return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));
	}

	/**
	 * 杞崲int涓篵yte鏁扮粍
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putInt(byte[] bb, int x, int index) {
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

	/**
	 * 阃氲绷byte鏁扮粍鍙栧埌int
	 * 
	 * @param bb
	 * @param index
	 *            绗嚑浣嶅紑濮?
	 * @return
	 */
	public static int getInt(byte[] bb, int index) {
		return (int) ((((bb[index + 3] & 0xff) << 24)
				| ((bb[index + 2] & 0xff) << 16)
				| ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
	}

	/**
	 * 杞崲long鍨嬩负byte鏁扮粍
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putLong(byte[] bb, long x, int index) {
		bb[index + 7] = (byte) (x >> 56);
		bb[index + 6] = (byte) (x >> 48);
		bb[index + 5] = (byte) (x >> 40);
		bb[index + 4] = (byte) (x >> 32);
		bb[index + 3] = (byte) (x >> 24);
		bb[index + 2] = (byte) (x >> 16);
		bb[index + 1] = (byte) (x >> 8);
		bb[index + 0] = (byte) (x >> 0);
	}

	/**
	 * 阃氲绷byte鏁扮粍鍙栧埌long
	 * 
	 * @param bb
	 * @param index
	 * @return
	 */
	public static long getLong(byte[] bb, int index) {
		return ((((long) bb[index + 7] & 0xff) << 56)
				| (((long) bb[index + 6] & 0xff) << 48)
				| (((long) bb[index + 5] & 0xff) << 40)
				| (((long) bb[index + 4] & 0xff) << 32)
				| (((long) bb[index + 3] & 0xff) << 24)
				| (((long) bb[index + 2] & 0xff) << 16)
				| (((long) bb[index + 1] & 0xff) << 8) | (((long) bb[index + 0] & 0xff) << 0));
	}

	/**
	 * 瀛楃鍒板瓧鑺傝浆鎹?
	 * 
	 * @param ch
	 * @return
	 */
	public static void putChar(byte[] bb, char ch, int index) {
		int temp = (int) ch;
		// byte[] b = new byte[2];
		for (int i = 0; i < 2; i ++ ) {
			bb[index + i] = new Integer(temp & 0xff).byteValue(); // 灏嗘渶楂树綅淇濆瓨鍦ㄦ渶浣庝綅
			temp = temp >> 8; // 鍚戝彸绉?浣?
		}
	}

	/**
	 * 瀛楄妭鍒板瓧绗﹁浆鎹?
	 * 
	 * @param b
	 * @return
	 */
	public static char getChar(byte[] b, int index) {
		int s = 0;
		if (b[index + 1] > 0)
			s += b[index + 1];
		else
			s += 256 + b[index + 0];
		s *= 256;
		if (b[index + 0] > 0)
			s += b[index + 1];
		else
			s += 256 + b[index + 0];
		char ch = (char) s;
		return ch;
	}

	/**
	 * float杞崲byte
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putFloat(byte[] bb, float x, int index) {
		// byte[] b = new byte[4];
		int l = Float.floatToIntBits(x);
		for (int i = 0; i < 4; i++) {
			bb[index + i] = new Integer(l).byteValue();
			l = l >> 8;
		}
	}

	/**
	 * 阃氲绷byte鏁扮粍鍙栧缑float
	 * 
	 * @param bb
	 * @param index
	 * @return
	 */
	public static float getFloat(byte[] b, int index) {
		int l;
		l = b[index + 0];
		l &= 0xff;
		l |= ((long) b[index + 1] << 8);
		l &= 0xffff;
		l |= ((long) b[index + 2] << 16);
		l &= 0xffffff;
		l |= ((long) b[index + 3] << 24);
		return Float.intBitsToFloat(l);
	}

	/**
	 * double杞崲byte
	 * 
	 * @param bb
	 * @param x
	 * @param index
	 */
	public static void putDouble(byte[] bb, double x, int index) {
		// byte[] b = new byte[8];
		long l = Double.doubleToLongBits(x);
		for (int i = 0; i < 4; i++) {
			bb[index + i] = new Long(l).byteValue();
			l = l >> 8;
		}
	}

	/**
	 * 阃氲绷byte鏁扮粍鍙栧缑float
	 * 
	 * @param bb
	 * @param index
	 * @return
	 */
	public static double getDouble(byte[] b) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l &= 0xffffffffffffffl;
		l |= ((long) b[7] << 56);
		return Double.longBitsToDouble(l);
	}
	
	/***
	 * byte 鏁扮粍涓?int 镄勭浉浜掕浆鎹? 
	 * @param b byte鏁扮粍
	 * @return int链?
	 */
	public static int byteArrayToInt(byte[] b) {  
	    return   b[3] & 0xFF |  
	            (b[2] & 0xFF) << 8 |  
	            (b[1] & 0xFF) << 16 |  
	            (b[0] & 0xFF) << 24;  
	}  
	  
	/***
	 * int涓? byte 鏁扮粍镄勭浉浜掕浆鎹? 
	 */
	public static byte[] intToByteArray(int a) {  
	    return new byte[] {  
	        (byte) ((a >> 24) & 0xFF),  
	        (byte) ((a >> 16) & 0xFF),     
	        (byte) ((a >> 8) & 0xFF),     
	        (byte) (a & 0xFF)  
	    };  
	}  
	
	 /** 
     * 镙规嵁byte鏁扮粍锛岀敓鎴愭枃浠?
     */  
    public static void saveFile(byte[] bfile, String filePath) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {
            file = new File(filePath);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
    
    /***
     * 鏁板€艰浆镞ユ湡
     * @param value 姣鏁?
     * @return 镞堕棿链?涓挞棬鐢ㄤ簬MOS瑙ｆ瀽浣跨敤
     */
    public static String formatD(long value){
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); 
		java.util.Date dt = new Date(value/1000);  
		String sDateTime = sdf.format(dt);
		return sDateTime;
	}

	/**
	 * 获取子数组
	 * @param src
	 * @param begin
	 * @param count
	 * @return
	 */
	public static byte[] getSubBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		System.arraycopy(src, begin, bs, 0, count);
		return bs;
	}
}

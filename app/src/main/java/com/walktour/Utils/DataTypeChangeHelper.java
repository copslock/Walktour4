package com.walktour.Utils;

public class DataTypeChangeHelper {
	  /** 
     * 将一个单字节的byte转换成32位的int 
     *  
     * @param b 
     *            byte 
     * @return convert result 
     */  
    public static int unsignedByteToInt(byte b) {  
        return (int) b & 0xFF;  
    }  
  
    /** 
     * 将一个单字节的Byte转换成十六进制的数 
     *  
     * @param b 
     *            byte 
     * @return convert result 
     */  
    public static String byteToHex(byte b) {  
        int i = b & 0xFF;  
        return Integer.toHexString(i);  
    }  
  
    /** 
     * 将一个4byte的数组转换成32位的int 
     *  
     * @param buf 
     *            bytes buffer 
     * @param byte[]中开始转换的位置 
     * @return convert result 
     */  
    public static long unsigned4BytesToInt(byte[] buf, int pos) {  
        int firstByte = 0;  
        int secondByte = 0;  
        int thirdByte = 0;  
        int fourthByte = 0;  
        int index = pos;  
        firstByte = (0x000000FF & ((int) buf[index]));  
        secondByte = (0x000000FF & ((int) buf[index + 1]));  
        thirdByte = (0x000000FF & ((int) buf[index + 2]));  
        fourthByte = (0x000000FF & ((int) buf[index + 3]));  
        index = index + 4;  
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;  
    }  
  
    /** 
     * 将16位的short转换成byte数组 
     *  
     * @param s 
     *            short 
     * @return byte[] 长度为2 
     * */  
    public static byte[] shortToByteArray(short s) {  
        byte[] targets = new byte[2];  
        for (int i = 0; i < 2; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((s >>> offset) & 0xff);  
        }  
        return targets;  
    }  
  
    /** 
     * 将32位整数转换成长度为4的byte数组 
     *  
     * @param s 
     *            int 
     * @return byte[] 
     * */  
    public static byte[] intToByteArray(int s) {  
        byte[] targets = new byte[4];  
        for (int i = 0; i < 4; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((s >>> offset) & 0xff);  
        }  
        return targets;  
    }  
  
    /** 
     * long to byte[] 
     *  
     * @param s 
     *            long 
     * @return byte[] 
     * */  
    public static byte[] longToByteArray(long s) {  
        byte[] targets = new byte[4];  
        for (int i = 0; i < 8; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((s >>> offset) & 0xff);  
        }  
        return targets;  
    }  
  
    /**32位int转byte[]*/  
    public static byte[] int2byte(int res) {  
        byte[] targets = new byte[2];  
        targets[0] = (byte) (res & 0xff);// 最低位  
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位  
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位  
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。  
        return targets;  
    }  
    
    
    /**int转byte[]*/  
    public static byte[] int2byte4(int res) {  
        byte[] targets = new byte[4];  
        targets[0] = (byte) (res & 0xff);// 最低位  
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位  
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位  
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。  
        return targets;  
    }  
  
    /** 
     * 将长度为2的byte数组转换为16位int 
     *  
     * @param res 
     *            byte[] 
     * @return int 
     * */  
    public static int byte2int(byte[] res) {  
        // res = InversionByte(res);  
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000  
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或  
        return targets;  
    } 
    
    /**
     * 截取指定byte数组指定位置,指定长度的byte数组
     * @param desc		目标数组
     * @param from		从标数据的什么位置开始
     * @param length	取指定长度
     * @param isReverse	是否高低位反转
     * @return
     */
    public static byte[] getArrayBySource(byte[] desc,int from,int length,boolean isReverse){
    	byte[] result = new byte[(desc.length >= length + from ? length : 0)];
    	if(result.length > 0){
	    	for(int i = from; i < from + length; i++){
	    		result[i - from] = desc[isReverse ? (length - i - 1) : i];
	    	}
    	}
    	return result;
    }
    
  /**
   * 两个byte数组合并
   * @param byte_1
   * @param byte_2
   * @return
   */
  	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
  		byte[] byte_3 = new byte[byte_1.length+byte_2.length];
  		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
  		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
  		return byte_3;
  	}
}

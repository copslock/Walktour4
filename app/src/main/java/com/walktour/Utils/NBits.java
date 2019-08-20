/*
 * @(#)Bits.java	1.7 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.walktour.Utils;
/**
 * Utility methods for packing/unpacking primitive values in/out of byte arrays
 * using big-endian byte ordering.
 */
public class NBits {
	 /*
     * Methods for unpacking primitive values from byte arrays starting at
     * given offsets.
     */

	public static boolean getBoolean(byte[] b, int off) {
	return b[off] != 0;
    }

	public  static char getChar(byte[] b, int off) {
	return (char) (((b[off + 0] & 0xFF) << 0) +
		       ((b[off + 1]) << 8));
    }

	public static short getShort(byte[] b, int off) {
	return (short) (((b[off + 0] & 0xFF) << 0) +
			((b[off + 1]) << 8));
    }

	public static int getInt(byte[] b, int off) {
	return ((b[off + 0] & 0xFF) << 0) +
	       ((b[off + 1] & 0xFF) << 8) +
	       ((b[off + 2] & 0xFF) << 16) +
	       ((b[off + 3]) << 24);
    }

	public static float getFloat(byte[] b, int off) {
	int i = ((b[off + 0] & 0xFF) << 0) +
		((b[off + 1] & 0xFF) << 8) +
		((b[off + 2] & 0xFF) << 16) +
		((b[off + 3]) << 24);
	return Float.intBitsToFloat(i);
    }

	public static long getLong(byte[] b, int off) {
	return ((b[off + 0] & 0xFFL) << 0) +
	       ((b[off + 1] & 0xFFL) << 8) +
	       ((b[off + 2] & 0xFFL) << 16) +
	       ((b[off + 3] & 0xFFL) << 24) +
	       ((b[off + 4] & 0xFFL) << 32) +
	       ((b[off + 5] & 0xFFL) << 40) +
	       ((b[off + 6] & 0xFFL) << 48) +
	       (((long) b[off + 7]) << 56);
    }

	public static double getDouble(byte[] b, int off) {
	long j = ((b[off + 0] & 0xFFL) << 0) +
		 ((b[off + 1] & 0xFFL) << 8) +
		 ((b[off + 2] & 0xFFL) << 16) +
		 ((b[off + 3] & 0xFFL) << 24) +
		 ((b[off + 4] & 0xFFL) << 32) +
		 ((b[off + 5] & 0xFFL) << 40) +
		 ((b[off + 6] & 0xFFL) << 48) +
		 (((long) b[off + 7]) << 56);
	return Double.longBitsToDouble(j);
    }

    /*
     * Methods for packing primitive values into byte arrays starting at given
     * offsets.
     */

	public static void putBoolean(byte[] b, int off, boolean val) {
	    b[off] = (byte) (val ? 1 : 0);
    }

	public static void putChar(byte[] b, int off, char val) {
	    b[off + 0] = (byte) (val >>> 0);
	    b[off + 1] = (byte) (val >>> 8);
    }

	public static void putShort(byte[] b, int off, short val) {
	    b[off + 0] = (byte) (val >>> 0);
	    b[off + 1] = (byte) (val >>> 8);
    }

	public static void putInt(byte[] b, int off, int val) {
        b[off + 0] = (byte) (val >>> 0);
        b[off + 1] = (byte) (val >>> 8);
        b[off + 2] = (byte) (val >>> 16);
        b[off + 3] = (byte) (val >>> 24);
    }
	public static void putIntBigEndian(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val >>> 0);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off + 0] = (byte) (val >>> 24);
    }
	public static void putFloat(byte[] b, int off, float val) {
        int i = Float.floatToIntBits(val);
        b[off + 0] = (byte) (i >>> 0);
        b[off + 1] = (byte) (i >>> 8);
        b[off + 2] = (byte) (i >>> 16);
        b[off + 3] = (byte) (i >>> 24);
    }

	public static void putLong(byte[] b, int off, long val) {
        b[off + 0] = (byte) (val >>> 0);
        b[off + 1] = (byte) (val >>> 8);
        b[off + 2] = (byte) (val >>> 16);
        b[off + 3] = (byte) (val >>> 24);
        b[off + 4] = (byte) (val >>> 32);
        b[off + 5] = (byte) (val >>> 40);
        b[off + 6] = (byte) (val >>> 48);
        b[off + 7] = (byte) (val >>> 56);
    }

	public static void putDouble(byte[] b, int off, double val) {
        long j = Double.doubleToLongBits(val);
        b[off + 0] = (byte) (j >>> 0);
        b[off + 1] = (byte) (j >>> 8);
        b[off + 2] = (byte) (j >>> 16);
        b[off + 3] = (byte) (j >>> 24);
        b[off + 4] = (byte) (j >>> 32);
        b[off + 5] = (byte) (j >>> 40);
        b[off + 6] = (byte) (j >>> 48);
        b[off + 7] = (byte) (j >>> 56);
    }
    /**
     * Method for trans number to raw string
     */
    static final char[] HEX = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    public static String buffToHexString(byte[] in){
		char[] str = new char[in.length*5];
        for(int i=0;i<in.length;i++){
            str[5*i]   = '0';
            str[5*i+1] = 'x';
            str[5*i+2] = HEX[(in[i]>>4)&0xf];
            str[5*i+3] = HEX[(in[i]&0xf)];
            str[5*i+4] = ' ';
        }
        str[in.length*5-1] = '\0';
		return String.valueOf(str,0,str.length-1);
	}
    public static String buffToHexRaw(byte[] in){
        char[] str = new char[in.length*2+1];
        for(int i=0;i<in.length;i++){
            str[2*i] = HEX[(in[i]>>4)&0xf];
            str[2*i+1] = HEX[(in[i]&0xf)];
        }
        str[in.length*2] = 'h';
        return String.valueOf(str);
    }
    public static String bitsToBinaryRaw(int in,int len){
        char[] buf = new char[len+1];
        for(int i=0;i<len;i++){
            if(((in >> (len-1-i))&1) == 1)
                buf[i] = '1';
            else
                buf[i] = '0';
        }
        buf[len] = 'b';
        return String.valueOf(buf);
    }
    public static String booleanToHexRaw(boolean in){
        byte[] buf = new byte[1];
        NBits.putBoolean(buf,0,in);
        return buffToHexRaw(buf);
    }
    public static String byteToHexRaw(byte in){
        byte[] buf = new byte[1];
        buf[0] = in;
        return buffToHexRaw(buf);
    }
    public static String shortToHexRaw(short in){
        byte[] buf = new byte[2];
        NBits.putShort(buf,0,in);
        return buffToHexRaw(buf);
    }
    public static String intToHexRaw(int in){
        byte[] buf = new byte[4];
        NBits.putInt(buf,0,in);
        return buffToHexRaw(buf);
    }
    public static String floatToHexRaw(float in){
        byte[] buf = new byte[4];
        NBits.putFloat(buf,0,in);
        return buffToHexRaw(buf);
    }
    public static String longToHexRaw(long in){
        byte[] buf = new byte[8];
        NBits.putLong(buf,0,in);
        return buffToHexRaw(buf);
    }
    public static String doubleToHexRaw(double in){
        byte[] buf = new byte[8];
        NBits.putDouble(buf,0,in);
        return buffToHexRaw(buf);
    }
}

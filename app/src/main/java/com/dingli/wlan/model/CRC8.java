package com.dingli.wlan.model;

/**
 * Utility class to calculate 8-bit CRC.
 * @author kc7bfi
 */

public class CRC8 {
	 /**
	     * Calculate the CRC value with data from a byte array.
	     * 
	     * @param data  The byte array
	     * @param len   The byte array length
	     * @return      The calculated CRC value
	     */
		public static byte calc(byte[] data , int len)
		{
			if (len > data.length)
				return 0 ; 
			byte crc = data[0];
			for (int i=1;i<len;i++) {
				 crc = (byte)((crc^data[i]) &0xff);
			}
			return crc;
		} 

}


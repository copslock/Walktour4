package com.walktour.service.test;

import java.util.Map;

/**
 *	Call the PESQ be JNI interface, because the JNI database is not stable,
	so the use of JNI interface remote services in a separate process 
 * @author qihang.li
 */
interface IPesqCalculator {
	/**
	 * Score 
	 * @param rawId 
	 * @param filePath To calculate the file path points 
	 * @return
	 */
	Map calculate(int rawId, String filePath);
}

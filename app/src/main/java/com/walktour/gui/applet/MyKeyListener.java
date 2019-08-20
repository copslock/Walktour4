package com.walktour.gui.applet;

import android.text.method.NumberKeyListener;

/**
 *监听键盘输入,只允许输入制定的字符 
 */

public class MyKeyListener{
	
	/**
	 * @只允许输入数字
	 * */
	public NumberKeyListener getNumberKeyListener(){ 
		return new NumberKeyListener(){
			@Override
			protected char[] getAcceptedChars() {
				char[] numberChars = {'1','2','3','4','5','6','7','8','9','0'};
		        return numberChars;
			}

			@Override
			public int getInputType() {
				return android.text.InputType.TYPE_CLASS_NUMBER;
			}
		};
	}
	
	/**
	 * @整数
	 * 只允许输入数字,负号,小数点
	 * */
	public NumberKeyListener getIntegerKeyListener(){ 
		return new NumberKeyListener(){
			@Override
			protected char[] getAcceptedChars() {
				char[] numberChars = {'1','2','3','4','5','6','7','8','9','0','-',};
		        return numberChars;
			}

			@Override
			public int getInputType() {
				return android.text.InputType.TYPE_CLASS_NUMBER;
			}
		};
	}
	
	/**
	 * @电话号码
	 * 只允许输入数字,*,
	 * */
	public NumberKeyListener getTelKeyListener(){ 
		return new NumberKeyListener(){
			@Override
			protected char[] getAcceptedChars() {
				char[] numberChars = {'1','2','3','4','5','6','7','8','9','0','+'};
		        return numberChars;
			}

			@Override
			public int getInputType() {
				return android.text.InputType.TYPE_CLASS_NUMBER;
			}
		};
	}
	
	/**
	 * @IP地址
	 * 只允许输入数字,*,
	 * */
	public NumberKeyListener getIpKeyListener(){ 
		return new NumberKeyListener(){
			@Override
			protected char[] getAcceptedChars() {
				char[] numberChars = {'1','2','3','4','5','6','7','8','9','0','.'};
		        return numberChars;
			}

			@Override
			public int getInputType() {
				return android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER;
			}
		};
	}
	
	
}
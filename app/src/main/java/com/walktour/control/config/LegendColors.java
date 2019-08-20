package com.walktour.control.config;

import android.content.Context;
import android.content.Intent;

import com.walktour.gui.R;
import com.walktour.model.DynamicColors;

public class LegendColors {
	private int nullityValue = -9999;
	private LegendColors(){
	}
	private static LegendColors instance;
	public synchronized static LegendColors getInstance(){
		if(instance == null){
			instance = new LegendColors();
		}
		return instance;
	}
	private DynamicColors[] dynamicLis = getInitDynamicColors();
	private int repeatNum = 0;
	/**
	 * 获得当前参数对应的散列值颜色
	 * @param value
	 * @return
	 */
	public synchronized int getDynamicColor(double value){
		//遍历当前颜色列表，如果存在所给值，则返回对应颜色，如果遍历到所在位置的值为无效值，则将该值置为当前值，并返回该值的颜色
		for(int i=0;i<dynamicLis.length;i++){
			if(dynamicLis[i].value == value){
				return dynamicLis[i].color;
			}else if(dynamicLis[i].value == nullityValue){
				dynamicLis[i].value = value;
				return dynamicLis[i].color;
			}
		}
		
		//到此处说明给定值不在列表中，且列表中不存在无效值通过重复使用记数器获取位置指定的颜色，并将该位置的值赋为当前值
		int returnPoint = repeatNum;
		dynamicLis[repeatNum].value = value;
		repeatNum +=1;
		if(repeatNum == dynamicLis.length)
			repeatNum = 0;
		
		return dynamicLis[returnPoint].color;
	}
	public synchronized int getDynamicColor(Context context,double value){
		//遍历当前颜色列表，如果存在所给值，则返回对应颜色，如果遍历到所在位置的值为无效值，则将该值置为当前值，并返回该值的颜色
		for(int i=0;i<dynamicLis.length;i++){
			if(dynamicLis[i].value == value){
				return dynamicLis[i].color;
			}else if(dynamicLis[i].value == nullityValue){
				dynamicLis[i].value = value;
				Intent intent = new Intent();
				intent.setAction(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE);
				context.sendBroadcast(intent);
				return dynamicLis[i].color;
			}
		}
		
		//到此处说明给定值不在列表中，且列表中不存在无效值通过重复使用记数器获取位置指定的颜色，并将该位置的值赋为当前值
		int returnPoint = repeatNum;
		dynamicLis[repeatNum].value = value;
		
		repeatNum +=1;
		if(repeatNum == dynamicLis.length)
			repeatNum = 0;
		//此处需要发送消息，让页面更新图例显示值
		Intent intent = new Intent();
		intent.setAction(com.walktour.gui.map.MapActivity.ACTION_MAP_COLOR_CHANGE);
		context.sendBroadcast(intent);
		
		return dynamicLis[returnPoint].color;
	}
	/**
	 * 获得散列值参数
	 * @return
	 */
	public synchronized DynamicColors[] getDynamicLis() {
		return dynamicLis;
	}
	/**
	 * 重置散列值
	 */
	public synchronized void reSetDynamicLis(){
		dynamicLis = getInitDynamicColors();
	}
	/**
	 * 初始化散列值的颜色数组
	 * @return
	 */
	private DynamicColors[] getInitDynamicColors(){
		return  new DynamicColors[]{
				new DynamicColors(R.color.light_red),new DynamicColors(R.color.light_green),
				new DynamicColors(R.color.red),new DynamicColors(R.color.green),
				new DynamicColors(R.color.green9),new DynamicColors(R.color.yellow),
				new DynamicColors(R.color.blue),new DynamicColors(R.color.cyan),
				new DynamicColors(R.color.gray),new DynamicColors(R.color.dark_gray),
				new DynamicColors(R.color.light_red2),new DynamicColors(R.color.magenta),
				new DynamicColors(R.color.light_yellow),new DynamicColors(R.color.light_zi),
				new DynamicColors(R.color.light_blue)};
	}
}

package com.walktour.model;


/**
 * 地图参数类
 * @author liqihang
 * */
public class PropertyMap{
	private String name;
	private int value1;
	private int value2;
	private int value3;
	private int value4;
	private int color1;
	private int color2;
	private int color3;
	private int color4;
	private int max;
	private int min;
	
	public PropertyMap(){
		
	}
	
	public PropertyMap(
			String name,
			int value1,
			int value2,
			int value3,
			int value4,
			int color1,
			int color2,
			int color3,
			int color4,
			int min,
			int max
			){
		this.name = name;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		this.color4 = color4;
		this.min = min;
		this.max = max;
	}
	
	public String getName(){return this.name;}
	public int getValue1(){return this.value1;}
	public int getValue2(){return this.value2;}
	public int getValue3(){return this.value3;}
	public int getValue4(){return this.value4;}
	public int getColor1(){return this.color1;}
	public int getColor2(){return this.color2;}
	public int getColor3(){return this.color3;}
	public int getColor4(){return this.color4;}
	public int getMin(){return this.min;}
	public int getMax(){return this.max;}
	
	public void setValue1(int value){this.value1 = value;}
	public void setValue2(int value){this.value2 = value;}
	public void setValue3(int value){this.value3 = value;}
	public void setValue4(int value){this.value4 = value;}
	public void setColor1(int value){this.color1 = value;}
	public void setColor2(int value){this.color2 = value;}
	public void setColor3(int value){this.color3 = value;}
	public void setColor4(int value){this.color4 = value;}
	public void setMin(int value){this.min = value;}
	public void setMax(int value){this.max = value;}
	
}
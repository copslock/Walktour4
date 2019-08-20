package com.walktour.model;


/**
 * 测试任务参数类
 * @author liqihang
 * */
public class Property{
	private String name;//参数名
	private boolean isDisplayOnChart;//是否显示在趋势图和柱状图
	private boolean isDisplayOnTable;//是否显示在表格中
	private int max;
	private int min;
	private String color;//曲线色:red,yellow,blue,white...
	
	public Property(){
		
	}
	
	public Property(String name,
			boolean isDisplayOnChart,
			boolean isDisplayOnTable,
			int max,
			int min,
			String color){
		this.name = name;
		this.isDisplayOnChart = isDisplayOnChart;
		this.isDisplayOnTable = isDisplayOnTable;
		this.max = max;
		this.min = min;
		this.color = color;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean isDisplayOnChart(){
		return this.isDisplayOnChart;
	}
	
	public boolean isDisplayOnTable(){
		return this.isDisplayOnTable;
	}
	
	public int getMax(){
		return this.max;
	}
	
	public int getMin(){
		return this.min;
	}
	
	public String getColor(){
		return this.color;
	}
	
	public void setName(String value){
		this.name = value;
	}
	
	public void setDisplayOnChart(boolean on){
		this.isDisplayOnChart = on;
	}
	
	public void setDisplayOnTable(boolean on){
		this.isDisplayOnTable = on;
	}
	
	public void setMax(int value){
		this.max= value;
	}
	
	public void setMin(int value){
		this.min= value;
	}
	
	public void setColor(String value){
		this.color = value;
	}
}
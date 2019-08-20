package com.walktour.model;

public class DynamicColors {
	public int color;
	public double value = -9999;
	public DynamicColors(int color){
		this.color = color; 
	}
	public DynamicColors(int color,double value){
		this.color = color;
		this.value = value;
	}
}

package com.dingli.wlan.apscan;

import android.net.wifi.ScanResult;

import java.util.Comparator;
/**
 * 对扫描到的接入点排序
 * @author Administrator
 *
 */
public class ScanResultComparator implements Comparator<ScanResult>{
	public static int SORT_ASC = 1;//升序
	public static int SORT_DESC = -1;//逆序
	
	public static final int LEVEL = 0;//对信号质量排序
	
	private int sortField;
	private int sortBy;
	public ScanResultComparator(int sortField,int sortBy){
		this.sortField = sortField;
		this.sortBy = sortBy;
	}
	public int compare(ScanResult sr1, ScanResult sr2) {
		int sortResult = 0;
		switch(sortField){
		case LEVEL:
			sortResult = sortBy*(sr1.level - sr2.level);
			break;
		}
		return sortResult;
	}
}

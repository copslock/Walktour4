package com.walktour.gui.map;

import java.io.Serializable;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 通用数据类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public  class GenericData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6322118988361065019L;
	private Queue<GenericPara> Para_Queue1 = new LinkedBlockingQueue<GenericPara>();
	private Queue<GenericPara> Para_Queue2 = new LinkedBlockingQueue<GenericPara>();
	private Queue<GenericPara> Para_Queue3 = new LinkedBlockingQueue<GenericPara>();
	private Queue<GenericPara> Para_Queue4 = new LinkedBlockingQueue<GenericPara>();
	private Queue<GenericPara> Current_Para = new LinkedBlockingQueue<GenericPara>();
	private Vector <GenericPara> TablePara = new Vector <GenericPara>();
	private Vector<GenericPara> ALITablePara = new Vector<GenericPara>();
	private Vector<String> eventPara= new Vector<String>();
	private final int EventShowLimit = 200;  //事件显示行数
    public GenericData()
	{
		
	}
	public  synchronized void clearPara_Queue1(){
		Para_Queue1.clear();
	}
	
	public  synchronized void clearPara_Queue2(){
		Para_Queue2.clear();
	}
	
	public  synchronized void clearPara_Queue3(){
		Para_Queue3.clear();
	}
	
	public  synchronized void clearPara_Queue4(){
		Para_Queue4.clear();
	}
	
	public  synchronized Queue<GenericPara> getPara_Queue1(){
		return Para_Queue1;
	}
	
	public  synchronized Queue<GenericPara> getPara_Queue2(){
		return Para_Queue2;
	}
	
	public  synchronized Queue<GenericPara> getPara_Queue3(){
		return Para_Queue3;
	}
	
	public  synchronized Queue<GenericPara> getPara_Queue4(){
		return Para_Queue4;
	}
	
	public  synchronized Queue<GenericPara> getCurrentPara(){
		return Current_Para;
	}
	
	public  synchronized void addPara_Queue1(GenericPara para){
		Para_Queue1.add(para);
	}
	
	public synchronized  void addPara_Queue2(GenericPara para){
		Para_Queue2.add(para);
	}
	
	public synchronized  void addPara_Queue3(GenericPara para){
		Para_Queue3.add(para);
	}
	
	public synchronized  void addPara_Queue4(GenericPara para){
		Para_Queue4.add(para);
	}
	
	public synchronized  void addCurrentPara(GenericPara para){
		Current_Para.add(para);
	}
	
	public synchronized  void removePara_Queue1(){
		Para_Queue1.remove();
	}
	
	public synchronized  void removePara_Queue2(){
		Para_Queue2.remove();
	}
	
	public synchronized  void removePara_Queue3(){
		Para_Queue3.remove();
	}
	
	public synchronized  void removePara_Queue4(){
		Para_Queue4.remove();
	}
	public synchronized  void removeCurrentPara(){
		Current_Para.remove();
	}
	
	public synchronized  void ClearCurrentPara(){
		Current_Para.clear();
	}
	
	public synchronized  void addTablePara(GenericPara para){
		TablePara.add(para);
	}
	
	public synchronized void addALITablePara(GenericPara para){
		ALITablePara.add(para);
	}
	
	public synchronized void clearTablePara(){
		TablePara.removeAllElements();
	}
	
	public synchronized void clearALITablePara(){
		ALITablePara.removeAllElements();
	}
	
	public synchronized GenericPara getTablePara(int i){
		return TablePara.elementAt(i);
	}
	
	public synchronized GenericPara getALITablePara(int i){
		return ALITablePara.elementAt(i);
	}
	
	public synchronized int getTableParaSize(){
		return TablePara.size();
	}
	
	public synchronized int getALITableParaSize(){
		return ALITablePara.size();
	}
}


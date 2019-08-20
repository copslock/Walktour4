package com.walktour.model;

import java.util.ArrayList;

/**
 * 统计自定义事件的Model,记录一个自定义事件发生的次数，及各次的详情
 * @author qihang.li
 */
public class TotalCustomModel {
	private String name = "";
	private ArrayList<OneEvent> eventList = new ArrayList<OneEvent>();
	private boolean visible = false;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return eventList.size();
	}

	public ArrayList<OneEvent> getEventList() {
		return eventList;
	}
	
	public void addEventSortTime(OneEvent newOne){
		/*boolean hasInsert = false;
		for( int i=0;i<eventList.size();i++){
			OneEvent one = eventList.get(i);
			if( newOne.time > one.time){
				eventList.add(i+1, newOne);
				hasInsert = true;
				break;
			}
		}
		if( !hasInsert ){
			eventList.add( 0,newOne );
		}*/
		
		eventList.add(newOne);
	}
	
	/**
	 * 平均时延
	 */
	public int getDelayAverage(){
		int total = 0;
		for( OneEvent e:eventList){
			total += e.delay;
		}
		return total/eventList.size();
	}
	
	public OneEvent InfoBuilder(){
		return new OneEvent();
	}

	public class OneEvent{
		public long time = 0;
		public int delay = 0;
		public double logitude = 0f;
		public double latitude = 0f;
	}
	
	public boolean toogleVisible(){
		return this.visible = !this.visible;
	}

	public boolean isVisible() {
		return visible;
	}
	
}

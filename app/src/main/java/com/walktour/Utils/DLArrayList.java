package com.walktour.Utils;

import java.util.ArrayList;
import java.util.Collection;

public class DLArrayList<T> extends ArrayList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 存储最大数据量
	 * */
	private final int MAX_SIZE = 2000;

	@Override
	public boolean add(T object) {
		// TODO Auto-generated method stub
		if (this.size() >= MAX_SIZE)
			removeObject();
		return super.add(object);
	}
	
	@Override
	public void add(int index, T object) {
		// TODO Auto-generated method stub
		super.add(index, object);
		if (this.size() >= MAX_SIZE)
			removeObject();
	}
	
	@Override
	public boolean addAll(Collection<? extends T> collection) {
		// TODO Auto-generated method stub
		if (this.size() + collection.size() > MAX_SIZE)
			removeObjects(this.size() + collection.size() - MAX_SIZE);
		return super.addAll(collection);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends T> collection) {
		// TODO Auto-generated method stub
		boolean result = super.addAll(index, collection);
		if (this.size() > MAX_SIZE)
			removeObjects(this.size() - MAX_SIZE);
		return result;
	}
	
	private void removeObject() {
		this.remove(0);
	}

	private void removeObjects(int size) {
		// TODO Auto-generated method stub
		for (int i = 0; i < size; i++) {
			this.remove(i);
		}
	}
}

/**
 * 
 */
package com.walktour.framework.view.draganddropgridview;

import com.walktour.model.Parameter;

public class Item {

	int mIcon;
	int mSpans;
	String mTitle;
	private Parameter parameter;

	public Item(int icon, int spans, String title, Parameter parameter) {

		mIcon = icon;
		mSpans = spans;
		mTitle = title;
		this.parameter = parameter;
	}

	public int getSpans() {
		return mSpans;
	}

	public int getIcon() {
		return mIcon;
	}

	public String getTitle() {
		return mTitle;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	
}

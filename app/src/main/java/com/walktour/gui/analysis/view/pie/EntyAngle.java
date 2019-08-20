package com.walktour.gui.analysis.view.pie;

public class EntyAngle {
	
	public EntyAngle(float startAngle, float sweepAngle){
		this.startAngle = startAngle;
		this.sweepAngle = sweepAngle;
	}

	private float startAngle;
	
	private float sweepAngle;

	public float getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(float startAngle) {
		this.startAngle = startAngle;
	}

	public float getSweepAngle() {
		return sweepAngle;
	}

	public void setSweepAngle(float sweepAngle) {
		this.sweepAngle = sweepAngle;
	}
	
	
	
}

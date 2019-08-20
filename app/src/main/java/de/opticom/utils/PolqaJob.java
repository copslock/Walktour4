package de.opticom.utils;

public class PolqaJob implements Cloneable {
	public PolqaInput input = new PolqaInput();
	public PolqaResult result = new PolqaResult();
	
	public PolqaJob() {
//		reset();
	}
	
	public void reset() {
		input.reset();
		result.reset();
	}
	
	@Override
	public PolqaJob clone() {
		PolqaJob job;
		try {
			job = (PolqaJob) super.clone();
			job.input = this.input.clone();
			job.result = this.result.clone();
			return job;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
}

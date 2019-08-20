package de.opticom.utils;

import de.opticom.polqa.PolqaWrapper;

public class PolqaInput implements Cloneable {
	public String referenceFilename;
	public String testFilename;
	public boolean superwideband;
	public int ituVersion;
	public String passthroughData;
	public int sampleRate;
	public boolean disableLevelAlignment;
	public boolean disableSrConversion;
	public boolean enableHaMode;
	public String[] skriptLine;
	// ...
	
	public PolqaInput() {
		reset();
	}
	
	public void reset() {
		referenceFilename = "";
		testFilename = "";
		superwideband = false;
		ituVersion = PolqaWrapper.POLQA_V1_1;
		passthroughData = "";
		skriptLine = null;
		sampleRate = 0;
		disableLevelAlignment = true;
		disableSrConversion = true;
		enableHaMode = false;
	}
	
	@Override
	public PolqaInput clone() {
		try {
			return (PolqaInput) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
}

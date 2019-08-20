package de.opticom.utils;

import de.opticom.polqa.PolqaWrapper;

public class PolqaResult implements Cloneable {
	// Additional error codes to PolqaWrapper
	public final static int POLQA_LOAD_AUDIO_FAILED = PolqaWrapper.POLQA_LAST_ERROR;
	public final static int POLQA_JOB_NOT_STARTED = PolqaWrapper.POLQA_LAST_ERROR + 1;
	
	public double mfMOSLQO;
	public double mfAvgDelay;
	public double mfMinDelay;
	public double mfMaxDelay;
	public double mfAttenuation;
	
	public double mfLevelReference;
	public double mfLevelDegraded;
	public double mfSnrReference;
	public double mfSnrDegraded;
	
	public double  mfActiveSpeechRatioRef;
	public double  mfActiveSpeechRatioDeg;
	
	public double dDeltaTime;
	public double loadWaveDuration;
	public double polqaInitDuration;
	public double polqaRunDuration;
	public double polqaGetResultsDuration;
	public int nrSamplesRef;
	public int nrSamplesDeg;
	public int sampleRateRef;
	public int sampleRateDeg;
	public int result;
	
	public PolqaResult() {
		reset();
	}
	
	public void reset() {
		mfMOSLQO = 0.0;
		mfAvgDelay = 0.0;
		mfMinDelay = 0.0;
		mfMaxDelay = 0.0;
		mfAttenuation = 0.0;
		mfLevelReference = 0.0;
		mfLevelDegraded = 0.0;
		mfSnrReference = 0.0;
		mfSnrDegraded = 0.0;
		mfActiveSpeechRatioRef = 0.0;
		mfActiveSpeechRatioDeg = 0.0;
		dDeltaTime = 0.0;
		loadWaveDuration = 0.0;
		polqaInitDuration = 0.0;
		polqaRunDuration = 0.0;
		polqaGetResultsDuration = 0.0;
		nrSamplesRef = 0;
		nrSamplesDeg = 0;
		sampleRateRef = 0;
		sampleRateDeg = 0;
		result = POLQA_JOB_NOT_STARTED;
	}
	
	@Override
	protected PolqaResult clone() {
		try {
			return (PolqaResult) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}
}

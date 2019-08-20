package de.opticom.polqa;

public class PolqaResultData 
{
	public float mfVersion;								/* 10 * version of result structure (e.g. 16.0 = V1.6.x) */
	public float mfP863Version;							/* 10 * version of the equivalent ITU reference code */
	public int mulMode;						    		/* Processing mode specified in call to PolqaLibInit()
													   	   (an OR combiunation of the control flags */
	public int mulSampleRate;							/* Processing sample rate in Hz */

	public float mfMOSLQO;								/* POLQA score (MOS-LQO) */

	public float mfMinDelay;							/* Minimum delay in ms */
	public float mfMaxDelay;							/* Minimum delay in ms */
	public float mfAvgDelay;							/* Average delay in ms */
	
	public float mfEModelRValue;						/* G.107 Rating (NB mode only!) */

	public float mfPitchReference;						/* Pitch frequency of the reference signal in Hz*/
	public float mfPitchDegraded;						/* Pitch frequency of the degraded signal in Hz */

	public float mfEstimatedSampleRate;					/* Estimated sample rate of the degraded signal */
	public int miResamplingApplied;						/* 1 if internal resampling was applied */


	/*----------------------------------------------------------------*/
	/* Values below are computed only if POLQA_RLEVEL2 was specified! */

	public float mfLevelReference;						/* Level of reference file in dBov (entire signal)*/
	public float mfLevelDegraded;						/* Level of degraded file in dBov  (entire signal)*/
	public float mfP56ActiveSpeechLevelRefdB;           /* Level of the active speech parts of the reference signal in dB */
	public float mfP56ActiveSpeechLevelDegdB;           /* Level of the active speech parts of the degraded signal in dB */
	public float mfP56PauseLevelRefdB;					/* Level of the pause parts of the reference signal in dB */
	public float mfP56PauseLevelDegdB;					/* Level of the pause parts of the degraded signal in dB */

	public float mfAttenuation;							/* Attenuation in dB */
	
	public float mfSnrReference;						/* SNR of the reference signal in dB */
	public float mfSnrDegraded;							/* SNR of the degraded signal in dB */

	public float mfActiveSpeechRatioRef;				/* Active speech ratio of the deference signal */
	public float mfActiveSpeechRatioDeg;				/* Active speech ratio of the degraded signal */

	/* VAD Info */
	public int mulElementsInVADVectors;					/* Number of elements in mpiVADSpeechStart, mpiVADSpeechStartDeg and mpiVADSpeechLength */
	public int[] mpiVADSpeechStartRef;					/* Vector with the index of each detected active speech section of the reference signal  */
	public int[] mpiVADSpeechStartDeg;					/* Vector with the index of each detected active speech section of the degraded signal  */
	public int[] mpiVADSpeechLengthRef;					/* Vector with the length of each detected active speech section of the reference signal  */
	public int[] mpiVADSpeechLengthDeg;					/* Vector with the length of each detected active speech section of the degraded signal  */
	
	/*----------------------------------------------------------------*/
	/* Values below are computed only if POLQA_RLEVEL3 was specified! */

	public int mulElementsInDelayBuffer;				/* Number of Elements in mpfDelayVsTimeBuffer */
	public float[] mpfDelayVsTimeBufferInp;				/* The delay of each sample in ms.
														   NOTE: Values are related to the input signals 
														   *without* any potential samplerate correction,
														   but including any mode dependent resampling to 8kHz or 48kHz!
														   The delay is the temporal offset of the reference
														   signal compared to the degraded signal. */
	public float[] mpfDelayVsTimeBufferRes;				/* The delay of each sample in ms.
														   NOTE: Values are related to the input signals 
														   *including* any resampling!
														   The delay is the temporal offset of the reference
														   signal compared to the degraded signal. */
	public int mulElementsInRefAlignedTimeBuffer;		/* Number of elements in mpfRefAlignedTimeBuffer */
	public float[] mpfRefAlignedTimeBuffer;				/* Buffer to hold the temporal (and optionally level)
														   aligned reference samples */

	public int mulElementsInDegAlignedTimeBuffer;		/* Number of elements in mpfDegAlignedTimeBuffer */
	public float[] mpfDegAlignedTimeBuffer;				/* Buffer to hold the temporal (and optionally level)
														   aligned degraded samples */
	
	public int mulMOSPerFrameBufferSize;				/* Number of elements in mfpMOSPerFrame */
	public float[] mfpMOSPerFrame;						/* Buffer to hold the local MOS per frame */
	
	public int mulFrameSize;							/* Frame size of the psychoacoustic models in samples (incl. 50% overlap) */
	public int mulNrFramesInSpectrogram;				/* Number of frames in the spectra (mppfSpectrumRef, mppfSpectrumDeg, mppfNoiseLoudnessXX) */
	
	public int mulNrLinesInSpectra;						/* Number of spectral lines in the spectra (mppfSpectrumXX, mppfLoudnessDensityXX) */
	public float[][] mppfSpectrumRef;					/* Spectrogram of the reference signal (frequency scale)*/
	public float[][] mppfSpectrumDeg;					/* Spectrogram of the degraded signal (frequency scale)*/
	
	public int mulNrBarkBands;							/* Number of Bands in the loudness arrays (mppfLoudnessDensityXX, mppfNoiseLoudnessGraph)*/
	public float[][] mppfLoudnessDensityRef;			/* Estimate of the specific loudness of reference signal (bark scale)*/
	public float[][] mppfLoudnessDensityDeg;			/* Estimate of the specific loudness of degraded signal (bark scale)*/
	public float[][] mppfNoiseLoudnessGraph;			/* Estimate of the of the Noise Loudness (bark scale)*/
	
	public int mulWarningStatus;						/* Bitmap for Warning Elements */
	
	public double dDeltaTime;							/* Processing time for PolqaRun module. Reguires POLQA_CHECK_PROCESSING_TIME to be set*/
	
	public PolqaResultData() { reset(); }
	
	private void resetArray2D(float[][] array) {
		if(array != null) {
			for(int i=0; i<array.length; ++i) {
				array[i] = null;
			}
		}
	}

	public void reset() {
		// 1D arrays
		mpfDelayVsTimeBufferInp = null;
		mpfDelayVsTimeBufferRes = null;
		mpfRefAlignedTimeBuffer = null;
		mpfDegAlignedTimeBuffer = null;
		mfpMOSPerFrame = null;
		mpiVADSpeechStartRef = null;
		mpiVADSpeechStartDeg= null;
		mpiVADSpeechLengthRef = null;
		mpiVADSpeechLengthDeg = null;

		// 2D arrays
		resetArray2D(mppfSpectrumRef);
		mppfSpectrumRef = null;
		
		resetArray2D(mppfSpectrumDeg);
		mppfSpectrumDeg = null;
		
		resetArray2D(mppfLoudnessDensityRef);
		mppfLoudnessDensityRef = null;
		
		resetArray2D(mppfLoudnessDensityDeg);
		mppfLoudnessDensityDeg = null;
		
		resetArray2D(mppfNoiseLoudnessGraph);
		mppfNoiseLoudnessGraph = null;
	}
};

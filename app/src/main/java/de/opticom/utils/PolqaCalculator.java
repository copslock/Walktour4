package de.opticom.utils;

import de.opticom.io.AudioDataMono;
import de.opticom.polqa.PolqaResultData;
import de.opticom.polqa.PolqaWrapper;

public class PolqaCalculator {
	private Object telephonyManager;
	private String licenseFileName;
	
	public PolqaCalculator(String licenseFileName, Object telephonyManager) {
		this.licenseFileName = licenseFileName;
		this.telephonyManager = telephonyManager;
	}
	
	public int Calc(PolqaJob job) {
		long t1, t2;
		int errorCode = PolqaWrapper.POLQA_OK;
		int result;
		PolqaWrapper polqaWrapper = new PolqaWrapper();
		AudioDataMono referenceFile = new AudioDataMono();
		AudioDataMono degradedFile = new AudioDataMono();

		try {
			// Load reference file
			t1 = System.currentTimeMillis();
			if (job.input.referenceFilename.toLowerCase().endsWith(".wav")) {
				result = referenceFile.read(job.input.referenceFilename, AudioDataMono.TYPE_WAVE, AudioDataMono.MODE_UNKNOWN, 0, 0);
			} else if (job.input.sampleRate != 0) {
				result = referenceFile.read(job.input.referenceFilename, AudioDataMono.TYPE_PCM, AudioDataMono.MODE_INTEGER16_LITLE_ENDIAN, job.input.sampleRate, 1);
			} else {
				result = AudioDataMono.AUDIO_ERROR;
			}
			if(result != AudioDataMono.AUDIO_OK) {
				job.result.result = PolqaResult.POLQA_LOAD_AUDIO_FAILED;
				return job.result.result;
			}
			
			// Load degraded file 
			if (job.input.testFilename.toLowerCase().endsWith(".wav")) {
				result = degradedFile.read(job.input.testFilename, AudioDataMono.TYPE_WAVE, AudioDataMono.MODE_UNKNOWN, 0, 0);
			} else if (job.input.sampleRate !=0) {
				result = degradedFile.read(job.input.testFilename, AudioDataMono.TYPE_PCM, AudioDataMono.MODE_INTEGER16_LITLE_ENDIAN, job.input.sampleRate, 1);
			} else {
				result = AudioDataMono.AUDIO_ERROR;
			}
			if(result != AudioDataMono.AUDIO_OK) {
				job.result.result = PolqaResult.POLQA_LOAD_AUDIO_FAILED;
				return job.result.result;
			}
			t2 = System.currentTimeMillis();
			job.result.loadWaveDuration = (t2 - t1)/1000.0;
			
			// Init POLQA
			int mode = (job.input.superwideband ? PolqaWrapper.POLQA_LC_SWIDE_H : PolqaWrapper.POLQA_LC_STANDARD_IRS);
			mode |= PolqaWrapper.POLQA_RLEVEL1;
			mode |= PolqaWrapper.POLQA_RLEVEL2;
			mode |= (job.input.disableLevelAlignment ? 0 : PolqaWrapper.POLQA_LEVEL_ALIGN);
			mode |= (job.input.disableSrConversion ? PolqaWrapper.POLQA_AUTO_SR_CONVERSION_OFF : 0);
			mode |= (job.input.enableHaMode ? (PolqaWrapper.POLQA_HA_MODE | PolqaWrapper.POLQA_HA_OMP) : 0);
			mode |= job.input.ituVersion;

			t1 = System.currentTimeMillis();
			errorCode = polqaWrapper.PolqaLibInit(licenseFileName, mode, telephonyManager);
			t2 = System.currentTimeMillis();
			if(errorCode != PolqaWrapper.POLQA_OK) {
				job.result.result = errorCode;
				return job.result.result;
			}
			job.result.polqaInitDuration = (t2 - t1)/1000.0;
	
			// Run POLQA
			t1 = System.currentTimeMillis();
			errorCode = polqaWrapper.PolqaLibRun(
					referenceFile.samples, 0, referenceFile.samples.length, referenceFile.sampleRate,
					degradedFile.samples, 0, degradedFile.samples.length, degradedFile.sampleRate);
			t2 = System.currentTimeMillis();
			if(errorCode != PolqaWrapper.POLQA_OK) {
				polqaWrapper.PolqaLibFree();
				job.result.result = errorCode;
				return job.result.result;
			}
			job.result.polqaRunDuration = (t2 - t1)/1000.0;
			
			// Get the results
			PolqaResultData polqaResultData = new PolqaResultData();
			t1 = System.currentTimeMillis();
			polqaWrapper.PolqaLibGetResult(polqaResultData);
			t2 = System.currentTimeMillis();
			job.result.polqaGetResultsDuration = (t2 - t1)/1000.0;
			
			job.result.mfMOSLQO = polqaResultData.mfMOSLQO;
			job.result.mfAttenuation = polqaResultData.mfAttenuation;
			job.result.mfAvgDelay = polqaResultData.mfAvgDelay;
			job.result.mfMinDelay = polqaResultData.mfMinDelay;
			job.result.mfMaxDelay = polqaResultData.mfMaxDelay;
			job.result.nrSamplesRef = referenceFile.samples.length;
			job.result.nrSamplesDeg = degradedFile.samples.length;
			job.result.sampleRateRef = referenceFile.sampleRate;
			job.result.sampleRateDeg = degradedFile.sampleRate;
			job.result.mfSnrReference = polqaResultData.mfSnrReference;
			job.result.mfSnrDegraded = polqaResultData.mfSnrDegraded;
			job.result.mfActiveSpeechRatioRef = polqaResultData.mfActiveSpeechRatioRef;
			job.result.mfActiveSpeechRatioDeg = polqaResultData.mfActiveSpeechRatioDeg;
			job.result.result = PolqaWrapper.POLQA_OK;
			
			// Close POLQA
			errorCode = polqaWrapper.PolqaLibFree();
			if(errorCode != PolqaWrapper.POLQA_OK) {
				job.result.result = errorCode;
			}
		}
		finally {
			polqaWrapper.PolqaLibFree();
			referenceFile.Reset();
			degradedFile.Reset();
		}
		
		return job.result.result;
	}
}

package de.opticom.polqa;

public class PolqaWrapper {
	
	// POLQA mode
    public final static int POLQA_LC_STANDARD_IRS 	= 0x0002;
    public final static int POLQA_LC_SWIDE_H 		= 0x0003;

    // Result verbosity
    public final static int POLQA_DEF     		= 0x000;	/* Default level - use internal preset (currently POLQA_RLEVEL1 | POLQA_RLEVEL2 | POLQA_RLEVEL3) */
    public final static int POLQA_RLEVEL1 		= 0x100;	/* Lowest informational level - highest computational speed */
    public final static int POLQA_RLEVEL2 		= 0x200;	/* Medium informational level - lower computational speed */
    public final static int POLQA_RLEVEL3 		= 0x400;	/* High informational level - low computational speed */
    public final static int POLQA_RLEVEL_ALL 	= POLQA_RLEVEL1 | POLQA_RLEVEL2 | POLQA_RLEVEL3;

    /* Specify whether to use automatic level alignment or not */
    /* Note: Using the automatuic mode is non-standard! */
    public final static int POLQA_LEVEL_ALIGN = 0x1000;  /* Switch on automatic level alignment between reference and test signal */
    								 /* Note: If this is set, POLQA_RLEVEL2 will be used automatically!) */

    /* Specify whether to use automatic samplerate conversion */
    /* Note: Using the automatic mode is non-standard! */
    public final static int POLQA_AUTO_SR_CONVERSION_OFF = 0x2000; /* NOTE: default is on */
    
    /* Specify if the acurate processing time shall be measured (adds up to 2s processing time per run) */
    public final static int POLQA_CHECK_PROCESSING_TIME = 0x40;

    /* Some helpers to mask off bits of ulMode that specify ....       */
    public final static int POLQA_LC_MASK 		= 0x000F;	/* ... the listening condition */
    public final static int POLQA_RLEVEL_MASK 	= 0x0700;	/* ... the result level        */
    public final static int POLQA_VERSION_MASK	= 0x70000;	/* ... POLQA version		   */
    
    /* POLQA high accuracy mode */
	public final static int POLQA_HA_MODE	= 0x4000;		/* Enable POLQA high accuracy mode */
	public final static int POLQA_HA_OMP	= 0x8000;		/* Use multiple threads (OpenMP) for high accuracy mode */

	/* Specify the POLQA Version to run */
	public final static int POLQA_V1_1	= 0x10000;			// Use POLQA V1.1
	public final static int POLQA_V2_4	= 0x20000;			// Use POLQA V2.4
	public final static int POLQA_V3	= 0x30000;			// For future versions

    // Error codes
	public final static int	POLQA_OK=0;
	public final static int POLQA_MEMORY_ALLOCATION_FAILED		= 1;
	public final static int POLQA_REGISTRATION_FAILED			= 2;
	public final static int POLQA_INITIALISATION_FAILED			= 3;
	public final static int POLQA_CALCULATION_FAILED			= 4;
	public final static int POLQA_CREATE_LICENSE_INFO_FAILED	= 5;
	public final static int POLQA_INPUT_SIGNALS_TOO_LONG		= 6;
	public final static int POLQA_INPUT_SIGNALS_TOO_SHORT		= 7;
	public final static int POLQA_SAMPLE_RATE_NOT_SUPPORTED		= 8;
	public final static int POLQA_WRONG_HANDLE					= 9;
	public final static int POLQA_FILE_OPEN_FAILED				= 10;
	public final static int POLQA_RESULT_LEVEL_TOO_LOW			= 11;
	public final static int POLQA_LIMIT_EXCEEDED				= 12;
	public final static int POLQA_LIMIT_INTERNAL_ERROR			= 13;
	public final static int POLQA_COMMANDLINE_FAILED			= 14;
	public final static int POLQA_ACCESS_VIOLATION				= 15;
	public final static int POLQA_EXCEPTION						= 16;
	public final static int POLQA_FP_EXCEPTION					= 17;
	public final static int POLQA_MAX_UPSAMPLING_EXCEEDED		= 18;
	public final static int POLQA_REF_LEVEL_TOO_LOW				= 19;
	public final static int POLQA_DEG_LEVEL_TOO_LOW				= 20;
	public final static int POLQA_LAST_ERROR					= 21;

	// Warnings issued if the processing of the input signals was possible, but the results
	// may not be reliable since the signals did not conform to P.863
	public final static int POLQA_NO_WARNINGS					= 0x00;
	public final static int POLQA_WARNING_REFERENCE_TOO_LONG	= 0x01;
	public final static int POLQA_WARNING_DEGRADED_TOO_LONG 	= 0x02;
	public final static int POLQA_WARNING_REFERENCE_LEVEL_HIGH	= 0x04;
	public final static int POLQA_WARNING_DEGRADED_LEVEL_HIGH	= 0x08;
	public final static int POLQA_WARNING_REFERENCE_LEVEL_LOW	= 0x10;
	public final static int POLQA_WARNING_DEGRADED_LEVEL_LOW	= 0x20;

	public final static String getErrorString( int errorCode ){
		switch( errorCode ){
		case POLQA_MEMORY_ALLOCATION_FAILED		:return "POLQA_MEMORY_ALLOCATION_FAILED";
		case  POLQA_REGISTRATION_FAILED			: return  "REGISTRATION_FAILED";
		case  POLQA_INITIALISATION_FAILED		: return  "INITIALISATION_FAILED";
		case  POLQA_CALCULATION_FAILED			: return  "CALCULATION_FAILED";
		case  POLQA_CREATE_LICENSE_INFO_FAILED	: return  "CREATE_LICENSE_INFO_FAILED";
		case  POLQA_INPUT_SIGNALS_TOO_LONG		: return  "INPUT_SIGNALS_TOO_LONG";
		case  POLQA_INPUT_SIGNALS_TOO_SHORT		: return  "INPUT_SIGNALS_TOO_SHORT";
		case  POLQA_SAMPLE_RATE_NOT_SUPPORTED	: return  "SAMPLE_RATE_NOT_SUPPORTED";
		case  POLQA_WRONG_HANDLE				: return  "WRONG_HANDLE";
		case  POLQA_FILE_OPEN_FAILED			: return  "FILE_OPEN_FAILED";
		case  POLQA_RESULT_LEVEL_TOO_LOW		: return  "RESULT_LEVEL_TOO_LOW";
		case POLQA_LIMIT_EXCEEDED				: return  "LIMIT_EXCEEDED";
		case POLQA_LIMIT_INTERNAL_ERROR			: return  "LIMIT_INTERNAL_ERROR";
		case POLQA_COMMANDLINE_FAILED			: return  "COMMANDLINE_FAILED";
		case POLQA_ACCESS_VIOLATION				: return  "ACCESS_VIOLATION";
		case POLQA_EXCEPTION					: return  "EXCEPTION";
		case POLQA_FP_EXCEPTION					: return  "FP_EXCEPTION";
		case POLQA_MAX_UPSAMPLING_EXCEEDED		: return  "MAX_UPSAMPLING_EXCEEDED";
		case POLQA_REF_LEVEL_TOO_LOW			: return  "REF_LEVEL_TOO_LOW";
		case POLQA_DEG_LEVEL_TOO_LOW			: return  "DEG_LEVEL_TOO_LOW";
		case  POLQA_LAST_ERROR					: return  "LAST_ERROR";
		default: return "Unknown";
		}
	}
	
	public int PolqaLibInit(String regFileName, int ulMode, Object telephonyManager)
	{
		if(handle != 0) return POLQA_WRONG_HANDLE;
		// Attention: internalPolqaLibInit() modifies handle!
		int result = internalPolqaLibInit(regFileName, ulMode, telephonyManager);
		return result;
	}

	public int PolqaLibRun(
			float[] refData, int refOffset, int refCount, int refSampleRate,
			float[] degData, int degOffset, int degCount, int degSampleRate)
	{
		return internalPolqaLibRun(
				refData, refOffset, refCount, refSampleRate,
				degData, degOffset, degCount, degSampleRate);
	}
	
	public int PolqaLibGetResult(PolqaResultData result)
	{
		return internalPolqaLibGetResult(result);
	}
	
	public int PolqaLibGetDelayHistogram(int FirstSample, int NumSamples, PolqaDelayHistogram histogram)
	{
		return internalPolqaLibGetDelayHistogram(FirstSample, NumSamples, histogram);
	}
	
	public int PolqaLibFree() {
		if (handle == 0) return POLQA_WRONG_HANDLE;
		// Attention: internalPolqaLibFree() modifies handle!
		int result = internalPolqaLibFree();
		return result;
	}
	
	public int PolqaCreateLicenseKeyInfo(String licenseInfoFilename, String licenseFileName, Object telephonyManager)
	{
		int result = internalPolqaCreateLicenseKeyInfo(licenseInfoFilename, licenseFileName, telephonyManager);
		return result;
	}

	public double PolqaLibGetSecondsToWait() {
		if (handle == 0) return 0.0;
		return internalPolqaLibGetSecondsToWait();
	}

	public long getHandle() { return handle; }
	private long handle = 0;

	private static native void initIDs();
	private native int internalPolqaLibInit(String regFileName, int ulMode, Object telephonyManager);
	private native int internalPolqaLibRun(
			float[] refData, int refOffset, int refCount, int refSampleRate,
			float[] degData, int degOffset, int degCount, int degSampleRate);
	private native int internalPolqaLibGetResult(PolqaResultData result);
	private native int internalPolqaLibGetDelayHistogram(int FirstSample, int NumSamples, PolqaDelayHistogram histogram);

	private native int internalPolqaLibFree();
	private native int internalPolqaCreateLicenseKeyInfo(String licenseInfoFilename, String licenseFileName, Object telephonyManager);
	private native double internalPolqaLibGetSecondsToWait();

	static {
		String dataModel = System.getProperty("sun.arch.data.model");
		String ext = ((dataModel != null && dataModel.equals("64")) ? "64" : "");
		System.loadLibrary("PolqaOemJava" + ext);
		initIDs();
	}
}

package de.opticom.io;

public class IOLibWrapper {
    // error codes
    public static final int AUDIO_OK 					= 0;	/* Okay */
	public static final int AUDIO_READ_ERROR			= 1;	/* Error while reading data from file */
	public static final int AUDIO_WRITE_ERROR			= 2;	/* Error while writing to file */
	public static final int AUDIO_FILEOPEN_ERROR		= 3;	/* Could not open file. Wrong file name? */
	public static final int AUDIO_MEMORY_ERROR			= 4;	/* Memory allocation error */
	public static final int AUDIO_NR_CHANNELS_ERROR		= 5;	/* function used with wrong number of channels */	
	public static final int AUDIO_UNKNOWN_TYPE			= 6;	/* Type not set or wrong type set. Not all types are supported by every routine. */
	public static final int AUDIO_UNKNOWN_MODE			= 7;	/* Mode not set or wrong type set. Not all modes are supported by every routine. */
	public static final int AUDIO_INITIALISATON_ERROR	= 8;	/* ? */
	public static final int AUDIO_PROCESSING_ERROR		= 9;	/* ? */
	public static final int AUDIO_INPUT_ARGS			= 10;	/* Input arguments to routine wrong. NULL pointer in most cases. */
	public static final int AUDIO_ERROR					= 11;	/* Internal unknown error. */
	public static final int AUDIO_UNKNOWN				= 99;	/* Internal unknown error. */
    
    // audio modes
    public static final int MODE_UNKNOWN 				= 0;
    public static final int MODE_IBM_ALAW 				= 1;
    public static final int MODE_IBM_MULAW 				= 2;
    public static final int MODE_INTEGER8 				= 3;
    public static final int MODE_INTEGER16_LITLE_ENDIAN	= 4;
    public static final int MODE_INTEGER16_BIG_ENDIAN 	= 5;
    public static final int MODE_INTEGER24_LITLE_ENDIAN	= 6;
    public static final int MODE_INTEGER24_BIG_ENDIAN	= 7;
    public static final int MODE_INTEGER32_LITLE_ENDIAN	= 8;
    public static final int MODE_INTEGER32_BIG_ENDIAN	= 9;
    public static final int MODE_FLOAT32_LITLE_ENDIAN	= 10;
    public static final int MODE_FLOAT32_BIG_ENDIAN		= 11;
    
    // audio types
    public static final int TYPE_UNKNOWN	= 0;
    public static final int TYPE_WAVE		= 1;
    public static final int TYPE_PCM		= 2;
    public static final int TYPE_TEXT		= 3;

	public int GetAudioDataWave(
			String referenceFilename,
			String degradedFilename)
	{
		Reset();
		int result = internalGetAudioData(referenceFilename, degradedFilename, false);
		return result;
	}
	
	public int GetAudioDataPCM(
			String referenceFilename,
			String degradedFilename,
			int sampleRate)
	{
		Reset();
		int result = internalGetAudioData(referenceFilename, degradedFilename, true);
		if(result==AUDIO_OK) this.sampleRate = sampleRate;
		return result;
	}
	
	public float[] refSamples = null;
	public float[] degSamples = null;
	public int sampleRate = 0;
	
	public void Reset() {
		refSamples = null;
		degSamples = null;
		sampleRate = 0;
	}
	
	private native int internalGetAudioData(
			String referenceFilename,
			String degradedFilename,
			boolean pcm);
	
	static {
		String dataModel = System.getProperty("sun.arch.data.model");
		String ext = ((dataModel != null && dataModel.equals("64")) ? "64" : "");
		System.loadLibrary("IoWrapperDll" + ext);
	}
}

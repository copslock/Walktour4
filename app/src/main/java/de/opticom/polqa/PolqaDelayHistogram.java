package de.opticom.polqa;

public class PolqaDelayHistogram 
{
	public float pFirstBin;
	public float pBinWidthExt;
	public int pPeakIndexExt;
	public int pNumBinsExt;
	public int[] ppHistogram;
	
	public PolqaDelayHistogram() { reset(); }
	
	public void reset() {
		pFirstBin = 0.0f;
		pBinWidthExt = 0.0f;
		pPeakIndexExt = 0;
		pNumBinsExt = 0;
		ppHistogram = null;
	}
}

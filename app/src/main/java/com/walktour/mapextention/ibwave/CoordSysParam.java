package com.walktour.mapextention.ibwave;

public class CoordSysParam {
	public static int Version = 1;
	public static int DatumParamCount = 8;
	public static int ParamCount = 9;
	public static int BoundCount = 4;
	private String mDatumParams[];
	private int mCoordType;
	private String mProjectType;
	private String mDatumType;
	private int mDatumID;
	private String mDatumEllipsoid;
	private String mProjectUnit;
	private String mParams[];
	private String mBounds[];
	// private boolean mCalculated;

	public CoordSysParam() {
		mDatumParams = new String[DatumParamCount];
		mCoordType = 0; // non earth
		mParams = new String[ParamCount];
		mBounds = new String[BoundCount];
		// mCalculated = false;
	}

	public String getDatumParams(int index) {
		return mDatumParams[index];
	}

	public void setDatumParams(int index, String mDatumParam) {
		this.mDatumParams[index] = mDatumParam;
	}

	public int getCoordType() {
		return mCoordType;
	}

	public void setCoordType(int mCoordType) {
		this.mCoordType = mCoordType;
	}

	public String getProjectType() {
		return mProjectType;
	}

	public void setProjectType(String mProjectType) {
		this.mProjectType = mProjectType;
	}

	public String getDatumType() {
		return mDatumType;
	}

	public void setDatumType(String mDatumType) {
		this.mDatumType = mDatumType;
	}

	public int getDatumID() {
		return mDatumID;
	}

	public void setDatumID(int mDatumID) {
		this.mDatumID = mDatumID;
	}

	public String getDatumEllipsoid() {
		return mDatumEllipsoid;
	}

	public void setDatumEllipsoid(String mDatumEllipsoid) {
		this.mDatumEllipsoid = mDatumEllipsoid;
	}

	public String getProjectUnit() {
		return mProjectUnit;
	}

	public void setProjectUnit(String mProjectUnit) {
		this.mProjectUnit = mProjectUnit;
	}

	public String getParams(int index) {
		return mParams[index];
	}

	public void setParams(int index, String sParam) {
		this.mParams[index] = sParam;
	}

	public String getBounds(int index) {
		return mBounds[index];
	}

	public void setBounds(int index, String sBound) {
		this.mBounds[index] = sBound;
	}
}

package com.walktour.mapextention.ibwave;

import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

class SiteConsts{
	static public String CSSiteID = "SiteID";
	static public String CSSectorID = "SectorID";
	static public String CSSiteName = "SiteName";
	static public String CSLongitude = "Longitude";
	static public String CSLatitude = "Latitude";
	static public String CSAzimuth = "Azimuth";
	static public String CSCellName = "CellName";
	static public String CSBeamwidth = "Beamwidth";

	public int miSiteID = -1;
	public int miSectorID = -1;
	public int miSiteName = -1;
	public int miLongitude = -1;
	public int miLatitude = -1;
	public int miAzimuth = -1;
	public int miCellName = -1;
	public int miBeamwidth = -1;
}

enum CellDisplayStyle {
	CILine,
	CISimple,
	CIPie,
	CIComplex
}

class Site{
	public java.util.ArrayList<Cell> mCells = new java.util.ArrayList<Cell>();
	public String mSiteID = "unknown";
	public String mSiteName = "unname";
	public double mLongitude = -9999;
	public double mLatitude = -9999;
	public Site(){
	}
}

class Cell {
	public String mSectorID = "unknown";
	public String mCellName = "unname";
	public double mAzimuth = 0;  //方向角，正北方向为0度，顺时针方向增加
	public double mBeamwidth = 60;
	public Cell(){
		
	}
}

public class SiteMap extends TabMap {
	private SiteConsts mSiteConsts = new SiteConsts();
	public java.util.ArrayList<Site> mSites = new java.util.ArrayList<Site>();

	private Rectangle2D.Double mDataScope;
	private CellDisplayStyle mCellDisplayStyle= CellDisplayStyle.CIPie;
	
	public SiteMap(String sFileName){
		
		mDataScope = new Rectangle2D.Double(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE * 2, Integer.MAX_VALUE * 2);
		
		LoadFromFile(sFileName);
	}

	public Rectangle2D.Double getDataScope()
	{
		return (Rectangle2D.Double)mDataScope.clone();
	}
	
	public void recalcDataScope(Projection aProj)
	{
		Site aSite;
		double dLeft = Integer.MAX_VALUE;
		double dTop = Integer.MIN_VALUE;
		double dRight = Integer.MIN_VALUE;
		double dBottom = Integer.MAX_VALUE;
		for (int i = 0; i < mSites.size(); i++){
			aSite = mSites.get(i);
			if(dLeft > aSite.mLongitude){
				dLeft = aSite.mLongitude;
			}
			if(dRight < aSite.mLongitude){
				dRight = aSite.mLongitude;
			}
			if(dTop < aSite.mLatitude){
				dTop = aSite.mLatitude;
			}
			if(dBottom > aSite.mLatitude){
				dBottom = aSite.mLatitude;
			}
		}
		Point2D.Double srcTopLeft = new Point2D.Double(dLeft, dTop);
		Point2D.Double dstTopLeft = new Point2D.Double(0, 0);
		aProj.transform(srcTopLeft, dstTopLeft);
		Point2D.Double srcBottomRight = new Point2D.Double(dRight, dBottom);
		Point2D.Double dstBottomRight = new Point2D.Double(0, 0);
		aProj.transform(srcBottomRight, dstBottomRight);
		 
		
		mDataScope.x = dstTopLeft.x;
		mDataScope.y = dstTopLeft.y;
		mDataScope.width = dstBottomRight.x - dstTopLeft.x;
		mDataScope.height = dstTopLeft.y - dstBottomRight.y;
	}
	
	private void LoadFromFile(String sFileName){
		java.util.List<String> ls = LoadTxtFile(sFileName);
		if (ls.size() < 2)
		{
			return;
		}
		
		String sLine = ls.get(0); //column title
		GetFieldIndexs(sLine);
		
		if (!CheckColumnIndexs())
		{
			return;
		}
		
		Site aSite = null;
		Cell aCell;
		String sSiteID;
		for (int i = 1; i < ls.size(); i++){
			sLine = ls.get(i);
			String[] sl = sLine.split("\t");
			if(aSite == null){
				aSite = new Site();
				mSites.add(aSite);
			}
			else{
				sSiteID = sl[mSiteConsts.miSiteID];
				if (!aSite.equals(sSiteID)){
					aSite = new Site();
					mSites.add(aSite);
				}
			}
			aCell = new Cell();
			aSite.mCells.add(aCell);
			GetFieldValues(sl, aSite, aCell);
		}
	}
	
	private void GetFieldValues(String[] sl, Site aSite, Cell aCell){
		String sValue;
		if(mSiteConsts.miSiteID >= 0){
			sValue = 
			aSite.mSiteID = sl[mSiteConsts.miSiteID];
		}
		if(mSiteConsts.miSiteName >= 0)
		{
			aSite.mSiteName = sl[mSiteConsts.miSiteName];
		}
		if(mSiteConsts.miLongitude >= 0)
		{
			sValue = sl[mSiteConsts.miLongitude];
			aSite.mLongitude = StrToDoubleDef(sValue, aSite.mLongitude);
		}
		if(mSiteConsts.miLatitude >= 0)
		{
			sValue = sl[mSiteConsts.miLatitude];
			aSite.mLatitude = StrToDoubleDef(sValue, aSite.mLatitude);
		}
		
		if(mSiteConsts.miSectorID >= 0)
		{
			aCell.mSectorID = sl[mSiteConsts.miSectorID];
		}
		if(mSiteConsts.miCellName >= 0)
		{
			aCell.mCellName = sl[mSiteConsts.miCellName];
		}
		if(mSiteConsts.miAzimuth >= 0)
		{
			sValue = sl[mSiteConsts.miAzimuth];
			aCell.mAzimuth = StrToDoubleDef(sValue, aCell.mAzimuth);
		}
		if(mSiteConsts.miBeamwidth >= 0)
		{
			sValue = sl[mSiteConsts.miBeamwidth];
			aCell.mBeamwidth = StrToDoubleDef(sValue, aCell.mBeamwidth);
		}
	}
	
	private double StrToDoubleDef(String s, double def){
		try{
			return Double.parseDouble(s);
		}
		catch(Exception e){
			return def;
		}
	}
	
	private void GetFieldIndexs(String s){
		String[] slLine = s.split("\t");
		for(int i = 0; i < slLine.length; i++){
			if (slLine[i].equalsIgnoreCase(SiteConsts.CSSiteID)){
				mSiteConsts.miSiteID = i;
			}
			if (slLine[i].equals(SiteConsts.CSSectorID)){
				mSiteConsts.miSectorID = i;
			}
			if (slLine[i].equals(SiteConsts.CSSiteName)){
				mSiteConsts.miSiteName = i;
			}
			if (slLine[i].equals(SiteConsts.CSLongitude)){
				mSiteConsts.miLongitude = i;
			}
			if (slLine[i].equals(SiteConsts.CSLatitude)){
				mSiteConsts.miLatitude = i;
			}
			if (slLine[i].equals(SiteConsts.CSAzimuth)){
				mSiteConsts.miAzimuth = i;
			}
			if (slLine[i].equals(SiteConsts.CSCellName)){
				mSiteConsts.miCellName = i;
			}
			if (slLine[i].equals(SiteConsts.CSBeamwidth)){
				mSiteConsts.miBeamwidth = i;
			}	
		}
	}
	
	//check file columns
	private boolean CheckColumnIndexs(){
		return !(mSiteConsts.miSiteID == -1 || 
				mSiteConsts.miSectorID == -1 || 
				mSiteConsts.miLongitude == -1 || 
				mSiteConsts.miLatitude == -1 || 
				mSiteConsts.miAzimuth == -1);
	}

	static public java.util.List<String> LoadTxtFile(String sFileName){
		java.util.List<String> ls = new java.util.ArrayList<String>();
		
		File file = new File(sFileName);   
		BufferedReader reader;
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);
			in.reset();
			if (first3bytes[0] == (byte) 0xEF 
					&& first3bytes[1] == (byte) 0xBB 
					&& first3bytes[2] == (byte) 0xBF) {// utf-8
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			}
			else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFE) {
				reader = new BufferedReader(new InputStreamReader(in, "unicode"));   
			} else if (first3bytes[0] == (byte) 0xFE  
                    && first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in, "utf-16be"));   
            } else if (first3bytes[0] == (byte) 0xFF 
            		&& first3bytes[1] == (byte) 0xFF) {
            	reader = new BufferedReader(new InputStreamReader(in, "utf-16le"));   
            } else {
            	reader = new BufferedReader(new InputStreamReader(in, "GBK"));   
            }   
			String str = reader.readLine();
			
			while (str != null) {
				ls.add(str);
				str = reader.readLine();
			}
			reader.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ls;   
}   


	public CellDisplayStyle getCellDisplayStyle() {
		return mCellDisplayStyle;
	}

	public void setCellDisplayStyle(CellDisplayStyle mCellDisplayStyle) {
		this.mCellDisplayStyle = mCellDisplayStyle;
	}
	
	public float GetAntLength(){
		return 24;
	}
	
	public boolean GetDisplayLabel(){
		return true;
	}
	
	public String GetCellLabel(Cell aCell){
		return aCell.mCellName;
	}
}

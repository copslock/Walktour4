package com.walktour.mapextention.ibwave;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;
import com.walktour.Utils.UtilsMethod;
import com.walktour.gui.map.MapFactory;
import com.walktour.gui.map.MapView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TabMapRaster extends TabMap {
	private java.util.List<String> lsTab;
	private CoordSysParam mCoordSysParam;
	private java.util.List<ControlPoint> mCtrlPts;
	private String mImg;
	private Bitmap mDrawData;
	private Rectangle2D.Double mDataScope;

	public TabMapRaster(String sFileName, StringBuffer coordSys) {
		setLayerName(sFileName);

		mCoordSysParam = new CoordSysParam();
		mCtrlPts = new java.util.ArrayList<ControlPoint>();
		mDataScope = new Rectangle2D.Double(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE * 2,
				Integer.MAX_VALUE * 2);

		loadFromFile(sFileName, coordSys);
	}

	private static boolean isFileExit(String path) {
		if (path == null) {
			return false;
		}
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
		}
		return true;
	}

	private static String GetItemContent(String sLine, String sErr) {
		int iPos = sLine.indexOf("\"");
		if (iPos < 0) {
			// error
			return sErr;
		}
		String s = sLine.substring(iPos + 1, sLine.length() - 1);
		return s;
	}

	private static String getFileName(String sFileLine) {
		String s = GetItemContent(sFileLine, "Table File Format Error!");
		return s;
	}

	private static String getLayerType(String sLine) {
		String s = GetItemContent(sLine, "Layer Type Format Error!");
		return s;
	}

	private static String[] getFieldBySpace(String sLine) {
		String ss[] = new String[2];
		int ipos = sLine.indexOf(" ");
		if (ipos >= 0) {
			ss[0] = sLine.substring(0, ipos);
			ss[1] = sLine.substring(ipos).trim();
		} else {
			ss[0] = sLine;
			ss[1] = "";
		}
		return ss;
	}

	private static String getCoordParams(java.util.List<String> ls) {
		String sCoordParams = "";
		String sLine;
		String sValue;
		String ss[];
		for (int i = 4; i < ls.size(); i++) {
			sLine = ls.get(i).trim();
			ss = getFieldBySpace(sLine);
			sValue = ss[0];
			sLine = ss[1];
			if (sValue.equals("CoordSys")) {
				sCoordParams = sLine;
				break;
			}
		}
		return sCoordParams;
	}

	private static java.util.List<String> stringToStrList(String sToSplit, String sSeparator, String sIgnore,
			boolean bAllowEmpty) {
		java.util.List<String> ls = new java.util.ArrayList<String>();
		int i = 0;
		int iLen = sToSplit.length();
		String sTmpStr = "";
		if (iLen == 0) {
			return ls;
		}

		while (true) {
			if (sIgnore.indexOf(sToSplit.charAt(i)) < 0) {
				if (sSeparator.indexOf(sToSplit.charAt(i)) >= 0) {
					if (sTmpStr.length() > 0) {
						ls.add(sTmpStr);
						sTmpStr = "";
					} else {
						if (bAllowEmpty) {
							ls.add(sTmpStr);
						}
					}
				} else {
					sTmpStr += sToSplit.charAt(i);
				}
			}
			i++;

			if (i >= iLen) {
				break;
			}
		}
		// last param
		if (sTmpStr.length() > 0) {
			ls.add(sTmpStr);
		}
		return ls;
	}

	private static boolean parseCoordSys(String sCoordParams, CoordSysParam aCoordSysParam) {
		boolean b = false;

		String sParams = sCoordParams;
		String ss[] = getFieldBySpace(sParams);
		String sField = ss[0];
		sParams = ss[1];

		java.util.List<String> strCoords = stringToStrList(sParams, " ,", "()", false);

		int iCount = strCoords.size();

		boolean bHaveBound = false;
		int iBIdx = iCount - 1;
		for (int i = 0; i < iCount; i++) {
			if (strCoords.get(i).equals("Bounds")) {
				bHaveBound = true;
				iBIdx = i - 1;
			}
		}

		aCoordSysParam.setCoordType(0);
		int iIdx = 1;

		if (sField.equals("Earth")) {
			aCoordSysParam.setCoordType(1);
			aCoordSysParam.setProjectType(strCoords.get(iIdx));
			iIdx++;

			aCoordSysParam.setDatumType(strCoords.get(iIdx));
			iIdx++;
			int iDatumID = strToIntDef(aCoordSysParam.getDatumType(), 0);
			aCoordSysParam.setDatumID(iDatumID);

			if (iCount > 3) {
				switch (aCoordSysParam.getDatumID()) {
				case 999: {
					aCoordSysParam.setDatumEllipsoid(strCoords.get(iIdx));
					iIdx++;
					for (int i = iIdx; i <= iIdx + 2; i++) {
						aCoordSysParam.setDatumParams(i - iIdx, strCoords.get(i));
					}
					iIdx++;
				}
				case 9999: {
					aCoordSysParam.setDatumEllipsoid(strCoords.get(iIdx));
					iIdx++;
					for (int i = iIdx; i < iBIdx; i++) {
						if (i - iIdx > 7) {
							break;
						}
						aCoordSysParam.setDatumParams(i - iIdx, strCoords.get(i));
					}
					iIdx++;
				}
				}
				if (iIdx < iBIdx) {
					aCoordSysParam.setProjectUnit(strCoords.get(iIdx));
					iIdx++;
				}
				// get params
				for (int i = iIdx; i <= iBIdx; i++) {
					if (i - iIdx > 7) {
						break;
					}
					aCoordSysParam.setParams(i - iIdx, strCoords.get(i));
				}
			}
			b = true;
		} else if (sField.equals("NonEarth")) {
			aCoordSysParam.setProjectType("");
			aCoordSysParam.setProjectUnit(strCoords.get(1));
			aCoordSysParam.setParams(0, "");
			aCoordSysParam.setParams(1, "");
			aCoordSysParam.setParams(2, "");
			b = true;
		}
		if (bHaveBound) {
			aCoordSysParam.setBounds(0, strCoords.get(iIdx + 1));
			aCoordSysParam.setBounds(1, strCoords.get(iIdx + 2));
			aCoordSysParam.setBounds(2, strCoords.get(iIdx + 3));
			aCoordSysParam.setBounds(3, strCoords.get(iIdx + 4));
		} else {
			aCoordSysParam.setBounds(0, "");
			aCoordSysParam.setBounds(1, "");
			aCoordSysParam.setBounds(2, "");
			aCoordSysParam.setBounds(3, "");
		}

		return b;
	}

	private static boolean haveControlPoint(String sLine) {
		int iLength = sLine.length();
		int iCount = 0;
		for (int i = 0; i < iLength; i++) {
			if (sLine.charAt(i) == '(') {
				iCount++;
				if (iCount == 2) {
					break;
				}
			}
		}
		return iCount == 2;
	}

	private static ControlPoint gainControlPoint(String sLine) {
		ControlPoint cp = new ControlPoint();
		int iPos = sLine.indexOf(',');
		String sAX = sLine.substring(1, iPos).trim();
		sLine = sLine.substring(iPos + 1);

		iPos = sLine.indexOf(')');
		String sAY = sLine.substring(0, iPos).trim();
		sLine = sLine.substring(iPos + 3);

		iPos = sLine.indexOf(',');
		String sPX = sLine.substring(0, iPos).trim();
		sLine = sLine.substring(iPos + 1);

		iPos = sLine.indexOf(')');
		String sPY = sLine.substring(0, iPos).trim();

		cp.ix = Integer.parseInt(sPX);
		cp.iy = Integer.parseInt(sPY);
		cp.dx = Double.parseDouble(sAX);
		cp.dy = Double.parseDouble(sAY);

		return cp;
	}

	private static String getExtractFilePath(String sFileName) {
		String s = sFileName.substring(0, sFileName.lastIndexOf("/") + 1);
		return s;
	}

	private static String getExtractFileExt(String sFileName) {
		String s = sFileName.substring(sFileName.lastIndexOf("/") + 1);
		return s;
	}

	private static int strToIntDef(String s, int iDef) {
		int ii;
		try {
			ii = Integer.parseInt(s);
		} catch (Exception e) {
			ii = iDef;
		}
		return ii;
	}

	private void loadFromFile(String sFileName, StringBuffer coordSys) {
		mCtrlPts.clear();

		lsTab = new java.util.ArrayList<String>();
		try {
			FileReader reader = new FileReader(sFileName);
			BufferedReader br = new BufferedReader(reader);
			String s1 = null;
			while ((s1 = br.readLine()) != null) {
				if (s1.indexOf("CoordSys") != -1) {
					coordSys.append(s1);
				}
				lsTab.add(s1);
			}
			br.close();
			reader.close();
		} catch (Exception e) {

		}

		int iIndex = 0;
		int ipos;
		String s = "";
		while (iIndex < lsTab.size()) {
			s = lsTab.get(iIndex);
			ipos = s.indexOf("File");
			if (ipos >= 0) {
				break;
			}
			iIndex++;
		}

		if (iIndex >= lsTab.size()) {
			// Table File Content Error!
			return;
		}

		String sPath = getExtractFilePath(sFileName);
		String sImg = sPath + getFileName(s);

		if (!isFileExit(sImg)) {
			// Image File not Exists;
			return;
		}

		mImg = sImg;

		s = lsTab.get(iIndex + 1);
		String sLyrType = getLayerType(s);

		if (!sLyrType.equals("RASTER")) {
			// Table Type Cannot support
			return;
		}

		s = getExtractFileExt(sImg);
		if (s.equalsIgnoreCase(".MIG")) {
			// Table Type Cannot support
			return;
		}

		// int iPrjType;

		String sCoordParams = getCoordParams(lsTab);
		if (!parseCoordSys(sCoordParams, mCoordSysParam)) {
			// error
			return;
		}

		for (int i = iIndex + 2; i < lsTab.size() - 2; i++) {
			if (haveControlPoint(lsTab.get(i))) {
				ControlPoint cp = gainControlPoint(lsTab.get(i).trim());
				mCtrlPts.add(cp);
			} else {
				break;
			}
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mImg, options);
		options.inSampleSize = UtilsMethod.computeSuitedSampleSize(options, MapView.IMG_MAX_PIXELS);
		MapFactory.getMapData().setSampleSize(options.inSampleSize);
		MapFactory.getMapData().setScale(options.inSampleSize > 0 ? 1 / options.inSampleSize : 1);
		options.inJustDecodeBounds = false;
		if (new File(mImg).exists()) {
			mDrawData = BitmapFactory.decodeFile(mImg, options);
		} else {
			mDrawData = null;
		}
	}

	// public String getImg() {
	// return mImg;
	// }

	// public int GetCtrlPtCount() {
	// return mCtrlPts.size();
	// }

	// public ControlPoint GetCtrlPt(int index) {
	// return mCtrlPts.get(index);
	// }

	// public CoordSysParam GetCoordSysParam() {
	// return mCoordSysParam;
	// }

	// public Bitmap getBmp() {
	// return mBmp;
	// }

	public Bitmap getDrawData() {
		return mDrawData;
	}

	public Rectangle2D.Double getDataScope() {
		return (Rectangle2D.Double) mDataScope.clone();
	}

	public int getWidth() {
		if (mDrawData != null) {
			return mDrawData.getWidth();
		}
		return 0;
	}

	public int getHeight() {
		if (mDrawData != null) {
			return mDrawData.getHeight();
		}
		return 0;
	}

	/**
	 * 换算出Tab文件对应的屏幕绘制区域<BR>
	 * [功能详细描述]
	 * 
	 * @param aProj
	 * @see com.example.bmi.TabMap#recalcDataScope(com.jhlabs.map.proj.Projection)
	 */
	public void recalcDataScope(Projection aProj) {
		// if (mCtrlPts.size() == 0)
		// {
		// return;
		// }
		// TransformationAffine6 ta = new TransformationAffine6();
		// double[][] sourceSet = new double[mCtrlPts.size()][2];
		// double[][] destSet = new double[mCtrlPts.size()][2];
		//
		// ControlPoint cp;
		//
		// boolean isLongLat = true;
		// for(int i = 0; i < mCtrlPts.size(); i++)
		// {
		// cp = mCtrlPts.get(i);
		// isLongLat = Consts.CEarthRectangle.contains(cp.dx, cp.dy);
		// if (!isLongLat)
		// {
		// break;
		// }
		// }
		//
		// Point2D.Double src = new Point2D.Double(0, 0);
		// Point2D.Double dst = new Point2D.Double(0, 0);
		//
		// Point2D.Double pps[] = new Point2D.Double[mCtrlPts.size()];
		// for(int i = 0; i < mCtrlPts.size(); i++)
		// {
		// cp = mCtrlPts.get(i);
		// pps[i] = new Point2D.Double(cp.dx, cp.dy);
		// if (!isLongLat)
		// {
		// src.x = cp.dx;
		// src.y = cp.dy;
		// aProj.inverseTransform(src, pps[i]);
		// }
		//
		// destSet[i][0] = pps[i].x;
		// destSet[i][1] = pps[i].y;
		//
		// sourceSet[i][0] = cp.ix;
		// sourceSet[i][1] = cp.iy;
		// }
		//
		// ta.init(destSet, sourceSet);
		//
		// double[] dp;
		// double[] vp = new double[2];
		// vp[0] = 0;
		// vp[1] = 0;
		// dp = ta.transform(vp);
		// src.x = dp[0];
		// src.y = dp[1];
		// aProj.transform(src, dst);
		// mDataScope.x = dst.x;
		// mDataScope.y = dst.y;
		//
		// vp[0] = getWidth();
		// vp[1] = getHeight();
		// dp = ta.transform(vp);
		// src.x = dp[0];
		// src.y = dp[1];
		// aProj.transform(src, dst);
		// mDataScope.width = dst.x - mDataScope.x;
		// mDataScope.height = mDataScope.y - dst.y;

		////////////////////////////////////////////////////////////////////////////////

		if (mCtrlPts.size() == 0) {
			return;
		}

		int ix = 0;
		int iy = 0;
		double rx = 0;
		double ry = 0;
		double xScale;
		double yScale;
		double rxDelta;
		double ryDelta;

		ControlPoint cp;
		ControlPoint cp0;
		Point2D.Double src = new Point2D.Double(0, 0);
		// Point2D.Double dst = new Point2D.Double(0, 0);

		boolean isLongLat = true;
		for (int i = 0; i < mCtrlPts.size(); i++) {
			cp = mCtrlPts.get(i);
			isLongLat &= Consts.CEarthRectangle.contains(cp.dx, cp.dy);
			if (!isLongLat) {
				break;
			}
		}

		Point2D.Double pt2ds[] = new Point2D.Double[mCtrlPts.size()];
		for (int i = 0; i < mCtrlPts.size(); i++) {
			cp = mCtrlPts.get(i);
			pt2ds[i] = new Point2D.Double(cp.dx, cp.dy);
			if (isLongLat) {
				src.x = cp.dx;
				src.y = cp.dy;
				aProj.transform(src, pt2ds[i]);
			}
		}

		int idx;
		int idy;
		for (int i = 1; i < mCtrlPts.size(); i++) {
			cp = mCtrlPts.get(i);
			cp0 = mCtrlPts.get(i - 1);
			idx = Math.abs(cp.ix - cp0.ix);
			idy = Math.abs(cp.iy - cp0.iy);
			if (idx != 0) {
				idx++;
			}
			if (idy != 0) {
				idy++;
			}
			if (idx > getWidth()) {
				idx = getWidth();
			}
			if (idy > getHeight()) {
				idy = getHeight();
			}
			ix += idx;
			iy += idy;
			rx += Math.abs(cp.dx - cp0.dx);
			ry += Math.abs(cp.dy - cp0.dy);
		}

		if (ix > 0 && iy > 0) {
			xScale = rx / ix;
			yScale = ry / iy;

			rxDelta = getWidth() * xScale;
			ryDelta = getHeight() * yScale;

			cp = mCtrlPts.get(0);
			mDataScope.x = Math.floor(pt2ds[0].x - cp.ix * xScale);
			mDataScope.y = Math.floor(pt2ds[0].y + cp.iy * yScale);
			mDataScope.width = rxDelta;
			mDataScope.height = ryDelta;
		}
	}
}

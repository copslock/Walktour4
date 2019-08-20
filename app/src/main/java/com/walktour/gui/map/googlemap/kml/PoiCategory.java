package com.walktour.gui.map.googlemap.kml;

import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.kml.constants.PoiConstants;

public class PoiCategory implements PoiConstants {
	private final int Id;
	public String Title;
	public boolean Hidden;
	public int IconId;
	public int MinZoom;

	public PoiCategory(int id, String title, boolean hidden, int iconid, int minzoom) {
		super();
		Id = id;
		Title = title;
		Hidden = hidden;
		IconId = iconid;
		MinZoom = minzoom;
	}

	public PoiCategory() {
		this(PoiConstants.EMPTY_ID, "", false, R.drawable.iconmarker, 14);
	}

	public PoiCategory(String title) {
		this(PoiConstants.EMPTY_ID, title, false, R.drawable.iconmarker, 14);
	}

	public int getId() {
		return Id;
	}

}

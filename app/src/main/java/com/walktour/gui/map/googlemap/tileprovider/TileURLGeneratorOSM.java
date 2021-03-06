package com.walktour.gui.map.googlemap.tileprovider;


public class TileURLGeneratorOSM extends TileURLGeneratorBase {
	private String IMAGE_FILENAMEENDING;

	public TileURLGeneratorOSM(String baseurl, String imagefilename) {
		super(baseurl);
		IMAGE_FILENAMEENDING = imagefilename;
	}

	@Override
	public String Get(int x, int y, int z) {
		return new StringBuilder().append(mName)
		.append(z)
		.append(SLASH)
		.append(x)
		.append(SLASH)
		.append(y)
		.append(this.IMAGE_FILENAMEENDING)
		.toString();
	}

}

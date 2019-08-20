package de.opticom.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class Utils {
	public static String[] listFiles(String path, final String extension) {
		// Read files in directory
		File tmpFile = new File(path);
		String[] tmpFileList = tmpFile.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				if (filename.toLowerCase().endsWith(extension))
					return true;
				return false;
			}
		});
		if(tmpFileList != null) Arrays.sort(tmpFileList);
		return tmpFileList;
	}
}

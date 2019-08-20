package com.walktour.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if ((reqWidth == 0 && reqHeight == 0) || reqWidth < 0 || reqHeight < 0) {// 返回原图
			inSampleSize = 1;
		} else if (reqWidth != 0 && reqHeight != 0) {
			if (height > reqHeight || width > reqWidth) {
				final int halfHeight = height / 2;
				final int halfWidth = width / 2;
				while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
					inSampleSize *= 2;
				}
			}
		} else {
			if (reqWidth > 0) {
				int scale = (int) (width / (float) reqWidth);
				inSampleSize = scale;
			} else {
				int scale = (int) (height / (float) reqHeight);
				System.out.println("scale:" + scale);
				inSampleSize = scale;
			}
			if (inSampleSize <= 0) {
				inSampleSize = 1;
			}
		}

		return inSampleSize;
	}

	// 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
	private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
		Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
		if (src != dst) { // 如果没有缩放，那么不回收
			src.recycle(); // 释放Bitmap的native像素数组
			src = null;
			System.gc();
		}
		return dst;
	}

	// 从Resources中加载图片
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 计算inSampleSize
		options.inJustDecodeBounds = false;
		Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
		return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
	}

	/**
	 * 从sd卡上加载图片
	 * 
	 * @param pathName
	 *            文件路径
	 * @param reqWidth
	 *            要求输出的图片宽度，传入0表示根据高度按比例缩放
	 * @param reqHeight
	 *            要求输出的图片高度，传入0表示根据宽度按比例缩放
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFd(String pathName, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		if (reqWidth == 0) {
			reqWidth = options.outWidth / options.inSampleSize;
		}
		if (reqHeight == 0) {
			reqHeight = options.outHeight / options.inSampleSize;
		}
		Bitmap src = BitmapFactory.decodeFile(pathName, options);
		return createScaleBitmap(src, reqWidth, reqHeight);
	}

	/***
	 * 保存图片文png图片
	 * 
	 * @param path
	 * @param fileName
	 */
	public static void saveBitmapToPNG(Bitmap bm, String path, String fileName) {
		File f = new File(path, fileName);
		File parent = f.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * @param bm
	 * @param fileName
	 *            包含图片全路径名
	 */
	public static void saveBitmapToPNG(Bitmap bm, String fileName) {
		File f = new File(fileName);
		File parent = f.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 加载文件为Bitmap,缩小12倍数
	 * 
	 * @param file
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap loadFileToBitmap(String file) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[100 * 1024];
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inSampleSize = 12;
			options.inInputShareable = true;
			Bitmap bitmap = BitmapFactory.decodeFile(file, options);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}

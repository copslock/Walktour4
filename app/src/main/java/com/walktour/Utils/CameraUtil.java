package com.walktour.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 相机工具类
 * 
 * @author weirong.fan
 * 
 */
public class CameraUtil {

	/**
	 * 从指定URL获取图片
	 * 
	 * @param uri
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
		Bitmap bitmap = null;
		try {
			InputStream is = context.getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inTempStorage = new byte[100 * 1000];
			options.inPurgeable = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(is, null, options);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 30, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1000 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			if(options<=0)
				break;
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/***
	 * 压缩图片为本地文件
	 * 
	 * @param bmp
	 * @param file
	 */
	public static void compressBmpToFile(Bitmap bmp, String fileName) {
		try {
			File file = new File(fileName);
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			file = null;
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
			bmp.compress(Bitmap.CompressFormat.PNG, 30, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 加载文件为图片
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapForFile(String filePath) {
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		return bitmap;
	}

	@SuppressWarnings("deprecation")
	public static Bitmap resizeBitmap(String fileName, int newWidth) throws Exception {
		InputStream is = new FileInputStream(fileName);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inTempStorage = new byte[100 * 1000];
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inSampleSize = 10;
		options.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float temp = ((float) height) / ((float) width);
		int newHeight = (int) ((newWidth) * temp);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		//matrix.postRotate(90);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		bitmap.recycle();
		bitmap = null;
		return resizedBitmap;
	}
}

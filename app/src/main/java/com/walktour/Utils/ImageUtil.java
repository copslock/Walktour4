package com.walktour.Utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图像转换工具类支持把bitmap 转换成jpeg 、png、bmp
 * 支持旋转
 * @author jianchao.wang
 * 
 */
public class ImageUtil {
	/** 文件类型 */
	public static enum FileType {
		JPEG, PNG, BMP;
	}

	/**
	 * 保存图像到文件中
	 * 
	 * @param path
	 *          保存路径
	 * @param bitmap
	 *          图像
	 * @param fileName
	 *          文件名
	 * @param fileType
	 *          文件类型
	 */
	public static void saveBitmapToFile(String path, Bitmap bitmap, String fileName, FileType fileType) {
		saveBitmapToFile(path, bitmap, fileName, fileType, true);
	}

	/**
	 * 保存图像到文件中
	 * 
	 * @param path
	 *          保存路径
	 * @param bitmap
	 *          图像
	 * @param fileName
	 *          文件名
	 * @param fileType
	 *          文件类型
	 * @param isConver
	 *          是否覆盖
	 */
	public static void saveBitmapToFile(String path, Bitmap bitmap, String fileName, FileType fileType,
			boolean isConver) {
		FileOutputStream fos = null;
		try {
			switch (fileType) {
			case JPEG:
				fileName += ".jpeg";
				break;
			case PNG:
				fileName += ".png";
				break;
			case BMP:
				fileName += ".bmp";
				break;
			default:
				return;
			}
			File pathFile = new File(path);
			if (!pathFile.exists())
				pathFile.mkdirs();
			File imageFile = new File(path + File.separator + fileName);
			if (!isConver && imageFile.exists())
				return;
			if (!imageFile.exists())
				imageFile.createNewFile();
			fos = new FileOutputStream(imageFile);
			switch (fileType) {
			case JPEG:
				bitmap.compress(CompressFormat.JPEG, 100, fos);
				break;
			case PNG:
				bitmap.compress(CompressFormat.PNG, 100, fos);
				break;
			case BMP:
				compressToBMP(bitmap, fos);
				break;
			}
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
				System.gc();
			}
		}
	}

	/**
	 * 转换成bmp格式
	 * 
	 * @throws IOException
	 */
	private static void compressToBMP(Bitmap bitmap, FileOutputStream fos) throws IOException {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		byte[] rgb = createBmpRGB888(pixels, width, height);
		byte[] header = createBmpHeader(rgb.length);
		byte[] infos = createBmpInfosHeader(width, height);
		byte[] buffer = new byte[54 + rgb.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(infos, 0, buffer, 14, infos.length);
		System.arraycopy(rgb, 0, buffer, 54, rgb.length);
		fos.write(buffer);
	}

	/**
	 * 生成BMP文件头
	 * 
	 * @param size
	 *          文件体大小
	 * @return
	 */
	private static byte[] createBmpHeader(int size) {
		byte[] buffer = new byte[14];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0x00;
		}
		buffer[0] = 0x42;
		buffer[1] = 0x4D;
		buffer[2] = (byte) (size >> 0);
		buffer[3] = (byte) (size >> 8);
		buffer[4] = (byte) (size >> 16);
		buffer[5] = (byte) (size >> 24);
		buffer[10] = 0x36;
		return buffer;
	}

	/**
	 * 生成BMP文件信息头
	 * 
	 * @param width
	 *          文件宽度
	 * @param height
	 *          文件高度
	 * @return
	 */
	private static byte[] createBmpInfosHeader(int width, int height) {
		byte[] buffer = new byte[40];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0x00;
		}
		buffer[0] = 0x28;
		buffer[4] = (byte) (width >> 0);
		buffer[5] = (byte) (width >> 8);
		buffer[6] = (byte) (width >> 16);
		buffer[7] = (byte) (width >> 24);
		buffer[8] = (byte) (height >> 0);
		buffer[9] = (byte) (height >> 8);
		buffer[10] = (byte) (height >> 16);
		buffer[11] = (byte) (height >> 24);
		buffer[12] = 0x01;
		buffer[14] = 0x18;
		buffer[24] = (byte) 0xE0;
		buffer[25] = 0x01;
		buffer[28] = 0x02;
		buffer[29] = 0x03;
		return buffer;
	}

	/**
	 * 生成rgb颜色
	 * 
	 * @param body
	 *          文件体
	 * @param width
	 *          图像宽度
	 * @param height
	 *          图像高度
	 * @return
	 */
	private static byte[] createBmpRGB888(int[] body, int width, int height) {
		int len = body.length;
		System.out.println(body.length);
		byte[] buffer = new byte[width * height * 3];
		int offset = 0;
		for (int i = len - 1; i >= width; i -= width) {
			// DIB文件格式最后一行为第一行，每行按从左到右顺序
			int end = i, start = i - width + 1;
			for (int j = start; j <= end; j++) {
				buffer[offset] = (byte) (body[j] >> 0);
				buffer[offset + 1] = (byte) (body[j] >> 8);
				buffer[offset + 2] = (byte) (body[j] >> 16);
				offset += 3;
			}
		}
		return buffer;
	}

	/**
	 * 获取图片的角度
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree  = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	/**
	 * 旋转图片
	 * @param path
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotaingImageView(String path , Bitmap bitmap) {
		//获取旋转的角度
		int degree=readPictureDegree(path);
		// 创建新的图片
		Bitmap resizedBitmap = rotaingImageView(degree,bitmap);
		return resizedBitmap;
	}
}

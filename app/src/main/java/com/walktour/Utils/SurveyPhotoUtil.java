package com.walktour.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.walktour.base.util.SDCardUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yi.lin on 2017/8/24.
 * 勘察基站图片处理工具
 */

public class SurveyPhotoUtil {
    /**
     * 添加水印位置
     */
    public enum WatermarkPosition {
        LEFT_TOP,
        RIGHT_TOP,
        LEFT_BOTTOM,
        RIGHT_BOTTOM
    }

    private SurveyPhotoUtil() {
        //no-instance
    }

    /**
     * 获取基站图片文件路径
     *
     * @param stationName
     * @return
     */
    private static String getSurveyStationPath(String stationName) {
        return SDCardUtil.getSDCardPath() + "Walktour/singlestation/survey/" + stationName + "/";
    }

    /**
     * 获取图片名称
     *
     * @param stationName
     * @param photoName
     * @return
     */
    public static String getPhotoFileName(String stationName, String photoName) {
        File file = new File(getSurveyStationPath(stationName));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file + "/" + photoName + ".jpg";
    }

    /**
     * @param bitmap 需要保存的Bitmap图片
     * @return 保存成功时返回图片的路径，失败时返回null
     */
    public static String savePhoto(String stationName, String photoName, Bitmap bitmap) {
        FileOutputStream outStream = null;
        String fileName = getPhotoFileName(stationName, photoName);
        try {
            outStream = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }


    /**
     * 修正被旋转图片
     *
     * @param originPath
     * @return
     */
    public static Bitmap fixRotatePhoto(String originPath) {
        int angle = readPictureDegree(originPath);
        Bitmap bmp = getCompressPhoto(originPath);
        Bitmap bitmap = rotateImageView(angle, bmp);
        return bitmap;
    }


    /**
     * 修正被旋转图片并加上水印
     *
     * @param stationName 基站名称
     * @param photoName   图片名字
     * @param originPath  原始被旋转图片的路径
     * @param position    水印位置
     * @return
     */
    public static String fixRotateAndWatermarkDate2Photo(String stationName, String photoName, String originPath, WatermarkPosition position) {
        Bitmap originBm = fixRotatePhoto(originPath);
        int w = originBm.getWidth();
        int h = originBm.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        mCanvas.drawBitmap(originBm, 0, 0, null);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(16);
        float textWidth = textPaint.measureText("yyyy-MM-dd HH:mm:ss");
        float textHeight = textPaint.descent() - textPaint.ascent();
        switch (position) {
            case LEFT_TOP:
                mCanvas.drawText(time, 5, 5, textPaint);
                break;
            case RIGHT_TOP:
                mCanvas.drawText(time, w - textWidth - 5, 5, textPaint);
                break;
            case LEFT_BOTTOM:
                mCanvas.drawText(time, 5, h - textHeight - 5, textPaint);
                break;
            case RIGHT_BOTTOM:
                mCanvas.drawText(time, w - textWidth - 5, h - textHeight - 5, textPaint);
                break;
        }
        mCanvas.save();
        mCanvas.restore();
        return savePhoto(stationName, photoName, newBitmap);
    }

    public static class Watermark {
        private WatermarkPosition mPosition;
        private String mText;

        public Watermark() {
        }

        public Watermark(WatermarkPosition position, String text) {
            mPosition = position;
            mText = text;
        }

        @Override
        public String toString() {
            return "Watermark{" +
                    "mPosition=" + mPosition +
                    ", mText='" + mText + '\'' +
                    '}';
        }

        public WatermarkPosition getPosition() {
            return mPosition;
        }

        public void setPosition(WatermarkPosition position) {
            mPosition = position;
        }

        public String getText() {
            return mText;
        }

        public void setText(String text) {
            mText = text;
        }
    }

    public static String fixRotateAndWatermarkDate2Photo(String stationName, String photoName, String originPath, Watermark... watermarks) {
        Bitmap originBm = fixRotatePhoto(originPath);
        int w = originBm.getWidth();
        int h = originBm.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(newBitmap);
        mCanvas.drawBitmap(originBm, 0, 0, null);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(16);
        if (watermarks != null && watermarks.length > 0) {
            for (Watermark watermark : watermarks) {
                String text = watermark.getText();
                float textWidth = textPaint.measureText(text);
                float textHeight = textPaint.descent() - textPaint.ascent();
                switch (watermark.getPosition()) {
                    case LEFT_TOP:
                        mCanvas.drawText(text, 5, 20, textPaint);
                        break;
                    case RIGHT_TOP:
                        mCanvas.drawText(text, w - textWidth - 5, 20, textPaint);
                        break;
                    case LEFT_BOTTOM:
                        mCanvas.drawText(text, 5, h - textHeight - 5, textPaint);
                        break;
                    case RIGHT_BOTTOM:
                        mCanvas.drawText(text, w - textWidth - 5, h - textHeight - 5, textPaint);
                        break;
                }
            }
        }
        mCanvas.save();
        mCanvas.restore();
        return savePhoto(stationName, photoName, newBitmap);
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
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
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotateImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 删除图片
     *
     * @param filePath
     */
    public static void deletePhoto(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) file.delete();
        }
    }

}

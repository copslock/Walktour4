package com.walktour.control.bean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 把目录assets中的文件写入到指定文件中
 */
@SuppressLint("SdCardPath")
public class AssetsWriter {
    private static final String TAG = "AssetsWriter";
    private String mAssetsFileName;
    private File mTargetFile;
    private String mFileContext;
    private com.walktour.control.bean.StreamConventor streamConventor;
    private Context mContext;
    private boolean overWrite = false;


    public AssetsWriter(Context context, String assetsFileName, File targetFile) {
        this(context, assetsFileName, targetFile, false);
    }

    public AssetsWriter(Context context, String assetsFileName) {
        this(context, assetsFileName, new File(""), false);
    }
    /**
     * 目标文件存放位置为/data/data/com.walktour.gui/files/config
     *
     * @param context        调用此类的Activity或者Service
     * @param assetsFileName assets目录中的文件名
     * @param targetFileName 目标文件名
     * @param overWrite      当文件已经存在时，是否覆盖原有文件
     */
    public AssetsWriter(Context context, String assetsFileName, String targetFileName, boolean overWrite) {
        this(context, assetsFileName, AppFilePathUtil.getInstance().getAppConfigFile(targetFileName), overWrite);
    }

    /**
     * @param context        调用此类的Activity或者Service
     * @param assetsFileName assets目录中的文件名
     * @param targetFile     目标文件
     * @param overWrite      当文件已经存在时，是否覆盖原有文件
     */
    public AssetsWriter(Context context, String assetsFileName, File targetFile, boolean overWrite) {
        this.mContext = context;
        this.mAssetsFileName = assetsFileName;
        this.overWrite = overWrite;
        this.mTargetFile = targetFile;
    }

    /**
     */
    private void writeTextFileByCode(String code) {
        //用MyFileWriter写入文件
        try {
            streamConventor = new com.walktour.control.bean.StreamConventor(this.mContext.getResources().getAssets().open(mAssetsFileName));
            mFileContext = streamConventor.getString();
        } catch (Exception e) {
            e.printStackTrace();
            mFileContext = "error";
        }
        this.write(mFileContext, code);
    }

    /**
     *
     */
    private void writeTextFile() {
        //用MyFileWriter写入文件
        try {
            streamConventor = new com.walktour.control.bean.StreamConventor(
                    this.mContext.getResources().getAssets().open(mAssetsFileName));
            mFileContext = streamConventor.getString();
        } catch (Exception e) {
            e.printStackTrace();
            mFileContext = "error";
        }
        MyFileWriter.write(this.mTargetFile.getAbsolutePath(), mFileContext);
    }

    /**
     * 复制二进制文件到指定目录
     */
    public void writeBinFile() {
        //建立目标文件
        if (this.mTargetFile == null)
            return;
        if (!overWrite && this.mTargetFile.exists() && this.mTargetFile.length() > 0) {
            return;
        }
        try {
            if (this.mTargetFile.exists()) {
                LogUtil.w("AssetsWriter", "-tageExist to Del:" + this.mTargetFile.delete());
            }
            LogUtil.w("AssetsWriter", "--source:" + mAssetsFileName);
            String tempFileName = null;
            OutputStream out;
            try {
                out = new FileOutputStream(this.mTargetFile);
            } catch (Exception e) {
                LogUtil.e(TAG, "OutputStream", e);
                UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
                tempFileName = String.format("/data/local/tmp/%s", this.mTargetFile.getName());
                File tempFile = new File(tempFileName);
                try {
                    if (!tempFile.createNewFile())
                        LogUtil.e(TAG, "create file error");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                out = new FileOutputStream(tempFile);
            }

            //读取asset文件
            try {
                InputStream in = mContext.getResources().getAssets().open(this.mAssetsFileName, AssetManager.ACCESS_BUFFER);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (tempFileName != null) {
                UtilsMethod.runRootCommand(String.format("cat %s > %s", tempFileName, this.mTargetFile.getAbsolutePath()));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }


        //给二进制文件授权
        UtilsMethod.runRootCommand("chmod 777 " + this.mTargetFile.getAbsolutePath());
    }


    /**
     * 把目录assets中的文件写入到/data/data/com.walktour.gui/files/config中
     */
    public void writeToConfigDir() {
        if (this.mTargetFile == null) {
            return;
        }
        //如果文件已经存在
        if (this.mTargetFile.exists()) {
            //如果要重写
            if (overWrite || this.mTargetFile.length() <= 0) {
                this.writeTextFile();
            }
        } else {
            this.writeTextFile();
        }
    }

    /**
     * 把目录assets中的文件写入到/data/data/com.walktour.gui/files/config中
     *
     * @param code 采用的编码默认为"UTF-8".
     */
    public void writeToConfigDirByCode(String code) {
        if (this.mTargetFile == null) {
            return;
        }
        //如果文件已经存在
        if (this.mTargetFile.exists()) {
            //如果要重写
            if (overWrite || this.mTargetFile.length() <= 0) {
                this.writeTextFileByCode(code);
            }
        } else {
            //用MyFileWriter写入文件
            this.writeTextFileByCode(code);
        }//end else
    }

    /**
     * 写数据到指定路径文件
     *
     * @param data 数据
     * @param code 编码
     */
    private void write(String data, String code) {
        if (this.mTargetFile == null) {
            return;
        }
        try {
            OutputStream stream = new FileOutputStream(this.mTargetFile);
            if (null != code && !code.trim().equals("")) {
                stream.write(data.getBytes(code));
            } else {
                stream.write(data.getBytes("UTF-8"));
            }
            stream.flush();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFileBytes() {
        try {
            InputStream in = mContext.getResources().getAssets().open(mAssetsFileName);
            int lenght = in.available();
            byte[] buffer = new byte[lenght];
            in.read(buffer);
            in.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }
}
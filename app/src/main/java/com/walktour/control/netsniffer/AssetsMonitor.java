/*
 * 文件名: AssetsMonitor.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 负责将asset中的文件拷贝到相应的文件夹中，并给文件授权
 * 创建人: 黄广府
 * 创建时间:2012-8-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.control.netsniffer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责将asset中的文件拷贝到相应的文件夹中，并给文件授权<BR>
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-8-30]
 */
public class AssetsMonitor implements IMonitor {
    
    private static final String TAG = "AssetsMonitor";
    
    private String versionFilePath;
    
    private Context context;
    
    public AssetsMonitor(Context context) {
        super();
        this.context = context;
        this.versionFilePath = context.getFilesDir() + "/bin/version";
    }
    
    @Override
    public void start() {
        // 版本号相同时不复制asset下的文件
        if (getAssetsVersionCode() == getVersionCode()) {
            LogUtil.i(TAG, "asset版本相同不进行复制");
            return;
        }
        List<CopyAsset> assets = getCopyAssets();
        for (CopyAsset asset : assets) {
            try {
                copyAsset(asset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 设置复制的版本号
        setAssetsVersionCode(getVersionCode());
    }
    
    @Override
    public void stop() {
        // nothing to do
    }
    
    // 此部分可以考虑以后配置在其他配置文件当中
    private List<CopyAsset> getCopyAssets() {
        List<CopyAsset> result = new ArrayList<AssetsMonitor.CopyAsset>();
        String destDir = context.getFilesDir() + "/bin/";
        result = new ArrayList<AssetsMonitor.CopyAsset>();
        result.add(new CopyAsset("analPcap", destDir, "777"));
        result.add(new CopyAsset("netsniffer", destDir, "777"));
        result.add(new CopyAsset("SetParams.ini", destDir, "666"));
        result.add(new CopyAsset("icmptest", destDir, "777"));
        return result;
    }
    
    // 将asset中的文件拷贝到相应的文件夹中，并给文件授权
    private void copyAsset(CopyAsset asset) throws IOException {
        InputStream in = null;
        try {
            in = context.getAssets().open(asset.name);
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "assets中不存在文件: " + asset.name);
            return;
        }
        
        String destFilename = asset.destDir + asset.name;
        
        File dir = new File(asset.destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        LogUtil.i(TAG, "正在拷贝asset文件: " + asset.name);
        OutputStream out = new FileOutputStream(destFilename);
        this.copy(in, out);
        // 对文件进行授权
        String chmodCmd = "chmod " + asset.permits + " " + destFilename;
        Command.exec(chmodCmd, true);
        
    }
    
    /**
     * 复制文件<BR>
     * @param is 输入流
     * @param os 输出流
     * @throws IOException IO异常
     */
    public void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        @SuppressWarnings("unused")
        int length;
        while ((length = is.read(buffer)) > -1) {
            os.write(buffer);
        }
        is.close();
        os.flush();
        os.close();
    }
    
    private static class CopyAsset {
        /**
         * asset的名字
         */
        String name;
        
        /**
         * 目标文件夹
         */
        String destDir;
        
        /**
         * 权限
         */
        String permits;
        
        public CopyAsset(String name, String destDir, String permits) {
            super();
            this.name = name;
            this.destDir = destDir;
            this.permits = permits;
        }
    }
    
    /**
     * 获取当前版本号<BR>
     * [功能详细描述] 
     * @return 版本号
     */
    private int getVersionCode() {
        int result = 0;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            result = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取已经复制的assets的版本号
     * 
     * @return
     */
    private int getAssetsVersionCode() {
        int result = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(versionFilePath));
            String verStr = br.readLine();
            result = Integer.parseInt(verStr);
        } catch (FileNotFoundException ee) {
            // do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    
    /**
     * 将版本号写到目标地址的版本文件
     * 
     * @param versionCode
     */
    private void setAssetsVersionCode(int versionCode) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(versionFilePath));
            bw.write(String.valueOf(versionCode));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}

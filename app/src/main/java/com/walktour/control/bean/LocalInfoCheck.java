package com.walktour.control.bean;

import android.content.Context;
import android.os.Environment;

import com.dingli.https.HttpsUtil;
import com.walktour.Utils.APNOperate;
import com.walktour.Utils.BuildPower;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.service.phoneinfo.utils.MobileUtil;

import java.io.File;
import java.io.FilenameFilter;

/*
 * 本地信息检测类
 * 
 * 检查本地隐藏目录中的指定文件是否存在
 * 如果存在返回真
 * 
 * 如果不存在在线检查指定服务器文件是否存在,
 * 如果存在将本地信息上传到另一服务器,并返回真
 * 否则返回false
 */
public class LocalInfoCheck {

    private final String TAG = "LocalInfoCheck";
    private final String fileName1 = "jNjNU4zTA==dSYjA5U2JsUJHOWpZV3d2ZEmRkdkbFhBdmd2tqTVhSMVZ"; // /system/tmp/local/tdeta_WTW290
    private final String fileName2 = "YmhkMUw=RzNiMHRHTDNSbGJYQXZkRmR6VlZ5PUFYZDE5"; // /Walktour/temp/test_uup
    private final String fileName3 = "dkpIWnVGMkw=MWNpR1p2UVdhOTNZV3hyZEc5ZEZkMzlGMFlTPUFETXpzbWN2"; // /android/data/walktour/wtWork300

    private Context mContext = null;
    private String localImei = "";
    private String imei = "";
    private String mnc = "";

    public LocalInfoCheck(Context context, String localKey, String imei) {
        this.mContext = context;
        this.localImei = localKey;
        this.imei = imei;
        this.mnc = String.valueOf(MobileUtil.getSIM_MNC(mContext));
    }

    /*
     * 本地信息检查结果
     */
    public boolean checkLocalInfo() {
        // 本地信息校验成功
        if (checkInfo()) {
            LogUtil.w(TAG, "--Local_Info_Success--");
            return true;
        }
        // 网络环境检查成功
        if (!APNOperate.getInstance(mContext).checkNetWorkIsAvailable()) {
            APNOperate.getInstance(mContext).setMobileDataEnabled(true, "", true, 1000 * 15);
        }
        if (checkLocalInfoByServer()) {
            localInfoSuccess();
            sendSuccessToBServer();
            LogUtil.w(TAG, "--Local_Info_Check_Success--");
            return true;
        }
        LogUtil.w(TAG, "--Local_Info_Check_Faild--");
        return false;
    }

    /*
     * 检查本地信息是否存在
     */
    public boolean checkInfo() {
        FileReader fileRead = new FileReader();
        String imei = MyPhoneState.getInstance().getDeviceId(mContext);
        String blue = MyPhoneState.getInstance().getBluetoothAddress();

        String key;
        boolean isEnable = false;

        File[] files = getFilesByTag(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(fileName1)));
        for (int i = 0; files != null && i < files.length; i++) {
            key = fileRead.getFileText(files[i]);
            // 当值为空或者内容校验不通过时
            if (key != null && key.equals(UtilsMethod.getMD5(blue) + localImei + UtilsMethod.jaem2(UtilsMethod.jam(imei)))) {
                // return false;
                isEnable = true;
                break;
            }
        }
        LogUtil.w(TAG, "--checkInfo_F1_Result:--" + isEnable);

        if (isEnable) {
            isEnable = false;
            // }else{
            // return false;
        }
        files = getFilesByTag(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(fileName2)));
        for (int i = 0; files != null && i < files.length; i++) {
            key = fileRead.getFileText(files[i]);
            if (key != null && key.equals(UtilsMethod.getMD5(imei) + localImei)) {
                isEnable = true;
                break;
            }
        }
        if (isEnable) {
            isEnable = false;
        } else {
            LogUtil.w(TAG, "--checkInfo_F2_Faild--");
            return false;
        }

        files = getFilesByTag(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(fileName3)));
        for (int i = 0; files != null && i < files.length; i++) {
            key = fileRead.getFileText(files[i]);
            if (key != null && key.equals(localImei + UtilsMethod.jaem2(UtilsMethod.jam(blue)))) {
                isEnable = true;
                break;
            }
        }
        if (!isEnable) {
            LogUtil.w(TAG, "--checkInfo_F3_Faild--");
        }

        return isEnable;
    }

    /**
     * 返回文指定文件目录下的文件列表
     *
     * @param filePath 文件路径
     * @return 文件数组
     */
    private File[] getFilesByTag(String filePath) {
        String dic = filePath.substring(0, filePath.lastIndexOf("/"));
        String name = filePath.substring(filePath.lastIndexOf("/") + 1);
        return new File(dic).listFiles(new LocalInfoFilter(name));
    }

    /**
     * 本地校验信息过滤器
     *
     * @author Tangwq
     */
    class LocalInfoFilter implements FilenameFilter {
        private String startName;

        LocalInfoFilter(String start) {
            startName = start;
        }

        @Override
        public boolean accept(File dir, String filename) {
            // return filename.indexOf(startName) >= 0;
            return filename.startsWith(startName);
        }
    }

    /*
     * 本地环境校验成功 保存本地信息
     */
    private void localInfoSuccess() {
        try {
            String imei = MyPhoneState.getInstance().getDeviceId(mContext);
            String blue = MyPhoneState.getInstance().getBluetoothAddress();

            UtilsMethod.runRootCommand("chmod 777 /data/local");

            String fileInfo = UtilsMethod.getMD5(blue) + localImei + UtilsMethod.jaem2(UtilsMethod.jam(imei));
            UtilsMethod.WriteFile(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(fileName1)) + mnc, fileInfo);

            fileInfo = UtilsMethod.getMD5(imei) + localImei;
            UtilsMethod.WriteFile(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(fileName2)) + mnc, fileInfo);

            fileInfo = localImei + UtilsMethod.jaem2(UtilsMethod.jam(blue));
            UtilsMethod.WriteFile(Environment.getExternalStorageDirectory().getPath() + UtilsMethod.jem(UtilsMethod.jaem2(fileName3)) + mnc, fileInfo);

        } catch (Exception e) {
            LogUtil.w(TAG, "localInfoSuccess", e);
        }
    }

    /*
     * 通过服务器检查本地信息有效性
     */
    private boolean checkLocalInfoByServer() {

        String paPath = UtilsMethod.jem(BuildPower.UL) + File.separator + UtilsMethod.jem(BuildPower.PD) + File.separator + imei + File.separator + localImei + ".x";

        return HttpsUtil.fileExist(mContext, paPath);
    }

    /*
     * 告诉B服务器当前信息初始化成功
     */
    private void sendSuccessToBServer() {

        String localFile = Environment.getExternalStorageDirectory().getPath() + "/walktour/" + imei;
        UtilsMethod.WriteFile(localFile, localImei);

        try {
            FtpOperate ftpClient = new FtpOperate(mContext);
            boolean isConnect = ftpClient.connect(UtilsMethod.jem(BuildPower.PI), 21, UtilsMethod.jem("PWNGWmNtOXBrNVdR"), UtilsMethod.jem("PUVYYm9kOHFNalJZWm1ESnBaMFU="));
            if (isConnect) {
                ftpClient.uploadFile(localFile, "/" + imei);
            }
            ftpClient.disconnect();
            // ftpClient = null;
        } catch (Exception e) {
            LogUtil.w(TAG, "", e);
        }
    }
}

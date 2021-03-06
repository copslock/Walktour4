package com.walktour.Utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.sdk.android.common.utils.IOUtils;
import com.walktour.base.util.Base64Util;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.license.Base64;
import com.walktour.model.ProcessModel;
import com.walktour.service.TestService;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsMethod {
    public static String tag = "UtilsMethod";

    public static DecimalFormat decFormat = new DecimalFormat("#.##");// ???????????????????????????
    public static DecimalFormat decFormat4 = new DecimalFormat("#.####");// ???????????????????????????
    public static DecimalFormat decFormat6 = new DecimalFormat("#.######");// ???????????????????????????
    public static DecimalFormat decFarmat10 = new DecimalFormat("#.##########");
    public static DecimalFormat decFarmat30 = new DecimalFormat("#.##############################");
    public static DecimalFormat decFarmat8Zero = new DecimalFormat("0.00000000");
    /**
     * ????????????????????? yyyy-MM-dd HH:mm:ss.SSS
     */
    public static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    /**
     * ?????????????????????yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat sdFormatss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    /**
     * ????????????????????? HH:mm:ss
     */
    public static SimpleDateFormat sdfhms = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()); // ?????????????????????
    /**
     * ????????????????????? HH:mm:ss
     */
    public static SimpleDateFormat sdfhms2 = new SimpleDateFormat("HHmmss", Locale.getDefault()); // ?????????????????????
    /**
     * ????????????????????? yyyyMMddHHmmss
     */
    public static SimpleDateFormat sdfyMdhms = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    /**
     * ????????????????????? yyyyMMddHHmmssSSS
     */
    public static SimpleDateFormat sdfhmsss = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()); // ??????????????????
    /**
     * ????????????????????? HHmmss.sss
     */
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss.sss", Locale.getDefault());

    /**
     * ????????????????????? ddMMyy
     */
    public static SimpleDateFormat ymdFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    /**
     * ????????????????????? yyyy-MM-dd
     */
    public static SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static DecimalFormat formatDD = new DecimalFormat("00");

    public static final long Hour = 1000 * 60 * 60;
    public static final long Minute = 1000 * 60;
    public static final float kbyteRage = 1000;

    /**
     * 7???ASCII??????????????????ISO646-US???Unicode???????????????????????????
     */
    public static final String CharSet_US_ASCII = "US-ASCII";
    /**
     * ISO ??????????????? No.1???????????? ISO-LATIN-1
     */
    public static final String CharSet_ISO_8859_1 = "ISO-8859-1";
    /**
     * 8 ??? UCS ????????????
     */
    public static final String CharSet_UTF_8 = "UTF-8";
    /**
     * 16 ??? UCS ???????????????Big Endian????????????????????????????????????????????????
     */
    public static final String CharSet_UTF_16BE = "UTF-16BE";
    /**
     * 16 ??? UCS ???????????????Little-endian????????????????????????????????????????????????
     */
    public static final String CharSet_UTF_16LE = "UTF-16LE";
    /**
     * 16 ??? UCS ??????????????????????????????????????????????????????????????????
     */
    public static final String CharSet_UTF_16 = "UTF-16";
    /**
     * ?????????????????????
     */
    public static final String CharSet_GBK = "GBK";
    /**
     * ??????????????? gb2312
     */
    public static final String CharSet_GB2312 = "GB2312";

    /**
     * 8???UCS??????
     */
    public static final String CharSet_UTS_2 = "ISO-10646-UCS-2";

    /**
     * ????????????????????????????????????
     *
     * @param str        ???????????????????????????
     * @param newCharset ????????????
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String newCharset) {
        try {
            if (str != null) {
                // ???????????????????????????????????????
                byte[] bs = str.getBytes();
                // ????????????????????????????????????
                return new String(bs, newCharset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????????????????????????????
     *
     * @param str        ???????????????????????????
     * @param oldCharset ?????????
     * @param newCharset ????????????
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String oldCharset, String newCharset) {
        try {
            if (str != null) {
                // ?????????????????????????????????????????????????????????????????????
                byte[] bs = str.getBytes(oldCharset);
                // ????????????????????????????????????
                return new String(bs, newCharset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUTF8StringFromGBKString(String gbkStr) {
        try {
            return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError();
        }
    }

    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }

    /**
     * ????????????
     *
     * @param num
     * @return
     */
    public static boolean isInteger(String num) {
        try {
            Integer.parseInt(num);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * ??????-9999
     *
     * @return
     */
    public static String filterStrValue(Object value) {
        String valueStr = String.valueOf(value);
        if (valueStr.length() != 0 && value.equals("-9999")) {
            return "";
        }
        return String.valueOf(value);
    }

    /**
     * String???int?????????
     *
     * @param value
     * @return
     */
    public static int StringToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public static String XmlSpecificCharChange(String chara) {
        if (chara == null)
            return "";
        String result = chara;
        if (result.indexOf("&") >= 0) {
            if (result.indexOf("&amp;") >= 0)
                result = result.replaceAll("&amp;", "&");
            result = result.replaceAll("&", "&amp;");
        }
        if (result.indexOf("'") >= 0) {
            if (result.indexOf("&apos;") >= 0)
                result = result.replaceAll("&apos;", "'");
            result = result.replaceAll("'", "&apos;");
        }
        if (result.indexOf("\"") >= 0) {
            if (result.indexOf("&quot;") >= 0)
                result = result.replaceAll("&quot;", "\"");
            result = result.replaceAll("\"", "&quot;");
        }
        if (result.indexOf("<") >= 0) {
            if (result.indexOf("&lt;") >= 0)
                result = result.replaceAll("&lt;", "<");
            result = result.replaceAll("<", "&lt;");
        }
        if (result.indexOf(">") >= 0) {
            if (result.indexOf("&gt;") >= 0)
                result = result.replaceAll("&gt;", ">");
            result = result.replaceAll(">", "&gt;");
        }
        if (result.length() == 0) {
            result = " ";
        }
        return result;
    }

    /**
     * ??????????????????????????????
     *
     * @param otherOrStr
     * @return
     */
    public static int OtherOrSum(String otherOrStr) {
        char[] c = otherOrStr.toCharArray();
        int otherOr = 0;
        for (int i = 0; i < c.length; i++) {
            otherOr = otherOr ^ c[i];
        }
        return otherOr;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param filePath
     * @param fileName
     * @param xmlstr
     */
    public static Boolean WriteFile(String filePath, String fileName, String xmlstr) {
        try {
            File path = new File(filePath);
            if (!path.exists()) {
                path.mkdirs();
            }

            OutputStream os = new FileOutputStream(filePath + (filePath.endsWith("/") ? "" : "/") + fileName, false);
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(xmlstr);
            osw.close();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param filePath
     * @param fileSize ????????????(byte)
     */
    public static void makeFile(String filePath, long fileSize) {

        File old = new File(filePath);
        if (old.exists()) {
            old.delete();
        }

        FileWriter fw = null;
        try {
            // ??????????????? true ?????????????????????????????????
            fw = new FileWriter(filePath, true);
            File file = new File(filePath);
            while (file.length() < fileSize) {
                fw.write(Math.random() + "\t" + Math.random() + "\n\r");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ????????????
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // ??????????????????????????????????????????
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // ??????????????????????????????????????????
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // ????????????
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // ???????????????????????????
            outBuff.flush();
        } finally {
            // ?????????
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param fileName
     * @return string
     */
    public static String ReadFile(String fileName) {
        try {
            String fileContent = "";
            File f = new File(fileName);
            if (!f.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(f);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                fileContent = fileContent + (fileContent.equals("") ? "" : "\n\r") + line;
            }
            reader.close();
            return fileContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param filePath
     * @param fileName
     * @param stringArray
     */
    public static void WriteFile(String filePath, String fileName, ArrayList<String> stringArray) {
        String input = "";
        for (int i = 0; i < stringArray.size(); i++) {
            input = input + stringArray.get(i) + "\n\r";
        }
        WriteFile(filePath, fileName, input);
    }

    /**
     * ???????????????
     *
     * @param filePath
     * @param oldName
     * @param newName
     */
    public static void FileReName(String filePath, String oldName, String newName) {
        try {
            File path = new File(filePath);
            if (!path.exists()) {
                LogUtil.i(tag, "--filepath not exists---" + filePath);
                return;
            }
            File file = new File(filePath + oldName);
            if (!file.exists()) {
                LogUtil.i(tag, "--source file not exists--" + (filePath + oldName));
                return;
            }

            String destPath = filePath + newName;
            File destDir = new File(destPath.substring(0, destPath.lastIndexOf("/")));
            if (!destDir.exists()) {
                LogUtil.i(tag, "--dest file dir not exists--" + (destPath.substring(0, destPath.lastIndexOf("/"))));
                destDir.mkdirs();
            }
            File dest = new File(destPath); // ????????????
            // ????????????????????????????????????????????????????????????
            if (dest.exists()) {
                deleteAll(filePath + newName);
            }

            file.renameTo(dest);

            LogUtil.i(tag, "--file:" + oldName + "--reNameTo:" + newName + "--Success--");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param path ?????????????????????
     */
    public static void deleteAll(String path) {
        File file = new File(path);
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                // ????????????????????????????????????
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteAll(files[i].getAbsolutePath());
                }
                // ???????????????
                file.delete();
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????***?????????????????????***???
     *
     * @param dirPath    ?????????????????????
     * @param createTime ???????????????????????????????????????
     */
    public static void removeFiles(String dirPath, long createTime) {
        File file = new File(dirPath);
        if (file.exists() && file.isDirectory()) {
            File[] list = file.listFiles();
            for (File f : list) {
                if (f.isFile()) {
                    if ((System.currentTimeMillis() - f.lastModified()) > createTime) {
                        f.delete();
                    }
                } else if (f.isDirectory()) {
                    removeFiles(f.getAbsolutePath(), createTime);
                    if (f.list().length == 0) {
                        f.delete();
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param file
     * @return
     */
    public static boolean IsFaildFileName(String file) {
        boolean isFaild = true;
        file = file.trim();
        if (file.length() < 1) {
            isFaild = false;
        }

        return isFaild;
    }

    /**
     * ???????????????
     */
    public static boolean MatcherString(String str) {
        String regEx = "0-9a-zA-Z._";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        boolean bl = m.find();
        return bl;
    }

    /**
     * ??????????????????????????????????????????,??????:rand.nextInt(?????????-?????????+1)+?????????;
     *
     * @param max
     * @param min
     * @return
     */
    public static int getInterzoneIntRandom(int max, int min) {
        Random rand = new Random();
        return rand.nextInt((max - min + 1) + min);
    }

    /**
     * int ?????????byte????????????
     */
    public static byte[] intToByteArray1(int int32) {
        byte[] result = new byte[4];
        // java???c??????????????????
        result[3] = (byte) ((int32 >> 24) & 0xFF);
        result[2] = (byte) ((int32 >> 16) & 0xFF);
        result[1] = (byte) ((int32 >> 8) & 0xFF);
        result[0] = (byte) (int32 & 0xFF);
        return result;
    }

    /**
     * ?????????????????????
     *
     * @param d
     * @return
     */
    public static byte[] doubleToByteArray(double d) {
        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(d);
        for (int i = 0; i < 8; i++) {
            b[i] = Long.valueOf(l).byteValue();
            l = l >> 8;
        }
        return b;
    }

    /**
     * Long??????????????????C????????????int64
     *
     * @param d
     * @return
     */
    public static byte[] longToByteArray(long d) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = Long.valueOf(d).byteValue();
            d = d >> 8;
        }
        return b;
    }

    /**
     * java???char?????????byte[]?????? ??????Java??????char??????????????????????????????????????????2????????? ???AscII??????char???1??????
     */
    public static byte[] charArrayToByte(char[] charArray) {
        byte[] result = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            int interger = charArray[i];
            byte b = (byte) (interger & 0xFF);
            result[i] = b;
        }
        return result;
    }

    /**
     * java???char???byte?????? ??????Java??????char??????????????????????????????????????????2????????? ???AscII??????char???1??????
     */
    public static byte charToByte(char c) {
        int i = c;
        byte b = (byte) (i & 0xFF);
        return b;
    }

    /**
     * short?????????byte[]??????
     */
    public static byte[] shortToBytes(short short16) {
        byte[] bytesRet = new byte[2];
        // java???c??????????????????
        bytesRet[1] = (byte) ((short16 >> 8) & 0xFF);
        bytesRet[0] = (byte) (short16 & 0xFF);
        return bytesRet;
    }

    /**
     * ??????byte????????????int
     *
     * @param bb
     * @param index ???????????????
     * @return
     */
    public static int getInt(byte[] bb, int index) {
        return ((bb[index + 3] & 0xff) << 24) | ((bb[index + 2] & 0xff) << 16) | ((bb[index + 1] & 0xff) << 8)
                | ((bb[index + 0] & 0xff) << 0);
    }

    /**
     * ??????ftp?????????????????????,???ftp?????????????????????????????????ftp????????????<RCU????????????>??????
     *
     * @param context
     * @param msgFlag         ???????????????flag,WalkCommonPara.MsgDataFlag_*
     * @param eventFlag       download????????? 0x00 upload?????????0x01
     * @param useMsTime       ?????????????????????(??????)
     * @param totalTransBytes ????????????????????????
     * @param interlMsTime    ????????????????????????????????????????????????
     * @param interBytes      ????????????????????????????????????????????????
     */
    public static void sendWriteRcuFtpData(Context context, char msgFlag, int eventFlag, int useMsTime,
                                           long totalTransBytes, int interlMsTime, int interBytes) {
        EventBytes.Builder(context).addInteger(eventFlag).addInteger(useMsTime).addInt64(totalTransBytes)
                .addInteger(interlMsTime).addInteger(interBytes).writeToRcu(msgFlag);
    }

    /**
     * ?????????????????????????????????ROOT?????? ???????????????????????????
     *
     * @param command
     * @return
     */
    public static boolean runRootCommand(String command) {
        return runRootCommand(command, Deviceinfo.getInstance().isCmdChoke());
    }

    /**
     * ?????????????????????????????????ROOT??????
     *
     * @param command ??????ROOT?????????
     * @param isChoke ???????????????????????????
     * @return
     */
    public static boolean runRootCommand(String command, boolean isChoke) {
        if (!ApplicationModel.getInstance().isGeneralMode()) { // &&
            // WalktourApplication.isRootSystem()
            Process process = null;
            DataOutputStream os = null;
            try {
                if (isChoke && command.endsWith("&")) {
                    command = command.substring(0, command.length() - 1);
                }

                LogUtil.d(tag, "---runRootCommand:" + command);
                String suOrSh=Deviceinfo.getInstance().getSuOrShCommand();
                LogUtil.d(tag,"su or sh:"+suOrSh);
                process = Runtime.getRuntime().exec(suOrSh);
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(command + "\n");
                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
                command=null;
            } catch (Exception e) {
                LogUtil.e(tag, e.getMessage(), e);
                return false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    // process.destroy();
                    process = null;
                } catch (Exception e) {
                    // nothing
                }
            }
            //LogUtil.w(tag, "---runRootCommand:" + command + " finish--");
            return true;
        }
        return runCommand(command);
    }

    /**
     * ?????????????????????????????????ROOT??????,?????????log
     *
     * @param command
     * @return
     */
    public static boolean runRootCommandNoLog(String command) {
        if (!ApplicationModel.getInstance().isGeneralMode()) { // &&
            // WalktourApplication.isRootSystem()
            Process process = null;
            DataOutputStream os = null;
            try {
                process = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(command + "\n");
                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    process = null;
                } catch (Exception e) {
                }
            }
            return true;
        }
        return runCommand(command);
    }

    /**
     * ???Root??????????????????<BR>
     * [??????????????????]
     *
     * @param command ??????
     * @return
     */
    public static boolean runCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            command=null;
        } catch (Exception e) {
            LogUtil.e(tag, e.getMessage(), e);
        } finally {
            process = null;
        }
        LogUtil.d(tag, "---runCommand:" + command + " finish--");
        return true;
    }

    /**
     * ???Root??????????????????<BR>
     * [??????????????????]
     *
     * @param command ??????
     * @return
     */
    public static boolean runShCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            LogUtil.d(tag, "---runShCommand:" + command + " start.");
            process = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            LogUtil.w(tag, "---runShCommand:" + e.getMessage() + " finish--");
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process = null;
            } catch (Exception e) {
                LogUtil.w(tag, "---runShCommand:" + e.getMessage() + " finish--");
            }
        }
        LogUtil.d(tag, "---runShCommand:" + command + " finish--");
        return true;
    }

    /**
     * ??????Shell??????
     *
     * @param command
     * @return ??????true, ??????false
     */
    public static boolean runRootCMD(String command) {
        boolean flag = false;
        Process proc = null;
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader err = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("/system/bin/sh");
            // java.lang.ProcessBuilder: Creates operating system processes.
            LogUtil.v("command", command);
            // pb.directory(new File("/"));//??????shell??????????????????
            proc = pb.start();
            // ???????????????????????????????????????SHELL????????????
            in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            // ????????????????????????????????????SHELL???????????????
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            out.println("su \n");
            out.println(command + "\n");
            out.println("exit");
            // proc.waitFor();
            String line;
            while ((line = in.readLine()) != null) {
                LogUtil.d("command", line); // ??????????????????
            }
            while ((line = err.readLine()) != null) {
                LogUtil.d("command", line); // ????????????????????????
            }

            flag = true;
        } catch (Exception e) {
            LogUtil.e("command", e.getMessage());
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (proc != null)
                    proc.destroy();
            } catch (Exception e) {
            }
        }
        return flag;
    }

    /**
     * ????????????Linux?????????????????????
     *
     * @param command
     * @return
     */
    public static String getLinuxCommandResult(String command) {
        Process process = null;
        DataOutputStream os = null;
        StringBuffer sb = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = read.readLine()) != null) {
                sb.append(line + "\n");
            }

            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
                // process.destroy();
                process = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * ????????????????????????????????? ??????????????????????????????{@link android.os.Process#killProcess}
     * ??????????????????????????????????????????????????????????????????
     *
     * @param pname               ?????????
     * @param killOtherAppProcess ????????????????????????????????????
     */
    public static void killProcessByPname(String pname, boolean killOtherAppProcess) {
        try {
            List<ProcessModel> pidList = getPidByPname(getProcess(), pname);
            if (pidList != null) {
                if (killOtherAppProcess) {
                    //???????????????logcat?????????
                    for (ProcessModel process : pidList) {
                        //??????:[user=root, pid=8844, ppid=1, name=logcat]
                        //??????????????????:?????????root??????,??????ppid==1,??????name==logcat,?????????????????????????????????,?????????pid?????????????????????????????????kill
                        if (!"logcat".equals(pname)) {
                            UtilsMethod.runRootCommand("  kill -9 " + process.getPid());
                        }
                    }
                    //kill logcat??????
                    for (ProcessModel process : pidList) {
                        if (process.getName().equals("logcat")) {
                            LogUtil.d("Kill_Logcat", "kill " + process);
                            try {
                                UtilsMethod.runRootCommand("  kill -9 " + process.getPid());
                                Thread.sleep(1000);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                LogUtil.w(tag, "killProcessByPname", ex);
                            }
                        }
                    }
                } else {
                    // ????????????????????????????????????????????????
                    for (ProcessModel process : pidList) {
                        android.os.Process.sendSignal(Integer.valueOf(process.getPid()), android.os.Process.SIGNAL_KILL);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w(tag, "killProcessByPname", e);
        }
    }

    /**
     * ???????????????????????????
     */
    public static List<String> getProcess() {
        String cmd = "ps";
        List<String> orgProcessList = new ArrayList<String>();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader read = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = read.readLine()) != null) {
                orgProcessList.add(line);
                // LogUtil.d(tag, "PS--------------->" + line);
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            LogUtil.w(tag, e.getMessage(), e);
        } finally {
			/*
			 * if (process != null) { process.destroy(); }
			 */
            process = null;
        }
        return orgProcessList;
    }

    /**
     * ??????????????????????????????id ??????????????????{@link String#contains}
     *
     * @param orgProcessList
     * @param pname
     * @return
     */
    public static List<ProcessModel> getPidByPname(List<String> orgProcessList, String pname) {
        List<ProcessModel> pidList = null;
        if (orgProcessList != null && orgProcessList.size() > 0) {
            int indexOfPid = getTagIndex(orgProcessList.get(0), "PID");
            int indexOfPpid = getTagIndex(orgProcessList.get(0), "PPID");
            int indexOfUser = getTagIndex(orgProcessList.get(0), "USER");
            int indexOfName = getTagIndex(orgProcessList.get(0), "NAME");

            if (indexOfPid == -1 || indexOfUser == -1 || indexOfName == -1) {
                return null;
            }

            pidList = new ArrayList<ProcessModel>();
            for (int i = 1; i < orgProcessList.size(); i++) {
                String processInfo = orgProcessList.get(i);
                String[] proStr = processInfo.split(" ");
                List<String> orgInfo = new ArrayList<String>();
                for (String str : proStr) {
                    if (!"".equals(str)) {
                        orgInfo.add(str);
                    }
                }
                // String _pname = orgInfo.get(orgInfo.size() - 1);
                if (processInfo.toLowerCase(Locale.getDefault()).contains(pname.toLowerCase(Locale.getDefault()))) {
                    LogUtil.d(tag, "--ps info:" + processInfo);
                    if (indexOfName >= orgInfo.size() || !orgInfo.get(indexOfName).contains(pname)) {
                        indexOfName = -1;
                        int p = 0;
                        for (String info : orgInfo) {
                            if (info.contains(pname)) {
                                indexOfName = p;
                                break;
                            }
                            p++;
                        }
                    }
                    if (indexOfName >= 0) {
                        ProcessModel proModel = new ProcessModel();
                        proModel.setPid(orgInfo.get(indexOfPid));
                        proModel.setPpid(orgInfo.get(indexOfPpid));
                        proModel.setName(orgInfo.get(indexOfName));
                        proModel.setUser(orgInfo.get(indexOfUser));

                        pidList.add(proModel);
                    }
                }
            }
        }
        return pidList;
    }

    /**
     * ??????PID???????????????
     *
     * @param headStr ????????? USER PID PPID VSIZE RSS WCHAN PC NAME
     * @return
     */
    public static int getTagIndex(String headStr, String tag) {
        String[] heads = headStr.split(" ");
        int indexOfPid = -1;
        for (String h : heads) {
            if (!"".equals(h)) {
                indexOfPid++;
                if (tag.toUpperCase(Locale.getDefault()).equals(h.toUpperCase(Locale.getDefault()))) {
                    break;
                }
            }
        }
        return indexOfPid;
    }

    /**
     * ???Trace????????????ROOT????????????????????????????????????
     *
     * @param context
     */
    public static void giveTraceRoot(String trace, Context context) {
        // int phoneType = MyPhoneState.getInstance().getPhoneType( context);
		/*
		 * switch( phoneType ){ case TelephonyManager.PHONE_TYPE_CDMA:
		 * runRootCommand("chmod 777 /dev/ttyUSB1"); break; default: runRootCommand(
		 * "chmod 777 /dev/smd1"); break; }
		 */
        if (!ApplicationModel.getInstance().isGeneralMode()) { // &&
            // WalktourApplication.isRootSystem()
            runRootCommand("chmod 777 " + trace);
        } else {
            Intent intent = new Intent(WalktourConst.WALKTOUR_EXEC_COMMAND);
            intent.putExtra(WalktourConst.COMMAND, "chmod 777 " + trace);
            context.sendBroadcast(intent);
        }
    }

    /**
     * ?????????????????????????????????IMSI??????MCC
     */
    public static String getMCCByIMSI(String imsi, String disableV) {
        String mcc = disableV;
        if (imsi != null && imsi.length() >= 13) {
            try {
                mcc = imsi.substring(0, 3);
            } catch (Exception e) {
                e.printStackTrace();
                mcc = disableV;
            }
        }
        return mcc;
    }

    /**
     * ?????????????????????????????????IMSI??????MNC
     */
    public static String getMNCByIMSI(String imsi, String disableV) {
        String mnc = disableV;
        if (imsi != null && imsi.length() >= 13) {
            try {
                mnc = "" + Integer.parseInt(imsi.substring(3, 5));
            } catch (Exception e) {
                e.printStackTrace();
                mnc = disableV;
            }
        }
        return mnc;
    }

    /**
     * ????????????
     */
    public static void rebootMachine() {
        runRootCommand("reboot");
    }

    /**
     * ??????????????????????????????(yyyy-MM-dd,HH:mm:ss)
     */
    public static String getSimpleDateFormat0(long time) {
        return new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????(HH:mm:ss)
     */
    public static String getSimpleDateFormat1(long time) {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????(HH:mm)
     */
    public static String getSimpleDateFormat2(long time) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????(""yyyy-MM-dd,HH:mm:ss"")
     */
    public static String getSimpleDateFormat3(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????(""yyyy-MM-dd HHmmss"")
     */
    public static String getSimpleDateFormat4(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????(""yyyy-MM-dd HHmmss"")
     */
    public static String getSimpleDateFormat5(long time) {
        return new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????("yyyy-MM-dd")
     */
    public static String getSimpleDateFormat6(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time);
    }

    /**
     * ??????????????????????????????(""yyyyMMdd-HHmmss"")
     */
    public static String getSimpleDateFormat7(long time) {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(time);
    }

    /**
     * @param value
     * @return ??????6????????????double
     */
    public static String getdecFormat6(double value) {
        return decFormat6.format(value);
    }

    /**
     * ??????????????????
     *
     * @param millonSecond
     */
    public static void setTime(long millonSecond, Context context) {
        String time = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.getDefault()).format(millonSecond);
        if (!ApplicationModel.getInstance().isGeneralMode()) { // &&
            // WalktourApplication.isRootSystem()
            UtilsMethod.runRootCommand("date -s " + time);
            // ???Root???????????????Partener????????????
        } else {
            Intent intent = new Intent(WalktourConst.WALKTOUR_SET_SYSTEMCLOCK);
            intent.putExtra(WalktourConst.WALKTOUR_SYSTEM_TIME, millonSecond);
            context.sendBroadcast(intent);
        }
    }

    /**
     * MD5??????
     *
     * @param plainText
     * @return
     */
    public static String getMD5(String plainText) {
        String encode = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            // System.out.println("result: " + buf.toString());// 32????????????
            // System.out.println("result: " + buf.toString().substring(8, 24));//
            // 16????????????
            encode = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encode;
    }

    /**
     * IP????????????????????? convert ipaddr like 1.1.1.1 to int
     *
     * @param ipString
     * @return
     */
    public static int convertIpString2Int(String ipString) {
        InetAddress addr = null;

        try {
            if (!Verify.isIp(ipString)) {
                ipString = "0.0.0.0";
            }

            addr = InetAddress.getByName(ipString);
            byte[] ipAddr = addr.getAddress();
            int address = 0;
            if (ipAddr.length == 4) {
                address = ipAddr[0] & 0xFF;
                address |= ((ipAddr[1] << 8) & 0xFF00);
                address |= ((ipAddr[2] << 16) & 0xFF0000);
                address |= ((ipAddr[3] << 24) & 0xFF000000);
            }
            LogUtil.d(tag, "--convertIpString2Int:" + ipString + "--result:" + address);
            return address;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * ??????????????????????????????IP????????????????????????IP???????????????????????????IP?????????????????????
     *
     * @param name
     * @return
     */
    public static String getHostAddressByName(String name) {
        try {
            return InetAddress.getByName(name).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();

            return name;
        }
    }

    /**
     * ???????????????????????? testername=twq ??????twq
     *
     * @param str
     * @return
     */
    public static String getEqualsValue(String str) {
        if (str != null && !str.equals("")) {
            if (str.indexOf("=") >= 0)
                return str.substring(str.indexOf("=") + 1);
            return str;
        }
        return "";
    }

    /**
     * ???????????????????????????
     *
     * @param options        ????????????
     * @param maxNumOfPixels ????????????????????????????????????
     * @return ??????????????????????????????????????????1???????????????????????????????????????????????????
     */
    public static int computeSuitedSampleSize(BitmapFactory.Options options, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        double v = w * h / maxNumOfPixels;
        LogUtil.d(tag, "---w=" + w + "---h=" + h + "---v=" + v);
        int samplesize = (int) Math.ceil(Math.sqrt(v));
        if (samplesize > 2) {
            //???????????????inSampleSize???????????????2???n??????????????????????????????
            int count = 1;
            for (int i = 1; i < 10; i++) {
                if (samplesize < Math.pow(2, i)) {
                    count = i - 1;
                    break;
                }
            }
            samplesize = (int) Math.pow(2, count);
        }
        LogUtil.d(tag, "----samplesize=" + samplesize);
        return samplesize > 1 ? samplesize : 1;
    }

    /**
     * ???????????????????????? ?????????route ????????????????????????deviceName ????????????????????????????????????????????????????????????????????????
     *
     * @param route
     * @author tangwq
     */
    public static void delDefaultRoute(String route) { // ,String deviceName
        // ???????????????????????????
        runRootCommand("ip route del default dev " + route + "&");
    }

    /**
     * ?????????????????????
     *
     * @param d
     * @return
     */
    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static final double EARTH_RADIUS = 6378.137;// ????????????

    /**
     * ???GPS?????????????????? ?????????KM???
     *
     * @param lat1 ??????????????????
     * @param lng1 ??????????????????
     * @param lat2 ??????????????????
     * @param lng2 ??????????????????
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        LogUtil.d(tag, "--la1:" + lat1 + "--ln1:" + lng1 + "--la2:" + lat2 + "--ln2:" + lng2);
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(
                Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        // LogUtil.d(tag,"---res1"+s);
        s = Math.round(s * 10000) / 10000.0;
        String res = decFormat4.format(s);
        LogUtil.d(tag, "---res=" + res);
        // getDistance_JW(lat1,lng1,lat2,lng2);
        return Double.parseDouble(res);
    }

    public static double getDistance_JW(double wd1, double jd1, double wd2, double jd2) {
        double x, y, out;
        double PI = 3.14159265;
        double R = 6.371229 * 1e6;

        x = (jd2 - jd1) * PI * R * Math.cos(((wd1 + wd2) / 2) * PI / 180) / 180;
        y = (wd2 - wd1) * PI * R / 180;
        out = Math.hypot(x, y);
        LogUtil.d(tag, "---distance_jw out:" + out);
        return out / 1000;
    }

    public static byte[] getVersionKey(String verName) {
        // -----?????????----------
        if (verName.length() < 4) {
            for (int i = 0; i < 4 - verName.length(); i++) {
                verName += "$";
            }
        } /*
			 * else if(verName.length() > 8){ verName = verName.substring(0,8); }
			 */
        verName = verName.substring(verName.length() - 4) + verName.substring(0, 4);

        char[] strs = verName.toCharArray();
		/*
		 * char[] endStr = new char[8]; for(int i=0;i<strs.length;i++){ endStr[(i+4)
		 * % 8] = strs[i]; }
		 */

		/*
		 * for(int i=0;i<strs.length;i++){ System.out.println("--" + i +":" +
		 * strs[i]); }
		 */

        // -----?????????----------
        String pkStr = "#rTi&a%b";
        char[] pkChar = pkStr.toCharArray();
        for (int i = 0; i < pkChar.length; i++) {
            strs[i] ^= pkChar[i];
        }

        // System.out.println(new String(strs));
		/*
		 * for(int i=0;i<strs.length;i++){ System.out.println("--" + i +":" +
		 * strs[i]); }
		 */
        return new String(strs).getBytes();
    }

    public static String jam(String str) {
        String s6 = Base64.encodeToString(str.getBytes());

        char[] c = s6.toCharArray();
        int l = c.length;
        for (int i = 0; i < l / 3; i++) {
            char b = c[i];
            c[i] = c[l - 1 - i];
            c[l - 1 - i] = b;
        }
        String cs = new String(c);

        String s62 = Base64.encodeToString(cs.getBytes());

        return s62;
    }

    public static String jaem2(String j1) {
        int l = j1.length() / 5;
        if (l >= 1) {
            return j1.substring(j1.length() - l) + j1.substring(j1.length() - l * 2, j1.length() - l)
                    + j1.substring(l * 2, j1.length() - l * 2) + j1.substring(l, l * 2) + j1.substring(0, l);
        }
        return j1;
    }

    public static String jem(String str) {
        if (StringUtil.isEmpty(str))
            return "";
        String s6 = new String(Base64.decode(str));

        char[] c = s6.toCharArray();
        int l = c.length;
        for (int i = 0; i < l / 3; i++) {
            char b = c[i];
            c[i] = c[l - 1 - i];
            c[l - 1 - i] = b;
        }
        String cs = new String(c);

        String s62 = new String(Base64.decode(cs));
        return s62;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    private static long getNetTimeFromSharePlatform() {
        String[] urls = {"http://61.143.60.84:63997/query_system_time.do",
                "http://112.91.151.37:63997/query_system_time.do"};
        for (String urlStr : urls) {
            URL url=null;
            HttpURLConnection uc=null;
            InputStream in=null;
            BufferedInputStream bis=null;
            try {
                LogUtil.d(tag,"getNetTimeFromSharePlatform=xstart:"+urlStr);
                url = new URL(urlStr);
                // ??????????????????
                uc = (HttpURLConnection) url.openConnection();
                long startTime = System.currentTimeMillis();
                uc.setConnectTimeout(500);
                uc.setReadTimeout(2000);
                uc.setUseCaches(false);
                // ????????????
                uc.connect();
                int resCode = uc.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    long endTime = System.currentTimeMillis();
                    int contentLength = uc.getContentLength();
                    in = uc.getInputStream();
                    bis = new BufferedInputStream(in);
                    // ??????????????????
                    byte[] receData = new byte[contentLength];
                    // ?????????????????????
                    int readLength = bis.read(receData, 0, contentLength);
                    // ??????????????????
                    int readAlreadyLength = readLength;
                    while (readAlreadyLength < contentLength) {
                        readLength = bis.read(receData, readAlreadyLength, contentLength - readAlreadyLength);
                        readAlreadyLength = readAlreadyLength + readLength;
                    }
                    String time = new String(receData);
                    LogUtil.d(tag,"getNetTimeFromSharePlatform=xend:"+urlStr);
                    return Long.parseLong(time) + (endTime - startTime) / 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null!=bis){
                    try {
                        bis.close();
                    }catch(Exception e1){
                        e1.printStackTrace();
                    }
                    bis=null;
                }
                if(null!=in){
                    try {
                        in.close();
                    }catch(Exception e1){
                        e1.printStackTrace();
                    }
                    in=null;
                }
                if(null!=uc){
                    try {
                        uc.disconnect();
                    }catch(Exception e1){
                        e1.printStackTrace();
                    }
                    uc=null;
                }
            }
        }
        return -1;
    }

    /**
     * ??????http://www.bjtime.cn ????????????????????????
     *
     * @return
     */
    public static long getNetTimeByBjtime() {
        long checkTime = getNetTimeFromSharePlatform();
        if (checkTime > 0)
            return checkTime;
        String[] urls = {"http://www.baidu.com/", "http://bjtime.cn/", "http://www.dinglicom.com/"};
        checkTime = 1450000000000L;
        int maxLoops = 3;
        for (String urlStr : urls) {
            for (int loops = 0; loops < maxLoops; loops++) {
                URL url=null;
                HttpURLConnection uc=null;
                try {
                    LogUtil.d(tag,"getNetTimeFromSharePlatform=ystart:"+urlStr);
                    url = new URL(urlStr);
                    // ??????????????????
                    uc = (HttpURLConnection)url.openConnection();
                    uc.setConnectTimeout(500);
                    uc.setReadTimeout(2000);
                    uc.setUseCaches(false);
                    // ????????????
                    uc.connect();
                    // ????????????????????????????????????????????????500???????????????????????????????????????
                    long time = uc.getDate() + 500;
                    LogUtil.d(tag,"getNetTimeFromSharePlatform=yend:"+urlStr+",time="+time);
                    if (time > checkTime) {
                        return time;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    if(null!=uc){
                        try{
                            uc.disconnect();
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }finally {
                            uc=null;
                        }
                    }
                    uc=null;
                    url=null;
                }
            }
        }
        return -1;
    }

    /**
     * ????????????????????????NTP(Network Time Protocol) ?????????????????????????????????
     *
     * @return
     * @throws Exception
     */
    public static long getNetTimeByNTP() throws Exception {
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            String timeServerUrl = "time-a.nist.gov";
            InetAddress timeServerAddress = InetAddress.getByName(timeServerUrl);
            timeClient.setDefaultTimeout(5000);
            // timeClient.setSoTimeout(5000);
            TimeInfo timeInfo = timeClient.getTime(timeServerAddress);
            TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
            // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd
            // HH:mm:ss",Locale.getDefault());
            return timeStamp.getTime();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * ???bitmap???????????????????????????
     *
     * @param bitmap
     * @param filePath
     * @author tangwq
     */
    public static void SaveBitmapToFile(Bitmap bitmap, String filePath) {
        File file = new File(filePath);
        FileOutputStream out;
        try {
            LogUtil.i(tag, "---save file paht:" + filePath + "---map is null:" + (bitmap == null));
            if (bitmap != null) {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException en) {
            en.printStackTrace();
        } catch (IOException ei) {
            ei.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???raw??????????????????????????? ???
     *
     * @param context ?????????
     * @param rawId   ????????????ID
     * @param desFile ????????????
     * @return ??????????????????
     */
    public static boolean writeRawResource(Context context, int rawId, File desFile) {
        return writeRawResource(context, rawId, desFile, false);
    }

    /**
     * ???raw??????????????????????????? ???(???????????????????????????????????????????????????????????????????????????)
     *
     * @param context     ?????????
     * @param rawId       ????????????ID
     * @param desFile     ????????????
     * @param isOverwrite ??????????????????????????????
     * @return ??????????????????
     */
    public static boolean writeRawResource(Context context, int rawId, File desFile, boolean isOverwrite) {
        LogUtil.d(tag, "---writeRawResource---:" + desFile.getAbsolutePath());
        try {
            if (desFile == null || (!isOverwrite && desFile.exists()))
                return false;
            long lastLenth=SharePreferencesUtil.getInstance(context).getLong(desFile.getAbsolutePath(),0l);//???????????????????????????

            if (isOverwrite&&lastLenth==desFile.length()&&lastLenth>0){
                LogUtil.d(tag,desFile.getAbsolutePath()+" file is exist.dont update."+"lastLenth="+lastLenth+"???desFile.lastModified()="+desFile.length());
                return false;
            }else{
                LogUtil.d(tag,desFile.getAbsolutePath()+" file is exist,need update."+"lastLenth="+lastLenth+"???desFile.lastModified()="+desFile.length());
            }
            if (isOverwrite && desFile.exists())
                desFile.delete();
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            if (!desFile.exists()) {
                try {
                    if (!desFile.createNewFile())
                        return false;
                } catch (Exception e2) {
                    return writeRawResource2(context, rawId, desFile, isOverwrite);
                }
            }
            InputStream in = context.getResources().openRawResource(rawId);
            OutputStream out = null;
            try {
                out = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
                SharePreferencesUtil.getInstance(context).saveLong(desFile.getAbsolutePath(),desFile.length());
            } catch (FileNotFoundException fe) {
                LogUtil.e(tag, fe.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            UtilsMethod.runRootCommand("chmod 777 " + desFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            LogUtil.e(tag, e.getMessage(), e);
            return false;
        }
    }

    /**
     * ???raw??????????????????????????? ???
     *
     * @param context     ?????????
     * @param rawId       ????????????ID
     * @param desFile     ????????????
     * @param isOverwrite ??????????????????????????????
     * @return ??????????????????
     */
    public static boolean writeRawResource(Context context, File srcsFile, File desFile, boolean isOverwrite) {
        LogUtil.d(tag, "---writeRawResource---:" + desFile.getAbsolutePath());
        try {
            if (desFile == null || (!isOverwrite && desFile.exists()))
                return false;
            if (isOverwrite && desFile.exists())
                desFile.delete();
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            if (!desFile.exists()) {
                try {
                    if (!desFile.createNewFile())
                        return false;
                } catch (Exception e2) {
                    return writeRawResource2(context, srcsFile, desFile, isOverwrite);
                }
            }
            InputStream in = new FileInputStream(srcsFile);;
            OutputStream out = null;
            try {
                out = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            } catch (FileNotFoundException fe) {
                LogUtil.e(tag, fe.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            UtilsMethod.runRootCommand("chmod 777 " + desFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            LogUtil.e(tag, e.getMessage(), e);
            return false;
        }
    }
    public static boolean writeRawResourceWithDecodeBase64(Context context, int rawId, File desFile, boolean isOverwrite) {
        LogUtil.d(tag, "---writeRawResource---:" + desFile.getAbsolutePath());
        try {
            if (desFile == null || (!isOverwrite && desFile.exists()))
                return false;
            if (isOverwrite && desFile.exists())
                desFile.delete();
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            if (!desFile.exists()) {
                try {
                    if (!desFile.createNewFile())
                        return false;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            InputStream in = context.getResources().openRawResource(rawId);
            String contentEncode = IOUtils.readStreamAsString(in, "UTF-8");
            InputStream inContentDecode = new ByteArrayInputStream(Base64Util.decodeToString(contentEncode).getBytes("UTF-8"));
            OutputStream out = null;
            try {
                out = new FileOutputStream(desFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inContentDecode.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            } catch (FileNotFoundException fe) {
                LogUtil.e(tag, fe.getMessage());
            } finally {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (inContentDecode != null) {
                    inContentDecode.close();
                }
            }
            UtilsMethod.runRootCommand("chmod 777 " + desFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            LogUtil.e(tag, e.getMessage(), e);
            return false;
        }
    }

    /**
     * ???raw??????????????????????????????????????????????????????lib????????????????????????????????????
     *
     * @param context     ?????????
     * @param rawId       ????????????ID
     * @param desFile     ????????????
     * @param isOverwrite ??????????????????????????????
     * @return ??????????????????
     */
    public static boolean writeRawResource2(Context context, int rawId, File desFile, boolean isOverwrite) {
        LogUtil.d(tag, "---writeRawResource2---:" + desFile.getAbsolutePath());
        try {
            if (desFile == null || (!isOverwrite && desFile.exists()))
                return false;
            if (isOverwrite && desFile.exists())
                desFile.delete();
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            File tempFile;
            String tempFileName = null;
            String name = desFile.getName();

            InputStream in = context.getResources().openRawResource(rawId);
            OutputStream out;
            try {
                out = new FileOutputStream(desFile);
            } catch (FileNotFoundException fe) {
                LogUtil.e(tag, fe.getMessage());
                tempFileName = String.format("%s/%s", context.getFilesDir().getParent(), name);
                tempFile = new File(tempFileName);
                out = new FileOutputStream(tempFile);
            }

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            out.close();
            in.close();

            if (tempFileName != null) {
                UtilsMethod.runRootCommand(String.format("cat %s > %s", tempFileName, desFile.getAbsolutePath()));
                UtilsMethod.runRootCommand(String.format("rm -r %s", tempFileName));
                UtilsMethod.runRootCommand("chmod 777 " + desFile.getAbsolutePath());
            }

            return true;

        } catch (Exception e) {
            LogUtil.e(tag, e.getMessage());
        }

        return false;
    }

    /**
     * ???raw??????????????????????????????????????????????????????lib????????????????????????????????????
     *
     * @param context     ?????????
     * @param rawId       ????????????ID
     * @param desFile     ????????????
     * @param isOverwrite ??????????????????????????????
     * @return ??????????????????
     */
    public static boolean writeRawResource2(Context context, File srsscFile, File desFile, boolean isOverwrite) {
        LogUtil.d(tag, "---writeRawResource2---:" + desFile.getAbsolutePath());
        try {
            if (desFile == null || (!isOverwrite && desFile.exists()))
                return false;
            if (isOverwrite && desFile.exists())
                desFile.delete();
            if (!desFile.getParentFile().exists()) {
                desFile.getParentFile().mkdirs();
            }
            File tempFile;
            String tempFileName = null;
            String name = desFile.getName();

            InputStream in = new FileInputStream(srsscFile);
            OutputStream out;
            try {
                out = new FileOutputStream(desFile);
            } catch (FileNotFoundException fe) {
                LogUtil.e(tag, fe.getMessage());
                tempFileName = String.format("%s/%s", context.getFilesDir().getParent(), name);
                tempFile = new File(tempFileName);
                out = new FileOutputStream(tempFile);
            }

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            out.close();
            in.close();

            if (tempFileName != null) {
                UtilsMethod.runRootCommand(String.format("cat %s > %s", tempFileName, desFile.getAbsolutePath()));
                UtilsMethod.runRootCommand(String.format("rm -r %s", tempFileName));
                UtilsMethod.runRootCommand("chmod 777 " + desFile.getAbsolutePath());
            }

            return true;

        } catch (Exception e) {
            LogUtil.e(tag, e.getMessage());
        }

        return false;
    }

    /**
     * ??????SDK???????????????<BR>
     * [??????????????????]
     *
     * @return SDK?????????
     */
    public static int getSDKVersionNumber() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * ?????????????????????7??????16??????CELLID?????????????????????4????????????ID ??????????????????????????????????????????16??????????????????
     * ??????27069589-->????????????101001...10101--->??????16???????????????????????????3221
     *
     * @param longCellid
     * @return
     * @author tangwq
     */
    public static int getLongTosShortCellID(int longCellid) {
        String binaryStr = Integer.toBinaryString(longCellid);
        String binary16 = (binaryStr.length() > 16 ? binaryStr.substring(binaryStr.length() - 16) : binaryStr);
        int shortId = Integer.valueOf(binary16, 2);
        // LogUtil.d(tag,"--longCellid:"+longCellid+"--binary:"+binaryStr+"---binary16:"+binary16+"--shortId:"+shortId);
        return shortId;
    }

    /**
     * [???????????????cellid?????????RNCID]<BR>
     * ???????????????????????????????????????28bit???7???16????????????3??????RNC ID,???4??????Cell ID RCN ??????????????????????????????????????????
     *
     * @param longCellid
     * @return
     */
    public static int getLongCellIdToRNCId(int longCellid) {
        String binaryStr = Integer.toBinaryString(longCellid);
        // ??????????????????????????????????????????28????????????0
        int subLeng = 28 - binaryStr.length();
        for (int i = 0; i < subLeng; i++) {
            binaryStr = "0" + binaryStr;
        }
        String head12 = binaryStr.substring(0, 12);
        // ??????12????????????????????????
        return Integer.valueOf(head12, 2);
    }

    /**
     * [???????????????cellid?????????RNCID]<BR>
     * ???????????????????????????????????????28bit???7???16????????????3??????RNC ID,???4??????Cell ID RCN ??????????????????????????????????????????
     *
     * @param longCellid
     * @return
     */
    public static String getLongCellIdToRNCId(String longCellid) {
        String shortRNCId = "";
        try {
            if (longCellid != null && !longCellid.equals("")) {
                int intCellid = Integer.parseInt(longCellid);
                shortRNCId = String.valueOf(getLongCellIdToRNCId(intCellid));
            }
        } catch (Exception e) {
            e.printStackTrace();
            shortRNCId = "0";
        }
        return shortRNCId;
    }

    /**
     * ?????????????????????7??????16??????CELLID?????????????????????4????????????ID ??????????????????????????????????????????16??????????????????
     * ??????27069589-->????????????101001...10101--->??????16???????????????????????????3221
     *
     * @param longCellid
     * @return
     * @author tangwq
     */
    public static String getLongTosShortCellID(String longCellid) {
        String shortCellId = "";
        try {
            if (longCellid != null && !longCellid.equals("")) {
                String binaryStr = Integer.toBinaryString(Integer.parseInt(longCellid));
                String binary16 = (binaryStr.length() > 16 ? binaryStr.substring(binaryStr.length() - 16) : binaryStr);
                int shortId = Integer.valueOf(binary16, 2);
                shortCellId = String.valueOf(shortId);
                // LogUtil.d(tag,"--longCellid:"+longCellid+"--binary:"+binaryStr+"---binary16:"+binary16+"--shortId:"+shortId);
            }
        } catch (Exception e) {
            shortCellId = "";
            e.printStackTrace();
        }
        return shortCellId;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param str
     * @param target
     * @return
     * @author tangwq
     */
    public static int getTargetStrTimes(String str, char target) {
        if (str != null) {
            int count = 0;
            char[] strs = str.toCharArray();
            for (int i = 0; i < strs.length; i++) {
                if (target == strs[i]) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    /**
     * [??????????????????????????????]<BR>
     * [??????????????????]
     *
     * @param context
     * @return
     */
    public static String getCurrentVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            LogUtil.w("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * [???bps????????????kbps]<BR>
     * [??????????????????]
     *
     * @param bps
     * @return
     */
    public static String bps2Kbps(String bps) {
        String kbps = "";
        try {
            if (bps != null && !bps.equals("")) {
                float bpsf = Float.parseFloat(bps) / UtilsMethod.kbyteRage;
                kbps = decFormat.format(bpsf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kbps;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param bps
     * @param multiple
     * @return
     */
    public static String enlargeMultiple(String bps, float multiple) {
        String kbps = "";
        try {
            if (bps != null && !bps.equals("")) {
                float bpsf = Float.parseFloat(bps) * multiple;
                kbps = decFormat.format(bpsf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kbps;
    }

    /**
     * ?????????????????????????????????
     *
     * @param bps
     * @param multiple
     * @return
     */
    public static String narrowMultiple(String bps, float multiple) {
        String kbps = "";
        try {
            if (bps != null && !bps.equals("")) {
                float bpsf = Float.parseFloat(bps) / multiple;
                kbps = decFormat.format(bpsf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return kbps;
    }

    /**
     * ??????????????????????????????
     */
    public static String addUnit(String value, String unit) {
        if (value == null || value.equals("")) {
            return "";
        }
        return value + " " + unit;
    }

    /**
     * ?????????????????????
     *
     * @param value
     * @param decimal
     * @return
     */
    public static String decimalMath(String value, int decimal) {
        try {
            if (!value.equals("-") && !value.equals("") && isNumericParm(value)) {
                DecimalFormat decimalFormat = new DecimalFormat(decimalFormatStr(decimal));
                return decimalFormat.format(Double.parseDouble(value));
            }
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * ????????????????????????
     *
     * @param str
     * @return
     */
    public static boolean isNumericParm(String str) {
        Pattern pattern = Pattern.compile("([+-]?\\d*)|([+-]?\\d*\\.\\d+)");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ??????????????????
     *
     * @param decimal
     * @return
     */
    private static String decimalFormatStr(int decimal) {
        StringBuffer sb = new StringBuffer();
        sb.append("#");
        if (decimal > 0) {
            sb.append(".");
            for (int i = 0; i < decimal; i++) {
                sb.append("#");
            }
        }

        return sb.toString();
    }

    /***
     * ????????????
     *
     * @param tickerText
     *          ?????????????????????
     * @param strBroadcast
     *          ???????????????????????????
     */
    @SuppressWarnings("deprecation")
    public static void showNotification(Context context, String tickerText, String strBroadcast) {
        // ?????????????????????
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // notification //????????????, ????????????????????????????????????,?????????????????????
        Notification.Builder notification = new Notification.Builder(context);
        notification.setTicker(tickerText);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0,
                new Intent((strBroadcast == null) ? "" : strBroadcast), 0);
        // must set this for content view, or will throw a exception
        // ??????????????????????????????????????????????????????notification?????????????????????
        // setLatestEventInfo(),??????????????????????????????????????????????????????notify()???
        notification.setAutoCancel(true);
        notification.setContentIntent(contentIntent);
        notification.setContentTitle(context.getString(R.string.sys_alarm));
        notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

    /**
     * ????????????????????????????????????
     */
    @SuppressWarnings("deprecation")
    public static void showNotification(Context context, String tickerText, Class<?> cls) {
        // ?????????????????????
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // notification //????????????, ????????????????????????????????????,?????????????????????
        Notification.Builder notification = new Notification.Builder(context);
        notification.setTicker(tickerText);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        // Intent ??????????????????????????????Activity
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, (cls != null ? cls : TestService.class)), 0);
        // must set this for content view, or will throw a exception
        // ??????????????????????????????????????????????????????notification?????????????????????
        // setLatestEventInfo(),??????????????????????????????????????????????????????notify()???
        notification.setAutoCancel(true);
        notification.setContentIntent(contentIntent);
        notification.setContentTitle(context.getString(R.string.sys_alarm));
        notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    public static String numToShowHexStr(String str) {
        // 2147483647 ???7FFFFFFF?????????
        if (str != null && !str.trim().equals("") && !str.equals("2147483647")) {
            try {
                return Integer.toHexString(Integer.parseInt(str)).toUpperCase(Locale.getDefault());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    /**
     * ????????????????????????????????????????????????
     */
    public static String numToShowDecimal2(String str) {
        if (str != null && !str.trim().equals("")) {
            try {
                return decFormat.format(TypeConver.StringToDouble(str));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     *
     * @param value ???????????????????????????
     * @param scale ??????
     * @return
     */
    public static String transferByScale(String value, int scale) {
        String result = "";
        if (!value.equals("")) {
            try {
                float invalue = Float.parseFloat(value);
                float f = invalue / (scale == 0 ? 1 : scale);
                result = decFormat.format(f);
            } catch (Exception e) {
                LogUtil.w(tag, "", e);
            }
        }

        return result;
    }

    /***
     * ???????????????????????????float
     *
     * @param str
     * @return
     */
    public static float StringToFloat(String str) {
        try {
            if (!str.equals("")) {
                return Float.parseFloat(str);
            }
        } catch (Exception e) {
            LogUtil.w(tag, "", e);
        }
        return 0.0f;
    }

    /**
     * ???????????????????????????float,?????????????????????????????????????????????????????????
     *
     * @param str
     * @param nullvalue
     * @return
     */
    public static float StringToFloat(String str, float nullvalue) {
        try {
            if (!str.equals("")) {
                return Float.parseFloat(str);
            }
        } catch (Exception e) {
            LogUtil.w(tag, "", e);
        }
        return nullvalue;
    }

    /**
     * ???16???????????????
     *
     * @return
     */
    public static String getBytesHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex + " ");
        }
        return sb.toString();
    }

    /**
     * ????????????CPU????????? zhihui.lian
     */

    public static long total = 0;
    public static long idle = 0;
    public static double usage = 0;

    public static double readUsage() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();

            String[] toks = load.split(" ");

            long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
            long currIdle = Long.parseLong(toks[5]);
            usage = (currTotal - total) * 100.0f / (currTotal - total + currIdle - idle);
            total = currTotal;
            idle = currIdle;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return usage;

    }

    /**
     * ??????SDcard?????????????????????????????????(Byte)
     */
    @SuppressWarnings("deprecation")
    public static Long getAvaiableSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            // ??????sdcard????????????
            StatFs sf = new StatFs(sdcardDir.getPath());
            // ??????block???SIZE
            long bSize = sf.getBlockSize();
            // ????????????Block?????????
            long availaBlock = sf.getAvailableBlocks();
            return bSize * availaBlock;
        }
        return (long) -1;
    }

    /**
     * Java???????????? ?????????????????????
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * ??????????????????????????????String????????????
     *
     * @return
     */
    public static long getSeconds(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long millionSeconds = 0;
        try {
            millionSeconds = sdf.parse(time).getTime(); // ??????
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds * 1000;
    }

    /**
     * ??????strings??????????????????fieldname????????????????????????
     *
     * @param context
     * @param fieldName
     * @return
     */
    public static String getStringsByFieldName(Context context, String fieldName) {
        try {
            return context.getString(R.string.class.getField(fieldName).getInt(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /*
     * ?????????String?????????????????? ??????2013-10-24 07:18:43 ?????? ??? 2013/10/24
     */
    public static String stringToDateShort(String time) {
        String dateString = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = formatter.parse(time);
            DateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            dateString = formatter1.format(date);
        } catch (ParseException e) {
            e.getStackTrace();
        }
        return dateString == null ? "time err" : dateString;
    }

    /**
     * UTC?????????????????????
     *
     * @param utcTime
     * @param utcTimePatten
     * @param localTimePatten
     * @return ????????????
     */
    public static String utc2LocalTime(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten, Locale.getDefault());
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        String localTime = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
            SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten, Locale.getDefault());
            localFormater.setTimeZone(TimeZone.getDefault());
            localTime = localFormater.format(gpsUTCDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localTime;
    }

    /**
     * ????????????????????????????????????????????????,??????????????????????????????
     *
     * @param filePath ??????????????????
     * @return File
     */
    public static void creatFile(String filePath) {
        String[] fileArray = filePath.split("/");
        String filePathTemp = "";

        try {
            for (int i = 0; i < fileArray.length - 1; i++) {
                filePathTemp += fileArray[i];
                filePathTemp += File.separator;
            }
            filePathTemp += fileArray[fileArray.length - 1];
            if (filePathTemp.equals("")) {
                filePathTemp = filePath;
            }
            File f = new File(filePathTemp);
            File pf = f.getParentFile();
            if (null != pf && !pf.exists()) {
                pf.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * ?????????String?????????????????? ??????20131024071843 ?????? ??? 2013/10/24
     */
    public static String stringToDate(String time, String format) {
        Log.i(tag, "------fileName time" + "time");
        String dateString = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            Date date = formatter.parse(time);
            DateFormat formatter1 = new SimpleDateFormat(format, Locale.getDefault());
            dateString = formatter1.format(date);
        } catch (ParseException e) {
            e.getStackTrace();
        }
        return dateString == null ? "time err" : dateString;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param fmtStr
     * @param paramValues
     * @return
     */
    public static String formatParamToShow(String fmtStr, String paramValues) {
        String result = "";
        if (paramValues != null && fmtStr != null) {
            Object[] values = paramValues.split("@@");
            // ????????????JNI?????????????????????
            for (int i = 0; i < values.length; i++) {
                String fmValue = values[i].toString();
                try {
                    float value = Float.parseFloat(fmValue);
                    if (value == -9999) {
                        values[i] = "";
                    } else {
                        values[i] = UtilsMethod.decFormat.format(value);
                    }
                } catch (Exception e) {
                    values[i] = fmValue;
                }
            }

            result = String.format(fmtStr, values);
        }
        return result;
    }

    /**
     * ??????????????????????????????????????????
     */
    public static String arrayToString(int[] params) {
        StringBuffer sb = new StringBuffer();
        for (int param : params) {
            sb.append(param);
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * ??????????????????????????????
     *
     * @param fileName
     * @return
     */
    public static boolean existFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * ???1970??????????????????,Wone??????
     */
    public static String getSpecialProcesTime(long time) {
        String timeStr = "";

        try {
            Calendar c = Calendar.getInstance();
            c.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
            c.setTimeInMillis(c.getTimeInMillis() + time * 1000);
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            timeStr = f.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    public static boolean WriteFile(String fileName, String content) {
        int index = fileName.lastIndexOf("/");
        String path = fileName.substring(0, index);
        String name = fileName.substring(index, fileName.length());
        return WriteFile(path, name, content);
    }

    /**
     * ???????????????
     */
    public static void ThreadSleep(int times) {
        try {
            Thread.sleep(times);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setTimeAuto(Context context, boolean auto) {
		/*
		 * //??????ROM????????????,????????????????????????????????????????????? Intent closeAutoTimes = new
		 * Intent(WalktourConst.AUTOMATIC_DATE_TIME_SETTING);
		 * closeAutoTimes.putExtra(WalktourConst.AUTOMATICTYPE, 0);
		 * context.sendBroadcast(closeAutoTimes);
		 */

        Settings.System.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME, auto ? 1 : 0);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isAutoTimeOpen(ContentResolver cResolver) {
        // return Settings.Global.getInt(getContentResolver(),
        // Settings.Global.AUTO_TIME, 0) == 1;
        return Settings.System.getInt(cResolver, Settings.System.AUTO_TIME, 0) == 1;
    }

    public static final int MARKSTATE_DEL = 0;
    public static final int MARKSTATE_ADD = 1;

    /**
     * ???????????????$MARK???????????????
     *
     * @param markState MARK?????????,0????????????????????????,1????????????????????????,????????????????????????????????????????????????
     * @param markX
     * @param markY
     * @return
     */
    public static StringBuffer buildMarkStr(int markState, double markX, double markY) {
        StringBuffer mark = new StringBuffer();
        mark.append("$");
        mark.append(markState == MARKSTATE_DEL ? "-" : "");
        mark.append("MARK");
        mark.append(",");
        mark.append(decFarmat10.format(markX));
        mark.append(",");
        mark.append(decFarmat10.format(markY));
        mark.append(",0");

        // ???????????????,??????????????????????????????????????????Walktour Mobile?????????
        int checksum = 0;
        for (int i = 1; i < mark.length(); i++) {
            checksum ^= mark.charAt(i);
        }
        String strSum = Integer.toHexString(checksum);
        if (strSum.length() == 1) {
            strSum = "0" + strSum;
        }
        mark.append("*");
        mark.append(strSum);
        mark.append("\r\n");

        return mark;
    }

    /**
     * ????????????
     *
     * @param molecule    ??????
     * @param denominator ??????
     * @return
     */
    public static String getIntMultiple(float molecule, float denominator) {
        return TotalDataInterface.getIntMultiple(molecule, denominator, 100, "%");
    }

    /**
     * ??????utc??????
     */
    public static String getUTCTime() {
        String gmtTime = "0";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HHmmss.sss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
            gmtTime = sdf.format(System.currentTimeMillis());
            Log.i(tag, gmtTime);
        } catch (Exception e) {
            LogUtil.w(tag, "getUTCTime", e);
            return "0";
        }
        return gmtTime;
    }

    /**
     * ???UTC??????????????????????????????
     *
     * @param UTCTime
     * @return
     */
    public static String getLocalTimeFromUTC(String UTCTime) {
        java.util.Date UTCDate = null;
        String localTimeStr = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:hh", Locale.getDefault());
        try {
            UTCDate = format.parse(UTCTime);
            format.setTimeZone(TimeZone.getTimeZone("GMT-8"));
            localTimeStr = format.format(UTCDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return localTimeStr;
    }

    /**
     * ???????????????UTC??????????????????????????????????????????????????????
     *
     * @return
     */
    public static long getLocalTimeByUTCTime() {
        return getLocalTimeByUTCTime(System.currentTimeMillis());
    }

    /**
     * ???????????????UTC??????????????????????????????????????????????????????
     *
     * @param utcTime
     * @return
     */
    public static long getLocalTimeByUTCTime(long utcTime) {
        // 1????????????????????????
        java.util.Calendar cal = java.util.Calendar.getInstance();
        // 2???????????????????????????
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        // 3????????????????????????
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        return utcTime + zoneOffset + dstOffset;
    }

    /**
     * ??????????????????????????????
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * ??????java??????????????????????????????.???0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// ???????????????0
            s = s.replaceAll("[.]$", "");// ??????????????????.?????????
        }
        return s;
    }

    /**
     * ??????????????????ID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param str
     * @param startChart
     * @param EndChart
     * @return
     */
    public static String getSubStringByChart(String str, String startChart, String EndChart) {
        return getSubStringByChart(str, startChart, EndChart, "");
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param str        ????????????????????????
     * @param startChart ?????????????????????
     * @param EndChart   ?????????????????????
     * @param defaultStr ?????????????????????????????????????????????
     * @return
     */
    public static String getSubStringByChart(String str, String startChart, String EndChart, String defaultStr) {
        if (str.lastIndexOf(startChart) > 0 && str.lastIndexOf(EndChart) > str.lastIndexOf(startChart)) {
            return str.substring(str.lastIndexOf(startChart) + 1, str.lastIndexOf(EndChart));
        }

        return defaultStr;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param contex
     * @param on
     */
    public static void setSpeakerphoneOn(Context contex, boolean on) {
        AudioManager audioManager = (AudioManager) contex.getSystemService(Context.AUDIO_SERVICE);
        if (on) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);// ???????????????
			/*
			 * audioManager.setRouting(AudioManager.MODE_NORMAL,
			 * AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
			 * setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
			 */
            // ??????????????????Earpiece?????????????????????????????????????????????
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param mContext
     * @param serviceName ?????????+???????????????????????????net.loonggg.testbackstage.TestService???
     * @return true?????????????????????false??????????????????????????????
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * ?????????HHmm?????????????????????,??????????????????
     *
     * @param hhmm 12:00
     * @return
     */
    public static int convertHHmmToSecond(String hhmm) {
        try {
            return Integer.parseInt(hhmm.substring(0, 2)) * 3600 + Integer.parseInt(hhmm.substring(3, 5)) * 60;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    /**
     * ????????????????????????HH:mm?????????????????????
     *
     * @param second
     * @return
     */
    public static String convertSecondToHHmm(int second) {
        return formatDD.format(second / 3600) + ":" + formatDD.format((second % 3600) / 60);
    }

    /**
     * ???????????????????????????
     *
     * @param htmlStr
     * @return
     */
    public static String htmlToStr(String htmlStr) {
        String result = "";
        boolean flag = true;
        if (htmlStr == null) {
            return null;
        }

        htmlStr = htmlStr.replace("\"", ""); // ????????????

        char[] a = htmlStr.toCharArray();
        int length = a.length;
        for (int i = 0; i < length; i++) {
            if (a[i] == '<') {
                flag = false;
                continue;
            }
            if (a[i] == '>') {
                flag = true;
                continue;
            }
            if (flag == true) {
                result += a[i];
            }
        }
        return result.toString();
    }

    /**
     * ??????????????????,???????????????RCU????????????+1????????????RCU?????????model1???<BR>
     * ?????????32???,???0??????,???12,13??????1???????????????,???18-21???????????????<BR>
     * ???????????????"&#D#@!(*^&#$Xx"???????????????????????????<BR>
     * TRcuModemInfo = packed record<BR>
     * IDTSign: packed array[0..11] of Char; //Add by phelix 100731<BR>
     * Flag: Byte; //?????????Rcu??????, 0:??????; 1:??????<BR>
     * SaveType: Byte; //????????????????????????, 0:??????; 1:??????; 2:Both<BR>
     * DongID: Cardinal;<BR>
     * //DongTime: Int64; //remark<BR>
     * LogID: DWORD;<BR>
     * StartTime: Int64; //?????????????????????(??????1970-01-01)<BR>
     * end<BR>
     *
     * @param rcuNum
     * @return
     */
    public static byte[] buildRucFileNum(int rcuNum) {
        try {
            // &#D#@!(*^&#$Xx Pioneer???????????????????????????x??????????????????1???
            byte[] maskStr = "&#D#@!(*^&#$X".getBytes();

            // StringBuffer defaultSb = new StringBuffer();
            byte[] defaultSb = new byte[32];
            int i = 0;
            for (; i < 18; i++) {
                defaultSb[i] = 0;
            }

			/*
			 * byte[] docId =
			 * intToByteArray1(getTelecomDogId(android.os.Build.MODEL)); for(byte cc :
			 * docId){ defaultSb[i++] = cc; }
			 */

            byte[] numChar = intToByteArray1(rcuNum);
            for (byte cc : numChar) {
                defaultSb[i++] = cc;
            }

            byte[] numDate = longToByteArray(getLocalTimeByUTCTime() / 1000);
            for (byte cc : numDate) {
                defaultSb[i++] = cc;
            }

            for (; i < 32; i++) {
                defaultSb[i++] = 0;
            }

            for (i = 0; i < defaultSb.length - 2; i++) {
                defaultSb[i] ^= maskStr[i % maskStr.length];
                System.out.println("--num:" + i + ":" + Integer.toHexString(defaultSb[i]) + "--maske:" + (char) defaultSb[i]
                        + "--byte:" + defaultSb[i]);
            }

            System.out.println("--num:" + 30 + ":" + Integer.toHexString(defaultSb[30]) + "--maske:" + (char) defaultSb[30]
                    + "--byte:" + defaultSb[30]);
            System.out.println("--num:" + 31 + ":" + Integer.toHexString(defaultSb[31]) + "--maske:" + (char) defaultSb[31]
                    + "--byte:" + defaultSb[31]);

            return defaultSb;
        } catch (Exception e) {
            LogUtil.w(tag, "", e);
        }

        return null;
    }

    /**
     * ?????????????????? <BR>
     * ??? 41834.69348013888 ?????? long(1405327116684) ????????????
     *
     * @param timeStr
     * @return
     */
    public static long getTime(String timeStr) {
        double date = Double.parseDouble(timeStr);
        int wholeDays = (int) Math.floor(date);
        int millisecondsInDay = (int) ((date - wholeDays) * 60 * 60 * 24 * 1000L + 0.5);
        Calendar calendar = new GregorianCalendar(); // using default time-zone
        setCalendar(calendar, wholeDays, millisecondsInDay, false);
        return calendar.getTime().getTime();
    }

    private static void setCalendar(Calendar calendar, int wholeDays, int millisecondsInDay, boolean use1904windowing) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        } else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, millisecondsInDay);
    }

    /**
     * ????????????
     *
     * @param path
     * @return
     */
    public static String readFile(String path) {
        String str = "";
        File file = new File(path);
        try {

            FileInputStream in = new FileInputStream(file);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            str = new String(buffer, "utf-8");

        } catch (IOException e) {
            return null;
        }
        return str;
    }

    /**
     * ??????-9999
     *
     * @param value
     * @return
     */
    public static Object convertValue(Object value) {
        Object mValue = "";
        mValue = value;
        if (value instanceof Float) {
            if ((Float) value == -9999) {
                mValue = "";
            }
        } else if (value instanceof Long) {
            if ((Long) value == -9999) {
                mValue = "";
            }
        } else if (value instanceof Integer) {
            if ((Integer) value == -9999) {
                mValue = "";
            }
        }
        return mValue;
    }

    /***
     * ??????????????????????????????
     * @param cmd ????????????
     * @return ????????????
     */
    public static String execRootCmdx(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());// ??????Root?????????android????????????su??????
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            Log.w("execRootCmdx", cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                result += line + "\n";
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                    dos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                    dis = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @return
     */
    public static boolean isSystemApplication(Context context) {
        boolean isSystemApplication = false;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            boolean isFlagSystem = (packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
            boolean isUidSystem =  "android.uid.system".equals(packageInfo.sharedUserId);
            LogUtil.i(tag,"-----sharedUserId:" + packageInfo.sharedUserId + ",isFlagSystem:" + isFlagSystem +  "---");
            isSystemApplication = (isFlagSystem || isUidSystem);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.i(tag,"-----isSystemApplication:" + isSystemApplication + "------");
        return isSystemApplication;
    }

    /***
     * ????????????GPS????????????
     *
     * @param context
     * @return
     */
    public static boolean openGPSService(Context context)
    {
        //???????????????????????????????????????????????????
        //1.settings put secure location_providers_allowed -network
        //2.settings put secure location_providers_allowed -gps
        String[] cmds = {"cd /system/bin", "settings put secure location_providers_allowed +gps"};
        try {
            Process p = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            //??????1???????????????
            Thread.sleep(1000);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * ????????????GPS????????????
     *
     * @param context
     * @return
     */
    public static boolean closeGPSService(Context context)
    {
        //???????????????????????????????????????????????????
        //1.settings put secure location_providers_allowed -network
        //2.settings put secure location_providers_allowed -gps
        String[] cmds = {"cd /system/bin", "settings put secure location_providers_allowed -gps"};
        try {
            Process p = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            //??????1???????????????
            Thread.sleep(1000);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

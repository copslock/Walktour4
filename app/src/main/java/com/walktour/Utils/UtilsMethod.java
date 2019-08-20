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

    public static DecimalFormat decFormat = new DecimalFormat("#.##");// 显示数据小数位格式
    public static DecimalFormat decFormat4 = new DecimalFormat("#.####");// 显示数据小数位格式
    public static DecimalFormat decFormat6 = new DecimalFormat("#.######");// 显示数据小数位格式
    public static DecimalFormat decFarmat10 = new DecimalFormat("#.##########");
    public static DecimalFormat decFarmat30 = new DecimalFormat("#.##############################");
    public static DecimalFormat decFarmat8Zero = new DecimalFormat("0.00000000");
    /**
     * 日期格式转换： yyyy-MM-dd HH:mm:ss.SSS
     */
    public static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    /**
     * 日期格式转换：yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat sdFormatss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    /**
     * 日期格式转换： HH:mm:ss
     */
    public static SimpleDateFormat sdfhms = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()); // 事件前时间格式
    /**
     * 日期格式转换： HH:mm:ss
     */
    public static SimpleDateFormat sdfhms2 = new SimpleDateFormat("HHmmss", Locale.getDefault()); // 事件前时间格式
    /**
     * 日期格式转换： yyyyMMddHHmmss
     */
    public static SimpleDateFormat sdfyMdhms = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    /**
     * 日期格式转换： yyyyMMddHHmmssSSS
     */
    public static SimpleDateFormat sdfhmsss = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()); // 基本时间格式
    /**
     * 日期格式转换： HHmmss.sss
     */
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss.sss", Locale.getDefault());

    /**
     * 日期格式转换： ddMMyy
     */
    public static SimpleDateFormat ymdFormat = new SimpleDateFormat("ddMMyy", Locale.getDefault());
    /**
     * 日期格式转换： yyyy-MM-dd
     */
    public static SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private static DecimalFormat formatDD = new DecimalFormat("00");

    public static final long Hour = 1000 * 60 * 60;
    public static final long Minute = 1000 * 60;
    public static final float kbyteRage = 1000;

    /**
     * 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块
     */
    public static final String CharSet_US_ASCII = "US-ASCII";
    /**
     * ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1
     */
    public static final String CharSet_ISO_8859_1 = "ISO-8859-1";
    /**
     * 8 位 UCS 转换格式
     */
    public static final String CharSet_UTF_8 = "UTF-8";
    /**
     * 16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序
     */
    public static final String CharSet_UTF_16BE = "UTF-16BE";
    /**
     * 16 位 UCS 转换格式，Little-endian（最高地址存放低位字节）字节顺序
     */
    public static final String CharSet_UTF_16LE = "UTF-16LE";
    /**
     * 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识
     */
    public static final String CharSet_UTF_16 = "UTF-16";
    /**
     * 中文超大字符集
     */
    public static final String CharSet_GBK = "GBK";
    /**
     * 中文字符集 gb2312
     */
    public static final String CharSet_GB2312 = "GB2312";

    /**
     * 8位UCS格式
     */
    public static final String CharSet_UTS_2 = "ISO-10646-UCS-2";

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换编码的字符串
     * @param newCharset 目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String newCharset) {
        try {
            if (str != null) {
                // 用默认字符编码解码字符串。
                byte[] bs = str.getBytes();
                // 用新的字符编码生成字符串
                return new String(bs, newCharset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串编码转换的实现方法
     *
     * @param str        待转换编码的字符串
     * @param oldCharset 原编码
     * @param newCharset 目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str, String oldCharset, String newCharset) {
        try {
            if (str != null) {
                // 用旧的字符编码解码字符串。解码可能会出现异常。
                byte[] bs = str.getBytes(oldCharset);
                // 用新的字符编码生成字符串
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
     * 是否整数
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
     * 去掉-9999
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
     * String到int的转换
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
     * 求传入字符串的异或和
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
     * 将指定内容写入指定路径下的文件中
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
     * 按指定大小创建一个包含随机数的文件
     *
     * @param filePath
     * @param fileSize 文件大小(byte)
     */
    public static void makeFile(String filePath, long fileSize) {

        File old = new File(filePath);
        if (old.exists()) {
            old.delete();
        }

        FileWriter fw = null;
        try {
            // 第二个参数 true 表示写入方式是追加方式
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

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    /**
     * 读取指定文件内容，如果不存在返回空
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
     * 将指定内容写入指定路径下的文件中
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
     * 文件更命名
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
            File dest = new File(destPath); // 目标文件
            // 如果要更名的文件或目录已存在，先直接删除
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
     * 删除目录以及目录下的所有文件
     *
     * @param path 指定的文件路径
     */
    public static void deleteAll(String path) {
        File file = new File(path);
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                // 递归删除目录下的所有文件
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteAll(files[i].getAbsolutePath());
                }
                // 删除本目录
                file.delete();
            }
        }
    }

    /**
     * 删除目录下所有超过指定时间的文件（***不删除目录本身***）
     *
     * @param dirPath    目录的绝对路径
     * @param createTime 超过此时间的文件才会被删除
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
     * 是否无效文件名
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
     * 字符串较验
     */
    public static boolean MatcherString(String str) {
        String regEx = "0-9a-zA-Z._";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        boolean bl = m.find();
        return bl;
    }

    /**
     * 获得最大值最小值区间的随机数,公式:rand.nextInt(最大值-最小值+1)+最小值;
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
     * int 类型到byte数组转换
     */
    public static byte[] intToByteArray1(int int32) {
        byte[] result = new byte[4];
        // java到c，高低位反转
        result[3] = (byte) ((int32 >> 24) & 0xFF);
        result[2] = (byte) ((int32 >> 16) & 0xFF);
        result[1] = (byte) ((int32 >> 8) & 0xFF);
        result[0] = (byte) (int32 & 0xFF);
        return result;
    }

    /**
     * 浮点到字节转换
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
     * Long转字节，对应C语言中的int64
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
     * java的char数组到byte[]转换 注意Java里的char长度决定于编码类型，最少也有2字节， 而AscII里的char是1字节
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
     * java的char到byte转换 注意Java里的char长度决定于编码类型，最少也有2字节， 而AscII里的char是1字节
     */
    public static byte charToByte(char c) {
        int i = c;
        byte b = (byte) (i & 0xFF);
        return b;
    }

    /**
     * short类型到byte[]转换
     */
    public static byte[] shortToBytes(short short16) {
        byte[] bytesRet = new byte[2];
        // java到c，高低位反转
        bytesRet[1] = (byte) ((short16 >> 8) & 0xFF);
        bytesRet[0] = (byte) (short16 & 0xFF);
        return bytesRet;
    }

    /**
     * 通过byte数组取到int
     *
     * @param bb
     * @param index 第几位开始
     * @return
     */
    public static int getInt(byte[] bb, int index) {
        return ((bb[index + 3] & 0xff) << 24) | ((bb[index + 2] & 0xff) << 16) | ((bb[index + 1] & 0xff) << 8)
                | ((bb[index + 0] & 0xff) << 0);
    }

    /**
     * 写入ftp格式的数据结构,除ftp业务外的几个旧业务也是ftp结构，见<RCU文件结构>说明
     *
     * @param context
     * @param msgFlag         表示业务的flag,WalkCommonPara.MsgDataFlag_*
     * @param eventFlag       download标识为 0x00 upload标识为0x01
     * @param useMsTime       已经传输的时间(毫秒)
     * @param totalTransBytes 已经传输的字节数
     * @param interlMsTime    和上次写入此事件时的时间隔的毫秒
     * @param interBytes      和上次写入此事件时的传输字节数差
     */
    public static void sendWriteRcuFtpData(Context context, char msgFlag, int eventFlag, int useMsTime,
                                           long totalTransBytes, int interlMsTime, int interBytes) {
        EventBytes.Builder(context).addInteger(eventFlag).addInteger(useMsTime).addInt64(totalTransBytes)
                .addInteger(interlMsTime).addInteger(interBytes).writeToRcu(msgFlag);
    }

    /**
     * 执行命令行操作，可拥有ROOT权限 以非阻塞的方式执行
     *
     * @param command
     * @return
     */
    public static boolean runRootCommand(String command) {
        return runRootCommand(command, Deviceinfo.getInstance().isCmdChoke());
    }

    /**
     * 执行命令行操作，可拥有ROOT权限
     *
     * @param command 执行ROOT命令串
     * @param isChoke 是否以阻塞方式进行
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
     * 执行命令行操作，可拥有ROOT权限,不打印log
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
     * 非Root方式执行命令<BR>
     * [功能详细描述]
     *
     * @param command 命令
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
     * 非Root方式执行命令<BR>
     * [功能详细描述]
     *
     * @param command 命令
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
     * 执行Shell命令
     *
     * @param command
     * @return 成功true, 失败false
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
            // pb.directory(new File("/"));//设置shell的当前目录。
            proc = pb.start();
            // 获取输入流，可以通过它获取SHELL的输出。
            in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            // 获取输出流，可以通过它向SHELL发送命令。
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            out.println("su \n");
            out.println(command + "\n");
            out.println("exit");
            // proc.waitFor();
            String line;
            while ((line = in.readLine()) != null) {
                LogUtil.d("command", line); // 打印输出结果
            }
            while ((line = err.readLine()) != null) {
                LogUtil.d("command", line); // 打印错误输出结果
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
     * 返回指定Linux命令行执行结果
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
     * 根据进程的名称终止进程 终止进程使用系统接口{@link android.os.Process#killProcess}
     * 所以只有符合条件的本进程开启的子进程会被杀死
     *
     * @param pname               进程名
     * @param killOtherAppProcess 是否把其它用户进程也杀死
     */
    public static void killProcessByPname(String pname, boolean killOtherAppProcess) {
        try {
            List<ProcessModel> pidList = getPidByPname(getProcess(), pname);
            if (pidList != null) {
                if (killOtherAppProcess) {
                    //先删除不是logcat的进程
                    for (ProcessModel process : pidList) {
                        //注意:[user=root, pid=8844, ppid=1, name=logcat]
                        //判断业务逻辑:如果是root进程,并且ppid==1,并且name==logcat,如果存在多个这样的进程,则保留pid最大的进程其他进程全部kill
                        if (!"logcat".equals(pname)) {
                            UtilsMethod.runRootCommand("  kill -9 " + process.getPid());
                        }
                    }
                    //kill logcat进程
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
                    // 此命令只会杀死符合本用户号的进程
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
     * 得到所有的进程信息
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
     * 根据进程名称获取进程id 查询标准为：{@link String#contains}
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
     * 获取PID所在的位置
     *
     * @param headStr 头信息 USER PID PPID VSIZE RSS WCHAN PC NAME
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
     * 给Trace口设备赋ROOT权限，收到开机消息时调用
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
     * 通过国际移动用户识别码IMSI号获MCC
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
     * 通过国际移动用户识别码IMSI号获MNC
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
     * 重启手机
     */
    public static void rebootMachine() {
        runRootCommand("reboot");
    }

    /**
     * 获取中国标准时间格式(yyyy-MM-dd,HH:mm:ss)
     */
    public static String getSimpleDateFormat0(long time) {
        return new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式(HH:mm:ss)
     */
    public static String getSimpleDateFormat1(long time) {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式(HH:mm)
     */
    public static String getSimpleDateFormat2(long time) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式(""yyyy-MM-dd,HH:mm:ss"")
     */
    public static String getSimpleDateFormat3(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式(""yyyy-MM-dd HHmmss"")
     */
    public static String getSimpleDateFormat4(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式(""yyyy-MM-dd HHmmss"")
     */
    public static String getSimpleDateFormat5(long time) {
        return new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式("yyyy-MM-dd")
     */
    public static String getSimpleDateFormat6(long time) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time);
    }

    /**
     * 获取中国标准时间格式(""yyyyMMdd-HHmmss"")
     */
    public static String getSimpleDateFormat7(long time) {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(time);
    }

    /**
     * @param value
     * @return 保留6位小数的double
     */
    public static String getdecFormat6(double value) {
        return decFormat6.format(value);
    }

    /**
     * 设置当前时间
     *
     * @param millonSecond
     */
    public static void setTime(long millonSecond, Context context) {
        String time = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.getDefault()).format(millonSecond);
        if (!ApplicationModel.getInstance().isGeneralMode()) { // &&
            // WalktourApplication.isRootSystem()
            UtilsMethod.runRootCommand("date -s " + time);
            // 非Root状态下使用Partener进行修改
        } else {
            Intent intent = new Intent(WalktourConst.WALKTOUR_SET_SYSTEMCLOCK);
            intent.putExtra(WalktourConst.WALKTOUR_SYSTEM_TIME, millonSecond);
            context.sendBroadcast(intent);
        }
    }

    /**
     * MD5加密
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

            // System.out.println("result: " + buf.toString());// 32位的加密
            // System.out.println("result: " + buf.toString().substring(8, 24));//
            // 16位的加密
            encode = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encode;
    }

    /**
     * IP地址转为整数型 convert ipaddr like 1.1.1.1 to int
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
     * 根据传进来的域名或者IP地址，返回对应的IP地址，即传进来的为IP地址时，无变化
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
     * 获得等号后面的值 testername=twq 返回twq
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
     * 计算合适的缩放比例
     *
     * @param options        图片参数
     * @param maxNumOfPixels 设定能加载图片的最大像素
     * @return 如果原图小于设定最大值，返回1，如果大于最大值，就取合适的比例值
     */
    public static int computeSuitedSampleSize(BitmapFactory.Options options, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        double v = w * h / maxNumOfPixels;
        LogUtil.d(tag, "---w=" + w + "---h=" + h + "---v=" + v);
        int samplesize = (int) Math.ceil(Math.sqrt(v));
        if (samplesize > 2) {
            //因为图片的inSampleSize参数只支持2的n次方，所以做特殊处理
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
     * 删除默认路由命令 入参：route 删除的路由名称，deviceName 手机名称（如不同手机命令不同需根据它做相应处理）
     *
     * @param route
     * @author tangwq
     */
    public static void delDefaultRoute(String route) { // ,String deviceName
        // 如需要另加命令处理
        runRootCommand("ip route del default dev " + route + "&");
    }

    /**
     * 经纬度转换弧度
     *
     * @param d
     * @return
     */
    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private static final double EARTH_RADIUS = 6378.137;// 地球半径

    /**
     * 求GPS两点间的距离 （单位KM）
     *
     * @param lat1 第一个点纬度
     * @param lng1 第一个点经度
     * @param lat2 第二个点纬度
     * @param lng2 第二个点经度
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
        // -----第一步----------
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

        // -----第二步----------
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
     * 通过共享平台获取当前网络时间
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
                // 生成连接对象
                uc = (HttpURLConnection) url.openConnection();
                long startTime = System.currentTimeMillis();
                uc.setConnectTimeout(500);
                uc.setReadTimeout(2000);
                uc.setUseCaches(false);
                // 发出连接
                uc.connect();
                int resCode = uc.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    long endTime = System.currentTimeMillis();
                    int contentLength = uc.getContentLength();
                    in = uc.getInputStream();
                    bis = new BufferedInputStream(in);
                    // 数据字节数组
                    byte[] receData = new byte[contentLength];
                    // 数据数组偏移量
                    int readLength = bis.read(receData, 0, contentLength);
                    // 已读取的长度
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
     * 通过http://www.bjtime.cn 获得当前网络时间
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
                    // 生成连接对象
                    uc = (HttpURLConnection)url.openConnection();
                    uc.setConnectTimeout(500);
                    uc.setReadTimeout(2000);
                    uc.setUseCaches(false);
                    // 发出连接
                    uc.connect();
                    // 因为获取的时间精确到秒，所以添加500毫秒有助于较少同步时的误差
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
     * 通过网络时间协议NTP(Network Time Protocol) 获得指定服务器网络时间
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
     * 将bitmap对象保存存图片文件
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
     * 把raw文件写入到指定目录 中
     *
     * @param context 上下文
     * @param rawId   资源文件ID
     * @param desFile 目标文件
     * @return 是否成功写入
     */
    public static boolean writeRawResource(Context context, int rawId, File desFile) {
        return writeRawResource(context, rawId, desFile, false);
    }

    /**
     * 把raw文件写入到指定目录 中(优化，如果已经存在并且文件的大小不变的话，则不改变)
     *
     * @param context     上下文
     * @param rawId       资源文件ID
     * @param desFile     目标文件
     * @param isOverwrite 是否覆盖已存在的文件
     * @return 是否成功写入
     */
    public static boolean writeRawResource(Context context, int rawId, File desFile, boolean isOverwrite) {
        LogUtil.d(tag, "---writeRawResource---:" + desFile.getAbsolutePath());
        try {
            if (desFile == null || (!isOverwrite && desFile.exists()))
                return false;
            long lastLenth=SharePreferencesUtil.getInstance(context).getLong(desFile.getAbsolutePath(),0l);//文件的最后修改时间

            if (isOverwrite&&lastLenth==desFile.length()&&lastLenth>0){
                LogUtil.d(tag,desFile.getAbsolutePath()+" file is exist.dont update."+"lastLenth="+lastLenth+"，desFile.lastModified()="+desFile.length());
                return false;
            }else{
                LogUtil.d(tag,desFile.getAbsolutePath()+" file is exist,need update."+"lastLenth="+lastLenth+"，desFile.lastModified()="+desFile.length());
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
     * 把raw文件写入到指定目录 中
     *
     * @param context     上下文
     * @param rawId       资源文件ID
     * @param desFile     目标文件
     * @param isOverwrite 是否覆盖已存在的文件
     * @return 是否成功写入
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
     * 把raw文件写入到指定目录中，对于没有获取到lib目录写权限的采用当前方法
     *
     * @param context     上下文
     * @param rawId       资源文件ID
     * @param desFile     目标文件
     * @param isOverwrite 是否覆盖已存在的文件
     * @return 是否成功写入
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
     * 把raw文件写入到指定目录中，对于没有获取到lib目录写权限的采用当前方法
     *
     * @param context     上下文
     * @param rawId       资源文件ID
     * @param desFile     目标文件
     * @param isOverwrite 是否覆盖已存在的文件
     * @return 是否成功写入
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
     * 获取SDK版本号信息<BR>
     * [功能详细描述]
     *
     * @return SDK版本号
     */
    public static int getSDKVersionNumber() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 如果传进来的为7位的16进制CELLID长码，转换为后4位的短码ID 转换算法：长码转为二进制，后16位转为十进制
     * 例子27069589-->二进制是101001...10101--->后面16位改为十进制，即为3221
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
     * [从长整形的cellid中获得RNCID]<BR>
     * 转换算法：长码转为二进制，28bit是7位16进制，前3位是RNC ID,后4位是Cell ID RCN 直接取前十二位转成十进制即可
     *
     * @param longCellid
     * @return
     */
    public static int getLongCellIdToRNCId(int longCellid) {
        String binaryStr = Integer.toBinaryString(longCellid);
        // 如果转换过来的二进制长度小于28，前面补0
        int subLeng = 28 - binaryStr.length();
        for (int i = 0; i < subLeng; i++) {
            binaryStr = "0" + binaryStr;
        }
        String head12 = binaryStr.substring(0, 12);
        // 将前12位二进制转为整形
        return Integer.valueOf(head12, 2);
    }

    /**
     * [从长整形的cellid中获得RNCID]<BR>
     * 转换算法：长码转为二进制，28bit是7位16进制，前3位是RNC ID,后4位是Cell ID RCN 直接取前十二位转成十进制即可
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
     * 如果传进来的为7位的16进制CELLID长码，转换为后4位的短码ID 转换算法：长码转为二进制，后16位转为十进制
     * 例子27069589-->二进制是101001...10101--->后面16位改为十进制，即为3221
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
     * 获得字符串中指定字符的个数
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
     * [获得当前应用的版本号]<BR>
     * [功能详细描述]
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
     * [将bps值转换成kbps]<BR>
     * [功能详细描述]
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
     * 将传进来的值做放大相应倍数处理
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
     * 将传进来的值做缩放比例
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
     * 为传进来的值添加单位
     */
    public static String addUnit(String value, String unit) {
        if (value == null || value.equals("")) {
            return "";
        }
        return value + " " + unit;
    }

    /**
     * 处理小数点位数
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
     * 判断是否数字正则
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
     * 小数格式处理
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
     * 显示通知
     *
     * @param tickerText
     *          通知显示的内容
     * @param strBroadcast
     *          点通知后要发的广播
     */
    @SuppressWarnings("deprecation")
    public static void showNotification(Context context, String tickerText, String strBroadcast) {
        // 生成通知管理器
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
        Notification.Builder notification = new Notification.Builder(context);
        notification.setTicker(tickerText);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0,
                new Intent((strBroadcast == null) ? "" : strBroadcast), 0);
        // must set this for content view, or will throw a exception
        // 如果想要更新一个通知，只需要在设置好notification之后，再次调用
        // setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
        notification.setAutoCancel(true);
        notification.setContentIntent(contentIntent);
        notification.setContentTitle(context.getString(R.string.sys_alarm));
        notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

    /**
     * 显示通知并跳到指定的地方
     */
    @SuppressWarnings("deprecation")
    public static void showNotification(Context context, String tickerText, Class<?> cls) {
        // 生成通知管理器
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // notification //通知图标, 状态栏显示的通知文本提示,通知产生的时间
        Notification.Builder notification = new Notification.Builder(context);
        notification.setTicker(tickerText);
        notification.setSmallIcon(R.mipmap.walktour);
        notification.setWhen(System.currentTimeMillis());
        // Intent 点击该通知后要跳转的Activity
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, (cls != null ? cls : TestService.class)), 0);
        // must set this for content view, or will throw a exception
        // 如果想要更新一个通知，只需要在设置好notification之后，再次调用
        // setLatestEventInfo(),然后重新发送一次通知即可，即再次调用notify()。
        notification.setAutoCancel(true);
        notification.setContentIntent(contentIntent);
        notification.setContentTitle(context.getString(R.string.sys_alarm));
        notification.setContentText(tickerText);
        mNotificationManager.notify(R.string.service_started, notification.build());
    }

    /**
     * 将传进来的数字字符串转换为十六进制显示
     */
    public static String numToShowHexStr(String str) {
        // 2147483647 为7FFFFFFF无效值
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
     * 将传进来的浮点数串取两位小数返回
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
     * 根据给定比例转化，结果保留为小数点后两位的字符串形式
     *
     * @param value 输入值的字符串形式
     * @param scale 比例
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
     * 将指定的字符串转成float
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
     * 将指定的字符串转成float,如果指定字符串无效，则返回指定的无效值
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
     * 以16进显示字节
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
     * 获取手机CPU使用率 zhihui.lian
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
     * 获取SDcard卡剩余内存，单位是字节(Byte)
     */
    @SuppressWarnings("deprecation")
    public static Long getAvaiableSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            // 取得sdcard文件路径
            StatFs sf = new StatFs(sdcardDir.getPath());
            // 获取block的SIZE
            long bSize = sf.getBlockSize();
            // 可使用的Block的数量
            long availaBlock = sf.getAvailableBlocks();
            return bSize * availaBlock;
        }
        return (long) -1;
    }

    /**
     * Java文件操作 获取文件扩展名
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
     * 通过时间获取毫秒数（String转微秒）
     *
     * @return
     */
    public static long getSeconds(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long millionSeconds = 0;
        try {
            millionSeconds = sdf.parse(time).getTime(); // 毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds * 1000;
    }

    /**
     * 根据strings资源文件中的fieldname获得对应资源内容
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
     * 时间由String转为显示格式 例：2013-10-24 07:18:43 显示 为 2013/10/24
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
     * UTC时间转本地时间
     *
     * @param utcTime
     * @param utcTimePatten
     * @param localTimePatten
     * @return 本地时间
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
     * 主要功能当传入的文件路径不存在时,自动迭代创建文件目录
     *
     * @param filePath 传入文件路径
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
     * 时间由String转为显示格式 例：20131024071843 显示 为 2013/10/24
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
     * 根据传进来的格式字符串，及参数值串，转化为当前列表最终显示结果
     *
     * @param fmtStr
     * @param paramValues
     * @return
     */
    public static String formatParamToShow(String fmtStr, String paramValues) {
        String result = "";
        if (paramValues != null && fmtStr != null) {
            Object[] values = paramValues.split("@@");
            // 格式化从JNI查询输出的结果
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
     * 将传进来的数组转换成值字符串
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
     * 查看某一文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean existFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 从1970年开始算时间,Wone处理
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
     * 线程停暂停
     */
    public static void ThreadSleep(int times) {
        try {
            Thread.sleep(times);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改系统设置里的“自动更新日期和时间”开关
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void setTimeAuto(Context context, boolean auto) {
		/*
		 * //当前ROM权限不够,需要将当前方法放到插件中去实现 Intent closeAutoTimes = new
		 * Intent(WalktourConst.AUTOMATIC_DATE_TIME_SETTING);
		 * closeAutoTimes.putExtra(WalktourConst.AUTOMATICTYPE, 0);
		 * context.sendBroadcast(closeAutoTimes);
		 */

        Settings.System.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME, auto ? 1 : 0);
    }

    /**
     * 当前自动同步时间开关是否打开
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
     * 生成室内打$MARK点的字符串
     *
     * @param markState MARK状态值,0表示当前为删除点,1表示当前为添加点,如格式有变化再根据此值做相关处理
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

        // 增加校验和,具体原因未找到相关文档，参照Walktour Mobile的代码
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
     * 求百分比
     *
     * @param molecule    分子
     * @param denominator 分母
     * @return
     */
    public static String getIntMultiple(float molecule, float denominator) {
        return TotalDataInterface.getIntMultiple(molecule, denominator, 100, "%");
    }

    /**
     * 获取utc时间
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
     * 将UTC时间转换为东八区时间
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
     * 根据传入的UTC时间整型数加上当前时区差值为本地时间
     *
     * @return
     */
    public static long getLocalTimeByUTCTime() {
        return getLocalTimeByUTCTime(System.currentTimeMillis());
    }

    /**
     * 根据传入的UTC时间整型数加上当前时区差值为本地时间
     *
     * @param utcTime
     * @return
     */
    public static long getLocalTimeByUTCTime(long utcTime) {
        // 1、取得本地时间：
        java.util.Calendar cal = java.util.Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        return utcTime + zoneOffset + dstOffset;
    }

    /**
     * 判断字符串是否为数字
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
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 获得全局唯一ID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 反回字符串中指定的开始，结束符之间的内容
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
     * 反回字符串中指定的开始，结束符之间的内容
     *
     * @param str        传入指定的字符串
     * @param startChart 指定开始特征符
     * @param EndChart   指定结束特征符
     * @param defaultStr 如果未截取到内容，反正默认结果
     * @return
     */
    public static String getSubStringByChart(String str, String startChart, String EndChart, String defaultStr) {
        if (str.lastIndexOf(startChart) > 0 && str.lastIndexOf(EndChart) > str.lastIndexOf(startChart)) {
            return str.substring(str.lastIndexOf(startChart) + 1, str.lastIndexOf(EndChart));
        }

        return defaultStr;
    }

    /**
     * 设置手机通话过程中的扬声器状态
     *
     * @param contex
     * @param on
     */
    public static void setSpeakerphoneOn(Context contex, boolean on) {
        AudioManager audioManager = (AudioManager) contex.getSystemService(Context.AUDIO_SERVICE);
        if (on) {
            audioManager.setSpeakerphoneOn(true);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			/*
			 * audioManager.setRouting(AudioManager.MODE_NORMAL,
			 * AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL);
			 * setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
			 */
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
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
     * 传入的HHmm日期格式字符串,转换为时间秒
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
     * 将时间秒数转换为HH:mm的时间表示格式
     *
     * @param second
     * @return
     */
    public static String convertSecondToHHmm(int second) {
        return formatDD.format(second / 3600) + ":" + formatDD.format((second % 3600) / 60);
    }

    /**
     * 字符串中去掉双引号
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

        htmlStr = htmlStr.replace("\"", ""); // 去掉引号

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
     * 电信招标需要,每生成一个RCU文件序号+1并写入到RCU文件的model1中<BR>
     * 格式为32位,第0开始,第12,13位为1不知道为啥,第18-21四位为序号<BR>
     * 根据指定串"&#D#@!(*^&#$Xx"按顺序循环异或返回<BR>
     * TRcuModemInfo = packed record<BR>
     * IDTSign: packed array[0..11] of Char; //Add by phelix 100731<BR>
     * Flag: Byte; //保存的Rcu数据, 0:精简; 1:原始<BR>
     * SaveType: Byte; //配置文件参数类型, 0:精简; 1:原始; 2:Both<BR>
     * DongID: Cardinal;<BR>
     * //DongTime: Int64; //remark<BR>
     * LogID: DWORD;<BR>
     * StartTime: Int64; //电脑时间的秒数(相对1970-01-01)<BR>
     * end<BR>
     *
     * @param rcuNum
     * @return
     */
    public static byte[] buildRucFileNum(int rcuNum) {
        try {
            // &#D#@!(*^&#$Xx Pioneer中的异或算法少了个x此处异或时少1个
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
     * 特殊时间处理 <BR>
     * 将 41834.69348013888 转为 long(1405327116684) 时间类型
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
     * 读取文件
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
     * 处理-9999
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
     * 执行命令并且输出结果
     * @param cmd 执行命令
     * @return 返回结果
     */
    public static String execRootCmdx(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec(Deviceinfo.getInstance().getSuOrShCommand());// 经过Root处理的android系统即有su命令
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
     * 当前应用是否被打包成系统级别应用
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
     * 自动开启GPS定位服务
     *
     * @param context
     * @return
     */
    public static boolean openGPSService(Context context)
    {
        //打开定位服务可以使用如下两条命令：
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
            //延时1秒获取状态
            Thread.sleep(1000);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 自动开启GPS定位服务
     *
     * @param context
     * @return
     */
    public static boolean closeGPSService(Context context)
    {
        //关闭定位服务可以使用如下两条命令：
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
            //延时1秒获取状态
            Thread.sleep(1000);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

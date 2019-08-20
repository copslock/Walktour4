/*
 * 文件名: GlobalExceptionHandler.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-8-22
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.replayfloatview.FloatWindowManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler{
    
    private static final String TAG = "GlobalExceptionHandler";

    public static final String EMAIL_FROM_ONE = "walktourtest1@126.com";
    public static final String EMAIL_FROM_TWO = "walktourtest2@126.com";
    public static final String EMAIL_FROM_THREE = "walktourtest3@126.com";
    public static final String EMAIL_FROM_FOUR = "walktourtest4@126.com";
    public static final String EMAIL_FROM_FIVE = "walktourtest5@126.com";
    public static final String EMAIL_FROM_SIX = "walktourtest6@126.com";
    public static final String EMAIL_FROM_SEVEN = "walktourtest7@126.com";
    public static final String EMAIL_FROM_EIGHT = "walktourtest8@126.com";
    public static final String EMAIL_FROM_NINE = "walktourtest9@126.com";
    public static final String EMAIL_FROM_TEN = "walktourtest10@126.com";
    
    private static final String STOPAPP = "adb shell am force-stop com.walktour.gui";
    
    public static final String SMTP_EMAIL_ADDRESS = "smtp.126.com";
    
    public static final String EMAIL_PASSWORD = "dinglicom";
    
    private Thread.UncaughtExceptionHandler defaultHandler;
    
    private static GlobalExceptionHandler instance;
    
    private boolean caughtException = false;
    
    private Context mContext;

    /**
     * 2018.07.02 记录崩溃次数 移动招标项
     */
    public static final String SP_KEY_EXCEPTION_TIMES = "key_exception_times";
    
    private static String[] EMAIL_TO_LIST = new String[]{"wuqing.tang@dinglicom.com","zhihui.lian@dinglicom.com"};
    private String[] emailAddrs = {GlobalExceptionHandler.EMAIL_FROM_ONE, GlobalExceptionHandler.EMAIL_FROM_TWO, GlobalExceptionHandler.EMAIL_FROM_THREE
            , GlobalExceptionHandler.EMAIL_FROM_FOUR, GlobalExceptionHandler.EMAIL_FROM_FIVE
            , GlobalExceptionHandler.EMAIL_FROM_SIX, GlobalExceptionHandler.EMAIL_FROM_SEVEN
            , GlobalExceptionHandler.EMAIL_FROM_EIGHT,  GlobalExceptionHandler.EMAIL_FROM_NINE,  GlobalExceptionHandler.EMAIL_FROM_TEN};
    /**
     * 客户端信息
     */
    private String clientInfo;

    private GlobalExceptionHandler(Context context) {
        this.mContext = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //收集客户端信息
        this.clientInfo = collectClientInfo();
        
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public static GlobalExceptionHandler get(Context mContext) {
        if (instance == null) {
            synchronized (GlobalExceptionHandler.class) {
                if (instance == null)
                    instance = new GlobalExceptionHandler(mContext);
            }
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e(TAG, "Caught WalkTour Global Exception");
        throwable.printStackTrace();
        if (caughtException) {
            defaultHandler.uncaughtException(thread, throwable);
            return;
        }
        caughtException = true;
        //boolean handled = false;

        //清空通知
        clearAllNotification();

        //2018.07.02 该功能为移动招标功能项
        addExceptionTimes();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        throwable.printStackTrace(ps);
        final String errorMsg = new String(baos.toByteArray());
        final String mailContent = errorMsg + WalktourConst.LINE_SEPARATOR + WalktourConst.LINE_SEPARATOR + clientInfo;

        //跑线程, 将报错信息发送到指定的邮箱
        new Thread() {
            @Override
            public void run() {
                String imei = null;
                String imsi = null;
                try {
                    ////////////////////增加保存错误崩溃日志到SD卡start///////////////////////////////
                    String appinfo = "WalkTour Crash Report"
                        + " ("
                        + "AppVersion: " + mContext.getPackageManager()
                        .getPackageInfo(mContext.getPackageName(), Context.MODE_PRIVATE).versionName
                        + ")";
                    saveException(appinfo, mailContent);
                    //////////////////////////保存崩溃日志end////////////////////////////////////////////
                        imei = MyPhoneState.getInstance().getIMEI(mContext);
                        imsi = MyPhoneState.getInstance().getIMSI(mContext);
                        String subject = "WalkTour Crash Report"
                                + " ("
                                + "AppVersion: " + mContext.getPackageManager()
                                .getPackageInfo(mContext.getPackageName(), Context.MODE_PRIVATE).versionName
                                + ", IMEI: " + (!StringUtil.isNullOrEmpty(imei) ? imei : "")
                                + ", IMSI: " + (!StringUtil.isNullOrEmpty(imei) ? imsi : "")
                                + ")";
                        try {
                        	if(FloatWindowManager.isRemoveView()){
                        		System.exit(0);
                        	}
                            sendMail(emailAddrs[(int) (Math.floor(Math.random() * emailAddrs.length))]
                                    , EMAIL_TO_LIST, subject, mailContent);
                            LogUtil.i("----------", "------start-----");
                            UtilsMethod.runCommand(STOPAPP);
                            LogUtil.i("----------", "------end-----");
                            LogUtil.e("Exception", mailContent);
                            if (WalktourApplication.isDebug)
                                Log.d(TAG, "Send email successful");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        }.start();
        
    }

    /**
     *  移动招标功能项 记录测试过程异常崩溃次数
     */
    public static void addExceptionTimes() {
        if(ApplicationModel.getInstance().isTestJobIsRun()){
            int originTimes = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).getInteger(SP_KEY_EXCEPTION_TIMES , 0);
            LogUtil.i(TAG,"-------start addExceptionTimes originTimes:" + originTimes + ",newTimes:" + (originTimes + 1) + "--------");
            boolean result = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).saveIntegerWithResult(SP_KEY_EXCEPTION_TIMES , originTimes + 1);
            LogUtil.i(TAG,"-------end addExceptionTimes success:" + result + "--------");
        }else{
            LogUtil.i(TAG,"-------addExceptionTimes TestJob is not running--------");
        }

    }

    /**
     * 移动招标功能项 获取记录的崩溃次数
     * @return 记录的崩溃次数
     */
    public static int getExceptionTimes(){
        int exceptionTimesRecord = SharePreferencesUtil.getInstance(WalktourApplication.getAppContext()).getInteger(SP_KEY_EXCEPTION_TIMES , 0);
        LogUtil.i(TAG,"------exceptionTimesRecord:" + exceptionTimesRecord + "---------");
        return exceptionTimesRecord;
    }


    /**
     * 保存异常日志信息到SD卡<BR>
     * [功能详细描述]
     * @param subject 主题
     * @param content 内容
     */
    private void saveException(String subject,String content){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File fileDir = new File(LogUtil.SAVE_LOG_DIR_PATH);
            // 判断目录是否已经存在
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    return;
                }
            }
            File file = new File(LogUtil.SAVE_EXCEPTION_PATH);
            // 判断日志文件是否已经存在
            if (!file.exists()) {
                try {
                    file.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                // 输出
                FileOutputStream fos = new FileOutputStream(file, true);
                PrintWriter out = new PrintWriter(fos);
                out.println("/////////////////////////////////////////////////////////////////////////////");
                Date currentTime = new Date(System.currentTimeMillis());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
                out.println(formatter.format(currentTime));
                out.println(subject);
                out.println(content);
                out.println("/////////////////////////////////////////////////////////////////////////////");
                out.flush();
                out.close();
            }
            catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 退出应用进程<BR>
     * [功能详细描述]
     */
    protected void exit() {
        Log.d(TAG, "Exit On Global Exception");
        String packageName = mContext.getPackageName();
        String processId = "";
        try {
            Runtime r = Runtime.getRuntime();
            java.lang.Process p = r.exec("ps");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                if (inline.endsWith(packageName)) {
                    break;
                }
            }
            br.close();
            StringTokenizer processInfoTokenizer = new StringTokenizer(inline);
            int count = 0;
            while (processInfoTokenizer.hasMoreTokens()) {
                count++;
                processId = processInfoTokenizer.nextToken();
                if (count == 2) {
                    break;
                }
            }
            r.exec("kill -15 " + processId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 组装手机客户端信息<BR>
     * [功能详细描述]
     * @return
     */
    @SuppressWarnings("deprecation")
    private String collectClientInfo() {
        StringBuilder systemInfo = new StringBuilder();
        systemInfo.append("CLIENT-INFO");
        systemInfo.append(WalktourConst.LINE_SEPARATOR);

        systemInfo.append("AppVersion: ");
        systemInfo.append(UtilsMethod.getCurrentVersionName(mContext));
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Model: ");
        systemInfo.append(Build.MODEL);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Version.Release: ");
        systemInfo.append(Build.VERSION.RELEASE);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Id: ");
        systemInfo.append(Build.ID);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Display: ");
        systemInfo.append(Build.DISPLAY);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Product: ");
        systemInfo.append(Build.PRODUCT);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Device: ");
        systemInfo.append(Build.DEVICE);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Board: ");
        systemInfo.append(Build.BOARD);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("CpuAbility: ");
        systemInfo.append(Build.CPU_ABI);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Manufacturer: ");
        systemInfo.append(Build.MANUFACTURER);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Brand: ");
        systemInfo.append(Build.BRAND);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Type: ");
        systemInfo.append(Build.TYPE);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Tags: ");
        systemInfo.append(Build.TAGS);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("FingerPrint: ");
        systemInfo.append(Build.FINGERPRINT);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);

        systemInfo.append("Version.Incremental: ");
        systemInfo.append(Build.VERSION.INCREMENTAL);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("SDK: ");
        systemInfo.append(Build.VERSION.SDK_INT);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        systemInfo.append("Version.CodeName: ");
        systemInfo.append(Build.VERSION.CODENAME);
        systemInfo.append(WalktourConst.LINE_SEPARATOR);
        String clientInfo = systemInfo.toString();
        systemInfo.delete(0, systemInfo.length());
        return clientInfo;
    }

    /**
     * 清空所有通知消息<BR>
     * [功能详细描述]
     */
    private void clearAllNotification(){
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    /**
     * 发送邮件方法<BR>
     * 发送邮件给所有开发者
     * @param from
     * @param toList 
     * @param subject
     * @param content
     * @throws Exception
     */
    public static void sendMail(final String from, final String[] toList
            , final String subject, final String content) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_EMAIL_ADDRESS);
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, EMAIL_PASSWORD);
            }
        });
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        InternetAddress[] address = new InternetAddress[toList.length];
        for (int i = 0; i < toList.length; i++)
            address[i] = new InternetAddress(toList[i]);
        msg.setRecipients(Message.RecipientType.TO, address);
        Date current = new Date();
        msg.setSubject(StringUtil.getString(subject));
        Multipart mp = new MimeMultipart();
        MimeBodyPart mbpContent = new MimeBodyPart();
        mbpContent.setText(content);
        if(WalktourApplication.isReportAttachments){
            File logFile = new File(WalktourApplication.CURRENT_LOG_PATH);
            if(logFile.exists()){//有附件  
                String logzipPath = logFile.getParent() + "/"+ logFile.getName()+ ".zip";
                LogUtil.i(TAG, "logzipPath:" + logzipPath);
                ZipUtil.zip(logFile,new File(logzipPath));
                if(new File(logzipPath).exists()){
                    MimeBodyPart mbp = new MimeBodyPart();
                    FileDataSource fds=new FileDataSource(logzipPath); //得到数据源  
                    mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
                    mbp.setFileName(MimeUtility.encodeWord(fds.getName(), "GB2312",null));
                    mbp.setFileName(fds.getName());  //得到文件名同样至入BodyPart  
                    mp.addBodyPart(mbp);  
                }
            }  
        }
        mp.addBodyPart(mbpContent);
        msg.setContent(mp);
        msg.setSentDate(current);
        try {
        	Transport.send(msg, address);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
        
    }
    
}

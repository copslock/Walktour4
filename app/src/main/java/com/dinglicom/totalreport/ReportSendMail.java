package com.dinglicom.totalreport;

import android.content.Context;
import android.os.Handler;

import com.walktour.Utils.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
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

/**
 * 报表分享，以邮件方式发送
 * @author zhihui.lian
 *
 */
public class ReportSendMail {
	/** 日志标识 */
	private static final String TAG = "SendMailReport";
	/** 报告文件名 */
	public static final String EXTRA_REPOET_FILES = "key_repoet_files";
	/** 服务器管理类 */
	private ServerManager mServerManager;

	/**
	 * 1、 发送邮件主题：终端号+测试数据上传成功通知 例：357242047911950测试数据上传成功通知
	 * 注：CDMA标识为MEID，其他手机标识为IMEI 2、 发送邮件内容：终端号+上传时间+已上传成功数据量+上传成功数据名称
	 */
	public void sendReportMail(String[] fileNames, Context context,Handler mHandler) {
		mServerManager = ServerManager.getInstance(context);
		if (fileNames != null && fileNames.length > 0)
			new Thread(new SendReportMailThread(fileNames, context,mHandler)).start();

	}

	/**
	 * 发送报告邮件线程
	 * 
	 */
	private class SendReportMailThread implements Runnable {
		/** 文件名列表 */
		private String[] mFileNames;
		/** 上下文 */
		private Context mContext;
		/**句柄**/
		private Handler mHandler;
		

		public SendReportMailThread(String[] fileNames, Context context,Handler mHandler) {
			this.mFileNames = fileNames;
			this.mContext = context;
			this.mHandler = mHandler;
		}

		@Override
		public void run() {
			{
				mHandler.sendEmptyMessage(0x01);
				String subject = MyPhoneState.getInstance().getIMEI(mContext)
						+ mContext.getResources().getString(R.string.total_uploaded_successfully_notice);
				StringBuffer contentBuffer = new StringBuffer();
				contentBuffer.append(String.format(mContext.getString(R.string.total_uploaded_at_time),
						MyPhoneState.getInstance().getIMEI(mContext), DateUtil.Y_M_D_H_M.format(new Date()),
						this.mFileNames.length));
				contentBuffer.append("<br>");
				for (int i = 0; i < this.mFileNames.length; i++) {
					contentBuffer.append((this.mFileNames[i].indexOf("/") != -1 ? this.mFileNames[i].substring(this.mFileNames[i].lastIndexOf("/") + 1, this.mFileNames[i].length()) : this.mFileNames[i]) + ";<br>");
				}
				LogUtil.i(TAG, contentBuffer.toString());
				Properties props = System.getProperties();
				props.put("mail.smtp.host", mServerManager.getEmailSendServer());
				if (mServerManager.getEmailSendPort().equals("465")) {
					props.put("mail.smtp.port", "465");
					props.put("mail.smtp.ssl", "true");
					props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					props.setProperty("mail.smtp.socketFactory.fallback", "false");
					props.setProperty("mail.smtp.socketFactory.port", "465");
				}
				props.put("mail.smtp.auth", "true");
				try {
					Authenticator authentic = new Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(mServerManager.getEmailSendAddress().split("@")[0],
									mServerManager.getEmailSendPassoword());
						}

					};
					Session session = Session.getDefaultInstance(props, authentic);
					session.setDebug(true);
					MimeMessage message = new MimeMessage(session);
					message.setSubject(subject);
					message.setFrom(new InternetAddress(mServerManager.getEmailSendAddress()));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(mServerManager.getEmailReciverAddress()));
					Multipart part = new MimeMultipart();
					BodyPart body = new MimeBodyPart();
					body.setContent(contentBuffer.toString(), "text/html;charset=utf-8");
					part.addBodyPart(body);
					for (int i = 0; i < mFileNames.length; i++) {
							try {
								MimeBodyPart mbp = new MimeBodyPart();
								FileDataSource fds=new FileDataSource(mFileNames[i]); //得到数据源  
								mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
								mbp.setFileName(MimeUtility.encodeWord(fds.getName(), "GB2312",null));
								mbp.setFileName(fds.getName());  //得到文件名同样至入BodyPart  
								part.addBodyPart(mbp);
							} catch (Exception e) {
								e.printStackTrace();
							}
					}
					
					
					message.setContent(part);
					
					Transport trans = session.getTransport("smtp");
					// 连接邮件服务器
					trans.connect();
					MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
					mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
					mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
					mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
					mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
					mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
					CommandMap.setDefaultCommandMap(mc);
					trans.sendMessage(message, message.getAllRecipients());
					LogUtil.d(TAG, "send success..");
					mHandler.sendEmptyMessage(0x02);
				} catch (MessagingException e) {
					e.printStackTrace();
					LogUtil.e(TAG, "send failed..");
					mHandler.sendEmptyMessage(0x03);
				}

			}
		}

	}

}

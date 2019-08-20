package com.walktour.service.app.datatrans.smtp;

import android.content.Context;
import android.content.Intent;

import com.walktour.Utils.DateUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
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

/**
 * 发送错误日志邮件报告
 * 
 * @author jianchao.wang
 *
 */
public class SendMailReport {
	/** 日志标识 */
	private static final String TAG = "SendMailReport";
	/** 报告文件名 */
	public static final String EXTRA_REPOET_FILES = "key_repoet_files";
	/** 服务器管理类 */
	private ServerManager mServerManager;

	/**
	 * 1、 发送邮件主题：终端号+测试数据上传成功通知 例：357242047911950测试数据上传成功通知
	 * 注：CDMA标识为MEID，其他手机标识为IMEI 2、 发送邮件内容：终端号+上传时间+已上传成功数据量+上传成功数据名称
	 * 例：357242047911950于2012/10/24-17：14成功上传2条数据： 数据A； 数据B。
	 */
	public void sendReportMail(String[] fileNames, Context context) {
		mServerManager = ServerManager.getInstance(context);
		if (mServerManager.isEmailNotifyToggle() && fileNames != null && fileNames.length > 0)
			new Thread(new SendReportMailThread(fileNames, context)).start();

	}

	/**
	 * 发送报告邮件线程
	 * 
	 * @author jianchao.wang
	 *
	 */
	private class SendReportMailThread implements Runnable {
		/** 文件名列表 */
		private String[] mFileNames;
		/** 上下文 */
		private Context mContext;

		public SendReportMailThread(String[] fileNames, Context context) {
			this.mFileNames = fileNames;
			this.mContext = context;
		}

		@Override
		public void run() {
			{
				String subject = MyPhoneState.getInstance().getIMEI(mContext)
						+ mContext.getResources().getString(R.string.data_uploaded_successfully_notice);
				StringBuffer contentBuffer = new StringBuffer();
				contentBuffer.append(String.format(mContext.getString(R.string.data_uploaded_at_time),
						MyPhoneState.getInstance().getIMEI(mContext), DateUtil.Y_M_D_H_M.format(new Date()),
						this.mFileNames.length));
				contentBuffer.append("<br>");
				for (int i = 0; i < this.mFileNames.length; i++) {
					contentBuffer.append(this.mFileNames[i] + ";<br>");
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
					// 邮件正文内容
					body.setContent(contentBuffer.toString(), "text/html;charset=utf-8");
					part.addBodyPart(body);
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
				} catch (MessagingException e) {
					Intent intent = new Intent(mContext.getApplicationContext(), EmailReSendDialog.class);
					intent.putExtra(EXTRA_REPOET_FILES, this.mFileNames);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
					e.printStackTrace();
					LogUtil.e(TAG, "send failed..");
				}
				/*
				 * MailSender sender = new MailSender(); sender.setSubject(subject);
				 * sender.setContent(contentBuffer.toString());
				 * 
				 * sender.setSmtpServer(mServerManager.getEmailSendServer());
				 * sender.setPort(Integer.valueOf(mServerManager.getEmailSendPort()));
				 * sender.setIfAuth(true);
				 * //sender.setUserName(ServerManager.getInstance
				 * (context).getEmailSendAddress());
				 * sender.setPassword(mServerManager.getEmailSendPassoword());
				 * sender.setFrom(mServerManager.getEmailSendAddress());
				 * sender.setTo(mServerManager.getEmailReciverAddress());
				 * sender.setSmtpEventListener(this); sender.send();
				 */

			}
		}

	}

}

package com.walktour.model;

public class TotalTaskModel {
	private int dialTimes;		//拨打尝试次数
	private int dialSuccTimes;	//接通次数
	private int dialDropTimes;	//掉话次数
	private int videoDTimes;	//视频拨打次数
	private int videoDSuccTimes;//视频拨打成功次数
	private int videoDDropTimes;//视频拨打掉话次数
	private float dialDelay;		//拨打时延
	private float videoDDelay;	//视频拨打时延
	
	private int attachTimes;	//Attach次数
	private int attachSuccTimes;//Attach成功次数
	private float attachDelay;	//Attach时延
	private int pdpTimes;		
	private int pdpSuccTimes;	
	private float pdpDelay;
	private int pingTimes;
	private int pingSuccTimes;
	private float pingDelay;
	private int wapRefTimes;
	private int wapRefSuccTimes;
	private float wapRefDelay;
	private int wapLoginTimes;
	private int wapLoginSuccTimes;
	private float wapLoginDelay;
	
	private int ftpULTimes;		//Ftp上传次数
	private int ftpULSuccTimes;	//Ftp 上传成功次数
	private float ftpULRate;		//上传速率
	private int ftpDLTimes;
	private int ftpDLSuccTimes;
	private float ftpDLRate;
	private int httpTimes;
	private int httpSuccTimes;
	private float httpRate;
	private int smtpTimes;
	private int smtpSuccTimes;
	private float smtpRate;
	private int pop3Times;
	private int pop3SuccTimes;
	private float pop3Rate;
	private int wapDLTimes;
	private int wapDLSuccTimes;
	private float wapDLRate;
	
	private int smsSendTimes;		//发送次数
	private int smsSendSuccTimes;	//发送成功
	private float smsSendDelay;		//发送时延
	private int smsReceiveSuccTimes;//接收成功
	private float smsPTPDelay;		//端到端时延
	
	private int mmsTimes;			//发送次数
	private int mmsSuccTimes;		//发送成功
	private float mmsSendDelay;		//发送时延
	private int mmsPushSuccTimes;	//PUSH成功次数
	private float mmsPushDelay;		//PUSH时延
	private int mmsReceiveSuccTimes;//接收成功
	private float mmsPTPDelay;		//端到端时延
	
	private int fetionTimes;
	private int fetionSuccTimes;
	private float fetionDelay;
	private int fetionReceiveSuccTimes;
	private float fetionPTPDelay;
	
	public int getDialTimes() {
		return dialTimes;
	}
	public void setDialTimes(int dialTimes) {
		this.dialTimes = dialTimes;
	}
	public int getDialSuccTimes() {
		return dialSuccTimes;
	}
	public void setDialSuccTimes(int dialSuccTimes) {
		this.dialSuccTimes = dialSuccTimes;
	}
	public int getDialDropTimes() {
		return dialDropTimes;
	}
	public void setDialDropTimes(int dialDropTimes) {
		this.dialDropTimes = dialDropTimes;
	}
	public int getVideoDTimes() {
		return videoDTimes;
	}
	public void setVideoDTimes(int videoDTimes) {
		this.videoDTimes = videoDTimes;
	}
	public int getVideoDSuccTimes() {
		return videoDSuccTimes;
	}
	public void setVideoDSuccTimes(int videoDSuccTimes) {
		this.videoDSuccTimes = videoDSuccTimes;
	}
	public int getVideoDDropTimes() {
		return videoDDropTimes;
	}
	public void setVideoDDropTimes(int videoDDropTimes) {
		this.videoDDropTimes = videoDDropTimes;
	}
	public float getDialDelay() {
		return dialDelay;
	}
	public void setDialDelay(float dialDelay) {
		this.dialDelay = dialDelay;
	}
	public float getVideoDDelay() {
		return videoDDelay;
	}
	public void setVideoDDelay(float videoDDelay) {
		this.videoDDelay = videoDDelay;
	}
	public int getAttachTimes() {
		return attachTimes;
	}
	public void setAttachTimes(int attachTimes) {
		this.attachTimes = attachTimes;
	}
	public int getAttachSuccTimes() {
		return attachSuccTimes;
	}
	public void setAttachSuccTimes(int attachSuccTimes) {
		this.attachSuccTimes = attachSuccTimes;
	}
	public float getAttachDelay() {
		return attachDelay;
	}
	public void setAttachDelay(float attachDelay) {
		this.attachDelay = attachDelay;
	}
	public int getPdpTimes() {
		return pdpTimes;
	}
	public void setPdpTimes(int pdpTimes) {
		this.pdpTimes = pdpTimes;
	}
	public int getPdpSuccTimes() {
		return pdpSuccTimes;
	}
	public void setPdpSuccTimes(int pdpSuccTimes) {
		this.pdpSuccTimes = pdpSuccTimes;
	}
	public float getPdpDelay() {
		return pdpDelay;
	}
	public void setPdpDelay(float pdpDelay) {
		this.pdpDelay = pdpDelay;
	}
	public int getPingTimes() {
		return pingTimes;
	}
	public void setPingTimes(int pingTimes) {
		this.pingTimes = pingTimes;
	}
	public int getPingSuccTimes() {
		return pingSuccTimes;
	}
	public void setPingSuccTimes(int pingSuccTimes) {
		this.pingSuccTimes = pingSuccTimes;
	}
	public float getPingDelay() {
		return pingDelay;
	}
	public void setPingDelay(float pingDelay) {
		this.pingDelay = pingDelay;
	}
	public int getWapRefTimes() {
		return wapRefTimes;
	}
	public void setWapRefTimes(int wapRefTimes) {
		this.wapRefTimes = wapRefTimes;
	}
	public int getWapRefSuccTimes() {
		return wapRefSuccTimes;
	}
	public void setWapRefSuccTimes(int wapRefSuccTimes) {
		this.wapRefSuccTimes = wapRefSuccTimes;
	}
	public float getWapRefDelay() {
		return wapRefDelay;
	}
	public void setWapRefDelay(float wapRefDelay) {
		this.wapRefDelay = wapRefDelay;
	}
	public int getWapLoginTimes() {
		return wapLoginTimes;
	}
	public void setWapLoginTimes(int wapLoginTimes) {
		this.wapLoginTimes = wapLoginTimes;
	}
	public int getWapLoginSuccTimes() {
		return wapLoginSuccTimes;
	}
	public void setWapLoginSuccTimes(int wapLoginSuccTimes) {
		this.wapLoginSuccTimes = wapLoginSuccTimes;
	}
	public float getWapLoginDelay() {
		return wapLoginDelay;
	}
	public void setWapLoginDelay(float wapLoginDelay) {
		this.wapLoginDelay = wapLoginDelay;
	}
	public int getFtpULTimes() {
		return ftpULTimes;
	}
	public void setFtpULTimes(int ftpULTimes) {
		this.ftpULTimes = ftpULTimes;
	}
	public int getFtpULSuccTimes() {
		return ftpULSuccTimes;
	}
	public void setFtpULSuccTimes(int ftpULSuccTimes) {
		this.ftpULSuccTimes = ftpULSuccTimes;
	}
	public float getFtpULRate() {
		return ftpULRate;
	}
	public void setFtpULRate(float ftpULRate) {
		this.ftpULRate = ftpULRate;
	}
	public int getFtpDLTimes() {
		return ftpDLTimes;
	}
	public void setFtpDLTimes(int ftpDLTimes) {
		this.ftpDLTimes = ftpDLTimes;
	}
	public int getFtpDLSuccTimes() {
		return ftpDLSuccTimes;
	}
	public void setFtpDLSuccTimes(int ftpDLSuccTimes) {
		this.ftpDLSuccTimes = ftpDLSuccTimes;
	}
	public float getFtpDLRate() {
		return ftpDLRate;
	}
	public void setFtpDLRate(float ftpDLRate) {
		this.ftpDLRate = ftpDLRate;
	}
	public int getHttpTimes() {
		return httpTimes;
	}
	public void setHttpTimes(int httpTimes) {
		this.httpTimes = httpTimes;
	}
	public int getHttpSuccTimes() {
		return httpSuccTimes;
	}
	public void setHttpSuccTimes(int httpSuccTimes) {
		this.httpSuccTimes = httpSuccTimes;
	}
	public float getHttpRate() {
		return httpRate;
	}
	public void setHttpRate(float httpRate) {
		this.httpRate = httpRate;
	}
	public int getSmtpTimes() {
		return smtpTimes;
	}
	public void setSmtpTimes(int smtpTimes) {
		this.smtpTimes = smtpTimes;
	}
	public int getSmtpSuccTimes() {
		return smtpSuccTimes;
	}
	public void setSmtpSuccTimes(int smtpSuccTimes) {
		this.smtpSuccTimes = smtpSuccTimes;
	}
	public float getSmtpRate() {
		return smtpRate;
	}
	public void setSmtpRate(float smtpRate) {
		this.smtpRate = smtpRate;
	}
	public int getPop3Times() {
		return pop3Times;
	}
	public void setPop3Times(int pop3Times) {
		this.pop3Times = pop3Times;
	}
	public int getPop3SuccTimes() {
		return pop3SuccTimes;
	}
	public void setPop3SuccTimes(int pop3SuccTimes) {
		this.pop3SuccTimes = pop3SuccTimes;
	}
	public float getPop3Rate() {
		return pop3Rate;
	}
	public void setPop3Rate(float pop3Rate) {
		this.pop3Rate = pop3Rate;
	}
	public int getWapDLTimes() {
		return wapDLTimes;
	}
	public void setWapDLTimes(int wapDLTimes) {
		this.wapDLTimes = wapDLTimes;
	}
	public int getWapDLSuccTimes() {
		return wapDLSuccTimes;
	}
	public void setWapDLSuccTimes(int wapDLSuccTimes) {
		this.wapDLSuccTimes = wapDLSuccTimes;
	}
	public float getWapDLRate() {
		return wapDLRate;
	}
	public void setWapDLRate(float wapDLRate) {
		this.wapDLRate = wapDLRate;
	}
	public int getSmsSendTimes() {
		return smsSendTimes;
	}
	public void setSmsSendTimes(int smsSendTimes) {
		this.smsSendTimes = smsSendTimes;
	}
	public int getSmsSendSuccTimes() {
		return smsSendSuccTimes;
	}
	public void setSmsSendSuccTimes(int smsSendSuccTimes) {
		this.smsSendSuccTimes = smsSendSuccTimes;
	}
	public float getSmsSendDelay() {
		return smsSendDelay;
	}
	public void setSmsSendDelay(float smsSendDelay) {
		this.smsSendDelay = smsSendDelay;
	}
	public int getSmsReceiveSuccTimes() {
		return smsReceiveSuccTimes;
	}
	public void setSmsReceiveSuccTimes(int smsReceiveSuccTimes) {
		this.smsReceiveSuccTimes = smsReceiveSuccTimes;
	}
	public float getSmsPTPDelay() {
		return smsPTPDelay;
	}
	public void setSmsPTPDelay(float smsPTPDelay) {
		this.smsPTPDelay = smsPTPDelay;
	}
	public int getMmsTimes() {
		return mmsTimes;
	}
	public void setMmsTimes(int mmsTimes) {
		this.mmsTimes = mmsTimes;
	}
	public int getMmsSuccTimes() {
		return mmsSuccTimes;
	}
	public void setMmsSuccTimes(int mmsSuccTimes) {
		this.mmsSuccTimes = mmsSuccTimes;
	}
	public float getMmsSendDelay() {
		return mmsSendDelay;
	}
	public void setMmsSendDelay(float mmsSendDelay) {
		this.mmsSendDelay = mmsSendDelay;
	}
	public int getMmsPushSuccTimes() {
		return mmsPushSuccTimes;
	}
	public void setMmsPushSuccTimes(int mmsPushSuccTimes) {
		this.mmsPushSuccTimes = mmsPushSuccTimes;
	}
	public float getMmsPushDelay() {
		return mmsPushDelay;
	}
	public void setMmsPushDelay(float mmsPushDelay) {
		this.mmsPushDelay = mmsPushDelay;
	}
	public int getMmsReceiveSuccTimes() {
		return mmsReceiveSuccTimes;
	}
	public void setMmsReceiveSuccTimes(int mmsReceiveSuccTimes) {
		this.mmsReceiveSuccTimes = mmsReceiveSuccTimes;
	}
	public float getMmsPTPDelay() {
		return mmsPTPDelay;
	}
	public void setMmsPTPDelay(float mmsPTPDelay) {
		this.mmsPTPDelay = mmsPTPDelay;
	}
	public int getFetionTimes() {
		return fetionTimes;
	}
	public void setFetionTimes(int fetionTimes) {
		this.fetionTimes = fetionTimes;
	}
	public int getFetionSuccTimes() {
		return fetionSuccTimes;
	}
	public void setFetionSuccTimes(int fetionSuccTimes) {
		this.fetionSuccTimes = fetionSuccTimes;
	}
	public float getFetionDelay() {
		return fetionDelay;
	}
	public void setFetionDelay(float fetionDelay) {
		this.fetionDelay = fetionDelay;
	}
	public int getFetionReceiveSuccTimes() {
		return fetionReceiveSuccTimes;
	}
	public void setFetionReceiveSuccTimes(int fetionReceiveSuccTimes) {
		this.fetionReceiveSuccTimes = fetionReceiveSuccTimes;
	}
	public float getFetionPTPDelay() {
		return fetionPTPDelay;
	}
	public void setFetionPTPDelay(float fetionPTPDelay) {
		this.fetionPTPDelay = fetionPTPDelay;
	}

}

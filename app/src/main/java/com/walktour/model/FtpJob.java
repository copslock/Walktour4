package com.walktour.model;

public class FtpJob {
	
	/**远程文件*/
	private String remoteFile = "";
	/**本地文件*/
	private String localFile = "";
	
	/**操作结果*/
	private boolean jobDone = false;
	
	/**
	 * Ftp操作任务
	 * @param remoteFile 远程文件
	 * @param localFile 本地文件
	 * @param mode 0上传，1下载, {@link FtpJob.MODE_*}
	 */
	public FtpJob(String remoteFile,String localFile){
		this.remoteFile = remoteFile;
		this.localFile = localFile;
	}
	
	public String getRemoteFile() {
		return remoteFile;
	}
	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}
	public String getLocalFile() {
		return localFile;
	}
	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}
	public boolean isJobDone() {
		return jobDone;
	}
	public void setJobDone(boolean jobDone) {
		this.jobDone = jobDone;
	}
	
}

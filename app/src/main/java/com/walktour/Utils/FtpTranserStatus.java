package com.walktour.Utils;

public class FtpTranserStatus {
	public static enum UploadStatus{
		Connect_FTP_Fail,
		Create_Directory_Fail,
		Create_Directory_Success,
		File_Exits,
		Remote_Bigger_Local,
		Upload_From_Break_Failed,
		Upload_From_Break_Success,
		Delete_Remote_Faild,
		Delete_Remote_Success,
		Upload_New_File_Failed,
		Upload_New_File_Success,
		Upload_Interrupted,
	}
	
	public static enum DownloadStatus{
		Remote_File_Noexist,
		Local_Bigger_Remote,
		Download_From_Break_Success,
		Download_From_Break_Failed,
		Download_New_Success,
		Download_New_Failed,
		Download_Stopped
	}
}

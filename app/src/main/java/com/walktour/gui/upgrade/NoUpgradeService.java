package com.walktour.gui.upgrade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.walktour.Utils.FtpTranserStatus.DownloadStatus;
import com.walktour.Utils.FtpOperate;
import com.walktour.Utils.FtpTranserStatus;
import com.walktour.base.util.LogUtil;
import com.walktour.model.FtpJob;
import com.walktour.model.FtpServerModel;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;

/**
 * 禁止升级下载Excel
 * @author jinfeng.xie
 * @data 2019/3/21
 */
public class NoUpgradeService extends Service implements FtpOperate.OnProgressChangeListener  {
    private static final String TAG = "NoUpgradeService";
    boolean isDoingJob=false;
    /** FTP操作类 */
    private FtpOperate ftp;
    private FtpServerModel ftpServerModel;
    /** IP列表 */
    private String ip="172.16.2.20";
    /** 端口 */
    private String port = "2221";
    /** 登录用户 */
    private String user = "walktour_beta";
    /** 登录密码 */
    private String password = "8T$18#856$$Jyiz";
    /** 连接超时时长 */
    private int timeout = 20 * 1000;

    private String serverCatalog="WalkTourNoUpgrade";

    String mlocalPath= Environment.getExternalStorageDirectory().toString() + "/walktour/export/NoUpgrade.xls";
    private String serverxlsName;


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG,"onCreate");
        new JobPreparer().start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG,"onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onProgressChange(long localSize, long remoteSize) {
        LogUtil.d(TAG,"remoteSize:"+remoteSize);
    }

    public class JobPreparer extends Thread {

        @Override
        public void run() {
            if (!isDoingJob) {
                isDoingJob = true;
                            if (checkConnect(ip, Integer.parseInt(port), user, password)) {
                                ftpServerModel = new FtpServerModel();
                                ftpServerModel.setName("VersionUpgrade");
                                ftpServerModel.setIp(ip);
                                ftpServerModel.setPort(port);
                                ftpServerModel.setLoginUser(user);
                                ftpServerModel.setLoginPassword(password);
                            }
                        doTheJob();
                isDoingJob = false;
                stopSelf();
            }

        }

    }

    private void doTheJob() {
		try {
			ftp = new FtpOperate(this, this);
            Thread.sleep(500);
			boolean connected = false;
			if (this.ftpServerModel != null)
				connected = ftp.connect(this.ftpServerModel.getIp(), Integer.parseInt(this.ftpServerModel.getPort()),
						this.ftpServerModel.getLoginUser(), this.ftpServerModel.getLoginPassword());
			LogUtil.d(TAG,"是否登录成功:"+connected);
			if (connected) {
				FTPFile[] files = ftp.getFTPLists(File.separator + this.serverCatalog, ftpServerModel);
				if (files != null) {
                    serverxlsName = new String();
					for (FTPFile file : files) {
						if (file.getName().contains("NoUpgrade.xls")) {
                            serverxlsName=file.getName();
                            FtpJob xmlJob = new FtpJob(File.separator + this.serverCatalog + File.separator + this.serverxlsName,
                                    mlocalPath);
                            File mclocatFile=new File(mlocalPath);
                            if (!mclocatFile.exists()){
                                downLoadFromFtp(this,xmlJob,true);
                            }else {
                                LogUtil.d(TAG,""+mlocalPath.length());
                                LogUtil.d(TAG,""+file.getSize());
                                if (mclocatFile.length()!=file.getSize()){
                                    downLoadFromFtp(this,xmlJob,true);
                                }
                            }
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Log.d(TAG, "---disconnect from ftp");
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

    /**
     * 检查连接是否可用
     *
     * @param serverIp
     *          服务端Ip
     * @param serverPort
     *          服务端Port
     * @param serverUser
     *          登录帐号
     * @param serverPassword
     *          登录密码
     */
    private boolean checkConnect(String serverIp, int serverPort, String serverUser, String serverPassword) {
        boolean connected = false;
        try {
            ftp = new FtpOperate();
            connected = ftp.connect(serverIp, serverPort, serverUser, serverPassword, this.timeout);
        } catch (Exception e) {
        } finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connected;
    }

    /**
     * FTP下载任务 ,请在单独线程里执行
     *
     * @param ftpJob
     *          任务
     * @param isReport
     *          是否报告百分比
     * @return 是否下载 成功
     */
    private Status downLoadFromFtp(Context context, FtpJob ftpJob, boolean isReport) {
        try {
            ftp = new FtpOperate(context, this);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean connected = ftp.connect(this.ftpServerModel.getIp(), Integer.parseInt(this.ftpServerModel.getPort()),
                    this.ftpServerModel.getLoginUser(), this.ftpServerModel.getLoginPassword());
            if (connected) {
                FtpTranserStatus.DownloadStatus downStatus = ftp.download(ftpJob.getRemoteFile(), ftpJob.getLocalFile(), isReport, false);
                if (FtpTranserStatus.DownloadStatus.Local_Bigger_Remote == downStatus) {
                    File file = new File(ftpJob.getLocalFile());
                    file.delete();
                    return this.downLoadFromFtp(context, ftpJob, isReport);
                }
                if (DownloadStatus.Download_From_Break_Success == downStatus
                        || DownloadStatus.Download_New_Success == downStatus) {
                    ftpJob.setJobDone(true);
                    return Status.DOWNLOAD_SUCCESS;
                } else if (DownloadStatus.Remote_File_Noexist == downStatus) {
                    return Status.REMOTE_NOT_EXSIT;
                } else if (DownloadStatus.Download_Stopped == downStatus) {
                    return Status.DOWNLOAD_STOP;
                }
            } else {
                return Status.CONNECT_FTP_FAIL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Log.d(TAG, "---disconnect from ftp");
                ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Status.DOWNLOAD_FAIL;
    }
}

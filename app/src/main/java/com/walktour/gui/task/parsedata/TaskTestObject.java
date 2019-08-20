package com.walktour.gui.task.parsedata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.gui.task.parsedata.model.task.dnslookup.TaskDNSLookUpModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
import com.walktour.gui.task.parsedata.model.task.email.sendreceive.TaskEmailSmtpPop3Model;
import com.walktour.gui.task.parsedata.model.task.facebook.TaskFaceBookModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.http.upload.TaskHttpUploadModel;
import com.walktour.gui.task.parsedata.model.task.idle.TaskEmptyModel;
import com.walktour.gui.task.parsedata.model.task.iperf.TaskIperfModel;
import com.walktour.gui.task.parsedata.model.task.mms.receive.TaskMmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.mms.send.TaskMmsSendModel;
import com.walktour.gui.task.parsedata.model.task.mms.sendreceive.TaskMmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.download.TaskMultiftpDownloadModel;
import com.walktour.gui.task.parsedata.model.task.multiftp.upload.TaskMultiftpUploadModel;
import com.walktour.gui.task.parsedata.model.task.multihttp.download.TaskMultiHttpDownModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.opensignal.TaskOpenSignalModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.gui.task.parsedata.model.task.pbm.TaskPBMModel;
import com.walktour.gui.task.parsedata.model.task.pdpactive.TaskPdpModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.reboot.TaskRebootModel;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.sms.send.TaskSmsSendModel;
import com.walktour.gui.task.parsedata.model.task.sms.sendreceive.TaskSmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.speedtest.TaskSpeedTestModel;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;
import com.walktour.gui.task.parsedata.model.task.udp.TaskUDPModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.task.parsedata.model.task.videostreaming.TaskStreamModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.gui.task.parsedata.model.task.weibo.TaskWeiBoModel;
import com.walktour.gui.task.parsedata.model.task.wlan.login.TaskWlanLoginModel;
import com.walktour.service.test.AttachTest;
import com.walktour.service.test.DNSLookUpTest;
import com.walktour.service.test.EmailReceive;
import com.walktour.service.test.EmailSend;
import com.walktour.service.test.FaceBookService;
import com.walktour.service.test.FtpDownTest;
import com.walktour.service.test.FtpMultiDownTest;
import com.walktour.service.test.FtpMultiUpTest;
import com.walktour.service.test.FtpUpTest;
import com.walktour.service.test.HttpDown;
import com.walktour.service.test.HttpLogon;
import com.walktour.service.test.HttpRefresh;
import com.walktour.service.test.HttpUploadTest;
import com.walktour.service.test.IperfTest;
import com.walktour.service.test.MOCTest;
import com.walktour.service.test.MTCTest;
import com.walktour.service.test.MmsTest;
import com.walktour.service.test.MultiHttpDownload;
import com.walktour.service.test.OpenSignalService;
import com.walktour.service.test.OttFacebookTest;
import com.walktour.service.test.OttInstagramTest;
import com.walktour.service.test.OttQQAppTest;
import com.walktour.service.test.OttSinaWeiboTest;
import com.walktour.service.test.OttSkypeTest;
import com.walktour.service.test.OttWeCallMocTest;
import com.walktour.service.test.OttWeChatTest;
import com.walktour.service.test.OttWecallMtcTest;
import com.walktour.service.test.OttWhatsAppMtcTest;
import com.walktour.service.test.OttWhatsAppTest;
import com.walktour.service.test.OttWhatsAppMocTest;
import com.walktour.service.test.PBMTest;
import com.walktour.service.test.PdpTest;
import com.walktour.service.test.PingTest;
import com.walktour.service.test.PingTestCMD;
import com.walktour.service.test.PingTestNB;
import com.walktour.service.test.RebootTest;
import com.walktour.service.test.SMSReceiveTest;
import com.walktour.service.test.SMSSendReceiveTest;
import com.walktour.service.test.SMSSendTest;
import com.walktour.service.test.SpeedTestService;
import com.walktour.service.test.StreamingTest;
import com.walktour.service.test.TestTaskService;
import com.walktour.service.test.TraceRouteService;
import com.walktour.service.test.TraceRouteServiceS8;
import com.walktour.service.test.UDPTest;
import com.walktour.service.test.VideoPlayBeiJingTest;
import com.walktour.service.test.VideoPlayTest;
import com.walktour.service.test.VideoPlayVitamioTest;
import com.walktour.service.test.VideoPlayYoutubeTest;
import com.walktour.service.test.WapLogon;
import com.walktour.service.test.WapRefresh;
import com.walktour.service.test.WebPage;
import com.walktour.service.test.WeiBoTest;
import com.walktour.service.test.WlanTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.walktour.service.test.UDPTest;

/**
 * 测试任务对象生成类
 *
 * @author tangwq
 */
public class TaskTestObject {
    private static final String tag = "TaskTestObject";
    private Context context;

    public static final String ControlCircleJ = "ControlCircleJ";

    public static int PING_LAST_DELAY = 0;

    public TaskTestObject(Context ctx) {
        this.context = ctx;
    }

    /**
     * 根据当前测试MODEL，获得相应测试服务的INTENT
     *
     * @param obj
     * @param j
     * @return
     */
    public Intent getTestTaskIntent(Object obj, int j, int pdpDelay) {
        Intent specificTestJob = new Intent();
        Bundle bundle = new Bundle();

        TaskModel model = (TaskModel) obj;
        LogUtil.v(tag, model.toString() + "->TaskType:" + model.getTaskType());
        WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(model.getTaskType());
        specificTestJob.putExtra(ControlCircleJ, j);
        switch (taskType) {
            case EmptyTask:

                break;
            /*主叫测试*/
            case InitiativeCall:
                TaskInitiativeCallModel mocCallModel = (TaskInitiativeCallModel) model;
                String jsonString = new Gson().toJson(mocCallModel);
//                bundle.putSerializable(WalkCommonPara.testModelKey, mocCallModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, jsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.InitiativeCall);
//                bundle.setClassLoader(TaskInitiativeCallModel.class.getClassLoader());

                specificTestJob.setClass(context, MOCTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            /*被叫测试*/
            case PassivityCall:
                TaskPassivityCallModel mtcCallModel = (TaskPassivityCallModel) model;
//                bundle.putSerializable(WalkCommonPara.testModelKey, mtcCallModel);
                String passivityCallJsonString = new Gson().toJson(mtcCallModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, passivityCallJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.PassivityCall);
//                bundle.setClassLoader(TaskPassivityCallModel.class.getClassLoader());

                specificTestJob.setClass(context, MTCTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            case InitiativeVideoCall:
                break;
            case PassivityVideoCall:
                break;
            case Ping:
                TaskPingModel pingModel = (TaskPingModel) model;
                String pingJsonString = new Gson().toJson(pingModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, pingJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Ping);

                if (ApplicationModel.getInstance().isNBTest()&&pingModel.getTypeProperty()!=WalkCommonPara.TypeProperty_Ppp) {
                    specificTestJob.setClass(context, PingTestNB.class);
                } else if (ApplicationModel.getInstance().isNBTest()&&pingModel.getTypeProperty()==WalkCommonPara.TypeProperty_Ppp) {
                    specificTestJob.setClass(context, PingTest.class);
                } else if (((TaskPingModel) model).getPingTestConfig().isCMDPing()) {
                    specificTestJob.setClass(context, PingTestCMD.class);
                } else {
                    specificTestJob.setClass(context, PingTest.class);
                }

                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                specificTestJob.putExtra(TestTaskService.KEY_PING_DELAY, PING_LAST_DELAY);
                break;
            //Attach
            case Attach:
                TaskAttachModel attachModel = (TaskAttachModel) model;
                //Attach的测试次数在服务里控制，所以这里把j设定为最后一次
                //j = attachModel.getRepeat() -1;
//                bundle.putSerializable(WalkCommonPara.testModelKey, attachModel);
//                bundle.setClassLoader(TaskAttachModel.class.getClassLoader());
                String attachJsonString = new Gson().toJson(attachModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, attachJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Attach);

                specificTestJob.setClass(context, AttachTest.class);
                specificTestJob.putExtras(bundle);
            /*specificTestJob.putExtra( PdpTest.KEY_REPEAT, attachModel.getRepeat() );
            specificTestJob.putExtra( PdpTest.KEY_TIMEOUT, attachModel.getKeepTime() );
			specificTestJob.putExtra( PdpTest.KEY_INTERVAL, attachModel.getInterVal() );*/
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                specificTestJob.putExtra(ControlCircleJ, model.getRepeat() - 1);
                break;

            //PDP
            case PDP:
                TaskPdpModel pdpModel = (TaskPdpModel) model;
                String pdpJsonString = new Gson().toJson(pdpModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, pdpJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.PDP);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, pdpModel);
                bundle.setClassLoader(TaskPdpModel.class.getClassLoader());*/
                //PDP的测试次数在服务里控制，所以这里把j设定为最后一次
                //j = pdpModel.getRepeat() -1;

                specificTestJob.setClass(context, PdpTest.class);
                specificTestJob.putExtras(bundle);
                //specificTestJob.putExtra( PdpTest.KEY_TIME, j+1 );
                //specificTestJob.putExtra( PdpTest.KEY_APN, pdpModel.getApn() );
            /*specificTestJob.putExtra( PdpTest.KEY_REPEAT, pdpModel.getRepeat() );
			specificTestJob.putExtra( PdpTest.KEY_TIMEOUT, pdpModel.getKeepTime() );
			specificTestJob.putExtra( PdpTest.KEY_INTERVAL, pdpModel.getInterVal() );*/
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                specificTestJob.putExtra(ControlCircleJ, model.getRepeat() - 1);
                break;

            case FTPUpload:
                TaskFtpModel ftpUpload = (TaskFtpModel) model;
                /*bundle.putSerializable(WalkCommonPara.testModelKey, ftpUpload);
                bundle.setClassLoader(TaskFtpModel.class.getClassLoader());*/
                String ftpUploadJsonString = new Gson().toJson(ftpUpload);
                bundle.putString(WalkCommonPara.testModelJsonKey, ftpUploadJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.FTPUpload);

                specificTestJob.setClass(context, FtpUpTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            /*Ftp下载*/
            case FTPDownload:
                TaskFtpModel ftpDownload = (TaskFtpModel) model;
                String ftpDownloadJsonString = new Gson().toJson(ftpDownload);
                bundle.putString(WalkCommonPara.testModelJsonKey, ftpDownloadJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.FTPDownload);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, ftpDownload);
                bundle.setClassLoader(TaskFtpModel.class.getClassLoader());*/

                specificTestJob.setClass(context, FtpDownTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;


            case MultiftpUpload:
                TaskMultiftpUploadModel mFtpUpModel = (TaskMultiftpUploadModel) model;
                String mFtpUploadJsonString = new Gson().toJson(mFtpUpModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, mFtpUploadJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.MultiftpUpload);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, mFtpUpModel);
                bundle.setClassLoader(TaskMultiftpUploadModel.class.getClassLoader());*/

                specificTestJob.setClass(context, FtpMultiUpTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            case MultiftpDownload:
                TaskMultiftpDownloadModel mFtpDownModel = (TaskMultiftpDownloadModel) model;
                String mFtpDownloadJsonString = new Gson().toJson(mFtpDownModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, mFtpDownloadJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.MultiftpDownload);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, mFtpDownModel);
                bundle.setClassLoader(TaskMultiftpDownloadModel.class.getClassLoader());*/

                specificTestJob.setClass(context, FtpMultiDownTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            case EmailPop3:
                TaskEmailPop3Model popModel = (TaskEmailPop3Model) model;
                String popJsonString = new Gson().toJson(popModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, popJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.EmailPop3);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, popModel);
                bundle.setClassLoader(TaskEmailPop3Model.class.getClassLoader());*/

                specificTestJob.setClass(context, EmailReceive.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            case EmailSmtp:
                TaskEmailSmtpModel smtpModel = (TaskEmailSmtpModel) model;
                String smtpJsonString = new Gson().toJson(smtpModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, smtpJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.EmailSmtp);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, smtpModel);
                bundle.setClassLoader(TaskEmailSmtpModel.class.getClassLoader());*/

                specificTestJob.setClass(context, EmailSend.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

//		case EmailSmtpAndPOP:	//邮件自发自收
//			TaskEmailSmtpPop3Model smtpAndPop3Model = (TaskEmailSmtpPop3Model) model;
//			bundle.putSerializable(WalkCommonPara.testModelKey, smtpAndPop3Model);
//			
//			specificTestJob.setClass(context,EmailSmtpAndPopTest.class);
//			specificTestJob.putExtras(bundle);
//			specificTestJob.putExtra(WalkCommonPara.testRepeatTimes,j+1);
//			break;

            /*短信接收测试*/
            case SMSIncept:
                TaskSmsReceiveModel smsRecmodel = (TaskSmsReceiveModel) model;
                String smsRecJsonString = new Gson().toJson(smsRecmodel);
                bundle.putString(WalkCommonPara.testModelJsonKey, smsRecJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.SMSIncept);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, smsRecmodel);
                bundle.setClassLoader(TaskSmsReceiveModel.class.getClassLoader());*/

                specificTestJob.setClass(context, SMSReceiveTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                specificTestJob.putExtra(ControlCircleJ, model.getRepeat() - 1);
                break;

            /*短信发送测试*/
            case SMSSend:
                TaskSmsSendModel smsSendModel = (TaskSmsSendModel) model;
                String smsSendJsonString = new Gson().toJson(smsSendModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, smsSendJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.SMSSend);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, smsSendModel);
                bundle.setClassLoader(TaskSmsSendModel.class.getClassLoader());*/

                specificTestJob.setClass(context, SMSSendTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            /*短信自发自收测试*/
            case SMSSendReceive:
                TaskSmsSendReceiveModel smsSRModel = (TaskSmsSendReceiveModel) model;
                String smsSRJsonString = new Gson().toJson(smsSRModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, smsSRJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.SMSSendReceive);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, smsSRModel);
                bundle.setClassLoader(TaskSmsSendReceiveModel.class.getClassLoader());*/

                specificTestJob.setClass(context, SMSSendReceiveTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            /*彩信接收测试	*/
            case MMSIncept:
                TaskMmsReceiveModel mmsReceiveModel = (TaskMmsReceiveModel) model;
                String mmsReceiveJsonString = new Gson().toJson(mmsReceiveModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, mmsReceiveJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.MMSIncept);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, mmsReceiveModel);
                bundle.setClassLoader(TaskMmsReceiveModel.class.getClassLoader());*/
                bundle.putInt(MmsTest.KEY_PDP_DELAY, pdpDelay);

                specificTestJob.setClass(context, MmsTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            /*彩信发送测试	*/
            case MMSSend:
                TaskMmsSendModel mmsSendModel = (TaskMmsSendModel) model;
                String mmsSendJsonString = new Gson().toJson(mmsSendModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, mmsSendJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.MMSSend);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, mmsSendModel);
                bundle.setClassLoader(TaskMmsSendModel.class.getClassLoader());*/
                bundle.putInt(MmsTest.KEY_PDP_DELAY, pdpDelay);

                specificTestJob.setClass(context, MmsTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            /*彩信自发自收*/
            case MMSSendReceive:
                TaskMmsSendReceiveModel mmsSRModel = (TaskMmsSendReceiveModel) model;
                String mmsSRJsonString = new Gson().toJson(mmsSRModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, mmsSRJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.MMSSendReceive);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, mmsSRModel);
                bundle.setClassLoader(TaskMmsSendReceiveModel.class.getClassLoader());*/
                bundle.putInt(MmsTest.KEY_PDP_DELAY, pdpDelay);

                specificTestJob.setClass(context, MmsTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            case WapRefurbish:
                TaskWapPageModel wapRefurbish = (TaskWapPageModel) model;
                String wapRefurbishJsonString = new Gson().toJson(wapRefurbish);
                bundle.putString(WalkCommonPara.testModelJsonKey, wapRefurbishJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WapRefurbish);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, wapRefurbish);
                bundle.setClassLoader(TaskWapPageModel.class.getClassLoader());*/
                bundle.putInt(WebPage.KEY_PDP_DELAY, pdpDelay);

                specificTestJob.setClass(context, WapRefresh.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                specificTestJob.putExtra(ControlCircleJ, model.getRepeat() - 1);
                break;
            case WapLogin:
                TaskWapPageModel wapModel = (TaskWapPageModel) model;
                String wapJsonString = new Gson().toJson(wapModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, wapJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WapLogin);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, wapModel);
                bundle.setClassLoader(TaskWapPageModel.class.getClassLoader());*/
                bundle.putInt(WebPage.KEY_PDP_DELAY, pdpDelay);

                specificTestJob.setClass(context, WapLogon.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case WapDownload:
                TaskWapPageModel wapdModel = (TaskWapPageModel) model;
                String wapdJsonString = new Gson().toJson(wapdModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, wapdJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WapDownload);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, wapdModel);
                bundle.setClassLoader(TaskWapPageModel.class.getClassLoader());*/

                specificTestJob.setClass(context, HttpDown.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;

            case Http:
                TaskHttpPageModel httpLogon = (TaskHttpPageModel) model;
                String httpLogonJsonString = new Gson().toJson(httpLogon);
                bundle.putString(WalkCommonPara.testModelJsonKey, httpLogonJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Http);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, httpLogon);
                bundle.setClassLoader(TaskHttpPageModel.class.getClassLoader());*/

                specificTestJob.setClass(context, HttpLogon.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case HttpRefurbish:
                TaskHttpPageModel httpModel = (TaskHttpPageModel) model;
                String httpJsonString = new Gson().toJson(httpModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, httpJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.HttpRefurbish);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, httpModel);
                bundle.setClassLoader(TaskHttpPageModel.class.getClassLoader());*/

                specificTestJob.setClass(context, HttpRefresh.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                specificTestJob.putExtra(ControlCircleJ, model.getRepeat() - 1);
                break;
            case HttpDownload:
                TaskHttpPageModel httpDown = (TaskHttpPageModel) model;
                String httpDownJsonString = new Gson().toJson(httpDown);
                bundle.putString(WalkCommonPara.testModelJsonKey, httpDownJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.HttpDownload);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, httpDown);
                bundle.setClassLoader(TaskHttpPageModel.class.getClassLoader());*/

                specificTestJob.setClass(context, HttpDown.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            //Wlan登录
            case WlanLogin:
                TaskWlanLoginModel wlanModel = (TaskWlanLoginModel) model;
                String wlanJsonString = new Gson().toJson(wlanModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, wlanJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WlanLogin);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, wlanModel);
                bundle.setClassLoader(TaskWlanLoginModel.class.getClassLoader());*/

                specificTestJob.setClass(context, WlanTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case Stream://流媒体业务 JIhong Xie 2012-7-19
                TaskStreamModel streamModel = (TaskStreamModel) model;
                String streamJsonString = new Gson().toJson(streamModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, streamJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Stream);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, streamModel);
                bundle.setClassLoader(TaskStreamModel.class.getClassLoader());*/

                //将当前测试次数与测试实体传送到Service中
                //specificTestJob = new Intent(context, StreamTest.class);
                specificTestJob = new Intent(context, StreamingTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case DNSLookUp:
                TaskDNSLookUpModel dnsModel = (TaskDNSLookUpModel) model;
                String dnsJsonString = new Gson().toJson(dnsModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, dnsJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.DNSLookUp);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, dnsModel);
                bundle.setClassLoader(TaskDNSLookUpModel.class.getClassLoader());*/

                //将当前测试次数与测试实体传送到Service中
                specificTestJob = new Intent(context, DNSLookUpTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case SpeedTest:
                TaskSpeedTestModel speedModel = (TaskSpeedTestModel) model;
                String speedString = new Gson().toJson(speedModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, speedString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.SpeedTest);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, speedModel);
                bundle.setClassLoader(TaskSpeedTestModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, SpeedTestService.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case HttpUpload:
                TaskHttpUploadModel uploadModel = (TaskHttpUploadModel) model;
                String uploadString = new Gson().toJson(uploadModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, uploadString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.HttpUpload);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, uploadModel);
                bundle.setClassLoader(TaskHttpUploadModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, HttpUploadTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case HTTPVS:
                TaskVideoPlayModel videoPlayModel = (TaskVideoPlayModel) model;
                if (videoPlayModel.getPlayerType() == TaskVideoPlayModel.PLAYER_TYPE_VITAMIO) {
                    //维他命测试默认打开播放视频
                    videoPlayModel.setVideoShow(true);
                }
                String videoPlayString = new Gson().toJson(videoPlayModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, videoPlayString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.HTTPVS);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, videoPlayModel);
                bundle.setClassLoader(TaskVideoPlayModel.class.getClassLoader());*/

                if (ApplicationModel.getInstance().isBeiJingTest()) {
                    specificTestJob = new Intent(context, VideoPlayBeiJingTest.class);
                } else {
                    switch (videoPlayModel.getPlayerType()) {
                        case TaskVideoPlayModel.PLAYER_TYPE_VITAMIO:
                            specificTestJob = new Intent(context, VideoPlayVitamioTest.class);
                            break;
                        case TaskVideoPlayModel.PLAYER_TYPE_YOUTUBE:
                            specificTestJob = new Intent(context, VideoPlayYoutubeTest.class);
                            break;
                        default:
                            specificTestJob = new Intent(context, VideoPlayTest.class);
                            break;
                    }
                }
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case Facebook:
                TaskFaceBookModel faceBookModel = (TaskFaceBookModel) model;
                String faceBookString = new Gson().toJson(faceBookModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, faceBookString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Facebook);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, faceBookModel);
                bundle.setClassLoader(TaskFaceBookModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, FaceBookService.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case TraceRoute:
                TaskTraceRouteModel traceRouteModel = (TaskTraceRouteModel) model;
                String traceRouteString = new Gson().toJson(traceRouteModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, traceRouteString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.TraceRoute);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, traceRouteModel);
                bundle.setClassLoader(TaskTraceRouteModel.class.getClassLoader());*/

                if (Deviceinfo.getInstance().isS8orS7()) {
                    String romValue = UtilsMethod.execRootCmdx("cat /proc/version");
                    if (romValue.contains("Linux version 3.18.20")) {//电信Volte ROM
                        specificTestJob = new Intent(context, TraceRouteService.class);
                    } else {
                        specificTestJob = new Intent(context, TraceRouteServiceS8.class);
                    }
                } else {
                    specificTestJob = new Intent(context, TraceRouteService.class);
                }
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case Iperf:
                TaskIperfModel iperfModel = (TaskIperfModel) model;
                String iperfString = new Gson().toJson(iperfModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, iperfString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Iperf);
               /* bundle.putSerializable(WalkCommonPara.testModelKey, iperfModel);
                bundle.setClassLoader(TaskIperfModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, IperfTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case PBM:
                TaskPBMModel pbmModel = (TaskPBMModel) model;
                String pbmJsonString = new Gson().toJson(pbmModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, pbmJsonString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.PBM);
//                bundle.putSerializable(WalkCommonPara.testModelKey, pbmModel);
//                bundle.setClassLoader(TaskPBMModel.class.getClassLoader());

                specificTestJob = new Intent(context, PBMTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case WeiBo://微博测试业务
                TaskWeiBoModel weiBoModel = (TaskWeiBoModel) model;
                String weiBoString = new Gson().toJson(weiBoModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, weiBoString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WeiBo);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, weiBoModel);
                bundle.setClassLoader(TaskWeiBoModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, WeiBoTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
//            case WeChat://微信测试业务
//                TaskWeChatModel weChatModel = (TaskWeChatModel) model;
//                String weChatString = new Gson().toJson(weChatModel);
//                bundle.putString(WalkCommonPara.testModelJsonKey, weChatString);
//                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WeChat);
//                /*bundle.putSerializable(WalkCommonPara.testModelKey, weChatModel);
//                bundle.setClassLoader(TaskWeChatModel.class.getClassLoader());*/
//
//                specificTestJob = new Intent(context, WeChatTest.class);
//                specificTestJob.putExtras(bundle);
//                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
//                break;
            case UDP://UDP测试业务
                TaskUDPModel udpModel = (TaskUDPModel) model;
                String udpString = new Gson().toJson(udpModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, udpString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.UDP);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, udpModel);
                bundle.setClassLoader(TaskUDPModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, UDPTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case REBOOT://REBOOT业务
                TaskRebootModel rebootModel = (TaskRebootModel) model;
                String rebootString = new Gson().toJson(rebootModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, rebootString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.REBOOT);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, udpModel);
                bundle.setClassLoader(TaskUDPModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, RebootTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case OpenSignal://Opensignal业务
                TaskOpenSignalModel openSignalModel = (TaskOpenSignalModel) model;
                String openSignalString = new Gson().toJson(openSignalModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, openSignalString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.OPENSIGNAL);
                /*bundle.putSerializable(WalkCommonPara.testModelKey, udpModel);
                bundle.setClassLoader(TaskUDPModel.class.getClassLoader());*/

                specificTestJob = new Intent(context, OpenSignalService.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case MultiHttpDownload://MultiHttpDownload业务
                TaskMultiHttpDownModel multiHttpDownloadModel = (TaskMultiHttpDownModel) model;
                String multiHttpDownloadString = new Gson().toJson(multiHttpDownloadModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, multiHttpDownloadString);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.MULTIHTTPDOWNLOAD);

                specificTestJob = new Intent(context, MultiHttpDownload.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case WeChat:
                TaskMultipleAppTestModel weChatModel = (TaskMultipleAppTestModel) model;
                String weChatModelJson = new Gson().toJson(weChatModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, weChatModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WeChat);

                specificTestJob = new Intent(context, OttWeChatTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
			case WeCallMoc:
                TaskWeCallModel wecallMocModel = (TaskWeCallModel) model;
                String wecallMocModelJson = new Gson().toJson(wecallMocModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, wecallMocModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WeCallMoc);

                specificTestJob = new Intent(context, OttWeCallMocTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
			case WeCallMtc:
                TaskWeCallModel wecallMtcModel = (TaskWeCallModel) model;
                String wecallMtcModelJson = new Gson().toJson(wecallMtcModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, wecallMtcModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WeCallMtc);

                specificTestJob = new Intent(context, OttWecallMtcTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
			case SkypeChat:
                TaskMultipleAppTestModel skypeChatModel = (TaskMultipleAppTestModel) model;
                String skypeChatModelJson = new Gson().toJson(skypeChatModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, skypeChatModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.SkypeChat);

                specificTestJob = new Intent(context, OttSkypeTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
            case QQ:
                TaskMultipleAppTestModel qqModel = (TaskMultipleAppTestModel) model;
                String qqModelJson = new Gson().toJson(qqModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, qqModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.QQ);

                specificTestJob = new Intent(context, OttQQAppTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
            case WhatsAppChat:
                TaskMultipleAppTestModel whatsModel = (TaskMultipleAppTestModel) model;
                String whatsAppModelJson = new Gson().toJson(whatsModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, whatsAppModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WhatsAppChat);

                specificTestJob = new Intent(context, OttWhatsAppTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
            case WhatsAppMoc:
                TaskMultipleAppTestModel whatsMocModel = (TaskMultipleAppTestModel) model;
                String whatsAppMocModelJson = new Gson().toJson(whatsMocModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, whatsAppMocModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WhatsAppMoc);

                specificTestJob = new Intent(context, OttWhatsAppMocTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
            case WhatsAppMtc:
                TaskMultipleAppTestModel whatsMtcModel = (TaskMultipleAppTestModel) model;
                String whatsAppMtcModelJson = new Gson().toJson(whatsMtcModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, whatsAppMtcModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.WhatsAppMtc);

                specificTestJob = new Intent(context, OttWhatsAppMtcTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);

                break;
            case SinaWeibo:
				TaskMultipleAppTestModel sinaModel = (TaskMultipleAppTestModel) model;
				String sinaModelJson = new Gson().toJson(sinaModel);
				bundle.putString(WalkCommonPara.testModelJsonKey, sinaModelJson);
				bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.SinaWeibo);

				specificTestJob = new Intent(context, OttSinaWeiboTest.class);
				specificTestJob.putExtras(bundle);
				specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
				break;
            case Facebook_Ott:
                TaskMultipleAppTestModel facebookModel = (TaskMultipleAppTestModel) model;
                String facebookModelJson = new Gson().toJson(facebookModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, facebookModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Facebook_Ott);

                specificTestJob = new Intent(context, OttFacebookTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            case Instagram_Ott:
                TaskMultipleAppTestModel instagramModel = (TaskMultipleAppTestModel) model;
                String instagramModelJson = new Gson().toJson(instagramModel);
                bundle.putString(WalkCommonPara.testModelJsonKey, instagramModelJson);
                bundle.putInt(WalkCommonPara.testModelJsonTypeKey, WalkStruct.TaskTypeIDs.Instagram_Ott);

                specificTestJob = new Intent(context, OttInstagramTest.class);
                specificTestJob.putExtras(bundle);
                specificTestJob.putExtra(WalkCommonPara.testRepeatTimes, j + 1);
                break;
            default:
                break;
        }
        return specificTestJob;
    }

    /**
     * 获得当前测试MODEL用测试计划串
     *
     * @param model
     * @return
     */
    public static String getTestPlanInfo(TaskModel model) {
        WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(model.getTaskType());
        String testPlan = "";

        switch (taskType) {
            case EmptyTask:
                testPlan = ((TaskEmptyModel) model).getTestPlanStr();
                break;
            case InitiativeCall:
                testPlan = ((TaskInitiativeCallModel) model).getTestPlanStr();
                break;
            case PassivityCall:
                testPlan = ((TaskPassivityCallModel) model).getTestPlanStr();
                break;
            case Ping:
                testPlan = ((TaskPingModel) model).getTestPlanStr();
                break;
            case Attach:
                testPlan = ((TaskAttachModel) model).getTestPlanStr();
                break;
            case PDP:
                testPlan = ((TaskPdpModel) model).getTestPlanStr();
                break;
            case FTPUpload:
                testPlan = ((TaskFtpModel) model).getTestPlanStr();
                break;
            case FTPDownload:
                testPlan = ((TaskFtpModel) model).getTestPlanStr();
                break;
            case Http:
            case HttpRefurbish:
            case HttpDownload:
                testPlan = ((TaskHttpPageModel) model).getTestPlanStr();
                break;
            case EmailPop3:
                testPlan = ((TaskEmailPop3Model) model).getTestPlanStr();
                break;
            case EmailSmtp:
                testPlan = ((TaskEmailSmtpModel) model).getTestPlanStr();
                break;
            case EmailSmtpAndPOP:
                testPlan = ((TaskEmailSmtpPop3Model) model).getTestPlanStr();
                break;
            case SMSIncept:
                testPlan = ((TaskSmsReceiveModel) model).getTestPlanStr();
                break;
            case SMSSend:
                testPlan = ((TaskSmsSendModel) model).getTestPlanStr();
                break;
            case SMSSendReceive:
                testPlan = ((TaskSmsSendReceiveModel) model).getTestPlanStr();
                break;
            case MMSIncept:
                testPlan = ((TaskMmsReceiveModel) model).getTestPlanStr();
                break;
            case MMSSend:
                testPlan = ((TaskMmsSendModel) model).getTestPlanStr();
                break;
            case MMSSendReceive:
                testPlan = ((TaskMmsSendReceiveModel) model).getTestPlanStr();
                break;
            case WapLogin:
            case WapRefurbish:
            case WapDownload:
                testPlan = ((TaskWapPageModel) model).getTestPlanStr();
                break;
            case WlanLogin:
                break;
            case Stream: //流媒体业务
                testPlan = ((TaskStreamModel) model).getTestPlanStr();
                break;
            case DNSLookUp:
                testPlan = ((TaskDNSLookUpModel) model).getTestPlanStr();
                break;
            case SpeedTest:
                testPlan = ((TaskSpeedTestModel) model).getTestPlanStr();
                break;
            case HttpUpload:
                testPlan = ((TaskHttpUploadModel) model).getTestPlanStr();
                break;
            case MultiRAB:
                testPlan = ((TaskRabModel) model).getTestPlanStr();
                break;
            case HTTPVS:
                testPlan = ((TaskVideoPlayModel) model).getTestPlanStr();
                break;
            case MultiftpUpload:
                testPlan = ((TaskMultiftpUploadModel) model).getTestPlanStr();
                break;
            case MultiftpDownload:
                testPlan = ((TaskMultiftpDownloadModel) model).getTestPlanStr();
                break;
            case Facebook:
                testPlan = ((TaskFaceBookModel) model).getTestPlanStr();
                break;
            case TraceRoute:
                testPlan = ((TaskTraceRouteModel) model).getTestPlanStr();
                break;
            case Iperf:
                testPlan = ((TaskIperfModel) model).getTestPlanStr();
                break;
            case PBM:
                testPlan = ((TaskPBMModel) model).getTestPlanStr();
                break;
            case WeiBo:
                testPlan = ((TaskWeiBoModel) model).getTestPlanStr();
                break;
//            case WeChat:
//                testPlan = ((TaskWeChatModel) model).getTestPlanStr();
//                break;
            case UDP:
                testPlan = ((TaskUDPModel) model).getTestPlanStr();
                break;
			case WeCallMoc:
			case WeCallMtc:
                testPlan = ((TaskWeCallModel) model).getTestPlanStr();
			    break;
            case WeChat:
			case SkypeChat:
            case QQ:
            case WhatsAppChat:
            case WhatsAppMoc:
            case WhatsAppMtc:
            case SinaWeibo:
            case Facebook_Ott:
            case Instagram_Ott:
				testPlan = ((TaskMultipleAppTestModel) model).getTestPlanStr();
				break;
            case REBOOT:
                testPlan = ((TaskRebootModel) model).getTestPlanStr();
                break;
            case OpenSignal:
                testPlan = ((TaskOpenSignalModel) model).getTestPlanStr();
                break;
            case MultiHttpDownload:
                testPlan = ((TaskMultiHttpDownModel) model).getTestPlanStr();
                break;
            default:
                break;
        }

        return testPlan;
    }

    private String getTestPlanName(TaskModel model) {
        WalkStruct.TaskType taskType = WalkStruct.TaskType.valueOf(model.getTaskType());
        String taskName = "";
        switch (taskType) {
            case EmptyTask:
                taskName = context.getString(R.string.path_idle);
                break;
            case InitiativeCall:
                taskName = AppVersionControl.getInstance().isTelecomInspection()|| AppVersionControl.getInstance().isUnicomGroup()? "MO" : context.getString(R.string.path_dial);
                break;
            case PassivityCall:
                taskName = AppVersionControl.getInstance().isTelecomInspection()|| AppVersionControl.getInstance().isUnicomGroup() ? "MT" : context.getString(R.string.path_dial);
                break;
            case Ping:
                taskName = context.getString(R.string.path_ping);
                break;
            case Attach:
                taskName = context.getString(R.string.path_attach);
                break;
            case PDP:
                taskName = context.getString(R.string.path_pdp);
                break;
            case FTPUpload:
                taskName = context.getString(R.string.path_ftpup);
                break;
            case FTPDownload:
                taskName = context.getString(R.string.path_ftpdown);
                break;
            case Http:
                taskName = context.getString(R.string.path_http_login);
                break;
            case HttpRefurbish:
                taskName = context.getString(R.string.path_http_Refresh);
                break;
            case HttpDownload:
                taskName = context.getString(R.string.path_http_down);
                break;
            case EmailPop3:
                taskName = context.getString(R.string.path_email_receive);
                break;
            case EmailSmtp:
                taskName = context.getString(R.string.path_email_send);
                break;
            case EmailSmtpAndPOP:
                taskName = context.getString(R.string.path_email_sr);
                break;
            case SMSIncept:
                taskName = context.getString(R.string.path_sms);
                break;
            case SMSSend:
                taskName = context.getString(R.string.path_sms);
                break;
            case SMSSendReceive:
                taskName = context.getString(R.string.path_sms);
                break;
            case MMSIncept:
                taskName = context.getString(R.string.path_mms);
                break;
            case MMSSend:
                taskName = context.getString(R.string.path_mms);
                break;
            case MMSSendReceive:
                taskName = context.getString(R.string.path_mms);
                break;
            case WapLogin:
                taskName = context.getString(R.string.path_wap_login);
                break;
            case WapRefurbish:
                taskName = context.getString(R.string.path_wap_Refresh);
                break;
            case WapDownload:
                taskName = context.getString(R.string.path_wap_down);
                break;
            case WlanLogin:
                break;
            case Stream: //流媒体业务
                taskName = context.getString(R.string.path_vs);
                break;
            case DNSLookUp:
                taskName = context.getString(R.string.path_dnslookup);
                break;
            case SpeedTest:
                taskName = context.getString(R.string.path_speedtest);
                break;
            case VOIP:
                taskName = context.getString(R.string.path_voip);
                break;
            case HttpUpload:
                taskName = context.getString(R.string.path_http_upload);
                break;
            case MultiRAB:
                taskName = context.getString(R.string.path_multirab);
                break;
            case HTTPVS:
                taskName = context.getString(R.string.path_videoplay);
                break;
            case MultiftpUpload:
                taskName = context.getString(R.string.path_mFtpUp);
                break;
            case MultiftpDownload:
                taskName = context.getString(R.string.path_mFtpDown);
                break;
            case Facebook:
                taskName = context.getString(R.string.path_mFacebook);
                break;
            case TraceRoute:
                taskName = context.getString(R.string.path_mTraceRoute);
                break;
            case Iperf:
                taskName = context.getString(R.string.path_mIperf);
                break;
            case PBM:
                taskName = context.getString(R.string.path_pbm);
                break;
            case WeiBo:
                taskName = context.getString(R.string.path_weibo);
                break;
//            case WeChat:
//                taskName = context.getString(R.string.path_wechat);
//                break;
            case UDP:
                taskName = context.getString(R.string.path_udp);
                break;
            case REBOOT:
                taskName = context.getString(R.string.path_reboot);
                break;
            case OpenSignal:
                taskName = context.getString(R.string.path_opensignal);
                break;
            case MultiHttpDownload:
                taskName = context.getString(R.string.path_multihttpdownload);
                break;
            case WeChat:
			case WeCallMoc:
			case WeCallMtc:
			case SkypeChat:
            case QQ:
            case WhatsAppChat:
            case WhatsAppMoc:
            case WhatsAppMtc:
            case SinaWeibo:
            case Facebook_Ott:
            case Instagram_Ott:
				taskName = taskType.name();
				break;
            default:
                break;
        }

        return taskName;
    }

    /**
     * 生成测试计划文件名称
     *
     * @param models 测试任务
     * @return
     */
    public String getTestPlanFile(List<TaskModel> models) {
        String names = "";
        int nameCount = 0;
        for (int i = 0; i < models.size() && nameCount < 3; i++) {
            TaskModel model = models.get(i);
            String jobName = getTestPlanName(model);
            if (model.getEnable() == 1 && names.indexOf(jobName) < 0) {
                names += (nameCount != 0 ? "_" : "") + jobName;
                nameCount++;
            }
        }
        LogUtil.w(tag, "--name:" + names);
        return names;
    }

    public static String stopResultName = "TaskModelName";
    public static String stopResultState = "StopResultState";

    public static Map<String, String> getStopResultMap(TaskModel model) {
        Map<String, String> map = new HashMap<String, String>();
        if (model == null) {
            map.put(stopResultName, "Unknow");
        } else {
            map.put(stopResultName, model.getTaskName());
        }
        return map;
    }
}

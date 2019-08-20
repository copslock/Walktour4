package com.walktour.gui.task.parsedata.txt;

import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ConfigUrl;
import com.walktour.control.config.SpeedTestParamter.ServerInfo;
import com.walktour.control.config.SpeedTestSetting;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.attach.TaskAttachModel;
import com.walktour.gui.task.parsedata.model.task.dnslookup.TaskDNSLookUpModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
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
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskMultipleAppTestModel;
import com.walktour.gui.task.parsedata.model.task.ott.TaskWeCallModel;
import com.walktour.gui.task.parsedata.model.task.pbm.TaskPBMModel;
import com.walktour.gui.task.parsedata.model.task.pdpactive.TaskPdpModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.sms.send.TaskSmsSendModel;
import com.walktour.gui.task.parsedata.model.task.sms.sendreceive.TaskSmsSendReceiveModel;
import com.walktour.gui.task.parsedata.model.task.speedtest.TaskSpeedTestModel;
import com.walktour.gui.task.parsedata.model.task.traceroute.TaskTraceRouteModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.task.parsedata.model.task.videostreaming.TaskStreamModel;
import com.walktour.gui.task.parsedata.model.task.wap.TaskWapPageModel;
import com.walktour.gui.task.parsedata.model.task.wechat.TaskWeChatModel;
import com.walktour.model.FTPGroupModel;
import com.walktour.model.FtpServerModel;
import com.walktour.model.UrlModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 测试任务：包含测试计划文件中<TestTasks>以下的所有项
 */
public class TestTasks {
	private ArrayList<TaskModel> testTasks = new ArrayList<TaskModel>();
	private boolean isFromUMPC = false;
	//标识是否是下载
	private boolean isDownload = false;

	/**
	 * 构造函数
	 * 
	 * @param file
	 *          要读取的测试计划文件
	 * @param fromUMPC
	 *          是否从UMPC下发
	 */
	public TestTasks(File file, boolean fromUMPC) throws Exception {
		ArrayList<String> lines = new ArrayList<String>();
		isFromUMPC = fromUMPC;
		FileInputStream inStream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		String line;
		boolean isStart = false;
		while ((line = br.readLine()) != null) {
			if (isStart) {
				lines.add(line);
			}
			if (!isStart && line.equals("<TestTasks>")) {
				isStart = true;
			}
		}
		br.close();
		br = null;
		// 把lines拆分成多个测试任务
		ArrayList<TaskText> tasksText = new ArrayList<TaskText>();// 记录多个测试任务文本
		ArrayList<String> position = new ArrayList<String>();
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).startsWith("[")) {
				position.add(String.valueOf(i));
			}
		}
		position.add(String.valueOf(lines.size()));

		// 把各个测试任务（文本）添加到tasksText 列表中
		for (int i = 1; i < position.size(); i++) {
			int start = getInt(position.get(i - 1));
			int end = getInt(position.get(i));
			TaskText task = new TaskText();
			for (int j = start; j < end; j++) {
				task.add(lines.get(j));
			}
			tasksText.add(task);
		}

		// 把各个测试任务(文本)转为手机里的测试任务模型
		for (int i = 0; i < tasksText.size(); i++) {
			TaskText task = tasksText.get(i);// 添加一个任务:task的内容是一个任务的文本所有行
			for (int j = 0; j < task.size(); j++) {
				String lineText = task.get(j);

				// 确认任务类型后，把task传入createTaskFtpModel方法，
				// 生成测试任务的模型，把这个模型添加到testTasks列表中

				if (lineText.equals("UploadData=1")) {
					testTasks.add(createUploadTask(task));
					break;
				}

				else if (lineText.equals("FTPUpload=1")) {
					testTasks.add(createTaskFtpULModel(task));
					break;
				}

				else if (lineText.equals("FTPDownload=1")) {
					testTasks.add(createTaskFtpDLModel(task));
					break;
				}

				else if (lineText.equals("Ping=1")) {
					testTasks.add(createTaskPingModel(task));
					break;
				}
				// 主叫或者被叫测试
				else if (lineText.equalsIgnoreCase("call=1")) {
					testTasks.add(createTaskCallModel(task));
					break;
				}
				// 发送或者接收短信测试
				else if (lineText.equalsIgnoreCase("sms=1")) {
					if (isFromUMPC) {
						testTasks.add(createSmsModelFromUMPC(task));
					} else {
						testTasks.add(createSmsModel(task));
					}
					break;
				}
				// 发送或者接收彩信测试
				else if (lineText.equalsIgnoreCase("mms=1")) {
					testTasks.add(createMmsModel(task));
					break;
				}
				// http测试
				else if (lineText.equalsIgnoreCase("http=1") || lineText.equalsIgnoreCase("httpex=1")) {
					if (isFromUMPC) {
						testTasks.add(createHttpModelFromUMPC(task));
					} else {
						testTasks.add(createHttpModel(task));
					}
					break;
				}
				// wap测试
				else if (lineText.equalsIgnoreCase("wap=1")) {
					testTasks.add(createTaskWapModel(task));
					break;
				}

				else if (lineText.equalsIgnoreCase("kjava=1")) {
					testTasks.add(createTaskKJavaModel(task));
					break;
				}

				else if (lineText.equalsIgnoreCase("email=1")) {
					testTasks.add(createEmailModel(task));
					break;
				} else if (lineText.equalsIgnoreCase("attch=1")) {//
					testTasks.add(createAttachTask(task));
					break;
				} else if (lineText.equalsIgnoreCase("pdpactive=1")) {//
					testTasks.add(createPdpTask(task));
					break;
				} else if (lineText.startsWith("Idle=1")) {//
					testTasks.add(createIdleTask(task));
					break;
				} else if (lineText.startsWith("MultiTask=1")) {//
					testTasks.add(createTaskMultiRabModel(task));
					break;
				} else if (lineText.startsWith("VideoStream=1")) {// 流媒体转换
					testTasks.add(createTaskVideoStreamModel(task));
					break;
				} else if (lineText.startsWith("SpeedTest=1") || lineText.startsWith("Speedtest=1")) {// 流媒体转换
					if (!isFromUMPC) {
						testTasks.add(createTaskSpeedTestModel(task));
					} else {
						testTasks.add(createUmpcTaskSpeedTestModel(task));
					}
					break;
				} else if (lineText.startsWith("DnsLookUp=1") || lineText.startsWith("DNSLookup=1")) {
					if (!isFromUMPC) {
						testTasks.add(createDNSLookUpModel(task));
					} else {
						testTasks.add(createUmpcDNSLookUpModel(task));
					}
					break;
				} else if (lineText.startsWith("HttpVideoStream=1") || lineText.startsWith("VideoPlay=1")) {
					if (!isFromUMPC) {
						testTasks.add(createHttpVsModel(task));
					} else {
						testTasks.add(createUmpcHttpVsModel(task));
					}
					break;
				} else if (lineText.startsWith("HttpUpTest=1") || lineText.startsWith("httpUpload=1")) {
					if (!isFromUMPC) {
						testTasks.add(createTaskHttpUpload(task));
					} else {
						testTasks.add(createUmpcTaskHttpUpload(task));
					}
					break;
				} else if (lineText.equalsIgnoreCase("MFTPDownload=1")) {
					if (!isFromUMPC) {
						testTasks.add(createMultilFTPDownModel(task));
					} else {
						testTasks.add(createUmpcMultilFTPDownModel(task));
					}
				} else if (lineText.equalsIgnoreCase("MFTPUpload=1")) {
					if (!isFromUMPC) {
						testTasks.add(createMultilFTPUploadModel(task));
					} else {
						testTasks.add(createUmpcMultilFTPUploadModel(task));
					}
				} else if (lineText.equalsIgnoreCase("FacebookTest=1")) {
					if (!isFromUMPC) {
						testTasks.add(createFaceBookModel(task));
					} else {
						testTasks.add(createUmpcFaceBookModel(task));
					}
				} else if (lineText.equalsIgnoreCase("IdleTest=1")) {
					if (!isFromUMPC) {
						testTasks.add(createTaskIdleModel(task));
					} else {
						break;
					}
				} else if (lineText.equalsIgnoreCase("TraceRoute=1") || lineText.equalsIgnoreCase("Tracert=1")) {
					if (isFromUMPC) {
						testTasks.add(createUmpcTraceRouteModel(task));
					} else {
						testTasks.add(createTraceRouteModel(task));
					}
					break;
				} else if (lineText.equalsIgnoreCase("Iperf=1")) {
					if (isFromUMPC) {
						break;
					}
					testTasks.add(createIperfModel(task));
					break;
				} else if (lineText.equals("PBM=1")) {
					if (isFromUMPC) {
						testTasks.add(createPbmIpadModel(task));
					} else {
						testTasks.add(createPbmModel(task));
					}
					break;
				} else if (lineText.equals("WeChat=1")){
					testTasks.add(createWeChatModel(task));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.WeChat.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.WeChat));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.WeCallMoc.getXmlTaskType()))){
					testTasks.add(createWxCallModel(task,WalkStruct.TaskType.WeCallMoc));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.WeCallMtc.getXmlTaskType()))){
					testTasks.add(createWxCallModel(task,WalkStruct.TaskType.WeCallMtc));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.SkypeChat.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.SkypeChat));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.QQ.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.QQ));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.SinaWeibo.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.SinaWeibo));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.WhatsAppChat.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.WhatsAppChat));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.WhatsAppMoc.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.WhatsAppMoc));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.WhatsAppMtc.getXmlTaskType()))){
					testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.WhatsAppMtc));
				}else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.Facebook_Ott.name()))){
                    testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.Facebook_Ott));
                }else if (lineText.equals(String.format("%s=1",WalkStruct.TaskType.Instagram_Ott.name()))){
                    testTasks.add(createMultipleAppTestModel(task,WalkStruct.TaskType.Instagram_Ott));
                }
				
			}
		}
	}

	public ArrayList<TaskModel> getTestTasks() {
		return this.testTasks;
	}

	/**
	 * 返回任务列表中的一个测试模型，由任务标签名指定返回
	 */
	public TaskModel getTaskModelByTag(String tagName) {
		ArrayList<TaskModel> modelList = getTestTasks();
		if (modelList.size() == 0) {
			return null;
		}
		for (int i = 0; i < modelList.size(); i++) {
			TaskModel model = modelList.get(i);
			if (model.getTag().equals(tagName)) {
				return model;
			}
		}
		return null;
	}

	/**
	 * 取等式line右边的值, 如line是FTPHost=61.143.60.84时返回61.143.60.84
	 */
	private String getValue(String line) {
		try {
			return line.substring(line.indexOf("=") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.w("TestTask", "---download task has error:" + line);
			return "0";
		}
	}

	/**
	 * 一个测试任务文本：记录文件中一个测试任务的所有行
	 */
	private class TaskText extends ArrayList<String> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1391283480934566226L;

	 
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :短信测试
	 */
	private TaskModel createUploadTask(TaskText taskText) {
		TaskModel task = new TaskModel();

		// 默认参数：
		task.setTag(taskText.get(0));
		task.setTaskName(taskText.get(0) + "." + "UploadFile");// 文件中的任务名作为前缀
		task.setTaskType(WalkStruct.TaskType.WalktourUpload.name());
		task.setEnable(1);
		task.setRepeat(0);// 重复次数在TestPlan中设定

		return task;
	}

	private TaskModel createIdleTask(TaskText taskText) {
		TaskEmptyModel task = new TaskEmptyModel();
		// 默认参数：
		task.setTag(taskText.get(0));
		task.setTaskName(taskText.get(0) + "." + "Idle");// 文件中的任务名作为前缀
		task.setTaskType(WalkStruct.TaskType.EmptyTask.toString());
		task.setEnable(1);
		task.setRepeat(0);// 重复次数在TestPlan中设定

		// 其它参数
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("IdleTime")) {
				task.getIdleTestConfig().setKeepTime(getInt(getValue(line)));
			}
			if (line.startsWith("MultiTest")) {
				task.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				task.setRabName(getValue(line));
			}
		}

		return task;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Attach测试
	 */
	private TaskModel createAttachTask(TaskText taskText) {
		TaskAttachModel task = new TaskAttachModel();

		// 默认参数：
		task.setTag(taskText.get(0));
		task.setTaskName(taskText.get(0) + "." + "Attach");// 文件中的任务名作为前缀
		task.setTaskType(WalkStruct.TaskType.Attach.toString());
		task.setEnable(1);
		task.setRepeat(0);// 重复次数在TestPlan中设定

		// 其它参数
		if (isFromUMPC) {
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("AttachInterval=")) {
					try {
						task.setInterVal(getInt(getValue(line))); /* 间隔 */
					} catch (Exception e) {
						task.setInterVal(30); /* 间隔 */
					}
				} else if (line.startsWith("AttachTimeout=")) {
					try {
						task.getAttachTestConfig().setKeepTime(getInt(getValue(line))); /* 超时 */
					} catch (Exception e) {
						task.getAttachTestConfig().setKeepTime(30);
					}
				} else if (line.startsWith("nettype=")) {
					task.setNettype(getInt(getValue(line)));
				} else if (line.startsWith("MultiTest=")) {
					task.setIsRab(getInt(getValue(line)));
				} else if (line.startsWith("MultiName=")) {
					task.setRabName(getValue(line));
				}
			}
		} else {
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("AttachInterval=")) {
					try {
						task.setInterVal(getInt(getValue(line)) / 1000); /* 间隔 */
					} catch (Exception e) {
						task.setInterVal(30); /* 间隔 */
					}
				} else if (line.startsWith("AttachTimeout=")) {
					try {
						task.getAttachTestConfig().setKeepTime(getInt(getValue(line)) / 1000); /* 超时 */
					} catch (Exception e) {
						task.getAttachTestConfig().setKeepTime(30);
					}
				} else if (line.startsWith("nettype=")) {
					task.setNettype(getInt(getValue(line)));
				}
			}
		}

		return task;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :PDP测试
	 */
	private TaskModel createPdpTask(TaskText taskText) {
		TaskPdpModel task = new TaskPdpModel();

		// 默认参数：
		task.setTag(taskText.get(0));
		task.setTaskName(taskText.get(0) + "." + "PDP");// 文件中的任务名作为前缀
		task.setTaskType(WalkStruct.TaskType.PDP.toString());
		task.setEnable(1);
		task.setRepeat(0);// 重复次数在TestPlan中设定
		// 其它参数
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("PDPInterval=")) {
				try {
					task.setInterVal(getInt(getValue(line)) / 1000); /* 间隔 */
				} catch (Exception e) {
					task.setInterVal(30); /* 间隔 */
				}
			} else if (line.startsWith("PDPTimeout=")) {
				try {
					task.setKeepTime(getInt(getValue(line)) / 1000); /* 超时 */
				} catch (Exception e) {
					task.setKeepTime(30); /* 超时 */
				}
			} else if (line.startsWith("nettype=")) {
				task.setNettype(getInt(getValue(line)));
			} else if (line.startsWith("MultiTest=")) {
				task.setIsRab(getInt(getValue(line)));
			} else if (line.startsWith("MultiName=")) {
				task.setRabName(getValue(line));
			}

		}
		return task;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :短信测试
	 */
	private TaskModel createSmsModel(TaskText taskText) {
		/** 判断测试类型：0.自发自收 1.发送 2.接收 */
		int type = 0;
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("SMSTestType")) {
				type = getInt(getValue(line));
				break;
			}
		}

		switch (type) {
		// 自发自收
		case 0:
			TaskSmsSendReceiveModel srModel = new TaskSmsSendReceiveModel();
			// 默认参数：
			srModel.setTag(taskText.get(0));
			srModel.setTaskName(taskText.get(0) + "." + "SMSSendReceive");// 文件中的任务名作为前缀
			srModel.setTaskType(WalkStruct.TaskType.SMSSendReceive.name());
			srModel.setEnable(1);
			srModel.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					srModel.setNettype(getInt(getValue(line)));
				}
				if (line.startsWith("SMSText0=")) {
					srModel.setContent(getValue(line)); /* 短信内容 */
				}
				if (line.startsWith("SMSRecvNum0=")) { /* 接收号码(本机号码) */
					srModel.setDesNumber(getValue(line));
				}
				if (line.startsWith("SMSIntVal=")) { /* 间隔(服务器默认设为0) */
					try {
						srModel.setInterVal(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setInterVal(30);
					}
				}
				if (line.startsWith("SMSServer=")) { /* 服务中心号码(CDMA无效) */
					srModel.setServerNumber(getValue(line));
				}
				if (line.startsWith("SMSTimeout=")) { /* 发送超时 */
					try {
						srModel.setSendTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setSendTimeOut(30);
					}
				}
				if (line.startsWith("SMSReportTimeout=")) { /* 接收超时 */
					try {
						srModel.setReceiveTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setReceiveTimeOut(60);
					}
				}
				if (line.startsWith("MultiTest=")) {
					srModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					srModel.setRabName(getValue(line));
				}
			}
			return srModel;

		// 发送
		case 1:
			TaskSmsSendModel smsendModel = new TaskSmsSendModel();
			// 默认参数：
			smsendModel.setTag(taskText.get(0));
			smsendModel.setTaskName(taskText.get(0) + "." + "SMSSend");// 文件中的任务名作为前缀
			smsendModel.setTaskType(WalkStruct.TaskType.SMSSend.name());
			smsendModel.setEnable(1);
			smsendModel.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					smsendModel.setNettype(getInt(getValue(line)));
				}
				if (line.startsWith("SMSText0=")) {
					smsendModel.setContent(getValue(line));
				}
				if (line.startsWith("SMSRecvNum0=")) {
					smsendModel.setDesNumber(getValue(line));
				}
				if (line.startsWith("SMSIntVal=")) {
					try {
						smsendModel.setInterVal(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						smsendModel.setInterVal(30);
					}
				}
				if (line.startsWith("SMSServer=")) {
					smsendModel.setServerNumber(getValue(line));
				}
				if (line.startsWith("SMSTimeout=")) {
					try {
						smsendModel.setTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						smsendModel.setTimeOut(60);
					}
				}
				if (line.startsWith("MultiTest=")) {
					smsendModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					smsendModel.setRabName(getValue(line));
				}
			}
			return smsendModel;

		// 接收
		case 2:
			TaskSmsReceiveModel model = new TaskSmsReceiveModel();
			// 默认参数：
			model.setTag(taskText.get(0));
			model.setTaskName(taskText.get(0) + "." + "SMSReceive");// 文件中的任务名作为前缀
			model.setTaskType(WalkStruct.TaskType.SMSIncept.name());
			model.setEnable(1);
			model.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			model.setInterVal(3); /* 接收短信和被叫一样，时间间隔是3 */
//			model.setSaveSms(1); /* 定为保存 */
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					model.setNettype(getInt(getValue(line)));
				}
				if (line.startsWith("SMSReportTimeout=")) {
					try {
						model.setTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						model.setTimeOut(60);
					}
				}
				if (line.startsWith("MultiTest=")) {
					model.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					model.setRabName(getValue(line));
				}

			}
			return model;

		default:
			return null;
		}
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :短信测试
	 */
	private TaskModel createSmsModelFromUMPC(TaskText taskText) {
		/** 判断测试类型：0.自发自收 1.发送 2.接收 */
		int type = 0;
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("SMSTestType")) {
				type = getInt(getValue(line));
				break;
			}
		}

		switch (type) {
		// 自发自收
		case 2:
			TaskSmsSendReceiveModel srModel = new TaskSmsSendReceiveModel();
			// 默认参数：
			srModel.setTag(taskText.get(0));
			srModel.setTaskName(taskText.get(0) + "." + "SMSSendReceive");// 文件中的任务名作为前缀
			srModel.setTaskType(WalkStruct.TaskType.SMSSendReceive.name());
			srModel.setEnable(1);
			srModel.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					srModel.setNettype(getInt(getValue(line)));
				}
				if (line.startsWith("SMSText0=")) {
					srModel.setContent(getValue(line)); /* 短信内容 */
				}
				if (line.startsWith("SMSRecvNum0=")) { /* 接收号码(本机号码) */
					srModel.setDesNumber(getValue(line));
				}
				if (line.startsWith("SMSIntVal=")) { /* 间隔(服务器默认设为0) */
					try {
						srModel.setInterVal(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setInterVal(15);
					}
				}
				if (line.startsWith("SMSServer=")) { /* 服务中心号码(CDMA无效) */
					srModel.setServerNumber(getValue(line));
				}
				if (line.startsWith("SMSTimeout=")) { /* 发送超时 */
					try {
						srModel.setSendTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setSendTimeOut(30);
					}
				}
				if (line.startsWith("SMSReportTimeout=")) { /* 接收超时 */
					try {
						srModel.setReceiveTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setReceiveTimeOut(120);
					}
				}
				if (line.startsWith("MultiTest=")) {
					srModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					srModel.setRabName(getValue(line));
				}
			}
			return srModel;

		// 发送
		case 0:
			TaskSmsSendModel smsendModel = new TaskSmsSendModel();
			// 默认参数：
			smsendModel.setTag(taskText.get(0));
			smsendModel.setTaskName(taskText.get(0) + "." + "SMSSend");// 文件中的任务名作为前缀
			smsendModel.setTaskType(WalkStruct.TaskType.SMSSend.name());
			smsendModel.setEnable(1);
			smsendModel.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					smsendModel.setNettype(getInt(getValue(line)));
				}
				if (line.startsWith("SMSText0=")) {
					smsendModel.setContent(getValue(line));
				}
				if (line.startsWith("SMSRecvNum0=")) {
					smsendModel.setDesNumber(getValue(line));
				}
				if (line.startsWith("SMSIntVal=")) {
					try {
						smsendModel.setInterVal(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						smsendModel.setInterVal(15);
					}
				}
				if (line.startsWith("SMSServer=")) {
					smsendModel.setServerNumber(getValue(line));
				}
				if (line.startsWith("SMSTimeout=")) {
					try {
						smsendModel.setTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						smsendModel.setTimeOut(60);
					}
				}
				if (line.startsWith("MultiTest=")) {
					smsendModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					smsendModel.setRabName(getValue(line));
				}

			}
			return smsendModel;

		// 接收
		case 1:
			TaskSmsReceiveModel model = new TaskSmsReceiveModel();
			// 默认参数：
			model.setTag(taskText.get(0));
			model.setTaskName(taskText.get(0) + "." + "SMSReceive");// 文件中的任务名作为前缀
			model.setTaskType(WalkStruct.TaskType.SMSIncept.name());
			model.setEnable(1);
			model.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			model.setInterVal(3); /* 接收短信和被叫一样，时间间隔是3 */
//			model.setSaveSms(1); /* 定为保存 */
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					model.setNettype(getInt(getValue(line)));
				}
				if (line.startsWith("SMSReportTimeout=")) {
					try {
						model.setTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						model.setTimeOut(120);
					}
				}
				if (line.startsWith("MultiTest=")) {
					model.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					model.setRabName(getValue(line));
				}
			}
			return model;

		default:
			return null;
		}
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :彩信测试
	 */
	private TaskModel createMmsModel(TaskText taskText) {
		/** 判断测试类型：0.自发自收 1.发送 2.接收 */
		int type = 0;
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("MMSTestType")) {
				type = getInt(getValue(line));
				break;
			}
		}

		switch (type) {
		// 自发自收
		case 0:
			TaskMmsSendReceiveModel srModel = new TaskMmsSendReceiveModel();
			// 默认参数：
			srModel.setTag(taskText.get(0));
			srModel.setTaskName(taskText.get(0) + "." + "MMSSendReceive");// 文件中的任务名作为前缀
			srModel.setTaskType(WalkStruct.TaskType.MMSSendReceive.name());
			srModel.setEnable(1);
			srModel.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					srModel.setNettype(getInt(getValue(line)));
				}
				/* 彩信中心地址 */
				if (line.startsWith("MMSCenter=")) {
					String address = getValue(line);
					if (!address.startsWith("http://")) {
						address = "http://" + address;
					}
					srModel.setServerAddress(address);
				}
				/* 网关 */
				if (line.startsWith("MMSGateway=")) {
					srModel.setGateway(getValue(line));
				}
				/* 端口 */
				if (line.startsWith("MMSPort=")) {
					srModel.setPort(getInt(getValue(line)));
				}
				/* 彩信大小,平台大小 为B，这里转为KB */
				if (line.startsWith("MMSSize=")) {
					int size = 20;
					try {
						size = getInt(getValue(line)) / 1000;
					} catch (Exception e) {
						size = 30;
					}
					if (size < 1) {
						size = 0;
					}
					if (size >= 300) {
						size = 300;
					}
					srModel.setFileSize(size);
				}
				/* 间隔时长 */
				if (line.startsWith("MMSInterval=")) {
					try {
						srModel.setInterVal(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setInterVal(15);
					}
				}
				/* 发送超时 */
				if (line.startsWith("MMSTimeout=") || line.startsWith("MMSTimeOut=")) {// UMPC中是MMSTimeOut
					try {
						srModel.setSendTimeout(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setSendTimeout(60);
					}
				}
				/* 发送状态报告超时 */
				if (line.toLowerCase(Locale.getDefault()).startsWith("mmsreporttimeout=")) {
					try {
						srModel.setPushTimeout(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setPushTimeout(60);
					}
				}
				/* 接收彩信号码 */
				if (line.startsWith("MMSRecvNum0=")) {
					srModel.setDestination(getValue(line));
				}

				// UMPC中才有该字段
				if (line.startsWith("MMSPushTimeout=")) {
					try {
						srModel.setPushTimeout(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setPushTimeout(60);
					}
				}
				if (line.startsWith("MMSRecvTimeout=")) {
					try {
						srModel.setReceiveTimeout(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						srModel.setReceiveTimeout(120);
					}
				}

				if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
					try {
						srModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
					} catch (Exception e) {
						srModel.setDisConnect(0);
					}
				}
				// 平台拨号策略
				if (line.startsWith("PDPDeActiveEveryMMS=")) {
					srModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
				}
				if (line.startsWith("MultiTest=")) {
					srModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					srModel.setRabName(getValue(line));
				}

			} // end for

			// 未知接收超时、PUSH超时
			if (srModel.getPushTimeout() <= 3) {
				srModel.setPushTimeout(120);
			}

			if (srModel.getReceiveTimeout() <= 3) {
				srModel.setReceiveTimeout(60);
			}
			return srModel;

		// 发送
		case 1:
			TaskMmsSendModel mmsSendModel = new TaskMmsSendModel();
			// 默认参数：
			mmsSendModel.setTag(taskText.get(0));
			mmsSendModel.setTaskName(taskText.get(0) + "." + "MMSSend");// 文件中的任务名作为前缀
			mmsSendModel.setTaskType(WalkStruct.TaskType.MMSSend.name());
			mmsSendModel.setEnable(1);
			mmsSendModel.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					mmsSendModel.setNettype(getInt(getValue(line)));
				}
				/* 彩信中心地址 */
				if (line.startsWith("MMSCenter=")) {
					String address = getValue(line);
					if (!address.startsWith("http://")) {
						address = "http://" + address;
					}
					mmsSendModel.setServerAddress(address);
				}
				/* 网关 */
				if (line.startsWith("MMSGateway=")) {
					mmsSendModel.setGateway(getValue(line));
				}
				/* 端口 */
				if (line.startsWith("MMSPort=")) {
					try {
						mmsSendModel.setPort(getInt(getValue(line)));
					} catch (Exception e) {
						mmsSendModel.setPort(80);
					}
				}
				/* 彩信大小,平台大小 为B，这里转为KB */
				if (line.startsWith("MMSSize=")) {
					int size = 30;
					try {
						size = getInt(getValue(line)) / 1000;
					} catch (Exception e) {

					}
					if (size < 1) {
						size = 0;
					}
					if (size >= 300) {
						size = 300;
					}
					mmsSendModel.setMediaFileSize(size);
				}
				/* 间隔时长 */
				if (line.startsWith("MMSInterval=")) {
					try {
						mmsSendModel.setInterVal(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						mmsSendModel.setInterVal(15);
					}
					mmsSendModel.setInterVal(mmsSendModel.getInterVal() < 1 ? 3 : mmsSendModel.getInterVal());
				}
				/* 发送超时 */
				if (line.startsWith("MMSTimeout=") || line.startsWith("MMSTimeOut=")) {// UMPC中是MMSTimeOut
					try {
						mmsSendModel.setTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						mmsSendModel.setTimeOut(120);
					}
				}
				/* 发送状态报告超时 */
				if (line.toLowerCase(Locale.getDefault()).startsWith("mmsreporttimeout=")) {
					try {
						mmsSendModel.setReportTime(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						mmsSendModel.setReportTime(60);
					}
				}

				/* 接收彩信号码 */
				if (line.startsWith("MMSRecvNum0=")) {
					mmsSendModel.setDestination(getValue(line));
				}

				// 小背包下发的拨号策略
				if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
					try {
						mmsSendModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
					} catch (Exception e) {
						mmsSendModel.setDisConnect(0);
					}
				}
				// 平台拨号策略
				if (line.startsWith("PDPDeActiveEveryMMS=")) {
					mmsSendModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
				}
				if (line.startsWith("MultiTest=")) {
					mmsSendModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					mmsSendModel.setRabName(getValue(line));
				}

			}
			return mmsSendModel;

		// 接收
		case 2:
			TaskMmsReceiveModel model = new TaskMmsReceiveModel();
			// 默认参数：
			model.setTag(taskText.get(0));
			model.setTaskName(taskText.get(0) + "." + "MMSReceive");// 文件中的任务名作为前缀
			model.setTaskType(WalkStruct.TaskType.MMSIncept.name());
			model.setEnable(1);
			model.setRepeat(0);// 重复次数在TestPlan中设定
			// 其它参数
			model.setInterVal(3); /* 和被叫一样，强行把时间间隔设定为3秒 */

			for (int i = 0; i < taskText.size(); i++) {
				String line = taskText.get(i);
				if (line.startsWith("nettype=")) {
					model.setNettype(getInt(getValue(line)));
				}
				/* 彩信中心地址 */
				if (line.startsWith("MMSCenter=")) {
					String address = getValue(line);
					if (!address.startsWith("http://")) {
						address = "http://" + address;
					}
					model.setServerAddress(address);
				}
				/* 网关 */
				if (line.startsWith("MMSGateway=")) {
					model.setGateway(getValue(line));
				}
				/* 端口 */
				if (line.startsWith("MMSPort=")) {
					try {
						model.setPort(getInt(getValue(line)));
					} catch (Exception e) {
						model.setPort(80);
					}
				}
				/* 间隔时长 */
				/*
				 * if( line.startsWith( "MMSInterval=" ) ){ model.setInterVal( getInt(
				 * getValue( line ) ) / 1000 ); model.setInterVal( model.getInterVal()<1
				 * ? 3: model.getInterVal() ); }
				 */
				/* 发送超时 */
				if (line.startsWith("MMSTimeout=")) {
					model.setTimeOut(getInt(getValue(line)) / 1000);
				}
				/* 发送状态报告超时 */
				if (line.toLowerCase(Locale.getDefault()).startsWith("mmsreporttimeout=")) {
					try {
						model.setPushTimeOut(getInt(getValue(line)) / 1000);
					} catch (Exception e) {
						model.setPushTimeOut(60);
					}
				}
				// UMPC中才有该字段
				if (line.startsWith("MMSPushTimeout")) {
					model.setPushTimeOut(getInt(getValue(line)) / 1000);
				}
				// UMPC中才有该字段
				if (line.startsWith("MMSRecvTimeout=")) {
					model.setTimeOut(getInt(getValue(line)) / 1000);
				}

				// 小背包下发的拨号策略
				if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
					try {
						model.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
					} catch (Exception e) {
						model.setDisConnect(0);
					}
				}
				if (line.startsWith("PDPDeActiveEveryMMS=")) {
					model.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
				}
				if (line.startsWith("MultiTest=")) {
					model.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					model.setRabName(getValue(line));
				}
			}

			// PUSH超时未有解析
			if (model.getPushTimeOut() <= 3) {
				model.setPushTimeOut(60);
			}

			return model;

		default:
			return null;
		}
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Http测试
	 */
	private TaskModel createHttpModel(TaskText taskText) {
		TaskHttpPageModel httpModel = new TaskHttpPageModel(WalkStruct.TaskType.HttpRefurbish.name());
		// 默认参数：
		httpModel.setTag(taskText.get(0));
		httpModel.setTaskName(taskText.get(0) + "." + "HttpRefresh");// 文件中的任务名作为前缀
		httpModel.setTaskType(WalkStruct.TaskType.HttpRefurbish.name());
		httpModel.setEnable(1);
		httpModel.setRepeat(0);// 重复次数在TestPlan中设定
		// 其它参数
		ArrayList<UrlModel> urlModel = new ArrayList<UrlModel>();
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("nettype="))
				httpModel.setNettype(getInt(getValue(line)));

			/* 是否http下载 */
			else if (line.startsWith("HTTPType=1")) {
				httpModel.setTaskType(WalkStruct.TaskType.HttpDownload.name());
				httpModel.setTaskName(taskText.get(0) + "." + "HttpDownload");// 文件中的任务名作为前缀
			}
			/* URL */
			else if (line.startsWith("HTTPURL=")) {
				ConfigUrl urlList = new ConfigUrl();
				UrlModel uModel = new UrlModel();
				uModel.setEnable("0");
				uModel.setName(getValue(line).toLowerCase(Locale.getDefault()).startsWith("http://") ? getValue(line)
						: "http://" + getValue(line));
				if (!urlList.contains(uModel.getName())) {
					urlList.addUrl(uModel);
				}
				urlModel.add(uModel);
				httpModel.setXmlUrl(getValue(line)); // 这里设置url是供httpDownload
				httpModel.setUrlModelList(urlModel);
			} else if (line.startsWith("httpurl") && !line.startsWith("httpurlcount")) { // 解析url多网址与统一平台
				// fleet4对应
				ConfigUrl urlList = new ConfigUrl();
				UrlModel uModel1 = new UrlModel();
				uModel1.setEnable("0");
				uModel1.setName(getValue(line).toLowerCase(Locale.getDefault()).startsWith("http://") ? getValue(line)
						: "http://" + getValue(line));
				if (!urlList.contains(uModel1.getName())) {
					urlList.addUrl(uModel1);
				}
				urlModel.add(uModel1);
				httpModel.setUrlModelList(urlModel);

			}
			/* 间隔时长 */
			else if (line.startsWith("HTTPInterval=")) {
				httpModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			/* 超时 */
			// 2012.03.15 fleetcloud平台此属性单位为秒
			// 2012.11.19 fleetcloud平台又变成毫秒
			else if (line.startsWith("HTTPTimeout=")) {
				httpModel.setTimeOut(getInt(getValue(line)));
				httpModel.setTimeOut(httpModel.getTimeOut() > 1000 ? getInt(getValue(line)) / 1000 : getInt(getValue(line)));
			}
			/* 无响应时间 */
			else if (line.startsWith("HTTPNoRespTime=")) {
				httpModel.setReponse(getInt(getValue(line)));
			}
			/* 每次断开拨号 */
			else if (line.startsWith("PDPDeActiveEveryHTTP=")) {
				httpModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}

			// 小背包下发的拨号策略
			else if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				try {
					httpModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
				} catch (Exception e) {
					httpModel.setDisConnect(0);
				}
			} else if (line.startsWith("MultiTest=")) {
				httpModel.setIsRab(getInt(getValue(line)));
			} else if (line.startsWith("MultiName=")) {
				httpModel.setRabName(getValue(line));
			}
		} // end for
			// 如果无响应时长为0时获得超时时长+30S
		if (httpModel.getReponse() == 0)
			httpModel.setReponse(httpModel.getTimeOut());
		return httpModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Http测试
	 */
	private TaskModel createHttpModelFromUMPC(TaskText taskText) {

		TaskHttpPageModel httpModel = new TaskHttpPageModel(WalkStruct.TaskType.Http.name());
		// 默认参数：
		httpModel.setTag(taskText.get(0));
		httpModel.setEnable(1);
		httpModel.setRepeat(0);// 重复次数在TestPlan中设定
		// 其它参数
		
		// 注意:TestType=1, TestMode=0 登陆
		// 注意:TestType=1, TestMode=1 刷新
		// 注意:TestType=0, TestMode=1 下载
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("TestType")) {
				if (getInt(getValue(line)) == 0) {
					httpModel.setTaskType(WalkStruct.TaskType.HttpDownload.name());
					httpModel.setTaskName(taskText.get(0) + "." + "HttpDownload");
					isDownload = true;
				} else {
					isDownload = false;
				}
			}
			if (line.startsWith("TestMode") && !isDownload) {// http 登陆和刷新
				if (getInt(getValue(line)) == 0) {
					httpModel.setTaskType(WalkStruct.TaskType.Http.name());
					httpModel.setTaskName(taskText.get(0) + "." + "HttpLogon");
					httpModel.setHttpTestMode(0);
				} else {
					httpModel.setTaskType(WalkStruct.TaskType.HttpRefurbish.name());
					httpModel.setTaskName(taskText.get(0) + "." + "HttpRefresh");
					httpModel.setHttpTestMode(1);
				}
			}
		}
		
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("nettype=")){
				httpModel.setNettype(getInt(getValue(line)));
			}
			
			/* URL */
			if (line.startsWith("HTTPURL=")) {
				ConfigUrl urlList = new ConfigUrl();
				ArrayList<UrlModel> urlModel = new ArrayList<UrlModel>();
				UrlModel uModel = new UrlModel();
				uModel.setEnable("0");
				uModel.setName(getValue(line).toLowerCase(Locale.getDefault()).startsWith("http://") ? getValue(line)
						: "http://" + getValue(line));
				if (!urlList.contains(getValue(line))) {
					urlList.addUrl(uModel);
				}
				urlModel.add(uModel);
				httpModel.setUrlModelList(urlModel);
				httpModel.setXmlUrl(getValue(line));
			}
			/* 间隔时长 */
			if (line.startsWith("HTTPInterval=")) {
				httpModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			/* 超时 */
			// 2012.03.19 fleetcloud平台此属性单位为秒,小背包为毫秒
			if (line.startsWith("HTTPTimeout=")) {
				httpModel.setTimeOut(getInt(getValue(line)) / 1000);
			}
			/* 无响应时间 */
			if (line.startsWith("HTTPNoResponse=")) {
				httpModel.setReponse(getInt(getValue(line)) / 1000);
			}
			/* 登陆次数，暂时先作为任务的次数 */
			if (line.startsWith("HTTPLogonCount")) {
				httpModel.setLogonCount(getInt(getValue(line)));
			}
			
			if(line.startsWith("HTTPThreadNum")){
				httpModel.setThreadCount(getInt(getValue(line)));
			}
//			if (line.startsWith("TestType")) {
//				if (getInt(getValue(line)) == 0) {
//					httpModel.setTaskType(WalkStruct.TaskType.HttpDownload.name());
//					httpModel.setTaskName(taskText.get(0) + "." + "HttpDownload");
//					isDownload = true;
//				} else {
//					isDownload = false;
//				}
//			}
//			if (line.startsWith("TestMode") && !isDownload) {
//				if (getInt(getValue(line)) == 0) {
//					httpModel.setTaskType(WalkStruct.TaskType.Http.name());
//					httpModel.setTaskName(taskText.get(0) + "." + "HttpLogon");
//					httpModel.setHttpTestMode(0);
//				} else {
//					httpModel.setTaskName(taskText.get(0) + "." + "HttpRefresh");
//					httpModel.setTaskType(WalkStruct.TaskType.HttpRefurbish.name());
//					httpModel.setHttpTestMode(1);
//				}
//			}

			if (line.startsWith("HTTPLoadImg")) {
				httpModel.setDownPicture(getInt(getValue(line)) == 1);
			}

			/* 每次断开网络 */
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				httpModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}

			if (line.startsWith("ServerType=")) {
				httpModel.setServerType(getInt(getValue(line)));
			}
			if (line.startsWith("MultiTest=")) {
				httpModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				httpModel.setRabName(getValue(line));
			}

			if (line.startsWith("AccountType=")) {
				httpModel.setAccountType(getInt(getValue(line)));
			}
			if (line.startsWith("api_key=")) {
				httpModel.setAccountKey(getValue(line));
			}
			if (line.startsWith("secret_key=")) {
				httpModel.setSecretKey(getValue(line));
			}

		} // end for
			// 如果无响应时长为0时获得超时时长+30S
		if (httpModel.getReponse() == 0)
			httpModel.setReponse(httpModel.getTimeOut());
		return httpModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Ping
	 */
	private TaskPingModel createTaskPingModel(TaskText taskText) {
		TaskPingModel pingModel = new TaskPingModel();

		// 默认参数
		pingModel.setTag(taskText.get(0));
		pingModel.setTaskName(taskText.get(0) + "." + "Ping");// 文件中的任务名作为前缀
		pingModel.setTaskType(WalkStruct.TaskType.Ping.name());
		pingModel.setEnable(1);
		pingModel.setDisConnect(0);

		// 来自文件的参数
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("nettype="))
				pingModel.setNettype(getInt(getValue(line)));
			if (line.startsWith("PingTimes=")) {
				pingModel.setRepeat(getInt(getValue(line)));
			}
			if (line.startsWith("PingTimeout=")) {
				pingModel.setTimeOut(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PingPacketSize=")) {
				pingModel.setSize(getInt(getValue(line)));
			}
			if (line.startsWith("PingIP=")) {
				pingModel.setIp(getValue(line));
			}
			if (line.startsWith("PingInterval=")) {
				pingModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				pingModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest=")) {
				pingModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				pingModel.setRabName(getValue(line));
			}
			if (line.startsWith("PingTTL=")) {
				pingModel.setTtl(getInt(getValue(line)));
			}
			/* 每次断开拨号 */
			else if (line.startsWith("PDPDeActiveEveryPing=")) {
				pingModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
		}

		return pingModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :FTPULModel
	 */
	private TaskFtpModel createTaskFtpULModel(TaskText task) {
		TaskFtpModel ftpTask = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());// FTP上传和下载用的是同一模型，区分在setTaskType
		FtpServerModel ftp = new FtpServerModel();
		// 默认参数：
		ftpTask.setTag(task.get(0));
		ftp.setName(task.get(0));
		ftpTask.setFtpServer(task.get(0));
		ftpTask.setTaskName(task.get(0) + "." + "FTPUpload");// 文件中的任务名作为前缀
		ftpTask.setEnable(1);
		ftpTask.setDisConnect(0);
		ftpTask.setThreadNumber(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			// 来自文件的参数 ：要注意单位 的转换，文件中的单位和TaskModel中的单位不同
			if (line.startsWith("nettype="))
				ftpTask.setNettype(getInt(getValue(line)));

			if (line.startsWith("FTPUploadPath=")) {
				ftpTask.setRemoteFile(getValue(line));
			}
			if (line.startsWith("FTPUploadSize=")) {
				ftpTask.setFileSize(Long.parseLong(getValue(line)) / 1000);
				if (ftpTask.getFileSize() > 1048576000)
					ftpTask.setFileSize(1048576000); // 当上传大小大于1G时只传1G
				ftpTask.setFileSource(1); // 设置文件获取方式为自动创建文件
			}
			if (line.startsWith("FTPUploadTime=")) {
				ftpTask.setTimeOut(getInt(getValue(line)));
			}
			if (line.startsWith("PSCall=")) {
				ftpTask.setPsCall(getInt(getValue(line)));
			}
			if (line.startsWith("FTPULInterval=")) {
				ftpTask.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("FTPULThread=")) {
				ftpTask.setThreadNumber(getInt(getValue(line)));
			}
			if (line.startsWith("FTPULNoRespTime=")) {
				ftpTask.setNoAnswer(getInt(getValue(line)));
			}
			if (line.startsWith("FTPHost=")) {
				ftp.setIp(getValue(line));
			}
			if (line.startsWith("PDPDeActiveEveryFTPUL=")) {
				ftpTask.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("FTPPort=")) {
				ftpTask.setPort(getInt(getValue(line)));
				ftp.setPort(getValue(line));
			}
			// 是否匿名
			if (line.startsWith("FTPUser=")) {
				if (line.trim().endsWith("FTPUser=")) {
					ftp.setLoginUser("");
					ftp.setAnonymous(true);
				} else {
					ftpTask.setUser(getValue(line));
					ftp.setLoginUser(getValue(line));
				}
			}

			if (line.startsWith("FTPPass=")) {
				if (line.endsWith("FTPPass=")) {
					ftp.setLoginPassword("");
				} else {
					ftpTask.setPass(getValue(line));
					ftp.setLoginPassword(getValue(line));
				}
			}
			// 2013.8.26 该属性放到ftp服务器信息里 ( FtpServerModel )
			if (line.startsWith("FTPPassive=")) {
				ftp.setConnect_mode(
						getValue(line).equals("1") ? FtpServerModel.CONNECT_MODE_PASSIVE : FtpServerModel.CONNECT_MODE_PORT);
			}

			if (line.startsWith("LoginTimeout=")) {
				ftpTask.setLoginTimeOut(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("TryTimes=")) {
				ftpTask.setLoginTimes(getInt(getValue(line)));
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				ftpTask.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest=")) {
				ftpTask.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				ftpTask.setRabName(getValue(line));
			}
		}

		new ConfigFtp().addFtp(ftp);

		return ftpTask;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :TaskFtpDLModel
	 */
	private TaskFtpModel createTaskFtpDLModel(TaskText task) {
		TaskFtpModel ftpTask = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());// FTP上传和下载用的是同一模型，区分在setTaskType
		FtpServerModel ftp = new FtpServerModel();
		// 默认参数：
		ftpTask.setTag(task.get(0));
		ftp.setName(task.get(0));
		ftpTask.setFtpServer(task.get(0));
		ftpTask.setTaskName(task.get(0) + "." + "FTPDownload");// 文件中的任务名作为前缀
		ftpTask.setEnable(1);
		ftpTask.setDisConnect(0);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			// 来自文件的参数 ：要注意单位 的转换，文件中的单位和TaskModel中的单位不同
			if (line.startsWith("FTPDownloadFile=")) {
				ftpTask.setRemoteFile(getValue(line));
			}
			if (line.startsWith("FTPUploadSize=")) {
				ftpTask.setFileSize(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("FTPDownloadTime=")) {
				ftpTask.setTimeOut(getInt(getValue(line)));
			}
			if (line.startsWith("PSCall=")) {
				ftpTask.setPsCall(getInt(getValue(line)));
			}
			if (line.startsWith("FTPDLInterval=")) {
				ftpTask.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("FTPDLThread=")) {
				ftpTask.setThreadNumber(getInt(getValue(line)));
			}
			if (line.startsWith("FTPDLNoRespTime=")) {
				ftpTask.setNoAnswer(getInt(getValue(line)));
			}
			if (line.startsWith("FTPHost=")) {
				ftp.setIp(getValue(line));
			}
			if (line.startsWith("nettype="))
				ftpTask.setNettype(getInt(getValue(line)));

			if (line.startsWith("PDPDeActiveEveryFTPDL=")) {
				ftpTask.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}

			if (line.startsWith("FTPPort=")) {
				ftpTask.setPort(getInt(getValue(line)));
				ftp.setPort(getValue(line));
			}

			// 是否匿名
			if (line.startsWith("FTPUser=")) {
				if (line.trim().endsWith("FTPUser=")) {
					ftp.setLoginUser("");
					ftp.setAnonymous(true);
				} else {
					ftpTask.setUser(getValue(line));
					ftp.setLoginUser(getValue(line));
				}
			}

			if (line.startsWith("FTPPass=")) {
				if (line.endsWith("FTPPass=")) {
					ftp.setLoginPassword("");
				} else {
					ftpTask.setPass(getValue(line));
					ftp.setLoginPassword(getValue(line));
				}
			}
			// 2013.8.26 该属性放到ftp服务器信息里 ( FtpServerModel )
			if (line.startsWith("FTPPassive=")) {
				ftp.setConnect_mode(
						getValue(line).equals("1") ? FtpServerModel.CONNECT_MODE_PASSIVE : FtpServerModel.CONNECT_MODE_PORT);
			}

			if (line.startsWith("LoginTimeout=")) {
				ftpTask.setLoginTimeOut(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("TryTimes=")) {
				ftpTask.setLoginTimes(getInt(getValue(line)));
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				ftpTask.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest=")) {
				ftpTask.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				ftpTask.setRabName(getValue(line));
			}
		}

		ConfigFtp configFtp = new ConfigFtp();
		configFtp.addFtp(ftp);
		return ftpTask;

	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :TaskCallModel
	 */
	private TaskModel createTaskCallModel(TaskText task) {

		// 首先判断 是主叫还是被叫
		boolean isInitCall = false;
		boolean isUnionTest = false;
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("dialed_test")) {
				// 如果有dialed_test,且dialed_test=0时是主叫
				if (getValue(line).equals("0")) {
					isInitCall = true;
					break;
				} else if (getValue(line).equals("1")) {
					isInitCall = false;
					break;
				} else if (getValue(line).equals("2")) {
					isInitCall = false;
					break;
				}
			}

			if (line.startsWith("NetUnion=")) {
				if (getValue(line).equals("1")) {
					isUnionTest = true;
				}
			}
		}

		// 如果有拨打号码，则为主叫,否则是被叫
		if (isInitCall) {
			TaskInitiativeCallModel callModel = new TaskInitiativeCallModel();
			// 默认参数：
			callModel.setTag(task.get(0));
			callModel.setTaskName(task.get(0) + "." + "MoCall");// 文件中的任务名作为前缀
			callModel.setTaskType(WalkStruct.TaskType.InitiativeCall.name());
			callModel.setEnable(1);
			callModel.setUnitTest(isUnionTest);

			for (int i = 0; i < task.size(); i++) {
				String line = task.get(i);
				if (line.startsWith("nettype="))
					callModel.setNettype(getInt(getValue(line)));

				if (line.startsWith("dial_no="))
					callModel.setCallNumber(getValue(line));

				if (line.startsWith("dial_time="))
					callModel.setKeepTime(getInt(getValue(line)));

				if (line.startsWith("idle_time="))
					callModel.setInterVal(getInt(getValue(line)));

				if (line.startsWith("accmaxtime="))
					callModel.setConnectTime(getInt(getValue(line)));

				if (line.startsWith("voice="))
					callModel.setMosTest(getInt(getValue(line)));
				if (line.startsWith("MultiTest=")) {
					callModel.setIsRab(getInt(getValue(line)));
				}
				if (line.startsWith("MultiName=")) {
					callModel.setRabName(getValue(line));
				}
				if (line.startsWith("TestType=") && !isUnionTest) {
					callModel.setCallMOSTestType(getInt(getValue(line)));
				}
				if (line.startsWith("MosType=")) {
					callModel.setCallMOSCount(getInt(getValue(line)));
				}
				if (line.startsWith("POLQASample=")) {
					callModel.setPolqaSample(getInt(getValue(line)));
				}
				if (line.startsWith("POLQACalc=")) {
					callModel.setPolqaCalc(getInt(getValue(line)));
				}
			}

			return callModel;
		}// 否则是被叫
		// AudioSystem.setDeviceConnectionState(arg0, arg1, arg2);
		TaskPassivityCallModel psCallModel = new TaskPassivityCallModel();
		psCallModel.setTag(task.get(0));
		psCallModel.setTaskName(task.get(0) + "." + "MTCall");// 文件中的任务名作为前缀
		psCallModel.setTaskType(WalkStruct.TaskType.PassivityCall.name());
		psCallModel.setEnable(1);
		psCallModel.setInterVal(3);// 默认是3秒
		psCallModel.setCallMOSServer(1);// 是否进行MOS值评估
		psCallModel.setParallelData(0);// 是否并行数据业务
		psCallModel.setUnitTest(isUnionTest);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("nettype=")) {
				psCallModel.setNettype(getInt(getValue(line)));
				continue;
			}
			if (line.startsWith("voice=")) {
				psCallModel.setCallMOSServer(getInt(getValue(line)));
				continue;
			}
			if (line.startsWith("MultiTest=")) {
				psCallModel.setIsRab(getInt(getValue(line)));
				continue;
			}
			if (line.startsWith("MultiName=")) {
				psCallModel.setRabName(getValue(line));
				continue;
			}
			if (line.startsWith("MosType=")) {
				psCallModel.setCallMOSCount(getInt(getValue(line)));
				continue;
			}
			if (line.startsWith("POLQASample=")) {
				psCallModel.setPolqaSample(getInt(getValue(line)));
				continue;
			}
		}
		return psCallModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Wap登录或者刷新,由wapurlcount=?决定
	 */
	private TaskWapPageModel createTaskWapModel(TaskText task) {
		TaskWapPageModel wapModel = new TaskWapPageModel(WalkStruct.TaskType.WapLogin.name());

		// 默认参数
		wapModel.setTag(task.get(0));
		wapModel.setTaskName(task.get(0) + ".WapLogin");
		wapModel.setEnable(1);
		wapModel.setShowPage(0);
		wapModel.setTaskType(WalkStruct.TaskType.WapLogin.name());
		wapModel.setRepeat(1);

		// 来自文件的参数
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("nettype="))
				wapModel.setNettype(getInt(getValue(line)));

			if (line.startsWith("waphomepage="))
				wapModel.setUrl(getValue(line));

			if (line.startsWith("wapipaddr="))
				wapModel.setGateway(getValue(line));

			if (line.startsWith("wapport="))
				wapModel.setPort(getInt(getValue(line)));

			if (line.startsWith("waptimeout="))
				wapModel.setTimeOut(getInt(getValue(line)) / 1000);

			if (line.startsWith("wapinterval="))
				wapModel.setInterVal(getInt(getValue(line)) / 1000);

			if (isFromUMPC) { // pad端只有wapLogon字段，与其他终端判断不同
				if (line.startsWith("waplogon=")) {
					int wapType = getInt(getValue(line));
					if (wapType == 0) {
						wapModel.setTaskName(task.get(0) + ".WapRefresh");
						wapModel.setTaskType(WalkStruct.TaskType.WapRefurbish.name());
					}
				}
			}
			/*
			 * 判断页面登录还是页面刷新，事实上这两种测试都是把waphomepage字段作为登录页面区分是不是wap刷新，以
			 * waplinklevel>0为标准
			 */
			if (line.startsWith("waplinklevel=")) {
				int waplinklevel = getInt(getValue(line));
				if (waplinklevel > 0) {
					// 当waplinklevel>0时为刷新测试
					wapModel.setTaskType(WalkStruct.TaskType.WapRefurbish.name());
					wapModel.setTaskName(task.get(0) + ".WapRefresh");
					wapModel.setRefreshDepth(waplinklevel);// 刷新层数
					// 刷新次数,下载的测试计划当中没有包含刷新次数,这里默认设定为层数和次数一样。
					wapModel.setRefreshcount(waplinklevel);
				} else {
					// 获取下一行
					String lineNext = task.get(i + 1);
					int wapurlcount = 0;
					// 如果下一行是wapurlcount,再判断wapurlcount是否大于0,
					if (lineNext.startsWith("wapurlcount=")) {
						wapurlcount = getInt(getValue(lineNext));
					} else {
						wapurlcount = 0;
					}

					if (wapurlcount > 0) {
						// 大于0则把这个当成是刷新层数
						wapModel.setTaskType(WalkStruct.TaskType.WapRefurbish.name());
						wapModel.setTaskName(task.get(0) + ".WapRefresh");
						wapModel.setRefreshDepth(wapurlcount);// 刷新层数
						// 刷新次数,下载的测试计划当中没有包含刷新次数,这里默认设定为层数和次数一样。
						wapModel.setRefreshcount(wapurlcount);
					} else {
						// 当waplinklevel==0时是登录测试
						wapModel.setTaskType(WalkStruct.TaskType.WapLogin.name());
						wapModel.setTaskName(task.get(0) + ".WapLogin");
						wapModel.setRefreshDepth(0);// 刷新层数
						wapModel.setRefreshcount(0);// 刷新次数
					}

				}
			}
			// 平台的拨号属性
			if (line.startsWith("PDPDeActiveEveryWap=")) {
				wapModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			// 小背包的拨号属性
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				wapModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest=")) {
				wapModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				wapModel.setRabName(getValue(line));
			}
		}
		wapModel.setWapType(0);// Wap下载时才有效
		return wapModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Wap下载KJava
	 */
	private TaskWapPageModel createTaskKJavaModel(TaskText task) {
		TaskWapPageModel wapModel = new TaskWapPageModel(WalkStruct.TaskType.WapDownload.name());

		// 默认参数
		wapModel.setTag(task.get(0));
		wapModel.setTaskName(task.get(0) + ".WapDownload");
		wapModel.setTaskType(WalkStruct.TaskType.WapDownload.name());
		wapModel.setEnable(1);
		wapModel.setRepeat(1);
		wapModel.setWapType(2);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("nettype="))
				wapModel.setNettype(getInt(getValue(line)));

			if (line.startsWith("KJAVAURL=")) {
				wapModel.setUrl(getValue(line));
			}
			if (line.startsWith("KJAVAGateway=")) {
				wapModel.setGateway(getValue(line));
			}
			if (line.startsWith("KJAVAPort=")) {
				wapModel.setPort(getInt(getValue(line)));
			}
			if (line.startsWith("KJAVATimeout=")) {
				wapModel.setTimeOut(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("KJAVAInterval=")) {
				wapModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("KJAVAType=")) {
				wapModel.setWapType(getInt(getValue(line)));
			}

			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				wapModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest=")) {
				wapModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				wapModel.setRabName(getValue(line));
			}

			if (line.startsWith("PDPDeActiveEveryKJava=")) {
				wapModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
		}

		// Wap 下载 用不到的参数
		wapModel.setRefreshcount(1);
		wapModel.setRefreshDepth(1);
		wapModel.setShowPage(0);

		return wapModel;
	}

	/**
	 * 根据测试类型创建Email测试模型类
	 * 
	 * @param taskText
	 *          服务器下发的Email测试内容
	 * @return Email测试模型类
	 */
	private TaskModel createEmailModel(TaskText taskText) {
		// 判断测试类型：0.发送 1.接收 2.自发自收
		int type = 0;
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("EmailType=")) {
				type = getInt(getValue(line));
				break;
			}
		}

		switch (type) {
		case 0:
			TaskEmailSmtpModel smtpModel = new TaskEmailSmtpModel();
			// 默认参数
			smtpModel.setTag(taskText.get(0));
			smtpModel.setTaskName(taskText.get(0) + "." + "EmailSmtp");// 文件中的任务名作为前缀
			smtpModel.setTaskType(WalkStruct.TaskType.EmailSmtp.name());
			smtpModel.setEnable(1);
			smtpModel.setRepeat(0);// 重复次数在TestPlan中设定
			smtpModel.setFileSource(1);
			// smtpModel.setBody("");
			smtpModel.setAdjunct("null");
			// smtpModel.setTag(tag)
			for (String line : taskText) {
				String lineValue = getValue(line);
				if (line.startsWith("nettype=")) {
					smtpModel.setNettype(getInt(getValue(line)));
				} else if (line.startsWith("EmailFrom=")) {
					smtpModel.setAccount(lineValue);
				} else if (line.startsWith("EmailTo=")) {
					smtpModel.setTo(lineValue);
				} else if (line.startsWith("EmailSubject=")) {
					smtpModel.setSubject(lineValue);
				} else if (line.startsWith("EmailContent=")) {
					smtpModel.setBody(lineValue);
				} else if (line.startsWith("EmailSMTPIP=")) {
					smtpModel.setEmailServer(lineValue);
				} else if (line.startsWith("EmailSMTPPort=")) {
					smtpModel.setPort(getInt(lineValue));
				} else if (line.startsWith("SSL") || line.startsWith("smtpssl")
						|| line.startsWith("EmailSMTPSecurityProtocol")) {
					smtpModel.setUseSSL(getInt(lineValue));
				} else if (line.startsWith("EmailSMTPUser=")) {
					smtpModel.setAccount(lineValue);
				} else if (line.startsWith("EmailSMTPPass=")) {
					smtpModel.setPassword(lineValue);
				} else if (line.startsWith("SMTPAuth=")) {
					smtpModel.setSmtpAuthentication(getInt(lineValue));
				} else if (line.startsWith("EmailSize=")) {
					smtpModel.setFileSize(getInt(lineValue) / 1000);
				} else if (line.startsWith(isFromUMPC ? "EmailSMTPTimeOut=" : "EmailTimeOut=")) {// ipad与平台匹配的超时字段不同
					smtpModel.setTimeOut(getInt(lineValue));
					smtpModel.setTimeOut(smtpModel.getTimeOut() > 1000 ? smtpModel.getTimeOut() / 1000 : smtpModel.getTimeOut());
				} else if (line.startsWith("EmailInterval=")) {
					smtpModel.setInterVal(getInt(lineValue));
					smtpModel
							.setInterVal(smtpModel.getInterVal() > 1000 ? smtpModel.getInterVal() / 1000 : smtpModel.getInterVal());
				} else if (line.startsWith("PDPDeActiveEveryEmail=")) {
					smtpModel.setDisConnect(pppRuleFleetToDevice(getInt(lineValue)));
				} else if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
					smtpModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
				} else if (line.startsWith("MultiTest=")) {
					smtpModel.setIsRab(getInt(getValue(line)));
				} else if (line.startsWith("MultiName=")) {
					smtpModel.setRabName(getValue(line));
				}
			}
			return smtpModel;
		case 1:
			TaskEmailPop3Model pop3Model = new TaskEmailPop3Model();
			// 默认参数
			pop3Model.setTag(taskText.get(0));
			pop3Model.setTaskName(taskText.get(0) + "." + "EmailPop3");// 文件中的任务名作为前缀
			pop3Model.setTaskType(WalkStruct.TaskType.EmailPop3.name());
			pop3Model.setEnable(1);
			pop3Model.setRepeat(0);// 重复次数在TestPlan中设定

			for (String line : taskText) {
				String lineValue = getValue(line);
				if (line.startsWith("nettype=")) {
					pop3Model.setNettype(getInt(getValue(line)));
				} else if (line.startsWith("EmailPOP3IP=")) {
					pop3Model.setEmailServer(lineValue);
				} else if (line.startsWith("EmailPOP3Port=")) {
					pop3Model.setPort(getInt(lineValue));
				} else if (line.startsWith("SSL") || line.startsWith("pop3ssl")
						|| line.startsWith("EmailPOP3SecurityProtocol")) {
					pop3Model.setUseSSL(getInt(lineValue));
				} else if (line.startsWith("EmailPOP3User=")) {
					pop3Model.setAccount(lineValue);
				} else if (line.startsWith("EmailPOP3Pass=")) {
					pop3Model.setPassword(lineValue);
				} else if (line.startsWith(isFromUMPC ? "EmailPOP3TimeOut=" : "EmailTimeOut=")) {
					pop3Model.setTimeOut(getInt(lineValue));
					pop3Model.setTimeOut(pop3Model.getTimeOut() > 1000 ? pop3Model.getTimeOut() / 1000 : pop3Model.getTimeOut());
				} else if (line.startsWith("PDPDeActiveEveryEmail=")) {
					pop3Model.setDisConnect(pppRuleFleetToDevice(getInt(lineValue)));
				} else if (line.startsWith("EmailInterval=")) {
					pop3Model.setInterVal(getInt(lineValue));
					pop3Model
							.setInterVal(pop3Model.getInterVal() > 1000 ? pop3Model.getInterVal() / 1000 : pop3Model.getInterVal());
				} else if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
					pop3Model.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
				} else if (line.startsWith("MultiTest=")) {
					pop3Model.setIsRab(getInt(getValue(line)));
				} else if (line.startsWith("MultiName=")) {
					pop3Model.setRabName(getValue(line));
				}
			}

			return pop3Model;
		case 2:
			break;
		}
		return null;
	}

	/**
	 * 
	 * 根据测试类型创建stream测试模型类 --读取文本转换
	 * 
	 * @Title: createTaskVideoStreamModel @param @param
	 *         task文本函数 @param @return @return TaskStreamModel 实体类 @throws
	 */
	private TaskStreamModel createTaskVideoStreamModel(TaskText task) {
		TaskStreamModel taskStreamModel = new TaskStreamModel();

		taskStreamModel.setTag(task.get(0));
		taskStreamModel.setTaskName(task.get(0) + ".VideoStream");
		taskStreamModel.setTaskType(WalkStruct.TaskType.Stream.name());
		taskStreamModel.setEnable(1);
		taskStreamModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("nettype")) {
				taskStreamModel.setNettype(getInt(getValue(line)));
			}
			if (line.startsWith("VideoURL")) {
				taskStreamModel.setmURL(getValue(line));
			}
			if (line.startsWith("VideoProtocol")) {
				taskStreamModel.setmUseTCP(getValue(line).equals("0"));
			}
			if (line.startsWith("VideoInterval")) {
				taskStreamModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("VideoTimeout")) {
				try {
					taskStreamModel.setmPlayTime(String.valueOf(getInt(getValue(line)) / 1000));
				} catch (Exception e) {
					taskStreamModel.setmPlayTime(taskStreamModel.getmPlayTime());
				}
			}
			if (line.startsWith("NoResponse")) {
				try {
					taskStreamModel.setmNodataTimeout(String.valueOf(getInt(getValue(line)) / 1000));
				} catch (Exception e) {
					taskStreamModel.setmNodataTimeout(taskStreamModel.getmNodataTimeout());
				}
			}
			if (line.startsWith("Protocol")) {
				taskStreamModel.setmUseTCP(getInt(getValue(line)) == 1);
			}
			if (line.startsWith("VideoPsCall")) {
				taskStreamModel.setPsCall(getInt(getValue(line)) != 1);
			}

			if (isFromUMPC) {
				if (line.startsWith("PlayMode")) {
					taskStreamModel.setPsCall(!(getInt(getValue(line)) == 0));
				}
			} else {
				if (line.startsWith("PlayMode")) {
					taskStreamModel.setPsCall(getInt(getValue(line)) == 0);
				}
			}

			if (line.startsWith("PlayTime")) {
				try {
					taskStreamModel.setmPlayTime(String.valueOf(getInt(getValue(line)) / 1000));
				} catch (Exception e) {
					taskStreamModel.setmPlayTime(taskStreamModel.getmPlayTime());
				}
			}
			if (line.startsWith("CacheSize")) {
				taskStreamModel.setCacheSize(getValue(line));
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")) {
				taskStreamModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("PDPDeActiveEveryVideo")) {
				taskStreamModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest")) {
				taskStreamModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				taskStreamModel.setRabName(getValue(line));
			}
		}
		return taskStreamModel;
	}

	/**
	 * 
	 * 根据测试类型创建SpeedTest
	 * 
	 * @Title: createTaskVideoStreamModel @param @param
	 *         task文本函数 @param @return @return TaskStreamModel 实体类 @throws
	 */
	private TaskSpeedTestModel createTaskSpeedTestModel(TaskText task) {
		TaskSpeedTestModel taskSpeedTestModel = new TaskSpeedTestModel();

		taskSpeedTestModel.setTag(task.get(0));
		taskSpeedTestModel.setTaskName(task.get(0) + ".SpeedTest");
		taskSpeedTestModel.setTaskType(WalkStruct.TaskType.SpeedTest.name());
		taskSpeedTestModel.setEnable(1);
		taskSpeedTestModel.setRepeat(1);
		SpeedTestSetting servers = SpeedTestSetting.getInstance(); // 获得speedservers解析工具类

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("SpeedServer=")) {
				if (getValue(line).length() != 0) {
					String urlStr[] = getValue(line).split("\\s");
					taskSpeedTestModel.setUrl(urlStr[0]);
				}
				ServerInfo serverInfo = servers.getByUrl(taskSpeedTestModel.getUrl());
				if (serverInfo != null) { // 通过url查询出"国家"、"城市"，插入到model
					taskSpeedTestModel.setCountry(serverInfo.getParent().getCountry());
					taskSpeedTestModel.setName(serverInfo.getSponsor());
				}
			}
			if (line.startsWith("SpeedInterval")) {
				taskSpeedTestModel.setInterVal(getInt(getValue(line)));
				taskSpeedTestModel.setInterVal(
						taskSpeedTestModel.getInterVal() > 1000 ? getInt(getValue(line)) / 1000 : Integer.parseInt(getValue(line)));

			}
			if (line.startsWith("PDPDeActiveEverySpeed")) {
				taskSpeedTestModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest")) {
				taskSpeedTestModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				taskSpeedTestModel.setRabName(getValue(line));
			}
		}
		return taskSpeedTestModel;
	}

	/**
	 * 
	 * 根据测试类型创建SpeedTest
	 * 
	 * @Title: createTaskVideoStreamModel @param @param
	 *         task文本函数 @param @return @return TaskStreamModel 实体类 @throws
	 */
	private TaskSpeedTestModel createUmpcTaskSpeedTestModel(TaskText task) {
		TaskSpeedTestModel taskSpeedTestModel = new TaskSpeedTestModel();

		taskSpeedTestModel.setTag(task.get(0));
		taskSpeedTestModel.setTaskName(task.get(0) + ".SpeedTest");
		taskSpeedTestModel.setTaskType(WalkStruct.TaskType.SpeedTest.name());
		taskSpeedTestModel.setEnable(1);
		taskSpeedTestModel.setRepeat(1);
		SpeedTestSetting servers = SpeedTestSetting.getInstance(); // 获得speedservers解析工具类

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("Url=")) {
				if (getValue(line).length() != 0) {
					taskSpeedTestModel.setUrl(getValue(line));
				}
				ServerInfo serverInfo = servers.getByUrl(taskSpeedTestModel.getUrl());
				if (serverInfo != null) { // 通过url查询出"国家"、"城市"，插入到model
					taskSpeedTestModel.setCountry(serverInfo.getParent().getCountry());
					taskSpeedTestModel.setName(serverInfo.getSponsor());
				}
			}
			if (line.startsWith("Interval=")) {
				taskSpeedTestModel.setInterVal(getInt(getValue(line)));
				taskSpeedTestModel.setInterVal(
						taskSpeedTestModel.getInterVal() > 1000 ? getInt(getValue(line)) / 1000 : Integer.parseInt(getValue(line)));

			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")) {
				taskSpeedTestModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest")) {
				taskSpeedTestModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				taskSpeedTestModel.setRabName(getValue(line));
			}
		}
		return taskSpeedTestModel;
	}

	/**
	 * 
	 * 根据测试类型创建DNSLookUpModel
	 * 
	 * @Title: createTaskVideoStreamModel @param @param
	 *         task文本函数 @param @return @return DNSLookUpModel 实体类 @throws
	 */
	private TaskDNSLookUpModel createDNSLookUpModel(TaskText task) {
		TaskDNSLookUpModel dnsLookUpModel = new TaskDNSLookUpModel();

		dnsLookUpModel.setTag(task.get(0));
		dnsLookUpModel.setTaskName(task.get(0) + ".DNSLookUp");
		dnsLookUpModel.setTaskType(WalkStruct.TaskType.DNSLookUp.name());
		dnsLookUpModel.setEnable(1);
		dnsLookUpModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("DnsHost=")) {
				dnsLookUpModel.getDnsTestConfig().setUrl(getValue(line));
			}
			if (line.startsWith("DnsTimeout")) {
				dnsLookUpModel.getDnsTestConfig().setTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("DnsInterval")) {
				dnsLookUpModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PDPDeActiveEveryDns")) {
				dnsLookUpModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest")) {
				dnsLookUpModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				dnsLookUpModel.setRabName(getValue(line));
			}
		}
		return dnsLookUpModel;
	}

	/**
	 * 
	 * 根据测试类型创建DNSLookUpModel
	 * 
	 * @Title: createTaskVideoStreamModel @param @param
	 *         task文本函数 @param @return @return DNSLookUpModel 实体类 @throws
	 */
	private TaskDNSLookUpModel createUmpcDNSLookUpModel(TaskText task) {
		TaskDNSLookUpModel dnsLookUpModel = new TaskDNSLookUpModel();

		dnsLookUpModel.setTag(task.get(0));
		dnsLookUpModel.setTaskName(task.get(0) + ".DNSLookUp");
		dnsLookUpModel.setTaskType(WalkStruct.TaskType.DNSLookUp.name());
		dnsLookUpModel.setEnable(1);
		dnsLookUpModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("Url=")) {
				dnsLookUpModel.getDnsTestConfig().setUrl(getValue(line));
			}
			if (line.startsWith("Timeout")) {
				dnsLookUpModel.getDnsTestConfig().setTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("Interval")) {
				dnsLookUpModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")) {
				dnsLookUpModel.setDisConnect(pppRuleUmpcToDevice((getInt(getValue(line)))));
			}
			if (line.startsWith("MultiTest")) {
				dnsLookUpModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				dnsLookUpModel.setRabName(getValue(line));
			}
		}
		return dnsLookUpModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :并行业务读取转换
	 */
	private TaskRabModel createTaskMultiRabModel(TaskText task) {
		TaskRabModel rabModel = new TaskRabModel();

		// 默认参数
		rabModel.setTag(task.get(0));
		rabModel.setTaskName(task.get(0) + ".ParallelService");
		rabModel.setTaskType(WalkStruct.TaskType.MultiRAB.name());
		rabModel.setEnable(1);
		rabModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("MultiName=")) {
				rabModel.setRabName(getValue(line));
			}
			if (line.startsWith("Interval=")) {
				rabModel.setInterVal(getInt(getValue(line)));
			}
			if (line.startsWith("DialDelay=")) {
				rabModel.setVoiceDelay(getInt(getValue(line)));
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				rabModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("TaskTimeMap=")) {
				rabModel.setRabRule(getValue(line));
			}
			if (line.startsWith("StartType=")) {
				rabModel.getParallelServiceTestConfig().setRabStartMode(getInt(getValue(line)));
			}
			if (line.startsWith("refTaskName=")) {
				rabModel.setRefTask(getValue(line));
			}
			if (line.startsWith("TaskState=")) {
				rabModel.setRefServiceIndex(getInt(getValue(line)));
			}

		}

		return rabModel;
	}

	@SuppressWarnings("deprecation")
	private TaskVideoPlayModel createHttpVsModel(TaskText task) {
		TaskVideoPlayModel vp = new TaskVideoPlayModel();
		vp.setTag(task.get(0));
		vp.setTaskName(task.get(0) + ".VideoPlay");
		vp.setTaskType(WalkStruct.TaskType.HTTPVS.name());
		vp.setEnable(1);
		vp.setRepeat(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("HttpVsURL=")) {
				vp.setUrl(getValue(line));
			}
			if (line.startsWith("HttpVsType=")) {
				vp.setVideoType(
						getInt(getValue(line)) != 1 ? TaskVideoPlayModel.VIDEO_TYPE_YOUKU : TaskVideoPlayModel.VIDEO_TYPE_YOUTUBE);
			}
			if (line.startsWith("HttpVsQuality=")) {
				int position = 0; // 默认0
				String videoQuality = getValue(line);
				String[] qualitys = { "001", "002", "003", "004", "005", "006", "007" };
				for (position = 0; position < qualitys.length; position++) {
					if (videoQuality.endsWith(qualitys[position])) {
						break;
					}
				}
				/*
				 * String[] youtube = { "1001", "1002", "1003",
				 * "1004","1005","1006","1007"}; // youtube视频质量 String[] youku = {
				 * "2001", "2002", "2003","2004","2005","2006","2007" }; // youtube视频质量
				 * 
				 * 
				 * if (vp.getVideoType() == 0) { if (videoQuality.equals(youtube[0])) {
				 * position = 0; } else if (videoQuality.equals(youtube[1])) { position
				 * = 1; } else if (videoQuality.equals(youtube[2])) { position = 2; }
				 * else if (videoQuality.equals(youtube[3])) { position = 3; } } else {
				 * if (videoQuality.equals(youku[0])) { position = 0; } else if
				 * (videoQuality.equals(youku[1])) { position = 1; } else if
				 * (videoQuality.equals(youku[2])) { position = 2; } }
				 */

				vp.setVideoQuality(position);
			}

			if (line.startsWith("HttpVsPsCall")) {
				vp.setPlayType(getInt(getValue(line)) == 1 ? 1 : 0);
			}

			if (line.startsWith("HttpVsNoDataTime=")) {
				vp.setNoDataTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PDPDeActiveEveryHttpVs=")) {
				vp.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("HttpVsInterval=")) {
				vp.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("HttpVsTimeout=")) {
				vp.setPlayTimeout(getInt(getValue(line)) / 1000);
			}
		}

		return vp;
	}

	private TaskVideoPlayModel createUmpcHttpVsModel(TaskText task) {
		TaskVideoPlayModel vp = new TaskVideoPlayModel();
		vp.setTag(task.get(0));
		vp.setTaskName(task.get(0) + ".VideoPlay");
		vp.setTaskType(WalkStruct.TaskType.HTTPVS.name());
		vp.setEnable(1);
		vp.setRepeat(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("URL=")) {
				vp.setUrl(getValue(line));
			}
			if (line.startsWith("Youtube=")) {
				vp.setPlayerType(getInt(getValue(line)) + 1);
			}
			if (line.startsWith("Quality=")) {
				int position = 0; // 默认0
				String videoQuality = getValue(line);
				String[] qualitys = { "001", "002", "003", "004", "005", "006", "007" };
				for (position = 0; position < qualitys.length; position++) {
					if (videoQuality.endsWith(qualitys[position])) {
						break;
					}
				}

				vp.setVideoQuality(position > 6 ? 0 : position);
			}
			if (line.startsWith("NoDataTimeout=")) {
				vp.setNoDataTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("Pscall=")) {
				vp.setPlayType(getInt(getValue(line)) == 2 ? 0 : 1);
			}
			if (line.startsWith("PlayTime")) {
				if (vp.getPlayType() == 1) {
					if(vp.getPlayTimerMode() == 0)
						vp.setPlayDuration(getInt(getValue(line)) / 1000);
					else
						vp.setPlayPercentage(getInt(getValue(line)));
				}
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")) {
				vp.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("Interval")) {
				vp.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("Timeout")) {
				if (vp.getPlayType() == 0) {
					vp.setPlayTimeout(getInt(getValue(line)) / 1000);
				}
			}
			if (line.startsWith("SaveMedia")) {
				vp.setSave(getInt(getValue(line)) == 1 ? true : false);
			}
			if (line.startsWith("BufferPlayThreshold")) {
				vp.setBufThred(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("UseBufferPercentage")) {
				vp.setBufTimerMode(getInt(getValue(line)));
			}

			if (line.startsWith("UsePlayPercentage")) {
				vp.setPlayTimerMode(getInt(getValue(line)));
			}

			if (line.startsWith("BufferCount")) {
				vp.setMaxBufCounts(getInt(getValue(line)));
			}
			if (line.startsWith("MaxBufferTime")) {
				if(vp.getBufTimerMode() == 0)
					vp.setMaxBufferTimeout(getInt(getValue(line)) / 1000);
				else
					vp.setMaxBufferPercentage(getInt(getValue(line)));
			}

			if (line.startsWith("BufferTime")) {
				vp.setBufTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MultiTest")) {
				vp.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				vp.setRabName(getValue(line));
			}

		}

		return vp;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :http上传
	 */
	private TaskHttpUploadModel createTaskHttpUpload(TaskText task) {
		TaskHttpUploadModel model = new TaskHttpUploadModel();

		// 默认参数
		model.setTag(task.get(0));
		model.setTaskName(task.get(0) + ".HTTPUpload");
		model.setTaskType(WalkStruct.TaskType.HttpUpload.name());
		model.setEnable(1);
		model.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("HttpUpWorkType=")) {
				int workType = getInt(getValue(line));
				model.setServerType(workType - 1 > 0 ? workType - 1 : 1); // 平台下发的3
				// youtube,2
				// 百度云
				// 对应这边的存值值2,1
			}
			if (line.startsWith("HttpUpUrl=")) {
				model.setUrl(getValue(line));
			}
			if (line.startsWith("HttpUpUser=")) {
				model.setUsername(getValue(line));
			}
			if (line.startsWith("HttpUpPassword")) {
				model.setPassword(getValue(line));
			}
			if (line.startsWith("HttpUpNoDataTime")) {
				model.setNoDataTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("HttpUpUploadSize")) {
				model.setFileSource(1);
				model.setFileSize(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PDPDeActiveEveryHttpUp=")) {
				model.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("HttpUpInterval=")) {
				model.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("HttpUpTimeout=")) {
				model.setTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("HttpUpPsCall")) {
				model.setTestMode(getInt(getValue(line)) == 0 ? 0 : 1);
			}

			// if (line.startsWith("AccountType=")) {
			// model.setAccountType(getInt(getValue(line)));
			model.setAccountType(0);
			// }
			if (line.startsWith("HttpUpUser=")) {
				model.setAccountKey(getValue(line));
			}
			if (line.startsWith("HttpUpPassword=")) {
				model.setSecretKey(getValue(line));
			}
			// if (line.startsWith("up_path=")) {
			// model.setServerPath(getValue(line));
			model.setServerPath("/");
			// }
		}

		return model;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :http上传
	 */
	private TaskHttpUploadModel createUmpcTaskHttpUpload(TaskText task) {
		TaskHttpUploadModel model = new TaskHttpUploadModel();

		// 默认参数
		model.setTag(task.get(0));
		model.setTaskName(task.get(0) + ".HTTPUpload");
		model.setTaskType(WalkStruct.TaskType.HttpUpload.name());
		model.setEnable(1);
		model.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("ServerType=")) {
				model.setServerType(getInt(getValue(line)) == 0 ? 2 : 1);
			}
			if (line.startsWith("UploadMode=")) {
				model.setTestMode(getInt(getValue(line)) == 0 ? 1 : 0);
			}
			if (line.startsWith("URL=")) {
				model.setUrl(getValue(line));
			}
			if (line.startsWith("User=")) {
				model.setUsername(getValue(line));
			}
			if (line.startsWith("Pswd=")) {
				model.setPassword(getValue(line));
			}
			if (line.startsWith("NoResponseTime")) {
				model.setNoDataTimeout(getInt(getValue(line)) / 1000);
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy=")) {
				model.setDisConnect(pppRuleUmpcToDevice((getInt(getValue(line)))));
			}
			if (line.startsWith("Interval=")) {
				model.setInterVal(getInt(getValue(line)) / 1000);
			}
			if(model.getTestMode()==0){//按文件
				if (line.startsWith("UploadTimeout=")) {
					model.setTimeout(getInt(getValue(line)) / 1000);
				}
			}else{//按时间
				if (line.startsWith("UploadLimit=")) {
					model.setTimeout(getInt(getValue(line)) / 1000);
				}
			}
			if (line.startsWith("FileGetMode")) {
				model.setFileSource(getInt(getValue(line)) == 0 ? 1 : 0);
			}
			if (line.startsWith("CreateFileSize")) {
				model.setFileSize(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("LocalFilePath")) {
				model.setFilePath(getValue(line));
			}
			if (line.startsWith("MultiTest")) {
				model.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				model.setRabName(getValue(line));
			}

			if (line.startsWith("AccountType=")) {
				model.setAccountType(getInt(getValue(line)));
			}
			if (line.startsWith("api_key=")) {
				model.setAccountKey(getValue(line));
			}
			if (line.startsWith("secret_key=")) {
				model.setSecretKey(getValue(line));
			}
			if (line.startsWith("up_path=")) {
				model.setServerPath(getValue(line));
			}
		}

		return model;
	}

	/**
	 * 解析平台下发MultilFTPDown计划<BR>
	 * [功能详细描述]
	 * 
	 * @param task
	 * @return
	 */
	public TaskMultiftpDownloadModel createMultilFTPDownModel(TaskText task) {
		TaskMultiftpDownloadModel mutilftpDownloadModel = new TaskMultiftpDownloadModel();
		mutilftpDownloadModel.setTag(task.get(0));
		mutilftpDownloadModel.setTaskName(task.get(0) + ".MultiftpDownload");
		mutilftpDownloadModel.setTaskType(WalkStruct.TaskType.MultiftpDownload.name());
		mutilftpDownloadModel.setEnable(1);
		mutilftpDownloadModel.setRepeat(1);
		ArrayList<FTPGroupModel> ftpServers = new ArrayList<FTPGroupModel>();
		FTPGroupModel[] ftpGroupModels = new FTPGroupModel[5];
		FtpServerModel[] ftpServerModels = new FtpServerModel[5];
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("MFTPDLPsCall")) {
				mutilftpDownloadModel.setTestMode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPDLPlayTime")) {
				mutilftpDownloadModel.setKeepTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPDLInterval")) {
				mutilftpDownloadModel.setInterVal(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPDLNoRespTime")) {
				mutilftpDownloadModel.setNoData(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("WaitTime")) {
				mutilftpDownloadModel.setWaitTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("PDPDeActiveEveryMFTPDL")) {
				mutilftpDownloadModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}

			if (line.startsWith("MFTPDLSession")) {
				mutilftpDownloadModel.setEndCodition(getInt(getValue(line)) == 1 ? 1 : 0);
			}

			// 第一个FTP服务器
			if (line.startsWith("MFTPDLHost=")) {
				ftpGroupModels[0] = new FTPGroupModel();
				ftpGroupModels[0].setFtpServers("MFTPD_" + getValue(line));
				ftpGroupModels[0].setEnable(1);
				ftpServerModels[0] = new FtpServerModel();
				ftpServerModels[0].setName("MFTPD_" + getValue(line));
				ftpServerModels[0].setIp(getValue(line));
			}
			if(ftpServerModels[0] != null && ftpGroupModels[0] != null){
				if (line.startsWith("MFTPDLPassive=")) {
					ftpServerModels[0].setConnect_mode(getInt(getValue(line)));
				}
				if (line.startsWith("MFTPDLPort=")) {
					ftpServerModels[0].setPort(getValue(line));
				}
	
				if (line.startsWith("MFTPDLUser=")) {
					ftpServerModels[0].setLoginUser(getValue(line));
				}
	
				if (line.startsWith("MFTPDLPass=")) {
					ftpServerModels[0].setLoginPassword(getValue(line));
				}
				if (line.startsWith("MFTPDLFile=")) {
					ftpGroupModels[0].setDownloadFile(getValue(line));
				}
	
				if (line.startsWith("SaveDLFile=")) {
					ftpGroupModels[0].setSavaFile(getInt(getValue(line)));
				}
			}

			// 第二个FTP服务器
			if (line.startsWith("MFTPDLHost0")) {
				ftpGroupModels[1] = new FTPGroupModel();
				ftpGroupModels[1].setFtpServers("MFTPD0_" + getValue(line));
				ftpGroupModels[1].setEnable(1);
				ftpServerModels[1] = new FtpServerModel();
				ftpServerModels[1].setName("MFTPD0_" + getValue(line));
				ftpServerModels[1].setIp(getValue(line));
			}

			if(ftpServerModels[1] != null && ftpGroupModels[1] != null){
				if (line.startsWith("MFTPDLPassive0=")) {
					ftpServerModels[1].setConnect_mode(getInt(getValue(line)));
				}
	
				if (line.startsWith("MFTPDLPort0")) {
					ftpServerModels[1].setPort(getValue(line));
				}
	
				if (line.startsWith("MFTPDLUser0")) {
					ftpServerModels[1].setLoginUser(getValue(line));
				}
	
				if (line.startsWith("MFTPDLPass0")) {
					ftpServerModels[1].setLoginPassword(getValue(line));
				}
	
				if (line.startsWith("MFTPDLFile0")) {
					ftpGroupModels[1].setDownloadFile(getValue(line));
				}
	
				if (line.startsWith("SaveDLFile0")) {
					ftpGroupModels[1].setSavaFile(getInt(getValue(line)));
				}
			}
			// 第三个FTP服务器
			if (line.startsWith("MFTPDLHost1")) {
				ftpGroupModels[2] = new FTPGroupModel();
				ftpGroupModels[2].setFtpServers("MFTPD1_" + getValue(line));
				ftpGroupModels[2].setEnable(1);
				ftpServerModels[2] = new FtpServerModel();
				ftpServerModels[2].setName("MFTPD1_" + getValue(line));
				ftpServerModels[2].setIp(getValue(line));
			}

			if(ftpServerModels[2] != null && ftpGroupModels[2] != null){
				if (line.startsWith("MFTPDLPassive1=")) {
					ftpServerModels[2].setConnect_mode(getInt(getValue(line)));
				}
	
				if (line.startsWith("MFTPDLPort1")) {
					ftpServerModels[2].setPort(getValue(line));
				}
	
				if (line.startsWith("MFTPDLUser1")) {
					ftpServerModels[2].setLoginUser(getValue(line));
				}
	
				if (line.startsWith("MFTPDLPass1")) {
					ftpServerModels[2].setLoginPassword(getValue(line));
				}
	
				if (line.startsWith("MFTPDLFile1")) {
					ftpGroupModels[2].setDownloadFile(getValue(line));
				}
	
				if (line.startsWith("SaveDLFile1")) {
					ftpGroupModels[2].setSavaFile(getInt(getValue(line)));
				}
			}
			
			// 第四个FTP服务器
			if (line.startsWith("MFTPDLHost2")) {
				ftpGroupModels[3] = new FTPGroupModel();
				ftpGroupModels[3].setFtpServers("MFTPD2_" + getValue(line));
				ftpGroupModels[3].setEnable(1);
				ftpServerModels[3] = new FtpServerModel();
				ftpServerModels[3].setName("MFTPD2_" + getValue(line));
				ftpServerModels[3].setIp(getValue(line));
			}

			if(ftpServerModels[3] != null && ftpGroupModels[3] != null){
				if (line.startsWith("MFTPDLPassive2=")) {
					ftpServerModels[3].setConnect_mode(getInt(getValue(line)));
				}
	
				if (line.startsWith("MFTPDLPort2")) {
					ftpServerModels[3].setPort(getValue(line));
				}
	
				if (line.startsWith("MFTPDLUser2")) {
					ftpServerModels[3].setLoginUser(getValue(line));
				}
	
				if (line.startsWith("MFTPDLPass2")) {
					ftpServerModels[3].setLoginPassword(getValue(line));
				}
	
				if (line.startsWith("MFTPDLFile2")) {
					ftpGroupModels[3].setDownloadFile(getValue(line));
				}
	
				if (line.startsWith("SaveDLFile2")) {
					ftpGroupModels[3].setSavaFile(getInt(getValue(line)));
				}
			}
			// 第五个FTP服务器
			if (line.startsWith("MFTPDLHost3")) {
				ftpGroupModels[4] = new FTPGroupModel();
				ftpGroupModels[4].setFtpServers("MFTPD3_" + getValue(line));
				ftpGroupModels[4].setEnable(1);
				ftpServerModels[4] = new FtpServerModel();
				ftpServerModels[4].setName("MFTPD3_" + getValue(line));
				ftpServerModels[4].setIp(getValue(line));
			}

			if(ftpServerModels[4] != null && ftpGroupModels[4] != null){
				if (line.startsWith("MFTPDLPassive3=")) {
					ftpServerModels[4].setConnect_mode(getInt(getValue(line)));
				}
	
				if (line.startsWith("MFTPDLPort3")) {
					ftpServerModels[4].setPort(getValue(line));
				}
	
				if (line.startsWith("MFTPDLUser3")) {
					ftpServerModels[4].setLoginUser(getValue(line));
				}
	
				if (line.startsWith("MFTPDLPass3")) {
					ftpServerModels[4].setLoginPassword(getValue(line));
				}
	
				if (line.startsWith("MFTPDLFile3")) {
					ftpGroupModels[4].setDownloadFile(getValue(line));
				}
	
				if (line.startsWith("SaveDLFile3")) {
					ftpGroupModels[4].setSavaFile(getInt(getValue(line)));
				}
			}
		}

		for (int i = 0; i < ftpGroupModels.length; i++) {
			if (ftpGroupModels[i] != null) {
				ftpServers.add(ftpGroupModels[i]);
			}
		}
		ConfigFtp configFtp = new ConfigFtp();
		for (int i = 0; i < ftpServerModels.length; i++) {
			if (ftpServerModels[i] != null) {
				configFtp.addFtp(ftpServerModels[i]);
			}
		}
		mutilftpDownloadModel.setWaitTime(3);
		mutilftpDownloadModel.setFtpServers(ftpServers);
		return mutilftpDownloadModel;
	}

	/**
	 * 解析MultilFTPDown业务Ipad下发计划<BR>
	 * [功能详细描述]
	 * 
	 * @param task
	 * @return
	 */
	@SuppressWarnings("null")
	private TaskMultiftpDownloadModel createUmpcMultilFTPDownModel(TaskText task) {
		TaskMultiftpDownloadModel mutilftpDownloadModel = new TaskMultiftpDownloadModel();
		mutilftpDownloadModel.setTag(task.get(0));
		mutilftpDownloadModel.setTaskName(task.get(0) + ".MultiftpDownload");
		mutilftpDownloadModel.setTaskType(WalkStruct.TaskType.MultiftpDownload.name());
		mutilftpDownloadModel.setEnable(1);
		mutilftpDownloadModel.setRepeat(1);
		ArrayList<FTPGroupModel> ftpServers = new ArrayList<FTPGroupModel>();
		FTPGroupModel ftpGroupModel0 = null, ftpGroupModel1 = null, ftpGroupModel2 = null, ftpGroupModel3 = null,
				ftpGroupModel4 = null;
		FtpServerModel ftpServerModel0 = null, ftpServerModel1 = null, ftpServerModel2 = null, ftpServerModel3 = null,
				ftpServerModel4 = null;

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("MFTPDLPsCall")) {
				mutilftpDownloadModel.setTestMode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPDLPlayTime")) {
				mutilftpDownloadModel.setKeepTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPDLInterval")) {
				mutilftpDownloadModel.setInterVal(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPDLNoRespTime")) {
				mutilftpDownloadModel.setNoData(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("WaitTime")) {
				mutilftpDownloadModel.setWaitTime(getInt(getValue(line)) / 1000);
			}

			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")) {
				mutilftpDownloadModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}

			// 第一个FTP服务器
			if (line.startsWith("MFTPDLHost=")) {
				ftpGroupModel0 = new FTPGroupModel();
				ftpGroupModel0.setFtpServers("MFTPD_" + getValue(line));
				ftpGroupModel0.setEnable(1);
				ftpServerModel0 = new FtpServerModel();
				ftpServerModel0.setName("MFTPD_" + getValue(line));
				ftpServerModel0.setIp(getValue(line));
			}

			if (line.startsWith("MFTPDLPort=")) {
				ftpServerModel0.setPort(getValue(line));
			}

			if (line.startsWith("MFTPDLUser=")) {
				ftpServerModel0.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPDLPass=")) {
				ftpServerModel0.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPDLFile=")) {
				ftpGroupModel0.setDownloadFile(getValue(line));
			}

			if (line.startsWith("SaveDLFile=")) {
				ftpGroupModel0.setSavaFile(getInt(getValue(line)));
			}

			// 第二个FTP服务器
			if (line.startsWith("MFTPDLHost0")) {
				ftpGroupModel1 = new FTPGroupModel();
				ftpGroupModel1.setFtpServers("MFTPD0_" + getValue(line));
				ftpGroupModel1.setEnable(1);
				ftpServerModel1 = new FtpServerModel();
				ftpServerModel1.setName("MFTPD0_" + getValue(line));
				ftpServerModel1.setIp(getValue(line));
			}

			if (line.startsWith("MFTPDLPort0")) {
				ftpServerModel1.setPort(getValue(line));
			}

			if (line.startsWith("MFTPDLUser0")) {
				ftpServerModel1.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPDLPass0")) {
				ftpServerModel1.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPDLFile0")) {
				ftpGroupModel1.setDownloadFile(getValue(line));
			}

			if (line.startsWith("SaveDLFile0")) {
				ftpGroupModel1.setSavaFile(getInt(getValue(line)));
			}

			// 第三个FTP服务器
			if (line.startsWith("MFTPDLHost1")) {
				ftpGroupModel2 = new FTPGroupModel();
				ftpGroupModel2.setFtpServers("MFTPD1_" + getValue(line));
				ftpGroupModel2.setEnable(1);
				ftpServerModel2 = new FtpServerModel();
				ftpServerModel2.setName("MFTPD1_" + getValue(line));
				ftpServerModel2.setIp(getValue(line));
			}

			if (line.startsWith("MFTPDLPort1")) {
				ftpServerModel2.setPort(getValue(line));
			}

			if (line.startsWith("MFTPDLUser1")) {
				ftpServerModel2.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPDLPass1")) {
				ftpServerModel2.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPDLFile1")) {
				ftpGroupModel2.setDownloadFile(getValue(line));
			}

			if (line.startsWith("SaveDLFile1")) {
				ftpGroupModel2.setSavaFile(getInt(getValue(line)));
			}

			// 第四个FTP服务器
			if (line.startsWith("MFTPDLHost2")) {
				ftpGroupModel3 = new FTPGroupModel();
				ftpGroupModel3.setFtpServers("MFTPD2_" + getValue(line));
				ftpGroupModel3.setEnable(1);
				ftpServerModel3 = new FtpServerModel();
				ftpServerModel3.setName("MFTPD2_" + getValue(line));
				ftpServerModel3.setIp(getValue(line));
			}

			if (line.startsWith("MFTPDLPort2")) {
				ftpServerModel3.setPort(getValue(line));
			}

			if (line.startsWith("MFTPDLUser2")) {
				ftpServerModel3.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPDLPass2")) {
				ftpServerModel3.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPDLFile2")) {
				ftpGroupModel3.setDownloadFile(getValue(line));
			}

			if (line.startsWith("SaveDLFile2")) {
				ftpGroupModel3.setSavaFile(getInt(getValue(line)));
			}

			// 第五个FTP服务器
			if (line.startsWith("MFTPDLHost3")) {
				ftpGroupModel4 = new FTPGroupModel();
				ftpGroupModel4.setFtpServers("MFTPD3_" + getValue(line));
				ftpGroupModel4.setEnable(1);
				ftpServerModel4 = new FtpServerModel();
				ftpServerModel4.setName("MFTPD3_" + getValue(line));
				ftpServerModel4.setIp(getValue(line));
			}

			if (line.startsWith("MFTPDLPort3")) {
				ftpServerModel4.setPort(getValue(line));
			}

			if (line.startsWith("MFTPDLUser3")) {
				ftpServerModel4.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPDLPass3")) {
				ftpServerModel4.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPDLFile3")) {
				ftpGroupModel4.setDownloadFile(getValue(line));
			}

			if (line.startsWith("SaveDLFile3")) {
				ftpGroupModel4.setSavaFile(getInt(getValue(line)));
			}

		}

		FTPGroupModel ftpGroupList[] = new FTPGroupModel[] { ftpGroupModel0, ftpGroupModel1, ftpGroupModel2, ftpGroupModel3,
				ftpGroupModel4 };
		for (int i = 0; i < ftpGroupList.length; i++) {
			if (ftpGroupList[i] != null) {
				ftpServers.add(ftpGroupList[i]);
			}
		}

		FtpServerModel[] ftpServerList = new FtpServerModel[] { ftpServerModel0, ftpServerModel1, ftpServerModel2,
				ftpServerModel3, ftpServerModel4 };
		ConfigFtp configFtp = new ConfigFtp();
		for (int i = 0; i < ftpServerList.length; i++) {
			if (ftpServerList[i] != null) {
				configFtp.addFtp(ftpServerList[i]);
			}
		}

		mutilftpDownloadModel.setFtpServers(ftpServers);
		return mutilftpDownloadModel;

	}

	/**
	 * 解析平台下发TaskMultiftpUploadModel计划<BR>
	 * <BR>
	 * [功能详细描述]
	 * 
	 * @param task
	 * @return
	 */
	@SuppressWarnings("null")
	private TaskMultiftpUploadModel createMultilFTPUploadModel(TaskText task) {
		TaskMultiftpUploadModel mutilftpUploadModel = new TaskMultiftpUploadModel();
		mutilftpUploadModel.setTag(task.get(0));
		mutilftpUploadModel.setTaskName(task.get(0) + ".MultiftpUpload");
		mutilftpUploadModel.setTaskType(WalkStruct.TaskType.MultiftpUpload.name());
		mutilftpUploadModel.setEnable(1);
		mutilftpUploadModel.setRepeat(1);
		mutilftpUploadModel.setWaitTime(3);
		ArrayList<FTPGroupModel> ftpServers = new ArrayList<FTPGroupModel>();
		FTPGroupModel ftpGroupModel0 = null, ftpGroupModel1 = null, ftpGroupModel2 = null, ftpGroupModel3 = null,
				ftpGroupModel4 = null;
		FtpServerModel ftpServerModel0 = null, ftpServerModel1 = null, ftpServerModel2 = null, ftpServerModel3 = null,
				ftpServerModel4 = null;

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("MFTPULPsCall")) {
				mutilftpUploadModel.setTestMode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPlayTime")) {
				mutilftpUploadModel.setKeepTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULInterval")) {
				mutilftpUploadModel.setInterVal(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULNoRespTime")) {
				mutilftpUploadModel.setNoData(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("WaitTime")) {
				mutilftpUploadModel.setWaitTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("PDPDeActiveEveryMFTPUL")) {
				mutilftpUploadModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}

			if (line.startsWith("MFTPULSession")) {
				mutilftpUploadModel.setEndCodition(getInt(getValue(line)) == 2 ? 1 : 0);
			}

			// 第一个FTP服务器
			if (line.startsWith("MFTPULHost=")) {
				ftpGroupModel0 = new FTPGroupModel();
				ftpGroupModel0.setFtpServers("MFTPU_" + getValue(line));
				ftpGroupModel0.setEnable(1);
				ftpGroupModel0.setFileSource(0);
				ftpServerModel0 = new FtpServerModel();
				ftpServerModel0.setName("MFTPU_" + getValue(line));
				ftpServerModel0.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPassive=")) {
				ftpServerModel0.setConnect_mode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPort=")) {
				ftpServerModel0.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser=")) {
				ftpServerModel0.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass=")) {
				ftpServerModel0.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize=")) {
				ftpGroupModel0.setFileSize(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULPath=")) {
				ftpGroupModel0.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource=")) {
				ftpGroupModel0.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile=")) {
				ftpGroupModel0.setLocalFile(getValue(line));
			}

			// 第二个FTP服务器
			if (line.startsWith("MFTPULHost0")) {
				ftpGroupModel1 = new FTPGroupModel();
				ftpGroupModel1.setFtpServers("MFTPU0_" + getValue(line));
				ftpGroupModel1.setEnable(1);
				ftpGroupModel1.setFileSource(0);
				ftpServerModel1 = new FtpServerModel();
				ftpServerModel1.setName("MFTPU0_" + getValue(line));
				ftpServerModel1.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPassive0=")) {
				ftpServerModel1.setConnect_mode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPort0")) {
				ftpServerModel1.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser0")) {
				ftpServerModel1.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass0")) {
				ftpServerModel1.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize0")) {
				ftpGroupModel1.setFileSize(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULPath0")) {
				ftpGroupModel1.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource0")) {
				ftpGroupModel1.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile0")) {
				ftpGroupModel1.setLocalFile(getValue(line));
			}

			// 第三个FTP服务器
			if (line.startsWith("MFTPULHost1")) {
				ftpGroupModel2 = new FTPGroupModel();
				ftpGroupModel2.setFtpServers("MFTPU1_" + getValue(line));
				ftpGroupModel2.setEnable(1);
				ftpGroupModel2.setFileSource(0);
				ftpServerModel2 = new FtpServerModel();
				ftpServerModel2.setName("MFTPU1_" + getValue(line));
				ftpServerModel2.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPassive1=")) {
				ftpServerModel2.setConnect_mode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPort1")) {
				ftpServerModel2.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser1")) {
				ftpServerModel2.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass1")) {
				ftpServerModel2.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize1")) {
				ftpGroupModel2.setFileSize(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULPath1")) {
				ftpGroupModel2.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource1")) {
				ftpGroupModel2.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile1")) {
				ftpGroupModel2.setLocalFile(getValue(line));
			}

			// 第四个FTP服务器
			if (line.startsWith("MFTPULHost2")) {
				ftpGroupModel3 = new FTPGroupModel();
				ftpGroupModel3.setFtpServers("MFTPU2_" + getValue(line));
				ftpGroupModel3.setEnable(1);
				ftpGroupModel3.setFileSource(0);
				ftpServerModel3 = new FtpServerModel();
				ftpServerModel3.setName("MFTPU2_" + getValue(line));
				ftpServerModel3.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPassive2=")) {
				ftpServerModel3.setConnect_mode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPort2")) {
				ftpServerModel3.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser2")) {
				ftpServerModel3.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass2")) {
				ftpServerModel3.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize2")) {
				ftpGroupModel3.setFileSize(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULPath2")) {
				ftpGroupModel3.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource2")) {
				ftpGroupModel3.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile2")) {
				ftpGroupModel3.setLocalFile(getValue(line));
			}

			// 第五个FTP服务器
			if (line.startsWith("MFTPULHost3")) {
				ftpGroupModel4 = new FTPGroupModel();
				ftpGroupModel4.setFtpServers("MFTPU3_" + getValue(line));
				ftpGroupModel4.setEnable(1);
				ftpGroupModel4.setFileSource(0);
				ftpServerModel4 = new FtpServerModel();
				ftpServerModel4.setName("MFTPU3_" + getValue(line));
				ftpServerModel4.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPassive3=")) {
				ftpServerModel4.setConnect_mode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPort3")) {
				ftpServerModel4.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser3")) {
				ftpServerModel4.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass3")) {
				ftpServerModel4.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize3")) {
				ftpGroupModel4.setFileSize(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULPath3")) {
				ftpGroupModel4.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource3")) {
				ftpGroupModel4.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile3")) {
				ftpGroupModel4.setLocalFile(getValue(line));
			}
		}

		FTPGroupModel ftpGroupList[] = new FTPGroupModel[] { ftpGroupModel0, ftpGroupModel1, ftpGroupModel2, ftpGroupModel3,
				ftpGroupModel4 };
		for (int i = 0; i < ftpGroupList.length; i++) {
			if (ftpGroupList[i] != null) {
				ftpServers.add(ftpGroupList[i]);
			}
		}
		ConfigFtp configFtp = new ConfigFtp();
		FtpServerModel[] ftpServerList = new FtpServerModel[] { ftpServerModel0, ftpServerModel1, ftpServerModel2,
				ftpServerModel3, ftpServerModel4 };
		for (int i = 0; i < ftpServerList.length; i++) {
			if (ftpServerList[i] != null) {
				configFtp.addFtp(ftpServerList[i]);
			}
		}
		mutilftpUploadModel.setFtpServers(ftpServers);

		return mutilftpUploadModel;
	}

	/**
	 * 创建Ipad TaskMultiftpUploadModel下发解析任务<BR>
	 * [功能详细描述]
	 * 
	 * @param task
	 *          下发字符
	 * @return
	 */
	@SuppressWarnings("null")
	private TaskMultiftpUploadModel createUmpcMultilFTPUploadModel(TaskText task) {
		TaskMultiftpUploadModel mutilftpUploadModel = new TaskMultiftpUploadModel();
		mutilftpUploadModel.setTag(task.get(0));
		mutilftpUploadModel.setTaskName(task.get(0) + ".MultiftpUpload");
		mutilftpUploadModel.setTaskType(WalkStruct.TaskType.MultiftpUpload.name());
		mutilftpUploadModel.setEnable(1);
		mutilftpUploadModel.setRepeat(1);

		ArrayList<FTPGroupModel> ftpServers = new ArrayList<FTPGroupModel>();
		FTPGroupModel ftpGroupModel0 = null, ftpGroupModel1 = null, ftpGroupModel2 = null, ftpGroupModel3 = null,
				ftpGroupModel4 = null;
		FtpServerModel ftpServerModel0 = null, ftpServerModel1 = null, ftpServerModel2 = null, ftpServerModel3 = null,
				ftpServerModel4 = null;

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("MFTPDLPsCall")) {
				mutilftpUploadModel.setTestMode(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPlayTime")) {
				mutilftpUploadModel.setKeepTime(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULInterval")) {
				mutilftpUploadModel.setInterVal(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("MFTPULNoRespTime")) {
				mutilftpUploadModel.setNoData(getInt(getValue(line)) / 1000);
			}

			if (line.startsWith("WaitTime")) {
				mutilftpUploadModel.setWaitTime(getInt(getValue(line)) / 1000);
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")) {
				mutilftpUploadModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}

			// 第一个FTP服务器
			if (line.startsWith("MFTPULHost=")) {
				ftpGroupModel0 = new FTPGroupModel();
				ftpGroupModel0.setFtpServers("MFTPU_" + getValue(line));
				ftpGroupModel0.setEnable(1);
				ftpServerModel0 = new FtpServerModel();
				ftpServerModel0.setName("MFTPU_" + getValue(line));
				ftpServerModel0.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPort=")) {
				ftpServerModel0.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser=")) {
				ftpServerModel0.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass=")) {
				ftpServerModel0.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize=")) {
				ftpGroupModel0.setFileSize(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPath=")) {
				ftpGroupModel0.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource=")) {
				ftpGroupModel0.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile=")) {
				ftpGroupModel0.setLocalFile(getValue(line));
			}

			// 第二个FTP服务器
			if (line.startsWith("MFTPULHost0")) {
				ftpGroupModel1 = new FTPGroupModel();
				ftpGroupModel1.setFtpServers("MFTPU0_" + getValue(line));
				ftpGroupModel1.setEnable(1);
				ftpServerModel1 = new FtpServerModel();
				ftpServerModel1.setName("MFTPU0_" + getValue(line));
				ftpServerModel1.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPort0")) {
				ftpServerModel1.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser0")) {
				ftpServerModel1.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass0")) {
				ftpServerModel1.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize0")) {
				ftpGroupModel1.setFileSize(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPath0")) {
				ftpGroupModel1.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource0")) {
				ftpGroupModel1.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile0")) {
				ftpGroupModel1.setLocalFile(getValue(line));
			}

			// 第三个FTP服务器
			if (line.startsWith("MFTPULHost1")) {
				ftpGroupModel2 = new FTPGroupModel();
				ftpGroupModel2.setFtpServers("MFTPU1_" + getValue(line));
				ftpGroupModel2.setEnable(1);
				ftpServerModel2 = new FtpServerModel();
				ftpServerModel2.setName("MFTPU1_" + getValue(line));
				ftpServerModel2.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPort1")) {
				ftpServerModel2.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser1")) {
				ftpServerModel2.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass1")) {
				ftpServerModel2.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize1")) {
				ftpGroupModel2.setFileSize(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPath1")) {
				ftpGroupModel2.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource1")) {
				ftpGroupModel2.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile1")) {
				ftpGroupModel2.setLocalFile(getValue(line));
			}

			// 第四个FTP服务器
			if (line.startsWith("MFTPULHost2")) {
				ftpGroupModel3 = new FTPGroupModel();
				ftpGroupModel3.setFtpServers("MFTPU2_" + getValue(line));
				ftpGroupModel3.setEnable(1);
				ftpServerModel3 = new FtpServerModel();
				ftpServerModel3.setName("MFTPU2_" + getValue(line));
				ftpServerModel3.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPort2")) {
				ftpServerModel3.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser2")) {
				ftpServerModel3.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass2")) {
				ftpServerModel3.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize2")) {
				ftpGroupModel3.setFileSize(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPath2")) {
				ftpGroupModel3.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource2")) {
				ftpGroupModel3.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile2")) {
				ftpGroupModel3.setLocalFile(getValue(line));
			}

			// 第五个FTP服务器
			if (line.startsWith("MFTPULHost3")) {
				ftpGroupModel4 = new FTPGroupModel();
				ftpGroupModel4.setFtpServers("MFTPU3_" + getValue(line));
				ftpGroupModel4.setEnable(1);
				ftpServerModel4 = new FtpServerModel();
				ftpServerModel4.setName("MFTPU3_" + getValue(line));
				ftpServerModel4.setIp(getValue(line));
			}

			if (line.startsWith("MFTPULPort3")) {
				ftpServerModel4.setPort(getValue(line));
			}

			if (line.startsWith("MFTPULUser3")) {
				ftpServerModel4.setLoginUser(getValue(line));
			}

			if (line.startsWith("MFTPULPass3")) {
				ftpServerModel4.setLoginPassword(getValue(line));
			}

			if (line.startsWith("MFTPULFileSize3")) {
				ftpGroupModel4.setFileSize(getInt(getValue(line)));
			}

			if (line.startsWith("MFTPULPath3")) {
				ftpGroupModel4.setUploadFilePath(getValue(line));
			}

			if (line.startsWith("FileSource3")) {
				ftpGroupModel4.setFileSource(getInt(getValue(line)));
			}

			if (line.startsWith("LocalFile3")) {
				ftpGroupModel4.setLocalFile(getValue(line));
			}
		}

		FTPGroupModel ftpGroupList[] = new FTPGroupModel[] { ftpGroupModel0, ftpGroupModel1, ftpGroupModel2, ftpGroupModel3,
				ftpGroupModel4 };
		for (int i = 0; i < ftpGroupList.length; i++) {
			if (ftpGroupList[i] != null) {
				ftpServers.add(ftpGroupList[i]);
			}
		}
		ConfigFtp configFtp = new ConfigFtp();
		FtpServerModel[] ftpServerList = new FtpServerModel[] { ftpServerModel0, ftpServerModel1, ftpServerModel2,
				ftpServerModel3, ftpServerModel4 };
		for (int i = 0; i < ftpServerList.length; i++) {
			if (ftpServerList[i] != null) {
				configFtp.addFtp(ftpServerList[i]);
			}
		}
		mutilftpUploadModel.setFtpServers(ftpServers);

		return mutilftpUploadModel;

	}

    private TaskWeCallModel createWxCallModel(TaskText task, WalkStruct.TaskType type){
        TaskWeCallModel model = new TaskWeCallModel();
        // 默认参数
        model.setTag(task.get(0));
        model.setTaskName(task.get(0) + "."+type.getXmlTaskType());
        model.setTaskType(type.getXmlTaskType());
        model.setEnable(1);
        model.setRepeat(1);
        for (int i = 0; i < task.size(); i++) {
            String line = task.get(i);
            if (line.startsWith("interval")) {
                model.setInterVal(getInt(getValue(line)));
            }
            if (line.startsWith("VoiceDuration")) {
                model.setKeepTime(getInt(getValue(line)));
            }
            if (line.startsWith("TaskTimeout")) {
                model.setConnectTime(getInt(getValue(line)));
            }
        }
        return model;
    }

	/**
	 * 解析平台下发的MultipleAppTest测试计划
	 * @param task
	 * @return
	 */private TaskMultipleAppTestModel createMultipleAppTestModel(TaskText task, WalkStruct.TaskType type){
		TaskMultipleAppTestModel taskMultipleModel = new TaskMultipleAppTestModel();
		// 默认参数
		taskMultipleModel.setTag(task.get(0));
		taskMultipleModel.setTaskName(task.get(0) + "."+type.getXmlTaskType());
		taskMultipleModel.setTaskType(type.getXmlTaskType());
		taskMultipleModel.setEnable(1);
		taskMultipleModel.setRepeat(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);

			if (line.startsWith("StartAppMode")) {
				taskMultipleModel.setStartAppMode(getInt(getValue(line)));
			}
			if (line.startsWith("SendText")) {
				taskMultipleModel.setSendText(getValue(line));
			}
			if (line.startsWith("interval")) {
				taskMultipleModel.setInterVal(getInt(getValue(line)));
			}
			if (line.startsWith("PictureSize")) {
				taskMultipleModel.setSendPictureType(getInt(getValue(line)));
			}
			if (line.startsWith("VoiceDuration")) {
				taskMultipleModel.setVoiceDuration(getInt(getValue(line)));
			}
			if (line.startsWith("TaskTimeout")) {
				taskMultipleModel.setTaskTimeout(getInt(getValue(line)));
			}
		}
		return taskMultipleModel;
	}

	private TaskFaceBookModel createFaceBookModel(TaskText task) {
		TaskFaceBookModel faceBookModel = new TaskFaceBookModel();
		// 默认参数
		faceBookModel.setTag(task.get(0));
		faceBookModel.setTaskName(task.get(0) + ".Facebook");
		faceBookModel.setTaskType(WalkStruct.TaskType.Facebook.name());
		faceBookModel.setEnable(1);
		faceBookModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("FacebookInterval")) {
				faceBookModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("FacebookUser")) {
				faceBookModel.setUser(getValue(line));
			}
			if (line.startsWith("FacebookPasswd")) {
				faceBookModel.setPassword(getValue(line));
			}

			if (line.startsWith("FacebookAppId")||line.startsWith("AppID")) {
				faceBookModel.setAppId(getValue(line));
			}

			if (line.startsWith("FacebookAppSecret")||line.startsWith("AppSecret")) {
				faceBookModel.setAppSecret(getValue(line));
			}

			if (line.startsWith("FacebookSendContent")||line.startsWith("Content")) {
				faceBookModel.setSendContent(getValue(line));
			}
			if (line.startsWith("FacebookSendPicSizeLevel")||line.startsWith("PicType")) {
				faceBookModel.setSendPicSizeLevel(getInt(getValue(line)));
			}
			if (line.startsWith("MultiTest")) {
				faceBookModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				faceBookModel.setRabName(getValue(line));
			}
			if (line.startsWith("PDPDeActiveEveryFacebook")) {
				faceBookModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}

		}
		return faceBookModel;
	}

	private TaskFaceBookModel createUmpcFaceBookModel(TaskText task) {
		TaskFaceBookModel faceBookModel = new TaskFaceBookModel();
		// 默认参数
		faceBookModel.setTag(task.get(0));
		faceBookModel.setTaskName(task.get(0) + ".Facebook");
		faceBookModel.setTaskType(WalkStruct.TaskType.Facebook.name());
		faceBookModel.setEnable(1);
		faceBookModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("FacebookInterval")) {
				faceBookModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("FacebookUser")) {
				faceBookModel.setUser(getValue(line));
			}
			if (line.startsWith("FacebookPasswd")) {
				faceBookModel.setPassword(getValue(line));
			}

			if (line.startsWith("FacebookAppId")||line.startsWith("AppID")) {
				faceBookModel.setAppId(getValue(line));
			}

			if (line.startsWith("FacebookAppSecret")||line.startsWith("AppSecret")) {
				faceBookModel.setAppSecret(getValue(line));
			}

			if (line.startsWith("FacebookSendContent")||line.startsWith("Content")) {
				faceBookModel.setSendContent(getValue(line));
			}
			if (line.startsWith("FacebookSendPicSizeLevel")||line.startsWith("PicType")) {
				faceBookModel.setSendPicSizeLevel(getInt(getValue(line)));
			}
			if (line.startsWith("MultiTest")) {
				faceBookModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				faceBookModel.setRabName(getValue(line));
			}
			if (line.toLowerCase(Locale.getDefault()).startsWith("ppppolicy")||line.startsWith("PPPPolicy")) {
				faceBookModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}

		}

		return faceBookModel;
	}

	/**
	 * 根据测试任务的文本生成测试任务的模型 :Idle
	 */
	private TaskEmptyModel createTaskIdleModel(TaskText taskText) {
		TaskEmptyModel emptyModel = new TaskEmptyModel();

		// 默认参数
		emptyModel.setTag(taskText.get(0));
		emptyModel.setTaskName(taskText.get(0) + "." + "Idle");// 文件中的任务名作为前缀
		emptyModel.setTaskType(WalkStruct.TaskType.EmptyTask.name());
		emptyModel.setEnable(1);

		// 来自文件的参数
		for (int i = 0; i < taskText.size(); i++) {
			String line = taskText.get(i);
			if (line.startsWith("IdleTime")) {
				emptyModel.getIdleTestConfig().setKeepTime(getInt(getValue(line)) / 1000);
			}
		}

		return emptyModel;
	}

	/**
	 * 生成TraceRoute Model
	 * 
	 * @param task
	 * @return
	 */

	private TaskTraceRouteModel createUmpcTraceRouteModel(TaskText task) {
		TaskTraceRouteModel traceRouteModel = new TaskTraceRouteModel();
		// 默认参数
		traceRouteModel.setTag(task.get(0));
		traceRouteModel.setTaskName(task.get(0) + ".Trace Route");
		traceRouteModel.setTaskType(WalkStruct.TaskType.TraceRoute.name());
		traceRouteModel.setEnable(1);
		traceRouteModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("Host")) {
				traceRouteModel.setHost(getValue(line));
			}
			if (line.startsWith("IPPackSize")) {
				traceRouteModel.setIpPacket(getInt(getValue(line)));
			}
			if (line.startsWith("HopTimeout")) {
				traceRouteModel.setHopTimeout(Long.parseLong(getValue(line)));
			}
			if (line.startsWith("HopIntervals")) {
				traceRouteModel.setHopInterval(Long.parseLong(getValue(line)));
			}
			if (line.startsWith("HopProbeNum")) {
				traceRouteModel.setHopProbeNum(getInt(getValue(line)));
			}
			if (line.startsWith("Interval")) {
				traceRouteModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PPPPolicy")) {
				traceRouteModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("MultiTest")) {
				traceRouteModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				traceRouteModel.setRabName(getValue(line));
			}

		}

		return traceRouteModel;
	}

	/**
	 * 解析TraceRoute 平台下发
	 * 
	 * @param task
	 * @return
	 */
	private TaskTraceRouteModel createTraceRouteModel(TaskText task) {
		TaskTraceRouteModel traceRouteModel = new TaskTraceRouteModel();
		// 默认参数
		traceRouteModel.setTag(task.get(0));
		traceRouteModel.setTaskName(task.get(0) + ".Trace Route");
		traceRouteModel.setTaskType(WalkStruct.TaskType.TraceRoute.name());
		traceRouteModel.setEnable(1);
		traceRouteModel.setRepeat(1);

		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("TracertIP")) {
				traceRouteModel.setHost(getValue(line));
			}
			if (line.startsWith("TracertPacketSize")) {
				traceRouteModel.setIpPacket(getInt(getValue(line)));
			}
			if (line.startsWith("TracertPerTimeout")) {
				traceRouteModel.setHopTimeout(Long.parseLong(getValue(line)));
			}
			if (line.startsWith("TracertPerInterval")) {
				traceRouteModel.setHopInterval(Long.parseLong(getValue(line)));
			}
			if (line.startsWith("TracertCheckNumber")) {
				traceRouteModel.setHopProbeNum(getInt(getValue(line)));
			}
			if (line.startsWith("TracertInterval")) {
				traceRouteModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PDPDeActiveEveryTracert")) {
				traceRouteModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));

			}
			if (line.startsWith("MultiTest")) {
				traceRouteModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName")) {
				traceRouteModel.setRabName(getValue(line));
			}

		}
		return traceRouteModel;
	}

	/**
	 * 解析平台下发Iperf
	 * 
	 * @param task
	 * @return
	 */
	private TaskIperfModel createIperfModel(TaskText task) {
		TaskIperfModel taskIperfModel = new TaskIperfModel();
		// 默认参数
		taskIperfModel.setTag(task.get(0));
		taskIperfModel.setTaskName(task.get(0) + ".Iperf");
		taskIperfModel.setTaskType(WalkStruct.TaskType.Iperf.name());
		taskIperfModel.setEnable(1);
		taskIperfModel.setRepeat(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);
			if (line.startsWith("IperfServerIP")) {
				taskIperfModel.setRemoteAddress(getValue(line));
				taskIperfModel.setTelnetAddress(getValue(line));
			}
			if (line.startsWith("IperfServerPort")) {
				taskIperfModel.setTelnetPort(getInt(getValue(line)));
			}
			if (line.startsWith("IperfUserName")) {
				taskIperfModel.setUserName(getValue(line));
			}
			if (line.startsWith("IperfPassword")) {
				taskIperfModel.setPassword(getValue(line));
			}
			if (line.startsWith("IperfPath")) {
				taskIperfModel.setIperfPath(getValue(line));
			}
			if (line.startsWith("IperfPath")) {
				taskIperfModel.setIperfPath(getValue(line));
			}
			if (line.startsWith("PDPDeActiveEveryIperf")) {
				taskIperfModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("IperfTimeout")) {
				taskIperfModel.setDuration(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("IperfInterval")) {
				taskIperfModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("IperfProtoType")) {
				taskIperfModel.setProtocol(getInt(getValue(line)));

				taskIperfModel.setUdpBandWidth(1);
				taskIperfModel.setUdpBuffSize(512);
				taskIperfModel.setUdpPacketSize(1400);
			}
		}
		return taskIperfModel;
	}

	/**
	 * 解析平台下发的微信测试计划
	 * @param task 
	 * @return
	 */
	private TaskWeChatModel createWeChatModel(TaskText task){
		TaskWeChatModel taskWeChatModel = new TaskWeChatModel();
		// 默认参数
		taskWeChatModel.setTag(task.get(0));
		taskWeChatModel.setTaskName(task.get(0) + ".WeChat");
		taskWeChatModel.setTaskType(WalkStruct.TaskType.WeChat.name());
		taskWeChatModel.setEnable(1);
		taskWeChatModel.setRepeat(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);

			if (line.startsWith("RecipientAccount")) {
				taskWeChatModel.setFriendName(getValue(line));
			}
			if (line.startsWith("SendText")) {
				taskWeChatModel.setSendText(getValue(line));
			}
			if (line.startsWith("interval")) {
				taskWeChatModel.setInterVal(getInt(getValue(line)));
			}
			if (line.startsWith("PictureSize")) {
				taskWeChatModel.setSendPictureType(getInt(getValue(line)));
			}
			if (line.startsWith("SpeechDuration")) {
				taskWeChatModel.setVoiceDuration(getInt(getValue(line)));
			}
			if (line.startsWith("SendTimeout")) {
				taskWeChatModel.setSendTimeout(getInt(getValue(line)));
			}
			if (line.startsWith("workMode")) {
				taskWeChatModel.setOperationType(getInt(getValue(line)));
			}
		}
		return taskWeChatModel;
	}
	
	/**
	 * 解析平台下发pbm
	 * 
	 * @param task
	 * @return
	 */
	private TaskPBMModel createPbmModel(TaskText task) {
		TaskPBMModel taskPBMModel = new TaskPBMModel();
		// 默认参数
		taskPBMModel.setTag(task.get(0));
		taskPBMModel.setTaskName(task.get(0) + ".PBM");
		taskPBMModel.setTaskType(WalkStruct.TaskType.PBM.name());
		taskPBMModel.setEnable(1);
		taskPBMModel.setRepeat(1);
		taskPBMModel.setSampleInterval(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);

			/** 解析服务器地址 */
			if (line.startsWith("PBMHost")) {
				taskPBMModel.setServerIP(getValue(line));
			}
			if (line.startsWith("PBMPort")) {
				taskPBMModel.setServerPort(getInt(getValue(line)));
			}
			// }
			if (line.startsWith("PBMInterval")) {
				taskPBMModel.setInterVal(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PBMPlayTime")) {
				taskPBMModel.setDuration(getInt(getValue(line)) / 1000);
			}
			if (line.startsWith("PDPDeActiveEveryPBM")) {
				taskPBMModel.setDisConnect(pppRuleFleetToDevice(getInt(getValue(line))));
			}

		}
		return taskPBMModel;
	}

	/**
	 * 解析ipad下发pbm
	 * 
	 * @param task
	 * @return
	 */
	private TaskPBMModel createPbmIpadModel(TaskText task) {
		TaskPBMModel taskPBMModel = new TaskPBMModel();
		// 默认参数
		taskPBMModel.setTag(task.get(0));
		taskPBMModel.setTaskName(task.get(0) + ".PBM");
		taskPBMModel.setTaskType(WalkStruct.TaskType.PBM.name());
		taskPBMModel.setEnable(1);
		taskPBMModel.setRepeat(1);
		taskPBMModel.setSampleInterval(1);
		for (int i = 0; i < task.size(); i++) {
			String line = task.get(i);

			/** 解析服务器地址 */
			if (line.startsWith("server_ip")) {
				taskPBMModel.setServerIP(getValue(line));
			}
			if (line.startsWith("server_port")) {
				taskPBMModel.setServerPort(getInt(getValue(line)));
			}
			// }
			if (line.startsWith("interval")) {
				taskPBMModel.setInterVal(getInt(getValue(line)));
			}
			if (line.startsWith("duration_s")) {
				taskPBMModel.setDuration(getInt(getValue(line)));
			}
			if (line.startsWith("PPPPolicy")) {
				taskPBMModel.setDisConnect(pppRuleUmpcToDevice(getInt(getValue(line))));
			}
			if (line.startsWith("up_sample_ratio")) {
				taskPBMModel.setUpSampleRatio(getInt(getValue(line)));
			}
			if (line.startsWith("down_sample_ratio")) {
				taskPBMModel.setDownSampleRatio(getInt(getValue(line)));
			}
			if (line.startsWith("nodata_timeout_s")) {
				taskPBMModel.setNodataTimeout(getInt(getValue(line)));
			}
			if (line.startsWith("MultiTest=")) {
				taskPBMModel.setIsRab(getInt(getValue(line)));
			}
			if (line.startsWith("MultiName=")) {
				taskPBMModel.setRabName(getValue(line));
			}

		}
		return taskPBMModel;
	}

	/**
	 * String int转换
	 * 
	 * @param str
	 *          要转换的String
	 * @return 异常时返回99000
	 */
	private int getInt(String str) {
		try {
			int result = Integer.parseInt(str);
			return result > 0 ? result : -result;
		} catch (Exception e) {
			return 99 * 1000;
		}
	}

	/**
	 * 拨号类型ID转换 UMPC中拨号类型为0每次断开，1任务完成断开，2从不断开 终端中设定0从不断开，1每次断开，2任务完成断开
	 * 因该设定中的0，1与之前终端的设置一致，故终端不修改为UMPC的编号，此处加转换
	 * 
	 * @author tangwq
	 * @param fromUmpc
	 * @return
	 */
	private int pppRuleUmpcToDevice(int fromUmpc) {
		int toDevice = fromUmpc + 1;
		if (toDevice > 2)
			toDevice = 0;
		return toDevice;
	}

	/**
	 * 平台只有两种拨号类型 勾选为每次断开 不勾选为任务断开 默认为从不断开
	 * 
	 * 终端中设定0从不断开，1每次断开，2任务完成断开
	 * 
	 * @return
	 */
	private int pppRuleFleetToDevice(int fromFleet) {
		int toDevice = fromFleet;
		if (toDevice == 0)
			toDevice = 2;
		return toDevice;
	}

}
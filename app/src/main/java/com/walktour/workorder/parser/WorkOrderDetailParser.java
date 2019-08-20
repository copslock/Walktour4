package com.walktour.workorder.parser;

import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ConfigUrl;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.model.FtpServerModel;
import com.walktour.model.UrlModel;
import com.walktour.workorder.model.WorkOrderDetail;
import com.walktour.workorder.model.WorkSubItem;
import com.walktour.workorder.model.WorkSubItem.CommandItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * 工单详细解析器
 * 注意1：为保证解析后返回的任务模型TaskModel中的taskName不重复，用<ItemsID>+<CommandID>+业务名称，如136136_1_MOC
 * Author: ZhengLei
 *   Date: 2013-6-7 下午4:33:47
 */
public class WorkOrderDetailParser extends XmlParser {
	private WorkOrderDetail workOrderDetail;

	public WorkOrderDetailParser(Reader reader) {
		super(reader);
	}

	public WorkOrderDetailParser(String fileName) {
		super(fileName);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void parse() {
		WorkSubItem workSubItem = null;
		CommandItem commandItem = null;
		TaskInitiativeCallModel callTaskModel = null;
		TaskFtpModel ftpTaskModel = null;
		TaskHttpPageModel httpTaskModel = null;
		TaskHttpPageModel httpTaskModelLogin = null;
		TaskPingModel pingTaskModel = null;
		TaskVideoPlayModel videoPlayModel = null;
		try {
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: // 判断当前事件是否是文档开始事件

						break;
					case XmlPullParser.START_TAG: // 判断当前事件是否是标签元素开始事件
						if("WorkOrderInfo".equals(parser.getName())) { // 判断开始标签元素是否是WorkOrderInfo
							workOrderDetail = new WorkOrderDetail();
						}
						if(workOrderDetail != null) {
							if("WorkID".equals(parser.getName())) {
								workOrderDetail.setWorkId(Integer.parseInt(parser.nextText()));
							} else if("WorkName".equals(parser.getName())) {
								workOrderDetail.setWorkName(parser.nextText());
							} else if("WorkArea".equals(parser.getName())) {
								workOrderDetail.setWorkArea(Integer.parseInt(parser.nextText()));
							} else if("WorkType".equals(parser.getName())) {
								workOrderDetail.setWorkType(Integer.parseInt(parser.nextText()));
							} else if("ProjectID".equals(parser.getName())) {
								workOrderDetail.setProjectId(Integer.parseInt(parser.nextText()));
							} else if("ProjectName".equals(parser.getName())) {
								workOrderDetail.setProjectName(parser.nextText());
							} else if("PlanEndTime".equals(parser.getName())) {
								workOrderDetail.setPlanEndTime(parser.nextText());
							} else if("SenderAccount".equals(parser.getName())) {
								workOrderDetail.setSenderAccount(parser.nextText());
							} else if("ProvinceID".equals(parser.getName())) {
								workOrderDetail.setProvinceId(Integer.parseInt(parser.nextText()));
							} else if("CityID".equals(parser.getName())) {
								workOrderDetail.setCityId(Integer.parseInt(parser.nextText()));
							} else if("AreaID".equals(parser.getName())) {
								workOrderDetail.setAreaId(Integer.parseInt(parser.nextText()));
							} else if("TestSite".equals(parser.getName())) {
								workOrderDetail.setTestSite(parser.nextText());
							} else if("TestBuilding".equals(parser.getName())) {
								workOrderDetail.setTestBuilding(parser.nextText());
							} else if("Address".equals(parser.getName())) {
								workOrderDetail.setAddress(parser.nextText());
							} else if("SiteSum".equals(parser.getName())) {
								workOrderDetail.setSiteSum(Integer.parseInt(parser.nextText()));
							} else if("BuildingSum".equals(parser.getName())) {
								workOrderDetail.setBuildingSum(Integer.parseInt(parser.nextText()));
							} else if("NetType".equals(parser.getName())) {
								workOrderDetail.setNetType(Integer.parseInt(parser.nextText()));
							} else if("isrecived".equals(parser.getName())) {
								workOrderDetail.setIsReceived(Integer.parseInt(parser.nextText()));
							}
						}

						// 开始工单任务命令的解析
						if("WorkSubItems".equals(parser.getName())) {
							workSubItem = new WorkSubItem();
						}

						if(workSubItem != null) {
							if("ItemsID".equals(parser.getName())) {
								workSubItem.setItemId(Integer.parseInt(parser.nextText()));
							} else if("ItemsName".equals(parser.getName())) {
								workSubItem.setItemName(parser.nextText());
							} else if("TestContent".equals(parser.getName())) {
								workSubItem.setTestContent(parser.nextText());
							} else if("SiteID".equals(parser.getName())) {
								workSubItem.setSiteId(parser.nextText());
							} else if("SiteName".equals(parser.getName())) {
								workSubItem.setSiteName(parser.nextText());
							} else if("SiteGPSLat".equals(parser.getName())) {
								workSubItem.setSiteGpsLat(Double.parseDouble(parser.nextText()));
							} else if("SiteGPSLon".equals(parser.getName())) {
								workSubItem.setSiteGpsLon(Double.parseDouble(parser.nextText()));
							} else if("SiteAddress".equals(parser.getName())) {
								workSubItem.setSiteAddress(parser.nextText());
							} else if("TestFloors".equals(parser.getName())) {
								workSubItem.setTestFloors(parser.nextText());
							} else if("TestScene".equals(parser.getName())) {
								workSubItem.setTestScene(parseInt(parser.nextText(), 1));
							} else if("TestType".equals(parser.getName())) {
								workSubItem.setTestType(parseInt(parser.nextText(), 1));
							} else if("FloorMap".equals(parser.getName())) {
								workSubItem.setFloorMap(parser.nextText());
							} else if("ServerType".equals(parser.getName())) {
								workSubItem.setServerType(parseInt(parser.nextText(), 1));
							} else if("LoopSum".equals(parser.getName())) {
								workSubItem.setLoopSum(Integer.parseInt(parser.nextText()));
							} else if("LoopInterval".equals(parser.getName())) {
								workSubItem.setLoopInterval(Integer.parseInt(parser.nextText()));
							} else if("ItemsCount".equals(parser.getName())) {
								workSubItem.setItemsCount(Integer.parseInt(parser.nextText()));
							}
						}

						// 解析到<CommandItem>标签
						if("CommandItem".equals(parser.getName())) {
							commandItem = workSubItem.new CommandItem(); // new 内部类
						}
						if(commandItem != null) {
							if("CommandID".equals(parser.getName())) {
								commandItem.setCommandID(Integer.parseInt(parser.nextText()));
							} else if("CommandName".equals(parser.getName())) {
								commandItem.setCommandName(parser.nextText());
							} else if("CommandOrder".equals(parser.getName())) {
								commandItem.setCommandOrder(Integer.parseInt(parser.nextText()));
							}
						}

						// 解析到<CommandCall>标签
						if("CommandCall".equals(parser.getName())) {
							callTaskModel = new TaskInitiativeCallModel();
							commandItem.setCommandDesc("Call");

						}
						if(callTaskModel != null) {
							// 设置任务名称、类型
							callTaskModel.setTaskName(workSubItem.getItemId() + "_" + commandItem.getCommandID() + "_" + "MOC");
							callTaskModel.setTaskType(WalkStruct.TaskType.InitiativeCall.name());
							callTaskModel.setKeepTime(60);
							if("CallTypeID".equals(parser.getName())) {
								// 暂无对应
							} else if("CallDuration".equals(parser.getName())) {
//								callTaskModel.setKeepTime(Integer.parseInt(parser.nextText()));
							} else if("CallDirection".equals(parser.getName())) {
								// 暂无对应
							} else if("CalledNumber".equals(parser.getName())) {
								callTaskModel.setCallNumber(parser.nextText());
							} else if("CallAccessTime".equals(parser.getName())) {
								callTaskModel.setConnectTime(Integer.parseInt(parser.nextText()));
							} else if("RepeatCount".equals(parser.getName())) {
								callTaskModel.setRepeat(Integer.parseInt(parser.nextText()));
							} else if("RepeatInterval".equals(parser.getName())) {
								callTaskModel.setInterVal(Integer.parseInt(parser.nextText()));
							} else if("CommandOrder".equals(parser.getName())) {
								// 暂无对应
							}
						}

						// 解析到<CommandFtp>标签
						if("CommandFtp".equals(parser.getName())) {
							//TODO:此模块作废,默认值
							ftpTaskModel = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
							ftpTaskModel.setDisConnect(1);
							ftpTaskModel.setNoAnswer(60);
						}
						if(ftpTaskModel != null) {
							// PsCall默认按照时间
							ftpTaskModel.setPsCall(1);

							if("ServerIP".equals(parser.getName())) {
								ftpTaskModel.setFtpServer(parser.nextText()); // 以ip作为名称
							} else if("ServerPort".equals(parser.getName())) {
								ftpTaskModel.setPort(Integer.parseInt(parser.nextText()));
							} else if("Account".equals(parser.getName())) {
								ftpTaskModel.setUser(parser.nextText());
							} else if("Password".equals(parser.getName())) {
								ftpTaskModel.setPass(parser.nextText());
								addFtp(ftpTaskModel); // 将Ftp添加到设置的Ftp列表中
							} else if("LoadedType".equals(parser.getName())) {
								String strValue = parser.nextText();
								if(strValue!=null && !"".equals(strValue)) {
									int intValue = Integer.parseInt(strValue);
									String prefix = workSubItem.getItemId() + "_" + commandItem.getCommandID() + "_";
									switch (intValue) {
										case 1:
											ftpTaskModel.setTaskName(prefix + "FtpUpload");
											commandItem.setCommandDesc("FtpUpload");
											ftpTaskModel.setTaskType(WalkStruct.TaskType.FTPUpload.name());
											// 设置为自动上传2M文件
											ftpTaskModel.setFileSource(1);
											ftpTaskModel.setFileSize(1000 * 2);
											break;
										case 2:
											ftpTaskModel.setTaskName(prefix + "FtpDownload");
											commandItem.setCommandDesc("FtpDownload");
											ftpTaskModel.setTaskType(WalkStruct.TaskType.FTPDownload.name());
											break;
										default:
											break;
									}
								}
							} else if("TransModeID".equals(parser.getName())) {
								// 暂无对应
							} else if("OverTime".equals(parser.getName())) { // 登录超时
								ftpTaskModel.setLoginTimeOut(parseInt(parser.nextText(), 60));
							} else if("IsPassive".equals(parser.getName())) {
//								ftpTaskModel.setPassive(Integer.parseInt(parser.nextText()));				暂时屏蔽
							} else if("LocalFile".equals(parser.getName())) {
								// 暂不处理
//								ftpTaskModel.setLocalFile(parser.nextText());
							} else if("ServerFile".equals(parser.getName())) {
								String value = parser.nextText();
								String taskType = ftpTaskModel.getTaskType();
								if(taskType.equals(WalkStruct.TaskType.FTPUpload)) { // 如果是Ftp上传，则取/download/3g.rar中的/download/
									ftpTaskModel.setRemoteFile(value.substring(0, value.lastIndexOf("/")));
								} else {
									ftpTaskModel.setRemoteFile(value);
								}
							} else if("IsValid".equals(parser.getName())) {
								// 暂无对应
							} else if("IsLimitChk".equals(parser.getName())) {
								// 暂无对应
							} else if("NoDataOverTime".equals(parser.getName())) { // 无流量（无响应）超时
//								ftpTaskModel.setNoAnswer(parseInt(parser.nextText(), 30));
							} else if("RepeatCount".equals(parser.getName())) {
								ftpTaskModel.setRepeat(Integer.parseInt(parser.nextText()));
							} else if("RepeatInterval".equals(parser.getName())) {
								ftpTaskModel.setInterVal(Integer.parseInt(parser.nextText()));
							} else if("ExecuteTime".equals(parser.getName())) { // 执行超时
								ftpTaskModel.setTimeOut(parseInt(parser.nextText(), 60));
							} else if("CommandOrder".equals(parser.getName())) {
								// 暂无对应
							}
						}

						// 解析到<CommandHttp>标签
						if("CommandHttp".equals(parser.getName())) {
							httpTaskModel = new TaskHttpPageModel(WalkStruct.TaskType.HttpDownload.name());
							commandItem.setCommandDesc("HttpDownload");
						}
						if(httpTaskModel != null) {
							// 设置任务名称、类型
							httpTaskModel.setTaskName(workSubItem.getItemId() + "_" + commandItem.getCommandID() + "_" + "HttpDownload");
							httpTaskModel.setTaskType(WalkStruct.TaskType.HttpDownload.name());
							httpTaskModel.setDisConnect(1);
							httpTaskModel.setTimeOut(300);
							httpTaskModel.setReponse(30);
							if("NetAddress".equals(parser.getName())) {
								httpTaskModel.setXmlUrl(parser.nextText());
							} else if("LocalFile".equals(parser.getName())) {
								// 有疑问
							} else if("RepeatCount".equals(parser.getName())) {
								httpTaskModel.setRepeat(Integer.parseInt(parser.nextText()));
							} else if("RepeatInterval".equals(parser.getName())) {
								httpTaskModel.setInterVal(Integer.parseInt(parser.nextText()));
							} else if("ExecuteTime".equals(parser.getName())) { // 执行超时
//								httpTaskModel.setTimeOut(parseInt(parser.nextText(), 60));
							} else if("OverTime".equals(parser.getName())) { // 登录超时
								// Walktour写死了，暂时没有对应的
							} else if("NoDataOverTime".equals(parser.getName())) { // 无流量（无响应）超时
//								httpTaskModel.setReponse(parseInt(parser.nextText(), 30));
							} else if("CommandOrder".equals(parser.getName())) {
								// 暂无对应
							}
						}

						// 解析到<CommandPing>标签
						if("CommandPing".equals(parser.getName())) {
							pingTaskModel = new TaskPingModel();
							commandItem.setCommandDesc("Ping");
						}
						if(pingTaskModel != null) {
							// 设置任务名称、类型
							pingTaskModel.setTaskName(workSubItem.getItemId() + "_" + commandItem.getCommandID() + "_" + "Ping");
							pingTaskModel.setTaskType(WalkStruct.TaskType.Ping.name());
							pingTaskModel.setDisConnect(0);
							if("ServerIP".equals(parser.getName())) {
								pingTaskModel.setIp(parser.nextText());
							} else if("PackageSize".equals(parser.getName())) {
								pingTaskModel.setSize(Integer.parseInt(parser.nextText()));
							} else if("PingCount".equals(parser.getName())) {
								// 跳过
							} else if("RepeatCount".equals(parser.getName())) {
								pingTaskModel.setRepeat(Integer.parseInt(parser.nextText()));
							} else if("RepeatInterval".equals(parser.getName())) {
								pingTaskModel.setInterVal(Integer.parseInt(parser.nextText()));
							} else if("CommandOrder".equals(parser.getName())) {
								// 暂无对应
							}
						}

						//解析到<CommandBrowse>标签
						if("CommandBrowse".equals(parser.getName())) {
							httpTaskModelLogin = new TaskHttpPageModel(WalkStruct.TaskType.HttpRefurbish.name());
							commandItem.setCommandDesc("HttpLogon");
							httpTaskModelLogin.setDisConnect(1);
						}

						if(httpTaskModelLogin != null){
							httpTaskModelLogin.setTaskName(workSubItem.getItemId() + "_" + commandItem.getCommandID() + "_" + "HttpLogon");
							httpTaskModelLogin.setTaskType(WalkStruct.TaskType.Http.name());
							if("BrowseAddress".equals(parser.getName())){
								String urlAddress = parser.nextText();
								ArrayList<UrlModel> urlModel=new ArrayList<UrlModel>();
								ConfigUrl urlList = new ConfigUrl();
								UrlModel uModel1 = new UrlModel();
								uModel1.setEnable("0");
								uModel1.setName(urlAddress.toLowerCase().startsWith("http://") ? urlAddress : "http://" + urlAddress);
								if (!urlList.contains(uModel1.getName())) {
									urlList.addUrl(uModel1);
								}
								urlModel.add(uModel1);
								httpTaskModelLogin.setUrlModelList(urlModel);
								if (parser.getEventType() != XmlPullParser.END_TAG) {
									parser.nextTag();
								}

							}else if("OverTime".equals(parser.getName())){
								httpTaskModelLogin.setTimeOut(Integer.valueOf(parser.nextText()));
							}else if("RepeatInterval".equals(parser.getName())){
								httpTaskModelLogin.setInterVal(Integer.valueOf(parser.nextText()));
							}
							else if("RepeatCount".equals(parser.getName())){
								httpTaskModelLogin.setRepeat(Integer.valueOf(parser.nextText()));
							}
						}


						//解析到<CommandVideo>标签
						if("CommandVideo".equals(parser.getName())) {
							videoPlayModel = new TaskVideoPlayModel();
							commandItem.setCommandDesc("VideoPlay");
							videoPlayModel.setVideoType(1);
							videoPlayModel.setPlayType(1);
							videoPlayModel.setMaxBufCounts(3);
							videoPlayModel.setMaxBufferTimeout(10);
							videoPlayModel.setDisConnect(1);
						}

						if(videoPlayModel != null){
							videoPlayModel.setTaskName(workSubItem.getItemId() + "_" + commandItem.getCommandID() + "_" + "VideoPlay");
							videoPlayModel.setTaskType(WalkStruct.TaskType.HTTPVS.name());
							if("VideoAddress".equals(parser.getName())){
								videoPlayModel.setUrl(parser.nextText());
							}else if("RepeatCount".equals(parser.getName())){
								videoPlayModel.setRepeat(Integer.parseInt(parser.nextText()));
							}else if("RepeatInterval".equals(parser.getName())){
								videoPlayModel.setInterVal(Integer.parseInt(parser.nextText()));
							}else if("ExecuteTime".equals(parser.getName())){
								videoPlayModel.setPlayTimeout(Integer.parseInt(parser.nextText()));
							}else if("OverTime".equals(parser.getName())){
								videoPlayModel.setNoDataTimeout(Integer.parseInt(parser.nextText()));
							}
						}

						break; // end "case XmlPullParser.START_TAG:"
					case XmlPullParser.END_TAG: // 判断当前事件是否是标签元素结束事件
						if("CommandCall".equals(parser.getName()) && commandItem!=null) {
							commandItem.setTaskModel(callTaskModel);
							callTaskModel = null;
						}
						if("CommandFtp".equals(parser.getName()) && commandItem!=null) {
							commandItem.setTaskModel(ftpTaskModel);
							ftpTaskModel = null;
						}
						if("CommandHttp".equals(parser.getName()) && commandItem!=null) {
							commandItem.setTaskModel(httpTaskModel);
							httpTaskModel = null;
						}
						if("CommandPing".equals(parser.getName()) && commandItem!=null) {
							commandItem.setTaskModel(pingTaskModel);
							pingTaskModel = null;
						}
						if("CommandBrowse".equals(parser.getName()) && commandItem!=null) {
							commandItem.setTaskModel(httpTaskModelLogin);
							httpTaskModelLogin = null;
						}

						if("CommandVideo".equals(parser.getName()) && commandItem!=null) {
							commandItem.setTaskModel(videoPlayModel);
							videoPlayModel = null;
						}
						if("CommandItem".equals(parser.getName()) && workSubItem!=null) {
							workSubItem.getCommandItems().add(commandItem);
							commandItem = null;
						}
						if("WorkSubItems".equals(parser.getName()) && workOrderDetail!=null) {
							workOrderDetail.getWorkSubItems().add(workSubItem);
							workSubItem = null;
						}
						if("WorkOrderInfo".equals(parser.getName())) { // 判断结束标签元素是否是WorkOrderInfo
							workSubItem = null;
						}
						break;
					default:
						break;
				} // end switch

				// 进入下一个元素并触发相应事件
				eventType = parser.next();

			} // end while
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public WorkOrderDetail getParseResult() {
		return workOrderDetail;
	}

	/**
	 * 添加解析后的Ftp到Ftp设置列表中
	 * @param ftpTaskModel ftp模型类
	 */
	private void addFtp(TaskFtpModel ftpTaskModel) {
		ConfigFtp configFtp = new ConfigFtp();
		String ftpIp = ftpTaskModel.getFtpServerName();
		FtpServerModel ftpServerModel = new FtpServerModel(
				ftpIp,
				ftpIp,
				ftpTaskModel.getPort()+"",
				ftpTaskModel.getUser(),
				ftpTaskModel.getPass());
		if(!configFtp.contains(ftpIp)) {
			configFtp.addFtp(ftpServerModel);
		}
	}

}

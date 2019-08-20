package com.walktour.gui.task.parsedata.xml.btu;

import android.content.Context;

import com.walktour.Utils.PPPRule;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigFtp;
import com.walktour.control.config.ConfigUrl;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.email.receive.TaskEmailPop3Model;
import com.walktour.gui.task.parsedata.model.task.email.send.TaskEmailSmtpModel;
import com.walktour.gui.task.parsedata.model.task.ftp.TaskFtpModel;
import com.walktour.gui.task.parsedata.model.task.http.page.TaskHttpPageModel;
import com.walktour.gui.task.parsedata.model.task.idle.TaskEmptyModel;
import com.walktour.gui.task.parsedata.model.task.mms.receive.TaskMmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.mms.send.TaskMmsSendModel;
import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;
import com.walktour.gui.task.parsedata.model.task.mtc.TaskPassivityCallModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;
import com.walktour.gui.task.parsedata.model.task.ping.TaskPingModel;
import com.walktour.gui.task.parsedata.model.task.sms.receive.TaskSmsReceiveModel;
import com.walktour.gui.task.parsedata.model.task.sms.send.TaskSmsSendModel;
import com.walktour.gui.task.parsedata.model.task.videoplay.TaskVideoPlayModel;
import com.walktour.gui.task.parsedata.model.task.wlan.ap.TaskWlanApModel;
import com.walktour.gui.task.parsedata.model.task.wlan.eteauth.TaskWlanEteAuthModel;
import com.walktour.gui.task.parsedata.model.task.wlan.login.TaskWlanLoginModel;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;
import com.walktour.model.FtpServerModel;
import com.walktour.model.UrlModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * TaskConverter
 * Btu平台的测试计划转换器，从BTU平台的Command转换到Walktour的{@link TaskModel}
 * 2014-2-28 上午9:49:19
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
public class TaskConverter {
	private Context mContext;
	private File xmlFile ;
	/** WLAN AP关联测试命令 */
	private final String WLAN_AP = "0x0901";
	/** WLAN WEB用户认证测试命令 */
	private final String WLAN_WEB = "0x0902";
	/** WLAN Http网站访问测试命令 */
	private final String WLAN_HTTP = "0x0903";
	/** WLAN ETE_AUTH端到端认证测试 */
	private final String WLAN_ETE_AUTH = "0x0907";
	/** WLAN FTP下载/上传测试命令 */
	private final String WLAN_FTP_DOWNUP = "0x0904";
	/** WLAN PING命令 */
	private final String WLAN_PING = "0x0905";
	/**测试计划来自IPACK下发*/
	private boolean planFromiPack	= false;
	
	/**
	 * 
	 * 创建一个新的实例 TaskConverter.
	 * 
	 * @param xmlFile 从BTU平台下载的测试计划Xml文件
	 */
	public TaskConverter(Context context,File xmlFile){
		this.mContext 		= context;
		this.xmlFile 		= xmlFile;
		this.planFromiPack 	= false;
	}
	
	/**
	 * 
	 * 创建一个新的实例 TaskConverter.
	 * 
	 * @param xmlFile 从BTU平台下载的测试计划Xml文件
	 */
	public TaskConverter(Context context,File xmlFile,boolean fromiPack){
		this.mContext 		= context;
		this.xmlFile 		= xmlFile;
		this.planFromiPack 	= fromiPack;
	}
	
	/**
	 * @return xml文件转化成doc
	 * */
	private Document getDocument() {
		Document doc = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream in = new FileInputStream( xmlFile );
			doc = docBuilder.parse(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return doc;
	}
	
	/**
	 * 函数功能：根据tagName返回Node的值
	 * @param element  
	 * @param tagName  形如<Enable>1</Enable>的tag
	 * @return
	 */
	private String getNodeString(Element element,String tagName){
		String value = "";
		try{
			value = element.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 函数功能：根据tagName返回Node的值
	 * @param element  
	 * @param tagName  形如<Enable>1</Enable>的tag
	 * @return
	 */
	private int getNodeInteger(Element element,String tagName){
		int value = 0;
		try{
			String str = element.getElementsByTagName(tagName).item(0).getFirstChild()
							.getNodeValue();
			value = Integer.parseInt(str);
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
	private String getAttributeString(Node node,String name){
		String result = "";
		try{
			result = node.getAttributes().getNamedItem(name).getNodeValue() ;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	private int getAttributeInteger(Node node,String name){
		int result = 0;
		try{
			result = Integer.parseInt( getAttributeString(node, name) );
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	private long getDateByFormat(String formatTime){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		
		try {
			Date date = simpleDateFormat.parse( formatTime );
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 函数功能：时分格式到该时间距离凌晨的毫秒数
	 * @param fmt 时分格式mm:ss
	 * @return
	 */
	private long getTimeByFmt(String fmt){
		long time = 0;
		try{
			String hour = fmt.split(":")[0];
			String min = fmt.split(":")[1];
			final int Hour = 60*60*1000;
			final int Minute = 60*1000; 
			time = Integer.parseInt(hour)*Hour + Integer.parseInt(min)*Minute; 
		}catch(Exception e){
			e.printStackTrace();
		}
		return time;
	}
	
	/**
	 * 函数功能：生成语音主叫Model
	 * 
	 * @param element
	 *            <Command Repeat="3"> 
	 *            	<ID>0x0500</ID>
	 *            	<CallNumber>10086</CallNumber> 
	 *            	<RandomCall>0</RandomCall>
	 *            	<Duration>180</Duration> 
	 *            	<Interval>20</Interval>
	 *            	<MaxTime>10</MaxTime> 
	 *            	<TestMOS>1</TestMOS>
	 *            	<CallMOSServer>0</CallMOSServer> 
	 *            	<MOSLimit>2.0</MOSLimit>
	 *            </Command>
	 * @return
	 */
	 private void createMocCallModel(Element element, List<TaskModel> listTaskModel) {
		 TaskInitiativeCallModel callModel = new TaskInitiativeCallModel();
		 callModel.setTaskType( WalkStruct.TaskType.InitiativeCall.name() );
		 callModel.setTaskName( mContext.getString(R.string.act_task_initiativecall ) );
		 callModel.setCallNumber( getNodeString(element, "CallNumber"));
		 callModel.setKeepTime( getNodeInteger(element,"Duration") );
		 callModel.setInterVal( getNodeInteger(element,"Interval") );
		 callModel.setConnectTime( 60 );//BTU没有这个选项
		 callModel.setRealtimeCalculation(true);
		 callModel.setMosTest( getNodeInteger(element,"TestMOS")==1 ?
				 TaskModel.MOS_ON : TaskModel.MOS_OFF );
		 if( callModel.getMosTest()==TaskModel.MOS_ON ){
			 callModel.setCallMOSTestType( getNodeInteger(element,"CallMOSServer") );
			 callModel.setCallMOSCount( TaskModel.MOS_POLQA );
			 callModel.setPolqaSample(TaskModel.POLQA_48K);
			 callModel.setPolqaCalc(1);
		 }else{
			 callModel.setCallMOSTestType( TaskModel.MOS_M2M );
			 callModel.setCallMOSCount( TaskModel.MOS_PESQ );
			 callModel.setPolqaSample(TaskModel.POLQA_8K);
			 callModel.setPolqaCalc(1);
		 }
		 
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			callModel.setRepeat(repeat);
			callModel.setEnable(1);
			callModel.setInterVal(getNodeInteger(element, "Interval"));
			callModel.setEditType(TaskModel.EDIT_TYPE_BTU);
			
			//如果测试计划来自iPack，那么主被叫的测试计划都设置主被叫联合
			if(planFromiPack){
				callModel.setUnitTest(true);
			}
			listTaskModel.add(callModel);
	 }
	 
	 /**
		 * 函数功能：生成语音主叫Model
		 * 
		 * @param element
		*	<Command Repeat="999">
		*		<ID>0x0700</ID>
		*		<PhoneNumber>1008611</PhoneNumber>
		*		<DialMode>1</DialMode>
		*		<RandomCall>0</RandomCall>
		*		<Duration>15</Duration>
		*		<Interval>20</Interval>
		*		<MaxTime>180</MaxTime>
		*		<TimeOut>180</TimeOut>
		*		<TestVMOS>0</TestVMOS>
		*		<CallVMOSServer>0</CallVMOSServer>
		*		<MOS-ILimit>2.8</MOS-ILimit>
		*		<SampleFile>null</SampleFile>
		*	</Command>
		 * @return
		 */
		 private void createVideoMocCallModel(Element element, List<TaskModel> listTaskModel) {
			 TaskInitiativeCallModel callModel = new TaskInitiativeCallModel();
			 callModel.setTaskType( WalkStruct.TaskType.InitiativeCall.name() );
			 callModel.setTaskName( mContext.getString(R.string.act_task_initiativecall ) );
			 callModel.setCallNumber( getNodeString(element, "PhoneNumber"));
			 callModel.setKeepTime( getNodeInteger(element,"Duration") );
			 callModel.setInterVal( getNodeInteger(element,"Interval") );
			 callModel.setConnectTime( 60 );//BTU没有这个选项
			 callModel.setCallMode(1);		//视频电话该值为1
			 callModel.setRealtimeCalculation(true);
			 callModel.setMosTest( getNodeInteger(element,"TestVMOS")==1 ?
					 TaskModel.MOS_ON : TaskModel.MOS_OFF );
			 if( callModel.getMosTest()==TaskModel.MOS_ON ){
				 callModel.setCallMOSTestType( getNodeInteger(element,"CallVMOSServer") );
				 callModel.setCallMOSCount( TaskModel.MOS_POLQA );
				 callModel.setPolqaSample(TaskModel.POLQA_48K);
				 callModel.setPolqaCalc(1);
			 }else{
				 callModel.setCallMOSTestType( TaskModel.MOS_M2M );
				 callModel.setCallMOSCount( TaskModel.MOS_PESQ );
				 callModel.setPolqaSample(TaskModel.POLQA_8K);
				 callModel.setPolqaCalc(1);
			 }
			 
				int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
				callModel.setRepeat(repeat);
				callModel.setEnable(1);
				callModel.setInterVal(getNodeInteger(element, "Interval"));
				callModel.setEditType(TaskModel.EDIT_TYPE_BTU);
				listTaskModel.add(callModel);
		 }
	 
	 /**
	 * 函数功能：
	 * 
	 * @param element
	 *            <Command Repeat="3"> 
	 *            	<ID>0x0501</ID> 
	 *            	<TestMOS>1</TestMOS>
	 *            	<MOSLimit>2.0
	 *            	</MOSLimit> 
	 *            </Command>
	 * @return
	 */
	 private void createMtcCallModel(Element element, List<TaskModel> listTaskModel) {
		 TaskPassivityCallModel psCallModel = new TaskPassivityCallModel();
		 psCallModel.setTaskName( mContext.getString(R.string.act_task_passivitycall));
		 psCallModel.setTaskType( WalkStruct.TaskType.PassivityCall.name() );
		 psCallModel.setInterVal( 3 );//默认是3秒
		 psCallModel.setCallMOSServer( getNodeInteger(element,"TestMOS")==1 ?
				 TaskModel.MOS_ON : TaskModel.MOS_OFF );//是否进行MOS值评估
		 if (psCallModel.getCallMOSServer()==TaskModel.MOS_ON){
			 psCallModel.setCallMOSCount( TaskModel.MOS_POLQA );
			 psCallModel.setPolqaSample(TaskModel.POLQA_48K);
		 }
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			psCallModel.setRepeat(repeat);
			psCallModel.setEnable(1);
			int vx=getNodeInteger(element, "Interval");
			if(vx==0){
				psCallModel.setInterVal(15);
			}else{
				psCallModel.setInterVal(vx);
			}
			psCallModel.setEditType(TaskModel.EDIT_TYPE_BTU);
			//如果测试计划来自iPack，那么主被叫的测试计划都设置主被叫联合
			if(planFromiPack){
				psCallModel.setUnitTest(true);
			}
			listTaskModel.add(psCallModel);
	 }
	 
	 /**
		 * 函数功能：
		 * 
		 * @param element
		 *            <Command Repeat="3"> 
		 *            	<ID>0x0501</ID> 
		 *            	<TestMOS>1</TestMOS>
		 *            	<MOSLimit>2.0
		 *            	</MOSLimit> 
		 *            </Command>
		 * @return
		 */
		 private void createVideoMtcCallModel(Element element, List<TaskModel> listTaskModel) {
			 TaskPassivityCallModel psCallModel = new TaskPassivityCallModel();
			 psCallModel.setTaskName( mContext.getString(R.string.act_task_passivitycall));
			 psCallModel.setTaskType( WalkStruct.TaskType.PassivityCall.name() );
			 psCallModel.setInterVal( 3 );	//默认是3秒
			 psCallModel.setCallMode(1);	//视频电话该值为1
			 psCallModel.setCallMOSServer( getNodeInteger(element,"TestVMOS")==1 ?
					 TaskModel.MOS_ON : TaskModel.MOS_OFF );//是否进行MOS值评估
			 if (psCallModel.getCallMOSServer()==TaskModel.MOS_ON){
				 psCallModel.setCallMOSCount( TaskModel.MOS_POLQA );
				 psCallModel.setPolqaSample(TaskModel.POLQA_48K);
			 }
				int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
				psCallModel.setRepeat(repeat);
				psCallModel.setEnable(1);
				psCallModel.setInterVal(getNodeInteger(element, "Interval"));
				psCallModel.setEditType(TaskModel.EDIT_TYPE_BTU);
				listTaskModel.add(psCallModel);
		 }
	 
	 /**
	 * 函数功能：生成短信发送Model
	 * 
	 * @param element
	 *            <Command Repeat="3"> 
	 *            	<ID>0x0609</ID>
	 *            	<ServerCenterAddress>10086</ServerCenterAddress>
	 *            	<Destination>13923361292</Destination> 
	 *            	<TimeOut>10</TimeOut>
	 *            	<Mode>1</Mode>
	 *            	<Text>0</Text> 
	 *            	<Report>0</Report> 
	 *            	<Content>this is sms test</Content> 
	 *            	<Interval>30</Interval> 
	 *            </Command>
	 * @return
	 */
	 private void createSmsSendModel(Element element, List<TaskModel> listTaskModel) {
		 TaskSmsSendModel model =  new TaskSmsSendModel();
		 //默认参数：
		 model.setTaskName( mContext.getString( R.string.act_task_smssend));
		 model.setTaskType( WalkStruct.TaskType.SMSSend.name() );
		 model.setContent( getNodeString(element, "Content"));
		 model.setDesNumber( getNodeString(element,"Destination") );
		 model.setInterVal( getNodeInteger(element, "Interval"));
		 model.setServerNumber( getNodeString(element, "ServerCenterAddress"));
		 model.setTimeOut( getNodeInteger(element, "TimeOut"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
	 }
	 
	 /**
	  * 函数功能：生成短信接收Model
	  * @param element
	  * 	 <Command Repeat="1">
                        <ID>0x0609</ID>
                        <ServerCenterAddress>10086</ServerCenterAddress>
                        <Destination>13923361292</Destination>
                        <TimeOut>1</TimeOut>
                        <Mode>0</Mode>
                        <Text>0</Text>
                        <Report>0</Report>
                        <Content>this is a test</Content>
                        <Interval>100</Interval>
                    </Command>
	  * @return
	  */
	 private void createSmsRecvModel(Element element, List<TaskModel> listTaskModel) {
		 TaskSmsReceiveModel model = new TaskSmsReceiveModel();
		 model.setTaskName( mContext.getString(R.string.act_task_smsincept));
		 model.setTaskType( WalkStruct.TaskType.SMSIncept.name() );
		 model.setTimeOut(  getNodeInteger(element, "TimeOut") );
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
	 }
	 
	 /**
	  * 彩信发送
	  * @param element
	  * <Command Repeat="3">
                        <ID>0x060A</ID>
                        <Gateway>202.101.224.68</Gateway>
                        <Agent>aa</Agent>
                        <Port>23</Port>
                        <ConnectionMode>1</ConnectionMode>
                        <Account>admin</Account>
                        <Password>admin</Password>
                        <ServerAddress>127.0.0.1</ServerAddress>
                        <Destination>13800138000</Destination>
                        <TimeOut>10</TimeOut>
                        <SyncMSNO>0</SyncMSNO>
                        <Content>this mms test</Content>
                        <MediaFileSize>100</MediaFileSize>
                        <Interval>100</Interval>
                        <APN>3</APN>
                    </Command>
	  * @return
	  */
	 private void createMmsSendModel( Element element, List<TaskModel> listTaskModel) {
		 TaskMmsSendModel model =  new TaskMmsSendModel();
		 model.setTaskName( mContext.getString(R.string.act_task_mmssend));
		 model.setTaskType( WalkStruct.TaskType.MMSSend.name() );
		 model.setServerAddress( getNodeString(element, "ServerAddress"));
		 model.setGateway( getNodeString(element, "Gateway"));
		 model.setPort( getNodeInteger(element, "Port"));
		 model.setMediaFileSize( getNodeInteger(element, "MediaFileSize"));
		 model.setInterVal( getNodeInteger(element, "Interval"));
		 model.setTimeOut( getNodeInteger(element, "TimeOut"));
		 model.setDestination( getNodeString(element, "Destination"));
		 model.setContent( getNodeString(element, "Content"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
	 }
	 
	 /**
	  * 函数功能：彩信接收
	  * @param element
	  *  <Command Repeat="1">
                        <ID>0x060B</ID>
                        <SyncMSNOs>0</SyncMSNOs>
                        <PTimeOut>5</PTimeOut>
                        <TimeOut>600</TimeOut>
                        <Agent>aa</Agent>
                        <ConnectionMode>1</ConnectionMode>
                        <Account>admin</Account>
                        <Password>admin</Password>
                        <Gateway>202.101.224.68</Gateway>
                        <Port>23</Port>
                    </Command>
	  * @return
	  */
	 private void createMmsRecvModel(Element element, List<TaskModel> listTaskModel) {
		 TaskMmsReceiveModel model = new TaskMmsReceiveModel();
		 model.setTaskName( mContext.getString(R.string.act_task_mmsincept));
		 model.setTaskType( WalkStruct.TaskType.MMSIncept.name() );
		 model.setServerAddress("http://mmsc.monternet.com");
		 model.setGateway( getNodeString(element, "Gateway"));
		 model.setPort(  getNodeInteger(element, "Port") );
		 model.setPushTimeOut( getNodeInteger(element, "PTimeOut"));
		 model.setTimeOut(getNodeInteger(element, "TimeOut"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
	 }
	 
	 /**
	  * 函数功能：流媒体播放
	  * @param element
	  * <Command Repeat="1">
                        <ID>0x0611</ID>
                        <Interval>15</Interval>
                        <APN>2</APN>
                        <Version>16</Version>
                        <URL>www.youku.com</URL>
                        <Username>admin</Username>
                        <Password>admin</Password>
                        <Agent>202.101.224.68</Agent>
                        <RTP>0</RTP>
                        <RtspHttpPort>0</RtspHttpPort>
                        <LocalRTPport>5004</LocalRTPport>
                        <PreBufferLength>5000</PreBufferLength>
                        <RebufferLength>5000</RebufferLength>
                        <PlayTime>150</PlayTime>
                    </Command>
	  * @return
	  */
	 @SuppressWarnings("deprecation")
	private void createVideoStreamModel(Element element, List<TaskModel> listTaskModel) {
		TaskVideoPlayModel vp=new TaskVideoPlayModel();
		vp.setTaskName( mContext.getString(R.string.act_task_stream) );
		vp.setTaskType(WalkStruct.TaskType.HTTPVS.name()); 
		vp.setUrl( getNodeString(element, "URL"));
		vp.setVideoType( TaskVideoPlayModel.VIDEO_TYPE_YOUKU );
		vp.setVideoQuality( 1 );
		vp.setPlayType( 1);
		vp.setPlayTimerMode( 0 );
		vp.setPlayTimeout( getNodeInteger(element, "PlayTime"));
		vp.setBufTimerMode( 0);
		vp.setBufThred( getNodeInteger(element, "BufferPlayThreshold") );
		vp.setBufTime( getNodeInteger(element, "BufferLength") );
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			vp.setRepeat(repeat);
			vp.setEnable(1);
			vp.setInterVal(getNodeInteger(element, "Interval"));
			vp.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(vp);
	 }
	 
	 /**
	  * 函数功能：Http下载
	  * @param element
	  *  <Command Repeat="3">
                <ID>0x060F</ID>
                <Interval>100</Interval>
                <TimeOut>30</TimeOut>
                <APN>2</APN>
                <Port>5566</Port>
                <URL>www.download.com</URL>
                <Proxy>0</Proxy>
                <URLinterval>2</URLinterval>
                <URLrandomNum>4</URLrandomNum>
                <Mode>2</Mode>
                <FileSize>25</FileSize>
            </Command>
	  * @return
	  */
	 private void createHttpDownModel( Element element , List<TaskModel> listTaskModel) {
		 TaskHttpPageModel httpModel =  new TaskHttpPageModel(WalkStruct.TaskType.HttpDownload.name());
		 httpModel.setTaskName( mContext.getString(R.string.act_task_httpDownload));
		 httpModel.setTaskType( WalkStruct.TaskType.HttpDownload.name() );
		 httpModel.setTimeOut( getNodeInteger(element, "TimeOut"));
		 httpModel.setXmlUrl( getNodeString(element, "URL"));
		 httpModel.setReponse(30);
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
		 httpModel.setRepeat(repeat);
		 httpModel.setEnable(1);
		 httpModel.setInterVal(getNodeInteger(element, "Interval"));
		 httpModel.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(httpModel);
	 }
	 
	 /**
	  * Http登陆
	  * @param element
	  * <Command Repeat="3">
            <ID>0x060F</ID>
            <Interval>100</Interval>
            <TimeOut>30</TimeOut>
            <APN>2</APN>
            <Port>5566</Port>
            <URL />
            <Proxy>0</Proxy>
            <URLmust>www.jd.com,www.sohu.com,www.163.com</URLmust>
            <URLinterval>2</URLinterval>
            <URLrandomNum>4</URLrandomNum>
            <Mode>0</Mode>
            <FileSize>25</FileSize>
        </Command>
	  * @return
	  */
	 private void createHttpLogonModel( Element element , List<TaskModel> listTaskModel) {
		 TaskHttpPageModel httpModel =  new TaskHttpPageModel(WalkStruct.TaskType.Http.name());
		 httpModel.setTaskName( mContext.getString(R.string.act_task_httpLogon) );
		 httpModel.setTaskType( WalkStruct.TaskType.Http.name() );
		 httpModel.setTimeOut( getNodeInteger(element, "TimeOut") );
		 ArrayList<UrlModel> urlList = new ArrayList<UrlModel>();
		 
		 String url = getNodeString(element, "URL");
		 if( url.length() > 1 ){
			 urlList.add( new UrlModel(url,"1") );
		 }

		 //草你马壁的平台你改来改去烦不烦呀
//		 04-25 22:10:25.615: D/DTLogSocket(30834):                         <UrlMust>www.baidu.com,www.jd.com,www.sohu.com,www.taobao.com,www.163.com,www.sina.com.cn</UrlMust>
//		 04-25 22:10:25.615: D/DTLogSocket(30834):                         <UrlInterval>2</UrlInterval>

		 
		 String[] urlArray = getNodeString(element, "UrlMust").split(",");
		 if( urlArray.length>0 ){
			 for(String s:urlArray){
				 if( s.length() > 1 ){
					 urlList.add( new UrlModel(s,"1") );
				 }
			 }
		 }
		 
		 
		 ConfigUrl configUrl = new ConfigUrl();
		 for(UrlModel model:urlList){
			 configUrl.addUrl(model);
		 }
		 httpModel.setUrlModelList(urlList);
		 
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
		 httpModel.setRepeat(repeat);
		 httpModel.setEnable(1);
			httpModel.setInterVal(getNodeInteger(element, "Interval"));
			httpModel.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(httpModel);
	 }
	 
	 /**
	  * Ping
	  * @param element
	  *  <Command Repeat="10">
            <ID>0x0604</ID>
            <IP>192.168.1.13</IP>
            <Interval>100</Interval>
            <Packagesize>1000</Packagesize>
            <TimeOut>200</TimeOut>
            <APN>2</APN>
        </Command>
	  * @return
	  */
	 private void createPingModel(Element element, List<TaskModel> listTaskModel) {
		 TaskPingModel pingModel = new TaskPingModel();
		 
		 pingModel.setTaskName( "Ping" );//文件中的任务名作为前缀
		 pingModel.setTaskType(WalkStruct.TaskType.Ping.name() );
		 pingModel.setIp( getNodeString(element, "IP"));
		 pingModel.setTimeOut( getNodeInteger(element, "TimeOut"));
		 pingModel.setSize( getNodeInteger(element, "Packagesize"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
		 pingModel.setRepeat(repeat);
		 pingModel.setEnable(1);
		 pingModel.setInterVal(getNodeInteger(element, "Interval"));
		 pingModel.setEditType(TaskModel.EDIT_TYPE_BTU);
		 listTaskModel.add(pingModel);
	 }
	 
	 /**
	  * <Command Repeat="1">
                        <ID>0x0502</ID>
                        <WaitTimes>60</WaitTimes>
                    </Command>
	  * @return
	  */
	 private void createIdleModel(Element element, List<TaskModel> listTaskModel) {
		 TaskEmptyModel task = new TaskEmptyModel();
		 task.setTaskName( "Idle");//文件中的任务名作为前缀
		 task.setTaskType( WalkStruct.TaskType.EmptyTask.toString() );
		 task.getIdleTestConfig().setKeepTime( getNodeInteger(element, "WaitTimes"));
		 
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
		 task.setRepeat(repeat);
		 task.setEnable(1);
		 task.setInterVal(getNodeInteger(element, "Interval"));
		 task.setEditType(TaskModel.EDIT_TYPE_BTU);
		 listTaskModel.add(task);
	 }
	 
	 /**
	  * 函数功能：Email发送 
	  * @param 
	  * <Command Repeat="3">
                        <ID>0x0613</ID>
                        <Interval>100</Interval>
                        <Timeout>100</Timeout>
                        <APN>2</APN>
                        <Sender>test</Sender>
                        <From>sender@sohu.com</From>
                        <To>dest@sohu.com</To>
                        <FileSize>100</FileSize>
                        <Subject>测试</Subject>
                        <Body>hello test</Body>
                        <Address>smtp.163.com`</Address>
                        <Port>95</Port>
                        <Authentication>0</Authentication>
                        <Account>username@163.com</Account>
                        <Password>password</Password>
                        <Encoding>4</Encoding>
                        <HTML>0</HTML>
                        <SSL>0</SSL>
                    </Command>
	  * @return
	  */
	 private void createEmailSendModel( Element element , List<TaskModel> listTaskModel) {
		 TaskEmailSmtpModel smtpModel = new TaskEmailSmtpModel();
		 smtpModel.setTaskName( mContext.getString(R.string.act_task_emailsmtp));
		 smtpModel.setTaskType( WalkStruct.TaskType.EmailSmtp.name() );
		 smtpModel.setFileSource(1);
		 smtpModel.setAdjunct("null");
		 smtpModel.setTimeOut( getNodeInteger(element, "Timeout"));
		 smtpModel.setAccount( getNodeString(element, "From"));
		 smtpModel.setTo( getNodeString(element, "To"));
		 smtpModel.setSubject( getNodeString(element, "Subject"));
		 smtpModel.setBody( getNodeString(element, "Body"));
		 smtpModel.setEmailServer( getNodeString(element, "Address") );
		 smtpModel.setPort( getNodeInteger(element, "Port"));
		 smtpModel.setUseSSL( getNodeInteger(element, "SSL") );
		 smtpModel.setPassword( getNodeString(element, "Password"));
		 smtpModel.setSmtpAuthentication( getNodeInteger(element, "Authentication"));
		 smtpModel.setFileSize( getNodeInteger(element, "FileSize"));
		 smtpModel.setTimeOut( getNodeInteger(element, "TimeOut"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
		 smtpModel.setRepeat(repeat);
		 smtpModel.setEnable(1);
		 smtpModel.setInterVal(getNodeInteger(element, "Interval"));
		 smtpModel.setEditType(TaskModel.EDIT_TYPE_BTU);
		 listTaskModel.add(smtpModel);
	 }
	 
	 
	
	/**
	 * 函数功能：Email接收
	 * @param element
	 * <Command Repeat="3">
	                    <ID>0x0612</ID>
	                    <Interval>100</Interval>
	                    <Timeout>60</Timeout>
	                    <APN>2</APN>
	                    <MailServer>pop3.163.com</MailServer>
	                    <Port>110</Port>
	                    <Username>dinglicom@163.com</Username>
	                    <Password>dinglicom</Password>
	                    <Deletemail>0</Deletemail>
	                    <Path>/sdcard/Walktour/temp</Path>
	                    <SSL>0</SSL>
	                </Command>
	 * @return
	 */
	private void createEmailRecvModel(Element element, List<TaskModel> listTaskModel) {
		TaskEmailPop3Model model = new TaskEmailPop3Model();
		model.setTaskName( mContext.getString(R.string.act_task_emailpop3));
		model.setTaskType( WalkStruct.TaskType.EmailPop3.name() );
		model.setTimeOut( getNodeInteger(element, "TimeOut"));
		model.setEmailServer( getNodeString(element, "MailServer"));
		model.setPort( getNodeInteger(element, "Port"));
		model.setUseSSL( getNodeInteger(element, "SSL"));
		model.setAccount( getNodeString(element, "Username"));
		model.setPassword( getNodeString(element, "Password"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
	}
	
	/**
	 * 函数功能：FTP下载
	 * @param element
	 * 				<Command Repeat="3">
                        <ID>0x060C</ID>
                        <RemoteHost>211.136.93.245</RemoteHost>
                        <Port>21</Port>
                        <Account>egprs</Account>
                        <Password>egprs123ftp</Password>
                        <TimeOut>180</TimeOut>
                        <Passive>0</Passive>
                        <Binary>1</Binary>
                        <Download>1</Download>下载
                        <RemoteFile>/2M.rar</RemoteFile>
                        <Interval>15</Interval>
                        <APN>1</APN>
                    </Command>
                    
                     <Command Repeat="3">
                        <ID>0x060C</ID>
                        <RemoteHost>211.136.93.245</RemoteHost>
                        <Port>21</Port>
                        <Account>egprs</Account>
                        <Password>egprs123ftp</Password>
                        <TimeOut>180</TimeOut>
                        <Passive>0</Passive>
                        <Binary>1</Binary>
                        <Download>0</Download>上传
                        <RemoteFile>/2M.rar</RemoteFile>
                        <FileSize>2048</FileSize>
                        <Interval>15</Interval>
                        <APN>1</APN>
                    </Command>
	 * @return
	 */
	private void createFtpModel(Element element, List<TaskModel> listTaskModel) {
		
		int mode = getNodeInteger(element, "Download");//1,下载  0,上传
		
		TaskFtpModel model = new TaskFtpModel(mode==1? WalkStruct.TaskType.FTPDownload.name()
				:WalkStruct.TaskType.FTPUpload.name());//FTP上传和下载用的是同一模型，区分在setTaskType
		model.setTaskName( mode==1? mContext.getString(R.string.act_task_ftpdownload)
				:mContext.getString(R.string.act_task_ftpupload) );
		model.setThreadNumber( mode==1? 3:1 );
		if( mode==0 ){
			model.setFileSource( 1 );
			model.setFileSize( getNodeInteger(element, "FileSize"));
		}
		model.setTimeOut( getNodeInteger(element, "TimeOut"));
		model.setRemoteFile( getNodeString(element, "RemoteFile"));
		model.setPsCall( TaskFtpModel.MODE_TIME );
		model.setNoAnswer(30);
		model.setLoginTimes(getNodeInteger(element, "MaxFTPland"));
		model.setThreadNumber(getNodeInteger(element, "ThreadNum"));
		 int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			if(!getNodeString(element, "SSID").equals("")&&!getNodeString(element, "User").equals("")&&!getNodeString(element, "Password").equals("")){
				model.getNetworkConnectionSetting().updateWifiParam(getNodeString(element, "SSID"), getNodeString(element, "User"), getNodeString(element, "Password"));
				if (!model.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !model.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !model.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
					model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
				}
			}
		//设置服务器
		FtpServerModel ftpServer = new FtpServerModel();
		String ip =  getNodeString(element, "RemoteHost");
		String user = getNodeString(element, "Account");
		ftpServer.setName( user+"@"+ip );
		ftpServer.setAnonymous( false );
		ftpServer.setConnect_mode( getNodeInteger(element, "Passive")==1
				? FtpServerModel.CONNECT_MODE_PASSIVE:FtpServerModel.CONNECT_MODE_PORT );
		ftpServer.setIp( ip );
		ftpServer.setPort( getNodeString(element, "Port") );
		ftpServer.setLoginUser( user );		
		ftpServer.setLoginPassword( getNodeString(element, "Password"));
		
		model.setFtpServer(ftpServer.getName());
		
		ConfigFtp configFtp = new ConfigFtp();
		configFtp.addFtp(ftpServer);
		
		listTaskModel.add(model);
	}
	/**
	 * 解析WLAN AP 测试
	 * 
	 * @param element
	 * @return
	 */
	private void createWlanAP(Element element, List<TaskModel> listTaskModel) {
		String ssid = getNodeString(element, "SSID");
		String user = getNodeString(element, "User");
		String password = getNodeString(element, "Password");

		if (null != ssid && ssid.length() > 0 && null != user && user.length() > 0 && null != password && password.length() > 0) {
			if (ssid.split(",").length == user.split(",").length & user.split(",").length == password.split(",").length) {
				String ssids[] = ssid.split(",");
				String users[] = user.split(",");
				String passwords[] = password.split(",");
				for (int i = 0; i < ssids.length; i++) {
					TaskWlanApModel model = new TaskWlanApModel();
					model.setTaskName(mContext.getString(R.string.act_task_wlanap)+"_"+ssids[i]);
					model.setTaskType(WalkStruct.TaskType.WlanAP.name());
//					model.setWifiSSID(ssids[i]);
//					model.setWifiUser(users[i]);
//					model.setWifiPassword(passwords[i]);
					model.getWlanAPRelationTestConfig().setApName(ssids[i]);
					model.getWlanAPRelationTestConfig().getWlanAccount().setUsername(users[i]);
					model.getWlanAPRelationTestConfig().getWlanAccount().setPassword(passwords[i]);
//					if (!model.getWifiSSID().equals("") && !model.getWifiUser().equals("") && !model.getWifiPassword().equals("")) {
					if (!model.getWlanAPRelationTestConfig().getApName().equals("") && !model.getWlanAPRelationTestConfig().getWlanAccount().getUsername().equals("") && !model.getWlanAPRelationTestConfig().getWlanAccount().getPassword().equals("")) {
						model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
					}
					int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
					model.setRepeat(repeat);
					model.setEnable(1);
					model.setTimeOut(getNodeInteger(element, "TimeOut"));
					model.setInterVal(getNodeInteger(element, "Interval"));
					model.setHoldTime(getNodeInteger(element, "Holdtime"));
					model.setEditType(TaskModel.EDIT_TYPE_BTU);
					listTaskModel.add(model);
				}
			}
		} else {
			TaskWlanApModel model = new TaskWlanApModel();
			model.setTaskName(mContext.getString(R.string.act_task_wlanap));
			model.setTaskType(WalkStruct.TaskType.WlanAP.name());
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setTimeOut(getNodeInteger(element, "TimeOut"));
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setHoldTime(getNodeInteger(element, "Holdtime"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
		}

	}

	/**
	 * 解析WLAN WEB 测试
	 * 
	 * @param element
	 * @return
	 */
	private void createWlanWEB(Element element, List<TaskModel> listTaskModel) {
		String ssid = getNodeString(element, "SSID");
		String user = getNodeString(element, "User");
		String password = getNodeString(element, "Password");

		if (null != ssid && ssid.length() > 0 && null != user && user.length() > 0 && null != password && password.length() > 0) {
			if (ssid.split(",").length == user.split(",").length & user.split(",").length == password.split(",").length) {
				String ssids[] = ssid.split(",");
				String users[] = user.split(",");
				String passwords[] = password.split(",");
				for (int i = 0; i < ssids.length; i++) {
					TaskWlanLoginModel model = new TaskWlanLoginModel();
					model.setTaskName(mContext.getString(R.string.act_task_wlanlogin)+"_"+ssids[i]);
					model.setTaskType(WalkStruct.TaskType.WlanLogin.name());
					
					
//					model.setWifiSSID(ssids[i]);
//					model.setWifiUser(users[i]);
//					model.setWifiPassword(passwords[i]);
					model.getWlanWebLoginTestConfig().setApName(ssids[i]);
					model.getWlanWebLoginTestConfig().getWlanAccount().setUsername(users[i]);
					model.getWlanWebLoginTestConfig().getWlanAccount().setPassword(passwords[i]);
//					if (!model.getWifiSSID().equals("") && !model.getWifiUser().equals("") && !model.getWifiPassword().equals("")) {
					if (!model.getWlanWebLoginTestConfig().getApName().equals("") && !model.getWlanWebLoginTestConfig().getWlanAccount().getUsername().equals("") && !model.getWlanWebLoginTestConfig().getWlanAccount().getPassword().equals("")) {
						model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
					}
					int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
					model.setRepeat(repeat);
					model.setEnable(1);
					model.setTimeOut(getNodeInteger(element, "TimeOut"));
					model.setInterVal(getNodeInteger(element, "Interval"));
					model.setEditType(TaskModel.EDIT_TYPE_BTU);
					listTaskModel.add(model);
				}
			}
		} else {
			TaskWlanLoginModel model = new TaskWlanLoginModel();
			model.setTaskName(mContext.getString(R.string.act_task_wlanlogin));
			model.setTaskType(WalkStruct.TaskType.WlanLogin.name());
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setTimeOut(getNodeInteger(element, "TimeOut"));
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
		}

	}

	/**
	 * 解析WLAN HTTP 测试
	 * 
	 * @param element
	 * @return
	 */
	private void createWlanHTTP(Element element, List<TaskModel> listTaskModel) {
		String ssid = getNodeString(element, "SSID");
		String user = getNodeString(element, "User");
		String password = getNodeString(element, "Password");

		if (null != ssid && ssid.length() > 0 && null != user && user.length() > 0 && null != password && password.length() > 0) {
			if (ssid.split(",").length == user.split(",").length & user.split(",").length == password.split(",").length) {
				String ssids[] = ssid.split(",");
				String users[] = user.split(",");
				String passwords[] = password.split(",");
				for (int i = 0; i < ssids.length; i++) {
					TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.HttpRefurbish.name());
					model.setDisConnect(PPPRule.pppHangupEvery);
					model.setTaskName(mContext.getString(R.string.act_task_httpFresh)+"_"+ssids[i]);
					model.setTaskType(WalkStruct.TaskType.HttpRefurbish.name());
					model.setHttpTestMode(TaskHttpPageModel.REFRESH);
					model.setTimeOut(getNodeInteger(element, "TimeOut"));
					model.setXmlUrl(getNodeString(element, "URL"));
					String[] urls = model.getXmlUrl().split(",");
					if (null != urls && urls.length > 0) {
						ArrayList<UrlModel>  urlsx=model.getUrlModelList();
						if(null!=urlsx&&urlsx.size()>0){
							for(UrlModel u:urlsx){
								u.setEnable("0");
							}
						}
						
						for (int j = 0; j < urls.length; j++) {
							boolean isH=false;
							if(null!=urlsx&&urlsx.size()>0){
								for(UrlModel u:urlsx){
									if(u.getName().equals(urls[j])){
										isH=true;
										u.setEnable("1");
									}
								}
							}
							if(urlsx != null && !isH){
								UrlModel urlM = new UrlModel();
								urlM.setEnable("1");
								urlM.setName(urls[j]);
								urlsx.add(urlM);
							} 
						} 
						model.setUrlModelList(urlsx);
					}
					urls = null;
					model.setReponse(30);
					model.getNetworkConnectionSetting().updateWifiParam(ssids[i], users[i], passwords[i]);
					if (!model.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !model.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !model.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
						model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
					}
					int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
					model.setRepeat(repeat);
					model.setEnable(1);
					model.setInterVal(getNodeInteger(element, "Interval"));
					model.setEditType(TaskModel.EDIT_TYPE_BTU);
					listTaskModel.add(model);
				}
			}
		} else {
			TaskHttpPageModel model = new TaskHttpPageModel(WalkStruct.TaskType.HttpRefurbish.name());
			model.setTaskName(mContext.getString(R.string.act_task_httpFresh));
			model.setTaskType(WalkStruct.TaskType.HttpRefurbish.name());
			model.setHttpTestMode(TaskHttpPageModel.REFRESH);
			model.setTimeOut(getNodeInteger(element, "TimeOut"));
			model.setXmlUrl(getNodeString(element, "URL"));
			String[] urls = model.getUrl().split(",");
			if (null != urls && urls.length > 0) {
				ArrayList<UrlModel>  urlsx=model.getUrlModelList();
				if(null!=urlsx&&urlsx.size()>0){
					for(UrlModel u:urlsx){
						u.setEnable("0");
					}
				}
				
				for (int j = 0; j < urls.length; j++) {
					boolean isH=false;
					if(null!=urlsx&&urlsx.size()>0){
						for(UrlModel u:urlsx){
							if(u.getName().equals(urls[j])){
								isH=true;
								u.setEnable("1");
							}
						}
					}
					if(urlsx != null && !isH){
						UrlModel urlM = new UrlModel();
						urlM.setEnable("1");
						urlM.setName(urls[j]);
						urlsx.add(urlM);
					} 
				} 
				model.setUrlModelList(urlsx);
			}
			urls = null;
			model.setReponse(30);
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
		}

	}

	/**
	 * 解析WLAN ETEAUTH 测试
	 * 
	 * @param element
	 * @return
	 */
	private void createWlanETEAUTH(Element element, List<TaskModel> listTaskModel) {
		String ssid = getNodeString(element, "SSID");
		String user = getNodeString(element, "User");
		String password = getNodeString(element, "Password");

		if (null != ssid && ssid.length() > 0 && null != user && user.length() > 0 && null != password && password.length() > 0) {
			if (ssid.split(",").length == user.split(",").length & user.split(",").length == password.split(",").length) {
				String ssids[] = ssid.split(",");
				String users[] = user.split(",");
				String passwords[] = password.split(",");
				for (int i = 0; i < ssids.length; i++) {
					TaskWlanEteAuthModel model = new TaskWlanEteAuthModel();
					model.setTaskName(mContext.getString(R.string.act_task_wlaneteauth)+"_"+ssids[i]);
					model.setTaskType(WalkStruct.TaskType.WlanEteAuth.name());
//					model.setWifiSSID(ssids[i]);
//					model.setWifiUser(users[i]);
//					model.setWifiPassword(passwords[i]);
					model.getWlanETEAuthTestConfig().setApName(ssids[i]);
					model.getWlanETEAuthTestConfig().getWlanAccount().setUsername(users[i]);
					model.getWlanETEAuthTestConfig().getWlanAccount().setPassword(passwords[i]);
//					if (!model.getWifiSSID().equals("") && !model.getWifiUser().equals("") && !model.getWifiPassword().equals("")) {
					if (!model.getWlanETEAuthTestConfig().getApName().equals("") && !model.getWlanETEAuthTestConfig().getWlanAccount().getUsername().equals("") && !model.getWlanETEAuthTestConfig().getWlanAccount().getPassword().equals("")) {
						model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
					}
					int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
					model.setRepeat(repeat);
					model.setEnable(1);
					model.setTimeOut(getNodeInteger(element, "TimeOut"));
					model.setInterVal(getNodeInteger(element, "Interval"));
					model.setEditType(TaskModel.EDIT_TYPE_BTU);
					listTaskModel.add(model);
				}
			}
		} else {
			TaskWlanEteAuthModel model = new TaskWlanEteAuthModel();
			model.setTaskName(mContext.getString(R.string.act_task_wlaneteauth));
			model.setTaskType(WalkStruct.TaskType.WlanEteAuth.name());
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setTimeOut(getNodeInteger(element, "TimeOut"));
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			listTaskModel.add(model);
		}

	}

	/**
	 * 解析WLAN FTPDOWNUP 测试,注意包含Down和Up,包含2个任务
	 * 
	 * @param element
	 * @return
	 */
	private void createWlanFTPDOWNUP(Element element, List<TaskModel> listTaskModel) {

		// 设置服务器
		FtpServerModel ftpServer = new FtpServerModel();
		String ip = getNodeString(element, "RemoteHost");
		ftpServer.setPort(getNodeString(element, "Port"));
		String account = getNodeString(element, "FTPAccount");
		ftpServer.setLoginPassword(getNodeString(element, "FTPPassword"));
		ftpServer.setName(account + "@" + ip);
		ftpServer.setAnonymous(false);
		ftpServer.setConnect_mode(getNodeInteger(element, "Passive") == 0 ? FtpServerModel.CONNECT_MODE_PASSIVE
				: FtpServerModel.CONNECT_MODE_PORT);
		ftpServer.setIp(ip);
		ftpServer.setLoginUser(account);
		ConfigFtp configFtp = new ConfigFtp();
		configFtp.addFtp(ftpServer);

		String ssid = getNodeString(element, "SSID");
		String user = getNodeString(element, "User");
		String password = getNodeString(element, "Password");

		int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
		int interval = getNodeInteger(element, "Interval");
		if (null != ssid && ssid.length() > 0 && null != user && user.length() > 0 && null != password
				&& password.length() > 0) {
			if (ssid.split(",").length == user.split(",").length
					& user.split(",").length == password.split(",").length) {
				String ssids[] = ssid.split(",");
				String users[] = user.split(",");
				String passwords[] = password.split(",");
				for (int i = 0; i < ssids.length; i++) {
					int mode = getNodeInteger(element, "Download");// 1,下载 0,上传

					// 下载
					if (mode == 1) {
						TaskFtpModel ftpDown = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
						ftpDown.setTaskName(mContext.getString(R.string.act_task_ftpdownload) + "_" + ssids[i]);
						ftpDown.setThreadNumber(3);
						ftpDown.setTimeOut(getNodeInteger(element, "TimeOut"));
						ftpDown.setRemoteFile(getNodeString(element, "RemoteFile"));
						ftpDown.setPsCall(TaskFtpModel.MODE_TIME);
						ftpDown.setNoAnswer(30);
						ftpDown.setDisConnect(PPPRule.pppHangupEvery);
						ftpDown.getNetworkConnectionSetting().updateWifiParam(ssids[i], users[i], passwords[i]);
						if (!ftpDown.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !ftpDown.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !ftpDown.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
							ftpDown.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
						}

						ftpDown.setRepeat(repeat);
						ftpDown.setEnable(1);
						ftpDown.setInterVal(interval);
						ftpDown.setEditType(TaskModel.EDIT_TYPE_BTU);
						ftpDown.setFtpServer(ftpServer.getName());
						listTaskModel.add(ftpDown);
					}
					// 上传
					if (mode == 0) {
						TaskFtpModel ftpUp = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());
						ftpUp.setTaskName(mContext.getString(R.string.act_task_ftpupload) + "_" + ssids[i]);
						ftpUp.setThreadNumber(1);
						ftpUp.setFileSource(1);
						ftpUp.setFileSize(getNodeInteger(element, "FileSize"));
						ftpUp.setTimeOut(getNodeInteger(element, "TimeOut"));
						ftpUp.setPsCall(TaskFtpModel.MODE_FILE);
						ftpUp.setRemoteFile(getNodeString(element, "RemoteFile"));
						ftpUp.setNoAnswer(30);
						ftpUp.getNetworkConnectionSetting().updateWifiParam(ssids[i], users[i], passwords[i]);
						if (!ftpUp.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !ftpUp.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !ftpUp.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
							ftpUp.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
						}
						ftpUp.setRepeat(repeat);
						ftpUp.setEnable(1);
						ftpUp.setInterVal(interval);
						ftpUp.setEditType(TaskModel.EDIT_TYPE_BTU);
						ftpUp.setDisConnect(PPPRule.pppHangupEvery);
						ftpUp.setFtpServer(ftpServer.getName());
						listTaskModel.add(ftpUp);
					}
				}
			}
		} else {
			int mode = getNodeInteger(element, "Download");// 1,下载 0,上传
			// 下载
			if (mode == 1) {
				TaskFtpModel ftpDown = new TaskFtpModel(WalkStruct.TaskType.FTPDownload.name());
				ftpDown.setTaskName(mContext.getString(R.string.act_task_ftpdownload));
				ftpDown.setThreadNumber(3);
				ftpDown.setTimeOut(getNodeInteger(element, "TimeOut"));
				ftpDown.setRemoteFile(getNodeString(element, "RemoteFile"));
				ftpDown.setPsCall(TaskFtpModel.MODE_TIME);
				ftpDown.setNoAnswer(30);
				ftpDown.setDisConnect(PPPRule.pppHangupEvery);
				if (!ftpDown.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !ftpDown.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !ftpDown.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
					ftpDown.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
				}

				ftpDown.setRepeat(repeat);
				ftpDown.setEnable(1);
				ftpDown.setInterVal(interval);
				ftpDown.setEditType(TaskModel.EDIT_TYPE_BTU);
				ftpDown.setFtpServer(ftpServer.getName());
				listTaskModel.add(ftpDown);
			}
			if (mode == 0) {
				TaskFtpModel ftpUp = new TaskFtpModel(WalkStruct.TaskType.FTPUpload.name());
				ftpUp.setTaskName(mContext.getString(R.string.act_task_ftpupload));
				ftpUp.setThreadNumber(1);
				ftpUp.setFileSource(1);
				ftpUp.setFileSize(getNodeInteger(element, "FileSize"));
				ftpUp.setTimeOut(getNodeInteger(element, "TimeOut"));
				ftpUp.setPsCall(TaskFtpModel.MODE_TIME);
				ftpUp.setNoAnswer(30);
				if (!ftpUp.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !ftpUp.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !ftpUp.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
					ftpUp.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
				}
				ftpUp.setRepeat(repeat);
				ftpUp.setEnable(1);
				ftpUp.setInterVal(interval);
				ftpUp.setEditType(TaskModel.EDIT_TYPE_BTU);
				ftpUp.setDisConnect(PPPRule.pppHangupEvery);
				ftpUp.setFtpServer(ftpServer.getName());
				listTaskModel.add(ftpUp);
			}
		}

	}

	/**
	 * 解析WLAN PING 测试
	 * 
	 * @param element
	 * @return
	 */
	private void createWlanPING(Element element, List<TaskModel> listTaskModel) {
		String ssid = getNodeString(element, "SSID");
		String user = getNodeString(element, "User");
		String password = getNodeString(element, "Password");
		if (null != ssid && ssid.length() > 0 && null != user && user.length() > 0 && null != password && password.length() > 0) {
			if (ssid.split(",").length == user.split(",").length && user.split(",").length == password.split(",").length) {
				String ssids[] = ssid.split(",");
				String users[] = user.split(",");
				String passwords[] = password.split(",");
				for (int i = 0; i < ssids.length; i++) {
					TaskPingModel model = new TaskPingModel();
					model.setTaskName(mContext.getString(R.string.act_task_ping));
					model.setTaskType(WalkStruct.TaskType.Ping.name());
					model.setTimeOut(getNodeInteger(element, "TimeOut"));
					model.setIp(getNodeString(element, "IP"));
					model.setSize(getNodeInteger(element, "Packagesize"));
					model.setInterVal(getNodeInteger(element, "Interval"));
					int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
					model.setRepeat(repeat);
					model.setEnable(1);
					model.setInterVal(getNodeInteger(element, "Interval"));
					model.setEditType(TaskModel.EDIT_TYPE_BTU);
					model.getNetworkConnectionSetting().updateWifiParam(ssids[i], users[i], passwords[i]);
					if (!model.getNetworkConnectionSetting().getWifiParam()[0].equals("") && !model.getNetworkConnectionSetting().getWifiParam()[1].equals("") && !model.getNetworkConnectionSetting().getWifiParam()[2].equals("")) {
						model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
					}
					model.setDisConnect(PPPRule.pppHangupEvery);
					model.setTaskName(model.getTaskName() + "_" + ssids[i]);
					listTaskModel.add(model);
				}
			}
		} else {
			TaskPingModel model = new TaskPingModel();
			model.setTaskName(mContext.getString(R.string.act_task_ping));
			model.setTaskType(WalkStruct.TaskType.Ping.name());
			model.setTimeOut(getNodeInteger(element, "TimeOut"));
			model.setIp(getNodeString(element, "IP"));
			model.setSize(getNodeInteger(element, "Packagesize"));
			model.setInterVal(getNodeInteger(element, "Interval"));
			int repeat = Integer.parseInt(element.getAttributes().getNamedItem("Repeat").getNodeValue());
			model.setRepeat(repeat);
			model.setEnable(1);
			model.setTypeProperty(WalkCommonPara.TypeProperty_Wlan);
			model.setInterVal(getNodeInteger(element, "Interval"));
			model.setEditType(TaskModel.EDIT_TYPE_BTU);
			model.setDisConnect(PPPRule.pppHangupEvery);
			listTaskModel.add(model);
		}
	}
	/**
	 * 函数功能：转换Command到TaskModel
	 * @param command 例如
	 * 			 <Command Repeat="3"> 
	 *            	<ID>0x0500</ID>
	 *            	<CallNumber>10086</CallNumber> 
	 *            	<RandomCall>0</RandomCall>
	 *            	<Duration>180</Duration> 
	 *            	<Interval>20</Interval>
	 *            	<MaxTime>10</MaxTime> 
	 *            	<TestMOS>1</TestMOS>
	 *            	<CallMOSServer>0</CallMOSServer> 
	 *            	<MOSLimit>2.0</MOSLimit>
	 *            </Command>
	 * @return 无法转换时返回null
	 */
	private List<TaskModel> convertCommand(Node command, List<TaskModel> listTaskModel) {
		listTaskModel.clear();
		Element element = (Element) command;

		String id = getNodeString(element, "ID");
		// 主叫
		if (id.equals("0x0500")) {
			createMocCallModel(element, listTaskModel);
		}
		// 视频主叫
		else if (id.equals("0x0700")) {
			createVideoMocCallModel(element, listTaskModel);
		}
		// 被叫
		else if (id.equals("0x0501")) {
			createMtcCallModel(element, listTaskModel);
		}
		//视频被叫
		else if (id.equals("0x0701")) {
			createVideoMtcCallModel(element, listTaskModel);
		}
		// Ping
		else if (id.equals("0x0604")) {
			createPingModel(element, listTaskModel);
		}
		// IDLE
		else if (id.equals("0x0502")) {
			createIdleModel(element, listTaskModel);
		}
		// 短信发送接收
		else if (id.equals("0x0609")) {
			int mode = getNodeInteger(element, "Mode");
			if (mode == 1) {
				createSmsSendModel(element, listTaskModel);
			} else if (mode == 0) {
				createSmsRecvModel(element, listTaskModel);
			}
		}
		// 彩信发送
		else if (id.equals("0x060A")) {
			createMmsSendModel(element, listTaskModel);
		}
		// 彩信接收
		else if (id.equals("0x060B")) {
			createMmsRecvModel(element, listTaskModel);
		}
		// 流媒体
		else if (id.equals("0x0611")) {
			createVideoStreamModel(element, listTaskModel);
		}
		// Http下载
		else if (id.equals("0x060F")) {
			int mode = getNodeInteger(element, "Mode");
			if (mode == 0) {
				createHttpLogonModel(element, listTaskModel);
			} else if (mode == 2) {
				createHttpDownModel(element, listTaskModel);
			}
		}
		// Email接收
		else if (id.equals("0x0612")) {
			createEmailRecvModel(element, listTaskModel);
		}
		// Email发送
		else if (id.equals("0x0613")) {
			createEmailSendModel(element, listTaskModel);
		}
		// FTP上传下载
		else if (id.equals("0x060C")) {
			createFtpModel(element, listTaskModel);
		}
		// WLAN AP关联测试命令
		else if (id.equals(WLAN_AP)) {
			createWlanAP(element, listTaskModel);
		}
		// WLAN WEB用户认证测试命令
		else if (id.equals(WLAN_WEB)) {
			createWlanWEB(element, listTaskModel);
		}
		// WLAN HTTP网站访问联测试命令
		else if (id.equals(WLAN_HTTP)) {
			createWlanHTTP(element, listTaskModel);
		}
		// WLAN ETE_AUTH端到端认证测试命令
		else if (id.equals(WLAN_ETE_AUTH)) {
			createWlanETEAUTH(element, listTaskModel);
		}
		// WLAN FTP下载/上传测试命令
		else if (id.equals(WLAN_FTP_DOWNUP)) {
			createWlanFTPDOWNUP(element, listTaskModel);
		}
		// WLAN Ping测试命令
		else if (id.equals(WLAN_PING)) {
			createWlanPING(element, listTaskModel);
		}
		return listTaskModel;
	}
	
	/**
	 * 函数功能：转换并发任务
	 * @param groupNode 形如下
	 * <CommandGroup StartLab="3030" EndLab="3031" FirstServiceEndType="0" FirstServiceEndDelay="0" SecondServiceStartType="1" SecondServiceStartDelay="10" SecondServiceEndType="0" SecondServiceEndDelay="0" Repeat="1">
            <Command Repeat="3">
                <ID>0x060C</ID>
                <RemoteHost>211.136.93.245</RemoteHost>
                <Port>21</Port>
                <Account>egprs</Account>
                <Password>egprs123ftp</Password>
                <TimeOut>180</TimeOut>
                <Passive>0</Passive>
                <Binary>1</Binary>
                <Download>1</Download>
                <RemoteFile>/2M.rar</RemoteFile>
                <Interval>15</Interval>
                <APN>1</APN>
                <ThreadNum>1</ThreadNum>
                <MaxDialNum>0</MaxDialNum>
                <MaxFTPland>3</MaxFTPland>
            </Command>
            <Command Repeat="3">
                <ID>0x0500</ID>
                <CallNumber>10086</CallNumber>
                <RandomCall>0</RandomCall>
                <Duration>180</Duration>
                <Interval>20</Interval>
                <MaxTime>10</MaxTime>
                <TestMOS>0</TestMOS>
                <CallMOSServer>0</CallMOSServer>
                <MOSLimit>2.0</MOSLimit>
            </Command>
    	</CommandGroup>
	 * @return
	 */
	TaskRabModel convertCommandGroup(Node node){
		TaskRabModel model = new TaskRabModel();
		model.setTaskType( WalkStruct.TaskType.MultiRAB.name() );
		model.setTaskName( mContext.getString(R.string.act_task_multirab) );
		model.setStartLable( getAttributeString(node,"StartLab") );
		model.setEndLable( getAttributeString(node, "EndLab") );
		model.setFirstServiceEndType( getAttributeInteger(node,"FirstServiceEndType") );
		model.setFirstServiceEndDelay( getAttributeInteger(node, "FirstServiceEndDelay"));
		model.setSecondServiceStartType( getAttributeInteger(node, "SecondServiceStartType"));
		model.setSecondServiceStartDelay( getAttributeInteger(node, "SecondServiceStartDelay"));
		model.setSecondServiceEndType( getAttributeInteger(node, "SecondServiceEndType"));
		model.setSecondServiceEndDelay( getAttributeInteger(node, "SecondServiceEndDelay"));
		model.setRepeat( getAttributeInteger(node, "Repeat"));
		model.setInterVal(15);
		
		//目前暂时只有先数据后语音的并发测试
		model.setVoiceDelay( model.getSecondServiceStartDelay() );
		
		Element element = (Element) node;
		NodeList commandList = element.getElementsByTagName("Command");
		ArrayList<TaskModel> taskList = getTaskFromCommands( commandList );
		for( TaskModel taskModel:taskList){
			taskModel.setIsRab(1);
			//并发子任务的名称为 业务名@并发标签
			taskModel.setRabName( taskModel.getTaskName() +"@"+model.getStartLable() );
		}
		model.setTaskModelList( taskList );
		
		return model;
	}
	
	/**
	 * @param nodeList 形如下面的Nodelist
	 * 
	 * 				<CommandGroup StartLab="3030" EndLab="3031" FirstServiceEndType="0" FirstServiceEndDelay="0" SecondServiceStartType="1" SecondServiceStartDelay="10" SecondServiceEndType="0" SecondServiceEndDelay="0" Repeat="1">
                        <Command Repeat="3">
                            <ID>0x060C</ID>
                            <RemoteHost>211.136.93.245</RemoteHost>
                            <Port>21</Port>
                            <Account>egprs</Account>
                            <Password>egprs123ftp</Password>
                            <TimeOut>180</TimeOut>
                            <Passive>0</Passive>
                            <Binary>1</Binary>
                            <Download>1</Download>
                            <RemoteFile>/2M.rar</RemoteFile>
                            <Interval>15</Interval>
                            <APN>1</APN>
                            <ThreadNum>1</ThreadNum>
                            <MaxDialNum>0</MaxDialNum>
                            <MaxFTPland>3</MaxFTPland>
                        </Command>
                        <Command Repeat="3">
                            <ID>0x0500</ID>
                            <CallNumber>10086</CallNumber>
                            <RandomCall>0</RandomCall>
                            <Duration>180</Duration>
                            <Interval>20</Interval>
                            <MaxTime>10</MaxTime>
                            <TestMOS>0</TestMOS>
                            <CallMOSServer>0</CallMOSServer>
                            <MOSLimit>2.0</MOSLimit>
                        </Command>
                    </CommandGroup>
	 * 
	 * 				<Command Repeat="3">
	 *                   <ID>0x0500</ID>
	 *                   <CallNumber>10086</CallNumber>
	 *                   <RandomCall>0</RandomCall>
	 *                   <Duration>180</Duration>
	 *                   <Interval>20</Interval>
	 *                   <MaxTime>150</MaxTime>
	 *                   <TestMOS>1</TestMOS>
	 *                   <CallMOSServer>1</CallMOSServer>
	 *                   <MOSLimit>2.0</MOSLimit>
	 *               </Command>
	 *               <Command Repeat="1">
	 *                   <ID>0x0609</ID>
	 *                   <ServerCenterAddress>10086</ServerCenterAddress>
	 *                   <Destination>13923361292</Destination>
	 *                   <TimeOut>1</TimeOut>
	 *                   <Mode>1</Mode>
	 *                   <Text>1</Text>
	 *                   <Report>0</Report>
	 *                   <Content>Sms Test</Content>
	 *                   <Interval>100</Interval>
	 *               </Command>
	 * @return
	 */

	private ArrayList<TaskModel> getTaskFromCommands(NodeList commandNodeList) {
		ArrayList<TaskModel> listTaskModel = new ArrayList<TaskModel>();
		List<TaskModel> list = new LinkedList<TaskModel>();
		int count = 0;
		for (int i = 0; i < commandNodeList.getLength(); i++) {
			Node command = commandNodeList.item(i);

			int nodeType = command.getNodeType();

			if (nodeType == Node.ELEMENT_NODE) {

				TaskModel model = null;

				Element element = (Element) command;

				// 并发业务
				if (getAttributeString(element, "StartLab").length() > 0) {
					model = convertCommandGroup(command);
					if (model != null) {
						// 加上执行序号
						model.setTaskName("(" + ++count + ")" + model.getTaskName());
						listTaskModel.add(model);
					}
				}
				// 非并发业务
				else {
					for (TaskModel taskModel : convertCommand(command, list)) {
						// 加上执行序号
						taskModel.setTaskName("(" + ++count + ")" + taskModel.getTaskName());
						listTaskModel.add(taskModel);
					}

				}
			}

		}

		return listTaskModel;
	}
	
	/**
	 * 从测试计划文件中读取当前版本号
	 * 
	 * 注意:此函数为读文件操作，会阻塞,不要在UI主线程中操作
	 * 
	 * @return 当前测试计划版本号
	 */
	public int getTaskVersion(){
		int result = 0;
		try{
			Document doc = getDocument();
			Element root = doc.getDocumentElement();
			Element testUnit = (Element)root.getElementsByTagName("AutoTestUnit").item(0);
			Element version = (Element)testUnit.getElementsByTagName("Version").item(0);
			result = Integer.parseInt(version.getFirstChild().getNodeValue());
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 函数功能：从文件中读取测试方案列表
	 * 
	 * 注意:此函数为读文件操作，会阻塞,不要在UI主线程中操作
	 * 
	 * @return
	 */
	public ArrayList<TestScheme> convertTestScheme(){
		ArrayList<TestScheme> list = new ArrayList<TestScheme>();
		Document doc = getDocument();
		if( doc ==null ){
			return list;
		}
		Element root = doc.getDocumentElement();
		if(root==null){
			return list;
		}
		
		root.normalize();
		NodeList nodeList = root.getElementsByTagName("TestScheme");
		for(int i=0;i<nodeList.getLength();i++){
			try{
				Element element=(Element)nodeList.item(i);
				TestScheme scheme = new TestScheme();
				scheme.setEnable( getNodeString(element, "Enable").equals("1") );
				scheme.setDesc(  getNodeString(element, "DESC") );
				scheme.setMoudleNum( getNodeInteger(element, "MSNO") );
				String[] dates = getNodeString(element, "ExecutiveDate").split(",");
				String beginDate = dates[0];
				String endDate = dates[ dates.length-1 ];
				scheme.setBeginDate( getDateByFormat(beginDate) );
				scheme.setEndDate( getDateByFormat(endDate));
				String beginTime = getNodeString(element, "BeginTime");
				String endTime = getNodeString(element, "EndTime");
				scheme.setBeginTime( getTimeByFmt(beginTime) );
				scheme.setEndTime( getTimeByFmt(endTime) );
				Element commandList = (Element)element.getElementsByTagName("CommandList").item(0);
				String repeat = commandList.getAttributes().getNamedItem("Repeat").getNodeValue();
				scheme.setCommandListRepeat( Integer.parseInt(repeat));
				
				//<Synchronize type="0">
				Node nodeSynchronize = null;
				for(int chis = 0; chis < commandList.getChildNodes().getLength(); chis++){
					if(commandList.getChildNodes().item(chis).getNodeType() == Node.ELEMENT_NODE){
						nodeSynchronize = commandList.getChildNodes().item(chis);//item[0]是document
						break;
					}
				}
				if(nodeSynchronize != null){
					NodeList commandNodeList = nodeSynchronize.getChildNodes();
					
					ArrayList<TaskModel> taskList = getTaskFromCommands( commandNodeList );
					scheme.setCommandList( taskList );
					scheme.setVersion(this.getTaskVersion());
					list.add(scheme);
				}
			}catch(Exception e){
				LogUtil.w("TaskConverter", "convertTestScheme",e);
			}
		}
		return list;
	}
	
}

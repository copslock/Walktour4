package com.walktour.Utils;

import com.walktour.model.TotalEventModel;
import com.walktour.model.TotalParaModel;
import com.walktour.model.TotalTaskModel;

import org.w3c.dom.Element;

public class TotalModelXmlChange {
	public static String defaultTotalTaskXml(){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
		sb.append("<root>\n\r");
		sb.append("\t<DialTimes>0</DialTimes>\n\r");
		sb.append("\t<DialSuccTimes>0</DialSuccTimes>\n\r");
		sb.append("\t<DialDropTimes>0</DialDropTimes>\n\r");
		sb.append("\t<VideoDTimes>0</VideoDTimes>\n\r");
		sb.append("\t<VideoDSuccTimes>0</VideoDSuccTimes>\n\r");
		sb.append("\t<VideoDDropTimes>0</VideoDDropTimes>\n\r");
		sb.append("\t<VideoDDelay>0</VideoDDelay>\n\r");

		sb.append("\t<AttachTimes>0</AttachTimes>\n\r");
		sb.append("\t<AttachSuccTimes>0</AttachSuccTimes>\n\r");
		sb.append("\t<AttachDelay>0</AttachDelay>\n\r");
		sb.append("\t<PdpTimes>0</PdpTimes>\n\r");
		sb.append("\t<PdpSuccTimes>0</PdpSuccTimes>\n\r");
		sb.append("\t<PdpDelay>0</PdpDelay>\n\r");
		sb.append("\t<PingTimes>0</PingTimes>\n\r");
		sb.append("\t<PingSuccTimes>0</PingSuccTimes>\n\r");
		sb.append("\t<PingDelay>0</PingDelay>\n\r");
		sb.append("\t<WapRefTimes>0</WapRefTimes>\n\r");
		sb.append("\t<WapRefSuccTimes>0</WapRefSuccTimes>\n\r");
		sb.append("\t<WapRefDelay>0</WapRefDelay>\n\r");
		sb.append("\t<WapLoginTimes>0</WapLoginTimes>\n\r");
		sb.append("\t<WapLoginSuccTimes>0</WapLoginSuccTimes>\n\r");
		sb.append("\t<WapLoginDelay>0</WapLoginDelay>\n\r");

		sb.append("\t<FtpULTimes>0</FtpULTimes>\n\r");
		sb.append("\t<FtpULSuccTimes>0</FtpULSuccTimes>\n\r");
		sb.append("\t<FtpULRate>0</FtpULRate>\n\r");
		sb.append("\t<FtpDLTimes>0</FtpDLTimes>\n\r");
		sb.append("\t<FtpDLSuccTimes>0</FtpDLSuccTimes>\n\r");
		sb.append("\t<FtpDLRate>0</FtpDLRate>\n\r");
		sb.append("\t<HttpTimes>0</HttpTimes>\n\r");
		sb.append("\t<HttpSuccTimes>0</HttpSuccTimes>\n\r");
		sb.append("\t<HttpRate>0</HttpRate>\n\r");
		sb.append("\t<SmtpTimes>0</SmtpTimes>\n\r");
		sb.append("\t<SmtpSuccTimes>0</SmtpSuccTimes>\n\r");
		sb.append("\t<SmtpRate>0</SmtpRate>\n\r");
		sb.append("\t<Pop3Times>0</Pop3Times>\n\r");
		sb.append("\t<Pop3SuccTimes>0</Pop3SuccTimes>\n\r");
		sb.append("\t<Pop3Rate>0</Pop3Rate>\n\r");
		sb.append("\t<WapDLTimes>0</WapDLTimes>\n\r");
		sb.append("\t<WapDLSuccTimes>0</WapDLSuccTimes>\n\r");
		sb.append("\t<WapDLRate>0</WapDLRate>\n\r");

		sb.append("\t<SmsSendTimes>0</SmsSendTimes>\n\r");
		sb.append("\t<SmsSendSuccTimes>0</SmsSendSuccTimes>\n\r");
		sb.append("\t<SmsSendDelay>0</SmsSendDelay>\n\r");
		sb.append("\t<SmsReceiveSuccTimes>0</SmsReceiveSuccTimes>\n\r");
		sb.append("\t<SmsPTPDelay>0</SmsPTPDelay>\n\r");

		sb.append("\t<MmsTimes>0</MmsTimes>\n\r");
		sb.append("\t<MmsSuccTimes>0</MmsSuccTimes>\n\r");
		sb.append("\t<MmsSendDelay>0</MmsSendDelay>\n\r");
		sb.append("\t<MmsPushSuccTimes>0</MmsPushSuccTimes>\n\r");
		sb.append("\t<MmsPushDelay>0</MmsPushDelay>\n\r");
		sb.append("\t<MmsReceiveSuccTimes>0</MmsReceiveSuccTimes>\n\r");
		sb.append("\t<MmsPTPDelay>0</MmsPTPDelay>\n\r");

		sb.append("\t<FetionTimes>0</FetionTimes>\n\r");
		sb.append("\t<FetionSuccTimes>0</FetionSuccTimes>\n\r");
		sb.append("\t<FetionDelay>0</FetionDelay>\n\r");
		sb.append("\t<FetionReceiveSuccTimes>0</FetionReceiveSuccTimes>\n\r");
		sb.append("\t<FetionPTPDelay>0</FetionPTPDelay>\n\r");
		sb.append("</root>\n\r");
		return sb.toString();
	}
	public static String defaultTotalEventXml(){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
		sb.append("<root>\n\r");
		sb.append("\t<DialTimes>0</DialTimes>\n\r");
		sb.append("\t<DialSuccTimes>0</DialSuccTimes>\n\r");
		sb.append("\t<DialFaildTimes>0</DialFaildTimes>\n\r");
		sb.append("\t<DialDropTimes>0</DialDropTimes>\n\r");
		sb.append("\t<SwitchSuccTimes>0</SwitchSuccTimes>\n\r");
		sb.append("\t<SwitchFaildTimes>0</SwitchFaildTimes>\n\r");
		sb.append("\t<SdcchAllotFaild>0</SdcchAllotFaild>\n\r");
		sb.append("\t<TchAllotFaild>0</TchAllotFaild>\n\r");
		sb.append("\t<LacTimes>0</LacTimes>\n\r");
		sb.append("\t<LacSuccTimes>0</LacSuccTimes>\n\r");
		sb.append("\t<RauTimes>0</RauTimes>\n\r");
		sb.append("\t<RauSuccTimes>0</RauSuccTimes>\n\r");
		sb.append("\t<AreaReelect>0</AreaReelect>\n\r");
		sb.append("\t<SignalCoverWeak>0</SignalCoverWeak>\n\r");
		sb.append("\t<RxQualityWeak>0</RxQualityWeak>\n\r");
		sb.append("\t<SwitchFrequent>0</SwitchFrequent>\n\r");
		sb.append("</root>\n\r");
		return sb.toString();
	}
	public static String defaultTotalParaXml(){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
		sb.append("<root>\n\r");
		sb.append("\t<RxQualFullMax>0</RxQualFullMax>\n\r");
		sb.append("\t<RxQualFullMin>0</RxQualFullMin>\n\r");
		sb.append("\t<RxQualFullAverage>0</RxQualFullAverage>\n\r");
		sb.append("\t<RxQualSubMax>0</RxQualSubMax>\n\r");
		sb.append("\t<RxQualSubMin>0</RxQualSubMin>\n\r");
		sb.append("\t<RxQualSubAverage>0</RxQualSubAverage>\n\r");
		sb.append("\t<RxLevFullMax>0</RxLevFullMax>\n\r");
		sb.append("\t<RxLevFullMin>0</RxLevFullMin>\n\r");
		sb.append("\t<RxLevFullAverage>0</RxLevFullAverage>\n\r");
		sb.append("\t<RxLevSubMax>0</RxLevSubMax>\n\r");
		sb.append("\t<RxLevSubMin>0</RxLevSubMin>\n\r");
		sb.append("\t<RxLevSubAverage>0</RxLevSubAverage>\n\r");
		sb.append("\t<FerFullMax>0</FerFullMax>\n\r");
		sb.append("\t<FerFullMin>0</FerFullMin>\n\r");
		sb.append("\t<FerFullAverage>0</FerFullAverage>\n\r");
		sb.append("\t<FerSubMax>0</FerSubMax>\n\r");
		sb.append("\t<FerSubMin>0</FerSubMin>\n\r");
		sb.append("\t<FerSubAverage>0</FerSubAverage>\n\r");
		sb.append("\t<BcchLevMax>0</BcchLevMax>\n\r");
		sb.append("\t<BcchLevMin>0</BcchLevMin>\n\r");
		sb.append("\t<BcchLevAverage>0</BcchLevAverage>\n\r");
		sb.append("</root>\n\r");
		return sb.toString();
	}
	
	public static TotalTaskModel TotalTaskXmlToModel(Element node){
		TotalTaskModel model = new TotalTaskModel();
		model.setDialTimes(Integer.parseInt(node.getElementsByTagName("DialTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setDialSuccTimes(Integer.parseInt(node.getElementsByTagName("DialSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setDialDropTimes(Integer.parseInt(node.getElementsByTagName("DialDropTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setVideoDTimes(Integer.parseInt(node.getElementsByTagName("VideoDTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setVideoDSuccTimes(Integer.parseInt(node.getElementsByTagName("VideoDSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setVideoDDropTimes(Integer.parseInt(node.getElementsByTagName("VideoDDropTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setVideoDDelay(Float.parseFloat(node.getElementsByTagName("VideoDDelay").item(0).getFirstChild().getNodeValue().trim()));

		model.setAttachTimes(Integer.parseInt(node.getElementsByTagName("AttachTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setAttachSuccTimes(Integer.parseInt(node.getElementsByTagName("AttachSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setAttachDelay(Float.parseFloat(node.getElementsByTagName("AttachDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setPdpTimes(Integer.parseInt(node.getElementsByTagName("PdpTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setPdpSuccTimes(Integer.parseInt(node.getElementsByTagName("PdpSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setPdpDelay(Float.parseFloat(node.getElementsByTagName("PdpDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setPingTimes(Integer.parseInt(node.getElementsByTagName("PingTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setPingSuccTimes(Integer.parseInt(node.getElementsByTagName("PingSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setPingDelay(Float.parseFloat(node.getElementsByTagName("PingDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapRefTimes(Integer.parseInt(node.getElementsByTagName("WapRefTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapRefSuccTimes(Integer.parseInt(node.getElementsByTagName("WapRefSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapRefDelay(Float.parseFloat(node.getElementsByTagName("WapRefDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapLoginTimes(Integer.parseInt(node.getElementsByTagName("WapLoginTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapLoginSuccTimes(Integer.parseInt(node.getElementsByTagName("WapLoginSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapLoginDelay(Float.parseFloat(node.getElementsByTagName("WapLoginDelay").item(0).getFirstChild().getNodeValue().trim()));

		model.setFtpULTimes(Integer.parseInt(node.getElementsByTagName("FtpULTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFtpULSuccTimes(Integer.parseInt(node.getElementsByTagName("FtpULSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFtpULRate(Float.parseFloat(node.getElementsByTagName("FtpULRate").item(0).getFirstChild().getNodeValue().trim()));
		model.setFtpDLTimes(Integer.parseInt(node.getElementsByTagName("FtpDLTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFtpDLSuccTimes(Integer.parseInt(node.getElementsByTagName("FtpDLSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFtpDLRate(Float.parseFloat(node.getElementsByTagName("FtpDLRate").item(0).getFirstChild().getNodeValue().trim()));
		model.setHttpTimes(Integer.parseInt(node.getElementsByTagName("HttpTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setHttpSuccTimes(Integer.parseInt(node.getElementsByTagName("HttpSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setHttpRate(Float.parseFloat(node.getElementsByTagName("HttpRate").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmtpTimes(Integer.parseInt(node.getElementsByTagName("SmtpTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmtpSuccTimes(Integer.parseInt(node.getElementsByTagName("SmtpSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmtpRate(Float.parseFloat(node.getElementsByTagName("SmtpRate").item(0).getFirstChild().getNodeValue().trim()));
		model.setPop3Times(Integer.parseInt(node.getElementsByTagName("Pop3Times").item(0).getFirstChild().getNodeValue().trim()));
		model.setPop3SuccTimes(Integer.parseInt(node.getElementsByTagName("Pop3SuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setPop3Rate(Float.parseFloat(node.getElementsByTagName("Pop3Rate").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapDLTimes(Integer.parseInt(node.getElementsByTagName("WapDLTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapDLSuccTimes(Integer.parseInt(node.getElementsByTagName("WapDLSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setWapDLRate(Float.parseFloat(node.getElementsByTagName("WapDLRate").item(0).getFirstChild().getNodeValue().trim()));

		model.setSmsSendTimes(Integer.parseInt(node.getElementsByTagName("SmsSendTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmsSendSuccTimes(Integer.parseInt(node.getElementsByTagName("SmsSendSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmsSendDelay(Float.parseFloat(node.getElementsByTagName("SmsSendDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmsReceiveSuccTimes(Integer.parseInt(node.getElementsByTagName("SmsReceiveSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setSmsPTPDelay(Float.parseFloat(node.getElementsByTagName("SmsPTPDelay").item(0).getFirstChild().getNodeValue().trim()));
			
		model.setMmsTimes(Integer.parseInt(node.getElementsByTagName("MmsTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setMmsSuccTimes(Integer.parseInt(node.getElementsByTagName("MmsSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setMmsSendDelay(Float.parseFloat(node.getElementsByTagName("MmsSendDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setMmsPushSuccTimes(Integer.parseInt(node.getElementsByTagName("MmsPushSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setMmsPushDelay(Float.parseFloat(node.getElementsByTagName("MmsPushDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setMmsReceiveSuccTimes(Integer.parseInt(node.getElementsByTagName("MmsReceiveSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setMmsPTPDelay(Float.parseFloat(node.getElementsByTagName("MmsPTPDelay").item(0).getFirstChild().getNodeValue().trim()));
			
		model.setFetionTimes(Integer.parseInt(node.getElementsByTagName("FetionTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFetionSuccTimes(Integer.parseInt(node.getElementsByTagName("FetionSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFetionDelay(Float.parseFloat(node.getElementsByTagName("FetionDelay").item(0).getFirstChild().getNodeValue().trim()));
		model.setFetionReceiveSuccTimes(Integer.parseInt(node.getElementsByTagName("FetionReceiveSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
		model.setFetionPTPDelay(Float.parseFloat(node.getElementsByTagName("FetionPTPDelay").item(0).getFirstChild().getNodeValue().trim()));
		return model;
	}
	public static String TotalTaskModelToXml(TotalTaskModel model){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
		sb.append("<root>\n\r");
		sb.append("\t<DialTimes>"+model.getDialTimes()+"</DialTimes>\n\r");
		sb.append("\t<DialSuccTimes>"+model.getDialSuccTimes()+"</DialSuccTimes>\n\r");
		sb.append("\t<DialDropTimes>"+model.getDialDropTimes()+"</DialDropTimes>\n\r");
		sb.append("\t<VideoDTimes>"+model.getVideoDTimes()+"</VideoDTimes>\n\r");
		sb.append("\t<VideoDSuccTimes>"+model.getVideoDSuccTimes()+"</VideoDSuccTimes>\n\r");
		sb.append("\t<VideoDDropTimes>"+model.getVideoDDropTimes()+"</VideoDDropTimes>\n\r");
		sb.append("\t<VideoDDelay>"+model.getVideoDDelay()+"</VideoDDelay>\n\r");
		                      
		sb.append("\t<AttachTimes>"+model.getAttachTimes()+"</AttachTimes>\n\r");
		sb.append("\t<AttachSuccTimes>"+model.getAttachSuccTimes()+"</AttachSuccTimes>\n\r");
		sb.append("\t<AttachDelay>"+model.getAttachDelay()+"</AttachDelay>\n\r");
		sb.append("\t<PdpTimes>"+model.getPdpTimes()+"</PdpTimes>\n\r");
		sb.append("\t<PdpSuccTimes>"+model.getPdpSuccTimes()+"</PdpSuccTimes>\n\r");
		sb.append("\t<PdpDelay>"+model.getPdpDelay()+"</PdpDelay>\n\r");
		sb.append("\t<PingTimes>"+model.getPingTimes()+"</PingTimes>\n\r");
		sb.append("\t<PingSuccTimes>"+model.getPingSuccTimes()+"</PingSuccTimes>\n\r");
		sb.append("\t<PingDelay>"+model.getPingDelay()+"</PingDelay>\n\r");
		sb.append("\t<WapRefTimes>"+model.getWapRefTimes()+"</WapRefTimes>\n\r");
		sb.append("\t<WapRefSuccTimes>"+model.getWapRefSuccTimes()+"</WapRefSuccTimes>\n\r");
		sb.append("\t<WapRefDelay>"+model.getWapRefDelay()+"</WapRefDelay>\n\r");
		sb.append("\t<WapLoginTimes>"+model.getWapLoginTimes()+"</WapLoginTimes>\n\r");
		sb.append("\t<WapLoginSuccTimes>"+model.getWapLoginSuccTimes()+"</WapLoginSuccTimes>\n\r");
		sb.append("\t<WapLoginDelay>"+model.getWapLoginDelay()+"</WapLoginDelay>\n\r");
		                      
		sb.append("\t<FtpULTimes>"+model.getFtpULTimes()+"</FtpULTimes>\n\r");
		sb.append("\t<FtpULSuccTimes>"+model.getFtpULSuccTimes()+"</FtpULSuccTimes>\n\r");
		sb.append("\t<FtpULRate>"+model.getFtpULRate()+"</FtpULRate>\n\r");
		sb.append("\t<FtpDLTimes>"+model.getFtpDLTimes()+"</FtpDLTimes>\n\r");
		sb.append("\t<FtpDLSuccTimes>"+model.getFtpDLSuccTimes()+"</FtpDLSuccTimes>\n\r");
		sb.append("\t<FtpDLRate>"+model.getFtpDLRate()+"</FtpDLRate>\n\r");
		sb.append("\t<HttpTimes>"+model.getHttpTimes()+"</HttpTimes>\n\r");
		sb.append("\t<HttpSuccTimes>"+model.getHttpSuccTimes()+"</HttpSuccTimes>\n\r");
		sb.append("\t<HttpRate>"+model.getHttpRate()+"</HttpRate>\n\r");
		sb.append("\t<SmtpTimes>"+model.getSmtpTimes()+"</SmtpTimes>\n\r");
		sb.append("\t<SmtpSuccTimes>"+model.getSmtpSuccTimes()+"</SmtpSuccTimes>\n\r");
		sb.append("\t<SmtpRate>"+model.getSmtpRate()+"</SmtpRate>\n\r");
		sb.append("\t<Pop3Times>"+model.getPop3Times()+"</Pop3Times>\n\r");
		sb.append("\t<Pop3SuccTimes>"+model.getPop3SuccTimes()+"</Pop3SuccTimes>\n\r");
		sb.append("\t<Pop3Rate>"+model.getPop3Rate()+"</Pop3Rate>\n\r");
		sb.append("\t<WapDLTimes>"+model.getWapDLTimes()+"</WapDLTimes>\n\r");
		sb.append("\t<WapDLSuccTimes>"+model.getWapDLSuccTimes()+"</WapDLSuccTimes>\n\r");
		sb.append("\t<WapDLRate>"+model.getWapDLRate()+"</WapDLRate>\n\r");
		                      
		sb.append("\t<SmsSendTimes>"+model.getSmsSendTimes()+"</SmsSendTimes>\n\r");
		sb.append("\t<SmsSendSuccTimes>"+model.getSmsSendSuccTimes()+"</SmsSendSuccTimes>\n\r");
		sb.append("\t<SmsSendDelay>"+model.getSmsSendDelay()+"</SmsSendDelay>\n\r");
		sb.append("\t<SmsReceiveSuccTimes>"+model.getSmsReceiveSuccTimes()+"</SmsReceiveSuccTimes>\n\r");
		sb.append("\t<SmsPTPDelay>"+model.getSmsPTPDelay()+"</SmsPTPDelay>\n\r");
		                      
		sb.append("\t<MmsTimes>"+model.getMmsTimes()+"</MmsTimes>\n\r");
		sb.append("\t<MmsSuccTimes>"+model.getMmsSuccTimes()+"</MmsSuccTimes>\n\r");
		sb.append("\t<MmsSendDelay>"+model.getMmsSendDelay()+"</MmsSendDelay>\n\r");
		sb.append("\t<MmsPushSuccTimes>"+model.getMmsPushSuccTimes()+"</MmsPushSuccTimes>\n\r");
		sb.append("\t<MmsPushDelay>"+model.getMmsPushDelay()+"</MmsPushDelay>\n\r");
		sb.append("\t<MmsReceiveSuccTimes>"+model.getMmsReceiveSuccTimes()+"</MmsReceiveSuccTimes>\n\r");
		sb.append("\t<MmsPTPDelay>"+model.getMmsPTPDelay()+"</MmsPTPDelay>\n\r");
		                      
		sb.append("\t<FetionTimes>"+model.getFetionTimes()+"</FetionTimes>\n\r");
		sb.append("\t<FetionSuccTimes>"+model.getFetionSuccTimes()+"</FetionSuccTimes>\n\r");
		sb.append("\t<FetionDelay>"+model.getFetionDelay()+"</FetionDelay>\n\r");
		sb.append("\t<FetionReceiveSuccTimes>"+model.getFetionReceiveSuccTimes()+"</FetionReceiveSuccTimes>\n\r");
		sb.append("\t<FetionPTPDelay>"+model.getFetionPTPDelay()+"</FetionPTPDelay>\n\r");
		sb.append("</root>\n\r");
		return sb.toString();
	}
	public static TotalEventModel TotalEventXmlToModel(Element node){
		TotalEventModel model  = new TotalEventModel();
//		model.setDialTimes(Integer.parseInt(node.getElementsByTagName("DialTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setDialSuccTimes(Integer.parseInt(node.getElementsByTagName("DialSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setDialFaildTimes(Integer.parseInt(node.getElementsByTagName("DialFaildTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setDialDropTimes(Integer.parseInt(node.getElementsByTagName("DialDropTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setSwitchSuccTimes(Integer.parseInt(node.getElementsByTagName("SwitchSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setSwitchFaildTimes(Integer.parseInt(node.getElementsByTagName("SwitchFaildTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setSdcchAllotFaild(Integer.parseInt(node.getElementsByTagName("SdcchAllotFaild").item(0).getFirstChild().getNodeValue().trim()));
//		model.setTchAllotFaild(Integer.parseInt(node.getElementsByTagName("TchAllotFaild").item(0).getFirstChild().getNodeValue().trim()));
//		model.setLacTimes(Integer.parseInt(node.getElementsByTagName("LacTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setLacSuccTimes(Integer.parseInt(node.getElementsByTagName("LacSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setRauTimes(Integer.parseInt(node.getElementsByTagName("RauTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setRauSuccTimes(Integer.parseInt(node.getElementsByTagName("RauSuccTimes").item(0).getFirstChild().getNodeValue().trim()));
//		model.setAreaReelect(Integer.parseInt(node.getElementsByTagName("AreaReelect").item(0).getFirstChild().getNodeValue().trim()));
//		model.setSignalCoverWeak(Integer.parseInt(node.getElementsByTagName("SignalCoverWeak").item(0).getFirstChild().getNodeValue().trim()));
//		model.setRxQualityWeak(Integer.parseInt(node.getElementsByTagName("RxQualityWeak").item(0).getFirstChild().getNodeValue().trim()));
//		model.setSwitchFrequent(Integer.parseInt(node.getElementsByTagName("SwitchFrequent").item(0).getFirstChild().getNodeValue().trim()));
		return model;
	}
	public static String TotalEventModelToXml(TotalEventModel model){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
		sb.append("<root>\n\r");
//		sb.append("\t<DialTimes>"+model.getDialTimes()+"</DialTimes>\n\r");
//		sb.append("\t<DialSuccTimes>"+model.getDialSuccTimes()+"</DialSuccTimes>\n\r");
//		sb.append("\t<DialFaildTimes>"+model.getDialFaildTimes()+"</DialFaildTimes>\n\r");
//		sb.append("\t<DialDropTimes>"+model.getDialDropTimes()+"</DialDropTimes>\n\r");
//		sb.append("\t<SwitchSuccTimes>"+model.getSwitchSuccTimes()+"</SwitchSuccTimes>\n\r");
//		sb.append("\t<SwitchFaildTimes>"+model.getSwitchFaildTimes()+"</SwitchFaildTimes>\n\r");
//		sb.append("\t<SdcchAllotFaild>"+model.getSdcchAllotFaild()+"</SdcchAllotFaild>\n\r");
//		sb.append("\t<TchAllotFaild>"+model.getTchAllotFaild()+"</TchAllotFaild>\n\r");
//		sb.append("\t<LacTimes>"+model.getLacTimes()+"</LacTimes>\n\r");
//		sb.append("\t<LacSuccTimes>"+model.getLacSuccTimes()+"</LacSuccTimes>\n\r");
//		sb.append("\t<RauTimes>"+model.getRauTimes()+"</RauTimes>\n\r");
//		sb.append("\t<RauSuccTimes>"+model.getRauSuccTimes()+"</RauSuccTimes>\n\r");
//		sb.append("\t<AreaReelect>"+model.getAreaReelect()+"</AreaReelect>\n\r");
//		sb.append("\t<SignalCoverWeak>"+model.getSignalCoverWeak()+"</SignalCoverWeak>\n\r");
//		sb.append("\t<RxQualityWeak>"+model.getRxQualityWeak()+"</RxQualityWeak>\n\r");
//		sb.append("\t<SwitchFrequent>"+model.getSwitchFrequent()+"</SwitchFrequent>\n\r");
		sb.append("</root>\n\r");
		return sb.toString();
	}
	public static TotalParaModel TotalParaXmlToModel(Element node){
		TotalParaModel model = new TotalParaModel();
		model.setRxQualFullMax(Integer.parseInt(node.getElementsByTagName("RxQualFullMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxQualFullMin(Integer.parseInt(node.getElementsByTagName("RxQualFullMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxQualFullAverage(Integer.parseInt(node.getElementsByTagName("RxQualFullAverage").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxQualSubMax(Integer.parseInt(node.getElementsByTagName("RxQualSubMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxQualSubMin(Integer.parseInt(node.getElementsByTagName("RxQualSubMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxQualSubAverage(Integer.parseInt(node.getElementsByTagName("RxQualSubAverage").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxLevFullMax(Integer.parseInt(node.getElementsByTagName("RxLevFullMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxLevFullMin(Integer.parseInt(node.getElementsByTagName("RxLevFullMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxLevFullAverage(Integer.parseInt(node.getElementsByTagName("RxLevFullAverage").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxLevSubMax(Integer.parseInt(node.getElementsByTagName("RxLevSubMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxLevSubMin(Integer.parseInt(node.getElementsByTagName("RxLevSubMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setRxLevSubAverage(Integer.parseInt(node.getElementsByTagName("RxLevSubAverage").item(0).getFirstChild().getNodeValue().trim()));
		model.setFerFullMax(Integer.parseInt(node.getElementsByTagName("FerFullMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setFerFullMin(Integer.parseInt(node.getElementsByTagName("FerFullMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setFerFullAverage(Integer.parseInt(node.getElementsByTagName("FerFullAverage").item(0).getFirstChild().getNodeValue().trim()));
		model.setFerSubMax(Integer.parseInt(node.getElementsByTagName("FerSubMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setFerSubMin(Integer.parseInt(node.getElementsByTagName("FerSubMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setFerSubAverage(Integer.parseInt(node.getElementsByTagName("FerSubAverage").item(0).getFirstChild().getNodeValue().trim()));
		model.setBcchLevMax(Integer.parseInt(node.getElementsByTagName("BcchLevMax").item(0).getFirstChild().getNodeValue().trim()));
		model.setBcchLevMin(Integer.parseInt(node.getElementsByTagName("BcchLevMin").item(0).getFirstChild().getNodeValue().trim()));
		model.setBcchLevAverage(Integer.parseInt(node.getElementsByTagName("BcchLevAverage").item(0).getFirstChild().getNodeValue().trim()));
		return model;
	}
	public static String TotalParaModelToXml(TotalParaModel model){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\r");
		sb.append("<root>\n\r");
		sb.append("\t<RxQualFullMax>"+model.getRxQualFullMax()+"</RxQualFullMax>\n\r");
		sb.append("\t<RxQualFullMin>"+model.getRxQualFullMin()+"</RxQualFullMin>\n\r");
		sb.append("\t<RxQualFullAverage>"+model.getRxQualFullAverage()+"</RxQualFullAverage>\n\r");
		sb.append("\t<RxQualSubMax>"+model.getRxQualSubMax()+"</RxQualSubMax>\n\r");
		sb.append("\t<RxQualSubMin>"+model.getRxQualSubMin()+"</RxQualSubMin>\n\r");
		sb.append("\t<RxQualSubAverage>"+model.getRxQualSubAverage()+"</RxQualSubAverage>\n\r");
		sb.append("\t<RxLevFullMax>"+model.getRxLevFullMax()+"</RxLevFullMax>\n\r");
		sb.append("\t<RxLevFullMin>"+model.getRxLevFullMin()+"</RxLevFullMin>\n\r");
		sb.append("\t<RxLevFullAverage>"+model.getRxLevFullAverage()+"</RxLevFullAverage>\n\r");
		sb.append("\t<RxLevSubMax>"+model.getRxLevSubMax()+"</RxLevSubMax>\n\r");
		sb.append("\t<RxLevSubMin>"+model.getRxLevSubMin()+"</RxLevSubMin>\n\r");
		sb.append("\t<RxLevSubAverage>"+model.getRxLevSubAverage()+"</RxLevSubAverage>\n\r");
		sb.append("\t<FerFullMax>"+model.getFerFullMax()+"</FerFullMax>\n\r");
		sb.append("\t<FerFullMin>"+model.getFerFullMin()+"</FerFullMin>\n\r");
		sb.append("\t<FerFullAverage>"+model.getFerFullAverage()+"</FerFullAverage>\n\r");
		sb.append("\t<FerSubMax>"+model.getFerSubMax()+"</FerSubMax>\n\r");
		sb.append("\t<FerSubMin>"+model.getFerSubMin()+"</FerSubMin>\n\r");
		sb.append("\t<FerSubAverage>"+model.getFerSubAverage()+"</FerSubAverage>\n\r");
		sb.append("\t<BcchLevMax>"+model.getBcchLevMax()+"</BcchLevMax>\n\r");
		sb.append("\t<BcchLevMin>"+model.getBcchLevMin()+"</BcchLevMin>\n\r");
		sb.append("\t<BcchLevAverage>"+model.getBcchLevAverage()+"</BcchLevAverage>\n\r");
		sb.append("</root>\n\r");
		return sb.toString();
	}
}

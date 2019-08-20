package com.walktour.gui.task.activity.scannertsma.model;

import com.dingli.seegull.SeeGullFlags;
import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *节点说明：
 ResultBufferDepth：缓存，范围为1 -1024，Pioneer默认为1024；
 ReceiverIndex：扫频仪索引，暂不使用，固定赋0；
 FrontEndSelectionMask：物理接收天线，暂不使用，固定赋0；
 ValuePerSec：扫频速率，单位Hz，Pioneer界面初始值为400Hz，因为配置项是每1000秒，所以传进来的配置需要乘上1000；
 DecodeOutputMode：0 为实时显示，1 为缓存显示，暂不使用，固定赋0；
 ChannelCount：信道配置项个数，最大值为1024；
 GSMChannel – Band：Band值；
 GSMChannel – IsULFlag：上下行标识，0为下行，1为上行，Pioneer界面初始值为0；
 GSMChannel – Channel：逻辑频点；
 Demodulation – Threshold：暂不使用，固定赋0；
 Demodulation – FrontEndSelectionMask：暂不使用，固定赋1；
 Demodulation – L3MsgCount：层三信息个数，最大值为32；
 Demodulation – L3Message：层三信息；

 * 
 * @author jinfeng.xie
 */

public class ColorCode extends ScanTaskModel {
	private static final String TAG = "ColorCodeModel";
	private ArrayList<Channel> channelList = new ArrayList<Channel>();

	private long ResultBufferDepth;
	private int FrontEndSelectionMask;
	private int ReceiverIndex;

	private long ValuePerSec;

	private int DecodeOutputMode;

	private Demodulation Demodulation=new Demodulation();

	public ArrayList<Channel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<Channel> channelList) {
		this.channelList = channelList;
	}

	public long getResultBufferDepth() {
		return ResultBufferDepth;
	}

	public void setResultBufferDepth(long resultBufferDepth) {
		ResultBufferDepth = resultBufferDepth;
	}

	public int getReceiverIndex() {
		return ReceiverIndex;
	}

	public void setReceiverIndex(int receiverIndex) {
		ReceiverIndex = receiverIndex;
	}

	public long getValuePerSec() {
		return ValuePerSec;
	}

	public void setValuePerSec(long valuePerSec) {
		ValuePerSec = valuePerSec;
	}


	public com.walktour.gui.task.activity.scannertsma.model.Demodulation getDemodulation() {
		return Demodulation;
	}

	public int getFrontEndSelectionMask() {
		return FrontEndSelectionMask;
	}

	public void setFrontEndSelectionMask(int frontEndSelectionMask) {
		FrontEndSelectionMask = frontEndSelectionMask;
	}

	public void setDemodulation(com.walktour.gui.task.activity.scannertsma.model.Demodulation demodulation) {
		Demodulation = demodulation;
	}

	public int getDecodeOutputMode() {
		return DecodeOutputMode;
	}

	public void setDecodeOutputMode(int decodeOutputMode) {
		DecodeOutputMode = decodeOutputMode;
	}
	/**
	 * ColorCodeModel Model转为XML
	 *
	 */
	@Override
	public void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException {
		LogUtil.d(TAG, "colorCodeModel");
		serializer.startTag(null, "ColorCode");
		NodeValue(serializer,"Enable", this == null ? "0" : this.getEnable());
		NodeValue(serializer,"TaskName", "ColorCode");
		NodeValue(serializer,"TaskType", taskType.name());
		serializer.startTag(null,"GSMSetting");
		NodeValue(serializer,"ResultBufferDepth",
				this == null ? 0 : this.getResultBufferDepth());
		NodeValue(serializer,"IsUlorDl",
				this == null ? false : this.isUpload());
		NodeValue(serializer,"ReceiverIndex", this == null ? 0
				: this.getReceiverIndex());
		NodeValue(serializer,"FrontEndSelectionMask",
				this == null ? 0 : this.getFrontEndSelectionMask());
		NodeValue(serializer,"ValuePerSec",
				this == null ? 0 : this.getValuePerSec()*1000);
		NodeValue(serializer,"DecodeOutputMode",
				this == null ? 0 : this.getDecodeOutputMode());
		NodeValue(serializer,"ChannelCount",channelList==null?""+0:""+channelList.size());
		serializer.startTag("", "Channels");
		for (int i=0;i<channelList.size();i++){
			serializer.startTag("", "Channel_"+i);
			channelList.get(i).writeToXml(serializer);
			serializer.endTag("", "Channel_"+i);
		}
		serializer.endTag("", "Channels");
		getDemodulation().writeToXml(serializer);
		serializer.endTag(null,"GSMSetting");
		serializer.endTag(null, "ColorCode");
	}
	/**
	 * GSM ColorCode ColorCode转Model
	 *
	 * @return
	 */
	@Override
	public void parserXml(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {
		this.setTaskType(TestSchemaType.GSMCOLORCODE.name());
		this.setStyle(0);
		this.setProtocolCode(SeeGullFlags.ProtocolCodes.PROTOCOL_GSM);
		this.setScanType(SeeGullFlags.ScanTypes.eScanType_ColorCode);
		this.setScanMode(0);

		int eventType = xmlParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_TAG:

					if ("ColorCode".equals(xmlParser.getName())) {
						this.setTaskName("ColorCode");
						this.setGroupName(TestSchemaType.valueOf(getTaskType()).getGroupName());
					} else if ("Enable".equals(xmlParser.getName())) {
						this.setEnable(Integer.valueOf(xmlParser.nextText()));

					} else if ("IsUlorDl".equals(xmlParser.getName())) {
						this.setUpload(Boolean.valueOf(xmlParser.nextText()));

					} else if ("ResultBufferDepth".equals(xmlParser.getName())) {
						this.setResultBufferDepth(Long.valueOf(xmlParser.nextText()));
					} else if ("ReceiverIndex".equals(xmlParser.getName())) {
						this.setReceiverIndex(Integer.valueOf(xmlParser.nextText()));
					} else if ("FrontEndSelectionMask".equals(xmlParser.getName())) {
						this.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
					} else if ("ValuePerSec".equals(xmlParser.getName())) {
						this.setValuePerSec(Long.valueOf(xmlParser.nextText())/1000);
					} else if ("DecodeOutputMode".equals(xmlParser.getName())) {
						this.setDecodeOutputMode(Integer.valueOf(xmlParser.nextText()));
					}else if ("Channels".equals(xmlParser.getName())) {
						this.setChannelList(Channel.parserXml(xmlParser));
					}else if ("Demodulation".equals(xmlParser.getName())) {
						this.setDemodulation(Demodulation.parserXml(xmlParser));
					}
					break;
				case XmlPullParser.END_TAG:
					if ("ColorCode".equals(xmlParser.getName())) {
						testModelList.add(this);
						return;
					}
					break;
			}
			eventType = xmlParser.next();
		}
	}
	@Override
	public String toString() {
		return "ColorCodeModel{" +
				"channelList=" + channelList +
				", ResultBufferDepth=" + ResultBufferDepth +
				", FrontEndSelectionMask=" + FrontEndSelectionMask +
				", ReceiverIndex=" + ReceiverIndex +
				", ValuePerSec=" + ValuePerSec +
				", DecodeOutputMode=" + DecodeOutputMode +
				", Demodulation=" + Demodulation +
				'}';
	}
}

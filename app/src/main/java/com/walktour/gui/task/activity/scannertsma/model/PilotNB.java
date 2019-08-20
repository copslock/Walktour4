package com.walktour.gui.task.activity.scannertsma.model;

/**
 * @author jinfeng.xie
 * @data 2019/2/19
 */

import com.walktour.model.TdL3Model;

import java.util.ArrayList;

/**<!-- NB -->
<NBIoTSetting>
	<ResultBufferDepthValue>0</ResultBufferDepthValue>
	<!-- MaxCount: 1024 -->
	<ChannelCount>1</ChannelCount>
	<NBChannel>
		<NBChannel_0>
			<Band>1288</Band>
			<EARFCN>3738</EARFCN>
			<AvgBlockCountPer1000Sec>5000</AvgBlockCountPer1000Sec>
			<OperationMode>3</OperationMode>
			<FrontEndSelectionMask>1</FrontEndSelectionMask>
			<FilterPCI>0</FilterPCI>
			<!-- MaxCount: 32 -->
			<L3Count>0</L3Count>
			<Demodulation>
				<!-- <Demodulation_0>
					<L3Message>0</L3Message>
				</Demodulation_0> -->
			</Demodulation>
			<FrequencyOffset>2500</FrequencyOffset>
			 <Frequency>2110</Frequency>
		</NBChannel_0>
	</NBChannel>
</NBIoTSetting>


* */
public class PilotNB {
	private int ResultBufferDepthValue;
	private	ArrayList<NBChannelModel> channelModels=new ArrayList<>();

	public int getResultBufferDepthValue() {
		return ResultBufferDepthValue;
	}

	public void setResultBufferDepthValue(int resultBufferDepthValue) {
		ResultBufferDepthValue = resultBufferDepthValue;
	}

	public ArrayList<NBChannelModel> getChannelModels() {
		return channelModels;
	}

	public void setChannelModels(ArrayList<NBChannelModel> channelModels) {
		this.channelModels = channelModels;
	}

	@Override
	public String toString() {
		return "PilotNB{" +
				"ResultBufferDepthValue=" + ResultBufferDepthValue +
				", channelModels=" + channelModels +
				'}';
	}

	class NBChannelModel{
		 private	int Band;
		 private	int EARFCN;
		 private	int AvgBlockCountPer1000Sec;
		 private    int OperationMode;
		 private	int FrontEndSelectionMask;
		 private	int FilterPCI;
		 private ArrayList<TdL3Model> l3Models;
		 private int FrequencyOffset;
		 private int Frequency;

		 public int getBand() {
			 return Band;
		 }

		 public void setBand(int band) {
			 Band = band;
		 }

		 public int getEARFCN() {
			 return EARFCN;
		 }

		 public void setEARFCN(int EARFCN) {
			 this.EARFCN = EARFCN;
		 }

		 public int getAvgBlockCountPer1000Sec() {
			 return AvgBlockCountPer1000Sec;
		 }

		 public void setAvgBlockCountPer1000Sec(int avgBlockCountPer1000Sec) {
			 AvgBlockCountPer1000Sec = avgBlockCountPer1000Sec;
		 }

		 public int getOperationMode() {
			 return OperationMode;
		 }

		 public void setOperationMode(int operationMode) {
			 OperationMode = operationMode;
		 }

		 public int getFrontEndSelectionMask() {
			 return FrontEndSelectionMask;
		 }

		 public void setFrontEndSelectionMask(int frontEndSelectionMask) {
			 FrontEndSelectionMask = frontEndSelectionMask;
		 }

		 public int getFilterPCI() {
			 return FilterPCI;
		 }

		 public void setFilterPCI(int filterPCI) {
			 FilterPCI = filterPCI;
		 }

		 public ArrayList<TdL3Model> getL3Models() {
			 return l3Models;
		 }

		 public void setL3Models(ArrayList<TdL3Model> l3Models) {
			 this.l3Models = l3Models;
		 }

		 public int getFrequencyOffset() {
			 return FrequencyOffset;
		 }

		 public void setFrequencyOffset(int frequencyOffset) {
			 FrequencyOffset = frequencyOffset;
		 }

		 public int getFrequency() {
			 return Frequency;
		 }

		 public void setFrequency(int frequency) {
			 Frequency = frequency;
		 }

		 @Override
		 public String toString() {
			 return "NBChannelModel{" +
					 "Band=" + Band +
					 ", EARFCN=" + EARFCN +
					 ", AvgBlockCountPer1000Sec=" + AvgBlockCountPer1000Sec +
					 ", OperationMode=" + OperationMode +
					 ", FrontEndSelectionMask=" + FrontEndSelectionMask +
					 ", FilterPCI=" + FilterPCI +
					 ", l3Models=" + l3Models +
					 ", FrequencyOffset=" + FrequencyOffset +
					 ", Frequency=" + Frequency +
					 '}';
		 }
	 }
}

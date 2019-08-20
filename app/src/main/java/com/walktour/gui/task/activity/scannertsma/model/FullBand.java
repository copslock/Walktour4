package com.walktour.gui.task.activity.scannertsma.model;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jinfeng.xie
 * @data 2019/2/15
 */
/*
* <FullBand>
		<!-- <FullBand_0>
			<StartFrequency>0</StartFrequency>
			<StopFrequency>0</StopFrequency>
			<Spacing>0</Spacing>
			<BandWidth>0</BandWidth>
		</FullBand_0> -->
	</FullBand>
FullBand – StartFrequency：起始频率，单位KHz
FullBand – StopFrequency：终止频率，单位KHz
FullBand – Spacing：步进，单位KHz
FullBand – BandWidth：测量带宽，单位KHz

*/
public class FullBand extends ScanSonTaskModel {
    private long StartFrequency = 0;
    private long StopFrequency = 0;
    private long Spacing = 0;
    private long BandWidth = 0;
    private int Frequency;//Frequency：频率。该模式下，使用外部传入的频率，而非通过Band和EARFCN计算出的值；

    public long getStartFrequency() {
        return StartFrequency;
    }

    public void setStartFrequency(long startFrequency) {
        StartFrequency = startFrequency;
    }

    public long getStopFrequency() {
        return StopFrequency;
    }

    public void setStopFrequency(long stopFrequency) {
        StopFrequency = stopFrequency;
    }

    public long getSpacing() {
        return Spacing;
    }

    public void setSpacing(long spacing) {
        Spacing = spacing;
    }

    public long getBandWidth() {
        return BandWidth;
    }

    public void setBandWidth(long bandWidth) {
        BandWidth = bandWidth;
    }

    @Override
    public String toString() {
        return "FullBand{" +
                "StartFrequency=" + StartFrequency +
                ", StopFrequency=" + StopFrequency +
                ", Spacing=" + Spacing +
                ", BandWidth=" + BandWidth +
                '}';
    }

    /**
     * @return
     * @throws Exception
     */
    public static ArrayList<FullBand> parserFullBand(XmlPullParser xmlParser) throws Exception {

        ArrayList<FullBand> fullBands = new ArrayList<FullBand>();
        FullBand fullBand = null;

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (xmlParser.getName().contains("FullBand_")) {
                        fullBand = new FullBand();
                    } else if ("StartFrequency".equals(xmlParser.getName())) {
                        fullBand.setStartFrequency(Long.parseLong(xmlParser.nextText()));
                    } else if ("StopFrequency".equals(xmlParser.getName())) {
                        fullBand.setStopFrequency(Long.parseLong(xmlParser.nextText()));
                    } else if ("Spacing".equals(xmlParser.getName())) {
                        fullBand.setSpacing(Long.parseLong(xmlParser.nextText()));
                    } else if ("BandWidth".equals(xmlParser.getName())) {
                        fullBand.setBandWidth(Long.parseLong(xmlParser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (xmlParser.getName().contains("FullBand_")) {
                        fullBands.add(fullBand);
                    }
                    if ("FullBand".equals(xmlParser.getName())) {
                        return fullBands;
                    }
            }
            eventType = xmlParser.next();
        }
        return fullBands;
    }
    @Override
    void writeToXml(XmlSerializer serializer) throws IOException {
        NodeValue(serializer, "StartFrequency", this.getStartFrequency() + "");
        NodeValue(serializer, "StopFrequency", this.getStopFrequency() + "");
        NodeValue(serializer, "Spacing", this.getSpacing() + "");
        NodeValue(serializer, "BandWidth", this.getBandWidth() + "");
    }
}

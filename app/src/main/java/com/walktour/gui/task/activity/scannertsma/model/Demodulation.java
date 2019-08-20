package com.walktour.gui.task.activity.scannertsma.model;

import com.walktour.Utils.StringUtil;
import com.walktour.model.TdL3Model;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author jinfeng.xie
 * @data 2019/2/19
 */
/*
*                <Demodulation>
					<Threshold>0</Threshold>
					<FrontEndSelectionMask>1</FrontEndSelectionMask>
					<!-- MaxCount: 32 -->
					<L3MsgCount>10</L3MsgCount>
					<L3Message>25,26,2,69,7,3,27,28,31,24</L3Message>
				</Demodulation>
Demodulation – Threshold：暂不使用，固定赋0；
Demodulation – FrontEndSelectionMask：暂不使用，固定赋1；
Demodulation – L3MsgCount：层三信息个数，最大值为32；
Demodulation – L3Message：层三信息；

*/
public class Demodulation extends ScanSonTaskModel {
   private int Threshold =0;
    private int FrontEndSelectionMask=1;
    private ArrayList<TdL3Model> l3Models=new ArrayList<>();

    public int getThreshold() {
        return Threshold;
    }

    public void setThreshold(int threshold) {
        Threshold = threshold;
    }

    public int getFrontEndSelectionMask() {
        return FrontEndSelectionMask;
    }

    public void setFrontEndSelectionMask(int frontEndSelectionMask) {
        FrontEndSelectionMask = frontEndSelectionMask;
    }

    public ArrayList<TdL3Model> getL3Models() {
        return l3Models;
    }

    public void setL3Models(ArrayList<TdL3Model> l3Models) {
        this.l3Models = l3Models;
    }

    @Override
    void writeToXml(XmlSerializer serializer) throws IOException {

                serializer.startTag("", "Demodulation");
                NodeValue( serializer,"Threshold", this.getThreshold() + "");
                NodeValue(  serializer,"FrontEndSelectionMask", this.getFrontEndSelectionMask() + "");
                if (this.getL3Models()!=null){
                    NodeValue( serializer,"L3MsgCount", this.getL3Models().size() + "");
                    String l3msgs="";
                    for (int i=0;i<this.getL3Models().size();i++){
                        if (i==0){
                            l3msgs+=this.getL3Models().get(i).getId();
                        }else {
                            l3msgs+=(","+this.getL3Models().get(i).getId());
                        }
                    }
                    NodeValue( serializer,"L3Message", l3msgs+"");
                }else {
                    NodeValue(serializer, "L3MsgCount", "0");
                    NodeValue( serializer,"L3Message", "");
                }
                serializer.endTag("", "Demodulation");
    }

    public void LteDemodulationNodeXml(XmlSerializer serializer) throws IOException {
        try {
            NodeValue(serializer,"L3Count", l3Models.size());
            serializer.startTag("", "Demodulation");
            if (l3Models != null) {
                for (int i = 0; i < l3Models.size(); i++) {
                    serializer.startTag("", "Demodulation_" + i);
                    NodeValue(serializer,"Threshold", this.getThreshold() + "");
                    NodeValue(serializer,"FrontEndSelectionMask", this.getFrontEndSelectionMask() + "");
                    NodeValue(serializer,"L3Message", this.getL3Models().get(i).getId() + "");
                    serializer.endTag("", "Demodulation_" + i);
                }
            }
            serializer.endTag("", "Demodulation");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return
     * @throws Exception
     */
    public static Demodulation parserXml(XmlPullParser xmlParser) throws Exception {

        Demodulation demodulationModel = new Demodulation();


        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("Threshold".equals(xmlParser.getName())) {
                        demodulationModel.setThreshold(Integer.valueOf(xmlParser.nextText()));
                    }else if("FrontEndSelectionMask".equals(xmlParser.getName())){
                        demodulationModel.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
                    }else if("L3Message".equals(xmlParser.getName())){
                        demodulationModel.setL3Models(parserL3Model(xmlParser.nextText()));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("Demodulation".equals(xmlParser.getName())){
                        return  demodulationModel;
                    }
                    break;
            }
            eventType = xmlParser.next();
        }
        return demodulationModel;
    }

    /**
     * @return
     * @throws Exception
     */
    public static Demodulation parserLTEDemodulationModel(XmlPullParser xmlParser) throws Exception {

        Demodulation demodulationModel = new Demodulation();
        ArrayList<TdL3Model> l3Models = new ArrayList<>();
        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("SINRtoThreshold".equals(xmlParser.getName())) {
                        demodulationModel.setThreshold(Integer.valueOf(xmlParser.nextText()));
                    } else if ("FrontEndSelectionMask".equals(xmlParser.getName())) {
                        demodulationModel.setFrontEndSelectionMask(Integer.valueOf(xmlParser.nextText()));
                    } else if ("L3Message".equals(xmlParser.getName())) {
                        TdL3Model model = new TdL3Model();
                        model.setId(Long.parseLong(xmlParser.nextText()));
//                        model.setL3Msg(xmlParser.nextText());
                        l3Models.add(model);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("Demodulation".equals(xmlParser.getName())) {
                        return demodulationModel;
                    }
                    break;
            }
            eventType = xmlParser.next();
        }
        demodulationModel.setL3Models(l3Models);
        return demodulationModel;
    }

    private static ArrayList<TdL3Model> parserL3Model(String s) {
        ArrayList<TdL3Model> l3Models = new ArrayList<>();
        TdL3Model l3Model=null;
        if (StringUtil.isEmpty(s)){
            return l3Models;
        }
        String[] strs = s.split(",");
        for (int i=0;i<strs.length;i++){
            System.out.println(strs[i]);
            l3Model=new TdL3Model();
            l3Model.setId(Long.parseLong(strs[i]));
            l3Model.setL3Msg(strs[i]);
            l3Models.add(l3Model);
        }
        return l3Models;
    }

    @Override
    public String toString() {
        return "DemodulationModel{" +
                "Threshold=" + Threshold +
                ", FrontEndSelectionMask=" + FrontEndSelectionMask +
                ", l3Models=" + l3Models +
                '}';
    }
}

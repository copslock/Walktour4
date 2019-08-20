package com.walktour.gui.task.activity.scannertsma.model;

import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jinfeng.xie
 * @data 2019/2/1
 * 注意：NB下的进行盲扫，ReqElement节点中必须以LTE – NB – LTE – NB的顺序插入
 * 测试业务配置模板：
 * <ACDSetting>
 * <MeasurementMode>2</MeasurementMode>
 * <Sensitivity>2</Sensitivity>
 * <NumberOfTrialsPerChannel>0</NumberOfTrialsPerChannel>
 * <MinimumDetectedBwInHz>5000000</MinimumDetectedBwInHz>
 * <NumReqElement>2</NumReqElement>
 * <ReqElement>
 * <ReqElement_0>
 * <NetType>5</NetType>
 * <BandIdMask>8</BandIdMask>
 * </ReqElement_0>
 * <ReqElement_1>
 * <NetType>16</NetType>
 * <BandIdMask>8</BandIdMask>
 * </ReqElement_1>
 * </ReqElement>
 * </ACDSetting>
 */

public class Blind extends ScanTaskModel {
    private static final String TAG = "Blind";
    /*
        枚举值，Pioneer界面初始值为1。
                1：SIMPLE，；
                2：SMART，；
    */
    private int MeasurementMode = 1;
    /*
        Sensitivity：枚举值，Pioneer界面初始值为2。
                1：FAIR， 很快的检测速度，合理的灵敏度；
                2：GOOD，检测速度快，灵敏度高；
                3：EXCELLENT，合理的检测速度，
    */
    private int Sensitivity = 2;
    //    NumberOfTrialsPerChannel：暂不使用，固定赋0；
    private int NumberOfTrialsPerChannel = 0;
    /* MinimumDetectedBwInHz：固定值，1.4MHz, 3MHz, 5MHz, 10MHz, 15MHz and 20MHz。
     只有LTE或NB下且MeasurementMode必须为SMART才可配置，
     Pioneer界面初始值为5MHz，其他网络不需要配置。*/
    private int MinimumDetectedBwInHz = 5000000;
    //   NumReqElement：配置项个数，最大值为128。
    private int NumReqElement = 2;

    private ArrayList<ReqElement> reqElements = new ArrayList<>();

    public int getMeasurementMode() {
        return MeasurementMode;
    }

    public void setMeasurementMode(int measurementMode) {
        MeasurementMode = measurementMode;
    }

    public int getSensitivity() {
        return Sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        Sensitivity = sensitivity;
    }

    public int getNumberOfTrialsPerChannel() {
        return NumberOfTrialsPerChannel;
    }

    public void setNumberOfTrialsPerChannel(int numberOfTrialsPerChannel) {
        NumberOfTrialsPerChannel = numberOfTrialsPerChannel;
    }

    public int getMinimumDetectedBwInHz() {
        return MinimumDetectedBwInHz;
    }

    public void setMinimumDetectedBwInHz(int minimumDetectedBwInHz) {
        MinimumDetectedBwInHz = minimumDetectedBwInHz;
    }

    public int getNumReqElement() {
        return NumReqElement;
    }

    public void setNumReqElement(int numReqElement) {
        NumReqElement = numReqElement;
    }

    public ArrayList<ReqElement> getReqElements() {
        return reqElements;
    }

    public void setReqElements(ArrayList<ReqElement> reqElements) {
        this.reqElements = reqElements;
    }

    @Override
    public void writeToXml(XmlSerializer serializer, TestSchemaType taskType) throws IOException {
        LogUtil.d(TAG, "blindModelToXml");
        serializer.startTag(null, "Blind");
        NodeValue(serializer, "Enable", this == null ? 0 : this.getEnable());
        NodeValue(serializer, "TaskName", "Blind");
        NodeValue(serializer, "TaskType", taskType.name());
        serializer.startTag(null, "ACDSetting");
        NodeValue(serializer, "MeasurementMode", this == null ? 0 : this.getMeasurementMode());
        NodeValue(serializer, "Sensitivity", this == null ? 0 : this.getSensitivity());
        NodeValue(serializer, "NumberOfTrialsPerChannel", this == null ? 0 : this.getNumberOfTrialsPerChannel());
        NodeValue(serializer, "MinimumDetectedBwInHz", this == null ? 0 : this.getMinimumDetectedBwInHz());
        NodeValue(serializer, "NumReqElement", this == null ? 0 : this.getNumReqElement());
        NodeValue(serializer, "MinimumDetectedBwInHz", this == null ? 0 : this.getMinimumDetectedBwInHz());
        NodeValue(serializer, "NumReqElement", reqElements == null ? 0 : reqElements.size());
        serializer.startTag("", "ReqElement");
        for (int i = 0; i < reqElements.size(); i++) {
            serializer.startTag("", "ReqElement_" + i);
            reqElements.get(i).writeToXml(serializer);
            serializer.endTag("", "ReqElement_" + i);
        }
        serializer.endTag("", "ReqElement");
        serializer.endTag(null, "ACDSetting");
        serializer.endTag(null, "Blind");
    }

    @Override
    public void parserXml(XmlPullParser xmlParser, List<ScanTaskModel> testModelList) throws Exception {

            this.setTaskName("Blind");

            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("TaskName".equals(xmlParser.getName())) {
                            this.setTaskName(xmlParser.nextText());
                        } else if ("TaskType".equals(xmlParser.getName())) {
                            TestSchemaType testType = TestSchemaType.valueOf(xmlParser.nextText());
                            this.setTaskType(testType.name());
                            this.setGroupName(testType.getGroupName());
                        } else if ("Enable".equals(xmlParser.getName())) {
                            this.setEnable(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ACDSetting".equals(xmlParser.getName())) {
                            this.setGroupName(TestSchemaType.valueOf(this.getTaskType()).getGroupName());
                        } else if ("Enable".equals(xmlParser.getName())) {
                            this.setEnable(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MeasurementMode".equals(xmlParser.getName())) {
                            this.setMeasurementMode(Integer.valueOf(xmlParser.nextText()));
                        } else if ("Sensitivity".equals(xmlParser.getName())) {
                            this.setSensitivity(Integer.valueOf(xmlParser.nextText()));
                        } else if ("NumberOfTrialsPerChannel".equals(xmlParser.getName())) {
                            this.setNumberOfTrialsPerChannel(Integer.valueOf(xmlParser.nextText()));
                        } else if ("MinimumDetectedBwInHz".equals(xmlParser.getName())) {
                            this.setMinimumDetectedBwInHz(Integer.valueOf(xmlParser.nextText()));
                        } else if ("NumReqElement".equals(xmlParser.getName())) {
                            this.setNumReqElement(Integer.valueOf(xmlParser.nextText()));
                        } else if ("ReqElements".equals(xmlParser.getName())) {
                            this.setReqElements(ReqElement.parserRReqElementModel(xmlParser));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("ACDSetting".equals(xmlParser.getName())) {
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
        return "Blind{" +
                "MeasurementMode=" + MeasurementMode +
                ", Sensitivity=" + Sensitivity +
                ", NumberOfTrialsPerChannel=" + NumberOfTrialsPerChannel +
                ", MinimumDetectedBwInHz=" + MinimumDetectedBwInHz +
                ", NumReqElement=" + NumReqElement +
                ", reqElements=" + reqElements +
                '}';
    }
}

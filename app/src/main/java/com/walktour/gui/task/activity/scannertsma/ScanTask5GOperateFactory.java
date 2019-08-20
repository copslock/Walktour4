package com.walktour.gui.task.activity.scannertsma;

import android.os.Environment;
import android.util.Xml;

import com.walktour.gui.task.activity.scannertsma.model.Blind;
import com.walktour.gui.task.activity.scannertsma.model.CW;
import com.walktour.gui.task.activity.scannertsma.model.Channel;
import com.walktour.gui.task.activity.scannertsma.model.ColorCode;
import com.walktour.gui.task.activity.scannertsma.model.Pilot;
import com.walktour.gui.task.activity.scannertsma.model.PilotLTE;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.gui.task.activity.scannertsma.model.Spectrum;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.task.activity.scannertsma.ui.ScannerTSMAInfoActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 扫频仪任务操作控制类
 *
 /**
 * @author jinfeng.xie
 */
public class ScanTask5GOperateFactory {
    private static final String TAG = "ScanTask5GOperateFactor";
    private ArrayList<ScanTaskModel> testModelList = new ArrayList<ScanTaskModel>();
    /**
     * 唯一实例
     */
    private static ScanTask5GOperateFactory sInstance = null;

    private XmlSerializer serializer;

    private static XmlPullParser xmlParser;

    public static final String TESTTYPE = "TestType";
    /**
     * 是否是PILOT编辑
     */
    public static final String IS_PILOT = "IsPilot";
    /**
     * 是否是LTE编辑
     */
    public static final String IS_LTE = "IsLTE";
    /**
     * 接口编码
     */
    public static final String NETTYPE = "NetType";

    public static final String IS_UPLOAD = "isUpload";

    private ArrayList<Channel> channelList = new ArrayList<Channel>();

    private ArrayList<Channel> restoreChannelList = new ArrayList<Channel>();                //还原频点列表


    private File newxmlfile;

    /**
     * 工厂操作单例
     *
     * @return
     */
    public static ScanTask5GOperateFactory getInstance() {
        if (sInstance == null) {
            sInstance = new ScanTask5GOperateFactory();
        }
        return sInstance;
    }


    /**
     * 初始化构造
     */
    private ScanTask5GOperateFactory() {
        newxmlfile = new File(Environment.getExternalStorageDirectory() + "/scanTSMAtasklist.xml");
        if (!newxmlfile.exists()) {
            try {
                newxmlfile.createNewFile();
                XmlFileCreator(testModelList);
            } catch (Exception e) {
                LogUtil.e(TAG, "create xml exception");
                e.printStackTrace();
            }
        }
    }


    /**
     * 生成任务XML
     *
     * @param baseModelList
     */
    private void XmlFileCreator(List<ScanTaskModel> baseModelList) {
        // we have to bind the new file with a FileOutputStream
        LogUtil.d(TAG, "create xml");
        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(newxmlfile);
        } catch (FileNotFoundException e) {
            LogUtil.d(TAG, "can't create FileOutputStream");
        }
        // we create a XmlSerializer in order to write xml data
        serializer = Xml.newSerializer();
        try {
            // we set the FileOutputStream as output for the serializer, using
            // UTF-8 encoding
            serializer.setOutput(fileos, "UTF-8");
            // Write <?xml declaration with encoding (if encoding not null) and
            // standalone flag (if standalone not null)
            serializer.startDocument(null, Boolean.valueOf(true));
            // set indentation option
            serializer.setFeature(
                    "http://xmlpull.org/v1/doc/features.html#indent-output",
                    true);
            ColorCode gsmColorCodeModel = new ColorCode();
            CW gsmCWModel = new CW();
            CW wcdmaCWModel = new CW();
            CW tdscdmaCWModel = new CW();
            CW lteCWModel = new CW();
            CW cdmaCWModel = new CW();

            Pilot wcdmaPilotModel = new Pilot();
            Pilot tdscdmaPilotModel = new Pilot();
            Pilot cdmaPilotModel = new Pilot();
            PilotLTE ltePilotModel = new PilotLTE();
            Pilot nblOTPilotModel = new Pilot();

            Spectrum gsmSpectrumModel = new Spectrum();
            Spectrum wcdmaSpectrumModel = new Spectrum();
            Spectrum tdsSpectrumModel = new Spectrum();
            Spectrum lteSpectrumModel = new Spectrum();
            Spectrum cmdaSpectrumModel = new Spectrum();

            Blind lteBlind = new Blind();
            Blind nbBlind = new Blind();
            Blind wcdmaBlind = new Blind();
            Blind cmdaBlind = new Blind();


            for (int i = 0; i < baseModelList.size(); i++) {
                switch (TestSchemaType.valueOf(baseModelList.get(i)
                        .getTaskType())) {
                    case GSMCOLORCODE:
                        gsmColorCodeModel = (ColorCode) baseModelList.get(i);
                        break;
                    case GSMCW:
                        gsmCWModel = (CW) baseModelList.get(i);
                        break;
                    case WCDMACW:
                        wcdmaCWModel = (CW) baseModelList.get(i);
                        break;
                    case WCDMAPILOT:
                        wcdmaPilotModel = (Pilot) baseModelList.get(i);
                        break;
                    case TDSCDMACW:
                        tdscdmaCWModel = (CW) baseModelList.get(i);
                        break;
                    case TDSCDMAPILOT:
                        tdscdmaPilotModel = (Pilot) baseModelList.get(i);
                        break;
                    case CDMACW:
                        cdmaCWModel = (CW) baseModelList.get(i);
                        break;
                    case CDMAPILOT:
                        cdmaPilotModel = (Pilot) baseModelList.get(i);
                        break;
                    case LTECW:
                        lteCWModel = (CW) baseModelList.get(i);
                        break;
                    case LTEPILOT:
                        ltePilotModel = (PilotLTE) baseModelList.get(i);
                        break;
                    case LTESPECTRUM:
                        lteSpectrumModel = (Spectrum) baseModelList.get(i);
                        break;
                    case LTEBLINE:
                        lteBlind = (Blind) baseModelList.get(i);
                        break;
                    case NBLOTBlind:
                        nbBlind = (Blind) baseModelList.get(i);
                        break;
                    case CDMABLIND:
                        cmdaBlind = (Blind) baseModelList.get(i);
                        break;
                    case WCDMABLIND:
                        wcdmaBlind = (Blind) baseModelList.get(i);
                        break;
                    case NBLOTPILOT:
                        nblOTPilotModel = (Pilot) baseModelList.get(i);
                        break;
                    case GSMSPECTRUM:
                        gsmSpectrumModel = (Spectrum) baseModelList.get(i);
                        break;
                    case CDMASPECTURM:
                        cmdaSpectrumModel = (Spectrum) baseModelList.get(i);
                        break;
                    case TDSCDMASPCTRUM:
                        tdsSpectrumModel = (Spectrum) baseModelList.get(i);
                        break;
                    case WCDMASPECTURM:
                        wcdmaSpectrumModel = (Spectrum) baseModelList.get(i);
                        break;
                    default:
                        break;
                }
            }
            LogUtil.d(TAG, "create ScanScheme");
            serializer.startTag(null, "ScanScheme");
            // ///////////////////GSM//////////////////////
            serializer.startTag(null, "GSM_Group");
            gsmColorCodeModel.writeToXml(serializer, TestSchemaType.GSMCOLORCODE);
            gsmCWModel.writeToXml(serializer, TestSchemaType.GSMCW);
            gsmSpectrumModel.writeToXml(serializer, TestSchemaType.GSMSPECTRUM);
            serializer.endTag(null, "GSM_Group");

            // ///////////////////WCDMA//////////////////////
            serializer.startTag(null, "WCDMA_Group");
            wcdmaCWModel.writeToXml(serializer, TestSchemaType.WCDMACW);
            wcdmaPilotModel.writeToXml(serializer, TestSchemaType.WCDMAPILOT);
            wcdmaSpectrumModel.writeToXml(serializer, TestSchemaType.WCDMASPECTURM);
            wcdmaBlind.writeToXml(serializer, TestSchemaType.WCDMABLIND);
            serializer.endTag(null, "WCDMA_Group");

            // ///////////////////Tdscdma//////////////////////
            serializer.startTag(null, "TDSCDMA_Group");
            tdscdmaCWModel.writeToXml(serializer, TestSchemaType.TDSCDMACW);
            tdscdmaPilotModel.writeToXml(serializer, TestSchemaType.TDSCDMAPILOT);
            tdsSpectrumModel.writeToXml(serializer, TestSchemaType.TDSCDMASPCTRUM);
            serializer.endTag(null, "TDSCDMA_Group");

            // ///////////////////LTE//////////////////////
            serializer.startTag(null, "LTE_Group");
            lteCWModel.writeToXml(serializer, TestSchemaType.LTECW);
            ltePilotModel.writeToXml(serializer, TestSchemaType.LTEPILOT);
            lteSpectrumModel.writeToXml(serializer, TestSchemaType.LTESPECTRUM);
            lteBlind.writeToXml(serializer, TestSchemaType.LTEBLINE);
            serializer.endTag(null, "LTE_Group");

            // ///////////////////Cdma//////////////////////
            serializer.startTag(null, "CDMA_Group");
            cdmaCWModel.writeToXml(serializer, TestSchemaType.CDMACW);
            cdmaPilotModel.writeToXml(serializer, TestSchemaType.CDMAPILOT);
            cmdaSpectrumModel.writeToXml(serializer, TestSchemaType.CDMASPECTURM);
            cmdaBlind.writeToXml(serializer, TestSchemaType.CDMABLIND);
            serializer.endTag(null, "CDMA_Group");

            // ///////////////////NBLot//////////////////////
            serializer.startTag(null, "NB_Group");
            nblOTPilotModel.writeToXml(serializer, TestSchemaType.NBLOTPILOT);
            nbBlind.writeToXml(serializer, TestSchemaType.NBLOTBlind);
            serializer.endTag(null, "NB_Group");

            serializer.endTag(null, "ScanScheme");

            serializer.endDocument();
            // write xml data into the FileOutputStream
            serializer.flush();
            // finally we close the file stream
            if (fileos != null)
                fileos.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "error occurred while creating xml file");
            e.printStackTrace();
        }
    }


    /**
     * xml解析器
     *
     */
    private void xmlParser() throws Exception {
        testModelList.clear();
        try {
            FileInputStream fs = new FileInputStream(newxmlfile);
            xmlParser = Xml.newPullParser();
            xmlParser.setInput(fs, "UTF-8");
            int eventType = xmlParser.getEventType();
            while ((eventType = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("GSM_Group".equalsIgnoreCase(xmlParser.getName())) {
                            new ColorCode().parserXml(xmlParser, testModelList);
                            new CW().parserXml(xmlParser,testModelList);
                            new Spectrum().parserXml(xmlParser,testModelList);
                        } else if ("WCDMA_Group".equalsIgnoreCase(xmlParser.getName())) {
                            new CW().parserXml(xmlParser,testModelList);
                             new Pilot().parserXml(xmlParser,testModelList);
                            new Spectrum().parserXml(xmlParser,testModelList);
                           new Blind().parserXml(xmlParser,testModelList);
                        } else if ("TDSCDMA_Group".equalsIgnoreCase(xmlParser.getName())) {
                            new CW().parserXml(xmlParser,testModelList);
                            new Pilot().parserXml(xmlParser,testModelList);
                            new Spectrum().parserXml(xmlParser,testModelList);
                        } else if ("CDMA_Group".equalsIgnoreCase(xmlParser.getName())) {
                            new CW().parserXml(xmlParser,testModelList);
                            new Pilot().parserXml(xmlParser,testModelList);
                            new Spectrum().parserXml(xmlParser,testModelList);
                            new Blind().parserXml(xmlParser,testModelList);
                        } else if ("LTE_Group".equalsIgnoreCase(xmlParser.getName())) {
                            new CW().parserXml(xmlParser,testModelList);
                            new PilotLTE().parserXml(xmlParser,testModelList);
                           new Spectrum().parserLteSpecturmXmlToModel(xmlParser,testModelList);
                            new Blind().parserXml(xmlParser,testModelList);
                        } else if ("NB_Group".equalsIgnoreCase(xmlParser.getName())) {
                            new Pilot().parserXml(xmlParser,testModelList);
                            new Blind().parserXml(xmlParser,testModelList);
                        }
                        break;

                    case XmlPullParser.END_TAG:

                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public ArrayList<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<Channel> channelList) {
        this.channelList = channelList;

    }


    /**
     * 设置任务列表勾选框
     */
    public void setEnable(ScanTaskModel taskModel, int position) {
        for (int i = 0; i < testModelList.size(); i++) {
            if (i == position) {
                testModelList.remove(position);
                testModelList.add(position, taskModel);
                break;
            }
        }
        setTaskModelToFile(testModelList);
    }


    /**
     * 将内存中的值，写入文件
     */

    public void setTaskModelToFile(ArrayList<ScanTaskModel> taskModels) {
        System.out.println(taskModels.toString());
        XmlFileCreator(taskModels);
    }

    /**
     * 任务获取接口，能获取整个配置任务列表
     *
     * @return
     */
    public ArrayList<ScanTaskModel> getTestModelList() {
        try {
            xmlParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testModelList;
    }

    public ArrayList<Channel> getRestoreChannelList() {
        return restoreChannelList;
    }


    /**
     * 最终获取列表
     *
     * @return
     */
    public ArrayList<ScanTaskModel> enableTasks() {
        ArrayList<ScanTaskModel> models = new ArrayList<ScanTaskModel>();
        models.clear();
        for (ScanTaskModel model : getTestModelList()) {
            if (model.getEnable() == 1) {
                models.add(model);
            }
        }
        return models;
    }


    /**
     * 判断任务配置是否有勾选项
     */
    public boolean hasEnableTask() {
        boolean hasTask = false;
        for (ScanTaskModel model : getTestModelList()) {
            if (model.getEnable() == 1) {
                hasTask = true;
                break;
            }
        }
        return hasTask;
    }

    public ArrayList<ScanTaskModel> addDefault() {
        return testModelList;
    }

    public ArrayList<ScannerTSMAInfoActivity.Content> getTableData(int testSchemaType){
        //模拟数据
        ArrayList<ScannerTSMAInfoActivity.Content> contents = new ArrayList<ScannerTSMAInfoActivity.Content>();
        contents.add(new ScannerTSMAInfoActivity.Content("ResultBufferDepth", "ReceiverIndex", "FrontEndSelectionMask", "ValuePerSec", "DecodeOutputMode", "MeasurementMode"));
        contents.add(new ScannerTSMAInfoActivity.Content( newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(), newRandomNumber(),  newRandomNumber()));
        return contents;
    }

    private String newRandomNumber() {
        return (new Random().nextInt(50) + 50) + "";
    }

}

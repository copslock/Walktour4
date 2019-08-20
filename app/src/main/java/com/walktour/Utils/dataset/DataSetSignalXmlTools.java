package com.walktour.Utils.dataset;

import android.util.Xml;

import com.walktour.Utils.FileUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有的信令及code对应关系解析
 */
public class DataSetSignalXmlTools {
    private final String TAG = "DataSetSignalXmlTools";
    /**
     * 单例
     */
    public static final DataSetSignalXmlTools instance = new DataSetSignalXmlTools();


    /**
     * 所有code,对应的信令集合
     */
    Map<Long, DataSetNode> map = new HashMap<>();

    /**
     * 私有构造器
     */
    private DataSetSignalXmlTools() {
        parXml();
    }

    public static synchronized DataSetSignalXmlTools getInstance() {
        return instance;
    }

    /**
     * 解析XML
     *
     * @param context
     */
    private void parXml() {
        map.clear();
        try {
            File file= AppFilePathUtil.getInstance().getAppConfigFile("D_Signal.xml");
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new ByteArrayInputStream(FileUtil.getBytesFromFile(file)), "UTF-8");
            int eventType = parser.getEventType();
            String tagName = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equals("node")) {
                            int count = parser.getAttributeCount();
                            if (count >= 3) {
                                DataSetNode node = new DataSetNode();
                                for (int i = 0; i < count; i++) {
                                    String name = parser.getAttributeName(i);
                                    String value = parser.getAttributeValue(i);

                                    if (name.equals("name")) {
                                        node.setName(value);
                                    } else if (name.equals("code")) {
                                        if(null!=value&&value.length()>0) {
                                            value = value.replace("0x", "");
                                            node.setCode(Long.valueOf(value, 16));
                                        }
                                    } else if (name.equals("ctg")) {
                                        node.setCtg(value);
                                    }
                                }
                                map.put(node.getCode(), node);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception ex) {
            LogUtil.w(TAG, ex.getMessage());
        }
    }




    /**
     * 获取所有的信令集合
     *
     * @return
     */
    public Map<Long, DataSetNode> getMap() {
        return map;
    }
}

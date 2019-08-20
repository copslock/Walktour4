package com.walktour.gui.task.activity.scannertsma.model;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * @author jinfeng.xie
 * @data 2019/5/31
 *
 * 扫频仪子节点的父类，列如Channel
 */
abstract class ScanSonTaskModel {
    /**
     * 解析子节点
     *
     * @param nodeName
     * @param nodeValue
     */
    public void NodeValue(XmlSerializer serializer, String nodeName, Object nodeValue) {

        try {
            serializer.startTag("", nodeName);
            serializer.text(String.valueOf(nodeValue));
            serializer.endTag("", nodeName);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    abstract void writeToXml(XmlSerializer serializer) throws IOException;
}

package com.walktour.control.config;

import android.annotation.SuppressLint;

import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyXMLWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedHashMap;

@SuppressLint("SdCardPath")
public class ConfigNetwork {

    private Document doc = null;
    private HashMap<String, Integer> networkList = new LinkedHashMap<>();

    private ConfigNetwork() {
        MyXMLWriter writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile("config_networktype.xml"));
        doc = writer.getDocument();
        buildNetworkInfo();
    }

    private static ConfigNetwork sInstance;

    public synchronized static ConfigNetwork getInstance() {
        if (sInstance == null) {
            sInstance = new ConfigNetwork();
        }
        return sInstance;
    }

    private void buildNetworkInfo() {
        NodeList node_list = doc.getElementsByTagName("network");
        for (int i = 0; i < node_list.getLength(); i++) {
            Node node = node_list.item(i);
            int networkid = Integer.parseInt(node.getAttributes().getNamedItem("networkid").getNodeValue());
            // String netName=
            // node.getAttributes().getNamedItem("networkname").getNodeValue();
            NodeList items = ((Element) node).getElementsByTagName("item");
            for (int j = 0; j < items.getLength(); j++) {
                Node item = items.item(j);
                String mnc = item.getAttributes().getNamedItem("value").getNodeValue();

                networkList.put(mnc, networkid);
            }
        }
    }

    /**
     * 根据传进来的MNC值返回配置好的指定运营商类型<BR>
     * 如果当前mnc类型不在配置列表中,根据当前手机指定的配置文件类型进行区分<BR>
     * 0:GSM/TDSCDMA/LTE<BR>
     * 1:CDMA/EVDO/LTE<BR>
     * 2:GSM/WCDMA/LTE<BR>
     *
     * @param mccmnc m
     * @return 网络类型
     */
    public int getNetworkType(String mccmnc) {
        LogUtil.w("ConfigNetwork", "--operatenum:" + mccmnc);
        if (networkList.containsKey(mccmnc)) {
            return networkList.get(mccmnc);
        }
        int netType = Deviceinfo.getInstance().getNettype();
        if (netType == NetType.TDSCDMA.getNetType() || netType == NetType.LTETDD.getNetType()) {
            return 0;
        } else if (netType == NetType.CDMA.getNetType() || netType == NetType.EVDO.getNetType()) {
            return 1;
        } else {
            return 2;
        }
    }
}

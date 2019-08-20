package com.walktour.control.config;

import android.annotation.SuppressLint;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLReader;
import com.walktour.control.config.SpeedTestParamter.ServerInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Jihong Xie SpeedTest_server.xml解析工具类
 */
@SuppressLint("SdCardPath")
public class SpeedTestSetting {
    // 从文件读取参数
    private MyXMLReader writer;

    private Map<String, SpeedTestParamter> paraList = new LinkedHashMap<>();// 所有参数的列表

    private static SpeedTestSetting setting;
    private static final String URL = "url";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String NAME = "name";
    private static final String COUNTRY = "country";
    private static final String COUNTRYCODE = "cc";
    private static final String SPONSOR = "sponsor";
    private static final String ID = "id";

    private SpeedTestSetting()
    {
        writer = new MyXMLReader(AppFilePathUtil.getInstance().getAppConfigDirectory(), "speedtest_server.xml");
        initialParameter();
    }

    public synchronized static SpeedTestSetting getInstance()
    {
        if (setting == null) {
            setting = new SpeedTestSetting();
        }
        return setting;
    }


    public void reloadData(){
        writer=null;
        paraList.clear();
        writer = new MyXMLReader(AppFilePathUtil.getInstance().getAppConfigDirectory(), "speedtest_server.xml");
        initialParameter();
    }
    /**
     * 初始化
     */
    private void initialParameter()
    {
        Document doc = writer.getDocument();
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("server");

        SpeedTestParamter paramter;
        Element node;
        String name;
        String country;
        String countryCode;
        SpeedTestParamter.ServerInfo serverInfo;
        for (int i = 0; i < nodeList.getLength(); i++) {

            node = (Element) nodeList.item(i);
            name=node.getAttribute(NAME);
            country=node.getAttribute(COUNTRY);
            countryCode = node.getAttribute(COUNTRYCODE);

            if(country.equals("China")||country.equals("CN")){
                countryCode="CN";
            }else if(name.equals("Hong Kong")||country.equals("Hong Kong")||countryCode.equals("HK")){
                countryCode="HK";
            }
            //国家已经包含在里面
            if (!paraList.containsKey(countryCode)) {
                paramter = new SpeedTestParamter();
                paraList.put(countryCode, paramter);
            } else {
                paramter = paraList.get(countryCode);
            }

            paramter.setCountrycode(countryCode);
            paramter.setCountry(node.getAttribute(COUNTRY));
            paramter.setId(node.getAttribute(ID));

            serverInfo = new SpeedTestParamter.ServerInfo();
            serverInfo.setName(node.getAttribute(NAME));
            serverInfo.setLat(node.getAttribute(LAT));
            serverInfo.setLon(node.getAttribute(LON));
            serverInfo.setSponsor(node.getAttribute(SPONSOR));
            serverInfo.setUrl(node.getAttribute(URL));
            serverInfo.setParent(paramter);

            paramter.getServerInfoList().add(serverInfo);
        }

        List<Map.Entry<String, SpeedTestParamter>> infoIds =
                new ArrayList<>(paraList.entrySet());
        // 排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, SpeedTestParamter>>() {
            public int compare(Map.Entry<String, SpeedTestParamter> o1,
                               Map.Entry<String, SpeedTestParamter> o2)
            {
                return (o1.getValue().getCountry().compareTo(o2.getValue().getCountry()));
            }
        });

        paraList.clear();

        for (Map.Entry<String, SpeedTestParamter> entry : infoIds) {
            Collections.sort(entry.getValue().getServerInfoList(), new Comparator<SpeedTestParamter.ServerInfo>() {
                @Override
                public int compare(SpeedTestParamter.ServerInfo o1, SpeedTestParamter.ServerInfo o2)
                {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            paraList.put(entry.getKey(), entry.getValue());
            //System.out.println(entry.getValue().getCountry());
        }
    }

    /**
     * 根据URL获取服务器所在国家和地区相关信息
     *
     * @param url
     * @return
     */
    public ServerInfo getByUrl(String url)
    {
        Iterator<Entry<String, SpeedTestParamter>> it = paraList.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, SpeedTestParamter> entry = it.next();
            List<ServerInfo> servers = entry.getValue().getServerInfoList();
            for (ServerInfo info : servers) {
                if (url.trim().equals(info.getUrl())) {
                    return info;
                }
            }
        }
        return null;
    }

    /**
     * 获取URL服务器地址
     *
     * @return
     */
    public Map<String, SpeedTestParamter> getParaList()
    {
        return paraList;
    }
}

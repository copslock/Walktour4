package com.dinglicom.totalreport;

import com.walktour.base.util.AppFilePathUtil;

/***
 * 报表常量信息
 *
 * @author weirong.fan
 *
 */
public final class ReportCommons {

    /***
     * 防止外部构造
     */
    private ReportCommons() {
        super();
    }

    /**
     * 自定义报表路径
     **/
    public static final String CUSTOMREPORT_PATH = AppFilePathUtil.getInstance().createSDCardBaseDirectory("TotalConfig", "customreport");
    /**
     * 自定义报表模板路径
     **/
    public static final String CUSTOMREPORT_TEMPLATE_PATH = AppFilePathUtil.getInstance().createSDCardBaseDirectory("TotalConfig", "customreportremplate");
    /**
     * 自定义报表的描述文件json名称
     **/
    static final String CUSTOMREPORT_JSONNAME = "customreport.json";
    /**
     * 自定义报表的请求XML名称
     **/
    static final String CUSTOMREPORT_XMLNAME = "customreport.xml";
    /**
     * 自定义报表的生成的excel模板文件名称
     **/
    static final String CUSTOMREPORT_XLSNAME = "customreport.xls";
}

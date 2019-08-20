package com.walktour.Utils.dataset;

/**
 * 数据集code与信令的对应节点
 */
public class DataSetNode {

    private String name="";

    private long code=0;

    private String ctg="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getCtg() {
        return ctg;
    }

    public void setCtg(String ctg) {
        this.ctg = ctg;
    }
}

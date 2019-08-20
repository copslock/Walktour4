package com.walktour.base.gui.customView;

/**
 * @author jinfeng.xie
 * @data 2019/3/15
 */
public class XMLTagBean {
  private    int level;
    private String tag;
    private String value;
    private int type;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "XMLTagBean{" +
                "level=" + level +
                ", tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}

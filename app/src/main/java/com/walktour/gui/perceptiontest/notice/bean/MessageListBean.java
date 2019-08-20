package com.walktour.gui.perceptiontest.notice.bean;

import java.util.List;

/**
 * @author Max
 * @data 2018/11/21
 */
public class MessageListBean {
    List<MessageBean> data;
    int total;
    public List<MessageBean> getBeans() {
        return data;
    }


    public void setBeans(List<MessageBean> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "MessageListBean{" +
                "beans=" + data +
                ", total=" + total +
                '}';
    }
}

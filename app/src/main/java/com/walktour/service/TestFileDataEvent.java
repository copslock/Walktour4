package com.walktour.service;

/**
 * Created by Yi.Lin on 2018/10/26.
 * 电信巡检版本发送测试文件信息给pad发送的EventBus时间
 */

public class TestFileDataEvent {

    private String data;

    public TestFileDataEvent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

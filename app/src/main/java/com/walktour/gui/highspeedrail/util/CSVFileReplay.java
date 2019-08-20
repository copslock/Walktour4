package com.walktour.gui.highspeedrail.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
/**
 * @date on 2018/9/4
 * @describe CSV模拟回放
 * @author jinfeng.xie
 * @version 1.0.0
 *
 */
public class CSVFileReplay {

    private  String filePath;   //文件路径
    private  int timeInterval;  //时间间隔
    private  boolean onReplay = false;
    private  boolean onWork = false;
    private ReplayThread replayThread = null;
    private  CSVReplayHandle handler = null;
    /*
    * 构建回放对象
    * @param f 文件路径
    * @param ti 读取间隔
    * */
    public  CSVFileReplay(String f,int ti)
    {
        filePath = f;
        timeInterval = ti;
    }

    //启动读取线程
    public void start()
    {
        if (!onReplay)
        {
            onWork = true;
            new ReplayThread().start();
        }
    }

    //停止读取
    public  void stop()
    {
        if (onReplay)
        {
            onWork = false;
        }
    }

    //设置回调
    public void setHandler(CSVReplayHandle h)
    {
        handler = h;
    }

    //获取回调对象
    public  CSVReplayHandle getHandler()
    {
        return handler;
    }

    //监听接口
    public  interface   CSVReplayHandle {
        void CSVReadRowData(String buffer[]);
    }

    //读取线程
    private class ReplayThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                onReplay = true;
                File csv = new File(filePath); // CSV文件路径
                BufferedReader br = new BufferedReader(new FileReader(csv));
                br.readLine();
                String line;
                /**
                 * 这里读取csv文件中的数据
                 */
                while ((line = br.readLine()) != null && onWork) { // 这里读取csv文件中的前10条数据
                    /**
                     *  csv格式每一列内容以逗号分隔,因此要取出想要的内容,以逗号为分割符分割字符串即可,
                     *  把分割结果存到到数组中,根据数组来取得相应值
                     */
                    String buffer[] = line.split(",");// 以逗号分隔
                    if (handler != null)
                    {
                        handler.CSVReadRowData(buffer);
                    }
                    //在工作中就休眠，否则退出
                    if ( onWork) {
                        sleep(timeInterval);
                    }
                    else
                    {
                        break;
                    }
                }
                br.close();
                onReplay = false;
            } catch (Exception e) {
                onReplay = false;
                e.printStackTrace();
            }
        }

    }
}

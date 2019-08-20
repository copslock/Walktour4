package com.walktour.Utils;

import android.content.Context;
import android.content.Intent;

import com.walktour.base.util.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * RCU事件字节组合
 */
public class EventBytes {
    private List<Byte> byteArray = new ArrayList<Byte>();
    private Context mContext;

    private static EventBytes eventBytes;

    /***
     * RCU事件的组合字节
     * @param context
     * @param eventFlag 事件flag
     */
    private EventBytes(Context context, int eventFlag)
    {
        this.mContext = context;
        this.addInteger(eventFlag);
    }

    private EventBytes(Context context)
    {
        this.mContext = context;
    }

    /**
     * RCU事件的组合字节,以自定义的字符数据组开头
     *
     * @param head 自定义的字符数据
     */
    private EventBytes(Context context, char[] head)
    {
        this.mContext = context;
        byte[] headBytes = UtilsMethod.charArrayToByte(head);
        for (byte b : headBytes) {
            byteArray.add(b);
        }
    }

    /**
     * 将已构建好的事件码流写入RCU文件中
     *
     * @param context
     * @param bytes   已构建好数据码流
     */
    private EventBytes(Context context, byte[] bytes)
    {
        this.mContext = context;
        for (byte b : bytes) {
            this.byteArray.add(b);
        }
    }

    /**
     * 增加事件的原因码
     *
     * @param arg 事件的原因码
     */
    public EventBytes addInteger(int arg)
    {
        byte[] bytes = UtilsMethod.intToByteArray1(arg);
        byteArray.add(bytes[0]);
        byteArray.add(bytes[1]);
        byteArray.add(bytes[2]);
        byteArray.add(bytes[3]);
        return this;
    }

    /**
     * 增加事件的原因码
     *
     * @param arg 事件的原因码
     */
    public EventBytes addDouble(double args)
    {
        byte[] bytes = UtilsMethod.doubleToByteArray(args);
        byteArray.add(bytes[0]);
        byteArray.add(bytes[1]);
        byteArray.add(bytes[2]);
        byteArray.add(bytes[3]);
        byteArray.add(bytes[4]);
        byteArray.add(bytes[5]);
        byteArray.add(bytes[6]);
        byteArray.add(bytes[7]);
        return this;
    }

    /**
     * 增加事件的原因码,添加int64，对应Java中的Long类型
     *
     * @param args 事件的原因码
     */
    public EventBytes addInt64(long args)
    {
        byte[] bytes = UtilsMethod.longToByteArray(args);
        byteArray.add(bytes[0]);
        byteArray.add(bytes[1]);
        byteArray.add(bytes[2]);
        byteArray.add(bytes[3]);
        byteArray.add(bytes[4]);
        byteArray.add(bytes[5]);
        byteArray.add(bytes[6]);
        byteArray.add(bytes[7]);
        return this;
    }

    /**
     * 增加事件的原因码
     *
     * @param arg 事件的原因码
     */
    public EventBytes addShort(short arg)
    {
        byte[] bytes = UtilsMethod.shortToBytes(arg);
        byteArray.add(bytes[0]);
        byteArray.add(bytes[1]);
        return this;
    }

    /**
     * 增加事件的原因码
     *
     * @param arg 事件的原因码
     */
    public EventBytes addByte(byte arg)
    {
        byteArray.add(arg);
        return this;
    }

    /**
     * 增加事件的原因码
     *
     * @param arg 事件的原因码
     */
    public EventBytes addBytes(byte[] arg)
    {
        for (byte b : arg) {
            byteArray.add(b);
        }
        return this;
    }

    /**
     * 增加事件
     *
     * @param arg
     * @return
     */
    public EventBytes addStringBuffer(String arg)
    {
        // android.os.Debug.waitForDebugger();
        if (arg == null || arg.length() < 1) {
            arg = " ";
        }

        byte[] bytes = UtilsMethod.intToByteArray1(arg.getBytes().length);
        for (byte b : bytes) {
            byteArray.add(b);
        }

        bytes = arg.getBytes();
        for (byte b : bytes) {
            byteArray.add(b);
        }

        return this;
    }

    /**
     * 增加RCU事件
     *
     * @param arg
     * @return
     */
    public EventBytes addSingle(float arg)
    {
        byte[] bytes = UtilsMethod.intToByteArray1(Float.floatToIntBits(arg));
        for (byte b : bytes) {
            byteArray.add(b);
        }
        return this;
    }

    /**
     * 2013.10.22 qihang.li 增加ascii编码的char数组
     *
     * @param charArray 要增加的字符串，编码为默认的UTF-8
     */
    public EventBytes addCharArray(String charArray)
    {
        byte[] strByteArray = new byte[0];
        try {
            strByteArray = charArray.getBytes(UtilsMethod.CharSet_UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return addBytes(strByteArray);
    }

    /**
     * 将字串中的内容以其整弄值的方式存入RCU文件中
     *
     * @param charArray
     * @return
     */
    public EventBytes addCharArrayByInt(String charArray)
    {
        char[] aa = charArray.toCharArray();
        String[] cc = new String[aa.length];
        for (int i = 0; i < aa.length; i++) {
            byte bb = 0;
            try {
                cc[i] = String.valueOf(aa[i]);
                bb = (byte) Integer.parseInt(cc[i]);
            } catch (Exception e) {
                LogUtil.w("EventBytes", "addCharArrayByInt", e);
                bb = (byte) aa[i];
            }
            byteArray.add(bb);
        }

        return this;
    }

    /**
     * 增加char[]
     *
     * @param arg       要增加的字符串
     * @param maxLength char[] arg要在rcu文件中占用的长度（字节数）
     *                  ,这里说的char是C中的char(1byte大小)，不同于java的char（2byte大小）
     */
    public EventBytes addCharArray(char[] arg, int maxLength)
    {
        byte[] bytesToRCU = new byte[maxLength];
        byte[] bytesFromArg = UtilsMethod.charArrayToByte(arg);
        for (int i = 0; i < bytesFromArg.length; i++) {
            // 只从bytesFromArg中复制charLength大小的字节
            if (i < maxLength) {
                bytesToRCU[i] = bytesFromArg[i];
            } else {
                break;
            }
        }
        this.addBytes(bytesToRCU);
        return this;
    }

    /**
     * 增加char ,这里的char是C中的char(1byte大小)，不同于java的char（2byte大小）
     *
     * @param c 要增加的char
     */
    public EventBytes addChar(char c)
    {
        this.addByte(UtilsMethod.charToByte(c));
        return this;
    }

    public byte[] getByteArray()
    {
        byte[] result = new byte[byteArray.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = byteArray.get(i).byteValue();
        }
        return result;
    }


    /**
     * 根据IMEI号生成TGUID号，RCU文档中对TGUID号的定义如下： struct TGUID { unsigned int D1;
     * unsigned short D2; unsigned short D3; unsigned char D4[8]; };
     *
     * @param guid 长度大于25的yyyyMMddHHmmssSSS+IMEI号
     * @return
     */
    public EventBytes addTguid(String guid)
    {

        int yyyyMMdd = Integer.parseInt(guid.substring(0, 8));
        short hhmm = Short.parseShort(guid.substring(8, 12));
        short ssSS = Short.parseShort(guid.substring(12, 16));
        String d4 = guid.substring(guid.length() - 12, guid.length());

        return addInteger(yyyyMMdd).addShort(hhmm).addShort(ssSS).addCharArray(d4);
    }

    /**
     * 以RCU标准事件的形式写入文件
     *
     * @param time 事件时间(微秒)
     */
    public void writeToRcu(long time)
    {

        if (String.valueOf(time).trim().length() != 16) { // 微秒处理
            time = time * 1000;
        }
        this.writeToRcu(WalkCommonPara.MsgDataFlag_E, time, 2, 0);

    }

    /**
     * 把采样点信息写入rcu文件
     *
     * @param time
     */
    public void writePointToRcu(long time)
    {
        if (String.valueOf(time).trim().length() != 16) { // 微秒处理
            time = time * 1000;
        }
        Intent intent = new Intent(WalkMessage.pushPointData);
        intent.putExtra("port", 2);// port
        intent.putExtra("time", time);// port
        intent.putExtra("buffer", getByteArray());// port
        intent.putExtra("size", getByteArray().length);// port
        mContext.sendBroadcast(intent);
        intent = null;
        byteArray.clear();
        eventBytes = null;
    }

    /**
     * 以自定义的Msg_FLAG形式写入RCU文件
     *
     * @param time 事件时间(微秒)
     */
    public void writeToRcu(int flag)
    {
        this.writeToRcu(flag, System.currentTimeMillis() * 1000, 2, 0);
    }

    /**
     * 以自定义的Msg_FLAG形式把gps点写入RCU文件
     *
     * @param flag 事件标识
     */
    public void writeGPSToRcu(int flag)
    {
        this.writeToRcu(flag, System.currentTimeMillis() * 1000, 0, 1);
    }

    /**
     * 以自定义的Msg_FLAG形式写入RCU文件
     *
     * @param flag 消息flag
     * @param time 事件时间(微秒)
     * @param port 端口号
     * @param type 0为室内打点,1为 GPS打点
     */
    private void writeToRcu(int flag, long time, int port, int type)
    {
        Intent intent = new Intent(WalkMessage.pushData);
        intent.putExtra("port", port);// port
        intent.putExtra("flag", flag);// port
        intent.putExtra("time", time);// port
        intent.putExtra("type", type);
        intent.putExtra("buffer", getByteArray());// port
        intent.putExtra("size", getByteArray().length);// port
        mContext.sendBroadcast(intent);
        intent = null;
        byteArray.clear();
        eventBytes = null;
    }

    /**
     * 为适应旧接口添加的函数
     *
     * @param intergers
     * @param str
     */
    public void addValues(int[] intergers, String str)
    {
        for (int i : intergers) {
            if (i != RcuEventCommand.NullityRcuValue) {
                this.addInteger(i);
            }
        }
        if (str.trim().length() > 0) {
            this.addCharArray(str.toCharArray(), str.length());
        }
    }

    /**
     * RCU事件的字节组合Builder
     *
     * @param context
     * @param eventFlag RCU标准事件的Flag
     * @return
     */
    public static EventBytes Builder(Context context, int eventFlag)
    {
        eventBytes = new EventBytes(context, eventFlag);
        return eventBytes;
    }

    /**
     * 没有flagEvent ID的事件
     */
    public static EventBytes Builder(Context context)
    {
        eventBytes = new EventBytes(context);
        return eventBytes;
    }

    public static EventBytes Builder(Context context, char[] head)
    {
        eventBytes = new EventBytes(context, head);
        return eventBytes;
    }

    /**
     * 生成已构建好码流写入事件对象
     *
     * @param context
     * @param bytes
     * @return
     */
    public static EventBytes Builder(Context context, byte[] bytes)
    {
        eventBytes = new EventBytes(context, bytes);
        return eventBytes;
    }

    /**
     * 获得数据大小
     *
     * @return
     */
    public int getBytesSize()
    {
        return this.byteArray.size();
    }
}

package com.walktour.service.automark.glonavin;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.walktour.base.util.LogUtil;
import com.walktour.service.automark.glonavin.bean.GlonavinPoint;

/**
 * Created by Yi.Lin on 2018/4/8.
 * 操作格纳微定位模块工具类
 */

public class GlonavinUtils {

    private static final String TAG = "GlonavinUtils";

    /**
     * 定位模块定位数据byte数组长度
     */
    private static final int POS_BYTES_LENGTH = 36;
    /**
     * 定位模块定位数据posX起始、结束游标(从0开始)
     */
    private static final int START_INDEX_OF_POSX = 6, END_INDEX_OF_POSX = 9;

    /**
     * 定位模块定位数据posY起始、结束游标(从0开始)
     */
    private static final int START_INDEX_OF_POSY = 10, END_INDEX_OF_POSY = 13;

    /**
     * 定位模块定位数据posZ起始、结束游标(从0开始)
     */
    private static final int START_INDEX_OF_POSZ = 14, END_INDEX_OF_POSZ = 17;


    /**
     * 定位模块俯仰角数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_PITCH_ANGLE = 18, END_INDEX_OF_PITCH_ANGLE = 21;
    /**
     * 定位模块横滚角数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_ROLL_ANGLE = 22, END_INDEX_OF_ROLL_ANGLE = 25;
    /**
     * 定位模块航向角数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_COURSE_ANGLE = 26, END_INDEX_OF_COURSE_ANGLE = 29;
    /**
     * 模块电量 数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_POWER = 30;
    /**
     * 模块信号强 度数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_SIGNAL_POWER = 31;
    /**
     * 当前楼层数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_CURRENT_FLOOR = 32;
    /**
     * 上下楼状数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_UP_DOWM_FLOOR = 33;
    /**
     * 功能位数据其实、结束游标（从0开始）
     */
    private static final int START_INDEX_OF_CRC = 34;


    private GlonavinUtils() {
        //no-instance
    }

    /**
     * 长距离校准模式
     * 0表示关闭， 1表示开启，其中长距离校正中2表示开启模块自身磁场校准。
     */
    public enum LongDistanceReviseMode {
        CLOSE((byte) 0),
        OPEN((byte) 1),
        OPEN_WITH_SELF_MAGNETIC_FIELD((byte) 2);
        private byte value;

        LongDistanceReviseMode(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    /**
     * 获取唤醒定位模块设备byte命令
     *
     * @param openFloorRecognition    是否开启楼层识别
     * @param openElevatorRecognition 是否开启电梯识别
     * @param longDistanceReviseMode  长距离校正模式
     * @param currentFloor            当前楼层
     * @param firstFloorHeight        建筑首层高
     * @param floorHeight             建筑层高
     * @return
     */
    public static byte[] getWakeModuleCommand(boolean openFloorRecognition, boolean openElevatorRecognition, LongDistanceReviseMode longDistanceReviseMode,
                                              byte currentFloor, byte firstFloorHeight, byte floorHeight) {
        byte[] wakeCommand = new byte[20];
        wakeCommand[0] = 0x24;
        wakeCommand[1] = (byte) 0xA1;
        wakeCommand[2] = (byte) (openFloorRecognition ? 1 : 0);//开启楼层识别(1B, 0||1，默认为0)
        wakeCommand[3] = (byte) (openElevatorRecognition ? 1 : 0);//开启电梯识别(1B, 0||1，默认为0)
        wakeCommand[4] = longDistanceReviseMode.getValue();//开启长距离校正(1B, 0||1||2， 默认为0)
        wakeCommand[5] = currentFloor;//当前楼层 (1B,默认为 1)
        wakeCommand[6] = firstFloorHeight;//建筑首层 高(1B,默 认为 0)
        wakeCommand[7] = floorHeight;//建筑层高 (1B,默认 为 0)
        //后12位保留位
        wakeCommand[8] = 0;
        wakeCommand[9] = 0;
        wakeCommand[10] = 0;
        wakeCommand[11] = 0;
        wakeCommand[12] = 0;
        wakeCommand[13] = 0;
        wakeCommand[14] = 0;
        wakeCommand[15] = 0;
        wakeCommand[16] = 0;
        wakeCommand[17] = 0;
        wakeCommand[18] = 0;
        wakeCommand[19] = 0;
        return wakeCommand;
    }


    /**
     * 获取使定位模块休眠命令
     *
     * @return
     */
    public static byte[] getSleepModuleCommand() {
        byte[] sleepCommand = new byte[2];
        sleepCommand[0] = 0x24;
        sleepCommand[1] = (byte) 0xA2;
        return sleepCommand;
    }

    /**
     * 获取更新定位模块参数命令
     *
     * @param currentFloor            楼层号
     * @param openFloorRecognition    开启楼层 识别(1B, 0||1，默认 为 0)
     * @param longDistanceReviseMode  开启长距 离校正 (1B, 0||1||2， 默认为 0)
     * @param openElevatorRecognition 开启电梯 识别(1B, 0||1，默认 为 0)
     * @return
     */
    public static byte[] getUpdateModuleParamCommand(byte currentFloor, boolean openFloorRecognition, LongDistanceReviseMode longDistanceReviseMode, boolean openElevatorRecognition) {
        byte[] updateCommand = new byte[8];
        updateCommand[0] = 0x24;
        updateCommand[1] = (byte) 0xA8;
        updateCommand[2] = currentFloor;
        updateCommand[3] = (byte) (openFloorRecognition ? 1 : 0);
        updateCommand[4] = longDistanceReviseMode.getValue();
        updateCommand[5] = (byte) (openElevatorRecognition ? 1 : 0);
        //后2B为保留区
        updateCommand[6] = 0;
        updateCommand[7] = 0;
        return updateCommand;
    }

    /**
     * 获取软件版本号命令
     *
     * @return
     */
    public static byte[] getSoftwareVersionCommand() {
        byte[] command = new byte[2];
        command[0] = 0x24;
        command[1] = (byte) 0xB1;
        return command;
    }

    /**
     * CRC8检查pos数据是否有效
     *
     * @param bytes
     * @return
     */
    public static boolean checkPosBytesValid(byte[] bytes) {
        if (bytes != null && bytes.length == POS_BYTES_LENGTH) {
            byte[] calBytes = new byte[POS_BYTES_LENGTH - 1];
            System.arraycopy(bytes, 0, calBytes, 0, POS_BYTES_LENGTH - 1);
            byte calResult = checkSumCrc8(calBytes);
            byte lastByte = bytes[POS_BYTES_LENGTH - 1];
            LogUtil.i(TAG, "--------calResult:" + calResult + ", lastByte:" + lastByte + "--------");
            return (calResult == lastByte);
        }
        return false;
    }


    /**
     * 根据传入定位模块返回的定位数据解析出posX值
     *
     * @param totalBytes 36B的byte[]
     * @return poiX(float)
     */
    public static float getPosX(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte[] bytesX = new byte[4];
            System.arraycopy(totalBytes, START_INDEX_OF_POSX, bytesX, 0, 4);
            return byteArrayToFloat(bytesX, 0);
        }
        return 0;
    }

    /**
     * 根据传入定位模块返回的定位数据解析出posY值
     *
     * @param totalBytes 36B的byte[]
     * @return poiY(float)
     */
    public static float getPosY(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte[] bytesY = new byte[4];
            System.arraycopy(totalBytes, START_INDEX_OF_POSY, bytesY, 0, 4);
            return byteArrayToFloat(bytesY, 0);
        }
        return 0;
    }

    /**
     * 根据传入定位模块返回的定位数据解析出posZ值
     *
     * @param totalBytes 36B的byte[]
     * @return poiZ(float)
     */
    public static float getPosZ(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte[] bytesZ = new byte[4];
            System.arraycopy(totalBytes, START_INDEX_OF_POSZ, bytesZ, 0, 4);
            LogUtil.d(TAG,""+bytesZ[0]+"，"+bytesZ[1]+","+bytesZ[2]+","+bytesZ[3]);
            return byteArrayToFloat(bytesZ, 0);
        }
        return 0;
    }


    /**
     * 根据传入定位模块返回的定位数据解析俯仰角
     *
     * @param totalBytes 36B的byte[]
     * @return 俯仰角
     */
    public static float getAngleOfPitch(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte[] bytesX = new byte[4];
            System.arraycopy(totalBytes, START_INDEX_OF_PITCH_ANGLE, bytesX, 0, 4);
            return byteArrayToFloat(bytesX, 0);
        }
        return 0;
    }

    /**
     * 根据传入定位模块返回的定位数据解析横滚角
     *
     * @param totalBytes 36B的byte[]
     * @return 横滚角
     */
    public static float getAngleOfRoll(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte[] bytesX = new byte[4];
            System.arraycopy(totalBytes, START_INDEX_OF_ROLL_ANGLE, bytesX, 0, 4);
            return byteArrayToFloat(bytesX, 0);
        }
        return 0;
    }

    /**
     * 根据传入定位模块返回的定位数据解析航向角
     *
     * @param totalBytes 36B的byte[]
     * @return 航向角
     */
    public static float getAngleOfCourse(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte[] bytesX = new byte[4];
            System.arraycopy(totalBytes, START_INDEX_OF_COURSE_ANGLE, bytesX, 0, 4);
            return byteArrayToFloat(bytesX, 0);
        }
        return 0;
    }


//    /**
//     * byte数组转float
//     *
//     * @param array byte数组
//     * @param pos   起始位置
//     * @return
//     */
//    private static float byteArrayToFloat(byte[] array, int pos) {
//        int accum = 0;
//        accum = array[pos + 0] & 0xFF;
//        accum |= (long) (array[pos + 1] & 0xFF) << 8;
//        accum |= (long) (array[pos + 2] & 0xFF) << 16;
//        accum |= (long) (array[pos + 3] & 0xFF) << 24;
//        return Float.intBitsToFloat(accum);
//    }
    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byteArrayToFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
    /**
     * byte数组转int
     *
     * @param array byte数组
     * @param pos   起始位置
     * @return
     */
    private static int byteArrayToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * CRC8 校验接口
     *
     * @param bytes
     * @return
     */
    private static byte checkSumCrc8(byte[] bytes) {
        int Crc;
        byte[] ch = new byte[8];
        byte ch1;
        int i, j, k;
        Crc = 0xff;
        for (i = 0; i < bytes.length; i++) {
            ch1 = bytes[i];
            for (j = 0; j < 8; j++) {
                ch[j] = (byte) (ch1 & 0x01);
                ch1 >>= 1;
            }
            for (k = 0; k < 8; k++) {
                ch[7 - k] <<= 7;
                if (((Crc ^ ch[7 - k]) & 0x80) > 0) {
                    Crc = (Crc << 1) ^ 0x1d;
                } else {
                    Crc <<= 1;
                }
            }
        }
        Crc ^= 0xff;
        return (byte) Crc;
    }


    /**
     * 给起始、结束点画线和箭头
     *
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     */
    public static void drawArrow(Canvas canvas, Paint paint, int sx, int sy, int ex, int ey) {
        double H = 50; // 箭头高度
        double L = 20; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        canvas.drawLine(sx, sy, ex, ey, paint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, paint);

    }


    /**
     * 计算三角形旋转角度
     *
     * @param px
     * @param py
     * @param ang
     * @param isChLen
     * @param newLen
     * @return
     */
    private static double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }


    /**
     * 已知弧度、位移获取下个点坐标
     *
     * @param prePoint
     * @param radian
     * @param dis
     * @return
     */
    public static GlonavinPoint nextPointWithDistance(GlonavinPoint prePoint, float radian, float dis) {
        float newR = radian;
        if (newR > 2 * Math.PI) {
            newR -= 2 * Math.PI;
        }
        GlonavinPoint newPoint = new GlonavinPoint();
        double dx = 0, dy = 0, rd;
        if (newR < Math.PI / 2) {
            rd = newR;
            dy = dis * Math.sin(rd);
            dx = dis * Math.cos(rd);
            newPoint.setX((float) (prePoint.getX() - dx));
            newPoint.setY((float) (prePoint.getY() - dy));
        } else if (newR < Math.PI) {
            rd = Math.PI - newR;
            dy = dis * Math.sin(rd);
            dx = dis * Math.cos(rd);
            newPoint.setX((float) (prePoint.getX() + dx));
            newPoint.setY((float) (prePoint.getY() - dy));
        } else if (newR < Math.PI / 2 * 3) {
            rd = newR - Math.PI;
            dy = dis * Math.sin(rd);
            dx = dis * Math.cos(rd);
            newPoint.setX((float) (prePoint.getX() + dx));
            newPoint.setY((float) (prePoint.getY() + dy));
        } else {
            rd = 2 * Math.PI - newR;
            dy = dis * Math.sin(rd);
            dx = dis * Math.cos(rd);
            newPoint.setX((float) (prePoint.getX() - dx));
            newPoint.setY((float) (prePoint.getY() + dy));
        }
        return newPoint;
    }
    /**
     * 根据传入定位模块返回的定位数据解析电量
     *
     * @param totalBytes 36B的byte[]
     * @return  电量
     */
    public static int getPower(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte b = totalBytes[START_INDEX_OF_POWER];
            return byteArrayToInt(b);
        }
        return 0;
    }

    /**
     * 根据传入定位模块返回的定位数据解析信号强度
     *
     * @param totalBytes 36B的byte[]
     * @return  信号强度
     */
    public static int getSignalPower(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte b = totalBytes[START_INDEX_OF_SIGNAL_POWER];
            return byteArrayToInt(b);
        }
        return 0;
    }
    /**
     * 根据传入定位模块返回的定位数据解析当前楼层
     *
     * @param totalBytes 36B的byte[]
     * @return  当前楼层
     */
    public static int getCurrentFloor(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte b = totalBytes[START_INDEX_OF_CURRENT_FLOOR];
            return byteArrayToInt(b);
        }
        return 0;
    }
    /**
     * 根据传入定位模块返回的定位数据解析上下楼状态
     *
     * @param totalBytes 36B的byte[]
     * @return  上下楼状态
     */
    public static int getUpDomnFloor(byte[] totalBytes) {
        if (totalBytes != null && totalBytes.length == POS_BYTES_LENGTH) {
            byte b = totalBytes[START_INDEX_OF_UP_DOWM_FLOOR];
            return byteArrayToInt(b);
        }
        return 0;
    }


    public static GlonavinPoint getGlonavinPoint(byte[] totalBytes) {
        GlonavinPoint currentPoint = new GlonavinPoint();
        if (checkPosBytesValid(totalBytes)) { //CRC8检查pos数据是否有效
            currentPoint.setX(getPosX(totalBytes));
            currentPoint.setY(getPosY(totalBytes));
            currentPoint.setZ(getPosZ(totalBytes));
            currentPoint.setAngle(getAngleOfCourse(totalBytes));
            currentPoint.setAngleOfPitch(getAngleOfPitch(totalBytes));
            currentPoint.setAngleOfRoll(getAngleOfRoll(totalBytes));
            currentPoint.setPower(getPower(totalBytes));
            currentPoint.setSignalPower(getSignalPower(totalBytes));
            currentPoint.setCurrentFloor(getCurrentFloor(totalBytes));
            currentPoint.setUpDownFloor(getUpDomnFloor(totalBytes));
        }
        return currentPoint;
    }

}

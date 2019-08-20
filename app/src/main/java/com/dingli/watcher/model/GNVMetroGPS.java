package com.dingli.watcher.model;

public class GNVMetroGPS {

    public int SequenceID;            //流水号
    public int StationID;             //站点ID
    public int PosType;               //位置信息类(0:静止[0表示站点位置信息],1:减速,2:匀速,3:加速,4:启动[非0表示两站之间的位置信息])
    //
    public long Second;        //系统UTC时间
    public long USecond;
    public double   Lat;              //-------------------------纬度
    public double   Lon;              //-------------------------经度
    public int ElectricRate;          //电量百分比
    public float    Speed;            //-------------------------速度(km/h)
    public int ValidFlag;      //0为无效，1就是有效
    public MetroGPS chanceToMetroGPSModel(){
        MetroGPS metroGPS=new MetroGPS();
        metroGPS.Lon=Lon;
        metroGPS.Lat=Lat;
        metroGPS.Speed=Speed;
        metroGPS.Atype=PosType;
        return metroGPS;
    }
    public int getSequenceID() {
        return SequenceID;
    }

    public void setSequenceID(int sequenceID) {
        SequenceID = sequenceID;
    }

    public int getStationID() {
        return StationID;
    }

    public void setStationID(int stationID) {
        StationID = stationID;
    }

    public int getPosType() {
        return PosType;
    }

    public void setPosType(int posType) {
        PosType = posType;
    }

    public long getSecond() {
        return Second;
    }

    public void setSecond(long second) {
        Second = second;
    }

    public long getUSecond() {
        return USecond;
    }

    public void setUSecond(long USecond) {
        this.USecond = USecond;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLon() {
        return Lon;
    }

    public void setLon(double lon) {
        Lon = lon;
    }

    public int getElectricRate() {
        return ElectricRate;
    }

    public void setElectricRate(int electricRate) {
        ElectricRate = electricRate;
    }

    public float getSpeed() {
        return Speed;
    }

    public void setSpeed(float speed) {
        Speed = speed;
    }

    public int getValidFlag() {
        return ValidFlag;
    }

    public void setValidFlag(int validFlag) {
        ValidFlag = validFlag;
    }

    @Override
    public String toString() {
        return "GNVMetroGPS{" +
                "SequenceID=" + SequenceID +
                ", StationID=" + StationID +
                ", PosType=" + PosType +
                ", Second=" + Second +
                ", USecond=" + USecond +
                ", Lat=" + Lat +
                ", Lon=" + Lon +
                ", ElectricRate=" + ElectricRate +
                ", Speed=" + Speed +
                ", ValidFlag=" + ValidFlag +
                '}';
    }
}

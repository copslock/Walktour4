package com.walktour.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yi.Lin on 2018/7/17.
 */

public class LteGsmStructModel {

    @SerializedName("arfcn")
    private int arfcn;
    @SerializedName("gsmBand")
    private byte gsmBand;
    @SerializedName("lnaState")
    private short lnaState ;
    @SerializedName("rssi")
    private float rssi;
    @SerializedName("srxLev")
    private float srxLev;

    public LteGsmStructModel(int arfcn, byte gsmBand, short lnaState, float rssi, float srxLev) {
        this.arfcn = arfcn;
        this.gsmBand = gsmBand;
        this.lnaState = lnaState;
        this.rssi = rssi;
        this.srxLev = srxLev;
    }

    @Override
    public String toString() {
        return "LteGsmStructModel{" +
                "arfcn=" + arfcn +
                ", gsmBand=" + gsmBand +
                ", lnaState=" + lnaState +
                ", rssi=" + rssi +
                ", srxLev=" + srxLev +
                '}';
    }

    public int getArfcn() {
        return arfcn;
    }

    public void setArfcn(int arfcn) {
        this.arfcn = arfcn;
    }

    public byte getGsmBand() {
        return gsmBand;
    }

    public void setGsmBand(byte gsmBand) {
        this.gsmBand = gsmBand;
    }

    public short getLnaState() {
        return lnaState;
    }

    public void setLnaState(short lnaState) {
        this.lnaState = lnaState;
    }

    public float getRssi() {
        return rssi;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }

    public float getSrxLev() {
        return srxLev;
    }

    public void setSrxLev(float srxLev) {
        this.srxLev = srxLev;
    }
}

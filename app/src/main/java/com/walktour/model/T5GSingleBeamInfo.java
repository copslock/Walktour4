package com.walktour.model;

public class T5GSingleBeamInfo {
    private int SSBIndex;
    private boolean ServingBeam;
    private int RxBeamID;
    private int CellTiming;
    private float RSRP;
    private float RSRQ;
    private float SINR;

    public T5GSingleBeamInfo(int SSBIndex, boolean servingBeam, int rxBeamID, int cellTiming, float RSRP, float RSRQ, float SINR)
    {
        this.SSBIndex = SSBIndex;
        ServingBeam = servingBeam;
        RxBeamID = rxBeamID;
        CellTiming = cellTiming;
        this.RSRP = RSRP;
        this.RSRQ = RSRQ;
        this.SINR = SINR;
    }

    public int getSSBIndex()
    {
        return SSBIndex;
    }

    public boolean isServingBeam()
    {
        return ServingBeam;
    }

    public int getRxBeamID()
    {
        return RxBeamID;
    }

    public int getCellTiming()
    {
        return CellTiming;
    }

    public float getRSRP()
    {
        return RSRP;
    }

    public float getRSRQ()
    {
        return RSRQ;
    }

    public float getSINR()
    {
        return SINR;
    }
}

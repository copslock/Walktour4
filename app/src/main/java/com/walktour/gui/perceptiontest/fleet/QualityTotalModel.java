package com.walktour.gui.perceptiontest.fleet;

public class QualityTotalModel {

    private double ERAB_SetupSuccessRatio;
    private double LTE_Service_DropRatio;
    private double RRC_SetupSuccessRatio;
    private double Radio_AccessRatio;

    public double getERAB_SetupSuccessRatio()
    {
        return ERAB_SetupSuccessRatio;
    }

    public void setERAB_SetupSuccessRatio(double ERAB_SetupSuccessRatio)
    {
        this.ERAB_SetupSuccessRatio = ERAB_SetupSuccessRatio;
    }

    public double getLTE_Service_DropRatio()
    {
        return LTE_Service_DropRatio;
    }

    public void setLTE_Service_DropRatio(double LTE_Service_DropRatio)
    {
        this.LTE_Service_DropRatio = LTE_Service_DropRatio;
    }

    public double getRRC_SetupSuccessRatio()
    {
        return RRC_SetupSuccessRatio;
    }

    public void setRRC_SetupSuccessRatio(double RRC_SetupSuccessRatio)
    {
        this.RRC_SetupSuccessRatio = RRC_SetupSuccessRatio;
    }

    public double getRadio_AccessRatio()
    {
        return Radio_AccessRatio;
    }

    public void setRadio_AccessRatio(double Radio_AccessRatio)
    {
        this.Radio_AccessRatio = Radio_AccessRatio;
    }
}

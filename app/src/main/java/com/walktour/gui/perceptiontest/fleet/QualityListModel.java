package com.walktour.gui.perceptiontest.fleet;

import java.io.Serializable;
import java.util.List;

public class QualityListModel  implements Serializable {


    private int total;
    private List<DataBean> data;

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
    }

    public List<DataBean> getData()
    {
        return data;
    }

    public void setData(List<DataBean> data)
    {
        this.data = data;
    }

    public static class DataBean implements Serializable {

        private double ERAB_SetupSuccessRatio;
        private double LTE_Service_DropRatio;
        private double RRC_SetupSuccessRatio;
        private double Radio_AccessRatio;
        private String Equipment_Net_Manager_Name;
        private String Item_Number;

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

        public void setLTE_Service_DropRatio(int LTE_Service_DropRatio)
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

        public String getEquipment_Net_Manager_Name()
        {
            return Equipment_Net_Manager_Name;
        }

        public void setEquipment_Net_Manager_Name(String Equipment_Net_Manager_Name)
        {
            this.Equipment_Net_Manager_Name = Equipment_Net_Manager_Name;
        }

        public String getItem_Number()
        {
            return Item_Number;
        }

        public void setItem_Number(String Item_Number)
        {
            this.Item_Number = Item_Number;
        }
    }
}

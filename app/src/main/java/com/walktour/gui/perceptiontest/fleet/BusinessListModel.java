package com.walktour.gui.perceptiontest.fleet;

import java.io.Serializable;
import java.util.List;

public class BusinessListModel implements Serializable {


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
        /**
         * Air_Download_Rate : 2102.2468
         * Air_Upload_Rate : 145.8706
         * Equipment_Net_Manager_Name : 大成国际中心
         * Item_Number : 101.940828.12
         */

        private double Air_Download_Rate;
        private double Air_Upload_Rate;
        private String Equipment_Net_Manager_Name;
        private String Item_Number;

        public double getAir_Download_Rate()
        {
            return Air_Download_Rate;
        }

        public void setAir_Download_Rate(double Air_Download_Rate)
        {
            this.Air_Download_Rate = Air_Download_Rate;
        }

        public double getAir_Upload_Rate()
        {
            return Air_Upload_Rate;
        }

        public void setAir_Upload_Rate(double Air_Upload_Rate)
        {
            this.Air_Upload_Rate = Air_Upload_Rate;
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

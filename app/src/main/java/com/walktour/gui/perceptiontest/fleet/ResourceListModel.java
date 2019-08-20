package com.walktour.gui.perceptiontest.fleet;

import java.io.Serializable;
import java.util.List;

public class ResourceListModel implements Serializable {

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

        private Object Altitude;
        private int Azimuth;
        private int CellId;
        private String CellName;
        private Object ElecTilt;
        private Object GroundHeight;
        private int Id;
        private double Latitude;
        private double Longitude;
        private Object MechTilt;
        private Object Provider;
        private int _RowID;

        public Object getAltitude()
        {
            return Altitude;
        }

        public void setAltitude(Object Altitude)
        {
            this.Altitude = Altitude;
        }

        public int getAzimuth()
        {
            return Azimuth;
        }

        public void setAzimuth(int Azimuth)
        {
            this.Azimuth = Azimuth;
        }

        public int getCellId()
        {
            return CellId;
        }

        public void setCellId(int CellId)
        {
            this.CellId = CellId;
        }

        public String getCellName()
        {
            return CellName;
        }

        public void setCellName(String CellName)
        {
            this.CellName = CellName;
        }

        public Object getElecTilt()
        {
            return ElecTilt;
        }

        public void setElecTilt(Object ElecTilt)
        {
            this.ElecTilt = ElecTilt;
        }

        public Object getGroundHeight()
        {
            return GroundHeight;
        }

        public void setGroundHeight(Object GroundHeight)
        {
            this.GroundHeight = GroundHeight;
        }

        public int getId()
        {
            return Id;
        }

        public void setId(int Id)
        {
            this.Id = Id;
        }

        public double getLatitude()
        {
            return Latitude;
        }

        public void setLatitude(double Latitude)
        {
            this.Latitude = Latitude;
        }

        public double getLongitude()
        {
            return Longitude;
        }

        public void setLongitude(double Longitude)
        {
            this.Longitude = Longitude;
        }

        public Object getMechTilt()
        {
            return MechTilt;
        }

        public void setMechTilt(Object MechTilt)
        {
            this.MechTilt = MechTilt;
        }

        public Object getProvider()
        {
            return Provider;
        }

        public void setProvider(Object Provider)
        {
            this.Provider = Provider;
        }

        public int get_RowID()
        {
            return _RowID;
        }

        public void set_RowID(int _RowID)
        {
            this._RowID = _RowID;
        }
    }
}

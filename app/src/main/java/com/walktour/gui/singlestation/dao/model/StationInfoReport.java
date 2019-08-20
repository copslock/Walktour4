package com.walktour.gui.singlestation.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * 基站信息报告
 */
@Entity(nameInDb = "station_info_report")
public class StationInfoReport implements Parcelable {
    /**
     * 站型：远程服务器报告
     */
    public static final int TYPE_REMOTE = 0;
    /**
     * 站型：本地报告
     */
    public static final int TYPE_LOCAL = 1;

    /**
     * 唯一主键，自增
     */
    @Id(autoincrement = true)
    private Long id;
    /**
     * 基站信息表ID
     */
    @Property(nameInDb = "station_id")
    private Long stationId;
    /**
     * 基站报告的类型,默认是远程报告
     */
    @Property(nameInDb = "report_type")
    private int type=TYPE_REMOTE;
    /**
     * 生成报告的路径
     */
    @Property(nameInDb = "report_path")
    private String reportPath="";

    
    protected StationInfoReport(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.stationId = (Long) in.readValue(Long.class.getClassLoader());
        this.type = in.readInt();
        this.reportPath=in.readString();
    }
    @Generated(hash = 842456952)
    public StationInfoReport(Long id, Long stationId, int type, String reportPath) {
        this.id = id;
        this.stationId = stationId;
        this.type = type;
        this.reportPath = reportPath;
    }
    @Generated(hash = 1802807565)
    public StationInfoReport() {
    }


    public static final Creator<StationInfoReport> CREATOR = new Creator<StationInfoReport>() {
        @Override
        public StationInfoReport createFromParcel(Parcel in) {
            return new StationInfoReport(in);
        }

        @Override
        public StationInfoReport[] newArray(int size) {
            return new StationInfoReport[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(stationId);
        dest.writeInt(type);
        dest.writeString(reportPath);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getStationId() {
        return this.stationId;
    }
    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getReportPath() {
        return this.reportPath;
    }
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }
}

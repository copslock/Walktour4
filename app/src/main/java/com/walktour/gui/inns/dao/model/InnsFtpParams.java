package com.walktour.gui.inns.dao.model;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by yi.lin on 2017/12/1.
 *
 * 上传到寅时服务器的FTP参数
 */
@Entity(nameInDb = "inns_ftp_params")
public class InnsFtpParams {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "ftp_down_ave")
    private double ftpDownAve;//	FTP平均下载速率

    @Property(nameInDb = "ftp_up_ave")
    private double ftpUpAve;//FTP平均上传速率

    @Property(nameInDb = "ftp_down_max")
    @SerializedName("ftpDownMax")
    private double ftpDownMax;//最高下载速率

    @Property(nameInDb = "ftp_down_min")
    private double ftpDownMin;//最低下载速率

    /**
     * 对应的txt文件全名（上传时只有文TXT文件的File对象，所以此处用它来做查找的uuid）
     */
    @Property(nameInDb = "file_name_detail")
    private String fileNameDetail;

    /**
     * 上传参数需要
     */
    @Property(nameInDb = "log_file_uuid")
    private String logFileUUID;

    @Override
    public String toString() {
        return "InnsFtpParams{" +
                "id=" + id +
                ", ftpDownAve=" + ftpDownAve +
                ", ftpUpAve=" + ftpUpAve +
                ", ftpDownMax=" + ftpDownMax +
                ", ftpDownMin=" + ftpDownMin +
                ", fileNameDetail='" + fileNameDetail + '\'' +
                ", logFileUUID='" + logFileUUID + '\'' +
                '}';
    }

    @Generated(hash = 852081269)
    public InnsFtpParams(Long id, double ftpDownAve, double ftpUpAve,
            double ftpDownMax, double ftpDownMin, String fileNameDetail,
            String logFileUUID) {
        this.id = id;
        this.ftpDownAve = ftpDownAve;
        this.ftpUpAve = ftpUpAve;
        this.ftpDownMax = ftpDownMax;
        this.ftpDownMin = ftpDownMin;
        this.fileNameDetail = fileNameDetail;
        this.logFileUUID = logFileUUID;
    }

    @Generated(hash = 600534606)
    public InnsFtpParams() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getFtpDownAve() {
        return this.ftpDownAve;
    }

    public void setFtpDownAve(double ftpDownAve) {
        this.ftpDownAve = ftpDownAve;
    }

    public double getFtpUpAve() {
        return this.ftpUpAve;
    }

    public void setFtpUpAve(double ftpUpAve) {
        this.ftpUpAve = ftpUpAve;
    }

    public double getFtpDownMax() {
        return this.ftpDownMax;
    }

    public void setFtpDownMax(double ftpDownMax) {
        this.ftpDownMax = ftpDownMax;
    }

    public double getFtpDownMin() {
        return this.ftpDownMin;
    }

    public void setFtpDownMin(double ftpDownMin) {
        this.ftpDownMin = ftpDownMin;
    }

    public String getFileNameDetail() {
        return this.fileNameDetail;
    }

    public void setFileNameDetail(String fileNameDetail) {
        this.fileNameDetail = fileNameDetail;
    }

    public String getLogFileUUID() {
        return this.logFileUUID;
    }

    public void setLogFileUUID(String logFileUUID) {
        this.logFileUUID = logFileUUID;
    }



}

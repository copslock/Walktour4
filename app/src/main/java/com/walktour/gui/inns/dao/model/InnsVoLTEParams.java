package com.walktour.gui.inns.dao.model;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by yi.lin on 2017/12/1.
 * <p>
 * 上传到寅时服务器的VoLTE参数
 */

@Entity(nameInDb = "inns_volte_params")
public class InnsVoLTEParams {

    @Id(autoincrement = true)
    private Long id;

    @SerializedName("connRate")
    @Property(nameInDb = "conn_rate")
    private double connRate;//接通率

    @Property(nameInDb = "drop_rate")
    private double dropRate;//掉话率

    @Property(nameInDb = "call_delay")
    private int callDelay;//呼叫建立时延

    @Property(nameInDb = "mos_3_Rate")
    private double mos3Rate;//MOS 3.0以上占比

    @Property(nameInDb = "mos_35_Rate")
    private double mos35Rate;//MOS 3.5以上占比

    @Property(nameInDb = "ims_success_rate")
    private double imsSuccessRate;//IMS注册成功率（%）

    @Property(nameInDb = "esrvcc_success_rate")
    private double esrvccSuccessRate;//eSRVCC成功率（%）

    @Property(nameInDb = "esrvcc_delay")
    private int esrvccDelay;//eSRVCC切换时延-用户面（ms）

    @Property(nameInDb = "rtp_lost_rate")
    private double rtpLostRate;//RTP丢包率

    @Property(nameInDb = "rtp_shake_rate")
    private double rtpShakeRate;//RTP抖动(ms)
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

    @Generated(hash = 1886684028)
    public InnsVoLTEParams(Long id, double connRate, double dropRate, int callDelay,
            double mos3Rate, double mos35Rate, double imsSuccessRate,
            double esrvccSuccessRate, int esrvccDelay, double rtpLostRate,
            double rtpShakeRate, String fileNameDetail, String logFileUUID) {
        this.id = id;
        this.connRate = connRate;
        this.dropRate = dropRate;
        this.callDelay = callDelay;
        this.mos3Rate = mos3Rate;
        this.mos35Rate = mos35Rate;
        this.imsSuccessRate = imsSuccessRate;
        this.esrvccSuccessRate = esrvccSuccessRate;
        this.esrvccDelay = esrvccDelay;
        this.rtpLostRate = rtpLostRate;
        this.rtpShakeRate = rtpShakeRate;
        this.fileNameDetail = fileNameDetail;
        this.logFileUUID = logFileUUID;
    }

    @Generated(hash = 473197946)
    public InnsVoLTEParams() {
    }

    @Override
    public String toString() {
        return "InnsVoLTEParams{" +
                "id=" + id +
                ", connRate=" + connRate +
                ", dropRate=" + dropRate +
                ", callDelay=" + callDelay +
                ", mos3Rate=" + mos3Rate +
                ", mos35Rate=" + mos35Rate +
                ", imsSuccessRate=" + imsSuccessRate +
                ", esrvccSuccessRate=" + esrvccSuccessRate +
                ", esrvccDelay=" + esrvccDelay +
                ", rtpLostRate=" + rtpLostRate +
                ", rtpShakeRate=" + rtpShakeRate +
                ", fileNameDetail='" + fileNameDetail + '\'' +
                ", logFileUUID='" + logFileUUID + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getConnRate() {
        return this.connRate;
    }

    public void setConnRate(double connRate) {
        this.connRate = connRate;
    }

    public double getDropRate() {
        return this.dropRate;
    }

    public void setDropRate(double dropRate) {
        this.dropRate = dropRate;
    }

    public int getCallDelay() {
        return this.callDelay;
    }

    public void setCallDelay(int callDelay) {
        this.callDelay = callDelay;
    }

    public double getMos3Rate() {
        return this.mos3Rate;
    }

    public void setMos3Rate(double mos3Rate) {
        this.mos3Rate = mos3Rate;
    }

    public double getMos35Rate() {
        return this.mos35Rate;
    }

    public void setMos35Rate(double mos35Rate) {
        this.mos35Rate = mos35Rate;
    }

    public double getImsSuccessRate() {
        return this.imsSuccessRate;
    }

    public void setImsSuccessRate(double imsSuccessRate) {
        this.imsSuccessRate = imsSuccessRate;
    }

    public double getEsrvccSuccessRate() {
        return this.esrvccSuccessRate;
    }

    public void setEsrvccSuccessRate(double esrvccSuccessRate) {
        this.esrvccSuccessRate = esrvccSuccessRate;
    }

    public int getEsrvccDelay() {
        return this.esrvccDelay;
    }

    public void setEsrvccDelay(int esrvccDelay) {
        this.esrvccDelay = esrvccDelay;
    }

    public double getRtpLostRate() {
        return this.rtpLostRate;
    }

    public void setRtpLostRate(double rtpLostRate) {
        this.rtpLostRate = rtpLostRate;
    }

    public double getRtpShakeRate() {
        return this.rtpShakeRate;
    }

    public void setRtpShakeRate(double rtpShakeRate) {
        this.rtpShakeRate = rtpShakeRate;
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

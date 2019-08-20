package com.walktour.gui.perceptiontest.notice.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author Max
 * @data 2018/11/18
 */
@Entity
public class MessageBean {
    @Id(autoincrement = false)
    long Id;
    String Title;
    String CreateDT;
    String Creator;
    String Content;
    boolean isRead=false;
    @Generated(hash = 942169818)
    public MessageBean(long Id, String Title, String CreateDT, String Creator,
            String Content, boolean isRead) {
        this.Id = Id;
        this.Title = Title;
        this.CreateDT = CreateDT;
        this.Creator = Creator;
        this.Content = Content;
        this.isRead = isRead;
    }
    @Generated(hash = 1588632019)
    public MessageBean() {
    }
    public long getId() {
        return this.Id;
    }
    public void setId(long Id) {
        this.Id = Id;
    }
    public String getTitle() {
        return this.Title;
    }
    public void setTitle(String Title) {
        this.Title = Title;
    }
    public String getCreateDT() {
        return this.CreateDT;
    }
    public void setCreateDT(String CreateDT) {
        this.CreateDT = CreateDT;
    }
    public String getCreator() {
        return this.Creator;
    }
    public void setCreator(String Creator) {
        this.Creator = Creator;
    }
    public String getContent() {
        return this.Content;
    }
    public void setContent(String Content) {
        this.Content = Content;
    }
    public boolean getIsRead() {
        return this.isRead;
    }
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "Id=" + Id +
                ", Title='" + Title + '\'' +
                ", CreateDT='" + CreateDT + '\'' +
                ", Creator='" + Creator + '\'' +
                ", Content='" + Content + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}

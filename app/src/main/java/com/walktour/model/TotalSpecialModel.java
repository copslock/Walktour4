/*
 * 文件名: TotalSpecialModel.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tangwq
 * 创建时间:2012-11-30
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.model;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * [存储特别统计项模型]<BR>
 * [对于HTTP，PING等业务，对于各参数的统计需要区分哪个网络，哪个网址，当前网络状态的特殊信令开关状态]
 * mainKey1,HTTP业务存储的是HTTP LOGON/REF/DOWN，PING业务存的是WCDMA，GSM..,事件的事件名
 * mainKey2，HTTP业务存储的是URL地址，PING业务存储存的是TBF-CLOSE/OPEN...，事件的序号
 * @author tangwq
 * @version [WalkTour Client V100R001C03, 2012-11-30] 
 */
public class TotalSpecialModel implements Parcelable{
    private String mainKey1 = "";
    private String mainKey2 = "";
    private String keyName = "";
    private long keyValue = 0;
    
    public TotalSpecialModel(String mainKey1,String mainKey2){
        this.mainKey1 = mainKey1;
        this.mainKey2 = mainKey2;
    }
    
    /**
     * 
     * @param mainKey1 HTTP业务存储的是HTTP LOGON/REF/DOWN，PING业务存的是WCDMA，GSM..,事件的事件名
     * @param mainKey2 HTTP业务存储的是URL地址，PING业务存储存的是TBF-CLOSE/OPEN...，事件的序号
     * @param keyName 属性名,如_HttpDelay, 自定义事件的event.name
     * @param keyValue 属性值
     */
    public TotalSpecialModel(String mainKey1,String mainKey2,String keyName,long keyValue){
        this.mainKey1 = mainKey1;
        this.mainKey2 = mainKey2;
        this.keyName = keyName;
        this.keyValue = keyValue;
    }
    
    /**
     * @return the mainKey1
     */
    public String getMainKey1() {
        return mainKey1;
    }
    /**
     * @param mainKey1 the mainKey1 to set
     */
    public void setMainKey1(String mainKey1) {
        this.mainKey1 = mainKey1;
    }
    /**
     * @return the mainKey2
     */
    public String getMainKey2() {
        return mainKey2;
    }
    /**
     * @param mainKey2 the mainKey2 to set
     */
    public void setMainKey2(String mainKey2) {
        this.mainKey2 = mainKey2;
    }
    /**
     * @return the keyName
     */
    public String getKeyName() {
        return keyName;
    }
    /**
     * @param keyName the keyName to set
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
    /**
     * @return the keyValue
     */
    public long getKeyValue() {
        return keyValue;
    }
    /**
     * @param keyValue the keyValue to set
     */
    public void setKeyValue(long keyValue) {
        this.keyValue = keyValue;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     * @see android.os.Parcelable#describeContents()
     */
    
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param dest
     * @param flags
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(mainKey1);
        dest.writeString(mainKey2);
        dest.writeString(keyName);
        dest.writeLong(keyValue);
    }
    
    /**
     * 在想要进行序列号传递的实体类内部一定要声明该常量。常量名只能是CREATOR,类型也必须是 
     * Parcelable.Creator<T> 
     */
    public static final Parcelable.Creator<TotalSpecialModel> CREATOR = new Creator<TotalSpecialModel>() {

        /**
         * [根据序列号的Parcel对象，反序列号为原本的实体对象]<BR>
         * [读出顺序要和writeToParcel的写入顺序相同]
         * @param source
         * @return
         * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
         */
        @Override
        public TotalSpecialModel createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            TotalSpecialModel totalModel = new TotalSpecialModel(source.readString(), source.readString(), 
                    source.readString(), source.readLong());
            return totalModel;
        }

        @Override
        public TotalSpecialModel[] newArray(int size) {
            // TODO Auto-generated method stub
            return null;
        }
    };
}

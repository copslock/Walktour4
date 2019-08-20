package com.walktour.gui.weifuwu.view.refreshlistview;
import android.os.Parcel;
import android.os.Parcelable;
/***
 * 动态刷新列表model
 * 
 * @author weirong.fan
 *
 */
public class ListViewModel implements Parcelable {
	/** 表示是终端 **/
	public static int INFO_DEVIE = 0;
	/** 表示是组 **/
	public static int INFO_GROUP = 1;
	/** 表示的是android终端 **/
	public static int OS_ANDROID = 0;
	/** 表示的是iphone终端 **/
	public static int OS_IPHONE = 1;
	/** 表示的是组终端 **/
	public static int OS_GROUP = 2;
	/** 区分是终端还是组 **/
	public int type = INFO_DEVIE;
	/** 系统类型 **/
	public int osType = OS_ANDROID;
	/** 终端或组的code信息 **/
	public String code;
	/** 终端或组的描述信息 **/
	public String describe;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + osType;
		result = prime * result + type;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListViewModel other = (ListViewModel) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (osType != other.osType)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(type);
		out.writeInt(osType);
		out.writeString(code);
		out.writeString(describe);
	}
	// 用来创建自定义的Parcelable的对象
	public static final Parcelable.Creator<ListViewModel> CREATOR = new Parcelable.Creator<ListViewModel>() {
		public ListViewModel createFromParcel(Parcel in) {
			return new ListViewModel(in);
		}
		public ListViewModel[] newArray(int size) {
			return new ListViewModel[size];
		}
	};
	// 读数据进行恢复
	private ListViewModel(Parcel in) {
		type = in.readInt();
		osType = in.readInt();
		code = in.readString();
		describe = in.readString();
	}
	public ListViewModel() {
		super();
	}
}

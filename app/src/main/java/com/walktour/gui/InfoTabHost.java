package com.walktour.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.TabHost;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.gui.newmap2.NewInfoTabActivity;

/**
 * 查看信息的tab页面
 * 
 * @author jianchao.wang
 *
 */
public class InfoTabHost extends TabHost {

	/** 信息类型：空，适用于其他调用当前类的模块 */
	public static final int INFO_TYPE_NULL = 0;
	/** 信息类型：地图 */
	public static final int INFO_TYPE_MAP = 1;
	/** 信息类型：除地图外 */
	public static final int INFO_TYPE_OTHER = 2;
	/** 信息类型 */
	private int infoType = INFO_TYPE_NULL;
	/** 界面 */
	private Activity activity;
	/** 是否初始化 */
	private boolean isInit = false;

	public InfoTabHost(Context context) {
		super(context);
	}

	public InfoTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setCurrentTabByTag(String tag) {
		if (!isInit && this.infoType != INFO_TYPE_NULL) {
			if (tag.equals("map") && this.infoType != INFO_TYPE_MAP) {
				this.activity.finish();
				TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
				TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
				Intent intent = new Intent(this.getContext(), NewInfoTabActivity.class);
				intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, INFO_TYPE_MAP);
				intent.putExtra("isReplay", false);
				this.activity.startActivity(intent);
				this.activity.overridePendingTransition(0, 0);
				return;
			} else if (!tag.equals("map") && this.infoType == INFO_TYPE_MAP) {
				this.activity.finish();
				if (tag.equals("info")) {
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Info;
				} else if (tag.equals("param")) {
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
				} else if (tag.equals("alarmmsg")) {
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.AlarmMsg;
				} else if (tag.equals("scanner")) {
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Scanner;
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
				}
				Intent intent = new Intent(this.getContext(), NewInfoTabActivity.class);
				intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, INFO_TYPE_OTHER);
				intent.putExtra("isReplay", false);
				this.activity.startActivity(intent);
				this.activity.overridePendingTransition(0, 0);
				return;
			}
		}
		super.setCurrentTabByTag(tag);
	}

	@Override
	public void setCurrentTab(int index) {
		if (!isInit && this.infoType != INFO_TYPE_NULL) {
			if (index == 0 && this.infoType != INFO_TYPE_MAP) {
				this.activity.finish();
				TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Map;
				TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
				Intent intent = new Intent(this.getContext(), NewInfoTabActivity.class);
				intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, INFO_TYPE_MAP);
				intent.putExtra("isReplay", false);
				this.activity.startActivity(intent);
				this.activity.overridePendingTransition(0, 0);
				return;
			} else if (index > 0 && this.infoType == INFO_TYPE_MAP) {
				switch (index) {
				case 1:
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Info;
					break;
				case 2:
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Param;
					break;
				case 3:
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.AlarmMsg;
					break;
				case 4:
					TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Scanner;
					TraceInfoInterface.currentShowChildTab = WalkStruct.ShowInfoType.Default;
					break;
				}
				this.activity.finish();
				Intent intent = new Intent(this.getContext(), NewInfoTabActivity.class);
				intent.putExtra(NewInfoTabActivity.INFO_TYPE_NAME, INFO_TYPE_OTHER);
				intent.putExtra("isReplay", false);
				this.activity.startActivity(intent);
				this.activity.overridePendingTransition(0, 0);
				return;
			}
		}
		super.setCurrentTab(index);
	}

	public int getInfoType() {
		return infoType;
	}

	public void setInfoType(int infoType) {
		this.infoType = infoType;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

}

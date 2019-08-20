package com.walktour.gui.map;

import android.os.Bundle;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
/**
 * 参数Activity类
 * Author:Zhengmin
 * Create Time:2010/4/16
 */
public class Para extends BasicActivity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paraview);
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TraceInfoInterface.currentShowTab = WalkStruct.ShowInfoType.Data;
	}
}

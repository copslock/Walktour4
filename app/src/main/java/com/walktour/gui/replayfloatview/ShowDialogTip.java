package com.walktour.gui.replayfloatview;

import android.os.Bundle;
import android.view.Window;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

public class ShowDialogTip extends BasicActivity implements ConverterDdib.ConverterDdibI{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_msg);
		ConverterDdib.getInstance(this).registerObserver(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ConverterDdib.getInstance(this).removeObserver(this);
	}

	@Override
	public void doFinish(int isSuccess) {
		System.out.println("=============showTip dofinish");
		this.finish();
	}

	@Override
	public void desDdibPath(String path) {
		// TODO Auto-generated method stub
	}

}

package com.walktour.gui.map;

import android.os.Bundle;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

public class Edge extends BasicActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_edgeview);
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}

package com.walktour.gui.data;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.gui.R;

public class MTFragment extends DTFragment{

	public MTFragment() {
		FLAG = SceneType.MultiTest.name();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.file_list, null);
		filterView = rootView.findViewById(R.id.radiogroup_top);
		initButtons(filterView);
		initAllPopWindows();
		lv = (ListView)rootView.findViewById(R.id.ListView01);
		mAdapter = new TreeViewAdapter(mActivity, root);
		lv.setAdapter(mAdapter);
		lv.setOnItemClickListener(this);
		refreshData();
		return rootView;
	}
}

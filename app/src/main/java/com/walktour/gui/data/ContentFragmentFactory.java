package com.walktour.gui.data;

import android.support.v4.app.Fragment;

import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.Utils.WalkStruct.TestType;

public class ContentFragmentFactory {

	public static Fragment newIntance(int key) {
		Fragment content = null;
		if (key == TestType.DT.getTestTypeId()) {
			content = new DTFragment();
		} else if (key == TestType.CQT.getTestTypeId()) {
			content = new CQTFragment();
		} else if (key == SceneType.Auto.getSceneTypeId()) {
			content = new AutoFragment();
		} else if (key == SceneType.MultiTest.getSceneTypeId()) {
			content = new MTFragment();
		} else if (key == SceneType.BTU.getSceneTypeId()) {
			content = new BTUFragment();
		} else if (key == SceneType.Anhui.getSceneTypeId()) {
			content = new AHFragment();
		} else if (key == SceneType.Huawei.getSceneTypeId()) {
			content = new HWFragment();
		} else if (key == SceneType.Fujian.getSceneTypeId()) {
			content = new FJFragment();
		}else if (key == SceneType.MultiATU.getSceneTypeId()) {
			content = new MultiATUFragment();
		} else if (key == SceneType.ATU.getSceneTypeId()) {
			content = new SingleATUFragment();
		}else if (key == SceneType.Metro.getSceneTypeId()) {
			content = new MetroFragment();
		}else if (key == SceneType.HighSpeedRail.getSceneTypeId()) {
			content = new HighSpeedRailFragment();
		}else if (key == SceneType.SingleSite.getSceneTypeId()) {
			content = new SingleStationFragment();
		}
		
		return content;
	}
}

package com.walktour.gui.newmap.offline.baidu;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 百度离线地图服务类
 * 
 * @author jianchao.wang
 * 
 */
public class BaiduMapOfflineService extends Service implements MKOfflineMapListener {
	// private final static String TAG = "BaiduMapOfflineService";
	/** 所有的城市列表 */
	private List<MKOLSearchRecord> allCities = new ArrayList<MKOLSearchRecord>();
	/** 离线地图管理类 */
	private MKOfflineMap mOffline = null;
	/** 点击的省份 */
	private MKOLSearchRecord clickProvince;
	/** 监听类 */
	private BaiduMapOfflineListener listener;

	/**
	 * 初始化离线模块
	 */
	public void init() {
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		this.allCities = this.mOffline.getOfflineCityList();
	}

	public class OfflineBinder extends Binder {

		/**
		 * 返回当前实例
		 * 
		 * @return
		 */
		public BaiduMapOfflineService getService() {
			return BaiduMapOfflineService.this;
		}
	}

	/**
	 * 获得可以下载的地图列表
	 * 
	 * @return
	 */
	public List<MKOLSearchRecord> getCityList() {
		List<MKOLSearchRecord> cityList = new ArrayList<MKOLSearchRecord>(this.allCities);
		// 展开点击的省份
		if (this.clickProvince != null) {
			for (int i = 0; i < cityList.size(); i++) {
				MKOLSearchRecord city = cityList.get(i);
				if (city.cityID == this.clickProvince.cityID) {
					cityList.addAll(i + 1, city.childCities);
					break;
				}
			}
		}
		List<MKOLUpdateElement> list = this.mOffline.getAllUpdateInfo();
		Set<Integer> idSet = new HashSet<Integer>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				idSet.add(list.get(i).cityID);
			}
		}
		// 过滤掉正在下载或已下载的城市
		if (!idSet.isEmpty()) {
			for (int i = 0; i < cityList.size(); i++) {
				MKOLSearchRecord city = cityList.get(i);
				boolean remove = false;
				if (idSet.contains(city.cityID)) {
					remove = true;
				} else if (city.cityType == 1) {
					int count = 0;
					for (MKOLSearchRecord child : city.childCities) {
						if (idSet.contains(child.cityID))
							count++;
					}
					if (count == city.childCities.size())
						remove = true;
				}
				if (remove) {
					cityList.remove(i);
					i--;
				}
			}
		}
		return cityList;
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null && this.listener != null) {
				this.listener.refresh();
			}
			break;
		}

	}

	/**
	 * 重新下载指定的城市
	 * 
	 * @param cityIdSet
	 *            城市ID集合
	 */
	public void redownload(Set<Integer> cityIdSet) {
		for (int id : cityIdSet) {
			this.mOffline.start(id);
		}
	}

	/**
	 * 删除指定的城市对象
	 * 
	 * @param cityIdSet
	 */
	public void deleteCities(Set<Integer> cityIdSet) {
		for (int id : cityIdSet) {
			this.mOffline.remove(id);
		}
	}

	/**
	 * 添加下载的城市
	 * 
	 * @param provinceIdSet
	 *            省份ID集合
	 * @param cityIdSet
	 *            城市ID集合
	 */
	public void addDownload(Set<Integer> provinceIdSet, Set<Integer> cityIdSet) {
		List<MKOLSearchRecord> list = new ArrayList<MKOLSearchRecord>();
		for (MKOLSearchRecord record : this.allCities) {
			if (record.cityType == 1) {
				if (provinceIdSet.contains(record.cityID))
					list.addAll(record.childCities);
				else {
					for (MKOLSearchRecord child : record.childCities) {
						if (cityIdSet.contains(child.cityID)) {
							list.add(child);
						}
					}
				}
			} else if (cityIdSet.contains(record.cityID)) {
				list.add(record);
			}
		}
		if (list.isEmpty())
			return;
		for (MKOLSearchRecord city : list) {
			this.mOffline.start(city.cityID);
		}
	}

	/**
	 * 设置点击的省份
	 * 
	 * @param record
	 *            省份
	 * @return
	 */
	public boolean setClickProvince(MKOLSearchRecord record) {
		boolean refresh = false;
		if (record.cityType == 1) {
			if (this.clickProvince == null || this.clickProvince.cityID != record.cityID) {
				this.clickProvince = record;
				refresh = true;
			} else {
				this.clickProvince = null;
				refresh = true;
			}
		} else if (record.cityType != 2 && this.clickProvince != null) {
			refresh = true;
			this.clickProvince = null;
		}
		return refresh;
	}

	/**
	 * 停止下载指定城市
	 * 
	 * @param cityIdSet
	 *            城市Id集合
	 */
	public void stopDownload(Set<Integer> cityIdSet) {
		for (int id : cityIdSet) {
			this.mOffline.pause(id);
		}
	}

	/**
	 * 获取已下载的城市
	 * 
	 * @return
	 */
	public List<MKOLUpdateElement> getLocalList() {
		List<MKOLUpdateElement> list = this.mOffline.getAllUpdateInfo();
		if (list == null)
			list = new ArrayList<MKOLUpdateElement>();
		List<MKOLUpdateElement> cityList = new ArrayList<MKOLUpdateElement>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).status == MKOLUpdateElement.FINISHED) {
				cityList.add(list.get(i));
			}
		}
		return cityList;
	}

	/**
	 * 更新已下载的城市地图
	 * 
	 * @param localIdSet
	 *            城市ID
	 */
	public void updateLocal(Set<Integer> localIdSet) {

	}

	/**
	 * 获取下载中的城市
	 * 
	 * @return
	 */
	public List<MKOLUpdateElement> getDownloadList() {
		List<MKOLUpdateElement> list = this.mOffline.getAllUpdateInfo();
		if (list == null)
			list = new ArrayList<MKOLUpdateElement>();
		List<MKOLUpdateElement> cityList = new ArrayList<MKOLUpdateElement>();
		for (int i = 0; i < list.size(); i++) {
			MKOLUpdateElement city = list.get(i);
			if (city.ratio < 100 && city.status != MKOLUpdateElement.FINISHED) {
				cityList.add(city);
			}
		}
		return cityList;
	}

	@Override
	public OfflineBinder onBind(Intent intent) {
		return new OfflineBinder();
	}

	public void setListener(BaiduMapOfflineListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDestroy() {
		this.clickProvince = null;
		this.mOffline.destroy();
	}

}

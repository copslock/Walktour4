package com.walktour.gui.newmap.offline.baidu;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.walktour.Utils.UtilsMethod;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.ScroollTabActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.ControlBar;
import com.walktour.gui.newmap.BaseMapActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 百度离线地图下载管理
 * 
 * @author jianchao.wang
 * 
 */
public class BaiduMapOfflineActivity extends ScroollTabActivity
		implements OnTabChangeListener, OnCheckedChangeListener, OnItemClickListener {

	private final static String TAG = "BaiduMapOfflineActivity";
	/** 地图下载分页 */
	private final static String TAB_ID_CITY = "cityView";
	/** 下载中分页 */
	private final static String TAB_ID_DOWNLOAD = "downloadView";
	/** 已下载分页 */
	private final static String TAB_ID_LOCAL = "localView";
	/** 可下载的城市列表 */
	private List<MKOLSearchRecord> cityList = new ArrayList<MKOLSearchRecord>();
	/** 下载中的城市列表 */
	private List<MKOLUpdateElement> downloadList = new ArrayList<MKOLUpdateElement>();
	/** 已下载的城市列表 */
	private List<MKOLUpdateElement> localList = new ArrayList<MKOLUpdateElement>();
	/** 当前选中的tag */
	private String tabId = "";
	/** 下载按钮 */
	private Button downloadButton;
	/** 停止按钮 */
	private Button stopButton;
	/** 删除按钮 */
	private Button deleteButton;
	/** 确定按钮 */
	private Button okButton;
	/** 勾选的下载城市ID集合 */
	private Set<Integer> cityIdSet = new HashSet<Integer>();
	/** 勾选的下载省份ID集合 */
	private Set<Integer> provinceIdSet = new HashSet<Integer>();
	/** 勾选下载中城市ID集合 */
	private Set<Integer> downloadIdSet = new HashSet<Integer>();
	/** 勾选本地城市ID集合 */
	private Set<Integer> localIdSet = new HashSet<Integer>();
	/** 当前对象 */
	private BaiduMapOfflineActivity activity;
	/** 城市列表适配 */
	private CityAdapter cityAdapter;
	/** 下载列表适配 */
	private DownloadAdapter downloadAdapter;
	/** 本地列表适配 */
	private LocalAdapter localAdapter;
	/** 是否监控勾选框的操作 */
	private boolean isNotifyChecked = false;
	/** 关联的服务类 */
	private BaiduMapOfflineService service;

	@Override
	public void initView() {
		SDKInitializer.initialize(this.getApplicationContext());
		setContentView(R.layout.map_baidu_offline_activity);
		this.activity = this;
		Intent intent = new Intent(this, BaiduMapOfflineService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		this.findView();
	}

	/**
	 * 关联控件
	 */
	@SuppressWarnings("deprecation")
	private void findView() {
		((TextView) findViewById(R.id.title_txt)).setText(R.string.baidumap_offline_title);
		mTabHost = (TabHost) findViewById(R.id.child_tabhost);
		mTabHost.setup(this.getLocalActivityManager());
		mTabHost.addTab(mTabHost.newTabSpec(TAB_ID_CITY).setIndicator("city").setContent(R.id.city_view));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_ID_DOWNLOAD).setIndicator("download").setContent(R.id.download_view));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_ID_LOCAL).setIndicator("local").setContent(R.id.local_view));
		scrollTag = initImageView(R.id.top_bar_select);
		tab1 = initButton(R.id.tab1);
		tab2 = initButton(R.id.tab2);
		tab3 = initButton(R.id.tab3);
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab3.setOnClickListener(this);
		mTabHost.setOnTabChangedListener(this);
		ControlBar bar = (ControlBar) this.findViewById(R.id.ControlBar);
		this.downloadButton = bar.getButton(0);
		this.downloadButton.setText(getResources().getString(R.string.download));
		this.downloadButton.setOnClickListener(this);
		this.downloadButton.setEnabled(false);
		this.stopButton = bar.getButton(1);
		this.stopButton.setText(getResources().getString(R.string.stop));
		this.stopButton.setOnClickListener(this);
		this.stopButton.setVisibility(View.GONE);
		this.deleteButton = bar.getButton(2);
		this.deleteButton.setText(getResources().getString(R.string.delete));
		this.deleteButton.setOnClickListener(this);
		this.deleteButton.setVisibility(View.GONE);
		this.okButton = bar.getButton(3);
		this.okButton.setText(getResources().getString(R.string.str_ok));
		this.okButton.setOnClickListener(this);
		this.tabId = "cityView";
		this.cityAdapter = new CityAdapter(this, R.layout.map_baidu_offline_city_row, this.cityList);
		ListView cityView = (ListView) findViewById(R.id.city_view);
		cityView.setAdapter(cityAdapter);
		cityView.setOnItemClickListener(this);
		this.downloadAdapter = new DownloadAdapter(this, R.layout.map_baidu_offline_download_row, this.downloadList);
		ListView downloadView = (ListView) findViewById(R.id.download_view);
		downloadView.setAdapter(downloadAdapter);
		this.localAdapter = new LocalAdapter(this, R.layout.map_baidu_offline_local_row, this.localList);
		ListView localView = (ListView) findViewById(R.id.local_view);
		localView.setAdapter(localAdapter);
	}

	/**
	 * 初始化地图下载列表
	 */
	private void initCityList() {
		this.cityList.clear();
		this.cityList.addAll(this.service.getCityList());
		this.cityAdapter.notifyDataSetChanged();
	}

	/**
	 * 初始化下载中列表
	 */
	private void initDownloadList() {
		this.downloadList.clear();
		this.downloadList.addAll(this.service.getDownloadList());
		this.downloadAdapter.notifyDataSetChanged();
	}

	/**
	 * 初始化本地列表
	 */
	private void initLocalList() {
		this.localList.clear();
		this.localList.addAll(this.service.getLocalList());
		this.localAdapter.notifyDataSetChanged();
	}

	/**
	 * 格式化数据大小
	 * 
	 * @param size
	 *          原始大小
	 * @return
	 */
	private String formatDataSize(long size) {
		String ret = "";
		if (size < (UtilsMethod.kbyteRage * UtilsMethod.kbyteRage)) {
			ret = String.format(Locale.getDefault(), "%sK", size / UtilsMethod.kbyteRage);
		} else {
			ret = String.format(Locale.getDefault(), "%.1fM", size / (UtilsMethod.kbyteRage * UtilsMethod.kbyteRage));
		}
		return ret;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "----onDestroy----");
		unbindService(conn);
	}

	@Override
	public void onTabChanged(String tabId) {
		this.tabId = tabId;
		if (this.tabId.equals(TAB_ID_CITY)) {
			this.downloadButton.setVisibility(View.VISIBLE);
			this.stopButton.setVisibility(View.GONE);
			this.deleteButton.setVisibility(View.GONE);
			this.initCityList();
		} else if (this.tabId.equals(TAB_ID_DOWNLOAD)) {
			this.downloadButton.setVisibility(View.VISIBLE);
			this.stopButton.setVisibility(View.VISIBLE);
			this.deleteButton.setVisibility(View.VISIBLE);
			this.initDownloadList();
		} else if (this.tabId.equals(TAB_ID_LOCAL)) {
			this.downloadButton.setVisibility(View.VISIBLE);
			this.stopButton.setVisibility(View.GONE);
			this.deleteButton.setVisibility(View.VISIBLE);
			this.initLocalList();
		}
	}

	/**
	 * 城市列表适配类
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class CityAdapter extends ArrayAdapter<MKOLSearchRecord> {
		/** 资源ID */
		private int resourceId;

		public CityAdapter(Context context, int textViewResourceId, List<MKOLSearchRecord> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
				view = vi.inflate(resourceId, null, true);
			} else {
				view = convertView;
			}
			MKOLSearchRecord record = this.getItem(position);
			String cityName = record.cityName;
			if (record.cityType == 2)
				cityName = "  " + cityName;
			((TextView) view.findViewById(R.id.city_name)).setText(cityName);
			((TextView) view.findViewById(R.id.map_size)).setText(formatDataSize(record.dataSize));
			CheckBox check = (CheckBox) view.findViewById(R.id.check);
			check.setTag(record);
			boolean isChecked = false;
			if (record.cityType == 1) {
				if (provinceIdSet.contains(record.cityID))
					isChecked = true;
			} else {
				if (cityIdSet.contains(record.cityID))
					isChecked = true;
			}
			isNotifyChecked = false;
			check.setChecked(isChecked);
			isNotifyChecked = true;
			check.setOnCheckedChangeListener(activity);
			view.setTag(record);
			return view;
		}
	}

	/**
	 * 城市列表适配类
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class DownloadAdapter extends ArrayAdapter<MKOLUpdateElement> {
		/** 资源ID */
		private int resourceId;

		public DownloadAdapter(Context context, int textViewResourceId, List<MKOLUpdateElement> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
				view = vi.inflate(resourceId, null, true);
			} else {
				view = convertView;
			}
			MKOLUpdateElement city = this.getItem(position);
			((TextView) view.findViewById(R.id.city_name)).setText(city.cityName);
			((TextView) view.findViewById(R.id.map_size)).setText(formatDataSize(city.serversize));
			String state = getResources().getString(R.string.baidumap_offline_state_finish);
			if (city.ratio < 100) {
				switch (city.status) {
				case MKOLUpdateElement.DOWNLOADING:
					state = city.ratio + "%";
					break;
				case MKOLUpdateElement.SUSPENDED:
					state = getResources().getString(R.string.baidumap_offline_state_stop);
					break;
				case MKOLUpdateElement.WAITING:
					state = getResources().getString(R.string.baidumap_offline_state_wait);
					break;
				case MKOLUpdateElement.FINISHED:
					state = getResources().getString(R.string.baidumap_offline_state_finish);
				default:
					state = getResources().getString(R.string.baidumap_offline_state_error);
					break;
				}
			}
			((TextView) view.findViewById(R.id.state)).setText(state);
			CheckBox check = (CheckBox) view.findViewById(R.id.check);
			check.setTag(city);
			boolean isChecked = false;
			if (downloadIdSet.contains(city.cityID))
				isChecked = true;
			isNotifyChecked = false;
			check.setChecked(isChecked);
			isNotifyChecked = true;
			check.setOnCheckedChangeListener(activity);
			view.setTag(city);
			return view;
		}
	}

	/**
	 * 城市列表适配类
	 * 
	 * @author jianchao.wang
	 * 
	 */
	private class LocalAdapter extends ArrayAdapter<MKOLUpdateElement> {
		/** 资源ID */
		private int resourceId;

		public LocalAdapter(Context context, int textViewResourceId, List<MKOLUpdateElement> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
				view = vi.inflate(resourceId, null, true);
			} else {
				view = convertView;
			}
			MKOLUpdateElement city = this.getItem(position);
			((TextView) view.findViewById(R.id.city_name)).setText(city.cityName);
			((TextView) view.findViewById(R.id.map_size)).setText(formatDataSize(city.size));
			String state = "";
			if (city.update) {
				state = getResources().getString(R.string.baidumap_offline_state_can_update);
			} else {
				state = getResources().getString(R.string.baidumap_offline_state_last);
			}
			((TextView) view.findViewById(R.id.state)).setText(state);
			CheckBox check = (CheckBox) view.findViewById(R.id.check);
			check.setTag(city);
			boolean isChecked = false;
			if (downloadIdSet.contains(city.cityID))
				isChecked = true;
			isNotifyChecked = false;
			check.setChecked(isChecked);
			isNotifyChecked = true;
			check.setOnCheckedChangeListener(activity);
			view.setTag(city);
			return view;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (!this.isNotifyChecked)
			return;
		if (this.tabId.equals(TAB_ID_CITY)) {
			onCityCheckedChanged(buttonView, isChecked);
		} else if (this.tabId.equals(TAB_ID_DOWNLOAD)) {
			onDownloadCheckedChanged(buttonView, isChecked);
		} else if (this.tabId.equals(TAB_ID_LOCAL)) {
			onLocalCheckedChanged(buttonView, isChecked);
		}
	}

	/**
	 * 已下载勾选框状态监听
	 * 
	 * @param buttonView
	 * @param isChecked
	 */
	private void onLocalCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MKOLUpdateElement city = (MKOLUpdateElement) buttonView.getTag();
		if (isChecked)
			this.localIdSet.add(city.cityID);
		else
			this.localIdSet.remove(city.cityID);
	}

	/**
	 * 下载中勾选框状态监听
	 * 
	 * @param buttonView
	 *          勾选框
	 * @param isChecked
	 *          勾选状态
	 */
	private void onDownloadCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MKOLUpdateElement city = (MKOLUpdateElement) buttonView.getTag();
		if (isChecked)
			this.downloadIdSet.add(city.cityID);
		else
			this.downloadIdSet.remove(city.cityID);
		if (this.downloadIdSet.isEmpty())
			this.downloadButton.setEnabled(false);
		else
			this.downloadButton.setEnabled(true);
	}

	/**
	 * 地图下载勾选框状态监听
	 * 
	 * @param buttonView
	 *          勾选框
	 * @param isChecked
	 *          勾选状态
	 */
	private void onCityCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MKOLSearchRecord checked = (MKOLSearchRecord) buttonView.getTag();
		if (isChecked) {
			if (checked.cityType == 1) {
				this.provinceIdSet.add(checked.cityID);
				for (MKOLSearchRecord child : checked.childCities) {
					this.cityIdSet.add(child.cityID);
				}
			} else {
				this.cityIdSet.add(checked.cityID);
			}
		} else {
			if (checked.cityType == 1) {
				this.provinceIdSet.remove(checked.cityID);
				for (MKOLSearchRecord child : checked.childCities) {
					this.cityIdSet.remove(child.cityID);
				}
			} else {
				this.cityIdSet.remove(checked.cityID);
			}
		}
		if (checked.cityType == 2) {
			for (MKOLSearchRecord record : this.cityList) {
				if (record.cityType == 1) {
					int count = 0;
					for (MKOLSearchRecord child : record.childCities) {
						if (this.cityIdSet.contains(child.cityID))
							count++;
					}
					if (record.childCities.size() == count)
						this.provinceIdSet.add(record.cityID);
					else
						this.provinceIdSet.remove(record.cityID);
				}
			}
		}
		if (this.cityIdSet.isEmpty())
			this.downloadButton.setEnabled(false);
		else
			this.downloadButton.setEnabled(true);
		this.initCityList();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.Button01:// 下载
			if (this.tabId.equals(TAB_ID_CITY)) {
				this.service.addDownload(provinceIdSet, cityIdSet);
				this.cityIdSet.clear();
				this.provinceIdSet.clear();
				this.mTabHost.setCurrentTabByTag(TAB_ID_DOWNLOAD);
				this.snapToScreen(this.mTabHost.getCurrentTab());
			} else if (this.tabId.equals(TAB_ID_DOWNLOAD)) {
				this.service.redownload(downloadIdSet);
				this.downloadIdSet.clear();
				this.initDownloadList();
			} else if (this.tabId.equals(TAB_ID_LOCAL)) {
				this.service.updateLocal(this.localIdSet);
				this.localIdSet.clear();
				this.mTabHost.setCurrentTabByTag(TAB_ID_DOWNLOAD);
				this.snapToScreen(this.mTabHost.getCurrentTab());
			}
			break;
		case R.id.Button02:// 停止
			if (this.tabId.equals(TAB_ID_DOWNLOAD)) {
				this.service.stopDownload(downloadIdSet);
				this.downloadIdSet.clear();
				this.initDownloadList();
			}
			break;
		case R.id.Button03:// 删除
			if (this.downloadIdSet.isEmpty() && this.localIdSet.isEmpty())
				return;
			new BasicDialog.Builder(BaiduMapOfflineActivity.this).setTitle(R.string.delete)
					.setMessage(R.string.baidumap_offline_delete_message)
					.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							if (tabId.equals(TAB_ID_DOWNLOAD)) {
								service.deleteCities(downloadIdSet);
								downloadIdSet.clear();
								initDownloadList();
							} else if (tabId.equals(TAB_ID_LOCAL)) {
								service.deleteCities(localIdSet);
								localIdSet.clear();
								initLocalList();
							}
						}
					}).setNegativeButton(R.string.str_cancle).show();
			break;
		case R.id.Button04:// 确定
			this.setResult(BaseMapActivity.OFFLINE_MAP_RESULT_CODE, new Intent());
			this.finish();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	/** 连接 */
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			// 返回一个MsgService对象
			service = ((BaiduMapOfflineService.OfflineBinder) binder).getService();
			service.init();
			initCityList();
			initDownloadList();
			initLocalList();

			// 注册回调接口来接收下载进度的变化
			service.setListener(new BaiduMapOfflineListener() {

				@Override
				public void refresh() {
					initDownloadList();
					initLocalList();
				}
			});
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MKOLSearchRecord record = (MKOLSearchRecord) view.getTag();
		boolean refresh = this.service.setClickProvince(record);
		if (refresh) {
			this.initCityList();
		}
	}

}

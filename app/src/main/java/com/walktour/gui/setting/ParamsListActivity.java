package com.walktour.gui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.stickyheader.SimpleSectionedListAdapter;
import com.walktour.framework.view.stickyheader.SimpleSectionedListAdapter.Section;
import com.walktour.gui.R;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ParamsListActivity extends BasicActivity implements OnClickListener{

	public final static int PARAM_TYPE_VOICE = 0;
	public final static int PARAM_TYPE_DATA = 1;
	private String networkType = "";
	private ListView list;
	private ListView searchList;
	private List<Parameter> datas = new ArrayList<Parameter>();
	private List<Parameter> searchDatas = new ArrayList<Parameter>();
	private List<Parameter> tmpList = new ArrayList<Parameter>();
	private LayoutInflater mLayoutInflater = null;
	private ListAdapter adapter = null;
	private SearchListAdapter searchListAdapter = null;
//	private String[] mHeaderNames = new String[]{"voice", "data"};
//	private int[] mHeaderPositions = new int[2];
	private List<String> mHeaderNames = new ArrayList<String>();
	private List<Integer> mHeaderPositions = new ArrayList<Integer>();
	private ArrayList<Section> sections = new ArrayList<Section>();
	/**搜索view*/
	private LinearLayout searchLayout = null;
	private ParameterSetting mParameterSet;
	private SimpleSectionedListAdapter simpleSectionedListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.params_list);
		mLayoutInflater = LayoutInflater.from(this);
		mParameterSet = ParameterSetting.getInstance();
        mParameterSet.initMapLocusShape(this);
		loadExtras();
		initView();
		initParamsData(networkType);
	}
	
	private void loadExtras() {
		
		Bundle bundle = getIntent().getExtras();
		if (bundle == null)
			return;
		networkType = bundle.getString("networkType");

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1://全选
			chooseAll(true);
			break;
		case R.id.button2://反选
			inverse();
			break;
		case R.id.button3://搜索
			if (searchLayout.isShown()) {
				searchLayout.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			} else {
				searchLayout.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.button4://重置
			//TODO
			reset();
			break;

		default:
			break;
		}
	}
	
	private void initTopbar() {
		ImageView iv = initImageView(R.id.pointer);
		TextView title = initTextView(R.id.title_txt);
		title.setText(getResources().getString(R.string.params_list_all_params));
		LinearLayout layoutRight = (LinearLayout)findViewById(R.id.title_right);
		View v = LayoutInflater.from(this).inflate(R.layout.button_style1, null);
		layoutRight.addView(v);
		v.findViewById(R.id.summit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				saveParameters();
				finish();
			}
		});
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	
	private void saveParameters() {
//		new Thread(){
//			public void run() {
				mParameterSet.saveMapParameters(datas);
				mParameterSet.initialParameter();
//			};
//		}.start();
	}

	private void initView() {
		initTopbar();
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		findViewById(R.id.button4).setOnClickListener(this);
		
		searchLayout = (LinearLayout)findViewById(R.id.search_bar);
		searchList = (ListView)findViewById(R.id.search_list);
		EditText searchEditText = initEditText(R.id.search_bar_edit);
		searchEditText.addTextChangedListener(mTextWatcher);
		searchListAdapter = new SearchListAdapter();
		searchList.setAdapter(searchListAdapter);
		
		list = (ListView)findViewById(R.id.list);
		adapter = new ListAdapter();
		
		simpleSectionedListAdapter = new SimpleSectionedListAdapter(this, adapter,
				R.layout.list_item_header, R.id.header);
		list.setAdapter(simpleSectionedListAdapter);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.d("TTT", "s:" + s.toString());
			search(s.toString());
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			
		}
	};
	
	/**
	 * 搜索
	 * @param name
	 */
	private void search(String name) {
		searchDatas.clear();
		if (name == null || name.trim().equals("")) {
			list.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.GONE);
		} else {
			list.setVisibility(View.GONE);
			searchList.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < datas.size(); i++) {
			Parameter item = datas.get(i);
			String nameTmp = item.getShowName();
			if (nameTmp.toLowerCase().contains(name.toLowerCase())) {
				searchDatas.add(item);
			}	
		}
		searchListAdapter.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
	}
	
	private void initParamsData(String networkType) {
		datas.clear();
		tmpList.clear();
		datas = mParameterSet.getParametersByNetworkType(networkType);
		tmpList.addAll(datas);
		tmpList.retainAll(datas);
		datas.clear();

		
		List<Parameter> voiceList = new ArrayList<Parameter>();
		List<Parameter> dataList = new ArrayList<Parameter>();
		List<Parameter> systemList = new ArrayList<Parameter>();
		List<Parameter> otherList = new ArrayList<Parameter>();
		
		for (int i = 0; i < tmpList.size(); i++) {
			if (tmpList.get(i).getTaskType() == 1) {
				voiceList.add(tmpList.get(i));
			} else if (tmpList.get(i).getTaskType() == 2) {
				dataList.add(tmpList.get(i));
			} else if (tmpList.get(i).getTaskType() == 3) {
				systemList.add(tmpList.get(i));
			} else {
				otherList.add(tmpList.get(i));
			}
		}
		datas.addAll(voiceList);
		datas.addAll(dataList);
		datas.addAll(systemList);
		datas.addAll(otherList);
		if (voiceList.size() > 0) {
			setHeaderData(0, 1);
		} 
		if (dataList.size() > 0) {
			setHeaderData(voiceList.size(), 2);
		}
		if (systemList.size() > 0) {
			setHeaderData(voiceList.size() + dataList.size(), 3);
		}
		if (otherList.size() > 0) {
			setHeaderData(voiceList.size() + dataList.size() + systemList.size(), 4);
		}
		sections.clear();
		for (int i = 0; i < mHeaderPositions.size(); i++) {
			sections.add(new Section(mHeaderPositions.get(i), mHeaderNames.get(i)));
		}
		simpleSectionedListAdapter.setSections(sections.toArray(new Section[0]));
		adapter.notifyDataSetChanged();
	}
	
	private void setHeaderData(int position, int taskType) {
		mHeaderPositions.add(position);
		if (taskType == 1) {
			mHeaderNames.add("Voice");
		} else if (taskType == 2) {
			mHeaderNames.add("Data");
		} else if (taskType == 3) {
			mHeaderNames.add("System");
		} else {
			mHeaderNames.add("Other");
		}
	}
	
	/**
	 * 全选 反选
	 * @param chooseAll
	 */
	private void chooseAll(boolean chooseAll) {
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			Parameter item = (Parameter) adapter.getItem(i);
//			item.setOnTable(chooseAll);
			item.setDynamicPara(chooseAll);
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 反选
	 */
	private void inverse() {
		int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			Parameter item = (Parameter) adapter.getItem(i);
			if (item.isDynamicPara()) {
				item.setDynamicPara(false);
			} else {
				item.setDynamicPara(true);
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	
	private void reset() {
		mParameterSet.initialParameter();
		mHeaderPositions.clear();
		initParamsData(networkType);
	}
	
	private class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder itemViewHolder;

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item, parent, false);

				itemViewHolder = new ViewHolder();
				itemViewHolder.txt_name = (TextView) convertView.findViewById(R.id.textViewTitle);
				itemViewHolder.img_icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
				convertView.setTag(itemViewHolder);

			} else {

				itemViewHolder = (ViewHolder) convertView.getTag();
			}

			final Parameter item = datas.get(position);
			final boolean isChecked = item.isDynamicPara();
			itemViewHolder.txt_name.setText(item.getShowName() + "");
			if (isChecked)
				itemViewHolder.img_icon.setImageResource(R.drawable.btn_check_on);//R.drawable.img_checked
			else
				itemViewHolder.img_icon.setImageResource(R.drawable.btn_check_off);//R.drawable.img_unchecked
			itemViewHolder.img_icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isChecked) {
						datas.get(position).setDynamicPara(false);
					} else {
						datas.get(position).setDynamicPara(true);
					}
					adapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}
	
	private class SearchListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return searchDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return searchDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder itemViewHolder;

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item, parent, false);

				itemViewHolder = new ViewHolder();
				itemViewHolder.txt_name = (TextView) convertView.findViewById(R.id.textViewTitle);
				itemViewHolder.img_icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
				convertView.setTag(itemViewHolder);

			} else {

				itemViewHolder = (ViewHolder) convertView.getTag();
			}

			final Parameter item = searchDatas.get(position);
			final boolean isChecked = item.isDynamicPara();
			itemViewHolder.txt_name.setText(item.getShowName());
			itemViewHolder.txt_name.setTextColor(getResources().getColor(R.color.red));
			if (isChecked)
				itemViewHolder.img_icon.setImageResource(R.drawable.btn_check_on);
			else
				itemViewHolder.img_icon.setImageResource(R.drawable.btn_check_off);
			itemViewHolder.img_icon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isChecked) {
						searchDatas.get(position).setDynamicPara(false);
					} else {
						searchDatas.get(position).setDynamicPara(true);
					}
					searchListAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}
	
	public static class ViewHolder {
		public TextView txt_name;
		public ImageView img_icon;
	}

}

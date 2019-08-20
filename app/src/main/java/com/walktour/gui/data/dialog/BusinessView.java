package com.walktour.gui.data.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.gui.R;
import com.walktour.gui.data.FilterKey;
import com.walktour.gui.data.model.BusinessModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessView extends BaseView{

	private View mView;
	private ListView mListView;
	public List<BusinessModel> selectedList = new ArrayList<BusinessModel>();
	public ListAdapter adapter;
	
	public BusinessView(Context context, String type) {
		super(context, type);
		init();
	}
	
	public View getView() {
		return this.mView;
	}
	
	private void init() {
		mView = inflater.inflate(R.layout.business_fragment, null);
		mListView = (ListView)mView.findViewById(R.id.ListView02);
		selectedList.clear();
		initData();
	}
	private void initData() {
		BusinessModel[] businesses = getBusiness();
		adapter = new ListAdapter(businesses);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (adapter.getDatas()[position].checked) {
					adapter.getDatas()[position].checked = false;
					selectedList.remove(adapter.getDatas()[position]);
				} else {
					adapter.getDatas()[position].checked = true;
					selectedList.add(adapter.getDatas()[position]);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 获取实际发生采样的测试任务
	 * @return
	 */
	private BusinessModel[] getBusiness() {
		String selectedKeys = mPreferences.getString(FilterKey.KEY_BUSINESS_SELECTED + type, "");
		List<BusinessModel> list = mDBManager.getBusinessList();
		BusinessModel[] result = new BusinessModel[list.size()];
		for (BusinessModel bm : list) {
			bm.checked = selectedKeys.contains(bm.typeKey + "");
			if (bm.checked) selectedList.add(bm);
		}
		list.toArray(result);
		return result;
	}
	
	public class ListAdapter extends BaseAdapter{
		private BusinessModel[] businesses;
		public ListAdapter(BusinessModel[] businesses) {
			this.businesses = businesses;
		}

		public BusinessModel[] getDatas() {
			return this.businesses;
		}
		@Override
		public int getCount() {
			return businesses.length;
		}

		@Override
		public Object getItem(int arg0) {
			return businesses[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.business_fragment_listitem, null);
				holder.tv_name = (TextView)view.findViewById(R.id.txt_name);
				holder.img_choose = (ImageView)view.findViewById(R.id.checkBox1);
				view.setTag(holder);
			} else {
				holder = (ViewHolder)view.getTag();
			}
			holder.tv_name.setText(businesses[position].name);
			holder.img_choose.setImageResource(businesses[position].checked ? R.drawable.btn_check_on : R.drawable.btn_check_off);
			return view;
		}
		
	}
	
	private static class ViewHolder{
		private TextView tv_name;
		private ImageView img_choose;
	}
	
}

package com.walktour.gui.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义自动匹配适配器
 * @author zhihui.lian
 *
 */
public class CustomAutoCompleteAdapter extends BaseAdapter implements Filterable {
		
	private Context context;
	private ArrayFilter mFilter;
	private ArrayList<String> mOriginalValues;// 所有的Item
	private List<String> mObjects;// 过滤后的item
	private LayoutInflater inflater;
	private final Object mLock = new Object();
	public CustomAutoCompleteAdapter(Context context,
			ArrayList<String> mOriginalValues) {
		this.context = context;
		this.mOriginalValues = mOriginalValues;
	}

	public void SetAutoCompleteAdapter(ArrayList<String> mOriginalValues) {
		this.mOriginalValues = mOriginalValues;
		mOriginalValues.add("null");
		mObjects = mOriginalValues;
	}

	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();  
			  
            if (prefix == null || prefix.length() == 0) {  
                synchronized (mLock) {
                	mOriginalValues.remove("null");
                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);  
                    results.values = list;  
                    results.count = list.size(); 
                    return results;
                }  
            } else {
                String prefixString = prefix.toString().toLowerCase();  
  
                final int count = mOriginalValues.size();  
  
                final ArrayList<String> newValues = new ArrayList<String>(count);  
  
                for (int i = 0; i < count; i++) {
                    final String value = mOriginalValues.get(i);  
                    final String valueText = value.toLowerCase();  
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);  
                    } 
                }  
                results.values = newValues;  
                results.count = newValues.size();  
            }  
  
            return results;
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			mObjects = (List<String>) results.values;
			mObjects.add("null");
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	public int getCount() {
		// TODO Auto-generated method stub
		if (mObjects != null) {
			return mObjects.size();
		} else {
			return 0;
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(position == mObjects.size() - 1 ){
			return	0;
		}else{
			return 	1;
		}
		
	}
	
	@Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mObjects.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		ViewHolder1 holder1 = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case 0:
				holder1 = new ViewHolder1();
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.search_list_item_historry, null);
				holder1.tv = (TextView) convertView.findViewById(R.id.clear_historry);
				convertView.setTag(holder1);
				break;
				
			case 1:
				holder = new ViewHolder();
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.search_list_item_autocomplete, null);
				holder.tv = (TextView) convertView.findViewById(R.id.search_list_item_textview);
				convertView.setTag(holder);
				break;
			}
		} else {
			switch (type) {
			case 0:
				holder1 = (ViewHolder1) convertView.getTag();				
				break;
			case 1:
				holder = (ViewHolder) convertView.getTag();
				break;	
			}
		}
		switch (type) {
		case 1:
			holder.tv.setText(mObjects.get(position));
			break;
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView iv;
	}
	class ViewHolder1 {
		TextView tv;
		ImageView iv;
	}
	

	public ArrayList<String> getAllItems() {
		return mOriginalValues;
	}

}
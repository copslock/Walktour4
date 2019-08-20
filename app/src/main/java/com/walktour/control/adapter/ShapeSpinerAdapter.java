package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.colorpicker.ColorPanelView;
import com.walktour.gui.R;

public class ShapeSpinerAdapter extends BasicActivity implements SpinnerAdapter {
    
    private Context mContext;
    
    private int[] sizes;
    
    /**
     * [构造简要说明]
     */
    public ShapeSpinerAdapter(Context context,int[] sizes) {
        super();
        this.mContext = context;
        this.sizes = sizes;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        
    }
    
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean hasStableIds() {
        return false;
    }
    
    @Override
    public int getViewTypeCount() {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = new ColorPanelView(mContext);
        int width = (int) (90 * mContext.getResources().getDisplayMetrics().density);
        int height = (int) (sizes[position] * mContext.getResources().getDisplayMetrics().density);
        convertView.setLayoutParams(new LayoutParams(width, height));
        return convertView;
    }
    
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public Object getItem(int position) {
        return sizes[position];
    }
    
    @Override
    public int getCount() {
        return sizes.length;
    }
    
    @SuppressLint("InflateParams")
		@Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
			ColorPanelView colorPanelView = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.color_spinner_item, null);
				colorPanelView = (ColorPanelView) convertView.findViewById(R.id.color_panel);
				// colorPanelView.setColor(Color.WHITE);
				convertView.setTag(colorPanelView);
			} else {
				colorPanelView = (ColorPanelView) convertView.getTag();
			}
			int height = (int) (sizes[position] * mContext.getResources().getDisplayMetrics().density);
			int panding = (int) (16 * mContext.getResources().getDisplayMetrics().density);
			colorPanelView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
			colorPanelView.setPadding(panding, 0, panding, 0);
			return convertView;
    }
}

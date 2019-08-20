package com.walktour.gui.applet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;


/**
 *<p>????ListView????ListActivity??Adapter,MySimpleAdapter??API??SimpleAdapter???????????,
 *??????ListView??Item?????ProgressBar</p>
 *@author qihang.li@dinglicom.com
 */
public class MySimpleAdapter extends BaseAdapter implements android.widget.Filterable {
	public final static String KEY_DELETE = "ItemDelete";
	private final String tag = "MySimpleAdapter";
    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;
    private final WeakHashMap<View, View[]> mHolders = new WeakHashMap<View, View[]>();

    private SimpleFilter mFilter;
    private ArrayList<Map<String, ?>> mUnfilteredData;
    
    private boolean[] mChecked;//?????????CheckBox??item;
    private Context context;
    /**
     * Constructor
     * 
     * @param context The context where the View associated with this SimpleAdapter is running
     * @param data A List of Maps. Each entry in the List corresponds to one row in the list. The
     *        Maps contain the data for each row, and should include all the entries specified in
     *        "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *        item. The layout file should include at least those named views defined in "to"
     * @param from A list of column names that will be added to the Map associated with each
     *        item.
     * @param to The views that should display column in the "from" parameter. These should all be
     *        TextViews. The first N views in this list are given the values of the first N columns
     *        in the from parameter.
     */
    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to) {
        mData = data;
        mChecked = new boolean[mData.size()];
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context=context;
        
    }
    
    /**
     * ????????item?????
     * */
    public boolean[] getChecked(){
    	return this.mChecked;
    }

    /**
     * ???????е?item
     * */
    public boolean hasChecked(){
    	for(boolean x:mChecked){
    		if(x)return true;
    	}
    	return false;
    }
    
    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);

            final int[] to = mTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for (int i = 0; i < count; i++) {
                holder[i] = v.findViewById(to[i]);
            }

            mHolders.put(v, holder);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    /**
     * <p>Sets the layout resource to create the drop down views.</p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }
    
    @Override
    public void notifyDataSetChanged() {
    	super.notifyDataSetChanged() ;
    	mChecked = new boolean[mData.size()];
    }

    private void bindView(int position, View view) {
        final Map<String, ?> dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final View[] holder = mHolders.get(view);
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = holder[i];
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                Object text = data == null ? "" : data;

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text.toString());
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                            mChecked[position] = (Boolean) data; 
                            final int c = position;
                            final int iFrom = i;
                            v.setOnClickListener(new OnClickListener(){
								@SuppressWarnings("unchecked")
								@Override
								public void onClick(View v) {
									mChecked[c] = ! mChecked[c];
									HashMap<String, Object> hashMap =  (HashMap<String, Object>) mData.get( c );
									hashMap.put((String)from[iFrom],(Object)mChecked[c] );
								}
                            	
                            });
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " + data.getClass());
                        }
                    }else if( v instanceof ImageButton ){//ImageButton???????????Delete???
                    	if( data instanceof Integer){
                    		setImageButton( (ImageButton)v,(Integer)data );
                			final int c = position;
                			v.setOnClickListener(
            					new OnClickListener(){
    								@Override
    								public void onClick(View v) {
    									new BasicDialog.Builder(context).setTitle(R.string.delete)
    									.setIcon(android.R.drawable.ic_menu_delete)
    									.setMessage(R.string.str_delete_makesure)
    									.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
    										@Override
    										public void onClick(DialogInterface dialog,
    												int which) {
    											Object obj = dataSet.get( MySimpleAdapter.KEY_DELETE  );
    	    									if( obj instanceof File){
    	    										File file = (File) obj;
    	    										LogUtil.w(tag,"delete file:"+file.getAbsolutePath() );
    	    										file.delete();
    	    									}
    	    									mData.remove( c );
    	    									MySimpleAdapter.this.notifyDataSetChanged();
    										}
    									}).setNegativeButton(R.string.str_cancle).show();
    									
    								}
                                	
                                }
                			);
                    	}else{
                    		throw new IllegalStateException(v.getClass().getName() +
                            " should be bound to a Integer");
                    	}
                    }else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);                            
                        } else {
                            setViewImage((ImageView) v, text.toString());
                        }
                    } else if(v instanceof ProgressBar){
                    	if(data instanceof Integer){
                    		setViewProgress( (ProgressBar)v, (Integer)data );
                    	} else {
                                throw new IllegalStateException(v.getClass().getName() +
                                        " should be bound to a Integer");
                    	}
                    }
                    else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link ViewBinder} used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     *
     */
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to
     *        remove the existing binder
     *
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * This method is called instead of {@link #setViewImage(ImageView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, String)
     */
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * By default, the value will be treated as an image resource. If the
     * value cannot be used as an image resource, the value is used as an
     * image Uri.
     *
     * This method is called instead of {@link #setViewImage(ImageView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(ImageView, int) 
     */
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }
    
    /**
     * ????Щ?????????
     * ??Bitmap???ImageView??????
     * */
    public void setViewImage(ImageView v, Bitmap value) {
        try {
            v.setImageBitmap(value);
        } catch (Exception e) {
        	e.printStackTrace();
            v.setImageResource( android.R.drawable.stat_notify_error );
        }
    }
    
    /**
     * ???????ITEM??ImageButton
     * */
    public void setImageButton(ImageButton v, int value) {
    	try {
    		v.setBackgroundResource(value);
    	} catch (Exception e) {
    		e.printStackTrace();
    		v.setImageResource( android.R.drawable.stat_notify_error );
    	}
    }
    
    /**
     * mData?????item view???????????
     * */
    protected  void setOnItemViewClick(int position){
    	
    }
    
    /**
     * Called by bindView() to set the text for a TextView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an TextView.
     *
     * @param v TextView to receive text
     * @param text the text to be set for the TextView
     */
    public void setViewText(TextView v, Object text) {
        if (text instanceof SpannableString) {
            v.setText((SpannableString)text);                         
        } else {
            v.setText((String)text);
        }
    }
    
    public void setViewProgress(ProgressBar v,int progress){
    	if( progress < 0 || progress>=100 ){
    		v.setVisibility(View.INVISIBLE);
    	}else{
    		v.setProgress(progress);
    		v.setVisibility(View.VISIBLE);
    	}
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    /**
     * This class can be used by external clients of SimpleAdapter to bind
     * values to views.
     *
     * You should use this class to bind values to views that are not
     * directly supported by SimpleAdapter or to change the way binding
     * occurs for views supported by SimpleAdapter.
     *
     * @see SimpleAdapter#setViewImage(ImageView, int)
     * @see SimpleAdapter#setViewImage(ImageView, String)
     * @see SimpleAdapter#setViewText(TextView, String)
     */
    public static interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         *
         * When binding is handled by this ViewBinder, this method must return true.
         * If this method returns false, SimpleAdapter will attempts to handle
         * the binding on its own.
         *
         * @param view the view to bind the data to
         * @param data the data to bind to the view
         * @param textRepresentation a safe String representation of the supplied data:
         *        it is either the result of data.toString() or an empty String but it
         *        is never null
         *
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

    /**
     * <p>An array filters constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<Map<String, ?>>(mData);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Map<String, ?>> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase(Locale.getDefault());

                ArrayList<Map<String, ?>> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<Map<String, ?>> newValues = new ArrayList<Map<String, ?>>(count);

                for (int i = 0; i < count; i++) {
                    Map<String, ?> h = unfilteredValues.get(i);
                    if (h != null) {
                        
                        int len = mTo.length;

                        for (int j=0; j<len; j++) {
                            String str =  (String)h.get(mFrom[j]);
                            
                            String[] words = str.split(" ");
                            int wordCount = words.length;
                            
                            for (int k = 0; k < wordCount; k++) {
                                String word = words[k];
                                
                                if (word.toLowerCase(Locale.getDefault()).startsWith(prefixString)) {
                                    newValues.add(h);
                                    break;
                                }
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
				@Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mData = (List<Map<String, ?>>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
    
   /* *//**
     * ???????item?????????б?
     * *//*
    public void setItemObjectList(ArrayList<File> fileList){
    	this.removedList = fileList;
    }
    
    private class OnItemDeleteListener implements View.OnClickListener{
    	private int position;
    	public OnItemDeleteListener(int position){
    		this.position = position;
    	}
    	
		@Override
		public void onClick(View arg0) {
			new Thread( new ThreadRemove() ).start();
		}
		
		private class ThreadRemove implements Runnable{
			@Override
			public void run() {
				Object object = removedList.remove(position);;
				if( object instanceof File){
					try{
						File file = (File) object;
						file.delete();
					}catch(Exception e){
						
					}
				}
			}
		}
		
    }*/
}

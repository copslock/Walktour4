/**
 * com.walktour.gui.locknet
 * LockBandwidth.java
 * 类功能：
 * 2013-11-6-下午4:34:03
 * 2013鼎利-版权所有
 * @author qihang.li@dinglicom.com
 */
package com.walktour.gui.locknet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.ForceNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LockBandwidth
 * 
 * 2013-11-6 下午4:34:03
 * 
 * @version 1.0.0
 * @author qihang.li@dinglicom.com
 */
@SuppressLint("InflateParams")
public class LockBandwidth extends LockBasicActivity {
	
	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#titleStringId()
	 */
	@Override
	protected int titleStringId() {
		return R.string.lock_lockband;
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#setViewByDeviceInfo()
	 */
	@Override
	protected void setViewByDeviceInfo() {
		layoutCurrent.setVisibility( View.GONE );
	}

	/**
	 * @see com.walktour.gui.locknet.LockBasicActivity#showLockDialog(int)
	 */
	@Override
	protected void showLockDialog(int layoutViewId) {
		
		mForceMgr.setOnTaskChangeListener( onTaskChangeListener );

		String[] parameterNames = null;
		String[] values = null;
		boolean[] checked = null ;
		switch (layoutViewId) {
		case R.id.setting_layout_2g:
			parameterNames = getResources().getStringArray(R.array.lock_gsm_band);
			checked = new boolean[]{false, false, false, false, false};
			values = getResources().getStringArray(R.array.lock_gsm_band_value);
			queryLockedBand( net2G );
			break;
		case R.id.setting_layout_3G:
			parameterNames = getResources().getStringArray(R.array.lock_td_band);
			checked = new boolean[]{false, false};
			values = getResources().getStringArray(R.array.lock_td_band_value);
			queryLockedBand( net3G );
			break;
		case R.id.setting_layout_lte:
			parameterNames = getResources().getStringArray(R.array.lock_lte_band);
			checked = new boolean[]{false, false, false, false};
			values = getResources().getStringArray(R.array.lock_lte_band_value);;
			queryLockedBand( net4G );
			break;
		}
		if( parameterNames !=null ){
			showCheckView(parameterNames, checked, values, layoutViewId);
		}
	}
	
	/**
	 * 函数功能：查询已经锁定的频段
	 */
	private void queryLockedBand(ForceNet queryType){
		mForceMgr.queryBand(queryType);
	}
	
	ListView list;
	static SimpleAdapter adapter;
	static ArrayList<HashMap<String, Object>> listdata;
	@SuppressWarnings("deprecation")
	private void showCheckView(final String[] parameterNames, final boolean[] checked, 
							  final String[] values, final int id){
        listdata = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < parameterNames.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemText", parameterNames[i]);
            map.put("ItemIcon", checked[i] ? R.drawable.btn_check_on : R.drawable.btn_check_off);
            map.put("ItemChecked", checked[i]);
            map.put("ItemTextValue", values[i]);
            listdata.add(map);
        }
        
        View view = inflater.inflate(R.layout.list_chart, null);
        list = (ListView) view.findViewById(R.id.list);
        
        adapter = new SimpleAdapter(this, listdata,
                R.layout.list_chart_item, new String[] { "ItemText",
                        "ItemIcon" }, new int[] { R.id.ItemText,
                        R.id.ItemIcon });
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                HashMap<String, Object> map = listdata.get(position);
                boolean c = !Boolean.valueOf(map.get("ItemChecked").toString());
                
                if (c) {
                    map.put("ItemIcon", R.drawable.btn_check_on);
                } else {
                    map.put("ItemIcon", R.drawable.btn_check_off);
                }
                map.put("ItemChecked", c);
                adapter.notifyDataSetChanged();
            }
        });
        
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay() .getMetrics(metric);
        new BasicDialog.Builder(this).setTitle(R.string.sys_chart_custom)
                .setView(view, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)(350 * metric.density)))
                .setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            	StringBuffer values = new StringBuffer("\"");
                            	boolean flag = false;
                                for (int i = 0; i < listdata.size(); i++) {
                                    HashMap<String, Object> map = listdata.get(i);
                                    boolean c = Boolean.valueOf(map.get("ItemChecked").toString());
                                    checked[i] = c;
                                    if(c){
                                    	values.append(map.get("ItemTextValue").toString()).append(",");
                                    	flag = true;
                                    }
                                }
                                
                                if(flag){//去掉最后的一个点号
                                	values.deleteCharAt(values.length()-1);
                                }
                                
                                values.append("\"");
                                
                                ForceNet net = net2G;
                                if(id == R.id.setting_layout_2g){
                        			net = net2G;
                        		}else if(id == R.id.setting_layout_3G){
                        			net = net3G;
                        		}else if(id == R.id.setting_layout_lte){
                        			net = net4G;
                        		}
                                mForceMgr.lockBand(net,values.toString() );
                                showProgressDialog(getString(R.string.exe_info));
                               
                            }
                        })
                .setNegativeButton(R.string.str_cancle)
                .show();
	}
	
	private OnTaskChangeListener onTaskChangeListener = new OnTaskChangeListener(){
		String regEx = "ok";
		/**
		 * @see com.dinglicom.dataset.ForceManager.OnTaskChangeListener#onFinished(boolean)
		 */
		@Override
		public void onFinished(boolean success) {
			super.onFinished(success);
			handler.obtainMessage( 0, success).sendToTarget();
		}
		

		@Override
		public void onSimATFinished(Boolean result, String resultContent, int opt) {
			if(result){
				//正则匹配忽略大小写
				Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(resultContent);
				boolean flag = m.find();
				if(flag && opt == SimLockTask.OPT_SEARCH){
					for(HashMap<String, Object> data : listdata){
						p = Pattern.compile(LockDataMatch.getKey(data.get("ItemText").toString()), 
								Pattern.CASE_INSENSITIVE);
						m = p.matcher(resultContent);
						if(m.find()){
							data.put("ItemIcon", R.drawable.btn_check_on);
							data.put("ItemChecked", true);
						}
					}
					adapter.notifyDataSetChanged();
				}else if(opt == SimLockTask.OPT_LOCK){
					if(flag){
						pb.setVisibility(View.GONE);
						diaMessage.setText(getString(R.string.lock_succ));
					}else{
						pb.setVisibility(View.GONE);
						diaMessage.setText(getString(R.string.lock_fail));
					}
				}
			}else{
				if(opt == SimLockTask.OPT_LOCK){
					pb.setVisibility(View.GONE);
					diaMessage.setText(getString(R.string.lock_fail));
				}
			}
		}
	
	};
	

}

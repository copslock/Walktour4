package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.walktour.control.config.ServerManager;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.xml.btu.model.TestScheme;

import java.text.SimpleDateFormat;
import java.util.List;

/***
 * BTU平台的测试方案列表Adatper
 * 
 * @author weirong.fan
 *
 */
@SuppressLint("SimpleDateFormat") 
public class SchemeAdapter extends BaseAdapter {
	private final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
	private Context context;
    private List<TestScheme> taskList;
    
    public SchemeAdapter(List<TestScheme> taskList,Context context) {
        this.taskList = taskList;
        this.context = context;
    }
 
    @Override
    public int getCount() {
    	return (null!=taskList&&taskList.size()>0)?taskList.size():0;
    }
 
    @Override
    public Object getItem(int position) {
        return (null!=taskList&&taskList.size()>0)?taskList.get(position):null;
    }
 
    @Override
    public long getItemId(int position) {
        return (null!=taskList&&taskList.size()>0)?position:0;
    }
 
    @Override
    public View getView(final int position,View convertView,ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_scheme, null);
            holder = new ViewHolder();
            holder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
            holder.ItemDate = (TextView) convertView.findViewById(R.id.ItemDate);
            holder.ItemTime = (TextView) convertView.findViewById(R.id.ItemTime);
            holder.ItemRadio = (RadioButton) convertView.findViewById(R.id.ItemRadio);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        TestScheme scheme = taskList.get(position);
        String startDate = formatDate.format( scheme.getBeginDate());
		String endDate = formatDate.format( scheme.getEndDate() );
		String startTime = formatTime.format( scheme.getBeginDate()+ scheme.getBeginTime() );
		String endTime = formatTime.format( scheme.getBeginDate()+ scheme.getEndTime() ); 
		int select=ServerManager.getInstance(context).getUploadServer();
		if(select==ServerManager.SERVER_ATU)
			holder.ItemTitle.setText("[ATU-"+scheme.getVersion()+"]"+scheme.getDesc()+"-"+position);
		else if(select==ServerManager.SERVER_BTU)
			holder.ItemTitle.setText("[BTU-"+scheme.getVersion()+"]"+scheme.getDesc()+"-"+position);
		holder.ItemDate.setText( startDate+"~"+endDate);
		holder.ItemTime.setText( startTime+"~"+endTime);
		holder.ItemRadio.setChecked( scheme.isUsing() );
        
        return convertView;
    }
    
    private class ViewHolder {
        TextView ItemTitle;
        TextView ItemDate;
        TextView ItemTime;
        RadioButton ItemRadio;
    }
    
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }
}

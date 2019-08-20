package com.walktour.gui.total;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.control.config.ParamInfo;
import com.walktour.control.config.ParamTotalInfo;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.ViewSizeLinstener;
import com.walktour.gui.R;

import java.util.List;

public class TotalPara extends BasicActivity implements ViewSizeLinstener{
    
	private ParamTotalInfo paramTotalInfo =  ParamTotalInfo.getInstance();
	private List<ParamInfo> paramList;
	private static final String tag = "TotalPara";
	String[] names ;
	private static int item_index = 0;
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.totalparaview);
        AbsoluteLayout totalAbsLay = (AbsoluteLayout) findViewById(R.id.total_absoluteLay);
        totalAbsLay.addView(new TotalParaView(this,this),new AbsoluteLayout.LayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT)));
        LinearLayout.LayoutParams sParams = new LinearLayout.LayoutParams(convertDIP2PX(this, 135), convertDIP2PX(this, 45));
        
        Spinner spinner = new Spinner(this.getParent());
        spinner.setId(R.id.Spinner01);
        spinner.setBackgroundResource(R.drawable.drop);
        spinner.getBackground().setAlpha(180);
        spinner.setLayoutParams(sParams);
        paramList = paramTotalInfo.getParamList();
        names = new String[paramList.size()];
        for(int i = 0;i<paramList.size();i++){
        	names[i] = paramList.get(i).getShowName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TotalPara.this, R.layout.simple_spinner_custom_layout, names);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(item_index, true);
        sendBroadcast(item_index);
        spinner.setOnItemSelectedListener(listener);
        totalAbsLay.addView(spinner);
    }
	
	private void sendBroadcast(int position){
		Intent intent = new Intent(WalkMessage.TotalParaSelect);
		intent.putExtra("item_position", position);
		sendBroadcast(intent);
	}
	private OnItemSelectedListener listener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			item_index = position;
			sendBroadcast(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		TotalDataByGSM.currentTotal = WalkStruct.ShowTotalType.Para;
	}

	
	//转换dip为px 
	public static int convertDIP2PX(Context context, int dip) { 
	    float scale = context.getResources().getDisplayMetrics().density; 
	    return (int)(dip*scale + 0.5f*(dip>=0?1:-1)); 
	} 
	
	
	 
	//转换px为dip 
	public static int convertPX2DIP(Context context, int px) { 
	    float scale = context.getResources().getDisplayMetrics().density; 
	    return (int)(px/scale + 0.5f*(px>=0?1:-1)); 
	}

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param height
     * @param weidth
     * @see com.walktour.framework.view.ViewSizeLinstener#onViewSizeChange(int, int)
     */
    
    @Override
	@SuppressWarnings("deprecation")
    public void onViewSizeChange(int height, int weidth) {
        int spWidth = convertDIP2PX(this, 135);
        AbsoluteLayout.LayoutParams sParams = new AbsoluteLayout.LayoutParams(spWidth,convertDIP2PX(this, 36),weidth - spWidth,height * 8 + convertDIP2PX(this, 10));
        this.findViewById(R.id.Spinner01).setLayoutParams(sParams);
    } 

}

package com.walktour.gui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.applet.MyKeyListener;

/**
 *	统计阀值设置
 * @author zhihui.lian
 *
 */
@SuppressLint("InflateParams")
public class TotalSettingActivity extends BasicActivity implements OnClickListener{
	


	private TextView rsrp_txt;
	private TextView pcch_txt;
	private TextView ci_txt;
	private TextView sinr_txt;
	private TextView rxLev_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.total_parameter_setting);
		findView();
	}
	
	/**
	 * 加载数据控件
	 */
	private void findView() {
		TextView title =  initTextView(R.id.title_txt);
		title.setText(getResources().getString(R.string.total_setting_title_str));
		ImageButton pointer = (ImageButton) findViewById(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TotalSettingActivity.this.finish();
			}
		});
		
		RelativeLayout  rsrp_ral = (RelativeLayout)findViewById(R.id.rsrp_ral);
		rsrp_ral.setOnClickListener(this);
		RelativeLayout  sinr_ral = (RelativeLayout)findViewById(R.id.sinr_ral);
		sinr_ral.setOnClickListener(this);
		RelativeLayout  pcch_ral = (RelativeLayout)findViewById(R.id.pcch_ral);
		pcch_ral.setOnClickListener(this);
		RelativeLayout  ci_ral = (RelativeLayout)findViewById(R.id.ci_ral);
		ci_ral.setOnClickListener(this);
		RelativeLayout  rxLev_ral = (RelativeLayout)findViewById(R.id.rxLev_ral);
		rxLev_ral.setOnClickListener(this);
		rsrp_txt = initTextView(R.id.rsrp_txt);
		pcch_txt = initTextView(R.id.pcch_txt);
		ci_txt = initTextView(R.id.ci_txt);
		sinr_txt = initTextView(R.id.sinr_txt);
		rxLev_txt = initTextView(R.id.rxLev_txt);
		
		
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rsrp_ral:
			showDialog(R.id.rsrp_ral);
			break;
			
		case R.id.sinr_ral:
			showDialog(R.id.sinr_ral);
			break;
		case R.id.pcch_ral:
			showDialog(R.id.pcch_ral);
			break;
		case R.id.ci_ral:
			showDialog(R.id.ci_ral);
			break;
		case R.id.rxLev_ral:
			showDialog(R.id.rxLev_ral);
			break;	

		default:
			break;
		}
	}
	
	
	
	
	
	
	/**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param id
     * @return
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
	@SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        BasicDialog.Builder builder = new BasicDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.alert_dialog_edittext, null); 
        final EditText editText = (EditText) view.findViewById(R.id.alert_textEditText);
        switch (id) {
            case R.id.rsrp_ral:
                editText.setSelectAllOnFocus(true);
                editText.setText(rsrp_txt.getText().toString());
                editText.setKeyListener(new MyKeyListener().getIntegerKeyListener());
                //editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle("Input")
                    .setView(view)                
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	rsrp_txt.setText(editText.getText().toString());
                            }
                    })  
                    .setNegativeButton(R.string.str_cancle);
                break;
            case R.id.pcch_ral:
                editText.setSelectAllOnFocus(true);
                editText.setText(pcch_txt.getText().toString());
                editText.setKeyListener(new MyKeyListener().getIntegerKeyListener());
                //editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle("Input")
                    .setView(view)                
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	pcch_txt.setText(editText.getText().toString());
                            }
                    })  
                    .setNegativeButton(R.string.str_cancle);
                break;    
            case R.id.sinr_ral:
                editText.setSelectAllOnFocus(true);
                editText.setText(sinr_txt.getText().toString());
                editText.setKeyListener(new MyKeyListener().getIntegerKeyListener());
                //editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle("Input")
                    .setView(view)                
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	sinr_txt.setText(editText.getText().toString());
                            }
                    })  
                    .setNegativeButton(R.string.str_cancle);
                break;  
                
            case R.id.ci_ral:
                editText.setSelectAllOnFocus(true);
                editText.setText(ci_txt.getText().toString());
                editText.setKeyListener(new MyKeyListener().getIntegerKeyListener());
                //editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle("Input")
                    .setView(view)                
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	ci_txt.setText(editText.getText().toString());
                            }
                    })  
                    .setNegativeButton(R.string.str_cancle);
                break;   
            case R.id.rxLev_ral:
                editText.setSelectAllOnFocus(true);
                editText.setText(rxLev_txt.getText().toString());
                editText.setKeyListener(new MyKeyListener().getIntegerKeyListener());
                //editText.setInputType(android.text.InputType.TYPE_NUMBER_VARIATION_NORMAL);
                builder.setIcon(android.R.drawable.ic_menu_edit)
                    .setTitle("Input")
                    .setView(view)                
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	rxLev_txt.setText(editText.getText().toString());
                            }
                    })  
                    .setNegativeButton(R.string.str_cancle);
                break;     
                
                
            
            default:
                break;
        }
        return builder.create();
    }
	
	
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.slide_in_down);
	}



	
}

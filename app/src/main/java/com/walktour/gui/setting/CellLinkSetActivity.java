/*
 * 文件名: CellLineSetActivity.java
 * 版    权：  Copyright Dingli. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-10-4
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.gui.setting;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.walktour.Utils.WalktourConst;
import com.walktour.control.adapter.ShapeSpinerAdapter;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicSpinner;
import com.walktour.framework.view.colorpicker.ColorPanelView;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-10-4] 
 */
public class CellLinkSetActivity extends BasicActivity implements OnClickListener{
    
    private SharedPreferences mPreferences;
    
    private int defaultColor = 0xFFFF0000/*0xFF000000*/;
    
    private ColorPanelView servingReferenPanel;
    
    private ColorPanelView activeSetPanel;
    
    private ColorPanelView monitorSetPanel;
    
    private ColorPanelView neighborPanel;
    
    private BasicSpinner servingReferenSP;
    
    private BasicSpinner activeSetSP;
    
    private BasicSpinner monitorSetSP;
    
    private BasicSpinner neighborSP;
    
    private CheckBox servingReferenCK;
    
    private CheckBox activeSetCK;
    
    private CheckBox monitorSetCK;
    
    private CheckBox neighborCK;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.celllinkset_activity);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        findView();
    }
    
    private void findView(){
        servingReferenCK  = (CheckBox) findViewById(R.id.serving_reference_cell_checkbox);
        activeSetCK = (CheckBox) findViewById(R.id.active_set_checkbox);
        monitorSetCK = (CheckBox) findViewById(R.id.monitor_candidate_checkbox);
        neighborCK = (CheckBox) findViewById(R.id.neighbor_checkbox);
        
        servingReferenSP = (BasicSpinner) findViewById(R.id.serving_reference_width_spinner);
        activeSetSP = (BasicSpinner) findViewById(R.id.active_set_width_spinner);
        monitorSetSP = (BasicSpinner) findViewById(R.id.monitor_candidate_width_spinner);
        neighborSP = (BasicSpinner) findViewById(R.id.neighbor_width_spinner);
        
        servingReferenPanel = (ColorPanelView)findViewById(R.id.serving_reference_color_panel);
        activeSetPanel = (ColorPanelView) findViewById(R.id.active_set_color_panel);
        monitorSetPanel = (ColorPanelView) findViewById(R.id.monitor_candidate_color_panel);
        neighborPanel = (ColorPanelView) findViewById(R.id.neighbor_color_panel);
        
        servingReferenCK.setOnClickListener(this);
        activeSetCK.setOnClickListener(this);
        monitorSetCK.setOnClickListener(this);
        neighborCK.setOnClickListener(this);
        
        servingReferenPanel.setOnClickListener(this);
        activeSetPanel.setOnClickListener(this);
        monitorSetPanel.setOnClickListener(this);
        neighborPanel.setOnClickListener(this);
        
        servingReferenCK.setChecked(mPreferences.getBoolean(WalktourConst.CellLink.SERVING_REFERENCE_ENABLE, true));
        activeSetCK.setChecked(mPreferences.getBoolean(WalktourConst.CellLink.ACTIVE_SET_ENABLE, true));
        monitorSetCK.setChecked(mPreferences.getBoolean(WalktourConst.CellLink.MONITOR_CANDIDATE_ENABLE, true));
        neighborCK.setChecked(mPreferences.getBoolean(WalktourConst.CellLink.NEIGHBOR_ENABLE, true));
        
        initWidthSpinner(servingReferenSP);
        initWidthSpinner(activeSetSP);
        initWidthSpinner(monitorSetSP);
        initWidthSpinner(neighborSP);
        
        servingReferenSP.setSelection(getWidthSpinnerSelection(mPreferences.getInt(WalktourConst.CellLink.SERVING_REFERENCE_WIDTH, 2)));
        activeSetSP.setSelection(getWidthSpinnerSelection(mPreferences.getInt(WalktourConst.CellLink.ACTIVE_SET_WIDTH, 2)));
        monitorSetSP.setSelection(getWidthSpinnerSelection(mPreferences.getInt(WalktourConst.CellLink.MONITOR_CANDIDATE_WIDTH, 2)));
        neighborSP.setSelection(getWidthSpinnerSelection(mPreferences.getInt(WalktourConst.CellLink.NEIGHBOR_WIDTH, 2)));
        
        servingReferenPanel.setColor(mPreferences.getInt(WalktourConst.CellLink.SERVING_REFERENCE_COLOR, defaultColor));
        activeSetPanel.setColor(mPreferences.getInt(WalktourConst.CellLink.ACTIVE_SET_COLOR, defaultColor));
        monitorSetPanel.setColor(mPreferences.getInt(WalktourConst.CellLink.MONITOR_CANDIDATE_COLOR, defaultColor));
        neighborPanel.setColor(mPreferences.getInt(WalktourConst.CellLink.NEIGHBOR_COLOR, defaultColor));
        
    }

    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param v
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.serving_reference_cell_checkbox:
                mPreferences.edit().putBoolean(WalktourConst.CellLink.SERVING_REFERENCE_ENABLE, servingReferenCK.isChecked()).commit();
                break;
            case R.id.active_set_checkbox:
                mPreferences.edit().putBoolean(WalktourConst.CellLink.ACTIVE_SET_ENABLE, activeSetCK.isChecked()).commit();
                break;
            case R.id.monitor_candidate_checkbox:
                mPreferences.edit().putBoolean(WalktourConst.CellLink.MONITOR_CANDIDATE_ENABLE, monitorSetCK.isChecked()).commit();
                break;
            case R.id.neighbor_checkbox:
                mPreferences.edit().putBoolean(WalktourConst.CellLink.NEIGHBOR_ENABLE, neighborCK.isChecked()).commit();
                break;
            case R.id.serving_reference_color_panel:
                onClickColorPickerDialog(servingReferenPanel,WalktourConst.CellLink.SERVING_REFERENCE_COLOR);
                break;
            case R.id.active_set_color_panel:
                onClickColorPickerDialog(activeSetPanel,WalktourConst.CellLink.ACTIVE_SET_COLOR);
                break;
            case R.id.monitor_candidate_color_panel:
                onClickColorPickerDialog(monitorSetPanel,WalktourConst.CellLink.MONITOR_CANDIDATE_COLOR);
                break;
            case R.id.neighbor_color_panel:
                onClickColorPickerDialog(neighborPanel,WalktourConst.CellLink.NEIGHBOR_COLOR);
                break;
            
            default:
                break;
        }
    }
    
    /**
     * 创建颜色选择器对话框<BR>
     * [功能详细描述]
     * @param colorPanelView 
     * @param colorKey
     */
    public void onClickColorPickerDialog(final ColorPanelView colorPanelView,final String colorKey) {
        int initialValue = mPreferences.getInt(colorKey, defaultColor);
        Log.d("mColorPicker", "initial value:" + initialValue);
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);
        colorDialog.setAlphaSliderVisible(false);
        colorDialog.builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(CellLinkSetActivity.this, "Selected Color: " + colorToHexString(colorDialog.getColor()), Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(colorKey, colorDialog.getColor());
                editor.commit();
                colorPanelView.setColor(colorDialog.getColor());
            }
        });
        colorDialog.builder.setNegativeButton(android.R.string.cancel);
        colorDialog.builder.show();
    }
    
    /**
     * 初始化Spinner选择连线宽度控件<BR>
     * [功能详细描述]
     * @param spinner
     */
    private void initWidthSpinner(final BasicSpinner spinner){
        final int sizes[] = new int[]{2,4,6};
        spinner.setAdapter(new ShapeSpinerAdapter(this, sizes));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                switch (spinner.getId()) {
                    case R.id.serving_reference_width_spinner:
                        mPreferences.edit().putInt(WalktourConst.CellLink.SERVING_REFERENCE_WIDTH, sizes[position]).commit();
                        break;
                    case R.id.active_set_width_spinner:
                        mPreferences.edit().putInt(WalktourConst.CellLink.ACTIVE_SET_WIDTH, sizes[position]).commit();
                        break;
                    case R.id.monitor_candidate_width_spinner:
                        mPreferences.edit().putInt(WalktourConst.CellLink.MONITOR_CANDIDATE_WIDTH, sizes[position]).commit();
                        break;
                    case R.id.neighbor_width_spinner:
                        mPreferences.edit().putInt(WalktourConst.CellLink.NEIGHBOR_WIDTH, sizes[position]).commit();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });
    }
    
    private int getWidthSpinnerSelection(int width){
        int selection = 0;
        switch (width) {
            case 2:
                selection = 0;
                break;
            case 4:
                selection = 1;
                break;
            case 6:
                selection = 2;
                break;
            
            default:
                break;
        }
        return selection;
    }
    
/*    private String colorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFFFF & color);
    }*/
}

package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog.Builder;
import com.walktour.framework.view.colorpicker.ColorPanelView;
import com.walktour.framework.view.colorpicker.ColorPickerDialog;
import com.walktour.gui.R;
import com.walktour.model.Parameter;
import com.walktour.model.Threshold;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class ParamsThresholdSettingActivity extends BasicActivity {

    private ParameterSetting mParameterSet;
    private Spinner paraChoice;
    /**
     * 颜色选择器列表
     */
    private List<ColorPanelView> colors = new ArrayList<ColorPanelView>();
    /**
     * 阀值显示界面
     */
    private LinearLayout thresholdLayout;
    /**
     * 阀值item界面
     */
    private LinearLayout thresholdItemLayout;
    /**
     * 阀值显示text
     */
    private List<TextView> thresholdValueTxtList = new ArrayList<TextView>();
    /**
     * <= >= RadioButtons
     */
    private List<RadioButton> rbListOne = new ArrayList<RadioButton>();
    /**
     * 升序 降序 RadioButtons
     */
    private List<RadioButton> rbListTwo = new ArrayList<RadioButton>();

    private String[] colorStrings;

    private ArrayAdapter<String> colorAdapter;

    private Context mContext;

    private LayoutInflater inflater;

    private List<ColorModel> datas = new ArrayList<ParamsThresholdSettingActivity.ColorModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.params_threshold_setting);
        mContext = this;
        inflater = LayoutInflater.from(this);
        mParameterSet = ParameterSetting.getInstance();
        mParameterSet.initMapLocusShape(this);
        initTopbar();
        findView();
    }

    private void initTopbar() {
        TextView title = initTextView(R.id.title_txt);
        title.setText(getResources().getString(R.string.threshold_setting));
        ImageView iv = initImageView(R.id.pointer);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                saveThreshold();
                finish();
            }
        });
    }

    private void findView() {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        paraChoice = (Spinner) findViewById(R.id.SpinnerParameter);
        thresholdLayout = (LinearLayout) findViewById(R.id.sys_map_param_threshold_view);
        thresholdItemLayout = (LinearLayout) findViewById(R.id.threshold_layout);
        RadioGroup rg1 = (RadioGroup) findViewById(R.id.radioGroup1);//设置<=/>=
        RadioGroup rg2 = (RadioGroup) findViewById(R.id.radioGroup2);//设置升序/降序
        rg1.setOnCheckedChangeListener(onCheckedChangeListrner);
        rg2.setOnCheckedChangeListener(onCheckedChangeListrner);
        rbListOne.clear();
        rbListTwo.clear();
        rbListOne.add((RadioButton) findViewById(R.id.radio0));
        rbListOne.add((RadioButton) findViewById(R.id.radio1));
        rbListTwo.add((RadioButton) findViewById(R.id.radio3));
        rbListTwo.add((RadioButton) findViewById(R.id.radio4));
        rbListOne.get(mParameterSet.getMapParameter().isMinEquals() ? 0 : 1).setChecked(true);
        rbListTwo.get(mParameterSet.getMapParameter().isAscending() ? 0 : 1).setChecked(true);

        findViewById(R.id.add_threshold).setOnClickListener(colorClickListener);//增加阀值

        //Spinner of Parameter
        final String[] parameters = mParameterSet.getParameterNames();
        final String[] shortNames = mParameterSet.getParameterShortNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ParamsThresholdSettingActivity.this, R.layout.simple_spinner_custom_layout, shortNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        paraChoice.setAdapter(adapter);
        paraChoice.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                mParameterSet.setMapParameter(parameters[arg2]);
                initThresholdView();//新界面
                //发送地图参数改变的广播
//				if(isInitalGps)
//				{
//					Intent intent = new Intent ();
//					intent.setAction(WalkMessage.mapParaChanged);
//					sendBroadcast(intent);
//					LogUtil.v(tag, "Param has Changed");
//				}
//				else
//				{
//					isInitalGps = true;
//				}

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        LogUtil.v("ShortAAA", mapParemeter.getId());
        paraChoice.setSelection(mParameterSet.getPositionOfParameter(mapParemeter.getId()), true);

        //Adapter of Color Spinner
        colorStrings = getResources().getStringArray(R.array.sys_map_color);
        colorAdapter = new ArrayAdapter<String>(ParamsThresholdSettingActivity.this,
                R.layout.simple_spinner_custom_layout, colorStrings);
        colorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //method two
        initColorSpinner();
    }

    private void initColorSpinner() {
        colorStrings = getResources().getStringArray(R.array.sys_map_color);
        for (int i = 0; i < colorStrings.length; i++) {
            ColorModel model = new ColorModel();
            model.colorName = colorStrings[i];
            model.color = mParameterSet.getColorOfPosition(i);
            datas.add(model);
        }
    }

    private android.widget.RadioGroup.OnCheckedChangeListener onCheckedChangeListrner = new android.widget.RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup arg0, int position) {

            switch (arg0.getId()) {
                case R.id.radioGroup1:
                    mParameterSet.getMapParameter().setMinEquals();
                    initThresholdView();
                    saveThreshold();
                    break;
                case R.id.radioGroup2:
                    soft(position);
                    saveThreshold();
                    break;

                default:
                    break;
            }
        }
    };

    private OnClickListener colorClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.SpinnerColor://修改颜色
                case R.id.img_color_select:
                    int index = Integer.parseInt(String.valueOf(v.getTag()));
                    onClickColorPickerDialog(colors.get(index));
                    break;
                case R.id.img_remove://删除阀值
                    int position = Integer.parseInt(String.valueOf(v.getTag()));
                    if (position == 0 || position == mParameterSet.getMapParameter().getThresholdList().size() - 1) {
                        return;
                    }
                    mParameterSet.delThresholdValue(position);
                    refreshView();
                    break;
                case R.id.sys_map_threshold_text://修改阀值
                    int witch = Integer.parseInt(String.valueOf(v.getTag()));
                    showThresholdChangeDialog(witch);
                    break;
                case R.id.add_threshold://增加阀值
                    showThresholdAddDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 保存对阀值的修改
     */
    private void saveThreshold() {
        this.mParameterSet.saveMapParameterThreshold();
    }

    /**
     * 参数阀值界面
     */
    private void initThresholdView() {

        Parameter mapParemeter = mParameterSet.getMapParameter();
        if (mapParemeter.getThresholdList() == null) {
            return;
        }
        int size = mapParemeter.getThresholdList().size();
        if (size == 0) {
            thresholdLayout.setVisibility(View.GONE);
            return;
        }
        thresholdLayout.setVisibility(View.VISIBLE);
        thresholdItemLayout.removeAllViews();
        colors.clear();
        thresholdValueTxtList.clear();

        for (int i = 0; i < mapParemeter.getThresholdList().size(); i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.sys_map_row, null);
            thresholdItemLayout.addView(v);
            ColorPanelView color = (ColorPanelView) v.findViewById(R.id.SpinnerColor);
            TextView valueText = (TextView) v.findViewById(R.id.sys_map_threshold_text);
            ImageView img_remove = (ImageView) v.findViewById(R.id.img_remove);
            if (i == 0 || i == mapParemeter.getThresholdList().size() - 1) {
                img_remove.setVisibility(View.INVISIBLE);
                valueText.setEnabled(false);
            } else {
                img_remove.setVisibility(View.VISIBLE);
                valueText.setEnabled(true);
            }
            ImageView img_color = (ImageView) v.findViewById(R.id.img_color_select);
            Spinner sp_color = (Spinner) v.findViewById(R.id.SpinnerColorDefault);
            CustomAdapter colorAdapter3 = new CustomAdapter(mContext, R.layout.threshold_color_row, datas);
            color.setTag(i);
            img_remove.setTag(i);
            img_color.setTag(i);
            valueText.setTag(i);
            sp_color.setTag(i);
//			sp_color.setAdapter(colorAdapter);
//			sp_color.setAdapter(colorAdapter2);
            sp_color.setAdapter(colorAdapter3);
            int selectionPosition = mParameterSet.getPositionOfColor(mapParemeter.getThresholdList().get(i).getColor());
            sp_color.setSelection(selectionPosition);
            colorAdapter3.setSelectedPosition(selectionPosition);
            sp_color.setOnItemSelectedListener(onItemSelectedListener);
            Log.d("TTT", "color:" + mapParemeter.getThresholdList().get(i).getColor());
            color.setColor(mapParemeter.getThresholdList().get(i).getColor());
            color.setOnClickListener(colorClickListener);
            img_color.setOnClickListener(colorClickListener);
            img_remove.setOnClickListener(colorClickListener);
            valueText.setOnClickListener(colorClickListener);
            colors.add(color);
            thresholdValueTxtList.add(valueText);
        }
        setThresholdValueText();
    }


    /**
     * @return
     */
    private void setThresholdValueText() {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        for (int i = 0; i < mapParemeter.getThresholdList().size(); i++) {
            thresholdValueTxtList.get(i).setText(mapParemeter.getThresholdList().get(i).getValue2Show());
        }
    }

    /**
     * 刷新界面
     */
    private void refreshView() {
        initThresholdView();
        saveThreshold();
    }

    private void soft(int position) {
        Parameter mapParameter = mParameterSet.getMapParameter();
        if (position == R.id.radio4 && !mapParameter.isAscending()) {
            return;
        }
        if (position == R.id.radio3 && mapParameter.isAscending()) {
            return;
        }
        List<Threshold> list = mapParameter.getThresholdList();
        List<Threshold> tmpList = new ArrayList<Threshold>();
        tmpList.addAll(list);
        list.clear();
        for (int i = tmpList.size() - 1; i >= 0; i--) {
            mParameterSet.getMapParameter().getThresholdList().add(tmpList.get(i));
        }
        initThresholdView();
    }

    private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {


        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
            int index = Integer.parseInt(String.valueOf(arg0.getTag()));
            mParameterSet.getMapParameter().getThresholdList().get(index).setColor(mParameterSet.getColorOfPosition(position));
            ((CustomAdapter) arg0.getAdapter()).setSelectedPosition(position);
            saveThreshold();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    };

    /**
     * 创建颜色选择器对话框<BR>
     * [功能详细描述]
     *
     * @param colorPanelView
     * @param colorKey
     */
    public void onClickColorPickerDialog(final ColorPanelView colorPanelView) {
        int initialValue = colorPanelView.getColor();
        Log.d("mColorPicker", "initial value:" + initialValue);
        final ColorPickerDialog colorDialog = new ColorPickerDialog(this, initialValue);
        colorDialog.setAlphaSliderVisible(false);
        colorDialog.builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                colorPanelView.setColor(colorDialog.getColor());
                int index = Integer.parseInt(String.valueOf(colorPanelView.getTag()));
                mParameterSet.getMapParameter().getThresholdList().get(index).setColor(colorDialog.getColor());
                saveThreshold();
            }
        });
        colorDialog.builder.setNegativeButton(android.R.string.cancel);
        colorDialog.builder.show();
    }

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		Builder builder = new Builder(this.getParent());
//		switch (id) {
//		case R.id.sys_map_threshold_text:
//			gggg
//			break;
//
//		default:
//			break;
//		}
//		return builder.create();
//	}

    /**
     * 弹出增加阀值dialog
     */
    private void showThresholdAddDialog() {
        Builder builder = new Builder(ParamsThresholdSettingActivity.this);
        View v = LayoutInflater.from(ParamsThresholdSettingActivity.this).inflate(R.layout.dialog_threshold_add_layout, null);
        final String operatorStr = mParameterSet.getMapParameter().isMinEquals() ? "<=" : ">=";
        Button button = (Button) v.findViewById(R.id.type);
        button.setText(operatorStr);
        final EditText contentEdit = (EditText) v.findViewById(R.id.value);
        Parameter parameter = mParameterSet.getMapParameter();
        int minValue = parameter.getMinimum() / parameter.getScale();
        long maxValue = parameter.getMaximum() / parameter.getScale();
        String range = minValue + " ~ " + maxValue;
        String title = getResources().getString(R.string.threshold_range) + range;
        builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(title).setView(v)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String valueString = contentEdit.getText().toString().trim();
                        if (!valueString.equals("")) {
                            int value = Integer.valueOf(valueString);
                            addThreshold(value);
                        }
                    }
                }).setNegativeButton(R.string.str_cancle);
        builder.create().show();
    }

    /**
     * 弹出修改阀值dialog
     */
    private void showThresholdChangeDialog(final int witch) {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        int scale = mapParemeter.getScale();
        Builder builder = new Builder(ParamsThresholdSettingActivity.this);
        View v = LayoutInflater.from(ParamsThresholdSettingActivity.this).inflate(R.layout.dialog_threshold_change_layout, null);

        final EditText contentEdit = (EditText) v.findViewById(R.id.value);
        contentEdit.setText(mapParemeter.getThresholdList().get(witch).getValue(mapParemeter.isAscending()) / scale + "");

        int leftValue = getLeftValue(witch) / scale;
        int rightValue = getRightValue(witch) / scale;
        String range = mapParemeter.isAscending() ? leftValue + " ~ " + rightValue : rightValue + " ~ " + leftValue;
        String title = getResources().getString(R.string.threshold_part_range) + range;

        builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(title).setView(v)
                .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String valueString = contentEdit.getText().toString().trim();
                        if (!valueString.equals("")) {
                            int value = Integer.valueOf(valueString);
                            changeThreshold(witch, value);
                        }
                    }
                }).setNegativeButton(R.string.str_cancle);
        builder.create().show();
    }

    /**
     * 添加阀值
     *
     * @param value
     */
    private void addThreshold(int value) {
        Parameter parameter = mParameterSet.getMapParameter();
        value = value * parameter.getScale();
        if (value > getMaxThresholdValue() || value < getMinThresholdValue()) {
            Toast.makeText(this, getResources().getString(R.string.threshold_out_off_range), Toast.LENGTH_SHORT).show();
            return;
        }
        mParameterSet.addThresholdValue(value, getColor2Write(getResources().getColor(R.color.orange)), parameter.getScale());
        refreshView();
    }

    /**
     * 拼成写入xml的颜色串
     */
    public String getColor2Write(int color) {
        String colorStr = Integer.toHexString(color);
        colorStr = colorStr.substring(2);
        return "#" + colorStr;
    }

    /**
     * 修改阀值
     *
     * @param witch
     * @param value
     */
    private void changeThreshold(int witch, int value) {
        Parameter parameter = mParameterSet.getMapParameter();
        value = value * parameter.getScale();
        if (!isWithin(witch, value)) {
            Toast.makeText(this, getResources().getString(R.string.threshold_out_off_range), Toast.LENGTH_SHORT).show();
            return;
        }
        mParameterSet.setMapParameterValue(witch, value);
        refreshView();
    }

    private boolean isWithin(int witch, int value) {
        int leftValue = getLeftValue(witch);
        int rightValue = getRightValue(witch);
        Parameter mapParemeter = mParameterSet.getMapParameter();
        boolean isAscending = mapParemeter.isAscending();
        int max = isAscending ? rightValue : leftValue;
        int min = isAscending ? leftValue : rightValue;
        if (value > min && value < max) {
            return true;
        }
        return false;
    }

    /**
     * 获取左值
     *
     * @return
     */
    private int getLeftValue(int position) {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        boolean isAscending = mapParemeter.isAscending();
        if (position == 0) {
            return mapParemeter.getThresholdList().get(position).getValue(isAscending);
        } else {
            return mapParemeter.getThresholdList().get(position - 1).getValue(isAscending);
        }

    }

    /**
     * 获取右值
     *
     * @return
     */
    private int getRightValue(int position) {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        boolean isAscending = mapParemeter.isAscending();
        if (position == mapParemeter.getThresholdList().size() - 1) {
            return mapParemeter.getThresholdList().get(position).getValue(isAscending);
        } else {
            return mapParemeter.getThresholdList().get(position + 1).getValue(isAscending);
        }
    }

    /**
     * 获取阀值的最大值
     *
     * @return
     */
    private int getMaxThresholdValue() {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        boolean isAscending = mapParemeter.isAscending();
        if (isAscending) {
            return mapParemeter.getThresholdList().get(mapParemeter.getThresholdList().size() - 1).getValue(isAscending);
        }
        return mapParemeter.getThresholdList().get(0).getValue(isAscending);
    }

    /**
     * 获取阀值的最小值
     *
     * @return
     */
    private int getMinThresholdValue() {
        Parameter mapParemeter = mParameterSet.getMapParameter();
        boolean isAscending = mapParemeter.isAscending();
        if (isAscending) {
            return mapParemeter.getThresholdList().get(0).getValue(isAscending);
        }
        return mapParemeter.getThresholdList().get(mapParemeter.getThresholdList().size() - 1).getValue(isAscending);
    }

    private class CustomAdapter extends ArrayAdapter<ColorModel> {

        private int selectedPosition = 0;
        private List<ColorModel> datas = new ArrayList<ColorModel>();

        public CustomAdapter(Context context, int textViewResourceId,
                             List<ColorModel> objects) {
            super(context, textViewResourceId, objects);
            datas.clear();
            datas.addAll(objects);
        }


        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            ColorModel item = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.threshold_color_row, null);
                TextView txt_color_name = (TextView) convertView.findViewById(R.id.txt_color_name);
                ColorPanelView color_view = (ColorPanelView) convertView.findViewById(R.id.SpinnerColor);
                CheckBox cb_check = (CheckBox) convertView.findViewById(R.id.cb_check);
                cb_check.setVisibility(View.VISIBLE);
                if (item != null) {
                    txt_color_name.setText(item.colorName);
                    color_view.setColor(item.color);
                }
                boolean checked = getSelectedPosition() == position;
                cb_check.setChecked(checked);
            }
            return convertView;
        }

        @Override
        public ColorModel getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(ColorModel item) {
            return super.getPosition(item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.threshold_color_row, null);
            }
            ColorModel item = getItem(position);
            TextView txt_color_name = (TextView) convertView.findViewById(R.id.txt_color_name);
            ColorPanelView color_view = (ColorPanelView) convertView.findViewById(R.id.SpinnerColor);
            CheckBox cb_check = (CheckBox) convertView.findViewById(R.id.cb_check);
            cb_check.setVisibility(View.INVISIBLE);
            if (item != null) {
                txt_color_name.setText(item.colorName);
                color_view.setColor(item.color);
            }
            return convertView;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
        }
    }

    private class ColorModel {
        public String colorName = "";
        public int color = 0;
    }
}

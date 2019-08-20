package com.walktour.gui.newmap.basestation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.gui.newmap.NewMapFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基站检索弹出窗口
 *
 * @author jianchao.wang
 */
public class BaseStationSearchPopWindow implements OnClickListener, OnTouchListener {
    /**
     * 弹出窗
     */
    private PopupWindow pop;
    //	/** 是否显示 */
    //	private boolean isShow = false;
    /**
     * 显示的坐标点X
     */
    private int x;
    /**
     * 显示的坐标点Y
     */
    private int y;
    /**
     * 移动的Y坐标
     */
    private int moveY;
    /**
     * 放置的父对象
     */
    private View parent;
    /**
     * 上下文
     */
    private Activity activity;
    /**
     * 基站数据的类型
     */
    private List<Set<BaseStation>> stationList = new ArrayList<Set<BaseStation>>();
    /**
     * 基站监听类
     */
    private StationAdapter stationAdapter;
    /**
     * 标题栏第一列
     */
    private TextView title1;
    /**
     * 标题栏第一列
     */
    private TextView title2;
    /**
     * 标题栏第一列
     */
    private TextView title3;
    /**
     * 标题栏第一列
     */
    private TextView title4;
    /**
     * 标题栏第一列
     */
    private TextView title5;
    /**
     * 检索文本
     */
    private EditText searchText;
    /**
     * 明细弹出窗口
     */
    private PopupWindow detailWindow;
    /**
     * 宽度
     */
    private int width;
    /**
     * 高度
     */
    private int height;
    /**
     * 明细列表
     */
    private List<String[]> detailList;
    /**
     * 明细适配器
     */
    private DetailAdapter detailAdapter;
    /**
     * 最后的Y坐标
     */
    private int lastY;
    /***
     * 选择的网络
     */
    private NewMapFactory factory;
    /**
     * 选择的所有网络
     **/
    private List<Integer> types = new LinkedList<>();

    @SuppressLint("InflateParams")
    public BaseStationSearchPopWindow(View parent, final Activity activity, int width, int height) {
        this.parent = parent;
        this.activity = activity;
        this.width = width;
        this.height = height;
        factory = NewMapFactory.getInstance();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        final View view = layoutInflater.inflate(R.layout.map_base_station_main_list, null);
        pop = new PopupWindow(view, width, height);
        this.pop.setFocusable(true);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setOnTouchListener(this);

        ImageButton close = (ImageButton) view.findViewById(R.id.close_btn);
        close.setOnClickListener(this);


        if (factory.getNetTypes().size() <= 0) {//如果没有选择，默认第一个
            factory.getNetTypes().add(BaseStation.NETTYPE_GSM);
        }

        final TextView netTypeTV = (TextView) view.findViewById(R.id.netTypeBtn);
        netTypeTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                types.clear();
                types.addAll(factory.getNetTypes());
                openDialogNetType(view, netTypeTV);
            }
        });
        ListView stationList = (ListView) view.findViewById(R.id.stationList);
        stationAdapter = new StationAdapter(activity, R.layout.map_base_station_main_row, this.stationList);
        stationList.setAdapter(stationAdapter);
        title1 = (TextView) view.findViewById(R.id.title1);
        title2 = (TextView) view.findViewById(R.id.title2);
        title3 = (TextView) view.findViewById(R.id.title3);
        title4 = (TextView) view.findViewById(R.id.title4);
        title5 = (TextView) view.findViewById(R.id.title5);
        this.searchText = (EditText) view.findViewById(R.id.search_content_edit);
        this.searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchStation();
            }

        });
        this.createDetailWindow();
        setHeader(view, netTypeTV);
    }


    private void openDialogNetType(final View view, final TextView netTypeTV) {
        final String[] hobbies = {"GSM", "WCDMA", "CDMA", "TD-SCDMA", "LTE"};
        boolean[] checkItems = new boolean[]{false, false, false, false, false};
        checkItems[0] = factory.getNetTypes().contains(BaseStation.NETTYPE_GSM);
        checkItems[1] = factory.getNetTypes().contains(BaseStation.NETTYPE_WCDMA);
        checkItems[2] = factory.getNetTypes().contains(BaseStation.NETTYPE_CDMA);
        checkItems[3] = factory.getNetTypes().contains(BaseStation.NETTYPE_TDSCDMA);
        checkItems[4] = factory.getNetTypes().contains(BaseStation.NETTYPE_LTE);

        new BasicDialog.Builder(activity)
                .setTitle(R.string.sc_taskcw_netchoice)
                .setMultiChoiceItems(hobbies, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            types.add(which + 1);
                        } else {
                            types.remove((Integer) (which + 1));
                        }
                    }
                }).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                factory.getNetTypes().clear();
                factory.getNetTypes().addAll(types);
                setHeader(view, netTypeTV);
            }
        }).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

    /**
     * 创建明细弹出窗口
     */
    @SuppressLint("InflateParams")
    private void createDetailWindow() {
        String inflater = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater vi = (LayoutInflater) this.activity.getSystemService(inflater);
        View view = vi.inflate(R.layout.map_base_station_detail_list, null, true);
        detailWindow = new PopupWindow(view, this.width - 60, this.height * 2, true);
        ListView layout = (ListView) view.findViewById(R.id.detailList);
        this.detailList = new ArrayList<String[]>();
        detailAdapter = new DetailAdapter(this.activity, R.layout.map_base_station_detail_row, this.detailList);
        layout.setAdapter(detailAdapter);
        detailWindow.setFocusable(true);
        view.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    detailWindow.dismiss();
                return false;
            }
        });
    }


    /**
     * 基站列表适配器
     *
     * @author jianchao.wang
     */
    private class StationAdapter extends ArrayAdapter<Set<BaseStation>> {

        /**
         * 资源ID
         */
        private int resourceId;

        public StationAdapter(Context context, int textViewResourceId, List<Set<BaseStation>> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                view = vi.inflate(resourceId, null, true);
            }
            view.setOnClickListener(BaseStationSearchPopWindow.this);
            LinearLayout listLayout = (LinearLayout) view.findViewById(R.id.detailList);
            listLayout.removeAllViews();
            Set<BaseStation> set = this.getItem(position);
            int netType = factory.getSearchNetType();
            for (BaseStation base : set) {
                TextView cellName = (TextView) view.findViewById(R.id.cell_name);
                cellName.setText(base.name);
                view.setTag(base);
                int details = base.details.size();
                String[][] values = new String[details][4];
                for (int i = 0; i < details; i++) {
                    BaseStationDetail detail = base.details.get(i);
                    switch (netType) {
                        case BaseStation.NETTYPE_GSM:
                            values[i] = new String[]{detail.bcch, detail.bsic, detail.lac, detail.cellId};
                            break;
                        case BaseStation.NETTYPE_CDMA:
                            values[i] = new String[]{detail.frequency, detail.pn, detail.evFreq, detail.evPn};
                            break;
                        case BaseStation.NETTYPE_WCDMA:
                            values[i] = new String[]{detail.uarfcn, detail.psc, detail.lac, detail.cellId};
                            break;
                        case BaseStation.NETTYPE_TDSCDMA:
                            values[i] = new String[]{detail.uarfcn, detail.cpi, detail.lac, detail.cellId};
                            break;
                        case BaseStation.NETTYPE_LTE:
                            values[i] = new String[]{detail.earfcn, detail.pci, detail.main.enodebId, detail.sectorId};
                            break;
                    }
                }
                for (int i = 0; i < details; i++) {
                    String inflater = Context.LAYOUT_INFLATER_SERVICE;
                    LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                    View detail = vi.inflate(R.layout.map_base_station_main_row1, null, true);
                    TextView column1 = (TextView) detail.findViewById(R.id.column1);
                    TextView column2 = (TextView) detail.findViewById(R.id.column2);
                    TextView column3 = (TextView) detail.findViewById(R.id.column3);
                    TextView column4 = (TextView) detail.findViewById(R.id.column4);
                    ImageButton button = (ImageButton) detail.findViewById(R.id.detail_btn);
                    button.setTag(base.details.get(i));
                    button.setOnClickListener(BaseStationSearchPopWindow.this);
                    column1.setText(values[i][0]);
                    column2.setText(values[i][1]);
                    column3.setText(values[i][2]);
                    column4.setText(values[i][3]);
                    listLayout.addView(detail);
                }
            }
            return view;
        }
    }

    /***
     * 设置头部切换窗口
     */
    private void setHeader(final View view, final TextView netTypeTV) {
        final List<Integer> netTypes = factory.getNetTypes();
        final TextView tv11 = (TextView) view.findViewById(R.id.title11);
        final TextView tv12 = (TextView) view.findViewById(R.id.title22);
        final TextView tv13 = (TextView) view.findViewById(R.id.title33);
        final TextView tv14 = (TextView) view.findViewById(R.id.title44);
        final TextView tv15 = (TextView) view.findViewById(R.id.title55);
        final TextView tv16 = (TextView) view.findViewById(R.id.title66);
        final TextView tv17 = (TextView) view.findViewById(R.id.title77);
        tv11.setVisibility(View.GONE);
        tv11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv11.setTextColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv13.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv14.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv15.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv16.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv17.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_GSM);
                setTitle();
                searchStation();
            }
        });
        tv12.setVisibility(View.GONE);
        tv12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv12.setTextColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv11.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv14.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv15.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv16.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv17.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_WCDMA);
                setTitle();
                searchStation();
            }
        });
        tv13.setVisibility(View.GONE);
        tv13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv13.setTextColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv11.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv14.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv15.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv16.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv17.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_CDMA);
                setTitle();
                searchStation();
            }
        });
        tv14.setVisibility(View.GONE);
        tv14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv14.setTextColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv11.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv13.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv15.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv16.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv17.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_TDSCDMA);
                setTitle();
                searchStation();
            }
        });
        tv15.setVisibility(View.GONE);
        tv15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv15.setTextColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv11.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv13.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv14.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv16.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv17.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_LTE);
                setTitle();
                searchStation();
            }
        });
        tv16.setVisibility(View.GONE);
        tv16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv16.setTextColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv11.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv13.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv14.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv15.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv17.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_NBIOT);
                setTitle();
                searchStation();
            }
        });
        tv17.setVisibility(View.GONE);
        tv17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv11.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv12.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv13.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv14.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv15.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv16.setBackgroundColor(activity.getResources().getColor(R.color.white));
                tv17.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                tv17.setTextColor(activity.getResources().getColor(R.color.white));
                tv12.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv11.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv13.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv14.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv15.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                tv16.setTextColor(activity.getResources().getColor(R.color.app_main_text_color));
                factory.setSearchNetType(BaseStation.NETTYPE_CAT_M);
                setTitle();
                searchStation();
            }
        });


        //默认是第一个
        if (netTypes.size() <= 0) {
            factory.getNetTypes().add(BaseStation.NETTYPE_GSM);
        }
        factory.setSearchNetType(netTypes.get(0));
        for (Integer i : netTypes) {
            switch (i) {
                case BaseStation.NETTYPE_GSM:
                    tv11.setVisibility(View.VISIBLE);
                    if (factory.getSearchNetType() == i) {
                        tv11.setTextColor(activity.getResources().getColor(R.color.white));
                        tv11.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                    }
                    break;
                case BaseStation.NETTYPE_WCDMA:
                    tv12.setVisibility(View.VISIBLE);
                    if (factory.getSearchNetType() == i) {
                        tv12.setTextColor(activity.getResources().getColor(R.color.white));
                        tv12.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                    }
                    break;
                case BaseStation.NETTYPE_CDMA:
                    tv13.setVisibility(View.VISIBLE);
                    if (factory.getSearchNetType() == i) {
                        tv13.setTextColor(activity.getResources().getColor(R.color.white));
                        tv13.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                    }
                    break;
                case BaseStation.NETTYPE_TDSCDMA:
                    tv14.setVisibility(View.VISIBLE);
                    if (factory.getSearchNetType() == i) {
                        tv14.setTextColor(activity.getResources().getColor(R.color.white));
                        tv14.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                    }
                    break;
                case BaseStation.NETTYPE_LTE:
                    tv15.setVisibility(View.VISIBLE);
                    if (factory.getSearchNetType() == i) {
                        tv15.setTextColor(activity.getResources().getColor(R.color.white));
                        tv15.setBackgroundColor(activity.getResources().getColor(R.color.search_btn));
                    }
                    break;
                default:
                    break;
            }
        }
        setTitle();
        searchStation();
    }


    private void setNetTypeTv(TextView netTypeTv) {
        List<Integer> netTypes = factory.getNetTypes();
        for (int i = 1; i < netTypes.size() + 1; i++) {
            if (i == 1) {
                netTypeTv.setText(getNetString(netTypes.get(i - 1)));
            } else {
                netTypeTv.setText(netTypeTv.getText() + "|" + getNetString(netTypes.get(i - 1)));
            }
        }
    }

    /***
     * 获取网络字符串
     * @param netType
     * @return
     */
    private String getNetString(Integer netType) {
        switch (netType) {
            case BaseStation.NETTYPE_GSM:
                return "GSM";
            case BaseStation.NETTYPE_CDMA:
                return "CDMA";
            case BaseStation.NETTYPE_WCDMA:
                return "WCDMA";
            case BaseStation.NETTYPE_TDSCDMA:
                return "TDSCDMA";
            case BaseStation.NETTYPE_LTE:
                return "LTE";
        }
        return "";
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        this.title1.setText("Cell Name");
        int netType = factory.getSearchNetType();
        switch (netType) {
            case BaseStation.NETTYPE_GSM:
                this.title2.setText("BCCH");
                this.title3.setText("BSIC");
                this.title4.setText("LAC");
                this.title5.setText("Cell ID");
                break;
            case BaseStation.NETTYPE_CDMA:
                this.title2.setText("Frequency");
                this.title3.setText("PN");
                this.title4.setText("EV Freq");
                this.title5.setText("EV PN");
                break;
            case BaseStation.NETTYPE_WCDMA:
                this.title2.setText("UARFCN");
                this.title3.setText("PSC");
                this.title4.setText("LAC");
                this.title5.setText("Cell ID");
                break;
            case BaseStation.NETTYPE_TDSCDMA:
                this.title2.setText("UARFCN");
                this.title3.setText("CPI");
                this.title4.setText("LAC");
                this.title5.setText("Cell ID");
                break;
            case BaseStation.NETTYPE_LTE:
                this.title2.setText("EARFCN");
                this.title3.setText("PCI");
                this.title4.setText("eNodeB ID");
                this.title5.setText("Sector ID");
                break;
        }
    }

    /**
     * 设置显示位置
     *
     * @param location
     */
    public void setLocation(int[] location) {
        this.x = location[0];
        this.y = location[1];
    }

    /**
     * 检索基站
     */
    private void searchStation() {
        this.stationList.clear();
        int latlngType = 2;
        if (ParameterSetting.getInstance().getDtDefaultMap().equals(this.activity.getResources().getStringArray(R.array.sys_dtmap_default)[1]))
            latlngType = 1;
        int netType = factory.getSearchNetType();
        List<BaseStation> list = BaseStationDBHelper.getInstance(this.activity).queryBaseStation(netType, latlngType,
                BaseStation.MAPTYPE_OUTDOOR);
        String keyword = this.searchText.getText().toString().trim();
        Map<String, Set<BaseStation>> map = new HashMap<String, Set<BaseStation>>();
        for (BaseStation base : list) {
            for (BaseStationDetail detail : base.details) {
                String[] values = null;
                switch (netType) {
                    case BaseStation.NETTYPE_GSM:
                        values = new String[]{base.name, detail.bcch, detail.bsic, detail.lac, detail.cellId};
                        break;
                    case BaseStation.NETTYPE_WCDMA:
                        values = new String[]{base.name, detail.uarfcn, detail.psc, detail.lac, detail.cellId};
                        break;
                    case BaseStation.NETTYPE_CDMA:
                        values = new String[]{base.name, detail.pn, detail.frequency, detail.evPn, detail.evFreq};
                        break;
                    case BaseStation.NETTYPE_TDSCDMA:
                        values = new String[]{base.name, detail.uarfcn, detail.cpi, detail.lac, detail.cellId};
                        break;
                    case BaseStation.NETTYPE_LTE:
                        values = new String[]{base.name, detail.earfcn, detail.pci, detail.main.enodebId, detail.sectorId};
                        break;
                }
                if (this.checkKeyword(keyword, values)) {
                    String key = base.longitude + "," + base.latitude;
                    if (map.containsKey(key)) {
                        map.get(key).add(base);
                    } else {
                        Set<BaseStation> set = new HashSet<BaseStation>();
                        set.add(base);
                        map.put(key, set);
                    }
                }
            }
        }
        this.stationList.addAll(map.values());
        this.stationAdapter.notifyDataSetChanged();
    }

    /**
     * 检查关键字是否存在指定数值中
     *
     * @param keyword
     * @param values
     * @return
     */
    private boolean checkKeyword(String keyword, String[] values) {
        if (keyword == null || keyword.trim().length() == 0)
            return true;
        for (String value : values) {
            if (value != null && value.indexOf(keyword) >= 0)
                return true;
        }
        return false;
    }

    /**
     * 显示弹出窗
     */
    public void show() {
        //		if (this.isShow)
        //			return;
        pop.showAtLocation(this.parent, Gravity.TOP | Gravity.LEFT, this.x, this.y);
        this.setTitle();
        this.searchStation();
        //		this.isShow = true;
    }

    /**
     * 关闭弹出窗
     */
    public void close() {
        this.moveY = 0;
        this.pop.dismiss();
        //		this.isShow = false;
    }

    /**
     * 弹出显示明细窗口
     *
     * @param detail 详情
     */
    private void showDetailWindow(BaseStationDetail detail) {
        this.fillBaseData(detail);
        this.detailWindow.showAtLocation(this.parent, Gravity.TOP | Gravity.LEFT, this.x + 30, this.y);
    }

    /**
     * 填充基站参数<BR>
     * [功能详细描述]
     *
     * @param detail
     */
    private void fillBaseData(BaseStationDetail detail) {
        int[] colNames = null;
        String[] colValues = null;
        switch (detail.main.netType) {
            case BaseStation.NETTYPE_GSM:
                colNames = new int[]{R.string.base_detail_site_name, R.string.base_detail_cell_id,
                        R.string.base_detail_cell_name, R.string.base_detail_longitude, R.string.base_detail_latitude,
                        R.string.base_detail_lac, R.string.base_detail_bcch, R.string.base_detail_bsic, R.string.base_detail_azimuth};
                colValues = new String[]{detail.main.name, detail.cellId, detail.cellName,
                        String.valueOf(detail.main.longitude), String.valueOf(detail.main.latitude), detail.lac, detail.bcch,
                        detail.bsic, String.valueOf(detail.bearing)};
                break;
            case BaseStation.NETTYPE_WCDMA:
                colNames = new int[]{R.string.base_detail_site_name, R.string.base_detail_cell_id,
                        R.string.base_detail_cell_name, R.string.base_detail_longitude, R.string.base_detail_latitude,
                        R.string.base_detail_lac, R.string.base_detail_uarfcn, R.string.base_detail_psc, R.string.base_detail_azimuth};
                colValues = new String[]{detail.main.name, detail.cellId, detail.cellName,
                        String.valueOf(detail.main.longitude), String.valueOf(detail.main.latitude), detail.lac, detail.uarfcn,
                        detail.psc, String.valueOf(detail.bearing)};
                break;
            case BaseStation.NETTYPE_TDSCDMA:
                colNames = new int[]{R.string.base_detail_site_name, R.string.base_detail_cell_id,
                        R.string.base_detail_cell_name, R.string.base_detail_longitude, R.string.base_detail_latitude,
                        R.string.base_detail_lac, R.string.base_detail_uarfcn, R.string.base_detail_cpi, R.string.base_detail_azimuth};
                colValues = new String[]{detail.main.name, detail.cellId, detail.cellName,
                        String.valueOf(detail.main.longitude), String.valueOf(detail.main.latitude), detail.lac, detail.uarfcn,
                        detail.cpi, String.valueOf(detail.bearing)};
                break;
            case BaseStation.NETTYPE_LTE:
                colNames = new int[]{R.string.base_detail_site_name, R.string.base_detail_cell_name,
                        R.string.base_detail_longitude, R.string.base_detail_latitude, R.string.base_detail_earfcn,
                        R.string.base_detail_pci, R.string.base_detail_enodeb_id, R.string.base_detail_sector_id,
                        R.string.base_detail_azimuth};
                colValues = new String[]{detail.main.name, detail.cellName, String.valueOf(detail.main.longitude),
                        String.valueOf(detail.main.latitude), detail.earfcn, detail.pci, detail.main.enodebId, detail.sectorId,
                        String.valueOf(detail.bearing)};
                break;
            default:
                colNames = new int[]{R.string.base_detail_site_name, R.string.base_detail_cell_name,
                        R.string.base_detail_longitude, R.string.base_detail_latitude, R.string.base_detail_pn,
                        R.string.base_detail_frequency, R.string.base_detail_ev_pn, R.string.base_detail_ev_freq,
                        R.string.base_detail_bid, R.string.base_detail_nid, R.string.base_detail_sid, R.string.base_detail_azimuth};
                colValues = new String[]{detail.main.name, detail.cellName, String.valueOf(detail.main.longitude),
                        String.valueOf(detail.main.latitude), detail.pn, detail.frequency, detail.evPn, detail.evFreq, detail.bid,
                        detail.nid, detail.sid, String.valueOf(detail.bearing)};
                break;
        }
        this.detailList.clear();
        for (int i = 0; i < colNames.length; i++) {
            String[] value = new String[2];
            value[0] = this.activity.getResources().getString(colNames[i]);
            value[1] = colValues[i];
            this.detailList.add(value);
        }
        this.detailAdapter.notifyDataSetChanged();
    }

    /**
     * 明细适配类
     *
     * @author jianchao.wang
     */
    private class DetailAdapter extends ArrayAdapter<String[]> {
        /**
         * 资源ID
         */
        private int resourceId;

        public DetailAdapter(Context context, int textViewResourceId, List<String[]> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null)
                view = convertView;
            else {
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                view = vi.inflate(resourceId, null, true);
            }
            String[] value = this.getItem(position);
            TextView columnName = (TextView) view.findViewById(R.id.column_name);
            columnName.setText(value[0]);
            TextView columnValue = (TextView) view.findViewById(R.id.column_value);
            columnValue.setText(value[1]);
            return view;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close_btn) {
            this.close();
        } else if (view.getId() == R.id.detail_btn) {
            BaseStationDetail detail = (BaseStationDetail) view.getTag();
            this.showDetailWindow(detail);
        } else {
            BaseStation baseData = (BaseStation) view.getTag();
            RefreshEventManager.notifyRefreshEvent(RefreshType.REFRSH_GOOGLEMAP_BASEDATA, baseData);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = (int) event.getRawY() - lastY;
                pop.update(x, y + moveY + dy, -1, -1);
                break;
            case MotionEvent.ACTION_UP:
                moveY += (int) event.getRawY() - lastY;
                break;
        }
        return true;
    }

}

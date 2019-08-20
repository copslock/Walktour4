package com.walktour.gui.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TabHostUtil;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.control.config.PageManager;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.ui.BasicTabActivity;
import com.walktour.framework.view.draganddropgridview.CoolDragAndDropGridView;
import com.walktour.framework.view.draganddropgridview.CoolDragAndDropGridView.DragAndDropListener;
import com.walktour.framework.view.draganddropgridview.Item;
import com.walktour.framework.view.draganddropgridview.ItemAdapter;
import com.walktour.framework.view.draganddropgridview.ItemAdapter.DeleteItemCallBack;
import com.walktour.framework.view.draganddropgridview.SpanVariableGridView;
import com.walktour.gui.R;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 参数设置界面
 *
 * @author jianchao.wang
 */
public class ParamsSettingActivity extends BasicTabActivity implements SpanVariableGridView.OnItemClickListener, OnClickListener, DeleteItemCallBack {

    private ParameterSetting mParameterSet;
    private List<ItemAdapter> adapterList = new ArrayList<ItemAdapter>();
    private List<CoolDragAndDropGridView> gridviewList = new ArrayList<CoolDragAndDropGridView>();
    private List<List<Item>> itemsList = new ArrayList<List<Item>>();
    private List<String> networkTypeList = new ArrayList<String>();
    private String networkType = "";
    private LinearLayout detailLayout;
    private LayoutInflater mLayoutInflater;
    private List<Parameter> allParameters;
    private RelativeLayout addParamLayout;

    private TabHost tabHost;
    private static int CURRENT_TAB = 0;// 选中第1个标签


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.params_setting);
        mParameterSet = ParameterSetting.getInstance();
        mParameterSet.initMapLocusShape(this);
        mLayoutInflater = LayoutInflater.from(this);
        initView();
        init();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    public void initTabHost() {
        if (tabHost==null){
            tabHost=getTabHost();
        }
        for (int i = 0; i < networkTypeList.size(); i++) {
            String name = networkTypeList.get(i);
            View tabView = createTab(name);
            tabHost.addTab(tabHost.newTabSpec(name).setIndicator(tabView).setContent(new Intent(this,SysFtp.class)));
        }
        tabHost.setCurrentTab(CURRENT_TAB);
        updateTab(tabHost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTab(tabHost);
                networkType = tabId;
                initParamsData(networkType);
            }

        });
    }

    /**
     * 生成tab页面
     * <p>
     * 内容
     */
    protected View createTab(String name) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabmini, null);
        TextView tvTab = (TextView) view.findViewById(R.id.tv_title);
        tvTab.setText(name);
        return view;
    }

    /**
     * 更新字体颜色
     */
    private void updateTab(TabHost tabHost) {
        TabHostUtil.updateTab(this, tabHost);
    }

    @Override
    public void onBackPressed() {
        saveChange();
        super.onBackPressed();
    }

    private void saveChange() {
        mParameterSet.saveMapParametersByOrder(allParameters);
        mParameterSet.initialParameter();
        ApplicationModel.getInstance().setParmDragBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initParamsData(networkType);
    }

    private void init() {
        allParameters = mParameterSet.getParameters();
        ArrayList<ShowInfoType> pageList = new PageManager(getApplicationContext(), false).getShowInfoList();
        for (int i = 0; i < pageList.size(); i++) {
            pageList.get(i).getNetType();
            String networkType = pageList.get(i).getNetGroup().name();
            if (!networkType.equals(NetType.Normal.name()) && !networkTypeList.contains(networkType)) {
                networkTypeList.add(networkType);
            }
        }
        initTabHost();
        if (networkTypeList.size() > 0) {
            networkType = networkTypeList.get(0);
            initParamsData(networkTypeList.get(0));
        }
    }


    @SuppressLint("InflateParams")
    private void initParamsData(String networkType) {
        detailLayout.removeAllViews();
        gridviewList.clear();
        adapterList.clear();
        Parameter[] parameters = mParameterSet.getTableParametersByNetworkType(networkType);
        itemsList.clear();
        final List<Item> voiceItems = new LinkedList<Item>();
        final List<Item> dataItems = new LinkedList<Item>();
        final List<Item> systemItems = new LinkedList<Item>();
        final List<Item> otherItems = new LinkedList<Item>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter item = parameters[i];
            int spans = item.isSingleLine() ? 2 : 1;
            if (item.getTaskType() == 1) {
                voiceItems.add(new Item(R.drawable.remove, spans, item.getShowName(), item));
            } else if (item.getTaskType() == 2) {
                dataItems.add(new Item(R.drawable.remove, spans, item.getShowName(), item));
            } else if (item.getTaskType() == 3) {
                systemItems.add(new Item(R.drawable.remove, spans, item.getShowName(), item));
            } else {
                otherItems.add(new Item(R.drawable.remove, spans, item.getShowName(), item));
            }
        }
        if (voiceItems.size() > 0) {
            itemsList.add(voiceItems);
        }
        if (dataItems.size() > 0) {
            itemsList.add(dataItems);
        }
        if (systemItems.size() > 0) {
            itemsList.add(systemItems);
        }
        if (otherItems.size() > 0) {
            itemsList.add(otherItems);
        }
        addParamLayout.setVisibility(itemsList.size() > 0 ? View.GONE : View.VISIBLE);
        for (int j = 0; j < itemsList.size(); j++) {
            View v = mLayoutInflater.inflate(R.layout.params_setting_detail_item, null);
            detailLayout.addView(v);
            v.findViewById(R.id.voice_add).setOnClickListener(clickListener);
            TextView title = (TextView) v.findViewById(R.id.txt_title);
            setTitle(title, itemsList.get(j).get(0).getParameter().getTaskType());

            final ItemAdapter itemAdapter = new ItemAdapter(this, itemsList.get(j));
            itemAdapter.setDeleteItemCallBack(this);
            adapterList.add(itemAdapter);
            final CoolDragAndDropGridView gridView = (CoolDragAndDropGridView) v.findViewById(R.id.coolDragAndDropGridView);
            gridviewList.add(gridView);
            gridView.setAdapter(itemAdapter);
            gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    gridView.startDragAndDrop();
                    return false;
                }

            });
            final List<Item> items = itemsList.get(j);
            gridView.setDragAndDropListener(new DragAndDropListener() {

                @Override
                public void onDropItem(int from, int to) {

                    if (from != to) {
                        Parameter fromParameter = items.get(from).getParameter();
                        Parameter toParameter = items.get(to).getParameter();
                        changePosition(fromParameter, toParameter, from - to);
                        items.add(to, items.remove(from));
                        itemAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onDraggingItem(int from, int to) {

                }

                @Override
                public void onDragItem(int from) {

                }

                @Override
                public boolean isDragAndDropEnabled(int position) {
                    return true;
                }
            });
        }

    }

    /**
     * 改变位置
     *
     * @param parameter       改变的参数
     * @param parameterBehind 移动到那个参数的后面
     * @param direction       移动方向
     */
    private void changePosition(Parameter parameter, Parameter parameterBehind, int direction) {
        System.out.println("direction:" + direction);
        System.out.println("from:[" + parameter.getShowName() + ":" + parameter.getKey() + "]----to:[" + parameterBehind.getShowName() + ":" + parameterBehind.getKey() + "]");
        for (int i = 0; i < allParameters.size(); i++) {
            if (allParameters.get(i).getKey().equals(parameter.getKey())) {
                allParameters.remove(i);
                break;
            }
        }
        for (int i = 0; i < allParameters.size(); i++) {
            if (allParameters.get(i).getKey().equals(parameterBehind.getKey())) {
                if (direction > 0) {//向上移动
                    allParameters.add(i, parameter);
                } else {
                    allParameters.add(i + 1, parameter);
                }
                break;
            }
        }
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(ParamsSettingActivity.this, ParamsListActivity.class);
            intent.putExtra("networkType", networkType);
            startActivityForResult(intent, 1);
        }
    };

    private void setTitle(TextView tv, int type) {
        if (type == 1) {
            tv.setText("Voice");
        } else if (type == 2) {
            tv.setText("Data");
        } else if (type == 3) {
            tv.setText("System");
        } else {
            tv.setText("Other");
        }
    }

    private void initTopbar() {
        TextView title = initTextView(R.id.title_txt);
        title.setText(getResources().getString(R.string.parameter_setting));
        ImageView iv = initImageView(R.id.pointer);
        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                saveChange();
                finish();
            }
        });
    }

    private void initView() {
        initTopbar();
        addParamLayout = (RelativeLayout) findViewById(R.id.add_param);
        addParamLayout.setVisibility(View.GONE);
        findViewById(R.id.img_add).setOnClickListener(clickListener);//添加参数
        detailLayout = (LinearLayout) findViewById(R.id.params_setting_detail);

    }


    private void refreshView() {
        notifyAllAdapterChanged();
    }

    private void notifyAllAdapterChanged() {
        for (int i = 0; i < adapterList.size(); i++) {
            adapterList.get(i).notifyDataSetChanged();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        for (int i = 0; i < adapterList.size(); i++) {
            adapterList.get(i).notifyDataSetInvalidated();
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

    @Override
    public void delete(Item item) {
        refreshView();
        item.getParameter().setDynamicPara(false);
        mParameterSet.saveMapParameter(item.getParameter());
    }
}

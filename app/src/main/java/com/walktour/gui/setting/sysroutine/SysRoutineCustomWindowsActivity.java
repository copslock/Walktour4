package com.walktour.gui.setting.sysroutine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.dragsortlistview.DragSortListView;
import com.walktour.framework.view.dragsortlistview.DragSortListView.RemoveListener;
import com.walktour.gui.R;
import com.walktour.gui.setting.ParamsSettingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 常规设置自定义窗口设置
 *
 * @author jianchao.wang
 */
public class SysRoutineCustomWindowsActivity extends BasicActivity {
    /**
     * 参数列表视图
     */
    private DragSortListView paramListView;
    /**
     * 列表
     */
    private ArrayAdapter<String> adapter;
    /**
     * 参数存储
     */
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_routine_setting_custom_window);
        mPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        findViewById(R.id.btn_base_params_setting).setOnClickListener(clickListener);
        paramListView = (DragSortListView) findViewById(R.id.param_listview);
        List<String> win = this.getAllWindows();
        List<String> paramText = new ArrayList<String>();
        String[] paramTextArray = new String[win.size()];
        for (int i = 0; i < win.size(); i++) {
            String name = win.get(i).split(":")[0];
            int position = mPreferences.getInt("SORT_" + getTab(name), i);
            paramTextArray[position] = name;
        }
        for (int i = 0; i < paramTextArray.length; i++) {
            paramText.add(paramTextArray[i]);
        }
        adapter = new ArrayAdapter<String>(this, R.layout.list_item_checkable, R.id.text, paramText);
        paramListView.setAdapter(adapter);
        DragSortListView list = getListView();

        for (int i = 0; i < paramText.size(); i++) {
            boolean ischecked = mPreferences.getBoolean("display_" + getTab(paramText.get(i)), true);
            list.setItemChecked(i, ischecked);
        }
        list.setDropListener(onDrop);
        list.setRemoveListener(onRemove);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.text);
                mPreferences
                        .edit()
                        .putBoolean("display_" + getTab(checkedTextView.getText().toString()),
                                !mPreferences.getBoolean("display_" + getTab(checkedTextView.getText().toString()), true)).commit();
            }
        });
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_base_params_setting:
                    jumpActivity(ParamsSettingActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                DragSortListView list = getListView();
                String item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
                list.moveCheckState(from, to);
                for (int i = 0; i < adapter.getCount(); i++) {
                    String item2 = adapter.getItem(i);
                    mPreferences.edit().putInt("SORT_" + getTab(item2), adapter.getPosition(item2)).commit();
                }
            }
        }
    };

    private RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            DragSortListView list = getListView();
            String item = adapter.getItem(which);
            adapter.remove(item);
            list.removeCheckState(which);
        }
    };


    /**
     * 获取所有窗口
     *
     * @return
     * @author MSI
     */
    private List<String> getAllWindows() {
        List<String> windows = new ArrayList<String>();
        String[] tmps = getResources().getStringArray(R.array.array_custom_window);
        for (int i = 0; i < tmps.length; i++) {
            String str = tmps[i] + ":" + "tab" + i;
            windows.add(str);
        }
        return windows;
    }

    /**
     * 获取对应的标签
     *
     * @param name
     * @return
     */
    private String getTab(String name) {
        String tab = "";
        List<String> list = this.getAllWindows();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(name)) {
                return list.get(i).split(":")[1];
            }
        }
        return tab;
    }

    /**
     * 获得列表视图
     *
     * @return
     */
    private DragSortListView getListView() {
        return paramListView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(SysRoutineActivity.SHOW_MAIN_TAB);
            this.sendBroadcast(intent);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}

package com.walktour.gui.newmap2.gis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.framework.database.model.BaseStation;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GisMapListActivity extends BasicActivity {

    @BindView(R.id.lv_station)
    ListView mLvStation;
    @BindView(R.id.tv_empty)
    View mEmptyView;

    private ArrayList<BaseStation> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_map_list);
        ButterKnife.bind(this);
        List<BaseStation> data = GisMapDataHolder.get().getData();
        if (data != null) {
            mDatas.addAll(data);
        }
        mLvStation.setEmptyView(mEmptyView);
        mLvStation.setAdapter(new GisListAdapter());
        mLvStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseStation baseStation = mDatas.get(position);
                Intent intent = new Intent(GisMapListActivity.this, GisMapDetailActivity.class);
                intent.putExtra(GisMapDetailActivity.EXTRA_STATION, baseStation);
                startActivity(intent);
            }
        });
    }


    @OnClick(R.id.ib_back)
    void back() {
        finish();
    }

    class GisListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public BaseStation getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler hodler = null;
            if (convertView == null) {
                convertView = View.inflate(GisMapListActivity.this, R.layout.item_gis_station_list, null);
                hodler = new ViewHodler();
                hodler.name = (TextView) convertView.findViewById(R.id.tv_station_name);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }

            hodler.name.setText(getItem(position).name);

            return convertView;
        }

        class ViewHodler {
            public TextView name;

        }
    }
}

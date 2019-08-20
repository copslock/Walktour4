package com.walktour.gui.newmap.innsmap.fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.innsmap.InnsMap.INNSMapSDKResource;
import com.innsmap.InnsMap.net.http.domain.net.NetAccreditCityBean;
import com.innsmap.InnsMap.net.http.domain.net.NetBuildingDetailBean;
import com.innsmap.InnsMap.net.http.domain.net.NetBuildingDetailFloorBean;
import com.innsmap.InnsMap.net.http.domain.net.NetCityBuildingBean;
import com.innsmap.InnsMap.net.http.listener.forout.NetAccreditCityListener;
import com.innsmap.InnsMap.net.http.listener.forout.NetBuildingDetailListener;
import com.innsmap.InnsMap.net.http.listener.forout.NetCityBuildingListener;
import com.walktour.gui.R;
import com.walktour.gui.StartDialog;
import com.walktour.gui.newmap.innsmap.InnsmapSelectActivity;
import com.walktour.service.innsmap.InnsmapFactory;
import com.walktour.service.innsmap.model.InnsmapModel;
import com.walktour.service.innsmap.model.InnsmapModel.Type;

import java.util.ArrayList;
import java.util.List;
/**
 * 寅时室内打点选择界面
 *
 * @author jianchao.wang
 */
public class InnsmapSelectFragment extends Fragment implements OnItemClickListener, OnClickListener {
    /**
     * 数据类型
     */
    private Type mType;
    /**
     * 对象列表
     */
    private List<InnsmapModel> mModels = new ArrayList<InnsmapModel>();
    /**
     * 对象列表
     */
    private List<InnsmapModel> mFilterList = new ArrayList<InnsmapModel>();
    /**
     * 列表适配器
     */
    private InnsmapAdapter mAdapter;
    /**
     * 工厂类
     */
    private InnsmapFactory mFactory;
    /**
     * 检索文件框
     */
    private EditText mSearchText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mFactory = InnsmapFactory.getInstance(getActivity());
        View view = inflater.inflate(R.layout.fragment_innsmap_select, container, false);
        int typeId = this.getArguments().getInt("typeId");
        this.mType = Type.valueOf(typeId);
        this.findView(view);
        return view;
    }
    /**
     * 视图设置
     *
     * @param view
     */
    private void findView(View view) {
        TextView title = (TextView) view.findViewById(R.id.title_txt);
        switch (this.mType) {
            case City:
                title.setText(R.string.innsmap_select_city);
                getAllCity();
                break;
            case Building:
                title.setText(R.string.innsmap_select_building);
                this.getAllBuilding();
                break;
            case Floor:
                title.setText(R.string.innsmap_select_floor);
                this.getAllFloor();
                break;
        }
        this.mFilterList.addAll(this.mModels);
        ImageButton pointer = (ImageButton) view.findViewById(R.id.pointer);
        pointer.setOnClickListener(this);
        ListView list = (ListView) view.findViewById(R.id.select_list);
        this.mAdapter = new InnsmapAdapter(this.getActivity(), R.layout.fragment_innsmap_select_row, this.mFilterList);
        list.setAdapter(this.mAdapter);
        list.setOnItemClickListener(this);
        mSearchText = (EditText) view.findViewById(R.id.search_content_edit);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filterList();
            }
        });
    }
    /**
     * 过滤列表
     */
    private void filterList() {
        String keyword = this.mSearchText.getText().toString().trim();
        this.mFilterList.clear();
        if (keyword.length() > 0) {
            for (InnsmapModel model : this.mModels) {
                if (model.getName().indexOf(keyword) >= 0)
                    this.mFilterList.add(model);
            }
        } else {
            this.mFilterList.addAll(this.mModels);
        }
        this.mAdapter.notifyDataSetChanged();
    }
    /**
     * 获取指定所有城市
     */
    private void getAllCity() {
        final InnsmapSelectActivity activity = (InnsmapSelectActivity) this.getActivity();
        activity.showProgressDialog(this.getString(R.string.innsmap_refresh_list));
        INNSMapSDKResource.getAccreditCity(new NetAccreditCityListener() {
            @Override
            public void onSuccess(List<NetAccreditCityBean> list) {
                mModels.clear();
                mFilterList.clear();
                for(NetAccreditCityBean city:list){
                    InnsmapModel model=new InnsmapModel();
                    model.setId(city.getCityId()+"");
                    model.setName(city.getCityName());
                    mModels.add(model);
                }
                mFactory.setmCities(mModels);
                mFilterList.addAll(mModels);
                InnsmapModel model=new InnsmapModel();
                model.setId("86");
                model.setName("中国");
                mFactory.setCurrentCountry(model);
                mAdapter.notifyDataSetChanged();
                activity.dismissProgress();
            }
            @Override
            public void onFail(String msg) {
                activity.dismissProgress();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 获取指定城市下的所有建筑物
     */
    private void getAllBuilding() {
        if (this.mFactory.getCurrentCountry() == null || this.mFactory.getCurrentCity() == null)
            return;
        final InnsmapSelectActivity activity = (InnsmapSelectActivity) this.getActivity();
        activity.showProgressDialog(this.getString(R.string.innsmap_refresh_list));
        int countryId = Integer.parseInt(this.mFactory.getCurrentCountry().getId());
        int cityId = Integer.parseInt(this.mFactory.getCurrentCity().getId());
        INNSMapSDKResource.getCityBuilding(countryId, cityId, new NetCityBuildingListener() {
            @Override
            public void onSuccess(List<NetCityBuildingBean> list) {
                mModels.clear();
                mFilterList.clear();
                for (NetCityBuildingBean bean : list) {
                    InnsmapModel model = new InnsmapModel();
                    model.setId(bean.getBuildingId());
                    model.setName(bean.getBuildingName());
                    mModels.add(model);
                }
                mFilterList.addAll(mModels);
                mAdapter.notifyDataSetChanged();
                activity.dismissProgress();
            }
            @Override
            public void onFail(String msg) {
                activity.dismissProgress();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * 获取指定建筑物的所有楼层
     */
    private void getAllFloor() {
        if (this.mFactory.getCurrentBuilding() == null)
            return;
        final InnsmapSelectActivity activity = (InnsmapSelectActivity) this.getActivity();
        activity.showProgressDialog(this.getString(R.string.innsmap_refresh_list));
        INNSMapSDKResource.getBuildingDetail(this.mFactory.getCurrentBuilding().getId(), new NetBuildingDetailListener() {
            @Override
            public void onSuccess(NetBuildingDetailBean buildingDetail) {
                mModels.clear();
                mFilterList.clear();
                for (NetBuildingDetailFloorBean bean : buildingDetail.getOvergroundList()) {
                    InnsmapModel model = new InnsmapModel();
                    model.setId(bean.getFloorId());
                    model.setName(bean.getFloorName());
                    mModels.add(model);
                }
                for (NetBuildingDetailFloorBean bean : buildingDetail.getUndergroundList()) {
                    InnsmapModel model = new InnsmapModel();
                    model.setId(bean.getFloorId());
                    model.setName(bean.getFloorName());
                    mModels.add(model);
                }
                mFilterList.addAll(mModels);
                mAdapter.notifyDataSetChanged();
                activity.dismissProgress();
            }
            @Override
            public void onFail(String msg) {
                activity.dismissProgress();
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pointer:
                this.getActivity().finish();
                break;
        }
    }
    /**
     * 城市列表适配器类
     *
     * @author jianchao.wang
     */
    private class InnsmapAdapter extends ArrayAdapter<InnsmapModel> {
        private int mResourceId;
        public InnsmapAdapter(Context context, int textViewResourceId, List<InnsmapModel> objects) {
            super(context, textViewResourceId, objects);
            this.mResourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.getContext()).inflate(this.mResourceId, null);
            }
            TextView name = (TextView) view.findViewById(R.id.innsmap_name);
            InnsmapModel model = this.getItem(position);
            name.setText(model.getName());
            return view;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.select_list) {
            InnsmapModel model = this.mModels.get(position);
            switch (this.mType) {
                case City:
                    this.mFactory.setCurrentCity(this.getActivity(), model);
                    break;
                case Building:
                    this.mFactory.setCurrentBuilding(this.getActivity(), model);
                    break;
                case Floor:
                    this.mFactory.setCurrentFloor(this.getActivity(), model);
                    break;
            }
            Intent data = new Intent();
            data.putExtra("typeId", this.mType.getId());
            this.getActivity().setResult(StartDialog.requestInnsmapCode, data);
            this.getActivity().finish();
        }
    }
}

package com.walktour.gui.newmap2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.gui.R;
import com.walktour.gui.newmap2.bean.RounteBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择路线弹窗（仅支持高德)
 *
 * @author zhicheng.chen
 * @date 2018/12/18
 */
public class RounteActivity extends FragmentActivity implements Inputtips.InputtipsListener {

    private static final int MAX_STATION = 18;

    @BindView(R.id.lv_station)
    ListView mLvStation;
    @BindView(R.id.lv_result)
    ListView mLvResult;
    @BindView(R.id.ll_rounte)
    LinearLayout mLlRounte;
    @BindView(R.id.ll_search)
    LinearLayout mLlSearch;
    @BindView(R.id.et_key_word)
    EditText mEtKeyWord;
    @BindView(R.id.pb_loading)
    ProgressBar mPb;
    @BindView(R.id.tv_msg)
    TextView mTvMsg;

    /**
     * 线路
     */
    private List<RounteBean<Tip>> mDatas = new ArrayList<>();
    /**
     * 地址搜索结果
     */
    private List<RounteBean<Tip>> mResultDatas = new ArrayList<>();
    /**
     * 角标
     */
    private int mIndex;
    private RounteAdapter mAdapter;
    private ResultAdapter mResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rounte);
        ButterKnife.bind(this);

        initRoute();
        initResult();
        setListener();
    }

    private void initRoute() {
        RounteBean start = new RounteBean();//起点
        RounteBean end = new RounteBean();//终点
        mDatas.add(start);
        mDatas.add(end);

        //读取缓存
        ArrayList<RounteBean<Tip>> cacheRounteDatas = loadFromCache();
        if (cacheRounteDatas != null && cacheRounteDatas.size() > 0) {
            mDatas.clear();
            mDatas.addAll(cacheRounteDatas);
        }

        mAdapter = new RounteAdapter();
        mLvStation.setAdapter(mAdapter);
    }

    private void initResult() {
        mResultAdapter = new ResultAdapter();
        mLvResult.setAdapter(mResultAdapter);
    }

    private void setListener() {
        mLvStation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIndex = position;
                mLlRounte.setVisibility(View.GONE);
                mLlSearch.setVisibility(View.VISIBLE);
                mEtKeyWord.setText("");
                mResultDatas.clear();
                mResultAdapter.notifyDataSetChanged();
            }
        });

        mLvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLlRounte.setVisibility(View.VISIBLE);
                mLlSearch.setVisibility(View.GONE);
                mDatas.set(mIndex, mResultDatas.get(position));
                mAdapter.notifyDataSetChanged();
                hideSoftKeyboard(RounteActivity.this);
            }
        });
        mEtKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTvMsg.getVisibility() == View.VISIBLE) {
                    mTvMsg.setVisibility(View.GONE);
                }
                if (mLvResult.getVisibility() == View.GONE) {
                    mLvResult.setVisibility(View.VISIBLE);
                }
                String newText = s.toString().trim();
                if (!TextUtils.isEmpty(newText)) {
                    InputtipsQuery inputquery = new InputtipsQuery(newText, null);
                    Inputtips inputTips = new Inputtips(getApplicationContext(), inputquery);
                    inputTips.setInputtipsListener(RounteActivity.this);
                    inputTips.requestInputtipsAsyn();
                    showLoading();
                }
            }
        });
    }

    @OnClick(R.id.btn_ok)
    void clickOk() {
        if (StringUtil.isEmpty(mDatas.get(0).name)) {
            ToastUtil.showShort(this, getString(R.string.please_enter_the_starting_point));
            return;
        }
        if (StringUtil.isEmpty(mDatas.get(mDatas.size() - 1).name)) {
            ToastUtil.showShort(this, getString(R.string.please_enter_the_terminal));
            return;
        }
        final ArrayList<RounteBean<Tip>> resultDatas = new ArrayList<>();
        for (RounteBean<Tip> mData : mDatas) {
            if (StringUtil.isEmpty(mData.name)) {
                continue;
            }
            resultDatas.add(mData);
        }

        saveToCache(resultDatas);

        EventBus.getDefault().postSticky(resultDatas);
        finish();
    }

    private ArrayList<RounteBean<Tip>> loadFromCache() {
        String json = SharePreferencesUtil.getInstance(RounteActivity.this).getString("rounte_cache", "");
        ArrayList<RounteBean<Tip>> resultDatas = new Gson().fromJson(json, new TypeToken<ArrayList<RounteBean<Tip>>>() {
        }.getType());
        return resultDatas;
    }

    private void saveToCache(ArrayList<RounteBean<Tip>> resultDatas) {
        SharePreferencesUtil.getInstance(RounteActivity.this).saveString("rounte_cache", new Gson().toJson(resultDatas));
    }

    @OnClick(R.id.btn_add)
    void clickAdd() {
        if (mDatas.size() < MAX_STATION) {
            RounteBean center = new RounteBean();
            RounteBean end = mDatas.remove(mDatas.size() - 1);
            mDatas.add(center);
            mDatas.add(end);
            mAdapter.notifyDataSetChanged();
            mLvStation.setSelection(mDatas.size() - 1);
        } else {
            ToastUtil.showLong(this, getString(R.string.max_input_16_via_point));
        }
    }

    @OnClick(R.id.btn_back)
    void clickBack() {
        mLlRounte.setVisibility(View.VISIBLE);
        mLlSearch.setVisibility(View.GONE);
        mEtKeyWord.setText("");
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        hideLoading();
        try {
            if (rCode == 1000) {
                mResultDatas.clear();
                for (Tip tip : tipList) {
                    if (null == tip.getPoint()) {
                        continue;
                    }
                    RounteBean<Tip> b = new RounteBean<>();
                    b.info = tip;
                    b.name = tip.getName();
                    b.adress = tip.getAddress();
                    b.lat = tip.getPoint().getLatitude();
                    b.lng = tip.getPoint().getLongitude();
                    mResultDatas.add(b);
                }
                if (mResultDatas.size() > 0) {
                    mResultAdapter.notifyDataSetChanged();
                } else {
                    mTvMsg.setText(R.string.sorry_no_result_change_keyword);
                    mTvMsg.setVisibility(View.VISIBLE);
                    mLvResult.setVisibility(View.GONE);
                }
            } else {
                mTvMsg.setText(R.string.fail_try_again);
                mTvMsg.setVisibility(View.VISIBLE);
                mLvResult.setVisibility(View.GONE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            mTvMsg.setText(R.string.fail_try_again);
            mTvMsg.setVisibility(View.VISIBLE);
            mLvResult.setVisibility(View.GONE);
        }
    }

    /**
     * 路线
     */
    class RounteAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public RounteBean getItem(int position) {
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
                convertView = View.inflate(RounteActivity.this, R.layout.item_dialog_rounte, null);
                hodler = new ViewHodler(convertView);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            if (position == 0) {
                hodler.tvFlag.setVisibility(View.VISIBLE);
                hodler.ivDel.setVisibility(View.GONE);
                hodler.tvFlag.setText(R.string.start_point);
                hodler.tvName.setHint("");
            } else if (position == mDatas.size() - 1) {
                hodler.tvFlag.setVisibility(View.VISIBLE);
                hodler.ivDel.setVisibility(View.GONE);
                hodler.tvFlag.setText(R.string.end_point);
                hodler.tvName.setHint("");
            } else {
                hodler.tvFlag.setVisibility(View.GONE);
                hodler.ivDel.setVisibility(View.VISIBLE);
                hodler.tvName.setHint(R.string.input_via_point);
                hodler.tvName.setText(getItem(position).name);
            }
            hodler.tvName.setText(getItem(position).name);
            hodler.ivDel.setTag(position);
            hodler.ivDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    mDatas.remove(position);
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        class ViewHodler {
            @BindView(R.id.tv_station_flag)
            public TextView tvFlag;
            @BindView(R.id.iv_delete_station)
            public ImageView ivDel;
            @BindView(R.id.tv_station_name)
            public TextView tvName;

            public ViewHodler(View root) {
                ButterKnife.bind(this, root);
            }

        }
    }

    /**
     * 地址搜索
     */
    class ResultAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mResultDatas.size();
        }

        @Override
        public RounteBean getItem(int position) {
            return mResultDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler hodler = null;
            if (convertView == null) {
                convertView = View.inflate(RounteActivity.this, R.layout.item_dialog_search, null);
                hodler = new ViewHodler(convertView);
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }
            hodler.tvAddressName.setText(getItem(position).name);
            hodler.tvAddressDesc.setText(getItem(position).adress);

            return convertView;
        }

        class ViewHodler {
            @BindView(R.id.tv_address_name)
            public TextView tvAddressName;
            @BindView(R.id.tv_address_desc)
            public TextView tvAddressDesc;

            public ViewHodler(View root) {
                ButterKnife.bind(this, root);
            }

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            finish();
        }
    }

    private void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showLoading() {
        mPb.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mPb.setVisibility(View.GONE);
    }

}

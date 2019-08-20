package com.walktour.gui.newmap2.filter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.ToastUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.WalktourApplication;
import com.walktour.gui.newmap.basestation.ImportBaseStationFilterManager;
import com.walktour.gui.newmap2.bean.StationEvent;
import com.walktour.gui.setting.SysMap;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 筛选参数弹窗
 *
 * @author zhicheng.chen
 * @date 2019/3/15
 */
public class ParamFilterDialog {
    private Context mContext;

    @BindView(R.id.tv_params_name)
    TextView tvParamName;
    @BindView(R.id.tv_params_name2)
    TextView tvParamName2;
    @BindView(R.id.et_params_min)
    EditText etParamMin;
    @BindView(R.id.et_params_max)
    EditText etParamMax;
    @BindView(R.id.et_params_min2)
    EditText etParamMin2;
    @BindView(R.id.et_params_max2)
    EditText etParamMax2;
    @BindView(R.id.sp_net_type)
    Spinner spinner;
    private final View dlgView;

    public ParamFilterDialog(Context context) {
        mContext = context;

        dlgView = LayoutInflater.from(mContext).inflate(R.layout.dlg_import_base_station_filter_setting, null);

        ButterKnife.bind(this, dlgView);

    }


    public void show() {
        final ImportBaseStationFilterManager filterManager = ImportBaseStationFilterManager.getInstance();
        final String[] params = getFilterNetType();

        if (params == null || params.length == 0) {

            com.walktour.base.util.ToastUtil.showShort(mContext, "请在设置界面，勾选基站显示网络类型");

        } else {

            if (params != null && params.length > 0 && filterManager.getFilterStrategy() == null) {
                filterManager.setFilterStrategy(getParamFilter(params[0]));
            }

            tvParamName.setText(filterManager.getFilterStrategy().getFirstParamName());
            tvParamName2.setText(filterManager.getFilterStrategy().getSecondParamName());
            etParamMin.setText(String.valueOf(filterManager.getFilterStrategy().getFirstParamMin()));
            etParamMax.setText(String.valueOf(filterManager.getFilterStrategy().getFirstParamMax()));
            etParamMin2.setText(String.valueOf(filterManager.getFilterStrategy().getSecondParamMin()));
            etParamMax2.setText(String.valueOf(filterManager.getFilterStrategy().getSecondParamMax()));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, params);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setPrompt(mContext.getString(R.string.net_type));


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    IParamFilter filter = getParamFilter(params[position]);
                    filterManager.setFilterStrategy(filter);
                    tvParamName.setText(filterManager.getFilterStrategy().getFirstParamName());
                    tvParamName2.setText(filterManager.getFilterStrategy().getSecondParamName());
                    etParamMin.setText(String.valueOf(filterManager.getFilterStrategy().getFirstParamMin()));
                    etParamMax.setText(String.valueOf(filterManager.getFilterStrategy().getFirstParamMax()));
                    etParamMin2.setText(String.valueOf(filterManager.getFilterStrategy().getSecondParamMin()));
                    etParamMax2.setText(String.valueOf(filterManager.getFilterStrategy().getSecondParamMax()));
                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            dlgView.findViewById(R.id.btn_reset_params).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterManager.getFilterStrategy().resetFirstParam();
                    etParamMin.setText(filterManager.getFilterStrategy().getFirstParamMin() + "");
                    etParamMax.setText(filterManager.getFilterStrategy().getFirstParamMax() + "");
                }
            });

            dlgView.findViewById(R.id.btn_reset_params2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filterManager.getFilterStrategy().resetSecondParam();
                    etParamMin2.setText(filterManager.getFilterStrategy().getSecondParamMin() + "");
                    etParamMax2.setText(filterManager.getFilterStrategy().getSecondParamMax() + "");
                }
            });


            new BasicDialog.Builder(mContext)
                    .setTitle(R.string.map_import_base_filter)
                    .setView(dlgView)
                    .setNegativeButton(R.string.str_cancle, null, true)
                    .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String minFirstParam = etParamMin.getText().toString().trim();
                            String maxFirstParam = etParamMax.getText().toString().trim();
                            String minSecondParam = etParamMin2.getText().toString().trim();
                            String maxSecondParam = etParamMax2.getText().toString().trim();

                            int minParam = TextUtils.isEmpty(minFirstParam) ? filterManager.getFilterStrategy().getFirstParamMin() : Integer.parseInt(minFirstParam);
                            int maxParam = TextUtils.isEmpty(maxFirstParam) ? filterManager.getFilterStrategy().getFirstParamMax() : Integer.parseInt(maxFirstParam);
                            int minParam2 = TextUtils.isEmpty(minSecondParam) ? filterManager.getFilterStrategy().getSecondParamMin() : Integer.parseInt(minSecondParam);
                            int maxParam2 = TextUtils.isEmpty(maxSecondParam) ? filterManager.getFilterStrategy().getSecondParamMax() : Integer.parseInt(maxSecondParam);

                            if (maxParam <= minParam || maxParam2 <= minParam2) {
                                ToastUtil.showToastShort(WalktourApplication.getAppContext(), mContext.getString(R.string.invalid_scope));
                            } else {
                                filterManager.getFilterStrategy().saveFirstParamRange(minParam, maxParam);
                                filterManager.getFilterStrategy().saveSecondParamRange(minParam2, maxParam2);
                            }

                            filterManager.setIsFilter(true);

                            // 通知地图刷新
                            StationEvent event = new StationEvent();
                            event.type = StationEvent.PARAM_FILTER;
                            EventBus.getDefault().post(event);
                        }
                    }, true).show();
        }

    }

    @NonNull
    private IParamFilter getParamFilter(String param) {
        IParamFilter filter;
        switch (param) {
            case "GSM":
                filter = new GsmParamFilter();
                break;
            case "WCDMA":
                filter = new WcdmaParamFilter();
                break;
            case "CDMA":
                filter = new CdmaParamFilter();
                break;
            case "TD-SCDMA":
                filter = new TdParamFilter();
                break;
            case "LTE":
                filter = new LteParamFilter();
                break;
            case "NB-IoT":
                filter = new NbParamFilter();
                break;
            default:
                filter = new LteParamFilter();
                break;
        }
        return filter;
    }

    @NonNull
    private String[] getFilterNetType() {
        List<String> netType = new ArrayList<>();
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(SysMap.BASE_GSM)) {
            netType.add("GSM");
        }
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(SysMap.BASE_WCDMA)) {
            netType.add("WCDMA");
        }
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(SysMap.BASE_CDMA)) {
            netType.add("CDMA");
        }
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(SysMap.BASE_TDSCDMA)) {
            netType.add("TD-SCDMA");
        }
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(SysMap.BASE_LTE)) {
            netType.add("LTE");
        }
        if (SharePreferencesUtil.getInstance(mContext).getBoolean(SysMap.BASE_NB_IoT)) {
            netType.add("NB-IoT");
        }
        return netType.toArray(new String[netType.size()]);
    }
}

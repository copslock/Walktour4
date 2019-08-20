package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.UnifyStruct;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ParameterSetting;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class DynamicParamterFragment extends Fragment implements RefreshEventListener {

    private DynamicParamView dynamicView;
    List<Parameter> parameters = new ArrayList<Parameter>();
    private ParameterSetting mParameterSet;
    private Parameter[] parameterArray; // 根据网络获取参数
    private String netTypeStr;
    private int page = 1;
    private TextView netTypeTxt;
    private TextView netTypeTxtNR;
    private TextView netTypeTxtENDC;
    private TextView type;
    private TextView code;
    private TextView param_name_1;
    private TextView param_value_1;
    private TextView param_name_2;
    private TextView param_value_2;
    private TextView param_name_3;
    private TextView param_value_3;
    private TextView param_name_4;
    private TextView param_value_4;
    private TextView param_name_5;
    private TextView param_value_5;
    private TextView param_name_6;
    private TextView param_value_6;
    private TextView param_name_1_nr;
    private TextView param_value_1_nr;
    private TextView param_name_2_nr;
    private TextView param_value_2_nr;
    private TextView param_name_3_nr;
    private TextView param_value_3_nr;
    private TextView param_name_4_nr;
    private TextView param_value_4_nr;
    private TextView param_name_5_nr;
    private TextView param_value_5_nr;
    private TextView param_name_6_nr;
    private TextView param_value_6_nr;
    private TextView titleArray[] = new TextView[7];
    private TextView valueArray[] = new TextView[7];
    private Context mContext;
    private ScrollView csv;

    private boolean isWlanTest = false;

    public DynamicParamterFragment(Context context)
    {
        mContext = context;
        mParameterSet = ParameterSetting.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
    {
        isWlanTest =
				TaskListDispose.getInstance().isWlanTest() && ApplicationModel.getInstance().isTestJobIsRun();
        View rootView;
        if (isWlanTest) {//无线网络
            rootView = inflater.inflate(R.layout.dynamic_param_layout_wifi, null);
            initViewWlan(rootView);
        } else if (TraceInfoInterface.currentNetType == CurrentNetState.NBIoT || TraceInfoInterface.currentNetType == CurrentNetState.CatM) {//NB网络
            rootView = inflater.inflate(R.layout.dynamic_param_layout_nb, null);
            initView(rootView);
        } else if (TraceInfoInterface.currentNetType == CurrentNetState.ENDC) {//NB网络
            rootView = inflater.inflate(R.layout.dynamic_param_layout_endc, null);
            initViewENDC(rootView);
        } else {
            rootView = inflater.inflate(R.layout.dynamic_param_layout, null);
            initView(rootView);
        }
        RefreshEventManager.addRefreshListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (isWlanTest) {
            getParamDataWlan();
        } else {
            getParamData();
        }
    }

    private void initViewENDC(View view)
    {
        csv = (ScrollView) view.findViewById(R.id.scrollView1);
        csv.setVerticalScrollBarEnabled(false);
        LinearLayout customView = (LinearLayout) view.findViewById(R.id.custom_view);
        netTypeTxt = (TextView) view.findViewById(R.id.network_type); // 网络类型
        netTypeTxtNR = (TextView) view.findViewById(R.id.network_type_nr); // 网络类型
        netTypeTxtENDC = (TextView) view.findViewById(R.id.network_type_endc); // 网络类型
        type = (TextView) view.findViewById(R.id.type); // 第二行标题
        code = (TextView) view.findViewById(R.id.code); // 第二行值
        param_name_1 = (TextView) view.findViewById(R.id.param_name_1); // 第一行2标题
        param_value_1 = (TextView) view.findViewById(R.id.param_value_1); // 第一行2值
        param_name_2 = (TextView) view.findViewById(R.id.param_name_2); // 第一行3标题
        param_value_2 = (TextView) view.findViewById(R.id.param_value_2); // 第一行3值
        param_name_3 = (TextView) view.findViewById(R.id.param_name_3); // 第二行2标题
        param_value_3 = (TextView) view.findViewById(R.id.param_value_3); // 第二行2值
        param_name_4 = (TextView) view.findViewById(R.id.param_name_4); // 第二行3标题
        param_value_4 = (TextView) view.findViewById(R.id.param_value_4); // 第二行3值
        param_name_5 = (TextView) view.findViewById(R.id.param_name_5); // 第二行2标题
        param_value_5 = (TextView) view.findViewById(R.id.param_value_5); // 第二行2值
        param_name_6 = (TextView) view.findViewById(R.id.param_name_6); // 第二行3标题
        param_value_6 = (TextView) view.findViewById(R.id.param_value_6); // 第二行3值

        param_name_1_nr = (TextView) view.findViewById(R.id.param_name_1_nr); // 第一行2标题
        param_value_1_nr = (TextView) view.findViewById(R.id.param_value_1_nr); // 第一行2值
        param_name_2_nr = (TextView) view.findViewById(R.id.param_name_2_nr); // 第一行3标题
        param_value_2_nr = (TextView) view.findViewById(R.id.param_value_2_nr); // 第一行3值
        param_name_3_nr = (TextView) view.findViewById(R.id.param_name_3_nr); // 第二行2标题
        param_value_3_nr = (TextView) view.findViewById(R.id.param_value_3_nr); // 第二行2值
        param_name_4_nr = (TextView) view.findViewById(R.id.param_name_4_nr); // 第二行3标题
        param_value_4_nr = (TextView) view.findViewById(R.id.param_value_4_nr); // 第二行3值
        param_name_5_nr = (TextView) view.findViewById(R.id.param_name_5_nr); // 第二行2标题
        param_value_5_nr = (TextView) view.findViewById(R.id.param_value_5_nr); // 第二行2值
        param_name_6_nr = (TextView) view.findViewById(R.id.param_name_6_nr); // 第二行3标题
        param_value_6_nr = (TextView) view.findViewById(R.id.param_value_6_nr); // 第二行3值

        initTxtArray();
        csv.setFillViewport(true);
        dynamicView = new DynamicParamView(mContext);
        dynamicView.setParamData(parameters);
        customView.addView(dynamicView);

    }

    private void initView(View view)
    {
        csv = (ScrollView) view.findViewById(R.id.scrollView1);
        csv.setVerticalScrollBarEnabled(false);
        LinearLayout customView = (LinearLayout) view.findViewById(R.id.custom_view);
        netTypeTxt = (TextView) view.findViewById(R.id.network_type); // 网络类型

        type = (TextView) view.findViewById(R.id.type); // 第二行标题
        code = (TextView) view.findViewById(R.id.code); // 第二行值
        param_name_1 = (TextView) view.findViewById(R.id.param_name_1); // 第一行2标题
        param_value_1 = (TextView) view.findViewById(R.id.param_value_1); // 第一行2值
        param_name_2 = (TextView) view.findViewById(R.id.param_name_2); // 第一行3标题
        param_value_2 = (TextView) view.findViewById(R.id.param_value_2); // 第一行3值
        param_name_3 = (TextView) view.findViewById(R.id.param_name_3); // 第二行2标题
        param_value_3 = (TextView) view.findViewById(R.id.param_value_3); // 第二行2值
        param_name_4 = (TextView) view.findViewById(R.id.param_name_4); // 第二行3标题
        param_value_4 = (TextView) view.findViewById(R.id.param_value_4); // 第二行3值
        param_name_5 = (TextView) view.findViewById(R.id.param_name_5); // 第二行2标题
        param_value_5 = (TextView) view.findViewById(R.id.param_value_5); // 第二行2值
        param_name_6 = (TextView) view.findViewById(R.id.param_name_6); // 第二行3标题
        param_value_6 = (TextView) view.findViewById(R.id.param_value_6); // 第二行3值
        initTxtArray();
        csv.setFillViewport(true);
        dynamicView = new DynamicParamView(mContext);
        dynamicView.setParamData(parameters);
        customView.addView(dynamicView);

    }

    private void initViewWlan(View view)
    {
        csv = (ScrollView) view.findViewById(R.id.scrollView1);
        csv.setVerticalScrollBarEnabled(false);
        LinearLayout customView = (LinearLayout) view.findViewById(R.id.custom_view);
        param_value_1 = (TextView) view.findViewById(R.id.param_value_1);
        param_value_2 = (TextView) view.findViewById(R.id.param_value_2);
        param_value_3 = (TextView) view.findViewById(R.id.param_value_3);
        csv.setFillViewport(true);
        dynamicView = new DynamicParamView(mContext);
        dynamicView.setParamData(parameters);
        customView.addView(dynamicView);
    }

    /**
     * 初始化text数组
     */
    private void initTxtArray()
    {
        if (TraceInfoInterface.currentNetType == CurrentNetState.NBIoT || TraceInfoInterface.currentNetType == CurrentNetState.CatM) {//NB网络
            titleArray=null;
            valueArray=null;
            titleArray = new TextView[]{param_name_1, param_name_2, type, param_name_3,
										param_name_4, param_name_5, param_name_6};
            valueArray = new TextView[]{param_value_1, param_value_2, code, param_value_3,
										param_value_4, param_value_5, param_value_6};
        } else if (TraceInfoInterface.currentNetType == CurrentNetState.ENDC) {
            titleArray=null;
            valueArray=null;
            titleArray = new TextView[]{param_name_1_nr, param_name_2_nr, param_name_3_nr,
										param_name_4_nr, param_name_5_nr, param_name_6_nr,
										param_name_1, param_name_2, type, param_name_3,
										param_name_4};
            valueArray = new TextView[]{param_value_1_nr, param_value_2_nr, param_value_3_nr,
										param_value_4_nr, param_value_5_nr, param_value_6_nr,
										param_value_1, param_value_2, code, param_value_3,
										param_value_4};
        } else {
            titleArray=null;
            valueArray=null;
            titleArray = new TextView[]{param_name_1, param_name_2, type, param_name_3,
										param_name_4};
            valueArray = new TextView[]{param_value_1, param_value_2, code, param_value_3,
										param_value_4};
        }
    }

    /**
     * 获取参数数据信息
     */
    private void getParamData()
    {
        CurrentNetState netType = !ApplicationModel.getInstance().isFreezeScreen() ?
				TraceInfoInterface.currentNetType
                : TraceInfoInterface.decodeFreezeNetType;
        createPublicData(netType);
        switch (netType) {
            case GSM:
                netTypeStr = CurrentNetState.GSM.name();
                break;
            case LTE:
                netTypeStr = CurrentNetState.LTE.name();
                break;
            case WCDMA:
                netTypeStr = CurrentNetState.WCDMA.name();
                break;
            case CDMA:
                netTypeStr = CurrentNetState.CDMA.name();
                break;
            case TDSCDMA:
                netTypeStr = CurrentNetState.TDSCDMA.name();
                break;
            case NBIoT:
                netTypeStr = CurrentNetState.NBIoT.name();
                break;
            case CatM:
                netTypeStr = CurrentNetState.CatM.name();
                break;
            case ENDC:
                netTypeStr = CurrentNetState.ENDC.name();
                break;
            case NoService:
            case Unknown:
            default:
                netTypeStr = CurrentNetState.LTE.name();
                break;
        }
        parameterArray = mParameterSet.getTableParametersByNetworkType(netTypeStr);
        parameters.clear();
        for (int i = 0; i < parameterArray.length; i++) {
            if (parameterArray[i].getTabIndex() == page) {
                parameters.add(parameterArray[i]);
            }
        }
        if (dynamicView != null) {
            dynamicView.setParamData(parameters);
            csv.measure(dynamicView.getWidth(), dynamicView.calculateViewHeight());
            dynamicView.invalidate();
        }
    }

    private void getParamDataWlan()
    {
        parameterArray = mParameterSet.getTableParametersByNetworkType("WLAN");
        parameters.clear();
        param_value_1.setText(TraceInfoInterface.getParaValue(UnifyParaID.WIFI_SSID) + "");
        param_value_2.setText(TraceInfoInterface.getParaValue(UnifyParaID.WIFI_IP_Address) + "");
        param_value_3.setText(TraceInfoInterface.getParaValue(UnifyParaID.WIFI_MAC_Address) + "");
        for (int i = 0; i < parameterArray.length; i++) {
            if (parameterArray[i].getTabIndex() == page) {
                parameters.add(parameterArray[i]);
            }
        }
        if (dynamicView != null) {
            dynamicView.setParamData(parameters);
            csv.measure(dynamicView.getWidth(), dynamicView.calculateViewHeight());
            dynamicView.invalidate();
        }
    }

    /**
     * 填固定公共值
     *
     * @param netType
     */

    private void createPublicData(CurrentNetState netType)
    {
        String networkName = "UNKNOWN";
        String[] pubicParamDatas = new String[]{"-", "-", "-", "-", "-"};
        String[] publicParamNames = new String[]{"Cell ID", "RxLev", "MCC/MNC", "BCCH", "BSIC"};
        switch (netType) {
            case GSM:
                networkName = "GSM";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas =
						new String[]{TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_Cell_ID),
                                               TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BCCHLev),
                                               TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MCC).concat("/")
                                                       .concat(TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_MNC)),
                                               TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BCCH),
                                               TraceInfoInterface.getParaValue(UnifyParaID.G_Ser_BSIC)};
                publicParamNames = new String[]{"Cell ID", "RxLev", "MCC/MNC", "BCCH", "BSIC"};
                break;
            // case EVDO:
            // networkName = "EVDO";
            // pubicParamDatas = new String[] { TraceInfoInterface.getParaValue(UnifyParaID.E_UATI),
            // TraceInfoInterface.getParaValue(UnifyParaID.E_Carrier1_TotalSINR),
            // TraceInfoInterface.getParaValue(UnifyParaID.E_EV_Frequenc),
            // TraceInfoInterface.getParaValue(UnifyParaID.E_ServingSectorPN) };
            // publicParamNames = new String[] { "UATI", "Total SINR", "Freq.", "PN" };
            // break;
            case CDMA:
                networkName = "CDMA";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas = new String[]{TraceInfoInterface.getParaValue(UnifyParaID.C_NID),
                                               TraceInfoInterface.getParaValue(UnifyParaID.C_Frequency),
                                               TraceInfoInterface.getParaValue(UnifyParaID.C_TotalEcIo),
                                               TraceInfoInterface.getParaValue(UnifyParaID.C_SID),
                                               TraceInfoInterface.getParaValue(UnifyParaID.C_ReferencePN)};
                publicParamNames = new String[]{"NID", "Freq", "Total EcIo", "SID", "PN"};
                break;
            case WCDMA:
                networkName = "WCDMA";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas =
						new String[]{TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Total_RSCP),
                                               TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Total_EcIo),
                                               TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MCC).concat("/")
                                                       .concat(TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_MNC)),
                                               TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_DL_UARFCN),
                                               TraceInfoInterface.getParaValue(UnifyParaID.W_Ser_Max_PSC)};
                publicParamNames = new String[]{"RSCP", "EcIo", "MCC/MNC", "UARFCN", "PSC"};
                break;
            case TDSCDMA:
                networkName = "TDSCDMA";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas =
						new String[]{TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_PCCPCHRSCP),
                                               TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_PCCPCHC2I),
                                               TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MCC).concat("/")
                                                       .concat(TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_MNC)),
                                               TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_UARFCN),
                                               TraceInfoInterface.getParaValue(UnifyParaID.TD_Ser_CPI)};
                publicParamNames = new String[]{"RSCP", "C/I", "MCC/MNC", "UARFCN", "CPI"};
                break;
            case LTE:
                networkName = "LTE";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas =
						new String[]{TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRP),
                                               TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_SINR),
                                               TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MCC).concat("/")
                                                       .concat(TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MNC)),
                                               TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_EARFCN),
                                               TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_PCI)};
                publicParamNames = new String[]{"RSRP", "SINR", "MCC/MNC", "EARFCN", "PCI"};
                break;
            case NBIoT:
                UnifyStruct.LTENB lteapn =
						((UnifyStruct.LTENB) TraceInfoInterface.getParaStruct(UnifyParaID.LTE_NB_FORMAT));
                String valuestr = "";
                if (lteapn != null) {
                    valuestr = lteapn.lteNb;
                }
                networkName = "NB-IoT";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas =
						new String[]{TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_RSRP),
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_SINR),
                                               "MCC/MNC",
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_EARFCN),
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_PCI),
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_IMSI),
                                               valuestr};
                publicParamNames = new String[]{"RSRP", "SINR", "MCC/MNC", "EARFCN", "PCI", "IMSI", "Version"};
                break;
            case CatM:
                UnifyStruct.LTENB catM = ((UnifyStruct.LTENB) TraceInfoInterface.getParaStruct(UnifyParaID.LTE_NB_FORMAT));
                String valueCatM = "";
                if (catM != null) {
                    valueCatM = catM.lteNb;
                }
                networkName = "CatM";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas = new String[]{TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_RSRP),
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_SINR),
                                               "MCC/MNC",
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_EARFCN),
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_SRV_PCI),
                                               TraceInfoInterface.getParaValue(UnifyParaID.NB_IMSI),
                                               valueCatM};
                publicParamNames = new String[]{"RSRP", "SINR", "MCC/MNC", "EARFCN", "PCI", "IMSI", "Version"};
                break;
            case ENDC:
                networkName = "ENDC";
                pubicParamDatas=null;
                publicParamNames=null;
                pubicParamDatas = new String[]{
                        TraceInfoInterface.getParaValue(UnifyParaID.NR_SS_RSRP),
                        TraceInfoInterface.getParaValue(UnifyParaID.NR_PCI),
                        TraceInfoInterface.getParaValue(UnifyParaID.NR_SS_SINR),
                        TraceInfoInterface.getParaValue(UnifyParaID.NR_Band),
                        TraceInfoInterface.getParaValue(UnifyParaID.NR_PointA_ARFCN),
                        TraceInfoInterface.getParaValue(UnifyParaID.NR_SSB_ARFCN),
                        TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_RSRP),
                        TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MCC).concat("/")
                                .concat(TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_MNC)),
                        TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_SINR),
                        TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_PCI),
                        TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_EARFCN),
                        TraceInfoInterface.getParaValue(UnifyParaID.L_SRV_Band)};
                publicParamNames = new String[]{"SS-RSRP", "PCI", "SS-SINR", "Band", "PointA ARFCN", "SSB ARFCN", "RSRP", "MCC/MNC", "SINR", "PCI", "EARFCN", "Band"};
                break;
            default:
                networkName = "UNKNOWN";
                break;
        }
        LogUtil.w("YUYUYU", "networkName=" + networkName + "," + titleArray.length + "," + valueArray.length);
        if (netTypeTxt != null) {
            if (networkName.equals("ENDC")) {
                netTypeTxtNR.setText("NR");
                netTypeTxt.setText("LTE");
                netTypeTxtENDC.setText("ENDC");
            } else {
                netTypeTxt.setText(networkName);
            }
            for (int i = 0; i < titleArray.length; i++) {
                titleArray[i].setText(publicParamNames[i]);
            }
            for (int i = 0; i < valueArray.length; i++) {
                valueArray[i].setText(pubicParamDatas[i]);
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        RefreshEventManager.removeRefreshListener(this);
    }

    @Override
    public void onRefreshed(RefreshType refreshType, Object object)
    {
        switch (refreshType) {
            case ACTION_WALKTOUR_TIMER_CHANGED:
                if (isWlanTest) {
                    getParamDataWlan();
                } else {
                    getParamData();
                }
                break;
            default:
                break;
        }
    }
}

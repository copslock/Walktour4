package com.walktour.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dinglicom.dataset.model.ENDCDataModel;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ShowInfo;
import com.walktour.gui.R;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CustomDLULView extends LinearLayout  {
    private static final String TAG = "CustomDLULView";
    @BindView(R.id.pdcp_nr_dl)
    TextView pdcpNrDl;
    @BindView(R.id.pdcp_nr_ul)
    TextView pdcpNrUl;
    @BindView(R.id.pdcp_lte_dl)
    TextView pdcpLteDl;
    @BindView(R.id.pdcp_lte_ul)
    TextView pdcpLteUl;
    @BindView(R.id.rlc_nr_dl)
    TextView rlcNrDl;
    @BindView(R.id.rlc_nr_ul)
    TextView rlcNrUl;
    @BindView(R.id.rlc_lte_dl)
    TextView rlcLteDl;
    @BindView(R.id.rlc_lte_ul)
    TextView rlcLteUl;
    @BindView(R.id.mac_nr_dl)
    TextView macNrDl;
    @BindView(R.id.mac_nr_ul)
    TextView macNrUl;
    @BindView(R.id.mac_lte_dl)
    TextView macLteDl;
    @BindView(R.id.mac_lte_ul)
    TextView macLteUl;
    @BindView(R.id.phy_nr_dl)
    TextView phyNrDl;
    @BindView(R.id.phy_nr_ul)
    TextView phyNrUl;
    @BindView(R.id.phy_lte_dl)
    TextView phyLteDl;
    @BindView(R.id.phy_lte_ul)
    TextView phyLteUl;
    @BindView(R.id.nr_max)
    TextView nrMax;
    @BindView(R.id.nr_mean)
    TextView nrMean;
    @BindView(R.id.lte_max)
    TextView lteMax;
    @BindView(R.id.lte_mean)
    TextView lteMean;
    private LayoutInflater layoutInflater;
    private Context context;
    View rootView;
    private DecimalFormat df = new DecimalFormat("#,###.00");
    private  ENDCDataModel nullData=new ENDCDataModel();//这个是空数据，用于非测试时展示
    public CustomDLULView(Context context) {
        this(context, null);

    }

    public CustomDLULView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.layout_custom_dlul_data, null);
        addView(rootView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ButterKnife.bind(this);
        updateData(nullData);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.destroyDrawingCache();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void updateData(ENDCDataModel endcDataModel) {

        pdcpNrDl.setText("" + getValue(endcDataModel.getNR_Thr_DL_PDCP_Thr()));
        pdcpNrUl.setText("" + getValue(endcDataModel.getNR_Thr_UL_PDCP_Thr()));
        pdcpLteDl.setText("" + getValue(endcDataModel.getL_Thr_DL_PDCP_Thr()));
        pdcpLteUl.setText("" + getValue(endcDataModel.getL_Thr_UL_PDCP_Thr()));
        rlcNrDl.setText("" + getValue(endcDataModel.getNR_Thr_DL_RLC_Thr()));
        rlcNrUl.setText("" + getValue(endcDataModel.getNR_Thr_UL_RLC_Thr()));
        rlcLteDl.setText("" + getValue(endcDataModel.getL_Thr_DL_RLC_Thr()));
        rlcLteUl.setText("" + getValue(endcDataModel.getL_Thr_UL_RLC_Thr()));
        macNrDl.setText("" + getValue(endcDataModel.getNR_Thr_DL_MAC_Thr()));
        macNrUl.setText("" + getValue(endcDataModel.getNR_Thr_UL_MAC_Thr()));
        macLteDl.setText("" + getValue(endcDataModel.getL_Thr_DL_MAC_Thr()));
        macLteUl.setText("" + getValue(endcDataModel.getL_Thr_UL_MAC_Thr()));
        phyNrDl.setText("" + getValue(endcDataModel.getNR_Thr_DL_Phy_Thr()));
        phyNrUl.setText("" + getValue(endcDataModel.getNR_Thr_UL_Phy_Thr()));
        phyLteDl.setText("" + getValue(endcDataModel.getL_Thr_DL_Phy_Thr()));
        phyLteUl.setText("" + getValue(endcDataModel.getL_Thr_UL_Phy_Thr()));

        nrMax.setText("Max:" + getValue(ShowInfo.getInstance().getMaxNR()));
        nrMean.setText("Mean:" + getValue(ShowInfo.getInstance().getMeanNR()));
        lteMax.setText("Max:" + getValue(ShowInfo.getInstance().getMaxLte()));
        lteMean.setText("Mean:" + getValue(ShowInfo.getInstance().getMeanLte()));
    }

    public String getValue(float value) {
        if (value == -9999.0 || value == 0) {
            return "—";
        } else {
            String dd = df.format(value / 1024/1024);//因为得到单位是bps，所以/1024
            if (dd.equals(".00")||dd.equals("0.00")){
                dd="—";
            }else if((dd.startsWith("."))){
                dd="0"+dd;
            }
            return dd;
        }
    }
    public void refreshView(){
        /*非数据业务*/
        if (ApplicationModel.getInstance().getCurrentTask().getDataType()==0||ApplicationModel.getInstance().getCurrentTask().getDataType()==1
        ||ApplicationModel.getInstance().getCurrentTask().getDataType()==4){
            updateData(nullData);
            return;
        }
        if (ApplicationModel.getInstance().isTestJobIsRun() || ApplicationModel.getInstance().isTesting()) {
            ENDCDataModel endcDataModel = ShowInfo.getInstance().getEndcDataModel();
            updateData(endcDataModel);
        }else {
            updateData(nullData);
        }
    }
}

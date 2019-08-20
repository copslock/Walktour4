package com.walktour.gui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.framework.view.RefreshEventManager;
import com.walktour.framework.view.RefreshEventManager.RefreshEventListener;
import com.walktour.framework.view.RefreshEventManager.RefreshType;
import com.walktour.gui.R;
import com.walktour.gui.map.celllist.BaseCellListView;
import com.walktour.gui.map.celllist.CDMACellListView;
import com.walktour.gui.map.celllist.CatMCellListView;
import com.walktour.gui.map.celllist.ENDCCellListView;
import com.walktour.gui.map.celllist.GSMCellListView;
import com.walktour.gui.map.celllist.LTECellListView;
import com.walktour.gui.map.celllist.LTEGSMCellListView;
import com.walktour.gui.map.celllist.NBIoTCellListView;
import com.walktour.gui.map.celllist.TDCellListView;
import com.walktour.gui.map.celllist.WCDMACellListView;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class DynamicCellInfoFragment extends Fragment implements RefreshEventListener {

    private View layout;
//    private LayoutParams layoutParams;
    private Context mContext;
    private MyPgAdapter mPgAdapter;
    private ArrayList<BaseCellListView> mCellListViews = new ArrayList<>();

    public DynamicCellInfoFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onRefreshed(RefreshType refreshType, Object object) {
        switch (refreshType) {
            case ACTION_WALKTOUR_TIMER_CHANGED:
                onNetWorkChangeView();
                break;
            default:
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout= inflater.inflate(R.layout.layout_cellinfo_fragment,null);
//        layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ViewPager vp = (ViewPager) layout.findViewById(R.id.view_pager);
        mPgAdapter = new MyPgAdapter(mCellListViews);
        vp.setAdapter(mPgAdapter);
//		TextView initView = new TextView(this.mContext);
//		initView.setBackgroundColor(Color.parseColor("#FBFAFA"));
//		initView.setTag(1000);
//		layout.addView(initView, layoutParams);
        onNetWorkChangeView();
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RefreshEventManager.addRefreshListener(this);
    }

    /**
     * 根据网络改变邻区显示
     */
    private void onNetWorkChangeView() {
        CurrentNetState netType = !ApplicationModel.getInstance().isFreezeScreen() ? TraceInfoInterface.currentNetType
                : TraceInfoInterface.decodeFreezeNetType;
//		int tag = (int)layout.getChildAt(0).getTag();
        mCellListViews.clear();
        switch (netType) {
            case GSM:
                /*
					if(tag != 1001){

						layout.removeAllViews();
						layout.addView(gsmCellListView,layoutParams);
					}
					*/
                GSMCellListView gsmCellListView = new GSMCellListView(this.mContext);
                gsmCellListView.setTag(1001);
                mCellListViews.add(gsmCellListView);
                break;
            case LTE:
					/*if(tag != 1002){
						LTECellListView	 lteCellListView = new LTECellListView(this.mContext);
						lteCellListView.setTag(1002);
						layout.removeAllViews();
						layout.addView(lteCellListView,layoutParams);
					}*/
                LTECellListView lteCellListView = new LTECellListView(this.mContext);
                lteCellListView.setTag(1002);
                LTEGSMCellListView ltegsmCellListView = new LTEGSMCellListView(this.mContext);
                ltegsmCellListView.setTag(1007);
                mCellListViews.add(lteCellListView);
                mCellListViews.add(ltegsmCellListView);
                break;
            case WCDMA:
//					if(tag != 1003){
                WCDMACellListView wCellListView = new WCDMACellListView(this.mContext);
                wCellListView.setTag(1003);
                mCellListViews.add(wCellListView);
//						layout.removeAllViews();
//						layout.addView(wCellListView,layoutParams);
//					}
                break;
            case CDMA:
//					if(tag != 1004){
                CDMACellListView cCellListView = new CDMACellListView(this.mContext);
                cCellListView.setTag(1004);
                mCellListViews.add(cCellListView);

//						layout.removeAllViews();
//						layout.addView(cCellListView,layoutParams);
//					}
                break;
            case TDSCDMA:
//					if(tag != 1005){
                TDCellListView tCellListView = new TDCellListView(this.mContext);
                tCellListView.setTag(1005);
                mCellListViews.add(tCellListView);
//						layout.removeAllViews();
//						layout.addView(tCellListView,layoutParams);
//					}
                break;
            case NBIoT:
//                    if(tag != 1006){
                NBIoTCellListView nbCellListView = new NBIoTCellListView(this.mContext);
                nbCellListView.setTag(1006);
                mCellListViews.add(nbCellListView);
//                        layout.removeAllViews();
//                        layout.addView(nbCellListView,layoutParams);
//                    }
                    break;
            case CatM:
                CatMCellListView catMCellListView = new CatMCellListView(this.mContext);
                catMCellListView.setTag(1007);
                mCellListViews.add(catMCellListView);
                break;
            case ENDC:
                ENDCCellListView endcCellListView = new ENDCCellListView(this.mContext);
                endcCellListView.setTag(1008);
                lteCellListView = new LTECellListView(this.mContext);
                lteCellListView.setTag(1002);
                ltegsmCellListView = new LTEGSMCellListView(this.mContext);
                ltegsmCellListView.setTag(1007);
                mCellListViews.add(endcCellListView);
                mCellListViews.add(lteCellListView);
                mCellListViews.add(ltegsmCellListView);
                break;
            case NoService:
            case Unknown:
            default:
//					if(tag != 1002){   //出异常默认为lte
                LTECellListView lteCellListViewDef = new LTECellListView(this.mContext);
                lteCellListViewDef.setTag(1002);
                mCellListViews.add(lteCellListViewDef);
//						layout.removeAllViews();
//						layout.addView(lteCellListView,layoutParams);
//					}
                break;
        }
        mPgAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RefreshEventManager.removeRefreshListener(this);
    }

    private class MyPgAdapter extends PagerAdapter {

        private ArrayList<BaseCellListView> mCellViews;

        public MyPgAdapter(ArrayList<BaseCellListView> cellViews) {
            mCellViews = cellViews;
        }

        @Override
        public int getCount() {
            return mCellViews == null ? 0 : mCellViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView(mCellViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(mCellViews.get(position));
            return mCellViews.get(position);
        }
    }
}

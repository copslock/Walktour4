package com.walktour.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.Utils.AppVersionControl;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.SceneType;
import com.walktour.gui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2012-9-13] 
 */
public class WalkTourGridViewAdapter extends BaseAdapter{
    private ApplicationModel appModel = ApplicationModel.getInstance();
    private Context context;
    /**
     * 菜单列表
     */
    private List<GridMenuItem> mMenuItems = new ArrayList<>();

    /**
     * [构造简要说明]
     */
    public WalkTourGridViewAdapter(Context context) {
        super();
        this.context = context;
        if(appModel.isBeiJingTest()){
            this.initBeiJingGridView();
            return;
        }
        //感知测试
        if(appModel.getSelectScene() == SceneType.Perception && AppVersionControl.getInstance().isPerceptionTest()){

            //任务管理
            this.mMenuItems.add(new GridMenuItem(R.drawable.taskmananger_up, R.string.survey_task_manager));
            //勘测任务
            this.mMenuItems.add(new GridMenuItem(R.drawable.kancerenwu_up, R.string.survey_task_completion));
            //勘测查询
            this.mMenuItems.add(new GridMenuItem(R.drawable.kanchasearch, R.string.survey_task_query));
        }
        if(appModel.isHuaWeiTest()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_task,R.string.main_task_hw));
        }else{
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_task, R.string.main_task));
        }
        if (!(appModel.getSelectScene() == SceneType.SingleSite && appModel.isSingleStationTest())) {
            if (appModel.isTestJobIsRun() || appModel.isTestStoping()) {
                this.mMenuItems.add(new GridMenuItem(R.drawable.main_stop, R.string.main_stop));
            } else {
//                if(appModel.getSelectScene() == SceneType.Perception && appModel.isPerceptionTest()){
//                    //感知测试不需要开始测试
//                }else {
                    this.mMenuItems.add(new GridMenuItem(R.drawable.main_start, R.string.main_start));
//                }
            }
        }

        this.mMenuItems.add(new GridMenuItem(R.drawable.main_info,R.string.main_info));



        if(appModel.getSelectScene() == SceneType.Perception && AppVersionControl.getInstance().isPerceptionTest()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.singlestation_test, R.string.single_station_test));
            this.mMenuItems.add(new GridMenuItem(R.drawable.notice_up,R.string.title_notice));
        }

        this.mMenuItems.add(new GridMenuItem(R.drawable.main_setting,R.string.main_setting));
        this.mMenuItems.add(new GridMenuItem(R.drawable.main_data,R.string.main_file));
        if (appModel.getSelectScene() != SceneType.SingleSite && appModel.getSelectScene() != SceneType.Metro && appModel.getSelectScene() != SceneType.HighSpeedRail&& appModel.getSelectScene() != SceneType.Perception) {
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_gps, R.string.main_gps));
        }

        if(appModel.getSelectScene() != SceneType.Perception &&appModel.getSelectScene() != SceneType.SingleSite && appModel.getNetList().contains(WalkStruct.ShowInfoType.ShowTotal)){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_total,R.string.main_result_main));
        }
        if(appModel.getSelectScene() != SceneType.SingleSite && appModel.getNetList().contains(WalkStruct.ShowInfoType.Playback)){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_replay,R.string.data_replay));
        }

        //twq20120403 如果当前正在测试中，那么增加显示暂停测试按钮 twq20120924通过权限控制显示暂停
        if(!(appModel.getSelectScene() == SceneType.SingleSite && appModel.isSingleStationTest()) && appModel.getNetList().contains(WalkStruct.ShowInfoType.PauseTest)
                && appModel.isTestJobIsRun() && !appModel.isTestStoping()){
            if (appModel.isTestPause())
                this.mMenuItems.add(new GridMenuItem(R.drawable.main_continue, R.string.main_continue));
            else
                this.mMenuItems.add(new GridMenuItem(R.drawable.main_pause, R.string.main_pause));
        }
        if(appModel.getNetList().contains(WalkStruct.ShowInfoType.WOnePro)){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_workorder, R.string.work_order));
        }
        if(appModel.isAnHuiTest()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_workorder, R.string.work_order_ah));
        }
        if(appModel.isHuaWeiTest()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_workorder, R.string.work_order_hw));
        }
        if(appModel.isFuJianTest()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_workorder, R.string.work_order_fj));
        }

        if(appModel.getSelectScene() != SceneType.Perception &&appModel.getSelectScene() != SceneType.SingleSite && appModel.isIntelligentAnalysis()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_analysis, R.string.intelligent_analysis));
        }
        if(appModel.getSelectScene() == SceneType.SingleSite && appModel.isSingleStationTest()){
            this.mMenuItems.add(new GridMenuItem(R.drawable.singlestation_test, R.string.single_station_test));
            this.mMenuItems.add(new GridMenuItem(R.drawable.singlestation_survey, R.string.single_station_survey));
            this.mMenuItems.add(new GridMenuItem(R.drawable.singlestation_report, R.string.single_station_report));
        }

        if(appModel.getSelectScene() == SceneType.Perception && AppVersionControl.getInstance().isPerceptionTest()){
            //资源概览
            this.mMenuItems.add(new GridMenuItem(R.drawable.resourceoverview, R.string.single_station_test_resource));
            //质量概览
            this.mMenuItems.add(new GridMenuItem(R.drawable.qualityoverview, R.string.single_station_test_quality));
            //业务概览
            this.mMenuItems.add(new GridMenuItem(R.drawable.businessoverview, R.string.single_station_test_business));
            //GIS分析
            this.mMenuItems.add(new GridMenuItem(R.drawable.gis_analysis,R.string.preception_gis_analysis));
        }

//        this.mMenuItems.add(new GridMenuItem(R.drawable.singlestation_report,R.string.mos_test));
    }

    /**
     * 初始化北京测试功能九宫格界面
     */
    private void initBeiJingGridView() {
        if(appModel.isTestJobIsRun() || appModel.isTestStoping())
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_stop, R.string.main_stop));
        else
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_start, R.string.main_start));
        this.mMenuItems.add(new GridMenuItem(R.drawable.main_info,R.string.main_info));
        this.mMenuItems.add(new GridMenuItem(R.drawable.main_setting,R.string.main_setting));
        if (appModel.getNetList().contains(WalkStruct.ShowInfoType.ShowTotal)) {
            this.mMenuItems.add(new GridMenuItem(R.drawable.main_total,R.string.main_result_main));
        }
    }

    @Override
    public int getCount() {
        return this.mMenuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mMenuItems.get(position).mNameId;
    }

    @Override
    public long getItemId(int position) {
        return this.mMenuItems.get(position).mNameId;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.walktour_main_gridview_item, null);
            holder = new ViewHolder();
            holder.ItemText = (TextView)convertView.findViewById(R.id.ItemText);
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.ItemImage);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.ItemText.setText(mMenuItems.get(position).mNameId);
        holder.iconImageView.setImageResource(mMenuItems.get(position).mIconId);
        convertView.setId(mMenuItems.get(position).mNameId);
        return convertView;

    }

    static class ViewHolder{
        private TextView ItemText;
        private ImageView iconImageView;
    }

    /**
     * 菜单对象
     */
    private class GridMenuItem{
        /**
         * 菜单图标
         */
        int mIconId;
        /**
         * 菜单名称
         */
        int mNameId;

        GridMenuItem(@DrawableRes int iconId, @StringRes int nameId){
            this.mIconId = iconId;
            this.mNameId = nameId;
        }
    }
}

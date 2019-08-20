package com.walktour.gui.task.activity.phone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinglicom.listviewtest.ExpandCollapseAnimation;
import com.walktour.Utils.StringUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.DateTimePickDialogUtil;
import com.walktour.framework.view.DateTimePickDialogUtil.CallBackI;
import com.walktour.gui.R;
import com.walktour.gui.applet.MyKeyListener;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.TaskGroupConfig;
import com.walktour.gui.task.parsedata.model.base.TaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * 任务组适配器,控制动画等
 *
 * @author zhihui.lian
 */
@SuppressLint("InflateParams")
public class TaskGroupExpandAdapter extends BaseAdapter implements CallBackI {

    private List<TaskGroupConfig> list = new ArrayList<TaskGroupConfig>();

    private SparseBooleanArray deleteMap = new SparseBooleanArray(); // 删除map

    // 上下文
    private Context context;

    private View lastOpen = null;

    // 记住最后一次打开状态的位置
    private int lastOpenPosition = -1;

    // 存储点击打开的items
    private BitSet openItems = new BitSet();

    // 存储动画滑动的高度
    private final SparseIntArray viewHeights = new SparseIntArray(10);

    private TaskGroupConfig taskGroupConfig = null;


    private int mPosition = 0;

    // 删除模式
    private boolean isDelModel = false;

    private SparseBooleanArray mapExist = new SparseBooleanArray();

    // 构造器
    public TaskGroupExpandAdapter(Context context, List<TaskGroupConfig> list, SparseBooleanArray mapExist) {
        this.context = context;
        this.list = list;
        this.mapExist = mapExist;
    }

    public void setDelMode(boolean isDelModel) {
        this.isDelModel = isDelModel;
        if (isDelModel) {
            openItems.clear();
            deleteMap.clear();
        }
        notifyDataSetChanged();
    }

    public boolean isDelModel() {
        return isDelModel;
    }

    public SparseBooleanArray getDeleteMap() {
        return deleteMap;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.taskgroup_list_item, null);
            viewHolder.ItemTestable = (ImageButton) convertView.findViewById(R.id.ItemTestable);
            viewHolder.info_lny = (RelativeLayout) convertView.findViewById(R.id.info_lny);
            viewHolder.groupname_title_txt = (TextView) convertView.findViewById(R.id.groupname_title_txt);
            viewHolder.time_txt = (TextView) convertView.findViewById(R.id.time_txt);
            viewHolder.expand_img = (ImageView) convertView.findViewById(R.id.expand_img);
            viewHolder.visibe_gone_lny = (LinearLayout) convertView.findViewById(R.id.visibe_gone_lny);
            viewHolder.groupname_txt = (TextView) convertView.findViewById(R.id.groupname_txt);
//			viewHolder.groupname_rly = (RelativeLayout) convertView.findViewById(R.id.groupname_rly);
            viewHolder.grouptask_txt = (TextView) convertView.findViewById(R.id.grouptask_txt);
//			viewHolder.grouptask_rly = (RelativeLayout) convertView.findViewById(R.id.grouptask_rly);
            viewHolder.groupRepeatCount_txt = (TextView) convertView.findViewById(R.id.groupRepeatCount_txt);
            viewHolder.groupRepeatCount_rly = (RelativeLayout) convertView.findViewById(R.id.groupRepeatCount_rly);
            viewHolder.groupInterval_txt = (TextView) convertView.findViewById(R.id.group_interval_txt);
            viewHolder.groupInterval_rly = (RelativeLayout) convertView.findViewById(R.id.group_interval_rly);
            viewHolder.timeduan_lny = (LinearLayout) convertView.findViewById(R.id.timeduan_lny);
            viewHolder.timeduan_cbx = (CheckBox) convertView.findViewById(R.id.timeduan_cbx);
            viewHolder.delTestable = (CheckBox) convertView.findViewById(R.id.delTestable);
            viewHolder.keeptime_rly = (RelativeLayout) convertView.findViewById(R.id.keeptime_rly);
            viewHolder.keeptime_txt = (TextView) convertView.findViewById(R.id.keeptime_txt);
            viewHolder.starttime_rly = (RelativeLayout) convertView.findViewById(R.id.starttime_rly);
            viewHolder.starttime_txt = (TextView) convertView.findViewById(R.id.starttime_txt);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        taskGroupConfig = list.get(position);

        viewHolder.visibe_gone_lny.setVisibility(openItems.get(position) == true ? View.VISIBLE : View.GONE);
        viewHolder.expand_img.setImageResource(openItems.get(position) == true ? R.drawable.expander_ic_maximized_black
                : R.drawable.expander_ic_minimized_black);
        enableFor(viewHolder.expand_img, viewHolder.visibe_gone_lny, position, parent);
        viewHolder.groupname_title_txt.setText(taskGroupConfig.getGroupName());
        viewHolder.time_txt.setText(context.getString(R.string.act_task_group_time_solt) + taskGroupConfig.getTimeDuration().getTaskExecuteDuration().getDuration());
        viewHolder.groupRepeatCount_txt.setText("" + taskGroupConfig.getGroupRepeatCount());
        viewHolder.groupInterval_txt.setText("" + taskGroupConfig.getGroupInterval());
        // viewHolder.ItemTestable.setChecked(taskGroupConfig.isCheck());
        viewHolder.groupname_txt.setText(taskGroupConfig.getGroupName());
        viewHolder.grouptask_txt.setText(getTaskNameStr(taskGroupConfig.getTasks()));
        viewHolder.timeduan_cbx.setChecked(taskGroupConfig.getTimeDuration().isCheck());
        String keepTimeStr = taskGroupConfig.getTimeDuration().getTaskExecuteDuration().getDuration();
        viewHolder.keeptime_txt.setText(keepTimeStr.equals("0") ? context.getString(R.string.task_ping_unlimited_str) : ssToTimeFormat(keepTimeStr));
        viewHolder.starttime_txt.setText(taskGroupConfig.getTimeDuration().getTaskExecuteDuration().getStartTime());
        final ImageButton itemTestable = viewHolder.ItemTestable;

        itemTestable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 存储的xml里组
                TaskGroupConfig taskGroupConfigV = list.get(position);
                List<TaskGroupConfig> listGroup = TaskListDispose.getInstance().getCurrentGroups();
                List<TaskModel> lists = new LinkedList<TaskModel>();
                for (TaskGroupConfig group : listGroup) {
                    if (group.getGroupID().equals(taskGroupConfigV.getGroupID())) {
                        lists.addAll(group.getTasks());
                    }
                }

                if (taskGroupConfigV.isCheck()) {// 取消
                    itemTestable.setImageResource(R.drawable.btn_check_off);
                    mapExist.put(position, false);
                    for (TaskModel m : lists) {
                        m.setCheck(false);
                    }
                } else {// 选择
                    itemTestable.setImageResource(R.drawable.btn_check_on);
                    mapExist.put(position, true);
                    for (TaskModel m : lists) {
                        m.setCheck(true);
                    }
                }
                for (TaskGroupConfig group : listGroup) {
                    if (group.getGroupID().equals(taskGroupConfigV.getGroupID())) {
                        group.setCheck(mapExist.get(position));
                    }
                }
                TaskListDispose.getInstance().writeXml();
            }
        });

        // 存储的xml里组
        List<TaskGroupConfig> listGroup = TaskListDispose.getInstance().getCurrentGroups();
        List<TaskModel> lists = new LinkedList<TaskModel>();
        for (TaskGroupConfig group : listGroup) {
            if (taskGroupConfig.getGroupID().equals(group.getGroupID())) {
                lists.addAll(taskGroupConfig.getTasks());
            }
        }

        if (!mapExist.get(position)) {
            viewHolder.ItemTestable.setImageResource(R.drawable.btn_check_off);
        } else {
            viewHolder.ItemTestable.setImageResource(R.drawable.btn_check_on);
            // 是否存在组内有勾选的任务
            boolean isAllCheck = true;
            for (TaskModel m : lists) {
                if (!m.isCheck()) {
                    isAllCheck = false;
                    break;
                }
            }

            if (!isAllCheck) {
                viewHolder.ItemTestable.setImageResource(R.drawable.btn_check_onoff);
            }
        }

        final LinearLayout timeduanLny = viewHolder.timeduan_lny;
        viewHolder.timeduan_cbx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    timeduanLny.setVisibility(View.VISIBLE);
                } else {
                    timeduanLny.setVisibility(View.GONE);
                }
                list.get(position).getTimeDuration().setCheck(checkBox.isChecked());
                TaskListDispose.getInstance().writeXml();
            }
        });
        timeduanLny.setVisibility(viewHolder.timeduan_cbx.isChecked() ? View.VISIBLE : View.GONE);
        if (isDelModel) { // 删除模式
            viewHolder.delTestable.setVisibility(View.VISIBLE);
            viewHolder.ItemTestable.setVisibility(View.GONE);
            viewHolder.expand_img.setVisibility(View.GONE);
        } else {
            viewHolder.delTestable.setVisibility(View.GONE);
            viewHolder.ItemTestable.setVisibility(View.VISIBLE);
            viewHolder.expand_img.setVisibility(View.VISIBLE);
        }

        viewHolder.delTestable.setChecked(deleteMap.get(position));
        viewHolder.delTestable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteModeOnClick(position);
                myListener.onItemClick(v, position);
            }
        });

        viewHolder.info_lny.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteModeOnClick(position);
                myListener.onItemClick(v, position);
            }
        });

        viewHolder.keeptime_rly.setOnClickListener(new OnClickListener() { // 持续时间编辑

            @Override
            public void onClick(View v) {
                showDialog(v, position);
            }
        });
        viewHolder.groupRepeatCount_rly.setOnClickListener(new OnClickListener() { // 外循环次数编辑

            @Override
            public void onClick(View v) {
                showDialog(v, position);
            }
        });
        viewHolder.groupInterval_rly.setOnClickListener(new OnClickListener() { // 任务组间隔编辑

            @Override
            public void onClick(View v) {
                showDialog(v, position);
            }
        });

        final TextView startTimeTxt = viewHolder.starttime_txt;

        viewHolder.starttime_rly.setOnClickListener(new OnClickListener() { //  编辑时间

            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                String startTime = startTimeTxt.getText().toString();
                if (startTime.length() != 0) {
                    startTime = startTime.substring(0, 5);
                }
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(activity, startTime);
                dateTimePicKDialog.setOnClickListener(TaskGroupExpandAdapter.this);
                mPosition = position;
                dateTimePicKDialog.dateTimePicKDialog(startTimeTxt);
            }
        });


        return convertView;
    }

    /**
     * 根据所点击view显示Dialog
     *
     * @param view
     * @param position
     */
    private void showDialog(View view, final int position) {
        BasicDialog.Builder builder = new BasicDialog.Builder(context);
        final TaskGroupConfig taskGroupConfig = list.get(position);
        LayoutInflater factory = LayoutInflater.from(context);
        final View viewDialog = factory.inflate(R.layout.alert_dialog_edittext, null);
        final EditText editText = (EditText) viewDialog.findViewById(R.id.alert_textEditText);
        switch (view.getId()) {
            case R.id.keeptime_rly:
                editText.setSelectAllOnFocus(true);
                final TextView keepTimeTxt = (TextView) view.findViewById(R.id.keeptime_txt);
                editText.setText(taskGroupConfig.getTimeDuration().getTaskExecuteDuration().getDuration());
                editText.setKeyListener(new MyKeyListener().getNumberKeyListener());
                builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.task_wlanap_hold_timeout).setView(viewDialog)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
                                    keepTimeTxt.setText(Integer.valueOf(editText.getText().toString()) == 0 ? context.getString(R.string.task_ping_unlimited_str)
                                            : ssToTimeFormat(editText.getText().toString()));
                                    taskGroupConfig.getTimeDuration().getTaskExecuteDuration()
                                            .setDuration(editText.getText().toString());
                                    TaskListDispose.getInstance().writeXml();
                                }
                            }
                        }).setNegativeButton(R.string.str_cancle);
                break;

            case R.id.groupRepeatCount_rly:
                editText.setSelectAllOnFocus(true);
                final TextView repeatCount = (TextView) view.findViewById(R.id.groupRepeatCount_txt);
                editText.setText(taskGroupConfig.getGroupRepeatCount() + "");
                editText.setKeyListener(new MyKeyListener().getNumberKeyListener());
                builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.str_looptimes).setView(viewDialog)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
                                    repeatCount.setText(editText.getText().toString());
                                    taskGroupConfig.setGroupRepeatCount(Integer.valueOf(editText.getText().toString()));
                                    TaskListDispose.getInstance().writeXml();
                                }
                            }
                        }).setNegativeButton(R.string.str_cancle);
                break;
            case R.id.group_interval_rly:
                editText.setSelectAllOnFocus(true);
                final TextView tvGroupInterval = (TextView) view.findViewById(R.id.group_interval_txt);
                editText.setText(String.valueOf(taskGroupConfig.getGroupInterval()));
                editText.setKeyListener(new MyKeyListener().getNumberKeyListener());
                builder.setIcon(android.R.drawable.ic_menu_edit).setTitle(R.string.task_interVal).setView(viewDialog)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
                                    tvGroupInterval.setText(editText.getText().toString());
                                    taskGroupConfig.setGroupInterval(Integer.valueOf(editText.getText().toString()));
                                    TaskListDispose.getInstance().writeXml();
                                }
                            }
                        }).setNegativeButton(R.string.str_cancle);
                break;
            default:
                break;
        }
        builder.show();
    }


    /**
     * 秒转换为 HH:mm:ss
     *
     * @param ssTime
     */
    @SuppressLint("SimpleDateFormat")
    private String ssToTimeFormat(String ssTime) {
        long ssTimeLong = 0;
        if (!StringUtil.isNullOrEmpty(ssTime)) {
            try {
                ssTimeLong = Integer.valueOf(ssTime) * 1000;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        format.setTimeZone((TimeZone.getTimeZone("GMT+00:00")));
        return format.format(ssTimeLong);
    }

    /**
     * 删除执行操作
     *
     * @param position
     */
    private void deleteModeOnClick(int position) {
        if (isDelModel) { // 删除模式
            if (!deleteMap.get(position)) {
                deleteMap.put(position, true);
            } else if (!deleteMap.get(position)) {
                deleteMap.put(position, true);
            } else {
                deleteMap.put(position, false);
            }
            notifyDataSetChanged();
        }
    }

    public OnClickMyListener myListener;

    public void setOnClickListener(OnClickMyListener myListener) {
        this.myListener = myListener;
    }

    public interface OnClickMyListener {
        void onItemClick(View v, int position);
    }

    /**
     * 获取子列表任务名字组字符串
     *
     * @return
     */
    private String getTaskNameStr(List<TaskModel> taskModels) {
        String nameStr = "";
        try {
            for (int i = 0; i < taskModels.size(); i++) {
                if (taskModels.get(i).getEnable() == 1) {
                    nameStr += taskModels.get(i).getTaskName() + ",";
                }
            }
            nameStr = nameStr.length() != 0 ? nameStr.substring(0, nameStr.length() - 1) : nameStr;
        } catch (Exception e) {
            e.printStackTrace();
            nameStr = "";
        }
        return nameStr;
    }

    /**
     * 伸缩控制
     *
     * @param button
     * @param target
     * @param position
     * @param parent
     */
    private void enableFor(final View button, final View target, final int position, final ViewGroup parent) {
        if (target == lastOpen && position != lastOpenPosition) {
            lastOpen = null;
        }
        if (position == lastOpenPosition) {
            lastOpen = target;
        }
        int height = viewHeights.get(position, -1);
        if (height == -1) {
            viewHeights.put(position, target.getMeasuredHeight());
        }
        updateExpandable(target, position);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Animation a = target.getAnimation();
                target.measure(target.getWidth(), target.getHeight());
                if (a != null && a.hasStarted() && !a.hasEnded()) {

                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.performClick();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                } else {

                    target.setAnimation(null);

                    int type = target.getVisibility() == View.VISIBLE ? ExpandCollapseAnimation.COLLAPSE
                            : ExpandCollapseAnimation.EXPAND;
                    if (type == ExpandCollapseAnimation.EXPAND) {
                        openItems.set(position, true);
                    } else {
                        openItems.set(position, false);
                    }
                    if (type == ExpandCollapseAnimation.EXPAND) {
                        if (lastOpenPosition != -1 && lastOpenPosition != position) {
                            if (lastOpen != null) {
                                animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE, parent);
                            }
                            openItems.set(lastOpenPosition, false);
                        }
                        lastOpen = target;
                        lastOpenPosition = position;
                    } else if (lastOpenPosition == position) {
                        lastOpenPosition = -1;
                    }
                    animateView(target, type, parent);
                }

                ImageView img = (ImageView) view;
                img.setImageResource(openItems.get(position) == true ? R.drawable.expander_ic_maximized_black
                        : R.drawable.expander_ic_minimized_black);
            }
        });
    }

    /**
     * 更新伸缩
     *
     * @param target
     * @param position
     */
    private void updateExpandable(View target, int position) {
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) target.getLayoutParams();
        if (openItems.get(position)) {
            target.setVisibility(View.VISIBLE);
            params.bottomMargin = 0;
        } else {
            target.setVisibility(View.GONE);
            params.bottomMargin = 0 - viewHeights.get(position);
        }
    }

    /**
     * 调用伸展动画
     */
    private void animateView(final View target, final int type, final ViewGroup parent) {
        Animation anim = new ExpandCollapseAnimation(target, type);
        anim.setDuration(100);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (type == ExpandCollapseAnimation.EXPAND) {
                    if (parent instanceof ListView) {
                        ListView listView = (ListView) parent;
                        int movement = target.getBottom();

                        Rect r = new Rect();
                        boolean visible = target.getGlobalVisibleRect(r);
                        Rect r2 = new Rect();
                        listView.getGlobalVisibleRect(r2);

                        if (!visible) {
                            listView.smoothScrollBy(movement, 100);
                        } else {
                            if (r2.bottom == r.bottom) {
                                listView.smoothScrollBy(movement, 100);
                            }
                        }
                    }
                }
                TaskGroupExpandAdapter.this.notifyDataSetChanged();
            }
        });
        target.startAnimation(anim);
    }

    private class ViewHolder {
        public TextView starttime_txt;
        public RelativeLayout starttime_rly;
        public TextView keeptime_txt;
        public RelativeLayout keeptime_rly;
        public RelativeLayout groupRepeatCount_rly;
        public RelativeLayout groupInterval_rly;
        public LinearLayout timeduan_lny;
        public TextView groupRepeatCount_txt;
        public TextView groupInterval_txt;
        //		public RelativeLayout grouptask_rly;
        public TextView grouptask_txt;
        //		public RelativeLayout groupname_rly;
        public TextView groupname_txt;
        public LinearLayout visibe_gone_lny;
        public ImageView expand_img;
        public TextView time_txt;
        public TextView groupname_title_txt;
        public RelativeLayout info_lny; // title信息
        public ImageButton ItemTestable; // 测试选项
        public CheckBox timeduan_cbx; // 时间段选项
        public CheckBox delTestable; // 删除按钮
    }

    @Override
    public void onPositiveButton(View v) {
        TextView startTimeTxt = (TextView) v;
        TaskGroupConfig taskGroupConfig = list.get(mPosition);
        taskGroupConfig.getTimeDuration().getTaskExecuteDuration().setStartTime(String.format(startTimeTxt.getText().toString() + "%s", ":01"));
        startTimeTxt.setText(taskGroupConfig.getTimeDuration().getTaskExecuteDuration().getStartTime());
        TaskListDispose.getInstance().writeXml();
    }

}

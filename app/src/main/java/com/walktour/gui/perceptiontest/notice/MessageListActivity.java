package com.walktour.gui.perceptiontest.notice;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ServerManager;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.perceptiontest.notice.adapter.MessageListAdapter;
import com.walktour.gui.perceptiontest.notice.bean.MessageBean;
import com.walktour.gui.perceptiontest.notice.dao.MessageDaoUtil;
import com.walktour.gui.perceptiontest.notice.net.FeelRetrofitManager;
import com.walktour.gui.setting.Sys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author jinfeng.xie
 * @data 2018-11-18
 */
public class MessageListActivity extends AppCompatActivity {
    private static final String TAG = "MessageListActivity";
    @BindView(R.id.rv_message_list)
    RecyclerView messageList;
    @BindView(R.id.title_txt)
    TextView titleTxt;
    @BindView(R.id.tv_unread)
    TextView tvUnread;
    @BindView(R.id.read_line)
    View readLine;
    @BindView(R.id.tv_read)
    TextView tvRead;
    @BindView(R.id.unread_line)
    View unreadLine;
    /**
     * 服务管理类
     */
    private ServerManager mServerManager;
    private MessageListAdapter mAdapter;
    private int currentMode = IS_UNREADED;
    private static int IS_UNREADED = 0;
    private static int IS_READED = 1;
    private MessageDaoUtil sqlUtil;
    private List<MessageBean> lists;
    private FeelRetrofitManager httpManger;//网络请求
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        titleTxt.setText(R.string.title_notice);
        switchButton(IS_UNREADED);
        this.mServerManager = ServerManager.getInstance(this);
    }

    private void initData() {
        sqlUtil = new MessageDaoUtil(this);
        /**
         * 如果表格為0時候，進去模擬數據
         */
//        lists = sqlUtil.queryAllmessage();
//        if (lists.size() == 0) {
//            sqlUtil.insertMessage(new MessageBean(new Random().nextLong(), "中国联通兵王卡开启新一轮厮杀", DateUtil.getCurrentTimes(),"admin",
//                    "经过多方信息搜索，实际上包括中国移动和中国电信在内，都在局部范围针对特定群体用户开展了大流量+大语音低价套餐营销。这种套餐的杀伤力非常大，可以说是杀敌一千自损八百，如果经营不够细致甚至还会出现杀敌八百自损一千的可能。因此，如果不到迫不得已，" +
//                            "这种套餐短期内大面积推广的可能性不高。但是既然这种套餐已经低价入市，那么开放所有用户办理的可能性就会存在。提前做好准备，我们认为是有必要的。", false));
//        }
        mAdapter = new MessageListAdapter(this);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        //添加Android自带的分割线
        messageList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        messageList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MessageBean mCurrentMessage = mAdapter.getList().get(i);
                showDialog(mCurrentMessage);
                LogUtil.e(TAG, "已经点击了：" + mCurrentMessage);
            }
        });
        getData(IS_UNREADED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.pointer)
    public void onViewClicked() {
        finish();
    }
    @OnClick({R.id.rl_unread, R.id.rl_read,R.id.pointersetting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_unread:
                currentMode = IS_UNREADED;
                switchButton(IS_UNREADED);
                getData(IS_UNREADED);
                break;
            case R.id.rl_read:
                currentMode = IS_READED;
                switchButton(IS_READED);
                getData(IS_READED);
                break;
            case R.id.pointersetting://登錄
//                currentMode = IS_READED;
//                switchButton(IS_READED);
//                showLoginServerDialog();
//                getData(IS_READED);
                break;
        }
    }
//    /**
//     * 显示登录服务器对话框
//     */
//    private void showLoginServerDialog() {
//        String ip = this.mServerManager.getDownloadFleetIp();
//        if (!this.mServerManager.getFleetServerType() || StringUtil.isNullOrEmpty(ip)) {
//            ToastUtil.showToastShort(this,R.string.work_order_fleet_ip_null);
//            jump2SettingActivity();
//            return;
//        }
//        int port = this.mServerManager.getDownloadFleetPort();
//        if (!this.mServerManager.getFleetServerType() || !Verify.isPort(String.valueOf(port))) {
//           ToastUtil.showToastShort(this,R.string.work_order_fleet_port_invalid);
//            jump2SettingActivity();
//            return;
//        }
//        View view= LayoutInflater.from(this).inflate(R.layout.dialog_single_station_login,null);
//        final EditText loginUser= (EditText) view.findViewById(R.id.login_user);
//        final EditText loginPassword= (EditText) view.findViewById(R.id.login_password);
//        loginUser.setText(mServerManager.getFleetAccount());
//        loginPassword.setText(mServerManager.getFleetPassword());
//        new BasicDialog.Builder(this).setTitle(getString(R.string.single_station_login_server)).
//                setView(view).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        }).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dealDialogCallBackValues(loginUser.getText().toString(),loginPassword.getText().toString());
//            }
//        }).show();
//    }
    /**
     * 跳转到服务器设置界面
     */
    private void jump2SettingActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt(Sys.CURRENTTAB, 0);
        Intent intent = new Intent(this,Sys.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    //不需要登錄了
//    public void dealDialogCallBackValues(String loginUser,String loginPassword) {
//        String ip = this.mServerManager.getDownloadFleetIp();
//        int port = this.mServerManager.getDownloadFleetPort();
//        this.mServerManager.setFleetAccount(loginUser);
//        this.mServerManager.setFleetPassword(loginPassword);
//        httpManger=FeelRetrofitManager.getInstance(ip,port);
//        this.httpManger.login(this,loginUser, loginPassword, new SimpleCallBack() {
//            @Override
//            public void onSuccess() {
//                ToastUtil.showToastShort(MessageListActivity.this,R.string.single_station_login_success);
//            }
//
//            @Override
//            public void onFailure(String message) {
//                ToastUtil.showToastShort(MessageListActivity.this,R.string.single_station_login_fail);
//            }
//        });
//    }
    /**
     * 0为未读，1为已读
     * 更新数据
     *
     * @param isReaded
     */
    private void getData(int isReaded) {
        if (isReaded == IS_UNREADED) {
            lists = sqlUtil.querymessageByIsReaded(false);
            mAdapter.replaceList(lists);
            mAdapter.notifyDataSetChanged();
        } else if (isReaded == IS_READED) {
            lists = sqlUtil.querymessageByIsReaded(true);
            mAdapter.replaceList(lists);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showDialog(final MessageBean message) {
        new BasicDialog.Builder(this).setTitle("" + message.getTitle())
                .setMessage(message.getContent()).setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                message.setIsRead(true);
                sqlUtil.updatemessage(message);
                getData(currentMode);
            }
        }).show();
    }

    /**
     * 改变按钮状态
     * 0为未读，1为已读
     *
     * @param i
     */
    @SuppressLint("NewApi")
    private void switchButton(int i) {
        readLine.setVisibility(View.GONE);
        unreadLine.setVisibility(View.GONE);
        tvRead.setTextColor(getColor(R.color.app_tag_text));
        tvUnread.setTextColor(getColor(R.color.app_tag_text));
        switch (i) {
            case 0:
                unreadLine.setVisibility(View.VISIBLE);
                tvUnread.setTextColor(Color.WHITE);
                break;
            case 1:
                readLine.setVisibility(View.VISIBLE);
                tvRead.setTextColor(Color.WHITE);
                break;
        }
    }


}

package com.walktour.gui.singlestation.test.fragment;

import android.os.Bundle;
import android.widget.EditText;

import com.walktour.base.gui.fragment.BaseDialogFragment;
import com.walktour.gui.R;
import com.walktour.gui.R2;

import butterknife.BindView;

/**
 * 登录服务器对话框
 * Created by wangk on 2017/8/9.
 */

public class LoginServerDialogFragment extends BaseDialogFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "LoginServerDialogFragment";
    /**
     * 文件列表
     */
    @BindView(R2.id.login_user)
    EditText mLoginUser;
    /**
     * 文件列表
     */
    @BindView(R2.id.login_password)
    EditText mLoginPassword;

    public LoginServerDialogFragment() {
        super(R.string.single_station_login_server, R.layout.dialog_single_station_login);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    protected void setShowValues() {
        this.mLoginUser.setText(this.getStringBundle("login_user"));
        this.mLoginPassword.setText(this.getStringBundle("login_password"));
    }

    @Override
    protected Bundle setCallBackValues() {
        Bundle bundle = new Bundle();
        bundle.putString("login_user", this.mLoginUser.getText().toString());
        bundle.putString("login_password", this.mLoginPassword.getText().toString());
        return bundle;
    }
}

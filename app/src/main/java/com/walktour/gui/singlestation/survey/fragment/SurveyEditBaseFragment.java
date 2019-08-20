package com.walktour.gui.singlestation.survey.fragment;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;

import com.walktour.Utils.StringUtil;
import com.walktour.base.gui.fragment.BaseFragment;
import com.walktour.gui.R;

/**
 * Created by yi.lin on 2017/9/4.
 * <p>
 * 勘察基站编辑界面基类
 */

public abstract class SurveyEditBaseFragment extends BaseFragment {

    /**
     * @param titleId          标题ID
     * @param fragmentLayoutId 视图关联的布局资源ID
     */
    public SurveyEditBaseFragment(@StringRes int titleId, @LayoutRes int fragmentLayoutId) {
        super(titleId, fragmentLayoutId);
    }

    @Override
    public int[] showActivityMenuItemIds() {
        return new int[]{R.id.menu_singlestation_login, R.id.menu_singlestation_upload};
    }

    /**
     * 获取文本控件的文本内容
     *
     * @param textView 文本控件
     * @return 文本内容
     */
    protected String getTextContent(@NonNull TextView textView) {
        return textView.getText().toString().trim();
    }

    /**
     * 获取原始值的显示文本
     *
     * @param oldValue 原始值
     * @return 显示文本
     */
    protected String getOldValueShow(String oldValue) {
        if (StringUtil.isNullOrEmpty(oldValue) || "0".equals(oldValue) || "0.0".equals(oldValue))
            return "";
        return "(" + oldValue + ")";
    }
}

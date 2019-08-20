package com.walktour.base.gui.fragment;

/**
 * 基础视图桩类
 * Created by wangk on 2017/4/7.
 */

public abstract class BaseHolder<T> {

    /**
     * 设置要显示的数据
     *
     * @param position 当前item的位置
     * @param data     数据，转换成对应的数据对象
     */
    public abstract void setData(int position, T data);

//    /**
//     * 对于视图的点击不做任何处理，用于处理无需响应控件呈现点击表现
//     */
//    public abstract void onClickNothing();
}

package com.walktour.gui.newmap2.filter;

import com.walktour.framework.database.model.BaseStation;

/**
 * 参数类型过滤
 *
 * @author zhicheng.chen
 * @date 2019/3/15
 */
public interface IParamFilter {

    /**
     * 获取网络类型
     *
     * @return
     */
    int getNetType();

    /**
     * 获取第一个过滤参数的名称
     *
     * @return
     */
    String getFirstParamName();

    /**
     * 获取第二个过滤参数的名称
     *
     * @return
     */
    String getSecondParamName();

    /**
     * 保存第一个参数的过滤范围
     *
     * @param min
     * @param max
     */
    void saveFirstParamRange(int min, int max);

    /**
     * 保存第二个参数的过滤范围
     *
     * @param min
     * @param max
     */
    void saveSecondParamRange(int min, int max);

    /**
     * 获取第一个参数的最小值
     *
     * @return
     */
    int getFirstParamMin();

    /**
     * 获取第一个参数的最大值
     *
     * @return
     */
    int getFirstParamMax();

    /**
     * 获取第二个参数的最小值
     *
     * @return
     */
    int getSecondParamMin();

    /**
     * 获取第二个参数的最大值
     *
     * @return
     */
    int getSecondParamMax();

    /**
     * 重置第一个参数
     */
    void resetFirstParam();

    /**
     * 重置第二个参数
     */
    void resetSecondParam();

    /**
     * 过滤
     * @return
     */
    boolean filter(BaseStation station);
}

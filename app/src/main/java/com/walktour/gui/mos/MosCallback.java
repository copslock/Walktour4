package com.walktour.gui.mos;

/**
 * Mos 回调接口
 *
 * @author zhicheng.chen
 * @date 2019/3/26
 */
public interface MosCallback<T> {
    void handleResult(T t);
}

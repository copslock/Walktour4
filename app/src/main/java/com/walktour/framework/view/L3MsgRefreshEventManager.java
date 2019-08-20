package com.walktour.framework.view;

import java.util.ArrayList;
import java.util.List;

public class L3MsgRefreshEventManager {

    private static L3MsgRefreshEventManager INSTANCE;
    private List<L3MsgRefreshEventListener> mListener = new ArrayList<>();

    public static L3MsgRefreshEventManager getInstance() {
        synchronized (L3MsgRefreshEventManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new L3MsgRefreshEventManager();
            }
        }
        return INSTANCE;
    }

    public void registerL3MsgRefreshEventListener(L3MsgRefreshEventListener listener) {
        if (listener != null && !mListener.contains(listener)) {
            mListener.add(listener);
        }
    }

    public void removeL3MsgRefreshEventListener(L3MsgRefreshEventListener listener) {
        if (listener != null && mListener.contains(listener)) {
            mListener.remove(listener);
        }
    }

    public void notifyL3MsgRefreshed(String actionType, String content) {
        for (L3MsgRefreshEventListener lt : mListener) {
            lt.onL3MsgRefreshed(actionType, content);
        }
    }

    public interface L3MsgRefreshEventListener {
        void onL3MsgRefreshed(String actionType, String content);
    }
}

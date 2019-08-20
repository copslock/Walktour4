package com.walktour.gui.setting.customevent.msg;

import android.annotation.SuppressLint;

import com.walktour.Utils.UnifyL3Decode;
import com.walktour.gui.setting.customevent.BaseCustomEventAdapter;
import com.walktour.gui.setting.customevent.model.CustomEventMsg;
import com.walktour.model.TdL3Model;

import java.util.List;
import java.util.Locale;

/**
 * 自定义信令事件列表适配类
 *
 * @author jianchao.wang
 */
@SuppressLint("InflateParams")
public class CustomEventMsgAdapter extends BaseCustomEventAdapter<CustomEventMsg> {

    public CustomEventMsgAdapter(CustomEventMsgListActivity context, int textViewResourceId, List<CustomEventMsg> objects,
                                 boolean isCheckMode) {
        super(context, textViewResourceId, objects, isCheckMode);
    }

    @Override
    protected String getDescription(CustomEventMsg define) {
        String content = "";
        TdL3Model msg1 = UnifyL3Decode.disposeL3Info(define.getL3MsgID1());
        TdL3Model msg2 = UnifyL3Decode.disposeL3Info(define.getL3MsgID2());
        content = msg1.getL3Msg() + "\n" + msg2.getL3Msg();
        if ((msg1.getId() > 0 || msg2.getId() > 0) && define.getInterval() > 0) {
            if (define.isCompare()) {
                String delay = String.format(Locale.getDefault(), "\n%s %.2fS", define.getComapreStr(),
                        define.getInterval() / 1000f);
                content += delay;
            }
        }
        return content;
    }
}

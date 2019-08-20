package com.dinglicom.dataset;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.dinglicom.ResourceCategory;
import com.dinglicom.dataset.model.DataSetEvent;
import com.dinglicom.dataset.model.EventModel;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.EventBytes;
import com.walktour.Utils.RcuEventCommand;
import com.walktour.Utils.TotalDataByGSM;
import com.walktour.Utils.TotalStruct.TotalCustomEvent;
import com.walktour.Utils.UnifyL3Decode;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.dataset.DataSetNode;
import com.walktour.Utils.dataset.DataSetSignalXmlTools;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.control.instance.AlertManager;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.CustomEventFactory;
import com.walktour.gui.setting.customevent.model.CustomEventMsg;
import com.walktour.model.TdL3Model;
import com.walktour.model.TotalSpecialModel;

import org.andnav.osm.util.GeoPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 事件管理器,负责所有业务测试事件的管理
 *
 * @author qihang.li
 */
@SuppressLint("UseSparseArrays")
public class EventManager {

    public static final Map<Long, DataSetNode> MAP = DataSetSignalXmlTools.getInstance().getMap();
    private static final String TAG = "EventManager";

    /**
     * 事件列表的最大值
     */
    public static final int EVENT_MAX = 300;
    /**
     * 事件清除
     */
    public static final int MSG_EVENT_CLEAR = -1;
    /**
     * 事件增加
     */
    public static final int MSG_EVENT_ADD = 0;
    /**
     * 回放时添加全部事件
     */
    public static final int MSG_EVENT_ADD_ALL = 1;
    /**
     * 当前采样点变化
     */
    public static final int MSG_INDEX_CHANGE = 2;
    /**
     * 事件过滤设置
     */
    public static final int MSG_EVENT_FILTER = 3;

    private static EventManager instance = null;

    /**
     * 所有定义好的层3信令
     */
    private List<TdL3Model> mL3MsgList = new ArrayList<TdL3Model>();

    /**
     * 业务测试界面的事件,有回放，关于于数据集和业务测试
     */
    private List<EventModel> mEventList = new ArrayList<EventModel>();

    private List<EventModel> mEventFreezeBak = new ArrayList<EventModel>();

    private final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
    /**
     * 满足自定义事件中第一条的层3信令
     */
    private List<TdL3Model> customMsg1 = new ArrayList<TdL3Model>();

    private Handler mHandler = null;
    /**
     * 常规设置配置文件
     */
    private ConfigRoutine configRoutine;

    /**
     * 防止外部构造
     */
    private EventManager() {
        configRoutine = ConfigRoutine.getInstance();
    }

    /**
     * 单例模式
     * @return
     */
    public synchronized static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public String getEventStr(int eventId) {
        return eventMap.get(eventId);
    }

    /**
     * 获取实体类的所有属性，返回ET_开头的事件Field数组
     */
    private ArrayList<Field> getEventFieldNames() {
        // 拿到该类
        // Class<?> clz = DataSetEvent.getClass();
        Field[] fields = DataSetEvent.class.getDeclaredFields();
        ArrayList<Field> fieldList = new ArrayList<Field>();
        for (Field f : fields) {
            Type type = f.getGenericType();
            String className = type.toString();
            if (className.equals("int")) {
                if (f.getName().startsWith("ET_")) {
                    fieldList.add(f);
                }
            }
        }
        return fieldList;
    }

    /**
     * 事件id和事件显示名称对应的Map
     */
    private static Map<Integer, String> eventMap = new HashMap<Integer, String>();
    /**
     * 事件id和事件关键名称对应的Map
     */
    private static Map<Integer, String> eventKeyMap = new HashMap<Integer, String>();

    public Map<Integer, String> getEventMap() {
        return eventMap;
    }

    /**
     * 获取事件id和事件关键名称对应的Map
     *
     * @return
     */
    public Map<Integer, String> getEventKeyMap() {
        return eventKeyMap;
    }
    /**
     * -------注意这是一个读取I/O的函数，要在独立线程里执行--------
     * 获取所有EventManager.ET_*事件的field名，和数据集的CommonDataSetEventResource.h文件对比
     * 把该文件里的事件显示、事件类型、事件ID关联起来,并放到HashMap里
     */
//    private HashMap<String, String> getEventHashMap(Context context) {
//        HashMap<String, String> map = new HashMap<String, String>();
//        // 读取CommonDataSetEventResource.h文件
//        File file = AppFilePathUtil.getInstance().getAppConfigFile("commondataseteventresource.h");
//        FileInputStream inStream = null;
//        BufferedReader reader = null;
//        try {
//            inStream = new FileInputStream(file);
//            String line;
//            reader = new BufferedReader(new InputStreamReader(inStream));
//            while (true) {
//                line = reader.readLine();
//                if (line != null) {
//                    if (line.trim().length()>0&&(!line.trim().startsWith("//"))) {// 忽略注释掉的
//                        if (line.contains("{") && line.contains("}") && line.contains("\"") && line.contains(",")) {
//                            String[] paras = line.substring(1, line.lastIndexOf("}")).split(",");
//                            String key = paras[3].trim();
//                            String value = paras[0].trim();
//                            value = value.substring(value.indexOf("\"") + 1, value.length() - 1).trim();
//                            map.put(key, value);
//                        }
//                    }
//                } else {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//                if (inStream != null) {
//                    inStream.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return map;
//    }
    /**
     * 初始化层3事件列表(用于自定义事件) 解析unifyl3decode.java文件
     */
    private void initL3Msg(Context context) {
        Map<Long, DataSetNode> map = MAP;
        Iterator<Map.Entry<Long, DataSetNode>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Long, DataSetNode> entry = entries.next();

            Long msgCode = entry.getKey();
            long msgHead = msgCode >> 16;
            msgHead = msgHead & 0x0000FFFF;
            if (msgHead != 0xd090 && msgHead != 0xfff0 && msgHead != 0xee03) {
                TdL3Model l3 = UnifyL3Decode.disposeL3Info(msgCode);

                if (!l3.getL3Msg().equals("")) {
                    mL3MsgList.add(l3);
                }
            }
        }
    }

    /**
     * 初始化事件,把事件ID和事件显示名称一一对应起来,程序启动的时候要运行
     *
     * @param context
     */
    public void initEevnts(Context context) {
        LogUtil.d(TAG, "-----initEevnts----- start");
        ArrayList<Field> fieldList = getEventFieldNames();
//        HashMap<String, String> map = getEventHashMap(context);
        for (Field field : fieldList) {
            try {
                int key = field.getInt(this);
//                String value=DatasetManager.getInstance(context).getEventName(key);
                String value= ResourceCategory.getInstance().GetEventStandardName(ApplicationModel.getInstance().getHandler_event(), key);
//                String value = map.get(field.getName());
                if (value != null) {
                    eventMap.put(key, value);
                    eventKeyMap.put(key, field.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        initL3Msg(context);

        LogUtil.d(TAG, "-----initEevnts----- end");
    }

    /**
     * 测试时增加一个事件
     *
     * @param sortByPointIndex 是否要按事件的采样点index排序号
     */
    public synchronized void addEvent(Context context, EventModel event, boolean sortByPointIndex) {
        synchronized (mEventList) {
            //如果暂停测试了，并且设置了停止无数据，则不输出日记
//            if (configRoutine.isPuaseNoData(context) && ApplicationModel.getInstance().isTestPause() &&!event.getEventStr().contains("Logging")) {
//                return;
//            }
            if (!event.getEventStr().equals("")) {
                if (event.getType() == EventModel.TYPE_DEFINE && event.isAlarm()) { // 加入自定义事件声音告警
                    AlertManager.getInstance(context).speak(event.getEventStr());
                }

                if (sortByPointIndex && event.getPointIndex() > 0) {
                    for (int i = mEventList.size() - 1; i > 0; i--) {
                        EventModel lastModel = mEventList.get(i);
                        if (event.getTime() > lastModel.getTime()) {
                            mEventList.add(i, event);
                            // 确保事件列表更新之后发送到界面刷新
                            if (mHandler != null) {
                                mHandler.obtainMessage(MSG_EVENT_ADD).sendToTarget();
                            }
                            break;
                        }
                    }
                } else {
                    mEventList.add(event);
                    // 确保事件列表更新之后发送到界面刷新
                    if (mHandler != null) {
                        mHandler.obtainMessage(MSG_EVENT_ADD).sendToTarget();
                    }
                }

                if (mEventList.size() > EVENT_MAX) {
                    while (mEventList.size() > EVENT_MAX) {
                        mEventList.remove(0);
                        // 确保事件列表更新之后发送到界面刷新
                        if (mHandler != null) {
                            mHandler.obtainMessage(MSG_EVENT_CLEAR).sendToTarget();
                        }
                    }
                }

                LogUtil.i(TAG, "--event:" + event.getEventStr());

                if (!event.getEventStr().trim().equals("")) {
                    // 发送到iPad端
                    Intent eventIntent = new Intent(WalkMessage.MutilyTester_Send_Event);
                    eventIntent.putExtra("eventTimes", event.getTime());
                    eventIntent.putExtra("eventValue", event.getEventStr());
                    context.sendBroadcast(eventIntent);
                }
            }
        }
    }

    /**
     * 添加一个只用于显示的事件,只有时间属性的事件
     *
     * @param event
     */
    public synchronized EventModel addEvent(Context context, String event) {
        EventModel model = new EventModel(System.currentTimeMillis(), event, EventModel.TYPE_STANDER);
        addEvent(context, model, false);
        return model;
    }

    public synchronized void addTagEvent(Context mContext, long lableTime, String eventStr) {
        short len = 0;
        try {
            len = (short) eventStr.getBytes(UtilsMethod.CharSet_UTF_8).length;
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventBytes.Builder(mContext, RcuEventCommand.TAG_EVENT).addShort(len).addCharArray(eventStr)
                .writeToRcu(lableTime * 1000);
    }

    /**
     * 添加一个空行事件
     */
    public synchronized void addSplitLine(Context context) {
        EventModel model = new EventModel(System.currentTimeMillis(), "", EventModel.TYPE_STANDER);
        addEvent(context, model, false);
    }

    /**
     * 回放时添加所有事件
     */
    public synchronized void addReplayEvents(List<EventModel> newEventList) {
        synchronized (mEventList) {
            mEventList.clear();
            // 确保事件列表更新之后发送到界面刷新
            if (mHandler != null) {
                mHandler.obtainMessage(MSG_EVENT_CLEAR).sendToTarget();
            }

            mEventList.addAll(newEventList);
            // 确保事件列表更新之后发送到界面刷新
            if (mHandler != null) {
                mHandler.obtainMessage(MSG_EVENT_ADD_ALL).sendToTarget();
            }
        }
    }

    public synchronized void setFreezeEvent() {
        mEventFreezeBak.clear();
        for (EventModel event : mEventList) {
            mEventFreezeBak.add(event);
        }
    }

    public synchronized void setUnFreezeEvent() {
        mEventFreezeBak.clear();
        if (mHandler != null) {
            mHandler.obtainMessage(MSG_EVENT_ADD).sendToTarget();
        }
    }

    public synchronized List<EventModel> getEventList() {
        if (ApplicationModel.getInstance().isFreezeScreen()) {
            return mEventFreezeBak;
        }
        return mEventList;
    }

    /**
     * 开始测试时清除 几个事件列表
     */
    public synchronized void clearEvents() {
        // 事件列表
        this.mEventList.clear();
        if (mHandler != null) {
            mHandler.obtainMessage(MSG_EVENT_CLEAR).sendToTarget();
        }
        CustomEventFactory.getInstance().clearEvents();
        // 自定义事件判断信令
        this.customMsg1.clear();

    }

    /**
     * 发送事件到主被叫独立进程
     *
     * @param context
     * @param event
     */
    public void sentEventBroadcast(Context context, EventModel event) {
        LogUtil.d(TAG, "----sentEventBroadcast----index:" + event.getEventIndex() + "----event:" + event.getEventStr() + "----time:" + df.format(new Date(event.getTime())));
        Intent callEvent = new Intent(WalkMessage.ACTION_EVENT);
        callEvent.putExtra(WalkMessage.KEY_EVENT_RCUID, event.getRcuId());
        callEvent.putExtra(WalkMessage.KEY_EVENT_TIME, event.getTime());
        callEvent.putExtra(WalkMessage.KEY_EVENT_STRING, event.getEventStr());
        callEvent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(callEvent);
    }

    public void regeditEventChangeHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void unRegeditEventChangeHandler() {
        this.mHandler = null;
    }

    /**
     * @return 自定义事件中有到的所有层3事件(包含id和string)
     */
    public List<TdL3Model> getCustomL3List() {
        return this.mL3MsgList;
    }

    /**
     * 根据ID号返回定义好的层3信令
     *
     * @param id 信令ID号
     * @return 没有对应信令时返回一个空的
     */
    public TdL3Model getCustomL3ById(long id) {
        for (TdL3Model m : mL3MsgList) {
            if (m.getId() == id) {
                return m;
            }
        }

        TdL3Model model = new TdL3Model();
        model.setId(0);
        return model;
    }

    /**
     * 根据L3信令判断自定义事件
     *
     * @param l3Model 新增加的层3信令
     * @return 层3信令是否触发的自定义事件, 有可能是多个
     */
    public synchronized List<EventModel> l3Msg2CustomEvent(Context context, TdL3Model l3Model, boolean sortByTime) {

        ArrayList<EventModel> result = new ArrayList<EventModel>();
        List<CustomEventMsg> customDefineList = CustomEventFactory.getInstance().getCustomEventMsgList();
        for (CustomEventMsg define : customDefineList) {
            // 单独信令事件
            if ((l3Model.getId() == define.getL3MsgID1() && define.getL3MsgID2() <= 0)
                    || (l3Model.getId() == define.getL3MsgID2() && define.getL3MsgID1() <= 0)) {

                String eventStr = define.getName();
                EventModel model = new EventModel(l3Model.getTime(), eventStr, EventModel.TYPE_DEFINE);
                model.setCustomEventName(define.getName());
                model.setPointIndex(l3Model.getPointIndex());
                model.setAlarm(define.isShowAlarm());
                model.setShowOnChart(define.isShowChart());
                model.setShowOnMap(define.isShowMap());
                model.setShowOnTotal(define.isShowTotal());
                model.setIconDrawablePath(define.getIconFilePath());

                addEvent(context, model, sortByTime);// 回放时需要回溯按时间排序
                Log.i(TAG, "--自定义信令--1");
                result.add(model);
            }
            // 组合两条信令的事件 信令1
            else if (l3Model.getId() == define.getL3MsgID1() && define.getL3MsgID2() > 0) {
                TdL3Model msg1 = l3Model;
                if (!customMsg1.contains(msg1)) {
                    customMsg1.add(msg1);
                    Log.i(TAG, "--自定义信令---2");
                    // 超出最大时延的msg1都清理掉
                    while ((customMsg1.get(0).getTime() - msg1.getTime()) > CustomEventFactory.getInstance().customMaxDelay) {
                        customMsg1.remove(0);
                    }
                }
            }
            // 组合两条信令的事件 信令2
            else if (l3Model.getId() == define.getL3MsgID2() && define.getL3MsgID1() > 0) {
                TdL3Model msg1Model = lookBackMsgList(define.getL3MsgID1());
                TdL3Model msg2Model = l3Model;
                if (msg1Model != null) {
                    int delay = (int) (msg2Model.getTime() - msg1Model.getTime());
                    boolean gen = define.hasGenerateEvent(msg1Model.getId(), msg2Model.getId(), delay);
                    if (gen) {
                        String eventStr = define.getName();
                        eventStr += String.format(":Delay %.2f(S)", delay / 1000f);
                        EventModel model = new EventModel(l3Model.getTime(), eventStr, EventModel.TYPE_DEFINE);
                        model.setCustomEventName(define.getName());
                        model.setCustomDelay(delay);
                        model.setPointIndex(l3Model.getPointIndex());
                        model.setAlarm(define.isShowAlarm());
                        model.setShowOnChart(define.isShowChart());
                        model.setShowOnMap(define.isShowMap());
                        model.setShowOnTotal(define.isShowTotal());
                        model.setIconDrawablePath(define.getIconFilePath());
                        Log.i(TAG, "--自定义信令---3");
                        addEvent(context, model, sortByTime);// 回放时需要回溯按时间排序
                        result.add(model);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 回溯自定义事件的信令列表
     *
     * @param msg1Id
     * @return 信令1，没有信令1时返回null
     */
    public TdL3Model lookBackMsgList(long msg1Id) {
        for (int i = customMsg1.size() - 1; i >= 0; i--) {
            TdL3Model t = customMsg1.get(i);
            if (t.getId() == msg1Id) {
                return t;
            }
        }
        return null;
    }

    /**
     * 增加一个自定义事件的统计
     */
    public void addCutstomEventTotal(Context context, EventModel event, GeoPoint geoPoint) {
        String mainKey1 = event.getCustomEventName();
        String mainKey2 = event.getTime() + "";
        HashMap<String, TotalSpecialModel> map = new HashMap<String, TotalSpecialModel>();
        // 时延
        TotalSpecialModel totalDelay = new TotalSpecialModel(mainKey1, mainKey2, TotalCustomEvent._delay.name(),
                event.getCustomDelay());
        map.put(TotalCustomEvent._delay.name(), totalDelay);
        // 纬度
        long latitude = (geoPoint == null) ? 0 : (long) geoPoint.getLatitudeE6();
        TotalSpecialModel totalLatitude = new TotalSpecialModel(mainKey1, mainKey2, TotalCustomEvent._latitude.name(),
                latitude);
        map.put(TotalCustomEvent._latitude.name(), totalLatitude);
        // 经度
        long longitude = (geoPoint == null) ? 0 : (long) geoPoint.getLongitudeE6();
        TotalSpecialModel totalLongitude = new TotalSpecialModel(mainKey1, mainKey2, TotalCustomEvent._longitude.name(),
                longitude);
        map.put(TotalCustomEvent._longitude.name(), totalLongitude);

        TotalDataByGSM.getInstance().updateTotalUnifyTimes(context, map);
    }

    /**
     * 指定的RCU事件ID是否存在于指定时间点之后的事件列表中
     *
     * @param rcuid
     * @param eventStartTime
     * @return
     */
    public boolean EventContainsRCUID(int rcuid, long eventStartTime) {
        boolean containId = false;

        for (int i = (mEventList.size() - 1); i >= 0 && mEventList.get(i).getTime() > eventStartTime; i--) {

            LogUtil.w("NetWorkAlive",
                    "--Event:" + mEventList.get(i).getEventStr() + "--ID:" + Integer.toHexString(mEventList.get(i).getRcuId())
                            + "--Time:" + UtilsMethod.sdFormat.format(mEventList.get(i).getTime()) + "--st:"
                            + UtilsMethod.sdFormat.format(eventStartTime));

            if (mEventList.get(i).getRcuId() == rcuid) {
                containId = true;
                break;
            }
        }
        return containId;
    }
}

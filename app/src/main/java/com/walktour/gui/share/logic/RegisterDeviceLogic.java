package com.walktour.gui.share.logic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.DesUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.StringUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.share.model.BaseResultInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel;
import com.walktour.gui.share.model.DeviceInfoModel.Device;
import com.walktour.gui.share.model.GroupInfoModel;
import com.walktour.gui.weifuwu.business.model.ShareDeviceModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupModel;
import com.walktour.gui.weifuwu.business.model.ShareGroupRelationModel;
import com.walktour.gui.weifuwu.business.table.ShareDataBase;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 注册终端实现逻辑
 *
 * @author zhihui.lian
 */
public class RegisterDeviceLogic {
    private static final String TAG = "RegisterDeviceLogic";
    private static RegisterDeviceLogic instance;
    private static Context mContext;
    public static final String SHARE_DEVICE_CODE = "share_device_code"; // 分享设备唯一编号存储
    public static final String SHARE_DEVICE_NAME = "share_device_name"; // 设备的备注名
    public static final String TOKEN_ID = "token_id"; // 分享设备唯一编号存储
    public static final String SESSION_ID = "session_id";// 保存请求时返回的session_id
    /**
     * 设备名称
     **/
    private String deviceName = "";
    private Lock lock = new ReentrantLock();
    private static boolean isRequiring = false;

    public static synchronized RegisterDeviceLogic getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new RegisterDeviceLogic();
        }
        return instance;
    }

    BaseResultInfoModel baseResultInfoModel = new BaseResultInfoModel();

    /**
     * 分享注册
     */
    private class RegiterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (isRequiring)
                return null;
            isRequiring = true;
            // if (TextUtils.isEmpty(ShareCommons.device_code)) {
            initDeviceCode();
            // }
            if (ShareCommons.token_id.equals("") || !ApplicationModel.getInstance().isBindXgSuccess) {
                registerDeviceOnBind();
            }
            return null;
        }

    }

    public void shareRegister() {
        new RegiterTask().execute();
    }

    /**
     * 初始化注册设备，获取设备号
     *
     * @return code 1成功 -1失败
     */
    private void initDeviceCode() {
        lock.lock();
        try {
            String imei = MyPhoneState.getInstance().getIMEI(mContext);
            if (!StringUtil.isNullOrEmpty(imei)) {
                // 对IMEI+@+毫秒时间进行des加密后，再进行base64加密
                imei = imei + "@" + System.currentTimeMillis();
                imei = new String(DesUtil.base64Encrypt(imei.getBytes()));
                baseResultInfoModel = ShareHttpRequestUtil.getInstance().registerDevice(imei, getLanguage());
                if (baseResultInfoModel.getReasonCode() == 1) {
                    if (baseResultInfoModel.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)
                            && !StringUtil.isNullOrEmpty(baseResultInfoModel.getDevice_code())) {
                        SharePreferencesUtil.getInstance(mContext).saveString(SHARE_DEVICE_CODE,
                                baseResultInfoModel.getDevice_code());
                        SharePreferencesUtil.getInstance(mContext).saveString(SESSION_ID,
                                baseResultInfoModel.getSession_id());
                        ShareCommons.device_code = baseResultInfoModel.getDevice_code();
                        ShareCommons.session_id = baseResultInfoModel.getSession_id();
                        LogUtil.w(TAG, "微服务注册成功.");
                    } else {
                        // 加载保存的数据
                        if (null == ShareCommons.device_code || ShareCommons.device_code.equals("")) {
                            ShareCommons.device_code = SharePreferencesUtil.getInstance(mContext)
                                    .getString(SHARE_DEVICE_CODE, "");
                        }
                        if (null == ShareCommons.session_id || ShareCommons.session_id.equals("")) {
                            ShareCommons.session_id = SharePreferencesUtil.getInstance(mContext).getString(SESSION_ID, "");
                        }
                        LogUtil.w(TAG, "微服务注册失败.");
                    }
                } else {
                    if (null == ShareCommons.device_code || ShareCommons.device_code.equals("")) {
                        ShareCommons.device_code = SharePreferencesUtil.getInstance(mContext)
                                .getString(SHARE_DEVICE_CODE, "");
                    }
                    if (null == ShareCommons.session_id || ShareCommons.session_id.equals("")) {
                        ShareCommons.session_id = SharePreferencesUtil.getInstance(mContext).getString(SESSION_ID, "");
                    }
                    LogUtil.w(TAG, "微服务注册失败.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.w(TAG, "微服务注册失败.");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 绑定设备
     */
    private void registerDeviceOnBind() {
        lock.lock();
        try {
            XGPushManager.registerPush(mContext, new XGIOperateCallback() {
                @Override
                public void onSuccess(Object data, int flag) {
                    ApplicationModel.getInstance().isBindXgSuccess = true;
                    Log.w(Constants.LogTag, "+++ register push sucess. token:" + data);
                    SharePreferencesUtil.getInstance(mContext).saveString(TOKEN_ID, data.toString());
                    ShareCommons.token_id = data.toString();
                    if (!StringUtil.isNullOrEmpty(data.toString())) {
                        // 每台设备默认给一个备注名
                        deviceName = SharePreferencesUtil.getInstance(mContext).getString(SHARE_DEVICE_NAME, "");
                        if (!ShareCommons.device_code.equals("")) {
                            ShareDeviceModel mm = null;
                            try {
                                mm = ShareDataBase.getInstance(mContext)
                                        .fetchDeviceByDeviceCode(ShareCommons.device_code);
                                if (null != mm)
                                    deviceName = mm.getDeviceName();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        baseResultInfoModel = ShareHttpRequestUtil.getInstance().editDevice(deviceName, data.toString(),
                                ShareCommons.session_id);
                        if (baseResultInfoModel.getReasonCode() == 1) {
                            if (baseResultInfoModel.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
                                try {
                                    ShareDeviceModel modelx = new ShareDeviceModel();
                                    modelx.setDeviceName(deviceName);
                                    modelx.setDeviceCode(ShareCommons.device_code);
                                    modelx.setDeviceOS(ShareDeviceModel.OS_ANDROID);
                                    ShareDataBase.getInstance(mContext).insertDevice(modelx);
                                    // 同步群组关系
                                    new UpdateGroup().execute();
                                    LogUtil.w(TAG, "微服务绑定成功.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LogUtil.w(TAG, "微服务绑定失败.");
                            }
                        } else {
                            LogUtil.w(TAG, "微服务绑定失败.");
                        }
                    }
                }

                @Override
                public void onFail(Object data, int errCode, String msg) {
                    if (null == ShareCommons.token_id || ShareCommons.token_id.equals("")) {
                        ShareCommons.token_id = SharePreferencesUtil.getInstance(mContext).getString(TOKEN_ID, "");
                    }
                    LogUtil.w(TAG, "微服务绑定失败.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isRequiring = false;
            lock.unlock();
        }
    }

    /***
     * 更新群组关系
     *
     * @author weirong.fan
     *
     */
    private class UpdateGroup extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            GroupInfoModel model = ShareHttpRequestUtil.getInstance().queryGrouprelations(ShareCommons.device_code,
                    ShareCommons.session_id);
            if (model.getReasonCode() == 1 && model.getResult_code().equals(BaseResultInfoModel.REQ_REGISTER_FAILURE)) {
                shareRegister();
                model = ShareHttpRequestUtil.getInstance().queryGrouprelations(ShareCommons.device_code,
                        ShareCommons.session_id);
            }
            if (model.getReasonCode() == 1) {
                if (model.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
                    try {
                        for (GroupInfoModel.Group g : model.getGroups()) {
                            ShareGroupModel gm = new ShareGroupModel();
                            gm.setGroupCode(g.getGroup_code());
                            gm.setGroupName(g.getGroup_name());
                            gm.setCreateDeviceCode(g.getDevice_code());
                            // 插入群组信息
                            ShareDataBase.getInstance(mContext).saveOrUpdateGroup(gm);
                            List<DeviceInfoModel.Device> listD = g.getDevices();
                            for (DeviceInfoModel.Device d : listD) {
                                ShareGroupRelationModel grm = new ShareGroupRelationModel();
                                grm.setGroupCode(g.getGroup_code());
                                grm.setDeviceCode(d.getDevice_code());
                                // 插入群组关系信息
                                ShareDataBase.getInstance(mContext).insertGroupRelation(grm);
                                ShareDeviceModel dm = new ShareDeviceModel();
                                dm.setDeviceCode(d.getDevice_code());
                                dm.setDeviceName(d.getDevice_name());
                                dm.setDeviceOS(d.getDevice_type().equalsIgnoreCase(ShareDeviceModel.OS_ANDROID + "")
                                        ? ShareDeviceModel.OS_ANDROID : ShareDeviceModel.OS_IOS);
                                // 插入设备信息
                                ShareDataBase.getInstance(mContext).insertDevice(dm);
                            }

                            //处理未收到消息启动软件后，删除未存在群组关系的信息
                            List<ShareGroupRelationModel> relations = ShareDataBase.getInstance(mContext).fetchGroupRelation(g.getGroup_code());
                            for (ShareGroupRelationModel relation : relations) {
                                boolean flag = false;
                                for (DeviceInfoModel.Device d : listD) {
                                    if (relation.getDeviceCode().equals(d.getDevice_code())) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    ShareDataBase.getInstance(mContext).deleteGroupRelation(relation.getGroupCode(), relation.getDeviceCode());
                                }
                            }
                        }
                        // 申请加自己为好友
                        List<ShareDeviceModel> listDS = ShareDataBase.getInstance(mContext).fetAllDevice();
                        DeviceInfoModel model3 = ShareHttpRequestUtil.getInstance().queryConfirmDevice("0",
                                ShareCommons.session_id);
                        if (model3.getReasonCode() == 1) {
                            if (model3.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
                                List<Device> desice = model3.getDevices();
                                for (Device d : desice) {
                                    boolean flag = false;
                                    for (ShareDeviceModel ld : listDS) {
                                        if (d.getDevice_code().equals(ld.getDeviceCode())) {
                                            flag = true;
                                            ld.setDeviceStatus(ShareDeviceModel.STATUS_CONFIRM);
                                            ld.setDeviceName(d.getDevice_name());
                                            ShareDataBase.getInstance(mContext).updateDevice(ld);
                                        }
                                    }
                                    if (!flag) {
                                        ShareDeviceModel ld = new ShareDeviceModel();
                                        ld.setDeviceCode(d.getDevice_code());
                                        ld.setDeviceName(d.getDevice_name());
                                        ld.setDeviceMessage(d.getRequest_message());
                                        ld.setDeviceOS(d.getDevice_type().equals(ShareDeviceModel.OS_ANDROID + "")
                                                ? ShareDeviceModel.OS_ANDROID : ShareDeviceModel.OS_IOS);
                                        ld.setDeviceStatus(ShareDeviceModel.STATUS_CONFIRM);
                                        ShareDataBase.getInstance(mContext).insertDevice(ld);
                                    }
                                }
                            }
                        }
                        // 申请别人加好友
                        listDS = ShareDataBase.getInstance(mContext).fetAllDevice();
                        model3 = ShareHttpRequestUtil.getInstance().queryConfirmDevice("1", ShareCommons.session_id);
                        if (model3.getReasonCode() == 1) {
                            if (model3.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
                                List<Device> desice = model3.getDevices();
                                for (Device d : desice) {
                                    boolean flag = false;
                                    for (ShareDeviceModel ld : listDS) {
                                        if (d.getDevice_code().equals(ld.getDeviceCode())) {
                                            flag = true;
                                            ld.setDeviceStatus(ShareDeviceModel.STATUS_NEW);
                                            ld.setDeviceName(d.getDevice_name());
                                            ShareDataBase.getInstance(mContext).updateDevice(ld);
                                        }
                                    }
                                    if (!flag) {
                                        ShareDeviceModel ld = new ShareDeviceModel();
                                        ld.setDeviceCode(d.getDevice_code());
                                        ld.setDeviceName(d.getDevice_name());
                                        ld.setDeviceMessage(d.getRequest_message());
                                        ld.setDeviceOS(d.getDevice_type().equals(ShareDeviceModel.OS_ANDROID + "")
                                                ? ShareDeviceModel.OS_ANDROID : ShareDeviceModel.OS_IOS);
                                        ld.setDeviceStatus(ShareDeviceModel.STATUS_NEW);
                                        ShareDataBase.getInstance(mContext).insertDevice(ld);
                                    }
                                }
                            }
                        }
                        // 好友设备
                        listDS = ShareDataBase.getInstance(mContext).fetAllDevice();
                        DeviceInfoModel model4 = ShareHttpRequestUtil.getInstance()
                                .queryFriendDevice(ShareCommons.session_id);
                        if (model4.getReasonCode() == 1) {
                            if (model4.getResult_code().equals(BaseResultInfoModel.REQ_SUCC)) {
                                List<Device> desice = model4.getDevices();
                                for (Device d : desice) {
                                    boolean flag = false;
                                    for (ShareDeviceModel ld : listDS) {
                                        if (d.getDevice_code().equals(ld.getDeviceCode())) {
                                            flag = true;
                                            if (ld.getDeviceStatus() != ShareDeviceModel.STATUS_ADDED) {
                                                ld.setDeviceStatus(ShareDeviceModel.STATUS_ADDED);
                                                ld.setDeviceName(d.getDevice_name());
                                                ShareDataBase.getInstance(mContext).updateDevice(ld);
                                            }
                                        }
                                    }
                                    if (!flag) {
                                        ShareDeviceModel ld = new ShareDeviceModel();
                                        ld.setDeviceCode(d.getDevice_code());
                                        ld.setDeviceName(d.getDevice_name());
                                        ld.setDeviceMessage(d.getRequest_message());
                                        ld.setDeviceOS(d.getDevice_type().equals(ShareDeviceModel.OS_ANDROID + "")
                                                ? ShareDeviceModel.OS_ANDROID : ShareDeviceModel.OS_IOS);
                                        ld.setDeviceStatus(ShareDeviceModel.STATUS_ADDED);
                                        ShareDataBase.getInstance(mContext).insertDevice(ld);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    private String getLanguage() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return "cn";
        return "en";
    }
}

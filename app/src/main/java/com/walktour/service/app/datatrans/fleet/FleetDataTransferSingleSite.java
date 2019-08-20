package com.walktour.service.app.datatrans.fleet;

import com.dinglicom.btu.comlib;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.LogUtil;
import com.walktour.base.util.MD5Util;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.gui.R;
import com.walktour.service.app.DataTransService;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yi.lin on 2017/9/8.
 * <p>
 * 单站验证基站测试Fleet服务器上传数据
 */

public class FleetDataTransferSingleSite extends FleetDataTransferBase {

    FleetDataTransferSingleSite(DataTransService service) {
        super("FleetDataTransferSingleSite", service);
    }

    @Override
    public boolean init() {
        LogUtil.d(TAG, "--------init-----------");
        mServerMgr.sendTipBroadcast(mService.getString(R.string.server_connect_stat) + mServerMgr.getUploadFleetIp() + ":"
                + mServerMgr.getUploadFleetPort());
        String serverIp = mServerMgr.getUploadFleetIp();
        int serverPort = mServerMgr.getUploadFleetPort();
        super.mServerDescribe = serverIp + "_" + serverPort;
        String password = MD5Util.encode(mServerMgr.getFleetPassword());
        String userId = "{" + MyPhoneState.getInstance().getGUID(mService) + "}";
        int cVer = 0;
        String sVer = mServerMgr.getFleetAccount();
        int syncTime = 0;
        int timeout = 6 * 1000 * 10;
        int mode = 1;//0默认设备登录模式; 1采用User和Pass认证模式
        int result = mLib.initclientmode("", serverIp, password, userId, this.mLogPath, timeout, comlib.LOGIN_TYPE_FLEET, serverPort,
                cVer, sVer, syncTime, mode);
        return result == comlib.DL_RET_OK;
    }

    @Override
    protected String getTag() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"Protocol\":\"SSV\",");
        sb.append("\"FileType\":\"RCU\",");
        if (super.mCurrentFile.getTestTypeId() == WalkStruct.TestType.DT.getTestTypeId())
            sb.append("\"SourceDataType\":\"DT\",");
        else
            sb.append("\"SourceDataType\":\"Indoor\",");
        if (super.mCurrentFile.hasExtraParam("TestPointID")) {
            sb.append("\"TestPointID\":").append(super.mCurrentFile.getStringExtraParam("TestPointID")).append(",");
        } else {
            sb.append("\"TestPointID\":").append("").append(",");
        }
        if (super.mCurrentFile.hasExtraParam("SceneName")) {
            sb.append("\"SceneName\":\"").append(super.mCurrentFile.getStringExtraParam("SceneName")).append("\",");
        } else {
            sb.append("\"SceneName\":\"").append("").append("\",");
        }
        if (super.mCurrentFile.hasExtraParam("TaskName")) {
            sb.append("\"TaskName\":\"").append(super.mCurrentFile.getStringExtraParam("TaskName")).append("\",");
        } else {
            sb.append("\"TaskName\":\"").append("").append("\",");
        }
        if (super.mCurrentFile.hasExtraParam("CellID")) {
            sb.append("\"CellID\":").append(super.mCurrentFile.getStringExtraParam("CellID")).append(",");
        } else {
            sb.append("\"CellID\":").append("").append(",");
        }
        sb.append("\"Info\":\"TestInfo\"}");
        return sb.toString();
    }

    @Override
    protected void initCurrentFileTypes() {
        Set<WalkStruct.FileType> fileTypes = new HashSet<>();
        fileTypes.add(WalkStruct.FileType.ORGRCU);
        fileTypes.add(WalkStruct.FileType.DCF);
        super.mCurrentFile.setFileTypes(fileTypes);
    }
}

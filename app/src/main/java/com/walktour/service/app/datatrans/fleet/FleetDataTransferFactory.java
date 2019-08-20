package com.walktour.service.app.datatrans.fleet;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.service.app.DataTransService;

/**
 * fleet平台数据上传工厂类
 *
 * @author jianchao.wang
 */
public class FleetDataTransferFactory {
    /**
     * 唯一实例
     */
    private static FleetDataTransferFactory sInstance;
    /**
     * 应用对象
     */
    private ApplicationModel appModel;

    private FleetDataTransferFactory() {
        appModel = ApplicationModel.getInstance();
    }

    /**
     * 获取唯一实例
     *
     * @return 唯一实例
     */
    public static FleetDataTransferFactory getInstance() {
        if (sInstance == null) {
            sInstance = new FleetDataTransferFactory();
        }
        return sInstance;
    }

    /**
     * 返回不同的数据传输对象
     *
     * @param service 服务类
     * @return 数据传输对象
     */
    public FleetDataTransferBase getDataTransfer(DataTransService service) {
        if (this.appModel.isHuaWeiTest())
            return new FleetDataTransferHuawei(service);
        else if (this.appModel.isAnHuiTest())
            return new FleetDataTransferAnhui(service);
        else if (this.appModel.getSelectScene() == WalkStruct.SceneType.SingleSite)
            return new FleetDataTransferSingleSite(service);
        else if (service.getServerManager().isUploadMOSFile()||service.getServerManager().isUploadTaggingFile())//选择同步MOS或标注
            return new FleetDataTransferMOS(service);
        else if (appModel.getNetList().contains(WalkStruct.ShowInfoType.TelecomSwitch))
            return new FleetDataTransferDXJTZB(service);
        else
            return new FleetDataTransferCommon(service);
    }
}
